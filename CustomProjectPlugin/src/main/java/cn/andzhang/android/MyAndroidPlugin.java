package cn.andzhang.android;


import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;

import java.util.function.Consumer;

import cn.andzhang.android.http.HttpRequest;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/24 14:01
 * @description
 */
public class MyAndroidPlugin implements Plugin<Project> {

    private final String GROUP_ID_ANDROID = "zs_plugin";
    private final String TASK_NAME_TOKEN = "AGetUploadToken";
    private final String TASK_NAME_UPLOAD = "AUploadApk";

    @Override
    public void apply(Project project) {

        createTasks(project);

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project pro) {
                pro.getTasks().matching(new Spec<Task>() {
                    @Override
                    public boolean isSatisfiedBy(Task task) {
                        return "assembleRelease".equals(task.getName());
                    }
                }).forEach(new Consumer<Task>() {
                    @Override
                    public void accept(Task task) {
                        task.dependsOn(TASK_NAME_TOKEN);
                        task.finalizedBy(TASK_NAME_UPLOAD);
                    }
                });
            }
        });
    }

    private void createTasks(Project project) {
        Task taskGetToken = project.task(TASK_NAME_TOKEN);
        taskGetToken.setGroup(GROUP_ID_ANDROID);
        taskGetToken.doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                HttpRequest.init(project);
            }
        });
        taskGetToken.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                HttpRequest.getInstance().getToken();
            }
        });

        Task taskUploadApk = project.task(TASK_NAME_UPLOAD);
        taskUploadApk.setGroup(GROUP_ID_ANDROID);
        taskUploadApk.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                HttpRequest.getInstance().uploadApk();
            }
        });
//        Task taskPostTextToDD = project.task("APostDD");
//        taskPostTextToDD.setGroup(GROUP_ID_ANDROID);
//        taskPostTextToDD.doLast(new Action<Task>() {
//            @Override
//            public void execute(Task task) {
//                HttpRequest.getInstance().getAppDetailInfo();
//            }
//        });
    }
}