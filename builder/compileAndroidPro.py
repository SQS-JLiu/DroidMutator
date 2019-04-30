#!/usr/bin/env python
#coding:utf-8
import subprocess,time
import os,platform
import shutil,sys
from XMLHandler import XMLHandler

settings = {}
settings = XMLHandler().readXML(sys.argv)
androidProject = settings["project_name"]
androidSourcePath = settings["project_path"]
package_path = settings["package_path"]
mutantFileDir = settings["mutants_path"]
sourceDir = settings["build_path"]
mutantApkDir = settings["apk_output"]

srcFile_path = []
mutantFileMap = {}
mutant_java_class = []
passed_compilation_num = 0
failed_compilation_num = 0

def copyAndroidProject(androidpath,project):
    # copy mutated project  (you can copy project manually, that will be faster...)
    isPath = os.path.exists(sourceDir)
    if not isPath:
        os.makedirs(sourceDir)
    else:
        print "Directory : " + sourceDir+" is exists."
    if not os.path.exists(sourceDir + os.sep + project):
        # copy dir           [ copy file : shutil.copyfile('listfile.py', 'd:/test.py') ]
        print "Copying " + project + "......"
        shutil.copytree(androidpath + os.sep + project,sourceDir + os.sep + project)
    else:
        print "Project : " + project + " is exists."

def initBuildEnv():
    # create source file dir
    copyAndroidProject(androidSourcePath,androidProject)
    # create dir for generated apk
    if not os.path.exists(mutantApkDir):
        os.makedirs(mutantApkDir)
    else:
        print "Directory : " + mutantApkDir + " is exists."
    if not os.path.exists(mutantApkDir + os.sep + androidProject):
        os.makedirs(mutantApkDir + os.sep + androidProject)
    else:
        # shutil.rmtree(mutantApkDir + os.sep + androidProject)
        # os.makedirs(mutantApkDir + os.sep + androidProject)
        print "Directory : " + mutantApkDir + os.sep + androidProject + " is exists."

def list_mutant_dir(path,list_name):
    for tempfile in os.listdir(path):
        file_path = os.path.join(path, tempfile)
        if os.path.isdir(file_path):
            list_mutant_dir(file_path, list_name)
        else:
            if file_path.endswith('.java'):
                list_name.append(file_path)
                # print file_path

def list_src_dir(path,list_name):  #传入存储的list_name
    for tempfile in os.listdir(path):
        file_path = os.path.join(path, tempfile)
        if os.path.isdir(file_path):
            list_src_dir(file_path, list_name)
        else:
            #  xxx/src/xxx.java
            if file_path.find(os.sep+"src"+os.sep) > 0 and file_path.endswith('.java'):
                list_name.append(file_path)
                # print file_path

def createMutantDir():
    pwd = os.getcwd()
    apk_dir = mutantApkDir + os.sep + androidProject
    for classQualifiedName in os.listdir(mutantFileDir):
        file_path = os.path.join(mutantFileDir, classQualifiedName)
        if os.path.isdir(file_path):
            mutant_java_class.append(classQualifiedName)
            if len(os.listdir(apk_dir)) < len(os.listdir(mutantFileDir)):
                temp_dir = apk_dir + os.sep + classQualifiedName
                try:
                    shutil.copytree(os.path.join(file_path, "mutants"), temp_dir)
                except shutil.Error as err:
                    # Ignore 255bit limit for long path files in Windows
                    pass
        else:
            shutil.copy(file_path, apk_dir)
    os.chdir(pwd)
    # extract mutant file
    for qualifiedName in os.listdir(apk_dir):
        mutantFilePath = []
        list_mutant_dir(os.path.join(apk_dir,qualifiedName), mutantFilePath)
        for filePath in mutantFilePath:
            mutantFileMap[filePath] = qualifiedName.replace(".",os.sep)+".java"
    # obtain source file
    list_src_dir(sourceDir + os.sep + androidProject, srcFile_path)

