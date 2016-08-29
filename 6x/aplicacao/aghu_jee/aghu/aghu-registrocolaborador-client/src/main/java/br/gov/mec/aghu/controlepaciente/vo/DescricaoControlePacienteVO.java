package br.gov.mec.aghu.controlepaciente.vo;

public class DescricaoControlePacienteVO {

	private String siglaConselho;
	private String nroRegistroConselho;
	
	public DescricaoControlePacienteVO() {
		
	}

	public String getSiglaConselho() {
		return siglaConselho;
	}

	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}

	public String getNroRegistroConselho() {
		return nroRegistroConselho;
	}

	public void setNroRegistroConselho(String nroRegistroConselho) {
		this.nroRegistroConselho = nroRegistroConselho;
	}
	
	public String getSiglaNumeroConselho() {		
		return this.siglaConselho + " " + this.nroRegistroConselho;
	}
}