package br.gov.mec.casca.tools.updater;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Dashboard;
import br.gov.mec.casca.model.Favorito;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Modulo;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissaoModulo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.model.PalavraChaveMenu;

public class HibernateHelper {

	public static final String HIBERNATE_CFG_FILE = "conf/hibernate.cfg.xml";

	private Session session;
	
	private SessionFactory sessionFactory;

	private Transaction transaction;

	private String hibernateFile = HIBERNATE_CFG_FILE;

	private static final Logger log = Logger.getLogger(HibernateHelper.class);

	private static HibernateHelper singleton;

	private HibernateHelper(String configFile) {
		hibernateFile = (configFile == null || configFile.isEmpty() ? HIBERNATE_CFG_FILE
				: configFile);
		log.info("Loading Hibernate Helper: " + hibernateFile);
	}

	public static HibernateHelper loadInstance(String configFile) {
		if (singleton == null) {
			singleton = new HibernateHelper(configFile);
		}
		return singleton;
	}

	public static HibernateHelper getSingleton() {
		return loadInstance(HIBERNATE_CFG_FILE);
	}

	public SessionFactory getSessionFactory() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addAnnotatedClass(Aplicacao.class);
		configuration.addAnnotatedClass(Dashboard.class);
		configuration.addAnnotatedClass(PalavraChaveMenu.class);
		configuration.addAnnotatedClass(Metodo.class);
		configuration.addAnnotatedClass(Componente.class);
		configuration.addAnnotatedClass(PermissoesComponentes.class);
		configuration.addAnnotatedClass(Permissao.class);
		configuration.addAnnotatedClass(PerfisPermissoes.class);
		configuration.addAnnotatedClass(Perfil.class);
		configuration.addAnnotatedClass(PerfisUsuarios.class);
		configuration.addAnnotatedClass(Usuario.class);
		configuration.addAnnotatedClass(Favorito.class);
		configuration.addAnnotatedClass(Menu.class);
		configuration.addAnnotatedClass(Modulo.class);
		configuration.addAnnotatedClass(PermissaoModulo.class);

		configuration.configure(hibernateFile);

		log.info(String.format("Hibernate config file: %s", HIBERNATE_CFG_FILE));
		log.info(String.format("Driver Class: %s",
				configuration.getProperty("connection.driver_class")));
		log.info(String.format("Datasource: %s",
				configuration.getProperty("connection.datasource")));
		log.info(String.format("URL: %s",
				configuration.getProperty("connection.url")));
		log.info(String.format("user: %s",
				configuration.getProperty("connection.username")));
		log.info(String.format("Dialect: %s",
				configuration.getProperty("dialect")));

		return configuration.buildSessionFactory();
	}

	public Session getSession() {
		if (session == null) {
			sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
		} else if(!session.isOpen()) {
			session = sessionFactory.openSession();
		}

		return session;
	}

	public void beginTransaction() {
		transaction = getSession().beginTransaction();
	}

	public void rollback() {
		transaction.rollback();
	}

	public void commit() {
		transaction.commit();
	}

	public Object salvar(Object object) {
		Object retorno = getSession().merge(object);
		this.flush();
		return retorno;
	}

	public void delete(Object object) {
		getSession().delete(object);
	}
	
	public void flush() {
		getSession().flush();		
	}	
}