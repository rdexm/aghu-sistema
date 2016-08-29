package br.gov.mec.aghu.exames.cadastrosapoio.business;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelDescricoesResulPadraoRN.AelDescricoesResulPadraoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadoPadraoCampoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelDescricoesResulPadraoRNTest extends AGHUBaseUnitTest<AelDescricoesResulPadraoRN>{
	
	@Mock
	private AelCampoLaudoDAO mockedAelCampoLaudoDAO;
	
	private AelDescricoesResulPadrao obterValorDefault(){
		final AelDescricoesResulPadrao descResultaPadrao = new AelDescricoesResulPadrao();
		final AelResultadoPadraoCampo resultadoPadraoCampo = new AelResultadoPadraoCampo();
		final AelResultadoPadraoCampoId id = new AelResultadoPadraoCampoId();
		id.setRpaSeq(Integer.valueOf(1));
		id.setSeqp(Integer.valueOf(1));
		resultadoPadraoCampo.setId(id);
		descResultaPadrao.setResultadoPadraoCampo(resultadoPadraoCampo);
		
		return descResultaPadrao;
	}
	
	@Test
	public void testVerificarRNDrppVerTpCampoLancaExcecao_AEL_00780() {
		
		Mockito.when(mockedAelCampoLaudoDAO.obterResultadoPadraoTipoCampoESituacao(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

		try {
			AelDescricoesResulPadrao descResultaPadrao = this.obterValorDefault();
			this.systemUnderTest.verificarRNDrppVerTpCampo(descResultaPadrao);
			Assert.fail("Deveria ocorrer MECBaseException");
		} catch (BaseException e) {
			Assert.assertTrue(AelDescricoesResulPadraoRNExceptionCode.AEL_00780 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarRNDrppVerTpCampoLancaExcecao_AEL_00782() {
		final AelCampoLaudo campoLaudo = new AelCampoLaudo();
		campoLaudo.setSituacao(DominioSituacao.I);
		
		Mockito.when(mockedAelCampoLaudoDAO.obterResultadoPadraoTipoCampoESituacao(Mockito.anyInt(), Mockito.anyInt())).thenReturn(campoLaudo);
		
		try {
			AelDescricoesResulPadrao descResultaPadrao = this.obterValorDefault();
			this.systemUnderTest.verificarRNDrppVerTpCampo(descResultaPadrao);
			Assert.fail("Deveria ocorrer MECBaseException");
		} catch (BaseException e) {
			Assert.assertTrue(AelDescricoesResulPadraoRNExceptionCode.AEL_00782 == e.getCode());
		}
	}
	
}
