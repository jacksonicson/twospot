package org.prot.appserver.python;

import org.python.core.PyDictionary;
import org.python.core.PyMethod;
import org.python.core.PyObject;

public class Wsgi extends PyObject
{
	class start_response extends PyMethod
	{

		public start_response(PyObject function, PyObject self, PyObject type)
		{
			super(function, self, type);
			// TODO Auto-generated constructor stub
		}
	}

	public void run_wsgi_app(PyMethod method)
	{
		PyDictionary dict = new PyDictionary(); 
		
		
		method.__call__(dict, null); 
	}
	
}
