package br.gov.mec.aghu.estoque;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO.NotaRecebimentoProvisorio;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;

/**
 * 
 * @author cvagheti
 */
public class SceItemRecebProvisorioDAOTest extends AbstractDAOTest<SceItemRecebProvisorioDAO> {
	@Override
	protected SceItemRecebProvisorioDAO doDaoUnderTests() {
		return new SceItemRecebProvisorioDAO() {
		
			private static final long serialVersionUID = -8899151226365446158L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SceItemRecebProvisorioDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return SceItemRecebProvisorioDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return SceItemRecebProvisorioDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}

	@Test
	@SuppressWarnings("unused")	
	public void pesquisarRelatorioItemRecProv() {
		
		List<Integer> listaNrpSeq = new ArrayList<Integer>();
		
		listaNrpSeq.add(1);
		
		doDaoUnderTests().pesquisarRelatorioItemRecProv(listaNrpSeq);
		//doDaoUnderTests().pesquisaContrato(idItemAutForn);
//		FsoNaturezaDespesa ntd = doDaoUnderTests()
//				.obtemNaturezaDespesaPorMaterial(0);
	}
	
	/**
	 * Testa soma da quantidade dos itens de uma ou mais notas de recebimento
	 * provisório associados a uma parcela de item de AF.
	 */
	@Test
	public void testSomaQtdeItensNotaRecebProvisorioParcela() {
		ScoProgEntregaItemAutorizacaoFornecimentoId id = 
				new ScoProgEntregaItemAutorizacaoFornecimentoId(87268, 1, 1, 13);
		
		ScoProgEntregaItemAutorizacaoFornecimento parcela = entityManager
				.find(ScoProgEntregaItemAutorizacaoFornecimento.class, id);
		
		doDaoUnderTests().somarQtdeItensNotaRecebProvisorio(parcela);
	}
	
	/**
	 * Testa pesquisa de notas de recebimento provisório associadas a um item de
	 * AF.
	 */
	@Test
	public void testPesquisaNotasRecebimentoProvisorio() {
		ScoItemAutorizacaoFornId itemAfId = new ScoItemAutorizacaoFornId();
		itemAfId.setAfnNumero(74377);
		itemAfId.setNumero(1);
		ScoItemAutorizacaoForn itemAf = entityManager.find(ScoItemAutorizacaoForn.class, itemAfId);
		List<NotaRecebimentoProvisorio> result = doDaoUnderTests().pesquisarNotasRecebimentoProvisorio(itemAf, 10);
		
		for (NotaRecebimentoProvisorio item : result) {
			item.getId();
		}
	}
	
	/**
	 * Testa pesquisa de notas de recebimento provisório associadas a parcela de
	 * um item de AF.
	 */
	@Test
	public void testPesquisaNotasRecebimentoProvisorioParcela() {
		ScoProgEntregaItemAutorizacaoFornecimentoId id = 
				new ScoProgEntregaItemAutorizacaoFornecimentoId(87268, 1, 1, 13);
		
		ScoProgEntregaItemAutorizacaoFornecimento parcela = entityManager
				.find(ScoProgEntregaItemAutorizacaoFornecimento.class, id);
		
		List<NotaRecebimentoProvisorio> result = doDaoUnderTests().pesquisarNotasRecebimentoProvisorio(parcela, 10);
		
		for (NotaRecebimentoProvisorio item : result) {
			item.getId();
		}
	}
	
	/**
	 * Testa pesquisa de notas de recebimento provisório associadas a um
	 * material/almoxarifado.
	 */
	@Test
	public void testPesquisaNotasRecebimentoProvisorioMaterialAlmox() {
		SceEstoqueAlmoxarifado estoque = entityManager.find(SceEstoqueAlmoxarifado.class, 80595);
		
		List<NotaRecebimentoProvisorio> result = doDaoUnderTests().pesquisarNotasRecebimentoProvisorio(estoque, 10);
		
		for (NotaRecebimentoProvisorio item : result) {
			item.getId();
		}
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}