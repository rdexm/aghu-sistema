package br.gov.mec.aghu.core.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.rules.ExternalResource;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("PMD")
class MockitoResource extends ExternalResource {

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	protected void before() throws Throwable {
		super.before();
		try {
			MockitoAnnotations.initMocks(this);
			logger.debug("Mockito inicializado.");
		} catch (Exception e) {
			logger.error("Erro ao inicializar Mockito.", e);
		}

	}
}
