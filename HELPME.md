@startuml
' Layout
left to right direction
skinparam shadowing false
skinparam rectangle {
BorderColor #555
}
skinparam database {
BorderColor #555
}
title Outreach Data Flow

rectangle "FF UI" as FF
rectangle "Task Manager" as TM
rectangle "FMR" as FMR
database "Forms DB" as DB

FF --> TM : PCU-IDs for outreach

note right of TM
Creates outreach tasks
for each PCU-ID
end note

TM --> FMR : PCU-ID

note right of FMR
Needs to resolve PCU-IDs
and retrieve data
end note

FMR --> DB : Save form data\n(with Form/Section ID)
DB --> FMR : Resolved data

@enduml
