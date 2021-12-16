package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    ConferenceService service;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
    }
    public ConfrenceInformation(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }
    public void setConferenceService(ConferenceService conferenceService){
        this.service = conferenceService;
    }
    public  ConcurrentLinkedQueue<String> getNames(){
        return this.service.getNames();
    }
}
