import os
import threading

def callback(target):
    print "calling back"
    target()
    print "done"

class MyApp:

    _wsgio = None
    _dict = None
    _name = None

    def __init__(self, wsgio, dict, name):
        self._wsgio = wsgio
        self._dict = dict
        self._name = name
        
        from org.prot.appserver.python import Wsg
        wsg = Wsg()
        wsg.test(callback)

    def run_wsgi_app(self, application):
        self.run_bare_wsgi_app(application)
         
    def run_bare_wsgi_app(self, application):
        wsgio = self._wsgio
        env = self._dict
         
        # thread safety
        if not (self._name == threading.currentThread().getName()):
            print "ERROR in run_bare_wsgi_app"
         
        # WSGI-defined variables 
        env["wsgi.version"] = (1, 0) # wsgi version
        env["wsgi.url_scheme"] = wsgio.getScheme() # scheme portion of the url # TODO: determine the current value
        env["wsgi.input"] = wsgio # input stream from which the HTTP request body can be read
        env["wsgi.errors"] = wsgio # output stream to which error output can be written (server error log)
        env["wsgi.multithread"] = True # The application object can be simultaneously invoked by another thread in the same process
        env["wsgi.multiprocess"] = False # The application object can be simultaneously invoked by another process
        env["wsgi.run_once"] = False # The application object will only be invoked one time during the life of its containing process
    
        # Call the application-object and give a reference to the start_response object
        # result is an iterable which contains the response content as strings (binary data)
        result = application(env, self.start_response)
        
        # check if result is None
        if result is not None:
            # Write everything into the response
            for data in result:
                wsgio.write(data) # TODO: pass this to jetty, send headers after the first write!
    
        try:
            result.close();
        except AttributeException:
            pass 
    
    # Function is used to begin the HTTP response. It is called before the first iterator call of the result object from the application object
    # or befor the first write to the returned stream from this method
    # @arg status is a string which contains the request status
    # @arg resonse_headers is a list of (key, value) pairs
    # @arg exc_info is optional. If it exists, it contains the application error
    def start_response(self, status, response_headers, exc_info=None):
        wsgio = self._wsgio
        
        # thread safety
        if not (self._name == threading.currentThread().getName()):
            print "ERROR in start_response"
        
        # Check if there are errors
        # TODO: Only if the headers have already been sent
        if exc_info is not None:
            raise exc_info[0], exc_info[1], exc_info[2]
        
        # TODO: Does not replace currently set headers
        # Write the status header
        wsgio.setHeader("Status", status);
        #wsgio.setStatus(int(status))
        
        # Write all other headers
        for name, val in response_headers:
            wsgio.setHeader(name, status);
    
        # Return a writable object which can be used to write into the response content
        # This response-method is deprecated - the result iterabel from the application object should be used instead
        # After the first write -> send the headers
        return wsgio.write

