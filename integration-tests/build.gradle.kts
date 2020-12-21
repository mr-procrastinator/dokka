import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.cli.common.toBooleanLenient

subprojects {
    sourceSets {
        create("integrationTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    configurations.getByName("integrationTestImplementation") {
        extendsFrom(configurations.implementation.get())
    }

    configurations.getByName("integrationTestRuntimeOnly") {
        extendsFrom(configurations.runtimeOnly.get())
    }

    dependencies {
        implementation(project(":integration-tests"))
    }

    val integrationTest by tasks.register<Test>("integrationTest") {
        maxHeapSize = "2G"
        description = "Runs integration tests."
        group = "verification"
        useJUnit {
            project.properties["exclude_s3_tests"]?.toString()?.toBooleanLenient()?.let { excludeS3Tests ->
                if(excludeS3Tests){
                    excludeCategories("org.jetbrains.dokka.it.S3Test")
                }
            }
        }


        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath

        setForkEvery(1)
        project.properties["dokka_integration_test_parallelism"]?.toString()?.toIntOrNull()?.let { parallelism ->
            maxParallelForks = parallelism
        }
        environment(
            "isExhaustive",
            project.properties["dokka_integration_test_is_exhaustive"]?.toString()?.toBoolean()
                ?: System.getenv("DOKKA_INTEGRATION_TEST_IS_EXHAUSTIVE")?.toBoolean()
                ?: false.toString()
        )

        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }

    tasks.check {
        dependsOn(integrationTest)
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":test-utils"))
    val coroutines_version: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jsoup:jsoup:1.12.1")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")
}
