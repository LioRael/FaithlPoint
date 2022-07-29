plugins {
    java
    id("io.izzel.taboolib") version "1.34"
    kotlin("jvm") version "1.6.10"
}

taboolib {
    install("common")
    install("common-5")
    install("platform-bukkit")
    install("module-lang")
    install("module-nms")
    install("module-ui-receptacle")
    install("module-chat")
    install("module-configuration")
    install("module-metrics")
    install("module-database")
    install("module-kether")
    install("expansion-player-database")
    classifier = null
    version = "6.0.9-40"
    description {
        contributors {
            name("Leosouthey")
        }
        links {
            name("homepage").url("https://faithl.com")
        }
        dependencies {
            name("AttributePlus").optional(true)
            name("OriginAttribute").optional(true)
            name("SX-Attribute").optional(true)
            name("Zaphkiel").optional(true)
            name("PlaceholderAPI").optional(true)
        }
    }
}

repositories {
    maven { url = uri("https://repo.tabooproject.org/repository/releases/") }
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { url = uri("https://mvnrepository.com/artifact/org.ow2.asm/asm") }
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("com.alibaba:fastjson:1.2.79")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    compileOnly("org.ow2.asm:asm:9.2")
    compileOnly("com.faithl:milim:1.0.3")
    compileOnly(fileTree("libs"))
}

task("copy") {
    //复制前执行构建
    dependsOn("clean")
    dependsOn("build")
    tasks.findByName("build")?.mustRunAfter("clean")
    mustRunAfter("build")
    //进行任务后进行复制
    doLast {
        copy{
            from("${buildDir.path}\\libs\\")
            into("F:/Server/Paper-1.12.2/plugins")
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
