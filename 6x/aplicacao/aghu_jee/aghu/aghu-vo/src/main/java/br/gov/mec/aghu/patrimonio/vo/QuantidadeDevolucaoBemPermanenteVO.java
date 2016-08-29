package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

public class QuantidadeDevolucaoBemPermanenteVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6380798975171814074L;

	private Integer quantidade;
	
	private Long quantidadeDisponivel;

	public enum Fields {
		
		QTD("quantidade"),
		QTD_DISP("quantidadeDisponivel");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Long getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(Long quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}
	
}