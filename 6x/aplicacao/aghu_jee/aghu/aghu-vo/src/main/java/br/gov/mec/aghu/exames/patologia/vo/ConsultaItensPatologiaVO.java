package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;

public class ConsultaItensPatologiaVO implements Serializable {

	private static final long serialVersionUID = 2125420067695143494L;

	private Integer iseSoeSeq;
	
	private Short iseSeqp;
	
	private String ufeEmaExaSigla;
	
	private Integer ufeEmaManSeq;
	
	private Integer velSeqp;
	
	private Integer calSeq;
	
	private Integer seqp;
	
	private String sitCodigo;

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public Integer getVelSeqp() {
		return velSeqp;
	}

	public void setVelSeqp(Integer velSeqp) {
		this.velSeqp = velSeqp;
	}

	public Integer getCalSeq() {
		return calSeq;
	}

	public void setCalSeq(Integer calSeq) {
		this.calSeq = calSeq;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public String getSitCodigo() {
		return sitCodigo;
	}

	public void setSitCodigo(String sitCodigo) {
		this.sitCodigo = sitCodigo;
	}

	
}
