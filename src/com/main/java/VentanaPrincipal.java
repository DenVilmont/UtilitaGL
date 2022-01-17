package com.main.java;
import com.main.java.MenuSettings.VentanaDBSettings;
import com.main.java.Ventanas.VentanaLog;
import com.main.java.Ventanas.VentanaMQSender;
import com.main.java.MenuSettings.VentanaMQSettings;
import com.main.java.MenuSettings.VentanaMesSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            "javax.swing.plaf.metal.MetalLookAndFeel");
//                    UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                new VentanaPrincipal().setVisible(true);
            }
        });
    }

    public VentanaPrincipal(){
        super();
        configuracionVentana();
        openLogReader();
    }

    private void configuracionVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(0,0, dimension.width-100, dimension.height-100);
        setTitle("UtilitaSupport");
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnMenuVentanas = new JMenu("Меню");
        menuBar.add(mnMenuVentanas);

        JMenuItem mntmLogReader = new JMenuItem("Ридер логов");
        mnMenuVentanas.add(mntmLogReader);
        mntmLogReader.addActionListener(e -> openLogReader());

        JMenuItem mntmMQSender = new JMenuItem("Подкинуть сообщение");
        mnMenuVentanas.add(mntmMQSender);
        mntmMQSender.addActionListener(e -> openMQSender());


        JMenu mnMenuSettings = new JMenu("Настройки");
        menuBar.add(mnMenuSettings);

        JMenuItem mntmMQSetting = new JMenuItem("Настройки MQ");
        mnMenuSettings.add(mntmMQSetting);
        mntmMQSetting.addActionListener(e -> openMQSetting());

        JMenuItem mntmMesSettings = new JMenuItem("Настройки сообщений");
        mnMenuSettings.add(mntmMesSettings);
        mntmMesSettings.addActionListener(e -> openMesSettings());

        JMenuItem mntmDBSettings = new JMenuItem("Настройки БД");
        mnMenuSettings.add(mntmDBSettings);
        mntmDBSettings.addActionListener(e -> openDBSettings());
    }

    private void openDBSettings() {
        if (getTitle().equals("UtilitaSupport::DBSettings"))
            return;
        VentanaDBSettings ventanaDB = new VentanaDBSettings();
        setContentPane(ventanaDB.getContentPane());
        setTitle("UtilitaSupport::DBSettings");
        revalidate();
    }

    private void openLogReader() {
        if (getTitle().equals("UtilitaSupport::LogReader"))
            return;
        VentanaLog ventanaLog = new VentanaLog();
        setContentPane(ventanaLog.getContentPane());
        setTitle("UtilitaSupport::LogReader");
        revalidate();
    }

    private void openMQSetting() {
        if (getTitle().equals("UtilitaSupport::MQSetting"))
            return;
        /*инициация окна настроек*/
        VentanaMQSettings mqSettings = new VentanaMQSettings();
        setContentPane(mqSettings.getContentPane());
        setTitle("UtilitaSupport::MQSetting");
        revalidate();
    }

    private void openMesSettings(){
        if (getTitle().equals("UtilitaSupport::MessageSettings"))
            return;
        /*инициация окна настроек*/
        VentanaMesSettings mesSettings = new VentanaMesSettings();
        setContentPane(mesSettings.getContentPane());
        setTitle("UtilitaSupport::MessageSettings");
        revalidate();
    }

    private void openMQSender(){
        if (getTitle().equals("UtilitaSupport::MQSender"))
            return;
        /*инициация окна настроек*/
        VentanaMQSender mqSender = new VentanaMQSender();
        setContentPane(mqSender.getContentPane());
        setTitle("UtilitaSupport::MQSender");
        revalidate();
    }
}
