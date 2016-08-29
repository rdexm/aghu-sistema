package br.gov.mec.aghu.exames.business;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.AelResultadoExameRN.AelResultadoExameRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelResultadoExameRNTest extends AGHUBaseUnitTest<AelResultadoExameRN>{

	@Mock
	private AelResultadoExameDAO mockedAelResultadoExameDAO;
	@Mock
	private AelCampoLaudoDAO mockedAelCampoLaudoDAO;
	@Mock
	private AelResultadoCaracteristicaDAO mockedAelResultadoCaracteristicaDAO;
	@Mock
	private AelResultadoCodificadoDAO mockedAelResultadoCodificadoDAO;
	
	
	private AelResultadoExame obterAelResultadoExame() {
		AelResultadoExame resultadoExame = new AelResultadoExame();
		AelResultadoExameId id = new AelResultadoExameId();
		resultadoExame.setId(id);
		return resultadoExame; 
	}
	
	
	private AelResultadoCodificado obterAelResultadoCodificado() {
		return new AelResultadoCodificado();
	}
	
	private void atribuirAelResultadoCaracteristica(AelResultadoExame elemento) {
		elemento.setResultadoCaracteristica(new AelResultadoCaracteristica());
	}
	
	private void atribuirAelGrupoResultadoCaracteristica(AelCampoLaudo elemento) {
		elemento.setGrupoResultadoCaracteristica(new AelGrupoResultadoCaracteristica());
	}
	
	private void atribuirAelGrupoResultadoCodificado(AelCampoLaudo elemento) {
		elemento.setGrupoResultadoCodificado(new AelGrupoResultadoCodificado());
	}
	
	private void atribuirAelResultadoCodificado(AelResultadoExame elemento) {
		elemento.setResultadoCodificado(new AelResultadoCodificado());
	}
	
	private void atribuirAelResultadoCodificadoComId(AelResultadoExame elemento) {
		elemento.setResultadoCodificado(new AelResultadoCodificado());
		elemento.getResultadoCodificado().setId(new AelResultadoCodificadoId());
	}
	
	private AelCampoLaudo obterAelCampoLaudo() {
		return new AelCampoLaudo();
	}
	
	private AelResultadoCaracteristica obterAelResultadoCaracteristica() {
		return new AelResultadoCaracteristica();
	}
	
	
	@Test
	public void testVerificarTipoCampoLaudoNulo() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(null);
		
		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
			Assert.fail("Nao deveria lancar a excecao AEL_00869");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoExameRNExceptionCode.AEL_00869 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarTipoCampoLaudoCodificado() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		this.atribuirAelResultadoCaracteristica(elemento);
		this.atribuirAelResultadoCodificado(elemento);
		
		final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.E);
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
			Assert.fail("Nao deveria lancar a excecao AEL_00804");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoExameRNExceptionCode.AEL_00804 == e.getCode());
		}
	}
		
	
	@Test
	public void testVerificarTipoCampoResultadoCaracteristicaInativo() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		elemento.setId(new AelResultadoExameId());
				
		final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
		this.atribuirAelGrupoResultadoCaracteristica(campoLaudo);
		
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);
		
		final AelResultadoCaracteristica resultadoCaracteristica = this.obterAelResultadoCaracteristica();
		resultadoCaracteristica.setIndSituacao(DominioSituacao.I);
		Mockito.when(mockedAelResultadoCaracteristicaDAO.obterResultadoCaracteristicaPorTipoCampo(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(resultadoCaracteristica);
		
		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
			Assert.fail("Nao deveria lancar a excecao AEL_00868");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoExameRNExceptionCode.AEL_00868 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarTipoCampoResultadoCodificadoInativo() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		elemento.setId(new AelResultadoExameId());
		this.atribuirAelResultadoCodificadoComId(elemento);
		
		
		final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.C);
		this.atribuirAelGrupoResultadoCaracteristica(campoLaudo);
		this.atribuirAelGrupoResultadoCodificado(campoLaudo);
		
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		final AelResultadoCaracteristica resultadoCaracteristica = this.obterAelResultadoCaracteristica();
		resultadoCaracteristica.setIndSituacao(DominioSituacao.A);
		Mockito.when(mockedAelResultadoCaracteristicaDAO.obterResultadoCaracteristicaPorTipoCampo(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(resultadoCaracteristica);
		
		final AelResultadoCodificado resultadoCodificado = this.obterAelResultadoCodificado();
		resultadoCodificado.setSituacao(DominioSituacao.I);
		Mockito.when(mockedAelResultadoCodificadoDAO.obterOriginal(Mockito.any(AelResultadoCodificadoId.class))).thenReturn(resultadoCodificado);

		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
			Assert.fail("Nao deveria lancar a excecao AEL_00867");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoExameRNExceptionCode.AEL_00867 == e.getCode());
		}
	}
	
		
	@Test
	public void testVerificarTipoCampoTipoNumericoInformado() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		elemento.setId(new AelResultadoExameId());
				
		final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.N);
				
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
			Assert.fail("Nao deveria lancar a excecao AEL_00801");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoExameRNExceptionCode.AEL_00801 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarTipoCampoTipoTextoNaoPermitido() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		elemento.setId(new AelResultadoExameId());
				
		final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
		campoLaudo.setTipoCampo(DominioTipoCampoCampoLaudo.T);
				
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);

		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
			Assert.fail("Nao deveria lancar a excecao AEL_00802");
		} catch (BaseException e) {
			Assert.assertTrue(AelResultadoExameRNExceptionCode.AEL_00802 == e.getCode());
		}
	}
	
	@Test
	public void testVerificarTipoCampoNaoLancaExcecao() {
		final AelResultadoExame elemento = this.obterAelResultadoExame();
		elemento.setId(new AelResultadoExameId());
				
		final AelCampoLaudo campoLaudo = this.obterAelCampoLaudo();
				
		Mockito.when(mockedAelCampoLaudoDAO.obterCampoLaudoPorSeq(Mockito.anyInt())).thenReturn(campoLaudo);
				
		try {
			this.systemUnderTest.verificarTipoCampo(elemento);
		} catch (BaseException e) {
			Assert.fail("Nao deveria lancar excecao");
		}
	}
	
}
