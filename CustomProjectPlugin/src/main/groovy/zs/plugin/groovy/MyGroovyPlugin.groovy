package zs.plugin.groovy

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyGroovyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task('AGroovyPlugin') {
            doFirst {
                System.out.println("I'm from the zs.plugin.groovy.MyGroovyPlugin.doFirst")
            }
            doLast {
                System.out.println("I'm from the zs.plugin.groovy.MyGroovyPlugin.doLast")
            }
        }
    }
}