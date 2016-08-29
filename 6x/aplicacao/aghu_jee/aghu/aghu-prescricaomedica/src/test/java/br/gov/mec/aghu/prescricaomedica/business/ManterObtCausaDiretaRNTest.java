package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObtCausaDiretaRNTest extends AGHUBaseUnitTest<ManterObtCausaDiretaRN>{
	
	@Test
	public void verificarTipoAltaSumarioTest() throws ApplicationBusinessException {
		
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		altaSumario.setSituacao(DominioSituacao.A);
		altaSumario.setTipo(DominioIndTipoAltaSumarios.ALT);
		
		try {
			
			systemUnderTest.verificarTipoAltaSumario(altaSumario);

		} catch (BaseException e) {
			
			Assert.assertEquals("MPM_02688", e.getMessage());
			
		}
		
	}

}
