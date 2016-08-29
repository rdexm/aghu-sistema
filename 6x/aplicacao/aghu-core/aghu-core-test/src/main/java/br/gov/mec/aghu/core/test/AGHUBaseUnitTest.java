package br.gov.mec.aghu.core.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;

@SuppressWarnings("PMD")
@RunWith(PowerMockRunner.class)
public abstract class AGHUBaseUnitTest<ClassUnderTest> {

	private Log logger = LogFactory.getLog(getClass());

	@Captor
	protected ArgumentCaptor<Object> argumentCaptor;
	@InjectMocks
	protected ClassUnderTest systemUnderTest;

	private Class<ClassUnderTest> systemClass;

	public AGHUBaseUnitTest() {
		try {
			Type superType = getClass().getGenericSuperclass();
			while (superType instanceof Class) {
				superType = ((Class) superType).getGenericSuperclass();
			}
			if (superType instanceof ParameterizedType) {
				systemClass = ((Class) ((ParameterizedType) superType).getActualTypeArguments()[0]);
				systemUnderTest = (ClassUnderTest) systemClass.newInstance();
				logger.debug("SystemUnderTest carregado com a classe : " + systemClass.getSimpleName()
						+ " com sucesso.");
			} else {
				throw new IllegalArgumentException();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Erro ao carregar classe do generics", e);
		}
	}

	@Rule
	private BusinessLoaderResource businessLoaderResource = new BusinessLoaderResource(this);

	@Rule
	private MockitoResource mockitoInitiator = new MockitoResource();

	@Rule
	private TestLogger testLogger = new TestLogger();

	private String usuarioLogado = "TEST_USER";
	

	@BeforeClass
	public static void initLog() {
		LoggerResource.iniciarLog();
	}

	public Log LOG() {
		return logger;
	}

	
	public String getUsuarioLogado() {
		return usuarioLogado;
	}

	
	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}
}
