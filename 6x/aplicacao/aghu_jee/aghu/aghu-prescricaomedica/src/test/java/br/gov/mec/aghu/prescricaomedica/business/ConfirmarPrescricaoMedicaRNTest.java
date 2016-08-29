package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.ConfirmarPrescricaoMedicaRN.ConfirmarPrescricaoMedicaExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.recursoshumanos.Pessoa;

/**
 * Classe de testes da RN de confirmar prescrição médica.
 * 
 * @author gmneto
 * 
 */
public class ConfirmarPrescricaoMedicaRNTest extends AGHUBaseUnitTest<ConfirmarPrescricaoMedicaRN>{
	
	private static final Log log = LogFactory.getLog(ConfirmarPrescricaoMedicaRNTest.class);
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
	@Mock
	private MpmCidAtendimentoDAO mockedCidAtendimentoDAO;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private MpmPrescricaoCuidadoDAO mockedMpmPrescricaoCuidadoDAO;
	@Mock
	private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;
	@Mock
	private MpmPrescricaoNptDAO mockedMpmPrescricaoNptDAO;
	@Mock
	private MpmPrescricaoProcedimentoDAO mockedMpmPrescricaoProcedimentoDAO;
	@Mock
	private PrescreverProcedimentoEspecialRN mockedPrescreverProcedimentoEspecialRN;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private Pessoa mockedPessoaLogada;
	@Mock
	private PrescricaoMedicaRN mockedPrescricaoMedicaRN;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IPermissionService mockedPermissionService; 
	@Mock
	private ManterPrescricaoMedicaON manterPrescricaoMedicaON;
	
