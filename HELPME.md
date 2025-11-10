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

rectangle "TASK MANAGER\n\nIt will create outreach\ntasks for each PCU-ID" as TM #ccffcc

rectangle "FMR\n\nNeeds are resolve\nPCU-IDS &\nfetch static data" as FMR #ffebcc

database "Saves the form\nin DB with\nForm/Section ID" as DB #e5ccff

' --- Relationships ---
UI -right-> TM : PCU-IDS\nFOR OUTREACH
TM -right-> FMR : PCU-ID
FMR -down-> DB

@enduml