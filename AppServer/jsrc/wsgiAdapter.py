import os


def test():
    print "test runs ok"



def add_wsgi_middleware(application):
  # return _config_handle.add_wsgi_middleware(application) TODO
  return application



def run_wsgi_app(application):
    wrappedApplication = add_wsgi_middleware(application) 
    run_bare_wsgi_app(wrappedApplication)
     

wsgio = None
def set(wsgio_in):
    global wsgio
    wsgio = wsgio    

def run_bare_wsgi_app(application):
    
    global wsgio
    
    # Create environment
    env = dict(os.environ)    
    
    # Environ variables
    env["REQUEST_METHOD"] = "TODO"
    env["SCRIPT_NAME"] = "Optional"
    env["PATH_INFO"] = "Optional"
    env["QUERY_STRING"] = "Opt"
    env["CONTENT_TYPE"] = "Opt"
    env["CONTENT_LENGTH"] = "Opt"
    env["SERVER_NAME"] = "TODO"
    env["SERVER_PORT"] = "TODO"
    env["SERVER_PROTOCOL"] = "HTTP/1.1 TODO"
    env["HTTP_..."] = "...TODO"
     
    # WSGI-defined variables 
    env["wsgi.version"] = (1, 0) # wsgi version
    env["wsgi.url_scheme"] = "http" # scheme portion of the url # TODO: determine the current value
    env["wsgi.input"] = wsgio # input stream from which the HTTP request body can be read
    env["wsgi.errors"] = wsgio # output stream to which error output can be written (server error log)
    env["wsgi.multithread"] = True # The application object can be simultaneously invoked by another thread in the same process
    env["wsgi.multiprocess"] = False # The application object can be simultaneously invoked by another process
    env["wsgi.run_once"] = True # The application object will only be invoked one time during the life of its containing process

    # Call the application-object and give a reference to the start_response object
    # result is an iterable which contains the response content as strings (binary data)
    result = application(env, start_response)
    
    # check if result is None
    if result is not None:
        # Write everything into the response
        for data in result:
            wsgio.write(data) # TODO: pass this to jetty, send headers after the first write!

    result.close(); # TODO: catch exception if close function does not exist! 

# Function is used to begin the HTTP response. It is called before the first iterator call of the result object from the application object
# or befor the first write to the returned stream from this method
# @arg status is a string which contains the request status
# @arg resonse_headers is a list of (key, value) pairs
# @arg exc_info is optional. If it exists, it contains the application error
def start_response(status, response_headers, exc_info=None):
    global wsgio
    
    # Check if there are errors
    # TODO: Only if the headers have already been sent
    if exc_info is not None:
        raise exc_info[0], exc_info[1], exc_info[2]
    
    # TODO: Does not replace currently set headers
    # Write the status header
    print "Status: %s" % status
    # Write all other headers
    for name, val in response_headers:
        print "%s: %s" % (name, val)

    # Return a writable object which can be used to write into the response content
    # This response-method is deprecated - the result iterabel from the application object should be used instead
    # After the first write -> send the headers
    return wsgio.write

