package br.gov.mec.aghu.configuracao;

import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.bancosangue.dao.AbsMovimentosComponentesDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioMcoType;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;

public class AbsMovimentosComponentesDAOTest extends AbstractDAOTest<AbsMovimentosComponentesDAO> {
	
	
	@Override
	protected AbsMovimentosComponentesDAO doDaoUnderTests() {
		return new AbsMovimentosComponentesDAO() {
			private static final long serialVersionUID = 5601001283354303568L;

			@Override
			protected Query createHibernateQuery(String query) {
				return AbsMovimentosComponentesDAOTest.this.createHibernateQuery(query);
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
				result = getDaoUnderTests().listarTransfusaoComExistsSemAgrupamento(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST", DominioOrigemAtendimento.I);
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
	
	@Test
	public void listarTransfusaoSemExistsSemAgrupamento() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarTransfusaoSemExistsSemAgrupamento(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST");
				//com dados
				/*result = getDaoUnderTests().listarTransfusaoUnidadeSangue(DateUtil.obterData(2000, 9, 01), 
						DateUtil.obterData(2001, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST");*/
				Assert.assertTrue(result.isEmpty());
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Test
	public void listarTransfusaoComExistsComAgrupamento() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarTransfusaoComExistsComAgrupamento(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST", DominioOrigemAtendimento.I);
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
	
	@Test
	public void listarTransfusaoSemExistsComAgrupamento() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarTransfusaoSemExistsComAgrupamento(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST");
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
	
	@Test
	public void listarTransfusaoComExistsSemAgrupamentoComDoacao() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarTransfusaoComExistsSemAgrupamentoComDoacao(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST", DominioOrigemAtendimento.I, "F", false);
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
	
	@Test
	public void listarTransfusaoSemExistsSemAgrupamentoComDoacao() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarTransfusaoSemExistsSemAgrupamentoComDoacao(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955,  new Short[] {20,41,102,103,126}, 
						DominioMcoType.FPA, false, "ST", "F", false);
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
	
	@Test
	public void listarMovimentosComponentesSemExistsComAgrupamento() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarMovimentosComponentesSemExistsComAgrupamentoIrradiado(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), 9999999, (short) 955, 
						DominioMcoType.FPA, true, false);
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
	
	@Test
	public void listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado() throws ApplicationBusinessException {

		List<DoacaoColetaSangueVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			try {			
				//sem dados
				result = getDaoUnderTests().listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado(DateUtil.obterData(2100, 9, 01), 
						DateUtil.obterData(2100, 11, 01), //9999999, (short) 955, 
						DominioMcoType.FPA, true, "ST", false);
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