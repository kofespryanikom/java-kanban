public class Epic extends Task {

    Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }
    Epic(String name, String description) {
        super(name, description, Status.NEW);
    }
}
