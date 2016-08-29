package br.gov.mec.aghu.prescricaomedica.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDietaId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterPrescricaoDietaONTest extends AGHUBaseUnitTest<ManterPrescricaoDietaON>{

	private static final Log log = LogFactory.getLog(ManterPrescricaoDietaONTest.class);

	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
	@Mock
	private PrescricaoMedicaON mockedPrescricaoMedicaON;
	@Mock
	private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
	@Mock
	private MpmItemPrescricaoDietaDAO mockedMpmItemPrescricaoDietaDAO;
	@Mock
	private ManterPrescricaoDietaRN mockedManterPrescricaoDietaRN;
	@Mock
	private IAghuFacade mockedAghuFacade;

	@Test
	public void validarNumeroVezesNulo() {
	
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta(
				new MpmItemPrescricaoDietaId(1, (long) 1, 1));

		try {
			systemUnderTest.validarNumeroVezes(itemDieta);			
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail();
		}
	}

}
