package br.gov.mec.aghu.exames.questionario.business;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.dao.AelValorValidoQuestaoDAO;
import br.gov.mec.aghu.exames.questionario.business.ValorValidoQuestaoON.ValorValidoQuestaoONExceptionCode;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelValorValidoQuestao;
import br.gov.mec.aghu.model.AelValorValidoQuestaoId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ValorValidoQuestaoONTest extends AGHUBaseUnitTest<ValorValidoQuestaoON>{

	@Mock
	private AelValorValidoQuestaoDAO mockedAelValorValidoQuestaoDAO;
	
	
	@Test
	public void testValidarRelacionamentosValorValidoBeforeDeleteSuccess() {
		Integer qaoSeq = 10;
		Short seqP = Short.valueOf("1");
		
		Set<AelRespostaQuestao> respostas = new HashSet<AelRespostaQuestao>();
		
		final AelValorValidoQuestao valorValido = new AelValorValidoQuestao();
		valorValido.setAelRespostaQuestoes(respostas);
		
		try {
			Mockito.when(mockedAelValorValidoQuestaoDAO.obterPorChavePrimaria(Mockito.any(AelValorValidoQuestaoId.class))).thenReturn(valorValido);
			
			systemUnderTest.validarRelacionamentosValorValidoBeforeDelete(qaoSeq, seqP);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testValidarRelacionamentosValorValidoBeforeDeleteError() {
		Integer qaoSeq = 10;
		Short seqP = Short.valueOf("1");
		
		AelRespostaQuestao resposta = new AelRespostaQuestao();
		Set<AelRespostaQuestao> respostas = new HashSet<AelRespostaQuestao>();
		respostas.add(resposta);
		
		final AelValorValidoQuestao valorValido = new AelValorValidoQuestao();
		valorValido.setAelRespostaQuestoes(respostas);
		
		try {
			Mockito.when(mockedAelValorValidoQuestaoDAO.obterPorChavePrimaria(Mockito.any(AelValorValidoQuestaoId.class))).thenReturn(valorValido);
			
			systemUnderTest.validarRelacionamentosValorValidoBeforeDelete(qaoSeq, seqP);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), ValorValidoQuestaoONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS);
		}
	}
	
}
