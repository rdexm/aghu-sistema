package br.gov.mec.aghu.compras;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoGrupoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.pac.vo.LicitacoesLiberarCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;

/**
 * Classe reponsável por testar unitariamente {@link ScoGrupoMaterialDAO}.
 * 
 * @author mlcruz
 */
public class ScoLicitacaoDAOTest extends AbstractDAOTest<ScoLicitacaoDAO> {
	
	@Override
	protected ScoLicitacaoDAO doDaoUnderTests() {
		return new ScoLicitacaoDAO() {

			private static final long serialVersionUID = 723792094616755271L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoLicitacaoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return ScoLicitacaoDAOTest.this.runCriteriaCount(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return ScoLicitacaoDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void testPesquisaLicitacoesScLiberar() {
		LicitacoesLiberarCriteriaVO criteria = new LicitacoesLiberarCriteriaVO();
		criteria.setNumero(1);
		criteria.setDescricao("Licitação A");
		criteria.getGeracao().setInicio(new Date());
		criteria.getGeracao().setFim(new Date());
		criteria.setTipo(DominioTipoSolicitacao.SC);
		criteria.setSolicitacaoCompraId(10);
		criteria.setMaterial(entityManager.find(ScoMaterial.class, 4642));

		criteria.setGestor(entityManager.find(
				RapServidores.class, new RapServidoresId(105171, (short) 962)));
		
		criteria.setModalidade(entityManager.find(
				ScoModalidadeLicitacao.class, "PG"));
		
		Integer firstResult = 0;
		Integer maxResult = 100;
		String order = ScoLicitacao.Fields.NUMERO.toString();
		boolean asc = false;
		
		doDaoUnderTests().pesquisarLicitacoesLiberar(
				criteria, firstResult, maxResult, order, asc);
	}

	@Test
	public void testPesquisaLicitacoesSsLiberar() {
		LicitacoesLiberarCriteriaVO criteria = new LicitacoesLiberarCriteriaVO();
		criteria.setNumero(1);
		criteria.setDescricao("Licitação A");
		criteria.getGeracao().setInicio(new Date());
		criteria.getGeracao().setFim(new Date());
		criteria.setTipo(DominioTipoSolicitacao.SS);
		criteria.setSolicitacaoServicoId(10);
		criteria.setServico(entityManager.find(ScoServico.class, 121));

		criteria.setGestor(entityManager.find(
				RapServidores.class, new RapServidoresId(105171, (short) 962)));
		
		criteria.setModalidade(entityManager.find(
				ScoModalidadeLicitacao.class, "PG"));
		
		Integer firstResult = 0;
		Integer maxResult = 100;
		String order = ScoLicitacao.Fields.NUMERO.toString();
		boolean asc = false;
		
		doDaoUnderTests().pesquisarLicitacoesLiberar(
				criteria, firstResult, maxResult, order, asc);
	}
	
	/**
	 * Testa contagem de PAC's para julgamento.
	 */
	@Test
	public void testContagemPacsParaJulgamento() {
		PacParaJulgamentoCriteriaVO criteria = new PacParaJulgamentoCriteriaVO();
		criteria.setNroPac(36);
		criteria.setDescricao("teste");
		criteria.setModalidade(entityManager.find(ScoModalidadeLicitacao.class, "LL"));
		criteria.setSituacao(DominioSituacaoLicitacao.JU);
		
		doDaoUnderTests().contarPacsParaJulgamento(criteria);
	}
	
	/**
	 * Testa pesquisa de PAC's para julgamento.
	 */
	@Test
	public void testPequisaPacsParaJulgamento() {
		PacParaJulgamentoCriteriaVO criteria = new PacParaJulgamentoCriteriaVO();
		doDaoUnderTests().pesquisarPacsParaJulgamento(criteria, 1, 10, null, true);
	}
	
	/**
	 * Testa obtenção de número máximo de dias de permanência pela licitação.
	 */
	@Test
	public void testObtencaoNumeroMaximoDeDiasDePermaneciaPelaLicitacao() {
		doDaoUnderTests().obterNumeroMaximoDeDiasDePermaneciaPelaLicitacao(0);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}