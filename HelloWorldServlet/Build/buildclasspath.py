import sys

def createClasspathList(eclipseProject, killPath=True):
    fProject = open(eclipseProject, "r")
    project = fProject.read()
    
    pattern = "path=\""
    mustContain = ".jar"
    
    li = []
    pointer = 0
    while pointer >= 0:
        nextFind = project.find(pattern, pointer)
        endFind = project.find("\"", nextFind + len(pattern) + 1)
        
        if(endFind < pointer):
            break
        pointer = endFind + 1
                
        extract = project[nextFind + len(pattern) : endFind]
        
        if killPath:
            last = extract.rfind("/");
            if last != -1:
                extract = extract[last + 1 : len(extract)]
        
        # Check if extract is a jar file
        if(extract.find(mustContain) == -1):
            continue

        li.append(extract)
        
    return li

def createAntClasspath(eclipseProject, killPath):
    output = ""
    for extract in createClasspathList(eclipseProject, killPath):
        print extract
        output += '<pathelement location="..%s" />\n' % extract
        
    return output


def createStartClasspath(eclipseProject, killPath):
    output = ""
    for extract in createClasspathList(eclipseProject, killPath):
        print extract
        output += '.%s\n' % extract
        
    return output

def createFileSet(eclipseProject, killPath):
    output = ""
    for extract in createClasspathList(eclipseProject, killPath):
        print extract
        output += '<includesfile file="..%s" />\n' % extract
    
    return output


def main():
    output = "<project>"
    
    cp = createAntClasspath(".classpath", False)
    output += "<path id='classpath'>" + cp + "</path>"
    
    #fileset = createFileSet(".classpath", False)
    #output += "<fileset id='libfiles'>" + fileset + "</fileset>"
        
    output += "</project>"
    
    javaFile = open("classpath.xml", "w")
    javaFile.write(output);
    javaFile.close()
    
    output = createStartClasspath(".classpath", False)
    javaFile = open("classpath.txt", "w")
    javaFile.write(output)
    javaFile.close()


if __name__ == "__main__":
    main()
