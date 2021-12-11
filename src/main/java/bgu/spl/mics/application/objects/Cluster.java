package bgu.spl.mics.application.objects;

import com.sun.org.glassfish.external.statistics.Statistic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Collection<CPU> CPUs;
	private Collection<GPU> GPUs;
	private BlockingQueue<DataBatch> unprocessed;
	private ConcurrentMap<GPU,BlockingQueue<DataBatch>> GPUsDataQueues;
	private Statistic statistic; //TODO crate Statistic callas.

	private static class SingeltonHolder{
		private static Cluster instance = new Cluster();
	}


	private Cluster(){
		unprocessed = new LinkedBlockingQueue<>();
		this.CPUs = new LinkedList<>();
		this.GPUs = new LinkedList<>();
		this.GPUsDataQueues = new ConcurrentHashMap<>();

	}
	public static Cluster getInstance() {
		return Cluster.SingeltonHolder.instance;
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