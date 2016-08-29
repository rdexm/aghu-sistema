package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoReinternacaoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaReinternacaoRNTest extends AGHUBaseUnitTest<ManterAltaReinternacaoRN>{

	@Mock
	private MpmMotivoReinternacaoDAO mockedMotivoReinternacaoDAO;
	
	@Test
	public void verificarDataReinternacaoTest() {
		
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -1);
	
		try {
			
			systemUnderTest.verificarDataReinternacao(data.getTime());
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("MPM_02891", e.getMessage());
			
		}
		
	}
	
	@Test
	public void verificarObservacaoTest001() {
		
		final MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao();
		String novoObservacao = "teste";
		Integer novoMrnSeq = 1;
		motivoReinternacao.setIndExigeComplemento(false);

		
		Mockito.when(mockedMotivoReinternacaoDAO.obterMotivoReinternacaoPeloId(Mockito.anyInt())).thenReturn(motivoReinternacao);

		try {
			
			systemUnderTest.verificarObservacao(novoObservacao, novoMrnSeq);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("MPM_02854", e.getMessage());
			
		}
		
	}
	
	@Test
	public void verificarObservacaoTest002() {
		
		final MpmMotivoReinternacao motivoReinternacao = new MpmMotivoReinternacao();
		String novoObservacao = null;
		Integer novoMrnSeq = 1;
		motivoReinternacao.setIndExigeComplemento(true);

		Mockito.when(mockedMotivoReinternacaoDAO.obterMotivoReinternacaoPeloId(Mockito.anyInt())).thenReturn(motivoReinternacao);
		
		try {
			
			systemUnderTest.verificarObservacao(novoObservacao, novoMrnSeq);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("MPM_02853", e.getMessage());
			
		}
		
	}

}
