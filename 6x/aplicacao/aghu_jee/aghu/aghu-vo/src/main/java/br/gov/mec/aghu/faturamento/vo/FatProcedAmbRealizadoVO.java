package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class FatProcedAmbRealizadoVO implements Serializable {

	private static final long serialVersionUID = -8354951184137264779L;
	
	private Long seq;
	private Integer phiSeq;
	private Integer valorNumerico;
	private Short cspCnvCodigo;
	private Byte cspSeq;
	private Date dthrRealizado;
	private Short espSeq;
	private Short unfSeq;

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getValorNumerico() {
		return valorNumerico;
	}

	public void setValorNumerico(Integer valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}
}
