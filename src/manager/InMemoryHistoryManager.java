package manager;

import dataStructures.Node;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private Node<Task> tail;
    private int size = 0;

    @Override
    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode;
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
            nodeMap.remove(task.getId());
        }
        size++;
        if (task instanceof Epic) {
            newNode = new Node<>(oldTail, new Epic((Epic) task), null);
        } else if (task instanceof Subtask) {
            newNode = new Node<>(oldTail, new Subtask((Subtask) task), null);
        } else {
            newNode = new Node<>(oldTail, new Task(task), null);
        }
        tail = newNode;
        if (oldTail != null) {
            oldTail.setNext(newNode);
        }
    }

    @Override
    public void add(Task task) {
        linkLast(task);
        nodeMap.put(task.getId(), tail);
    }

    @Override
    public void removeNode(Node<Task> node) {
        if (node != null) {
            Node<Task> prevNode = node.getPrev();
            Node<Task> nextNode = node.getNext();
            if (prevNode == null && nextNode == null) {
                tail = null;
            } else if (prevNode == null) {
                nextNode.setPrev(null);
            } else if (nextNode == null) {
                tail = prevNode;
                prevNode.setNext(null);
            } else {
                prevNode.setNext(nextNode);
                nextNode.setPrev(prevNode);
            }
            size--;
            nodeMap.remove(node.getData().getId());
        }
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasksHistory = new ArrayList<>();
        Node<Task> element = tail;
        for (int i = 0; i < size; i++) {
            tasksHistory.add(element.getData());
            element = element.getPrev();
        }
        return tasksHistory;
    }

    public Map<Integer, Node<Task>> getNodeMap() {
        return nodeMap;
    }
}
