package br.gov.mec.aghu.transplante.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.model.AipPacientes;



public class PacienteTransplanteMedulaOsseaVO {
	
	private Integer prontuario;
	private String nome;
	private Integer transplanteSeq;
	private Date dataIngresso;
	private Integer cptSeq;
	private DominioSituacaoTmo tipoTmo;
	private DominioTipoAlogenico tipoAlogenico;
	private DominioTipoOrgao tipoOrgao;
	private Date dataNascimento;
	private DominioSituacaoTransplante situacao;
	private AipPacientes aipPacientes;
	private String tipo;

	/*
	 * para o sql da #41792
	 */
	private Date dataTransplante;
	private String tipoIndTmo;
	private String tipoIndAlogenico;
	
	
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}
	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}
	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico; 
	}
	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}
	public Integer getCptSeq() {
		return cptSeq;
	}
	public void setCptSeq(Integer cptSeq) {
		this.cptSeq = cptSeq;
	}
	public Date getDataIngresso() {
		return dataIngresso;
	}
	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
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

	public String getTipoIndTmo() {
		return tipoIndTmo;
	}
	public void setTipoIndTmo(String tipoIndTmo) {
		this.tipoIndTmo = tipoIndTmo;
	}
	public String getTipoIndAlogenico() {
		return tipoIndAlogenico;
	}
	public void setTipoIndAlogenico(String tipoIndAlogenico) {
		this.tipoIndAlogenico = tipoIndAlogenico;
	}
	
	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}
	
	public AipPacientes getAipPacientes() {
		return aipPacientes;
	}
	public void setAipPacientes(AipPacientes aipPacientes) {
		this.aipPacientes = aipPacientes;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}
	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}


	public enum Fields{
		NOME("nome"),
		PRONTUARIO("prontuario"),
		DATA_INGRESSO("dataIngresso"),
		CPT_SEQ("cptSeq"),
		TIPO_TMO("tipoTmo"),
		TIPO_ORGAO("tipoOrgao"),
		TIPO("tipo"),
		TIPO_ALOGENICO("tipoAlogenico"),
		DATA_NASCIMENTO("dataNascimento"),
    	TRANSPLANTE_SEQ("transplanteSeq"),
		SITUACAO("situacao"),
		AIP_PACIENTES("aipPacientes");
	
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
