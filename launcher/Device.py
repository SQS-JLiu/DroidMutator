import logging
import sys,os,time
import subprocess
import re
from App.app import App
from App.intent import Intent
from adb import ADB

class Device(object):

    def __init__(self,device=None):
        self.logger = logging.getLogger(self.__class__.__name__)
        if device is None:
            from .DeviceHelper import get_available_devices
            all_devices = get_available_devices()
            if len(all_devices) == 0:
                self.logger.warning("ERROR: No device connected.")
                sys.exit(-1)
            device = all_devices[0]
        if "emulator" in device:
            self.is_emulator = True
        else:
            self.is_emulator = False
        self.device = device

        self.grant_perm = False ## Grant all permissions while installing. Useful for Android 6.0+

        # basic device information
        self.display_info = None
        self.sdk_version = None
        self.release_version = None
        self.ro_debuggable = None
        self.ro_secure = None
        self.connected = True

        # adapters
        self.adb = ADB(device=self)

        self.adapters = {
            self.adb: True,
            # self.telnet: False,
        }

    def set_up(self):
        """
         Set connections on this device
         """
        # wait for emulator to start
        self.wait_for_device()
        for adapter in self.adapters:
            adapter_enabled = self.adapters[adapter]
            if not adapter_enabled:
                continue
            adapter.set_up()


    def connect(self):
        """
             establish connections on this device
             :return:
             """
        for adapter in self.adapters:
            adapter_enabled = self.adapters[adapter]
            if not adapter_enabled:
                continue
            adapter.connect()

        self.get_sdk_version()
        self.get_release_version()
        self.get_ro_secure()
        self.get_ro_debuggable()
        self.get_display_info()

        self.unlock()
        self.check_connectivity()
        self.connected = True

    def disconnect(self):
        """
       disconnect current device
       :return:
       """
        self.connected = False
        for adapter in self.adapters:
            adapter_enabled = self.adapters[adapter]
            if not adapter_enabled:
                continue
            adapter.disconnect()

    def tear_down(self):
        for adapter in self.adapters:
            adapter_enabled = self.adapters[adapter]
            if not adapter_enabled:
                continue
            adapter.tear_down()

    def install_app(self,app):
        """
        install an app to device
        @param app: instance of App
        @return:
        """
        assert isinstance(app, App)
        # subprocess.check_call(["adb", "-s", self.serial, "uninstall", app.get_package_name()],
        #                       stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        package_name = app.get_package_name()
        if package_name not in self.adb.get_installed_apps():
            install_cmd = ["adb", "-s", self.device, "install", "-r"]
            if self.grant_perm and self.get_sdk_version() >= 23:
                install_cmd.append("-g")
            install_cmd.append(app.app_path)
            install_p = subprocess.Popen(install_cmd, stdout=subprocess.PIPE)
            count = 0
            while self.connected and package_name not in self.adb.get_installed_apps():
                print("Please wait while installing the app...")
                count+=1
                if count > 30:
                    break
                time.sleep(2)
            if not self.connected:
                install_p.terminate()
                return
            install_p.terminate()
        # dumpsys_p = subprocess.Popen(["adb", "-s", self.serial, "shell",
        #                               "dumpsys", "package", package_name], stdout=subprocess.PIPE)
        # dumpsys_lines = []
        # while True:
        #     line = dumpsys_p.stdout.readline()
        #     if not line:
        #         break
        #     if not isinstance(line, str):
        #         line = line.decode()
        #     dumpsys_lines.append(line)
        # if self.output_dir is not None:
        #     package_info_file_name = "%s/dumpsys_package_%s.txt" % (self.output_dir, app.get_package_name())
        #     package_info_file = open(package_info_file_name, "w")
        #     package_info_file.writelines(dumpsys_lines)
        #     package_info_file.close()
        # app.dumpsys_main_activity = self.__parse_main_activity_from_dumpsys_lines(dumpsys_lines)

        self.logger.info("App installed: %s" % package_name)
        self.logger.info("Main activity: %s" % app.get_main_activity())

    def uninstall_app(self,app):
        """
        Uninstall an app from device.
        :param app: an instance of App or a package name
        """
        if isinstance(app, App):
            package_name = app.get_package_name()
        else:
            package_name = app
        if package_name in self.adb.get_installed_apps():
            uninstall_cmd = ["adb", "-s", self.device, "uninstall", package_name]
            uninstall_p = subprocess.Popen(uninstall_cmd, stdout=subprocess.PIPE)
            while package_name in self.adb.get_installed_apps():
                print("Please wait while uninstalling the app...")
                time.sleep(2)
            uninstall_p.terminate()

    def start_app(self,app):
        """
       start an app on the device
       :param app: instance of App, or str of package name
       :return:
       """
        if isinstance(app, str):
            package_name = app
        elif isinstance(app, App):
            package_name = app.get_package_name()
            if app.get_main_activity():
                package_name += "/%s" % app.get_main_activity()
        else:
            self.logger.warning("unsupported param " + app + " with type: ", type(app))
            return
        intent = Intent(suffix=package_name)
        self.send_intent(intent)

    def pull_file(self, remote_file, local_file):
        self.adb.run_cmd(["pull", remote_file, local_file])

    def push_file(self,local_file, remote_dir="/sdcard/"):
        """
       push file/directory to target_dir
       :param local_file: path to file/directory in host machine
       :param remote_dir: path to target directory in device
       :return:
       """
        if not os.path.exists(local_file):
            self.logger.warning("push_file file does not exist: %s" % local_file)
        self.adb.run_cmd(["push", local_file, remote_dir])

    def shutdown(self):
        self.adb.shell("reboot -p")

    def wait_for_device(self):
        """
        wait until the device is fully booted
        :return:
        """
        self.logger.info("waiting for device")
        try:
            subprocess.check_call(["adb", "-s", self.device, "wait-for-device"])
        except:
            self.logger.warning("error waiting for device")

    def get_sdk_version(self):
        """
        Get version of current SDK
        """
        if self.sdk_version is None:
            self.sdk_version = self.adb.get_sdk_version()
        return self.sdk_version

    def get_release_version(self):
        """
        Get version of current SDK
        """
        if self.release_version is None:
            self.release_version = self.adb.get_release_version()
        return self.release_version

    def get_ro_secure(self):
        if self.ro_secure is None:
            self.ro_secure = self.adb.get_ro_secure()
        return self.ro_secure

    def get_ro_debuggable(self):
        if self.ro_debuggable is None:
            self.ro_debuggable = self.adb.get_ro_debuggable()
        return self.ro_debuggable

    def get_display_info(self, refresh=True):
        """
        get device display information, including width, height, and density
        :param refresh: if set to True, refresh the display info instead of using the old values
        :return: dict, display_info
        """
        if self.display_info is None or refresh:
            self.display_info = self.adb.get_display_info()
        return self.display_info

    def unlock(self):
        """
        unlock screen
        skip first-use tutorials
        etc
        :return:
        """
        self.adb.unlock()

    def check_connectivity(self):
        """
        check if the device is available
        """
        for adapter in self.adapters:
            adapter_name = adapter.__class__.__name__
            adapter_enabled = self.adapters[adapter]
            if not adapter_enabled:
                print("[CONNECTION] %s is not enabled." % adapter_name)
            else:
                if adapter.check_connectivity():
                    print("[CONNECTION] %s is enabled and connected." % adapter_name)
                else:
                    print("[CONNECTION] %s is enabled but not connected." % adapter_name)

    def send_intent(self, intent):
        """
        send an intent to device via am (ActivityManager)
        :param intent: instance of Intent or str
        :return:
        """
        assert self.adb is not None
        assert intent is not None
        if isinstance(intent, Intent):
            cmd = intent.get_cmd()
        else:
            cmd = intent
        return self.adb.shell(cmd)

    def start_activity_via_monkey(self, package):
        """
        use monkey to start activity
        @param package: package name of target activity
        """
        cmd = 'monkey'
        if package:
            cmd += ' -p %s' % package
        out = self.adb.shell(cmd)
        if re.search(r"(Error)|(Cannot find 'App')", out, re.IGNORECASE | re.MULTILINE):
            raise RuntimeError(out)

    def get_package_path(self, package_name):
        """
        get installation path of a package (app)
        :param package_name:
        :return: package path of app in device
        """
        dat = self.adb.shell('pm path %s' % package_name)
        package_path_re = re.compile('^package:(.+)$')
        m = package_path_re.match(dat)
        if m:
            path = m.group(1)
            return path.strip()
        return None

    def app_is_running(self,packageName):
        """
        Get app's pid information
         USER      PID   PPID  VSIZE  RSS   WCHAN              PC  NAME
         u0_a1104  10881 870   1646504 57404 SyS_epoll_ 0000000000 S com.phikal.regex
        :return:
        """
        cmd = "adb -s "+self.device+ " shell \"ps|grep "+packageName+"\""
        proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        (stdout, stderr) = proc.communicate()
        vtLine = stdout.split(os.linesep)
        if len(vtLine) > 1:
            for line in vtLine:
                vtLine = line.strip().split()
                if len(vtLine)  == 9 and vtLine[8] == packageName:
                    return True
        proc.terminate()
        return False

    def get_top_activity_name(self):
        """
        Get current activity
        """
        r = self.adb.shell("dumpsys activity activities")
        activity_line_re = re.compile('\* Hist #\d+: ActivityRecord{[^ ]+ [^ ]+ ([^ ]+) t(\d+)}')
        m = activity_line_re.search(r)
        if m:
            return m.group(1).split('/')[0]
            # return m.group(1).replace("/","")
        # data = self.adb.shell("dumpsys activity top").splitlines()
        # regex = re.compile("\s*ACTIVITY ([A-Za-z0-9_.]+)/([A-Za-z0-9_.]+)")
        # m = regex.search(data[1])
        # if m:
        #     return m.group(1) + "/" + m.group(2)
        self.logger.warning("Unable to get top activity name.")
        return None

    @staticmethod
    def __parse_main_activity_from_dumpsys_lines(lines):
        main_activity = None
        activity_line_re = re.compile("[^ ]+ ([^ ]+)/([^ ]+) filter [^ ]+")
        action_re = re.compile("Action: \"([^ ]+)\"")
        category_re = re.compile("Category: \"([^ ]+)\"")

        activities = {}

        cur_package = None
        cur_activity = None
        cur_actions = []
        cur_categories = []

        for line in lines:
            line = line.strip()
            m = activity_line_re.match(line)
            if m:
                activities[cur_activity] = {
                    "actions": cur_actions,
                    "categories": cur_categories
                }
                cur_package = m.group(1)
                cur_activity = m.group(2)
                if cur_activity.startswith("."):
                    cur_activity = cur_package + cur_activity
                cur_actions = []
                cur_categories = []
            else:
                m1 = action_re.match(line)
                if m1:
                    cur_actions.append(m1.group(1))
                else:
                    m2 = category_re.match(line)
                    if m2:
                        cur_categories.append(m2.group(1))

        if cur_activity is not None:
            activities[cur_activity] = {
                "actions": cur_actions,
                "categories": cur_categories
            }

        for activity in activities:
            if "android.intent.action.MAIN" in activities[activity]["actions"] \
                    and "android.intent.category.LAUNCHER" in activities[activity]["categories"]:
                main_activity = activity
        return main_activity