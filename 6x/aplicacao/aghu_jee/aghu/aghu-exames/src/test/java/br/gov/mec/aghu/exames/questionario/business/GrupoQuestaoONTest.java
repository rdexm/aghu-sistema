package br.gov.mec.aghu.exames.questionario.business;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.dao.AelGrupoQuestaoDAO;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoQuestaoONTest  extends AGHUBaseUnitTest<GrupoQuestaoON>{

	@Mock
	private GrupoQuestaoRN grupoQuestaoRNMocked;
	@Mock
	private AelGrupoQuestaoDAO aelGrupoQuestaoDAOMocked;

	@Test
	public void testVerificarDelecaoSuccess() {
		try {
			final AelGrupoQuestao grupo = new AelGrupoQuestao();

			Mockito.when(aelGrupoQuestaoDAOMocked.obterPorChavePrimaria(Mockito.any(Object.class))).thenReturn(grupo);
			systemUnderTest.remover(1);

		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarDelecaoSuccessComportamentoLista2() {
		try {
			final AelGrupoQuestao grupo = new AelGrupoQuestao();
			Set<AelQuestoesQuestionario> aelQuestoesQuestionarios = new HashSet<AelQuestoesQuestionario>();
			grupo.setAelQuestoesQuestionarios(aelQuestoesQuestionarios);

			Mockito.when(aelGrupoQuestaoDAOMocked.obterPorChavePrimaria(Mockito.any(Object.class))).thenReturn(grupo);
			systemUnderTest.remover(1);

		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test(expected=ApplicationBusinessException.class)
	public void testVerificarDelecaoError() throws ApplicationBusinessException{
		final AelGrupoQuestao grupo = new AelGrupoQuestao();
		Set<AelQuestoesQuestionario> aelQuestoesQuestionarios = new HashSet<AelQuestoesQuestionario>();
		aelQuestoesQuestionarios.add(new AelQuestoesQuestionario());
		grupo.setAelQuestoesQuestionarios(aelQuestoesQuestionarios);

		Mockito.when(aelGrupoQuestaoDAOMocked.obterPorChavePrimaria(Mockito.any(Object.class))).thenReturn(grupo);
		systemUnderTest.remover(1);
	}

}
