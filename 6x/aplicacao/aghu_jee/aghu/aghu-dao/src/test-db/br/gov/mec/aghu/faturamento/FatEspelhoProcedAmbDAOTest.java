package br.gov.mec.aghu.faturamento;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioTipoFormularioDataSus;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoBpiVO;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoVO;

public class FatEspelhoProcedAmbDAOTest extends AbstractDAOTest<FatEspelhoProcedAmbDAO> {

	@Override
	protected FatEspelhoProcedAmbDAO doDaoUnderTests() {
		return new FatEspelhoProcedAmbDAO() {
			private static final long serialVersionUID = 849383745639858311L;

			@Override
			protected Query createHibernateQuery(String query) {
				return FatEspelhoProcedAmbDAOTest.this.createHibernateQuery(query);
			}

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatEspelhoProcedAmbDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return FatEspelhoProcedAmbDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return FatEspelhoProcedAmbDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {

	}

	@Test
	public void executarCursorConsulta() {
		if (isEntityManagerOk()) {
			Calendar dt = Calendar.getInstance();
			dt.set(Calendar.MONTH, 9);
			dt.set(Calendar.DAY_OF_MONTH, 31);
			dt.set(Calendar.HOUR_OF_DAY, 22);
			dt.set(Calendar.MINUTE, 0);
			dt.set(Calendar.SECOND, 0);
			dt.set(Calendar.MILLISECOND, 0);
			List<CursorEspelhoVO> result = this.daoUnderTests
					.listarEspelho(true, true,dt.getTime(), 2011,
							11, DominioModuloCompetencia.AMB,
							DominioTipoFormularioDataSus.C);
			logger.info("###############################################");
			if (result == null) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou " + result.size()+ " registros.");
				for (CursorEspelhoVO cursorEspelhoVO : result) {
					logger.info("Competencia " + cursorEspelhoVO.getCompetencia());
					logger.info("FccSeq " + cursorEspelhoVO.getFccSeq());
					logger.info("FcfSeq " + cursorEspelhoVO.getFcfSeq());
					logger.info("Idade " + cursorEspelhoVO.getIdade());
					logger.info("IphPhoSeq " + cursorEspelhoVO.getIphPhoSeq());
					logger.info("IphSeq " + cursorEspelhoVO.getIphSeq());
					logger.info("ProcedimentoHosp " + cursorEspelhoVO.getProcedimentoHosp());
					logger.info("Quantidade " + cursorEspelhoVO.getQuantidade());
					logger.info("OrigemInf " + cursorEspelhoVO.getOrigemInf());
					logger.info("VlrAnestes " + cursorEspelhoVO.getVlrAnestes());
					logger.info("VlrProc " + cursorEspelhoVO.getVlrProc());
					logger.info("VlrSadt " + cursorEspelhoVO.getVlrSadt());
					logger.info("VlrServHosp " + cursorEspelhoVO.getVlrServHosp());
					logger.info("VlrServProf " + cursorEspelhoVO.getVlrServProf());
					
				}
			}
		}
	}

	@Test
	public void executaCursorEspelhoBpi() {
		if (isEntityManagerOk()) {
			/*List<CursorEspelhoBpiVO> result = this.daoUnderTests
					.listarEspelhoBpi(DominioSimNao.N, new Date(), 2050,
							12, DominioModuloCompetencia.AMB,
							DominioTipoFormularioDataSus.C);*/
			Calendar c = new GregorianCalendar();
			
			c.set(Calendar.YEAR, 2011);
			c.set(Calendar.MONTH, 9);
			c.set(Calendar.DAY_OF_MONTH, 31);
			c.set(Calendar.HOUR_OF_DAY, 22);
			c.set(Calendar.MINUTE, 00);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			List<CursorEspelhoBpiVO> result = this.daoUnderTests
			.listarEspelhoBpi(true, c, 2011,
					11, DominioModuloCompetencia.AMB,
					DominioTipoFormularioDataSus.I);

			logger.info("retornou " + result.size() + " registros");
			
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testeObterGrupoAtendimento() {
		final Short ctcCnvCodigo = 1;
		final Short iphPhoSeq = 4;
		final Integer iphSeq = 836;
		Short espSeq = 7;

		if (isEntityManagerOk()) {
			Byte result = this.daoUnderTests.obterGrupoAtendimento(iphSeq,
					iphPhoSeq, ctcCnvCodigo, espSeq);
			if (result == null) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou CodigoSus=" + result);
			}
			espSeq = 0;
			result = this.daoUnderTests.obterGrupoAtendimento(iphSeq,
					iphPhoSeq, ctcCnvCodigo, espSeq);
			if (result == null) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou CodigoSus=" + result);
			}
		}
	}

	@Override
	protected void finalizeMocks() {
		
	}
}