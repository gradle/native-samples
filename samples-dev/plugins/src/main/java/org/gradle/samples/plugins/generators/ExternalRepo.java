package org.gradle.samples.plugins.generators;

import com.google.common.collect.ImmutableList;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.samples.plugins.SampleGeneratorTask;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ExternalRepo {
    private final String name;
    private final Property<String> repoUrl;
    private final List<Action<? super SampleGeneratorTask>> sourceActions = new ArrayList<>();
    private final List<Action<? super Changes>> repoActions = new ArrayList<>();

    @Inject
    public ExternalRepo(String name, ObjectFactory objectFactory) {
        this.name = name;
        repoUrl = objectFactory.property(String.class);
    }

    /**
     * Adds an action to run to configure the source templates for this repo. The source templates are applied to the Git repo clone
     */
    void copySource(Action<? super SampleGeneratorTask> action) {
        sourceActions.add(action);
    }

    /**
     * Adds an action to run to apply a change to the repo. These are applied after the source templates, in order added.
     */
    void change(Action<? super Changes> action) {
        repoActions.add(action);
    }

    public Property<String> getRepoUrl() {
        return repoUrl;
    }

    public String getName() {
        return name;
    }

    public List<Action<? super SampleGeneratorTask>> getSourceActions() {
        return ImmutableList.copyOf(sourceActions);
    }

    public List<Action<? super Changes>> getRepoActions() {
        return ImmutableList.copyOf(repoActions);
    }
}
