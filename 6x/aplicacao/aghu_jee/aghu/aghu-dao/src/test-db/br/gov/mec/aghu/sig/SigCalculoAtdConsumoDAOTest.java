package br.gov.mec.aghu.sig;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdConsumoDAO;

/**
 * 
 * @author rmalvezzi
 *
 */
public class SigCalculoAtdConsumoDAOTest extends AbstractDAOTest<SigCalculoAtdConsumoDAO> {

	@Override
	protected SigCalculoAtdConsumoDAO doDaoUnderTests() {
		return new SigCalculoAtdConsumoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria, boolean cacheable) {
				return SigCalculoAtdConsumoDAOTest.this.runCriteriaUniqueResult(criteria, cacheable);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigCalculoAtdConsumoDAOTest.this.runCriteria(criteria);
			}
			
		};
	}

	@Test
	public void buscarItemConsumoTest() {
		this.daoUnderTests.buscarItemConsumo(this.entityManager.find(SigCalculoAtdPermanencia.class, 1), this.entityManager.find(SigObjetoCustoVersoes.class, 75),
				this.entityManager.find(FatProcedHospInternos.class, 4088));
		this.daoUnderTests.buscarItemConsumo(this.entityManager.find(SigCalculoAtdPermanencia.class, 1), null,
				this.entityManager.find(FatProcedHospInternos.class, 4088));
	}

	@Test
	public void buscarConsumoPacienteConsumidosTest() {
		this.daoUnderTests.buscarConsumoPacienteConsumidos(this.entityManager.find(SigProcessamentoCusto.class, 1));
	}

	@Test
	public void buscarProducaoPacienteTest() {
		this.daoUnderTests.buscarProducaoPaciente(this.entityManager.find(SigProcessamentoCusto.class, 1));
	}

	@Test
	public void buscarCalculoAtendimentoConsumoTest() {
		this.daoUnderTests.buscarCalculoAtendimentoConsumo(this.entityManager.find(SigCalculoAtdPermanencia.class, 1),
				this.entityManager.find(SigObjetoCustoVersoes.class, 75), this.entityManager.find(FccCentroCustos.class, 31313));
	}

	@Test
	public void buscarConsultoriasMedicasPorAtendimentoTest() {
		this.daoUnderTests.buscarConsultoriasMedicasPorAtendimento(3432, new Date(), new Date());
	}

	@Test
	public void buscarOretesesProtesesPorInternacaoTest() {
		this.daoUnderTests.buscarOrtesesProtesesPorInternacao(2321, new Date(), new Date());
	}

	@Test
	public void buscarProcedimentosEspeciaisDiversosTest() {
		this.daoUnderTests.buscarProcedimentosEspeciaisDiversos(2321, new Date(), new Date());
	}

	@Test
	public void buscarProcedimentosEspeciaisCirurgicosTest() {
		this.daoUnderTests.buscarProcedimentosEspeciaisCirurgicos(2321, new Date(), new Date());
	}

	@Test
	public void buscarSolicitacoesHemoterapicasRealizadasNaInternacaoTest() {
		this.daoUnderTests.buscarSolicitacoesHemoterapicasRealizadasNaInternacao(11403687, new Date(), new Date());
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}