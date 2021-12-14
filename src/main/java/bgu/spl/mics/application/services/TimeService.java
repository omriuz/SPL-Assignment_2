package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private Timer timer ;
	private long speed;
	private long duration;

	public TimeService() {
		super("TimeService");
	}
	public TimeService(int duration, int speed){
		super("TimeService");
		timer = new Timer();
		this.duration = duration;
		this.speed = speed;
	}

	@Override
	protected void initialize() {
		TimerTask tickTask = new TimerTask(){
			public void run(){
				while(duration>=0) {
					sendBroadcast(new TickBroadcast());
					duration -= speed;
				}
				timer.cancel();
				timer.purge();
			}
		};
		timer.schedule(tickTask,speed);
		
	}

}
