
plugins {
    java
    application
}

application.mainClass.set("up.visulog.cli.GIULancher")

dependencies {
    implementation(project(":analyzer"))
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    testImplementation("junit:junit:4.+")
}


