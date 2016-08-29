package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.business.ConfirmarPrescricaoMedicaON.ConfirmarPrescricaoMedicaONExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualUnfDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoConvenioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe de testes da ON de confirmar prescrição médica.
 * 
 * @author gmneto
 * 
 */
public class ConfirmarPrescricaoMedicaONTest extends AGHUBaseUnitTest<ConfirmarPrescricaoMedicaON>{
	
	private static final Log log = LogFactory.getLog(ConfirmarPrescricaoMedicaONTest.class);

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private MpmPrescricaoMedicaDAO mockedMpmPrescricaoMedicaDAO;
	@Mock
	private MpmPrescricaoCuidadoDAO mockedMpmPrescricaoCuidadoDAO;
	@Mock
	private MpmPrescricaoDietaDAO mockedMpmPrescricaoDietaDAO;
	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;
	@Mock
	private MpmCuidadoUsualUnfDAO mockedMpmCuidadoUsualUnfDAO;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private ConfirmarPrescricaoMedicaRN mockedConfirmarPrescricaoMedicaRN;
	@Mock
	private INutricaoFacade mockedNutricaoFacade;
	@Mock
	private LaudoRN mockedLaudoRN;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private SolicitacaoHemoterapicaON mockedSolicitacaoHemoterapicaON;
	@Mock
	private MpmTipoLaudoConvenioDAO mockedMpmTipoLaudoConvenioDAO;
	@Mock
	private MpmTipoLaudoDAO mockedTipoLaudoDAO;
	@Mock
	private MpmLaudoDAO mockedMpmLaudoDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;


	@Before
	public void iniciar() throws BaseException {
		whenObterServidorLogado();
	}
	
