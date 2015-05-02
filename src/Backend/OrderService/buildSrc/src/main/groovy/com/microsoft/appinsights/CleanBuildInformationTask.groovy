
package com.microsoft.appinsights

import java.text.SimpleDateFormat
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*


class CleanBuildInformationTask extends DefaultTask
{
    @TaskAction
    void remove()
    {
        def buildInfoFile = project.file('src/main/resources/buildinfo.properties')

        if (buildInfoFile.exists())
            buildInfoFile.delete()
    }
}
