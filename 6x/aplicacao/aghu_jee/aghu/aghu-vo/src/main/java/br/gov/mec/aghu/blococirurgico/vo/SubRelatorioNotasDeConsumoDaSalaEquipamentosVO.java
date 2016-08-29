package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class SubRelatorioNotasDeConsumoDaSalaEquipamentosVO implements Serializable {
	
	private static final long serialVersionUID = -2047178099786822060L;

	private String descricaoEquipamento;  //77
	private Short ordem;
	
	public SubRelatorioNotasDeConsumoDaSalaEquipamentosVO() {
		super();
	}
	
	public SubRelatorioNotasDeConsumoDaSalaEquipamentosVO(String descricaoEquipamento, Short ordem) {
		super();
		this.descricaoEquipamento = descricaoEquipamento;
		this.ordem = ordem;
	}
	
	public SubRelatorioNotasDeConsumoDaSalaEquipamentosVO(String descricaoEquipamento) {
		super();
		this.descricaoEquipamento = descricaoEquipamento;
	}

	public enum Fields {

		DESCRICAO_EQUIPAMENTO("descricaoEquipamento"),
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
	public String getDescricaoEquipamento() {
		return descricaoEquipamento;
	}

	public void setDescricaoEquipamento(String descricaoEquipamento) {
		this.descricaoEquipamento = descricaoEquipamento;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
}