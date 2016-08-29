package br.gov.mec.aghu.compras.vo;

/**
 * VO responsável pelos valores de base do cálculo de saldo de AF
 * 
 * @author matheus
 */
public class BaseCalculoSaldoAfVO {
	/** Quantidade Solicitada */
	private Integer qtdeSolicitada;
	
	/** Quantidade Recebida */
	private Integer qtdeRecebida;
	
	/** Fator de Conversão */
	private Integer fatorConversao;
	
	// Getters/Setters

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public Integer getQtdeRecebida() {
		return qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}
	
	/** Campos */
	public static enum Field {
		QTDE_SOLICITADA("qtdeSolicitada"),
		QTDE_RECEBIDA("qtdeRecebida"),
		FATOR_CONVERSAO("fatorConversao");
		
		private String field;

		Field(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return field;
		}
	}
}