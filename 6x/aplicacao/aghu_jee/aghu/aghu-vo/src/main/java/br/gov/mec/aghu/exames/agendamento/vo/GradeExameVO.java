package br.gov.mec.aghu.exames.agendamento.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class GradeExameVO implements BaseBean {


	private static final long serialVersionUID = -7743288614215933570L;

	/**
	 * 
	 */

	private Integer seq;

	private Short grade;

	private String numero;

	private String descricaoGrupo;
	
	private String descricaoExame;

	private String responsavel;

	private String situacao;

	public GradeExameVO() {
	}

	public GradeExameVO(Integer seq, Short grade, String numero, String descricaoGrupo,  
			String descricaoExame, String responsavel, String situacao) {
		this.seq = seq;
		this.grade = grade;
		this.numero = numero;
		this.descricaoGrupo = descricaoGrupo;
		this.descricaoExame = descricaoExame;
		this.responsavel = responsavel;
		this.situacao = situacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getGrade() {
		return grade;
	}

	public void setGrade(Short grade) {
		this.grade = grade;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}

	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

}
