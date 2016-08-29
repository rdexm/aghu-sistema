package br.gov.mec.aghu.core.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

@SuppressWarnings("PMD")
public class TestLogger extends TestWatcher {

	private Log logger = LogFactory.getLog(getClass());

	@Override
	protected void failed(Throwable e, Description description) {
		if (!description.getAnnotation(Test.class).expected().isInstance(e)) {
			logger.error(description.getMethodName(), e);
		}
		super.failed(e, description);
	}
}
