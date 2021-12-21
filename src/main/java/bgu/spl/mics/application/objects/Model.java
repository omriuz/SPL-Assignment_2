package bgu.spl.mics.application.objects;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum Status {PreTrained, Training, Trained, Tested};
    public enum Results {None, Good, Bad};

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Results results;

    public Model(String name, String type, int size) {
        this.name = name;
        this.data = new Data(stringToDataType(type), size);
        this.status = Status.PreTrained;
        this.results = Results.None;
    }
    public Status getStatus() {
        return status;
    }
    public String getStatusString(){
        if(status == Status.PreTrained) return "PreTrained";
        else if(status == Status.Training) return "Training";
        else if(status == Status.Trained) return "Trained";
        else return "Tested";
    }
    public Results getResults() {
        return results;
    }

    private Data.Type stringToDataType(String type){
        Data.Type dataType = null;
        if(type.equals("Images"))
            dataType = Data.Type.Images;
        else if(type.equals("Text"))
            dataType = Data.Type.Text;
        else if(type.equals("Tabular"))
            dataType = Data.Type.Tabular;
        return dataType;
    }


    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStatus(Status status){this.status = status;}
    public void setResults(Results results){this.results = results;}
    public void setStudent(Student student) {
        this.student = student;
    }
}

