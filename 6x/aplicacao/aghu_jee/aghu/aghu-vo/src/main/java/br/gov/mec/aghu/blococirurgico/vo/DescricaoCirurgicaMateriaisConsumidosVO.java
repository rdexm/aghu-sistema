package br.gov.mec.aghu.blococirurgico.vo;

public class DescricaoCirurgicaMateriaisConsumidosVO {

	private Short seqItemReq;
	private String material;
	private Integer qtdeAutorizada;
	private Integer qtdeDispensada;
	private Integer qtdeUtilizada;
	private Boolean incompativel;
	private String motivoIncompatibilidade;
	private String itemSus;
	private Short seqRequisicaoOpme;
	
	public enum Fields {

		MATERIAL("material"),
		QTDE_AUTORIZADA("qtdeAutorizada"),
		QTDE_DISPENSADA("qtdeDispensada"),
		QTDE_UTILIZADA("qtdeUtilizada"),
		INCOMPATIVEL("incompativel"),
		MOTIVO_INCOMPATIBILIDADE("motivoIncompatibilidade"),
		ITEM_SUS("itemSus"),
		SEQ_REQUISICAO_OPME("seqRequisicaoOpme"),
		SEQ_ITEM_REQ("seqItemReq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Integer getQtdeAutorizada() {
		return qtdeAutorizada;
	}

	public void setQtdeAutorizada(Integer qtdeAutorizada) {
		this.qtdeAutorizada = qtdeAutorizada;
	}

	public Integer getQtdeDispensada() {
		return qtdeDispensada;
	}

	public void setQtdeDispensada(Integer qtdeDispensada) {
		this.qtdeDispensada = qtdeDispensada;
	}

	public Integer getQtdeUtilizada() {
		return qtdeUtilizada;
	}

	public void setQtdeUtilizada(Integer qtdeUtilizada) {
		this.qtdeUtilizada = qtdeUtilizada;
	}
	
	public Boolean getIncompativel() {
		return incompativel;
	}

	public void setIncompativel(Boolean incompativel) {
		this.incompativel = incompativel;
	}

	public String getMotivoIncompatibilidade() {
		return motivoIncompatibilidade;
	}

	public void setMotivoIncompatibilidade(String motivoIncompatibilidade) {
		this.motivoIncompatibilidade = motivoIncompatibilidade;
	}

	public Short getSeqRequisicaoOpme() {
		return seqRequisicaoOpme;
	}

	public void setSeqRequisicaoOpme(Short seqRequisicaoOpme) {
		this.seqRequisicaoOpme = seqRequisicaoOpme;
	}

	public String getItemSus() {
		return itemSus;
	}

	public void setItemSus(String itemSus) {
		this.itemSus = itemSus;
	}

	public Short getSeqItemReq() {
		return seqItemReq;
	}

	public void setSeqItemReq(Short seqItemReq) {
		this.seqItemReq = seqItemReq;
	}
	
}
