package org.prot.storage.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectCondition implements Serializable
{
	private static final long serialVersionUID = -1163609480212762858L;

	private List<AtomarCondition> atoms = new ArrayList<AtomarCondition>();

	public void addCondition(AtomarCondition atom)
	{
		this.atoms.add(atom);
	}
}
