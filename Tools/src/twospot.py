from getpass import getpass
from optparse import OptionParser
import Cookie
import httplib
import os
import sys
import urllib
import yaml
import zipfile

###########################################
# Properties
###########################################

DEV_MODE = False

# Development
SERVER_PORTAL = 'portal.twospot.local'
SERVER_DEPLOY = 'deploy.twospot.local'

# Production
if not DEV_MODE:
    SERVER_PORTAL = 'portal.twospot.informatik.fh-augsburg.de'
    SERVER_DEPLOY = 'deploy.twospot.informatik.fh-augsburg.de'
    
PORT = 6060
TIMEOUT = 10

###########################################
###########################################


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

class AuthenticationFailed(Exception):
    def __init__(self, value):
        self.value = value
    
    def __str__(self):
        return repr(self.value)

class InvalidDirectoryName(Exception):
    def __init__(self, value):
        self.value = value
    
    def __str__(self):
        return repr(self.value)


def parseAppYaml(pathToYaml):
    file = open(pathToYaml, "r")
    result = yaml.load(file)
    return result; 



def upload(warFile, appId, version='null'):
    # Read username and password
    username = raw_input("username: ")
    password = getpass("password:")
    
    # Login ###
    print "Logging in..."
    con = httplib.HTTPConnection(SERVER_PORTAL, PORT, timeout=TIMEOUT)
    params = urllib.urlencode({'username':username, 'password':password})
    headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
    con.request('POST', '/loginHandler.htm', params, headers)

    response = con.getresponse()
    c = Cookie.SimpleCookie(response.getheader('set-cookie'))
    if 'UID' not in c:
        raise AuthenticationFailed("Authentication failed")
    
    print "  Logged in with (uid): " + c['UID'].value
    
    
    # Register ###
    print "Register upload..."
    con = httplib.HTTPConnection(SERVER_PORTAL, PORT, timeout=TIMEOUT)
    headers = {'cookie':'UID=' + c["UID"].value}
    con.request('GET', '/deploy.htm?id=' + appId + '&ver=' + version, '', headers)
    response = con.getresponse()
    
    print "Status: %i" % response.status
    if response.status != 200:
        print "Reason: %s" % response.reason
        return
    
    token = response.read()
    print "  Upload token: %s" % token 
    
    # Deploy ###
    print "Uploading..."
    file = open(warFile, mode='rb')
    con = httplib.HTTPConnection(SERVER_DEPLOY, PORT, timeout=TIMEOUT)
    con.request('POST', '/' + appId + '/' + version + '/' + token + '/', file, headers)
    response = con.getresponse()
    
    print "Status: %i" % response.status
    if response.status != 200:
        print "Reason: %s" % response.reason
        return
    
    # Commit ###
    print "Committing upload..."
    con = httplib.HTTPConnection(SERVER_PORTAL, PORT, timeout=TIMEOUT)
    headers = {'cookie':'UID=' + c["UID"].value}
    con.request('GET', '/deployDone.htm?id=' + appId + '&ver=' + version, '', headers)
    response = con.getresponse()
    token = response.read()
    headers = {'cookie':'UID=' + c["UID"].value}

    print "Status: %i" % response.status
    if response.status != 200:
        print "Reason: %s" % response.reason
        return


def testApp(directory):
    required = [
                "WEB-INF",
                "app.yaml"
                ]
    
    for test in required:
        testDir = directory + os.sep + test
        if not os.path.exists(testDir):
            raise InvalidApplication("Missing application directory: %s" % testDir);

    # Parse the YAML-File
    yaml = parseAppYaml(directory + os.sep + "app.yaml")
    print "App-Configuration: %s" % yaml
    
    return yaml 



def deploy(directory):
    if not os.path.isdir(directory):
        raise InvalidDirectory("Destination path is not a directory")

    # Is the directory an application directory?
    yaml = testApp(directory)

    zipFile = directory + os.sep + ".deploy"
    if os.path.exists(zipFile):
        print "deleting old deployment"
        os.remove(zipFile)
    
    zipFile = directory + os.sep + ".deploy"
    zip = zipfile.ZipFile(zipFile, "w")
    
    # Iterate over all files
    for root, dirs, files in os.walk(directory):
        # Get the relatve directory
        relRoot = root.replace(directory, "")
        print "adding files from: %s to: %s" % (root, relRoot)

        # Add the directory to the zip file
        # This does not work with all operating systems!
        #if relRoot is not "":
        #    zip.write(root, relRoot)
            
        # Iterate over all files in the directory
        for file in files:
            # Check if this is an invalid file
            if file.find(".deploy") >= 0:
                print "   skipping: %s" % file
                continue
            
            # Check if its a file
            if os.path.isfile(root + os.sep + file):          
                # Add the file to the zip file
                print "   adding file: %s" % file
                zip.write(root + os.sep + file, relRoot + os.sep + file)
            else:
                print "   skipping dir: %s" % file
    
    zip.close()
    
    # Upload the zip-file to the Web-Server
    appId = yaml["appId"]
    upload(zipFile , appId)


def compileBuildfile(file, target, params):
    file = open(file, mode='r')
    ftarget = open(target, mode='w')
    
    lines = file.readlines()
    for line in lines:
        for key in params.keys():
            line = line.replace("$$%s$$" % key, params[key])
        ftarget.write(line)
        
    ftarget.close()
    file.close()


