package com.main.java.MenuSettings;

import com.main.java.Conf.MQSetting;
import com.main.java.Conf.SQLiteHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class VentanaMQSettings extends JFrame {
    private final SQLiteHandler dbHandler = SQLiteHandler.getInstance();
    private final HashMap<String, MQSetting> mqSettings = dbHandler.getAllMQSettings();

    private DefaultTableModel modeloInfo;
    private JTable table;
    private JTextField textFieldName;
    private JTextField textFieldHost;
    private JTextField textFieldPort;
    private JTextField textFieldChannel;
    private JTextField textFieldQMgr;

    Dimension labelSize = new Dimension(20, 60);
    Font font = new Font("Verdana", Font.PLAIN, 12);



    public JPanel getContentPane(){
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));

        contentPane.setLayout(new BorderLayout());


        modeloInfo = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloInfo.setColumnIdentifiers(new String[]{"Название","Подключение"});
        table = new JTable(modeloInfo);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMinWidth(200);
        table.getColumnModel().getColumn(1).setMaxWidth(Integer.MAX_VALUE);
        JScrollPane scrollTablePane = new JScrollPane(table);
        scrollTablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectRow();
            }
        });

        contentPane.add(scrollTablePane, BorderLayout.WEST);



        JPanel alignmentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(alignmentPanel);
        alignmentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        alignmentPanel.setBorder(BorderFactory.createTitledBorder("Параметры"));


        JLabel lblName = getNewJLabel("Название");
        textFieldName = new JTextField();

        JLabel lblHost = getNewJLabel("IP адрес");
        textFieldHost = new JTextField();

        JLabel lblPort = getNewJLabel("Порт");
        textFieldPort = new JTextField();

        JLabel lblChannel = getNewJLabel("Канал");
        textFieldChannel = new JTextField();

        JLabel lblQMgr = getNewJLabel("Менеджер очередей");
        textFieldQMgr = new JTextField();


        JButton buttonDelete = new JButton("Удалить");
        buttonDelete.addActionListener(e -> deleteMQ());
        JButton buttonGuardarCambios = new JButton("Сохранить изменения");
        buttonGuardarCambios.addActionListener(e -> guardarCambios());
        JButton buttonGuardarNuevo = new JButton("Сохранить новое");
        buttonGuardarNuevo.addActionListener(e -> guardarNuevo());

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(lblName, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblHost, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblPort, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblChannel, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblQMgr, DEFAULT_SIZE,200,PREFERRED_SIZE)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(textFieldName, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldHost, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldPort, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldChannel, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldQMgr, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonDelete)
                                .addComponent(buttonGuardarCambios)
                                .addComponent(buttonGuardarNuevo)
                        )
                )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblName)
                        .addComponent(textFieldName)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblHost)
                        .addComponent(textFieldHost)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblPort)
                        .addComponent(textFieldPort)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblChannel)
                        .addComponent(textFieldChannel)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblQMgr)
                        .addComponent(textFieldQMgr)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(buttonDelete)
                        .addComponent(buttonGuardarCambios)
                        .addComponent(buttonGuardarNuevo)
                )
        );


        contentPane.add(alignmentPanel, BorderLayout.CENTER);

        if (mqSettings.size() > 0){
            rellenarTableMQ();
        }
        return contentPane;
    }

    private JLabel getNewJLabel(String lblText){
        JLabel label = new JLabel(lblText);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setPreferredSize(labelSize);
        label.setFont(font);
        return label;
    }


    private void guardarCambios(){
        deleteMQ();
        guardarNuevo();

    }
    private void deleteMQ(){
        MQSetting selectedMQ = getSelectedMQ();
        dbHandler.deleteMQSetting(selectedMQ.getName());
        mqSettings.remove(selectedMQ.getName());
        rellenarTableMQ();
    }
    private void guardarNuevo(){
        String name = textFieldName.getText();
        String host = textFieldHost.getText();
        String port = textFieldPort.getText();
        String channel = textFieldChannel.getText();
        String qMgr = textFieldQMgr.getText();

        if (name.isEmpty() || host.isEmpty() || port.isEmpty()
                || channel.isEmpty() || qMgr.isEmpty()){
            JOptionPane.showMessageDialog(null, "Заполнены не все данные");
            return;
        }
        if (mqSettings.containsKey(name)){
            JOptionPane.showMessageDialog(null, "Название должно быть уникальным");
            return;
        }
        MQSetting mqSetting = new MQSetting(name, host, port, channel, qMgr, "");

        mqSettings.put(name, mqSetting);
        dbHandler.addNewMQSetting(mqSetting);
        rellenarTableMQ();

    }

    private void rellenarTableMQ(){
        while (modeloInfo.getRowCount() > 0){
            modeloInfo.removeRow(0);
        }
        for (MQSetting mqSetting : mqSettings.values()) {
            String s = mqSetting.getChannel() + "/"
                    + mqSetting.getHost() + "("
                    + mqSetting.getPortString() + ")";
            modeloInfo.addRow(new String[]{mqSetting.getName(), s});
        }
        if (modeloInfo.getRowCount() > 0){
            table.setRowSelectionInterval(0, 0);
            selectRow();
        }else {
            textFieldName.setText("");
            textFieldHost.setText("");
            textFieldPort.setText("");
            textFieldChannel.setText("");
            textFieldQMgr.setText("");
        }
    }

    private void selectRow(){
        MQSetting selectedMQ = getSelectedMQ();
        textFieldName.setText(selectedMQ.getName());
        textFieldHost.setText(selectedMQ.getHost());
        textFieldPort.setText(selectedMQ.getPortString());
        textFieldChannel.setText(selectedMQ.getChannel());
        textFieldQMgr.setText(selectedMQ.getQmgr());
    }

    private MQSetting getSelectedMQ(){
        String selectedMQ = (String) modeloInfo.getValueAt(table.getSelectedRow(), 0);
        return mqSettings.get(selectedMQ);
    }

}
