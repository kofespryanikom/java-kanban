package manager;

import dataStructures.Node;
import model.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {

    void linkLast(Task task);

    void add(Task task);

    void removeNode(Node<Task> node);

    List<Task> getTasks();

    Map<Integer, Node<Task>> getNodeMap();
}