	private void esperarRefresh() {
	}
		

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarDiagnosticosPorPacienteCid com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemDiagnosticos() {
		Mockito.when(mockedAmbulatorioFacade.listarDiagnosticosPorPacienteCid(Mockito.any(AipPacientes.class), Mockito.any(AghCid.class))).
		thenReturn(new ArrayList<MamDiagnostico>());
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * pesquisarPrescricaoNptPorPME com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemNPT(final List<MpmPrescricaoNpt> npts) {
		Mockito.when(mockedMpmPrescricaoNptDAO.pesquisarPrescricaoNptPorPME(Mockito.any(MpmPrescricaoMedicaId.class), Mockito.any(Date.class), Mockito.any(Boolean.class))).
		thenReturn(npts);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarDiagnosticosPorPacienteCid com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemDiagnosticosAtivos(
			final List<MamDiagnostico> diagnosticos) {
		Mockito.when(mockedAmbulatorioFacade.listarDiagnosticosAtivosPorCidAtendimento(Mockito.any(MpmCidAtendimento.class))).
		thenReturn(diagnosticos);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * obterListaMedicamentosPrescritosPelaChavePrescricao com quaisquer
	 * parâmetros adequados, retornando a lista passada por parâmetro a este
	 * método.
	 * 
	 * @param medicamentos
	 */
	private void esperarListagemMedicamentos(
			final List<MpmPrescricaoMdto> medicamentos) {
		Mockito.when(mockedMpmPrescricaoMdtoDAO.obterListaMedicamentosPrescritosPelaChavePrescricao(Mockito.any(MpmPrescricaoMedicaId.class), Mockito.any(Date.class))).
		thenReturn(medicamentos);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarDiagnosticosPorPacienteCid com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarContarDiagnosticosAtendimento(
			final Integer diagnosticos) {
		Mockito.when(mockedCidAtendimentoDAO.contarDiagnosticosAtendimento(Mockito.any(AghAtendimentos.class))).
		thenReturn(Long.valueOf(diagnosticos));
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarDiagnosticosPorPacienteCid com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemCidsAtendimento(
			final List<MpmCidAtendimento> cidsAtendimento) {
		Mockito.when(mockedCidAtendimentoDAO.listarCidAtendimentosPorAtendimento(Mockito.any(AghAtendimentos.class))).
		thenReturn(cidsAtendimento);
	}


	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarDiagnosticosPorPacienteCid com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemItensPrescricao(
			final List<ItemPrescricaoMedica> itens) {
		Mockito.when(mockedMpmPrescricaoMedicaDAO.listarItensPrescricaoMedicaConfirmacao(Mockito.any(MpmPrescricaoMedica.class), Mockito.any(Date.class))).
		thenReturn(itens);
	}

	private void esperarQtdItensPrescricao(
			final List<ItemPrescricaoMedicaVO> itens) throws ApplicationBusinessException {
		Mockito.when(manterPrescricaoMedicaON.buscarItensPrescricaoMedica(Mockito.any(MpmPrescricaoMedicaId.class), Mockito.any(Boolean.class))).
		thenReturn(itens);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * buscaDietaPorPrescricaoMedica com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemDietas(final List<MpmPrescricaoDieta> dietas) {
		Mockito.when(mockedMpmPrescricaoDietaDAO.buscaDietaPorPrescricaoMedica(Mockito.any(MpmPrescricaoMedicaId.class), Mockito.any(Date.class), Mockito.any(Boolean.class))).
		thenReturn(dietas);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarFatProcedHospInternosPorMaterial com quaisquer parâmetros
	 * adequados, retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListarFatProcedHospInternosPorMaterial(
			final List<FatProcedHospInternos> retorno) {
		Mockito.when(mockedFaturamentoFacade.listarFatProcedHospInternosPorMaterial(Mockito.any(ScoMaterial.class))).
		thenReturn(retorno);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarProcedimentosGeracaoLaudos com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListarProcedimentosGeracaoLaudos(
			final List<MpmPrescricaoProcedimento> retorno) {
		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.listarProcedimentosGeracaoLaudos(Mockito.any(MpmPrescricaoMedica.class))).
		thenReturn(retorno);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarDiagnosticosPorPacienteCid com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemPrescricaoCuidadoPendenteCpa(
			final List<MpmPrescricaoCuidado> itens) {
		Mockito.when(mockedMpmPrescricaoCuidadoDAO.listarPrescricaoCuidadoPendenteCpa(Mockito.any(MpmPrescricaoMedica.class))).
		thenReturn(itens);
	}

	/**
	 * Testa o caminho feliz de uma prescrição médica.
	 * @throws BaseException 
	 * 
	 */
	//@Test
	public void confirmarPrescricaoMedica() throws BaseException {
		
		esperarAtendimentoNaoAmbulatorial();
		Date dataAtual = new Date();

		List<MpmCidAtendimento> listaCidsAtendimento = new ArrayList<MpmCidAtendimento>();

		// neste cidAtendimento nada deve mudar
		// neste cidAtendimento o incCidPaciente deve ir p/
		// true.
		MpmCidAtendimento cidAtendimento1 = new MpmCidAtendimento();
		cidAtendimento1.setAltCidPaciente(true);
		AghCid cid = new AghCid();
		cid.setSituacao(DominioSituacao.A);
		cidAtendimento1.setCid(cid);

		// neste cidAtendimento o incAltPaciente deve ir p/ true. Seus
		// diagnósticos tb são alterados.
		MpmCidAtendimento cidAtendimento2 = new MpmCidAtendimento();
		cidAtendimento2.setAltCidPaciente(false);
		cidAtendimento2.setDthrFim(dataAtual);

		listaCidsAtendimento.add(cidAtendimento1);
		listaCidsAtendimento.add(cidAtendimento2);

		this.esperarListagemCidsAtendimento(listaCidsAtendimento);

		List<MamDiagnostico> listaDiagnosticos = new ArrayList<MamDiagnostico>();
		MamDiagnostico diagnostico = new MamDiagnostico();
		listaDiagnosticos.add(diagnostico);
		this.esperarListagemDiagnosticosAtivos(listaDiagnosticos);

		this.esperarListagemDiagnosticos();
		
		
		this.esperarListagemMedicamentos();
		
		this.esperarParametroUnidadeML();

		List<ItemPrescricaoMedica> itensPrescricao = new ArrayList<ItemPrescricaoMedica>();

		// o ind_pendete deve ir p/ 'N', o servidor validação deve ser o logado
		// e a dtrhvalida deve ser a atual.
		MpmSolicitacaoConsultoria solicitacaoConsultoria = new MpmSolicitacaoConsultoria();
		solicitacaoConsultoria
				.setIndPendente(DominioIndPendenteItemPrescricao.B);
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade("Cirurgia");
		solicitacaoConsultoria.setEspecialidade(especialidade);
		

		// o ind_pendete deve ir p/ 'N', o servidor validação deve ser o logado
		// e a dtrhvalida deve ser a atual.
		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		cuidado.setIndPendente(DominioIndPendenteItemPrescricao.R);
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setSintaxe("# X ao dia");
		cuidado.setMpmTipoFreqAprazamentos(tipoFrequenciaAprazamento);
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidadoUsual.setIndOutros(true);
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);
		
		
		// o ind_pendete deve ir p/ 'N', o servidor validação deve ser o logado
		// e a dtrhvalida deve ser a atual.
		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		dieta.setIndPendente(DominioIndPendenteItemPrescricao.P);
		

		// filho: o ind_pendete deve ir p/ 'N', o servidor validação deve ser o
		// logado e a dtrhvalida deve ser a atual.
		// 
		// pai: o ind_pendete deve ir p/ 'N', o
		// servidor valida movimentação deve ser o logado
		// e a dtrhvalidaMovimentação deve ser a atual
		MpmPrescricaoMdto medicamento = new MpmPrescricaoMdto();
		MpmPrescricaoMdto medicamentoPai = new MpmPrescricaoMdto();
		medicamento.setPrescricaoMdtoOrigem(medicamentoPai);
		medicamento.setIndPendente(DominioIndPendenteItemPrescricao.P);

		// o ind_pendete deve ir p/ 'N', o
		// servidor valida movimentação deve ser o logado
		// e a dtrhvalidaMovimentação deve ser a atual
		MpmPrescricaoProcedimento procedimento = new MpmPrescricaoProcedimento();
		MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId();
		procedimento.setId(id);
		procedimento.setIndPendente(DominioIndPendenteItemPrescricao.E);

		// o ind_pendete deve ir p/ 'N', o servidor validação deve ser o
		// logado e a dtrhvalida deve ser a atual. o
		// servidor valida movimentação deve ser o logado
		// e a dtrhvalidaMovimentação deve ser a atual
		MpmPrescricaoNpt npt = new MpmPrescricaoNpt();
		npt.setIndPendente(DominioIndPendenteItemPrescricao.Y);

		itensPrescricao.add(solicitacaoConsultoria);
		itensPrescricao.add(cuidado);
		itensPrescricao.add(dieta);
		itensPrescricao.add(medicamento);
		itensPrescricao.add(procedimento);
		itensPrescricao.add(npt);

		this.esperarListagemItensPrescricao(itensPrescricao);

		ArrayList<MpmPrescricaoCuidado> listaPrescricaoCuidado = new ArrayList<MpmPrescricaoCuidado>();
		this
				.esperarListagemPrescricaoCuidadoPendenteCpa(listaPrescricaoCuidado);

		

		this.esperarContarDiagnosticosAtendimento(1);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);
		this.esperarListagemDietas(listaDietas);

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AipPacientes paciente = new AipPacientes();
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.PERMITE_PRESCRICAO_BI);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidadeFuncional.setCaracteristicas(caracteristicas);

		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 3));
		atendimento.setPaciente(paciente);
		atendimento.setInternacao(internacao);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.S);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		
		try {
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);
			Date dataPosterior = new Date();

			Assert.assertNull(prescricao.getDthrMovimento());
			Assert.assertNull(prescricao.getDthrInicioMvtoPendente());
			Assert.assertEquals(prescricao.getSituacao(),
					DominioSituacaoPrescricao.L);

			Assert.assertTrue(cidAtendimento1.getIncCidPaciente());
			Assert.assertTrue(cidAtendimento2.getAltCidPaciente());

			Assert.assertTrue(DateUtil.entre(diagnostico.getDthrMvto(),
					dataAtual, dataPosterior));
			Assert.assertTrue(DateUtil.entre(diagnostico.getDthrValidaMvto(),
					dataAtual, dataPosterior));
			Assert
					.assertEquals(diagnostico.getIndSituacao(),
							DominioSituacao.I);
			Assert.assertEquals(diagnostico.getIndPendente(),
					DominioIndPendenteDiagnosticos.C);

			Assert.assertEquals(solicitacaoConsultoria.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(solicitacaoConsultoria
					.getDthrValida(), dataAtual, dataPosterior));
			Assert.assertNull(solicitacaoConsultoria
					.getDthrValidaMovimentacao());

			Assert.assertEquals(cuidado.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(cuidado.getDthrValida(),
					dataAtual, dataPosterior));
			Assert.assertNull(cuidado.getDthrValidaMovimentacao());

			Assert.assertEquals(dieta.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(dieta.getDthrValida(), dataAtual,
					dataPosterior));
			Assert.assertNull(dieta.getDthrValidaMovimentacao());

			Assert.assertEquals(medicamento.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(medicamento.getDthrValida(),
					dataAtual, dataPosterior));
			Assert.assertNull(medicamento.getDthrValidaMovimentacao());
			Assert.assertEquals(medicamentoPai.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(medicamentoPai
					.getDthrValidaMovimentacao(), dataAtual, dataPosterior));
			Assert.assertNull(medicamentoPai.getDthrValida());

			Assert.assertEquals(procedimento.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(procedimento
					.getDthrValidaMovimentacao(), dataAtual, dataPosterior));
			Assert.assertNull(procedimento.getDthrValida());

			Assert.assertEquals(npt.getIndPendente(),
					DominioIndPendenteItemPrescricao.N);
			Assert.assertTrue(DateUtil.entre(npt.getDthrValidaMovimentacao(),
					dataAtual, dataPosterior));
			Assert.assertTrue(DateUtil.entre(npt.getDthrValida(), dataAtual,
					dataPosterior));
			Assert.assertEquals(prescricao.getAtendimento().getIndPacCpa(),
					false);

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}

	private void esperarListagemMedicamentos() {
		Mockito.when(mockedMpmPrescricaoMdtoDAO.pesquisarMedicamentosPrescricaoCalculoQuantidade(Mockito.any(MpmPrescricaoMedica.class), Mockito.any(Date.class))).
		thenReturn(new ArrayList<MpmPrescricaoMdto>());
	}
	
	
	private void esperarParametroUnidadeML() {
		
		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(Mockito.any(AghuParametrosEnum.class))).
			thenReturn(null);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * Testa a confirmação de uma prescrição médica que esteja livre. O sistema
	 * deverá lançar uma exceção MPM-01256.
	 */
	@Test
	public void confirmarPrescricaoMedicaLivre() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.L);
		esperarAtendimentoNaoAmbulatorial();

		try {
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert.fail("Deveria ter lançado exceção MPM-01256");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a MPM-01256", e
					.getCode(),
					ConfirmarPrescricaoMedicaExceptionCode.PRESCRICAO_LIVRE);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica cuja dthr_movimento esteja
	 * nulo. O sistema deverá lançar uma exceção mpm-01257.
	 */
	@Test
	public void confirmarPrescricaoMedicaDthrNula() {

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		prescricao.setDthrMovimento(null);
		esperarAtendimentoNaoAmbulatorial();
	
		try {
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert.fail("Deveria ter lançado exceção mpm-01257");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a mpm-01257", e
					.getCode(),
					ConfirmarPrescricaoMedicaExceptionCode.DTHR_MOVIMENTO_NULO);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica cuja previsão de alta seja
	 * anterior a data atual. O sistema deverá lançar uma exceção
	 * PREVISAO_ALTA_ANTERIOR_DATA_ATUAL.
	 */
	//@Test
	public void confirmarPrescricaoDataPrevAlta() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, -3));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		esperarAtendimentoNaoAmbulatorial();

