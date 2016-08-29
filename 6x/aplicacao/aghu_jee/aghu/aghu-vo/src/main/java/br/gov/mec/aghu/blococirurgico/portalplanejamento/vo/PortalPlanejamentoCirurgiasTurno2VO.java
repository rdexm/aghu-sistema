package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortalPlanejamentoCirurgiasTurno2VO implements Serializable {
	
	
	private static final long serialVersionUID = -8410332527501487381L;
	
	private Date horarioTotal;
	private String horarioOcupado;
	private Double porcentagem;
	private Integer minutosOcupacao;
	private Integer totalMinutosTurno; 
	private Boolean bloqueado = false;
	private Boolean indisponivel = false;
	private Boolean overbooking = false;
	private Boolean livre = false;
	private Boolean cedencia = false;
	private String motivoBloqueio;
	private Integer dia;
	private String descricaoTurno;
	private String siglaTurno;
	private Date horarioInicialTurno;
	private Date horarioFinalTurno;
	
	private List<PortalPlanejamentoCirurgiasReservaVO> listaReservas= new ArrayList<PortalPlanejamentoCirurgiasReservaVO>();
	private List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();

	public PortalPlanejamentoCirurgiasTurno2VO() {
		super();
	}

	public PortalPlanejamentoCirurgiasTurno2VO(Integer dia, String siglaTurno) {
		super();
		this.dia = dia;
		this.siglaTurno = siglaTurno;
	}

	public PortalPlanejamentoCirurgiasTurno2VO(Boolean indisponivel,
			String siglaTurno, Date horarioInicialTurno, Date horarioFinalTurno) {
		super();
		this.indisponivel = indisponivel;
		this.siglaTurno = siglaTurno;
		this.horarioInicialTurno = horarioInicialTurno;
		this.horarioFinalTurno = horarioFinalTurno;
	}

	public PortalPlanejamentoCirurgiasTurno2VO(Boolean indisponivel,
			Integer dia, String siglaTurno, Date horarioInicialTurno,
			Date horarioFinalTurno, Integer minutosOcupacao) {
		super();
		this.indisponivel = indisponivel;
		this.dia = dia;
		this.siglaTurno = siglaTurno;
		this.horarioInicialTurno = horarioInicialTurno;
		this.horarioFinalTurno = horarioFinalTurno;
		this.minutosOcupacao = minutosOcupacao;
	}

	public PortalPlanejamentoCirurgiasTurno2VO(Boolean indisponivel,
			Integer dia, String siglaTurno, Date horarioInicialTurno,
			Date horarioFinalTurno) {
		super();
		this.indisponivel = indisponivel;
		this.dia = dia;
		this.siglaTurno = siglaTurno;
		this.horarioInicialTurno = horarioInicialTurno;
		this.horarioFinalTurno = horarioFinalTurno;
	}

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
		if(minutosOcupacao != null && !bloqueado && !indisponivel) {
			return new String(String.format("%02d", minutosOcupacao / 60) + ":" + String.format("%02d", minutosOcupacao % 60));
		}
		return horarioOcupado;
	}
	public void setHorarioOcupado(String horarioOcupado) {
		this.horarioOcupado = horarioOcupado;
	}
	public Double getPorcentagem() {
		if(totalMinutosTurno != null && minutosOcupacao != null) {
			Double percentual = (new Double(minutosOcupacao) / new Double(totalMinutosTurno))*100;
			return (percentual > 100) ? 100 : percentual;
		}
		return porcentagem;
	}
	public void setPorcentagem(Double porcentagem) {
		this.porcentagem = porcentagem;
	}
	public void setOverbooking(Boolean overbooking) {
		this.overbooking = overbooking;
	}
	public Boolean getOverbooking() {
		if(totalMinutosTurno != null && minutosOcupacao != null) {
			return (minutosOcupacao > totalMinutosTurno);
		}
		return overbooking;
	}
	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}
	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}
	public Boolean getIndisponivel() {
		return indisponivel;
	}
	public void setIndisponivel(Boolean indisponivel) {
		this.indisponivel = indisponivel;
	}
	public List<PortalPlanejamentoCirurgiasReservaVO> getListaReservas() {
		return listaReservas;
	}
	public void setListaReservas(
			List<PortalPlanejamentoCirurgiasReservaVO> listaReservas) {
		this.listaReservas = listaReservas;
	}
	public List<PortalPlanejamentoCirurgiasAgendaVO> getListaAgendas() {
		return listaAgendas;
	}
	public void setListaAgendas(
			List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas) {
		this.listaAgendas = listaAgendas;
	}
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	public Boolean getLivre() {
		return livre;
	}
	public void setLivre(Boolean livre) {
		this.livre = livre;
	}
	public String getDescricaoTurno() {
		return descricaoTurno;
	}
	public void setDescricaoTurno(String descricaoTurno) {
		this.descricaoTurno = descricaoTurno;
	}
	public Date getHorarioInicialTurno() {
		return horarioInicialTurno;
	}
	public void setHorarioInicialTurno(Date horarioInicialTurno) {
		this.horarioInicialTurno = horarioInicialTurno;
	}
	public Date getHorarioFinalTurno() {
		return horarioFinalTurno;
	}
	public void setHorarioFinalTurno(Date horarioFinalTurno) {
		this.horarioFinalTurno = horarioFinalTurno;
	}
	public Boolean getCedencia() {
		return cedencia;
	}
	public void setCedencia(Boolean cedencia) {
		this.cedencia = cedencia;
	}
	public String getSiglaTurno() {
		return siglaTurno;
	}
	public void setSiglaTurno(String siglaTurno) {
		this.siglaTurno = siglaTurno;
	}

	public Integer getMinutosOcupacao() {
		return minutosOcupacao;
	}

	public void setMinutosOcupacao(Integer minutosOcupacao) {
		this.minutosOcupacao = minutosOcupacao;
	}

	public Integer getTotalMinutosTurno() {
		return totalMinutosTurno;
	}

	public void setTotalMinutosTurno(Integer totalMinutosTurno) {
		this.totalMinutosTurno = totalMinutosTurno;
	}

	public enum Fields {
		DIA("dia"),
		EQUIPE("equipe"),
		HORA_INICIAL_TURNO("horarioInicialTurno");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dia == null) ? 0 : dia.hashCode());
		result = prime * result
				+ ((siglaTurno == null) ? 0 : siglaTurno.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PortalPlanejamentoCirurgiasTurno2VO)) {
			return false;
		}
		PortalPlanejamentoCirurgiasTurno2VO other = (PortalPlanejamentoCirurgiasTurno2VO) obj;
		if (dia == null) {
			if (other.dia != null) {
				return false;
			}
		} else if (!dia.equals(other.dia)) {
			return false;
		}
		if (siglaTurno == null) {
			if (other.siglaTurno != null) {
				return false;
			}
		} else if (!siglaTurno.equals(other.siglaTurno)) {
			return false;
		}
		return true;
	}
}
