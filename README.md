# Clara
Application written in Java that will predict the outcome of a chemical reaction given the reactants and reaction conditions. Includes a custom scripting language with JavaScript-like syntax for batch processing and programmable usage of the engine. Core of the prediction engine will utilize a neural network implementation of machine learning.

## Setting Up

1. Select an IDE or other text editor - [Eclipse](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/oxygen2) or [IntelliJ](https://www.jetbrains.com/idea/)
 are recommended, though it is certainly possible do do it all from a simple text editor and the command line.
2. Ensure both [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
and [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
are installed
3. Navigate to your project directory and clone this repository or your fork of this repository `git clone https://github.com/ScottNotFound/clara.git`
4. If using an IDE, import project as a gradle project
- For Eclipse:
    1. Start up eclipse
    2. Go to `File` > `Import...` then `Gradle` > `Gradle Project` and click `Next`
    3. Set the root directory to the cloned repository and click `Finish`
- For IntelliJ:
    1. Start up IntelliJ
    2. Go to `File` > `New` > `Project from Existing Sources...`
    3. Navigate to the directory of the cloned repository and select `build.gradle` and click `OK`
    4. Click `OK` again