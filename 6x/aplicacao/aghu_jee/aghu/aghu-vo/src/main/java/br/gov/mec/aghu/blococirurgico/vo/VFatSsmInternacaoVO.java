package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

public class VFatSsmInternacaoVO implements java.io.Serializable {

	private static final long serialVersionUID = -919149445993441359L;

	private Date dthrCriacao;
	private String sinaisSintomas;
	private String condicoes;
	private String resultadosProvas;
	private String cidCodigo;
	private String cidDescricao;
	private String cidCodigoSec;
	private Long codTabela;
	private String descricaoItemProcedimento;
	private String descricaoProcedimento;
	private Short prioridade;
	
	private Integer iphSeq;
	private Short phoSeq;
	private String descricaoSinonimo;
	
	public VFatSsmInternacaoVO(){}
	
	public VFatSsmInternacaoVO(Date dthrCriacao, String sinaisSintomas,
			String condicoes, String resultadosProvas, String cidCodigo,
			String cidDescricao, String cidCodigoSec, Long codTabela,
			String descricaoItemProcedimento, String descricaoProcedimento,
			Short prioridade) {
		super();
		this.dthrCriacao = dthrCriacao;
		this.sinaisSintomas = sinaisSintomas;
		this.condicoes = condicoes;
		this.resultadosProvas = resultadosProvas;
		this.cidCodigo = cidCodigo;
		this.cidDescricao = cidDescricao;
		this.cidCodigoSec = cidCodigoSec;
		this.codTabela = codTabela;
		this.descricaoItemProcedimento = descricaoItemProcedimento;
		this.descricaoProcedimento = descricaoProcedimento;
		this.prioridade = prioridade;
	}

	public Date getDthrCriacao() {
		return dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	public String getSinaisSintomas() {
		return sinaisSintomas;
	}

	public String getCondicoes() {
		return condicoes;
	}

	public String getResultadosProvas() {
		return resultadosProvas;
	}

	public String getCidCodigo() {
		return cidCodigo;
	}

	public String getCidDescricao() {
		return cidDescricao;
	}

	public String getCidCodigoSec() {
		return cidCodigoSec;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public String getDescricaoItemProcedimento() {
		return descricaoItemProcedimento;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setSinaisSintomas(String sinaisSintomas) {
		this.sinaisSintomas = sinaisSintomas;
	}

	public void setCondicoes(String condicoes) {
		this.condicoes = condicoes;
	}

	public void setResultadosProvas(String resultadosProvas) {
		this.resultadosProvas = resultadosProvas;
	}

	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}

	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}

	public void setCidCodigoSec(String cidCodigoSec) {
		this.cidCodigoSec = cidCodigoSec;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public void setDescricaoItemProcedimento(String descricaoItemProcedimento) {
		this.descricaoItemProcedimento = descricaoItemProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}
	
	public Short getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Short prioridade) {
		this.prioridade = prioridade;
	}
	

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public String getDescricaoSinonimo() {
		return descricaoSinonimo;
	}

	public void setDescricaoSinonimo(String descricaoSinonimo) {
		this.descricaoSinonimo = descricaoSinonimo;
	}
	
	public enum Fields {
		DTHR_CRIACAO("dthrCriacao"), 
		SINAIS_SINTOMAS("sinaisSintomas"), 
		CONDICOES("condicoes"), 
		RESULTADOS_PROVAS("resultadosProvas"), 
		CID_CODIGO("cidCodigo"), 
		CID_DESCRICAO("cidDescricao"), 
		CID_CODIGO_SEC("cidCodigoSec"),
		COD_TABELA("codTabela"),
		DESCRICAO_ITEM_PROCEDIMENTO ("descricaoItemProcedimento"),
		DESCRICAO_PROCEDIMENTO ("descricaoProcedimento"),
		PRIORIDADE ("prioridade"),
		IPH_SEQ("iphSeq"),
		PHO_SEQ("phoSeq"),
		DESC_SINONIMO("descricaoSinonimo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
