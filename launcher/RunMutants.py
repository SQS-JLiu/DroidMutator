#coding:utf-8
import sys,os
import logging
from DeviceHelper import get_available_devices
from Device import Device
from App.app import App
from ConfigReader import ConfigReader
import threading,time

class RunMutants(object):
    # this is a single instance class
    _instance_lock = threading.Lock()
    # How long we wait until we timeout on key dispatching.(Input event dispatching timed out)
    KEY_DISPATCHING_TIMEOUT = 5 # seconds

    def __init__(self,app_path=None,output_dir=None,singlemode=True,debug_mode=False):
        logging.basicConfig(level=logging.DEBUG if debug_mode else logging.INFO)
        self.logger = logging.getLogger(self.__class__.__name__)
        all_devices = get_available_devices()
        if len(all_devices) == 0:
            self.logger.warning("ERROR: No device connected.")
            sys.exit(-1)
        self.vtDevice =[]
        if singlemode:
            self.vtDevice.append(Device(all_devices[0]))
        else:
            for device in all_devices:
                self.vtDevice.append(Device(device))
        #self.app = App(app_path, output_dir=output_dir)
        self.lock = threading.Lock()
        self.androidProject = None
        self.mutantApkDir = None
        self.projectApkDir = None
        self.apkPaths = []
        self.apps = []
        self.mutation_home="."

    def __new__(cls, *args, **kwargs):
        if not hasattr(RunMutants, "_instance"):
            with RunMutants._instance_lock:
                if not hasattr(RunMutants, "_instance"):
                    RunMutants._instance = object.__new__(cls)
        return RunMutants._instance

    def start(self):
        settings = ConfigReader().readXML(sys.argv)
        self.androidProject = settings["project_name"]
        self.mutantApkDir = settings["apk_output"]
        self.mutation_home = settings["mutation_home"]
        self.projectApkDir = self.mutantApkDir + os.sep + self.androidProject
        self.getApkPaths(self.projectApkDir,self.apkPaths)
        self.initApps()
        #print "length: ",len(self.apkPaths)
        threads = []
        for device in self.vtDevice:
           task = threading.Thread(target=self.check_crash_app,args=(device,))
           task.start()
           threads.append(task)
        for t in threads:
            t.join()
        self.output_compilation_and_crash_report()

    def check_crash_app(self,device):
        device.set_up()
        device.connect()
        app = self.getOneApp()
        if app is not None:
            device.uninstall_app(app)
        while app is not None:
            device.install_app(app)
            device.start_app(app)
            time.sleep(RunMutants.KEY_DISPATCHING_TIMEOUT)
            # print "Activity Name: "+device.get_top_activity_name(),self.app.get_package_name()
            print "----------------------------------------------------"
            print "APK: ",app.app_path
            if device.app_is_running(app.get_package_name()):
                print " Launch app successfully... "
            else:
                # creating a file flag while apk file is crash
                open(str(app.app_path).replace(".apk", "_app_crash_flag"),"w").close()
                print "Launch app failed..."
            print "----------------------------------------------------"
            device.uninstall_app(app)
            app = self.getOneApp()
        self.stop()

    def initApps(self):
        self.apps = []
        for appPath in self.apkPaths:
          self.apps.append(appPath)

    def getOneApp(self):
        app = None
        if self.apps is not None:
            self.lock.acquire()
            if len(self.apps) > 0:
                app = self.apps.pop()
            self.lock.release()
        if app is None:
            return None
        return App(app)

    def stop(self):
        for device in self.vtDevice:
            device.disconnect()

    def getApkPaths(self,path,apkPaths):
        fileNameList = os.listdir(path)
        for fileName in fileNameList:
            filePath = path+os.sep+fileName
            if os.path.isdir(filePath):
                self.getApkPaths(filePath,apkPaths)
                continue
            if str(fileName).endswith(".apk"):
                apkPaths.append(filePath)

    def getCrashFlag(self,path,crashFlagList):
        fileNameList = os.listdir(path)
        for fileName in fileNameList:
            filePath = path+os.sep+fileName
            if os.path.isdir(filePath):
                self.getCrashFlag(filePath,crashFlagList)
                continue
            if str(fileName).endswith("_app_crash_flag"):
                crashFlagList.append(filePath)

    def output_compilation_and_crash_report(self):
        crashFlagList = []
        self.getCrashFlag(self.projectApkDir, crashFlagList)
        outputFileDir = self.mutation_home+os.sep+"reports";
        if not os.path.exists(outputFileDir):
            os.makedirs(outputFileDir)
        outputFile = open(outputFileDir+os.sep+self.androidProject+"_report.txt","a")
        print "----------------------------------------------------"
        outputFile.write("----------------------------------------------------"+os.linesep)
        print "Output Report("+time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())+"):"
        outputFile.write("Output Report("+time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())+"):" +os.linesep)
        print "----------------------------------------------------"
        outputFile.write("----------------------------------------------------"+os.linesep)
        mutantsCount,failedCount = self.get_compilation_results()
        print "Mutants Count: ",mutantsCount
        outputFile.write("Mutants Count: "+str(mutantsCount)+os.linesep)
        print "Compilation Success: ",mutantsCount-failedCount,"\tCompilation Failed: ",failedCount
        outputFile.write("Compilation Success: "+str(mutantsCount-failedCount)+"  Compilation Failed: "+str(failedCount)+os.linesep)
        print "APK File: ", len(self.apkPaths)
        outputFile.write("APK File: "+str(len(self.apkPaths))+os.linesep)
        success = len(self.apkPaths) - len(crashFlagList)
        failed = len(crashFlagList)
        print "Launch Success : ", success, "\tLaunch Failed: ", failed
        outputFile.write("Launch Success : "+str(success)+ "  Launch Failed: "+str(failed)+os.linesep)
        print "----------------------------------------------------"
        outputFile.write("----------------------------------------------------")
        outputFile.close()

    def get_compilation_results(self):
        javaFileCount = 0
        compilationFailedCount = 0
        vtClassName = os.listdir(self.projectApkDir)
        for className in vtClassName:
            classPath = self.projectApkDir + os.sep + className
            vtMethodName = os.listdir(classPath)
            for methodName in vtMethodName:
                methodPath = classPath + os.sep + methodName
                if os.path.isdir(methodPath):
                    vtMutantName = os.listdir(methodPath)
                    for mutantName in vtMutantName:
                        mutantPath = methodPath + os.sep + mutantName
                        vtFileName = os.listdir(mutantPath)
                        for fileName in vtFileName:
                            if fileName.endswith(".java"):
                                javaFileCount+=1
                                if len(vtFileName) == 1:
                                    compilationFailedCount+=1
        return javaFileCount,compilationFailedCount

if "__main__" == __name__:
    RunMutants().start()
    sys.exit(0)
