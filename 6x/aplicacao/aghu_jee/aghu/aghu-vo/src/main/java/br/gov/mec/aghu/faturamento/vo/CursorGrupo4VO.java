package br.gov.mec.aghu.faturamento.vo;

public class CursorGrupo4VO {
	private static final long serialVersionUID = 8426324676593447873L;

	private Long iphCodSus;
	private Short quantidade;

	public Long getIphCodSus() {
		return iphCodSus;
	}

	public void setIphCodSus(Long iphCodSus) {
		this.iphCodSus = iphCodSus;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public enum Fields {
		IPH_COD_SUS("iphCodSus"), QUANTIDADE("quantidade");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public String toString() {
		return "CursorCGrupo4 [iphCodSus=" + iphCodSus + ", quantidade=" + quantidade + "]";
	}
}
