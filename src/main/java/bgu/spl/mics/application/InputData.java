package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

import java.util.List;

public class InputData{
    private List<Student> Students;
    private List<GPU> GPUS;
    private List<CPU> CPUS;
    private List<ConfrenceInformation> Conferences;
    private int TickTime;
    private int Duration;


    public InputData(List<Student> students, List<GPU> GPUS, List<CPU> CPUS, List<ConfrenceInformation> conferences, int tickTime, int duration) {
        Students = students;
        this.GPUS = GPUS;
        this.CPUS = CPUS;
        Conferences = conferences;
        TickTime = tickTime;
        Duration = duration;
    }

    public InputData(){}

    public List<Student> getStudents() {
        return Students;
    }

    public void setStudents(List<Student> students) {
        Students = students;
    }

    public List<GPU> getGPUS() {
        return GPUS;
    }

    public void setGPUS(List<GPU> GPUS) {
        this.GPUS = GPUS;
    }

    public List<CPU> getCPUS() {
        return CPUS;
    }

    public void setCPUS(List<CPU> CPUS) {
        this.CPUS = CPUS;
    }

    public List<ConfrenceInformation> getConferences() {
        return Conferences;
    }

    public void setConferences(List<ConfrenceInformation> conferences) {
        Conferences = conferences;
    }

    public int getTickTime() {
        return TickTime;
    }

    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }
}
