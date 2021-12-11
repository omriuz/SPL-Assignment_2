package bgu.spl.mics.application.objects;

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
    enum Degree {
        MSc, PhD
    }

    private int name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private HashSet<Model> models;

    public Student(int name, String department, Degree status) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.papersRead = 0;
        this.papersRead = 0;
        this.models = new HashSet<>();
    }

    public int getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public HashSet<Model> getModels(){
        return models;
    }

    public void incPublications() {
        this.publications++;
    }

    public void incPapersRead() {
        this.papersRead++;
    }
}
