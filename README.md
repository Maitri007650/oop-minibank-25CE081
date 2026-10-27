# MiniBank

A simple banking application developed using Java as part of the Object Oriented Programming with Java (OOPJ) course.

## Features

- Create bank accounts
- Deposit and withdraw money
- Transfer money between accounts
- View account details and transaction history
- Save and load account data
- Generate bank reports
- Demonstrate multithreading and transaction processing
- Use Java annotations, reflection, generics, and exception handling

## Requirements

- Java JDK 21 or later
- Git (optional, for version control)

## Project Structure

The source code is organized into packages such as `model`, `service`, `util`, `exception`, and `concurrent`.

## How to Compile and Run

Open Git Bash in the project directory.

Compile all Java source files:

```bash
javac -d out $(find src -name "*.java")
```

Run the application:

```bash
java -cp out service.MiniBank
```

## Running the JAR

If `MiniBank.jar` is available and has been built with the correct main class, run:

```bash
java -jar MiniBank.jar
```

## Repository

GitHub: https://github.com/Maitri007650/oop-minibank-25CE081