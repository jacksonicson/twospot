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

	private HBaseAdmin hBaseAdmin;

	public SchemaCreator(ConnectionFactory connectionFactory) throws MasterNotRunningException
	{
		this.hBaseAdmin = new HBaseAdmin(connectionFactory.getHBaseConfiguration());
	}

	public SchemaCreator(HBaseAdmin admin)
	{
		this.hBaseAdmin = admin;
	}
	
	public void checkAndCreate() throws IOException
	{
		checkSequences();
		checkEntities();
		checkIndexByKind();
		checkIndexByPropertyDesc();
		checkIndexByPropertyAsc();
		checkIndexCustom();
	}

	private void checkSequences() throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_SEQUENCES);

		String tableName = StorageUtils.TABLE_SEQUENCES;
		byte[][] families = { StorageUtils.bCounter };

		checkTable(tableName, families);
	}

	private void checkEntities() throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_ENTITIES);

		String tableName = StorageUtils.TABLE_ENTITIES;
		byte[][] families = { StorageUtils.bEntity };

		checkTable(tableName, families);
	}

	private void checkIndexByKind() throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_BY_KIND);

		String tableName = StorageUtils.TABLE_INDEX_BY_KIND;
		byte[][] families = { StorageUtils.bKey };

		checkTable(tableName, families);
	}

	private void checkIndexByPropertyDesc() throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_BY_PROPERTY_DESC);

		String tableName = StorageUtils.TABLE_INDEX_BY_PROPERTY_DESC;
		byte[][] families = { StorageUtils.bKey };

		checkTable(tableName, families);
	}

	private void checkIndexByPropertyAsc() throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC);

		String tableName = StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC;
		byte[][] families = { StorageUtils.bKey };

		checkTable(tableName, families);
	}

	private void checkIndexCustom() throws IOException
	{
		logger.debug("Checking for table: " + StorageUtils.TABLE_INDEX_CUSTOM);

		String tableName = StorageUtils.TABLE_INDEX_CUSTOM;
		byte[][] families = { StorageUtils.bKey };

		checkTable(tableName, families);
	}

	private void checkTable(String tableName, byte[][] families) throws IOException
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
		for (byte[] reqFamily : families)
		{
			if (!hTable.hasFamily(reqFamily))
			{
				tableModified = true;

				HColumnDescriptor hColumn = new HColumnDescriptor(reqFamily);
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
