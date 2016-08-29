package br.gov.mec.aghu.internacao.vo;

import java.util.Date;
import java.util.List;


/**
 * Classe responsável por agrupar informações a serem exibidos no relatório
 * de pacientes por unidade.
 * 
 * @author tfelini
 * 
 */
public class PacienteUnidadeFuncionalVO {
	
	//Paciente
	private String ltoLtoId;
	private String estadoSaude;
	private String nomePaciente;
	private Integer idadePaciente;
	private Integer conNumero;
	private String prontuario;
	private Date dataInicioAtendimento;
	private String siglaEspecialidade;
	private String senha;
	private List<String> acompanhantes;

	//Unidade Funcional
	private Integer unfMaeSeq;
	private String unfMaeDescricao;
	private Integer unfFilhaSeq;
	private String unfFilhaDescricao;
	
	private String lblUnidade;
	
    public String getLtoLtoId() {
		return ltoLtoId;
	}
	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}
	public String getEstadoSaude() {
		return estadoSaude;
	}
	public void setEstadoSaude(String estadoSaude) {
		this.estadoSaude = estadoSaude;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getIdadePaciente() {
		return idadePaciente;
	}
	public void setIdadePaciente(Integer idadePaciente) {
		this.idadePaciente = idadePaciente;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public Date getDataInicioAtendimento() {
		return dataInicioAtendimento;
	}
	public void setDataInicioAtendimento(Date dataInicioAtendimento) {
		this.dataInicioAtendimento = dataInicioAtendimento;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public Integer getUnfMaeSeq() {
		return unfMaeSeq;
	}
	public void setUnfMaeSeq(Integer unfMaeSeq) {
		this.unfMaeSeq = unfMaeSeq;
	}
	public String getUnfMaeDescricao() {
		return unfMaeDescricao;
	}
	public void setUnfMaeDescricao(String unfMaeDescricao) {
		this.unfMaeDescricao = unfMaeDescricao;
	}
	public Integer getUnfFilhaSeq() {
		return unfFilhaSeq;
	}
	public void setUnfFilhaSeq(Integer unfFilhaSeq) {
		this.unfFilhaSeq = unfFilhaSeq;
	}
	public String getUnfFilhaDescricao() {
		return unfFilhaDescricao;
	}
	public void setUnfFilhaDescricao(String unfFilhaDescricao) {
		this.unfFilhaDescricao = unfFilhaDescricao;
	}
	public void setAcompanhantes(List<String> acompanhantes) {
		this.acompanhantes = acompanhantes;
	}
	public List<String> getAcompanhantes() {
		return acompanhantes;
	}
	public String getLblUnidade() {
		return lblUnidade;
	}
	public void setLblUnidade(String lblUnidade) {
		this.lblUnidade = lblUnidade;
	}
	
}