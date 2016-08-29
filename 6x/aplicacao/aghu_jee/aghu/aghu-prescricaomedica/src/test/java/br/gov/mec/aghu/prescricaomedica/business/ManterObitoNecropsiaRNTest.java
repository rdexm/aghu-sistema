package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObitoNecropsiaRNTest extends AGHUBaseUnitTest<ManterObitoNecropsiaRN>{
	
	@Test
	public void verificarIndicadorNecropsiaTest() throws ApplicationBusinessException {
		
		final DominioSimNao necropsia = DominioSimNao.S;
		final MpmAltaSumario altaSumario = new MpmAltaSumario();
		
		altaSumario.setTipo(DominioIndTipoAltaSumarios.ALT);
		altaSumario.setSituacao(DominioSituacao.I);
		
		try {
			
			systemUnderTest.verificarIndicadorNecropsia(necropsia, altaSumario);

		} catch (BaseException e) {
			
			Assert.assertEquals("MPM_02742", e.getMessage());
			
		}
		
		
	}

}
