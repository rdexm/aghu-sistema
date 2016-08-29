package br.gov.mec.aghu.compras.parecer.business;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário de ON responsável por análise técnica de item de proposta de
 * fornecedor.
 * 
 * @author mlcruz
 */
public class ScoParecerItemPropostaFornecedorONTest extends AGHUBaseUnitTest<ScoParecerItemPropostaFornecedorON>{
	
	@Mock	
	/** DAO de Item de AF Mockeado */
	private ScoItemAutorizacaoFornDAO itemAfDao;
	
	@Mock
	/** DAO de Item da Proposta Mockeado */
	private ScoItemPropostaFornecedorDAO itemPropostaDao;
	
	@Mock
	/** DAO de Item da Licitação */
	private ScoItemLicitacaoDAO itemLicitacaoDao;
	
	@Mock
	private IPacFacade pacFacade;
	
	@Mock
	private ScoAutorizacaoFornDAO autorizacaoFornDAO;
	
	/**
	 * Testa gravação de análise técnica.
	 * @throws BaseException 
	 */
	@Test
	public void testGravacaoAnaliseTecnica() throws BaseException {
		final ScoItemPropostaFornecedor item = new ScoItemPropostaFornecedor();
		final ScoItemLicitacao itemLicitacao = new ScoItemLicitacao();
		itemLicitacao.setId(new ScoItemLicitacaoId(1, (short) 2));
		
		item.setId(new ScoItemPropostaFornecedorId(1, 2, (short) 3));
		item.setItemLicitacao(itemLicitacao);
		item.setMotDesclassif(DominioMotivoDesclassificacaoItemProposta.DI);
		item.setJustifAutorizUsr("Teste");
		
		Mockito.when(itemLicitacaoDao.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(1, (short) 2)).thenReturn(itemLicitacao);
		
		Mockito.when(autorizacaoFornDAO.verificarItemPropostaEmAf(item.getId().getPfrLctNumero(), 
				item.getId().getPfrFrnNumero())).thenReturn(true);

		Mockito.when(itemAfDao.verificarPropostaEmItemAfExcluido(item.getId().getPfrLctNumero(), 
				item.getId().getPfrFrnNumero(),
				item.getId().getNumero())).thenReturn(true);

		Mockito.when(itemPropostaDao.verificarItemPossuiPropostaEscolhida(item)).thenReturn(false);

		systemUnderTest.gravarAnaliseTecnica(item);
		
		assertEquals(Boolean.FALSE, item.getItemLicitacao().getPropostaEscolhida());
	}
	
	/**
	 * Testa gravação de análise técnica, retirando motivo de desclassificação
	 * para item com proposta já escolhida.
	 * @throws BaseException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testGravacaoAnaliseTecnicaSemMotivoDesclassificacaoPropostaEscolhida() 
			throws BaseException {
		ScoItemPropostaFornecedorId id = new ScoItemPropostaFornecedorId(1, 2, (short) 3);
		final ScoItemLicitacao itemLicitacao = new ScoItemLicitacao();
		itemLicitacao.setId(new ScoItemLicitacaoId(1, (short) 2));
		
		final ScoItemPropostaFornecedor original = new ScoItemPropostaFornecedor();
		original.setId(id);
		original.setItemLicitacao(itemLicitacao);
		original.setMotDesclassif(DominioMotivoDesclassificacaoItemProposta.DI);
		original.setJustifAutorizUsr("Teste");
		
		final ScoItemPropostaFornecedor item = new ScoItemPropostaFornecedor();
		item.setId(id);
		item.setItemLicitacao(itemLicitacao);
		
		Mockito.when(autorizacaoFornDAO.verificarItemPropostaEmAf(item.getId().getPfrLctNumero(), 
				item.getId().getPfrFrnNumero())).thenReturn(false);

		Mockito.when(itemLicitacaoDao.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(1, (short) 2)).thenReturn(itemLicitacao);

		Mockito.when(itemPropostaDao.obterOriginal(item)).thenReturn(original);
		
		Mockito.when(itemPropostaDao.verificarItemPossuiPropostaEscolhida(item)).thenReturn(true);

		systemUnderTest.gravarAnaliseTecnica(item);
	}
	
	/**
	 * Testa gravação de análise técnica, retirando motivo de desclassificação.
	 * @throws BaseException 
	 */
	@Test
	public void testGravacaoAnaliseTecnicaSemMotivoDesclassificacao() throws BaseException {
		ScoItemPropostaFornecedorId id = new ScoItemPropostaFornecedorId(1, 2, (short) 3);
		final ScoItemLicitacao itemLicitacao = new ScoItemLicitacao();
		itemLicitacao.setId(new ScoItemLicitacaoId(1, (short) 2));
		itemLicitacao.setPropostaEscolhida(Boolean.TRUE);
		
		final ScoItemPropostaFornecedor original = new ScoItemPropostaFornecedor();
		original.setId(id);
		original.setItemLicitacao(itemLicitacao);
		original.setMotDesclassif(DominioMotivoDesclassificacaoItemProposta.DI);
		original.setJustifAutorizUsr("Teste");
		
		final ScoItemPropostaFornecedor item = new ScoItemPropostaFornecedor();
		item.setId(id);
		item.setItemLicitacao(itemLicitacao);

		Mockito.when(autorizacaoFornDAO.verificarItemPropostaEmAf(item.getId().getPfrLctNumero(), 
				item.getId().getPfrFrnNumero())).thenReturn(false);

		Mockito.when(itemLicitacaoDao.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(1, (short) 2)).thenReturn(itemLicitacao);

		Mockito.when(itemPropostaDao.obterOriginal(item)).thenReturn(original);
		
		Mockito.when(itemPropostaDao.verificarItemPossuiPropostaEscolhida(item)).thenReturn(false);

		systemUnderTest.gravarAnaliseTecnica(item);
		
		assertEquals(item.getIndEscolhido(), item.getItemLicitacao().getPropostaEscolhida());
	}
	
}
