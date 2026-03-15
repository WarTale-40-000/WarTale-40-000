plugins {
    id("java")
    // See documentation on https://scaffoldit.dev
    id("dev.scaffoldit") version "0.2.14"
}

group = "Wartale"
version = "1.0.2"
var manifestVersion = version as String

//
// Automatically configures the builds, but you can switch scripts if you wish!
//
hytale {
    manifest {
        Group = group as String
        Name = "Wartale"
        Main = "com.warhammer.wartale.WartalePlugin"
        Version = manifestVersion
        ServerVersion = "2026.02.19-1a311a592"
        Dependencies = mapOf("Hytale:EntityModule" to "*")
    }
}
