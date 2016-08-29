package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;


/**
 * Classe responsável por agrupar informações a serem exibidos no grid
 * de pacientes por Profissional/Especialidade
 * 
 * @author tfelini
 * 
 */
public class PacienteProfissionalEspecialidadeVO implements BaseBean {
	
	private String prontuario;
	private String nomePaciente;
	private Date internacao;
	private String nomeMedico;
	private String leito;
	
	private String quarto;
	private String unidadeFuncional;
	private String clinica;
	
	
	public PacienteProfissionalEspecialidadeVO() {
	}
	
	public PacienteProfissionalEspecialidadeVO(String prontuario,
			String nomePaciente, Date internacao, String nomeMedico,
			String leito, String quarto, String unidadeFuncional,
			String clinica) {

		this.prontuario = prontuario;
		this.nomePaciente = nomePaciente;
		this.internacao = internacao;
		this.nomeMedico = nomeMedico;
		this.leito = leito;
		this.quarto = quarto;
		this.unidadeFuncional = unidadeFuncional;
		this.clinica = clinica;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getInternacao() {
		return internacao;
	}

	public void setInternacao(Date internacao) {
		this.internacao = internacao;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getQuarto() {
		return quarto;
	}

	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}

	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica = clinica;
	}
	
	
	
}
