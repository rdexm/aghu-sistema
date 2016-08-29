package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;


public class ConsultaPacienteVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5323579923783300675L;

	private Date dtConsulta;
	
	private String nome;
	
	private String descricao;
	
	private Boolean emergencia = false;
	
	private String estilo;
	
	private Integer matricula;
	
	private Short vinCodigo;
	
	private Short seqEspecialidade;
		
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setEmergencia(Boolean emergencia) {
		this.emergencia = emergencia;
	}
	public Boolean getEmergencia() {
		return emergencia;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getEstilo() {
		return estilo;
	}
	public void setEstilo(String estilo) {
		this.estilo = estilo;
	}
	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}
	public Date getDtConsulta() {
		return dtConsulta;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}
	
	
	
	
}
