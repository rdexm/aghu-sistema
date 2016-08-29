package br.gov.mec.aghu.exames.vo;


public class VAelArcoSolicitacaoVO {
	
	private Integer soeSeq;
	private Short seqp;
	private String descricaoExame;
	private String descricaoMaterialAnalise;
	private String situacaoItem;
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	public String getDescricaoExame() {
		return descricaoExame;
	}
	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}
	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}
	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}
	public String getSituacaoItem() {
		return situacaoItem;
	}
	public void setSituacaoItem(String situacaoItem) {
		this.situacaoItem = situacaoItem;
	}

}
