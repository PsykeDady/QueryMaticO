classDiagram
 QueryCraft  <|.. SQLInsertCraft
 QueryCraft  <|.. SQLUpdateCraft
 QueryCraft  <|.. SQLDeleteCraft
 QueryCraft  <|-- SelectCraft
 SelectCraft <|.. SQLSelectCraft
