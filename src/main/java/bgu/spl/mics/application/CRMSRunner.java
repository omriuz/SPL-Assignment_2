package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static AtomicInteger gpuTime = new AtomicInteger(0);
    public static AtomicInteger cpuTime = new AtomicInteger(0);
    public static AtomicInteger amountOfBatches = new AtomicInteger(0);



    public static void main(String[] args) throws FileNotFoundException {
        JsonObject input = buildJSONObject("C:\\Users\\nonom\\IdeaProjects\\SPL-Assignment_2\\example_input.json");
        List<Student> students = BuildStudents(input);
        List<ConfrenceInformation> conferences = BuildConferences(input);
        List<GPU> gpus = BuildGPUs(input);
        List<CPU> cpus = BuildCPUs(input);
        TimeService timeService = BuildTimeService(input);
        System.out.println(students.get(2).getModels().get(1).getName());
//
//        Student firstStudent = new Student("Simba","Computer Science", Student.Degree.MSc);
//        Model firstModel = new Model("YOLO10",new Data(Data.Type.Images,200000),firstStudent);
//        firstStudent.addModel(firstModel);
//        StudentService firstStudentService = new StudentService("first",firstStudent);
//        GPU firstGpu = new GPU(GPU.Type.RTX3090);
//        GPUService firstGpuService = new GPUService("first",firstGpu);
//        CPU cpu = new CPU(32);
//        CPUService firstCPUtService = new CPUService("first",cpu);
//        ConfrenceInformation first = new ConfrenceInformation("ICML",2000);
//        ConferenceService conferenceService = new ConferenceService(first);
//        TimeService timer = new TimeService(5500,50);
//        Thread gpu = new Thread(firstGpuService);
//        Thread cpu1 = new Thread(firstCPUtService);
//        Thread conf1 = new Thread(conferenceService);
//        Thread student1 = new Thread(firstStudentService);
//        Thread timing = new Thread(timer);
//        gpu.start();
//        cpu1.start();
//        conf1.start();
//        student1.start();
//        timing.start();
//        createOutputFile(getOutputString(students,confrences,gpuTime.get(),cpuTime.get(),amountOfBatches.get()));
    }

    public static String getOutputString(List<Student> students, List<ConfrenceInformation> confs, int gpuTime,int cpuTime, int amountOfBatches){
        StringBuilder ans = new StringBuilder();
        for(Student student : students){
            StringBuilder studentDescription = new StringBuilder(student.getName() + " Trained models are:\n");
            HashSet <Model> published = student.getPublishedModels();
            for( Model m : student.getTrainedModels()){
                studentDescription.append(m.getName()).append(" ");
                studentDescription.append(published.contains(m) ? "Published\n" : "Not published\n");
            }
            studentDescription.append("Number of papers read: ").append(student.getPapersRead());
            ans.append(studentDescription);
        }
        for(ConfrenceInformation conf : confs){
            StringBuilder confDescription = new StringBuilder(conf.getName() + " publications are:\n");
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

    public static JsonObject buildJSONObject(String path){
        File input = new File(path);
        JsonObject inputObject = null;
        try {
            JsonElement inputElement = JsonParser.parseReader(new FileReader(input));
            inputObject = inputElement.getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputObject;
    }

    public static List<Student> BuildStudents(JsonObject input){
        JsonArray jsonArrayOfStudents = input.get("Students").getAsJsonArray();
        List<Student> students = new LinkedList<>();
        for(JsonElement studentElement : jsonArrayOfStudents){
            JsonObject studentObject = studentElement.getAsJsonObject();
            String name = studentObject.get("name").getAsString();
            String department = studentObject.get("department").getAsString();
            String status = studentObject.get("status").getAsString();
            List<Model> models = BuildModels(studentObject);
            students.add(new Student(name,department,status,models));
        }
        return students;
    }
    public static List<Model> BuildModels(JsonObject input){
        JsonArray jsonArrayOfModels = input.get("models").getAsJsonArray();
        List<Model> models = new LinkedList<>();
        for(JsonElement modelElemet : jsonArrayOfModels){
            JsonObject modelObject = modelElemet.getAsJsonObject();
            String name = modelObject.get("name").getAsString();
            String type = modelObject.get("type").getAsString();
            int size = modelObject.get("size").getAsInt();
            models.add(new Model(name, type, size));
        }
        return models;
    }

    public static List<GPU> BuildGPUs(JsonObject input){
        JsonArray jsonArrayOfGPUs = input.get("GPUS").getAsJsonArray();
        List<GPU> GPUs = new LinkedList<>();
        int arrySize = jsonArrayOfGPUs.size();
        for (int i=0; i<arrySize; i++){
            String type = jsonArrayOfGPUs.get(0).getAsString();
            GPUs.add(new GPU(type));
        }
        return GPUs;
    }
    public static List<CPU> BuildCPUs(JsonObject input){
        JsonArray jsonArrayOfCPUs = input.get("CPUS").getAsJsonArray();
        List<CPU> CPUs = new LinkedList<>();
        int arrySize = jsonArrayOfCPUs.size();
        for (int i=0; i<arrySize; i++){
            int cores = jsonArrayOfCPUs.get(i).getAsInt();
            CPUs.add(new CPU(cores));
        }
        return CPUs;
    }
    public static List<ConfrenceInformation> BuildConferences(JsonObject input){
        JsonArray jsonArrayOfConferences = input.getAsJsonArray("Conferences").getAsJsonArray();
        List<ConfrenceInformation> conferences = new LinkedList<>();
        for(JsonElement conferenceElement : jsonArrayOfConferences){
            JsonObject conferenceObject = conferenceElement.getAsJsonObject();
            String name = conferenceObject.get("name").getAsString();
            int date = conferenceObject.get("date").getAsInt();
            conferences.add(new ConfrenceInformation(name,date));
        }
        return conferences;
    }
    public static TimeService BuildTimeService(JsonObject input){

        TimeService timeService = null;
        int tickTime = input.get("TickTime").getAsInt();
        int duration = input.get("Duration").getAsInt();
        timeService = new TimeService(duration,tickTime);
        return timeService;
    }



}


