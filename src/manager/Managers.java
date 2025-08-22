package manager;

import java.io.File;
import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() {
        TaskManager taskManager = new InMemoryTaskManager();
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }

    public static FileBackedTaskManager getBackedTaskManager(String path) {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(path);
        return backedTaskManager;
    }

    public static FileBackedTaskManager getRecoveredBackedManager(String path) throws IOException {
        File file = new File(path);
        FileBackedTaskManager backedTaskManager = FileBackedTaskManager.loadFromFile(file);
        return backedTaskManager;
    }
}
