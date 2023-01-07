package cn.andzhang.groovy.md5

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import java.nio.charset.Charset

/**
 System.out.println("I'm from the cn.andzhang.groovy.MyGroovyPlugin.create_md5_task.doLast")
 it.destinationDirectory = project.layout.buildDirectory.dir('md5')
 it.source(project.layout.projectDirectory.file('src/filetomd5'))
 */
abstract class CreateMD5Task extends SourceTask {

    @OutputDirectory
    abstract DirectoryProperty getDestinationDirectory();

    @TaskAction
    void createHashes() {
        for (File sourceFile : getSource().getFiles()) {
            try {
                InputStream stream = new FileInputStream(sourceFile);
                System.out.println("Generating MD5 for " + sourceFile.getName() + "...");
                // Artificially make this task slower.
                Thread.sleep(1000);
                Provider<RegularFile> md5File = getDestinationDirectory().file(sourceFile.getName() + ".md5");
                FileUtils.writeStringToFile(md5File.get().getAsFile(), DigestUtils.md5Hex(stream), (Charset) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}