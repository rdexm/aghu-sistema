package br.gov.mec.aghu.faturamento;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.model.FatCompetencia;

public class FatCompetenciaDAOTest extends AbstractDAOTest<FatCompetenciaDAO> {
	
	@Override
	protected FatCompetenciaDAO doDaoUnderTests() {
		return new FatCompetenciaDAO() {
			private static final long serialVersionUID = 4749076569186807884L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatCompetenciaDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}
	
	private List<FatCompetencia> hqlObterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia modulo) {

		List<FatCompetencia> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select c ");
		hql.append(" from " + FatCompetencia.class.getSimpleName() + " c");
		hql.append(" where " + FatCompetencia.Fields.MODULO.toString() + " = :p_modulo");
		hql.append(" 	and " + FatCompetencia.Fields.DT_HR_FIM.toString() + " is null");
		query = ((Session) entityManager.getDelegate()).createQuery(hql.toString());
		query.setParameter("p_modulo", modulo);
		result = query.list();

		return result;
	}

	
	@Test
	public void testObterListaCompetenciaSemDataFimPorModulo() {

		List<FatCompetencia> result = null;
		List<FatCompetencia> hqlResult = null;
		DominioModuloCompetencia modulo = null;

		if (isEntityManagerOk()) {
			// setup: modulo = null;
			modulo = null;
			// assert
			try {
				result = getDaoUnderTests().obterListaCompetenciaSemDataFimPorModulo(modulo);
				Assert.assertTrue(result.isEmpty());
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
			// setup: modulo != null;
			modulo = DominioModuloCompetencia.SIS;
			// assert
			result = getDaoUnderTests().obterListaCompetenciaSemDataFimPorModulo(modulo);
			hqlResult = this.hqlObterListaCompetenciaSemDataFimPorModulo(modulo);
		}
	}

	
	private List<FatCompetencia> hqlObterListaAtivaPorModulo(DominioModuloCompetencia modulo) {

		List<FatCompetencia> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select c ");
		hql.append(" from " + FatCompetencia.class.getSimpleName() + " c");
		hql.append(" where " + FatCompetencia.Fields.MODULO.toString() + " = :p_modulo");
		hql.append(" 	and " + FatCompetencia.Fields.IND_SITUACAO.toString() + " = :p_sit");
		query = ((Session) entityManager.getDelegate()).createQuery(hql.toString());
		query.setParameter("p_modulo", modulo);
		query.setParameter("p_sit", DominioSituacaoCompetencia.A);
		result = query.list();

		return result;
	}

	
	@Test
	public void testObterListaAtivaPorModulo() {

		List<FatCompetencia> result = null;
		List<FatCompetencia> hqlResult = null;
		DominioModuloCompetencia modulo = null;

		if (isEntityManagerOk()) {
			// setup: modulo = null;
			modulo = null;
			// assert
			try {
				result = getDaoUnderTests().obterListaAtivaPorModulo(modulo);
				Assert.assertTrue(result.isEmpty());
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
			// setup: modulo != null;
			modulo = DominioModuloCompetencia.MAMA;
			// assert
			result = getDaoUnderTests().obterListaAtivaPorModulo(modulo);
			hqlResult = this.hqlObterListaAtivaPorModulo(modulo);
		}
	}

	@Test
	public void testObterCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao() {
		if (isEntityManagerOk()) {
			final DominioModuloCompetencia modulo = DominioModuloCompetencia.AMB;
			final Integer mes = 5;
			final Integer ano = 2011;
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2011);
			dtCalendar.set(Calendar.MONTH, 3);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dtHoraInicio = dtCalendar.getTime();
			final List<FatCompetencia> result = getDaoUnderTests().obterCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao(modulo, mes, ano,
					dtHoraInicio);
			if (result == null) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou 1 registro.");
			}
		}
	}
}