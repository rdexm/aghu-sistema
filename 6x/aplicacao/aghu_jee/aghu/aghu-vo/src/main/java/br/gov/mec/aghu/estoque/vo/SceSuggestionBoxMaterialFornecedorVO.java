package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class SceSuggestionBoxMaterialFornecedorVO implements Serializable {

	private static final long serialVersionUID = -873216036442894435L;

	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer numeroPac;
	private Boolean vencedor;
	private String vencedorPorExtenso;
	private String vinculado;

	public enum Fields {
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		NUMERO_PAC("numeroPac"),
		VENCEDOR("vencedor"),
		VINCULADO("vinculado");

		private String value;

		private Fields(String value){
			this.value = value;
		}

		public String getValue(){
			return this.value;
		}
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Boolean getVencedor() {
		return vencedor;
	}

	public void setVencedor(Boolean vencedor) {
		if(vencedor){
			this.setVencedorPorExtenso("Sim");
		} else {
			this.setVencedorPorExtenso("Não");
		}
		this.vencedor = vencedor;
	}

	public String getVinculado() {
		return vinculado;
	}

	public void setVinculado(String vinculado) {
		this.vinculado = vinculado;
	}

	public String getVencedorPorExtenso() {
		return vencedorPorExtenso;
	}

	public void setVencedorPorExtenso(String vencedorPorExtenso) {
		this.vencedorPorExtenso = vencedorPorExtenso;
	}
}
