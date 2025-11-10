@startuml
' --- Settings ---
skinparam componentStyle rectangle
skinparam roundCorner 10
skinparam linetype ortho
skinparam database {
BorderColor black
BackgroundColor #e5ccff
}
skinparam rectangle {
BorderColor black
}

' --- Component Definitions ---
rectangle "FF UI" as UI #cce5ff
rectangle "TASK MANAGER" as TM #ccffcc {
It will create outreach
tasks for each PCU-ID
}
rectangle "FMR" as FMR #ffebcc {
Needs are resolve
PCU-IDS &
fetch static data
}
database "Saves the form\nin DB with\nForm/Section ID" as DB #e5ccff

' --- Relationships ---
UI -right-> TM : PCU-IDS\nFOR OUTREACH
TM -right-> FMR : PCU-ID
FMR -down-> DB

@enduml