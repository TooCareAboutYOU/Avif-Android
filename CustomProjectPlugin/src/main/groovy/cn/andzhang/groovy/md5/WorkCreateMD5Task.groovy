package cn.andzhang.groovy.md5

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

abstract class WorkCreateMD5Task extends SourceTask {

    private abstract class GenerateMD5 implements WorkAction<MD5WorkParameters> {
        @Override
        void execute() {
            try {
                File sourceFile = getParameters().getSourceFile().getAsFile().get()
                File md5File = getParameters().getMD5File().getAsFile().get()
                InputStream inputStream = new FileInputStream(sourceFile)
                System.out.println("Generating MD5 for " + sourceFile.getName() + "...")

                // Artificially make this task slower.
                Thread.sleep(1000)
                FileUtils.writeStringToFile(md5File, DigestUtils.md5Hex(inputStream), (String) null)
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    @OutputDirectory
    abstract DirectoryProperty getDestinationDirectory();

    @Inject
    abstract WorkerExecutor getWorkerExecutor();

    @TaskAction
    void createHashes() {
        WorkQueue workQueue = getWorkerExecutor().noIsolation();

        for (File sourceFile : getSource().getFiles()) {
            Provider<RegularFile> md5File = getDestinationDirectory().file(sourceFile.name + ".md5");
            workQueue.submit(GenerateMD5.class, params -> {
                params.getSourceFile().set(sourceFile)
                params.getMD5File().set(md5File)
            })
        }
    }

}