package com.main.java.Ventanas;

import com.main.java.Conf.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static javax.swing.GroupLayout.Alignment.BASELINE;

public class VentanaMQSender extends JFrame {
    private final SQLiteHandler dbHandler = SQLiteHandler.getInstance();
    private OracleHandler oracleHandler = null;
    private final MQHandler mqHandler = MQHandler.getInstance();
    private final HashMap<String, DBSetting> dbSettings = dbHandler.getAllDBSettings();
    private final HashMap<String, Message> messages = dbHandler.getAllMessages();
    private MQSetting mqSetting;

    private JTextArea textAreaMes;
    public JTextArea textAreaLog;
    private JTextField textFieldEnv;
    Dimension preferredSize = new Dimension(310, 50);
    Font font = new Font("Verdana", Font.PLAIN, 12);
    JComboBox comboBoxMQ;
    DefaultComboBoxModel comboBoxQueueModel;
    JComboBox comboBoxQueue;
    private DefaultComboBoxModel comboBoxDBModel;
    private JComboBox comboBoxDB;


    public JPanel getContentPane(){
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));

        contentPane.setLayout(new BorderLayout());

        textAreaMes = new JTextArea();
        textAreaMes.setLineWrap(true);
        textAreaMes.setWrapStyleWord(true);
        textAreaMes.setText("");
        JScrollPane scrollTextAreaMesPane = new JScrollPane(textAreaMes);
        scrollTextAreaMesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTextAreaMesPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollTextAreaMesPane.setBorder(BorderFactory.createTitledBorder("Сообщение"));

        contentPane.add(scrollTextAreaMesPane, BorderLayout.CENTER);

        textAreaLog = MQLog.getInstance();
        textAreaLog.setLineWrap(true);
        textAreaLog.setWrapStyleWord(true);
        textAreaLog.setText("");
        JScrollPane scrollTextAreaLogPane = new JScrollPane(textAreaLog);
        scrollTextAreaLogPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTextAreaLogPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollTextAreaLogPane.setPreferredSize(new Dimension(500, 150));
        scrollTextAreaLogPane.setBorder(BorderFactory.createTitledBorder("Логирование"));

        contentPane.add(scrollTextAreaLogPane, BorderLayout.SOUTH);


        JPanel settingPanel = new JPanel();
        GroupLayout layout = new GroupLayout(settingPanel);
        settingPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        settingPanel.setBorder(BorderFactory.createTitledBorder("Параметры"));

        contentPane.add(settingPanel, BorderLayout.WEST);

        JLabel lblTTP = getNewJLabel("Выбрать подключение ТТП");

        comboBoxMQ = new JComboBox(dbHandler.getMQNames());
        comboBoxMQ.setMinimumSize(preferredSize);
        comboBoxMQ.setPreferredSize(preferredSize);
        comboBoxMQ.setMaximumSize(preferredSize);
        comboBoxMQ.addActionListener(e -> comboBoxMQAction());

        JLabel lblQueue = getNewJLabel("Выбрать очередь");

        comboBoxQueueModel = new DefaultComboBoxModel();
        comboBoxQueue = new JComboBox(comboBoxQueueModel);
        comboBoxQueue.setMinimumSize(preferredSize);
        comboBoxQueue.setPreferredSize(preferredSize);
        comboBoxQueue.setMaximumSize(preferredSize);
        comboBoxMQAction();


        JLabel lblDB = getNewJLabel("Выбрать БД (поиск запроса)");
        String[] databases = dbSettings.keySet().toArray(new String[dbSettings.size()]);
        comboBoxDBModel = new DefaultComboBoxModel(databases);
        comboBoxDB = new JComboBox(comboBoxDBModel);
        comboBoxDB.setMinimumSize(preferredSize);
        comboBoxDB.setPreferredSize(preferredSize);
        comboBoxDB.setMaximumSize(preferredSize);


        JLabel lblEnv = getNewJLabel("EnvelopeID запроса");
        textFieldEnv = new JTextField();
        textFieldEnv.setMinimumSize(preferredSize);
        textFieldEnv.setPreferredSize(preferredSize);
        textFieldEnv.setMaximumSize(preferredSize);
        textFieldEnv.setToolTipText("При заполнении будут использованы значения справочника сообщений для замены идентификаторов");

        JButton btnSendMessage = new JButton("Отправить сообщение");
        btnSendMessage.addActionListener(e -> btnSendMessageAction());
        JButton btnQueuesRequest = new JButton("Запросить очереди заново");
        btnQueuesRequest.addActionListener(e -> btnQueuesRequestAction());


        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(lblTTP)
                        .addComponent(comboBoxMQ)
                        .addComponent(lblQueue)
                        .addComponent(comboBoxQueue)
                        .addComponent(lblDB)
                        .addComponent(comboBoxDB)
                        .addComponent(lblEnv)
                        .addComponent(textFieldEnv)
                        .addComponent(btnSendMessage)
                        .addComponent(btnQueuesRequest)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTTP)
                                .addComponent(comboBoxMQ)
                                .addComponent(lblQueue)
                                .addComponent(comboBoxQueue)
                                .addComponent(lblDB)
                                .addComponent(comboBoxDB)
                                .addComponent(lblEnv)
                                .addComponent(textFieldEnv)
                                .addComponent(btnSendMessage)
                                .addComponent(btnQueuesRequest)
                        )
                )
        );




        return contentPane;
    }

    private JLabel getNewJLabel(String labelText){
        JLabel label = new JLabel(labelText);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setPreferredSize(preferredSize);
        label.setFont(font);
        return label;
    }



    private void btnSendMessageAction() {
        String strMessage = textAreaMes.getText();
        if (mqSetting == null) {
            mqSetting = dbHandler.getMQ((String) comboBoxMQ.getSelectedItem());
        }
        String queue = (String) comboBoxQueue.getSelectedItem();
        if (mqSetting == null || queue == null || queue.isEmpty()){
            MQLog.write("Ошибка при получении настроек ТТП");
            return;
        }
        DBSetting dbSetting = dbSettings.get((String) comboBoxDB.getSelectedItem());
        String envelope = textFieldEnv.getText().trim();
        int indexStartMes = strMessage.indexOf("MessageKind>")+12;
        int indexEndMes = strMessage.indexOf("<", indexStartMes+1);
        String messageKind = strMessage.substring(indexStartMes, indexEndMes);
        MQLog.write("Подготавливаем к отправке сообщение " + messageKind);

        if (dbSetting == null && envelope.length() != 36 && !messages.containsKey(messageKind)){
            MQLog.write("Отсутствует настройка БД & некорректен EnvelopeID запроса & "+messageKind+" не найден в справочнике. Сообщение будет отправлено ,без обработки");
            sendMessage(mqSetting, queue, strMessage);
            return;
        }else if((dbSetting == null || envelope.length() != 36) && messages.containsKey(messageKind)){
            MQLog.write("Отсутствует настройка БД или некорректен EnvelopeID. "+messageKind+" найден в справочнике. Будут сгенерированы новые идентификатороры перед отправкой");
            Document xmlDocResponde = XmlUtil.getDocumentFromString(strMessage);
            strMessage = generarGUIDs(xmlDocResponde, messages.get(messageKind));
            sendMessage(mqSetting, queue, strMessage);
            return;
        }else if (dbSetting != null && envelope.length() == 36 && messages.containsKey(messageKind)){
            MQLog.write("Выбрана БД: " + dbSetting.getName()+ ". EnvelopeID соответствует формату. "
                    + messageKind + " найден в справочнике. Будут сгенерированы и заменены идентификатороры перед отправкой");
            replaseYgenerarGUIDs(strMessage, queue, dbSetting, envelope, messageKind);
        }else if (dbSetting != null && envelope.length() == 36 && !messages.containsKey(messageKind)) {
            MQLog.write("Выбрана БД: " + dbSetting.getName() + ". EnvelopeID соответствует формату. "
                    + messageKind + " Не найден в справочнике.");
            MQLog.write(messageKind + " Будет добавлен в справочник с настройками по умолчанию и будет выполнена попытка подкидывания сообщения с настройками по умолчанию.");
            Message message = new Message();
            message.setMessageType(messageKind);
            message.getGenerarGUID().add("Envelope/Header/RoutingInf/EnvelopeID");
            message.getReplaceGUID().add(new String[]{"Envelope/Header/RoutingInf/EnvelopeID", "Envelope/Header/RoutingInf/InitialEnvelopeID"});
            dbHandler.addNewMessage(message);
            messages.put(messageKind, message);

            replaseYgenerarGUIDs(strMessage, queue, dbSetting, envelope, messageKind);
        }


    }

    private void replaseYgenerarGUIDs(String strMessage, String queue, DBSetting dbSetting, String envelope, String messageKind) {
        Document xmlDocResponde = XmlUtil.getDocumentFromString(strMessage);
        oracleHandler = OracleHandler.getInstance(dbSetting);
        String xmlStrRequest = oracleHandler.getRequestDocumentFromDB(envelope);
        if (xmlStrRequest != null && !xmlStrRequest.isEmpty()){
            MQLog.write("xmlDocRequest получен из БД");
            Document xmlDocRequest = XmlUtil.getDocumentFromString(xmlStrRequest);
            generarGUIDs(xmlDocResponde, messages.get(messageKind));
            for (String[] replase : messages.get(messageKind).getReplaceGUID()) {
                Node nodeFrom = XmlUtil.getNode(xmlDocRequest, replase[0]);
                Node nodeTo = XmlUtil.getNode(xmlDocResponde, replase[1]);
                nodeTo.setTextContent(nodeFrom.getTextContent());
            }
            strMessage = XmlUtil.getStringFromDocument(xmlDocResponde);
        }else {
            MQLog.write("xmlDocRequest не найден в БД. Будут сгенерированы новые идентификатороры(без замены) перед отправкой");
            strMessage = generarGUIDs(xmlDocResponde, messages.get(messageKind));
        }
        sendMessage(mqSetting, queue, strMessage);
    }

    private String generarGUIDs(Document xmlDocResponde, Message message) {
        for (String generarPath : message.getGenerarGUID()) {
            Node node = XmlUtil.getNode(xmlDocResponde, generarPath);
            node.setTextContent(XmlUtil.getRandomGUID());
        }
        return XmlUtil.getStringFromDocument(xmlDocResponde);
    }

    private void sendMessage(MQSetting mqSetting, String queue, String message){
        boolean result = mqHandler.sendMessage(this.mqSetting, queue, message);
        if (result){
            MQLog.write("Сообщение успешно отправлено.");
            this.mqSetting.setUltimaQueue(queue);
            dbHandler.cambuarMQSetting(this.mqSetting.getName(), this.mqSetting);
        }else {
            MQLog.write("При отправке сообщения возникла ошибка.");
        }
    }

    private void comboBoxMQAction() {
        MQLog.write("Выбрано подключение MQ: " + comboBoxMQ.getSelectedItem());
        mqSetting = dbHandler.getMQ((String) comboBoxMQ.getSelectedItem());
        if (mqSetting == null) {
            return;
        }
        MQLog.write("Начали запрос очередей из БД");
        ArrayList<String> queuesList = dbHandler.getQueues(mqSetting);
        MQLog.write("Из БД запрошено " + queuesList.size() + " очередей");
        if (queuesList.size() < 1){
            btnQueuesRequestAction();
        }

        rellenarQueues(queuesList);
    }

    private void btnQueuesRequestAction() {
        ArrayList<String> queuesList;
        if (mqSetting == null) {
            mqSetting = dbHandler.getMQ((String) comboBoxMQ.getSelectedItem());
        }
        MQLog.write("Начали запрос очередей из ТТП");
        queuesList = mqHandler.getQueues(mqSetting);
        MQLog.write("Из ТТП запрошено " + queuesList.size() + " очередей");

        rellenarQueues(queuesList);

    }

    private void rellenarQueues(ArrayList<String> queuesList){
        comboBoxQueueModel.removeAllElements();
        for (String queue : queuesList) {
            comboBoxQueueModel.addElement(queue);
        }
        if (mqSetting.getUltimaQueue() != null && !mqSetting.getUltimaQueue().isEmpty()){
            comboBoxQueue.setSelectedItem(mqSetting.getUltimaQueue());
        }
    }
}
