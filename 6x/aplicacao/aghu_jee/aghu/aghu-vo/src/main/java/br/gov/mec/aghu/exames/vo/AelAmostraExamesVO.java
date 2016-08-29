package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;

public class AelAmostraExamesVO {
	
	private Short seqVO;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Integer amoSoeSeq;
	private Integer amoSeqp;
	private Boolean selecionado;
	private Integer numeroMapa;
	private String descricaoUsual;
	private String descricaoMaterial;
	private DominioSituacaoAmostra situacao;
	
	
	public Short getSeqVO() {
		return seqVO;
	}
	public void setSeqVO(Short seqVO) {
		this.seqVO = seqVO;
	}
	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}
	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}
	public Short getIseSeqp() {
		return iseSeqp;
	}
	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
	public Integer getAmoSoeSeq() {
		return amoSoeSeq;
	}
	public void setAmoSoeSeq(Integer amoSoeSeq) {
		this.amoSoeSeq = amoSoeSeq;
	}
	public Integer getAmoSeqp() {
		return amoSeqp;
	}
	public void setAmoSeqp(Integer amoSeqp) {
		this.amoSeqp = amoSeqp;
	}
	public Boolean getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	public Integer getNumeroMapa() {
		return numeroMapa;
	}
	public void setNumeroMapa(Integer numeroMapa) {
		this.numeroMapa = numeroMapa;
	}
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public DominioSituacaoAmostra getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoAmostra situacao) {
		this.situacao = situacao;
	}
	
	public enum Fields {

		EMA_EXA_SIGLA("id.emaExaSigla"),
		EMA_MAN_SEQ("id.emaManSeq"),
		CAL_SEQ("id.calSeq"),
		SITUACAO("situacao"),
		EXAME_MATERIAL_ANALISE("exameMaterialAnalise"),
		CAMPO_LAUDO("campoLaudo");

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
