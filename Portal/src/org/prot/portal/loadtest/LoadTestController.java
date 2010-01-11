package org.prot.portal.loadtest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoadTestController implements Controller
{
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		// Random r = new Random(System.currentTimeMillis());
		// for (int v = 0; v < 2; v++)
		// {
		// long i = Math.abs((r.nextInt())) % 9999999;
		// for (int t = 2; t < i / 2; t++)
		// {
		// if (i % t == 0)
		// break;
		// }
		// }
		// Should block the thread!
		Thread.sleep(2000);

		return null;
	}

}
