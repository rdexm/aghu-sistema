package br.gov.mec.aghu.estoque.vo;

public class MarcaModeloMaterialVO {

	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer codigoMarcaComercial;
	private String descricaoMarcaComercial;
	private Integer seqpMarcaModelo;
	private String descricaoMarcaModelo;
	private Integer codigoMarcaModelo;

	public enum Fields {

		CODIGO_MATERIAL("codigoMaterial"), NOME_MATERIAL("nomeMaterial"), CODIGO_MARCA_COMERCIAL("codigoMarcaComercial"), DESCRICAO_MARCA_COMERCIAL(
				"descricaoMarcaComercial"), SEQP_MARCA_MODELO("seqpMarcaModelo"), DESCRICAO_MARCA_MODELO("descricaoMarcaModelo"), CODIGO_MARCA_MODELO(
				"codigoMarcaModelo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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

	public Integer getCodigoMarcaComercial() {
		return codigoMarcaComercial;
	}

	public void setCodigoMarcaComercial(Integer codigoMarcaComercial) {
		this.codigoMarcaComercial = codigoMarcaComercial;
	}

	public String getDescricaoMarcaComercial() {
		return descricaoMarcaComercial;
	}

	public void setDescricaoMarcaComercial(String descricaoMarcaComercial) {
		this.descricaoMarcaComercial = descricaoMarcaComercial;
	}

	public Integer getSeqpMarcaModelo() {
		return seqpMarcaModelo;
	}

	public void setSeqpMarcaModelo(Integer seqpMarcaModelo) {
		this.seqpMarcaModelo = seqpMarcaModelo;
	}

	public String getDescricaoMarcaModelo() {
		return descricaoMarcaModelo;
	}

	public void setDescricaoMarcaModelo(String descricaoMarcaModelo) {
		this.descricaoMarcaModelo = descricaoMarcaModelo;
	}

	public Integer getCodigoMarcaModelo() {
		return codigoMarcaModelo;
	}

	public void setCodigoMarcaModelo(Integer codigoMarcaModelo) {
		this.codigoMarcaModelo = codigoMarcaModelo;
	}

}
