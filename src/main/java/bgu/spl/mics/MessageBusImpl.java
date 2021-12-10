package bgu.spl.mics;
import java.lang.Override;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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

	private static class SingeltonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl(){
		this.eventsQueuesOfServices = new ConcurrentHashMap<>();
		this.broadcastsListsOfServices = new ConcurrentHashMap<>();
		this.microservicesQueus = new ConcurrentHashMap<>();
		this.eventsFutures = new ConcurrentHashMap<>();
	}
	public static MessageBusImpl getInstance(){
		return SingeltonHolder.instance;
	}

	private void addFuture(Event e, Future future){
		eventsFutures.put(e, future);
	}

	@Override
	public <T> boolean isSubscribeToEvent(int id, Class<? extends Event<T>> type){
		return eventsListsOfServiseces.get(type).isIn(id);
	}

	@Override
	public <T> boolean isSubscribeToBroadcast(int id, Class<? extends Broadcast> type){
		return broadcastsListsOfServiseces.get(type).contains(id);
	}

	public int getNextId(){
		int next = nextId;
		nextId++;
		return next;
	}
	@Override
	public boolean isRegister(int id){
		return true;
	}

	public boolean inQueue(int id, Message message){
		return true;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}



	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}



}
