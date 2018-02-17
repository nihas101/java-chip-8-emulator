# Chip-8 Emulator [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Build Status](https://travis-ci.org/nihas101/java-chip-8-emulator.svg?branch=master)](https://travis-ci.org/nihas101/java-chip-8-emulator) [![Maintainability](https://api.codeclimate.com/v1/badges/aa53a727391e465e281a/maintainability)](https://codeclimate.com/github/nihas101/java-chip-8-emulator/maintainability) [![codecov](https://codecov.io/gh/nihas101/java-chip-8-emulator/branch/master/graph/badge.svg)](https://codecov.io/gh/nihas101/java-chip-8-emulator)



This is an emulator for the interpreted programming language Chip-8 written in Java.

[![Click here]()](https://github.com/nihas101/java-chip-8-emulator/releases/latest)  for the latest release

## Requirements
**Java 1.8** is required to run this program.

## Features

- Load and play ROMs
- Reconfigure the controls
- Change the background and sprite color
- Adjust the speed of emulation
- Save and load states

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
./chip8
```
in the command-line interface.

## Manual

### Input
The Emulator expects a **US keyboard layout** and has the following standard key assignments:

| Chip-8 keys   | Keyboard keys   |
| ------------- | --------------  |
| 1 2 3 C       | 1 2 3 4         |
| 4 5 6 D       | Q W E R         |
| 7 8 9 E       | A S D F         |
| A 0 B F       | Z X C V         |

#### Reconfiguring keys

_TODO_

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
