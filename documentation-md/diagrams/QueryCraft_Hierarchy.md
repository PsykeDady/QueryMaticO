classDiagram
 QueryMaticO  <|.. SQLInsertMaticO
 QueryMaticO  <|.. SQLUpdateMaticO
 QueryMaticO  <|.. SQLDeleteMaticO
 QueryMaticO  <|-- SelectMaticO
 SelectMaticO <|.. SQLSelectMaticO
