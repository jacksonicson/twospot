package org.prot.stor.hbase.query.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class QueryPlan
{
	private static final Logger logger = Logger.getLogger(QueryPlan.class);

	private List<QueryStep> plan = new ArrayList<QueryStep>();

	public void appendStep(QueryStep step)
	{
		logger.debug("Adding query step to plan: " + step);
		plan.add(step);
	}

	public List<QueryStep> getPlan()
	{
		return this.plan;
	}
}
