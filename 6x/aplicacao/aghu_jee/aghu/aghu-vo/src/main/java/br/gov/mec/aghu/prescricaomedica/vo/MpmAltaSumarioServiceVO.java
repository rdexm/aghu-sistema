package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * @author frocha
 *
 */
public class MpmAltaSumarioServiceVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1352744858618728093L;
	private String tipo;
	private Date dthrAlta;
	private Integer atendimentoSeq;
	private Integer atendimentoPacienteSeq;
	private Short seqp;
	private Long registroSeq;
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Date getDthrAlta() {
		return dthrAlta;
	}
	public void setDthrAlta(Date dthrAlta) {
		this.dthrAlta = dthrAlta;
	}
	public Integer getAtendimentoPacienteSeq() {
		return atendimentoPacienteSeq;
	}
	public void setAtendimentoPacienteSeq(Integer atendimentoPacienteSeq) {
		this.atendimentoPacienteSeq = atendimentoPacienteSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Long getRegistroSeq() {
		return registroSeq;
	}
	public void setRegistroSeq(Long registroSeq) {
		this.registroSeq = registroSeq;
	}
	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}
	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}
	
	
}
