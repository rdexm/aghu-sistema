package br.gov.mec.aghu.ambulatorio.business;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;


public class AtendimentoPacientesAgendadosRNTest extends AGHUBaseUnitTest<AtendimentoPacientesAgendadosRN>{

	@Test
	public void verificaPendenteRPV1(){
		Boolean retorno;
		
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.R);
		assertEquals(true, retorno);
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.P);
		assertEquals(true, retorno);
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.V);
		assertEquals(true, retorno);
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.A);
		assertEquals(false, retorno);
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.C);
		assertEquals(false, retorno);
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.E);
		assertEquals(false, retorno);
		retorno = systemUnderTest.verificaPendenteRPV(DominioIndPendenteAmbulatorio.X);
		assertEquals(false, retorno);
	}
}
