plugins {
    id("com.optum.giraffle") version "@project_version@"@propertiesPlugin@
}

val gHost: String by project
val gAdminUserName: String by project
val gAdminPassword: String by project
val gUserName: String by project
val gPassword: String by project
val gGraphName: String by project
val gClientVersion: String? by project

val tokenMap: LinkedHashMap<String, String> = linkedMapOf("graphname" to gGraphName)

tigergraph {
    scriptDir.set(file("db_scripts"))
    tokens.set(tokenMap)
    serverName.set(gHost)
    userName.set(gUserName)
    password.set(gPassword)
    adminUserName.set(gAdminUserName)
    adminPassword.set(gAdminPassword)
    gClientVersion?.let {
        gsqlClientVersion.set(it)
    }
}
