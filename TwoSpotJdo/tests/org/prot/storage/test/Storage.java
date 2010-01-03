package org.prot.storage.test;

import java.util.List;
import java.util.Random;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prot.jdo.storage.StorageHelper;
import org.prot.storage.test.data.Person;

public class Storage
{
	private static PersistenceManager manager;

	@BeforeClass
	public static void connect()
	{
		// Configure logger
		DOMConfigurator.configure(Test.class.getResource("/etc/log4j.xml"));

		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		manager = pmf.getPersistenceManager();
		StorageHelper.setAppId("gogo");
	}

	@Before
	@After
	public void clean() throws Exception
	{
		if (manager.currentTransaction().isActive())
			manager.currentTransaction().rollback();

		Query query = manager.newQuery(Person.class);
		List<Person> persons = (List<Person>) query.execute();

		for (Person p : persons)
			manager.deletePersistent(p);
	}

	@Test
	public void testUpdate() throws Exception
	{
		Random r = new Random();

		Person person = new Person();
		person.setUsername("Alex");
		person.setMessage("Message from Alex");
		person.setTime(System.currentTimeMillis());
		person.setAsdf(r.nextInt());
		person.setType(r.nextDouble());

		manager.currentTransaction().begin();
		person = manager.makePersistent(person);
		manager.currentTransaction().commit();

		try
		{

			Query query = manager.newQuery(Person.class);
			List<Person> persons = (List<Person>) query.execute();

			for (Person p : persons)
			{
				p.getMessage();
				p.setMessage("New message from Alex");
				manager.makePersistent(p);
			}

			persons = (List<Person>) query.execute();
			Assert.assertTrue(persons.size() > 0);

			for (Person p : persons)
			{
				Assert.assertEquals("New message from Alex", p.getMessage());
				manager.deletePersistent(p);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testDelete() throws Exception
	{
		Random r = new Random();

		Person person = new Person();
		person.setUsername("Alex");
		person.setMessage("Message from Alex");
		person.setTime(System.currentTimeMillis());
		person.setAsdf(r.nextInt());
		person.setType(r.nextDouble());

		try
		{
			manager.currentTransaction().begin();
			person = manager.makePersistent(person);
			manager.currentTransaction().commit();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		Query query = manager.newQuery(Person.class);
		List<Person> persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() > 0);

		for (Person p : persons)
			manager.deletePersistent(p);

		persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 0);
	}

	@Test
	public void testCreate() throws Exception
	{
		Random r = new Random();

		Person person = new Person();
		person.setUsername("Alex");
		person.setMessage("Message from Alex");
		person.setTime(System.currentTimeMillis());
		person.setAsdf(r.nextInt());
		person.setType(r.nextDouble());

		try
		{
			manager.currentTransaction().begin();
			person = manager.makePersistent(person);
			manager.currentTransaction().commit();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		Query query = manager.newQuery(Person.class);
		List<Person> persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() > 0);

		for (Person p : persons)
			manager.deletePersistent(p);
	}
}
