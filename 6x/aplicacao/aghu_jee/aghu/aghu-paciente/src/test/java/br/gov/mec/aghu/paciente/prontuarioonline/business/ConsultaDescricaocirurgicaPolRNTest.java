package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ConsultaDescricaocirurgicaPolRNTest extends AGHUBaseUnitTest<ConsultaDescricaoCirurgicaPolRN>{

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private IBlocoCirurgicoFacade mockedBlocoCirurgicoFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */
	
	@Before
	public void doBeforeEachTestCase() throws BaseException{
		whenObterServidorLogado();
	}

	@Test
	public void buscarIdadeTest(){
		String condition = systemUnderTest.buscarIdade(new Date(), new Date());
		Assert.assertNotNull(condition);
		
	}
	
	@Test
	public void buscarResponsavelTest() throws BaseException{
		esperarbuscaConselhoProfissionalServidorVO();
		String condition = systemUnderTest.buscarResponsavel(Integer.valueOf(1).shortValue(), 1);
		Assert.assertNotNull(condition);
		
	}
	
	@Test
	public void verificarBoolFDCTest(){
		esperarObterMaxFDCSeqp();
		Boolean condition = systemUnderTest.verificarBoolFDC(1, Integer.valueOf(1).shortValue());
		Assert.assertTrue(condition);
	}
	
	@Test
	public void verificarBoolNTATest(){
		esperarverificarBoolNTA();
		Boolean condition = systemUnderTest.verificarBoolNTA(1, Integer.valueOf(1).shortValue());
		Assert.assertTrue(condition);
	}
	
	@Test
	public void verificarMatriculaNTATest(){
		Integer condition = systemUnderTest.verificarMatriculaNTA(1);
		Assert.assertNotNull(condition);
	}
	
	@Test
	public void verificarVerificarM6Test(){
		Boolean condition = systemUnderTest.verificarM6(1);
		Assert.assertTrue(condition);
	}
	
	@Test
	public void verificarVerificarApresentaLabelProjetoTest(){
		Boolean condition = systemUnderTest.verificarApresentaLabelProjeto(new String());
		Assert.assertTrue(condition);
	}
	
	@Test
	public void verificarBuscarNumDesenhoTest(){
		esperarObterMaxFDCSeqp();
		String condition = systemUnderTest.buscarNumDesenho(1, Integer.valueOf(1).shortValue(), 1);
		Assert.assertNotNull(condition);
	}
		
	@Test
	public void verificarVerificarApresentaLabelRespTest(){
		esperarObterMaxFDCSeqp();
		Boolean condition = systemUnderTest.verificarApresentaLabelResp(new String());
		Assert.assertTrue(condition);
	}
	
	@Test
	public void verificarBuscarNumNotasAdicionaisTest(){
		esperarverificarBoolNTA();
		String condition = systemUnderTest.buscarNumNotasAdicionais(1, Integer.valueOf(1).shortValue(), 1);
		Assert.assertNotNull(condition);
	}
	
	@Test
	public void verificarVerificarApresentaTituloTest(){
		esperarMbcNotaAdicionaisDaoObterNTASeqp();
		Boolean condition = systemUnderTest.verificarApresentaTitulo(1, Integer.valueOf(1).shortValue());
		Assert.assertTrue(condition);
	}
	
	@Test
	public void verificarBuscarLeitoRodapeTest(){
		esperarFarmaciaFacadeObterLocalizacaoPacienteParaRelatorio();
		String condition = systemUnderTest.buscarLeitoRodape(new AghAtendimentos());
		Assert.assertNotNull(condition);
	}
	
	@ Test
	public void verificarBuscarConselhoTest() throws ApplicationBusinessException {
		//esperarbuscaConselhoProfissionalServidorVO();
		esperarBuscaConselhoProfissional();
		String condition = systemUnderTest.buscarConselho(Integer.valueOf(1).shortValue(), 1);
		Assert.assertNotNull(condition);
	}
	
	public void esperarFarmaciaFacadeObterLocalizacaoPacienteParaRelatorio() {
		Mockito.when(mockedFarmaciaFacade.obterLocalizacaoPacienteParaRelatorio(Mockito.any(AghAtendimentos.class))).thenReturn("");
	}
	
	public void esperarbuscaConselhoProfissionalServidorVO() throws ApplicationBusinessException {
		Mockito.when(mockedPrescricaoMedicaFacade.buscaConselhoProfissionalServidorVO(Mockito.anyInt(), Mockito.anyShort(), Mockito.anyBoolean()))
		.thenReturn(new BuscaConselhoProfissionalServidorVO());
	}
	
	public void esperarBuscaConselhoProfissional() {
		Mockito.when(mockedRegistroColaboradorFacade.buscaConselhoProfissional(Mockito.anyInt(), Mockito.anyShort()))
		.thenReturn(new ConselhoProfissionalServidorVO());
	}
	
	public void esperarObterMaxFDCSeqp(){
		Mockito.when(mockedBlocoCirurgicoFacade.obterMaxFDCSeqp(Mockito.anyInt(), Mockito.anyShort()))
		.thenReturn(1);
	}
	
	public void esperarverificarBoolNTA(){
		Mockito.when(mockedBlocoCirurgicoFacade.obterMaxNTASeqp(Mockito.anyInt(), Mockito.anyShort()))
		.thenReturn(1);
	}
	
	public void esperarMbcNotaAdicionaisDaoObterNTASeqp(){
		Mockito.when(mockedBlocoCirurgicoFacade.obterNTASeqp(Mockito.anyInt(), Mockito.anyShort()))
		.thenReturn(1);
	}

	public void setMockedPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade) {
		this.mockedPrescricaoMedicaFacade = mockedPrescricaoMedicaFacade;
	}

	public IPrescricaoMedicaFacade getMockedPrescricaoMedicaFacade() {
		return mockedPrescricaoMedicaFacade;
	}

	public void setMockedFarmaciaFacade(IFarmaciaFacade mockedFarmaciaFacade) {
		this.mockedFarmaciaFacade = mockedFarmaciaFacade;
	}

	public IFarmaciaFacade getMockedFarmaciaFacade() {
		return mockedFarmaciaFacade;
	}
	
	public void setMockedBlocoCirurgicoFacade(IBlocoCirurgicoFacade mockedBlocoCirurgicoFacade) {
		this.mockedBlocoCirurgicoFacade = mockedBlocoCirurgicoFacade;
	}

	public IBlocoCirurgicoFacade getMockedBlocoCirurgicoFacade() {
		return mockedBlocoCirurgicoFacade;
	}
	
    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
