# DroidMutator
A mutation testing tool:

Video Url: https://youtu.be/xxx

# The Overview of Project
### The prepare work:

    Please make sure that the build computer meets the following conditions:
    JDK 1.8.+
    Maven
    Android Studio (sdk and gradle)
    Python 2.7+
### Two ways to get tools：

#### 1.click to download [DroidMutator]( https://raw.github.com/SQS-JLiu/DroidMutator/master/muTool.zip )
    
#### 2. Build by yourself
    step 1. git clone https://github.com/SQS-JLiu/DroidMutator.git
    step 2. mvn clean
    step 3. mvn package
    you will find DroidMutator file in project directory (./target/DroidMutator-1.0-SNAPSHOT-jar-with-dependencies.jar),
    The following files and DroidMutator-1.0-xxx.jar are in a working directory：
    muLocation.xml, mutator.xml, operators.config, \libs\*.jar, \builder, \launcher
    
    java -jar DroidMutator.jar project_config=xxx
    [location_config=xxx] [project_config=xxx] [operators_config=xxx..]
    python compileAndroidPro.py [xxx/mutator.xml]
    python RunMutants.py [xxx/mutator.xml]

    app-debug.apk
    app-debug_app_crash_flag
