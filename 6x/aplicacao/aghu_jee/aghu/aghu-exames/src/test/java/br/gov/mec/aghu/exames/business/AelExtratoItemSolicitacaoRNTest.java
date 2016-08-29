package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.AelExtratoItemSolicitacaoRN.AelExtratoItemSolicitacaoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacaoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelExtratoItemSolicitacaoRNTest extends AGHUBaseUnitTest<AelExtratoItemSolicitacaoRN>{
	
	@Mock
	private AelExtratoItemSolicitacaoDAO mockedAelExtratoItemSolicitacaoDAO; 
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private AelMatrizSituacaoDAO mockedAelMatrizSituacaoDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	
	/**
	 * Verifica se a situacao e <br>
	 * esta diferente de Ativa(A).
	 */
	@Test
	public void verificarSituacaoInvalida() {
		final AelSitItemSolicitacoes entityMocked = new AelSitItemSolicitacoes();
		entityMocked.setIndSituacao(DominioSituacao.I);
		final AelExtratoItemSolicitacao extratoItemSolicitacao = new AelExtratoItemSolicitacao();
		extratoItemSolicitacao.setAelSitItemSolicitacoes(entityMocked);
		
		try {
			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(extratoItemSolicitacao.getAelSitItemSolicitacoes().getCodigo())).thenReturn(entityMocked);
			
			systemUnderTest.verificarSituacao(extratoItemSolicitacao);
			Assert.fail("Deveria ocorrer ApplicationBusinessException...");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == AelExtratoItemSolicitacaoRNExceptionCode.AEL_00420);
		}
	}
	
	/**
	 * Verifica se a situacao e <br>
	 * esta Ativa(A).
	 */
	@Test
	public void verificarSituacaoValida() {
		final AelExtratoItemSolicitacao extratoItemSolicitacao = new AelExtratoItemSolicitacao();
		extratoItemSolicitacao.setAelSitItemSolicitacoes(new AelSitItemSolicitacoes());
		final AelSitItemSolicitacoes entityMocked = new AelSitItemSolicitacoes();
		entityMocked.setIndSituacao(DominioSituacao.A);
		
		try {
			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(entityMocked);

			systemUnderTest.verificarSituacao(extratoItemSolicitacao);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer ApplicationBusinessException...");
		}
	}
	
	/**
	 * Verifica se a situacao do extrato nao e igual a situacao<br>
	 * do item novo.  
	 * 
	 */
	@Test
	public void verificarSituacaoExtratoDiferente() {
		final AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setCodigo("AC");
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		itemSolicitacaoExame.setId(id);
		
		final AelExtratoItemSolicitacao entityMocked = new AelExtratoItemSolicitacao();
		entityMocked.setAelSitItemSolicitacoes(aelSitItemSolicitacoes);
		entityMocked.setItemSolicitacaoExame(itemSolicitacaoExame);
					
		final AelSitItemSolicitacoes aelSitItemSolicitacoesDif = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoesDif.setCodigo("AG");
		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(aelSitItemSolicitacoesDif);
		
		try {
			Mockito.when(mockedAelItemSolicitacaoExameDAO.obterOriginal(id)).thenReturn(itemSolicitacaoExames);
			
			systemUnderTest.verificarSituacaoExtrato(entityMocked);

		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == AelExtratoItemSolicitacaoRNExceptionCode.AEL_00496);
		}
	}
	
	/**
	 * Verifica se a situacao do extrato  e igual a situacao<br>
	 * do item novo.  
	 * 
	 */
	@Test
	public void verificarSituacaoExtrato() {
		final AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setCodigo("AC");
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		itemSolicitacaoExame.setId(id);
		
		final AelExtratoItemSolicitacao entityMocked = new AelExtratoItemSolicitacao();
		entityMocked.setAelSitItemSolicitacoes(aelSitItemSolicitacoes);
		entityMocked.setItemSolicitacaoExame(itemSolicitacaoExame);
					
		final AelSitItemSolicitacoes aelSitItemSolicitacoesDif = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoesDif.setCodigo("AC");
		final AelItemSolicitacaoExames itemSolicitacaoExames = new AelItemSolicitacaoExames();
		itemSolicitacaoExames.setSituacaoItemSolicitacao(aelSitItemSolicitacoesDif);
		
		try {
			
			Mockito.when(mockedAelItemSolicitacaoExameDAO.obterOriginal(id)).thenReturn(itemSolicitacaoExames);

			systemUnderTest.verificarSituacaoExtrato(entityMocked);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria ocorrer ApplicationBusinessException...");
		}
	}
	
	@Test
	public void verificarAreaExecutora() {
		final AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setCodigo("AE");
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		itemSolicitacaoExame.setId(id);
		
		final AelExtratoItemSolicitacao entityMocked = new AelExtratoItemSolicitacao();
		entityMocked.setAelSitItemSolicitacoes(aelSitItemSolicitacoes);
		entityMocked.setItemSolicitacaoExame(itemSolicitacaoExame);
	
		try {
			AghParametros aghParametro = new AghParametros();
			aghParametro.setVlrTexto("AE");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);

			systemUnderTest.verificarAreaExecutora(entityMocked);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Nao deveria ocorrer ApplicationBusinessException...");
		}
		
	}
	
	@Test
	public void verificarAreaExecutoraSituacaoDiferente() {
		final AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setCodigo("AC");
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		itemSolicitacaoExame.setId(id);
		
		final AelExtratoItemSolicitacao entityMocked = new AelExtratoItemSolicitacao();
		entityMocked.setAelSitItemSolicitacoes(aelSitItemSolicitacoes);
		entityMocked.setItemSolicitacaoExame(itemSolicitacaoExame);
	
		try {
			AghParametros aghParametro = new AghParametros();
			aghParametro.setVlrTexto("AE");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);

			systemUnderTest.verificarAreaExecutora(entityMocked);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Nao deveria ocorrer ApplicationBusinessException...");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void verificarMotivoCancelamento() {
		final AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setCodigo("CA");
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		itemSolicitacaoExame.setId(id);
		
		final AelExtratoItemSolicitacao entityMocked = new AelExtratoItemSolicitacao();
		entityMocked.setAelSitItemSolicitacoes(aelSitItemSolicitacoes);
		entityMocked.setItemSolicitacaoExame(itemSolicitacaoExame);
		entityMocked.setAelMotivoCancelaExames(new AelMotivoCancelaExames());
		AelExtratoItemSolicitacaoId idItemSolic = new AelExtratoItemSolicitacaoId();
		entityMocked.setId(idItemSolic);
	
		try {
			AghParametros aghParametro = new AghParametros();
			aghParametro.setVlrTexto("CA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);

			List<AelExtratoItemSolicitacao> extratoItemSolicList = new ArrayList<AelExtratoItemSolicitacao>();
			AelExtratoItemSolicitacao elementToAdd = new AelExtratoItemSolicitacao();
			AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
			sitItemSolicitacoes.setCodigo("PX");
			elementToAdd.setAelSitItemSolicitacoes(sitItemSolicitacoes);
			extratoItemSolicList.add(elementToAdd);
			Mockito.when(mockedAelExtratoItemSolicitacaoDAO.buscaAelExtratoItemSolicitacaoAnteriores(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort()))
			.thenReturn(extratoItemSolicList);

			AelMatrizSituacao matrizSituacao = new AelMatrizSituacao();
			matrizSituacao.setIndExigeMotivoCanc(Boolean.TRUE);
			Mockito.when(mockedAelMatrizSituacaoDAO.obterPorTransicao(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(matrizSituacao);
						
			systemUnderTest.verificarMotivoCancelamento(entityMocked);
			Assert.fail("Deveria ocorrer BaseException...");
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode() == AelExtratoItemSolicitacaoRNExceptionCode.AEL_00486);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void verificarMotivoCancelamentoDiferente() {
		final AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
		aelSitItemSolicitacoes.setCodigo("CA");
		final AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		itemSolicitacaoExame.setId(id);
		
		final AelExtratoItemSolicitacao entityMocked = new AelExtratoItemSolicitacao();
		entityMocked.setAelSitItemSolicitacoes(aelSitItemSolicitacoes);
		entityMocked.setItemSolicitacaoExame(itemSolicitacaoExame);
		entityMocked.setAelMotivoCancelaExames(new AelMotivoCancelaExames());
		AelExtratoItemSolicitacaoId idItemSolic = new AelExtratoItemSolicitacaoId();
		entityMocked.setId(idItemSolic);
	
		try {
			AghParametros aghParametro = new AghParametros();
			aghParametro.setVlrTexto("CA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(aghParametro);

			List<AelExtratoItemSolicitacao> extratoItemSolicList = new ArrayList<AelExtratoItemSolicitacao>();
			AelExtratoItemSolicitacao elementToAdd = new AelExtratoItemSolicitacao();
			AelSitItemSolicitacoes sitItemSolicitacoes = new AelSitItemSolicitacoes();
			sitItemSolicitacoes.setCodigo("PX");
			elementToAdd.setAelSitItemSolicitacoes(sitItemSolicitacoes);
			extratoItemSolicList.add(elementToAdd);
			Mockito.when(mockedAelExtratoItemSolicitacaoDAO.buscaAelExtratoItemSolicitacaoAnteriores(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyShort()))
			.thenReturn(extratoItemSolicList);

			AelMatrizSituacao matrizSituacao = new AelMatrizSituacao();
			matrizSituacao.setIndExigeMotivoCanc(Boolean.FALSE);
			Mockito.when(mockedAelMatrizSituacaoDAO.obterPorTransicao(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(matrizSituacao);
						
			systemUnderTest.verificarMotivoCancelamento(entityMocked);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail("Nao deveria ocorrer BaseException...");
		}
		
	}
	
}
