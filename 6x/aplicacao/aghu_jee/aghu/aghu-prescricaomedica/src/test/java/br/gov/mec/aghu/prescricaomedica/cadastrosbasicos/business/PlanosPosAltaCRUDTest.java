package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PlanosPosAltaCRUDTest extends AGHUBaseUnitTest<PlanosPosAltaCRUD>{
	
	@Mock
	private MpmPlanoPosAltaDAO mockedMpmPlanoPosAltaDAO;
	
	@Test
	public void persistPlanoPosAlta() {
		MpmPlanoPosAlta plano =  new MpmPlanoPosAlta();
		plano.setIndOutros(DominioSimNao.N);
		plano.setIndExigeComplemento(true);
		
		try {
			systemUnderTest.persistPlanoPosAlta(plano);
		} catch (ApplicationBusinessException e) {
			fail();
		}
	}
}