package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PortalPlanejamentoCirurgiasTurnoVO implements Serializable {
	
	private static final long serialVersionUID = 3487201369407286178L;
	
	private Date horarioTotal;
	private String horarioOcupado;
	private Double porcentagem;
	private Boolean bloqueado;
	private Boolean semUso;
	private Boolean overbooking;
	private String motivoBloqueio;
	
	private Integer dia;	
	private String turno;
	private String descricaoTurno;
	private Date horaInicioEquipe;
	private Date horaFimEquipe;
	private String sala;
	private Short ordem;
	private List<MbcAgendas> agendas;
	private Date horarioOcupadoDate;
	
	private Date horarioInicialTurno;
	private Date horarioFinalTurno;
	
	//atributo da tela de detalhe
	private List<DetalheAgendaCirurgiaHorarioVO> listaHorarios = new ArrayList<DetalheAgendaCirurgiaHorarioVO>();
	
	public Boolean getBloqueado() {
		return bloqueado;
	}
	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
	}
	public Date getHorarioTotal() {
		return horarioTotal;
	}
	public void setHorarioTotal(Date horarioTotal) {
		this.horarioTotal = horarioTotal;
	}
	public String getHorarioOcupado() {
		return horarioOcupado;
	}
	public void setHorarioOcupado(String horarioOcupado) {
		this.horarioOcupado = horarioOcupado;
	}
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	
//	public Boolean getOverbooking() {
//		if(horarioOcupado != null && horarioTotal != null
//				&& !bloqueado
//				&& horarioOcupado.compareTo(horarioTotal) > 0) {
//			return true;
//		}
//		return false;
//	}
	public Double getPorcentagem() {
		return porcentagem;
	}
	public void setPorcentagem(Double porcentagem) {
		this.porcentagem = porcentagem;
	}
	public void setOverbooking(Boolean overbooking) {
		this.overbooking = overbooking;
	}
	public Boolean getOverbooking() {
		return overbooking;
	}
	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}
	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public Date getHoraInicioEquipe() {
		return horaInicioEquipe;
	}
	public void setHoraInicioEquipe(Date horaInicioEquipe) {
		this.horaInicioEquipe = horaInicioEquipe;
	}
	public Date getHoraFimEquipe() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(horaFimEquipe);
		if(cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
			return DateUtil.obterDataComHoraFinal(horaFimEquipe);
		}
		return horaFimEquipe;
	}
	public void setHoraFimEquipe(Date horaFimEquipe) {
		this.horaFimEquipe = horaFimEquipe;
	}
	public String getSala() {
		return sala;
	}
	public void setSala(String sala) {
		this.sala = sala;
	}
	public Boolean getSemUso() {
		return semUso;
	}
	public void setSemUso(Boolean semUso) {
		this.semUso = semUso;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public List<MbcAgendas> getAgendas() {
		return agendas;
	}
	public void setAgendas(List<MbcAgendas> agendas) {
		this.agendas = agendas;
	}
	public Date getHorarioOcupadoDate() {
		return horarioOcupadoDate;
	}
	public void setHorarioOcupadoDate(Date horarioOcupadoDate) {
		this.horarioOcupadoDate = horarioOcupadoDate;
	}
	public List<DetalheAgendaCirurgiaHorarioVO> getListaHorarios() {
		return listaHorarios;
	}
	public void setListaHorarios(List<DetalheAgendaCirurgiaHorarioVO> listaHorarios) {
		this.listaHorarios = listaHorarios;
	}
	public Date getHorarioInicialTurno() {
		return horarioInicialTurno;
	}
	public void setHorarioInicialTurno(Date horarioInicialTurno) {
		this.horarioInicialTurno = horarioInicialTurno;
	}
	public Date getHorarioFinalTurno() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(horarioFinalTurno);
		if(cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
			return DateUtil.obterDataComHoraFinal(horarioFinalTurno);
		}
		return horarioFinalTurno;
	}
	public void setHorarioFinalTurno(Date horarioFinalTurno) {
		this.horarioFinalTurno = horarioFinalTurno;
	}
	public String getDescricaoTurno() {
		return descricaoTurno;
	}
	public void setDescricaoTurno(String descricaoTurno) {
		this.descricaoTurno = descricaoTurno;
	}
}
