package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AtendimentosEmergenciaPOLRNTest extends AGHUBaseUnitTest<AtendimentosEmergenciaPOLRN>{

	
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private IAghuFacade mockedAghucade;
	
	@Test
	public void obterAtendimentoPorTriagemTest() {
		final Long trgSeq = (long) 5974;
		esperarAghAtendimentos();
		Assert.assertNotNull(systemUnderTest.obterAtendimentoPorTriagem(trgSeq));
	}

	//Expectations para aghuFacade
	private void esperarAghAtendimentos() {
		Mockito.when(mockedAghucade.obterAtendimentoPorTriagem(Mockito.anyLong())).thenReturn(getAtendimento());
	}	
	
	private AghAtendimentos getAtendimento() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(12389);	
		return atendimento; 
	}
	
	@Test
	public void verificarImpressaoTest() {
		final Long trgSeq = (long) 5974;
		final Long rgtSeq = (long) 4396;
		esperarMamEvolucoes();
  	    esperarMamAnamneses();	
		Assert.assertNotNull(systemUnderTest.verificarImpressao(trgSeq, rgtSeq));
	}

	//Expectations para ambulatorioFacade
	private void esperarMamEvolucoes() {
		Mockito.when(mockedAmbulatorioFacade.obterEvolucaoPorTriagemERegistro(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getEvolucoes());
	}	
	
	private List<MamEvolucoes> getEvolucoes() {
		List<MamEvolucoes> evolucoes = new ArrayList<MamEvolucoes>(); 	
		return evolucoes; 
	}
	
	//Expectations para ambulatorioFacade
	private void esperarMamAnamneses() {
		Mockito.when(mockedAmbulatorioFacade.obterAnamnesePorTriagemERegistro(Mockito.anyLong(), Mockito.anyLong())).thenReturn(getAnamneses());
	}	
	
	private List<MamAnamneses> getAnamneses() {
		List<MamAnamneses> anamneses = new ArrayList<MamAnamneses>(); 	
		return anamneses; 
	}
	
	
	
}