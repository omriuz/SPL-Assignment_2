package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

public class inputData{
    private Student[] Students;
    private GPU[] GPUS;
    private CPU[] CPUS;
    private ConfrenceInformation[] Conferences;
    private int TickTime;
    private int Duration;

    public inputData(Student[] Students, GPU[] GPUS, CPU[] CPUS, ConfrenceInformation[] Conference, int TickTime, int Duration){
        this.Students = Students;
        this.GPUS = GPUS;
        this.CPUS = CPUS;
        this.Conferences = Conference;
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    public inputData(){}

    public Student[] getStudents() {
        return Students;
    }

    public GPU[] getGPUS() {
        return GPUS;
    }

    public CPU[] getCPUS() {
        return CPUS;
    }

    public ConfrenceInformation[] getConfrences(){return Conferences;}


}
