package lunatech.lunchplanner.viewModels

import play.api.libs.json.{ Json, OFormat }

case class DishForm (
  name: String,
  description: String,
  isVegetarian: Boolean = false,
  hasSeaFood: Boolean = false,
  hasPork: Boolean = false,
  hasBeef: Boolean = false,
  hasChicken: Boolean = false,
  isGlutenFree: Boolean = false,
  hasLactose: Boolean = false,
  remarks: Option[String] = None
)

object DishForm {
  implicit val dishFormFormat: OFormat[DishForm] = Json.format[DishForm]
}