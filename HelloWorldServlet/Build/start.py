import os
import sys

def loadClasspath(file, specific):
    # Load the classpath from classpath.txt
    file = open(sys.path[0] + file, "r")
    lines = file.readlines()
    classpath = ""
    for line in lines:
        line = line.replace("\n", "")
        classpath += line + ";"
    
    # Add the classpath from the argument    
    for cp in specific:
        classpath += cp + ";"
    
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
    
    params.append('-classpath')
    params.append(classpath)
    
    params.append(mainClass)
    
    params.extend(args)
    
    os.chdir(sys.path[0])
    os.execvp("java", params)


def main(args):
    name = args[1]
    print name
    
    if name == 'fileserver':
        print 'starting fileserver'
        runFileserver(args[2:-1])
        
    elif name == 'frontend':
        print 'starting frontend'
        runFrontend(args[2:-1])
        
    elif name == 'master':
        print 'starting master'
        runMaster(args[2:-1])
        
    elif name == 'controller':
        print 'starting controller'
        runController(args[2:-1])
    
    
        
if __name__ == "__main__":
    sys.exit(main(sys.argv))
    
    
