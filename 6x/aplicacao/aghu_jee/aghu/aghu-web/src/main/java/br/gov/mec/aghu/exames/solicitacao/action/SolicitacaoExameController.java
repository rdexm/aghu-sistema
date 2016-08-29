package br.gov.mec.aghu.exames.solicitacao.action;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.AtenderPacientesEvolucaoController;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioTelaOriginouSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.exames.action.ListarAmostrasSolicitacaoRecebimentoController;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.questionario.action.ImprimeInformacoesComplementaresController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.sismama.action.QuestionarioSismamaController;
import br.gov.mec.aghu.exames.solicitacao.sismama.action.QuestionarioSismamaInfComplementaresController;
import br.gov.mec.aghu.exames.solicitacao.vo.ConfirmacaoImpressaoEtiquetaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameItemVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class SolicitacaoExameController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(SolicitacaoExameController.class);
	private static final long serialVersionUID = 8482249832677382664L;
	
	private static final String PAGE_SOLICITACAO_EXAME="exames-solicitacaoExameCRUD";
	private static final String PAGE_PESQUISAR_EXAMES="exames-solicitacaoExameList";
	private static final String PAGE_EXIBIR_TICKET_PACIENTE="exames-exibirTicketPaciente";
	private static final String PAGE_LISTAR_EXAMES_AGENDAMENTO = "exames-listarAgendamento";
	
	public static final String POR_EXAME = "0";
	public static final String POR_LOTE = "1";
	
	public static final String SEM_IMPRESSAO = "SEM_IMPRESSAO";
	public static final String MENSAGEM_ETIQUETAS_IMPRESSAS_SUCESSO_REIMPRIMIR_ETIQUETA = "MENSAGEM_ETIQUETAS_IMPRESSAS_SUCESSO_REIMPRIMIR_ETIQUETA";
	public static final String ERRO_IMPRESSORA_ETIQUETA_NAO_ENCONTRADA = "ERRO_IMPRESSORA_ETIQUETA_NAO_ENCONTRADA";
	private static final String PAGE_PACIENTE_EVOLUCAO="ambulatorio-atenderPacientesEvolucao";
	
	private Boolean concordaNormaSeguranca = Boolean.FALSE;
	private Boolean precisaConcordarComNormaSeguranca = Boolean.FALSE;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade ;
		
	@EJB
	private IAdministracaoFacade administracaoFacade;

	@EJB
	private ICascaFacade cascaFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@Inject
	private ListarExamesSendoSolicitadosController listarExamesSendoSolicitadosController;

	@Inject @SelectionQualifier
	private ListarExamesSendoSolicitadosLoteController listarExamesSendoSolicitadosLoteController;

	@Inject
	private VisualizarTicketPacienteController visualizarTicketPacienteController;

	@Inject
	private ImprimeInformacoesComplementaresController imprimeInformacoesComplementaresController;

	@Inject
	private QuestionarioSismamaInfComplementaresController questionarioSismamaInfComplementaresController;

	@Inject
	private QuestionarioSismamaController questionarioSismamaController;

	@Inject
	private IdentificarUnidadeExecutoraController identificarUnidadeExecutoraController;

	@Inject
	private ListarAmostrasSolicitacaoRecebimentoController listarAmostrasSolicitacaoRecebimentoController;
	
	@Inject 
	private AtenderPacientesEvolucaoController atenderPacientesEvolucaoController;

	private String paginaChamadora;
	private SolicitacaoExameVO solicitacaoExame = new SolicitacaoExameVO();
	private Integer atendimentoSeq;
	private Integer atendimentoDiversoSeq;

	private boolean habilitaBotaoAgendamento = false;

	private List<ItemSolicitacaoExameVO> listaItemSolicitacaoExame;
	
    private List<ExameAgendamentoMesmoHorarioVO> listaDifAgendaPaciente;

	/**
	 * Aba corrente
	 */
	private String currentTabName;
	private Boolean includeUnidadeTrabalho;

	private boolean exibirModalNumeroSolicitacao;

	private List<AgendamentoExameVO> examesAgendamentoSelecao;

	private Boolean origemAmbulatorio = Boolean.FALSE;
	private List<AelRespostaQuestao> respostas;
	private Boolean existeUnidadeFuncional;

	private String nomeMicro;
	private ConfirmacaoImpressaoEtiquetaVO confirmacaoImpressaoEtiquetaVO;

	// Parametros vindos do Emergencia;
	private String abaDestino;
	private String voltarPara;
	private Boolean voltarEmergencia = Boolean.FALSE;
	private Integer pacCodigo;
	private Short seqp;
	private Integer conNumero;
	private boolean inibeSolicExame;

	private boolean usuarioEstudanteMedicina;
	private boolean usuarioSolicExameProtocoloEnfermagem;
	private boolean usuarioUnidadeExecutora;
	private boolean isOrigemInternacao = false;
	private boolean desabilitaSugestionUnidadeSolicitante;
	
	// #48567
	private Boolean temExameRedome = Boolean.FALSE;
	private ConfirmacaoImpressaoEtiquetaVO confirmacaoImpressaoEtiquetaRedomeVO;

	private AghAtendimentos atendimento;
	private DominioOrigemAtendimento origem;
	private Integer codProntuario;
	
	private Boolean forcarGeracaoPendente = Boolean.FALSE;
	private Boolean isSolicitarExame = false;

	private void zerarController() {
		initSolicitacaoExameVO();
		atendimentoSeq = null;
		atendimentoDiversoSeq = null;
		
		listaItemSolicitacaoExame = null;

		includeUnidadeTrabalho = null;

		exibirModalNumeroSolicitacao = false;
		
		examesAgendamentoSelecao = null;
		
		origemAmbulatorio = false;
		respostas = null;
		
		currentTabName = POR_EXAME;
		
		confirmacaoImpressaoEtiquetaVO = new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null);
		confirmacaoImpressaoEtiquetaRedomeVO = new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.FALSE, Boolean.TRUE);
		temExameRedome = Boolean.FALSE;
		concordaNormaSeguranca = Boolean.FALSE;
		precisaConcordarComNormaSeguranca = Boolean.FALSE;
		
		forcarGeracaoPendente = Boolean.FALSE;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);

		if (this.getRequestParameter("atdSeq") != null && !this.getRequestParameter("atdSeq").isEmpty()) {
			this.setParameters();
		}
		currentTabName = POR_EXAME;
		initSolicitacaoExameVO();
		
		confirmacaoImpressaoEtiquetaVO = new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null);
		confirmacaoImpressaoEtiquetaRedomeVO = new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.FALSE, Boolean.TRUE);
	}

	private void initSolicitacaoExameVO() {
		solicitacaoExame = new SolicitacaoExameVO(new SolicitacaoExameItemVO());
	}

	@Override
	protected BaseEntity getEntidadePai() {

		if (this.solicitacaoExame == null) {
			return null;
		}

		return this.solicitacaoExame.getAtendimento();
	}

	/**
	 * Metodo configurado no page.xml para ser chamado ao entrar na tela.
	 * 
	 */
	public void inicio() {
	
		carregarTela();
		verificarPermiteSolicitarOuPendente();
		filtrarExamesProtocoloEnfermagem();
		verificarPermissaoUnidadeExecutora();
		carregaParametros();
		listarExamesSendoSolicitadosController.inicio();
	
	}
	
	private void carregaParametros() {
		try {
			AghParametros paramUnidadeRadiologia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HABILITA_UNI_FUNCIONAL_SOLIC_EXA);
			setDesabilitaSugestionUnidadeSolicitante(paramUnidadeRadiologia.getVlrTexto() != null && paramUnidadeRadiologia.getVlrTexto().equalsIgnoreCase("N"));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void verificarPermiteSolicitarOuPendente() {
		if (cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "solicitarExamePorEstudanteMedicina", "executar")) {
			setUsuarioEstudanteMedicina(true);
			// No momento a permissão "solicitarExamePorEstudanteMedicina" possibilita a Solicitação de Exames apenas
			// pela Lista de Pacientes do módulo Ambulatório. No futuro será contemplado a Solic. de Exames também por Emergência-Térreo
			
			if (paginaChamadora == null  || !isPaginaChamadoraAtendimentoAmbulatorio()) {
				setInibeSolicExame(true);
				apresentarMsgNegocio(Severity.INFO, this.getBundle().getString("AEL_00832"));
			} else {
				solicitacaoExame.setGeraSolicExameSitPendente(true);
			}
		}
		if (forcarGeracaoPendente){
			solicitacaoExame.setGeraSolicExameSitPendente(true);
		}
	}

	private boolean isPaginaChamadoraAtendimentoAmbulatorio() {
		return paginaChamadora != null 
				&& (paginaChamadora.equals("ambulatorio-atenderPacientesAgendados")
						|| paginaChamadora.equals("ambulatorio-atenderPacientesEvolucao"));
	}

	private void filtrarExamesProtocoloEnfermagem(){
		if (!cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "inibirFiltroExamesProtocEnfermagem", "pesquisar") 
				&& cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "listarExamesProtocEnfermagem", "pesquisar")){
			setUsuarioSolicExameProtocoloEnfermagem(true);
		}

		if (this.solicitacaoExame != null && solicitacaoExame.getOrigem() != null && solicitacaoExame.getOrigem().equals(DominioOrigemAtendimento.I)){
			setOrigemInternacao(true);
		} else {
			setOrigemInternacao(false);
		}
	}

	private void verificarPermissaoUnidadeExecutora() {
		if (cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "elaborarSolicitacaoExameConsultaAntiga", "executor")){
			setUsuarioUnidadeExecutora(true);
		} else {
			setUsuarioUnidadeExecutora(false);
		}
	}

	private void setParameters() {
		this.setAtendimentoSeq(Integer.parseInt(this.getRequestParameter("atdSeq")));
		this.setAbaDestino(this.getRequestParameter("abaDestino"));
		this.setVoltarPara(this.getRequestParameter("voltarPara"));
		this.setVoltarEmergencia(Boolean.parseBoolean(this.getRequestParameter("voltarEmergencia")));
		this.setPacCodigo(Integer.valueOf(this.getRequestParameter("pacCodigo")));
		this.setSeqp(Short.valueOf(this.getRequestParameter("seqp")));
		this.setConNumero(Integer.valueOf(this.getRequestParameter("numeroConsulta")));
	}
	@SuppressWarnings("PMD.NPathComplexity")
	private void carregarTela() {
		this.exibirModalNumeroSolicitacao = false;

		this.setCurrentTabName(POR_EXAME);

		try {
			solicitacaoExame = solicitacaoExameFacade.buscaSolicitacaoExameVO(atendimentoSeq, atendimentoDiversoSeq);
			
			//Atendimento pode ter vindo preenchido de outras telas.
			if (atendimento !=null && atendimentoSeq != null && atendimento.getSeq().equals(atendimentoSeq)){
				solicitacaoExame.setAtendimento(atendimento);
				if (atendimento.getUnidadeFuncional() != null && atendimento.getUnidadeFuncional().getSeq()!= null
						&& solicitacaoExame.getUnfSeq()!=null && solicitacaoExame.getUnfSeq().equals(atendimento.getUnidadeFuncional().getSeq())){
					solicitacaoExame.setUnidadeFuncional(atendimento.getUnidadeFuncional());
				}
				
			} else {
				if(solicitacaoExame.getUnfSeq() != null){
					solicitacaoExame.setUnidadeFuncional(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(solicitacaoExame.getUnfSeq()));
				}
				if (solicitacaoExame.getAtendimento() == null && solicitacaoExame.getAtendimentoDiverso() == null) {
					if (solicitacaoExame.getAtendimentoSeq() != null) {
						solicitacaoExame.setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(solicitacaoExame
								.getAtendimentoSeq()));
					} else if (solicitacaoExame.getAtendimentoDiversoSeq() != null) {
						solicitacaoExame.setAtendimentoDiverso(examesFacade
								.obterAelAtendimentoDiversosPorChavePrimaria(solicitacaoExame.getAtendimentoDiversoSeq()));
					}
				}
				
			}
			//Responsável default é o usuário logado para todos casos genéricos
			solicitacaoExame.setResponsavel(servidorLogadoFacade.obterServidorLogadoSemCache());
			
			if (DominioOrigemAtendimento.X.equals(solicitacaoExame.getOrigem())){
				solicitacaoExame.setResponsavel(null);
			} 
			
			// Determina tela que originou a chamada
			origemAmbulatorio = false;
			if (paginaChamadora == null) {
				solicitacaoExame.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_PESQUISA_SOLICITACAO_EXAME);
				setPaginaChamadora(DominioTelaOriginouSolicitacaoExame.TELA_PESQUISAR_EXAMES.getDescricao());
			} else {
				if (isPaginaChamadoraAtendimentoAmbulatorio()) {
					solicitacaoExame.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_AMBULATORIO);
					origemAmbulatorio = true;
				} else {
					solicitacaoExame.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.obterPorRetorno(paginaChamadora));
				}
			}

			this.setIncludeUnidadeTrabalho(solicitacaoExameFacade.mostrarUnidadeTrabalhoSolicitacaoExame(solicitacaoExame));
			
			if(includeUnidadeTrabalho && identificarUnidadeExecutoraController.getUnidadeExecutora()!= null){
				if (solicitacaoExame.getNumeroConsulta() != null
						&& DominioOrigemAtendimento.A.equals(solicitacaoExame.getOrigem())
						|| DominioOrigemAtendimento.U.equals(solicitacaoExame.getOrigem())) {
					/* AELP_BUSCA_CONSULTA */
					solicitacaoExame.setResponsavel(this.solicitacaoExameFacade.buscarConsulta(this.solicitacaoExame));
				}
			}
			if (solicitacaoExame.getNumeroConsulta() != null 
					&& (origemAmbulatorio || this.voltarEmergencia || DominioOrigemAtendimento.A.equals(solicitacaoExame.getOrigem()))) {
				/**
				 * rn_isep_autoriza_mam
				 */
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

				if (!registroColaboradorFacade.permiteSerResponsSolicExame(servidorLogado.getId().getVinCodigo(), servidorLogado.getId().getMatricula())) {
					solicitacaoExame.setResponsavel(this.solicitacaoExameFacade.buscarConsulta(this.solicitacaoExame));
				}
			}
			
			this.setListaItemSolicitacaoExame(solicitacaoExame.getItemSolicitacaoExameVos());
			
			this.setRespostas(null);

			if (DominioOrigemAtendimento.X.equals(solicitacaoExame.getOrigem())) {
				solicitacaoExame.setInformacoesClinicas(DominioOrigemAtendimento.X.getDescricao());
			}

			if (solicitacaoExame.getUnidadeFuncional() != null) {
				this.setExisteUnidadeFuncional(true);
			} else {
				this.setExisteUnidadeFuncional(false);
			}
			
			listarExamesSendoSolicitadosController.initController(solicitacaoExame);

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public void renderAbaLote() {
		if (POR_LOTE.equals(this.getCurrentTabName())) {
			if (this.solicitacaoExame == null) {
				throw new IllegalStateException("Este metodo foi chamado em um momento invalido da controller.");
			}
			listarExamesSendoSolicitadosLoteController.renderAbaPorLote(solicitacaoExame);
		}
	}

	public List<AghCid> pesquisarCids(String param) {

		String sigla = null;
		Integer manSeq = null;

		if (getRenderPorLote()){
			
			if (this.listarExamesSendoSolicitadosLoteController.getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise()
					.getAelExames().getSigla() != null
					&& this.listarExamesSendoSolicitadosLoteController.getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise()
							.getId().getManSeq() != null) {

				sigla = this.listarExamesSendoSolicitadosLoteController.getItemSolicitacaoExameVo().getUnfExecutaExame()
						.getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames().getSigla();
				
				manSeq = this.listarExamesSendoSolicitadosLoteController.getItemSolicitacaoExameVo().getUnfExecutaExame()
						.getUnfExecutaExame().getAelExamesMaterialAnalise().getId().getManSeq();
			}
		} else if (this.listarExamesSendoSolicitadosController.getItemSolicitacaoExameVo().getUnfExecutaExame()
				.getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames().getSigla() != null
				&& this.listarExamesSendoSolicitadosController.getItemSolicitacaoExameVo().getUnfExecutaExame()
						.getUnfExecutaExame().getAelExamesMaterialAnalise().getId().getManSeq() != null) {

			sigla = this.listarExamesSendoSolicitadosController.getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame()
					.getAelExamesMaterialAnalise().getAelExames().getSigla();
			manSeq = this.listarExamesSendoSolicitadosController.getItemSolicitacaoExameVo().getUnfExecutaExame()
					.getUnfExecutaExame().getAelExamesMaterialAnalise().getId().getManSeq();
		}
		return this.returnSGWithCount(
				aghuFacade.pesquisarCidsPorDescricaoOuIdPassandoExame(
						param == null ? "" : param,100,
						this.solicitacaoExame.getAtendimento() == null
								|| this.solicitacaoExame.getAtendimento().getPaciente() == null
								|| this.solicitacaoExame.getAtendimento().getPaciente().getSexo() == null
								|| DominioSexo.I.equals(this.solicitacaoExame.getAtendimento().getPaciente()
										.getSexo()) ? null	: DominioSexoDeterminante.valueOf(this.solicitacaoExame.getAtendimento().getPaciente().getSexo().toString()), sigla,
						manSeq), contarCids(param, sigla, manSeq));
	}

	public Long contarCids(String param, String sigla, Integer manSeq) {
		return aghuFacade.contarCidsPorDescricaoOuIdPassandoExame(
				param == null ? "" : param,
				this.solicitacaoExame.getAtendimento() == null || this.solicitacaoExame.getAtendimento().getPaciente() == null
						|| this.solicitacaoExame.getAtendimento().getPaciente().getSexo() == null
						|| DominioSexo.I.equals(this.solicitacaoExame.getAtendimento().getPaciente().getSexo()) ? null : DominioSexoDeterminante
						.valueOf(this.solicitacaoExame.getAtendimento().getPaciente().getSexo().toString()), sigla, manSeq );
	}

	public void removerRespostasQuestao(final Short sequencialItem) {
		if (this.getRespostas() != null && sequencialItem != null) {
			final List<AelRespostaQuestao> respostas = new ArrayList<AelRespostaQuestao>();
			for (final AelRespostaQuestao aelRespostaQuestao : this.getRespostas()) {
				if (!aelRespostaQuestao.getSequencial().equals(sequencialItem)) {
					respostas.add(aelRespostaQuestao);
				}
			}
			this.setRespostas(respostas);
		}
	}

	public void adicionarRespostas(final List<AelGrupoQuestao> grupoQuestaos, final Short sequencialItem) {
		if (this.respostas == null) {
			this.respostas = new ArrayList<AelRespostaQuestao>();
		}
		for (final AelGrupoQuestao grupo : grupoQuestaos) {
			for (AelRespostaQuestao resposta : grupo.getAelRespostaQuestaos()) {
				resposta.setSequencial(sequencialItem);
				respostas.add(resposta);
			}
		}
	}

	// FIM #2257

	public void editarItemSolicitacaoExame(ItemSolicitacaoExameVO item) {
		if (item != null) {
//			this.setRenderPorExame(true);
//			this.setRenderPorLote(false);
			this.setCurrentTabName(POR_EXAME);
			this.listarExamesSendoSolicitadosController.doSetItemSolicitacaoExameVoEdicao(item);
		}
	}

	public void limpar() {

		this.listarExamesSendoSolicitadosController.initItemSolicitacaoExameVo();

		this.carregarTela();

		questionarioSismamaController.limpar();
		questionarioSismamaInfComplementaresController.limpar();
		this.concordaNormaSeguranca = Boolean.FALSE;
		this.precisaConcordarComNormaSeguranca = Boolean.FALSE;
	}

	public void excluirItemSolicitacaoExame(ItemSolicitacaoExameVO item) {
		if (item != null) {

			if (!item.getDependentesObrigratorios().isEmpty()) {
				List<ItemSolicitacaoExameVO> itensSolicitacao = getListaItemSolicitacaoExame();
				
				List<Integer> indicesDependentesObrigatorios = getIndicesDependentesObrigatorios(item, itensSolicitacao);
				removerItensSolicitacaoPorIndex(indicesDependentesObrigatorios);
			}

			//Se o ítem é opcional desmarca na lista de opcionais do pai.
			if (item.getEhDependenteOpcional()) {
				for (ItemSolicitacaoExameVO iseVO : this.getListaItemSolicitacaoExame()) {
					for (ItemSolicitacaoExameVO iseVOOpcional : iseVO.getDependentesOpcionais()) {
						if (iseVOOpcional == item) { //Testa se é igual por referência á memória
							iseVOOpcional.setExameOpcionalSelecionado(Boolean.FALSE);
							break;
						}
					}
				}
			}
			
			//Se o ítem tem exames opcionais na lista remove
			if (!item.getDependentesOpcionais().isEmpty()) {
				this.getListaItemSolicitacaoExame().removeAll(item.getDependentesOpcionais());
			}
			this.removerRespostasQuestao(item.getSequencial());
			this.getListaItemSolicitacaoExame().remove(item);
			this.renumerarSeqItensSolicExame();
		}		
	}

	private List<Integer> getIndicesDependentesObrigatorios(ItemSolicitacaoExameVO item, List<ItemSolicitacaoExameVO> itensSolicitacao) {
		List<ItemSolicitacaoExameVO> dependentesObrigatoriosDoItem = item.getDependentesObrigratorios();
		
		List<Integer> indicesDependentesObrigatorios = new ArrayList<Integer>();
		
		int index = itensSolicitacao.indexOf(item) + 1;
		ItemSolicitacaoExameVO itemSolicitacao = null;
		do {
			itemSolicitacao = itensSolicitacao.get(index);
			
			if(itemSolicitacao.getEhDependenteObrigatorio() && dependentesObrigatoriosDoItem.contains(itemSolicitacao)) {
				indicesDependentesObrigatorios.add(index);
			}
			
			index++;
		} while ((index < itensSolicitacao.size()) && itemSolicitacao.getEhDependenteObrigatorio());
		
		return indicesDependentesObrigatorios;
	}
	
	private void removerItensSolicitacaoPorIndex(List<Integer> indicesParaRemover) {
		int quantidadeItensRemovidos = 0;
		for (Integer indiceParaRemover : indicesParaRemover) {
			getListaItemSolicitacaoExame().remove((indiceParaRemover - quantidadeItensRemovidos));
			quantidadeItensRemovidos++;
		}
	}

	private void renumerarSeqItensSolicExame(){
		Short seq = Short.valueOf("1");
		for (ItemSolicitacaoExameVO item : this.getListaItemSolicitacaoExame()){
			if(!item.getEhDependenteObrigatorio() && !item.getEhDependenteOpcional()){
				item.setSequencial(seq);
				seq++;
			}
		}		
	}
	
	public Short retornarProximoSeqItemExame(){
		Short nextSeq = Short.valueOf("1");
		for (ItemSolicitacaoExameVO item : this.getListaItemSolicitacaoExame()){
			if(!item.getEhDependenteObrigatorio() && !item.getEhDependenteOpcional()){
				nextSeq++;
			}
		}
		
		return nextSeq;
	}
	
	private Boolean mostrarMensagemConcordanciaRaioX(SolicitacaoExameVO solicitacaoExame) {
		try {
			AghParametros paramUnidadeRadiologia = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_UNID_RADIODIAG);
			for (ItemSolicitacaoExameVO voExame : solicitacaoExame.getItemSolicitacaoExameVos()) {
				if(voExame.getItemSolicitacaoExame() != null && voExame.getItemSolicitacaoExame().getAelUnfExecutaExames() != null && voExame.getItemSolicitacaoExame().getAelUnfExecutaExames().getUnidadeFuncional() != null && voExame.getItemSolicitacaoExame().getTipoTransporte() != null){
					if (voExame.getItemSolicitacaoExame().getAelUnfExecutaExames().getUnidadeFuncional().getSeq() == paramUnidadeRadiologia
							.getVlrNumerico().shortValue()	&& voExame.getTipoTransporte() == DominioTipoTransporte.L) {
						return Boolean.TRUE;
					}
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return Boolean.FALSE;
	}

	/**
	 * Método executado pelos botoes de Sim ou Não da modal de Concordância com a norma de segurança para execução de RX no leito
	 * @author rhrosa
	 */
	public void confirmarMensagemConcordancia(Boolean concorda) {
		if (concorda) {
			this.concordaNormaSeguranca = Boolean.TRUE;
			this.gravar();
		}
	}

	public String gravar() {
		try {
			this.precisaConcordarComNormaSeguranca = mostrarMensagemConcordanciaRaioX(this.solicitacaoExame);
			
			if (precisaConcordarComNormaSeguranca) {
				openDialog("modalConcordanciaWG");
			}

			if (this.precisaConcordarComNormaSeguranca) {
				if (this.concordaNormaSeguranca) {
					return this.finalizarGravacao();
				} else {			
					return null;		
				}
			}

			if (!this.precisaConcordarComNormaSeguranca && !this.concordaNormaSeguranca) {
				return this.finalizarGravacao();
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private String finalizarGravacao() throws BaseException {
		try {
			//Valida Unidade de Coleta de acordo com o parâmetro P_COD_UNIDADE_COLETA_DEFAULT
			this.validarUnidadeFuncionalColeta();
			
			// Unidade de Trabalho. Serah utilizada para o gravar. definir como
			// passar para o gravar.
			this.solicitacaoExame.setUnidadeTrabalho(null);
			if (this.includeUnidadeTrabalho) {
				this.solicitacaoExame.setUnidadeTrabalho(this.identificarUnidadeExecutoraController.getUnidadeExecutora());
			}

			// TODO implementar controle de excluido quando necessario.
//			List<AelItemSolicitacaoExames> itemSolicExameExcluidos = new LinkedList<AelItemSolicitacaoExames>();
			if(this.solicitacaoExame.getUnidadeFuncional().getSeq() != null){
				AghUnidadesFuncionais unidade = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.solicitacaoExame.getUnidadeFuncional().getSeq());
				solicitacaoExame.setUnidadeFuncional(unidade);
			}
			
			solicitacaoExame = examesBeanFacade.gravaSolicitacaoExame(solicitacaoExame, getEnderecoRedeHostRemoto());

			List<ItemSolicitacaoExameVO> itemSolicitacaoExameVO = solicitacaoExame.getItemSolicitacaoExameVos();

			if (itemSolicitacaoExameVO != null) {
				verificarSeExameRedomeEnviaParaReceberAmostra(itemSolicitacaoExameVO);
			}

            Short unidadeFuncionalUsuarioSeq = null;
            if (getIncludeUnidadeTrabalho() &&
            		this.identificarUnidadeExecutoraController != null &&
                    this.identificarUnidadeExecutoraController.getUnidadeExecutora() != null &&
                    this.identificarUnidadeExecutoraController.getUnidadeExecutora().getSeq() != null){
                unidadeFuncionalUsuarioSeq = this.identificarUnidadeExecutoraController.getUnidadeExecutora().getSeq();
            }

			exibirModalNumeroSolicitacao = true;

            habilitaBotaoAgendamento = agendamentoExamesFacade.validaHabilitaAgendamento(solicitacaoExame.getAtendimentoSeq(), solicitacaoExame.getSolicitacaoExameSeq(),
							solicitacaoExame.getAtendimentoDiversoSeq(),solicitacaoExame.getUnidadeFuncional().getSeq(),
                    unidadeFuncionalUsuarioSeq);

			if (habilitaBotaoAgendamento) {
				this.examesAgendamentoSelecao = this.obterExames();
			}

			if ("descricaoCirurgicaCirRealizada".equals(this.paginaChamadora)) {
				return "descricaoCirurgicaCirRealizada";
			}
			
			// Em caso de sucesso ao Gravar atualiza a renderização da tela (form)
			//RequestContext.getCurrentInstance().execute("renderCadastroForm();");
			
//			if (vo.isGeraSolicExameSitPendente()){
//				this.getSolicitacaoExameFacade().finalizarGeracaoSolicExamePendente(result.getSeqSolicitacaoSalva());
//			}
			
			//Solicitação de Exame gerada com situação = Pendente não deve gerar Etiqueta
			if (!solicitacaoExame.isGeraSolicExameSitPendente() && verificarImpressaoEtiqueta().equals("CONFIRMA_IMPRESSAO")) {
				openDialog("modalImpressaoEtiquetaAmostraWG");
			} else if (!solicitacaoExame.isGeraSolicExameSitPendente() && verificarSeImprimeEtiquetaRedome()) {
				openDialog("modalImpressaoEtiquetaAmostraRedomeWG");
			} else {
				exibirModalNumeroSolicitacao = true;
				openDialog("modalNumeroSolicitacaoWG");
			}
			
			imprimirQuestionarios();
			
			this.concordaNormaSeguranca = Boolean.FALSE;
			this.precisaConcordarComNormaSeguranca = Boolean.FALSE;

			//this.setRespostas(null);
		} catch (BaseException e) {
			if(e.getMessage().equals("P_COD_UNIDADE_COLETA_DEFAULT_NÃO_É_UMA_UND_FUNCIONAL_VÁLIDA")){
				this.apresentarMsgNegocio(Severity.ERROR,"O valor do parâmetro P_COD_UNIDADE_COLETA_DEFAULT não é uma Unidade Funcional de Coleta válida");
			}else{
				super.apresentarExcecaoNegocio(e);
			}
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_OBTER_NOME_MICRO");
		}
		return null;
	}

	public void verificarSeExameRedomeEnviaParaReceberAmostra(List<ItemSolicitacaoExameVO> listaItemSolicitacaoExameVOs)
			throws BaseException {

		if (this.identificarUnidadeExecutoraController.getUnidadeExecutora() != null) {

			AghUnidadesFuncionais unidadeExecutora = this.identificarUnidadeExecutoraController.getUnidadeExecutora();

			for (ItemSolicitacaoExameVO itemSoe : listaItemSolicitacaoExameVOs) {

				AelItemSolicitacaoExames item = itemSoe.getItemSolicitacaoExame();

				if (item != null) {
					Boolean isRedome = solicitacaoExameFacade.verificarSeExameSendoSolicitadoRedome(item,
							unidadeExecutora);

					if (isRedome) {

						// Uma vez que identifique que há um exame REDOME,
						// mantém esta informação para ser utilizada
						// durante a verificação da necessidade de impressão de
						// etiqueta de amostra de exame REDOME
						// durante a finalização de gravação da solicitação de
						// exame
						temExameRedome = isRedome;
						Integer soeSeq = item.getSolicitacaoExame().getSeq();
						this.getListarAmostrasSolicitacaoRecebimentoController().iniciar();
						String str = Integer.toString(soeSeq);
						Long soeSeqConvertido = Long.parseLong(String.valueOf(str));
						this.listarAmostrasSolicitacaoRecebimentoController
								.setValorEntradaCampoSolicitacao(soeSeqConvertido);
						this.listarAmostrasSolicitacaoRecebimentoController.pesquisar();
						this.listarAmostrasSolicitacaoRecebimentoController
								.setNaoMostrarMensagemSucessoQndoExaRedomeForSolicitado(true);
						this.getListarAmostrasSolicitacaoRecebimentoController().receberTodasAmostras();
						this.solicitacaoExameFacade.gravarExtratoDoadorRedome(soeSeq);
					}
				}
			}
		}
	}

//    public void preencheModalHorariosExamesConcorrentesDisponiveis() {
//        listaDifAgendaPaciente = agendamentoExamesFacade
//                .pesquisaHorariosDisponiveisAgendamentoConcorrente(getPacCodigo(),
//                                                     getSolicitacaoExame().getI)
//    }
	
	private void imprimirQuestionarios() {
		if (this.solicitacaoExame.isImprimirQuestionario()) {
			imprimeInformacoesComplementaresController.setMostrarMensagens(false);
			// imprimeInformacoesComplementaresController.setPacCodigo(solicitacaoExame.getCodPaciente());
			imprimeInformacoesComplementaresController.setSoeSeq(solicitacaoExame.getSeqSolicitacaoSalva());
			for (final ItemSolicitacaoExameVO item : this.listaItemSolicitacaoExame) {
				if (item.getQuestionarios() != null && !item.getQuestionarios().isEmpty() && item.getSeqp() != 0) {
				imprimeInformacoesComplementaresController.setSeqp(item.getSeqp());
					for (final AelQuestionarios questionario : item.getQuestionarios()) {
						imprimeInformacoesComplementaresController.setQtnSeq(questionario.getSeq());
						imprimeInformacoesComplementaresController.directPrint();
					}
				}
			}
		}
	}

	public String agendarExames() {
		this.examesAgendamentoSelecao = this.obterExames();
		return PAGE_LISTAR_EXAMES_AGENDAMENTO;
	}

	public Boolean getIseEmEdicao() {
		boolean emEdicao = false;
		if (getSolicitacaoExame().getItemSolicitacaoExameVos() != null) {
			for (ItemSolicitacaoExameVO iseVO : getSolicitacaoExame().getItemSolicitacaoExameVos()) {
				if (iseVO.getEmEdicao()) {
					emEdicao = true;
					break;
				}
			}
		}
		return emEdicao;
	}

	public List<AgendamentoExameVO> obterExames() {
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = this.solicitacaoExameFacade.pesquisarItensSolicitacoesExamesPorSolicitacao(this.getSolicitacaoExame().getSeqSolicitacaoSalva());
		List<AgendamentoExameVO> listaExames = new ArrayList<AgendamentoExameVO>();
		for (AelItemSolicitacaoExames itemSolicitacaoExame : listaItemSolicitacaoExame) {
			AgendamentoExameVO agendamentoExameVO = new AgendamentoExameVO();
			itemSolicitacaoExame.setSolicitacaoExame(this.solicitacaoExameFacade.obterSolicitacaoExame(this.getSolicitacaoExame().getSeqSolicitacaoSalva()));
			agendamentoExameVO.setItemExame(itemSolicitacaoExame);
			agendamentoExameVO.setSelecionado(true);
			if (itemSolicitacaoExame != null && itemSolicitacaoExame.getAelUnfExecutaExames() != null) {
				agendamentoExameVO.setDthrReativacao(itemSolicitacaoExame.getAelUnfExecutaExames().getDthrReativaTemp());	
			}
			listaExames.add(agendamentoExameVO);
		}
		return listaExames;
	}

	public void posDeleteActionSbUnidadeFuncional() {
		setExisteUnidadeFuncional(false);
		if (listarExamesSendoSolicitadosController != null) {
			listarExamesSendoSolicitadosController.initItemSolicitacaoExameVo();
			listarExamesSendoSolicitadosController.setUnfExecutaExame(null);
			listaItemSolicitacaoExame.clear();
		}
		if (listarExamesSendoSolicitadosLoteController != null) {
			listarExamesSendoSolicitadosLoteController.setModalMessage(null);
			listarExamesSendoSolicitadosLoteController.setModalListaExames(null);
			listarExamesSendoSolicitadosLoteController.setExibirModalExamesSemPermissao(false);
			renderAbaLote();
		}
	}

	public void posSelectionActionSbUnidadeFuncional() {
		this.setExisteUnidadeFuncional(true);
	}

	public String cancelar() {
		String retorno = null;
		
		this.listarExamesSendoSolicitadosController.initItemSolicitacaoExameVo();
		this.listarExamesSendoSolicitadosLoteController.zerarController();
		zerarController();
		if (StringUtils.isNotBlank(this.getPaginaChamadora()) && !this.isSolicitarExame) {
			return getPaginaChamadora();
		}
		if(this.isSolicitarExame){
			atenderPacientesEvolucaoController.setAcao(null);
			this.setIsSolicitarExame(false);
			retorno = PAGE_PACIENTE_EVOLUCAO;
		}else{
			retorno = PAGE_PESQUISAR_EXAMES;
		}				
		return retorno;
	}

	public boolean exibirModalNumeroSolicitacao() {
		return exibirModalNumeroSolicitacao;
	}

	private String verificarImpressaoEtiqueta() {

		try {
			nomeMicro = getEnderecoRedeHostRemoto();
			AghMicrocomputador microcomputador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeMicro, null);
			if (microcomputador == null) {
				apresentarMsgNegocio(Severity.INFO, "MSG_MICROCOMPUTADOR_NAO_ENCONTRADO_AGH_MICROCOMPUTADOR");
			} else {
				confirmacaoImpressaoEtiquetaVO = this.solicitacaoExameFacade.verificarImpressaoEtiqueta(
						solicitacaoExame, microcomputador);
				if (confirmacaoImpressaoEtiquetaVO.getConfirmaImpressao()) {
					return "CONFIRMA_IMPRESSAO";
				} else if (confirmacaoImpressaoEtiquetaVO.getConfirmaImpressaoSemModal()) {
					return "CONFIRMA_IMPRESSAO_SEM_MODAL";
				}
			}

		} catch (UnknownHostException e) {
			LOG.error("Nome do Microcomputador Não Encontrado!");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		return "COMPUTADOR_NAO_ENCONTRATO";
	}

	public void confirmarImpressaoEtiqueta(Boolean imprime) {
		confirmacaoImpressaoEtiquetaVO.setConfirmaImpressao(confirmacaoImpressaoEtiquetaVO.getConfirmaImpressao() && imprime);
	}

	public String redirecionarAposExibirNumeroSolicitacao() {
		//Antes de redirecionar, executa as ações pós insert que podem gerar erro
		try {
			imprimirEtiquetas();
			//gerando a pendência de asssinatura digital para o arquivo de solicitação de exames
			boolean origemAmbulatorio = isPaginaChamadoraAtendimentoAmbulatorio();
			if(!origemAmbulatorio){
				this.gerarSolicitacaoExamePendenciaAssinaturaDigital(solicitacaoExame
						.getSolicitacaoExameSeq());
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		if(solicitacaoExame.getImprimiuTicketPaciente()) {

			visualizarTicketPacienteController.setCodSolicitacao(solicitacaoExame.getSeqSolicitacaoSalva());
			visualizarTicketPacienteController.setPaginaChamadora(getPaginaChamadora());
			return PAGE_EXIBIR_TICKET_PACIENTE;

		} else {
			return cancelar();
		}
	}

	private void imprimirEtiquetas() throws BaseException {
		List<ConfirmacaoImpressaoEtiquetaVO> listaConfirmacoesImpressora = new LinkedList<>();
		listaConfirmacoesImpressora.add(confirmacaoImpressaoEtiquetaVO);
		listaConfirmacoesImpressora.add(confirmacaoImpressaoEtiquetaRedomeVO);
		List<String> respostaImpressoras = null;
		AghUnidadesFuncionais unidadeExecutora = this.identificarUnidadeExecutoraController.getUnidadeExecutora();
		respostaImpressoras = this.solicitacaoExameFacade.executarValidacoesPosGravacaoSolicitacaoExame(
				solicitacaoExame, nomeMicro, new Date(), listaConfirmacoesImpressora, unidadeExecutora);
		for (String respostaImpressora : respostaImpressoras) {
			if (!SEM_IMPRESSAO.equals(respostaImpressora)) { // se retornar null usuario nao confirmou a impressao
				if (respostaImpressora != null) {
					this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_ETIQUETAS_IMPRESSAS_SUCESSO_REIMPRIMIR_ETIQUETA,
							respostaImpressora);
				} else {
					this.apresentarMsgNegocio(Severity.INFO, ERRO_IMPRESSORA_ETIQUETA_NAO_ENCONTRADA);
				}
			}
		}
	}

	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(
			String objPesquisa) {
		return this.returnSGWithCount(this.solicitacaoExameFacade
				.buscarUnidadeFuncionais(objPesquisa), this
				.buscarUnidadeFuncionaisCount(objPesquisa));
	}

	public Long buscarUnidadeFuncionaisCount(String objPesquisa) {
		return solicitacaoExameFacade
				.buscarUnidadeFuncionaisCount((String) objPesquisa);
	}

	public List<RapServidores> buscarServidoresSolicitacaoExame(String objPesquisa) {
		return  this.returnSGWithCount(solicitacaoExameFacade.buscarServidoresSolicitacaoExame(objPesquisa), this.buscarServidoresSolicitacaoExameCount(objPesquisa));
	}

	public Long buscarServidoresSolicitacaoExameCount(String objPesquisa) {
		return solicitacaoExameFacade.buscarServidoresSolicitacaoExameCount(objPesquisa);
	}

	public AghUnidadesFuncionais obterUnidadeTrabalho(){
		AghUnidadesFuncionais unidadeTrabalho = null;
		if (this.includeUnidadeTrabalho) {
			unidadeTrabalho = this.identificarUnidadeExecutoraController.getUnidadeExecutora();
		}		
		
		return unidadeTrabalho;
	}
	
	private boolean verificarImpressaoEtiquetaRedome() throws ApplicationBusinessException, UnknownHostException {
		String nomeImpressora = solicitacaoExameFacade.obterNomeImpressoraEtiquetasRedome(getEnderecoRedeHostRemoto());
		if (nomeImpressora == null || nomeImpressora.isEmpty()) {
			return false;
		}
		confirmacaoImpressaoEtiquetaRedomeVO.setConfirmaImpressao(Boolean.TRUE);
		return true;
	}

	public boolean verificarSeImprimeEtiquetaRedome() throws ApplicationBusinessException, UnknownHostException {
		return temExameRedome && verificarImpressaoEtiquetaRedome();
	}

	public void confirmarImpressaoEtiquetaRedome(Boolean imprime) {
		confirmacaoImpressaoEtiquetaRedomeVO.setConfirmaImpressao(confirmacaoImpressaoEtiquetaRedomeVO
				.getConfirmaImpressao() && imprime);
	}
	
	public void setSolicitacaoExame(SolicitacaoExameVO solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}

	public SolicitacaoExameVO getSolicitacaoExame() {
		return solicitacaoExame;
	}

	public void setPaginaChamadora(String paginaChamadora) {
		this.paginaChamadora = paginaChamadora;
	}

	public String getPaginaChamadora() {
		return paginaChamadora;
	}

	public Boolean getIncludeUnidadeTrabalho() {
		return includeUnidadeTrabalho;
	}

	public void setIncludeUnidadeTrabalho(Boolean includeUnidadeTrabalho) {
		this.includeUnidadeTrabalho = includeUnidadeTrabalho;
	}

	public void setListaItemSolicitacaoExame(
			List<ItemSolicitacaoExameVO> listaItemSolicitacaoExame) {
		this.listaItemSolicitacaoExame = listaItemSolicitacaoExame;
	}

	public List<ItemSolicitacaoExameVO> getListaItemSolicitacaoExame() {
		return listaItemSolicitacaoExame;
	}

	private List<TicketExamesPacienteVO> colecao = new ArrayList<TicketExamesPacienteVO>(0);

	@Override
	public Collection<TicketExamesPacienteVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/exames/report/relatorioSolicitacaoExamesCertificacaoDigital.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_SOLICITAR_EXAME");
		params.put("tituloRelatorio", "Solicitação de Exames");

		return params;
	}

	public void gerarSolicitacaoExamePendenciaAssinaturaDigital(Integer codSolicitacao) {
		try {
			this.colecao = examesFacade
					.pesquisarRelatorioSolicitacaoExamesCertificacaoDigital(codSolicitacao);

			if (colecao == null) {
				return;
			}
			gerarDocumento();
		} catch (Exception e) {
			LOG.error("Erro", e);
		}
	}

	public Boolean getRenderPorExame() {
		return POR_EXAME.equals(this.getCurrentTabName());
	}

	public Boolean getRenderPorLote() {
		return POR_LOTE.equals(this.getCurrentTabName());
	}

	public String getPorExame() {
		return POR_EXAME;
	}

	public String getPorLote() {
		return POR_LOTE;
	}

	public Boolean getOrigemAmbulatorio() {
		return origemAmbulatorio;
	}

	public void setOrigemAmbulatorio(Boolean origemAmbulatorio) {
		this.origemAmbulatorio = origemAmbulatorio;
	}

	public void setRespostas(List<AelRespostaQuestao> respostas) {
		this.respostas = respostas;
	}

	public List<AelRespostaQuestao> getRespostas() {
		return respostas;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getAtendimentoDiversoSeq() {
		return atendimentoDiversoSeq;
	}

	public void setAtendimentoDiversoSeq(Integer atendimentoDiversoSeq) {
		this.atendimentoDiversoSeq = atendimentoDiversoSeq;
	}

	public String getCurrentTabName() {
		return currentTabName;
	}

	public void setCurrentTabName(String currentTabName) {
		this.currentTabName = currentTabName;
	}

	public Boolean getExisteUnidadeFuncional() {
		return existeUnidadeFuncional;
	}

	public void setExisteUnidadeFuncional(Boolean existeUnidadeFuncional) {
		this.existeUnidadeFuncional = existeUnidadeFuncional;
	}

	public String getAbaDestino() {
		return abaDestino;
	}

	public void setAbaDestino(String abaDestino) {
		this.abaDestino = abaDestino;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getVoltarEmergencia() {
		return voltarEmergencia;
	}

	public void setVoltarEmergencia(Boolean voltarEmergencia) {
		this.voltarEmergencia = voltarEmergencia;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public VisualizarTicketPacienteController getVisualizarTicketPacienteController() {
		return visualizarTicketPacienteController;
	}

	public void setVisualizarTicketPacienteController(
			VisualizarTicketPacienteController visualizarTicketPacienteController) {
		this.visualizarTicketPacienteController = visualizarTicketPacienteController;
	}

	public ImprimeInformacoesComplementaresController getImprimeInformacoesComplementaresController() {
		return imprimeInformacoesComplementaresController;
	}

	public void setImprimeInformacoesComplementaresController(
			ImprimeInformacoesComplementaresController imprimeInformacoesComplementaresController) {
		this.imprimeInformacoesComplementaresController = imprimeInformacoesComplementaresController;
	}

	public boolean isUsuarioEstudanteMedicina() {
		return usuarioEstudanteMedicina;
	}

	public void setUsuarioEstudanteMedicina(boolean usuarioEstudanteMedicina) {
		this.usuarioEstudanteMedicina = usuarioEstudanteMedicina;
	}

	public boolean isInibeSolicExame() {
		return inibeSolicExame;
	}

	public void setInibeSolicExame(boolean inibeSolicExame) {
		this.inibeSolicExame = inibeSolicExame;
	}

	public IdentificarUnidadeExecutoraController getIdentificarUnidadeExecutoraController() {
		return identificarUnidadeExecutoraController;
	}

	public void setIdentificarUnidadeExecutoraController(
			IdentificarUnidadeExecutoraController identificarUnidadeExecutoraController) {
		this.identificarUnidadeExecutoraController = identificarUnidadeExecutoraController;
	}

	public boolean isUsuarioSolicExameProtocoloEnfermagem() {
		return usuarioSolicExameProtocoloEnfermagem;
	}

	public void setUsuarioSolicExameProtocoloEnfermagem(
			boolean usuarioSolicExameProtocoloEnfermagem) {
		this.usuarioSolicExameProtocoloEnfermagem = usuarioSolicExameProtocoloEnfermagem;
	}

	public boolean isHabilitaBotaoAgendamento() {
		return habilitaBotaoAgendamento;
	}

	public void setHabilitaBotaoAgendamento(boolean habilitaBotaoAgendamento) {
		this.habilitaBotaoAgendamento = habilitaBotaoAgendamento;
	}

	public String getPageSolicitacaoExame() {
		return PAGE_SOLICITACAO_EXAME;
	}

	public String getPagePesquisarAtendPaciente() {
		return PAGE_PESQUISAR_EXAMES;
	}

	public List<ExameAgendamentoMesmoHorarioVO> getListaDifAgendaPaciente() {
		return listaDifAgendaPaciente;
	}

	public void setListaDifAgendaPaciente(List<ExameAgendamentoMesmoHorarioVO> listaDifAgendaPaciente) {
		this.listaDifAgendaPaciente = listaDifAgendaPaciente;
	}

	public boolean isUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(boolean usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public ListarAmostrasSolicitacaoRecebimentoController getListarAmostrasSolicitacaoRecebimentoController() {
		return listarAmostrasSolicitacaoRecebimentoController;
	}

	public void setListarAmostrasSolicitacaoRecebimentoController(
			ListarAmostrasSolicitacaoRecebimentoController listarAmostrasSolicitacaoRecebimentoController) {
		this.listarAmostrasSolicitacaoRecebimentoController = listarAmostrasSolicitacaoRecebimentoController;
	}

	public Boolean isTemExameRedome() {
		return temExameRedome;
	}

	public void setTemExameRedome(Boolean temExameRedome) {
		this.temExameRedome = temExameRedome;
	}

	public ConfirmacaoImpressaoEtiquetaVO getConfirmacaoImpressaoEtiquetaRedomeVO() {
		return confirmacaoImpressaoEtiquetaRedomeVO;
	}

	public void setConfirmacaoImpressaoEtiquetaRedomeVO(
			ConfirmacaoImpressaoEtiquetaVO confirmacaoImpressaoEtiquetaRedomeVO) {
		this.confirmacaoImpressaoEtiquetaRedomeVO = confirmacaoImpressaoEtiquetaRedomeVO;
	}
	
	public List<AgendamentoExameVO> getExamesAgendamentoSelecao() {
		return this.examesAgendamentoSelecao;
	}
	
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}
	
	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public boolean isOrigemInternacao() {
		return isOrigemInternacao;
	}

	public void setOrigemInternacao(boolean isOrigemInternacao) {
		this.isOrigemInternacao = isOrigemInternacao;
	}
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	public Integer getCodProntuario() {
		return codProntuario;
	}
	public void setCodProntuario(Integer codProntuario) {
		this.codProntuario = codProntuario;
	}
	
	public Boolean getForcarGeracaoPendente() {
		return forcarGeracaoPendente;
	}
	
	public void setForcarGeracaoPendente(Boolean forcarGeracaoPendente) {
		this.forcarGeracaoPendente = forcarGeracaoPendente;
	}

	public Boolean getIsSolicitarExame() {
		return isSolicitarExame;
	}

	public void setIsSolicitarExame(Boolean isSolicitarExame) {
		this.isSolicitarExame = isSolicitarExame;
	}

	public boolean isDesabilitaSugestionUnidadeSolicitante() {
		return desabilitaSugestionUnidadeSolicitante;
	}

	public void setDesabilitaSugestionUnidadeSolicitante(
			boolean desabilitaSugestionUnidadeSolicitante) {
		this.desabilitaSugestionUnidadeSolicitante = desabilitaSugestionUnidadeSolicitante;
	}
    
    public void validarUnidadeFuncionalColeta() throws ApplicationBusinessException{
		examesFacade.pesquisarUnidadeFuncionalColeta();
	}
}