import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import static java.awt.Component.LEFT_ALIGNMENT;
import static javax.swing.JOptionPane.showMessageDialog;

class GUI {

    private static Box debuggerButtonsBH;
    private static GUI messenger;
    private static JTextArea outputTA;
    private static JScrollPane outputSP;

    private static AdjustmentListener outputScrolling = e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum());

//    private static boolean opened = true;

    private static String defaultFilePath = "data/tutorial.txt";

    private static JFrame frame;
    //private static Box taskButtonsBH;
    private static JButton showStateBtn;

 //   private StringBuilder outputTAStrBui = new StringBuilder();

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
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Velbloud Planner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setUp(frame);

            PrintStream printStream = new PrintStream(new CustomOutputStream(outputTA));
   //         PrintStream standardOut = System.out;


            System.setOut(printStream);
            System.setErr(printStream);
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
        debuggerButtonsBH = Box.createHorizontalBox();

        JButton stopBtn = new JButton("Pozastavit");
        stopBtn.addActionListener(e -> stopBtnController());

        JButton previeusStepBtn = new JButton("Krok zpět");
        previeusStepBtn.addActionListener(e -> previeusStepBtnController());

        JButton nextStepBtn = new JButton("Krok dopředu");
        nextStepBtn.addActionListener(e -> nextStepBtnController());

        JButton runToEndBtn = new JButton("Doběhnout dokonce");
        runToEndBtn.addActionListener(e -> runToEndBtnController());

        showStateBtn = new JButton("Aktuální stav");
        showStateBtn.addActionListener(e -> showStateBtnController());

        debuggerButtonsBH.add(stopBtn);
        debuggerButtonsBH.add(previeusStepBtn);
        debuggerButtonsBH.add(nextStepBtn);
        debuggerButtonsBH.add(runToEndBtn);
        debuggerButtonsBH.add(showStateBtn);
        //tlacitka nefunguji pred importem souboru
        for (Component component :
                debuggerButtonsBH.getComponents()) {
            component.setEnabled(false);
        }

        //Pridat odebrat pozadavek
        /*taskButtonsBH = Box.createHorizontalBox();

        JButton addNewTaskBtn = new JButton("Přidat nový požadavek");
        addNewTaskBtn.addActionListener(e -> addNewTaskBtnController());

        JButton deleteTaskBtn = new JButton("Smazat požadavek");
        deleteTaskBtn.addActionListener(e -> deleteTaskBtnController());

        taskButtonsBH.add(addNewTaskBtn);
        taskButtonsBH.add(deleteTaskBtn);
        //tlacitka nefunguji pred importem souboru
        for (Component component :
                taskButtonsBH.getComponents()) {
            component.setEnabled(false);
        }*/

        //Rychlost slider
        JSlider speedSlider = setUpSlider();

        //TextArea pro vypis
        outputTA = new JTextArea();
        outputTA.setEditable(false);
        outputTA.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        outputSP = new JScrollPane (outputTA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        outputSP.getVerticalScrollBar().addAdjustmentListener(outputScrolling);

        //Komponenty do MainVB
        mainVB.add(debuggerButtonsBH);
        //mainVB.add(taskButtonsBH);
        mainVB.add(speedSlider);
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
    
    private static JSlider setUpSlider() {
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL);
        speedSlider.setMinimum(1);
        speedSlider.setMaximum(8);
        speedSlider.setValue(4);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        Hashtable<Integer,JLabel> labelTable = new Hashtable<>();
        labelTable.put(1, new JLabel("0.25") );
        labelTable.put(2, new JLabel("0.5") );
        labelTable.put(3, new JLabel("0.75") );
        labelTable.put(4, new JLabel("1") );
        labelTable.put(5, new JLabel("1.25") );
        labelTable.put(6, new JLabel("1.5") );
        labelTable.put(7, new JLabel("1.75") );
        labelTable.put(8, new JLabel("2") );
        speedSlider.setLabelTable( labelTable );
        speedSlider.addChangeListener(e -> speedSliderController(speedSlider));
		return speedSlider;
    }

    /*private static void addNewTaskBtnController() {
        JFrame newTaskFrame = new JFrame("Přidat nový požadavek");
        newTaskFrame.setVisible(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        Box mainVB = Box.createVerticalBox();


        NumberFormat format = NumberFormat.getInstance();

        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        //formatter.setCommitsOnValidEdit(true);


        JLabel idOasisLabel = new JLabel("Zadejte id oázy:");
        NumberFormatter formatterIdOasis = new NumberFormatter(format);
        formatterIdOasis.setValueClass(Integer.class);
        formatterIdOasis.setMinimum(0);
        formatterIdOasis.setMaximum(Main.getOasisMaxId());
        formatterIdOasis.setAllowsInvalid(false);
        JFormattedTextField idOasisTF = new JFormattedTextField(formatterIdOasis);

        JLabel bascketCountLabel = new JLabel("Zadejte počet košů:");
        JFormattedTextField bascketCounTF = new JFormattedTextField(formatter);

        JLabel dedalineLabel = new JLabel("Deadline čas:");
        JFormattedTextField deadlineTF = new JFormattedTextField(formatter);

        JButton addEventBtn = new JButton("Přidat Požadavek");
        addEventBtn.addActionListener(e ->
                addEventBtnController(
                        newTaskFrame,
                        idOasisTF.getValue(),
                        bascketCounTF.getValue(),
                        deadlineTF.getValue()
                        ));

        //mainVB add Components
        mainVB.add(idOasisLabel);
        mainVB.add(idOasisTF);

        mainVB.add(bascketCountLabel);
        mainVB.add(bascketCounTF);

        mainVB.add(dedalineLabel);
        mainVB.add(deadlineTF);

        panel.add(mainVB);
        newTaskFrame.add(panel);
        newTaskFrame.pack();
        //id oazy
        //pocet kosu
        //deadline
        //cas pridani ziskat auto
    }*/

    /*private static void addEventBtnController(JFrame frame, int idOasis, int bascketCount, int deadline) {
        Main.addTaskEvent(new Task());

        frame.dispose();
    }*/

    public static void stopBtnController() {
        //Aktivovat Tlacitka Tasku
        /*for (Component component :
                taskButtonsBH.getComponents()) {
            component.setEnabled(true);
        }*/

        Main.stopRunningOutput();
    }

    private static void previeusStepBtnController() {
        showStateBtn.setEnabled(false);
        Main.previusStepEvent();
    }

    private static void nextStepBtnController() {
        if (Main.nextStepEvent()[1]) {showStateBtn.setEnabled(true);}
    }

    private static void runToEndBtnController() {
        //Dektivovat Tlacitka Tasku
        /*for (Component component :
                taskButtonsBH.getComponents()) {
            component.setEnabled(false);
        }*/

        Thread thread = new Thread(() -> {Main.runToEnd();});
        thread.start();
    }

    private static void showStateBtnController() {
        showMessageDialog(null, Main.getAppReport(), "Aktuální stav přepravy", JOptionPane.PLAIN_MESSAGE);
    }

    private static void startMain(String filePath) {
        Thread thread = new Thread(() -> Main.start(filePath));
        thread.start();
    }

    private static void speedSliderController(JSlider slider) {
        double sliderMin = slider.getMinimum();
        double sliderMax = slider.getMaximum();
        double sliderValue = slider.getValue();
        Main.changeSpeed(sliderValue, sliderMin, sliderMax);
    }

    private static void importMapController() {
        //TODO opakovane zadani souboru hazi chyby
        //Zaktivovat Tlacitka
        for (Component component :
                debuggerButtonsBH.getComponents()) {
            component.setEnabled(true);
        }
        /*for (Component component :
                taskButtonsBH.getComponents()) {
            component.setEnabled(true);
        }*/

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

        if (defaultFilePath != null) {startMain(defaultFilePath);}
        else {
            fileChooser.showOpenDialog(frame);
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {startMain(selectedFile.getAbsolutePath());}
        }

        JScrollBar scrollbar = outputSP.getVerticalScrollBar();
        //aby bylo mozni scrollovat nahoru
        for( AdjustmentListener al : scrollbar.getAdjustmentListeners()) {
            scrollbar.removeAdjustmentListener(al);
        }
        //aby vychozi pozice scrollovani nahoru byla nejnizsi
        scrollbar.setValue(scrollbar.getMaximum()/2);

        //Dektivovat Tlacitka
       /* for (Component component :
                debuggerButtonsBH.getComponents()) {
            component.setEnabled(false);
        }*/
        /*for (Component component :
                taskButtonsBH.getComponents()) {
            component.setEnabled(false);
        }*/

    }

}

/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 * @author www.codejava.net
 *
 */
class CustomOutputStream extends OutputStream {
    private final JTextArea textArea;

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