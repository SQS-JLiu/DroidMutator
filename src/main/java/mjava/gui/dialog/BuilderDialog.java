package mjava.gui.dialog;

import edu.ecnu.sqslab.mjava.MutationSystem;
import mjava.util.ShellUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class BuilderDialog extends JDialog {
    private JButton buildBtn;
    private JButton cancelBtn;

    public BuilderDialog(){
        initDialog();
    }

    private void initDialog(){
        this.setTitle("Builder");
        //this.setBounds(300,100,500,500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initBuilder();
        this.setModal(true);
    }

    private void initBuilder(){
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
        contentPanel.setLayout(new GridLayout(3, 1));
        this.setContentPane(contentPanel);

//        JTextPane outputPane = new JTextPane();
//        //outputPane.setPreferredSize(new Dimension(500,200));
//        outputPane.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(outputPane);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        contentPanel.add(scrollPane);
        contentPanel.add(ConsolePane.getInstance(),BorderLayout.CENTER);

        JPanel startBtnPanel = new JPanel();
        startBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER,100,10));
        buildBtn = new JButton("Build");
        buildBtn.setActionCommand("1");
        buildBtn.addActionListener(new BtnListener());
        cancelBtn = new JButton("Cancel");
        cancelBtn.setActionCommand("2");
        cancelBtn.addActionListener(new BtnListener());
        startBtnPanel.add(buildBtn);
        startBtnPanel.add(cancelBtn);
        contentPanel.add(startBtnPanel);

        //Tips: Do not close the window while building
        JPanel tipsPanel = new JPanel();
        tipsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel tipsLabel = new JLabel();
        tipsLabel.setForeground(Color.red);
        tipsLabel.setText("Tips : Do not close the window while building !");
        tipsPanel.add(tipsLabel);
        contentPanel.add(tipsPanel);
    }

    class BtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            int idBtn = Integer.valueOf(e.getActionCommand());
            switch (idBtn){
                case 1:
                    buildBtn.setEnabled(false);
                    cancelBtn.setEnabled(false);
                    new Thread(){
                        public void run(){ //使用线程，方式阻塞当前事件   执行指令添加-u,避免python输出缓存问题
                            String cmd = MutationSystem.PYTHON_PATH +" -u "+ System.getProperty("user.dir");
                            cmd = cmd + File.separator +"builder" + File.separator + "compileAndroidPro.py";
                            Map<String, String> result = ShellUtil.execute(cmd,null);
                            //System.out.println(cmd +"\nFlag: "+result.get("exitFlag")+"\nOutput: "+result.get("output"));
                            if(result.get("exitFlag").equals("1")){
                                System.err.println("Build failed.");
                            }
                            buildBtn.setEnabled(true);
                            cancelBtn.setEnabled(true);
                        }
                    }.start();
                    break;
                case 2:
                    dispose();
                    break;
            }
        }
    }
}
