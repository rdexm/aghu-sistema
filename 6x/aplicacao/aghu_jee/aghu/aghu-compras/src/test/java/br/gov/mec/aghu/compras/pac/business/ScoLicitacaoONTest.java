package br.gov.mec.aghu.compras.pac.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoQuadroAprovacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoQuadroAprovacaoVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoVO;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Teste Unitário do Serviço de Licitação
 * 
 * @author mlcruz
 */
public class ScoLicitacaoONTest extends AGHUBaseUnitTest<ScoLicitacaoON>{	
	
	/** DAO Mockeado */
	@Mock
	private ScoLicitacaoDAO licitacaoDao;
	
	/** DAO de Itens da Licitação Mockeado */
	@Mock
	private ScoItemLicitacaoDAO itemLicitacaoDao;

	/** DAO de Itens de Licitação para Quadro de Aprovação Mockeado */
	@Mock
	private ScoItemLicitacaoQuadroAprovacaoDAO itemLicitacaoQuadroAprovacaoDAO;
	
	/** DAO de Parcelas de Pagamento Mockeado */
	@Mock
	private ScoParcelasPagamentoDAO parcelasPagamentoDao;
	
	/** Façade de Parâemtros Mockeada */
	@Mock
	private IParametroFacade parametroFacade;
	
	/**
	 * Testa encaminhamento de licitação ao parecer técnico.
	 */
	@Test
	public void testEncaminhamentoLicitacaoParecerTecnico() {
		final ScoLicitacao licitacao = new ScoLicitacao();
		
		Mockito.when(licitacaoDao.atualizar(licitacao)).thenReturn(licitacao);

		systemUnderTest.encaminharParecerTecnico(licitacao);

		assertEquals(DominioSituacaoLicitacao.PT, licitacao.getSituacao());
		
		assertEquals(DateUtil.truncaData(new Date()),
				DateUtil.truncaData(licitacao.getDtEnvioParecTec()));
	}
	
	/**
	 * Testa encaminhamento de licitação à comissão.
	 */
	@Test
	public void testEncaminhamentoLicitacaoComissao() {
		final ScoLicitacao licitacao = new ScoLicitacao();
		
		Mockito.when(licitacaoDao.atualizar(licitacao)).thenReturn(licitacao);
		
		systemUnderTest.encaminharComissao(licitacao);

		assertEquals(DominioSituacaoLicitacao.CL, licitacao.getSituacao());
		
		assertEquals(DateUtil.truncaData(new Date()),
				DateUtil.truncaData(licitacao.getDtEnvioComisLicit()));
	}
	
	/**
	 * Testa pesquisa de PAC's para julgamento.
	 */
	@Test
	public void testPesquisaPacsParaJulgamento() {
		// Critério
		final PacParaJulgamentoCriteriaVO criteria = new PacParaJulgamentoCriteriaVO();
		final int first = 0, max = 100;
		final String order = null;
		final boolean asc = true;
		
		// Resultado
		final List<PacParaJulgamentoVO> resultA = new ArrayList<PacParaJulgamentoVO>();
		final PacParaJulgamentoVO pacX = new PacParaJulgamentoVO();
		pacX.setNumero(36);
		resultA.add(pacX);
		final Long qtdItens = 2l, qtdItensJulgados = 1l; 
		
		Mockito.when(licitacaoDao.pesquisarPacsParaJulgamento(criteria, first, max, order, asc)).thenReturn(resultA);
		
		Mockito.when(itemLicitacaoDao.contarItensLicitacao(pacX.getNumero())).thenReturn(qtdItens);
		
		Mockito.when(itemLicitacaoDao.contarItensLicitacaoJulgados(pacX.getNumero())).thenReturn(qtdItensJulgados);
		
		List<PacParaJulgamentoVO> resultB = systemUnderTest
				.pesquisarPacsParaJulgamento(criteria, first, max, order, asc);
		
		PacParaJulgamentoVO pacY = resultB.get(0);
		assertEquals(qtdItens, Long.valueOf(pacY.getQtdeItens()));
		assertEquals(qtdItensJulgados, Long.valueOf(pacY.getQtdeItensJulgados()));
	}
	
