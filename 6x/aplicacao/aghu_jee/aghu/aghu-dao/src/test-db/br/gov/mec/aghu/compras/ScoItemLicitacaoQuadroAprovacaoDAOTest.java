package br.gov.mec.aghu.compras;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoQuadroAprovacaoDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

/**
 * Teste Unitário do Repositório de Itens de Licitação
 * 
 * @author mlcruz
 */
public class ScoItemLicitacaoQuadroAprovacaoDAOTest extends AbstractDAOTest<ScoItemLicitacaoQuadroAprovacaoDAO> {

	@Override
	protected ScoItemLicitacaoQuadroAprovacaoDAO doDaoUnderTests() {
		return new ScoItemLicitacaoQuadroAprovacaoDAO() {
			private static final long serialVersionUID = 5925830399421305648L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoItemLicitacaoQuadroAprovacaoDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	/**
	 * Testa pesquisa de itens sem proposta.
	 */
	@Test
	public void testPesquisaItensSemProposta() {
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(40163);
		doDaoUnderTests().pesquisarItensSemProposta(ids, true);
	}
	
	/**
	 * Testa pesquisa de itens com proposta.
	 */
	@Test
	public void testPesquisaItensComProposta() {
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(40163);
		doDaoUnderTests().pesquisarItensComProposta(ids, true);
	}
	
	/**
	 * Testa pesquisa de itens com proposta escolhida.
	 */
	@Test
	public void testPesquisaItensComPropostaEscolhida() {
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(40163);
		doDaoUnderTests().pesquisarItensComPropostaEscolhida(ids, true);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}