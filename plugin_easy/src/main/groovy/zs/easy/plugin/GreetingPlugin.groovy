import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println "I'm from CustomPlugin"

        project.task('ACustom'){
            doFirst {
                println "I'm from CustomPlugin.doFirst"
            }

            doLast {
                println "I'm from CustomPlugin.doLast"
            }
        }
    }
}