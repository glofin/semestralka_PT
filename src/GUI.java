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

/**
 * Trida vytvari GUI pro aplikaci
 */
class GUI {

    /** tlacitka pro krokovani*/
    private static Box debuggerButtonsBH;
    private static GUI messenger;
    /**vypis z eventu*/
    private static JTextArea outputTA;
    /**scrolovaci pane kde je vypis eventu*/
    private static JScrollPane outputSP;
    /** listener posouvajici scroll pane dolu pri vypisu dat*/
    private static AdjustmentListener outputScrolling = e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum());

    private static String defaultFilePath = "data/tutorial.txt";

    /** hlavni okno GUI*/
    private static JFrame frame;
    /**tlacitko pro zobrazeni stavu prepravy*/
    private static JButton showStateBtn;
    /**tlacitko pro krokovani zpet*/
    private static JButton previeusStepBtn;
    /**tlacitko pro krokovani dopredu*/
    private static JButton nextStepBtn;
    /**tlacitko pro dobehnuti dokonce*/
    private static JButton runToEndBtn;
    /**tlacitko pro zastavni*/
    private static JButton stopBtn;
    /**slider pro rychlost vypisu*/
    private static JSlider speedSlider;
    private static PrintStream printStream;

    /**
     * Jedinacek konstruktor
     */
    private GUI(){};

    public static GUI getInstance(){
        if(messenger==null) {
            messenger = new GUI();
        }
        return messenger;
    }

    /**
     * Hlavni metoda otevre hlavni okno
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Velbloud Planner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setUp(frame);

            printStream = new PrintStream(new CustomOutputStream(outputTA));
            PrintStream standardOut = System.out;


            System.setOut(printStream);
            System.setErr(standardOut);
        });
    }


    /**
     * Nastavi kompnenty ve frame
     */
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

        stopBtn = new JButton("Pozastavit");
        stopBtn.addActionListener(e -> stopBtnController());
        stopBtn.setEnabled(false);

        previeusStepBtn = new JButton("Krok zpět");
        previeusStepBtn.addActionListener(e -> previeusStepBtnController());

        nextStepBtn = new JButton("Krok dopředu");
        nextStepBtn.addActionListener(e -> nextStepBtnController());

        runToEndBtn = new JButton("Doběhnout dokonce");
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
        speedSlider = new JSlider(JSlider.HORIZONTAL);
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


    /**
     * Akce pri kliknuti na zastaveni
     */
    public static void stopBtnController() {

        Main.stopRunningOutput();
        //Deaktivovat tlactka kroku kdyz jede dopredu
        previeusStepBtn.setEnabled(true);
        nextStepBtn.setEnabled(true);
        runToEndBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    /**
     * Akce pri kliknuti na krok zpet
     */
    private static void previeusStepBtnController() {
        showStateBtn.setEnabled(false);
        Main.previusStepEvent();
    }

    /**
     * Akce pri kliknuti na krok dopredu
     */
    private static void nextStepBtnController() {
        if (Main.nextStepEvent()[1]) {showStateBtn.setEnabled(true);}
    }

    /**
     * Akce pri kliknuti na dobehnout dokonce
     */
    private static void runToEndBtnController() {
        //nastaveni rychlosti outputu podle slideru
        speedSliderController(speedSlider);

        Thread thread = new Thread(Main::runToEnd);
        thread.start();

        //Deaktivovat tlactka kroku kdyz jede dopredu
        previeusStepBtn.setEnabled(false);
        nextStepBtn.setEnabled(false);
        runToEndBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    /**
     * Zobrazi okno s aktualnim stavem prepravy
     */
    private static void showStateBtnController() {
        showMessageDialog(null, Main.getAppReport(), "Aktuální stav přepravy", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Zavola start z main, ktere nastartuje aplikace
     * @param filePath cesta k souboru mapy
     */
    private static void startMain(String filePath) {
        //System.out.println("GUI start main" + filePath);
        Thread thread = new Thread(() -> Main.start(filePath));
        thread.start();
    }

    /**
     * Ovladani rychlosti vypisu - pri zmene hodnoty
     * @param slider slider pro ovlivnovani hodnoty
     */
    private static void speedSliderController(JSlider slider) {
        double sliderMin = slider.getMinimum();
        double sliderMax = slider.getMaximum();
        double sliderValue = slider.getValue();
        Main.changeSpeed(sliderValue, sliderMin, sliderMax);
    }

    /**
     * Pri kliknuti na importovat soubor
     * Nacte soubor z file okna
     */
    private static void importMapController() {

        System.out.println();
        System.out.println();
        //System.out.println("import");

        //Zaktivovat Tlacitka
        for (Component component :
                debuggerButtonsBH.getComponents()) {
            component.setEnabled(true);
        }
        stopBtn.setEnabled(false);

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

    }

    public static PrintStream getPrintStream() {
        return printStream;
    }
}

/**
 * Trida pro vypis z konzole do outputTA
 *
 */
class CustomOutputStream extends OutputStream {
    private final JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}