def add_wsgi_middleware(application):
  # return _config_handle.add_wsgi_middleware(application) TODO
  return application



def run_wsgi_app(application):
    wrappedApplication = add_wsgi_middleware(application) 
    run_bare_wsgi_app(wrappedApplication)
     
    

def run_bare_wsgi_app(application):
    
    env["wsgi.version"] = (1, 0) # wsgi version
    env["wsgi.url_scheme"] = "http" # scheme portion of the url # TODO: determine the current value
    env["wsgi.input"] = sys.stdin # input stream from which the HTTP request body can be read
    env["wsgi.errors"] = sys.stderr # output stream to which error output can be written (server error log)
    env["wsgi.multithread"] = True # The application object can be simultaneously invoked by another thread in the same process
    env["wsgi.multiprocess"] = False # The application object can be simultaneously invoked by another process
    env["wsgi.run_once"] = True # The application object will only be invoked one time during the life of its containing process

    # Put environment variables into the environment
    env = dict(os.environ)
    
    # Call the application-object and give a reference to the start_response object
    result = application(env, start_response)
    
    # check if result is None
    if result is not None:
        # Write everything into the response
        for data in result:
            sys.stdout.write(data) # TODO: pass this to jetty



def start_response(status, headers, exc_info=None):
    if exc_info is not None:
        raise exc_info[0], exc_info[1], exc_info[2]
    
    for name, val in headers:
        print "%s: %s" % (name, val)
    
    return sys.stdout.write # TODO: pass handler from jetty
