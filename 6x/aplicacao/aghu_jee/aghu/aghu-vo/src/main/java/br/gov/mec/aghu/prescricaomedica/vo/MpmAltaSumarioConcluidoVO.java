package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
/**
 * 
 * @author frocha
 *
 */
public class MpmAltaSumarioConcluidoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1352744858618728093L;
	private Integer pacienteSeq;
	private Short seqp;


	public Integer getPacienteSeq() {
		return pacienteSeq;
	}
	public void setPacienteSeq(Integer pacienteSeq) {
		this.pacienteSeq = pacienteSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
}
