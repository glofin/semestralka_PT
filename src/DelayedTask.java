import java.util.List;

public class DelayedTask {
    public Task task;
    public MyPath path;
    public List<Camel> camelsOnTask;

    public DelayedTask(Task task, MyPath path, List<Camel> camelsOnTask) {
        this.task = task;
        this.path = path;
        this.camelsOnTask = camelsOnTask;
    }
}
