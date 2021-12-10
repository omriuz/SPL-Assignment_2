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
	private ConcurrentHashMap<Class<? extends Event>, CountList> eventsListsOfServiseces;
	private ConcurrentHashMap<Class<? extends Event>, List<Integer>> broadcastsListsOfServiseces;
	private ConcurrentHashMap<Integer, Queue<Message>> microservicesQueus;
	private ConcurrentHashMap<Integer, Future> microservicesFutures;
	//	private HashMap<Integer, MicroService> mocroservicesId;
	private int nextId = 0;
	private static final MessageBusImpl instance = new MessageBusImpl();

	private MessageBusImpl(){
	}

	@Override
	public void addFuture(int id, Future future){
	}

	public static MessageBusImpl getInstance(){
		return instance;
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
