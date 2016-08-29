package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;

public class LocalizacaoFiltroVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 578018115246354372L;
	
	private Long seq;
	private String nome;
	private String descricao;
	private DominioSituacao indSituacao;
	private PtmEdificacaoVO edificacao;
	private FccCentroCustos centroCusto;
	private String edificacaoNome;
	private Integer edificacaoSeq;
	private String cCustoDescricao;
	private Integer cCustoCodigo;
	
	public String getEdificacaoNome() {
		return edificacaoNome;
	}
	public void setEdificacaoNome(String edificacaoNome) {
		this.edificacaoNome = edificacaoNome;
	}
	public String getcCustoDescricao() {
		return cCustoDescricao;
	}
	public void setcCustoDescricao(String cCustoDescricao) {
		this.cCustoDescricao = cCustoDescricao;
	}
	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		DESCRICAO("descricao"),
		EDIFICACAO("edificacao"),
		IND_SITUACAO("indSituacao"),
		CENTRO_CUSTO("centroCusto"),
		EDIFICACAO_NOME("edificacaoNome"),
		CENTRO_CUSTO_DESCRICAO("cCustoDescricao"),
		SEQ_EDIFICACAO("edificacaoSeq"),
		CENTRO_CUSTO_CODIGO("cCustoCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public String concatenarCodigoDescricao(){
		return this.getcCustoCodigo() + " - " + this.getcCustoDescricao();
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public PtmEdificacaoVO getEdificacao() {
		return edificacao;
	}
	public void setEdificacao(PtmEdificacaoVO edificacao) {
		this.edificacao = edificacao;
	}
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}
	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public Integer getcCustoCodigo() {
		return cCustoCodigo;
	}
	public void setcCustoCodigo(Integer cCustoCodigo) {
		this.cCustoCodigo = cCustoCodigo;
	}
	public Integer getEdificacaoSeq() {
		return edificacaoSeq;
	}
	public void setEdificacaoSeq(Integer edificacaoSeq) {
		this.edificacaoSeq = edificacaoSeq;
	}
	

	
}
