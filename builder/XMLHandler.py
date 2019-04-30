#coding:utf-8
from xml.dom.minidom import parse
import xml.dom.minidom
import os

class XMLHandler:

    def __init__(self):
        self.xmlDir = os.path.abspath("..") + os.path.sep + "mutator.xml"
        #self.xmlDir = "E:\IdeaProjects\DroidMutator\mutator.xml"
        self.androidProject = ""
        self.androidSourcePath = ""
        self.mutantFileDir= ""
        self.sourceDir = ""
        self.mutantApkDir = ""
        self.settings = {}

    def readXML(self,argv):
        if len(argv) == 2:
            self.xmlDir = argv[1]
        DOMTree = xml.dom.minidom.parse(self.xmlDir)
        Settings = DOMTree.documentElement
        Mutation = Settings.getElementsByTagName("Mutation")
        Directory = Mutation[0].getElementsByTagName("Directory")
        for directory in Directory:
            if directory.hasAttribute("name"):
                self.settings[directory.getAttribute("name")] = directory.childNodes[0].data
        Builder = Settings.getElementsByTagName("Builder")
        Directory = Builder[0].getElementsByTagName("Directory")
        for directory in Directory:
            if directory.hasAttribute("name"):
                self.settings[directory.getAttribute("name")] = directory.childNodes[0].data
        #print self.settings
        return self.settings

# if __name__ == "__main__":
#     print XMLHandler().xmlDir
   #XMLHandler().readXML()
