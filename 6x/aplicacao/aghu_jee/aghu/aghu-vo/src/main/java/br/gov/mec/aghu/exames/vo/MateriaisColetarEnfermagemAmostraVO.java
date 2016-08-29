package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;

public class MateriaisColetarEnfermagemAmostraVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1984336031514521147L;
	
	private Integer atdSeq;
	private Integer prontuario;
	private String nomePaciente;
	private Integer soeSeq;
	private Short amoSeqp;
	private Integer nroUnico;
	private Date dtNumeroUnico;
	private Short unfSeq;
	private String local;
	private DominioSituacaoAmostra situacao;
	
	public MateriaisColetarEnfermagemAmostraVO() {}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
	}

	public Integer getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}

	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}

	public void setDtNumeroUnico(Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public DominioSituacaoAmostra getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAmostra situacao) {
		this.situacao = situacao;
	}
	
}
