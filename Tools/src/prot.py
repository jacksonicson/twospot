import sys
import httplib
import string

SERVER = 'localhost:5050'

def uploadFile(file):
    # filename
    index = str.find(file, ".")
    if index > 0:    
        name = file[0:index]
        print "using name: %s" % name
    
    # Reading (r) a binary file (b)
    file = open("hellopython.war", mode='rb')
    
    # Create a new connection and send the request (file)
    con = httplib.HTTPConnection(SERVER)
    response = con.request('POST', '/app/' + name, file)
    

def main(args):
    file = args[1]
    print "uploading file: %s" % file
    uploadFile(file) 

if __name__ == "__main__":
    sys.exit(main(sys.argv))
    