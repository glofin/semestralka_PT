import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;

import static java.awt.Component.LEFT_ALIGNMENT;

class GUI_test{

    private static GUI_test messenger;
    private static JTextArea outputTA;
    private static JScrollPane outputSP;

    private static AdjustmentListener outputScrolling = e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum());

    private static boolean opened = true;

    private static String defaultFilePath = "data/tutorial.txt";

    private static JFrame frame;

    private StringBuilder outputTAStrBui = new StringBuilder();

    private GUI_test(){};

    public static GUI_test getInstance(){
        if(messenger==null) {
            messenger = new GUI_test();
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
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Velbloud Planner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setUp(frame);

            PrintStream printStream = new PrintStream(new CustomOutputStream(outputTA));
            PrintStream standardOut = System.out;

            // re-assigns standard output stream and error output stream
            System.setOut(printStream);
            System.setErr(printStream);
            //Main.start("data/tutorial.txt");
            //Main.start2();
        });


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
        importMap.addActionListener(e -> importMapController());
        file.add(importMap);
        menu.add(file);

        //Main VerticalBox
        Box mainVB = Box.createVerticalBox();
        //mainVB.setSize(800,450);

        //Tlacitka

        //Debugger Tlacitka
        Box debuggerButtonsBH = Box.createHorizontalBox();

        JButton stop = new JButton("Pozastavit");
        stop.addActionListener(e -> printLog());
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
        speed.addChangeListener(e -> speedController(speed));

        //TextArea pro vypis
        outputTA = new JTextArea();
        outputTA.setEditable(false);
        outputTA.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        outputSP = new JScrollPane (outputTA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        outputSP.getVerticalScrollBar().addAdjustmentListener(outputScrolling);

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
    public static void printLog() {
        Thread thread = new Thread(() -> {
            while (true) {
                Main.start(defaultFilePath);
            }
        });
        thread.start();
    }

    static int i = 0;
    public static void stopController() {
        TestGUI.start();
        /*i++;
        outputTA.append(i + "\n");*/
    }

    public void addToOutputGUI(String output){
        outputTA.append(output);
        //outputSP.getVerticalScrollBar().update(outputSP.getVerticalScrollBar().getGraphics());
        //outputTA.update(outputTA.getGraphics());
        /*outputSP.getVerticalScrollBar().update(outputSP.getVerticalScrollBar().getGraphics());
        outputSP.getVerticalScrollBar().repaint();
        outputSP.update(outputTA.getGraphics());*/



        //outputSP.getVerticalScrollBar().setValue(outputSP.getVerticalScrollBar().getMaximum());
    }

    public void addToOutputGUI2(String output){

        outputTA.append(output);
        //outputSP.getVerticalScrollBar().update(outputSP.getVerticalScrollBar().getGraphics());
        outputTA.update(outputTA.getGraphics());
        /*outputSP.getVerticalScrollBar().update(outputSP.getVerticalScrollBar().getGraphics());
        outputSP.getVerticalScrollBar().repaint();
        outputSP.update(outputTA.getGraphics());*/



        //outputSP.getVerticalScrollBar().setValue(outputSP.getVerticalScrollBar().getMaximum());
    }
    public void addToOutputGUI3(String output){
        final String text = outputTAStrBui.toString() + "\n";

        SwingUtilities.invokeLater(() -> outputTA.append(text));

        outputTAStrBui.setLength(0);
        outputTAStrBui.append(output);
    }

    private static void speedController(JSlider slider) {
        double sliderMin = slider.getMinimum();
        double sliderMax = slider.getMaximum();
        double sliderValue = slider.getValue();
        Main.changeSpeed(sliderValue, sliderMin, sliderMax);
    }

    private static void importMapController() {
        /*JFileChooser fileChooser = new JFileChooser();

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
        });*/
        Main.start2();
        //Main.start(defaultFilePath);
        /*if (defaultFilePath != null) Main.start(defaultFilePath);
        else {
            fileChooser.showOpenDialog(frame);
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) Main.start(selectedFile.getAbsolutePath());
        }*/

        /*JScrollBar scrollbar = outputSP.getVerticalScrollBar();
        //aby bylo mozni scrollovat nahoru
        for( AdjustmentListener al : scrollbar.getAdjustmentListeners()) {
            scrollbar.removeAdjustmentListener(al);
        }*/
        //aby vychozi pozice scrollovani nahoru byla nejnizsi
        //scrollbar.setValue(scrollbar.getMaximum()/2);
    }

}



/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 * @author www.codejava.net
 *
 */
 class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        // redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}