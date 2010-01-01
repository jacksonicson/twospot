package org.prot.storage.test;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.apache.log4j.xml.DOMConfigurator;
import org.prot.jdo.storage.StorageHelper;

public class Test
{
	private void testJdo()
	{
//		// Configure logger
//		DOMConfigurator.configure(Test.class.getResource("/etc/log4j.xml"));
//
//		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
//		PersistenceManager manager = pmf.getPersistenceManager();
//		StorageHelper.setAppId("gogo");
//
//		for (int a = 0; a < 1; a++)
//		{
//			for (int i = 1; i < 2; i++)
//			{
//				Person person = new Person();
//				person.setUsername("Alex" + i);
//
//				System.out.println("Making persistent now");
//
//				manager.currentTransaction().begin();
//				manager.makePersistent(person);
//				manager.currentTransaction().commit();
//
//				System.out.println("Persist done");
//			}
//
//		}
//
//		Query query = manager.newQuery("select from test.Person where username == name");
//		query.declareParameters("java.lang.String name");
//
//		List<Person> persons = (List<Person>) query.execute("Alex1");
//		if (persons == null)
//		{
//			System.out.println("persons is null");
//			return;
//		}
//		for (Person p : persons)
//		{
//			System.out.println("Person in datastore: " + p.getUsername() + " key: " + p.getKey()
//					+ " Message: " + p.getMessage());
//
//			p.setMessage("BLABLA " + System.currentTimeMillis());
//			manager.makePersistent(p);
//
//			// manager.deletePersistent(p);
//		}
	}

	public Test()
	{
		testJdo();
	}

	public static void main(String arg[])
	{
		new Test();
	}
}
