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
import javax.servlet.jsp.tagext.TagSupport;

import beans.templates.PageParameter;

public class PutTag extends TagSupport
{
	private String name, content, direct = "false";

	public void setName(String s)
	{
		name = s;
	}

	public void setContent(String s)
	{
		content = s;
	}

	public void setDirect(String s)
	{
		direct = s;
	}

	public int doStartTag() throws JspException
	{
		InsertTag parent = (InsertTag) getAncestor("tags.templates.InsertTag");
		if (parent == null)
			throw new JspException("PutTag.doStartTag(): " + "No InsertTag ancestor");

		Stack template_stack = parent.getStack();

		if (template_stack == null)
			throw new JspException("PutTag: no template stack");

		Hashtable params = (Hashtable) template_stack.peek();

		if (params == null)
			throw new JspException("PutTag: no hashtable");

		params.put(name, new PageParameter(content, direct));

		return SKIP_BODY;
	}

	public void release()
	{
		name = content = direct = null;
	}

	private TagSupport getAncestor(String className) throws JspException
	{
		Class klass = null; // can’t name variable "class"
		try
		{
			klass = Class.forName(className);
		} catch (ClassNotFoundException ex)
		{
			throw new JspException(ex.getMessage());
		}
		return (TagSupport) findAncestorWithClass(this, klass);
	}
}
