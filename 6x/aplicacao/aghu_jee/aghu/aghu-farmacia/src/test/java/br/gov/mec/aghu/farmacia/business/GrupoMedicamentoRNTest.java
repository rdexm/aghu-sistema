package br.gov.mec.aghu.farmacia.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.farmacia.business.GrupoMedicamentoRN.GrupoMedicamentoRNExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoMedicamentoRNTest extends AGHUBaseUnitTest<GrupoMedicamentoRN>{

	private static final Log log = LogFactory.getLog(GrupoMedicamentoRNTest.class);
	
	@Test
	public void verificaDescricaoTest() {
		try {
			systemUnderTest.verificaDescricao();
			Assert.fail("Deveria ter lançado exceção AFA_00431");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00431", e
					.getCode(),
					GrupoMedicamentoRNExceptionCode.AFA_00431);
		}
	}
	
}
