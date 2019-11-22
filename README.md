# DroidMutator
A type checking based mutation tool:

Video Url: https://youtu.be/0WFAcuTXFZE

Mutation operator description: [Operator description](https://github.com/SQS-JLiu/DroidMutator/blob/master/OperatorsDescription.md)

# The Overview of Project
### The prepare work:

    Please make sure that the build computer meets the following conditions:
    JDK 1.8.+
    Maven
    Android Studio (sdk and gradle)
    Python 2.7+
### Two ways to get tools：

#### 1. click to download [DroidMutator]( https://raw.github.com/SQS-JLiu/DroidMutator/master/muTool.zip ) image
The image archive contains executable programs and demo app. The main directory structure is as follows:
1. `DroidMutator.jar:` Mutant generation component    <br>
2. `\builder:` Used to package mutants into APK files   <br>
3. `\launcher:` Used to install the APK file and launch the app  <br>
4. `\libs:` The path to the libs folder, which contains dependencies for parsing Android features  <br>
5. `muLocation.xml:` Mutation scope profile   <br>
6. `mutator.xml:`  Configuring mutation generation, packaging, and output paths  <br>
7. `operators.config:` Used to configure mutation operators  <br>
   ![dir_tree](https://github.com/SQS-JLiu/DroidMutator/blob/master/readme/dir_tree.jpg)
    
#### 2. Build by yourself
    step 1. git clone https://github.com/SQS-JLiu/DroidMutator.git
    step 2. cd  DroidMutator
    step 3. mvn clean
    step 4. mvn package
##### The generated runnable jar can be found in: ./target/DroidMutator-1.0-SNAPSHOT-jar-with-dependencies.jar
    Combine the runable jar package with the files in the project to get the above directory structure.


### Two ways to generate mutants:
1. Graphical interface performs mutation operations. The user can select the file to be mutated and the mutation operator first, then click Generate to mutate, and the mutation result can be viewed through the Viewer.
   (The configuration file under the execution path is loaded by default.).

        java -jar DroidMutator.jar   
   ![gui](https://github.com/SQS-JLiu/DroidMutator/blob/master/readme/mutate_gui.png)
2. Command line interface performs mutation operations. It reads the muLocation.xml, mutator.xml or operators.config configuration file in the custom directory for mutation analysis. It uses all the mutation operators configured in operators.config to mutate all the code files in the source code directory.
    
        java -jar DroidMutator.jar [location_config=path2file/muLocation.xml] 
        [project_config=path2file/mutator.xml] [operators_config=path2file/operators.config]
***
### Build each mutant as an installable file (APK file)
   The Builder needs to read the mutator.xml to get the mutant file and output the APK file to the specified location. By default, Builder will read the mutator.xml file in the directory structure described above. Of course, you can also specify a path parameter. The instructions are as follows: <br>
(Tips: You need to ensure that the environment can build applications using gradle.) <br>

        cd builder
        python compileAndroidPro.py [path2file/mutator.xml]
If the mutant is a stillborn mutant (i.e., code syntax error), the corresponding mutant will not be generated APK.
***
### Install and launch each APK file
   The launcher needs to read the mutator.xml to get the mutant APK file location. The reading method is the same as the above Builder. The instructions are as follows: <br>
   (Tips: You need to ensure that there is a simulated emulator or physical device connected to the computer before executing the command.)
        
        cd launcher
        python RunMutants.py  [path2file/mutator.xml]
  If the app-debug_app_crash_flag file identifier exists in the same directory of the mutant APK, 
  it means that the mutant is trivial mutant (i.e., those leading to crashes on app launch).
