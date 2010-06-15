/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package tags.templates;

import java.util.Hashtable;
import java.util.Stack;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

public class InsertTag extends TagSupport
{
	private String template;
	private Stack stack;

	public void setTemplate(String template)
	{
		this.template = template;
	}

	public int doStartTag() throws JspException
	{
		stack = getStack();
		stack.push(new Hashtable());
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		try
		{
			pageContext.include(template);
		} catch (Exception ex)
		{ // IOException or ServletException
			throw new JspException(ex.getMessage());
		}
		stack.pop();
		return EVAL_PAGE;
	}

	public void release()
	{
		template = null;
		stack = null;
	}

	public Stack getStack()
	{
		Stack s = (Stack) pageContext.getAttribute("template-stack", PageContext.REQUEST_SCOPE);
		if (s == null)
		{
			s = new Stack();
			pageContext.setAttribute("template-stack", s, PageContext.REQUEST_SCOPE);
		}
		return s;
	}
}
