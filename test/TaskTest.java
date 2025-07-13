import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    public void tasksShouldBeEqualIfID() {
        Task task1 = new Task("Task1", "Description1", 1, Status.NEW);
        Task task2 = new Task("Task2", "Description2", 1, Status.NEW);
        Assertions.assertEquals(task1, task2, "Задачи не равны друг другу");
    }
}