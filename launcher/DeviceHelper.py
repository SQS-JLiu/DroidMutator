import subprocess
import os
import threading

def get_available_devices():
    """
    Get a list of device serials connected via adb
    :return: list of str, each str is a device serial number
    """
    r = subprocess.check_output(["adb", "devices"])
    if not isinstance(r, str):
        r = r.decode()
    devices = []
    for line in r.splitlines():
        segs = line.strip().split()
        if len(segs) == 2 and segs[1] == "device":
            devices.append(segs[0])
    return devices

def get_available_emulators():
    """
     Get a list of android virtual devices
     :return: list of str, each str is a device name
     """
    r = subprocess.check_output(["emulator", "-list-avds"])
    if not isinstance(r, str):
        r = r.decode()
    avds = []
    for line in r.splitlines():
        name = line.strip()
        if name != "":
            avds.append(name)
    return avds

def run_emulators(singlemode=True):
    avds = get_available_emulators()
    pwd = os.getcwd()
    # change dir to execute command
    ANDROID_HOME = "" ##  e.g. D:\\Android\\Sdk
    workdir = "."    ## e.g. workdir = "D:\\Android\\Sdk\\emulator"
    if "ANDROID_HOME" in os.environ:
        ANDROID_HOME = os.environ["ANDROID_HOME"]
    if ANDROID_HOME != "":
        workdir = ANDROID_HOME + os.path.sep + "emulator"
    os.chdir(workdir)
    if singlemode is True and len(avds) > 0:
        #results = subprocess.check_output(["emulator", "-avd "+avds[0]])
        #ret = os.popen("call ./scripts/start_emulator.bat " + avds[0])
        ret = os.popen("emulator -avd "+avds[0])
        results = ret.read(50)
        if results.find("HAX is working and emulator runs") > -1:
            print "AVD: "+avds[0]+" loading succeed !!!"
        ret.close()
    elif singlemode is False:
        for avd in avds:
            ret = os.popen("emulator -avd " + avd)
            results = ret.read(50)
            if results.find("HAX is working and emulator runs") > -1:
                print "AVD: " + avd + " loading succeed !!!"
            ret.close()
    # recover dir
    os.chdir(pwd)

def run_emulators_in_thread(singlemode=True):
    args = (singlemode,)
    run_thread = threading.Thread(target=run_emulators,args=args)
    run_thread.start()
    return True

if "__main__" == __name__:
    run_emulators_in_thread()
    # print get_available_emulators()
    # print get_available_devices()