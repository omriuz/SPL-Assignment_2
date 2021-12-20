package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws InterruptedException {
////        Student firstStudent = new Student("Simba","Computer Science", Student.Degree.MSc);
////        Model firstModel = new Model("YOLO10",new Data(Data.Type.Images,200000),firstStudent);
////        firstStudent.addModel(firstModel);

//        GPU firstGpu = new GPU(GPU.Type.RTX3090);

//        CPU cpu = new CPU(32);

//        ConfrenceInformation first = new ConfrenceInformation("ICML",2000);



//        createOutputFile(getOutputString(students,confrences,gpuTime.get(),cpuTime.get(),amountOfBatches.get()));
        //TODO: check path before submission
        JsonObject input = buildJSONObject("C:\\Users\\omri9\\Documents\\GitHub\\Assignment_2\\example_input.json");
        List<Student> students = BuildStudents(input);
        List<ConferenceInformation> conferences = BuildConferences(input);
        List<GPU> gpus = BuildGPUs(input);
        List<CPU> cpus = BuildCPUs(input);
        List<GPUService> gpuServices = buildGpuServices(gpus);
        List<CPUService> cpuServices = buildCpuServices(cpus);
        List<ConferenceService> conferenceServices = buildConferenceServices(conferences);
        List<StudentService> studentServices = buildStudentServices(students);
        List<Thread> threads = new LinkedList<>();
        TimeService timer = BuildTimeService(input);
        for(CPUService service : cpuServices)
            threads.add(new Thread(service,service.getName()));
        for(GPUService service : gpuServices)
            threads.add(new Thread(service,service.getName()));
        for(ConferenceService service : conferenceServices)
            threads.add(new Thread(service,service.getName()));
        for(StudentService service : studentServices)
            threads.add((new Thread(service,service.getName())));
        Thread timing = new Thread(timer,"timing");
        for(Thread thread : threads)
            thread.start();
        Thread.sleep(1000);
        timing.start();
        timing.join();
        for(Thread thread : threads)
            thread.interrupt();
        int cpuTime = sumCpuTime(cpus);
        int gpuTime = sumGpuTime(gpus);
        int amountOfBatches = sumAmount(cpus);
        System.out.println(amountOfBatches + "_______________________" + cpuTime);
        createOutputFile(getOutputString(students,conferences,gpuTime,cpuTime,amountOfBatches));
        System.out.println("finished the program");
        //TODO: make sure that all the threads are cancelled

    }

    private static int sumAmount(List<CPU> cpus) {
        int sum = 0;
        for(CPU cpu : cpus) {
            sum += cpu.getAmount();
            System.out.println(cpu.getCount());
        }
        return sum;
    }

    private static int sumGpuTime(List<GPU> gpus) {
        int sum = 0;
        for(GPU gpu : gpus) {
            sum += gpu.getCount();
            System.out.println(gpu.getCount());
        }
        return sum;
    }

    private static int sumCpuTime(List<CPU> cpus) {
        int sum = 0;
        for(CPU cpu : cpus)
            sum += cpu.getCount();
        return sum;
    }

    public static String getOutputString(List<Student> students, List<ConferenceInformation> confs, int gpuTime, int cpuTime, int amountOfBatches){
        StringBuilder ans = new StringBuilder();
        ans.append("Students:\n\n");
        for(Student student : students){
            StringBuilder studentDescription = new StringBuilder("\tStudent name: " + student.getName() +"\n\tDepartment: " + student.getDepartment()+"\n\tDegree status: ");
            studentDescription.append(student.getDegStatus());
            studentDescription.append("\n\tPapers read: ").append(student.getPapersRead());
            studentDescription.append("\n\tPublications: ").append(student.getPublications());
            studentDescription.append("\n\tthe trained models are:\n\n");
            HashSet <Model> published = student.getPublishedModels();
            for( Model m : student.getTrainedModels()){
                studentDescription.append("\t\tModel name: ").append(m.getName()).append("\n\t\tType: ").append(m.getData().getTypeName()).append("\n\t\tSize: ").append(m.getData().getSize());
                studentDescription.append("\n\t\tStatus: ").append(m.getStatusString());
                if(m.getStatus()== Model.Status.Tested) studentDescription.append("\n\t\tResult: ").append(m.getResults().toString());
                studentDescription.append("\n\t\tPublished: ").append(published.contains(m) ? "YES\n\n" : "NOT\n\n");
            }
            ans.append(studentDescription);
        }
        ans.append("Conferences:\n");
        for(ConferenceInformation conf : confs){
            StringBuilder confDescription = new StringBuilder("For Conference " + conf.getName() + " the publications are:\n");
            for(String name : conf.getNames()) confDescription.append(name).append(" ");
            ans.append(confDescription + "\n");
        }
        ans.append("In total:\n");
        ans.append("\n").append("CPU Time: ").append(cpuTime).append("\n");
        ans.append("GPU Time: ").append(gpuTime).append("\n");
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
            models.sort((x, y) -> Integer.compare( x.getData().getSize(),y.getData().getSize()));
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
            Model model = new Model(name, type, size);
            models.add(model);
        }
        return models;
    }

    public static List<GPU> BuildGPUs(JsonObject input){
        JsonArray jsonArrayOfGPUs = input.get("GPUS").getAsJsonArray();
        List<GPU> GPUs = new LinkedList<>();
        int arrySize = jsonArrayOfGPUs.size();
        for (int i=0; i<arrySize; i++){
            String type = jsonArrayOfGPUs.get(i).getAsString();
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
    public static List<ConferenceInformation> BuildConferences(JsonObject input){
        JsonArray jsonArrayOfConferences = input.getAsJsonArray("Conferences").getAsJsonArray();
        List<ConferenceInformation> conferences = new LinkedList<>();
        for(JsonElement conferenceElement : jsonArrayOfConferences){
            JsonObject conferenceObject = conferenceElement.getAsJsonObject();
            String name = conferenceObject.get("name").getAsString();
            int date = conferenceObject.get("date").getAsInt();
            conferences.add(new ConferenceInformation(name,date));
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

    public static List<GPUService> buildGpuServices(List<GPU> gpus){
        List<GPUService> ans = new LinkedList<>();
        int counter = 0;
        for(GPU gpu : gpus)
            ans.add(new GPUService(String.valueOf(counter++),gpu));
        return ans;
    }
    public static List<CPUService> buildCpuServices(List<CPU> cpus){
        List<CPUService> ans = new LinkedList<>();
        int counter = 0;
        for(CPU cpu : cpus)
            ans.add(new CPUService(String.valueOf(counter++),cpu));
        return ans;
    }
    public static List<ConferenceService> buildConferenceServices(List<ConferenceInformation> confs){
        List<ConferenceService> ans = new LinkedList<>();
        for(ConferenceInformation conf : confs)
            ans.add(new ConferenceService(conf));
        return ans;
    }
    public static List<StudentService> buildStudentServices(List<Student> students){
        List<StudentService> ans = new LinkedList<>();
        int counter = 0;
        for(Student student : students)
            ans.add(new StudentService(String.valueOf(counter++),student));
        return ans;
    }
//        StudentService firstStudentService = new StudentService("first",students.get(0));


}
