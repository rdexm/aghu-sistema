package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNptId;
import br.gov.mec.aghu.model.MpmItemConsultoriaSumario;
import br.gov.mec.aghu.model.MpmItemConsultoriaSumarioId;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoSumario;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemConsultoriaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemCuidadoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemDietaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemHemoterapiaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemMdtoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemNptSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemProcedimentoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterSintaxeSumarioRNTest extends AGHUBaseUnitTest<ManterSintaxeSumarioRN>{
	
	private static final Log log = LogFactory.getLog(ManterSintaxeSumarioRNTest.class);

	@Mock
	private MpmSolicitacaoConsultoriaDAO mockedMpmSolicitacaoConsultoriaDAO; 
	@Mock
	private MpmItemPrescricaoSumarioDAO mockedMpmItemPrescricaoSumarioDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private MpmItemConsultoriaSumarioDAO mockedMpmItemConsultoriaSumarioDAO;
	@Mock
	private MpmPrescricaoNptDAO mockedMpmPrescricaoNptDAO;
	@Mock
	private MpmComposicaoPrescricaoNptDAO mockedMpmComposicaoPrescricaoNptDAO;
	@Mock
	private MpmItemPrescricaoNptDAO mockedMpmItemPrescricaoNptDAO;
	@Mock
	private MpmItemNptSumarioDAO mockedMpmItemNptSumarioDAO;
	@Mock
	private MpmItemCuidadoSumarioDAO mockedMpmItemCuidadoSumarioDAO;
	@Mock
	private IProcedimentoTerapeuticoFacade mockedProcedimentoTerapeuticoFacade;
	@Mock
	private MpmItemMdtoSumarioDAO mockedMpmItemMdtoSumarioDAO;
	@Mock
	private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
	@Mock
	private MpmItemPrescricaoDietaDAO mockedMpmItemPrescricaoDietaDAO;
	@Mock
	private MpmItemDietaSumarioDAO mockedMpmItemDietaSumarioDAO;
	@Mock
	private MpmPrescricaoCuidadoDAO mockedMpmPrescricaoCuidadoDAO;
	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;
	@Mock
	private MpmItemPrescricaoMdtoDAO mockedMpmItemPrescricaoMdtoDAO;
	@Mock
	private MpmPrescricaoProcedimentoDAO mockedMpmPrescricaoProcedimentoDAO;
	@Mock
	private MpmModoUsoPrescProcedDAO mockedMpmModoUsoPrescProcedDAO;
	@Mock
	private MpmItemProcedimentoSumarioDAO mockedMpmItemProcedimentoSumarioDAO;
	@Mock
	private IBancoDeSangueFacade mockedBancoSangueFacade;
	@Mock
	private MpmItemHemoterapiaSumarioDAO mockedMpmItemHemoterapiaSumarioDAO;
	
	//#################################
	//### TESTES PRESCRICAO SUMARIO ###
	//#################################
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected=NullPointerException.class)
	public void montaSintaxeSumarioItemConsultoriaDadosNulosTest() throws ApplicationBusinessException{
		
		MpmSolicitacaoConsultoria solConsul = new MpmSolicitacaoConsultoria();
		MpmSolicitacaoConsultoriaId solConsulId = new MpmSolicitacaoConsultoriaId();
		solConsul.setId(solConsulId);
		Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(solConsul);
		
		
		MpmItemPrescricaoSumario ips = new MpmItemPrescricaoSumario();
		ips.setSeq(123);
		List<MpmItemPrescricaoSumario> listaIps = new ArrayList<MpmItemPrescricaoSumario>();
		listaIps.add(ips);

		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(DominioTipoItemPrescricaoSumario.class)))
		.thenReturn(listaIps);

		AghAtendimentoPacientes atendimentoPaciente = new AghAtendimentoPacientes();
		AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
		atendimentoPacienteId.setAtdSeq(123);
		atendimentoPacienteId.setSeq(12);
		atendimentoPaciente.setId(atendimentoPacienteId);

		Mockito.when(mockedAghuFacade.obterAghAtendimentoPacientesPorChavePrimaria(Mockito.any(AghAtendimentoPacientesId.class))).thenReturn(atendimentoPaciente);

		systemUnderTest.montaSintaxeSumarioItemConsultoria(null, null, null);
	}

	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void montaSintaxeSumarioItemConsultoriaDadosValidosTest() throws ApplicationBusinessException {
		
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade("Geriatria");
		
		MpmSolicitacaoConsultoria solConsul = new MpmSolicitacaoConsultoria();
		MpmSolicitacaoConsultoriaId solConsulId = new MpmSolicitacaoConsultoriaId();
		solConsul.setId(solConsulId);
		solConsul.setEspecialidade(especialidade);
		Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(solConsul);

		MpmItemPrescricaoSumario ips = new MpmItemPrescricaoSumario();
		ips.setSeq(123);
		List<MpmItemPrescricaoSumario> listaIps = new ArrayList<MpmItemPrescricaoSumario>();
		listaIps.add(ips);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(DominioTipoItemPrescricaoSumario.class)))
		.thenReturn(listaIps);
		
		AghAtendimentoPacientes atendimentoPaciente = new AghAtendimentoPacientes();
		AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
		atendimentoPacienteId.setAtdSeq(123);
		atendimentoPacienteId.setSeq(12);
		atendimentoPaciente.setId(atendimentoPacienteId);
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPacientesPorChavePrimaria(Mockito.any(AghAtendimentoPacientesId.class))).thenReturn(atendimentoPaciente);
		systemUnderTest.montaSintaxeSumarioItemConsultoria(123, 123, 123);
	}

	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void montaSintaxeSumarioItemConsultoriaSemItemPrescricaoSumarioTest() throws ApplicationBusinessException{
		
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade("Geriatria");
		
		MpmSolicitacaoConsultoria solConsul = new MpmSolicitacaoConsultoria();
		MpmSolicitacaoConsultoriaId solConsulId = new MpmSolicitacaoConsultoriaId();
		solConsul.setId(solConsulId);
		solConsul.setEspecialidade(especialidade);
		solConsul.setTipo(DominioTipoSolicitacaoConsultoria.C);
		solConsul.setIndUrgencia(DominioSimNao.S);
		Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(solConsul);
		
		List<MpmItemPrescricaoSumario> listaIps = new ArrayList<MpmItemPrescricaoSumario>();
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(DominioTipoItemPrescricaoSumario.class)))
		.thenReturn(listaIps);
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(12);

		MpmItemConsultoriaSumarioId itemConsultoriaSumarioId = new MpmItemConsultoriaSumarioId();
		itemConsultoriaSumarioId.setItuSeq(123);
		itemConsultoriaSumarioId.setSeqp(12);
		MpmItemConsultoriaSumario itemConsultoriaSumario = new MpmItemConsultoriaSumario();
		itemConsultoriaSumario.setId(itemConsultoriaSumarioId);
		
		AghAtendimentoPacientes atendimentoPaciente = new AghAtendimentoPacientes();
		AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
		atendimentoPacienteId.setAtdSeq(123);
		atendimentoPacienteId.setSeq(12);
		atendimentoPaciente.setId(atendimentoPacienteId);
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPacientesPorChavePrimaria(Mockito.any(AghAtendimentoPacientesId.class))).thenReturn(atendimentoPaciente);
				
		systemUnderTest.montaSintaxeSumarioItemConsultoria(123, 123, 123);
	}

	
	//################################
	//### TESTES NUTRICAO PARENTAL ###
	//################################
	@Test(expected=NullPointerException.class)
	public void montaSintaxeSumarioItemNutricaoParentalDadosNulosTest() throws ApplicationBusinessException {
		MpmPrescricaoNpt pn = new MpmPrescricaoNpt(); 
		Mockito.when(mockedMpmPrescricaoNptDAO.obterNutricaoParentalTotalPeloId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(pn);
		systemUnderTest.montaSintaxeSumarioItemNutricaoParental(null, null, null);
	}
	
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void montaSintaxeSumarioItemNutricaoParentalDadosValidosTest() throws ApplicationBusinessException {
		
		MpmPrescricaoNptId id = new MpmPrescricaoNptId();
		id.setSeq(123);
		MpmPrescricaoNpt pn = new MpmPrescricaoNpt(); 
		pn.setId(id);
		pn.setSegueGotejoPadrao(false);
		pn.setBombaInfusao(false);
		Mockito.when(mockedMpmPrescricaoNptDAO.obterNutricaoParentalTotalPeloId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(pn);

		List<MpmComposicaoPrescricaoNpt> listaNpt = new ArrayList<MpmComposicaoPrescricaoNpt>();
		Mockito.when(mockedMpmComposicaoPrescricaoNptDAO.listarComposicoesPrescricaoNpt(Mockito.anyInt(), Mockito.anyLong())).thenReturn(listaNpt);

		List<MpmItemPrescricaoSumario> lista = new ArrayList<MpmItemPrescricaoSumario>();
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(DominioTipoItemPrescricaoSumario.class)))
		.thenReturn(lista);


		AghAtendimentoPacientes atdPac = new AghAtendimentoPacientes();
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPacientesPorChavePrimaria(Mockito.any(AghAtendimentoPacientesId.class))).thenReturn(atdPac);
				
		systemUnderTest.montaSintaxeSumarioItemNutricaoParental(123, 123, 123);
	}

	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void montaSintaxeSumarioItemNutricaoParentalDadosValidosComItemPrescricaoNptTest() throws ApplicationBusinessException {
		List<MpmItemPrescricaoNpt> listaItemNpt = new ArrayList<MpmItemPrescricaoNpt>();
		AfaComponenteNpt componenteNpt = new AfaComponenteNpt();
		componenteNpt.setIndImpDoseSumario(true);
		MpmItemPrescricaoNpt itemPrescNpt = new MpmItemPrescricaoNpt();
		AfaMedicamento med = new AfaMedicamento();
		AfaTipoApresentacaoMedicamento tipoApresentacaoMed = new AfaTipoApresentacaoMedicamento();
		tipoApresentacaoMed.setSigla("aa");
		med.setTipoApresentacaoMedicamento(tipoApresentacaoMed);
		componenteNpt.setAfaMedicamentos(med);
		itemPrescNpt.setAfaComponenteNpts(componenteNpt);
		listaItemNpt.add(itemPrescNpt);
		
		Mockito.when(mockedMpmItemPrescricaoNptDAO.listarItensPrescricaoNpt(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(listaItemNpt);
		
		MpmPrescricaoNptId id = new MpmPrescricaoNptId();
		id.setSeq(123);
		MpmPrescricaoNpt pn = new MpmPrescricaoNpt(); 
		pn.setId(id);
		pn.setSegueGotejoPadrao(true);
		pn.setBombaInfusao(true);
		pn.setObservacao("abc");
		Mockito.when(mockedMpmPrescricaoNptDAO.obterNutricaoParentalTotalPeloId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(pn);
		
		MpmComposicaoPrescricaoNptId cpNtpId = new MpmComposicaoPrescricaoNptId();
		cpNtpId.setSeqp((short)132);
		cpNtpId.setPnpAtdSeq(132);
		MpmComposicaoPrescricaoNpt cpNtp = new MpmComposicaoPrescricaoNpt();
		cpNtp.setId(cpNtpId);
		List<MpmComposicaoPrescricaoNpt> listaNpt = new ArrayList<MpmComposicaoPrescricaoNpt>();
		listaNpt.add(cpNtp);
		Mockito.when(mockedMpmComposicaoPrescricaoNptDAO.listarComposicoesPrescricaoNpt(Mockito.anyInt(), Mockito.anyLong())).thenReturn(listaNpt);

		List<MpmItemPrescricaoSumario> lista = new ArrayList<MpmItemPrescricaoSumario>();
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(DominioTipoItemPrescricaoSumario.class)))
		.thenReturn(lista);

		AghAtendimentoPacientes atdPac = new AghAtendimentoPacientes();
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPacientesPorChavePrimaria(Mockito.any(AghAtendimentoPacientesId.class))).thenReturn(atdPac);
		systemUnderTest.montaSintaxeSumarioItemNutricaoParental(123, 123, 123);
	}
	
}
