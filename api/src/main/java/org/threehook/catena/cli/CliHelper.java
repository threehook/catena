package org.threehook.catena.cli;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CliHelper {

    @Value("${project.name.tech}")
    private String projectNameTech;

    @Value("${info.build.version}")
    private String projectVersion;

    public String getApplicationNameTech() {
        return projectNameTech + "-" + projectVersion;
    }
}
