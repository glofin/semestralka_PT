import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Hashtable;

import static java.awt.Component.*;

class GUI {

    private static GUI messenger;
    private static JTextArea outputTA;
    private static JScrollPane outputSP;

    private static AdjustmentListener outputScrolling = e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum());

    private static boolean opened = true;

    private static JFrame frame;

    private GUI(){};

    public static GUI getInstance(){
        if(messenger==null) {
            messenger = new GUI();
        }
        return messenger;
    }

    /*public static void start(){
        JFrame frame = new JFrame("Velbloud z Main");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitGUI(frame);
            }
        });
        setUp(frame);
    }*/

    public static void main(String[] args) {
        frame = new JFrame("Velbloud Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUp(frame);
        //Main.start("data/tutorial.txt");
    }

    private static void setUp(JFrame frame) {

        //Frame
        frame.setSize(800, 500);
        //Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        //Menu
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JButton importMap = new JButton("Import Map");
        importMap.addActionListener(e -> importMapFile());
        file.add(importMap);
        menu.add(file);

        //Main VerticalBox
        Box mainVB = Box.createVerticalBox();
        //mainVB.setSize(800,450);

        //Tlacitka

        //Debugger Tlacitka
        Box debuggerButtonsBH = Box.createHorizontalBox();

        JButton stop = new JButton("Pozastavit");
        JButton previeusStep = new JButton("Krok zpět");
        JButton nextStep = new JButton("Krok dopředu");
        JButton toEnd = new JButton("Doběhnout dokonce");
        JButton showState = new JButton("Aktuální stav");

        debuggerButtonsBH.add(stop);
        debuggerButtonsBH.add(previeusStep);
        debuggerButtonsBH.add(nextStep);
        debuggerButtonsBH.add(toEnd);
        debuggerButtonsBH.add(showState);

        //Pridat odebrat pozadavek
        Box taskButtonsBH = Box.createHorizontalBox();

        JButton addNewTask = new JButton("Přidat nový požadavek");
        JButton deleteTask = new JButton("Smazat požadavek");

        taskButtonsBH.add(addNewTask);
        taskButtonsBH.add(deleteTask);

        //Rychlost slider
        JSlider speed = new JSlider(JSlider.HORIZONTAL);
        speed.setMinimum(1);
        speed.setMaximum(8);
        speed.setValue(4);
        /*speed.setMajorTickSpacing(10);
        speed.setMinorTickSpacing(1);*/
        speed.setPaintTicks(true);
        speed.setPaintLabels(true);
        Hashtable<Integer,JLabel> labelTable = new Hashtable<>();
        labelTable.put(1, new JLabel("0.25") );
        labelTable.put(2, new JLabel("0.5") );
        labelTable.put(3, new JLabel("0.75") );
        labelTable.put(4, new JLabel("1") );
        labelTable.put(5, new JLabel("1.25") );
        labelTable.put(6, new JLabel("1.5") );
        labelTable.put(7, new JLabel("1.75") );
        labelTable.put(8, new JLabel("2") );
        speed.setLabelTable( labelTable );

        //TextArea pro vypis
        outputTA = new JTextArea("test");
        outputTA.setEditable(false);
        outputTA.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        outputSP = new JScrollPane (outputTA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //Komponenty do MainVB
        mainVB.add(debuggerButtonsBH);
        mainVB.add(taskButtonsBH);
        mainVB.add(speed);
        mainVB.add(outputSP);

        //Komponenty do Panel
        panel.add(menu);
        panel.add(mainVB);
        panel.add(outputSP);

        //Komponenty do Frame
        //frame.add(panel);
        frame.getContentPane().add(BorderLayout.PAGE_START, menu);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    public static void addToOutputGUI(String output){
        outputTA.append(output);
        outputSP.getVerticalScrollBar().addAdjustmentListener(outputScrolling);
    }

    private static void importMapFile() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "TXT Documents (*.txt)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".txt");
                }
            }
        });
        fileChooser.showOpenDialog(frame);
        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null) Main.start(selectedFile.getAbsolutePath());
        outputSP.getVerticalScrollBar().getAdjustmentListeners();

        outputSP.getVerticalScrollBar().removeAdjustmentListener(outputScrolling);

    }

}