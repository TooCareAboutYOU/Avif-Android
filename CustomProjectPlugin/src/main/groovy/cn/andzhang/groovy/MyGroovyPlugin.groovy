package cn.andzhang.groovy

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyGroovyPlugin implements Plugin<Project> {

    def GROUP_ID_GROOVY = 'zs_plugin'
    def GROUP_ID_FILE = 'file_tasks'

    @Override
    void apply(Project project) {
        project.task('AGroovyTask') {
            setGroup(GROUP_ID_GROOVY)
            doFirst {
                System.out.println("I'm from the cn.andzhang.groovy.MyGroovyPlugin.AGroovyTask.doFirst")
            }
            doLast {
                System.out.println("I'm from the cn.andzhang.groovy.MyGroovyPlugin.AGroovyTask.doLast")
            }
        }
//        project.tasks.register('writeToTile', CustomPluginTask) {
//            it.setGroup(GROUP_ID_FILE)
//            def greetFile = project.objects.fileProperty()
//            greetFile.set(project.layout.projectDirectory.file("Hello.txt"))
//            it.destination = greetFile
//            it.message = 'Message from Task：writeToTile'
//            doLast {
//                println('Task：writeToTile is run finished')
//            }
//        }
    }
}