# DroidMutator
Mutation testing tool

#### 1.click download [DroidMutator]( https://raw.github.com/SQS-JLiu/DroidMutator/master/muTool.zip )
    
#### 2. git clone https://github.com/SQS-JLiu/DroidMutator.git
    mvn clean
    mvn package
    E:\IdeaProjects\MutationTest\DroidMutator\target\DroidMutator-1.0-SNAPSHOT-jar-with-dependencies.jar
    muLocation.xml
    mutator.xml
    operators.config
    \libs\*.jar
    \builder
    \launcher

    java -jar DroidMutator.jar project_config=xxx
    [location_config=xxx] [project_config=xxx] [operators_config=xxx..]
    python compileAndroidPro.py [xxx/mutator.xml]
    python RunMutants.py [xxx/mutator.xml]

    app-debug.apk
    app-debug_app_crash_flag
