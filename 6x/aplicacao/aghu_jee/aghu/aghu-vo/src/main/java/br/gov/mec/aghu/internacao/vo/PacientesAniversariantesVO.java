package br.gov.mec.aghu.internacao.vo;

/**
 *
 * Objeto de visualização do relatório de pacientes aniversáriantes.
 * 
 * @author bsoliveira
 *
 */
public class PacientesAniversariantesVO {
	
	private String andar;
	
	private String ala;
	
	private String leito;
	
	private String prontuario;
	
	private String nomePaciente;
	
	private String dataNascimento;

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}

	public String getAla() {
		return ala;
	}

	public void setAla(String ala) {
		this.ala = ala;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
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

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	
	
}
