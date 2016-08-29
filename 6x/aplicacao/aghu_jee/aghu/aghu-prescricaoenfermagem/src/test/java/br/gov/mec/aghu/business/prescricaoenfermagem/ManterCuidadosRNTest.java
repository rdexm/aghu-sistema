package br.gov.mec.aghu.business.prescricaoenfermagem;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.prescricaoenfermagem.ManterCuidadosON.ManterCuidadosONExceptionCode;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterCuidadosRNTest extends AGHUBaseUnitTest<ManterCuidadosRN>{

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;

	@Test
	public void testVerificarFrequenciaEPE_00228(){
		try {
			Short tipoFrequenciaSeq = Short.valueOf("0");
			Short frequenciaSeq = Short.valueOf("1");
			systemUnderTest.verificarFrequencia(tipoFrequenciaSeq, frequenciaSeq);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterCuidadosONExceptionCode.EPE_00228);
		}
	}
	
	@Test
	public void testVerificarFrequenciaEPE_00229(){
		try {
			Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(null);
			
			Short tipoFrequenciaSeq = Short.valueOf("1");
			Short frequenciaSeq = Short.valueOf("1");
			systemUnderTest.verificarFrequencia(tipoFrequenciaSeq, frequenciaSeq);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterCuidadosONExceptionCode.EPE_00229);
		}
	}
	
	@Test
	public void testVerificarFrequenciaEPE_00230(){
		try {
			MpmTipoFrequenciaAprazamento tipoFrequencia = new MpmTipoFrequenciaAprazamento();
			tipoFrequencia.setIndDigitaFrequencia(Boolean.FALSE);
			Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(tipoFrequencia);

			Short tipoFrequenciaSeq = Short.valueOf("1");
			Short frequenciaSeq = Short.valueOf("1");
			systemUnderTest.verificarFrequencia(tipoFrequenciaSeq, frequenciaSeq);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterCuidadosONExceptionCode.EPE_00230);
		}
	}
	
	@Test
	public void testVerificarFrequenciaEPE_00231(){
		try {
			MpmTipoFrequenciaAprazamento tipoFrequencia = new MpmTipoFrequenciaAprazamento();
			tipoFrequencia.setIndDigitaFrequencia(Boolean.TRUE);
			Mockito.when(mockedPrescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(Mockito.anyShort())).thenReturn(tipoFrequencia);
			
			Short tipoFrequenciaSeq = Short.valueOf("1");
			Short frequenciaSeq = Short.valueOf("1");
			systemUnderTest.verificarFrequencia(tipoFrequenciaSeq, frequenciaSeq);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterCuidadosONExceptionCode.EPE_00231);
		}
	}
}
