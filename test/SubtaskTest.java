import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    @Test
    public void subtasksShouldBeEqualIfID() {
        Subtask subtask1 = new Subtask(2,"Task1", "Description1", 1, Status.NEW);
        Subtask subtask2 = new Subtask(3,"Task2", "Description2", 1, Status.NEW);
        Assertions.assertEquals(subtask1, subtask2, "Задачи не равны друг другу");
    }
}