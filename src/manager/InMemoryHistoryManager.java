package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> customLinkedList = new HashMap<>();

    private Node first;

    private Node last;

    private class Node {
        Task element;
        Node next;
        Node previous;

        Node(Node previous, Task element, Node next) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        remove(taskId);

        linkLast(task);
        customLinkedList.put(taskId, last);
    }

    public void remove(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
            customLinkedList.remove(id);
        }
    }

    public List<Task> getTasks() { // собирает все задачи в ArrayList
        List<Task> listHistoryList = new ArrayList<>();
        Node node = first;
        while (node != null) {
            listHistoryList.add(node.element);
            node = node.next;
        }
        return listHistoryList;
    }

    private void linkLast(Task task) { // добавляет задачу в конец списка
        final Node oldTail = last;
        final Node newNode = new Node(oldTail, task, null);
        last = newNode;
        if (oldTail == null) {
            first = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node node) { // принимает узел связного списка и вырезает его
        Node nextNext = node.next;
        Node previousPrev = node.previous;
        if (previousPrev == null) {
            first = nextNext;
        } else {
            previousPrev.next = nextNext;
        }
        if (nextNext == null) {
            last = previousPrev;
        } else {
            nextNext.previous = previousPrev;
        }
    }
}