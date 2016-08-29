package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioPontuacaoApacheCreatinaSerica;
import br.gov.mec.aghu.dominio.DominioPontuacaoApacheFrequenciaCardiaca;
import br.gov.mec.aghu.model.MpmEscalaGlasgow;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VisualizacaoFichaApacheONTest extends AGHUBaseUnitTest<VisualizacaoFichaApacheON>{

	@Test
	public void calcularPontuacaoFichaApacheNullTest() {

		try {
			MpmFichaApache fichaApache = new MpmFichaApache();
			
			Integer pontuacao = systemUnderTest.calcularPontuacaoFichaApache(fichaApache);
			Assert.assertEquals(pontuacao, null);
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	

	@Test
	public void calcularPontuacaoFichaApacheTest() {

		try {
			Integer pontuacaoEsperada = 21;
			
			MpmFichaApache fichaApache = new MpmFichaApache();
			fichaApache.setEscalaGlasgow(new MpmEscalaGlasgow());
			fichaApache.setPontuacaoApacheCreatininaSerica(DominioPontuacaoApacheCreatinaSerica.POSITIVO_4);
			fichaApache.setPontuacaoApacheFrequenciaCardiaca(DominioPontuacaoApacheFrequenciaCardiaca.POSITIVO_2);
			
			Integer pontuacao = systemUnderTest.calcularPontuacaoFichaApache(fichaApache);
			Assert.assertEquals(pontuacao, pontuacaoEsperada);
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}	
}
