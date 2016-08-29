package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.exames.cadastrosapoio.business.GrupoMaterialAnaliseON.GrupoMaterialONExceptionCode;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoMaterialAnaliseONTest extends AGHUBaseUnitTest<GrupoMaterialAnaliseON>{
	
	
	
	@Test
	public void testValidarCamposSuccess() {
		final Integer seqExclusao = 12;
		final AelGrupoMaterialAnalise grupoMaterialAnalise = new AelGrupoMaterialAnalise();
		grupoMaterialAnalise.setSeq(seqExclusao);
		grupoMaterialAnalise.setDescricao("descricao teste");
		
		try {
			systemUnderTest.validarCampos(grupoMaterialAnalise);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testValidarCamposError() {
		final Integer seqExclusao = 12;
		final AelGrupoMaterialAnalise grupoMaterialAnalise = new AelGrupoMaterialAnalise();
		grupoMaterialAnalise.setSeq(seqExclusao);
		
		try {
			systemUnderTest.validarCampos(grupoMaterialAnalise);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoMaterialONExceptionCode.CAMPO_OBRIGATORIO);
		}
	}
	
}
