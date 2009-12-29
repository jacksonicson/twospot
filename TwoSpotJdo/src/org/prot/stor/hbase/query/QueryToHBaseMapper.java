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
import org.datanucleus.store.mapped.StatementClassMapping;
import org.datanucleus.store.mapped.StatementResultMapping;
import org.prot.stor.hbase.query.plan.FetchExpression;
import org.prot.stor.hbase.query.plan.FetchType;
import org.prot.stor.hbase.query.plan.IntersectExpression;
import org.prot.stor.hbase.query.plan.KeyParameter;
import org.prot.stor.hbase.query.plan.LiteralParameter;
import org.prot.stor.hbase.query.plan.QueryPlan;
import org.prot.stor.hbase.query.plan.QueryStep;

public class QueryToHBaseMapper extends AbstractExpressionEvaluator
{
	private static final Logger logger = Logger.getLogger(QueryToHBaseMapper.class);

	private QueryPlan queryPlan;

	private CompilationComponent compilationComponent;

	// ---

	private Map parameters;

	private String candidateAlias;

	private QueryCompilation compilation;

	private StatementClassMapping resultDefForClass;

	private StatementResultMapping resultDef;

	private AbstractClassMetaData acmd;

	private FetchPlan fetchPlan;

	private ObjectManager objectManager;

	private ClassLoaderResolver clr;

	private boolean caseSensitive = true;

	Stack<QueryStep> stack = new Stack<QueryStep>();

	public QueryToHBaseMapper(QueryPlan queryPlan, QueryCompilation compilation, Map parameters,
			AbstractClassMetaData cmd, FetchPlan fetchPlan, ObjectManager om)
	{
		this.queryPlan = queryPlan;

		this.parameters = parameters;
		this.fetchPlan = fetchPlan;
		this.objectManager = om;
		this.compilation = compilation;
		this.clr = om.getClassLoaderResolver();

		if (this.compilation.getQueryLanguage().equalsIgnoreCase("JPQL"))
			this.caseSensitive = false;
	}

	public String getQueryLanguage()
	{
		return this.compilation.getQueryLanguage();
	}

	public void compile()
	{
		// Get the filter expression
		if (compilation.getExprFilter() != null)
		{
			// Update compilation comopnent
			compilationComponent = CompilationComponent.FILTER;

			// Check if there is an OR operator in the expression (unsupported)
			if (QueryUtils.expressionHasOrOperator(compilation.getExprFilter()))
				throw new NucleusException("OR filterin is unsupported");

			// Evaluate the expression (calls process methods in this class)
			compilation.getExprFilter().evaluate(this);

			// Build the query plan with the remaining stack content
			while (!stack.isEmpty())
			{
				QueryStep step = stack.pop();
				queryPlan.appendStep(step);
			}
		}
	}

	protected Object processAndExpression(Expression expr)
	{
		logger.debug("Processing AND expression");

		FetchExpression right = (FetchExpression) stack.pop();
		FetchExpression left = (FetchExpression) stack.pop();

		IntersectExpression intersection = new IntersectExpression(left, right);
		stack.push(intersection);

		return intersection;
	}

	protected Object processParameterExpression(ParameterExpression expr)
	{
		logger.debug("Processing parameter expression");

		return null;
	}

	protected Object processVariableExpression(VariableExpression expr)
	{
		logger.debug("Processing variable expression");

		return null;
	}

	protected Object processLiteral(Literal expr)
	{
		logger.debug("Processing LITERAL");

		Object value = expr.getLiteral();
		byte[] bValue = new byte[0];
		if (value instanceof String)
			bValue = Bytes.toBytes((String) value);
		else if (value instanceof Long)
			bValue = Bytes.toBytes((Long) value);
		else if (value instanceof Integer)
			bValue = Bytes.toBytes((Integer) value);
		else if (value instanceof Boolean)
			bValue = Bytes.toBytes((Boolean) value);
		else
		{
			logger.warn("Unsupported literal type: " + value.getClass());
			bValue = Bytes.toBytes(value.toString());
		}

		LiteralParameter propertyParameter = new LiteralParameter(bValue);
		stack.push(propertyParameter);

		return propertyParameter;
	}

	protected Object processPrimaryExpression(PrimaryExpression expr)
	{
		logger.debug("Processing PRIMARY expression");

		if (expr.getLeft() != null)
		{
			throw new NucleusException("Primary expression cannot have a left part");
		}

		String id = expr.getId();
		byte[] bKey = Bytes.toBytes(id);

		KeyParameter keyParameter = new KeyParameter(bKey);
		stack.push(keyParameter);

		return keyParameter;
	}

	protected Object processEqExpression(Expression expr)
	{
		logger.debug("Processing EQUALS expression");

		LiteralParameter right = (LiteralParameter) stack.pop();
		LiteralParameter left = (LiteralParameter) stack.pop();

		FetchExpression fetch = new FetchExpression(FetchType.EQUALS, left, right);
		stack.push(fetch);

		return null;
	}

	protected Object processNoteqExpression(Expression expr)
	{
		logger.debug("Processing NOT EQUALS expression");

		LiteralParameter right = (LiteralParameter) stack.pop();
		LiteralParameter left = (LiteralParameter) stack.pop();

		FetchExpression fetch = new FetchExpression(FetchType.NOT_EQUALS, left, right);
		stack.push(fetch);

		return null;
	}

	protected Object processGteqExpression(Expression expr)
	{
		logger.debug("Prcoessing GREATER EQUALS expression");

		LiteralParameter right = (LiteralParameter) stack.pop();
		LiteralParameter left = (LiteralParameter) stack.pop();

		FetchExpression fetch = new FetchExpression(FetchType.EQUALS_GREATER, left, right);
		stack.push(fetch);

		return null;
	}

	protected Object processGtExpression(Expression expr)
	{
		logger.debug("Processsing GREATER expression");

		LiteralParameter right = (LiteralParameter) stack.pop();
		LiteralParameter left = (LiteralParameter) stack.pop();

		FetchExpression fetch = new FetchExpression(FetchType.GREATER, left, right);
		stack.push(fetch);

		return null;
	}

	protected Object processLteqExpression(Expression expr)
	{
		logger.debug("Processing LOWER EQUALS expression");

		LiteralParameter right = (LiteralParameter) stack.pop();
		LiteralParameter left = (LiteralParameter) stack.pop();

		FetchExpression fetch = new FetchExpression(FetchType.EQUALS_LOWER, left, right);
		stack.push(fetch);

		return null;
	}

	protected Object processLtExpression(Expression expr)
	{
		logger.debug("Processing LOWER expression");

		LiteralParameter right = (LiteralParameter) stack.pop();
		LiteralParameter left = (LiteralParameter) stack.pop();

		FetchExpression fetch = new FetchExpression(FetchType.LOWER, left, right);
		stack.push(fetch);

		return null;
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
