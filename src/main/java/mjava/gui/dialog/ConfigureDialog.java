package mjava.gui.dialog;

import com.google.common.collect.Maps;
import mjava.util.XMLHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class ConfigureDialog extends JDialog {
    private JTextField muHomePathText;
    private JTextField packagePathText;
    private JTextField mutantsPathText;
    private JTextField appNameText;
    private  JTextField srcPathText;
    private  JTextField buildPathText;
    private JTextField outputPathText;
    private JLabel tipsLabel;
    public ConfigureDialog() {
        initDialog();
    }

    private void initDialog() {
        this.setTitle("Settings");
        //this.setPreferredSize(new Dimension(200,200));
        //this.setBounds(300, 100, 550, 550);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initContentPanel();
        readSettings();
        this.setModal(true);
    }

    private void initContentPanel() {
        int textSize = 30;
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(1, 1, 0, 1));
        contentPanel.setLayout(new GridLayout(9, 1, 1, 1));
        this.setContentPane(contentPanel);
        //mutator configure
        // 1
        JPanel muHomePathPanel = new JPanel();
        muHomePathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel muHomePathLabel = new JLabel("Mutation home: ");
        muHomePathText = new JTextField(); //path
        muHomePathText.setColumns(textSize);
        muHomePathText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                packagePathText.setText(muHomePathText.getText() + File.separator +"src");
                mutantsPathText.setText(muHomePathText.getText() + File.separator+"result");
                buildPathText.setText(muHomePathText.getText() + File.separator+"pro_source");
                outputPathText.setText(muHomePathText.getText() + File.separator+"apk_output");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        muHomePathPanel.add(muHomePathLabel);
        muHomePathPanel.add(muHomePathText);
        JButton dirBtn_1 = new JButton();
        dirBtn_1.setBorderPainted(false);
        dirBtn_1.setContentAreaFilled(false);
        dirBtn_1.setIcon(new ImageIcon("./figure/choose.png"));
        dirBtn_1.setActionCommand("1");
        dirBtn_1.addActionListener(new BtnListener());
        muHomePathPanel.add(dirBtn_1);
        JPanel pathPane_1 = new JPanel(new GridLayout(2,1));
        JLabel describe_1 = new JLabel("** Home directory of files that need to be mutated **");
        pathPane_1.add(describe_1);
        pathPane_1.add(muHomePathPanel);
        contentPanel.add(pathPane_1);
        //2
        JPanel packagePathPanel = new JPanel();
        packagePathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel packagePathLabel = new JLabel();
        packagePathLabel.setText("Package   path: ");
        packagePathText = new JTextField(); //path
        packagePathText.setColumns(textSize);
        packagePathPanel.add(packagePathLabel);
        packagePathPanel.add(packagePathText);
        JButton dirBtn_2 = new JButton();
        dirBtn_2.setBorderPainted(false);
        dirBtn_2.setContentAreaFilled(false);
        dirBtn_2.setIcon(new ImageIcon("./figure/choose.png"));
        dirBtn_2.setActionCommand("2");
        dirBtn_2.addActionListener(new BtnListener());
        packagePathPanel.add(dirBtn_2);
        JPanel pathPane_2 = new JPanel(new GridLayout(2,1));
        JLabel describe_2 = new JLabel("** The directory of the package of the source file **");
        pathPane_2.add(describe_2);
        pathPane_2.add(packagePathPanel);
        contentPanel.add(pathPane_2);
        //3
        JPanel mutantsPathPanel = new JPanel();
        mutantsPathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel mutantsPathLabel = new JLabel();
        mutantsPathLabel.setText("Mutants output: ");
        mutantsPathText = new JTextField(); //path
        mutantsPathText.setColumns(textSize);
        mutantsPathPanel.add(mutantsPathLabel);
        mutantsPathPanel.add(mutantsPathText);
        JButton dirBtn_3 = new JButton();
        dirBtn_3.setBorderPainted(false);
        dirBtn_3.setContentAreaFilled(false);
        dirBtn_3.setIcon(new ImageIcon("./figure/choose.png"));
        dirBtn_3.setActionCommand("3");
        dirBtn_3.addActionListener(new BtnListener());
        mutantsPathPanel.add(dirBtn_3);
        JPanel pathPane_3 = new JPanel(new GridLayout(2,1));
        JLabel describe_3 = new JLabel("** Mutant output directory **");
        pathPane_3.add(describe_3);
        pathPane_3.add(mutantsPathPanel);
        contentPanel.add(pathPane_3);

        //builder configure
        //4
        JPanel appPathPanel = new JPanel();
        appPathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel appPathLabel = new JLabel();
        appPathLabel.setText("Project    name: ");
        appNameText = new JTextField(); //path
        appNameText.setColumns(textSize);
        //appPathText.setEditable(false);
        appPathPanel.add(appPathLabel);
        appPathPanel.add(appNameText);
        //JButton dirBtn_4 = new JButton();
        //dirBtn_4.setBorderPainted(false);
        //dirBtn_4.setContentAreaFilled(false);
        //dirBtn_4.setIcon(new ImageIcon("./figure/choose.png"));
        //dirBtn_4.setActionCommand("4");
        //dirBtn_4.addActionListener(new BtnListener());
        //appPathPanel.add(dirBtn_4);
        JPanel pathPane_4 = new JPanel(new GridLayout(2,1));
        JLabel describe_4 = new JLabel("** Android project name **");
        pathPane_4.add(describe_4);
        pathPane_4.add(appPathPanel);
        contentPanel.add(pathPane_4);
        //5
        JPanel srcPathPanel = new JPanel();
        srcPathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel srcPathLabel = new JLabel();
        srcPathLabel.setText("Project      path: ");
        srcPathText = new JTextField(); //path
        srcPathText.setColumns(textSize);
        srcPathPanel.add(srcPathLabel);
        srcPathPanel.add(srcPathText);
        JButton dirBtn_5 = new JButton();
        dirBtn_5.setBorderPainted(false);
        dirBtn_5.setContentAreaFilled(false);
        dirBtn_5.setIcon(new ImageIcon("./figure/choose.png"));
        dirBtn_5.setActionCommand("5");
        dirBtn_5.addActionListener(new BtnListener());
        srcPathPanel.add(dirBtn_5);
        JPanel pathPane_5 = new JPanel(new GridLayout(2,1));
        JLabel describe_5 = new JLabel("** Android project directory **");
        pathPane_5.add(describe_5);
        pathPane_5.add(srcPathPanel);
        contentPanel.add(pathPane_5);
        //6
        JPanel buildPathPanel = new JPanel();
        buildPathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel buildPathLabel = new JLabel();
        buildPathLabel.setText("Build          path: ");
        buildPathText = new JTextField(); //path
        buildPathText.setColumns(textSize);
        buildPathPanel.add(buildPathLabel);
        buildPathPanel.add(buildPathText);
        JButton dirBtn_6 = new JButton();
        dirBtn_6.setBorderPainted(false);
        dirBtn_6.setContentAreaFilled(false);
        dirBtn_6.setIcon(new ImageIcon("./figure/choose.png"));
        dirBtn_6.setActionCommand("6");
        dirBtn_6.addActionListener(new BtnListener());
        buildPathPanel.add(dirBtn_6);
        JPanel pathPane_6 = new JPanel(new GridLayout(2,1));
        JLabel describe_6 = new JLabel("** Temporary project directory for building apk files **");
        pathPane_6.add(describe_6);
        pathPane_6.add(buildPathPanel);
        contentPanel.add(pathPane_6);
        //7
        JPanel outputPathPanel = new JPanel();
        outputPathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel outputPathLabel = new JLabel();
        outputPathLabel.setText("Apk         output: ");
        outputPathText = new JTextField(); //path
        outputPathText.setColumns(textSize);
        outputPathPanel.add(outputPathLabel);
        outputPathPanel.add(outputPathText);
        JButton dirBtn_7 = new JButton();
        dirBtn_7.setBorderPainted(false);
        dirBtn_7.setContentAreaFilled(false);
        dirBtn_7.setIcon(new ImageIcon("./figure/choose.png"));
        dirBtn_7.setActionCommand("7");
        dirBtn_7.addActionListener(new BtnListener());
        outputPathPanel.add(dirBtn_7);
        JPanel pathPane_7 = new JPanel(new GridLayout(2,1));
        JLabel describe_7 = new JLabel("** Apk file output directory **");
        pathPane_7.add(describe_7);
        pathPane_7.add(outputPathPanel);
        contentPanel.add(pathPane_7);

        JPanel saveAndCancelPanel = new JPanel();
        saveAndCancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,100,10));
        JButton saveBtn = new JButton("Save");
        saveBtn.setActionCommand("8");
        saveBtn.addActionListener(new saveBtnListener());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setActionCommand("9");
        cancelBtn.addActionListener(new saveBtnListener());
        saveAndCancelPanel.add(saveBtn);
        saveAndCancelPanel.add(cancelBtn);
        contentPanel.add(saveAndCancelPanel);

        //Tips: Effective after restart
        JPanel tipsPanel = new JPanel();
        tipsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tipsLabel = new JLabel();
        tipsLabel.setForeground(Color.red);
        tipsLabel.setText("Tips : Effective after restart !");
        tipsPanel.add(tipsLabel);
        contentPanel.add(tipsPanel);
    }

    class BtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            UIManager.put("FileChooser.cancelButtonText", "Cancel");
            JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Choose Directory");
            chooser.setApproveButtonText("Choose");
            int returnVal = chooser.showOpenDialog(chooser);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = chooser.getSelectedFile();
                int idBtn = Integer.valueOf(e.getActionCommand());
                switch (idBtn){
                    case 1:
                        muHomePathText.setText(file.getAbsoluteFile().toString());
                        break;
                    case 2:
                        packagePathText.setText(file.getAbsoluteFile().toString());
                        break;
                    case 3:
                        mutantsPathText.setText(file.getAbsoluteFile().toString());
                        break;
                    case 4:
                        appNameText.setText(file.getAbsoluteFile().toString());
                        break;
                    case 5:
                        srcPathText.setText(file.getAbsoluteFile().toString());
                        break;
                    case 6:
                        buildPathText.setText(file.getAbsoluteFile().toString());
                        break;
                    case 7:
                        outputPathText.setText(file.getAbsoluteFile().toString());
                        break;
                }
            }
        }
    }

    class saveBtnListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            int idBtn = Integer.valueOf(e.getActionCommand());
            switch (idBtn){
                case 8:
                    saveSettings();
                    tipsLabel.setText(tipsLabel.getText()+"                  "+"Save success!!!");
                    break;
                case 9:
                    dispose();
                    break;
            }
        }

    }

    private boolean readSettings(){
        Map<String,String> settingsMap;
        settingsMap = new XMLHandler().readSettings();
        if(settingsMap == null){
            return false;
        }
        if(settingsMap.containsKey("mutation_home")){
            muHomePathText.setText(settingsMap.get("mutation_home"));
        }
        if(settingsMap.containsKey("package_path")){
            packagePathText.setText(settingsMap.get("package_path"));
        }
        if(settingsMap.containsKey("mutants_path")){
            mutantsPathText.setText(settingsMap.get("mutants_path"));
        }
        if(settingsMap.containsKey("project_name")){
            appNameText.setText(settingsMap.get("project_name"));
        }
        if(settingsMap.containsKey("project_path")){
            srcPathText.setText(settingsMap.get("project_path"));
        }
        if(settingsMap.containsKey("build_path")){
            buildPathText.setText(settingsMap.get("build_path"));
        }
        if(settingsMap.containsKey("apk_output")){
            outputPathText.setText(settingsMap.get("apk_output"));
        }
        return true;
    }

    private boolean saveSettings(){
        Map<String,String> settingsMap = Maps.newHashMap();
        settingsMap.put("mutation_home",muHomePathText.getText());
        settingsMap.put("package_path", packagePathText.getText());
        settingsMap.put("mutants_path",mutantsPathText.getText());
        settingsMap.put("project_name", appNameText.getText());
        settingsMap.put("project_path", srcPathText.getText());
        settingsMap.put("build_path", buildPathText.getText());
        settingsMap.put("apk_output", outputPathText.getText());
        return new XMLHandler().writeSettings(settingsMap);
    }
}
