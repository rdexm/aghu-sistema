package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class EquipamentoSistemaPatrimonioVO implements java.io.Serializable {

	private static final long serialVersionUID = 5867281234640453342L;

	public EquipamentoSistemaPatrimonioVO(){
		
	}
	
	private final String EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA = "^0*";
	
	private String codigo;
	private BigDecimal valor;
	private BigDecimal retorno;
	private String conta;
	private String descricao;
	private BigDecimal valorDepreciado;
	

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}


	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValorDepreciado() {
		return valorDepreciado;
	}

	public void setValorDepreciado(BigDecimal valorDepreciado) {
		this.valorDepreciado = valorDepreciado;
	}

	public BigDecimal getRetorno() {
		return retorno;
	}

	public void setRetorno(BigDecimal retorno) {
		this.retorno = retorno;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getCodigo() {
		
		return codigo;
	}

	public void setCodigo(String codigo) {
		if (codigo == null) {
			this.codigo = null;
		} else {
			this.codigo = codigo.replaceFirst(
					EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA, "");
		}
	}
	
	public enum Fields {
		CODIGO("codigo"),
		VALOR("valor"),
		RETORNO("retorno"),
		CONTA("conta"), 
		DESCRICAO("descricao"), 
		VALORDEPRECIADO("valor-depreciado");

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
