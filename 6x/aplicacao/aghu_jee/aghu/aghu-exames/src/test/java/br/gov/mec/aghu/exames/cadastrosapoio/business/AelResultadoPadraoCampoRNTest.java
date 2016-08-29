package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelResultadoPadraoCampoRN.AelResultadoPadraoCampoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadoPadraoCampoId;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelResultadoPadraoCampoRNTest extends AGHUBaseUnitTest<AelResultadoPadraoCampoRN>{
	
	@Mock
	private AelResultadosPadraoDAO mockedAelResultadosPadraoDAO;
	@Mock
	private AelCampoLaudoDAO mockedAelCampoLaudoDAO;
	@Mock
	private AelResultadoCaracteristicaDAO mockedAelResultadoCaracteristicaDAO;
	@Mock
	private AelResultadoCodificadoDAO mockedAelResultadoCodificadoDAO;
	@Mock
	private AelResultadoPadraoCampoDAO mockedAelResultadoPadraoCampoDAO;
	
	private AelResultadoPadraoCampo obterAelResultadoPadraoCampo() {
		final AelResultadosPadrao resultadoPadrao = new AelResultadosPadrao();
		resultadoPadrao.setSeq(Integer.valueOf(1));
		final AelResultadoPadraoCampo resulPadraoCampo = new AelResultadoPadraoCampo();
		resulPadraoCampo.setResultadoPadrao(resultadoPadrao);

		return resulPadraoCampo;
	}
	
	
	private AelResultadosPadrao obterAelResultadosPadrao() {
		return new AelResultadosPadrao();
	}
	
	
	private AelCampoLaudo obterAelCampoLaudo() {
		return new AelCampoLaudo();
	}
	
	private AelCampoLaudo obterAelCampoLaudoComSeq() {
		AelCampoLaudo campoLaudo = new AelCampoLaudo();
		campoLaudo.setSeq(Integer.valueOf(1));
		
		return campoLaudo;
	}
	
	
	private AelResultadoCaracteristica obterAelResultadoCaracteristicaComSeq(){
		AelResultadoCaracteristica resultadoCaracteristica = new AelResultadoCaracteristica();
		resultadoCaracteristica.setSeq(Integer.valueOf(1));
		return resultadoCaracteristica;
	}
	
	
	private AelResultadoCodificado obterAelResultadoCodificadoComSeq() {
		AelResultadoCodificado resultadoCodificado = new AelResultadoCodificado();
		AelResultadoCodificadoId id = new AelResultadoCodificadoId();
		id.setGtcSeq(Integer.valueOf(1));
		id.setSeqp(Integer.valueOf(1));
		resultadoCodificado.setId(id);
		return resultadoCodificado;
	}
	
	
	private AelResultadoCaracteristica obterAelResultadoCaracteristicaSemSeq() {
		return new AelResultadoCaracteristica();
	}
		
	private AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristicaComSeq() {
		AelGrupoResultadoCaracteristica grupoResultCaracts = new AelGrupoResultadoCaracteristica();
		grupoResultCaracts.setSeq(Integer.valueOf(1));
		return grupoResultCaracts;
	}
		
	private AelParametroCamposLaudo obterAelParametroCamposLaudoComSeq() {
		AelParametroCamposLaudo parametroCampoLaudo = new AelParametroCamposLaudo();
		AelParametroCampoLaudoId id = new AelParametroCampoLaudoId();
		parametroCampoLaudo.setId(id);
		return parametroCampoLaudo;
	}
	
	
	@Test
	public void testExecutarRestricoesLancaExcecao_AEL_00669_01() {
		try {
			AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setParametroCampoLaudo(new AelParametroCamposLaudo());
			resultadoPadraoCampo.setCampoLaudo(new AelCampoLaudo());
			this.systemUnderTest.executarRestricoes(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00669");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00669 == e.getCode());
		}
	}
	
	@Test
	public void testExecutarRestricoesNaoLancaExcecao_AEL_00669_02() {
		try {
			AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setCampoLaudo(new AelCampoLaudo());
			this.systemUnderTest.executarRestricoes(resultadoPadraoCampo);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar a excecao AEL_00669");
		}
	}
	
	@Test
	public void testExecutarRestricoesNaoLancaExcecao_AEL_00669_03() {
		try {
			AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setParametroCampoLaudo(new AelParametroCamposLaudo());
			this.systemUnderTest.executarRestricoes(resultadoPadraoCampo);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar a excecao AEL_00669");
		}
	}
	
	
	@Test
	public void testExecutarRestricoesLancaExcecao_AEL_00670() {
		try {
			AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setResultadoCodificado(new AelResultadoCodificado());
			resultadoPadraoCampo.setResultadoCaracteristica(new AelResultadoCaracteristica());
			this.systemUnderTest.executarRestricoes(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00670");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00670 == e.getCode());
		}
	}
	
	@Test
	public void testExecutarRestricoesNaoLancaExcecao() {
		try {
			AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			this.systemUnderTest.executarRestricoes(resultadoPadraoCampo);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar Excecao");
		}
	}
	
	
	@Test
	public void testVerificarRNRpcpVerArcExmLancaExcecao_AEL_00814_01() {
		final AelResultadosPadrao resultadoPadrao = this.obterAelResultadosPadrao();
		resultadoPadrao.setExameMaterialAnalise(new AelExamesMaterialAnalise());
		Mockito.when(mockedAelResultadosPadraoDAO.obterOriginal(Mockito.anyInt())).thenReturn(resultadoPadrao);
		
		try {
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setId(new AelResultadoPadraoCampoId());
			resultadoPadraoCampo.setCampoLaudo(this.obterAelCampoLaudoComSeq());
			this.systemUnderTest.verificarRNRpcpVerArcExm(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00814");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00814 == e.getCode());
		}
	}
	
	
	@Test
	public void testVerificarRNRpcpVerArcExmNaoLancaExcecao() {
		Mockito.when(mockedAelResultadosPadraoDAO.obterOriginal(Mockito.anyInt())).thenReturn(null);

		try {
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setId(new AelResultadoPadraoCampoId());
			resultadoPadraoCampo.setCampoLaudo(new AelCampoLaudo());
			this.systemUnderTest.verificarRNRpcpVerArcExm(resultadoPadraoCampo);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar excecao");
		}
	}

	
	@Test
	public void testVerificarRNRpcpVerTipCampLancaExcecaoAEL_AEL_00804_01() {
		
		try {
			final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.E);
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setCampoLaudo(campoLaudo);
			resultadoPadraoCampo.setResultadoCaracteristica(this.obterAelResultadoCaracteristicaComSeq());
			this.systemUnderTest.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00804");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00804 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarRNRpcpVerTipCampLancaExcecaoAEL_AEL_00804_02() {
		
		try {
			final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.E);
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setCampoLaudo(campoLaudo);
			resultadoPadraoCampo.setResultadoCaracteristica(this.obterAelResultadoCaracteristicaSemSeq());
			resultadoPadraoCampo.setResultadoCodificado(this.obterAelResultadoCodificadoComSeq());
			this.systemUnderTest.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00804");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00804 == e.getCode());
		}
	}
	
	
	@Test
	public void testVerificarRNRpcpVerTipCampLancaExcecaoAEL_AEL_00803() {
		
		try {
			final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setCampoLaudo(campoLaudo);
			this.systemUnderTest.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00803");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00803 == e.getCode());
		}
	}
	
	
	@Test
	public void testVerificarRNRpcpVerTipCampLancaExcecaoAEL_AEL_00806() {
		
		Mockito.when(mockedAelResultadoCaracteristicaDAO.obterResultadoCaracteristicaPorTipoCampo(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);
		
		try {
			final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
			campoLaudo.setGrupoResultadoCaracteristica(this.obterAelGrupoResultadoCaracteristicaComSeq());
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setCampoLaudo(campoLaudo);
			resultadoPadraoCampo.setParametroCampoLaudo(this.obterAelParametroCamposLaudoComSeq());
			resultadoPadraoCampo.setResultadoCaracteristica(this.obterAelResultadoCaracteristicaSemSeq());
			resultadoPadraoCampo.setResultadoCodificado(this.obterAelResultadoCodificadoComSeq());
			this.systemUnderTest.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00806");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00806 == e.getCode());
		}
	}
	
	
	@Test
	public void testVerificarRNRpcpVerTipCampLancaExcecaoAEL_AEL_00868() {
		final AelResultadoCaracteristica resultadoCaracteristica =	this.obterAelResultadoCaracteristicaSemSeq();
		resultadoCaracteristica.setIndSituacao(DominioSituacao.I);
		
		Mockito.when(mockedAelResultadoCaracteristicaDAO.obterResultadoCaracteristicaPorTipoCampo(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(resultadoCaracteristica);
		
		try {
			final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
			campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
			campoLaudo.setGrupoResultadoCaracteristica(this.obterAelGrupoResultadoCaracteristicaComSeq());
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setCampoLaudo(campoLaudo);
			resultadoPadraoCampo.setParametroCampoLaudo(this.obterAelParametroCamposLaudoComSeq());
			resultadoPadraoCampo.setResultadoCaracteristica(this.obterAelResultadoCaracteristicaSemSeq());
			resultadoPadraoCampo.setResultadoCodificado(this.obterAelResultadoCodificadoComSeq());
			this.systemUnderTest.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
			Assert.fail("Deveria lancar a excecao AEL_00868");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_00868 == e.getCode());
		}
	}

	@Test
	public void testVerificarRNRpcpVerDuplic() {
		Mockito.when(mockedAelResultadoPadraoCampoDAO.obterResultadoPadraoCampoPorParametro(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(new AelResultadoPadraoCampo());

		try {
			final AelResultadoPadraoCampo resultadoPadraoCampo = this.obterAelResultadoPadraoCampo();
			resultadoPadraoCampo.setId(new AelResultadoPadraoCampoId());
			resultadoPadraoCampo.setCampoLaudo(this.obterAelCampoLaudo());
			this.systemUnderTest.verificarRNRpcpVerDuplic(resultadoPadraoCampo);
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoPadraoCampoRNExceptionCode.AEL_01072 == e.getCode());
		}
	}
	
}
