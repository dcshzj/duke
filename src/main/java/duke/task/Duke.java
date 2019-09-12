package duke.task;

import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import duke.task.DukeException;
import duke.task.Parser;
import duke.task.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.Ui;

public class Duke extends Application {
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    private boolean toBreak;

    /**
     * Constructor function.
     * @param filePath The path to the file for tasks storage.
     */
    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        toBreak = false;

        try {
            tasks = new TaskList(storage.load());
        } catch (DukeException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Overloaded constructor function for JavaFX.
     */
    public Duke() {

    }

    /**
     * The main function to execute the program.
     */
    public void run() {
        Scanner sc = new Scanner(System.in);
        this.ui.printHello();

        while (sc.hasNext()) {
            String next = sc.nextLine();

            try {
                this.handleInput(next);
            } catch (DukeException e) {
                Ui.printError(e.getMessage());
            }

            if (this.toBreak) {
                break;
            }
        }

        sc.close();
    }

    /**
     * The main function to execute the program in GUI mode.
     * @param stage The stage to display content in.
     */
    @Override
    public void start(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        sendButton.setOnMouseClicked((event) -> {
            dialogContainer.getChildren().add(getDialogLabel(userInput.getText()));
            userInput.clear();
        });

        userInput.setOnAction((event) -> {
            dialogContainer.getChildren().add(getDialogLabel(userInput.getText()));
            userInput.clear();
        });

        // Scroll down to the end every time dialogContainer's height changes
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        // sendButton.setOnMouseClicked((event) -> {
        //     handleUserInput();
        // });

        // userInput.setOnAction((event) -> {
        //     handleUserInput();
        // });
    }

    /**
     * Iteration 1:
     * Creates a label with the specified text and adds it to the dialog container.
     * @param text String containing text to add
     * @return Label with the specified text that has word wrap enabled.
     */
    private Label getDialogLabel(String text) {
        Label textToAdd = new Label(text);
        textToAdd.setWrapText(true);

        return textToAdd;
    }

    /**
     * This function retrieves the response from Duke based on the user's input.
     * @param input The user's input from the chatbot.
     * @return String with the response text.
     */
    public String getResponse(String input) {
        return "Duke heard: " + input;
    }

    /**
     * The main entry point to the program.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Duke("../data/tasks.txt").run();
    }

    /**
     * This function does basic handling of the user's input and pass on the arguments to Parser.
     * @param next The next input by the user.
     * @throws DukeException When a program-specific exception has occurred.
     */
    public void handleInput(String next) throws DukeException {
        if (next.equals("list")) {
            System.out.print(Parser.handleList(this.tasks));
        } else if (next.startsWith("done")) {
            Task task = Parser.handleDone(this.tasks, next);
            this.ui.printDone(task);
            this.storage.save(this.tasks);
        } else if (next.startsWith("delete")) {
            Task task = Parser.handleDelete(this.tasks, next);
            this.ui.printDelete(task, this.tasks.getTasks().size());
            this.storage.save(this.tasks);
        } else if (next.startsWith("find")) {
            this.ui.printFind(Parser.handleFind(this.tasks, next));
        } else if (next.equals("bye")) {
            this.toBreak = true;
            this.ui.printBye();
        } else {
            Task task = Parser.handleItem(tasks, next);
            this.ui.printAdd(task, (this.tasks.getTasks().size()));
            this.storage.save(this.tasks);
        }
    }
}
