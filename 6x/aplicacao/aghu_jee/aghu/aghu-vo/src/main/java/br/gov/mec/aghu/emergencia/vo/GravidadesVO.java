package br.gov.mec.aghu.emergencia.vo;

public class GravidadesVO {
	
	private Short seqAgrupamentoGravidade;
	private Short seqGravidade;
	private Long seqTriagem;
	private Integer conNumero;
	
	public GravidadesVO(Short seqAgrupamentoGravidade, Short seqGravidade,
			Long seqTriagem, Integer conNumero) {
		super();
		this.seqAgrupamentoGravidade = seqAgrupamentoGravidade;
		this.seqGravidade = seqGravidade;
		this.seqTriagem = seqTriagem;
		this.conNumero = conNumero;
	}
	
	public Short getSeqAgrupamentoGravidade() {
		return seqAgrupamentoGravidade;
	}
	public void setSeqAgrupamentoGravidade(Short seqAgrupamentoGravidade) {
		this.seqAgrupamentoGravidade = seqAgrupamentoGravidade;
	}
	public Short getSeqGravidade() {
		return seqGravidade;
	}
	public void setSeqGravidade(Short seqGravidade) {
		this.seqGravidade = seqGravidade;
	}
	public Long getSeqTriagem() {
		return seqTriagem;
	}
	public void setSeqTriagem(Long seqTriagem) {
		this.seqTriagem = seqTriagem;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	

	public enum Fields {
		SEQ_TRIAGEM("seqTriagem"),
		SEQ_GRAVIDADE("seqGravidade"),		
		SEQ_AGRUPAMENTO_GRAVIDADE("seqAgrupamentoGravidade"),
		CON_NUMERO("conNumero");

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
