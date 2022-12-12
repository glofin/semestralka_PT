import java.util.List;

/**
 * Trida pro to kdyz nema sklad dostatek kosu pro Task
 * prida se do event v EventManageru event DelayedTask ktery
 * se vykona kdyz sklad uz ma dostatek kosu
 */
public class DelayedTask {
    public Task task;
    public MyPath path;
    public List<Camel> camelsOnTask;

    /**
     * Konstruktor
     * @param task task ktery se puvodne zpracovaval
     * @param path vybrana cesta pro tento task
     * @param camelsOnTask vybrani velbloudi pro tento task
     */
    public DelayedTask(Task task, MyPath path, List<Camel> camelsOnTask) {
        this.task = task;
        this.path = path;
        this.camelsOnTask = camelsOnTask;
    }
}
