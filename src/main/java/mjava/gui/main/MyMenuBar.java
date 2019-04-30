package mjava.gui.main;

import mjava.gui.dialog.BuilderDialog;
import mjava.gui.dialog.ConfigureDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyMenuBar extends JMenuBar {

    public MyMenuBar(){
        initMenu();
    }

    private void initMenu(){
        JMenu menuFile = new JMenu("File"), menuBuild = new JMenu("Build");

        menuFile.setMnemonic('F');
        menuBuild.setMnemonic('B');

        this.add(menuFile);
        this.add(menuBuild);

        JMenuItem itemSettings = new JMenuItem("Settings");
        menuFile.add(itemSettings);

        JMenuItem itemBuilder = new JMenuItem("Builder");
        menuBuild.add(itemBuilder);

        itemSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("I'm settings...");
                ConfigureDialog configurePage = new ConfigureDialog();
                configurePage.pack();
                configurePage.setVisible(true);
            }
        });

        itemBuilder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("I'm builder...");
                BuilderDialog builderPage = new BuilderDialog();
                builderPage.pack();
                builderPage.setVisible(true);
            }
        });
    }
}
