package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterDiagnosticoAtendimentoONTest extends AGHUBaseUnitTest<ManterDiagnosticoAtendimentoON>{

	@Mock
	private MpmCidAtendimentoDAO mockedMpmCidAtendimentoDAO;	

	/**
	 * Testa inclusão de atendimento null.
	 */
	@Test
	public void incluirAtendimentoNuloTest () {
		
		try {
			
			systemUnderTest.incluir(null, null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(ManterDiagnosticoAtendimentoON.ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO, 
					e.getCode());
		}
	}
	/**
	 * Testa a inclusão de atendimento.
	 */
	@Test
	public void incluirAtendimentoTest () {
		
		MpmCidAtendimento atendimento = new MpmCidAtendimento ();
		
		try {
			
			systemUnderTest.incluir(atendimento, null);
		} catch (ApplicationBusinessException e) {
			
			Assert.assertEquals(ManterDiagnosticoAtendimentoON.ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO, 
					e.getCode());
		}
	}	
	/**
	 * Testa exclusão de atendimento nulo.
	 */
	@Test
	public void excluirAtendimentoNuloTest () {
		
		try {
			
			systemUnderTest.excluir(null, null);
		} catch (ApplicationBusinessException e) {
			
			Assert.assertEquals(ManterDiagnosticoAtendimentoON.ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO, 
					e.getCode());
		}
	}
	/**
	 * Testa alteração de atendimento nulo.
	 */
	@Test
	public void alterarAtendimentoNuloTest () {
		
		try {
			
			systemUnderTest.alterar(null, null);
		} catch (ApplicationBusinessException e) {
			
			Assert.assertEquals(ManterDiagnosticoAtendimentoON.ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO, 
					e.getCode());
		}
	}
}
