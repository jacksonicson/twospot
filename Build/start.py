import os
import string
import sys

def getClasspathSeparator():
    separator = ";"
    if os.name == 'posix':
        separator = ":"
    
    return separator

def loadClasspath(file, additionalCp):
    # Load the classpath from classpath.txt
    file = open(sys.path[0] + file, "r")
    lines = file.readlines()
    
    # Load classpath separator
    separator = getClasspathSeparator()
    
    # Read classpath from file
    classpath = ""
    for line in lines:
        line = line.replace("\r\n", "")
        line = line.replace("\n", "")
        line = line.replace("\r", "")
        classpath += line + separator
    
    # Add the classpath from the argument    
    for additional in additionalCp:
        classpath += additional + separator
    
    return classpath    


def runFileserver(args):
    mainClass = 'org.prot.httpfileserver.Main'
    
    classpath = []
    classpath.append('./Libs/gen/twospot-fileserver.jar')
    classpath.append('./conf/util/')
    classpath.append('./conf/fileserver')
    classpath = loadClasspath('/httpserver_classpath.txt', classpath)
    
    print classpath
    
    params = []
    params.append('java')
    
    params.append('-Xms30m')
    params.append('-Xmx100m')
    
    params.append('-classpath')
    params.append(classpath)
    
    params.append(mainClass)
    
    params.extend(args)
    
    os.chdir(sys.path[0])
    os.execvp("java", params)


def runFrontend(args):
    mainClass = 'org.prot.frontend.Main'
    
    classpath = []
    classpath.append('./Libs/gen/twospot-frontend.jar')
    classpath.append('./conf/util/')
    classpath.append('./conf/frontend')
    classpath = loadClasspath('/frontend_classpath.txt', classpath)
    
    print classpath
    
    params = []
    params.append('java')
    
    params.append('-Xms30m')
    params.append('-Xmx100m')
    
    params.append('-classpath')
    params.append(classpath)
    
    params.append(mainClass)
    
    params.extend(args)
    
    os.chdir(sys.path[0])
    os.execvp("java", params)


def runMaster(args):
    mainClass = 'org.prot.manager.Main'
    
    classpath = []
    classpath.append('./Libs/gen/twospot-master.jar')
    classpath.append('./conf/util/')
    classpath.append('./conf/master')
    classpath = loadClasspath('/master_classpath.txt', classpath)
    
    print classpath
    
    params = []
    params.append('java')
    
    params.append('-Xms30m')
    params.append('-Xmx100m')
    
    params.append('-classpath')
    params.append(classpath)
    
    params.append(mainClass)
    
    params.extend(args)
    
    os.chdir(sys.path[0])
    os.execvp("java", params)


def runController(args):
    mainClass = 'org.prot.controller.Main'
    
    classpath = []
    classpath.append('./Libs/gen/twospot-controller.jar')
    classpath.append('./conf/util/')
    classpath.append('./conf/controller')
    classpath = loadClasspath('/controller_classpath.txt', classpath)
    
    print classpath
    
    params = []
    params.append('java')
    
    params.append('-Xms30m')
    params.append('-Xmx100m')
    
    params.append('-classpath')
    params.append(classpath)
    
    params.append(mainClass)
    
    params.extend(args)
    
    os.chdir(sys.path[0])
    os.execvp("java", params)



def writePid():
    pid = os.getpid()
    pidfile = str(pid) + ".pid"
    print "Writing %i to %s" % (pid, pidfile)
    
    file = open(pidfile, 'w')
    file.write(str(pid))
    file.close()



def killAll():
    dirlist = os.listdir(os.curdir)
    for item in dirlist:
        if item.find('.pid') != -1:
            index = item.find('.pid')
            pid = item[0:index]
            print "Killing %s" % pid
            os.kill(int(pid), 15)
            
            toremove = os.curdir + os.sep + item
            print "Deleting file %s" % toremove
            os.remove(toremove)



def main(args):
    name = args[1]
    print "Option %s" % name
    
    if name == 'fileserver':
        print 'starting fileserver'
        writePid()
        runFileserver(args[2:-1])
        
    elif name == 'frontend':
        print 'starting frontend'
        writePid()
        runFrontend(args[2:-1])
        
    elif name == 'master':
        print 'starting master'
        writePid()
        runMaster(args[2:-1])
        
    elif name == 'controller':
        print 'starting controller'
        writePid();
        runController(args[2:-1])
    
    elif name == 'kill':
        print 'shutting down'
        killAll()


        
if __name__ == "__main__":
    sys.exit(main(sys.argv))

