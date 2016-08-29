package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class MarcaComercialMaterialVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2999734850148580964L;

	private Integer codigo;
	private String descricao;
	private String unidade;
	private Double valorUnitario;
	
	
	public enum Fields {

		CODIGO("codigo"), 
		DESCRICAO("descricao"), 
		UNIDADE("unidade"),
		VALOR_UNITARIO("valorUnitario");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Double getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	
}
