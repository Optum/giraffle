plugins {
    id("com.optum.giraffle") version "@version@"@propertiesPlugin@
}

val gsqlHost: String by project
val gsqlAdminUsername: String by project
val gsqlAdminPassword: String by project
val gsqlUsername: String by project
val gsqlPassword: String by project
val gsqlGraphName: String by project

val tokenMap: LinkedHashMap<String, String> = linkedMapOf("graphname" to gsqlGraphName)

tigergraph {
    scriptDir.set(file("db_scripts"))
    tokens.set(tokenMap)
    serverName.set(gsqlHost)
    userName.set(gsqlUsername)
    password.set(gsqlPassword)
    adminUserName.set(gsqlAdminUsername)
    adminPassword.set(gsqlAdminPassword)
} 
