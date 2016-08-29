package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class RelatorioConsumoSinteticoMaterialVO implements  Comparable<RelatorioConsumoSinteticoMaterialVO> {

	private Integer codigoCentroCusto;
	private String descricaoCentroCusto;
	private String nomeMaterial;
	private Integer codigoMaterial;
	private ScoUnidadeMedida unidadeMedida;
	private Integer quantidade;
	private BigDecimal custoMedioPonderado;
	private BigDecimal valor;
	private String unidadeMedidaCodigo;
	private BigDecimal percentual;
	
	public enum Fields {
		CODIGO_CENTRO_CUSTO("codigoCentroCusto"),
		DESCRICAO_CENTRO_CUSTO("descricaoCentroCusto"),
		NOME_MATERIAL("nomeMaterial"),
		CODIGO_MATERIAL("codigoMaterial"),
		UNIDADE_MEDIDA("unidadeMedida"),
		QUANTIDADE("quantidade"),
		CUSTO_MEDIO_PONDERADO("custoMedioPonderado"),
		VALOR("valor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int compareTo(RelatorioConsumoSinteticoMaterialVO other) {
		// Compara código do centro de custo
		int result = this.getCodigoCentroCusto().compareTo(other.getCodigoCentroCusto());
		if (result == 0) {
			if(this.getDescricaoCentroCusto() != null && other.getDescricaoCentroCusto() != null){
				// Compara descrição do centro de custo
				result = this.getDescricaoCentroCusto().compareTo(other.getDescricaoCentroCusto());
				 if (result == 0) {
					 if(this.getValor() != null && other.getValor() != null){
						 // Compara pelo valor
						 result = this.getValor().compareTo(other.getValor());
					 }
				 }
			}
		}
        return result;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}

	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	
	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
		if(unidadeMedida!=null){
			this.unidadeMedidaCodigo = this.unidadeMedida.getCodigo();
		}
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getCustoMedioPonderado() {
		return custoMedioPonderado;
	}

	public void setCustoMedioPonderado(BigDecimal custoMedioPonderado) {
		this.custoMedioPonderado = custoMedioPonderado;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	
	public String getunidadeMedidaCodigo() {
		return unidadeMedidaCodigo;
	}
	
	public void setunidadeMedidaCodigoo(String unidadeMedidaCodigo) {
		this.unidadeMedidaCodigo = unidadeMedidaCodigo;
	}
	
	public BigDecimal getPercentual() {
		return percentual;
	}
	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	
}