def createProject(directory, projectType, projectName):
    if not os.path.isdir(directory):
        raise InvalidDirectory("Destination path is not a directory")  
    
    # Create the application folder
    directory += os.sep + projectName
    if os.path.exists(directory):
        raise InvalidDirectory("Directory already exists")
    
    os.mkdir(directory)        
    
    # Create the folder structure
    if projectType == "java":
        # Folders
        os.mkdir(directory + os.sep + "src")
        os.mkdir(directory + os.sep + "lib")
        os.mkdir(directory + os.sep + "WEB-INF")
        os.mkdir(directory + os.sep + "/WEB-INF/lib")
        os.mkdir(directory + os.sep + "/WEB-INF/classes")
        
        # Tokens to replace
        tokens = {"PROJ_NAME" : projectName}
        
        # Create the buildfile
        buildfile = os.path.dirname(__file__) + os.sep + "java" + os.sep + "build.xml"
        buildfileTarget = directory + os.sep + "build.xml"
        compileBuildfile(buildfile, buildfileTarget, tokens)
        
        # Other scripts
        buildfile = os.path.dirname(__file__) + os.sep + "java" + os.sep + "build.bat"
        buildfileTarget = directory + os.sep + "build.bat"
        compileBuildfile(buildfile, buildfileTarget, tokens)
        
        # Infos
        buildfile = os.path.dirname(__file__) + os.sep + "java" + os.sep + "info.txt"
        buildfileTarget = directory + os.sep + "lib" + os.sep + "info.txt"
        compileBuildfile(buildfile, buildfileTarget, tokens)
        
    elif projectType == "python":
        os.mkdir(directory + os.sep + "/WEB-INF")
        os.mkdir(directory + os.sep + "/WEB-INF/python")
        os.mkdir(directory + os.sep + "/WEB-INF/python/" + projectName)
    else:
        raise InvalidProjectType("Valid project types are java, python")
    
    # Create the YAML-file
    yaml = ["appId: %s\n" % projectName,
            "runtime: %s\n" % projectType,
            "\n"
            ]
    
    # Python-Specific YAML-content
    if projectType == "python":
        yaml += ["handlers: \n",
            "- refUrl: /.* \n",
            "  file: %s/TODO.py" % projectName
            ]
    
    file = open(directory + os.sep + "app.yaml", "w")
    file.writelines(yaml)
    file.close; 
    
    

def runServer(directory):
    print "Running server from (app dir): %s" % directory 
    
    # Check the directory and parse the YAML-Configuration
    yaml = testApp(directory)
    appId = yaml["appId"]

    # Extract the working directory
    index = directory.find(appId)
    if index != -1:
        directory = directory[0:index]
    else:
        raise InvalidDirectoryName("The application directoy has to be equal to the AppId %s" % appId)
    
    # Launch the server process
    appSrvPort = '8080'     # The port on which the DevServer listens
    stdio = 'true'          # Enable stdio outputs
    controller = 'false'    # DevServer doesn't require a Controller
    workDir = directory     # Directory which contains the application directory which name is equal to the AppId

    # Load the classpath from classpath.txt
    file = open(sys.path[0] + os.sep + "appserver_classpath.txt", "r")
    lines = file.readlines()
    classpath = ".;"
    for line in lines:
        line = line.replace("\n", "")
        classpath += line + ";"

    additionalCp = []
    additionalCp.append('./Libs/gen/twospot-appserver.jar')
    additionalCp.append('./conf/util/')
    additionalCp.append('./conf/appserver/')
    for cp in additionalCp:
        classpath += cp + ";" 

    # Main-Class
    javaMain = "org.prot.appserver.Main"

    # Configure the server params
    params = []
    params.append('java')
    params.append('-classpath')
    params.append(classpath)
    
    # Java main
    params.append(javaMain);
    
    # Application parameters
    params.append('-appId')
    params.append(appId)
    
    params.append('-appSrvPort')
    params.append(appSrvPort)
    
    params.append('-stdio')
    params.append(stdio)
    
    params.append('-controller')
    params.append(controller)
    
    params.append('-workDir')
    params.append(workDir)
    
    # Print the params before starting
    print params
    
    # Replace the own process with the AppServer-process
    print "executing in: %s" % sys.path[0]
    os.chdir(sys.path[0])
    os.execvp("java", params)
    


def main(args):
    
    # Tests
#    args.append("--dir")
#    args.append("C:/temp/blabla")
#    args.append("--deploy")
    
    parser = OptionParser()
    parser.add_option("--runServer", action="store_true", dest="runServer");
    
    parser.add_option("--createProject", action="store", dest="projectName")
    parser.add_option("--type", action="store", type="string", dest="projectType")
    
    parser.add_option("--deploy", action="store_true", dest="deploy")
    
    parser.add_option("--dir", action="store", type="string",)
    parser.add_option("--server", action="store", dest="server")
    
    # Parse the command line 
    (options, args) = parser.parse_args(args)
    
    # Check for command line errors
    if options.projectName and not options.projectType:
        parser.error("--createProject requires --type")
        return
    
    if options.dir:
        if not os.path.isdir(options.dir):
            parser.error(options.dir + " is not a directory")
            return
    else:
        options.dir = os.getcwd()
    
    # Set properties
    if options.server:
        global SERVER
        SERVER = options.server
    
   # Call functions
    if options.projectName:
        createProject(options.dir, options.projectType, options.projectName)
    
    elif options.deploy:
        deploy(options.dir)
        
    elif options.runServer:
        runServer(options.dir)
        
    else:
        parser.error("Invalid operation")
    


if __name__ == "__main__":
    sys.exit(main(sys.argv))
    
    
