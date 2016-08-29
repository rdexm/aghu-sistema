package br.gov.mec.aghu.core.dao;


import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.core.dao.test.AghParametros;
import br.gov.mec.aghu.core.persistence.dao.EntityIntrospector;

public class PreviousEntitySearcherTest {
	
	private PreviousEntitySearcher systemUnderTest;
	
	
	@Before
	public void doBeforeEachTestCase() {
		systemUnderTest = new PreviousEntitySearcher();
	}
	
	
	/**
	 * 
	 * 
	 */
	@Test
	public void testBuildQueryAghParametros() {
		String expectedHql = "select o.seq, o.sisSigla, sistema, o.nome, o.criadoEm, o.criadoPor, o.alteradoEm, o.alteradoPor, o.vlrData, o.vlrNumerico, o.vlrTexto, o.descricao, o.rotinaConsistencia, o.exemploUso, o.vlrDataPadrao, o.vlrNumericoPadrao, o.vlrTextoPadrao from AghParametros o  left outer join o.sistema sistema  where o.seq = :entityId ";
		
		Field fieldId = EntityIntrospector.getFieldId(AghParametros.class);
		String hql = systemUnderTest.buildQuery(AghParametros.class, fieldId);
		
		Assert.assertTrue(expectedHql.equalsIgnoreCase(hql));
		
	}
	
	
}
