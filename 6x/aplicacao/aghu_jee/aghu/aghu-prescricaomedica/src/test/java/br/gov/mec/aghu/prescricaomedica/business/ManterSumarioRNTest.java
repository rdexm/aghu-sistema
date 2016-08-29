package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.jdbc.Expectations;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioValorDataItemSumario;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.MpmDataItemSumario;
import br.gov.mec.aghu.model.MpmDataItemSumarioId;
import br.gov.mec.aghu.model.MpmItemPrescricaoSumario;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidadoId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmDataItemSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Metodos nao testados pois nao possuiam regras de negocio no momento da implementacao dos testes
 * 
 * public void geraItens(...)
 * 
 *
 */
public class ManterSumarioRNTest extends AGHUBaseUnitTest<ManterSumarioRN>{

	@Mock
	private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
	@Mock
	private MpmItemPrescricaoSumarioDAO mockedMpmItemPrescricaoSumarioDAO;
	@Mock
	private MpmPrescricaoCuidadoDAO mockedMpmPrescricaoCuidadoDAO;
	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;
	@Mock
	private IBancoDeSangueFacade mockedBancoSangueFacade;
	@Mock
	private MpmSolicitacaoConsultoriaDAO mockedMpmSolicitacaoConsultoriaDAO;
	@Mock
	private MpmPrescricaoProcedimentoDAO mockedMpmPrescricaoProcedimentoDAO;
	@Mock
	private MpmPrescricaoNptDAO mockedMpmPrescricaoNptDAO;
	@Mock
	private MpmDataItemSumarioDAO mockedMpmDataItemSumarioDAO;
	@Mock
	private ManterSintaxeSumarioRN mockedManterSintaxeSumarioRN;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private ManterAltaSumarioRN mockedManterAltaSumarioRN;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;

	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSumarioPrescricaoDadosNulosTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedPacienteFacade.gerarAtendimentoPaciente(Mockito.anyInt())).thenReturn(1);
	
		Mockito.when(mockedManterAltaSumarioRN.obterDataInternacao2(Mockito.anyInt())).thenReturn(new Date());
	
		Mockito.when(mockedAghuFacade.obterDataFimAtendimento(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(), 1));
	
		MpmPrescricaoMedica pm = new MpmPrescricaoMedica();
		pm.setDtReferencia(new Date());
		Mockito.when(mockedMpmPrescricaoMedicaDAO.obterPrescricaoMedicaComMaiorDataReferenciaParaGerarSumario(Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(pm);
		
		MpmPrescricaoMedica pm2 = new MpmPrescricaoMedica();
		List<MpmPrescricaoMedica> lista = new ArrayList<MpmPrescricaoMedica>();
		lista.add(pm2);
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(Mockito.anyInt(), 
				Mockito.any(Date.class))).thenReturn(lista);
	
		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyLong())).thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(listaDataItemSumario);
		
		systemUnderTest.geraDadosSumarioPrescricao(null, null);
		
	}

	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSumarioPrescricaoTest() throws ApplicationBusinessException {
		
			Mockito.when(mockedPacienteFacade.gerarAtendimentoPaciente(Mockito.anyInt())).thenReturn(1);

			Mockito.when(mockedManterAltaSumarioRN.obterDataInternacao2(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(),  -2));

			Mockito.when(mockedAghuFacade.obterDataFimAtendimento(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(),  1));

			MpmPrescricaoMedica pm = new MpmPrescricaoMedica();
			Mockito.when(mockedMpmPrescricaoMedicaDAO.obterPrescricaoMedicaComMaiorDataReferenciaParaGerarSumario(Mockito.anyInt(), Mockito.any(Date.class), 
					Mockito.any(Date.class))).thenReturn(pm);
			
			List<MpmPrescricaoMedica> listaPrescricaoMedica = new ArrayList<MpmPrescricaoMedica>(1);
			MpmPrescricaoMedica pm2 = new MpmPrescricaoMedica();
			listaPrescricaoMedica.add(pm2);
			List<MpmPrescricaoMedica> lista = new ArrayList<MpmPrescricaoMedica>();
			lista.add(pm2);
			Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(Mockito.anyInt(), 
					Mockito.any(Date.class))).thenReturn(lista);

			List<MpmPrescricaoMedica> listaPrescricaoMed = new ArrayList<MpmPrescricaoMedica>(1);
			MpmPrescricaoMedica pm3 = new MpmPrescricaoMedica();
			MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
			id.setAtdSeq(1);
			id.setSeq(1);
			pm3.setId(id);
			listaPrescricaoMed.add(pm3);

			Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasComDataImpSumario(Mockito.anyInt(), 
					Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(listaPrescricaoMed);

			Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), 
					Mockito.anyInt(), Mockito.anyLong())).thenReturn(1);

			List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
			MpmDataItemSumario dis = new MpmDataItemSumario();
			MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
			disId.setApaAtdSeq(1);
			disId.setApaSeq(1);
			disId.setItuSeq(1);
			disId.setSeqp(1);
			dis.setId(disId);
			dis.setData(new Date());
			dis.setValor(DominioValorDataItemSumario.P.toString());
			
			listaDataItemSumario.add(dis);
			
			Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(),
					Mockito.anyInt())).thenReturn(listaDataItemSumario);

			systemUnderTest.geraDadosSumarioPrescricao(1, DominioTipoEmissaoSumario.I);
		
	}
	
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSumarioPrescricaoTipoEmissaoPTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedPacienteFacade.gerarAtendimentoPaciente(Mockito.anyInt())).thenReturn(1);
		
		Mockito.when(mockedManterAltaSumarioRN.obterDataInternacao2(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(),  -2));
	
		Mockito.when(mockedAghuFacade.obterDataFimAtendimento(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(), 1));
		
		List<MpmPrescricaoMedica> listaPrescMed = new ArrayList<MpmPrescricaoMedica>(1);
		MpmPrescricaoMedica pm = new MpmPrescricaoMedica();
		pm.setDtReferencia(DateUtil.adicionaDias(new Date(),  -3));
		MpmPrescricaoMedicaId id1 = new MpmPrescricaoMedicaId();
		id1.setAtdSeq(1);
		id1.setSeq(1);
		pm.setId(id1);
		listaPrescMed.add(pm);
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasParaGerarSumarioDePrescricao(Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(listaPrescMed);

		List<MpmPrescricaoMedica> listaPrescricaoMedica = new ArrayList<MpmPrescricaoMedica>(1);
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(Mockito.anyInt(), 
				Mockito.any(Date.class))).thenReturn(listaPrescricaoMedica);

		List<MpmPrescricaoMedica> listaPrescricaoMed = new ArrayList<MpmPrescricaoMedica>(1);
		MpmPrescricaoMedica pm3 = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(1);
		id.setSeq(1);
		pm3.setId(id);
		listaPrescricaoMed.add(pm3);
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasComDataImpSumario(Mockito.anyInt(), 
				Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(listaPrescricaoMed);

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyLong())).thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(listaDataItemSumario);
		
		systemUnderTest.geraDadosSumarioPrescricao(1, DominioTipoEmissaoSumario.P);
	}
	
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSumarioPrescricaoTemDadosFalseTest() throws ApplicationBusinessException {

		Mockito.when(mockedPacienteFacade.gerarAtendimentoPaciente(Mockito.anyInt())).thenReturn(1);
		
		Mockito.when(mockedManterAltaSumarioRN.obterDataInternacao2(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(),  -2));
	
		Mockito.when(mockedAghuFacade.obterDataFimAtendimento(Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(), 1));

		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasParaGerarSumarioDePrescricao(Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(null);

		List<MpmPrescricaoMedica> listaPrescricaoMedica = new ArrayList<MpmPrescricaoMedica>(1);
		MpmPrescricaoMedica pm2 = new MpmPrescricaoMedica();
		listaPrescricaoMedica.add(pm2);
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(Mockito.anyInt(), 
				Mockito.any(Date.class))).thenReturn(listaPrescricaoMedica);
		
		List<MpmPrescricaoMedica> listaPrescricaoMed = new ArrayList<MpmPrescricaoMedica>(1);
		MpmPrescricaoMedica pm3 = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(1);
		id.setSeq(1);
		pm3.setId(id);
		listaPrescricaoMed.add(pm3);
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarPrescricoesMedicasComDataImpSumario(Mockito.anyInt(), Mockito.any(Date.class),
				Mockito.any(Date.class))).thenReturn(listaPrescricaoMed);

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyLong())).thenReturn(1);

		Mockito.when(mockedMpmDataItemSumarioDAO.obterMenorDataDoDataItemSumario(Mockito.anyInt(), Mockito.anyInt())).thenReturn(DateUtil.adicionaDias(new Date(),  -5));
		
		systemUnderTest.geraDadosSumarioPrescricao(1, DominioTipoEmissaoSumario.P);
		
	}
	
	//###############################//
	//###### Prescricao Dietas ######//
	//###############################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosPrescricaoDietasDadosNulosTest() throws ApplicationBusinessException {
		
		List<MpmPrescricaoDieta> listaPrescricaoDieta =  new ArrayList<MpmPrescricaoDieta>(1);
		MpmPrescricaoDieta pc = new MpmPrescricaoDieta();
		MpmPrescricaoDietaId id = new MpmPrescricaoDietaId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pc.setId(id);
		
		pc.setDthrInicio(new Date());
		pc.setDthrFim(new Date());
		pc.setServidorValidacao(new RapServidores());
		pc.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoDieta.add(pc);
		
		Mockito.when(mockedMpmPrescricaoDietaDAO.obterPrescricoesDietaParaSumarioDieta(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(listaPrescricaoDieta);

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).
		thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);

		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).
		thenReturn(listaDataItemSumario);

		systemUnderTest.processaListaPrescricaoDietas(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoDietasTest() throws ApplicationBusinessException {
		
		List<MpmPrescricaoDieta> listaPrescricaoDieta1 =  new ArrayList<MpmPrescricaoDieta>(1);
		MpmPrescricaoDieta pc = new MpmPrescricaoDieta();
		MpmPrescricaoDietaId id = new MpmPrescricaoDietaId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pc.setId(id);
		
		pc.setDthrInicio(new Date());
		pc.setDthrFim(new Date());
		pc.setServidorValidacao(new RapServidores());
		pc.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoDieta1.add(pc);
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoDietaDAO.obterPrescricoesDietaPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).
		thenReturn(listaPrescricaoDieta1);

		Mockito.when(mockedMpmPrescricaoDietaDAO.obterPrescricoesDietaPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).
		thenReturn(new ArrayList<MpmPrescricaoDieta>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).
		thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);

		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).
		thenReturn(listaDataItemSumario);

		List<MpmPrescricaoDieta> listaPrescricaoDieta = new ArrayList<MpmPrescricaoDieta>();
		MpmPrescricaoDieta prescricaoDieta = new MpmPrescricaoDieta();
		MpmPrescricaoDietaId prescricaoDietaId = new MpmPrescricaoDietaId();
		prescricaoDietaId.setAtdSeq(123);
		prescricaoDietaId.setSeq((long)123);
		prescricaoDieta.setId(prescricaoDietaId);
		Calendar c = Calendar.getInstance();
		
		prescricaoDieta.setDthrInicio(c.getTime());
		prescricaoDieta.setDthrFim(new Date());
		prescricaoDieta.setServidorValidaMovimentacao(new RapServidores());
		prescricaoDieta.setIndPendente(DominioIndPendenteItemPrescricao.N);
		listaPrescricaoDieta.add(prescricaoDieta);
		
		systemUnderTest.processaListaPrescricaoDietas(listaPrescricaoDieta,1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoDietasDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		List<MpmPrescricaoDieta> listaPrescricaoDieta1 =  new ArrayList<MpmPrescricaoDieta>(1);
		MpmPrescricaoDieta pc = new MpmPrescricaoDieta();
		MpmPrescricaoDietaId id = new MpmPrescricaoDietaId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pc.setId(id);
		
		pc.setDthrInicio(new Date());
		pc.setDthrFim(new Date());
		pc.setServidorValidacao(new RapServidores());
		pc.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoDieta1.add(pc);
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoDietaDAO.obterPrescricoesDietaParaSumarioDieta(Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(listaPrescricaoDieta1);

		Mockito.when(mockedMpmPrescricaoDietaDAO.obterPrescricoesDietaPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(listaPrescricaoDieta1);

		Mockito.when(mockedMpmPrescricaoDietaDAO.obterPrescricoesDietaPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmPrescricaoDieta>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemDietasMedicas(Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyLong())).thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt())).thenReturn(listaDataItemSumario);
		
		List<MpmPrescricaoDieta> listaPrescricaoDieta = new ArrayList<MpmPrescricaoDieta>();
		MpmPrescricaoDieta prescricaoDieta = new MpmPrescricaoDieta();
		MpmPrescricaoDietaId prescricaoDietaId = new MpmPrescricaoDietaId();
		prescricaoDietaId.setAtdSeq(123);
		prescricaoDietaId.setSeq((long)123);
		prescricaoDieta.setId(prescricaoDietaId);
		Calendar c = Calendar.getInstance();
		prescricaoDieta.setDthrInicio(c.getTime());
		prescricaoDieta.setDthrFim(new Date());
		prescricaoDieta.setServidorValidaMovimentacao(new RapServidores());
		prescricaoDieta.setIndPendente(DominioIndPendenteItemPrescricao.N);
		listaPrescricaoDieta.add(prescricaoDieta);
		
		systemUnderTest.processaListaPrescricaoDietas(listaPrescricaoDieta, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}
	

	//###############################//
	//##### Prescricao Cuidados #####//
	//###############################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosPrescricaoCuidadosDadosNulosTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemCuidadosMedicos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);

		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(listaDataItemSumario);

		systemUnderTest.processaListaPrescricaoCuidados(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoCuidadosTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario(); 
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoCuidadoDAO.obterPrescricoesCuidadoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
		.thenReturn(new ArrayList<MpmPrescricaoCuidado>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemCuidadosMedicos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);

		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt())).thenReturn(listaDataItemSumario);

		List<MpmPrescricaoCuidado> listaPrescricaoCuidado =  new ArrayList<MpmPrescricaoCuidado>(1);
		MpmPrescricaoCuidado pc = new MpmPrescricaoCuidado();
		MpmPrescricaoCuidadoId id = new MpmPrescricaoCuidadoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pc.setId(id);
		
		pc.setDthrInicio(new Date());
		pc.setDthrFim(new Date());
		pc.setServidorValidacao(new RapServidores());
		pc.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoCuidado.add(pc);
		systemUnderTest.processaListaPrescricaoCuidados(listaPrescricaoCuidado, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoCuidadosDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoCuidadoDAO.obterPrescricoesCuidadoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmPrescricaoCuidado>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemCuidadosMedicos(Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyLong())).thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt())).thenReturn(listaDataItemSumario);

		List<MpmPrescricaoCuidado> listaPrescricaoCuidado =  new ArrayList<MpmPrescricaoCuidado>(1);
		MpmPrescricaoCuidado pc = new MpmPrescricaoCuidado();
		MpmPrescricaoCuidadoId id = new MpmPrescricaoCuidadoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pc.setId(id);
		
		pc.setDthrInicio(new Date());
		pc.setDthrFim(new Date());
		pc.setServidorValidacao(new RapServidores());
		pc.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoCuidado.add(pc);
		systemUnderTest.processaListaPrescricaoCuidados(listaPrescricaoCuidado,1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}
	

	//###############################//
	//### Prescricao Medicamentos ###//
	//###############################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosPrescricaoMedicamentosDadosNulosTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);

		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);
		
		systemUnderTest.processaListaPrescricaoMdtos(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoMedicamentosTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario(); 
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		List<MpmPrescricaoMdto> listaPrescricaoMdto =  new ArrayList<MpmPrescricaoMdto>(1);
		MpmPrescricaoMdto pm = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pm.setId(id);
		
		pm.setDthrInicio(new Date());
		pm.setDthrFim(new Date());
		pm.setServidorValidacao(new RapServidores());
		pm.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoMdto.add(pm);
		systemUnderTest.processaListaPrescricaoMdtos(listaPrescricaoMdto, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoMedicamentosDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		List<MpmPrescricaoMdto> listaPrescricaoMdto =  new ArrayList<MpmPrescricaoMdto>(1);
		MpmPrescricaoMdto pm = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pm.setId(id);
		
		pm.setDthrInicio(new Date());
		pm.setDthrFim(new Date());
		pm.setServidorValidacao(new RapServidores());
		pm.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoMdto.add(pm);
		systemUnderTest.processaListaPrescricaoMdtos(listaPrescricaoMdto,1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}

	//##############################//
	//##### Prescricao Solucao #####//
	//##############################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosPrescricaoSolucoesDadosNulosTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		systemUnderTest.processaListaPrescricaoSolucoes(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoSolucoesTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		List<MpmPrescricaoMdto> listaPrescricaoMdto = new ArrayList<MpmPrescricaoMdto>(1);
		MpmPrescricaoMdto pm = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pm.setId(id);
		
		pm.setDthrInicio(new Date());
		pm.setDthrFim(new Date());
		pm.setServidorValidacao(new RapServidores());
		pm.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoMdto.add(pm);
		systemUnderTest.processaListaPrescricaoSolucoes(listaPrescricaoMdto, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 *  
	 */
	@Test
	public void verificarGeraDadosPrescricaoSolucoesDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);
		
		List<MpmPrescricaoMdto> listaPrescricaoMdto = new ArrayList<MpmPrescricaoMdto>(1);
		MpmPrescricaoMdto pm = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pm.setId(id);
		
		pm.setDthrInicio(new Date());
		pm.setDthrFim(new Date());
		pm.setServidorValidacao(new RapServidores());
		pm.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoMdto.add(pm);
		systemUnderTest.processaListaPrescricaoSolucoes(listaPrescricaoMdto,1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}
	

	//###############################//
	//### Prescricao Procedimento ###//
	//###############################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosPrescricaoProcedimentosDadosNulosTest() throws ApplicationBusinessException {

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemProcedimento(Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyLong())).thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		systemUnderTest.processaListaPrescricaoProcedimentos(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosPrescricaoProcedimentosTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);
		
		List<MpmPrescricaoProcedimento> listaPrescricaoProcedimento = new ArrayList<MpmPrescricaoProcedimento>(1);
		MpmPrescricaoProcedimento pp = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pp.setId(id);
		
		pp.setDthrInicio(new Date());
		pp.setDthrFim(new Date());
		pp.setServidorValidacao(new RapServidores());
		pp.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoProcedimento.add(pp);
		
		systemUnderTest.processaListaPrescricaoProcedimentos(listaPrescricaoProcedimento, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosPrescricaoProcedimentosDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		List<MpmPrescricaoProcedimento> listaPrescricaoProcedimento1 = new ArrayList<MpmPrescricaoProcedimento>(1);
		MpmPrescricaoProcedimento pp1 = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId id1 = new MpmPrescricaoProcedimentoId();
		id1.setAtdSeq(1);
		id1.setSeq((long)1);
		pp1.setId(id1);
		
		pp1.setDthrInicio(new Date());
		pp1.setDthrFim(new Date());
		pp1.setServidorValidacao(new RapServidores());
		pp1.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoProcedimento1.add(pp1);
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);

		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.obterPrescricoesProcedimentoParaSumarioMdto(Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(listaPrescricaoProcedimento1);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterPrescricoesMdtoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(new ArrayList<MpmPrescricaoMdto>(0));

		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.obterPrescricoesProcedimentoPai(Mockito.anyLong(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmPrescricaoProcedimento>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
		.thenReturn(1);
				
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		List<MpmPrescricaoProcedimento> listaPrescricaoProcedimento = new ArrayList<MpmPrescricaoProcedimento>(1);
		MpmPrescricaoProcedimento pp = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId();
		id.setAtdSeq(1);
		id.setSeq((long)1);
		pp.setId(id);
		
		pp.setDthrInicio(new Date());
		pp.setDthrFim(new Date());
		pp.setServidorValidacao(new RapServidores());
		pp.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoProcedimento.add(pp);
		
		systemUnderTest.processaListaPrescricaoProcedimentos(listaPrescricaoProcedimento, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}

	//################################//
	//### Solicitacao Hemoterapica ###//
	//################################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosSolicitacoesHemoterapicasDadosNulosTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemHemoterapia(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);
		
		systemUnderTest.processaListaPrescricaoHemoterapica(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSolicitacoesHemoterapicasTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedBancoSangueFacade.obterPrescricoesHemoterapicaPai(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<AbsSolicitacoesHemoterapicas>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemHemoterapia(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapicas = new ArrayList<AbsSolicitacoesHemoterapicas>(1);
		AbsSolicitacoesHemoterapicas sh = new AbsSolicitacoesHemoterapicas();
		AbsSolicitacoesHemoterapicasId id = new AbsSolicitacoesHemoterapicasId();
		id.setAtdSeq(1);
		id.setSeq(1);
		sh.setId(id);
		
		sh.setDthrFim(new Date());
		sh.setDthrSolicitacao(new Date());
		
		listaSolicitacoesHemoterapicas.add(sh);
		systemUnderTest.processaListaPrescricaoHemoterapica(listaSolicitacoesHemoterapicas, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSolicitacoesHemoterapicasDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedBancoSangueFacade.obterPrescricoesHemoterapicaPai(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<AbsSolicitacoesHemoterapicas>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemHemoterapia(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);

		List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapicas = new ArrayList<AbsSolicitacoesHemoterapicas>(1);
		AbsSolicitacoesHemoterapicas sh = new AbsSolicitacoesHemoterapicas();
		AbsSolicitacoesHemoterapicasId id = new AbsSolicitacoesHemoterapicasId();
		id.setAtdSeq(1);
		id.setSeq(1);
		sh.setId(id);
		
		sh.setDthrFim(new Date());
		sh.setDthrSolicitacao(new Date());
		sh.setServidorValidaMovimentacao(new RapServidores());
		sh.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaSolicitacoesHemoterapicas.add(sh);
		systemUnderTest.processaListaPrescricaoHemoterapica(listaSolicitacoesHemoterapicas, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}
	
	//###############################//
	//### Solicitacao Consultoria ###//
	//###############################//
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosSolicitacaoConsultoriaDadosNulosTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemConsultoria(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);		
		
		systemUnderTest.processaListaPrescricaoConsultoria(null, null, null, null, null, null);
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSolicitacaoConsultoriaTest() throws ApplicationBusinessException {
		
		Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.obterSolicitacoesConsultoriaPai(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmSolicitacaoConsultoria>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemConsultoria(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);		

		List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria = new ArrayList<MpmSolicitacaoConsultoria>(1);
		MpmSolicitacaoConsultoria sc = new MpmSolicitacaoConsultoria();
		MpmSolicitacaoConsultoriaId id = new MpmSolicitacaoConsultoriaId();
		id.setAtdSeq(1);
		id.setSeq(1);
		sc.setId(id);

		sc.setDthrSolicitada(new Date());
		
		listaSolicitacaoConsultoria.add(sc);

		systemUnderTest.processaListaPrescricaoConsultoria(listaSolicitacaoConsultoria, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), new Date());
		
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosSolicitacaoConsultoriaDataReferenciaMaiorTest() throws ApplicationBusinessException {
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);

		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmSolicitacaoConsultoriaDAO.obterSolicitacoesConsultoriaPai(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmSolicitacaoConsultoria>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemConsultoria(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);

		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);		
		
		List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria = new ArrayList<MpmSolicitacaoConsultoria>(1);
		MpmSolicitacaoConsultoria sc = new MpmSolicitacaoConsultoria();
		MpmSolicitacaoConsultoriaId id = new MpmSolicitacaoConsultoriaId();
		id.setAtdSeq(1);
		id.setSeq(1);
		sc.setId(id);

		sc.setDthrSolicitada(new Date());
		
		listaSolicitacaoConsultoria.add(sc);

		systemUnderTest.processaListaPrescricaoConsultoria(listaSolicitacaoConsultoria, 1, 2, new Date(), DateUtil.adicionaDias(new Date(),  1), DateUtil.adicionaDias(new Date(),  1));
		
	}
	
	//#############################//
	//###    NutricaoParental   ###//
	//#############################//
	
	/**
	 * Testa a execucao com dados nulos
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void verificarGeraDadosPrescricaoNutricaoParentalDadosNulosTest() throws ApplicationBusinessException {
		List<MpmPrescricaoNpt> listaPrescricaoNpt =  new ArrayList<MpmPrescricaoNpt>(1);
		MpmPrescricaoNpt pc = new MpmPrescricaoNpt();
		MpmPrescricaoNptId id = new MpmPrescricaoNptId();
		id.setAtdSeq(1);
		id.setSeq(1);
		pc.setId(id);
		
		pc.setDthrInicio(new Date());
		pc.setDthrFim(new Date());
		pc.setServidorValidacao(new RapServidores());
		pc.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoNpt.add(pc);
		
		Mockito.when(mockedMpmPrescricaoNptDAO.obterPrescricoesNptParaSumarioPrescricao(Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(listaPrescricaoNpt);

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemNutricaoParental(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);		
		
		systemUnderTest.processaListaPrescricaoNutricaoParental(null, null, null, null, null, null);
	}
	
	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosPrescricaoNutricaoParentalTest() throws ApplicationBusinessException {
		List<MpmPrescricaoNpt> listaPrescricaoNpt1 =  new ArrayList<MpmPrescricaoNpt>(1);
		MpmPrescricaoNpt pc1 = new MpmPrescricaoNpt();
		MpmPrescricaoNptId id1 = new MpmPrescricaoNptId();
		id1.setAtdSeq(1);
		id1.setSeq(1);
		pc1.setId(id1);
		
		pc1.setDthrInicio(new Date());
		pc1.setDthrFim(new Date());
		pc1.setServidorValidacao(new RapServidores());
		pc1.setIndPendente(DominioIndPendenteItemPrescricao.N);
		
		listaPrescricaoNpt1.add(pc1);
		
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);
		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoNptDAO.obterPrescricoesNptPai(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmPrescricaoNpt>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemNutricaoParental(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);		
		
		
		List<MpmPrescricaoNpt> listaPrescricaoNpt = new ArrayList<MpmPrescricaoNpt>();
		MpmPrescricaoNpt prescricaoNpt = new MpmPrescricaoNpt();
		MpmPrescricaoNptId prescricaoNptId = new MpmPrescricaoNptId();
		prescricaoNptId.setAtdSeq(123);
		prescricaoNptId.setSeq(123);
		prescricaoNpt.setId(prescricaoNptId);
		Calendar c = Calendar.getInstance();
		prescricaoNpt.setDthrInicio(c.getTime());
		prescricaoNpt.setDthrFim(new Date());
		prescricaoNpt.setServidorValidaMovimentacao(new RapServidores());
		prescricaoNpt.setIndPendente(DominioIndPendenteItemPrescricao.N);
		listaPrescricaoNpt.add(prescricaoNpt);
		
		systemUnderTest.processaListaPrescricaoNutricaoParental(listaPrescricaoNpt, 1, 2, new Date(), DateUtil.adicionaDias(new Date(), 1), new Date());
	}

	/**
	 * Testa a execucao com dados validos
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarGeraDadosPrescricaoNutricaoParentalDataReferenciaMaiorTest() throws ApplicationBusinessException {
		MpmItemPrescricaoSumario itemPrescricaoSumario = new MpmItemPrescricaoSumario();
		itemPrescricaoSumario.setSeq(123);

		Mockito.when(mockedMpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(itemPrescricaoSumario);

		Mockito.when(mockedMpmPrescricaoNptDAO.obterPrescricoesNptPai(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), 
				Mockito.any(Date.class))).thenReturn(new ArrayList<MpmPrescricaoNpt>(0));

		Mockito.when(mockedManterSintaxeSumarioRN.montaSintaxeSumarioItemNutricaoParental(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(1);
		
		List<MpmDataItemSumario> listaDataItemSumario = new ArrayList<MpmDataItemSumario>(1);
		MpmDataItemSumario dis = new MpmDataItemSumario();
		MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
		disId.setApaAtdSeq(1);
		disId.setApaSeq(1);
		disId.setItuSeq(1);
		disId.setSeqp(1);
		dis.setId(disId);
		dis.setData(new Date());
		dis.setValor(DominioValorDataItemSumario.P.toString());
		
		listaDataItemSumario.add(dis);
		
		Mockito.when(mockedMpmDataItemSumarioDAO.listarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listaDataItemSumario);		
		
		List<MpmPrescricaoNpt> listaPrescricaoNpt = new ArrayList<MpmPrescricaoNpt>(
				1);
		MpmPrescricaoNpt pNpt = new MpmPrescricaoNpt();
		MpmPrescricaoNptId pNptId = new MpmPrescricaoNptId();
		pNptId.setAtdSeq(1);
		pNptId.setSeq(1);
		pNpt.setId(pNptId);

		pNpt.setServidorValidaMovimentacao(new RapServidores());
		pNpt.setIndPendente(DominioIndPendenteItemPrescricao.N);

		pNpt.setDthrInicio(new Date());
		pNpt.setDthrFim(new Date());

		listaPrescricaoNpt.add(pNpt);
		
		systemUnderTest.processaListaPrescricaoNutricaoParental(listaPrescricaoNpt, 1, 2, new Date(), DateUtil.adicionaDias(new Date(), 1), DateUtil.adicionaDias(new Date(), 1));
	}
	
}
