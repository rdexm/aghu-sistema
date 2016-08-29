package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ListaMateriaisRequisicaoOpmesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3477513880572801509L;
	private String codigoDescricaoMaterial;
	private String compativel;
	private String licitado;
	private Integer quantidadeSolicitada;
	private Integer quantidadeAutorizadaHospital;
	private BigDecimal valorUnitario;
	private Double valorTotalSolicitado;
	private BigDecimal valorTabelaSus;
	private Double diferencaValor;
	private byte[] anexoOrcamento;

	public enum Fields {

		CODIGO_DESCRICAO_MATERIAL("codigoDescricaoMaterial"), 
		COMPATIVEL("compativel"), 
		LICITADO("licitado"), 
		QUANTIDADE_SOLICITADA("quantidadeSolicitada"), 
		QUANTIDADE_AUTORIZADA_HOSPITAL("quantidadeAutorizadaHospital"), 
		VALOR_UNITARIO("valorUnitario"), 
		VALOR_TOTAL_SOLICITADO("valorTotalSolicitado"), 
		VALOR_TABELA_SUS("valorTabelaSus"),
		DIFERENCA_VALOR("diferencaValor"),
		ANEXO("anexoOrcamento");

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

	public String getCompativel() {
		return compativel;
	}

	public void setCompativel(String compativel) {
		this.compativel = compativel;
	}

	public String getLicitado() {
		return licitado;
	}

	public void setLicitado(String licitado) {
		this.licitado = licitado;
	}

	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	public Integer getQuantidadeAutorizadaHospital() {
		return quantidadeAutorizadaHospital;
	}

	public void setQuantidadeAutorizadaHospital(Integer quantidadeAutorizadaHospital) {
		this.quantidadeAutorizadaHospital = quantidadeAutorizadaHospital;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorTotalSolicitado() {
		return valorTotalSolicitado;
	}

	public void setValorTotalSolicitado(Double valorTotalSolicitado) {
		this.valorTotalSolicitado = valorTotalSolicitado;
	}

	public BigDecimal getValorTabelaSus() {
		return valorTabelaSus;
	}

	public void setValorTabelaSus(BigDecimal valorTabelaSus) {
		this.valorTabelaSus = valorTabelaSus;
	}

	public Double getDiferencaValor() {
		return diferencaValor;
	}

	public void setDiferencaValor(Double diferencaValor) {
		this.diferencaValor = diferencaValor;
	}

	public byte[] getAnexoOrcamento() {
		return anexoOrcamento;
	}

	public void setAnexoOrcamento(byte[] anexoOrcamento) {
		this.anexoOrcamento = anexoOrcamento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(codigoDescricaoMaterial == null){
			result = prime * result + 0;
		}else{
			result = prime * result + codigoDescricaoMaterial.hashCode(); 
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ListaMateriaisRequisicaoOpmesVO)) {
			return false;
		}
		ListaMateriaisRequisicaoOpmesVO other = (ListaMateriaisRequisicaoOpmesVO) obj;
		if (codigoDescricaoMaterial == null) {
			if (other.codigoDescricaoMaterial != null) {
				return false;
			}
		} else if (!codigoDescricaoMaterial
				.equals(other.codigoDescricaoMaterial)) {
			return false;
		}
		return true;
	}

}
