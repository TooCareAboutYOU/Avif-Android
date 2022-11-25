package zs.plugin.android;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/24 14:01
 * @description
 */
public class MyAndroidPlugin implements Plugin<Project> {

    final String GROUP_ID_GROOVY = "zs_plugin";

    @Override
    public void apply(Project project) {
        Task taskHello = project.task("AAndroidTask");
        taskHello.setGroup(GROUP_ID_GROOVY);
        taskHello.doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("I'm from the zs.plugin.android.MyAndroidPlugin.doFirst");
            }
        });
        taskHello.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("I'm from the zs.plugin.android.MyAndroidPlugin.doLast");
            }
        });
    }
}