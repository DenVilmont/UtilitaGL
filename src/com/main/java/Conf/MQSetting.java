package com.main.java.Conf;


import java.util.ArrayList;

public class MQSetting{
    private String name = null;
    private String host = null;
    private String port = null;
    private String channel = null;
    private String qmgr = null;
    private String ultimaQueue = "";
    private ArrayList<String> queues;


    public String getName() {
        return name;
    }
    public String getHost() {
        return host;
    }
    public String getPortString() {
        return port;
    }
    public Integer getPortInt() {
        return Integer.parseInt(port);
    }
    public String getChannel() {
        return channel;
    }
    public String getQmgr() {
        return qmgr;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<String> getQueues() {
        return queues;
    }
    public String getUltimaQueue() {
        return ultimaQueue;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public void setQmgr(String qmgr) {
        this.qmgr = qmgr;
    }
    public void setQueues(ArrayList<String> queues) {
        this.queues = queues;
    }
    public void setUltimaQueue(String ultimaQueue) {
        this.ultimaQueue = ultimaQueue;
    }

    public MQSetting(){}
    public MQSetting(String name, String host, String port, String channel, String qmgr, String ultimaQueue) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.qmgr = qmgr;
        this.ultimaQueue = ultimaQueue;
    }
}