		try {
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert
					.fail("Deveria ter lançado exceção PREVISAO_ALTA_ANTERIOR_DATA_ATUAL");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert
					.assertEquals(
							"Exceção lançada deveria ser a PREVISAO_ALTA_ANTERIOR_DATA_ATUAL",
							e.getCode(),
							ConfirmarPrescricaoMedicaExceptionCode.PREVISAO_ALTA_ANTERIOR_DATA_ATUAL);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica sem qualquer diagnostico. O
	 * sistema deverá lançar uma exceção DIAGNOSTICOS_ATENDIMENTO.
	 */
	//@Test
	public void confirmarPrescricaoDiagnosticos() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 1));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);

		this
				.esperarContarDiagnosticosAtendimento(0);
		esperarAtendimentoNaoAmbulatorial();

		try {
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert.fail("Deveria ter lançado exceção prescrição sem diagnóstico");
			
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
			Assert.assertTrue(true);

		} catch (BaseException e) {
			Assert.fail("Deveria ter lançado exceção prescrição sem diagnóstico");
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica sem qualquer dieta para
	 * unidades funcionais onde dieta é obrigatório. O sistema deverá lançar uma
	 * exceção QUANTIDADE_DIETAS.
	 */
	@Test
	public void confirmarPrescricaoDietasObrigatorio() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		atendimento.setUnidadeFuncional(unidade);
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 1));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);

	

		this.esperarContarDiagnosticosAtendimento(1);

		this.esperarListagemDietas(new ArrayList<MpmPrescricaoDieta>());
		esperarAtendimentoNaoAmbulatorial();
		
		try {
			this.esperarQtdItensPrescricao(Arrays.asList(new ItemPrescricaoMedicaVO()));

			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert.fail("Deveria ter lançado exceção QUANTIDADE_DIETAS");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals(
					"Exceção lançada deveria ser a QUANTIDADE_DIETAS", e
							.getCode(),
					ConfirmarPrescricaoMedicaExceptionCode.QUANTIDADE_DIETAS);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica com várias dietas para
	 * unidades funcionais onde dieta NÃO é obrigatório. O sistema deve lançar a
	 * exceção QUANTIDADE_DIETAS_OPCIONAL
	 */
	@Test
	public void confirmarPrescricaoDietas() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		atendimento.setUnidadeFuncional(unidade);
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 1));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidade.setCaracteristicas(caracteristicas);

		

		this.esperarContarDiagnosticosAtendimento(1);

		this.esperarListagemDietas(Arrays.asList(new MpmPrescricaoDieta(),
				new MpmPrescricaoDieta()));
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarQtdItensPrescricao(Arrays.asList(new ItemPrescricaoMedicaVO()));
			
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert
					.fail("Deveria ter lançado exceção QUANTIDADE_DIETAS_OPCIONAL");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert
					.assertEquals(
							"Exceção lançada deveria ser a QUANTIDADE_DIETAS_OPCIONAL",
							e.getCode(),
							ConfirmarPrescricaoMedicaExceptionCode.QUANTIDADE_DIETAS);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica com uma prescrição de dieta
	 * com bomba de infusão quando a unidade não permite. O sistema deve lançar
	 * uma BOMBA_INFUSAO_NAO_PERMITIDA_DIETA
	 */
	@Test
	public void confirmarPrescricaoBombaInfusaoDieta() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		atendimento.setUnidadeFuncional(unidade);
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 1));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidade.setCaracteristicas(caracteristicas);

		

		this.esperarContarDiagnosticosAtendimento(1);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		dieta.setIndBombaInfusao(true);

		this.esperarListagemDietas(Arrays.asList(dieta));
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarQtdItensPrescricao(Arrays.asList(new ItemPrescricaoMedicaVO()));

			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert
					.fail("Deveria ter lançado exceção BOMBA_INFUSAO_NAO_PERMITIDA_DIETA");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert
					.assertEquals(
							"Exceção lançada deveria ser a BOMBA_INFUSAO_NAO_PERMITIDA_DIETA",
							e.getCode(),
							ConfirmarPrescricaoMedicaExceptionCode.BOMBA_INFUSAO_NAO_PERMITIDA_DIETA);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica com uma prescrição de
	 * medicamento com bomba de infusão quando a unidade não permite. O sistema
	 * deve lançar uma BOMBA_INFUSAO_NAO_PERMITIDA_MEDICAMENTO
	 */
	@Test
	public void confirmarPrescricaoBombaInfusaoMedicamento() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		atendimento.setUnidadeFuncional(unidade);
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 1));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidade.setCaracteristicas(caracteristicas);

		List<MpmCidAtendimento> listacidsAtendimento = new ArrayList<MpmCidAtendimento>();
		listacidsAtendimento.add(new MpmCidAtendimento());

		this.esperarContarDiagnosticosAtendimento(2);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		dieta.setIndBombaInfusao(false);

		this.esperarListagemDietas(Arrays.asList(dieta));

		MpmPrescricaoMdto medicamento = new MpmPrescricaoMdto();
		medicamento.setIndBombaInfusao(true);

		this.esperarListagemMedicamentos(Arrays.asList(medicamento));
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarQtdItensPrescricao(Arrays.asList(new ItemPrescricaoMedicaVO()));

			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert
					.fail("Deveria ter lançado exceção BOMBA_INFUSAO_NAO_PERMITIDA_MEDICAMENTO");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert
					.assertEquals(
							"Exceção lançada deveria ser a BOMBA_INFUSAO_NAO_PERMITIDA_MEDICAMENTO",
							e.getCode(),
							ConfirmarPrescricaoMedicaExceptionCode.BOMBA_INFUSAO_NAO_PERMITIDA_MEDICAMENTO);
		}

	}

	/**
	 * Testa a confirmação de uma prescrição médica com uma prescrição de npt
	 * com bomba de infusão quando a unidade não permite. O sistema deve lançar
	 * uma BOMBA_INFUSAO_NAO_PERMITIDA_NPT
	 */
	@Test
	public void confirmarPrescricaoBombaInfusaoNPT() {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId =  new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		atendimento.setUnidadeFuncional(unidade);
		internacao.setDtPrevAlta(DateUtil.adicionaDias(dataAtual, 1));

		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);
		prescricao.setDthrMovimento(dataAtual);
		prescricao.setSituacao(DominioSituacaoPrescricao.U);

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidade.setCaracteristicas(caracteristicas);


		this.esperarContarDiagnosticosAtendimento(1);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		dieta.setIndBombaInfusao(false);

		this.esperarListagemDietas(Arrays.asList(dieta));

		MpmPrescricaoMdto medicamento = new MpmPrescricaoMdto();
		medicamento.setIndBombaInfusao(false);

		this.esperarListagemMedicamentos(Arrays.asList(medicamento));

		MpmPrescricaoNpt npt = new MpmPrescricaoNpt();
		npt.setBombaInfusao(true);

		this.esperarListagemNPT(Arrays.asList(npt));
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarQtdItensPrescricao(Arrays.asList(new ItemPrescricaoMedicaVO()));

			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());
			Assert
					.fail("Deveria ter lançado exceção BOMBA_INFUSAO_NAO_PERMITIDA_NPT");

		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert
					.assertEquals(
							"Exceção lançada deveria ser a BOMBA_INFUSAO_NAO_PERMITIDA_NPT",
							e.getCode(),
							ConfirmarPrescricaoMedicaExceptionCode.BOMBA_INFUSAO_NAO_PERMITIDA_NPT);
		}

	}

	/**
	 * Testa a atualizacao de atendimento paciente cpa
	 */
	@Test
	public void atualizarAtendimentoPacienteCpa() {
		
		ArrayList<MpmPrescricaoCuidado> listaPrescricaoCuidado = new ArrayList<MpmPrescricaoCuidado>();

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();

		MpmPrescricaoMedica prescricaoMedicaPrescricao = new MpmPrescricaoMedica();

		AghAtendimentos atendimentoprescricao = new AghAtendimentos();
		atendimentoprescricao.setIndPacAtendimento(DominioPacAtendimento.S);

		prescricaoMedicaPrescricao.setAtendimento(atendimentoprescricao);
		cuidado.setPrescricaoMedica(prescricaoMedicaPrescricao);

		listaPrescricaoCuidado.add(cuidado);

		this
				.esperarListagemPrescricaoCuidadoPendenteCpa(listaPrescricaoCuidado);

		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.S);
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		prescricaoMedica.setAtendimento(atendimento);
	
		try {
			systemUnderTest.atualizarAtendimentoPacienteCpa(prescricaoMedica, NOME_MICROCOMPUTADOR, new Date());
		} catch(Exception e) {
			assert(false);
		}
		
		Assert.assertEquals(cuidado.getPrescricaoMedica().getAtendimento()
				.getIndPacCpa(), true);
	}

	/**
	 * 
	 */
	@Test
	public void listarProcedimentosGeracaoLaudosTest() {

		List<MpmPrescricaoProcedimento> lista = new ArrayList<MpmPrescricaoProcedimento>();

		MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();

		lista.add(prescricaoProcedimento);

		this.esperarListarProcedimentosGeracaoLaudos(lista);

		List<FatProcedHospInternos> listaProcedimentosHospitalaresInternos = new ArrayList<FatProcedHospInternos>();
		FatProcedHospInternos procedimentoInterno = new FatProcedHospInternos();
		listaProcedimentosHospitalaresInternos.add(procedimentoInterno);

		this
				.esperarListarFatProcedHospInternosPorMaterial(listaProcedimentosHospitalaresInternos);

	}
	
	private void esperarAtendimentoNaoAmbulatorial() {
		Mockito.when(mockedAghuFacade.obterAtendimento(Mockito.anyInt(), Mockito.any(DominioPacAtendimento.class), Mockito.anyList())).
		thenReturn(null);
	}

}
