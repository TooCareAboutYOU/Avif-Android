package zs.plugin.groovy.md5;

import org.gradle.api.tasks.SourceTask;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/25 10:24
 * @description
 */
public abstract class UrlVerifyTask extends SourceTask {

//    @OutputDirectory
//    abstract DirectoryProperty getDestinationDirectory();
//
//    @Inject
//    abstract WorkerExecutor getWorkerExecutor();
//
//    @TaskAction
//    public void verify() {
//        WorkQueue workQueue = getWorkerExecutor().noIsolation();
//        for (File sourceFile : getSource().getFiles()) {
//            Provider<RegularFile> md5File=getDestinationDirectory().file(sourceFile.name+".md5");
//            workQueue.submit(GenerateMD)
//        }
//    }
}