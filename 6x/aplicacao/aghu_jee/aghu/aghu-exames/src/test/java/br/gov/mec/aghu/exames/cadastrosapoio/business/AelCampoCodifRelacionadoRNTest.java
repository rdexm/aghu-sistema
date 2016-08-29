package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelCampoCodifRelacionadoRN.AelCampoCodifRelacionadoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoCodifRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelCampoCodifRelacionadoRNTest extends AGHUBaseUnitTest<AelCampoCodifRelacionadoRN>{
	
	@Mock
	private AelCampoCodifRelacionadoDAO mockedAelCampoCodifRelacionadoDAO;
	@Mock
	private AelCampoLaudoDAO mockedAelCampoLaudoDAO;
	
	@Test
	public void testLancaExcecaoAEL_01314() {
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(null);

		try {
			final Integer calSeq = Integer.valueOf(1);
			this.systemUnderTest.verificarTipoCampo(calSeq);
			Assert.fail("Deveria lancar exception AEL_01314");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == AelCampoCodifRelacionadoRNExceptionCode.AEL_01314);
		}
	}
	
	@Test
	public void testLancaExcecaoAEL_01315() {
		final AelCampoLaudo campoLaudo = new AelCampoLaudo();
		campoLaudo.setSituacao(DominioSituacao.I);
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		try {
			final Integer calSeq = Integer.valueOf(1);
			this.systemUnderTest.verificarTipoCampo(calSeq);
			Assert.fail("Deveria lancar exception AEL_01315");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == AelCampoCodifRelacionadoRNExceptionCode.AEL_01315);
		}
	}
	
	@Test
	public void testLancaExcecaoAEL_01316() {
		final AelCampoLaudo campoLaudo = new AelCampoLaudo();
		campoLaudo.setSituacao(DominioSituacao.A);
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.A);
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		try {
			final Integer calSeq = Integer.valueOf(1);
			this.systemUnderTest.verificarTipoCampo(calSeq);
			Assert.fail("Deveria lancar exception AEL_01316");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == AelCampoCodifRelacionadoRNExceptionCode.AEL_01316);
		}
	}
	
	@Test
	public void testNaoLancaExcecao() {
		final AelCampoLaudo campoLaudo = new AelCampoLaudo();
		campoLaudo.setSituacao(DominioSituacao.A);
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		try {
			final Integer calSeq = Integer.valueOf(1);
			this.systemUnderTest.verificarTipoCampo(calSeq);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar exception");
		}
	}
	
	
}
