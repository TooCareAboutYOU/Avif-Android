package cn.andzhang.groovy.md5

import org.gradle.api.file.RegularFileProperty
import org.gradle.workers.WorkParameters

interface MD5WorkParameters extends WorkParameters {
    RegularFileProperty getSourceFile();

    RegularFileProperty getMD5File();
}