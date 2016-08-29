package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import br.gov.mec.aghu.dominio.DominioMesAgendamento;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MapaDisponibilidadeVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe controle da tela Mapa de Disponibilidade.
 * 
 * @author rafael.silvestre
 */
public class MapaDisponibilidadeController extends ActionController {

	private static final long serialVersionUID = 5571876870047501885L;
	
	private static final Log LOG = LogFactory.getLog(MapaDisponibilidadeController.class);
	
	private static final String AGENDAMENTO_DETALHADO = "procedimentoterapeutico-agendaDetalhada";
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AgendaDetalhadaController agendaDetalhadaController;

	private boolean pesquisaAtiva;
	private Date dataInicio;
	private Date dataPesquisa;
	private DominioMesAgendamento mes;
	private Integer ano;
	private List<Integer> anos;
	private List<MapaDisponibilidadeVO> mapaDisponibilidade;
	private List<MptTurnoTipoSessao> turnos;
	private MptFavoritoServidor favoritoServidor;
	private MptSalas sala;
	private MptTipoSessao tipoSessao;
	private MptTurnoTipoSessao turnoTipoSessao;
	private ScheduleModel horariosRetornados;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	/**
	 * Método invocado ao carregar a tela.
	 */
	public void iniciar() {
		
		if (this.tipoSessao != null && this.sala != null) {
			this.pesquisar();
			
		} else {
			this.carregarMesListaAnos();
			try {
				this.favoritoServidor = this.procedimentoTerapeuticoFacade.obterFavoritoPorServidor(
						this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			if (this.favoritoServidor != null) {
				if (this.favoritoServidor.getSeqTipoSessao() != null) {
					this.tipoSessao = procedimentoTerapeuticoFacade.obterMptTipoSessaoPorSeqAtivo(this.favoritoServidor.getSeqTipoSessao());
					this.obterTurnos();
				}
				if (this.favoritoServidor.getSeqSala() != null) {
					this.sala = procedimentoTerapeuticoFacade.obterSalaPorSeqAtiva(this.favoritoServidor.getSeqSala());
				}
				if (this.tipoSessao != null && this.sala != null) {
					this.pesquisar();
				}
			}
		}
	}

	/**
	 * Ação do botão Pesquisar.
	 */
	public void pesquisar() {
		this.dataPesquisa = DateUtil.obterData(this.ano, this.mes.getCodigo(), 1);
		this.mapaDisponibilidade = pesquisarMapaDisponibilidadePorData(this.dataPesquisa);
		if (mapaDisponibilidade == null) {
			this.pesquisaAtiva = false;
			apresentarMsgNegocio("MENSAGEM_SEM_AGENDA_DISPONIVEL");
		}
	}

	/**
	 * Consulta Mapa de Disponibilidade conforme data passado por parametro. 
	 */
	private List<MapaDisponibilidadeVO> pesquisarMapaDisponibilidadePorData(Date data) {
		DominioTurno turno = null;
		if (this.turnoTipoSessao != null) {
			turno = this.turnoTipoSessao.getTurno();
		}
		this.dataInicio = data;
		List<MapaDisponibilidadeVO> mapa = consultarMapaDisponibilidade(this.tipoSessao, this.sala, data, turno);
		if (mapa != null && !mapa.isEmpty()) {
			this.horariosRetornados = new DefaultScheduleModel();
			for (MapaDisponibilidadeVO vo : mapa) {
				if (vo != null) {
					this.horariosRetornados.addEvent(this.adicionarEventoCabecalho(vo));
					this.horariosRetornados.addEvent(this.adicionarEventoCorpo(vo));
				}
			}
			this.pesquisaAtiva = true;
		}
		return mapa;
	}

	/**
	 * Consulta pelo dados do Mapa de Disponibilidade passando os filtros selecionados. 
	 */
	private List<MapaDisponibilidadeVO> consultarMapaDisponibilidade(MptTipoSessao tipoSessao, MptSalas sala, Date data, DominioTurno turno) {
		List<MapaDisponibilidadeVO> mapa = null;
		try {
			mapa = procedimentoTerapeuticoFacade.consultarMapaDisponibilidade(tipoSessao != null ? tipoSessao.getSeq() : null, sala != null ? sala.getSeq() : null, data, turno);
		} catch (ParseException e) {
			LOG.error("Exceção capturada: ", e);
		}
		return mapa;
	}
	
	/**
	 * Ação do link Mês Anterior
	 */
	public void previousMonth() {
		pesquisarMapaDisponibilidadePorData(DateUtil.adicionaMeses(this.dataInicio, -1));
	}
	
	/**
	 * Ação do link Próximo Mês
	 */
	public void nextMonth() {
		pesquisarMapaDisponibilidadePorData(DateUtil.adicionaMeses(this.dataInicio, 1));
	}
	
	/**
	 * Obtem nome do mês e do ano para exibição do titulo do calendario (Mês - Ano). 
	 */
	public String obterNomeMesAtual() {
		String retorno = "";
		if (this.dataInicio != null) {
			SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
			SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy");
			String mes = DominioMesAgendamento.obterDominioMesAgendamento(Integer.valueOf(sdfMes.format(this.dataInicio))-1).getDescricao();
			Integer ano = Integer.valueOf(sdfAno.format(this.dataInicio));
			retorno = mes.concat(" - ").concat(ano.toString());
		}
		return retorno;
	}
	
	/**
	 * Carrega as informações do cabeçalho do evento.
	 */
	private DefaultScheduleEvent adicionarEventoCabecalho(MapaDisponibilidadeVO vo) {
		DefaultScheduleEvent event = new DefaultScheduleEvent();
		if (vo.getPercentual() != null) {
			Integer percentual = vo.getPercentual().intValue();
			if (percentual <= 80) {
				event.setStyleClass("verde");
			} else if (percentual > 80 && percentual < 100) {
				event.setStyleClass("amarelo");
			} else {
				event.setStyleClass("vermelho");
			}
			event.setTitle(percentual.toString().concat("%"));
		}
		event.setAllDay(true);
		event.setStartDate(vo.getDataInicio());
		event.setEndDate(vo.getDataInicio());
		return event;
	}
	
	/**
	 * Carrega as informações do corpo do evento. 
	 */
	private DefaultScheduleEvent adicionarEventoCorpo(MapaDisponibilidadeVO vo) {
		DefaultScheduleEvent event = new DefaultScheduleEvent();
		event.setStyleClass("azul");
		event.setTitle(this.obterDescricaoEvento(vo));
		event.setAllDay(true);
		event.setStartDate(vo.getDataInicio());
		event.setEndDate(vo.getDataInicio());
		return event;
	}
	
	/**
	 * Obtem a descrição do titulo para o corpo do evento. 
	 */
	private String obterDescricaoEvento(MapaDisponibilidadeVO vo) {
		String descricao = "";
		if (vo.getMinutosDisponiveis() != null) {
			descricao = descricao.concat("Tempo Disp.: ").concat(obterTempoEmHoras(vo.getMinutosDisponiveis())).concat("\n");
		}
		if (vo.getTempoSerie() != null) {
			descricao = descricao.concat("Tempo Série: ").concat(obterTempoEmHoras(vo.getTempoSerie())).concat("\n");
		}
		if (vo.getNumeroPacientes() != null) {
			descricao = descricao.concat("Pacientes: ").concat(vo.getNumeroPacientes().toString()).concat("\n");
		}
		if (vo.getExtras() != null && vo.getExtras() != 0L) {
			descricao = descricao.concat("Extras: ").concat(vo.getExtras().toString()).concat("\n");
		}
		return descricao;
	}
	
	/**
	 * Obtem o tempo em horas no formato HH:MM 
	 */
	private String obterTempoEmHoras(Integer tempo) {
		
		String retorno = "";
		Integer horas = tempo / 60;
		Integer minutos = tempo % 60;
		if (horas < 10) {
			retorno = retorno.concat("0");
		}
		retorno = retorno.concat(horas.toString()).concat(":");
		if (minutos < 10) {
			retorno = retorno.concat("0");
		}
		retorno = retorno.concat(minutos.toString());
		
		return retorno;
	}

	/**
	 * Ação do botão Limpar.
	 */
	public void limpar() {
		this.tipoSessao = null;
		this.sala = null;
		this.turnoTipoSessao = null;
		this.mapaDisponibilidade = null;
		this.horariosRetornados = null;
		this.pesquisaAtiva = false;
		this.carregarMesListaAnos();
	}

	/**
	 * Inicializa os Combo Box de Mês e Ano. 
	 */
	private void carregarMesListaAnos() {
		SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy");
		this.dataInicio = new Date();
		this.mes = DominioMesAgendamento.obterDominioMesAgendamento(Integer.valueOf(sdfMes.format(this.dataInicio))-1);
		this.ano = Integer.valueOf(sdfAno.format(this.dataInicio));
		this.anos = new ArrayList<Integer>();
		for (int i = 0; i <= 10; i++) {
			this.anos.add(this.ano+i);
		}
	}
	
	/**
	 * Método invocado pelo Suggestion Box de Tipos de Sessão 
	 */
	public List<MptTipoSessao> listarTiposSessao(final String strPesquisa) {
		return this.returnSGWithCount(
				this.procedimentoTerapeuticoFacade.listarTiposSessao(strPesquisa),
				this.procedimentoTerapeuticoFacade.listarTiposSessaoCount(strPesquisa));
	}
	
	/**
	 * Método invocado pelo Suggestion Box de Sala 
	 */
	public List<MptSalas> listarSalas(final String strPesquisa) {
		return this.returnSGWithCount(
				this.procedimentoTerapeuticoFacade.listarSalas(this.tipoSessao.getSeq(), strPesquisa),
				this.procedimentoTerapeuticoFacade.listarSalasCount(this.tipoSessao.getSeq(), strPesquisa));
	}
	
	/**
	 * Obtem lista que preencherá Combo Box de Turno.
	 * Ação invocada após selecionar um Tipo de de Sessão.
	 */
	public void obterTurnos() {
		if (tipoSessao != null && tipoSessao.getSeq() != null) {
			this.turnos = this.procedimentoTerapeuticoFacade.obterTurnosPorTipoSessaoOrdenado(this.tipoSessao.getSeq());
		}
	}
	
	/**
	 * Limpa o Suggestion Box de Sala e o Combo Box de Turno.
	 * Ação invocada após limpar o Tipo de Sessão.
	 */
	public void limparSbSalaTurnos() {
		this.sala = null;
		this.turnos = null;
	}
	
	public void redirecionarEvento(SelectEvent selectEvent) {
		ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();
		this.agendaDetalhadaController.setDataMapeamento(event.getStartDate());
		this.agendaDetalhadaController.setTpsSeq(this.tipoSessao.getSeq());
		this.agendaDetalhadaController.setSalSeq(this.sala.getSeq());
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AGENDAMENTO_DETALHADO);
	}
	
	public void redirecionarData(SelectEvent selectEvent) {
		Date dataMapeamento = (Date) selectEvent.getObject();
		this.agendaDetalhadaController.setDataMapeamento(dataMapeamento);
		this.agendaDetalhadaController.setTpsSeq(this.tipoSessao.getSeq());
		this.agendaDetalhadaController.setSalSeq(this.sala.getSeq());
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AGENDAMENTO_DETALHADO);
	}
	
