package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ConsultaNotasPolRNTest extends AGHUBaseUnitTest<ConsultaNotasPolRN>{

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	
	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */
	
	@Test
	public void verificarCertificadoAssinadoTest(){
		esperarVerificaImprimeNotasDao(); 
		Boolean condition = systemUnderTest.verificarCertificadoAssinado(123, DominioTipoDocumento.ACO);
		Assert.assertTrue(condition);
		
	}
	
	@Test
	public void buscarVersaoSeqDocTest(){
		esperarBuscarVersaoSeqDocDao();
		Integer condition = systemUnderTest.buscarVersaoSeqDoc(123, DominioTipoDocumento.ACO);
		Assert.assertNotNull(condition);
	}
	
	@Test
	public void chamarDocCertificadoTest(){
		esperarVerificaImprimeNotasDao();
		esperarBuscarVersaoSeqDocDao();
		Boolean condition = systemUnderTest.chamarDocCertificado(123, DominioTipoDocumento.ACO);
		Assert.assertTrue(condition);
	}
	
	@Test
	public void buscarDadosCabecalhoTest() throws ApplicationBusinessException {
		esperarBuscaConsProfPrescricaoMedicaFacade();
		esperarObterDescricaoCidCapitalizadaAmbulatorioFacade();
		esperarObterDescricaoCidCapitalizadaAmbulatorioFacade1();
		String condition = systemUnderTest.buscarDadosCabecalho(new MamNotaAdicionalEvolucoes());
		Assert.assertNotNull(condition);
	}
	
	@Test
	public void buscarDadosRodapeTest() throws ApplicationBusinessException {
		esperarBuscaConsProfPrescricaoMedicaFacade();
		esperarObterDescricaoCidCapitalizadaAmbulatorioFacade();
		esperarObterDescricaoCidCapitalizadaAmbulatorioFacade1();
		String condition = systemUnderTest.buscarDadosRodape(new MamNotaAdicionalEvolucoes());
		Assert.assertNotNull(condition);
	}
	
	@Test
	public void buscarDadosPacienteTest(){
		esperarBuscaPacientePacienteFacade();
		esperarObterDescricaoCidCapitalizadaAmbulatorioFacade1();
		MamNotaAdicionalEvolucoes nota = new MamNotaAdicionalEvolucoes();
		AipPacientes paciente = new AipPacientes();
		nota.setPaciente(paciente);
		String[] condition = systemUnderTest.buscarDadosPaciente(nota);
		Assert.assertNotNull(condition[0]);
		Assert.assertNotNull(condition[1]);
	}
	
	public void esperarVerificaImprimeNotasDao(){
		List<AghVersaoDocumento> l = new ArrayList<AghVersaoDocumento>();
		AghVersaoDocumento newAghVersaoDocumento = new AghVersaoDocumento();
		l.add(newAghVersaoDocumento);

		Mockito.when(mockedAmbulatorioFacade.verificaImprime(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class))).thenReturn(l);
	}
	
	public void esperarBuscarVersaoSeqDocDao(){
		List<AghVersaoDocumento> l = new ArrayList<AghVersaoDocumento>();
		AghVersaoDocumento newAghVersaoDocumento = new AghVersaoDocumento();
		newAghVersaoDocumento.setSeq(1);
		l.add(newAghVersaoDocumento);

		Mockito.when(mockedAmbulatorioFacade.buscarVersaoSeqDoc(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class))).thenReturn(l);
	}
	
	public void esperarBuscaConsProfPrescricaoMedicaFacade() throws ApplicationBusinessException {
		Object[] retorno = new Object[4];
		for (int i = 0; i < retorno.length; i++) {
			retorno[i] = new String(); 
		}
		Mockito.when(mockedPrescricaoMedicaFacade.buscaConsProf(Mockito.any(RapServidores.class))).thenReturn(retorno);
	}
	
	public void esperarObterDescricaoCidCapitalizadaAmbulatorioFacade1(){
		Mockito.when(mockedAmbulatorioFacade.obterDescricaoCidCapitalizada(Mockito.anyString())).thenReturn("");
	}
	
	public void esperarObterDescricaoCidCapitalizadaAmbulatorioFacade(){
		Mockito.when(mockedAmbulatorioFacade.obterDescricaoCidCapitalizada(Mockito.anyString(), Mockito.any(CapitalizeEnum.class))).thenReturn("");
	}
	
	public void esperarBuscaPacientePacienteFacade(){
		AipPacientes retorno = new AipPacientes();
		retorno.setProntuario(123);
		Mockito.when(mockedPacienteFacade.buscaPaciente(Mockito.anyInt())).thenReturn(retorno);
	}
	
	
	public void setSystemUnderTest(ConsultaNotasPolRN systemUnderTest) {
		this.systemUnderTest = systemUnderTest;
	}

	public ConsultaNotasPolRN getSystemUnderTest() {
		return systemUnderTest;
	}

	public void setMockedPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade) {
		this.mockedPrescricaoMedicaFacade = mockedPrescricaoMedicaFacade;
	}

	public IPrescricaoMedicaFacade getMockedPrescricaoMedicaFacade() {
		return mockedPrescricaoMedicaFacade;
	}

	public void setMockedAmbulatorioFacade(IAmbulatorioFacade mockedAmbulatorioFacade) {
		this.mockedAmbulatorioFacade = mockedAmbulatorioFacade;
	}

	public IAmbulatorioFacade getMockedAmbulatorioFacade() {
		return mockedAmbulatorioFacade;
	}

	public void setMockedPacienteFacade(IPacienteFacade mockedPacienteFacade) {
		this.mockedPacienteFacade = mockedPacienteFacade;
	}

	public IPacienteFacade getMockedPacienteFacade() {
		return mockedPacienteFacade;
	}
}
