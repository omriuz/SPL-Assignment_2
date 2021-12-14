package bgu.spl.mics;
import java.lang.Override;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentMap<Class<? extends Event>, BlockingQueue<MicroService>> eventsQueuesOfServices;
	private ConcurrentMap<Class<? extends Broadcast>, Vector<MicroService>> broadcastsListsOfServices;
	private ConcurrentMap<MicroService, BlockingQueue<Message>> microservicesQueus;
	private ConcurrentMap<Event, Future> eventsFutures;

	private static class SingletonHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl(){
		this.eventsQueuesOfServices = new ConcurrentHashMap<>();
		this.broadcastsListsOfServices = new ConcurrentHashMap<>();
		this.microservicesQueus = new ConcurrentHashMap<>();
		this.eventsFutures = new ConcurrentHashMap<>();
	}
	public static synchronized MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}

	private void addFuture(Event e, Future future){
		eventsFutures.put(e, future);
	}


	public <T> boolean isSubscribeToEvent(MicroService m, Class<? extends Event<T>> type){
		return eventsQueuesOfServices.get(type).contains(m);
	}


	public <T> boolean isSubscribeToBroadcast(MicroService m, Class<? extends Broadcast> type){
		return broadcastsListsOfServices.get(type).contains(m);
	}

	public boolean isRegistered(MicroService m){
		return microservicesQueus.containsKey(m);
	}

	public boolean inQueue(MicroService m, Message message){
		return microservicesQueus.get(m).contains(message);
	}

	@Override
	public synchronized  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		eventsQueuesOfServices.putIfAbsent(type, new LinkedBlockingDeque<>());
		eventsQueuesOfServices.get(type).add(m); // add m to the Queue
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		broadcastsListsOfServices.putIfAbsent(type, new Vector<>());
		broadcastsListsOfServices.get(type).add(m); // add m to the Queue
	}



	@Override
	public synchronized  <T> void complete(Event<T> e, T result) {
		eventsFutures.get(e).resolve(result);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		for(MicroService m : broadcastsListsOfServices.get(b.getClass())){ // for each microservice in b type, add b to its queue.
			microservicesQueus.get(m).add(b);
		}
	}

	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		try {
			MicroService next = eventsQueuesOfServices.get(e.getClass()).take(); //get the next microservice to deal with e type
			microservicesQueus.get(next).put(e); // add e to next queue
			eventsQueuesOfServices.get(e.getClass()).put(next); // return next the end of the e type queue
			eventsFutures.put(e,future); // crate a connection between e and the new future
		}catch (InterruptedException inter){};

		return future;
	}

	@Override
	public synchronized void register(MicroService m) {
			microservicesQueus.putIfAbsent(m, new LinkedBlockingDeque<>());
	}

	@Override
	public synchronized void unregister(MicroService m) {
		// need to deal with the references related to this microservice
			microservicesQueus.remove(m);
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		return microservicesQueus.get(m).take();
	}



}
