package org.prot.storage;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.StorageUtils;
import org.prot.storage.query.QueryEngine;
import org.prot.storage.query.StorageQuery;

public class StorageImpl implements Storage {
	private static final Logger logger = Logger.getLogger(StorageImpl.class);

	private ConnectionFactory connectionFactory;

	public StorageImpl() {
		this.connectionFactory = new ConnectionFactory();
	}

	@Override
	public List<Key> createKey(String appId, long amount) {
		KeyCreator keyCreator = new KeyCreator(connectionFactory);
		try {
			return keyCreator.fetchKey(appId, amount);
		} catch (IOException e) {
			logger.error(e);
			return null;
		} finally {
		}
	}

	@Override
	public void createObject(String appId, String kind, Key key, byte[] obj) {
		// Asserts
		assert (key != null);

		// Assert field size
		StorageUtils.assertFieldSize(obj.length);

		// Create the entity
		ObjectCreator creator = new ObjectCreator(connectionFactory);
		try {
			creator.createObject(appId, kind, key, obj);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
		}
	}

	@Override
	public void updateObject(String appId, String kind, Key key, byte[] obj) {
		// Asserts
		assert (key != null);

		// Assert field size
		StorageUtils.assertFieldSize(obj.length);

		// Update the entity;
		ObjectUpdater updater = new ObjectUpdater(connectionFactory);
		try {
			updater.updateObject(appId, kind, key, obj);
		} catch (IOException e) {
			logger.error(e);
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} finally {
		}
	}

	@Override
	public List<byte[]> query(StorageQuery query) {
		ImplQueryHandler handler = new ImplQueryHandler(connectionFactory);
		QueryEngine engine = new QueryEngine(handler);

		try {
			return engine.run(query);
		} finally {
		}
	}

	@Override
	public byte[] query(String appId, Key key) {
		ImplQueryHandler handler = new ImplQueryHandler(connectionFactory);
		QueryEngine engine = new QueryEngine(handler);
		return engine.fetch(appId, key);
	}

	@Override
	public boolean deleteObject(String appId, String kind, Key key) {
		try {
			ObjectRemover remover = new ObjectRemover(connectionFactory);
			remover.removeObject(appId, kind, key);
			return true;
		} catch (IOException e) {
			logger.error(e);
			return false;
		} catch (NullPointerException e) {
			return false;
		} finally {
		}
	}

	@Override
	public List<String> listKinds(String appId) {
		try {
			Analyzer analyzer = new Analyzer(connectionFactory);
			return analyzer.listKinds(appId);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public List<byte[]> scanEntities(String appId, String kind) {
		try {
			Analyzer analyzer = new Analyzer(connectionFactory);
			return analyzer.scanEntities(appId, kind);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}
}
