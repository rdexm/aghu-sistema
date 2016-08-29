package br.gov.mec.aghu.business.bancosangue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.business.bancosangue.SolicitacaoHemoterapicaRN.SolicitacaoHemoterapicaRNExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SolicitacaoHemoterapicaRNTest extends AGHUBaseUnitTest<SolicitacaoHemoterapicaRN> {

	private static final Log log = LogFactory.getLog(SolicitacaoHemoterapicaRNTest.class);

	@Test
	public void verificaDelecaoSolicitacaoHemoterapicaTest() {
		try {
			systemUnderTest.verificaDelecaoSolicitacaoHemoterapica();
			Assert.fail("Deveria ter lançado exceção ABS_00479");
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a ABS_00479", e.getCode(), SolicitacaoHemoterapicaRNExceptionCode.ABS_00479);
		}
	}
}
