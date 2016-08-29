package br.gov.mec.aghu.blococirurgico.vo;

public class RelatorioLaudoAIHSolicVO {

	private String nomeHospital;
	private String cgcHospital;
	private String nomePaciente;
	private String nomeMedico;
	private Long cpfMedico;
	private Short crmMedico;
	private String materialSolicitado;
	
	public String getNomeHospital() {
		return nomeHospital;
	}
	public void setNomeHospital(String nomeHospital) {
		this.nomeHospital = nomeHospital;
	}
	public String getCgcHospital() {
		return cgcHospital;
	}
	public void setCgcHospital(String cgcHospital) {
		this.cgcHospital = cgcHospital;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getNomeMedico() {
		return nomeMedico;
	}
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
	public Long getCpfMedico() {
		return cpfMedico;
	}
	public void setCpfMedico(Long cpfMedico) {
		this.cpfMedico = cpfMedico;
	}
	public Short getCrmMedico() {
		return crmMedico;
	}
	public void setCrmMedico(Short crmMedico) {
		this.crmMedico = crmMedico;
	}
	public String getMaterialSolicitado() {
		return materialSolicitado;
	}
	public void setMaterialSolicitado(String materialSolicitado) {
		this.materialSolicitado = materialSolicitado;
	}	
}
