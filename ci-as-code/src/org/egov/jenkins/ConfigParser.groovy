package org.egov.jenkins

import org.egov.jenkins.models.BuildConfig
import org.egov.jenkins.models.JobConfig

class ConfigParser {

    static List<JobConfig> parseConfig(def yaml, def env) {
        List<JobConfig> configs = populateConfigs(yaml.config);
        String jobName = env.JOB_NAME;

        List<JobConfig> filteredJobConfigs = new ArrayList<>();

        configs.each { config ->
            if(config.getName().equalsIgnoreCase(jobName)){
                filteredJobConfigs.add(config);
            }
        }
        if(filteredJobConfigs.isEmpty())
            throw new Exception("Invalid Job");

        return filteredJobConfigs;

    }

    static List<JobConfig> populateConfigs(def yaml) {
        List<JobConfig> config = new ArrayList<>();
        yaml.each { job ->
            List<BuildConfig> buildConfigs = new ArrayList<>();
            job.build.each { build ->
                String dockerFile = "";
                String buildContext = ""

                if(build.context == null)
                    buildContext = ".";
                else
                    buildContext = build.context;

                if(build.dockerFile == null)
                    dockerFile = build.context + "/Dockerfile";
                else
                    dockerFile = build.dockerFile;

                BuildConfig buildConfig = new BuildConfig(buildContext, build.imageName, dockerFile);
                buildConfigs.add(buildConfig);
            }
            JobConfig jobConfig = new JobConfig(job.name, buildConfigs);
            config.add(jobConfig);
        }

        return config;
    }

}
