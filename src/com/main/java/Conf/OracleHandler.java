package com.main.java.Conf;


import java.sql.*;

public class OracleHandler {
    private String CON_STR;
    private String typeDB = "APSPP";
    private DBSetting dbSetting;


    private static OracleHandler instance = null;

    public static synchronized OracleHandler getInstance(DBSetting dbSetting){
        if (instance == null)
            instance = new OracleHandler(dbSetting);
        return instance;
    }

    private OracleHandler(DBSetting dbSetting) {
        this.CON_STR = dbSetting.getConnectionString();
        this.typeDB = dbSetting.getType();
        this.dbSetting = dbSetting;
    }
    private Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(CON_STR, dbSetting.getLogin(), dbSetting.getPassword());
        return connection;
    }

    public String getRequestDocumentFromDB(String envelope){
        String query = "";
        switch (typeDB){
            case "APSPP": query = "SELECT MESSAGEBODY AS MESSAGE FROM messages WHERE ENVELOPEID = ?"; break;
            case "UPI"  : query = "SELECT d.BLOB_DATA AS MESSAGE FROM message_info m, document d where m.SOURCE = d.ID AND m.ENVELOPE_ID = ?";break;
            default: return null;
        }
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, envelope);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                Blob blob = resultSet.getBlob("MESSAGE");
                return XmlUtil.getStringFromBlob(blob);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
