package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;

/**
 * Classe utilizada para armazenar temporariamente
 * os horários marcados
 * 
 * @author israel.haas
 *
 */
public class HorariosAgendaVO implements BaseBean {

	private static final long serialVersionUID = -4603016839846055803L;
	
	private List<HorariosAgendamentoSessaoVO> listaFinal;
	private Date dataInicioCalculada;
	private boolean marcouHorario;
	private Short diaRefAnterior;
	private boolean percorreuDia;
	private boolean tuco;
	private MptTurnoTipoSessao filtroTurno;
	private List<DominioDiaSemana> diasSemanaFiltro;
	private Date dthrHoraInicio;
	private StringBuilder logTentativas;
	private Integer nroTentativas;
	private List<String> logsHorarioBloqueado;
	private boolean gerouLogAmbulatorio;
	private boolean sugestao;
	private HorariosAgendamentoSessaoVO menorHorario;
	private boolean encontrouMenorHorario;
	
	public HorariosAgendaVO() {
	}

	public List<HorariosAgendamentoSessaoVO> getListaFinal() {
		return listaFinal;
	}

	public void setListaFinal(List<HorariosAgendamentoSessaoVO> listaFinal) {
		this.listaFinal = listaFinal;
	}

	public Date getDataInicioCalculada() {
		return dataInicioCalculada;
	}

	public void setDataInicioCalculada(Date dataInicioCalculada) {
		this.dataInicioCalculada = dataInicioCalculada;
	}

	public boolean isMarcouHorario() {
		return marcouHorario;
	}

	public void setMarcouHorario(boolean marcouHorario) {
		this.marcouHorario = marcouHorario;
	}

	public Short getDiaRefAnterior() {
		return diaRefAnterior;
	}

	public void setDiaRefAnterior(Short diaRefAnterior) {
		this.diaRefAnterior = diaRefAnterior;
	}

	public boolean isPercorreuDia() {
		return percorreuDia;
	}

	public void setPercorreuDia(boolean percorreuDia) {
		this.percorreuDia = percorreuDia;
	}

	public boolean isTuco() {
		return tuco;
	}

	public void setTuco(boolean tuco) {
		this.tuco = tuco;
	}

	public MptTurnoTipoSessao getFiltroTurno() {
		return filtroTurno;
	}

	public void setFiltroTurno(MptTurnoTipoSessao filtroTurno) {
		this.filtroTurno = filtroTurno;
	}

	public List<DominioDiaSemana> getDiasSemanaFiltro() {
		return diasSemanaFiltro;
	}

	public void setDiasSemanaFiltro(List<DominioDiaSemana> diasSemanaFiltro) {
		this.diasSemanaFiltro = diasSemanaFiltro;
	}

	public Date getDthrHoraInicio() {
		return dthrHoraInicio;
	}

	public void setDthrHoraInicio(Date dthrHoraInicio) {
		this.dthrHoraInicio = dthrHoraInicio;
	}

	public StringBuilder getLogTentativas() {
		return logTentativas;
	}

	public void setLogTentativas(StringBuilder logTentativas) {
		this.logTentativas = logTentativas;
	}

	public Integer getNroTentativas() {
		return nroTentativas;
	}

	public void setNroTentativas(Integer nroTentativas) {
		this.nroTentativas = nroTentativas;
	}

	public List<String> getLogsHorarioBloqueado() {
		return logsHorarioBloqueado;
	}

	public void setLogsHorarioBloqueado(List<String> logsHorarioBloqueado) {
		this.logsHorarioBloqueado = logsHorarioBloqueado;
	}

	public boolean isGerouLogAmbulatorio() {
		return gerouLogAmbulatorio;
	}

	public void setGerouLogAmbulatorio(boolean gerouLogAmbulatorio) {
		this.gerouLogAmbulatorio = gerouLogAmbulatorio;
	}

	public boolean isSugestao() {
		return sugestao;
	}

	public void setSugestao(boolean sugestao) {
		this.sugestao = sugestao;
	}

	public HorariosAgendamentoSessaoVO getMenorHorario() {
		return menorHorario;
	}

	public void setMenorHorario(HorariosAgendamentoSessaoVO menorHorario) {
		this.menorHorario = menorHorario;
	}

	public boolean isEncontrouMenorHorario() {
		return encontrouMenorHorario;
	}

	public void setEncontrouMenorHorario(boolean encontrouMenorHorario) {
		this.encontrouMenorHorario = encontrouMenorHorario;
	}
}
