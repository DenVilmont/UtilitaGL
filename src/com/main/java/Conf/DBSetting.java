package com.main.java.Conf;

public class DBSetting {
    private String name = null;
    private String type = null;
    private String host = null;
    private String port = null;
    private String service = null;
    private String login = null;
    private String password = null;
    private String connectionString = null;

    public DBSetting(){}

    public DBSetting(String name, String type, String host, String port, String service, String login, String password) {
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.service = service;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
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
    public String getService() {
        return service;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public String getConnectionString(){return String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, service); }

    public void setName(String name) {
        this.name = name;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public void setPort(Integer port) {
        this.port = String.valueOf(port);
    }
    public void setService(String service) {
        this.service = service;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
