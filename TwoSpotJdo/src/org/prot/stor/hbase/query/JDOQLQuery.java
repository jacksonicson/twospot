/**********************************************************************
Copyright (c) 2009 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package org.prot.stor.hbase.query;

import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.ObjectManager;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Expression.DyadicOperator;
import org.datanucleus.query.expression.Expression.Operator;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.store.query.AbstractJDOQLQuery;

/**
 * Implementation of JDOQL for HBase datastores.
 */
public class JDOQLQuery extends AbstractJDOQLQuery
{

	private static final Logger logger = Logger.getLogger(JDOQLQuery.class);

	/**
	 * Constructs a new query instance that uses the given persistence manager.
	 * 
	 * @param om
	 *            the associated ObjectManager for this query.
	 */
	public JDOQLQuery(ObjectManager om)
	{
		this(om, (JDOQLQuery) null);
	}

	/**
	 * Constructs a new query instance having the same criteria as the given
	 * query.
	 * 
	 * @param om
	 *            The ObjectManager
	 * @param q
	 *            The query from which to copy criteria.
	 */
	public JDOQLQuery(ObjectManager om, JDOQLQuery q)
	{
		super(om, q);
	}

	/**
	 * Constructor for a JDOQL query where the query is specified using the
	 * "Single-String" format.
	 * 
	 * @param om
	 *            The persistence manager
	 * @param query
	 *            The query string
	 */
	public JDOQLQuery(ObjectManager om, String query)
	{
		super(om, query);
	}

	private void translate(String left, Expression filter)
	{
		Class expressionClass = filter.getClass();
		if (expressionClass == DyadicExpression.class)
		{
			System.out.println(left + "dyadic expression");
			Operator operator = filter.getOperator();
			assert (operator != null);
			String strOperator = operator.toString().trim();
			DyadicOperator dyoperator = (DyadicOperator) operator;
			if (strOperator.equals("OR"))
			{
			} else if (strOperator.equals("="))
			{

			}

		} else if (expressionClass == PrimaryExpression.class)
		{
			System.out.println(left + "primary expression");
		}

		Operator operator = filter.getOperator();
		if (operator != null)
		{
			System.out.println(left + "Operator string: " + filter.getOperator().toString());
			if (filter.getOperator().toString().trim().equals("OR"))
			{
				System.out.println(left + "Operator is OR");
				translate(left + " ", filter.getLeft());
				translate(left + " ", filter.getRight());
			} else if (filter.getOperator().toString().trim().equals("="))
			{
				System.out.println(left + "Operator is EQUALS");
				translate(left + "L ", filter.getLeft());
				translate(left + "R ", filter.getRight());
			}
			return;
		} else if (filter.getSymbol() != null)
		{
			Symbol symb = filter.getSymbol();
			System.out.println(left + "Symbol: " + symb);
			return;
		} else if (filter.getClass() == Literal.class)
		{
			Literal l = (Literal) filter;
			System.out.println(left + l.getLiteral());
		} else
		{
			System.out.println(left + "unknown expression");
			System.out.println(left + filter);
		}
	}

	protected Object performExecute(Map parameters)
	{
		logger.debug("perform execute");

		Expression filter = this.compilation.getExprFilter();
		translate("", filter);

		// HBaseManagedConnection mconn = (HBaseManagedConnection)
		// om.getStoreManager().getConnection(om);
		// try
		// {
		// long startTime = System.currentTimeMillis();
		// if (NucleusLogger.QUERY.isDebugEnabled())
		// {
		// NucleusLogger.QUERY.debug(LOCALISER.msg("021046", "JDOQL",
		// getSingleStringQuery(), null));
		// }
		// List candidates = null;
		// if (candidateCollection != null)
		// {
		// candidates = new ArrayList(candidateCollection);
		// } else if (candidateExtent != null)
		// {
		// candidates = new ArrayList();
		// Iterator iter = candidateExtent.iterator();
		// while (iter.hasNext())
		// {
		// candidates.add(iter.next());
		// }
		// } else
		// {
		// candidates = HBaseUtils.getObjectsOfCandidateType(om, mconn,
		// candidateClass, subclasses,
		// ignoreCache);
		// }
		//
		// // Apply any result restrictions to the results

		// JavaQueryEvaluator resultMapper = new JDOQLEvaluator(this,
		// candidates, compilation, parameters,
		// om.getClassLoaderResolver());
		// Collection results = resultMapper.execute(true, true, true, true,
		// true);
		//
		// if (NucleusLogger.QUERY.isDebugEnabled())
		// {
		// NucleusLogger.QUERY.debug(LOCALISER.msg("021074", "JDOQL", ""
		// + (System.currentTimeMillis() - startTime)));
		// }
		//
		// return results;
		// } finally
		// {
		// mconn.release();
		// }

		return null;
	}
}