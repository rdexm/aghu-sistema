package br.gov.mec.aghu.configuracao;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.bancosangue.dao.AbsDoacoesDAO;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;

public class AbsDoacoesDAOTest extends AbstractDAOTest<AbsDoacoesDAO> {
	
	private final Integer matricula = 9999999;
	private final Short serVinCodigo = 955;
	private final String tdoCodigo = "F";


	@Override
	protected AbsDoacoesDAO doDaoUnderTests() {
		return new AbsDoacoesDAO() {
			private static final long serialVersionUID = 3239646207687383479L;

			@Override
			public boolean isOracle() {
				return AbsDoacoesDAOTest.this.isOracle();
			}
			
			@Override
			protected SQLQuery createSQLQuery(String query) {
				return AbsDoacoesDAOTest.this.createSQLQuery(query);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return AbsDoacoesDAOTest.this.createHibernateQuery(query);
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
	public void listarDoadorSangueTriagemClinica() {
		if (isEntityManagerOk()) {
			//assert
			//sem dados
			getDaoUnderTests().listarDoadorSangueTriagemClinica(DateUtil.obterData(2100, 01, 01), 
					DateUtil.obterData(2100, 02, 01),
					matricula,
					serVinCodigo);
			//com dados
			getDaoUnderTests().listarDoadorSangueTriagemClinica(DateUtil.obterData(2001, 01, 01), 
					DateUtil.obterData(2001, 02, 01), 
					matricula, 
					serVinCodigo);
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void listarDoacaoPorTipo() {
		if (isEntityManagerOk()) {
			//assert
			//sem dados
			getDaoUnderTests().listarDoacaoPorTipo(DateUtil.obterData(2100, 01, 01), 
					DateUtil.obterData(2100, 02, 01), 
					tdoCodigo, matricula, serVinCodigo); 
			Assert.assertTrue(true);
		}
	}

	@Test
	public void listarDoacaoPorPeriodo() {
		if (isEntityManagerOk()) {
			//assert
			//sem dados
			getDaoUnderTests().listarDoacaoPorPeriodo(DateUtil.obterData(2100, 01, 01), 
					DateUtil.obterData(2100, 02, 01), 
					matricula, serVinCodigo); 
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void listarDoacaoColetaSangue() {
		if (isEntityManagerOk()) {
			//sem dados
			getDaoUnderTests().listarDoacaoColetaSangue(DateUtil.obterData(2100, 01, 01), 
					DateUtil.obterData(2100, 02, 01), 
					tdoCodigo, matricula, serVinCodigo); 
			//com dados
			//result = getDaoUnderTests().listarDoacaoColetaSangue(DateUtil.obterData(2001, 01, 01), DateUtil.obterData(2001, 02, 01), tdoCodigo, matricula, serVinCodigo); 
			Assert.assertTrue(true);
		}
	}
}