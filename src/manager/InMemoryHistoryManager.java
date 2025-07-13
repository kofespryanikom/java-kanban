package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> tasksHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        tasksHistory.add(0, new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus()));
        if (tasksHistory.size() > 10) {
            tasksHistory.remove(10);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return tasksHistory;
    }
}
