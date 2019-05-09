plugins {
    id("com.optum.giraffle") version "@version@"@propertiesPlugin@
}

val gsqlHost: String by project
val gsqlAdminUserName: String by project
val gsqlAdminPassword: String by project
val gsqlUserName: String by project
val gsqlPassword: String by project
val gsqlGraphName: String by project

val tokenMap: LinkedHashMap<String, String> = linkedMapOf("graphname" to gsqlGraphName)

tigergraph {
    scriptDir.set(file("db_scripts"))
    tokens.set(tokenMap)
    serverName.set(gsqlHost)
    userName.set(gsqlUserName)
    password.set(gsqlPassword)
    adminUserName.set(gsqlAdminUserName)
    adminPassword.set(gsqlAdminPassword)
}
