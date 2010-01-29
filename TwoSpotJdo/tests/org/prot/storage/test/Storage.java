package org.prot.storage.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prot.jdo.storage.StorageHelper;
import org.prot.storage.test.data.Person;

public class Storage
{
	private static PersistenceManagerFactory pmf;

	private static PersistenceManager manager;

	@BeforeClass
	public static void connect() throws Exception
	{
		// Configure logger
		DOMConfigurator.configure(Test.class.getResource("/etc/log4j.xml"));

		try
		{
			pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
			manager = pmf.getPersistenceManager();
			StorageHelper.setAppId("gogo");
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@Before
	// @After
	public void clean() throws Exception
	{
		try
		{
			if (manager.currentTransaction().isActive())
				manager.currentTransaction().rollback();

			Query query = manager.newQuery(Person.class);
			List<Person> persons = (List<Person>) query.execute();

			System.out.println("Removing: " + persons.size());
			for (Person p : persons)
				manager.deletePersistent(p);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryAnd() throws Exception
	{
		Random r = new Random();

		for (int i = 0; i < 50; i++)
		{
			Person person = new Person();
			person.setUsername("Alex");
			person.setMessage("Message from Alex");
			person.setTime(System.currentTimeMillis());
			person.setAsdf(i);
			person.setType(r.nextDouble());

			manager.currentTransaction().begin();
			manager.makePersistent(person);
			manager.currentTransaction().commit();
		}

		Query query = manager.newQuery(Person.class);
		query.setFilter("asdf > 25 && asdf < 30");
		List<Person> persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 4);
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() > 25 && p.getAsdf() < 30);

		query.setFilter("asdf > 25 && asdf <= 30");
		persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 5);
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() > 25 && p.getAsdf() <= 30);

		query.setFilter("asdf >= 25 && asdf <= 30");
		persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 6);
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() >= 25 && p.getAsdf() <= 30);

		query.setFilter("asdf >= 25 && asdf < 30");
		persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 5);
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() >= 25 && p.getAsdf() < 30);
	}

	@Test
	public void testQueryRange() throws Exception
	{
		Random r = new Random();

		for (int i = 0; i < 50; i++)
		{
			Person person = new Person();
			person.setUsername("Alex");
			person.setMessage("Message from Alex");
			person.setTime(System.currentTimeMillis());
			person.setAsdf(i);
			person.setType(r.nextDouble());

			manager.currentTransaction().begin();
			manager.makePersistent(person);
			manager.currentTransaction().commit();
		}

		Query query = manager.newQuery(Person.class);
		query.setFilter("asdf > 25");
		List<Person> persons = (List<Person>) query.execute();
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() > 25);

		query.setFilter("asdf < 25");
		persons = (List<Person>) query.execute();
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() < 25);

		query.setFilter("asdf == 25");
		persons = (List<Person>) query.execute();
		for (Person p : persons)
			Assert.assertTrue(p.getAsdf() == 25);

		query.setFilter("asdf >= 25");
		persons = (List<Person>) query.execute();
		boolean foundEquals = false;
		for (Person p : persons)
		{
			Assert.assertTrue(p.getAsdf() >= 25);
			foundEquals |= p.getAsdf() == 25;
		}
		Assert.assertTrue(foundEquals);

		query.setFilter("asdf <= 25");
		persons = (List<Person>) query.execute();
		foundEquals = false;
		for (Person p : persons)
		{
			Assert.assertTrue(p.getAsdf() <= 25);
			foundEquals |= p.getAsdf() == 25;
		}
		Assert.assertTrue(foundEquals);
	}

	@Test
	public void testQueryString() throws Exception
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
			manager.makePersistent(person);
			manager.currentTransaction().commit();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		Query query = manager.newQuery(Person.class);
		query.setFilter("username == 'Bob'");
		List<Person> persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 0);

		query.setFilter("username == 'Alex'");
		persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() > 0);

		for (Person p : persons)
			manager.deletePersistent(p);
	}

	@Test
	public void testQueryLimit() throws Exception
	{
		try
		{
			Random r = new Random();

			for (int i = 0; i < 20; i++)
			{
				Person person = new Person();
				person.setUsername("Alex");
				person.setMessage("Message from Alex");
				person.setTime(System.currentTimeMillis());
				person.setAsdf(i);
				person.setType(r.nextDouble());

				manager.currentTransaction().begin();
				manager.makePersistent(person);
				manager.currentTransaction().commit();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		Query query = manager.newQuery(Person.class);
		query.setRange(0, 10);

		List<Person> persons = (List<Person>) query.execute();
		Assert.assertTrue(persons.size() == 10);

		for (Person p : persons)
			manager.deletePersistent(p);
	}

	@Test
	public void testQueryUnique() throws Exception
	{
		try
		{
			Random r = new Random();

			for (int i = 0; i < 3; i++)
			{
				Person person = new Person();
				person.setUsername("Alex");
				person.setMessage("Message from Alex");
				person.setTime(System.currentTimeMillis());
				person.setAsdf(i);
				person.setType(r.nextDouble());

				manager.currentTransaction().begin();
				manager.makePersistent(person);
				manager.currentTransaction().commit();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		Query query = manager.newQuery(Person.class);
		query.setUnique(true);

		Person person = (Person) query.execute();
		Assert.assertTrue(person != null);
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

		person = manager.makePersistent(person);

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

		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testComplexUpdate() throws Exception
	{
		final Random r = new Random();

		for (int i = 0; i < 1; i++)
		{
			Person person = new Person();
			person.setUsername("Alex");
			person.setMessage("Message from Alex");
			person.setTime(System.currentTimeMillis());
			person.setAsdf(r.nextInt());
			person.setType(r.nextDouble());

			person = manager.makePersistent(person);
		}

		try
		{
			List<Thread> tl = new ArrayList<Thread>();

			for (int i = 0; i < 6; i++)
			{
				Thread thr = new Thread()
				{
					public void run()
					{
						PersistenceManager mmanger = pmf.getPersistenceManager();
						mmanger.currentTransaction().begin();

						Query query = mmanger.newQuery(Person.class);
						List<Person> persons = (List<Person>) query.execute();

						for (int k = 0; k < 1; k++)
						{
							for (Person p : persons)
							{
								try
								{
									p.setTime(10);
									mmanger.makePersistent(p);
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
						
						mmanger.currentTransaction().commit();
					}
				};
				thr.start();
				tl.add(thr);
			}

			for (Thread t : tl)
				t.join();


			Query query = manager.newQuery(Person.class);
			List<Person> persons = (List<Person>) query.execute();

			for (Person test : persons)
			{
				System.out.println(test.getTime());
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			// throw e;
		}
	}

	@Test
	public void testDetachCopy() throws Exception
	{
		Random r = new Random();
		String username = "Alex";
		String message = "Message from Alex";
		long timestamp = System.currentTimeMillis();
		int asdf = r.nextInt();
		double type = r.nextDouble();

		Person person = new Person();
		person.setUsername(username);
		person.setMessage(message);
		person.setTime(timestamp);
		person.setAsdf(asdf);
		person.setType(type);

		try
		{
			manager.currentTransaction().begin();
			manager.makePersistent(person);
			manager.currentTransaction().commit();

			person = manager.detachCopy(person);

			Assert.assertTrue(username.equals(person.getUsername()));
			Assert.assertTrue(message.equals(person.getMessage()));
			Assert.assertTrue(timestamp == person.getTime());
			Assert.assertTrue(asdf == person.getAsdf());
			Assert.assertTrue(type == person.getType());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testMakePersistent() throws Exception
	{
		Random r = new Random();
		String username = "Alex";
		String message = "Message from Alex";
		long timestamp = System.currentTimeMillis();
		int asdf = r.nextInt();
		double type = r.nextDouble();

		Person person = new Person();
		person.setUsername(username);
		person.setMessage(message);
		person.setTime(timestamp);
		person.setAsdf(asdf);
		person.setType(type);

		try
		{
			manager.currentTransaction().begin();
			manager.makePersistent(person);
			manager.currentTransaction().commit();

			Assert.assertTrue(username.equals(person.getUsername()));
			Assert.assertTrue(message.equals(person.getMessage()));
			Assert.assertTrue(timestamp == person.getTime());
			Assert.assertTrue(asdf == person.getAsdf());
			Assert.assertTrue(type == person.getType());

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testUnique() throws Exception
	{
		Random r = new Random();

		try
		{
			manager.currentTransaction().begin();
			for (int i = 0; i < 100; i++)
			{
				Person person = new Person();
				person.setUsername("Alex");
				person.setMessage("Message from Alex");
				person.setTime(System.currentTimeMillis());
				person.setAsdf(i);
				person.setType(r.nextDouble());

				person = manager.makePersistent(person);
			}
			manager.currentTransaction().commit();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		Query query = manager.newQuery(Person.class);
		query.setUnique(true);
		query.setFilter("asdf == " + 50);
		Person person = (Person) query.execute();

		Assert.assertNotNull(person);
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
