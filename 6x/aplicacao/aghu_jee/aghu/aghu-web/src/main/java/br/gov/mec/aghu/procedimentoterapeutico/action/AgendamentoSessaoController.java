package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PercentualOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.protocolo.business.IProtocoloFacade;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AgendamentoSessaoController extends ActionController {

	private static final long serialVersionUID = 6737267605397903908L;

	private static final Log LOG = LogFactory.getLog(AgendamentoSessaoController.class);

	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String PAGE_SUGESTAO_AGENDA = "sugestaoAgenda";
	private static final String PAGE_AGENDAMENTO_SESSAO = "agendamentoSessao";
	private static final String PAGE_IMPRESSAO_TICKET_AGENDAMENTO = "procedimentoterapeutico-impressaoTicketAgendamento";
	private static final String PAGE_CONFIRMAR_CANCELAR_RESERVA = "procedimentoterapeutico-confirmarCancelarReservaHorario";
	private static final String PAGE_LISTA_PACIENTES_ESPERA = "visualizarPacientesListaEspera";
	
	private static final String HIFEN = " - ";
	
	@Inject
	private ImprimirTicketAgendamentoController imprimirTicketAgendamentoController;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IProtocoloFacade protocoloFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private MptTipoSessao tipoSessao;
	private AipPacientes paciente;
	private Integer prescricao;
	private DominioTipoLocal acomodacao;
	private List<MptTurnoTipoSessao> turnos;
	private MptTurnoTipoSessao turnoTipoSessao;
	private Date dataInicio;
	private Date dataFim;
	private MptSalas sala;
	private MpaVersaoProtAssistencial protocolo;
	private Short dia;
	private Date tempo;
	private Short ciclo;
	
	List<PrescricaoPacienteVO> listaPrescricoes;
	PrescricaoPacienteVO prescricaoSelecionada;
	private List<CadIntervaloTempoVO> listaHorarios;
	private CadIntervaloTempoVO itemSelecionado;
	
	List<HorariosAgendamentoSessaoVO> horariosGerados;
	List<HorarioReservadoVO> horariosReservados;
	
	private ScheduleModel horariosSugeridos;
	private Date dataInicialCalendario;
	private String informacoesPaciente;
	
	private Boolean permissaoImprimirTicket = Boolean.FALSE;
	private boolean manutencaoAgendamento;
	private Boolean telaLista = false;
	@Inject
	private VisualizarPacientesListaEsperaController visualizarPacientesListaEsperaController;
	
	private Date dthrHoraInicio;
	private String[] diasSelecionados;
	private List<String> diaSemana;
	
	private String logTentativas;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void inicio() {
		
		this.permissaoImprimirTicket = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(),"impressaoTicketAgendamento", "imprimir");
		
		if (tipoSessao == null) {
			try {
				MptFavoritoServidor favoritoServidor = this.procedimentoTerapeuticoFacade.obterFavoritoPorServidor(
							this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				
				
				
				if (favoritoServidor != null) {
					this.tipoSessao = favoritoServidor.getTipoSessao();
					this.obterTurnos();
					if (favoritoServidor.getSala() != null) {
						this.sala = favoritoServidor.getSala();
					}
				}
			} catch (ApplicationBusinessException e1) {
				apresentarExcecaoNegocio(e1);
			}
			this.acomodacao = DominioTipoLocal.T;
			// O campo “A partir de:” deve vir populado com a data atual + 1.
			this.dataInicio = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 1);
			
			try {
				AghParametros paramDias = this.parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_DATA_FINAL_AGENDAMENTO);
				
				this.dataFim = DateUtil.adicionaDias(this.dataInicio, paramDias.getVlrNumerico().intValue());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		if (this.manutencaoAgendamento) {
			try {
				this.obterListaPrescricoesPorPaciente();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public List<MptTipoSessao> listarTiposSessao(final String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarTiposSessao(strPesquisa),
				this.procedimentoTerapeuticoFacade.listarTiposSessaoCount(strPesquisa));
	}
	
	public void limparSbSalaTurnos() {
		this.sala = null;
		this.turnos = null;
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			this.paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			// #41689
			this.obterListaPrescricoesPorPaciente();
			
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void obterListaPrescricoesPorPaciente() throws ApplicationBusinessException {
		AghParametros paramValidadePrescr = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_VALIDADE_PRECRICAO_QUIMIO);
		
		Integer validadePrescrQuimio =  paramValidadePrescr.getVlrNumerico().intValue();
		Date dataCalculada = DateUtil.adicionaDias(DateUtil.truncaData(new Date()),-validadePrescrQuimio);
		
		this.listaPrescricoes = this.procedimentoTerapeuticoFacade
				.obterListaPrescricoesPorPaciente(this.paciente.getCodigo(), dataCalculada);
		
		this.listaHorarios = new ArrayList<CadIntervaloTempoVO>();
		
		if (!this.listaPrescricoes.isEmpty()) {
			if (this.manutencaoAgendamento) {
				for (PrescricaoPacienteVO vo : this.listaPrescricoes) {
					if (vo.getPteSeq().equals(this.prescricao)) {
						this.prescricaoSelecionada = vo;
						this.carregarListaHorariosPrescricao();
						break;
					}
				}
			} else {
				this.prescricaoSelecionada = this.listaPrescricoes.get(0);
				this.carregarListaHorariosPrescricao();
			}
		}
	}
	
	public void carregarListaHorariosPrescricao() {
		if (this.prescricaoSelecionada != null) {
			this.prescricao = this.prescricaoSelecionada.getPteSeq();
			this.dataInicio = this.prescricaoSelecionada.getDataSugerida();
			this.listaHorarios = this.procedimentoTerapeuticoFacade
					.listarIntervalosTempoPrescricao(this.prescricaoSelecionada.getLote(), this.prescricaoSelecionada.getCloSeq());
			this.ordenarListaHorarios();
		} else {
			this.limparPrescricao();
		}
	}
	
	public void limparListas() {
		this.dataInicio = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 1);
		this.listaPrescricoes = new ArrayList<PrescricaoPacienteVO>();
		this.listaHorarios = new ArrayList<CadIntervaloTempoVO>();
		this.prescricaoSelecionada = null;
		this.prescricao = null;
		this.itemSelecionado = null;
	}
	
	private void limparPrescricao() {
		this.dataInicio = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), 1);
		this.listaHorarios = new ArrayList<CadIntervaloTempoVO>();
		this.prescricaoSelecionada = null;
		this.prescricao = null;
		this.itemSelecionado = null;
	}
	
	public void processarBuscaPacientePorCodigo(Integer codigoPaciente) {
		if (codigoPaciente != null) {
			this.setPaciente(this.pacienteFacade.buscaPaciente(codigoPaciente));
			// #41689
			try {
				this.obterListaPrescricoesPorPaciente();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		} else {
			this.paciente = null;
		}
	}
	
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}
	
	public void obterTurnos() {
		this.turnos = this.procedimentoTerapeuticoFacade.obterTurnosPorTipoSessaoOrdenado(this.tipoSessao.getSeq());
		popularDiasSemana();
	}

	public List<MptSalas> listarSalas(final String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarSalas(this.tipoSessao.getSeq(), strPesquisa),
				this.procedimentoTerapeuticoFacade.listarSalasCount(this.tipoSessao.getSeq(), strPesquisa));
	}
	
	public List<MpaVersaoProtAssistencial> listarProtocolos(final String strPesquisa) {
		return this.returnSGWithCount(this.protocoloFacade.listarProtocolosAssistenciais(strPesquisa),
				this.protocoloFacade.listarProtocolosAssistenciaisCount(strPesquisa));
	}
	
	public void carregarListaHorarios() {
		this.listaHorarios = this.protocoloFacade.listarIntervalosTempo(this.protocolo.getId().getSeqp(), this.protocolo.getId().getPtaSeq());
		this.ordenarListaHorarios();
	}
	
	private void ordenarListaHorarios() {
		Collections.sort(this.listaHorarios, new Comparator<CadIntervaloTempoVO>() {
			@Override
			public int compare(CadIntervaloTempoVO o1, CadIntervaloTempoVO o2) {
				return o1.getDiaReferencia().compareTo(o2.getDiaReferencia());
			}
		});
	}
	
	public void adicionarHorario() {
		if (this.listaHorarios == null) {
			this.listaHorarios = new ArrayList<CadIntervaloTempoVO>();
		}
		for (CadIntervaloTempoVO item : this.listaHorarios) {
			if (item.getDiaReferencia().equals(this.dia)) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_DIA_EXISTENTE_AGENDAR_SESSAO", this.dia);
				return;
			}
		}
		Short diasPeriodo = (short) ((DateUtil.obterQtdDiasEntreDuasDatas(dataInicio, dataFim).shortValue()) + 1);
		if (this.dia.compareTo(diasPeriodo) > 0) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DIA_MAIOR_QUE_INTERVALO_AGENDAR_SESSAO");
			return;
		}
		
		CadIntervaloTempoVO vo = new CadIntervaloTempoVO();
		vo.setDiaReferencia(this.dia);
		vo.setQtdeHoras(this.tempo);
		// Seta o "tempo" em forma de período para posterior utilização.
		vo.setHoraInicioReferencia(DateUtil.truncaData(new Date()));
		vo.setHoraFimReferencia(DateUtil.comporDiaHora(new Date(), this.tempo));
		vo.setCiclo(this.getCiclo());
		vo.setAgendar(true);
		this.listaHorarios.add(vo);
		
		this.ordenarListaHorarios();
		
		this.dia = null;
		this.tempo = null;
	}
	
	public void atualizarTempo(CadIntervaloTempoVO item) {
		this.listaHorarios.remove(item);
		item.setHoraInicioReferencia(DateUtil.truncaData(new Date()));
		item.setHoraFimReferencia(DateUtil.comporDiaHora(new Date(), item.getQtdeHoras()));
		
		this.listaHorarios.add(item);
		
		this.ordenarListaHorarios();
	}
	
	public String agendar() {
		Short vpaPtaSeq = this.protocolo != null ? this.protocolo.getId().getPtaSeq() : null;
		Short vpaSeqp = this.protocolo != null ? this.protocolo.getId().getSeqp() : null;
		DominioTurno turno = this.turnoTipoSessao != null ? this.turnoTipoSessao.getTurno() : null;
		try {
			MptAgendamentoSessao agendamentoSessao = this.procedimentoTerapeuticoFacade.agendarSessao(this.horariosGerados, this.tipoSessao.getSeq(), this.sala.getSeq(), turno,
					this.paciente.getCodigo(), vpaPtaSeq, vpaSeqp, this.acomodacao, this.dataInicio, this.dataFim, getEnderecoRedeHostRemoto());
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AGENDAMENTO_AGENDAR_SESSAO");
			
			if (this.prescricao != null && this.permissaoImprimirTicket) {
				imprimirTicketAgendamentoController.setValorRadio(1);
				imprimirTicketAgendamentoController.setPaciente(paciente);
				imprimirTicketAgendamentoController.setCameFrom(true);
				return PAGE_IMPRESSAO_TICKET_AGENDAMENTO;
			} else {
				imprimirTicketAgendamentoController.setValorRadio(2);
				if (agendamentoSessao != null){
					imprimirTicketAgendamentoController.setSeqAgendamento(agendamentoSessao.getSeq());					
				}	
				imprimirTicketAgendamentoController.setCameFrom(true);
				return PAGE_IMPRESSAO_TICKET_AGENDAMENTO;
			}
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String sugerirAgenda() {
		if (this.paciente == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_OBRIGATORIO");
			return null;
		}
		

		horariosReservados = procedimentoTerapeuticoFacade.obterReservasPacienteParaConfirmacaoCancelamento(paciente.getCodigo(), listaHorarios);

		if (verificarAcessoConfirmarReserva()) {
			return PAGE_CONFIRMAR_CANCELAR_RESERVA;
		}

		DominioTurno turno = this.turnoTipoSessao != null ? this.turnoTipoSessao.getTurno() : null;
		try {
			this.procedimentoTerapeuticoFacade.validarFiltrosAgendamentoSessao(this.listaHorarios, this.tipoSessao.getSeq(),
					turno, this.dataInicio, this.dataFim, this.dthrHoraInicio);
			
			this.horariosGerados = this.procedimentoTerapeuticoFacade.sugerirAgendaSessao(this.listaHorarios,
					this.tipoSessao.getSeq(), this.sala.getSeq(), this.sala.getEspecialidade().getSeq(),
					this.paciente.getCodigo(), this.prescricao, this.acomodacao, turno,
					this.dataInicio, this.dataFim, this.dthrHoraInicio, this.diasSelecionados, true);
			
			if (this.horariosGerados.get(0).getLogTentativas() != null) {
				if (this.horariosGerados.get(0).getLogTentativas().isEmpty()) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_SEM_AGENDA_DISPONIVEL_AGENDAR_SESSAO");
					return null;
					
				} else {
					this.logTentativas = this.horariosGerados.get(0).getLogTentativas();
					super.openDialog("modalLogTentativasWG");
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_SEM_AGENDA_DISPONIVEL_AGENDAR_SESSAO");
					return null;
				}
				
			} else {
				this.dataInicialCalendario = this.horariosGerados.get(0).getDataInicio();
				this.informacoesPaciente = this.obterInformacoesPaciente();
				
				this.horariosSugeridos = new DefaultScheduleModel();
				
				// Estória #44202 - Monta o percentual de ocupação no período.
				List<PercentualOcupacaoVO> listaPercentais = this.procedimentoTerapeuticoFacade
						.preencherPercentualOcupacao(this.sala.getSeq(), this.tipoSessao.getSeq(), this.horariosGerados);
				for (PercentualOcupacaoVO percentual : listaPercentais) {
					horariosSugeridos.addEvent(adicionarEventoCabecalho(percentual));
				}
				
				for (HorariosAgendamentoSessaoVO vo : this.horariosGerados) {
					horariosSugeridos.addEvent(this.preencherSchedule(vo));
				}
				return PAGE_SUGESTAO_AGENDA;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private boolean verificarAcessoConfirmarReserva() {
		return prescricaoSelecionada != null && horariosReservados != null && !horariosReservados.isEmpty();
	}

	private String obterInformacoesPaciente() {
		String prontuarioEPaciente = CoreUtil.formataProntuario(this.paciente.getProntuario()).concat(HIFEN).concat(this.paciente.getNome());
		String descProtocolo = "";
		if (this.prescricaoSelecionada == null) {
			descProtocolo = this.protocolo != null ? this.protocolo.getMpaProtocoloAssistencial().getTitulo() : "";
			
		} else {
			descProtocolo = this.prescricaoSelecionada.getProtocolo();
		}
		
		return prontuarioEPaciente.concat(HIFEN).concat("Protocolo ").concat(descProtocolo).concat(HIFEN).concat("Ciclo ").concat("1");
	}
	
	/**
	 * Carrega as informações do cabeçalho do evento.
	 */
	private DefaultScheduleEvent adicionarEventoCabecalho(PercentualOcupacaoVO vo) {
		DefaultScheduleEvent event = new DefaultScheduleEvent();
		Integer percentual = vo.getPercentual();
		if (percentual <= 80) {
			event.setStyleClass("verde");
		} else if (percentual > 80 && percentual < 100) {
			event.setStyleClass("amarelo");
		} else {
			event.setStyleClass("vermelho");
		}
		event.setTitle(percentual.toString().concat("%"));
		event.setDescription(this.obterDescricaoOcupacao(vo));
		event.setAllDay(true);
		event.setStartDate(vo.getDataEvento());
		event.setEndDate(vo.getDataEvento());
		event.setEditable(false);
		return event;
	}
	
	private String obterDescricaoOcupacao(PercentualOcupacaoVO vo) {
		String percentual = vo.getPercentual().toString().concat("%");
		
		return "Ocupação: ".concat(percentual).concat("\n")
				.concat("Tempo disponível: ").concat(vo.getTempoDisponivelFormatado()).concat("\n")
				.concat("Pacientes: ").concat(vo.getNumeroPacientes().toString());
	}
	
	private DefaultScheduleEvent preencherSchedule(HorariosAgendamentoSessaoVO vo) {
		DefaultScheduleEvent event = new DefaultScheduleEvent();
		event.setTitle("D".concat(vo.getDia().toString()).concat(HIFEN).concat(this.paciente.getNome()));
		event.setDescription(this.obterDescricaoPaciente(vo));
		event.setStartDate(vo.getDataInicio());
		event.setEndDate(vo.getDataFim());
		event.setEditable(false);
			
		return event;
	}
	
	private String obterDescricaoPaciente(HorariosAgendamentoSessaoVO vo) {
		String acomodacao = vo.getAcomodacao().getDescricao().concat(": ");
		String horaInicio = DateUtil.obterDataFormatada(vo.getDataInicio(), "HH:mm");
		String horaFim = DateUtil.obterDataFormatada(vo.getDataFim(), "HH:mm");
		String nomePaciente = CoreUtil.formataProntuario(this.paciente.getProntuario()).concat(" ").concat(this.paciente.getNome());
		String ciclo = vo.getCiclo() != null ? vo.getCiclo().toString() : "";
		String descProtocolo = "";
		if (this.prescricaoSelecionada == null) {
			descProtocolo = this.protocolo != null ? this.protocolo.getMpaProtocoloAssistencial().getTitulo() : "";
			
		} else {
			descProtocolo = this.prescricaoSelecionada.getProtocolo();
		}
		
		return nomePaciente.concat("\n")
				.concat("Protocolo ").concat(descProtocolo).concat(" - Ciclo ").concat(ciclo).concat("\n")
				.concat(horaInicio).concat(HIFEN).concat(horaFim).concat(" D").concat(vo.getDia().toString()).concat("\n")
				.concat("Sala ").concat(this.sala.getDescricao()).concat("\n")
				.concat(acomodacao).concat(vo.getLoaSeq().toString());
	}
	
	public void limparListaHorarios() {
		this.listaHorarios.clear();
	}
	
	public void excluir() {
		this.listaHorarios.remove(this.itemSelecionado);
	}
	
	public String voltar() {
		if(this.telaLista){
			visualizarPacientesListaEsperaController.setPesquisou(true);
			return PAGE_LISTA_PACIENTES_ESPERA;
		}else{
			return PAGE_AGENDAMENTO_SESSAO;			
		}
	}
	
	
	private void popularDiasSemana() {
		List<MptDiaTipoSessao> listaDias = this.procedimentoTerapeuticoFacade.obterDiasPorTipoSessao(this.tipoSessao.getSeq());
		Collections.sort(listaDias, new Comparator<MptDiaTipoSessao>() {
			@Override
			public int compare(MptDiaTipoSessao o1, MptDiaTipoSessao o2) {
				return o1.getDia().compareTo(o2.getDia());
			}
		});
		diaSemana = new ArrayList<String>();
		for (MptDiaTipoSessao itemDias : listaDias) {
			switch (itemDias.getDia()) {
			case (byte) 1:
				diaSemana.add(DominioDiaSemana.DOM.getDescricaoCompleta());
				break;
			case (byte) 2:
				diaSemana.add(DominioDiaSemana.SEG.getDescricaoCompleta());
				break;
			case (byte) 3:
				diaSemana.add(DominioDiaSemana.TER.getDescricaoCompleta());
				break;
			case (byte) 4:
				diaSemana.add(DominioDiaSemana.QUA.getDescricaoCompleta());
				break;
			case (byte) 5:
				diaSemana.add(DominioDiaSemana.QUI.getDescricaoCompleta());
				break;
			case (byte) 6:
				diaSemana.add(DominioDiaSemana.SEX.getDescricaoCompleta());
				break;
			case (byte) 7:
				diaSemana.add(DominioDiaSemana.SAB.getDescricaoCompleta());
				break;
			}
		}
	}
	
	
	
	// getters & setters
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getPrescricao() {
		return prescricao;
	}

	public void setPrescricao(Integer prescricao) {
		this.prescricao = prescricao;
	}

	public DominioTipoLocal getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(DominioTipoLocal acomodacao) {
		this.acomodacao = acomodacao;
	}

	public List<MptTurnoTipoSessao> getTurnos() {
		return turnos;
	}

	public void setTurnos(List<MptTurnoTipoSessao> turnos) {
		this.turnos = turnos;
	}

	public MptTurnoTipoSessao getTurnoTipoSessao() {
		return turnoTipoSessao;
	}

	public void setTurnoTipoSessao(MptTurnoTipoSessao turnoTipoSessao) {
		this.turnoTipoSessao = turnoTipoSessao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	public MpaVersaoProtAssistencial getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(MpaVersaoProtAssistencial protocolo) {
		this.protocolo = protocolo;
	}

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public List<PrescricaoPacienteVO> getListaPrescricoes() {
		return listaPrescricoes;
	}

	public void setListaPrescricoes(List<PrescricaoPacienteVO> listaPrescricoes) {
		this.listaPrescricoes = listaPrescricoes;
	}

	public PrescricaoPacienteVO getPrescricaoSelecionada() {
		return prescricaoSelecionada;
	}

	public void setPrescricaoSelecionada(PrescricaoPacienteVO prescricaoSelecionada) {
		if (prescricaoSelecionada.equals(this.prescricaoSelecionada)) {
			this.prescricaoSelecionada = null;
		} else {
			this.prescricaoSelecionada = prescricaoSelecionada;	
		}
	}

	public List<CadIntervaloTempoVO> getListaHorarios() {
		return listaHorarios;
	}

	public void setListaHorarios(List<CadIntervaloTempoVO> listaHorarios) {
		this.listaHorarios = listaHorarios;
	}

	public CadIntervaloTempoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(CadIntervaloTempoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<HorariosAgendamentoSessaoVO> getHorariosGerados() {
		return horariosGerados;
	}

	public void setHorariosGerados(List<HorariosAgendamentoSessaoVO> horariosGerados) {
		this.horariosGerados = horariosGerados;
	}
	
	public ScheduleModel getHorariosSugeridos() {
        return horariosSugeridos;
    }
	
	public void setHorariosSugeridos(ScheduleModel horariosSugeridos) {
		this.horariosSugeridos = horariosSugeridos;
	}

	public Date getDataInicialCalendario() {
		return dataInicialCalendario;
	}

	public void setDataInicialCalendario(Date dataInicialCalendario) {
		this.dataInicialCalendario = dataInicialCalendario;
	}

	public String getInformacoesPaciente() {
		return informacoesPaciente;
	}

	public void setInformacoesPaciente(String informacoesPaciente) {
		this.informacoesPaciente = informacoesPaciente;
	}

	public boolean isManutencaoAgendamento() {
		return manutencaoAgendamento;
	}

	public void setManutencaoAgendamento(boolean manutencaoAgendamento) {
		this.manutencaoAgendamento = manutencaoAgendamento;
	}

	public List<HorarioReservadoVO> getHorariosReservados() {
		return horariosReservados;
	}

	public void setHorariosReservados(List<HorarioReservadoVO> horariosReservados) {
		this.horariosReservados = horariosReservados;
	}

	public Boolean getTelaLista() {
		return telaLista;
	}

	public void setTelaLista(Boolean telaLista) {
		this.telaLista = telaLista;
	}

	public Date getDthrHoraInicio() {
		return dthrHoraInicio;
	}

	public void setDthrHoraInicio(Date dthrHoraInicio) {
		this.dthrHoraInicio = dthrHoraInicio;
	}

	public String[] getDiasSelecionados() {
		return diasSelecionados;
	}

	public void setDiasSelecionados(String[] diasSelecionados) {
		this.diasSelecionados = diasSelecionados;
	}

	public List<String> getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(List<String> diaSemana) {
		this.diaSemana = diaSemana;
	}

	public String getLogTentativas() {
		return logTentativas;
	}

	public void setLogTentativas(String logTentativas) {
		this.logTentativas = logTentativas;
	}
}
