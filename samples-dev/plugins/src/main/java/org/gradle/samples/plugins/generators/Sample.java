package org.gradle.samples.plugins.generators;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.samples.plugins.SampleGeneratorTask;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Sample implements Named {
    private final String name;
    private final DirectoryProperty sampleDir;
    private final List<Action<SampleGeneratorTask>> sourceActions = new ArrayList<Action<SampleGeneratorTask>>();
    private final List<Action<Zip>> zipActions = new ArrayList<Action<Zip>>();

    @Inject
    public Sample(String name, ObjectFactory objectFactory) {
        this.name = name;
        this.sampleDir = objectFactory.directoryProperty();
    }

    @Override
    public String getName() {
        return name;
    }

    public DirectoryProperty getSampleDir() {
        return sampleDir;
    }

    public void copySource(Action<SampleGeneratorTask> action) {
        sourceActions.add(action);
    }

    public void zipSource(Action<Zip> action) {
        zipActions.add(action);
    }

    public List<Action<SampleGeneratorTask>> getSourceActions() {
        return sourceActions;
    }

    public List<Action<Zip>> getZipActions() {
        return zipActions;
    }
}
