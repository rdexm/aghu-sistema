package br.gov.mec.aghu.exames.business;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AghAtendimentosPacExternEnforceRNTest extends AGHUBaseUnitTest<AghAtendimentosPacExternEnforceRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private AghAtendimentosPacExternRN mockedAghAtendimentosPacExternRN;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	
	@Test
	public void testUpdateCobertura() {
		AghAtendimentosPacExtern atdPacExtern = new AghAtendimentosPacExtern();
		
		try {
			systemUnderTest.update(atdPacExtern, atdPacExtern, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testUpdateCoberturaComConvenioSaudePlanoDiferente() {
		AghAtendimentosPacExtern atdPacExtern1 = new AghAtendimentosPacExtern();
		atdPacExtern1.setConvenioSaudePlano(new FatConvenioSaudePlano());
		AghAtendimentosPacExtern atdPacExtern2 = new AghAtendimentosPacExtern();
		
		try {
			systemUnderTest.update(atdPacExtern1, atdPacExtern2, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testUpdateCoberturaComServidorDiferente() {
		AghAtendimentosPacExtern atdPacExtern1 = new AghAtendimentosPacExtern();
		AghAtendimentosPacExtern atdPacExtern2 = new AghAtendimentosPacExtern();
		atdPacExtern2.setServidor(new RapServidores());
		
		try {
			systemUnderTest.update(atdPacExtern1, atdPacExtern2, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.fail();
		}
	}

	
}
