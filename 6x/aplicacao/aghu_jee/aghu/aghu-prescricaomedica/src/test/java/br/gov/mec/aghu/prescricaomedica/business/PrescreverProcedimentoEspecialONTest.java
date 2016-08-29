package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PrescreverProcedimentoEspecialONTest extends AGHUBaseUnitTest<PrescreverProcedimentosEspeciaisON>{
	
	/**
	 * Deve gerar exceção para especiais diversos.
	 */
	@Test
	public void validaCamposObrigatorios001() {
		
		try {
		
			MpmPrescricaoProcedimento prescProc = new MpmPrescricaoProcedimento();
			this.systemUnderTest.validaCamposObrigatorios(prescProc, DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
			fail("Deveria ter ocorrido uma exceção!!!");
		
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("ERRO_CAMPO_ESPECIAL_DIVERSAS_OBRIGATORIO", atual.getMessage());
		
		}		
		
	}
	
	/**
	 * Deve gerar exceção para procedimentos realizados no leito.
	 */
	@Test
	public void validaCamposObrigatorios002() {
		
		try {
		
			MpmPrescricaoProcedimento prescProc = new MpmPrescricaoProcedimento();
			this.systemUnderTest.validaCamposObrigatorios(prescProc, DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO);
			fail("Deveria ter ocorrido uma exceção!!!");
		
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("ERRO_CAMPO_PROCEDIMENTO_NO_LEITO_OBRIGATORIO", atual.getMessage());
		
		}		
		
	}
	
	/**
	 * Deve gerar exceção para orteses e proteses.
	 */
	@Test
	public void validaCamposObrigatorios003() {
		
		try {
		
			MpmPrescricaoProcedimento prescProc = new MpmPrescricaoProcedimento();
			this.systemUnderTest.validaCamposObrigatorios(prescProc, DominioTipoProcedimentoEspecial.ORTESES_PROTESES);
			fail("Deveria ter ocorrido uma exceção!!!");
		
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("ERRO_CAMPO_ORTESES_OBRIGATORIO", atual.getMessage());
		
		}		
		
	}
	

}
