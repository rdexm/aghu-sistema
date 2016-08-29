package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.extensions.event.timeline.TimelineSelectEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AcomodacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioAcomodacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AgendaDetalhadaController extends ActionController {

	private static final long serialVersionUID = 734138764286061667L;

	private static final String MANUTENCAO_AGENDAMENTO = "procedimentoterapeutico-manutencaoAgendamentoSessaoTerapeutica";
	
	private static final String MAPA_DISPONIBILIDADE = "procedimentoterapeutico-mapaDisponibilidade";
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private ManutencaoAgendamentoSessaoTerapeuticaController manutencaoAgendamentoSessaoTerapeuticaController;
	
	private TimelineModel model;
	private Short tpsSeq;
	private MptTipoSessao tipoSessao;
	private Short salSeq;
	private MptSalas sala;
	private String descrTipoSessao;
	private Date dataMapeamento;
	private String dataFormatada;
	private String salaMapeamento;
	
	private Date turnoInicial;
	private Date turnoFinal;
	
	private TimelineEvent eventoSelecionado;
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void inicio() {
		this.tipoSessao = this.procedimentoTerapeuticoFacade.obterTipoSessaoPorChavePrimaria(this.tpsSeq);
		this.descrTipoSessao = "Tipo de Sessão ".concat(this.tipoSessao.getDescricao());
		this.sala = this.procedimentoTerapeuticoFacade.obterMptSalaPorChavePrimaria(salSeq);
		this.dataFormatada = "Data: ".concat(DateUtil.obterDataFormatada(this.dataMapeamento, "dd/MM/yyyy"));
		this.salaMapeamento = "Sala: ".concat(this.sala.getDescricao());
		
		List<MptTurnoTipoSessao> turnos = this.procedimentoTerapeuticoFacade.obterTurnosPorTipoSessaoOrdenado(this.tpsSeq);
		this.turnoInicial = DateUtil.comporDiaHora(this.dataMapeamento, turnos.get(0).getHoraInicio());
		this.turnoFinal = DateUtil.comporDiaHora(this.dataMapeamento, turnos.get(turnos.size()-1).getHoraFim());
		this.turnoFinal = DateUtil.adicionaMinutos(this.turnoFinal, 5);
		
		this.popularTimeline();
	}
	
	private void popularTimeline() {
		List<AcomodacaoVO> listaAcomodacoes = this.procedimentoTerapeuticoFacade
				.obterListaHorariosAgendadosPorAcomodacao(this.tpsSeq, this.salSeq, this.dataMapeamento);
		
		this.model = new TimelineModel(); 
		for (AcomodacaoVO acomodacao : listaAcomodacoes) {
			TimelineGroup group = new TimelineGroup(acomodacao.getLoaSeq().toString(), acomodacao);
			model.addGroup(group);
			
			for (HorarioAcomodacaoVO evento : acomodacao.getListaHorariosReservados()) {
				model.add(new TimelineEvent(evento, evento.getDataInicio(), evento.getDataFim(), false, group.getId(), "reservado"));
			}
			for (HorarioAcomodacaoVO evento : acomodacao.getListaHorariosMarcados()) {
				model.add(new TimelineEvent(evento, evento.getDataInicio(), evento.getDataFim(), false, group.getId(), "marcado"));
			}
			for (HorarioAcomodacaoVO evento : acomodacao.getListaHorariosLivres()) {
				model.add(new TimelineEvent(evento, evento.getDataInicio(), evento.getDataFim(), false, group.getId(), "livre"));
			}
		}
	}
	
	public void onSelect(TimelineSelectEvent e) {
		this.eventoSelecionado = e.getTimelineEvent();
		if (!this.eventoSelecionado.getStyleClass().equalsIgnoreCase("livre")) {
			this.openDialog("modalEventoWG");
		}
	}
	
	public String redirecionarManutencao() {
		this.manutencaoAgendamentoSessaoTerapeuticaController.setDataMapeamento(this.dataMapeamento);
		if(this.eventoSelecionado.getStyleClass().equalsIgnoreCase("reservado")){
			this.manutencaoAgendamentoSessaoTerapeuticaController.setPossuiReserva(true);
		}
		return MANUTENCAO_AGENDAMENTO;
	}
	
	public String voltar() {
		return MAPA_DISPONIBILIDADE;
	}

	// getters & setters
	public TimelineModel getModel() {
		return model;
	}

	public void setModel(TimelineModel model) {
		this.model = model;
	}

	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public Short getSalSeq() {
		return salSeq;
	}

	public void setSalSeq(Short salSeq) {
		this.salSeq = salSeq;
	}

	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	public String getDescrTipoSessao() {
		return descrTipoSessao;
	}

	public void setDescrTipoSessao(String descrTipoSessao) {
		this.descrTipoSessao = descrTipoSessao;
	}

	public Date getDataMapeamento() {
		return dataMapeamento;
	}

	public void setDataMapeamento(Date dataMapeamento) {
		this.dataMapeamento = dataMapeamento;
	}

	public String getDataFormatada() {
		return dataFormatada;
	}

	public void setDataFormatada(String dataFormatada) {
		this.dataFormatada = dataFormatada;
	}

	public String getSalaMapeamento() {
		return salaMapeamento;
	}

	public void setSalaMapeamento(String salaMapeamento) {
		this.salaMapeamento = salaMapeamento;
	}

	public Date getTurnoInicial() {
		return turnoInicial;
	}

	public void setTurnoInicial(Date turnoInicial) {
		this.turnoInicial = turnoInicial;
	}

	public Date getTurnoFinal() {
		return turnoFinal;
	}

	public void setTurnoFinal(Date turnoFinal) {
		this.turnoFinal = turnoFinal;
	}

	public TimelineEvent getEventoSelecionado() {
		return eventoSelecionado;
	}

	public void setEventoSelecionado(TimelineEvent eventoSelecionado) {
		this.eventoSelecionado = eventoSelecionado;
	}
}
