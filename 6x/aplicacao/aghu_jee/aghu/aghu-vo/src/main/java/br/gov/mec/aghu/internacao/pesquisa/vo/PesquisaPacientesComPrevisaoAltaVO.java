package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.core.commons.BaseBean;




/**
 * Os dados armazenados nesse objeto representam os parametros da pesquisa de pacientes com previsão de alta
 * 
 * @author Stanley Araujo
 */
public class PesquisaPacientesComPrevisaoAltaVO implements BaseBean {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7676299242427308591L;
	private String prontuarioPaciente;
	private String nomePaciente;
	private String nomeProfessor;
	private String leitoID;
	private String dataInternacao;
	private String dataPrevisaoAlta;
	private String especialidadeNomeReduzido;
	private String descricaoQuarto;
	private String unidadeFuncionalDescricao;

	
	
	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getNomeProfessor() {
		return nomeProfessor;
	}
	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}
	public String getLeitoID() {
		return leitoID;
	}
	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}
	public String getDataInternacao() {
		return dataInternacao;
	}
	public void setDataInternacao(String dataInternacao) {
		this.dataInternacao = dataInternacao;
	}
	public String getDataPrevisaoAlta() {
		return dataPrevisaoAlta;
	}
	public void setDataPrevisaoAlta(String dataPrevisaoAlta) {
		this.dataPrevisaoAlta = dataPrevisaoAlta;
	}
	public String getEspecialidadeNomeReduzido() {
		return especialidadeNomeReduzido;
	}
	public void setEspecialidadeNomeReduzido(String especialidadeNomeReduzido) {
		this.especialidadeNomeReduzido = especialidadeNomeReduzido;
	}
	
	
	public String getUnidadeFuncionalDescricao() {
		return unidadeFuncionalDescricao;
	}
	public void setUnidadeFuncionalDescricao(String unidadeFuncionalDescricao) {
		this.unidadeFuncionalDescricao = unidadeFuncionalDescricao;
	}
	public String getDescricaoQuarto() {
		return descricaoQuarto;
	}
	public void setDescricaoQuarto(String descricaoQuarto) {
		this.descricaoQuarto = descricaoQuarto;
	}
	
	
	
		
}
