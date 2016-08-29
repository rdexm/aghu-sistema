package br.gov.mec.aghu.faturamento;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;

public class FatEspelhoItemContaHospDAOTest extends AbstractDAOTest<FatEspelhoItemContaHospDAO> { 

	@Override
	protected void initMocks() {}

	@Override
	protected void finalizeMocks() {}
	
	@Override
	protected FatEspelhoItemContaHospDAO doDaoUnderTests() {
		return new FatEspelhoItemContaHospDAO() {
			private static final long serialVersionUID = 9082181739008187955L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatEspelhoItemContaHospDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return FatEspelhoItemContaHospDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			@Override
			public boolean isOracle() {
				return FatEspelhoItemContaHospDAOTest.this.isOracle();
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return FatEspelhoItemContaHospDAOTest.this.createHibernateQuery(query);
			}
		};
	}


	@Test
	public void listarEspelhoItemContaHospPorCthSeq() {
		if (isEntityManagerOk()) {
			//assert
			this.daoUnderTests.listarEspelhoItemContaHospPorCthSeq(1);
			Assert.assertTrue(true);
		}
	}

	@Test
	public void cItemAgrupPorProcComDados() {

		if (isEntityManagerOk()) {
			//assert
			this.daoUnderTests.cItemAgrupPorProc(441009, DominioTipoItemEspelhoContaHospitalar.D, Boolean.FALSE);
		}
	}

	@Test
	public void listarcItemAgrupPorProcComp() {

		if (isEntityManagerOk()) {
			this.daoUnderTests.cItemAgrupPorProcComp(null, 
					DominioTipoItemEspelhoContaHospitalar.D, Boolean.FALSE, null, null, null, null, null);
		}
	}
	
	@Test
	public void obterFatEspelhoItemContaHospPorCthSeqSeqpTivTaoCodSus(){
		if (isEntityManagerOk()) {
			//assert
			Integer cthSeq = 441684;
			Short seqp = Short.valueOf("1");
			Long pCodSus = 802010024L;
			Byte pTivSeq = 20;
			Byte pTaoSeq = Byte.valueOf("34");
			
			//sem dados 
			FatEspelhoItemContaHosp result = this.daoUnderTests.obterFatEspelhoItemContaHospPorCthSeqSeqpTivTaoCodSus(cthSeq, seqp, pCodSus, pTivSeq, pTaoSeq);
			logger.info(result);
		}
	}
}