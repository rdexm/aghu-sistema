package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorAtoFatcBuscaServClassVO implements Serializable {
		
	private static final long serialVersionUID = 1298777979584794137L;

	private Integer eaiCthSeq;
	
	private Long codTabela;
	private String descricaoIph;

	private Long codTabelaAto;
	private String descricaoAto;

	private Short iphPhoSeq;
	private Integer iphSeq;

	private Short iphPhoSeqCobrado;
	private Integer iphSeqCobrado;

	private Integer tivSeq;
	private Byte taoSeq;
	
	private Short quantidade;
	
	// DominioTipoQuantidadeProced
	private String tipoQuantidade;

	// DominioIndOrigemItemContaHospitalar
	private String indOrigem;

	private Short ufeUnfseq;
	private Short unfSeq;
	private Integer ipsRmpSeq;
	
	public enum Fields {
		EAI_CTH_SEQ("eaiCthSeq"),
		COD_TABELA("codTabela"),
		DESCRICAO_IPH("descricaoIph"), 
		COD_TABELA_ATO("codTabelaAto"),
		DESCRICAO_ATO("descricaoAto"),

		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_SEQ("iphSeq"),
		
		IPH_PHO_SEQ_COBRADO("iphPhoSeqCobrado"),
		IPH_SEQ_COBRADO("iphSeqCobrado"),
		
		TIV_SEQ("tivSeq"),
		TAO_SEQ("taoSeq"),
		
		QUANTIDADE("quantidade"),
		TIPO_QUANTIDADE("tipoQuantidade"),
		IND_ORIGEM("indOrigem"),

		UFE_UNF_SEQ("ufeUnfseq"),
		UNF_SEQ("unfSeq"),
		IPS_RMP_SEQ("ipsRmpSeq");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getEaiCthSeq() {
		return eaiCthSeq;
	}

	public void setEaiCthSeq(Integer eaiCthSeq) {
		this.eaiCthSeq = eaiCthSeq;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricaoIph() {
		return descricaoIph;
	}

	public void setDescricaoIph(String descricaoIph) {
		this.descricaoIph = descricaoIph;
	}

	public Long getCodTabelaAto() {
		return codTabelaAto;
	}

	public void setCodTabelaAto(Long codTabelaAto) {
		this.codTabelaAto = codTabelaAto;
	}

	public String getDescricaoAto() {
		return descricaoAto;
	}

	public void setDescricaoAto(String descricaoAto) {
		this.descricaoAto = descricaoAto;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Short getIphPhoSeqCobrado() {
		return iphPhoSeqCobrado;
	}

	public void setIphPhoSeqCobrado(Short iphPhoSeqCobrado) {
		this.iphPhoSeqCobrado = iphPhoSeqCobrado;
	}

	public Integer getIphSeqCobrado() {
		return iphSeqCobrado;
	}

	public void setIphSeqCobrado(Integer iphSeqCobrado) {
		this.iphSeqCobrado = iphSeqCobrado;
	}

	public Integer getTivSeq() {
		return tivSeq;
	}

	public void setTivSeq(Integer tivSeq) {
		this.tivSeq = tivSeq;
	}

	public Byte getTaoSeq() {
		return taoSeq;
	}

	public void setTaoSeq(Byte taoSeq) {
		this.taoSeq = taoSeq;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public String getTipoQuantidade() {
		return tipoQuantidade;
	}

	public void setTipoQuantidade(String tipoQuantidade) {
		this.tipoQuantidade = tipoQuantidade;
	}

	public String getIndOrigem() {
		return indOrigem;
	}

	public void setIndOrigem(String indOrigem) {
		this.indOrigem = indOrigem;
	}

	public Short getUfeUnfseq() {
		return ufeUnfseq;
	}

	public void setUfeUnfseq(Short ufeUnfseq) {
		this.ufeUnfseq = ufeUnfseq;
	}

	public Integer getIpsRmpSeq() {
		return ipsRmpSeq;
	}

	public void setIpsRmpSeq(Integer ipsRmpSeq) {
		this.ipsRmpSeq = ipsRmpSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
}