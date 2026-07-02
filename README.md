# 6x6 Sudoku Project of the University of Valle

6x6 Sudoku with 2x3 blocks, developed with a graphical user interface, keyboard and mouse events. It features an attractive design for the user, a help system, real-time error checking on the board, and clear usage instructions.

This project was developed as part of the Fundamentals of Event-Oriented Programming course at Universidad del Valle.

## Main Features

- 6x6 Sudoku board.
- 2x3 internal blocks.
- Graphical user interface built with JavaFX.
- Keyboard and mouse event handling.
- Real-time validation of user inputs.
- Visual feedback for invalid numbers.
- Help system for suggesting possible values without solving the entire board.
- User-friendly visual design.
- Model View Controller architecture.
- Maven-based project structure.
- Git and GitHub version control.

## Technologies Used

- Java 17
- Amazon Corretto 17
- JavaFX
- Maven
- IntelliJ Idea
- Visual Studio Code
- Git and GitHub

## Project Architecture

This project follows the Model View Controller architectural pattern.

### Model

Contains the logic of the Sudoku game, including the board, cells, validation rules, and internal data structures.

### View

Contains the graphical interface elements, such as windows, styles, resources, and visual components.

### Controller

Connects the user interface with the game logic. It manages user actions such as starting a new game, selecting cells, entering numbers, asking for help, and validating the board.

## Building the Project

This project uses Maven as its build tool.

### Requirements

Before running the project, make sure you have installed:

- Java JDK 17, preferably Amazon Corretto 17.
- Maven installed and available in your system PATH.
- Visual Studio Code with the Extension Pack for Java.
- Git installed.

To verify your Java version, run:

```bash
java -version
```

To verify your Maven installation, run:

```bash
mvn -v
```

The project must be executed with Java 17 because the Maven configuration uses that version.

## Compile the Project

Run the following command from the project's root directory:

```bash
mvn clean compile
```

## Package the Project

To package the project, run:

```bash
mvn clean package
```

## Run the Project

To run the JavaFX application correctly, use the following command from the project's root directory:

```bash
mvn clean javafx:run
```

This is the recommended way to run the project.


## Authors

- Juan Pablo Lozano Restrepo - 2521505
- Daniel Fernando Vallejo Cabrera - 2343154

## Project Status

Academic project developed to implement a 6x6 Sudoku game with a graphical user interface, mouse and keyboard events, real-time validation, a limited help system, Model View Controller architecture, and GitHub-based version control.