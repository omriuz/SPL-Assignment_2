package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static AtomicInteger gpuTime = new AtomicInteger(0);
    public static AtomicInteger cpuTime = new AtomicInteger(0);
    public static AtomicInteger amountOfBatches = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        List<Student> students = new LinkedList<Student>();
        List<ConfrenceInformation> confrences = new LinkedList<>();
        List<GPU> gpus = new LinkedList<>();
        List<CPU> cpus = new LinkedList<>();
        Student firstStudent = new Student("Simba","Computer Science", Student.Degree.MSc);
        Model firstModel = new Model("YOLO10",new Data(Data.Type.Images,200000),firstStudent);
        firstStudent.addModel(firstModel);
        StudentService firstStudentService = new StudentService("first",firstStudent);
        GPU firstGpu = new GPU(GPU.Type.RTX3090);
        GPUService firstGpuService = new GPUService("first",firstGpu);
        CPU cpu = new CPU(32);
        CPUService firstCPUtService = new CPUService("first",cpu);
        ConfrenceInformation first = new ConfrenceInformation("ICML",2000);
        ConferenceService conferenceService = new ConferenceService(first);
        TimeService timer = new TimeService(5500,1);
        Thread gpu = new Thread(firstGpuService,"GPU");
        Thread cpu1 = new Thread(firstCPUtService,"CPU");
        Thread conf1 = new Thread(conferenceService,"Conference");
        Thread student1 = new Thread(firstStudentService,"Student");
        Thread timing = new Thread(timer,"timing");
        gpu.start();
        cpu1.start();
        conf1.start();
        student1.start();
        timing.start();
//        students.add(firstStudent);
//        confrences.add(first);
        createOutputFile(getOutputString(students,confrences,gpuTime.get(),cpuTime.get(),amountOfBatches.get()));
    }

    public static String getOutputString(List<Student> students, List<ConfrenceInformation> confs, int gpuTime,int cpuTime, int amountOfBatches){
        StringBuilder ans = new StringBuilder();
        for(Student student : students){
            StringBuilder studentDescription = new StringBuilder("For Student " + student.getName() + " Trained models are:\n");
            HashSet <Model> published = student.getPublishedModels();
            for( Model m : student.getTrainedModels()){
                studentDescription.append(m.getName()).append(" ");
                studentDescription.append(published.contains(m) ? "Published\n" : "Not published\n");
            }
            studentDescription.append("\n");
            studentDescription.append("Number of papers read: ").append(student.getPapersRead()).append("\n");
            ans.append(studentDescription);
        }
        for(ConfrenceInformation conf : confs){
            StringBuilder confDescription = new StringBuilder("For Conference " + conf.getName() + " the publications are:\n");
            for(String name : conf.getNames()) confDescription.append(name).append(" ");
            ans.append(confDescription);
        }
        ans.append("\n").append("CPU Time: ").append(gpuTime).append("\n");
        ans.append("GPU Time: ").append(cpuTime).append("\n");
        ans.append("Amount of batches: ").append(amountOfBatches);
        return ans.toString();


    }
    public static void createOutputFile(String outputString){
        File myObj = new File("output.txt");
        try {
            FileWriter myWriter = new FileWriter("output.txt");
            myWriter.write(outputString);
            myWriter.close();
        }
        catch (IOException e){
            System.out.println("an error occurred writing to the output file");
        }
    }

//    public void readJson() {
//        Gson gson = new Gson();
//        FileReader reader = null;
//        try {
//            reader = new FileReader("example_input.json");
//        } catch (FileNotFoundException e) {}
//        inputData input = gson.fromJson(reader, inputData.class);
//        print(input.getStudents()[0].getPapersRead());
//
//    }

}
