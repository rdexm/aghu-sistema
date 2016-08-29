package br.gov.mec.aghu.estoque.vo;

public class MaterialClassificacaoVO {

	private String nomeMaterial;
	private Integer codMaterial;
	private Long cn5;
	private String unidade;
	private Boolean selecionado;

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Integer getCodMaterial() {
		return codMaterial;
	}

	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}

	public Long getCn5() {
		return cn5;
	}

	public void setCn5(Long cn5) {
		this.cn5 = cn5;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public enum Fields {

		NOME_MATERIAL("nomeMaterial"), COD_MATERIAL("codMaterial"), CN5("cn5"), UNIDADE("unidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
