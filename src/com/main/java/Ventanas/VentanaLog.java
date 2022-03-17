package com.main.java.Ventanas;

import com.main.java.Conf.XmlUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Pattern;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class VentanaLog {

    private final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    private ArrayList<String[]> datosEncontrados = new ArrayList<>();
    private ArrayList<String[]> logData = new ArrayList<>();
    /*logData - лист строк логов. Каждая строка хранится в массиве, где:
     * String[0] - дата и время для первой колонки таблицы.
     * String[1] - остаток строки после даты и времени до следующей даты и времени (многострочные ошибки - это одна строка.)
     * String[2] - отформатированное представление XML сообщения, если он есть в строке. если в строке нет сообщения то = ""
     */

    private JPanel contentPane;

    private JTextField textFieldFichero;

    private boolean flagOpenInNotepad = false;

    private JTextField textFieldBuscar;

    private JTextArea textArea;

    private DefaultTableModel modeloInfo;
    private JTable table;

    /**
     * Launch the application.
     */


    public JPanel getContentPane(){
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        /*Поле для файла*/
        textFieldFichero = new JTextField();

        /*кнопка выбора файла*/
        JButton buttonSeleccionar = new JButton("Открыть");
        buttonSeleccionar.addActionListener(e -> btnSeleccionarAction());

        /*Поле для поиска*/
        textFieldBuscar = new JTextField();
        textFieldBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    btnBuscarAction();
                }
            }
        });

        JButton buttonBuscar = new JButton("Искать");
        buttonBuscar.addActionListener(e -> btnBuscarAction());

        JButton buttonClear = new JButton("Сброс");
        buttonClear.addActionListener(e -> rellenarTodosDatos());



        /*Подпись "Поиск"*/
        JLabel labelBuscar = new JLabel("Поиск:");
        labelBuscar.setFont(new Font("Areal", Font.PLAIN, 16));
        labelBuscar.setHorizontalAlignment(SwingConstants.LEFT);

        JButton buttonOpenInNotepad = new JButton("Notepad++");
        buttonOpenInNotepad.addActionListener(e -> openInNotepad());

        /*Таблица со строками логов*/
        modeloInfo = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloInfo.setColumnIdentifiers(new String[]{"Дата", "Запись"});
        table = new JTable(modeloInfo);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMinWidth(170);
        table.getColumnModel().getColumn(1).setMinWidth(dimension.width/3-table.getColumnModel().getColumn(0).getMinWidth());
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
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ){
                    selectRow();
                }
            }
        });


        /*Текстовое поле для декодированного сообщения*/
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText("");
        JScrollPane scrollTextAreaPane = new JScrollPane(textArea);
        scrollTextAreaPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollTextAreaPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        /*Разметка*/
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldFichero, DEFAULT_SIZE,300,PREFERRED_SIZE)
                                .addComponent(buttonSeleccionar)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(labelBuscar)
                                .addComponent(textFieldBuscar, DEFAULT_SIZE,330,PREFERRED_SIZE)
                                .addComponent(buttonBuscar)
                                .addComponent(buttonClear)
                                .addComponent(buttonOpenInNotepad)
                        )
                        .addComponent(scrollTablePane,DEFAULT_SIZE,dimension.width/3,PREFERRED_SIZE)
                )
                .addComponent(scrollTextAreaPane)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(textFieldFichero)
                                        .addComponent(buttonSeleccionar)
                                )
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(labelBuscar)
                                        .addComponent(textFieldBuscar)
                                        .addComponent(buttonBuscar)
                                        .addComponent(buttonClear)
                                        .addComponent(buttonOpenInNotepad)
                                )
                                .addComponent(scrollTablePane)
                        )
                        .addComponent(scrollTextAreaPane)
                )
        );
        if (logData.size() > 0){
            rellenarTodosDatos();
        }
        return contentPane;
    }

    private void selectRow() {
        ArrayList<String[]> list = (datosEncontrados.size() == 0) ? logData : datosEncontrados;
        String[] selectedRow = list.get(table.getSelectedRow());
        textArea.setText(selectedRow[0]);
        int index = selectedRow[1].indexOf("Содержимое");
        if (index < 0){
            textArea.append(selectedRow[1]);
            flagOpenInNotepad = false;
        }else {
            String decodedString = decodeString(selectedRow[1].substring(index+11));
            String formattedXML = XmlUtil.xmlFormat(decodedString,4);
            textArea.append(selectedRow[1].substring(0, index+11));
            textArea.append(System.lineSeparator());
            textArea.append(formattedXML);
            flagOpenInNotepad = true;
        }
    }

    private void btnBuscarAction() {
        String cadena = textFieldBuscar.getText();
        if (!cadena.isEmpty() && !Pattern.matches("^\\b+$", cadena)) {
            rellenarDatosEncontrados(cadena);
        }
    }

    private void openInNotepad() {
        if (!flagOpenInNotepad) return;
        String xmlString = textArea.getText();
        /*удаляю первую строку с датой/временем и символ переноса строки. оставляю только XML*/
        xmlString = xmlString.substring(xmlString.indexOf(System.lineSeparator()) + System.lineSeparator().length());

        /*Записываю XML в файл C:\Users\%USER%\Downloads\TempFile_(Timestamp).xml*/
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Path path = FileSystems.getDefault(). getPath (System.getProperty( "user.home" ), "downloads", "Temp_" + timestamp.getTime() + ".xml" );
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString()), StandardCharsets.UTF_8))) {
            writer.write(xmlString);
        } catch (Exception e){
            e.printStackTrace();
        }

        /*Передаю файл на открытие сторонней программе*/
        try {
            Desktop.getDesktop().open(new File(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String decodeString(String encodedString){
        byte[] decodedBytes= Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }


    private void btnSeleccionarAction(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.LOG", "log");
        fileChooser.setFileFilter(filtro);

        int seleccion= fileChooser.showOpenDialog(contentPane);
        if(seleccion==JFileChooser.APPROVE_OPTION){
            logData.clear();
            File fichero = fileChooser.getSelectedFile();
            textFieldFichero.setText(fichero.getAbsolutePath());
            try (FileInputStream fis = new FileInputStream(fichero);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader fileReader = new BufferedReader(isr)
            ) {
                StringBuilder cadena= new StringBuilder();
                String regex="^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{4}.+";
                String[] datas;

                while(fileReader.ready()){

                    cadena.append(fileReader.readLine());
                    if (Pattern.matches(regex, cadena.toString())){
                        datas = new String[2];
                        datas[0] = cadena.substring(0,24);
                        datas[1] = cadena.substring(24);
                        logData.add(datas);
                    }else {
                        datas = logData.get(logData.size()-1);
                        datas[1] += System.lineSeparator() + cadena;
                        logData.set(logData.size()-1, datas);
                    }
                    cadena = new StringBuilder();

                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            rellenarTodosDatos();
        }
    }

    private void rellenarTodosDatos(){
        datosEncontrados.clear();
        textFieldBuscar.setText("");
        flagOpenInNotepad = false;
        while (modeloInfo.getRowCount() > 0){
            modeloInfo.removeRow(0);
        }
        logData.forEach(arr -> modeloInfo.addRow(new String[]{arr[0], arr[1].substring(0,Math.min(100, arr[1].length()))}));
        table.setRowSelectionInterval(0, 0);
        selectRow();
    }

    private void rellenarDatosEncontrados(String cadena){
        datosEncontrados.clear();
        textArea.setText("");
        flagOpenInNotepad = false;
        while (modeloInfo.getRowCount() > 0){
            modeloInfo.removeRow(0);
        }

        for (String[] logDataArray : logData) {
            int index = logDataArray[1].indexOf("Содержимое");
            String decodedXML = index < 0 ? "" : decodeString(logDataArray[1].substring(index+11));
            if (logDataArray[0].contains(cadena)
                    || logDataArray[1].contains(cadena)
                    || decodedXML.contains(cadena)){
                datosEncontrados.add(logDataArray);

            }
        }
        datosEncontrados.forEach(arr -> modeloInfo.addRow(new String[]{arr[0], arr[1].substring(0,Math.min(100, arr[1].length()))}));
        table.setRowSelectionInterval(0, 0);
        System.gc();
    }
}

