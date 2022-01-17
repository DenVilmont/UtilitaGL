package com.main.java.Conf;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class SQLiteHandler {
    private static final String CON_STR = "jdbc:sqlite:" + System.getProperty("user.dir") + "/UtilSupDB.db";


    private static SQLiteHandler instance = null;

    public static synchronized SQLiteHandler getInstance(){
        if (instance == null)
            instance = new SQLiteHandler();
        return instance;
    }

    private Connection connection;

    private SQLiteHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement statement = this.connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS MQSettings " +
                    "(Id integer PRIMARY KEY AUTOINCREMENT," +
                    " name text, " +
                    " host text, " +
                    " port text, " +
                    " channel text, " +
                    " qmgr text, " +
                    " ultimaQueue text)";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS queues " +
                    "(Id integer PRIMARY KEY AUTOINCREMENT," +
                    " mqname text REFERENCES MQSettings(name) on delete cascade on update cascade, " +
                    " queuename text)";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Messages " +
                    "(Id integer PRIMARY KEY AUTOINCREMENT," +
                    " messagetype text, " +
                    " generarGUID text, " +
                    " replaceGUIDFrom text, " +
                    " replaceGUIDTo text)";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Databases " +
                    "(Id integer PRIMARY KEY AUTOINCREMENT," +
                    " name text, " +
                    " type text, " +
                    " host text, " +
                    " port text, " +
                    " service text, " +
                    " login text, " +
                    " password text)";
            statement.executeUpdate(sql);



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewMQSetting(MQSetting mqSetting) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO MQSettings(`name`, `host`, `port`, `channel`, `qmgr`) " +
                        "VALUES(?, ?, ?, ?, ?)")) {
            statement.setObject(1, mqSetting.getName());
            statement.setObject(2, mqSetting.getHost());
            statement.setObject(3, mqSetting.getPortString());
            statement.setObject(4, mqSetting.getChannel());
            statement.setObject(5, mqSetting.getQmgr());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMQSetting(String name) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM MQSettings WHERE name = ?")) {
            statement.setObject(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, MQSetting> getAllMQSettings() {
        try (Statement statement = this.connection.createStatement()) {
            HashMap<String, MQSetting> mqSettings = new HashMap<>();
            ResultSet resultSet = statement.executeQuery("SELECT name, host, port, channel, qmgr, ultimaQueue FROM MQSettings");
            while (resultSet.next()) {
                MQSetting message = new MQSetting(resultSet.getString("name"),
                        resultSet.getString("host"),
                        resultSet.getString("port"),
                        resultSet.getString("channel"),
                        resultSet.getString("qmgr"),
                        resultSet.getString("ultimaQueue"));
                mqSettings.put(message.getName(), message);
            }

            return mqSettings;

        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public HashMap<String, Message> getAllMessages(){
        try (Statement statement = this.connection.createStatement()) {
            HashMap<String, Message> messages = new HashMap<>();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT messagetype FROM Messages");
            while (resultSet.next()) {
                Message message = new Message(resultSet.getString("messagetype"));
                message.setGenerarGUID(getGenerarGUIDs(message.getMessageType()));
                message.setReplaceGUID(getReplaceGUIDs(message.getMessageType()));
                messages.put(message.getMessageType(),message);
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    public ArrayList<String> getGenerarGUIDs(String messageType){
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT generarGUID FROM Messages WHERE messagetype = ?")) {
            statement.setObject(1, messageType);
            ArrayList<String> guids = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String guid = resultSet.getString("generarGUID");
                if (guid != null && !guid.isEmpty()) {
                    guids.add(guid);
                }
            }

            return guids;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public ArrayList<String[]> getReplaceGUIDs(String messageType){
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT replaceGUIDFrom, replaceGUIDTo FROM Messages WHERE messagetype = ?")) {
            statement.setObject(1, messageType);
            ArrayList<String[]> guids = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String guidFrom = resultSet.getString("replaceGUIDFrom");
                String guidTo = resultSet.getString("replaceGUIDTo");
                if (guidFrom != null && guidTo != null && !guidFrom.isEmpty() && !guidTo.isEmpty()) {
                    guids.add(new String[]{guidFrom, guidTo});
                }
            }

            return guids;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public void addNewMessage(Message message) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO Messages(messagetype, generarGUID, replaceGUIDFrom, replaceGUIDTo) VALUES(?, ?, ?, ?)")) {
            statement.setObject(1, message.getMessageType());
            statement.setObject(2, message.getGenerarGUID().get(0));
            statement.setObject(3, message.getReplaceGUID().get(0)[0]);
            statement.setObject(4, message.getReplaceGUID().get(0)[1]);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteMessage(String messageType) {
        try (PreparedStatement statement = this.connection.prepareStatement("DELETE FROM Messages WHERE messagetype = ?")) {
            statement.setObject(1, messageType);
            statement.executeUpdate();
            vacuum();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarMessage(String oldMesType, String newMesType) {
        try (PreparedStatement statement = this.connection.prepareStatement("UPDATE Messages SET messagetype = ? WHERE messagetype = ?")) {
            statement.setObject(1, newMesType);
            statement.setObject(2, oldMesType);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewGenerarGuid(String messageType, String guid) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO Messages(messagetype, generarGUID) VALUES(?, ?)")) {
            statement.setObject(1, messageType);
            statement.setObject(2, guid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGenerarGUID(String messageType, String guid) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Messages WHERE messagetype = ? AND generarGUID = ?")) {
            statement.setObject(1, messageType);
            statement.setObject(2, guid);
            statement.executeUpdate();
            vacuum();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarGenerarGUID(String messageType, String oldGuid, String newGuid) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE Messages SET generarGUID = ? WHERE messagetype = ? AND generarGUID = ?")) {
            statement.setObject(1, newGuid);
            statement.setObject(2, messageType);
            statement.setObject(3, oldGuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReplaceGUID(String messageType, String guidFrom, String guidTo) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Messages WHERE messagetype = ? AND replaceGUIDFrom = ? AND replaceGUIDTo = ?")) {
            statement.setObject(1, messageType);
            statement.setObject(2, guidFrom);
            statement.setObject(3, guidTo);
            statement.executeUpdate();
            vacuum();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambiarReplaceGUID(String messageType, String oldGuidFrom, String oldGuidTo, String newGuidFrom, String newGuidTo) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE Messages SET replaceGUIDFrom = ?, replaceGUIDTo = ? " +
                " WHERE messagetype = ? AND replaceGUIDFrom = ? AND replaceGUIDTo = ?")) {
            statement.setObject(1, newGuidFrom);
            statement.setObject(2, newGuidTo);
            statement.setObject(3, messageType);
            statement.setObject(4, oldGuidFrom);
            statement.setObject(5, oldGuidTo);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewReplaceGuid(String messageType, String guidFrom, String guidTo) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO Messages(messagetype, replaceGUIDFrom, replaceGUIDTo) " +
                        "VALUES(?, ?, ?)")) {
            statement.setObject(1, messageType);
            statement.setObject(2, guidFrom);
            statement.setObject(3, guidTo);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void vacuum(){
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("VACUUM;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] getMQNames() {
        try (Statement statement = this.connection.createStatement()) {
            ArrayList<String> mqNames = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM MQSettings");
            while (resultSet.next()) {
                mqNames.add(resultSet.getString("name"));
            }
            String[] arrMQNames = new String[mqNames.size()];

            return mqNames.toArray(arrMQNames);

        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{"Настройки MQ оттсутствуют"};
        }
    }

    public MQSetting getMQ(String mqName) {
        try (PreparedStatement statement = this.connection.prepareStatement("SELECT name, host, port, channel, qmgr, ultimaQueue FROM MQSettings WHERE name = ?")) {
            statement.setObject(1, mqName);
            MQSetting mqSetting = new MQSetting();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                mqSetting = new MQSetting(resultSet.getString("name"),
                        resultSet.getString("host"),
                        resultSet.getString("port"),
                        resultSet.getString("channel"),
                        resultSet.getString("qmgr"),
                        resultSet.getString("ultimaQueue"));
            }

            return mqSetting;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getQueues(MQSetting mqSetting) {

        try (PreparedStatement statement = this.connection.prepareStatement("SELECT queuename FROM queues WHERE mqname = ?")) {
            statement.setObject(1, mqSetting.getName());
            ArrayList<String> queuesList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                queuesList.add(resultSet.getString("queuename"));
            }
            return queuesList;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removeQueues(MQSetting mqSetting) {
        try (PreparedStatement statement = this.connection.prepareStatement("DELETE FROM queues WHERE mqname = ?")) {
            statement.setObject(1, mqSetting.getName());
            statement.executeUpdate();
            vacuum();
        } catch (SQLException e) {
            MQLog.write("При удалении очередей из БД произошла ошибка: " + e.getMessage());
        }
    }

    public void addQueues(MQSetting mqSetting, ArrayList<String> queuesList) {
        try (PreparedStatement statement = this.connection.prepareStatement("INSERT INTO queues(`mqname`, `queuename`) VALUES(?, ?)")) {
            statement.setObject(1, mqSetting.getName());
            for (String queue : queuesList) {
                statement.setObject(2, queue);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            MQLog.write("При добавлении очередей в БД произошла ошибка: " + e.getMessage());
        }
    }

    public void setUltimaQueue(MQSetting mqSetting, String queue) {
        try (PreparedStatement statement = this.connection.prepareStatement("UPDATE MQSettings SET ultimaQueue = ? " +
                " WHERE name = ?")) {
            statement.setObject(1, queue);
            statement.setObject(2, mqSetting.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, DBSetting> getAllDBSettings() {
        try (Statement statement = this.connection.createStatement()) {
            HashMap<String, DBSetting> dbSettings = new HashMap<>();
            ResultSet resultSet = statement.executeQuery("SELECT name, type, host, port, service, login, password FROM Databases");
            while (resultSet.next()) {
                DBSetting dbSetting = new DBSetting(resultSet.getString("name"),
                        resultSet.getString("type"),
                        resultSet.getString("host"),
                        resultSet.getString("port"),
                        resultSet.getString("service"),
                        resultSet.getString("login"),
                        resultSet.getString("password"));
                dbSettings.put(dbSetting.getName(), dbSetting);
            }

            return dbSettings;

        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void addNewDBSetting(DBSetting dbSetting) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO Databases(name, type, host, port, service, login, password) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, dbSetting.getName());
            statement.setObject(2, dbSetting.getType());
            statement.setObject(3, dbSetting.getHost());
            statement.setObject(4, dbSetting.getPortString());
            statement.setObject(5, dbSetting.getService());
            statement.setObject(6, dbSetting.getLogin());
            statement.setObject(7, dbSetting.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteDBSetting(String name) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Databases WHERE name = ?")) {
            statement.setObject(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cambuarMQSetting(String name, MQSetting mqSetting) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE MQSettings SET name = ?, host = ?, port = ?, channel = ?, qmgr = ?, ultimaQueue = ? " +
                        " WHERE name = ?")) {
            statement.setObject(1, mqSetting.getName());
            statement.setObject(2, mqSetting.getHost());
            statement.setObject(3, mqSetting.getPortString());
            statement.setObject(4, mqSetting.getChannel());
            statement.setObject(5, mqSetting.getQmgr());
            statement.setObject(6, mqSetting.getUltimaQueue());
            statement.setObject(7, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}