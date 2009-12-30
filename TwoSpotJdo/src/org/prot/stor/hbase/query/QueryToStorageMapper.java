package org.prot.stor.hbase.query;

import java.util.Map;
import java.util.Stack;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.ObjectManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.compiler.CompilationComponent;
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
import org.prot.storage.query.AtomLiteral;
import org.prot.storage.query.AtomarCondition;
import org.prot.storage.query.ConditionType;
import org.prot.storage.query.StorageQuery;

public class QueryToStorageMapper extends AbstractExpressionEvaluator
{
	private static final Logger logger = Logger.getLogger(QueryToStorageMapper.class);

	private StorageQuery storageQuery;

	private QueryCompilation compilation;

	private CompilationComponent compilationComponent;

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
			throw new NucleusException("ORDERING expressions are not supported");
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
			// Update compilation comopnent
			compilationComponent = CompilationComponent.FILTER;

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
		byte[] bValue = null;

		if (value instanceof String)
			bValue = Bytes.toBytes((String) value);

		else if (value instanceof Double)
			bValue = Bytes.toBytes((Double) value);

		else if (value instanceof Long)
			bValue = Bytes.toBytes((Long) value);

		else if (value instanceof Integer)
			bValue = Bytes.toBytes((Integer) value);

		else if (value instanceof Boolean)
			bValue = Bytes.toBytes((Boolean) value);

		else if (value instanceof Character)
			bValue = Bytes.toBytes(((Character) value).toString());

		else
		{
			logger.error("Unsupported paramter value type: " + value.getClass());
			return null;
		}

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
		byte[] bValue = null;

		if (value instanceof String)
			bValue = Bytes.toBytes((String) value);

		else if (value instanceof Double)
			bValue = Bytes.toBytes((Double) value);

		else if (value instanceof Long)
			bValue = Bytes.toBytes((Long) value);

		else if (value instanceof Integer)
			bValue = Bytes.toBytes((Integer) value);

		else if (value instanceof Boolean)
			bValue = Bytes.toBytes((Boolean) value);

		else if (value instanceof Character)
			bValue = Bytes.toBytes(((Character) value).toString());

		else
		{
			logger.error("Unsupported literal type: " + value.getClass());
			return null;
		}

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
		AtomLiteral value = (AtomLiteral) stack.pop();
		AtomLiteral property = (AtomLiteral) stack.pop();

		AtomarCondition condition = new AtomarCondition(ConditionType.NOT_EQUALS, property, value);
		this.storageQuery.getCondition().addCondition(condition);
		stack.push(condition);

		return condition;
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
		throw new NucleusException("OR expressions are not supported");
	}

	protected Object processInvokeExpression(InvokeExpression expr)
	{
		throw new NucleusException("InvokeExpressions are not supported");
	}

	protected Object processSubqueryExpression(SubqueryExpression expr)
	{
		throw new NucleusException("Subqueries are not supported");
	}

	protected Object processAddExpression(Expression expr)
	{
		throw new NucleusException("Add expressions are not supported");
	}

	protected Object processDivExpression(Expression expr)
	{
		throw new NucleusException("Div expressions are not supported");
	}

	protected Object processMulExpression(Expression expr)
	{
		throw new NucleusException("Mul expressions are not supported");
	}

	protected Object processSubExpression(Expression expr)
	{
		throw new NucleusException("Sub expressions are not supported");
	}

	protected Object processComExpression(Expression expr)
	{
		throw new NucleusException("Com expressions are not supported");
	}

	protected Object processModExpression(Expression expr)
	{
		throw new NucleusException("Mod expressions are not supported");
	}

	protected Object processNegExpression(Expression expr)
	{
		throw new NucleusException("Neg expressions are not supported");
	}

	protected Object processNotExpression(Expression expr)
	{
		throw new NucleusException("Not expressions are not supported");
	}

	protected Object processCastExpression(CastExpression expr)
	{
		throw new NucleusException("Cast expressions are not supported");
	}

	protected Object processIsExpression(Expression expr)
	{
		throw new NucleusException("Is expressions are not supported");
	}

	protected Object processCreatorExpression(CreatorExpression expr)
	{
		throw new NucleusException("Creator expressions are not supported");
	}

	protected Object processLikeExpression(Expression expr)
	{
		throw new NucleusException("Like expressions are not supported");
	}
}
