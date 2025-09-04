package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

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

    @Test
    public void testManagerSaveException(){
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager("C::/file.csv");
        assertThrows(ManagerSaveException.class, fileBackedTaskManager::save,
                "Вызов метода save() может привести к исключению");
    }
}
