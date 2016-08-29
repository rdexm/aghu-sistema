package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class ItemRecebimentoProvisorioVO implements Serializable {

	private static final long serialVersionUID = 8919020356375252002L;

	private Short itlNumeroMaterial;
	private Short itlNumeroServico;
	private Integer parcela;
	private Integer quantidade;
	private Double valor;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String descricaoMaterial;
	private String codigoUnidadeMedida;
	private Integer codigoServico;
	private String nomeServico;
	private String descricaoServico;
	private String descricaoMarca;
	private String nomeComercial;
	private Integer numero;
	private Integer afnNumero;
	private Boolean atraso;
	private Short seqAlm;
	private Float variacao;
	private Float variacaoAtual;
	private Boolean existeSaldo;
	private Boolean valorConfere;
	private Integer qtdSaldoAf;
	private Double valorItem;
	private Integer nrpSeq;
	private Integer nroItem;
	private Integer eslSeq;
	private Boolean existeSpe;
	private Double valorEfetivado;

	public ItemRecebimentoProvisorioVO() {

	}

	public enum Fields {
		ITL_NUMERO_MATERIAL("itlNumeroMaterial"),
		ITL_NUMERO_SERVICO("itlNumeroServico"),
		PEA_PARCELA("parcela"),
		QUANTIDADE("quantidade"),
		VALOR("valor"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		CODIGO_UNIDADE_MEDIDA_MATERIAL("codigoUnidadeMedida"),
		CODIGO_SERVICO("codigoServico"),
		NOME_SERVICO("nomeServico"),
		DESCRICAO_SERVICO("descricaoServico"),
		DESCRICAO_MARCA("descricaoMarca"),
		NOME_COMERCIAL("nomeComercial"),
		SEQ_ALMOXARIFADO("seqAlm"),
		PERC_VAR_PRECO("variacao"),
		PERC_VAR_PRECO_INICIAL("variacaoAtual"), 
 		VALOR_UNITARIO("valorItem"),
		AFN_NUMERO("afnNumero"), 
		NUMERO("numero"),
		NRP_SEQ("nrpSeq"),
		NRO_ITEM("nroItem"),
		ESL_SEQ("eslSeq"),
		VALOR_EFETIVADO("valorEfetivado"),
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getItlNumero() {
		return this.getItlNumeroMaterial() == null ? this.getItlNumeroServico() : this.getItlNumeroMaterial();
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public Integer getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoServico(String descricaoServico) {
		this.descricaoServico = descricaoServico;
	}

	public String getDescricaoServico() {
		return descricaoServico;
	}
	
	public void setDescricaoMarca(String descricaoMarca) {
		this.descricaoMarca = descricaoMarca;
	}

	public String getDescricaoMarca() {
		return descricaoMarca;
	}
	
	public void setNomeComercial(String nomeComercial) {
		this.nomeComercial = nomeComercial;
	}

	public String getNomeComercial() {
		return nomeComercial;
	}

	public void setAtraso(Boolean atraso) {
		this.atraso = atraso;
	}

	public Boolean getAtraso() {
		return atraso;
	}

	public void setSeqAlm(Short seqAlm) {
		this.seqAlm = seqAlm;
	}

	public Short getSeqAlm() {
		return seqAlm;
	}

	public void setVariacao(Float variacao) {
		this.variacao = variacao;
	}

	public Float getVariacao() {
		return variacao;
	}

	public Boolean getExisteSaldo() {
		return existeSaldo;
	}

	public void setExisteSaldo(Boolean existeSaldo) {
		this.existeSaldo = existeSaldo;
	}

	public Boolean getValorConfere() {
		return valorConfere;
	}

	public void setValorConfere(Boolean valorConfere) {
		this.valorConfere = valorConfere;
	}

	public Integer getQtdSaldoAf() {
		return qtdSaldoAf;
	}

	public void setQtdSaldoAf(Integer qtdSaldoAf) {
		this.qtdSaldoAf = qtdSaldoAf;
	}

	public Double getValorItem() {
		return valorItem;
	}

	public void setValorItem(Double valorItem) {
		this.valorItem = valorItem;
	}

	public Integer getNrpSeq() {
		return nrpSeq;
	}

	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	public Integer getNroItem() {
		return nroItem;
	}

	public void setNroItem(Integer nroItem) {
		this.nroItem = nroItem;
	}
	
	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	public Boolean getExisteSpe() {
		return existeSpe;
	}

	public void setExisteSpe(Boolean existeSpe) {
		this.existeSpe = existeSpe;
	}

	public void setVariacaoAtual(Float variacaoAtual) {
		this.variacaoAtual = variacaoAtual;
	}

	public Float getVariacaoAtual() {
		return variacaoAtual;
	}

	public void setItlNumeroMaterial(Short itlNumeroMaterial) {
		this.itlNumeroMaterial = itlNumeroMaterial;
	}

	public Short getItlNumeroMaterial() {
		return itlNumeroMaterial;
	}

	public void setItlNumeroServico(Short itlNumeroServico) {
		this.itlNumeroServico = itlNumeroServico;
	}

	public Short getItlNumeroServico() {
		return itlNumeroServico;
	}
	
	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}
}
