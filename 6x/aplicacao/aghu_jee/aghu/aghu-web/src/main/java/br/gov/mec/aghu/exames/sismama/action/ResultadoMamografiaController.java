package br.gov.mec.aghu.exames.sismama.action;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioLinfonodosAxilaresMamografia;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.sismama.action.ResultadoMamografiaController.MamografiaControllerException;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ResultadoMamografiaController extends ActionController {

	private static final String C_RAD_NUM_FILM_D = "C_RAD_NUM_FILM_D";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ResultadoMamografiaController.class);

	private static final long serialVersionUID = 2101511488024586214L;

	private static final String PAGE_EXAMES_RESULTADO_MAMOGRAFIA_LIST = "exames-resultadoMamografiaList";
	private static final String PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD = "exames-resultadoMamografiaCRUD";
	// private static final String PAGE_EXAMES_RESULTADO_NOTA_ADICIONAL = "exames-resultadoNotaAdicional";

	private AipPacientes paciente;
	private String prontuario;
	private String nomePaciente;
	private Integer solicitacao;
	private Short item;
	private String descricaoUsualExame;
	private String informacaoClinica;

	// k_variaveis
	private String situacaoLiberado; // v_situacao_liberado
	private String situacaoAreaExecutora; // v_situacao_na_area_executora
	private String situacaoExecutando; // v_situacao_executando
	private String rxMamaBilateral; // v_rx_mama_bilateral
	private Short iseSeqpLida; // v_ise_seqp_lida
	private String sexoPaciente; // v_sexo_paciente

	private Boolean reabrirLaudo;
	private Boolean apresentarModalReabrirAssinarLaudo;
	private String msgModalReabrirLaudo;

	private Map<String, Boolean> mapControleTela;

	private Integer abaSelecionada;

	private Boolean habilitarMamaDireita;
	private Boolean habilitarMamaEsquerda;

	private boolean exibirMensagemReabrirLaudo;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject
	private ResultadoMamografiaPaginatorController resultadoMamografiaPaginatorController;

	@Inject
	private ConsultarResultadosNotaAdicionalController resultadosNotaAdicionalController;

	@Inject
	private ResultadoMamografiaDireitaController resultadoMamografiaDireitaController;

	@Inject
	private ResultadoMamografiaEsquerdaController resultadoMamografiaEsquerdaController;

	@Inject
	private ResultadoMamografiaConclusaoController resultadoMamografiaConclusaoController;

	private Map<String, AelSismamaMamoResVO> mapMamaD;
	private Map<String, AelSismamaMamoResVO> mapMamaE;
	private Map<String, AelSismamaMamoResVO> mapAbaConclusao;

	// private Boolean directPrint = false;
	private Boolean modalConfirmaImpressao = false;

	private Integer vTipoVel;

	private boolean carregouPagina;

	public enum MamografiaControllerException implements BusinessExceptionCode {
		AEL_03243;
	}

	public void iniciar() throws ApplicationBusinessException {
	 


		this.apresentarMensagemLaudoReaberto();

		inicializarDadosItemExames();

		habilitarMamaDireita = false;
		habilitarMamaEsquerda = false;

		// inicializar MAP com atributos da tela

		mapMamaD = examesLaudosFacade.inicializarMapDireita(); // inicializarMapDireita();
		mapMamaE = examesLaudosFacade.inicializarMapEsquerda(); // inicializarMapEsquerda();
		mapAbaConclusao = examesLaudosFacade.inicializarMapConclusao(); // inicializarMapConclusao();

		resultadoMamografiaDireitaController.pesquisarParametrosDireita();
		resultadoMamografiaEsquerdaController.pesquisarParametrosEsquerda();

		pesquisarParametros();
		configurarTela();

		this.carregouPagina = true;
	
	}

	public String imprimirLaudo() {

		// variaveis utilizadas na integração. Cópia do arquivo: consultarResultadosNotaAdicionalController
		AelItemSolicitacaoExames ise = examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(new AelItemSolicitacaoExamesId(getSolicitacao(), getItem()));
		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		Vector<Short> solSeqs = new Vector<Short>();
		solSeqs.add(ise.getId().getSeqp());
		solicitacoes.put(ise.getId().getSoeSeq(), solSeqs);

		// directPrint = false;
		modalConfirmaImpressao = false;

		if (vTipoVel == 2) {
			resultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);
			resultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
			// return ResultadoMamografiaControllerTarget.LAUDO.toString();
		} else { // igual 3
			resultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);
			resultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
			// return ResultadoMamografiaControllerTarget.LAUDO.toString();
		}

		resultadosNotaAdicionalController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_SISMAMA);
		resultadosNotaAdicionalController.directPrint(null);

		return null;
	}

	public void configurarTela() {
		montarControleAbas();
		montarControleTela();

		carregarDados();

		resultadoMamografiaDireitaController.montaLinhasNodulos(mapMamaD);
		resultadoMamografiaDireitaController.montaLinhasMicrocalcificacao(mapMamaD);
		resultadoMamografiaDireitaController.montaLinhasAssimetriaFocal(mapMamaD);
		resultadoMamografiaDireitaController.montaLinhasAssimetriaDifusa(mapMamaD);

		resultadoMamografiaEsquerdaController.montaLinhasNodulos(mapMamaE);
		resultadoMamografiaEsquerdaController.montaLinhasNodulos(mapMamaE);
		resultadoMamografiaEsquerdaController.montaLinhasMicrocalcificacao(mapMamaE);
		resultadoMamografiaEsquerdaController.montaLinhasAssimetriaFocal(mapMamaE);
		resultadoMamografiaEsquerdaController.montaLinhasAssimetriaDifusa(mapMamaE);
	}

	public void montarControleAbas() {
		setIseSeqpLida(null);

		Map<String, Object> mapControleAbas = examesLaudosFacade.montarControleAbas(getIseSeqpLida(), getSolicitacao(), getItem(), resultadoMamografiaEsquerdaController.getRxMamaEsquerda(),
				resultadoMamografiaDireitaController.getRxMamaDireita());
		String habilitaAbaDir = (String) mapControleAbas.get(DominioSismamaMamoCadCodigo.HABILITA_MAMA_D.toString());
		String habilitaAbaEsq = (String) mapControleAbas.get(DominioSismamaMamoCadCodigo.HABILITA_MAMA_E.toString());

		resultadoMamografiaDireitaController.setHabilitaMamaDireita(habilitaAbaDir);
		resultadoMamografiaEsquerdaController.setHabilitaMamaEsquerda(habilitaAbaEsq);
		setIseSeqpLida((Short) mapControleAbas.get(DominioSismamaMamoCadCodigo.ISE_SEQP_LIDA.toString()));
	}

	public void montarControleTela() {
		mapControleTela = new HashMap<String, Boolean>();

		setMapControleTela(examesLaudosFacade.montarControleTela(getSolicitacao(), getItem(), getSituacaoLiberado(), getSituacaoAreaExecutora(), getSituacaoExecutando(),
				resultadoMamografiaDireitaController.getHabilitaMamaDireita(), resultadoMamografiaEsquerdaController.getHabilitaMamaEsquerda()));

		setAbaSelecionada(2);

		if (mapControleTela.get(DominioSismamaMamoCadCodigo.HABILITAR_ABA_2.toString())) {
			setAbaSelecionada(1);
		}

		if (mapControleTela.get(DominioSismamaMamoCadCodigo.HABILITAR_ABA_1.toString())) {
			setAbaSelecionada(0);
		}

		// RN2
		resultadoMamografiaDireitaController.setvNaoInformadoDOk(false);
		resultadoMamografiaEsquerdaController.setvNaoInformadoEOk(false);
	}

	public void carregarDados() {
		try {
			setInformacaoClinica(examesLaudosFacade.obterDadosInformacaoClinica(getSolicitacao(), getItem()));
			examesLaudosFacade.obterInformacoesMama(getSolicitacao(), getItem(), resultadoMamografiaDireitaController.getHabilitaMamaDireita(), mapMamaD,
					resultadoMamografiaEsquerdaController.getHabilitaMamaEsquerda(), mapMamaE, mapAbaConclusao);

			resultadoMamografiaConclusaoController.setResidenteConectado(examesLaudosFacade.verificarResidenteConectadoResultadoMamografia());

			VRapPessoaServidor vRapPessoaServidor = examesLaudosFacade.obterResidenteResultadoMamografia(getSolicitacao(), getItem());

			resultadoMamografiaConclusaoController.setResidente(null);
			if (vRapPessoaServidor != null && vRapPessoaServidor.getId().getSerMatricula() != null && vRapPessoaServidor.getId().getSerVinCodigo() != null) {
				RapServidores servidor = registroColaboradorFacade.obterRapServidorPorVinculoMatricula(vRapPessoaServidor.getId().getSerMatricula(), vRapPessoaServidor.getId().getSerVinCodigo());
				resultadoMamografiaConclusaoController.setResidente(servidor);
			}

			resultadoMamografiaConclusaoController.setResponsavel(examesLaudosFacade.obterResponsavelResultadoMamografia(getSolicitacao(), getItem(),
					resultadoMamografiaDireitaController.getHabilitaMamaDireita(), mapAbaConclusao));

		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	private void inicializarDadosItemExames() {

		this.solicitacao = this.resultadoMamografiaPaginatorController.getSoeSeq();
		this.item = this.resultadoMamografiaPaginatorController.getSeqp();

		AelItemSolicitacaoExames ise = examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(new AelItemSolicitacaoExamesId(getSolicitacao(), getItem()), new Enum[]{AelItemSolicitacaoExames.Fields.AEL_EXAMES}, null);
		if (ise != null) {
			setNomePaciente(examesFacade.buscarLaudoNomePaciente(ise.getSolicitacaoExame()));
			setProntuario(examesFacade.buscarLaudoProntuarioPaciente(ise.getSolicitacaoExame()));
			setInformacaoClinica(examesLaudosFacade.obterRespostaMamo(ise));
			setDescricaoUsualExame(ise.getExame().getDescricaoUsual());

			// Busca dados do paciente
			if (!StringUtils.isEmpty(getProntuario())) {
				paciente = pacienteFacade.obterPacientePorProntuario(Integer.valueOf(getProntuario().replace("/", "")));
			}
		}
	}

	private boolean validarCampoNumFilme(AelItemSolicitacaoExames ise) {
		// p_trata_mamografia_bilateral

		boolean gravar = true;

		if (ise != null && ise.getAelUnfExecutaExames() != null && ise.getAelUnfExecutaExames().getId() != null && getRxMamaBilateral().equals(ise.getAelUnfExecutaExames().getId().getEmaExaSigla())) {

			if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getNumeroFilmes() == null && !resultadoMamografiaDireitaController.isvNaoInformadoDOk()) {
				resultadoMamografiaDireitaController.setvNaoInformadoDOk(false);
				this.apresentarMsgNegocio(Severity.INFO, "AEL_03347");
				gravar = false;

			}
			if (mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()).getNumeroFilmes() == null && !resultadoMamografiaEsquerdaController.isvNaoInformadoEOk()) {
				resultadoMamografiaEsquerdaController.setvNaoInformadoEOk(false);
				this.apresentarMsgNegocio(Severity.INFO, "AEL_03348");
				gravar = false;
			}
		}

		return gravar;
	}

	public void gravar() {

		try {
			AelItemSolicitacaoExames ise = examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(new AelItemSolicitacaoExamesId(getSolicitacao(), getItem()));

			setarResonsavelResidente();

			Map<String, AelSismamaMamoResVO> mapa = new HashMap<String, AelSismamaMamoResVO>();
			mapa.putAll(mapMamaD);
			mapa.putAll(mapMamaE);
			mapa.putAll(mapAbaConclusao);
			// p_trata_mamografia_bilateral
			if (validarCampoNumFilme(ise) && validarResponsavel()) {
				try {

					if (paciente != null) {
						sexoPaciente = paciente.getSexo().toString();
					}
					examesLaudosFacade.salvarDados(ise, mapMamaD, mapMamaE, mapAbaConclusao, rxMamaBilateral, sexoPaciente, mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString())
							.getNumeroFilmes() != null, mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getNumeroFilmes() != null, resultadoMamografiaConclusaoController
							.getResponsavel().getId().getNome(), informacaoClinica);

					this.apresentarMsgNegocio(Severity.INFO, "Sismama gravado com sucesso!");

				} catch (ApplicationBusinessException e) {
					LOG.error("Exceção caputada:", e);
				} catch (BaseListException e) {
					this.apresentarExcecaoNegocio(e);
				}
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}

	}

	private void setarResonsavelResidente() {
		AelSismamaMamoResVO voResidente = new AelSismamaMamoResVO();
		voResidente.setResidente(resultadoMamografiaConclusaoController.getResidente());

		AelSismamaMamoResVO voResponsavel = new AelSismamaMamoResVO();
		voResponsavel.setResponsavel(resultadoMamografiaConclusaoController.getResponsavel());

		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_RESIDENTE.toString(), voResidente);
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_SER_MATR_RESID.toString(), voResidente);
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_SER_VIN_COD_RESID.toString(), voResidente);
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_RESPONSAVEL.toString(), voResponsavel);
	}

	private boolean validarResponsavel() throws ApplicationBusinessException {
		if (resultadoMamografiaConclusaoController.getResponsavel() == null) {
			throw new ApplicationBusinessException(MamografiaControllerException.AEL_03243);
		}
		return true;
	}

	/**
	 * p_busca_parametros
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void pesquisarParametros() throws ApplicationBusinessException {
		String sitCodigoLi = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO).getVlrTexto();
		setSituacaoLiberado(sitCodigoLi);

		String sitCodigoAe = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA).getVlrTexto();
		setSituacaoAreaExecutora(sitCodigoAe);

		String sitCodigoEx = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO).getVlrTexto();
		setSituacaoExecutando(sitCodigoEx);

		String sitCodigoMamaBilateral = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_RX_MAMA_BILATERAL).getVlrTexto();
		setRxMamaBilateral(sitCodigoMamaBilateral);
	}

	public String voltar() {
		this.limparParametros();
		return PAGE_EXAMES_RESULTADO_MAMOGRAFIA_LIST;
	}

	private void limparParametros() {

		this.paciente = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.solicitacao = null;
		this.item = null;
		this.descricaoUsualExame = null;
		this.informacaoClinica = null;

		this.situacaoLiberado = null;
		this.situacaoAreaExecutora = null;
		this.situacaoExecutando = null;
		this.rxMamaBilateral = null;
		this.iseSeqpLida = null;
		this.sexoPaciente = null;

		this.reabrirLaudo = null;
		this.apresentarModalReabrirAssinarLaudo = null;
		this.msgModalReabrirLaudo = null;

		this.mapControleTela = null;
		this.abaSelecionada = null;

		this.habilitarMamaDireita = null;
		this.habilitarMamaEsquerda = null;

		this.mapMamaD = null;
		this.mapMamaE = null;
		this.mapAbaConclusao = null;

		// this.directPrint = false;
		this.modalConfirmaImpressao = false;

		this.vTipoVel = null;

		this.exibirMensagemReabrirLaudo = false;

		this.carregouPagina = false;
	}

	public void concluirLaudo() {
		setAbaSelecionada(2);
	}

	public String assinarLaudo() {
		try {
			Integer matriculaResponsavel = null;
			Integer vinCodigoResponsavel = null;

			if (paciente != null) {
				sexoPaciente = paciente.getSexo().toString();
			}

			setarResonsavelResidente();

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			if (resultadoMamografiaConclusaoController.getResponsavel() != null) {
				matriculaResponsavel = resultadoMamografiaConclusaoController.getResponsavel().getId().getMatricula();
				vinCodigoResponsavel = resultadoMamografiaConclusaoController.getResponsavel().getId().getVinCodigo();
			}

			examesLaudosFacade.validarResponsavel(matriculaResponsavel, vinCodigoResponsavel);
			vTipoVel = examesLaudosFacade.assinarLaudo(getSituacaoLiberado(), getSolicitacao(), getItem(), mapMamaD, mapMamaE, mapAbaConclusao, getRxMamaBilateral(), getSexoPaciente(),
					resultadoMamografiaEsquerdaController.getHabilitaMamaEsquerda(), resultadoMamografiaDireitaController.getHabilitaMamaDireita(), resultadoMamografiaConclusaoController
							.getResponsavel().getId().getNome(), matriculaResponsavel, vinCodigoResponsavel, informacaoClinica, nomeMicrocomputador);

			// directPrint = false;
			modalConfirmaImpressao = true;

			return PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD;

		} catch (BaseListException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void confirmaImpressao() {
		// directPrint = true;
		modalConfirmaImpressao = false;
		this.imprimirLaudo();
		// try {
		// this.iniciar();
		// } catch (ApplicationBusinessException e) {
		// apresentarExcecaoNegocio(e);
		// }
		// return null;
		// return PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD;
	}

	public void naoConfirmaImpressao() {
		// directPrint = false;
		this.modalConfirmaImpressao = false;
		// return null;
		// return PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD;
	}

	public String reabrirLaudoAssinado() {
		examesLaudosFacade.reabrirLaudo(getSituacaoAreaExecutora(), getSituacaoLiberado(), getSolicitacao(), getItem());
		this.exibirMensagemReabrirLaudo = true;
		return PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD;
	}

	/**
	 * Apresenta a mensagem AEL_02709
	 */
	private void apresentarMensagemLaudoReaberto() {
		try {
			if (this.exibirMensagemReabrirLaudo) {
				this.apresentarMsgNegocio(Severity.INFO, "AEL_02709");
			}
		} finally {
			this.exibirMensagemReabrirLaudo = false;
		}
	}

	public void exibirModalReabrirLaudo() {
		setReabrirLaudo(true);
		setApresentarModalReabrirAssinarLaudo(examesLaudosFacade.exibirModalReabrirLaudo(getSituacaoLiberado(), getSolicitacao(), getItem()));

		if (getApresentarModalReabrirAssinarLaudo()) {
			setMsgModalReabrirLaudo(obterMsgModalReabrirLaudo());
		}
	}

	private String obterMsgModalReabrirLaudo() {
		AelItemSolicitacaoExamesId chavePrimaria = new AelItemSolicitacaoExamesId(getSolicitacao(), getItem());
		AelItemSolicitacaoExames ise = examesLaudosFacade.obterAelItemSolicitacaoExamesPorChavePrimaria(chavePrimaria);

		StringBuilder sb = new StringBuilder(50);
		sb.append("Este exame foi liberado em ")
		.append(DateUtil.obterDataFormatada(ise.getDthrLiberada(), "dd/MM/yyyy HH:mm:ss"))
		.append(". Deseja continuar?");

		return sb.toString();
	}

	public void exibirModalAssinarLaudo() {
		setReabrirLaudo(false);
		setApresentarModalReabrirAssinarLaudo(examesLaudosFacade.exibirModalAssinarLaudo(getSituacaoAreaExecutora(), getSituacaoExecutando(), getSolicitacao(), getItem()));
	}

	public String getMsgModalMamoBilateral() {
		String lado = "";
		if (mapMamaD.get(C_RAD_NUM_FILM_D) == null || mapMamaD.get(C_RAD_NUM_FILM_D).getNumeroFilmes() == null || mapMamaD.get(C_RAD_NUM_FILM_D).getNumeroFilmes() == 0) {
			lado = this.getBundle().getString("LABEL_MODAL_MAMO_BILATERAL_DIREITA");
		} else {
			lado = this.getBundle().getString("LABEL_MODAL_MAMO_BILATERAL_ESQUERDA");
		}
		String msg = this.getBundle().getString("LABEL_MODAL_MAMO_BILATERAL_MENSAGEM");

		msg = msg.replace("{0}", lado);
		return msg;
	}

	public Boolean getNenhumFilmeInformado() {
		return (mapMamaD.get(C_RAD_NUM_FILM_D) == null || mapMamaD.get(C_RAD_NUM_FILM_D).getNumeroFilmes() == null || mapMamaD.get(C_RAD_NUM_FILM_D).getNumeroFilmes() == 0)
				&& (mapMamaE.get("C_RAD_NUM_FILM_E") == null || mapMamaE.get("C_RAD_NUM_FILM_E").getNumeroFilmes() == null || mapMamaE.get("C_RAD_NUM_FILM_E").getNumeroFilmes() == 0);
	}

	public void preGravar() {
		// Verifica se os número de filmes foram informados ou não

		habilitarMamaDireita = false;
		habilitarMamaEsquerda = false;

		if (mapControleTela.get(DominioSismamaMamoCadCodigo.HABILITAR_ABA_1.toString())
				&& ((mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()) == null || mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getNumeroFilmes() == null || mapMamaD
						.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getNumeroFilmes() == 0))) {
			habilitarMamaDireita = true;
		}

		if (mapControleTela.get(DominioSismamaMamoCadCodigo.HABILITAR_ABA_2.toString())
				&& ((mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()) == null || mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()).getNumeroFilmes() == null || mapMamaE
						.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()).getNumeroFilmes() == 0))) {
			habilitarMamaEsquerda = true;
		}

		if (habilitarMamaDireita || habilitarMamaEsquerda) {
			this.openDialog("modalMamografiaBilateralWG");
		} else {
			gravar();
		}
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Short getItem() {
		return item;
	}

	public void setItem(Short item) {
		this.item = item;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public String getInformacaoClinica() {
		return informacaoClinica;
	}

	public void setInformacaoClinica(String informacaoClinica) {
		this.informacaoClinica = informacaoClinica;
	}

	public String getSituacaoLiberado() {
		return situacaoLiberado;
	}

	public void setSituacaoLiberado(String situacaoLiberado) {
		this.situacaoLiberado = situacaoLiberado;
	}

	public String getSituacaoAreaExecutora() {
		return situacaoAreaExecutora;
	}

	public void setSituacaoAreaExecutora(String situacaoAreaExecutora) {
		this.situacaoAreaExecutora = situacaoAreaExecutora;
	}

	public String getSituacaoExecutando() {
		return situacaoExecutando;
	}

	public void setSituacaoExecutando(String situacaoExecutando) {
		this.situacaoExecutando = situacaoExecutando;
	}

	public String getRxMamaBilateral() {
		return rxMamaBilateral;
	}

	public void setRxMamaBilateral(String rxMamaBilateral) {
		this.rxMamaBilateral = rxMamaBilateral;
	}

	public Short getIseSeqpLida() {
		return iseSeqpLida;
	}

	public void setIseSeqpLida(Short iseSeqpLida) {
		this.iseSeqpLida = iseSeqpLida;
	}

	public String getSexoPaciente() {
		return sexoPaciente;
	}

	public void setSexoPaciente(String sexoPaciente) {
		this.sexoPaciente = sexoPaciente;
	}

	public Boolean getReabrirLaudo() {
		return reabrirLaudo;
	}

	public void setReabrirLaudo(Boolean reabrirLaudo) {
		this.reabrirLaudo = reabrirLaudo;
	}

	public Boolean getApresentarModalReabrirAssinarLaudo() {
		return apresentarModalReabrirAssinarLaudo;
	}

	public void setApresentarModalReabrirAssinarLaudo(Boolean apresentarModalReabrirAssinarLaudo) {
		this.apresentarModalReabrirAssinarLaudo = apresentarModalReabrirAssinarLaudo;
	}

	public String getMsgModalReabrirLaudo() {
		return msgModalReabrirLaudo;
	}

	public void setMsgModalReabrirLaudo(String msgModalReabrirLaudo) {
		this.msgModalReabrirLaudo = msgModalReabrirLaudo;
	}

	public Map<String, Boolean> getMapControleTela() {
		return mapControleTela;
	}

	public void setMapControleTela(Map<String, Boolean> mapControleTela) {
		this.mapControleTela = mapControleTela;
	}

	public Boolean habilitarChecksLinfonodos(String chave) {
		DominioLinfonodosAxilaresMamografia selecionado;
		if (mapMamaD.containsKey(chave)) {
			selecionado = mapMamaD.get(chave).getLinfonodoAxilar();
		} else {
			selecionado = mapMamaE.get(chave).getLinfonodoAxilar();
		}
		if (selecionado != null && (selecionado.equals(DominioLinfonodosAxilaresMamografia.NORMAL) || selecionado.equals(DominioLinfonodosAxilaresMamografia.NAO_VISIBILIZADO))) {
			if (mapMamaD.containsKey(chave)) {
				mapMamaD.get(DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_D.toString()).setChecked(false);
				mapMamaD.get(DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_D.toString()).setChecked(false);
				mapMamaD.get(DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_D.toString()).setChecked(false);
			} else {
				mapMamaE.get(DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_E.toString()).setChecked(false);
				mapMamaE.get(DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_E.toString()).setChecked(false);
				mapMamaE.get(DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_E.toString()).setChecked(false);
			}

			return false;
		}
		return true;
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public Map<String, AelSismamaMamoResVO> getMapMamaD() {
		if (mapMamaD == null) {
			mapMamaD = new HashMap<String, AelSismamaMamoResVO>();
		}
		return mapMamaD;
	}

	public void setMapMamaD(Map<String, AelSismamaMamoResVO> mapMamaD) {
		this.mapMamaD = mapMamaD;
	}

	public Map<String, AelSismamaMamoResVO> getMapMamaE() {
		if (mapMamaE == null) {
			mapMamaE = new HashMap<String, AelSismamaMamoResVO>();
		}
		return mapMamaE;
	}

	public void setMapMamaE(Map<String, AelSismamaMamoResVO> mapMamaE) {
		this.mapMamaE = mapMamaE;
	}

	public Map<String, AelSismamaMamoResVO> getMapAbaConclusao() {
		if (mapAbaConclusao == null) {
			mapAbaConclusao = new HashMap<String, AelSismamaMamoResVO>();
		}
		return mapAbaConclusao;
	}

	public void setMapAbaConclusao(Map<String, AelSismamaMamoResVO> mapAbaConclusao) {
		this.mapAbaConclusao = mapAbaConclusao;
	}

	public Boolean getModalConfirmaImpressao() {
		return modalConfirmaImpressao;
	}

	public void setModalConfirmaImpressao(Boolean modalConfirmaImpressao) {
		this.modalConfirmaImpressao = modalConfirmaImpressao;
	}

	public boolean isCarregouPagina() {
		return carregouPagina;
	}

}
