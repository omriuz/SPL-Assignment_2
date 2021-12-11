package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<String> {
    private final Model model;
    private Boolean tested;
    private String results;

    public TestModelEvent(Model model) {
        this.model = model;
        this.tested = false;
        results = "None";
    }

    public Model getModel() {
        return model;
    }

    public Boolean getTested() {
        return tested;
    }

    public String getResults() {
        return results;
    }
    public void setResults(String newResult){
        this.results = newResult;
    }

}
