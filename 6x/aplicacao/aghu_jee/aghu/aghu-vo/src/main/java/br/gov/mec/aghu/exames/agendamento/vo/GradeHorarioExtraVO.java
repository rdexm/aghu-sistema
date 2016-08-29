package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;

/**
 * 
 * @author danilo.santos
 *
 */
public class GradeHorarioExtraVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7365811381816759727L;

	
	private Integer seqGrade;
	private Short grade;
	private Integer grupoExameSeq;
	private String descrGrupoEx;
	private String siglaExame;
	private Integer matExame;
	private String nomeUsualMaterial;
	private String numSala;
	private String nomeFunc;
	private Short unfExame;
	private String exame;
	
	public Integer getSeqGrade() {
		return seqGrade;
	}
	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}
	public Short getGrade() {
		return grade;
	}
	public void setGrade(Short grade) {
		this.grade = grade;
	}
	public String getDescrGrupoEx() {
		return descrGrupoEx;
	}
	public void setDescrGrupoEx(String descrGrupoEx) {
		this.descrGrupoEx = descrGrupoEx;
	}
	public String getSiglaExame() {
		return siglaExame;
	}
	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}
	public Integer getMatExame() {
		return matExame;
	}
	public void setMatExame(Integer matExame) {
		this.matExame = matExame;
	}
	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}
	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}
	public String getNumSala() {
		return numSala;
	}
	public void setNumSala(String numSala) {
		this.numSala = numSala;
	}
	public String getNomeFunc() {
		return nomeFunc;
	}
	public void setNomeFunc(String nomeFunc) {
		this.nomeFunc = nomeFunc;
	}
	public Short getUnfExame() {
		return unfExame;
	}
	public void setUnfExame(Short unfExame) {
		this.unfExame = unfExame;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	public Integer getGrupoExameSeq() {
		return grupoExameSeq;
	}
	public void setGrupoExameSeq(Integer grupoExameSeq) {
		this.grupoExameSeq = grupoExameSeq;
	}
}
