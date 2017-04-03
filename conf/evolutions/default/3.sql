# --- !Ups

CREATE TABLE "Dish" (
  uuid UUID NOT NULL,
  menuUUID UUID,
  name TEXT NOT NULL,
  description TEXT,
  isVegetarian BOOLEAN NOT NULL DEFAULT FALSE,
  hasSeaFood BOOLEAN NOT NULL DEFAULT FALSE,
  hasPork BOOLEAN NOT NULL DEFAULT FALSE,
  HasBeef BOOLEAN NOT NULL DEFAULT FALSE,
  hasChicken BOOLEAN NOT NULL DEFAULT FALSE,
  isGlutenFree BOOLEAN NOT NULL DEFAULT FALSE,
  hasLactose BOOLEAN NOT NULL DEFAULT FALSE,
  remarks TEXT,
  CONSTRAINT dish_pkey_ PRIMARY KEY (uuid)
);

# --- !Downs

DROP TABLE "Dish";
