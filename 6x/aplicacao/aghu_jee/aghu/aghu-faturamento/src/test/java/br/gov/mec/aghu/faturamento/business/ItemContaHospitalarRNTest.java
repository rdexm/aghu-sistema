package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ItemContaHospitalarRNTest extends AGHUBaseUnitTest<ItemContaHospitalarRN>{

	@Mock
	private VFatContaHospitalarPacDAO mockedVFatContaHospitalarPacDAO;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private FatItemContaHospitalarJnDAO mockedFatItemContaHospitalarJnDAO;
	@Mock
	private IExamesFacade mockedIExamesFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private FatProcedHospInternosDAO mockedFatProcedHospInternosDAO;
	@Mock
	private ContaHospitalarRN mockedContaHospitalarRN;
	@Mock
	private FatContasHospitalaresDAO mockedFatContasHospitalaresDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	
	@Before
	public void doBefore() throws Exception {
		whenObterServidorLogado();
	}

	/**
	 * Testa Calcula Idade com dados nulos
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testCalculaIdadeDadosNulosTest() throws ApplicationBusinessException {

		VFatContaHospitalarPac vFat = new VFatContaHospitalarPac();
		Mockito.when(mockedVFatContaHospitalarPacDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(vFat);

		systemUnderTest.fatcBuscaIdadePac(3994);
	}

	/**
	 * Testa calcula idade sem datas
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testCalculaIdadeSemData() throws ApplicationBusinessException {

		VFatContaHospitalarPac vFat = new VFatContaHospitalarPac();
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(498);
		vFat.setPaciente(paciente);
		Mockito.when(mockedVFatContaHospitalarPacDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(vFat);

		Mockito.when(mockedPacienteFacade.obterAipPacientesPorChavePrimaria(Mockito.anyInt())).thenReturn(paciente);

		systemUnderTest.fatcBuscaIdadePac(3994);
	}
	
	/**
	 * Testa calcula idade com datas < 1 ano
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testCalculaIdadeComDataMenosUmAno() throws ApplicationBusinessException {
		VFatContaHospitalarPac vFat = new VFatContaHospitalarPac();
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(498);
		paciente.setDtNascimento(DateUtil.adicionaMeses(new Date(), -2));
		vFat.setCthDtIntAdministrativa(new Date());
		vFat.setPaciente(paciente);
		Mockito.when(mockedVFatContaHospitalarPacDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(vFat);

		Mockito.when(mockedPacienteFacade.obterAipPacientesPorChavePrimaria(Mockito.anyInt())).thenReturn(paciente);

		systemUnderTest.fatcBuscaIdadePac(3994);
	}
	
	/**
	 * Testa calcula idade com datas > 1 ano
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testCalculaIdadeComDataMaisUmAno() throws ApplicationBusinessException {
		
		VFatContaHospitalarPac vFat = new VFatContaHospitalarPac();
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(498);
		paciente.setDtNascimento(DateUtil.obterData(1982, 06, 05));
		vFat.setCthDtIntAdministrativa(new Date());
		vFat.setPaciente(paciente);
		
		Mockito.when(mockedVFatContaHospitalarPacDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(vFat);

		Mockito.when(mockedPacienteFacade.obterAipPacientesPorChavePrimaria(Mockito.anyInt())).thenReturn(paciente);

		systemUnderTest.fatcBuscaIdadePac(3994);
	}

	/**
	 * Testa AposExcluirItemContaHospitalar com dados nullos
	 * @throws ApplicationBusinessException
	 */
	
	@Test(expected = NullPointerException.class)
	public void testAposExcluirItemContaHospitalarNullo() throws ApplicationBusinessException {
		systemUnderTest.executarAposExcluirItemContaHospitalar(null);
	}

	/**
	 * Testa AposExcluirItemContaHospitalar com dados
	 * @throws ApplicationBusinessException
	 */
	
	@Test
	public void testAposExcluirItemContaHospitalar() throws ApplicationBusinessException {
		FatItemContaHospitalar fat = new FatItemContaHospitalar();
		FatItemContaHospitalarId id = new FatItemContaHospitalarId(1, (short) 1);
		fat.setId(id);
	}

	/** 
	 * Testa AposAtualizarItemContaHospitalar com dados
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAposAtualizarItemContaHospitalar() throws ApplicationBusinessException {
		FatItemContaHospitalar oldFat = new FatItemContaHospitalar();
		FatItemContaHospitalarId oldId = new FatItemContaHospitalarId(1, (short) 1);
		oldFat.setId(oldId);

		FatItemContaHospitalar newFat = new FatItemContaHospitalar();
		FatItemContaHospitalarId newId = new FatItemContaHospitalarId(2, (short) 1);
		newFat.setId(newId);
		
		systemUnderTest.executarAposAtualizarItemContaHospitalar(newFat, oldFat);
	}
	
	/**
	 * Testa a execucao fatpVerItemSolic com dados nulos
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testFatpVerItemSolicNullo() throws ApplicationBusinessException {
		AelItemSolicitacaoExames resultado = null;
		Mockito.when(mockedIExamesFacade.obteritemSolicitacaoExamesPorChavePrimaria(Mockito.any(AelItemSolicitacaoExamesId.class))).thenReturn(resultado);

		systemUnderTest.fatpVerItemSolic(1, (short)1);
	}
		
	/**
	 * Testa a execucao fatpVerItemSolic com dados
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testFatpVerItemSolic() throws ApplicationBusinessException {
		AelItemSolicitacaoExames resultado = new AelItemSolicitacaoExames();
		Mockito.when(mockedIExamesFacade.obteritemSolicitacaoExamesPorChavePrimaria(Mockito.any(AelItemSolicitacaoExamesId.class))).thenReturn(resultado);

		systemUnderTest.fatpVerItemSolic(181117, (short)1);
	}

	/**
	 * Testa a execucao fatpVerPrcrProced com dados nulos
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testFatpVerPrcrProcedNullo() throws ApplicationBusinessException {
	
		MpmPrescricaoProcedimento resultado = null;
		Mockito.when(mockedPrescricaoMedicaFacade.obterProcedimentoPorId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(resultado);

		systemUnderTest.fatpVerPrcrProced(1, 1l);
	}
	
	/**
	 * Testa a execucao fatpVerPrcrProced com dados
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testFatpVerPrcrProced() throws ApplicationBusinessException {

		MpmPrescricaoProcedimento resultado = new MpmPrescricaoProcedimento();
		Mockito.when(mockedPrescricaoMedicaFacade.obterProcedimentoPorId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(resultado);

		systemUnderTest.fatpVerPrcrProced(1, 1l);
	}
	
	/**
	 * Testa a execucao fatpVerPrcrNpts com dados nulos
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testFatpVerPrcrNptsNullo() throws ApplicationBusinessException {

		MpmPrescricaoNpt resultado = null;
		Mockito.when(mockedPrescricaoMedicaFacade.obterPrescricaoNptPorChavePrimaria(Mockito.any(MpmPrescricaoNptId.class))).thenReturn(resultado);

		systemUnderTest.fatpVerPrcrNpts(1, 1);
	}
	
	/**
	 * Testa a execucao fatpVerPrcrNpts com dados
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testFatpVerPrcrNpts() throws ApplicationBusinessException {

		MpmPrescricaoNpt resultado = new MpmPrescricaoNpt();
		Mockito.when(mockedPrescricaoMedicaFacade.obterPrescricaoNptPorChavePrimaria(Mockito.any(MpmPrescricaoNptId.class))).thenReturn(resultado);

		systemUnderTest.fatpVerPrcrNpts(1, 1);
	}

	/**
	 * Testa a execucao executarAntesExcluirItemContaHospitalar
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void testExecutarAntesExcluirItemContaHospitalar() throws ApplicationBusinessException {
	
		systemUnderTest.executarAntesExcluirItemContaHospitalar(DominioSituacaoItenConta.A);
	}

	/**
	 * Testa a execucao executarAntesExcluirItemContaHospitalar
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testExecutarAntesExcluirItemContaHospitalarException() throws ApplicationBusinessException {
	
		systemUnderTest.executarAntesExcluirItemContaHospitalar(DominioSituacaoItenConta.N);
	}
	
	/**
	 * Testa a execucao executarAntesInserirItemContaHospitalarException
	 * @throws BaseException 
	 */
	
	@Test(expected = ApplicationBusinessException.class)
	public void testExecutarAntesInserirItemContaHospitalarException() 
		throws BaseException {
		RapServidores servidorLogado = mockedServidorLogadoFacade.obterServidorLogado();	
		FatProcedHospInternos resultado = new FatProcedHospInternos();
		resultado.setSituacao(DominioSituacao.I);
		Mockito.when(mockedFatProcedHospInternosDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(resultado);

		Boolean retorno = false;
		Mockito.when(mockedContaHospitalarRN.rnCthcVerReapres(Mockito.anyInt())).thenReturn(retorno);

		Mockito.when(mockedContaHospitalarRN.rnCthcVerDesdobr(Mockito.anyInt())).thenReturn(retorno);

		FatItemContaHospitalar fat = new FatItemContaHospitalar();

		FatItemContaHospitalarId fatId = new FatItemContaHospitalarId();
		fatId.setCthSeq(1);
		fatId.setSeq((short) 1);
		fat.setId(fatId);
		
		FatProcedHospInternos procHospInt = new FatProcedHospInternos();
		procHospInt.setSeq(1);
		procHospInt.setSituacao(DominioSituacao.A);
		fat.setProcedimentoHospitalarInterno(procHospInt);
		fat.setIndOrigem(DominioIndOrigemItemContaHospitalar.ABS);
		
		systemUnderTest.executarAntesInserirItemContaHospitalar(fat, servidorLogado, new Date(), null);
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
