package lunatech.lunchplanner.schedulers.actors

import akka.actor.Actor
import lunatech.lunchplanner.services.{
  MenuPerDayPerPersonService,
  SlackService,
  UserService
}
import play.api.libs.ws.WSClient
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class LunchBotActor(ws: WSClient,
                    hostName: String,
                    userService: UserService,
                    menuPerDayPerPersonService: MenuPerDayPerPersonService,
                    slackService: SlackService)
    extends Actor {

  override def receive: Receive = {
    case StartBot => act
  }

  def act: Unit = {
    val allEmails = userService.getAllEmailAddresses
    val filtered = getEmailAddressesOfUsersWhoHaveNoDecision(allEmails)
    val userIds = getSlackUserIdsByUserEmails(filtered)
    val channelIds = openConversation(userIds)
    val response = postMessages(channelIds)
    response onComplete {
      case Success(r) =>
        Logger.info(r)
      case Failure(t) => throw t
    }
  }

  def getEmailAddressesOfUsersWhoHaveNoDecision(
      allEmails: Future[Seq[String]]): Future[Seq[String]] = {
    val emailsOfAttendees =
      menuPerDayPerPersonService.getAttendeesEmailAddressesForUpcomingLunch

    for {
      all <- allEmails
      toFilterOut <- emailsOfAttendees
    } yield all.filterNot(toFilterOut.contains(_))
  }

  def getSlackUserIdsByUserEmails(
      emails: Future[Seq[String]]): Future[Seq[String]] = {
    for {
      emailList <- emails
      userIds <- slackService.getAllSlackUsersByEmails(emailList)
    } yield userIds
  }

  def openConversation(
      slackUserIds: Future[Seq[String]]): Future[Seq[String]] = {
    for {
      userIds <- slackUserIds
      channelIds <- slackService.openConversation(userIds)
    } yield channelIds
  }

  def postMessages(channelIds: Future[Seq[String]]): Future[String] = {
    for {
      channels <- channelIds
      response <- slackService.postMessage(channels)
    } yield response
  }

}

case object StartBot