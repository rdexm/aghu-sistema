package br.gov.mec.aghu.configuracao;

import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.bancosangue.dao.AbsRegSanguineoPacientesDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;

public class AbsRegSanguineoPacientesDAOTest extends AbstractDAOTest<AbsRegSanguineoPacientesDAO> {
	
	@Override
	protected AbsRegSanguineoPacientesDAO doDaoUnderTests() {
		return new AbsRegSanguineoPacientesDAO() {
			private static final long serialVersionUID = 4994789192937131576L;
			
			@Override
			protected Query createHibernateQuery(String query) {
				return AbsRegSanguineoPacientesDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}

	@Test
	public void listarTransfusaoComExistsSemAgrupamento() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarRegSanguineoPacienteComExistsSemAgrupamento(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955, DominioOrigemAtendimento.I);
				//com dados
				/*result = getDaoUnderTests().listarTransfusaoUnidadeSangue(DateUtil.obterData(2000, 9, 01), 
						DateUtil.obterData(2001, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST", DominioOrigemAtendimento.I);*/
				Assert.assertTrue(result.isEmpty());
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}	
}