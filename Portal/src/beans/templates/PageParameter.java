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
package beans.templates;

/**
 * Source Code from:
 * http://www.javaworld.com/jw-09-2000/jw-0915-jspweb.html?page=3
 * 
 */
public class PageParameter {
	private String content, direct;

	public void setContent(String s) {
		content = s;
	}

	public void setDirect(String s) {
		direct = s;
	}

	public String getContent() {
		return content;
	}

	public boolean isDirect() {
		return Boolean.valueOf(direct).booleanValue();
	}

	public PageParameter(String content, String direct) {
		this.content = content;
		this.direct = direct;
	}
}
