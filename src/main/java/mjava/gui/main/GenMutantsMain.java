package mjava.gui.main;

import com.google.common.collect.Maps;
import edu.ecnu.sqslab.mjava.InheritanceRelation;
import edu.ecnu.sqslab.mjava.MutationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Vector;

/**
 * @author  Jian Liu
 * Created by user on 2018/5/5.
 */
public class GenMutantsMain extends JFrame {
    private final static Logger logger = LoggerFactory.getLogger(GenMutantsMain.class);

    private JTabbedPane mutantTabbedPane = new JTabbedPane();

    /** Panel for generating mutants. */
    private MutantsGenPanel genPanel;

    /** Panel for viewing details of  mutants.  */
    TraditionalMutantsViewerPanel tvPanel;

    public static Map<String,String> optionMap = Maps.newHashMap();

    public GenMutantsMain()
    {
        try
        {
            jbInit();
            this.setTitle("DroidMutator");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Main program for generating mutants (no parameter required for run).
     *  supporting functions:
     *  (1) selection of Java source files to apply,
     *  (2) selection of mutation operators to apply
     * @throws Exception
     */
    public static void main (String[] args)
    {
        System.out.println("The main method starts [System home is "+MutationSystem.SYSTEM_HOME+"].");
        logger.info("------------------------------------------------------------------------------");
        logger.info("The main method starts [System home is "+MutationSystem.SYSTEM_HOME+"].");
        try {
            if(args != null){ // shell/cmd: setting configuration file，and mutate directly
                String[] option;
                for (String op : args){  // location_config=xxx/project_config=xxx/operators_config=xxx/...
                    if(op.contains("=")){
                        option = op.split("=");
                        optionMap.put(option[0],option[1]);
                    }else {
                        System.out.println("Options format:  location_config=xxx.xml \n" +
                                           "                 project_config=xxx.xml \n" +
                                           "                 operators_config=xxx.config");
                        System.exit(0);
                    }
                }
            }
            MutationSystem.setMutationStructure();
            MutationSystem.recordClassRelation();
            InheritanceRelation.getInstance().initInhertionInfo();
        }
        catch (NoClassDefFoundError e) {
            System.err.println("[ERROR] Could not find one of the classes necessary to run DroidMutator. Make sure that the .jar file for openjava is in your classpath.");
            System.err.println();
            e.printStackTrace();
            return;
        }
        if(optionMap.size() == 0){ //Non-command line startup
            GenMutantsMain main = new GenMutantsMain();
            try {
                main.pack();
            }
            catch (NullPointerException e) {
                System.err.println("[ERROR] An error occurred while initializing DroidMutator. This may have happened because the files used by DroidMutator are in an unexpected state. Try deleting any uncompiled mutants that were generated in the result/ directory, and then re-generate them.");
                System.err.println();
                e.printStackTrace();
                return;
            }
            main.setVisible(true);
        }else {
            Vector fileVt = MutationSystem.getNewTragetFiles();
            String[] file_list = new String[fileVt.size()];
            for (int i=0;i<fileVt.size();i++){
                file_list[i] = (String) fileVt.get(i);
            }
            MutantsGenPanel.generateMutants(file_list,MutationSystem.tm_operators);
        }
        logger.info("Mutation has been completed...");
    }

    /** <p> Initialize GenMutantsMain </p> */
    private void jbInit()
    {
        MyMenuBar myMenuBar = new MyMenuBar();
        this.setJMenuBar(myMenuBar);
        genPanel = new MutantsGenPanel(this);
        tvPanel = new TraditionalMutantsViewerPanel();

        mutantTabbedPane.add("Mutants Generator", genPanel);
        mutantTabbedPane.add("Mutants Viewer", tvPanel);
        this.getContentPane().add(mutantTabbedPane);
        this.addWindowListener( new java.awt.event.WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                this_windowClosing(e);
            }
        } );
    }

    private void this_windowClosing (WindowEvent e)
    {
        System.exit(0);
    }
}
