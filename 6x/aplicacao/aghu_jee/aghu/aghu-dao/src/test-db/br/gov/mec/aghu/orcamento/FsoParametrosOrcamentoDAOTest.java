package br.gov.mec.aghu.orcamento;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioIndicadorParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoResultVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;

/**
 * Teste unitÃ¡rio da classe FsoParametrosOrcamentoDAO.
 * 
 * @author mlcruz
 */
public class FsoParametrosOrcamentoDAOTest extends AbstractDAOTest<FsoParametrosOrcamentoDAO> {
	@Override
	protected FsoParametrosOrcamentoDAO doDaoUnderTests() {
		return new FsoParametrosOrcamentoDAO() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7222722979304609067L;

			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return FsoParametrosOrcamentoDAOTest.this.runCriteriaCount(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return FsoParametrosOrcamentoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}

	/**
	 * Testa pesquisa
	 */
	@Test
	public void testPesquisar() {
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setIndicador(DominioIndicadorParametrosOrcamento.P);
		criteria.setSituacao(DominioSituacao.A);

		List<FsoParametrosOrcamentoResultVO> result = doDaoUnderTests()
				.pesquisarParametrosOrcamentoVO(criteria, 0, 10,
						FsoParametrosOrcamento.Fields.SEQ.toString(), true);

		for (@SuppressWarnings("unused")
		FsoParametrosOrcamentoResultVO item : result)
			;
	}
	
	/**
	 * Testa pesquisarParametrosOrcamento
	 */
	@Test
	public void testPesquisarCentroCustos() {
		// CritÃ©rio
		final FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		criteria.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.MATERIAL);

		criteria.setMaterial(entityManager.find(ScoMaterial.class, 999999));
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(BigDecimal.ZERO);
		criteria.setCentroCusto(entityManager
				.find(FccCentroCustos.class, 31313));
		
		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.R);
		criteria.setAcoes(acoes);
		criteria.setMaxResults(100);
		criteria.setOrder(FccCentroCustos.Fields.CODIGO.toString());
		
		List<FccCentroCustos> result = doDaoUnderTests().pesquisarCentroCustos(criteria);
		
		for (@SuppressWarnings("unused")
		FccCentroCustos i : result)
			;
	}
	
	/**
	 * Testa testPesquisarNaturezas
	 */
	@Test
	public void testPesquisarNaturezasParaSc() {
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		criteria.setMaterial(entityManager.find(ScoMaterial.class, 999999));

		testPesquisarNaturezas(DominioTipoSolicitacao.SC, criteria);
	}
	
	/**
	 * Testa testPesquisarNaturezas
	 */
	@Test
	public void testPesquisarNaturezasParaSs() {
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		criteria.setServico(entityManager.find(ScoServico.class, 470));
		
		testPesquisarNaturezas(DominioTipoSolicitacao.SS, criteria);
	}
	
	/**
	 * Testa testPesquisarNaturezas
	 * @param criteria 
	 */
	private void testPesquisarNaturezas(DominioTipoSolicitacao tipo, FsoParametrosOrcamentoCriteriaVO criteria) {		
		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.R);
		criteria.setAcoes(acoes);

		criteria.setAplicacao(tipo);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(BigDecimal.ZERO);
		criteria.setCentroCusto(entityManager
				.find(FccCentroCustos.class, 44300));
		criteria.setGrupoNatureza(entityManager.find(
				FsoGrupoNaturezaDespesa.class, 319011));
		FsoNaturezaDespesaId naturezaId = new FsoNaturezaDespesaId(319011,
				Byte.valueOf("11"));
		criteria.setNatureza(entityManager.find(FsoNaturezaDespesa.class,
				naturezaId));
		criteria.setNaturezaShouldBeNull(criteria.getNatureza() != null);
		criteria.setMaxResults(100);
		criteria.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.MATERIAL);
		criteria.setLimite(null);
		criteria.setValor(null);
		criteria.setCentroCusto(null);
		
		Long result = doDaoUnderTests().contarParametrosOrcamento(criteria);
		Assert.assertNotNull(result);
	}
	
	/**
	 * Testa contagem de parÃ¢metros orÃ§amentÃ¡rios
	 */
	@Test
	public void testCountParametrosOrcamento() {
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		criteria.setAcoes(acoes);
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setGrupoNatureza(entityManager.find(FsoGrupoNaturezaDespesa.class, 319009));
		criteria.setMaterial(entityManager.find(ScoMaterial.class, 999999));
		criteria.setMaxResults(1);
		criteria.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.GERAL);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setSituacao(DominioSituacao.A);
		
		criteria.setNatureza(entityManager.find(FsoNaturezaDespesa.class,
				new FsoNaturezaDespesaId(319009, Byte.valueOf("58"))));
		
		Long result = doDaoUnderTests().contarParametrosOrcamento(criteria);
		
		Assert.assertNotNull(result);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}