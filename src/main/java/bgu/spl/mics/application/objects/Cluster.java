package bgu.spl.mics.application.objects;


/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {


	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		//TODO: Implement this
		return null;
	}

	public void sendDataBatchToCluster(DataBatch batch){
			unprocessed.add(batch);
	}
	public DataBatch sendDataBatchToGPU(GPU gpu){
		DataBatch d = null;
		try{
			d = GPUsDataQueues.get(gpu).take();
		}
		catch (InterruptedException e) {}
		return d;
	}

	public void sendDataBatchToCPU(CPU cpu){
		try {
			cpu.addDataBatch(unprocessed.take());
		}catch (InterruptedException I){};
	}

	public void addProcessedData(DataBatch dataBatch){
		GPUsDataQueues.get(dataBatch.getGpu()).add(dataBatch);
	}
	public Boolean isThereDataBatch(GPU gpu){
		return !GPUsDataQueues.get(gpu).isEmpty();
	}
}
