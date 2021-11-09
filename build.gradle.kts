plugins {
    java
}

version = "0.0.1"
group = "up"

allprojects {
    repositories {
        mavenCentral()
    }

    plugins.apply("java")

    java.sourceCompatibility = JavaVersion.VERSION_1_10
    dependencies{

           // Ajout des librairies pour graphiques

            implementation("org.jfree:jfreechart:1.0.19")
            implementation("org.jfree:jcommon:1.0.23")
        }

}
