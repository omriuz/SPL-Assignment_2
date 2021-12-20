package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Date;
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
	private int currentTime;
	private int percent;
	private boolean printed;

	public TimeService() {
		super("TimeService");
	}
	public TimeService(int duration, int speed){
		super("TimeService");
		timer = new Timer();
		this.duration = duration;
		this.speed = speed;
		this.currentTime = 0;
		this.percent = 0;
		this.printed = false;
	}

	@Override
	protected void initialize() {
		TimerTask tickTask = new TimerTask(){
			public void run(){
				if(currentTime<=duration) {
					sendBroadcast(new TickBroadcast(currentTime));
					currentTime += speed;
					percent = (currentTime* 100/duration);
//					System.out.println(percent + " ____________ " + currentTime);
					if(percent % 10 == 0){
						if (!printed) {
							printed = true;
							System.out.println(percent + "%");
						}
					}else printed = false;
				}
				else{
					sendBroadcast(new TerminateBroadcast());
					timer.purge();
					timer.cancel();
				}
			}
		};
//		timer.schedule(tickTask,1000,speed);
		timer.scheduleAtFixedRate(tickTask,1000,speed);
		subscribeBroadcast(TerminateBroadcast.class,(t)->{
			terminate();
		});

		
	}

}
