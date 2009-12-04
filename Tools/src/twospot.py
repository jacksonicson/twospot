from optparse import OptionParser
import Cookie
import httplib
import os
import sys
import urllib
import yaml
import zipfile

###########################################
# Properties (TODO: This is the portal app)
###########################################

SERVER = 'localhost'
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



def parseAppYaml(pathToYaml):
    file = open(pathToYaml, "r")
    result = yaml.load(file)
    return result; 



def upload(warFile, appId):
    # Deployment requires authentication
    
    
    # Read username and password
    username = raw_input("username: ")
    password = raw_input("password: ")
    
    # Login
    con = httplib.HTTPConnection(SERVER, PORT, timeout=TIMEOUT)
    params = urllib.urlencode({'username':username, 'password':password})
    headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
    con.request('POST', '/loginHandler.htm', params, headers)

    response = con.getresponse()
    c = Cookie.SimpleCookie(response.getheader('set-cookie'))
    if 'UID' not in c:
        raise AuthenticationFailed("Authentication failed")
    
    print "  Logged in with (uid): " + c['UID'].value
    
    # Reading (r) a binary file (b)
    file = open(warFile, mode='rb')
    
    # Send the request (file)
    print "Uploading..."
    con = httplib.HTTPConnection(SERVER, PORT, timeout=TIMEOUT)
    headers = {'cookie':'UID=' + c["UID"].value}
    con.request('GET', '/deploy.htm?id=' + appId + '&ver=null', file, headers)
    response = con.getresponse()
    
    print "Status: %i" % response.status
    if response.status != 200:
        print "Reason: %s" % response.reason
    
    



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
    upload(zipFile , appId)



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
    
    file = open(directory + "/app.yaml", "w")
    file.writelines(yaml)
    file.close; 
    
    

def runServer(directory):
    print "Running server with: %s" % directory 
    
    # Check the directory and parse the YAML-Configuration
    yaml = testApp(directory)
    appId = yaml["appId"]

    # Extract the working directory
    index = directory.find(appId)
    directory = directory[0:index]
    
    # Launch the server process
    # -appId ff2 -appSrvPort 9090 -stdio true -controller false -workDir C:/temp/tests/
    appSrvPort = '8080'
    stdio = 'true'
    controller = 'false'
    workDir = directory

    # Load the classpath from classpath.txt
    file = open(sys.path[0] + "/classpath.txt", "r")
    lines = file.readlines()
    classpath = ".;"
    for line in lines:
        line = line.replace("\n", "")
        classpath += "." + line + ";"

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
    
    
