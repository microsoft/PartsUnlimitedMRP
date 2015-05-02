
package com.microsoft.appinsights

import java.text.SimpleDateFormat
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*


class BuildInformationTask extends DefaultTask
{
    File versionFile
    @OutputFile File buildInfoFile = project.file('src/main/resources/buildinfo.properties')

    @TaskAction
    void readBuildInformation()
    {
        // Jenkins version
        def buildNumber = getPropertyOrDefault("BUILD_NUMBER", null)
        def buildId = getPropertyOrDefault("BUILD_ID", null)
        def buildUrl = getPropertyOrDefault("BUILD_URL", null)
        def buildTag = getPropertyOrDefault("BUILD_TAG", null)
        def gitCommit = getPropertyOrDefault("GIT_COMMIT", null)
        def gitUrl = getPropertyOrDefault("GIT_URL", null)
        def gitBranch = getPropertyOrDefault("GIT_BRANCH", null)

        /* VSO version
        def buildNumber = getPropertyOrDefault("BUILD_BUILDNUMBER", null)
        def buildId = getPropertyOrDefault("BUILD_BUILDID", null)
        def buildUrl = getPropertyOrDefault("BUILD_BUILDURI", null)
        def buildTag = getPropertyOrDefault("BUILD_TAG", null)
        def gitCommit = getPropertyOrDefault("BUILD_SOURCEVERSION", null)
        def gitUrl = getPropertyOrDefault("BUILD_REPOSITORY_URI", null)
        def gitBranch = getPropertyOrDefault("BUILD_SOURCEBRANCH", null)
       */

        def date = new Date()
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

        def buildTime = formatter.format(date)

        def buildVersion = "#.#"

        if (versionFile.exists())
        {
            def versionProps = new Properties()
            versionFile.withInputStream { stream -> versionProps.load(stream) }

            def names = versionProps.stringPropertyNames();

            buildVersion =
                    ((!names.contains('major') || isNullOrEmpty(versionProps.major)) ? "#" : versionProps.major) + "."
            buildVersion += ((!names.contains('minor') || isNullOrEmpty(versionProps.minor)) ? "#" : versionProps.minor)
        }

        def resourcesDirectory = buildInfoFile.parentFile

        resourcesDirectory.mkdirs()

        if (!buildInfoFile.exists())
            buildInfoFile.createNewFile()

        buildInfoFile.withOutputStream {
            stream ->
                def writer = new OutputStreamWriter(stream).newPrintWriter()
                if (isNullOrEmpty(buildNumber))
                    writer.println("build.number: " + buildVersion + ".#")
                else
                    writer.println("build.number: " + buildVersion + "." + buildNumber)
                printIfNotEmpty(writer, "build.timestamp", buildTime)
                printIfNotEmpty(writer, "build.id", buildId)
                printIfNotEmpty(writer, "build.url", buildUrl)
                printIfNotEmpty(writer, "build.tag", buildTag)
                printIfNotEmpty(writer, "git.url", gitUrl)
                printIfNotEmpty(writer, "git.branch", gitBranch)
                printIfNotEmpty(writer, "git.commit", gitCommit)

                writer.close()
                stream.close()

                logger.quiet "Wrote build information to " + buildInfoFile.path
        }
    }

    void printIfNotEmpty(writer, prefix, str)
    {
        if (!isNullOrEmpty(str))
            writer.println(prefix + ": " + str)
    }

    Boolean isNullOrEmpty(str)
    {
        str == null || str.isEmpty()
    }

    String getPropertyOrDefault(name, defaultValue)
    {
        def result = defaultValue;
        def env = System.getenv(name)
        if (!isNullOrEmpty(env))
            result = env;
        result
    }
}
