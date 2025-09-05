package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private TaskManager inMemoryTaskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void managersCreations() {
        inMemoryTaskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void historyManagerShouldReturnCorrectListAfterAdditionOfTasksAndRemoval() {

        Task task = inMemoryTaskManager.formulateTaskForCreation("Task1", "Description1", Status.NEW);
        Epic epic = inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtask = inMemoryTaskManager.formulateSubtaskForCreation(1,"Subtask1", "Description1",
                Status.NEW);

        historyManager.add(subtask);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> correctListAfterAddition = List.of(subtask, epic, task);

        Assertions.assertEquals(correctListAfterAddition, historyManager.getTasks());

        historyManager.removeNode(historyManager.getNodeMap().get(task.getId()));

        List<Task> correctListAfterRemoval = List.of(subtask, epic);

        Assertions.assertEquals(correctListAfterRemoval, historyManager.getTasks());
    }

    @Test
    public void historyManagerShouldSavePreviousTaskStates() {

        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(1,"Subtask1",
                "Description1", Status.NEW));

        Task firstTaskReturn = inMemoryTaskManager.returnTaskByID(0);
        Epic firstEpicReturn = inMemoryTaskManager.returnEpicByID(1);
        Subtask firstSubtaskReturn = inMemoryTaskManager.returnSubtaskByID(2);

        inMemoryTaskManager.renewTask(new Task( "Task1Renewed",
                "Description1Renewed", 0, Status.IN_PROGRESS));
        inMemoryTaskManager.renewEpic(new Epic("Epic1Renewed", "Description1Renewed", 1));
        inMemoryTaskManager.renewSubtask(new Subtask(1 ,"Task1Renewed", "Renewed",
                2, Status.IN_PROGRESS));

        List<Task> taskHistory = inMemoryTaskManager.getHistory();

        Assertions.assertEquals(firstTaskReturn.getName(), taskHistory.get(2).getName());
        Assertions.assertEquals(firstEpicReturn.getName(), taskHistory.get(1).getName());
        Assertions.assertEquals(firstSubtaskReturn.getName(), taskHistory.get(0).getName());
    }

    @Test
    public void historyManagerShouldReturnNoTasksInCaseWhenNoTasks() {
        Assertions.assertEquals(List.of(), inMemoryTaskManager.getHistory());
    }

    @Test
    public void historyManagerShouldReturnNoDuplicates() {
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "60", "22:30 01.01.2000"));
        inMemoryTaskManager.returnTaskByID(0);
        inMemoryTaskManager.returnTaskByID(0);
        Assertions.assertEquals("[0,TASK,0,NEW,0,null,60,22:30 01.01.2000]",
                inMemoryTaskManager.getHistory().toString());
    }

    @Test
    public void historyManagerShouldRemoveFirstTaskFromHistoryCorrectly() {
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("0", "0", Status.NEW));
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("1", "1", Status.NEW));
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("2", "2", Status.NEW));
        historyManager.removeNode(historyManager.getNodeMap().get(0));
        Assertions.assertEquals("[2,TASK,2,NEW,2,null,null,null, 1,TASK,1,NEW,1,null,null,null]",
                historyManager.getTasks().toString());
    }

    @Test
    public void historyManagerShouldRemoveLastTaskFromHistoryCorrectly() {
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("0", "0", Status.NEW));
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("1", "1", Status.NEW));
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("2", "2", Status.NEW));
        historyManager.removeNode(historyManager.getNodeMap().get(2));
        Assertions.assertEquals("[1,TASK,1,NEW,1,null,null,null, 0,TASK,0,NEW,0,null,null,null]",
                historyManager.getTasks().toString());
    }

    @Test
    public void historyManagerShouldRemoveMiddleTaskFromHistoryCorrectly() {
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("0", "0", Status.NEW));
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("1", "1", Status.NEW));
        historyManager.add(inMemoryTaskManager.formulateTaskForCreation("2", "2", Status.NEW));
        historyManager.removeNode(historyManager.getNodeMap().get(1));
        Assertions.assertEquals("[2,TASK,2,NEW,2,null,null,null, 0,TASK,0,NEW,0,null,null,null]",
                historyManager.getTasks().toString());
    }

}
