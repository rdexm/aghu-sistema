package br.gov.mec.aghu.blococirurgico;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.vo.DiagnosticosPrePosOperatoriosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoPlano;

public class MbcDiagnosticoDescricaoDAOTest extends AbstractDAOTest<MbcDiagnosticoDescricaoDAO> {
	
	@Override
	protected MbcDiagnosticoDescricaoDAO doDaoUnderTests() {
		return new MbcDiagnosticoDescricaoDAO() {
			private static final long serialVersionUID = 1058151041682953177L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return MbcDiagnosticoDescricaoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return MbcDiagnosticoDescricaoDAOTest.this.createSQLQuery(query);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}


	@Test
	public void testBuscarCidSeqMbcDiagnosticosDescricoes() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Integer crgSeq = 244713;
			final Integer phiSeq = 28768;
			final DominioTipoPlano validade = DominioTipoPlano.A;

			try {
				Integer result = this.daoUnderTests.buscarCidSeqMbcDiagnosticosDescricoes(crgSeq, phiSeq, validade);
				logger.info("###############################################");
				if (result == null) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void obterDiagnosticosPrePosOperatorio() throws ApplicationBusinessException {
		final Date dataInicial = DateUtil.obterData(2012, 7, 1); 
		final Date dataFinal = DateUtil.obterData(2012, 8, 1);
		
		try {
			List<DiagnosticosPrePosOperatoriosVO> result = doDaoUnderTests().obterDiagnosticosPrePosOperatorio(dataInicial, dataFinal);
			if (result == null || result.isEmpty()) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
				for (DiagnosticosPrePosOperatoriosVO resultado : result) {
					logger.info("CirurgiasExposicaoRadiacaoIonizanteVO = " + resultado);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			Assert.fail("Not expecting exception: " + e);
		}
	}
	
	@Override
	protected void finalizeMocks() {
	}
}
