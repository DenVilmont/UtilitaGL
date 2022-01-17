package com.main.java.Conf;

import java.util.ArrayList;

public class Message {
    private String messageType;

    private ArrayList<String> generarGUID = new ArrayList<>();

    private ArrayList<String[]> replaceGUID = new ArrayList<>();

    public Message(){}
    public Message(String messageType){
        this.messageType = messageType;
    }

    public void setMessageType(String messageType) {

        this.messageType = messageType;
    }
    public void setGenerarGUID(ArrayList<String> generarGUID) {

        this.generarGUID = generarGUID;
    }
    public void setReplaceGUID(ArrayList<String[]> replaceGUID) {
        this.replaceGUID = replaceGUID;
    }


    public String getMessageType() {

        return messageType;
    }
    public ArrayList<String> getGenerarGUID() {
        return generarGUID;
    }
    public ArrayList<String[]> getReplaceGUID() {
        return replaceGUID;
    }
}