	/**
	 * 
	 * Getters and Setters 
	 * 
	 */
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	public MptTurnoTipoSessao getTurnoTipoSessao() {
		return turnoTipoSessao;
	}

	public void setTurnoTipoSessao(MptTurnoTipoSessao turnoTipoSessao) {
		this.turnoTipoSessao = turnoTipoSessao;
	}

	public List<MptTurnoTipoSessao> getTurnos() {
		return turnos;
	}

	public void setTurnos(List<MptTurnoTipoSessao> turnos) {
		this.turnos = turnos;
	}

	public DominioMesAgendamento getMes() {
		return mes;
	}

	public void setMes(DominioMesAgendamento mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public List<Integer> getAnos() {
		return anos;
	}

	public void setAnos(List<Integer> anos) {
		this.anos = anos;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public MptFavoritoServidor getFavoritoServidor() {
		return favoritoServidor;
	}

	public void setFavoritoServidor(MptFavoritoServidor favoritoServidor) {
		this.favoritoServidor = favoritoServidor;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public List<MapaDisponibilidadeVO> getMapaDisponibilidade() {
		return mapaDisponibilidade;
	}

	public void setMapaDisponibilidade(List<MapaDisponibilidadeVO> mapaDisponibilidade) {
		this.mapaDisponibilidade = mapaDisponibilidade;
	}

	public ScheduleModel getHorariosRetornados() {
		return horariosRetornados;
	}

	public void setHorariosRetornados(ScheduleModel horariosRetornados) {
		this.horariosRetornados = horariosRetornados;
	}

	public Date getDataPesquisa() {
		return dataPesquisa;
	}

	public void setDataPesquisa(Date dataPesquisa) {
		this.dataPesquisa = dataPesquisa;
	}
}
