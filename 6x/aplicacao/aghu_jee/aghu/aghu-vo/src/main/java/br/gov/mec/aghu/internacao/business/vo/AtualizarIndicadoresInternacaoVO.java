package br.gov.mec.aghu.internacao.business.vo;


public class AtualizarIndicadoresInternacaoVO {

	private Boolean indPacienteInternado;
	private Boolean indSaidaPaciente;

	public AtualizarIndicadoresInternacaoVO() {
	}

	public AtualizarIndicadoresInternacaoVO(Boolean indPacienteInternado,
			Boolean indSaidaPaciente) {
		this.indPacienteInternado = indPacienteInternado;
		this.indSaidaPaciente = indSaidaPaciente;
	}

	public Boolean getIndPacienteInternado() {
		return this.indPacienteInternado;
	}

	public void setIndPacienteInternado(Boolean indPacienteInternado) {
		this.indPacienteInternado = indPacienteInternado;
	}

	public Boolean getIndSaidaPaciente() {
		return this.indSaidaPaciente;
	}

	public void setIndSaidaPaciente(Boolean indSaidaPaciente) {
		this.indSaidaPaciente = indSaidaPaciente;
	}

}