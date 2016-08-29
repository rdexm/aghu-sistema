package br.gov.mec.aghu.core.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.ClassLoaderUtil;

@SuppressWarnings("PMD")
class BusinessLoaderResource extends ExternalResource {

	private static final String CORE_PACKAGE = "br.gov.mec.aghu.core.";
	private static final String JAVAX_ANNOTATION_RESOURCE = "javax.annotation.Resource";
	private static final String JAVAX_EJB_SESSION_CONTEXT = "javax.ejb.SessionContext";
	private static final String JAVAX_INJECT_INJECT = "javax.inject.Inject";
	private static final String JAVA_SECURITY_PRINCIPAL = "java.security.Principal";

	private final Log logger = LogFactory.getLog(getClass());

	private AGHUBaseUnitTest<?> test;

	public BusinessLoaderResource(AGHUBaseUnitTest<?> test) {
		this.test = test;
	}

	protected void before(Statement base) throws Throwable {
		super.before();

		Class<? extends Object> baseClass = test.systemUnderTest.getClass();
		try {
			while (baseClass.getSuperclass() != null
					&& !baseClass.getSuperclass().getCanonicalName().contains(CORE_PACKAGE)) {
				baseClass = baseClass.getSuperclass();
			}
			if (baseClass.getSuperclass() != null) {
				Class<?> superClass = test.systemUnderTest.getClass().getSuperclass();
				Class<Annotation> classeInject = ClassLoaderUtil.loadClass(JAVAX_INJECT_INJECT);
				Class<Annotation> classeResource = ClassLoaderUtil.loadClass(JAVAX_ANNOTATION_RESOURCE);

				for (Field f : superClass.getDeclaredFields()) {
					for (Annotation a : f.getAnnotations()) {
						if (classeInject.isInstance(a)) {
							try {
								Class<Object> classe = ClassLoaderUtil.loadClass(f.getType().getName());
								if (classe != null && !isPrimitive(classe)) {
									Object o = Mockito.mock(classe);
									PowerMockito.field(test.systemUnderTest.getClass(), f.getName()).set(
											test.systemUnderTest, o);
								}
							} catch (Exception e) {
								logger.warn("Erro ao inicializar classe auxiliar " + f.getType().getName() + ".", e);
							}
						}
						if (classeResource.isInstance(a)) {
							try {
								Class<Object> classe = ClassLoaderUtil.loadClass(f.getType().getName());
								if (classe != null && !isPrimitive(classe)) {
									Object o = Mockito.mock(classe);
									if (classe.getCanonicalName().equalsIgnoreCase(JAVAX_EJB_SESSION_CONTEXT)) {
										Class<?> classePrincipal = ClassLoaderUtil.loadClass(JAVA_SECURITY_PRINCIPAL);
										Object mockedPrincipal = Mockito.mock(classePrincipal);
										PowerMockito.when(mockedPrincipal, "getName").then(new Answer<String>() {
											@Override
											public String answer(InvocationOnMock invocation) throws Throwable {
												return test.getUsuarioLogado();
											}

										});
										PowerMockito.when(o, "getCallerPrincipal").thenReturn(mockedPrincipal);
									}
									PowerMockito.field(test.systemUnderTest.getClass(), f.getName()).set(
											test.systemUnderTest, o);
								}
							} catch (Exception e) {
								logger.warn("Erro ao inicializar classe auxiliar " + f.getType().getName() + ".", e);
							}
						}

					}
				}
			}
			logger.debug("Classes Auxliares do Negocio inicializadas.");
		} catch (Exception e) {
			logger.error("Erro ao inicializar Classes Auxliares do Negocio.", e);
		}

	}

	private boolean isPrimitive(Class<?> classe) {
		return (classe.isPrimitive() || classe == Double.class || classe == Float.class || classe == Long.class
				|| classe == Integer.class || classe == Short.class || classe == Character.class
				|| classe == Byte.class || classe == Boolean.class);
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return statement(base);
	}

	private Statement statement(final Statement base) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				before(base);
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}

}
