import sys

def createClasspathList(eclipseProject):
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
        last = extract.rfind("/");
        if last != -1:
            extract = extract[last + 1 : len(extract)]
        
        # Check if extract is a valid classpath
        if(extract.find(mustContain) == -1):
            continue

        li.append(extract)
        
    return li

def createClasspathFile(eclipseProject):
    output = ""
    for extract in createClasspathList(eclipseProject):
        print extract
        output += 'libs.add("%s");\n' % extract
    
    return output
        
output = createClasspathFile("../../AppServer/.classpath")
java = """
package org.prot.controller.generated;

import java.util.ArrayList;
import java.util.List;

class AppServerLibs {
    private static List<String> libs = new ArrayList<String>();
    static {
        %s
    }
    
    public static List<String> getLibs()
    {
        return AppServerLibs.libs;
    }
}
""" % output

javaFile = open("./org/prot/controller/generated/AppServerLibs.java", "w")
javaFile.write(java);
javaFile.close()


