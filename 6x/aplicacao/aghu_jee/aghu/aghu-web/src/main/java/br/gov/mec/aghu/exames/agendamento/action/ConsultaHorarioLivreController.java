package br.gov.mec.aghu.exames.agendamento.action;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoFacadeBean;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.GradeHorarioExtraVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class ConsultaHorarioLivreController extends ActionController {

	private static final String VISUALIZAR_EXAMES_AGENDADOS_PACIENTE = "visualizarExamesAgendadosPaciente";
	private static final String SOLICITACAO_EXAME_CRUD = "solicitacaoExameCRUD";
	private static final String LISTAR_EXAMES_AGENDAMENTO_SELECAO = "listarExamesAgendamentoSelecao";
	private static final String AMBULATORIO_ATENDER_PACIENTES_AGENDADOS = "ambulatorio-atenderPacientesAgendados";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ConsultaHorarioLivreController.class);
	private static final long serialVersionUID = -7644960452717330801L;

	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;

	@EJB
	private IAgendamentoFacadeBean agendamentoFacadeBean;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPermissionService permissionService;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private VisualizarExamesAgendadosPacienteController visualizarExamesAgendadosPacienteController;

	private List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO;
	private Date data;
	private Date hora;
	private AghUnidadesFuncionais unidadeExecutora;
	private Integer grade;
	private AelGrupoExames grupoExame;
	private AelSalasExecutorasExames salaExecutoraExame;
	private RapServidores servidor;
	private String sigla;
	private Integer matExame;
	private Short unfExame;
	private Date dtReativacao;
	private Integer soeSeq;
	private Short seqp;
	private List<VAelHrGradeDispVO> listaHorarios;
	private Integer idSelecionado;
	private VAelHrGradeDispVO vAelHrGradeDispVO;
	private ItemHorarioAgendadoVO itemHorarioAgendadoVO;
	private List<AelItemSolicitacaoExames> listaItemSolicitacaoExame;
	private Short unfExecutora;
	private Short seqpSelecionado;
	private AipPacientes paciente;
	private Integer conNumero;
	private List<AgendamentoExameVO> examesAgendamentoSelecao;
	private List<AgendamentoExameVO> examesSelecionados;
	private String possuiAgendamento;
	private String possuiAgendamentoAmbulatorio;
	private Boolean primeiraPesquisa = false;
	private AelTipoMarcacaoExame tipoMarcacaoExame;
	private GradeHorarioExtraVO gradeHorarioExtra;
	private Date dataHora;
	private String cameFrom;
	private String origem;
	private Boolean exibeComponentes;
	private Boolean examePendente;
	private String agendamento;
	private String agendamentoAmbulatorio;
	private List<ItemHorarioAgendadoVO> listaAgendamento;
	private Boolean exibeModalConfirmacaoHorario;
	private Boolean executouIdentificacaoGrupoExames;
	private Boolean permitirHorarioExtra;
	private boolean agendaAmostraComum;
	private String indexToggle ;
	private String alturaHorarios = "150";
	private boolean origemSolicitacao = false;


	private enum ConsultaHorarioLivreControllerExceptionCode implements BusinessExceptionCode {
		AEL_00523, SELECAO_EXAME_JA_AGENDADO;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String carregarContexto() {
		this.indexToggle = "0,1";
		examesSelecionados = this.agendamentoExamesFacade.obterExamesSelecionados(examesAgendamentoSelecao);

		// Caso um exame tenha amostras em comum com outros exames, todos devem
		// ser agendados em conjunto.
		agendaAmostraComum = agendamentoExamesFacade.verificarExistenciaAmostrasComum(examesSelecionados);
		
		this.possuiAgendamento = WebUtil.initLocalizedMessage("PACIENTE_POSSUI_AGENDAMENTO", null, (Object[]) null);
		if (examesSelecionados != null && !examesSelecionados.isEmpty()) {
			AgendamentoExameVO agendamentoExameVO = examesSelecionados.get(0);

			if (agendamentoExameVO.getItemExame() != null
					&& agendamentoExameVO.getItemExame().getSolicitacaoExame() != null) {
				this.soeSeq = agendamentoExameVO.getItemExame().getSolicitacaoExame().getSeq();
			}
			if (agendamentoExameVO.getItemExame() != null
					&& agendamentoExameVO.getItemExame().getSolicitacaoExame() != null
					&& agendamentoExameVO.getItemExame().getSolicitacaoExame().getAtendimento() != null) {
				if (agendamentoExameVO.getItemExame().getSolicitacaoExame().getAtendimento() != null) {
					AghAtendimentos atendimento = agendamentoExameVO.getItemExame().getSolicitacaoExame()
							.getAtendimento();
					paciente = atendimento.getPaciente();
					if (atendimento.getConsulta() != null) {
						conNumero = atendimento.getConsulta().getNumero();
					}
				} else {
					if (agendamentoExameVO.getItemExame().getSolicitacaoExame().getAtendimentoDiverso() != null) {
						AelAtendimentoDiversos atendimentoDiverso = agendamentoExameVO.getItemExame()
								.getSolicitacaoExame().getAtendimentoDiverso();
						paciente = atendimentoDiverso.getAipPaciente();
					}
				}
			}
			if (agendamentoExameVO.getItemExame().getUnidadeFuncional() != null) {
				unidadeExecutora = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.getUnfExecutora());
			}
		}

		if (!cameFrom.equals(VISUALIZAR_EXAMES_AGENDADOS_PACIENTE)) {
			listaItemHorarioAgendadoVO = new ArrayList<ItemHorarioAgendadoVO>();
			AgendamentoExameVO agendamentoExameVOAux = examesSelecionados.get(0);
			dtReativacao = agendamentoExameVOAux.getDthrReativacao();
			for (AgendamentoExameVO agendamentoExameVO : examesSelecionados) {
				if (DateUtil.validaDataMaior(agendamentoExameVO.getDthrReativacao(), dtReativacao)) {
					dtReativacao = agendamentoExameVO.getDthrReativacao();
				}
				ItemHorarioAgendadoVO itemHorarioAgendadoVO = new ItemHorarioAgendadoVO();
				if (agendamentoExameVO.getItemExame().getAelUnfExecutaExames() != null) {
					// itemHorarioAgendadoVO.setSelecionado(Boolean.FALSE);
					itemHorarioAgendadoVO.setIdentificadoAgendamentoExameEmGrupo(Boolean.FALSE);
					itemHorarioAgendadoVO.setUnfExecutaExamesId(agendamentoExameVO.getItemExame()
							.getAelUnfExecutaExames().getId());
					if (paciente != null) {
						Long examesAgendados = this.agendamentoExamesFacade
								.obterExamesAgendamentosPaciente(paciente.getCodigo(), agendamentoExameVO
										.getItemExame().getAelUnfExecutaExames().getId());
						if (agendamentoExameVO.getDthrAgenda() == null && examesAgendados > 0) {
							itemHorarioAgendadoVO.setPacCodigo(paciente.getCodigo());
							itemHorarioAgendadoVO.setVisualizaExames(true);
						}
					}
				}
				itemHorarioAgendadoVO.setSeqp(agendamentoExameVO.getItemExame().getId().getSeqp());
				if (agendamentoExameVO.getItemExame().getSolicitacaoExame() != null) {
					itemHorarioAgendadoVO.setSoeSeq(agendamentoExameVO.getItemExame().getSolicitacaoExame().getSeq());
				}
				if (agendamentoExameVO.getItemExame().getExame() != null) {
					itemHorarioAgendadoVO.setDescricaoExame(agendamentoExameVO.getItemExame().getExame()
							.getDescricaoUsual());
					itemHorarioAgendadoVO.setSigla(agendamentoExameVO.getItemExame().getExame().getSigla());
				}
				if (agendamentoExameVO.getItemExame().getMaterialAnalise() != null) {
					itemHorarioAgendadoVO.setSeqMaterialAnalise(agendamentoExameVO.getItemExame().getMaterialAnalise()
							.getSeq());
					itemHorarioAgendadoVO.setDescricaoMaterialAnalise(agendamentoExameVO.getItemExame()
							.getMaterialAnalise().getDescricao());
				}
				if (agendamentoExameVO.getItemExame().getUnidadeFuncional() != null) {
					itemHorarioAgendadoVO.setSeqUnidade(agendamentoExameVO.getItemExame().getUnidadeFuncional()
							.getSeq());
					itemHorarioAgendadoVO.setDescricaoUnidade(agendamentoExameVO.getItemExame().getUnidadeFuncional()
							.getDescricao());
				}
				if (agendamentoExameVO.getItemExame().getSituacaoItemSolicitacao() != null) {
					itemHorarioAgendadoVO.setDescricaoSituacao(agendamentoExameVO.getItemExame()
							.getSituacaoItemSolicitacao().getDescricao());
					itemHorarioAgendadoVO.setCodigoSituacao(agendamentoExameVO.getItemExame()
							.getSituacaoItemSolicitacao().getCodigo());
				}

				listaItemHorarioAgendadoVO.add(itemHorarioAgendadoVO);
			}
		} else {
			for (ItemHorarioAgendadoVO itemHorarioAgendadoVOCurrent : listaItemHorarioAgendadoVO) {
				if (itemHorarioAgendadoVOCurrent.getCodigoSituacao().equals(
						DominioSituacaoItemSolicitacaoExame.AC.toString())
						|| itemHorarioAgendadoVOCurrent.getCodigoSituacao().equals(
								DominioSituacaoItemSolicitacaoExame.AX.toString())) {
					itemHorarioAgendadoVOCurrent.setVisualizaExames(Boolean.TRUE);
				} else {
					itemHorarioAgendadoVOCurrent.setVisualizaExames(Boolean.FALSE);
				}
			}
		}

		Boolean visualizaExamesAgendados = selecionarItemVisualizarExamesAgendados();

		exibeModalConfirmacaoHorario = Boolean.FALSE;
		executouIdentificacaoGrupoExames = Boolean.FALSE;

		if (visualizaExamesAgendados && this.getCameFrom() != VISUALIZAR_EXAMES_AGENDADOS_PACIENTE) {
			visualizarExamesAgendadosPacienteController.setListaItemHorarioAgendadoVO(listaItemHorarioAgendadoVO);
			return VISUALIZAR_EXAMES_AGENDADOS_PACIENTE;
		}
		return null;
	}

	private Boolean selecionarItemVisualizarExamesAgendados() {
		Boolean visualizaExamesAgendados = Boolean.FALSE;
		if (!listaItemHorarioAgendadoVO.isEmpty()) {
			if (permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "agendarExame",
					VISUALIZAR_EXAMES_AGENDADOS_PACIENTE)) {
				for (ItemHorarioAgendadoVO item : listaItemHorarioAgendadoVO) {
					if (item.getVisualizaExames() != null && item.getVisualizaExames()) {
						atualizarModalPendencia();
						seqpSelecionado = item.getSeqp();
						atribuirItemHorarioAgendado();
						visualizaExamesAgendados = Boolean.TRUE;
						break;
					}
				}
			}
			if (!visualizaExamesAgendados) {
				// Seleciona o primeiro exame listado
				seqpSelecionado = listaItemHorarioAgendadoVO.get(0).getSeqp();
				atribuirItemHorarioAgendado();
			}
		}

		if (cameFrom.equals(SOLICITACAO_EXAME_CRUD) || this.agendamentoAmbulatorio != null) {
			this.exibeComponentes = false;
			this.agendamentoAmbulatorio = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(
					paciente.getCodigo(), true);
			this.agendamento = null;
			this.pesquisarAgendamentosPacientePorDatas(paciente.getCodigo());
		} else {
			this.exibeComponentes = true;
			this.agendamento = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(paciente, false);
			this.agendamentoAmbulatorio = null;
		}

		return visualizaExamesAgendados;
	}

	public void pesquisarAgendamentosPacientePorDatas(Integer pacCodigo) {
		List<Date> listaDatas = agendamentoExamesFacade.pesquisarSugestaoAgendamentoPorPaciente(paciente.getCodigo(),
				true);
		Date primeiraData = null;
		Date segundaData = null;
		if (listaDatas != null && listaDatas.size() == 2) {
			primeiraData = listaDatas.get(0);
			segundaData = listaDatas.get(1);
		} else if (listaDatas != null && listaDatas.size() == 1) {
			primeiraData = listaDatas.get(0);
		}
		this.setListaAgendamento(agendamentoExamesFacade.pesquisarAgendamentoPacientePorDatas(pacCodigo, primeiraData,
				segundaData));
	}

	public void verificarSelecaoExames() {
		if (agendamentoExamesFacade.validarAgendamentoExamesEmGrupo(examesAgendamentoSelecao,
				vAelHrGradeDispVO.getGrupoExame(), this.seqpSelecionado)) {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SELECAO_EXAMES_AGENDAMENTO_PARCIAL");
		}
	}

	public String voltar() {
		this.examePendente = false;
		if (this.getOrigem() != null) {
			return this.getOrigem();
		} else {
			return LISTAR_EXAMES_AGENDAMENTO_SELECAO;
		}
	}

	public String voltarSolicitacaoExame() {
		return AMBULATORIO_ATENDER_PACIENTES_AGENDADOS;
	}

	public String verificarExamesPendentes() {
		this.examePendente = false;

		for (ItemHorarioAgendadoVO item : listaItemHorarioAgendadoVO) {
			if (item.getItemHorarioAgendadoId() == null) {
				this.examePendente = true;
			}
		}
		return this.voltar();
	}

	public void pesquisar() {
		try {
			this.indexToggle = "2";
			this.alturaHorarios = "400";
			this.examePendente = false;
			this.vAelHrGradeDispVO = null;
			this.listaHorarios = this.agendamentoExamesFacade.pesquisarHorariosLivresParaExame(
					itemHorarioAgendadoVO.getSigla(), itemHorarioAgendadoVO.getSeqMaterialAnalise(),
					itemHorarioAgendadoVO.getSeqUnidade(), dtReativacao, itemHorarioAgendadoVO.getSoeSeq(),
					itemHorarioAgendadoVO.getSeqp(), data, hora, grade, grupoExame, salaExecutoraExame, servidor);
			if (listaHorarios != null && listaHorarios.size() > 0) {
				this.vAelHrGradeDispVO = listaHorarios.get(0);
				this.agendamentoExamesFacade.identificarAgendamentoExamesEmGrupo(vAelHrGradeDispVO.getGrupoExame(),
						listaItemHorarioAgendadoVO, Boolean.FALSE);
				// executouIdentificacaoGrupoExames = Boolean.TRUE;
				idSelecionado = vAelHrGradeDispVO.getId();
				verificarSelecaoExames();
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_HORARIO_LIVRE_NAO_ENCONTRADO");
			}
			executouIdentificacaoGrupoExames = Boolean.TRUE;
			primeiraPesquisa = true;
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
    public void alteraAba(TabChangeEvent event) {
    	this.ajustaAlturaHorarios();
    }
    
    public void fechaAba(TabCloseEvent event) {
    	this.ajustaAlturaHorarios();
    }
    
    private void ajustaAlturaHorarios(){
    	if ("2".equals(indexToggle)){
    		this.alturaHorarios = "400"; 
    	}else if (indexToggle.contains("0") && indexToggle.contains("1") && indexToggle.contains("2")){
    		this.alturaHorarios = "75";
    	}else{
    		this.alturaHorarios = "230";
    	}
    	
    }

	// Para permanecer na tela é necessário que ainda exista algum item não
	// agendado.
	// Marca como selecionado o primeiro item ainda não agendado
	private boolean verificarExistenciaExamesNaoAgendados() {
		for (ItemHorarioAgendadoVO item : listaItemHorarioAgendadoVO) {
			if (item.getItemHorarioAgendadoId() == null) {
				item.setSelecionado(Boolean.TRUE);
				seqpSelecionado = item.getSeqp();
				this.itemHorarioAgendadoVO = item;
				return true;
			}
		}
		return false;
	}

	public void atribuirItemHorarioAgendado() {
		this.limparPesquisa();
		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			if (itemHorarioAgendadoVO.getSeqp().equals(seqpSelecionado)) {
				itemHorarioAgendadoVO.setSelecionado(Boolean.TRUE);
				this.itemHorarioAgendadoVO = itemHorarioAgendadoVO;
			} else {
				itemHorarioAgendadoVO.setSelecionado(Boolean.FALSE);
			}
		}
	}

	public void atribuirHorario() {
		for (VAelHrGradeDispVO vAelHrGradeDispVO : listaHorarios) {
			if (vAelHrGradeDispVO.getId().equals(idSelecionado)) {
				this.vAelHrGradeDispVO = vAelHrGradeDispVO;
				verificarSelecaoExames();
				this.agendamentoExamesFacade.identificarAgendamentoExamesEmGrupo(
						this.vAelHrGradeDispVO.getGrupoExame(), this.listaItemHorarioAgendadoVO, Boolean.FALSE);
				break;
			}
		}
	}

	public void limparModalHorarioExtra() {
		this.limparCamposModalHorarioExtra();
		this.pesquisar();
	}

	public void limparCamposModalHorarioExtra() {
		this.gradeHorarioExtra = null;
		this.dataHora = null;
		this.tipoMarcacaoExame = null;
	}

	// Retorna lista de Itens registros selecionados ou toda a lista de exames
	// caso estes tenham amostras em comum
	private List<ItemHorarioAgendadoVO> buscarHorariosSelecionados() {
		List<ItemHorarioAgendadoVO> listaItemHorarioSelecionado = new ArrayList<ItemHorarioAgendadoVO>();

		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			if (itemHorarioAgendadoVO.getSelecionado() || agendaAmostraComum) {
				AelItemSolicitacaoExames itemSolicitacaoExameOriginal = agendamentoExamesFacade
						.obterItemSolicitacaoExameOriginal(itemHorarioAgendadoVO.getSoeSeq(),
								itemHorarioAgendadoVO.getSeqp());
				itemSolicitacaoExameOriginal.setOrigemTelaSolicitacao(origemSolicitacao);
				itemHorarioAgendadoVO.setItemSolicitacaoExameOriginal(itemSolicitacaoExameOriginal);
				listaItemHorarioSelecionado.add(itemHorarioAgendadoVO);
			}
		}

		return listaItemHorarioSelecionado;
	}

	public String gravarHorario() {
		this.examePendente = false;
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}

		try {
			this.agendamentoFacadeBean.gravarHorario(this.buscarHorariosSelecionados(), vAelHrGradeDispVO,
					nomeMicrocomputador, examesAgendamentoSelecao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_HORARIO_EXAME");
			this.listaItemHorarioAgendadoVO = this.agendamentoExamesFacade.atualizarListaItemHorarioAgendadoVO(
					listaItemHorarioAgendadoVO, vAelHrGradeDispVO.getGrade(), vAelHrGradeDispVO.getSeqGrade(),
					vAelHrGradeDispVO.getDescricaoSala(), soeSeq);
			if (paciente != null) {
				this.atualizaVisualizarExamesAgendadosPaciente();
				if (cameFrom.equals(SOLICITACAO_EXAME_CRUD) || this.agendamentoAmbulatorio != null) {
					this.pesquisarAgendamentosPacientePorDatas(paciente.getCodigo());
					this.agendamentoAmbulatorio = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(
							paciente.getCodigo(), true);
					this.agendamento = null;
				} else {
					this.agendamento = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(
							paciente.getCodigo(), false);
					this.agendamentoAmbulatorio = null;
				}
			}
		} catch (BaseException e) {
			examesAgendamentoSelecao = this.agendamentoFacadeBean
					.reatacharListaExamesAgendamentoSelecao(examesAgendamentoSelecao);
			if (e.getCode().toString()
					.equals(ConsultaHorarioLivreControllerExceptionCode.SELECAO_EXAME_JA_AGENDADO.toString())) {
				exibeModalConfirmacaoHorario = Boolean.TRUE;
			} else {
				this.apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
		}

		agendaAmostraComum = false;
		if (this.verificarExistenciaExamesNaoAgendados()) {
			this.limparPesquisa();
			return null;
		} else {
			return LISTAR_EXAMES_AGENDAMENTO_SELECAO;
		}
	}

	public String gravarAgendaHorarioExtra() {
		this.examePendente = false;
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			Date dataHoraExtra = this.agendamentoExamesFacade.gravarHorarioExtra(this.buscarHorariosSelecionados(),
					gradeHorarioExtra, unfExecutora, dataHora, tipoMarcacaoExame, nomeMicrocomputador);
			// this.agendamentoExamesFacade.flush();
			SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_HORARIO_EXAME_EXTRA",
					formatoDataHora.format(dataHoraExtra));

			this.listaItemHorarioAgendadoVO = this.agendamentoExamesFacade.atualizarListaItemHorarioAgendadoVO(
					listaItemHorarioAgendadoVO, gradeHorarioExtra.getGrade(), gradeHorarioExtra.getSeqGrade(),
					gradeHorarioExtra.getNumSala(), soeSeq);
			if (paciente != null) {
				this.atualizaVisualizarExamesAgendadosPaciente();
				if (cameFrom.equals(SOLICITACAO_EXAME_CRUD) || this.agendamentoAmbulatorio != null) {
					this.pesquisarAgendamentosPacientePorDatas(paciente.getCodigo());
					this.agendamentoAmbulatorio = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(
							paciente.getCodigo(), true);
					this.agendamento = null;
				} else {
					this.agendamento = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(
							paciente.getCodigo(), false);
					this.agendamentoAmbulatorio = null;
				}
			}

			limparCamposModalHorarioExtra();

			if (this.verificarExistenciaExamesNaoAgendados()) {
				this.limparPesquisa();
				return null;
			} else {
				return LISTAR_EXAMES_AGENDAMENTO_SELECAO;
			}
		} catch (BaseException e) {
			if (e.getCode().toString()
					.equals(ConsultaHorarioLivreControllerExceptionCode.SELECAO_EXAME_JA_AGENDADO.toString())) {
				exibeModalConfirmacaoHorario = Boolean.TRUE;
			} else {
				this.apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
		}

		return null;
	}

	public void confirmarGravacaoHorario() {
		String nomeMicrocomputador = null;
		Date dataHoraExtra = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção capturada:", e);
		}
		try {
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal = agendamentoExamesFacade
					.obterItemSolicitacaoExameOriginal(itemHorarioAgendadoVO.getSoeSeq(),
							itemHorarioAgendadoVO.getSeqp());
			itemHorarioAgendadoVO.setItemSolicitacaoExameOriginal(itemSolicitacaoExameOriginal);

			if (gradeHorarioExtra != null) {
				dataHoraExtra = agendamentoExamesFacade.obterDataHoraDisponivelParaGradeEUnidadeExecutora(unfExecutora,
						gradeHorarioExtra.getSeqGrade(), dataHora);
				AelHorarioExameDisp horarioExameDisp = agendamentoExamesFacade.montarHorarioExameDisp(dataHoraExtra,
						unfExecutora, gradeHorarioExtra.getSeqGrade(), tipoMarcacaoExame);
				agendamentoExamesFacade.inserirItemHorarioAgendadoExtra(itemHorarioAgendadoVO, horarioExameDisp,
						Boolean.FALSE, Boolean.TRUE, nomeMicrocomputador);
			} else {
				agendamentoExamesFacade.inserirItemHorarioAgendado(itemHorarioAgendadoVO, vAelHrGradeDispVO,
						Boolean.FALSE, Boolean.TRUE, nomeMicrocomputador);
			}

			// Caso seja gravação de hora extra exibe msg de sucesso indicando
			// qual a data/hora gravada.
			if (dataHoraExtra != null) {
				SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_HORARIO_EXAME_EXTRA",
						formatoDataHora.format(dataHoraExtra));
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_HORARIO_EXAME");
			}

			Short unfGrade = null;
			Integer gradeSeqp = null;
			String numSala = null;

			if (gradeHorarioExtra != null) {
				unfGrade = gradeHorarioExtra.getGrade();
				gradeSeqp = gradeHorarioExtra.getSeqGrade();
				numSala = gradeHorarioExtra.getNumSala();
			} else {
				unfGrade = vAelHrGradeDispVO.getGrade();
				gradeSeqp = vAelHrGradeDispVO.getSeqGrade();
				numSala = vAelHrGradeDispVO.getDescricaoSala();
			}
			this.listaItemHorarioAgendadoVO = this.agendamentoExamesFacade.atualizarListaItemHorarioAgendadoVO(
					listaItemHorarioAgendadoVO, unfGrade, gradeSeqp, numSala, soeSeq);
			if (paciente != null) {
				this.atualizaVisualizarExamesAgendadosPaciente();
			}
			exibeModalConfirmacaoHorario = Boolean.FALSE;
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void atualizaVisualizarExamesAgendadosPaciente() {
		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			Long examesAgendados = this.agendamentoExamesFacade
					.obterExamesAgendamentosPaciente(paciente.getCodigo(),
							itemHorarioAgendadoVO.getUnfExecutaExamesId());
			if (itemHorarioAgendadoVO.getItemHorarioAgendadoId() == null && examesAgendados > 0) {
				itemHorarioAgendadoVO.setVisualizaExames(true);
			} else {
				itemHorarioAgendadoVO.setVisualizaExames(false);
			}
		}
	}

	public void atualizarIdentificacaoExamesGrupoHorarioExtra() {
		if (gradeHorarioExtra != null) {
			this.agendamentoExamesFacade.identificarAgendamentoExamesEmGrupo(gradeHorarioExtra.getGrupoExameSeq(),
					listaItemHorarioAgendadoVO, Boolean.TRUE);
		} else {
			agendamentoExamesFacade.desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
		}
	}

	public void limparPesquisa() {
		this.indexToggle = "0,1";
		this.data = null;
		this.hora = null;
		this.grade = null;
		this.grupoExame = null;
		this.salaExecutoraExame = null;
		this.servidor = null;
		this.listaHorarios = null;
		this.vAelHrGradeDispVO = null;
		primeiraPesquisa = false;
		agendamentoExamesFacade.desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
	}

	public List<AelGrupoExames> pesquisarGrupoExame(String parametro) {
		return this.examesFacade.pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(parametro, unidadeExecutora);
	}

	public List<AelSalasExecutorasExames> pesquisarSala(String parametro) {
		return this.examesFacade.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(parametro, unidadeExecutora);
	}

	public List<RapServidores> pesquisarServidor(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidores(parametro);
	}

	private String getNomeMicrocomputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		return nomeMicrocomputador;
	}

	public void cancelarItemHorarioAgendado(ItemHorarioAgendadoVO itemHorarioAgendadoVO) {
		try {

			this.examePendente = false;
			agendamentoExamesFacade.cancelarItemHorarioAgendadoMarcado(
					itemHorarioAgendadoVO.getItemHorarioAgendadoId(), unfExecutora, getNomeMicrocomputador());
			// agendamentoExamesFacade.flush();
			itemHorarioAgendadoVO.setDthrAgenda(null);
			itemHorarioAgendadoVO.setGrade(null);
			itemHorarioAgendadoVO.setSala(null);
			itemHorarioAgendadoVO.setItemHorarioAgendadoId(null);
			// Obtem a nova situacao do exame após o cancelamento
			AelItemSolicitacaoExames itemSolicitacaoExame = examesFacade
					.obterItemSolicitacaoExamePorChavePrimaria(new AelItemSolicitacaoExamesId(itemHorarioAgendadoVO
							.getSoeSeq(), itemHorarioAgendadoVO.getSeqp()));
			itemHorarioAgendadoVO.setCodigoSituacao(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo());
			itemHorarioAgendadoVO
					.setDescricaoSituacao(itemSolicitacaoExame.getSituacaoItemSolicitacao().getDescricao());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CANCELAMENTO_HORARIOS_EXAME");
			atualizaVisualizarExamesAgendadosPaciente();
			if (cameFrom.equals(SOLICITACAO_EXAME_CRUD) || this.agendamentoAmbulatorio != null) {
				this.pesquisarAgendamentosPacientePorDatas(paciente.getCodigo());
				this.agendamentoAmbulatorio = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(
						paciente.getCodigo(), true);
				this.agendamento = null;
			} else {
				this.agendamento = agendamentoExamesFacade.obterSugestaoAgendamentoPorPaciente(paciente.getCodigo(),
						false);
				this.agendamentoAmbulatorio = null;
			}
		} catch (BaseException e) {
			// Necessário tratar msg de erro com parametro, pois em
			// AelHorarioExameDispRN pode lançar excecao com rollback que
			// desatacha o objeto passado por parametro.
			AelItemHorarioAgendado itemHorarioAgendado = agendamentoExamesFacade
					.obterItemHorarioAgendadoPorId(itemHorarioAgendadoVO.getItemHorarioAgendadoId());
			if (e.getCode() == ConsultaHorarioLivreControllerExceptionCode.AEL_00523) {
				this.apresentarMsgNegocio(Severity.ERROR, "AEL_00523", itemHorarioAgendado.getId().getIseSoeSeq());
				LOG.error("Erro", e);
				return;
			}
			apresentarExcecaoNegocio(e);
			LOG.error("Erro", e);
		}
	}

	public String atualizarModalPendencia() {
		this.examePendente = false;
		this.atualizarListaItensPendentesAgendamento();
		return VISUALIZAR_EXAMES_AGENDADOS_PACIENTE;
	}

	private void atualizarListaItensPendentesAgendamento() {
		List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoPendentes = new ArrayList<ItemHorarioAgendadoVO>();

		for (ItemHorarioAgendadoVO itemHorarioAgendado : listaItemHorarioAgendadoVO) {
			if (itemHorarioAgendado.getItemHorarioAgendadoId() == null) {
				listaItemHorarioAgendadoPendentes.add(itemHorarioAgendado);
			}
		}

		visualizarExamesAgendadosPacienteController.setListaItemHorarioAgendadoVO(listaItemHorarioAgendadoPendentes);
	}

	public void fecharModalConfirmaGravacaoHorario() {
		this.exibeModalConfirmacaoHorario = Boolean.FALSE;
	}

	public List<GradeHorarioExtraVO> pesquisarGrade(String parametro) {
		return this.agendamentoExamesFacade.pesquisarGradeHorarioExtra(parametro, this.unfExecutora,
				itemHorarioAgendadoVO.getSigla(), itemHorarioAgendadoVO.getSeqMaterialAnalise(),
				itemHorarioAgendadoVO.getSeqUnidade());
	}

	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(String parametro) {
		return this.examesFacade.pesquisarTipoMarcacaoExame(parametro);
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public AelGrupoExames getGrupoExame() {
		return grupoExame;
	}

	public void setGrupoExame(AelGrupoExames grupoExame) {
		this.grupoExame = grupoExame;
	}

	public AelSalasExecutorasExames getSalaExecutoraExame() {
		return salaExecutoraExame;
	}

	public void setSalaExecutoraExame(AelSalasExecutorasExames salaExecutoraExame) {
		this.salaExecutoraExame = salaExecutoraExame;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getMatExame() {
		return matExame;
	}

	public void setMatExame(Integer matExame) {
		this.matExame = matExame;
	}

	public Short getUnfExame() {
		return unfExame;
	}

	public void setUnfExame(Short unfExame) {
		this.unfExame = unfExame;
	}

	public Date getDtReativacao() {
		return dtReativacao;
	}

	public void setDtReativacao(Date dtReativacao) {
		this.dtReativacao = dtReativacao;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public List<VAelHrGradeDispVO> getListaHorarios() {
		return listaHorarios;
	}

	public void setListaHorarios(List<VAelHrGradeDispVO> listaHorarios) {
		this.listaHorarios = listaHorarios;
	}

	public Integer getIdSelecionado() {
		return idSelecionado;
	}

	public void setIdSelecionado(Integer idSelecionado) {
		this.idSelecionado = idSelecionado;
	}

	public VAelHrGradeDispVO getvAelHrGradeDispVO() {
		return vAelHrGradeDispVO;
	}

	public void setvAelHrGradeDispVO(VAelHrGradeDispVO vAelHrGradeDispVO) {
		this.vAelHrGradeDispVO = vAelHrGradeDispVO;
	}

	public List<AelItemSolicitacaoExames> getListaItemSolicitacaoExame() {
		return listaItemSolicitacaoExame;
	}

	public void setListaItemSolicitacaoExame(List<AelItemSolicitacaoExames> listaItemSolicitacaoExame) {
		this.listaItemSolicitacaoExame = listaItemSolicitacaoExame;
	}

	public List<ItemHorarioAgendadoVO> getListaItemHorarioAgendadoVO() {
		return listaItemHorarioAgendadoVO;
	}

	public void setListaItemHorarioAgendadoVO(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		this.listaItemHorarioAgendadoVO = listaItemHorarioAgendadoVO;
	}

	public Short getUnfExecutora() {
		return unfExecutora;
	}

	public void setUnfExecutora(Short unfExecutora) {
		this.unfExecutora = unfExecutora;
	}

	public ItemHorarioAgendadoVO getItemHorarioAgendadoVO() {
		return itemHorarioAgendadoVO;
	}

	public void setItemHorarioAgendadoVO(ItemHorarioAgendadoVO itemHorarioAgendadoVO) {
		this.itemHorarioAgendadoVO = itemHorarioAgendadoVO;
	}

	public Short getSeqpSelecionado() {
		return seqpSelecionado;
	}

	public void setSeqpSelecionado(Short seqpSelecionado) {
		this.seqpSelecionado = seqpSelecionado;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getPossuiAgendamento() {
		return possuiAgendamento;
	}

	public void setPossuiAgendamento(String possuiAgendamento) {
		this.possuiAgendamento = possuiAgendamento;
	}

	public Boolean getPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}

	public AelTipoMarcacaoExame getTipoMarcacaoExame() {
		return tipoMarcacaoExame;
	}

	public void setTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame) {
		this.tipoMarcacaoExame = tipoMarcacaoExame;
	}

	public GradeHorarioExtraVO getGradeHorarioExtra() {
		return gradeHorarioExtra;
	}

	public void setGradeHorarioExtra(GradeHorarioExtraVO gradeHorarioExtra) {
		this.gradeHorarioExtra = gradeHorarioExtra;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getExibeComponentes() {
		return exibeComponentes;
	}

	public void setExibeComponentes(Boolean exibeComponentes) {
		this.exibeComponentes = exibeComponentes;
	}

	public String getPossuiAgendamentoAmbulatorio() {
		return possuiAgendamentoAmbulatorio;
	}

	public void setPossuiAgendamentoAmbulatorio(String possuiAgendamentoAmbulatorio) {
		this.possuiAgendamentoAmbulatorio = possuiAgendamentoAmbulatorio;
	}

	public Boolean getExamePendente() {
		return examePendente;
	}

	public void setExamePendente(Boolean examePendente) {
		this.examePendente = examePendente;
	}

	public String getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(String agendamento) {
		this.agendamento = agendamento;
	}

	public String getAgendamentoAmbulatorio() {
		return agendamentoAmbulatorio;
	}

	public void setAgendamentoAmbulatorio(String agendamentoAmbulatorio) {
		this.agendamentoAmbulatorio = agendamentoAmbulatorio;
	}

	public List<ItemHorarioAgendadoVO> getListaAgendamento() {
		return listaAgendamento;
	}

	public void setListaAgendamento(List<ItemHorarioAgendadoVO> listaAgendamento) {
		this.listaAgendamento = listaAgendamento;
	}

	public Boolean getExibeModalConfirmacaoHorario() {
		return exibeModalConfirmacaoHorario;
	}

	public void setExibeModalConfirmacaoHorario(Boolean exibeModalConfirmacaoHorario) {
		this.exibeModalConfirmacaoHorario = exibeModalConfirmacaoHorario;
	}

	public Boolean getExecutouIdentificacaoGrupoExames() {
		return executouIdentificacaoGrupoExames;
	}

	public void setExecutouIdentificacaoGrupoExames(Boolean executouIdentificacaoGrupoExames) {
		this.executouIdentificacaoGrupoExames = executouIdentificacaoGrupoExames;
	}

	public List<AgendamentoExameVO> getExamesAgendamentoSelecao() {
		return examesAgendamentoSelecao;
	}

	public void setExamesAgendamentoSelecao(List<AgendamentoExameVO> examesAgendamentoSelecao) {
		this.examesAgendamentoSelecao = examesAgendamentoSelecao;
	}

	public List<AgendamentoExameVO> getExamesSelecionados() {
		return examesSelecionados;
	}

	public void setExamesSelecionados(List<AgendamentoExameVO> examesSelecionados) {
		this.examesSelecionados = examesSelecionados;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public boolean isAgendaAmostraComum() {
		return agendaAmostraComum;
	}

	public void setAgendaAmostraComum(boolean agendaAmostraComum) {
		this.agendaAmostraComum = agendaAmostraComum;
	}

	public Boolean getPermitirHorarioExtra() {
		return permitirHorarioExtra;
	}

	public void setPermitirHorarioExtra(Boolean permitirHorarioExtra) {
		this.permitirHorarioExtra = permitirHorarioExtra;
	}
	
	public String getAlturaHorarios() {
		return alturaHorarios;
	}

	public void setAlturaHorarios(String alturaHorarios) {
		this.alturaHorarios = alturaHorarios;
	}
	
	public String getIndexToggle() {
		return indexToggle;
	}

	public void setIndexToggle(String indexToggle) {
		this.indexToggle = indexToggle;
	}

	
	public boolean isOrigemSolicitacao() {
		return origemSolicitacao;
	}

	
	public void setOrigemSolicitacao(boolean origemSolicitacao) {
		this.origemSolicitacao = origemSolicitacao;
	}
}