package br.gov.mec.aghu.transplante.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoOrgao;



public class PacienteTransplanteOrgaoVO {
	
	private String nomePaciente;
	private Integer prontuario;
	private Integer transplanteSeq;
	private DominioTipoOrgao orgao;
	private Date dataIngresso;
	private Integer cptSeq;
	
	
	/**#41792
	 */
	private String tipoOrgao;
	private Date dataTransplante;
	
	public String getTipoOrgao() {
		return tipoOrgao;
	}
	public void setTipoOrgao(String tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}
	public Integer getCptSeq() {
		return cptSeq;
	}
	public void setCptSeq(Integer cptSeq) {
		this.cptSeq = cptSeq;
	}
	public DominioTipoOrgao getOrgao() {
		return orgao;
	}
	public void setOrgao(DominioTipoOrgao orgao) {
		this.orgao = orgao;
	}
	public Date getDataIngresso() {
		return dataIngresso;
	}
	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getTransplanteSeq() {
		return transplanteSeq;
	}
	public void setTransplanteSeq(Integer transplanteSeq) {
		this.transplanteSeq = transplanteSeq;
	}
	
	public Date getDataTransplante() {
		return dataTransplante;
	}
	public void setDataTransplante(Date dataTransplante) {
		this.dataTransplante = dataTransplante;
	}

	public enum Fields{
		NOME("nomePaciente"),
		PRONTUARIO("prontuario"),
		ORGAO("orgao"),
		DATA_INGRESSO("dataIngresso"),
		CPT_SEQ("cptSeq"),
		TRANSPLANTE_SEQ("transplanteSeq");
	
	private String fields;
	
	private Fields(String s){
		this.fields = s;
	}
	
	@Override
	public String toString(){
		return fields;
	}
	
	}
	

}
