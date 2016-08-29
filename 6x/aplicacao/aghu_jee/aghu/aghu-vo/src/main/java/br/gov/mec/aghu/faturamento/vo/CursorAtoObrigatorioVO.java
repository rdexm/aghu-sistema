package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoQuantidadeProced;




public class CursorAtoObrigatorioVO {

	private Integer eaiCthSeq;
	private Long codTabela;
	private String descricao;
	private Long codTabelaAto;
	private String descricaoAto;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Short iphPhoSeqCobrado;
	private Integer iphSeqCobrado;
	private Integer tivSeq;
	private Byte taoSeq;
	private Short quantidade;
	private DominioTipoQuantidadeProced tipoQuantidade;
	private Date dthrRealizado;
	
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
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	public DominioTipoQuantidadeProced getTipoQuantidade() {
		return tipoQuantidade;
	}
	public void setTipoQuantidade(DominioTipoQuantidadeProced tipoQuantidade) {
		this.tipoQuantidade = tipoQuantidade;
	}
	public Date getDthrRealizado() {
		return dthrRealizado;
	}
	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}
	
		
}