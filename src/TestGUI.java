
public class TestGUI {
    static int i = 0;
    public static void start() {
        while (true) {
            i++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //GUI.getInstance().addToOutputGUI3(i + "\n");
            System.out.println(i);
        }
    }

    public static void printLog() {
        Thread thread = new Thread(() -> {
            while (true) {

            }
        });
        thread.start();
    }
}
