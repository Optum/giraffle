plugins {
    id("com.optum.giraffle") version "@version@"
    id("net.saliman.properties") version "1.4.6"
}

val gsqlHost: String by project
val gsqlAdminUsername: String by project
val gsqlAdminPassword: String by project
val gsqlGraphname: String by project

