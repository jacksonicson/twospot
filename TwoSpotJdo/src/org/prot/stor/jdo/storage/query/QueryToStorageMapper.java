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
package org.prot.stor.jdo.storage.query;

import java.util.Map;
import java.util.Stack;

import javax.jdo.Query;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.ObjectManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUnsupportedOptionException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.evaluator.AbstractExpressionEvaluator;
import org.datanucleus.query.expression.CastExpression;
import org.datanucleus.query.expression.CreatorExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.SubqueryExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.prot.jdo.storage.messages.types.StorageProperty;
import org.prot.storage.query.AtomLiteral;
import org.prot.storage.query.AtomarCondition;
import org.prot.storage.query.ConditionType;
import org.prot.storage.query.StorageQuery;

public class QueryToStorageMapper extends AbstractExpressionEvaluator
{
	private static final Logger logger = Logger.getLogger(QueryToStorageMapper.class);

	private StorageQuery storageQuery;

	private QueryCompilation compilation;

	private Map parameters;

	private AbstractClassMetaData acmd;

	private ObjectManager objectManager;

	private FetchPlan fetchPlan;

	private ClassLoaderResolver clr;

	// Stack which is used for compilation
	private Stack<Object> stack = new Stack<Object>();

	public QueryToStorageMapper(StorageQuery query, QueryCompilation compilation, Map parameters,
			AbstractClassMetaData acmd, FetchPlan fetchPlan, ObjectManager om)
	{
		this.storageQuery = query;

		this.compilation = compilation;

		this.parameters = parameters;

		this.acmd = acmd;

		this.fetchPlan = fetchPlan;

		this.objectManager = om;

		this.clr = om.getClassLoaderResolver();
	}

	public void compile()
	{
		if (compilation.getExprFrom() != null)
		{
			throw new NucleusException("FROM expressions are not supported");
		}

		if (compilation.getExprResult() != null)
		{
			throw new NucleusException("RESULT expressions are not supported");
		}

		if (compilation.getExprOrdering() != null)
		{
			// throw new NucleusException("ORDERING expressions are not supported");
		}

		if (compilation.getExprHaving() != null)
		{
			throw new NucleusException("HAVING expressions are not supported");
		}

		if (compilation.getExprGrouping() != null)
		{
			throw new NucleusException("GROUPING expressions are not supported");
		}

		if (compilation.getExprUpdate() != null)
		{
			throw new NucleusException("UPDATE expressions are not supported");
		}

		// Get the filter expression
		if (compilation.getExprFilter() != null)
		{
			// Check if there is an OR operator in the expression (unsupported)
			if (QueryUtils.expressionHasOrOperator(compilation.getExprFilter()))
				throw new NucleusException("OR filterin is unsupported");

			// Evaluate the expression (calls process... methods in this class)
			compilation.getExprFilter().evaluate(this);

		} else
		{
			storageQuery.setKind(compilation.getCandidateClass().getSimpleName());
		}

		// Unique queries
		if (compilation.returnsSingleRow())
		{
			storageQuery.getLimit().setUnique(true);
		}
	}

	protected Object processAndExpression(Expression expr)
	{
		// Do nothing
		return null;
	}

	protected Object processParameterExpression(ParameterExpression expr)
	{
		Object paramValue = null;
		if (parameters != null && parameters.containsKey(expr.getId()))
			paramValue = parameters.get(expr.getId());

		if (paramValue == null)
			throw new NucleusException("Undeclared parameter: " + expr.getId());

		Object value = paramValue;
		byte[] bValue = StorageProperty.bytesFrom(value.getClass(), value);
		AtomLiteral literal = new AtomLiteral(bValue);
		stack.push(literal);

		return literal;
	}

	protected Object processVariableExpression(VariableExpression expr)
	{
		throw new NucleusException("Variables are not supported");
	}

	protected Object processLiteral(Literal expr)
	{
		Object value = expr.getLiteral();
		byte[] bValue = StorageProperty.bytesFrom(value.getClass(), value);
		AtomLiteral literal = new AtomLiteral(bValue);
		stack.push(literal);

		return literal;
	}

	protected Object processPrimaryExpression(PrimaryExpression expr)
	{
		if (expr.getLeft() != null)
			throw new NucleusException("Primary expression cannot have a left part");

		String id = expr.getId();
		byte[] bKey = Bytes.toBytes(id);

		AtomLiteral literal = new AtomLiteral(bKey, true);
		stack.push(literal);

		return literal;
	}

	protected Object processEqExpression(Expression expr)
	{
		AtomLiteral value = (AtomLiteral) stack.pop();
		AtomLiteral property = (AtomLiteral) stack.pop();

		AtomarCondition condition = new AtomarCondition(ConditionType.EQUALS, property, value);
		this.storageQuery.getCondition().addCondition(condition);
		stack.push(condition);

		return condition;
	}

	protected Object processNoteqExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: !=");
	}

	protected Object processGteqExpression(Expression expr)
	{
		AtomLiteral value = (AtomLiteral) stack.pop();
		AtomLiteral property = (AtomLiteral) stack.pop();

		AtomarCondition condition = new AtomarCondition(ConditionType.GREATER_EQUALS, property, value);
		this.storageQuery.getCondition().addCondition(condition);
		stack.push(condition);

		return condition;
	}

	protected Object processGtExpression(Expression expr)
	{
		AtomLiteral value = (AtomLiteral) stack.pop();
		AtomLiteral property = (AtomLiteral) stack.pop();

		AtomarCondition condition = new AtomarCondition(ConditionType.GREATER, property, value);
		this.storageQuery.getCondition().addCondition(condition);
		stack.push(condition);

		return condition;
	}

	protected Object processLteqExpression(Expression expr)
	{
		AtomLiteral value = (AtomLiteral) stack.pop();
		AtomLiteral property = (AtomLiteral) stack.pop();

		AtomarCondition condition = new AtomarCondition(ConditionType.LOWER_EQUALS, property, value);
		this.storageQuery.getCondition().addCondition(condition);
		stack.push(condition);

		return condition;
	}

	protected Object processLtExpression(Expression expr)
	{
		AtomLiteral value = (AtomLiteral) stack.pop();
		AtomLiteral property = (AtomLiteral) stack.pop();

		AtomarCondition condition = new AtomarCondition(ConditionType.LOWER, property, value);
		this.storageQuery.getCondition().addCondition(condition);
		stack.push(condition);

		return condition;
	}

	protected Object processOrExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: OR");
	}

	protected Object processInvokeExpression(InvokeExpression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: INVOKE");
	}

	protected Object processSubqueryExpression(SubqueryExpression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: SUBQUERY");
	}

	protected Object processAddExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: AND");
	}

	protected Object processDivExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: DIV");
	}

	protected Object processMulExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: MUL");
	}

	protected Object processSubExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: SUB");
	}

	protected Object processComExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: COMPARE");
	}

	protected Object processModExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: MOD");
	}

	protected Object processNegExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: NEG");
	}

	protected Object processNotExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: NOT");
	}

	protected Object processCastExpression(CastExpression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: CAST");
	}

	protected Object processIsExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: IS");
	}

	protected Object processCreatorExpression(CreatorExpression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: CREATOR");
	}

	protected Object processLikeExpression(Expression expr)
	{
		throw new NucleusUnsupportedOptionException("Unsupported query operator: LIKE");
	}
}
