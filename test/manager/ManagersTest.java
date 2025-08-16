package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ManagersTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void managersCreations() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void managerShouldReturnWorkingTaskManager() {
        Assertions.assertTrue(taskManager instanceof InMemoryTaskManager);
        Assertions.assertTrue(historyManager instanceof InMemoryHistoryManager);
    }

    @Test
    public void managerShouldBeAbleAddCorrectTaskTypesAndFindTasksByID() {

        taskManager.createTask(taskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        taskManager.createEpic(taskManager.formulateEpicForCreation("Epic1", "Description1"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW));

        Assertions.assertTrue(taskManager.returnTaskByID(0) instanceof Task);
        Assertions.assertTrue(taskManager.returnEpicByID(1) instanceof Epic);
        Assertions.assertTrue(taskManager.returnSubtaskByID(2) instanceof Subtask);
    }

    @Test
    public void managerShouldGenerateSameIDasMeant() {
        taskManager.createTask(taskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        Assertions.assertEquals(0, taskManager.getIdCounter());
        taskManager.createEpic(taskManager.formulateEpicForCreation("Epic1", "Description1"));
        Assertions.assertEquals(1, taskManager.getIdCounter());
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW));
        Assertions.assertEquals(2, taskManager.getIdCounter());
    }

    @Test
    public void taskPassedAndTaskReturnedShouldBeEqual() {
        Task taskBeforePassing = taskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW);
        Epic epicBeforePassing = taskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtaskBeforePassing = taskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW);

        taskManager.createTask(taskBeforePassing);
        taskManager.createEpic(epicBeforePassing);
        taskManager.createSubtask(subtaskBeforePassing);

        Task taskAfterPassing = taskManager.returnTaskByID(0);
        Epic epicAfterPassing = taskManager.returnEpicByID(1);
        Subtask subtaskAfterPassing = taskManager.returnSubtaskByID(2);

        Assertions.assertEquals(taskBeforePassing.getName(), taskAfterPassing.getName());
        Assertions.assertEquals(taskBeforePassing.getDescription(), taskAfterPassing.getDescription());
        Assertions.assertEquals(taskBeforePassing.getId(), taskAfterPassing.getId());
        Assertions.assertEquals(taskBeforePassing.getStatus(), taskAfterPassing.getStatus());

        Assertions.assertEquals(epicBeforePassing.getName(), epicAfterPassing.getName());
        Assertions.assertEquals(epicBeforePassing.getDescription(), epicAfterPassing.getDescription());
        Assertions.assertEquals(epicBeforePassing.getId(), epicAfterPassing.getId());
        Assertions.assertEquals(epicBeforePassing.getStatus(), epicAfterPassing.getStatus());
        Assertions.assertEquals(epicBeforePassing.getSubtasks(), epicAfterPassing.getSubtasks());

        Assertions.assertEquals(subtaskBeforePassing.getName(), subtaskAfterPassing.getName());
        Assertions.assertEquals(subtaskBeforePassing.getDescription(), subtaskAfterPassing.getDescription());
        Assertions.assertEquals(subtaskBeforePassing.getId(), subtaskAfterPassing.getId());
        Assertions.assertEquals(subtaskBeforePassing.getStatus(), subtaskAfterPassing.getStatus());
        Assertions.assertEquals(subtaskBeforePassing.getEpicID(), subtaskAfterPassing.getEpicID());
    }

    @Test
    public void historyManagerShouldSavePreviousTaskStates() {

        taskManager.createTask(taskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        taskManager.createEpic(taskManager.formulateEpicForCreation("Epic1", "Description1"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1,"Subtask1",
                "Description1", Status.NEW));

        Task firstTaskReturn = taskManager.returnTaskByID(0);
        Epic firstEpicReturn = taskManager.returnEpicByID(1);
        Subtask firstSubtaskReturn = taskManager.returnSubtaskByID(2);

        taskManager.renewTask(new Task( "Task1Renewed",
                "Description1Renewed", 0, Status.IN_PROGRESS));
        taskManager.renewEpic(new Epic("Epic1Renewed", "Description1Renewed", 1));
        taskManager.renewSubtask(new Subtask(1 ,"Task1Renewed", "Renewed",
                2, Status.IN_PROGRESS));

        List<Task> taskHistory = taskManager.getHistory();

        Assertions.assertEquals(firstTaskReturn.getName(), taskHistory.get(2).getName());
        Assertions.assertEquals(firstEpicReturn.getName(), taskHistory.get(1).getName());
        Assertions.assertEquals(firstSubtaskReturn.getName(), taskHistory.get(0).getName());
    }

    @Test
    public void historyManagerShouldReturnCorrectListAfterAdditionOfTasksAndRemoval() {

        Task task = taskManager.formulateTaskForCreation("Task1", "Description1", Status.NEW);
        Epic epic = taskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtask = taskManager.formulateSubtaskForCreation(1,"Subtask1", "Description1",
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
    public void taskManagerShouldDeleteSubtasksFromEpicsThatAreNotUsed() {
        Epic epic = taskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtask1 = taskManager.formulateSubtaskForCreation(0,"Subtask1", "Description1",
                Status.NEW);
        Subtask subtask2 = taskManager.formulateSubtaskForCreation(0,"Subtask2", "Description2",
                Status.NEW);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskByID(1);
        Epic epicReturned = taskManager.returnEpicByID(0);

        Assertions.assertFalse(epicReturned.getSubtasks().contains(subtask1.getId()));
    }

    @Test
    public void tasksFieldsCanBeEditedViaSettersAndGetters() {
        Task task = taskManager.formulateTaskForCreation("Task1", "Description1", Status.NEW);
        Epic epic = taskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtask = taskManager.formulateSubtaskForCreation(1,"Subtask1", "Description1",
                Status.NEW);

        task.setStatus(Status.IN_PROGRESS);
        Assertions.assertNotEquals(Status.NEW, task.getStatus());

        epic.addSubtask(subtask.getId());
        epic.getSubtasks().add(1);
        Assertions.assertNotEquals(List.of(2), epic.getSubtasks());
    }
}