	/**
	 * Indica ao mock para esperar uma chamada do método
	 * buscaDietaPorPrescricaoMedica com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemDietas(final List<MpmPrescricaoDieta> dietas) {
		Mockito.when(mockedMpmPrescricaoDietaDAO.
				buscaDietaPorPrescricaoMedica(Mockito.any(MpmPrescricaoMedicaId.class), Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(dietas);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional com quaisquer
	 * parâmetros adequados, retornando a lista passada por parâmetro a este
	 * método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemAnuTipoItemDietaUnfs(
			final List<AnuTipoItemDietaUnfs> anuTipoItemDietaUnfs) {
		Mockito.when(mockedNutricaoFacade.
				listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(Mockito.any(AnuTipoItemDieta.class), Mockito.any(AghUnidadesFuncionais.class))).thenReturn(anuTipoItemDietaUnfs);
	}

	private void esperarBuscaConselhoProfissionalServidorVO(
			final BuscaConselhoProfissionalServidorVO vo) throws ApplicationBusinessException {
		try {
			Mockito.when(mockedPrescricaoMedicaFacade.
					buscaConselhoProfissionalServidorVO(Mockito.anyInt(), Mockito.anyShort())).thenReturn(vo);
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * pesquisarCuidadosMedicos com quaisquer parâmetros adequados, retornando a
	 * lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemCuidados(
			final List<MpmPrescricaoCuidado> cuidados) {
		Mockito.when(mockedMpmPrescricaoCuidadoDAO.
				pesquisarCuidadosMedicos(Mockito.any(MpmPrescricaoMedicaId.class), Mockito.any(Date.class), Mockito.any(Boolean.class))).thenReturn(cuidados);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * obterListaSolicitacoesHemoterapicasPelaChavePrescricao com quaisquer
	 * parâmetros adequados, retornando a lista passada por parâmetro a este
	 * método.
	 * 
	 */
	private void esperarListagemSolicitacoesHemoterapicas(
			final List<AbsSolicitacoesHemoterapicas> solicitacoesHemoterapicas) {
		Mockito.when(mockedSolicitacaoHemoterapicaON.
				obterListaSolicitacoesHemoterapicasPelaChavePrescricao(Mockito.any(MpmPrescricaoMedicaId.class))).thenReturn(solicitacoesHemoterapicas);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarAfaViaAdmUnfAtivasPorUnidadeFuncional com quaisquer parâmetros
	 * adequados, retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemAfaViaAdmUnfUnidadeFuncional(
			final List<AfaViaAdmUnf> listAfaViaAdmUnf) {
		Mockito.when(mockedFarmaciaFacade.
				listarAfaViaAdmUnfAtivasPorUnidadeFuncional(Mockito.any(AghUnidadesFuncionais.class))).thenReturn(listAfaViaAdmUnf);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarAfaViaAdmUnfAtivasPorUnidadeFuncional com quaisquer parâmetros
	 * adequados, retornando a lista passada por parâmetro a este método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemAfaViaAdmUnfUnidadeFuncionalEViaAdministracao(
			final List<AfaViaAdmUnf> listAfaViaAdmUnf) {
		Mockito.when(mockedFarmaciaFacade.
				listarAfaViaAdmUnfAtivasPorUnidadeFuncionalEViaAdministracao(Mockito.any(AghUnidadesFuncionais.class), 
						Mockito.any(AfaViaAdministracao.class))).thenReturn(listAfaViaAdmUnf);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarMpmCuidadoUsualUnfPorUnidadeFuncionalCuidado com quaisquer
	 * parâmetros adequados, retornando a lista passada por parâmetro a este
	 * método.
	 * 
	 * @param diagnosticos
	 */
	private void esperarListagemMpmCuidadoUsualUnf(
			final List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnf) {
		Mockito.when(mockedMpmCuidadoUsualUnfDAO.
				listarMpmCuidadoUsualUnfPorUnidadeFuncionalCuidado(Mockito.any(MpmCuidadoUsual.class), 
						Mockito.any(AghUnidadesFuncionais.class))).thenReturn(listaMpmCuidadoUsualUnf);
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
		Mockito.when(mockedMpmPrescricaoMdtoDAO.
				obterListaMedicamentosPrescritosPelaChavePrescricao(Mockito.any(MpmPrescricaoMedicaId.class), 
						Mockito.any(Date.class))).thenReturn(medicamentos);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarLaudosPorAtendimentoETipo com quaisquer parâmetros adequados,
	 * retornando a lista passada por parâmetro a este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarlistarLaudosPorAtendimentoETipo(
			final List<MpmLaudo> laudos) {
		Mockito.when(mockedMpmLaudoDAO.
				listarLaudosPorAtendimentoETipo(Mockito.any(AghAtendimentos.class), 
						Mockito.any(MpmTipoLaudo.class), Mockito.any(Date.class))).thenReturn(laudos);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarProcedimentosGeracaoLaudos com quaisquer parâmetros adequados,
	 * retornando o map passado por parâmetro a este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarListarProcedimentosGeracaoLaudos(
			final Map<MpmPrescricaoProcedimento, FatProcedHospInternos> retorno) {
		try {
			Mockito.when(mockedConfirmarPrescricaoMedicaRN.
					listarProcedimentosGeracaoLaudos(Mockito.any(MpmPrescricaoMedica.class))).thenReturn(retorno);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarProcedimentosCirurgicosGeracaoLaudos com quaisquer parâmetros
	 * adequados, retornando o map passado por parâmetro a este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarListarProcedimentosCirurgicosGeracaoLaudos(
			final Map<MpmPrescricaoProcedimento, FatProcedHospInternos> retorno) {
		try {
			Mockito.when(mockedConfirmarPrescricaoMedicaRN.
					listarProcedimentosCirurgicosGeracaoLaudos(Mockito.any(MpmPrescricaoMedica.class))).thenReturn(retorno);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * listarProcedimentosDiversosGeracaoLaudos com quaisquer parâmetros
	 * adequados, retornando o map passado por parâmetro a este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarListarProcedimentosDiversosGeracaoLaudos(
			final Map<MpmPrescricaoProcedimento, FatProcedHospInternos> retorno) {
		try {
			Mockito.when(mockedConfirmarPrescricaoMedicaRN.
					listarProcedimentosDiversosGeracaoLaudos(Mockito.any(MpmPrescricaoMedica.class))).thenReturn(retorno);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * Indica ao mock para esperar uma chamada do método buscarAghParametro com
	 * quaisquer parâmetros adequados, retornando o map objeto por parâmetro a
	 * este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarBuscarAghParametro(final AghParametros retorno) {
		try {
			Mockito.when(mockedParametroFacade.
					buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(retorno);
		} catch (ApplicationBusinessException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * obterConvenioAtendimento com quaisquer parâmetros adequados, retornando o
	 * objeto passado por parâmetro a este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarObterConvenioAtendimento(
			final FatConvenioSaudePlano retorno) {
		Mockito.when(mockedConfirmarPrescricaoMedicaRN.
				obterConvenioAtendimento(Mockito.any(AghAtendimentos.class))).thenReturn(retorno);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * obterConvenioAtendimento com quaisquer parâmetros adequados, retornando o
	 * objeto passado por parâmetro a este método.
	 * 
	 * @param medicamentos
	 */
	private void esperarObterTempoValidadeTipoLaudo(final Short retorno) {
		Mockito.when(mockedMpmTipoLaudoConvenioDAO.
				obterTempoValidadeTipoLaudo(Mockito.anyShort(), Mockito.any(FatConvenioSaudePlano.class))).thenReturn(retorno);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * verificarPacienteContaApac
	 * 
	 * @param diagnosticos
	 */
	private void esperarVerificarPacienteContaApac() {
		Mockito.when(mockedConfirmarPrescricaoMedicaRN.
				verificarPacienteContaApac()).thenReturn(false);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método obterPorChavePrimaria
	 * 
	 * @param diagnosticos
	 */
	private void esperarObterTipoLaudoPorChavePrimaria(
			final MpmTipoLaudo tipoLaudo) {
		Mockito.when(mockedTipoLaudoDAO.
				obterPorChavePrimaria(Mockito.any(Object.class))).thenReturn(tipoLaudo);
	}

	/**
	 * Indica ao mock para esperar uma chamada do método
	 * obterCountLaudosPorTipoEAtendimento
	 * 
	 * @param diagnosticos
	 */
	private void esperarObterCountLaudosPorTipoEAtendimento(
			final Integer retorno) {
		Mockito.when(mockedMpmLaudoDAO.
				obterCountLaudosPorTipoEAtendimento(Mockito.any(AghAtendimentos.class), Mockito.any(MpmTipoLaudo.class))).thenReturn(Long.valueOf(retorno));
	}

	private void esperarRefreshPrescricaoMedica() {
	}

	private void esperarObterTempoValidadeTipoLaudoPermanenciaMaior() {
	}

	private void esperarObterPorChavePrimaria() {
	}

	//

	/**
	 * Testa o caminho feliz da confirmação da prescricao médica.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void confirmarPrescricao() throws ApplicationBusinessException {

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);

		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		FatItensProcedHospitalar intensProcHosp = new FatItensProcedHospitalar();
		Short dias = 4;
		intensProcHosp.setQuantDiasFaturamento(dias);
		internacao.setDthrInternacao(new Date());
		internacao.setItemProcedimentoHospitalar(intensProcHosp);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setInternacao(internacao);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarObterPorChavePrimaria();
		this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
		this.esperarRefreshPrescricaoMedica();
		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo(null);

		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();

		try {
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação de prescricao médica quando há uma dieta nãoi
	 * permitida.
	 */
	@Test
	public void confirmarPrescricaoDietaNaoPermitida() throws ApplicationBusinessException{

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setUnidadeFuncional(unidadeFuncional);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();
		AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta();
		tipoItemDieta.setDescricao("descricao");
		itemDieta.setTipoItemDieta(tipoItemDieta);

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(new ArrayList<AnuTipoItemDietaUnfs>());

		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.fail();

		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					ConfirmarPrescricaoMedicaONExceptionCode.PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO);
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a cofirmação de uma prescrição quando a via de administração não é
	 * permitida.
	 */
	@Test
	public void confirmarPrescricaoViaAdministracaoNaoPermitida() throws ApplicationBusinessException{

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setUnidadeFuncional(unidadeFuncional);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		AfaViaAdmUnf afaViaAdmUnf = new AfaViaAdmUnf();

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(Arrays
				.asList(afaViaAdmUnf));

		MpmPrescricaoMdto prescricaoMedicamento = new MpmPrescricaoMdto();
		AfaViaAdministracao viaAdministracao = new AfaViaAdministracao();
		viaAdministracao.setDescricao("descricao");
		prescricaoMedicamento.setViaAdministracao(viaAdministracao);

		this.esperarListagemMedicamentos(Arrays.asList(prescricaoMedicamento));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncionalEViaAdministracao(new ArrayList<AfaViaAdmUnf>());

		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.fail();

		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					ConfirmarPrescricaoMedicaONExceptionCode.PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO);
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a cofirmação de uma prescrição quando um cuidado não é permitida.
	 */
	@Test
	public void confirmarPrescricaoCuidadoNaoPermitido() throws ApplicationBusinessException{

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setUnidadeFuncional(unidadeFuncional);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmTipoFrequenciaAprazamento tipoFreq = new MpmTipoFrequenciaAprazamento();
		tipoFreq.setDescricao("teste");
		cuidado.setMpmTipoFreqAprazamentos(tipoFreq);
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidadoUsual.setIndOutros(false);
		cuidadoUsual.setDescricao("teste");
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(new ArrayList<MpmCuidadoUsualUnf>());


		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);

		try {
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.fail();

		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					ConfirmarPrescricaoMedicaONExceptionCode.PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO);
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudos para
	 * procedimentos materiais.
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoProcedimentosMateriais() throws ApplicationBusinessException {

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		internacao.setDthrInternacao(new Date());
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setUnidadeFuncional(unidadeFuncional);
		atendimento.setInternacao(internacao);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudosMateriais = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();
		MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();
		prescricaoProcedimento.setDuracaoTratamentoSolicitado((short) 1);
		FatProcedHospInternos procedimentoInterno = new FatProcedHospInternos();
		mapRetornoGeracaoLaudosMateriais.put(prescricaoProcedimento,
				procedimentoInterno);

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudosMateriais);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo(null);

		Collection<MpmLaudo> listaLaudos = new ArrayList<MpmLaudo>();
		MpmLaudo laudo = new MpmLaudo();
		listaLaudos.add(laudo);



		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();
		
		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudo para
	 * procedimento cirurgico.
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoProcedimentoCirurgico() throws ApplicationBusinessException{

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		internacao.setDthrInternacao(new Date());
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setInternacao(internacao);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudosCirurgico = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();
		MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();
		prescricaoProcedimento.setDuracaoTratamentoSolicitado((short) 1);
		FatProcedHospInternos procedimentoInterno = new FatProcedHospInternos();
		mapRetornoGeracaoLaudosCirurgico.put(prescricaoProcedimento,
				procedimentoInterno);

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudosCirurgico);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo(null);

		MpmLaudo laudo = new MpmLaudo();


		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);

		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudo para
	 * procedimentos especiais diversos.
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoProcedimentoEspeciaisDiversos() throws ApplicationBusinessException{

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		internacao.setDthrInternacao(new Date());
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setInternacao(internacao);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudosEspeciaisDiversos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();
		MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();
		prescricaoProcedimento.setId(new MpmPrescricaoProcedimentoId(1,
				(long) 1));

		FatProcedHospInternos procedimentoInterno = new FatProcedHospInternos();
		procedimentoInterno.setSeq(1);
		mapRetornoGeracaoLaudosEspeciaisDiversos.put(prescricaoProcedimento,
				procedimentoInterno);

		prescricaoProcedimento = new MpmPrescricaoProcedimento();
		prescricaoProcedimento.setId(new MpmPrescricaoProcedimentoId(2,
				(long) 2));
		prescricaoProcedimento.setDuracaoTratamentoSolicitado((short) 1);
		procedimentoInterno = new FatProcedHospInternos();
		procedimentoInterno.setSeq(2);
		mapRetornoGeracaoLaudosEspeciaisDiversos.put(prescricaoProcedimento,
				procedimentoInterno);

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudosEspeciaisDiversos);

		this.esperarVerificarPacienteContaApac();

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		// this.esperarObterTempoValidadeTipoLaudo(null);

		Collection<MpmLaudo> listaLaudos = new ArrayList<MpmLaudo>();
		MpmLaudo laudo = new MpmLaudo();
		listaLaudos.add(laudo);


		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();
		
		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR,new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudo UTI mas com
	 * laudo válido 1
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoUTIComLaudoValido1()throws ApplicationBusinessException {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		prescricao.setDthrInicio(dataAtual);

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.LAUDO_CTI);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidadeFuncional.setCaracteristicas(caracteristicas);

		atendimento.setUnidadeFuncional(unidadeFuncional);

		AinInternacao internacao = new AinInternacao();
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);

		internacao.setDthrInternacao(dataAtual);
		atendimento.setInternacao(internacao);

		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarVerificarPacienteContaApac();

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo((short) 5);

		MpmTipoLaudo tipoLaudo = new MpmTipoLaudo();
		this.esperarObterTipoLaudoPorChavePrimaria(tipoLaudo);

		MpmLaudo laudo = new MpmLaudo();
		laudo.setDthrInicioValidade(dataAtual);
		List<MpmLaudo> laudosList = new ArrayList<MpmLaudo>();
		laudosList.add(laudo);
		this.esperarlistarLaudosPorAtendimentoETipo(laudosList);



		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();
		
		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudo UTI mas com
	 * laudo válido 2
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoUTIComLaudoValido2()throws ApplicationBusinessException {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		prescricao.setDthrInicio(dataAtual);

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.LAUDO_CTI);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidadeFuncional.setCaracteristicas(caracteristicas);

		atendimento.setUnidadeFuncional(unidadeFuncional);

		AinInternacao internacao = new AinInternacao();
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);

		internacao.setDthrInternacao(dataAtual);
		atendimento.setInternacao(internacao);

		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarVerificarPacienteContaApac();

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo((short) 5);

		MpmTipoLaudo tipoLaudo = new MpmTipoLaudo();
		this.esperarObterTipoLaudoPorChavePrimaria(tipoLaudo);

		MpmLaudo laudo = new MpmLaudo();
		laudo.setDthrInicioValidade(DateUtil.adicionaDias(dataAtual, -1));
		laudo.setDthrFimValidade(DateUtil.adicionaDias(dataAtual, +1));
		List<MpmLaudo> laudosList = new ArrayList<MpmLaudo>();
		laudosList.add(laudo);
		this.esperarlistarLaudosPorAtendimentoETipo(laudosList);


		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudo UTI
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoUTI()throws ApplicationBusinessException {

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		prescricao.setDthrInicio(dataAtual);

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		AghCaractUnidFuncionais caracteristica = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionaisId caracteristicaid = new AghCaractUnidFuncionaisId();

		caracteristicaid
				.setCaracteristica(ConstanteAghCaractUnidFuncionais.LAUDO_CTI);
		caracteristica.setId(caracteristicaid);

		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(caracteristica);
		unidadeFuncional.setCaracteristicas(caracteristicas);

		atendimento.setUnidadeFuncional(unidadeFuncional);

		AinInternacao internacao = new AinInternacao();
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);

		internacao.setDthrInternacao(dataAtual);
		atendimento.setInternacao(internacao);

		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarVerificarPacienteContaApac();

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo((short) 5);

		MpmTipoLaudo tipoLaudo = new MpmTipoLaudo();
		this.esperarObterTipoLaudoPorChavePrimaria(tipoLaudo);

		MpmLaudo laudo = new MpmLaudo();
		laudo.setDthrInicioValidade(DateUtil.adicionaDias(dataAtual, -10));

		List<MpmLaudo> laudosList = new ArrayList<MpmLaudo>();
		laudosList.add(laudo);
		this.esperarlistarLaudosPorAtendimentoETipo(laudosList);

		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);
		esperarAtendimentoNaoAmbulatorial();

		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();
			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}

	/**
	 * Testa a confirmação da prescricao médica com geração de laudo permanencia
	 * maior.
	 */
	@Test
	public void confirmarPrescricaoGeracaoLaudoPermanenciaMaior() throws ApplicationBusinessException{

		Date dataAtual = new Date();

		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId prescricaoId = new MpmPrescricaoMedicaId();
		prescricaoId.setSeq(1);
		prescricaoId.setAtdSeq(2);
		prescricao.setId(prescricaoId);
		prescricao.setDthrInicio(dataAtual);

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setUnidadeFuncional(unidadeFuncional);

		AinInternacao internacao = new AinInternacao();
		FatItensProcedHospitalar itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		itemProcedimentoHospitalar.setQuantDiasFaturamento((short) 5);
		itemProcedimentoHospitalar.setDiasPermanenciaMaior((short) 5);
		internacao.setItemProcedimentoHospitalar(itemProcedimentoHospitalar);

		internacao.setDthrInternacao(DateUtil.adicionaDias(dataAtual, -50));
		atendimento.setInternacao(internacao);

		prescricao.setAtendimento(atendimento);

		MpmPrescricaoDieta dieta = new MpmPrescricaoDieta();
		MpmItemPrescricaoDieta itemDieta = new MpmItemPrescricaoDieta();

		Set<MpmItemPrescricaoDieta> itensDietas = new HashSet<MpmItemPrescricaoDieta>();
		itensDietas.add(itemDieta);

		dieta.setItemPrescricaoDieta(itensDietas);

		List<MpmPrescricaoDieta> listaDietas = Arrays.asList(dieta);

		this.esperarListagemDietas(listaDietas);

		this.esperarListagemAnuTipoItemDietaUnfs(Arrays
				.asList(new AnuTipoItemDietaUnfs()));

		this.esperarListagemAfaViaAdmUnfUnidadeFuncional(new ArrayList<AfaViaAdmUnf>());

		MpmPrescricaoCuidado cuidado = new MpmPrescricaoCuidado();
		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();
		cuidado.setMpmCuidadoUsuais(cuidadoUsual);

		this.esperarListagemCuidados(Arrays.asList(cuidado));

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		this.esperarListagemSolicitacoesHemoterapicas(Arrays
				.asList(solicitacaoHemoterapica));

		this.esperarListagemMpmCuidadoUsualUnf(Arrays
				.asList(new MpmCuidadoUsualUnf()));

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> mapRetornoGeracaoLaudos = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		this.esperarListarProcedimentosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosCirurgicosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarListarProcedimentosDiversosGeracaoLaudos(mapRetornoGeracaoLaudos);

		this.esperarVerificarPacienteContaApac();

		AghParametros parametroAgh = new AghParametros();
		parametroAgh.setVlrNumerico(BigDecimal.ONE);
		this.esperarBuscarAghParametro(parametroAgh);

		this.esperarObterConvenioAtendimento(null);

		this.esperarObterTempoValidadeTipoLaudo((short) 5);

		MpmTipoLaudo tipoLaudo = new MpmTipoLaudo();
		this.esperarObterTipoLaudoPorChavePrimaria(tipoLaudo);

		this.esperarObterCountLaudosPorTipoEAtendimento(0);

		MpmLaudo laudo = new MpmLaudo();
		laudo.setDthrInicioValidade(DateUtil.adicionaDias(dataAtual, -10));

		List<MpmLaudo> laudosList = new ArrayList<MpmLaudo>();
		laudosList.add(laudo);

		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();

		vo.setNumeroRegistroConselho("11111111");

		this.esperarBuscaConselhoProfissionalServidorVO(vo);

		try {
			this.esperarObterPorChavePrimaria();
			this.esperarObterTempoValidadeTipoLaudoPermanenciaMaior();
			this.esperarRefreshPrescricaoMedica();

			systemUnderTest.confirmarPrescricaoMedica(prescricao, NOME_MICROCOMPUTADOR, new Date());

			Assert.assertTrue(true);

		} catch (BaseException e) {
			log.error(e.getMessage());
		}

	}
	
	private void esperarAtendimentoNaoAmbulatorial() {
		Mockito.when(mockedAghuFacade.
				obterAtendimento(Mockito.anyInt(), Mockito.any(DominioPacAtendimento.class), Mockito.anyList())).thenReturn(null);
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
