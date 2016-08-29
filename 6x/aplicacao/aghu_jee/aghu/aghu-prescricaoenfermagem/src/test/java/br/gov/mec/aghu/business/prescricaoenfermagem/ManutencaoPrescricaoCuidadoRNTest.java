package br.gov.mec.aghu.business.prescricaoenfermagem;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.prescricaoenfermagem.ManutencaoPrescricaoCuidadoRN.ManutencaoPrescricaoCuidadoRNExceptionCode;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManutencaoPrescricaoCuidadoRNTest extends AGHUBaseUnitTest<ManutencaoPrescricaoCuidadoRN>{

	@Mock
	private EpeCuidadosDAO mockedEpeCuidadosDAO;

	@Test
	public void testExcecaoVerificarDescricaoCuidado()
			throws BaseException {

		EpeCuidados cuidado = new EpeCuidados();
		cuidado.setIndDigitaComplemento(true);
		Mockito.when(mockedEpeCuidadosDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(cuidado);
		
		EpePrescricoesCuidados prescricao = new EpePrescricoesCuidados();
		prescricao.setCuidado(cuidado);
		
		try{
			systemUnderTest.verificarDescricaoCuidado(prescricao);	
		}
		catch (BaseException e) {
			Assert.assertTrue(e.getCode() == ManutencaoPrescricaoCuidadoRNExceptionCode.EPE_00232);
		}
	}

}
