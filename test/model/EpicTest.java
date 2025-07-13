package model;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

class EpicTest {

    @Test
    public void epicsShouldBeEqualIfID() {
        Epic epic1 = new Epic("Epic1", "Description1", 1);
        Epic epic2 = new Epic("Epic2", "Description2", 1);
        Assertions.assertEquals(epic1, epic2, "Задачи не равны друг другу");
    }
}