package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioDiaSemana;

public class EscalaSalasBuscaVO implements Serializable {
	private static final long	serialVersionUID	= -5703268223801700001L;

	private Short				seqp;
	private String				turno;
	private Short				ordem;
	private DominioDiaSemana	diasemana;
	private Boolean		urgencia;
	private Boolean		particular;
	private Short				casseq;

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	public Boolean getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(Boolean urgencia) {
		this.urgencia = urgencia;
	}

	public Boolean getParticular() {
		return particular;
	}

	public void setParticular(Boolean particular) {
		this.particular = particular;
	}

	public void setDiasemana(DominioDiaSemana diasemana) {
		this.diasemana = diasemana;
	}

	public DominioDiaSemana getDiasemana() {
		return diasemana;
	}

	public void setCasseq(Short casseq) {
		this.casseq = casseq;
	}

	public Short getCasseq() {
		return casseq;
	}
}
