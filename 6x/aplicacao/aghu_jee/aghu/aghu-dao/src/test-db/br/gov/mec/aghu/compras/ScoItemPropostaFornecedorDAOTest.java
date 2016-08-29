package br.gov.mec.aghu.compras;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoGrupoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;

/**
 * Classe reponsável por testar unitariamente {@link ScoGrupoMaterialDAO}.
 * 
 * @author mlcruz
 */
public class ScoItemPropostaFornecedorDAOTest extends AbstractDAOTest<ScoItemPropostaFornecedorDAO> {
	@Override
	protected ScoItemPropostaFornecedorDAO doDaoUnderTests() {
		return new ScoItemPropostaFornecedorDAO() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 723792094616755271L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoItemPropostaFornecedorDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return ScoItemPropostaFornecedorDAOTest.this.runCriteriaCount(criteria);
			}
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return ScoItemPropostaFornecedorDAOTest.this.runCriteriaUniqueResult(criteria);
			}
		};
	}
	
	/**
	 * Testa contagem de itens escolhidos/em AF.
	 */
	@Test
	public void testContagemItensEmAf() {
		ScoPropostaFornecedor proposta = entityManager.find(
				ScoPropostaFornecedor.class, 
					new ScoPropostaFornecedorId(73565, 124));

		doDaoUnderTests().contarItensEmAf(proposta);
	}
	
	/**
	 * Testa verificação de item associado a condição de pagamento.
	 */
	@Test
	public void testVerificacaoItemAssociadoCondicaoPagamento() {
		doDaoUnderTests().existeItemAssociadoACondicao(1);
	}
	
	/**
	 * Testa verificação de item possuir proposta escolhida.
	 */
	@Test
	public void testVerificacaoItemPossuiPropostaEscolhida() {
		ScoItemPropostaFornecedor item = entityManager.find(
				ScoItemPropostaFornecedor.class,
				new ScoItemPropostaFornecedorId(41840, 1489, (short) 1));
		
		doDaoUnderTests().verificarItemPossuiPropostaEscolhida(item);
	}
	
	/**
	 * Testa obtenção de SC para análise técnica.
	 */
	@Test
	public void testObtencaoScParaAnaliseTecnica() {
		ScoItemPropostaFornecedorId id = new ScoItemPropostaFornecedorId(1, 2, (short) 3);
		doDaoUnderTests().obterScParaAnaliseTecnica(id);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}