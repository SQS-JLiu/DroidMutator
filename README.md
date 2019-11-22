# DroidMutator
A type checking based mutation tool:

Video Url: https://youtu.be/0WFAcuTXFZE

# The Overview of Project
### The prepare work:

    Please make sure that the build computer meets the following conditions:
    JDK 1.8.+
    Maven
    Android Studio (sdk and gradle)
    Python 2.7+
### Two ways to get tools：

#### 1. click to download [DroidMutator]( https://raw.github.com/SQS-JLiu/DroidMutator/master/muTool.zip ) image
    
#### 2. Build by yourself
    step 1. git clone https://github.com/SQS-JLiu/DroidMutator.git
    step 2. mvn clean
    step 3. mvn package
##### you will find DroidMutator file in project directory (./target/DroidMutator-1.0-SNAPSHOT-jar-with-dependencies.jar)
    The following files and DroidMutator-1.0-xxx.jar are in a working directory：
    muLocation.xml, mutator.xml, operators.config, \libs\*.jar, \builder, \launcher
   ![dir_tree](https://github.com/SQS-JLiu/DroidMutator/blob/master/readme/dir_tree.jpg)

### Two methods of mutant generation:
1. In the GUI select the files to be mutated and the mutation operators
   (The configuration file under the execution path is loaded by default.).

        java -jar DroidMutator.jar   
   ![gui](https://github.com/SQS-JLiu/DroidMutator/blob/master/readme/mutate_gui.png)
2. Pass in the custom muLocation.xml, mutator.xml or operators.config file.
   This will use all configuration operators to mutate all source files directly(Not display GUI).
    
        java -jar DroidMutator.jar [location_config=path/muLocation.xml] [project_config=path/mutator.xml] [operators_config=path/operators.config]
### Build each mutant as an installable file (APK file)
   The Builder needs to read the mutator.xml to get the mutant file and output the APK file to the specified location. By default, Builder will read the mutator.xml file in the directory structure described above. Of course, you can also specify a path parameter. The instructions are as follows: <br>
(Tips: You need to ensure that the environment can build applications using gradle.) <br>

        cd builder
        python compileAndroidPro.py [path2file/mutator.xml]
If the mutant is a stillborn mutant (i.e., code syntax error), the corresponding mutant will not be generated APK.
### Install and launch each APK file
   The launcher needs to read the mutator.xml to get the mutant APK file location. The reading method is the same as the above Builder. The instructions are as follows: <br>
   (Tips: You need to ensure that there is a simulated emulator or physical device connected to the computer before executing the command.)
        
        cd launcher
        python RunMutants.py  [path2file/mutator.xml]
  If the app-debug_app_crash_flag file identifier exists in the same directory of the mutant APK, 
  it means that the mutant is trivial mutant (i.e., those leading to crashes on app launch).
