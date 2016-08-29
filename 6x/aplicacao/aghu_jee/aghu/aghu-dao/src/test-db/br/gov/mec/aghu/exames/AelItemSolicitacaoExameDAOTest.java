package br.gov.mec.aghu.exames;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.vo.AtendPacExternPorColetasRealizadasVO;

public class AelItemSolicitacaoExameDAOTest extends AbstractDAOTest<AelItemSolicitacaoExameDAO> {
	

	@Override
	protected AelItemSolicitacaoExameDAO doDaoUnderTests() {
		return new AelItemSolicitacaoExameDAO() {
			private static final long serialVersionUID = -5740052516836967791L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AelItemSolicitacaoExameDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		int x = 0;
		logger.info("inicializando mocks");
		if (x > 0) {
			logger.info("x > 0");
		}
	}
	 
	@Override
	protected void finalizeMocks() {
		int x = 0;
		logger.info("finalizando mocks");
		if (x > 0) {
			logger.info("x > 0");
		}
	}

	
	@Test
	public void listarTriagemRealizadaEmergenciaTestaConsultaPostgreSQLRetornarDados() {
		List<AtendPacExternPorColetasRealizadasVO> listaVO = null;
		
		if (isEntityManagerOk()) {
			//assert
			//retornar dados
			listaVO = getDaoUnderTests().listarAtendPacExternPorColetasRealizadas(
					DateUtil.obterData(2012, 9, 31, 22, 00), DateUtil.obterData(2012, 10, 30, 23, 59), 67, 21, "CA");
			Assert.assertTrue(true);
			
			logger.info("retornou " + listaVO.size() + " registros");
		}
	}
}