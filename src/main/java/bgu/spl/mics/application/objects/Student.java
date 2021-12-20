package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }
    private String name;
    private String department;
    private Degree degStatus;
    private int publications;
    private int papersRead;
    private List<Model> models;
    private Future future;
    private int currentModel;
    private Boolean finished;
    private List<String> publishedModelNames;
    private HashSet<Model> trainedModels;
    private HashSet<Model> publishedModels;
    public Student(String name, String department, String status, List<Model> models) {
        this.name = name;
        this.department = department;
        this.degStatus = stringToDegree(status);
        this.publications = 0;
        this.papersRead = 0;
        this.models = models;
        this.publishedModelNames = new LinkedList<>();
        this.future = null;
        this.currentModel = 0;
        this.finished = false;
        this.trainedModels = new HashSet<>();
        this.publishedModels = new HashSet<>();
        for(Model m : models)
            m.setStudent(this);
    }
    private Degree stringToDegree(String status){
        Degree degStatus = null;
        if(status.equals("MSc"))
            degStatus = Degree.MSc;
        else if(status.equals("PhD"))
            degStatus = Degree.PhD;
        return degStatus;
    }
    public String getName() {
        return name;
    }
    public String getDepartment() {
        return department;
    }
    public Degree getDegStatus() {
        return degStatus;
    }
    public int getPublications() {
        return publications;
    }
    public int getPapersRead() {
        return papersRead;
    }
    public List<Model> getModels(){
        return models;
    }
    public <T> Future<T> getFuture(){ return future;}
    public <T> void setFuture(Future<T> future){this.future = future;}
    public List<String> getPublishedModelNames(){return publishedModelNames;}
    public void addPublishedModel(Model model){
        if(model != null) {
            publishedModels.add(model);
            publishedModelNames.add(model.getName());
        }
    }
    public void incPublications() {
        this.publications++;
    }
    public void incPapersRead() {
        this.papersRead++;
    }
    public Model getNextModel(){
        Model returnModel=null;
        if(finished)
            returnModel = null;
        else {
            if (currentModel == models.size()) {
                finished = true;
            }
            else {
                returnModel = models.get(currentModel);
                currentModel++;
            }
        }
        return returnModel;
    }
    public void addModel(Model model){
        models.add(model);
    }
    public Boolean isFinished(){
        return finished;
    }
    public void addTrainedModel(Model model){
        trainedModels.add(model);
    }
    public HashSet<Model> getTrainedModels() {
        return trainedModels;
    }

    public HashSet<Model> getPublishedModels() {
        return publishedModels;
    }
}
