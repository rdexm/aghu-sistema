package br.gov.mec.aghu.sig;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;

/**
 * @author rmalvezzi
 */
public class SigProcessamentoCustoDAOTest extends AbstractDAOTest<SigProcessamentoCustoDAO> {

	SigProcessamentoCusto processamento;
	
	@Override
	protected SigProcessamentoCustoDAO doDaoUnderTests() {
		return new SigProcessamentoCustoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigProcessamentoCustoDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigProcessamentoCustoDAOTest.this.createHibernateQuery(query);
			}

			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return SigProcessamentoCustoDAOTest.this.runCriteriaCount(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return SigProcessamentoCustoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected boolean executeCriteriaExists(DetachedCriteria criteria) {
				return SigProcessamentoCustoDAOTest.this.runCriteriaExists(criteria);
			}
		};
	}

	@Test
	public void buscaConsumoInsumoMvtoContaMensaisTest() {
		this.daoUnderTests.buscarConsumoInsumoMvtoContaMensais(DominioTipoMovimentoConta.SIP, new BigDecimal(5), new BigDecimal(3), processamento);
		this.daoUnderTests.buscarConsumoInsumoMvtoContaMensais(DominioTipoMovimentoConta.SIT, new BigDecimal(5), new BigDecimal(3), processamento);
	}

	@Test
	public void buscarDebitoServicosTest() {
		this.daoUnderTests.buscarDebitoServicos(processamento);
	}

	@Test
	public void buscaGruposOcupacaoAlocadosAtividadesTest() {
		this.daoUnderTests.buscarGruposOcupacaoAlocadosAtividades(14);
	}

	@Test
	public void obterDetalheVisualizacaoAnaliseCCTest() {
		this.daoUnderTests.obterDetalheVisualizacaoAnaliseCC(13, this.entityManager.find(FccCentroCustos.class, 31313));
	}

	@Test
	public void buscarCustosVisaoCentroCustosTest() {
		this.daoUnderTests.buscarCustosVisaoCentroCustos(5, 5, 13, this.entityManager.find(FccCentroCustos.class, 31313), null, null);
	}

	@Test
	public void buscarCustosVisaoCentroCustosCountTest() {
		this.daoUnderTests.buscarCustosVisaoCentroCustosCount(13, this.entityManager.find(FccCentroCustos.class, 31313), null, null);
	}

	@Test
	public void obterSigProcessamentoCustoCompetenciaTest() {
		this.daoUnderTests.obterSigProcessamentoCustoCompetencia(new Date());
	}

	@Test
	public void pesquisarCompetenciaTest() {
		this.daoUnderTests.pesquisarCompetencia();
		this.daoUnderTests.pesquisarCompetencia(DominioSituacaoProcessamentoCusto.A);
	}

	@Test
	public void verificarExistenciaProcessamentoPendenteTest() {
		this.daoUnderTests.verificarExistenciaProcessamentoPendente();
	}

	@Test
	public void pesquisarProcessamentoCustoCountTest() {
		this.daoUnderTests.pesquisarProcessamentoCustoCount(this.entityManager.find(SigProcessamentoCusto.class, 13), DominioSituacaoProcessamentoCusto.A);
	}

	@Test
	public void pesquisarProcessamentoCustoTest() {
		this.daoUnderTests.pesquisarProcessamentoCusto(5, 5, null, true, this.entityManager.find(SigProcessamentoCusto.class, 13),
				DominioSituacaoProcessamentoCusto.A);
	}

	@Test
	public void pesquisarCompetenciaSemProducaoTest() {
		this.daoUnderTests.pesquisarCompetenciaSemProducao(null, null);
		this.daoUnderTests.pesquisarCompetenciaSemProducao(this.entityManager.find(SigObjetoCustoVersoes.class, 1), null);
		this.daoUnderTests.pesquisarCompetenciaSemProducao(null, this.entityManager.find(SigDirecionadores.class, 1));
		this.daoUnderTests
				.pesquisarCompetenciaSemProducao(this.entityManager.find(SigObjetoCustoVersoes.class, 1), this.entityManager.find(SigDirecionadores.class, 1));
	}

	@Override
	protected void initMocks() {
		 processamento= new SigProcessamentoCusto(4);
         processamento.setDataInicio(new Date());
         processamento.setCompetencia(new Date());
         processamento.setDataFim(new Date());
	}

	@Override
	protected void finalizeMocks() {
	}
}