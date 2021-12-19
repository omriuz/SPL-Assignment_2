package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private String name;
    private int date;
    private ConcurrentLinkedQueue<String> names;
    ConferenceService service;

    public ConferenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        this.names = new ConcurrentLinkedQueue<>();
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
        return this.names;
    }
    public void addName(String name){
        names.add(name);
    }
}
