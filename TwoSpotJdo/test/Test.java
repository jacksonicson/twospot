import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

public class Test
{
	public Test()
	{
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		PersistenceManager manager = pmf.getPersistenceManager();

		Person person = new Person();
		person.setName("Test");
		person.setAge(10);

		System.out.println("Making persistent now");
		manager.currentTransaction().begin();
		manager.makePersistent(person);
		manager.currentTransaction().commit();
		System.out.println("Persist done");

		Query query = manager.newQuery(Person.class);
		List<Person> persons = (List<Person>) query.execute();
		for (Person p : persons)
		{
			System.out.println("Person in datastore: " + p);
		}
	}

	public static void main(String arg[])
	{
		new Test();
	}
}