	/**
	 * Testa encaminhamento de PAC's ao comprador.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testEncaminhamentoPacsComprador() throws ApplicationBusinessException {
		final Integer ID = 36;
		final Set<Integer> pacIds = new HashSet<Integer>();
		pacIds.add(ID);
		final ScoLicitacao pojo = new ScoLicitacao();
		
		Mockito.when(licitacaoDao.obterPorChavePrimaria(ID)).thenReturn(pojo);
		
		Mockito.when(licitacaoDao.atualizar(pojo)).thenReturn(pojo);

		systemUnderTest.encaminharComprador(pacIds, false);
		assertEquals(DominioSituacaoLicitacao.GR, pojo.getSituacao());
	}
	
	/**
	 * Testa encaminhamento de PAC's julgados ao comprador.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testEncaminhamentoPacsJulgadosComprador() throws ApplicationBusinessException {
		final Integer ID = 36;
		final Set<Integer> pacIds = new HashSet<Integer>();
		pacIds.add(ID);
		final ScoLicitacao pojo = new ScoLicitacao();
		pojo.setSituacao(DominioSituacaoLicitacao.JU);
		
		
		Mockito.when(licitacaoDao.obterPorChavePrimaria(ID)).thenReturn(pojo);
		
		
		systemUnderTest.encaminharComprador(pacIds, false);
	}
	
	/**
	 * Testa pesquisa de PAC's para quadro de aprovação.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testPesquisaPacsQuadroAprovacao() throws ApplicationBusinessException {
		final Integer id = 108397;
		final Set<Integer> ids = new HashSet<Integer>();
		ids.addAll(Arrays.asList(id));
		
		ScoModalidadeLicitacao modalidade = new ScoModalidadeLicitacao();
		modalidade.setCodigo("TP");
		
		final AghParametros membroClParam = new AghParametros();
		final String nomeAssinatura1 = "Membro A";
		membroClParam.setVlrTexto(nomeAssinatura1);
		final String deptoAssinatura1 = "Depto. X";
		membroClParam.setDescricao(deptoAssinatura1);
		
		final ItemLicitacaoQuadroAprovacaoVO itemSemProposta = new ItemLicitacaoQuadroAprovacaoVO();
		itemSemProposta.setNumeroPac(id);
		itemSemProposta.setModalidade(modalidade);
		itemSemProposta.setNumeroItem((short) 3);
		itemSemProposta.setMotivoCancelamento(DominioMotivoCancelamentoComissaoLicitacao.CD);
		
		final ItemLicitacaoQuadroAprovacaoVO itemComProposta = new ItemLicitacaoQuadroAprovacaoVO();
		itemComProposta.setNumeroPac(id);
		itemComProposta.setModalidade(modalidade);
		itemComProposta.setNumeroItem((short) 1);
		itemComProposta.setExcluido(true);
		itemComProposta.setSituacaoJulgamento(DominioSituacaoJulgamento.JU);
		
		final ItemLicitacaoQuadroAprovacaoVO itemComPropostaEscolhida = new ItemLicitacaoQuadroAprovacaoVO();
		itemComPropostaEscolhida.setNumeroPac(id);
		itemComPropostaEscolhida.setModalidade(modalidade);
		itemComPropostaEscolhida.setNumeroItem((short) 2);
		itemComPropostaEscolhida.setCondicao(new ScoCondicaoPagamentoPropos());
		
		Mockito.when(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_1)).thenReturn(true);
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_1)).thenReturn(membroClParam);

		Mockito.when(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_2)).thenReturn(true);
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_2)).thenReturn(new AghParametros());

		Mockito.when(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_3)).thenReturn(true);
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_3)).thenReturn(new AghParametros());

		Mockito.when(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_4)).thenReturn(true);
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_4)).thenReturn(new AghParametros());

		Mockito.when(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_5)).thenReturn(true);
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_5)).thenReturn(new AghParametros());

		Mockito.when(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_6)).thenReturn(true);
		Mockito.when(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_6)).thenReturn(new AghParametros());

		Mockito.when(itemLicitacaoQuadroAprovacaoDAO.pesquisarItensSemProposta(ids, true)).thenReturn(Arrays.asList(itemSemProposta));
		
		Mockito.when(itemLicitacaoQuadroAprovacaoDAO.pesquisarItensComProposta(ids, true)).thenReturn(Arrays.asList(itemComProposta));

		Mockito.when(itemLicitacaoQuadroAprovacaoDAO.pesquisarItensComPropostaEscolhida(ids, true)).thenReturn(Arrays.asList(itemComPropostaEscolhida));

		Mockito.when(parcelasPagamentoDao.contarParcelasPagamentoProposta(itemComPropostaEscolhida.getCondicao())).thenReturn(Long.valueOf("3"));
		
		Iterator<ItemLicitacaoQuadroAprovacaoVO> result = systemUnderTest
				.pesquisarPacsQuadroAprovacao(ids, true).iterator();
		
		ItemLicitacaoQuadroAprovacaoVO next = result.next();
		assertEquals(itemComProposta.getNumeroItem(), next.getNumeroItem());
		assertNull(itemComProposta.getMotivo());
		assertNotSame(true, itemComProposta.getPendente());
		
		next = result.next();
		assertEquals(itemComPropostaEscolhida.getNumeroItem(), next.getNumeroItem());
		assertEquals((short) 3, (short) next.getParcelas());
		
		next = result.next();
		assertEquals(itemSemProposta.getNumeroItem(), next.getNumeroItem());
		assertEquals(true, itemSemProposta.getSemProposta());
		assertEquals(DominioMotivoCancelamentoComissaoLicitacao.CD.getDescricao(), itemSemProposta.getMotivo());
	}
	
}
