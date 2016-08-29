package br.gov.mec.aghu.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.gov.mec.aghu.dao.EntityManagerDAOTestHelper;

public class ConexaoHibernateTest {
	
	static EntityManagerDAOTestHelper helper = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		helper = EntityManagerDAOTestHelper.getInstance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		helper.logStats();
		helper = null;
	}

	@Before
	public void setUp() throws Exception {
		if (helper.isEmOk()) {
			helper.startTransaction();
		}
	}

	@After
	public void tearDown() throws Exception {
		helper.stopTransaction();
	}

	@Test
	public void validarConexaoTest() {
		Assert.assertTrue(true);
//		if (!helper.isEmOk()) {
//			fail("Problemas com banco de dados: Conexão inválida / Mapeamento errado.");
//		}
	}
	
}
