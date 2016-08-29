package br.gov.mec.aghu.configuracao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;

public class AacGradeAgendamenConsultasDAOTest extends AbstractDAOTest<AacGradeAgendamenConsultasDAO> {

	@Override
	protected AacGradeAgendamenConsultasDAO doDaoUnderTests() {
		return new AacGradeAgendamenConsultasDAO() {
			private static final long serialVersionUID = -2373611114699215901L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AacGradeAgendamenConsultasDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Test
	public void testObterOriginalBuscaSimples() throws ApplicationBusinessException {
		AacGradeAgendamenConsultas elemento = new AacGradeAgendamenConsultas();
		elemento.setSeq(Integer.valueOf(933));
		this.daoUnderTests.obterOriginal(elemento);
	}
	

	@Test
	public void listarDoadorSangueTriagemClinica() throws ApplicationBusinessException {

		List<AacGradeAgendamenConsultas> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {				
				//sem dados
				result = this.daoUnderTests.executaCursorGetCboExame(654565466);
				//com dados
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (AacGradeAgendamenConsultas result1 : result) {
						logger.info("Seq = " + result1.getSeq());
						logger.info("ConvenioSus = " + result1.getConvenioSus());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	

	@Override
	protected void finalizeMocks() {
	}
}