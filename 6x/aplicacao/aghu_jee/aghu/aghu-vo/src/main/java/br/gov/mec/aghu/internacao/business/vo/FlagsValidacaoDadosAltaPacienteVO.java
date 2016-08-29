package br.gov.mec.aghu.internacao.business.vo;

public class FlagsValidacaoDadosAltaPacienteVO {
	
	//Flags para controle de exibicao das modais
	private boolean validaDados = true;
	private boolean validaDadosInformadosEstorno = true;
	private boolean validaDadosInformados = true;
	private boolean validaDadosFaturamento = true;
	private boolean validaDadosDaAltaPaciente = true;
	private boolean validaPermissoesDarAltaPaciente = true;
	
	private String mensagem = "";
	
	public boolean isValidaDados() {
		return validaDados;
	}

	public void setValidaDados(boolean validaDados) {
		this.validaDados = validaDados;
	}

	public boolean isValidaDadosInformadosEstorno() {
		return validaDadosInformadosEstorno;
	}

	public void setValidaDadosInformadosEstorno(boolean validaDadosInformadosEstorno) {
		this.validaDadosInformadosEstorno = validaDadosInformadosEstorno;
	}

	public boolean isValidaDadosInformados() {
		return validaDadosInformados;
	}

	public void setValidaDadosInformados(boolean validaDadosInformados) {
		this.validaDadosInformados = validaDadosInformados;
	}

	public boolean isValidaDadosFaturamento() {
		return validaDadosFaturamento;
	}

	public void setValidaDadosFaturamento(boolean validaDadosFaturamento) {
		this.validaDadosFaturamento = validaDadosFaturamento;
	}

	public boolean isValidaDadosDaAltaPaciente() {
		return validaDadosDaAltaPaciente;
	}

	public void setValidaDadosDaAltaPaciente(boolean validaDadosDaAltaPaciente) {
		this.validaDadosDaAltaPaciente = validaDadosDaAltaPaciente;
	}

	public boolean isValidaPermissoesDarAltaPaciente() {
		return validaPermissoesDarAltaPaciente;
	}

	public void setValidaPermissoesDarAltaPaciente(boolean validaPermissoesDarAltaPaciente) {
		this.validaPermissoesDarAltaPaciente = validaPermissoesDarAltaPaciente;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
