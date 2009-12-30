package org.prot.storage.connection;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;

public class SchemaCreator
{
	private static final Logger logger = Logger.getLogger(SchemaCreator.class);

	private ConnectionFactory connectionFactory;
	private HBaseAdmin hBaseAdmin;

	public SchemaCreator(ConnectionFactory connectionFactory) throws MasterNotRunningException
	{
		this.connectionFactory = connectionFactory;
		this.hBaseAdmin = new HBaseAdmin(connectionFactory.getHBaseConfiguration());
	}

	public void checkAndCreate() throws IOException
	{
		HBaseManagedConnection connection = (HBaseManagedConnection) connectionFactory
				.createManagedConnection();

		checkSequences(connection);
		checkEntities(connection);
		checkIndexByKind(connection);
		checkIndexByPropertyDesc(connection);
		checkIndexByPropertyAsc(connection);
		checkIndexCustom(connection);
	}

	private void checkSequences(HBaseManagedConnection connection) throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_SEQUENCES);

		String tableName = StorageUtils.TABLE_SEQUENCES;
		String[] families = { "counter" };

		checkTable(tableName, families);
	}

	private void checkEntities(HBaseManagedConnection connection) throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_ENTITIES);

		String tableName = StorageUtils.TABLE_ENTITIES;
		String[] families = { "serialized" };

		checkTable(tableName, families);
	}

	private void checkIndexByKind(HBaseManagedConnection connection) throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_BY_KIND);

		String tableName = StorageUtils.TABLE_INDEX_BY_KIND;
		String[] families = { "key" };

		checkTable(tableName, families);
	}

	private void checkIndexByPropertyDesc(HBaseManagedConnection connection) throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_BY_PROPERTY_DESC);

		String tableName = StorageUtils.TABLE_INDEX_BY_PROPERTY_DESC;
		String[] families = { "key" };

		checkTable(tableName, families);
	}

	private void checkIndexByPropertyAsc(HBaseManagedConnection connection) throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC);

		String tableName = StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC;
		String[] families = { "key" };

		checkTable(tableName, families);
	}

	private void checkIndexCustom(HBaseManagedConnection connection) throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_CUSTOM);

		String tableName = StorageUtils.TABLE_INDEX_CUSTOM;
		String[] families = { "key" };

		checkTable(tableName, families);
	}

	private void checkTable(String tableName, String[] families) throws IOException
	{
		// Check and create the table if necessary
		HTableDescriptor hTable;
		try
		{
			hTable = hBaseAdmin.getTableDescriptor(tableName.getBytes());
		} catch (TableNotFoundException e)
		{
			hTable = new HTableDescriptor(tableName);
			hBaseAdmin.createTable(hTable);
		}

		// Create table families
		boolean tableModified = false;
		for (String reqFamily : families)
		{
			if (!hTable.hasFamily(reqFamily.getBytes()))
			{
				tableModified = true;

				HColumnDescriptor hColumn = new HColumnDescriptor(reqFamily.getBytes());
				hTable.addFamily(hColumn);
			}
		}

		// Apply all changes to the table
		if (tableModified)
		{
			hBaseAdmin.disableTable(hTable.getName());
			hBaseAdmin.modifyTable(hTable.getName(), hTable);
			hBaseAdmin.enableTable(hTable.getName());
		}
	}
}
