# Chip-8 Emulator [![Build Status](https://travis-ci.org/nihas101/java-chip-8-emulator.svg?branch=master)](https://travis-ci.org/nihas101/java-chip-8-emulator)

This is an emulator for the interpreted programming language Chip-8 written in Java.

[![Click here]()](https://github.com/nihas101/java-chip-8-emulator/releases/latest)  for the latest release

## Requirements
**Java 1.8** is required to run this program.

## Build Standalone Distribution

To create a standalone distribution as a zip or tar file, run:

```sh
./gradlew distZip
```
or
```sh
./gradlew distTar
```

The distribution is placed under `build/distributions`.

## How to run the emulator

To run the program from the distribution, extract the zip or tar file, and run the launch script for your system in the bin folder by typing:
```sh
./chip-8
```
in the command-line interface.

## Manual

### Input
The Emulator expects a **US keyboard layout** and has the following key assignments:

| Chip-8 key    | Emulator key   |
| ------------- | -------------  |
| 0             | N              |
| 1             | Q              |
| 2             | W              |
| 3             | E              |
| 4             | A              |
| 5             | S              |
| 6             | D              |
| 7             | Z              |
| 8             | X              |
| 9             | C              |
| A             | B              |
| B             | M              |
| C             | R              |
| D             | F              |
| E             | V              |
| F             | ,              |

### Debug
This Emulator features a simple debug-mode and can be controlled with the following keys:

| Debug key      | Action                                     |
| -------------  | -----------------------------------------  |
| F1             | Opens/Closes the debug-window              |
| F2             | Activates/Deactivates step-by-step-mode    |
| F3             | Executes the next step (one CPU-cycle)     |
| F4             | Resets the CPU                             |

## Sources

The sources used to create this emulator:
* [www.codeslinger.co.uk/pages/projects/chip8.html](http://www.codeslinger.co.uk/pages/projects/chip8.html)
* [devernay.free.fr/hacks/chip8/C8TECH10.HTM](http://devernay.free.fr/hacks/chip8/C8TECH10.HTM)
