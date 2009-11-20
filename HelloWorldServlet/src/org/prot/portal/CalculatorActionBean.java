package org.prot.portal;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * A very simple calculator action.
 * 
 * @author Tim Fennell
 */
public class CalculatorActionBean implements ActionBean
{
	private ActionBeanContext context;
	private double numberOne;
	private double numberTwo;
	private double result;

	public ActionBeanContext getContext()
	{
		return context;
	}

	public void setContext(ActionBeanContext context)
	{
		this.context = context;
	}

	public double getNumberOne()
	{
		return numberOne;
	}

	public void setNumberOne(double numberOne)
	{
		System.out.println("blabla"); 
		this.numberOne = numberOne;
	}

	public double getNumberTwo()
	{
		return numberTwo;
	}

	public void setNumberTwo(double numberTwo)
	{
		this.numberTwo = numberTwo;
	}

	public double getResult()
	{
		return result;
	}

	public void setResult(double result)
	{
		this.result = result;
	}

	@DefaultHandler
	public Resolution addition()
	{
		result = getNumberOne() + getNumberTwo();
		System.out.println("Result: " + result);
		return new ForwardResolution("/index.jsp");
	}
}