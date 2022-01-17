package com.main.java.MenuSettings;

import com.main.java.Conf.DBSetting;
import com.main.java.Conf.MQSetting;
import com.main.java.Conf.SQLiteHandler;
import org.sqlite.core.DB;

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

public class VentanaDBSettings extends JFrame {
    private final SQLiteHandler dbHandler = SQLiteHandler.getInstance();
    private final HashMap<String, DBSetting> dbSettings = dbHandler.getAllDBSettings();

    Dimension labelSize = new Dimension(20, 60);
    Font font = new Font("Verdana", Font.PLAIN, 12);
    private DefaultTableModel modeloInfo;
    private JTable table;

    JComboBox comboBoxTypeDB;
    DefaultComboBoxModel comboBoxTypeBDModel;
    private JTextField textFieldName;
    private JTextField textFieldHost;
    private JTextField textFieldPort;
    private JTextField textFieldService;
    private JTextField textFieldLogin;
    private JTextField textFieldPassword;


    public JPanel getContentPane() {
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        contentPane.setLayout(new BorderLayout());


        modeloInfo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloInfo.setColumnIdentifiers(new String[]{"Название", "Подключение"});
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


        JLabel lblType = getNewJLabel("Тип КПС");
        comboBoxTypeBDModel = new DefaultComboBoxModel(new String[]{"APSPP", "UPI"});
        comboBoxTypeDB = new JComboBox(comboBoxTypeBDModel);

        JLabel lblName = getNewJLabel("Название");
        textFieldName = new JTextField();

        JLabel lblHost = getNewJLabel("IP адрес");
        textFieldHost = new JTextField();

        JLabel lblPort = getNewJLabel("Порт");
        textFieldPort = new JTextField();

        JLabel lblService = getNewJLabel("Сервис");
        textFieldService = new JTextField();

        JLabel lblLogin = getNewJLabel("Логин");
        textFieldLogin = new JTextField();

        JLabel lblPassword = getNewJLabel("Пароль");
        textFieldPassword = new JTextField();

        JButton buttonDelete = new JButton("Удалить");
        buttonDelete.addActionListener(e -> deleteDB());
        JButton buttonGuardarCambios = new JButton("Сохранить изменения");
        buttonGuardarCambios.addActionListener(e -> guardarCambios());
        JButton buttonGuardarNuevo = new JButton("Сохранить новое");
        buttonGuardarNuevo.addActionListener(e -> guardarNuevoDB());

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(lblName, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblType, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblHost, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblPort, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblService, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblLogin, DEFAULT_SIZE,200,PREFERRED_SIZE)
                        .addComponent(lblPassword, DEFAULT_SIZE,200,PREFERRED_SIZE)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(textFieldName, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(comboBoxTypeDB, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldHost, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldPort, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldService, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldLogin, DEFAULT_SIZE,400,PREFERRED_SIZE)
                        .addComponent(textFieldPassword, DEFAULT_SIZE,400,PREFERRED_SIZE)
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
                        .addComponent(lblType)
                        .addComponent(comboBoxTypeDB)
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
                        .addComponent(lblService)
                        .addComponent(textFieldService)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblLogin)
                        .addComponent(textFieldLogin)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(lblPassword)
                        .addComponent(textFieldPassword)
                )
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(buttonDelete)
                        .addComponent(buttonGuardarCambios)
                        .addComponent(buttonGuardarNuevo)
                )
        );

        contentPane.add(alignmentPanel, BorderLayout.CENTER);


        if (dbSettings.size() > 0){
            rellenarTableDB();
        }
        return contentPane;
    }

    private void guardarCambios() {
        DBSetting selectedDB = getSelectedDB();
        dbHandler.deleteDBSetting(selectedDB.getName());
        dbSettings.remove(selectedDB.getName());
        guardarNuevoDB();
    }

    private void deleteDB() {
        DBSetting selectedDB = getSelectedDB();
        dbHandler.deleteDBSetting(selectedDB.getName());
        dbSettings.remove(selectedDB.getName());
        rellenarTableDB();
    }

    private void guardarNuevoDB() {
        String name = textFieldName.getText();
        String type = (String) comboBoxTypeDB.getSelectedItem();
        String host = textFieldHost.getText();
        String port = textFieldPort.getText();
        String service = textFieldService.getText();
        String login = textFieldLogin.getText();
        String password = textFieldPassword.getText();

        if (name.isEmpty() || host.isEmpty() || port.isEmpty()
                || service.isEmpty() || login.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(null, "Заполнены не все данные");
            return;
        }
        if (dbSettings.containsKey(name)){
            JOptionPane.showMessageDialog(null, "Название должно быть уникальным");
            return;
        }
        DBSetting dbSetting = new DBSetting(name, type, host, port, service, login, password);

        dbSettings.put(name, dbSetting);
        dbHandler.addNewDBSetting(dbSetting);
        rellenarTableDB();
    }

    private void rellenarTableDB() {
        while (modeloInfo.getRowCount() > 0){
            modeloInfo.removeRow(0);
        }
        for (DBSetting dbSetting : dbSettings.values()) {
            String s = dbSetting.getHost() + "@"
                    + dbSetting.getPortString() + ":"
                    + dbSetting.getService();
            modeloInfo.addRow(new String[]{dbSetting.getName(), s});
        }
        if (modeloInfo.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            selectRow();
        }
    }

    private DBSetting getSelectedDB() {
        String selectedDB = (String) modeloInfo.getValueAt(table.getSelectedRow(), 0);
        return dbSettings.get(selectedDB);
    }

    private void selectRow() {
        DBSetting selectedDB = getSelectedDB();
        textFieldName.setText(selectedDB.getName());
        comboBoxTypeDB.setSelectedItem(selectedDB.getType());
        textFieldHost.setText(selectedDB.getHost());
        textFieldPort.setText(selectedDB.getPortString());
        textFieldService.setText(selectedDB.getService());
        textFieldLogin.setText(selectedDB.getLogin());
        textFieldPassword.setText(selectedDB.getPassword());
    }


    private JLabel getNewJLabel(String lblText){
        JLabel label = new JLabel(lblText);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setPreferredSize(labelSize);
        label.setFont(font);
        return label;
    }

}
