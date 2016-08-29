package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class SubRelatorioNotasDeConsumoDaSalaMateriaisVO implements Serializable {
	
	private static final long serialVersionUID = -2047178099786822060L;

	private String descricaoMaterial;  //91
	private Short ordem; 
	
	public SubRelatorioNotasDeConsumoDaSalaMateriaisVO() {
		super();
	}
	
	public SubRelatorioNotasDeConsumoDaSalaMateriaisVO(String descricaoMaterial, Short ordem) {
		super();
		this.descricaoMaterial = descricaoMaterial;
		this.ordem = ordem;
	}

	public enum Fields {

		DESCRICAO_MATERIAL("descricaoMaterial"),
		ORDEM("ordem");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	//Getters and Setters
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
}