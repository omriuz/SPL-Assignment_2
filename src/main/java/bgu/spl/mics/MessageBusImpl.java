package bgu.spl.mics;
import bgu.spl.mics.application.messages.TerminateBroadcast;

import java.lang.Override;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentMap<Class<? extends Event>, BlockingQueue<MicroService>> eventsQueuesOfServices;
	private ConcurrentMap<Class<? extends Broadcast>, BlockingDeque<MicroService>> broadcastsListsOfServices;
	private Map<MicroService, BlockingDeque<Message>> microservicesQueues;
	private ConcurrentMap<Event, Future> eventsFutures;
	private Object lockBroadcast;
	private Object lockEvent;
	private Object lockMicroServices;
	private Object lockFuture;

	private static class SingletonHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl() {
		this.eventsQueuesOfServices = new ConcurrentHashMap<>();
		this.broadcastsListsOfServices = new ConcurrentHashMap<>();
		this.microservicesQueues = new ConcurrentHashMap<>();
		this.eventsFutures = new ConcurrentHashMap<>();
		this.lockBroadcast = new Object();
		this.lockEvent = new Object();
		this.lockMicroServices = new Object();
		this.lockFuture = new Object();

	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	public <T> boolean isSubscribeToEvent(MicroService m, Class<? extends Event> type) {
		return eventsQueuesOfServices.get(type).contains(m);
	}

	public <T> boolean isSubscribeToBroadcast(MicroService m, Class<? extends Broadcast> type) {
		return broadcastsListsOfServices.get(type).contains(m);
	}

	public boolean isRegistered(MicroService m) {
		return microservicesQueues.containsKey(m);
	}

	public boolean inQueue(MicroService m, Message message) {
		return microservicesQueues.get(m).contains(message);
	}

	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
//		synchronized (lockEvent) {
		eventsQueuesOfServices.putIfAbsent(type, new LinkedBlockingDeque<>());
		eventsQueuesOfServices.get(type).add(m); // add m to the Queue
//		}
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
//		synchronized (lockBroadcast) {
		broadcastsListsOfServices.putIfAbsent(type, new LinkedBlockingDeque<>());
		broadcastsListsOfServices.get(type).add(m); // add m to the Queue
//		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
//		synchronized (lockEvent) {
		eventsFutures.get(e).resolve(result);
//		}
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
//		synchronized (lockMicroServices) {
		for (MicroService m : broadcastsListsOfServices.get(b.getClass())) { // for each microservice in b type, add b to its queue.
			if (b.getClass() == TerminateBroadcast.class)
				microservicesQueues.get(m).addLast(b);
			else
				microservicesQueues.get(m).addLast(b);
		}
//		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		MicroService next = null;
		if(!eventsQueuesOfServices.get(e.getClass()).isEmpty()){
			synchronized (lockEvent) {
				next = eventsQueuesOfServices.get(e.getClass()).poll(); //get the next microservice to deal with e type
				eventsQueuesOfServices.get(e.getClass()).add(next); // return next the end of the e type queue
			}
//				synchronized (lockMicroServices) {
			microservicesQueues.get(next).addFirst(e); // add e to next queue
//				}
//				synchronized (lockFuture) {
			eventsFutures.put(e, future); // crate a connection between e and the new future
//				}
		}
		return future;
	}

	@Override
	public synchronized void register(MicroService m) {
//		synchronized (lockMicroServices) {
		microservicesQueues.putIfAbsent(m, new LinkedBlockingDeque<>());
//		}
	}

	@Override
	public synchronized void unregister(MicroService m) {
		// TODO: need to deal with the references related to this microservice
//		synchronized (lockMicroServices) {
		microservicesQueues.remove(m);
//		}
//		synchronized (lockEvent) {
		for (Map.Entry<Class<? extends Event>, BlockingQueue<MicroService>> entry : eventsQueuesOfServices.entrySet()) {
			if (isSubscribeToEvent(m, entry.getKey()))
				entry.getValue().remove(m);
		}
//		}
//		synchronized (lockBroadcast) {
		for (Map.Entry<Class<? extends Broadcast>, BlockingDeque<MicroService>> entry : broadcastsListsOfServices.entrySet()) {
			if (isSubscribeToBroadcast(m, entry.getKey()))
				entry.getValue().remove(m);
		}
//		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
//		synchronized (lockMicroServices) {
		return microservicesQueues.get(m).take();
//		}

	}

	public void unsubscribeBroadcast(Class<? extends Broadcast> type, MicroService m){
		broadcastsListsOfServices.get(type).remove(m); // add m to the Queue
	}

}
