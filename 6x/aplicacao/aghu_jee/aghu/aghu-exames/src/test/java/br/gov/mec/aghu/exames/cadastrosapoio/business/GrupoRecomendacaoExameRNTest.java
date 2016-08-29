package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoRecomendacaoExameRNTest extends AGHUBaseUnitTest<GrupoRecomendacaoExameRN>{
	
	@Test(expected=IllegalArgumentException.class)
	public void testHasModificacao001() {
		AelGrupoRecomendacaoExame entity1 = null;
		AelGrupoRecomendacaoExame entity2 = null;
		
		systemUnderTest.hasModificacao(entity1, entity2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testHasModificacao002() {
		AelGrupoRecomendacaoExame entity1 = new AelGrupoRecomendacaoExame();
		AelGrupoRecomendacaoExame entity2 = null;
		
		systemUnderTest.hasModificacao(entity1, entity2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testHasModificacao003() {
		AelGrupoRecomendacaoExame entity1 = null;
		AelGrupoRecomendacaoExame entity2 = new AelGrupoRecomendacaoExame();
		
		systemUnderTest.hasModificacao(entity1, entity2);
	}
	
	@Test
	public void testHasModificacao004() {
		AelGrupoRecomendacaoExame entity1 = new AelGrupoRecomendacaoExame();
		AelGrupoRecomendacaoExame entity2 = new AelGrupoRecomendacaoExame();
		
		boolean modificado = systemUnderTest.hasModificacao(entity1, entity2);
		Assert.assertTrue(modificado == false);
	}

	@Test
	public void testHasModificacao005() {
		AelGrupoRecomendacaoExame entity1 = new AelGrupoRecomendacaoExame();
		AelGrupoRecomendacaoExame entity2 = new AelGrupoRecomendacaoExame();
		
		entity2.setCriadoEm(new Date());
		
		boolean modificado = systemUnderTest.hasModificacao(entity1, entity2);
		Assert.assertTrue(modificado);
	}
	
}
