package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<Boolean> {
    private final Model model;
    private Model.Results results;

    public TestModelEvent(Model model) {
        this.model = model;
        results = Model.Results.None;
    }

    public Model getModel() {
        return model;
    }
}
