package br.gov.mec.aghu.compras.vo;


public class ValoresProgramadosVO {

	private Double valorTotal;
	private Double valorEfetivado;

	public enum Fields {
		VALOR_TOTAL("valorTotal"), VALOR_EFETIVADO("valorEfetivado");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

}