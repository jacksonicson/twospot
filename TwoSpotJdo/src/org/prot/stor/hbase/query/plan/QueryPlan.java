package org.prot.stor.hbase.query.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.stor.hbase.HBaseManagedConnection;

public class QueryPlan
{
	private static final Logger logger = Logger.getLogger(QueryPlan.class);

	private List<QueryStep> plan = new ArrayList<QueryStep>();

	public void appendStep(QueryStep step)
	{
		logger.debug("Adding query step to plan: " + step);
		plan.add(step);
	}

	private void optimize()
	{
		logger.debug("Optimizing the query");
	}

	public List<Object> execute(HBaseManagedConnection connection)
	{
		// Optimize the plan
		optimize();

		List<Object> candidates = new ArrayList<Object>();

		for (QueryStep step : plan)
		{
			step.exeucte(connection, candidates);
		}

		return candidates;
	}
}