def genMutantProject():
    srcfile = ""
    gradlewDir = getApkBuildDir(sourceDir + os.sep + androidProject)
    pwd = os.getcwd()
    # change dir to execute command
    os.chdir(gradlewDir)
    for mutantFile,qualifiedName in mutantFileMap.items():
        temp = str(mutantFile).split(os.sep)
        fileName = temp[len(temp)-1]
        apkFile = str(mutantFile).replace(fileName,"app-debug.apk")
        if os.path.exists(apkFile):
            continue
        print "*****mutant file : ", mutantFile
        bFlag = False
        for srcfile in srcFile_path:
            if  srcfile.endswith(qualifiedName):
                shutil.copy(mutantFile,srcfile)
                bFlag = True
                print "*****src file : ",srcfile
                break
        if bFlag is False:
            continue
        # execute "gradlew assembleDebug" to build apk
        result = run("gradlew assembleDebug")
        success = False
        # Linux
        # for res in result:
        #     if str(res).upper().find("BUILD SUCCESSFUL") > -1:
        #         success = True
        #         break
        # Windows
        if result.upper().find("BUILD SUCCESSFUL") > -1:
            success = True
        # move apk to mutant dir
        global passed_compilation_num,failed_compilation_num
        if success:
            passed_compilation_num = passed_compilation_num + 1
            moveApk2MutantDir(str(mutantFile).replace(fileName,"app-debug.apk"))
        else:
            failed_compilation_num = failed_compilation_num + 1
            print "Build apk failed !!!"
        # recover project file
        try:
            shutil.copy(package_path + os.sep + qualifiedName, srcfile)
        except shutil.Error:
            print "Recovering project file failed :",srcfile
    # recover dir
    time.sleep(5)
    os.chdir(pwd)

def moveApk2MutantDir(mutantApkPath):
    apk_list = []
    get_apk_list(sourceDir + os.sep + androidProject,apk_list)
    try:
        if len(apk_list) == 0:
            print "Warning , No Apk generated !!!"
        for apk_path in apk_list:
            # xxx/build/outputs/apk/xxx/xxx.apk
            if str(apk_path).find("build"+os.sep+"outputs"+os.sep+"apk") > -1:
                shutil.move(apk_path,mutantApkPath)
                print "** Move apk from: "+apk_path
    except:
        print "Apk already exists!!!"

def get_apk_list(path,list_name):
    for tempfile in os.listdir(path):
        file_path = os.path.join(path, tempfile)
        if os.path.isdir(file_path):
            get_apk_list(file_path, list_name)
        else:
            if file_path.endswith('.apk'):
                list_name.append(file_path)

def getApkBuildDir(pro_dir):
    gradlew_list = []
    get_gradlew_list(pro_dir,gradlew_list)
    length = len(gradlew_list[0])
    gradlew_path = gradlew_list[0]
    for gradlew in gradlew_list:
        if length > len(gradlew):
            length = len(gradlew)
            gradlew_path = gradlew
    return str(gradlew_path).replace(os.sep+"gradlew","")

def get_gradlew_list(path,list_name):
    for tempfile in os.listdir(path):
        file_path = os.path.join(path, tempfile)
        if os.path.isdir(file_path):
            get_gradlew_list(file_path, list_name)
        else:
            if file_path.endswith('gradlew'):
                list_name.append(file_path)

def testParams():
    print androidProject
    print androidSourcePath
    print mutantFileDir
    print sourceDir
    print mutantApkDir

def getMutantAndApkCount(path):
    # path = "E:\\mutatorHome\\experiments\\apk_output\\World-Weather"
    vtMutants = []
    vtAPKs = []
    list_mutant_dir(path, vtMutants)
    get_apk_list(path, vtAPKs)
    print  len(vtMutants)
    print  len(vtAPKs)
    return  vtMutants,vtAPKs

def run(cmd):
    ret = os.popen(cmd)
    result = ret.read()
    ret.close()
    return result

def run2(cmd):
    try:
        result = []
        if platform.system().upper() == "LINUX":
            proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, close_fds=True)
        elif platform.system().upper() == "WINDOWS":
            proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, close_fds=False)
        (stdout, stderr) = proc.communicate()
        output = stdout.split("\n")
        for line in output:
            if line == '':
                continue
            result.append(line)
        return str(result)
    except Exception as err:
        raise Exception("failed to execute command: %s, reason: %s" % (' '.join(cmd), err.message))

if __name__ == "__main__":
    print "Build starting..."
    initBuildEnv()
    createMutantDir()
    genMutantProject()
    if passed_compilation_num == 0 and failed_compilation_num == 0:
        print "*** Project %s was already built ***"%androidProject
    print  "Passed compilation mutants: ",passed_compilation_num, "\nFailed compilation mutants: ",failed_compilation_num
    print  "Build finished..."
    sys.exit(0)
    #testParams()
