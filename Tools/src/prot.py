import sys
import httplib
from optparse import OptionParser
import os
import zipfile
import yaml

class InvalidDirectory(Exception):
    def __init__(self, value):
        self.value = value
    
    def __str__(self):
        return repr(self.value)

class InvalidApplication(Exception):
    def __init__(self, value):
        self.value = value
    
    def __str__(self):
        return repr(self.value)
    

class InvalidProjectType(Exception):
    def __init__(self, value):
        self.value = value
    
    def __str__(self):
        return repr(self.value)

def parseAppYaml(pathToYaml):
    file = open(pathToYaml, "r")
    result = yaml.load(file)
    return result; 
 
 
 
SERVER = 'localhost:5050'
def upload(warFile, appId):
    # Reading (r) a binary file (b)
    file = open(warFile, mode='rb')
    
    # Create a new connection and send the request (file)
    con = httplib.HTTPConnection(SERVER)
    print "Uploading..."
    response = con.request('POST', '/app/' + appId, file)
    print "Done"


def testApp(directory):
    required = [
                "/WEB-INF",
                "/app.yaml"
                ]
    
    for test in required:
        testDir = directory + test
        if not os.path.exists(directory + test):
            raise InvalidApplication("Missing application directory: %s" % testDir);

    # Parse the YAML-File
    yaml = parseAppYaml(directory + "/app.yaml")
    print "App-Configuration: %s" % yaml
    
    return yaml 


def deploy(directory):
    if not os.path.isdir(directory):
        raise InvalidDirectory("Destination path is not a directory")

    # Is the directory an application directory?
    yaml = testApp(directory)

    zipFile = directory + "/.deploy"
    if os.path.exists(zipFile):
        print "deleting old deployment"
        os.remove(zipFile)
    
    zipFile = directory + "/.deploy"
    zip = zipfile.ZipFile(zipFile, "w")
    
    # Iterate over all files
    for root, dirs, files in os.walk(directory):
        # Get the relatve directory
        relRoot = root.replace(directory, "")
        print "adding files from: %s" % relRoot

        # Add the directory to the zip file
        if relRoot is not "":
            zip.write(root, relRoot)
            
        # Iterate over all files in the directory
        for file in files:
            # Check if this is an invalid file
            if file.find(".deploy") >= 0:
                print "   skipping: %s" % file
                continue
            
            # Add the file to the zip file
            print "   adding file: %s" % file
            zip.write(root + "/" + file, relRoot + "/" + file)
    
    zip.close()
    
    # Upload the zip-file to the Web-Server
    appId = yaml["appId"]
    upload(zipFile ,appId)



def createProject(directory, projectType, projectName):
    if not os.path.isdir(directory):
        raise InvalidDirectory("Destination path is not a directory")  
    
    # Create the application folder
    directory += "/" + projectName
    if os.path.exists(directory):
        raise InvalidDirectory("Directory already exists")
    
    os.mkdir(directory)        
    
    # Create the folder structure
    if projectType == "java":
        os.mkdir(directory + "/WEB-INF")
        os.mkdir(directory + "/WEB-INF/lib")
        os.mkdir(directory + "/WEB-INF/classes")
    elif projectType == "python":
        os.mkdir(directory + "/WEB-INF")
        os.mkdir(directory + "/WEB-INF/python")
        os.mkdir(directory + "/WEB-INF/python/" + projectName)
    else:
        raise InvalidProjectType("Valid project types are java, python")
    
    # Create the YAML-file
    yaml = ["appId: %s \n" % projectName,
            "runtime: %s \n" % projectType,
            "\n"
            ]
    
    # Python-Specific YAML-content
    if projectType == "python":
        yaml += ["handlers: \n",
            "- refUrl: /.* \n",
            "  file: %s/TODO.py" % projectName
            ]
    
    file = open(directory + "/app.yaml", "w")
    file.writelines(yaml)
    file.close; 
    

def main(args):
    parser = OptionParser()
    parser.add_option("--dir", action="store", type="string", )
    parser.add_option("--createProject", action="store", dest="projectName")
    parser.add_option("--type", action="store", type="string", dest="projectType")
    parser.add_option("--deploy", action="store_true", dest="deploy")
    
    # Parse the command line 
    (options, args) = parser.parse_args(args)
    
    # Check for command line errors
    if options.projectName and not options.projectType:
        parser.error("--createProject requires --type")
    
    if options.dir:
        if not os.path.isdir(options.dir):
            parser.error(options.dir + " is not a directory")
    else:
        options.dir = os.getcwd()
    
    # Call functions
    if options.projectName:
        createProject(options.dir, options.projectType, options.projectName)
    
    if options.deploy:
        deploy(options.dir)
    

if __name__ == "__main__":
    sys.exit(main(sys.argv))
    
    