package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class DemoFinanceiroOPMEVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7416608662364724322L;
	/**
	 * 
	 */

	private String especialidade; 
	private String paciente; 
	private String materialHospitalar; 
	private Double valorCompativel;
	private Double valorIncompativel;
	
	public enum Fields {

		ESPECIALIDADE("especialidade"), 
		PACIENTE("paciente"),
		MATERIALHOSPITALAR("materialHospitalar"), 
		VALORCOMPATIVEL("valorCompativel"), 
		VALORINCOMPATIVEL("valorIncompativel"); 
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	


	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getMaterialHospitalar() {
		return materialHospitalar;
	}

	public void setMaterialHospitalar(String materialHospitalar) {
		this.materialHospitalar = materialHospitalar;
	}

	public Double getValorCompativel() {
		return valorCompativel;
	}

	public void setValorCompativel(Double valorCompativel) {
		this.valorCompativel = valorCompativel;
	}

	public Double getValorIncompativel() {
		return valorIncompativel;
	}

	public void setValorIncompativel(Double valorIncompativel) {
		this.valorIncompativel = valorIncompativel;
	}

}
