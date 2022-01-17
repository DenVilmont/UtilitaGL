package com.main.java.MenuSettings;

import com.main.java.Conf.Message;
import com.main.java.Conf.SQLiteHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class VentanaMesSettings extends JFrame {
    private final SQLiteHandler dbHandler = SQLiteHandler.getInstance();
    private final HashMap<String, Message> messages = dbHandler.getAllMessages();

    private DefaultTableModel modeloInfoMes;
    private JTable tableMes;
    private DefaultTableModel modeloInfoEnv;
    private JTable tableEnv;
    private DefaultTableModel modeloInfoRef;
    private JTable tableRef;
    private JTextField textFieldMessage;
    private JTextField textFieldEnv;
    private JTextField textFieldRefTo;
    private JTextField textFieldRefFrom;
    Dimension labelSize = new Dimension(20, 60);
    Font font = new Font("Verdana", Font.PLAIN, 12);


    public JPanel getContentPane(){
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);


        JLabel lblTableMes = getNewJLabel("Message Type ответа.");

        modeloInfoMes = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloInfoMes.setColumnIdentifiers(new String[]{"Message Type"});
        tableMes = new JTable(modeloInfoMes);
        tableMes.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableMes.getColumnModel().getColumn(0).setMinWidth(300);
        tableMes.getColumnModel().getColumn(0).setMaxWidth(Integer.MAX_VALUE);
        JScrollPane scrollTablePaneMes = new JScrollPane(tableMes);
        scrollTablePaneMes.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTablePaneMes.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableMes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectRowMes();
            }
        });
        JLabel lblFieldMessage = new JLabel("");
        textFieldMessage = new JTextField();
        JButton buttonDeleteMessage = new JButton("Удалить");
        buttonDeleteMessage.addActionListener(e -> deleteMessage());
        JButton buttonCambiarMessage = new JButton("Изменить");
        buttonCambiarMessage.addActionListener(e -> cambiarMessage());
        JButton buttonAddNewMessage = new JButton("Добавить");
        buttonAddNewMessage.addActionListener(e -> addNewMessage());



        JLabel lblTableEnv1 = new JLabel("Список идентификаторов для генерации");
        lblTableEnv1.setVerticalAlignment(JLabel.BOTTOM);
        lblTableEnv1.setHorizontalAlignment(JLabel.LEFT);
        lblTableEnv1.setPreferredSize(labelSize);
        lblTableEnv1.setFont(font);

        JLabel lblTableEnv2 = getNewJLabel("(В ответном сообщении. Формат: Envelope/Header/RoutingInf/EnvelopeID)");

        modeloInfoEnv = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloInfoEnv.setColumnIdentifiers(new String[]{"Идентификатор"});
        tableEnv = new JTable(modeloInfoEnv);
        tableEnv.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableEnv.getColumnModel().getColumn(0).setMinWidth(600);
        tableEnv.getColumnModel().getColumn(0).setMaxWidth(Integer.MAX_VALUE);
        JScrollPane scrollTablePaneEnv = new JScrollPane(tableEnv);
        scrollTablePaneEnv.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTablePaneEnv.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableEnv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectRowEnv();
            }
        });



        JLabel lblFieldEnv = new JLabel("GUID");
        textFieldEnv = new JTextField();
        JButton buttonDeleteEnv = new JButton("Удалить");
        buttonDeleteEnv.addActionListener(e -> deleteEnv());
        JButton buttonCambiarEnv = new JButton("Изменить");
        buttonCambiarEnv.addActionListener(e -> cambiarEnv());
        JButton buttonAddNewEnv = new JButton("Добавить");
        buttonAddNewEnv.addActionListener(e -> addNewEnv());



        JLabel lblTableRef1 = new JLabel("Список идентификаторов для замены из запроса в ответ");
        lblTableRef1.setVerticalAlignment(JLabel.BOTTOM);
        lblTableRef1.setHorizontalAlignment(JLabel.LEFT);
        lblTableRef1.setPreferredSize(labelSize);
        lblTableRef1.setFont(font);

        JLabel lblTableRef2 = getNewJLabel("(From - поле в запросе; To - поле в ответе; * - первый, любой документ на данном уровне.)");

        modeloInfoRef = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloInfoRef.setColumnIdentifiers(new String[]{"From","To"});
        tableRef = new JTable(modeloInfoRef);
        tableRef.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableRef.getColumnModel().getColumn(0).setMinWidth(300);
        tableRef.getColumnModel().getColumn(1).setMinWidth(300);
        tableRef.getColumnModel().getColumn(1).setMaxWidth(Integer.MAX_VALUE);
        JScrollPane scrollTablePaneRef = new JScrollPane(tableRef);
        scrollTablePaneRef.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTablePaneRef.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableRef.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectRowRef();
            }
        });


        JLabel lblFieldRefFrom = new JLabel("From");
        lblFieldRefFrom.setVerticalAlignment(JLabel.BOTTOM);
        lblFieldRefFrom.setHorizontalAlignment(JLabel.LEFT);
        lblFieldRefFrom.setPreferredSize(labelSize);
        lblFieldRefFrom.setFont(font);
        textFieldRefFrom = new JTextField();

        JLabel lblFieldRefTo = new JLabel("To");
        lblFieldRefTo.setVerticalAlignment(JLabel.BOTTOM);
        lblFieldRefTo.setHorizontalAlignment(JLabel.LEFT);
        lblFieldRefTo.setPreferredSize(labelSize);
        lblFieldRefTo.setFont(font);
        textFieldRefTo = new JTextField();

        JButton buttonDeleteRef = new JButton("Удалить");
        buttonDeleteRef.addActionListener(e -> deleteRef());
        JButton buttonCambiarRef = new JButton("Изменить");
        buttonCambiarRef.addActionListener(e -> cambiarRef());
        JButton buttonAddNewRef = new JButton("Добавить");
        buttonAddNewRef.addActionListener(e -> addNewRef());


        /*Разметка*/
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(lblTableMes, DEFAULT_SIZE,300,PREFERRED_SIZE)
                        .addComponent(scrollTablePaneMes, 300,300,300)
                        .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblFieldMessage,0,0,0)
                                        .addComponent(textFieldMessage, 288,288,288)
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(buttonDeleteMessage)
                                        .addComponent(buttonCambiarMessage)
                                        .addComponent(buttonAddNewMessage)
                                )
                        )
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(lblTableEnv1, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblTableEnv2, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(scrollTablePaneEnv, 600,600,600)
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblFieldEnv,50,50,50)
                                                .addComponent(textFieldEnv, 300,300,300)
                                        )
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(buttonDeleteEnv)
                                                .addComponent(buttonCambiarEnv)
                                                .addComponent(buttonAddNewEnv)
                                        )
                                )
                        )
                        .addComponent(lblTableRef1, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblTableRef2, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(scrollTablePaneRef, 600,600,600)
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblFieldRefFrom,50,50,50)
                                                .addComponent(textFieldRefFrom, 300,300,300)
                                        )
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblFieldRefTo,50,50,50)
                                                .addComponent(textFieldRefTo, 300,300,300)
                                        )
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(buttonDeleteRef)
                                                .addComponent(buttonCambiarRef)
                                                .addComponent(buttonAddNewRef)
                                        )
                                )
                        )
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTableMes)
                                .addComponent(scrollTablePaneMes)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(BASELINE)
                                                .addComponent(lblFieldMessage)
                                                .addComponent(textFieldMessage)
                                        )
                                        .addGroup(layout.createParallelGroup(TRAILING)
                                                .addComponent(buttonDeleteMessage)
                                                .addComponent(buttonCambiarMessage)
                                                .addComponent(buttonAddNewMessage)
                                        )
                                )
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblTableEnv1)
                                .addComponent(lblTableEnv2)
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(scrollTablePaneEnv)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(BASELINE)
                                                        .addComponent(lblFieldEnv)
                                                        .addComponent(textFieldEnv)
                                                )
                                                .addGroup(layout.createParallelGroup(BASELINE)
                                                        .addComponent(buttonDeleteEnv)
                                                        .addComponent(buttonCambiarEnv)
                                                        .addComponent(buttonAddNewEnv)
                                                )
                                        )
                                )
                                .addComponent(lblTableRef1)
                                .addComponent(lblTableRef2)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(scrollTablePaneRef)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(BASELINE)
                                                        .addComponent(lblFieldRefFrom)
                                                        .addComponent(textFieldRefFrom)
                                                )
                                                .addGroup(layout.createParallelGroup(BASELINE)
                                                        .addComponent(lblFieldRefTo)
                                                        .addComponent(textFieldRefTo)
                                                )
                                                .addGroup(layout.createParallelGroup(BASELINE)
                                                        .addComponent(buttonDeleteRef)
                                                        .addComponent(buttonCambiarRef)
                                                        .addComponent(buttonAddNewRef)
                                                )
                                        )
                                )
                        )
                )
        );





        if (messages.size() > 0){
            rellenarTableMes();
        }
        return contentPane;
    }

    private Message getSelectedMessage(){
        String selectedMes = (String) modeloInfoMes.getValueAt(tableMes.getSelectedRow(), 0);
        return messages.get(selectedMes);
    }


    private JLabel getNewJLabel(String labelText){
        JLabel label = new JLabel(labelText);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setPreferredSize(labelSize);
        label.setFont(font);
        return label;
    }


    private void rellenarTableMes(){
        while (modeloInfoMes.getRowCount() > 0){
            modeloInfoMes.removeRow(0);
        }
        for (String str : messages.keySet()) {
            modeloInfoMes.addRow(new String[]{str});
        }
        if (modeloInfoMes.getRowCount() > 0){
            tableMes.setRowSelectionInterval(0, 0);
            selectRowMes();
        }else {
            textFieldMessage.setText("");
            while (modeloInfoEnv.getRowCount() > 0){
                modeloInfoEnv.removeRow(0);
            }
            while(modeloInfoRef.getRowCount() > 0){
                modeloInfoRef.removeRow(0);
            }
        }
    }
    private void selectRowMes() {
        Message message = getSelectedMessage();
        textFieldMessage.setText(message.getMessageType());
        rellenarTableEnv(message.getMessageType());
        rellenarTableRef(message.getMessageType());
    }
    private void deleteMessage() {
        Message message = getSelectedMessage();
        dbHandler.deleteMessage(message.getMessageType());
        messages.remove(message.getMessageType());
        rellenarTableMes();
    }
    private void cambiarMessage() {
        String newMessageType = textFieldMessage.getText();
        if (newMessageType.isEmpty()){
            JOptionPane.showMessageDialog(null, "MessageType не может быть пустым");
            return;
        }
        Message message = getSelectedMessage();
        dbHandler.cambiarMessage(message.getMessageType(), newMessageType);
        messages.remove(message.getMessageType());
        message.setMessageType(newMessageType);
        messages.put(newMessageType, message);
        rellenarTableMes();
    }
    private void addNewMessage() {
        String newMessageType = textFieldMessage.getText();
        if (newMessageType.isEmpty() || messages.containsKey(newMessageType)){
            JOptionPane.showMessageDialog(null, "MessageType не может повторяться или быть пустым");
            return;
        }

        Message message = new Message();
        message.setMessageType(newMessageType);
        message.getGenerarGUID().add("Envelope/Header/RoutingInf/EnvelopeID");
        message.getReplaceGUID().add(new String[]{"Envelope/Header/RoutingInf/EnvelopeID", "Envelope/Header/RoutingInf/InitialEnvelopeID"});
        dbHandler.addNewMessage(message);
        messages.put(newMessageType, message);
        rellenarTableMes();
    }



    private void rellenarTableEnv(String selectedMes){
        Message message = messages.get(selectedMes);
        ArrayList<String> generarGUIDs = message.getGenerarGUID();
        while (modeloInfoEnv.getRowCount() > 0){
            modeloInfoEnv.removeRow(0);
        }
        for (String generarGUID : generarGUIDs) {
                modeloInfoEnv.addRow(new String[]{generarGUID});

        }
        if (modeloInfoEnv.getRowCount() > 0){
            tableEnv.setRowSelectionInterval(0, 0);
            selectRowEnv();
        }else {
            textFieldEnv.setText("");
        }
    }
    private void rellenarTableRef(String selectedMes){
        Message message = messages.get(selectedMes);
        ArrayList<String[]> replaceGUIDs = message.getReplaceGUID();
        while(modeloInfoRef.getRowCount() > 0){
            modeloInfoRef.removeRow(0);
        }
        for (String[] replaceGUID : replaceGUIDs) {
                modeloInfoRef.addRow(replaceGUID);
        }
        if (modeloInfoRef.getRowCount() > 0){
            tableRef.setRowSelectionInterval(0, 0);
            selectRowRef();
        }else {
            textFieldRefFrom.setText("");
            textFieldRefTo.setText("");
        }
    }




    private void selectRowEnv() {
        Message message = getSelectedMessage();
        String guid = message.getGenerarGUID().get(tableEnv.getSelectedRow());
        textFieldEnv.setText(guid);
    }
    private void addNewEnv() {
        Message message = getSelectedMessage();
        String guid = textFieldEnv.getText();
        dbHandler.addNewGenerarGuid(message.getMessageType(), guid);
        message.getGenerarGUID().add(guid);
        rellenarTableEnv(message.getMessageType());
        textFieldEnv.setText("");
    }
    private void deleteEnv() {
        Message message = getSelectedMessage();
        String guid = message.getGenerarGUID().get(tableEnv.getSelectedRow());
        dbHandler.deleteGenerarGUID(message.getMessageType(), guid);
        message.getGenerarGUID().remove(tableEnv.getSelectedRow());
        rellenarTableEnv(message.getMessageType());
    }
    private void cambiarEnv() {
        Message message = getSelectedMessage();
        String oldGuid = message.getGenerarGUID().get(tableEnv.getSelectedRow());
        String newGuid = textFieldEnv.getText();
        dbHandler.cambiarGenerarGUID(message.getMessageType(), oldGuid, newGuid);
        message.getGenerarGUID().remove(oldGuid);
        message.getGenerarGUID().add(newGuid);
        rellenarTableEnv(message.getMessageType());
    }



    private void selectRowRef() {
        Message message = getSelectedMessage();
        String guidFrom = message.getReplaceGUID().get(tableRef.getSelectedRow())[0];
        String guidTo = message.getReplaceGUID().get(tableRef.getSelectedRow())[1];
        textFieldRefFrom.setText(guidFrom);
        textFieldRefTo.setText(guidTo);
    }
    private void deleteRef() {
        Message message = getSelectedMessage();
        String guidFrom = message.getReplaceGUID().get(tableRef.getSelectedRow())[0];
        String guidTo = message.getReplaceGUID().get(tableRef.getSelectedRow())[1];
        dbHandler.deleteReplaceGUID(message.getMessageType(), guidFrom, guidTo);
        message.getReplaceGUID().remove(tableRef.getSelectedRow());
        rellenarTableRef(message.getMessageType());
    }
    private void cambiarRef() {
        Message message = getSelectedMessage();
        String oldGuidFrom = message.getReplaceGUID().get(tableRef.getSelectedRow())[0];
        String oldGuidTo = message.getReplaceGUID().get(tableRef.getSelectedRow())[1];
        String newGuidFrom = textFieldRefFrom.getText();
        String newGuidTo = textFieldRefTo.getText();
        dbHandler.cambiarReplaceGUID(message.getMessageType(), oldGuidFrom, oldGuidTo, newGuidFrom, newGuidTo);
        message.getReplaceGUID().remove(tableRef.getSelectedRow());
        message.getReplaceGUID().add(new String[]{newGuidFrom, newGuidTo});
        rellenarTableRef(message.getMessageType());
    }
    private void addNewRef() {
        Message message = getSelectedMessage();
        String guidFrom = textFieldRefFrom.getText();
        String guidTo = textFieldRefTo.getText();
        dbHandler.addNewReplaceGuid(message.getMessageType(), guidFrom, guidTo);
        message.getReplaceGUID().add(new String[]{guidFrom, guidTo});
        textFieldRefFrom.setText("");
        textFieldRefTo.setText("");
        rellenarTableRef(message.getMessageType());

    }



}
