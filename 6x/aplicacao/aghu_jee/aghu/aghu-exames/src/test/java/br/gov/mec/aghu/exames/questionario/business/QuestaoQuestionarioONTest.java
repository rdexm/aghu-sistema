package br.gov.mec.aghu.exames.questionario.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class QuestaoQuestionarioONTest extends AGHUBaseUnitTest<QuestaoQuestionarioON>{

	@Mock
	private QuestaoQuestionarioRN grupoQuestaoQuestionarioRNMocked;
	@Mock
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAOMocked;

	@Test
	public void testPersistir() throws BaseException {
		try {

			systemUnderTest.persistir(new AelQuestoesQuestionario());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
}
