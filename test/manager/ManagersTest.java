package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class ManagersTest {

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
    public void backedTaskManagerShouldCreateBlankFileIfSaveMethodWasCalled() throws IOException {
        File backingFile = File.createTempFile("saved_tasks", ".csv");
        FileBackedTaskManager backedTaskManager = Managers.getBackedTaskManager(backingFile.toString());

        backedTaskManager.save();

        Assertions.assertEquals("id,type,name,status,description,epic,duration,startTime\n",
                Files.readString(backingFile.toPath()));
    }

    @Test
    public void backedTaskManagerShouldRecoverNoTasksFromBlankFile() throws IOException {
        File backingFile = File.createTempFile("saved_tasks", ".csv");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backingFile))) {
            bufferedWriter.write("id,type,name,status,description,epic,duration,startTime\n");
        }
        FileBackedTaskManager recoveredBackedTaskManager = Managers.getRecoveredBackedManager(backingFile.toString());
        List<Task> recoveredTaskList = recoveredBackedTaskManager.returnTasksList();
        List<Epic> recoveredEpicList = recoveredBackedTaskManager.returnEpicsList();
        List<Subtask> recoveredSubtaskList = recoveredBackedTaskManager.returnSubtasksList();

        Assertions.assertEquals(List.of(), recoveredTaskList);
        Assertions.assertEquals(List.of(), recoveredEpicList);
        Assertions.assertEquals(List.of(), recoveredSubtaskList);
    }

    @Test
    public void backedTaskManagerShouldCreateRecoverFileIfSaveMethodWasCalled() throws IOException {
        File backingFile = File.createTempFile("saved_tasks", ".csv");
        FileBackedTaskManager backedTaskManager = Managers.getBackedTaskManager(backingFile.toString());

        backedTaskManager.createTask(backedTaskManager.formulateTaskForCreation("Task1", "Description1",
                Status.NEW));
        backedTaskManager.createEpic(backedTaskManager.formulateEpicForCreation("Epic1", "Description1"));
        backedTaskManager.createSubtask(backedTaskManager.formulateSubtaskForCreation(1,"Task1",
                "Description1", Status.NEW));
        backedTaskManager.save();

        Assertions.assertEquals("id,type,name,status,description,epic,duration,startTime\n"
                + backedTaskManager.returnTaskByID(0).toString() + "\n"
                + backedTaskManager.returnEpicByID(1).toString() + "\n"
                + backedTaskManager.returnSubtaskByID(2).toString() + "\n", Files.readString(backingFile.toPath()));
    }

    @Test
    public void backedTaskManagerShouldRecoverTasksFromFile() throws IOException {
        File backingFile = File.createTempFile("saved_tasks", ".csv");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backingFile))) {
            bufferedWriter.write("id,type,name,status,description,epic,duration,startTime\n" +
                    "0,TASK,0,NEW,0,null,1000,23:14 05.12.1999\n" +
                    "1,EPIC,1,DONE,1,null,null,null\n" +
                    "2,SUBTASK,2,DONE,2,1,null,null\n");
        }
        FileBackedTaskManager recoveredBackedTaskManager = Managers.getRecoveredBackedManager(backingFile.toString());
        List<Task> recoveredTaskList = recoveredBackedTaskManager.returnTasksList();
        List<Epic> recoveredEpicList = recoveredBackedTaskManager.returnEpicsList();
        List<Subtask> recoveredSubtaskList = recoveredBackedTaskManager.returnSubtasksList();

        Assertions.assertEquals("[0,TASK,0,NEW,0,null,1000,23:14 05.12.1999]", recoveredTaskList.toString());
        Assertions.assertEquals("[1,EPIC,1,DONE,1,null,null,null]", recoveredEpicList.toString());
        Assertions.assertEquals("[2,SUBTASK,2,DONE,2,1,null,null]", recoveredSubtaskList.toString());
    }

    @Test
    public void backedTaskManagerShouldRecoverEpicsAndSubtasksFromFile() throws IOException {
        File backingFile = File.createTempFile("saved_tasks", ".csv");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backingFile))) {
            bufferedWriter.write("id,type,name,status,description,epic,duration,startTime\n" +
                    "1,EPIC,1,DONE,1,null,1000,23:14 05.12.1999\n" +
                    "2,SUBTASK,2,DONE,2,1,1000,23:14 05.12.1999\n");
        }
        FileBackedTaskManager recoveredBackedTaskManager = Managers.getRecoveredBackedManager(backingFile.toString());
        List<Epic> recoveredEpicList = recoveredBackedTaskManager.returnEpicsList();
        List<Subtask> recoveredSubtaskList = recoveredBackedTaskManager.returnSubtasksList();

        Assertions.assertEquals("[1,EPIC,1,DONE,1,null,1000,23:14 05.12.1999]", recoveredEpicList.toString());
        Assertions.assertEquals("[2,SUBTASK,2,DONE,2,1,1000,23:14 05.12.1999]", recoveredSubtaskList.toString());
    }

    @Test
    public void epicSubtasksMapShouldBeRecoveredFromFile() throws IOException {
        File backingFile = File.createTempFile("saved_tasks", ".csv");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backingFile))) {
            bufferedWriter.write("id,type,name,status,description,epic\n" +
                    "1,EPIC,1,DONE,1,null,null,null\n" +
                    "2,SUBTASK,2,DONE,2,1,null,null,null\n" +
                    "3,SUBTASK,3,DONE,3,1,null,null,null\n");
        }
        FileBackedTaskManager recoveredBackedTaskManager = Managers.getRecoveredBackedManager(backingFile.toString());
        Epic recoveredEpic = recoveredBackedTaskManager.returnEpicByID(1);

        Assertions.assertEquals("[2, 3]",
                recoveredEpic.getSubtasks().toString());
    }
}