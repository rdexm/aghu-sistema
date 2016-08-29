package br.gov.mec.aghu.exames.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.exames.dao.AelExameLimitadoAtendJnDAO;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ExameLimitadoAtendRNTest extends AGHUBaseUnitTest<ExameLimitadoAtendRN>{
	
	private static final Log log = LogFactory.getLog(ExameLimitadoAtendRNTest.class);

	@Mock
	private AelExameLimitadoAtendJnDAO mockedAelExameLimitadoAtendJnDAO;
	
	@Test
	public void posRemoverExameLimitadoAtendTest() {
		
		AelExamesLimitadoAtend exameLimitadoAtend = new AelExamesLimitadoAtend();
    			
    	try {    		
			systemUnderTest.posRemoverExameLimitadoAtend(exameLimitadoAtend);
			
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		
	}
	
	
}
