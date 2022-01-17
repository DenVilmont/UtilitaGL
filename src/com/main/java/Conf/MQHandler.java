package com.main.java.Conf;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MQHandler {
    private static MQHandler instance = null;
    private final SQLiteHandler dbHandler = SQLiteHandler.getInstance();

    public static synchronized MQHandler getInstance(){
        if (instance == null)
            instance = new MQHandler();
        return instance;
    }

    private MQHandler() {}

    public boolean sendMessage(MQSetting mqSetting, String que, String message){

        Connection connection;
        Session session;
        MessageProducer producer;
        try {
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            JmsConnectionFactory cf = ff.createConnectionFactory();

            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, mqSetting.getHost());
            cf.setIntProperty(WMQConstants.WMQ_PORT, mqSetting.getPortInt());
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, mqSetting.getChannel());
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, mqSetting.getQmgr());
            cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "UtilitaSupport");
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, false);

            connection = cf.createConnection();

            boolean transacted = false;
            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("queue://" + mqSetting.getQmgr() + "/" + que);
            producer = session.createProducer(queue);
            TextMessage outMessage = session.createTextMessage(message);
            producer.send(outMessage);

            dbHandler.setUltimaQueue(mqSetting, que);
            return true;
        } catch (JMSException e) {
            MQLog.write("При отправке сообщения в очередь произошла ошибка: " + e.getMessage());
            return false;
        }

    }

    public ArrayList<String> getQueues(MQSetting mqSetting) {
        ArrayList<String> queuesList;
        try {
            PCFMessageAgent agent = new PCFMessageAgent(mqSetting.getHost(), mqSetting.getPortInt(), mqSetting.getChannel());
            PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_NAMES);
            request.addParameter(CMQC.MQCA_Q_NAME, "*");
            request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
            PCFMessage[] responses = agent.send(request);
            String[] queues = (String[]) responses [0].getParameterValue (CMQCFC.MQCACF_Q_NAMES);

            queuesList = new ArrayList<>(Arrays.asList(queues));

            dbHandler.removeQueues(mqSetting);
            dbHandler.addQueues(mqSetting, queuesList);

        }catch (Exception e){
            MQLog.write("При запросе очередей из ТТП произошла ошибка: " + e.getMessage());
            return new ArrayList<>();
        }

        return queuesList;
    }
}
