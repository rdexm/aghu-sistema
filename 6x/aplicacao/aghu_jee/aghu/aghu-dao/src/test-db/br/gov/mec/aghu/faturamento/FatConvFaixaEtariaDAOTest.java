package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatConvFaixaEtariaDAO;
import br.gov.mec.aghu.model.FatConvFaixaEtaria;

public class FatConvFaixaEtariaDAOTest extends AbstractDAOTest<FatConvFaixaEtariaDAO> {

	@Override
	protected FatConvFaixaEtariaDAO doDaoUnderTests() {
		return new FatConvFaixaEtariaDAO() {
			private static final long serialVersionUID = -3821187825373986437L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatConvFaixaEtariaDAOTest.this.runCriteria(criteria);
			}
		};
	}


	@Override
	protected void initMocks() {
		
	}
	
	@Test
	public void testObterListaCorrenteAtivoPorIdadeConvenio() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			List<FatConvFaixaEtaria> result = this.daoUnderTests.obterListaCorrenteAtivoPorIdadeConvenio((short)1024, (short) 1);
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " resultados.");
				for (FatConvFaixaEtaria result1 : result) {
					logger.info("seqp = " + result1.getId().getSeqp());
					logger.info("CnvCodigo = " + result1.getId().getCnvCodigo());
					logger.info("CODIGO_SUS = " + result1.getCodigoSus());
					logger.info("IdadeInicio = " + result1.getIdadeInicio());
					logger.info("IdadeFim = " + result1.getIdadeFim());
					logger.info("DtInicioValidade = " + result1.getDtInicioValidade());
					logger.info("DtFimValidade = " + result1.getDtFimValidade());
					logger.info("IndSituacaoRegistro = " + result1.getIndSituacaoRegistro());
				}
			}
		}
	}


	@Override
	protected void finalizeMocks() {
	}
}