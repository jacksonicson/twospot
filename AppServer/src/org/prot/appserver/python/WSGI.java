package org.prot.appserver.python;

import org.python.core.PyBuiltinFunction;
import org.python.core.PyFunction;
import org.python.core.PyObject;

public class WSGI
{

	public void test(PyFunction function) {
		
		function.__call__(new Test()); 
	}
	
	class Test extends PyBuiltinFunction
	{

		protected Test()
		{
			super("Test", "");
		}
		
		public PyObject __call__() {
			System.out.println("OK"); 
			return null; 
		}
		
	}
}
