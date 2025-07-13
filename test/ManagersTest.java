import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ManagersTest {
    TaskManager taskManager;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    public void managersCreations() {
        taskManager = Managers.getDefault();
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
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW));

        Task firstTaskReturn = taskManager.returnTaskByID(0);
        Epic firstEpicReturn = taskManager.returnEpicByID(1);
        Subtask firstSubtaskReturn = taskManager.returnSubtaskByID(2);

        taskManager.createTask(taskManager.formulateTaskForCreation("Task1Renewed",
                "Description1Renewed", Status.IN_PROGRESS));
        taskManager.createEpic(taskManager.formulateEpicForCreation("Epic1Renewed",
                "Description1Renewed"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1,"Task1Renewed",
                "Renewed", Status.IN_PROGRESS));

        Task secondTaskReturn = taskManager.returnTaskByID(0);
        Epic secondEpicReturn = taskManager.returnEpicByID(1);
        Subtask secondSubtaskReturn = taskManager.returnSubtaskByID(2);

        ArrayList<Task> taskHistory = taskManager.getHistory();
        Assertions.assertEquals(firstSubtaskReturn.getName(), taskHistory.get(0).getName());
        Assertions.assertEquals(firstEpicReturn.getName(), taskHistory.get(1).getName());
        Assertions.assertEquals(firstSubtaskReturn.getName(), taskHistory.get(2).getName());
        Assertions.assertEquals(secondTaskReturn.getName(), taskHistory.get(3).getName());
        Assertions.assertEquals(secondEpicReturn.getName(), taskHistory.get(4).getName());
        Assertions.assertEquals(secondSubtaskReturn.getName(), taskHistory.get(5).getName());
    }
}