package br.gov.mec.aghu.blococirurgico.vo;

public class InfMateriaisNaoCompativeisVO {

	private String codigoDescricaoMaterial;
	private String licitado;
	private Integer qtdeSolicitado;
	private Integer qtdeSus;
	private Double vlrUnitario;
	private Double vlrTotalSolicitado;
	private Double vlrTabelaSus;
	private Double diferencaValor;
	private Integer materialNovo;

	public enum Fields {	
		
		CODIGO_DESCRICAO_MATERIAL("codigoDescricaoMaterial"),
		LICITADO("licitado"),
		QTDE_SOLICITADO("qtdeSolicitado"),
		QTDE_SUS("qtdeSus"),
		VLR_UNITARIO("vlrUnitario"),
		VLR_TOTAL_SOLICITADO("vlrTotalSolicitado"),
		VLR_TABELA_SUS("vlrTabelaSus"),
		DIFERENCA_VALOR("diferencaValor"),
		MATERIAL_NOVO("materialNovo");
	
		private String fields;
	
		private Fields(String fields) {
			this.fields = fields;
		}
	
		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	
	public String getCodigoDescricaoMaterial() {
		return codigoDescricaoMaterial;
	}

	public void setCodigoDescricaoMaterial(String codigoDescricaoMaterial) {
		this.codigoDescricaoMaterial = codigoDescricaoMaterial;
	}

	public String getLicitado() {
		return licitado;
	}

	public void setLicitado(String licitado) {
		this.licitado = licitado;
	}

	public Integer getQtdeSolicitado() {
		return qtdeSolicitado;
	}

	public void setQtdeSolicitado(Integer qtdeSolicitado) {
		this.qtdeSolicitado = qtdeSolicitado;
	}

	public Integer getQtdeSus() {
		return qtdeSus;
	}

	public void setQtdeSus(Integer qtdeSus) {
		this.qtdeSus = qtdeSus;
	}

	public Double getVlrUnitario() {
		return vlrUnitario;
	}

	public void setVlrUnitario(Double vlrUnitario) {
		this.vlrUnitario = vlrUnitario;
	}

	public Double getVlrTotalSolicitado() {
		return vlrTotalSolicitado;
	}

	public void setVlrTotalSolicitado(Double vlrTotalSolicitado) {
		this.vlrTotalSolicitado = vlrTotalSolicitado;
	}

	public Double getVlrTabelaSus() {
		return vlrTabelaSus;
	}

	public void setVlrTabelaSus(Double vlrTabelaSus) {
		this.vlrTabelaSus = vlrTabelaSus;
	}

	public Double getDiferencaValor() {
		return diferencaValor;
	}

	public void setDiferencaValor(Double diferencaValor) {
		this.diferencaValor = diferencaValor;
	}

	public Integer getMaterialNovo() {
		return materialNovo;
	}

	public void setMaterialNovo(Integer materialNovo) {
		this.materialNovo = materialNovo;
	}

}
