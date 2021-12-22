package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */


public class DataBatch {
    private final Data data;
    private GPU gpu;

    public DataBatch(Data data, int start_index) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public GPU getGpu() {
        return gpu;
    }

    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }
}
