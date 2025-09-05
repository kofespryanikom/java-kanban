package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

    private TaskManager inMemoryTaskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void managersCreations() {
        inMemoryTaskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void managerShouldReturnWorkingTaskManager() {
        Assertions.assertTrue(inMemoryTaskManager instanceof InMemoryTaskManager);
        Assertions.assertTrue(historyManager instanceof InMemoryHistoryManager);
    }

    @Test
    public void managerShouldBeAbleAddCorrectTaskTypesAndFindTasksByID() {

        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW));

        Assertions.assertTrue(inMemoryTaskManager.returnTaskByID(0) instanceof Task);
        Assertions.assertTrue(inMemoryTaskManager.returnEpicByID(1) instanceof Epic);
        Assertions.assertTrue(inMemoryTaskManager.returnSubtaskByID(2) instanceof Subtask);
    }

    @Test
    public void managerShouldGenerateSameIDasMeant() {
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        Assertions.assertEquals(0, inMemoryTaskManager.getIdCounter());
        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1"));
        Assertions.assertEquals(1, inMemoryTaskManager.getIdCounter());
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW));
        Assertions.assertEquals(2, inMemoryTaskManager.getIdCounter());
    }

    @Test
    public void taskPassedAndTaskReturnedShouldBeEqual() {
        Task taskBeforePassing = inMemoryTaskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW);
        Epic epicBeforePassing = inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtaskBeforePassing = inMemoryTaskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW);

        inMemoryTaskManager.createTask(taskBeforePassing);
        inMemoryTaskManager.createEpic(epicBeforePassing);
        inMemoryTaskManager.createSubtask(subtaskBeforePassing);

        Task taskAfterPassing = inMemoryTaskManager.returnTaskByID(0);
        Epic epicAfterPassing = inMemoryTaskManager.returnEpicByID(1);
        Subtask subtaskAfterPassing = inMemoryTaskManager.returnSubtaskByID(2);

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
    public void taskManagerShouldDeleteSubtasksFromEpicsThatAreNotUsed() {
        Epic epic = inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtask1 = inMemoryTaskManager.formulateSubtaskForCreation(0,"Subtask1", "Description1",
                Status.NEW);
        Subtask subtask2 = inMemoryTaskManager.formulateSubtaskForCreation(0,"Subtask2", "Description2",
                Status.NEW);
        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.deleteSubtaskByID(1);
        Epic epicReturned = inMemoryTaskManager.returnEpicByID(0);

        Assertions.assertFalse(epicReturned.getSubtasks().contains(subtask1.getId()));
    }

    @Test
    public void tasksFieldsCanBeEditedViaSettersAndGetters() {
        Task task = inMemoryTaskManager.formulateTaskForCreation("Task1", "Description1", Status.NEW);
        Epic epic = inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1");
        Subtask subtask = inMemoryTaskManager.formulateSubtaskForCreation(1,"Subtask1", "Description1",
                Status.NEW);

        task.setStatus(Status.IN_PROGRESS);
        Assertions.assertNotEquals(Status.NEW, task.getStatus());

        epic.addSubtask(subtask.getId());
        epic.getSubtasks().add(1);
        Assertions.assertNotEquals(List.of(2), epic.getSubtasks());
    }

    @Test
    public void taskManagerShouldReturnCorrectPrioritizedTaskList() {
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "5", "23:00 01.01.2000"));
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "10", "23:30 01.01.2000"));
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "30", "21:09 01.01.2000"));
        Assertions.assertEquals("[2,TASK,0,NEW,0,null,30,21:09 01.01.2000, " +
                "0,TASK,0,NEW,0,null,5,23:00 01.01.2000, 1,TASK,0,NEW,0,null,10,23:30 01.01.2000]",
                inMemoryTaskManager.getPrioritizedTasks().toString());
    }

    @Test
    public void calculationOfStatusOfEpicShouldBeRightWithBoundaryConditions() {
        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(0,"Subtask1",
                "Description1", Status.NEW));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(0,"Subtask2",
                "Description2", Status.NEW));

        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic2", "Description2"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(3,"Subtask3",
                "Description3", Status.DONE));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(3,"Subtask4",
                "Description4", Status.DONE));

        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic3", "Description3"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(6,"Subtask5",
                "Description5", Status.NEW));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(6,"Subtask6",
                "Description6", Status.DONE));

        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic4", "Description4"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(9,"Subtask7",
                "Description1", Status.NEW));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(9,"Subtask8",
                "Description1", Status.IN_PROGRESS));

        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic5", "Description5"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(12,"Subtask9",
                "Description1", Status.IN_PROGRESS));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(12,"Subtask10",
                "Description1", Status.IN_PROGRESS));

        Assertions.assertEquals(Status.NEW, inMemoryTaskManager.returnEpicByID(0).getStatus());
        Assertions.assertEquals(Status.DONE, inMemoryTaskManager.returnEpicByID(3).getStatus());
        Assertions.assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.returnEpicByID(6).getStatus());
        Assertions.assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.returnEpicByID(9).getStatus());
        Assertions.assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.returnEpicByID(12).getStatus());
    }

    @Test
    public void subtasksShouldHaveCorrectEpicId() {
        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic1", "Description1"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(0,"Subtask1",
                "Description1", Status.NEW));
        inMemoryTaskManager.createEpic(inMemoryTaskManager.formulateEpicForCreation("Epic2", "Description2"));
        inMemoryTaskManager.createSubtask(inMemoryTaskManager.formulateSubtaskForCreation(2,"Subtask2",
                "Description2", Status.NEW));

        Assertions.assertEquals(0, inMemoryTaskManager.returnSubtaskByID(1).getEpicID());
        Assertions.assertEquals(2, inMemoryTaskManager.returnSubtaskByID(3).getEpicID());
    }

    @Test
    public void taskManagerShouldCalculateDateAndTimeIntersectionsCorrectly() {
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "60", "22:30 01.01.2000"));
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "60", "23:00 01.01.2000"));
        inMemoryTaskManager.createTask(inMemoryTaskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "70", "21:09 01.01.2000"));

        Assertions.assertEquals("[0,TASK,0,NEW,0,null,60,22:30 01.01.2000, 2,TASK,0,NEW,0,null,70,21:09 01.01.2000]",
                inMemoryTaskManager.returnTasksList().toString());
    }

}