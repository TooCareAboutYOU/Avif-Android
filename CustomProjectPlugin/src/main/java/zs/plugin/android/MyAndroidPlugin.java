package zs.plugin.android;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;

import java.util.function.Consumer;

import zs.plugin.android.http.HttpRequest;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/24 14:01
 * @description
 */
public class MyAndroidPlugin implements Plugin<Project> {

    final String GROUP_ID_GROOVY = "zs_plugin";

    @Override
    public void apply(Project project) {
        HttpRequest.init(project);

        createTasks(project);

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project pro) {
                pro.getTasks().matching(new Spec<Task>() {
                    @Override
                    public boolean isSatisfiedBy(Task task) {
                        return task.getName().equals("assembleRelease");
                    }
                }).forEach(new Consumer<Task>() {
                    @Override
                    public void accept(Task task) {
                        task.dependsOn("AGetUploadToken");
                        task.finalizedBy("AUploadApk");
                    }
                });
            }
        });
    }

    private void createTasks(Project project) {
        Task taskGetToken = project.task("AGetUploadToken");
        taskGetToken.setGroup(GROUP_ID_GROOVY);
        taskGetToken.doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                HttpRequest.getInstance().getUploadKey();
            }
        });

        Task taskUploadApk = project.task("AUploadApk");
        taskUploadApk.setGroup(GROUP_ID_GROOVY);
        taskUploadApk.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                HttpRequest.getInstance().uploadApk();
            }
        });

        Task taskPostTextToDD = project.task("APostDD");
        taskPostTextToDD.setGroup(GROUP_ID_GROOVY);
        taskPostTextToDD.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                HttpRequest.getInstance().getAppDetailInfo();
            }
        });
    }
}