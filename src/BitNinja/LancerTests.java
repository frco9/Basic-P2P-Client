/**
 * Cette classe permet de lancer les tests de manière automatisée par introspection sur toutes les classes commencant par Test
 *
 * @author BitNinja
 * @since 2014
 */
package BitNinja;

import java.lang.reflect.Method;

class LancerTests {

	/**
	 * Permet de lancer toutes les méthodes de test des différentes classes de test par introspection.
	 * Capture une exception si une des méthodes executée renvoie une erreur
	 * 
	 * @param c Un objet "Class" de n'importe quel type
	 */
	static private void lancer(Class<?> c) throws Exception {
		Object tests = c.newInstance();
		Method[] methods = c.getDeclaredMethods();
		//Nombre total de méthodes à executer
		int num_meth = 0;
		//Nombre de méthodes executées avec succès
		int i=0;
		Class parent = c.getSuperclass();

		// Récupération et execution des méthodes commençant par "test" de la classe parente, si la courante dérive d'une de nos classes de test (TestPassagerAbstrait)
		if (parent.getName().contains("Test")) {
			Method[] parent_methods = parent.getDeclaredMethods();
			for (Method parent_method : parent_methods) {
				if (parent_method.getName().startsWith("test")) {
					try{
						// System.out.println("       Tests de "+parent_method.getName());
						parent_method.setAccessible(true);
						parent_method.invoke(tests);
					}
					catch (Throwable e) {
						System.out.println("Erreur \""+e.getCause()+"\" dans la methode : "+parent_method.getName());
						e.printStackTrace();
						i--;
					}
					i++;
					num_meth++;
				}
			}
		}
		
	// Récupération et execution des méthodes commençant par "test" de la classe courante
		for (Method method : methods) {
			if (method.getName().startsWith("test")) {
				try{
					// System.out.println("       Tests de "+method.getName());
					method.setAccessible(true);
					method.invoke(tests);
				}
				catch (Throwable e) {
					System.out.println("Erreur \""+e.getCause()+"\" dans la methode : "+method.getName());
					e.printStackTrace();
					i--;
				}
				i++;
				num_meth++;
			}
		}
		if (i == num_meth) {
			System.out.println("Tests de "+c.getName()+" :  OK ("+i+"/"+num_meth+")");
		} else {
			System.out.println("Tests de "+c.getName()+" :  Erreur ("+i+"/"+num_meth+")");
		}
		
	}

	public static void main(String... args) {
		boolean estMisAssertion = false;
		assert estMisAssertion = true;

		//Verification de l'activation des assertions
		if (!estMisAssertion) {
			System.out.println("Execution impossible sans l'option -ea");
			return;
		}

		// Lancement des tests

		//Tableau contenant les classes à tester.
		Class[] classesName = {TestClientFile.class, TestParser.class, TestClient.class};

		for (Class className : classesName) {
			try {
				lancer(className);
			}
			catch (Throwable e) {
				System.err.println(e.getCause());
			}   
		}


	}
}

