package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

public class SolicitacaoExamesVO implements Serializable {

	
	private static final long serialVersionUID = -2912587321708530893L;
	private Integer seq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinculo;
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinculo() {
		return serVinculo;
	}
	public void setSerVinculo(Short serVinculo) {
		this.serVinculo = serVinculo;
	}
	
}
