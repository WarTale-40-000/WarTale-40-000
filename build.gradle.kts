plugins {
    id("java")
    // See documentation on https://scaffoldit.dev
    id("dev.scaffoldit") version "0.2.2"
}

group = "Wartale"
version = "1.0.0"

//
// Automatically configures the builds, but you can switch scripts if you wish!
//
hytale {
    manifest {
        Group = group as String
        Name = "Wartale"
        Main = "com.warhammer.wartale.Wartale"
        Version = version as String
    }
}
