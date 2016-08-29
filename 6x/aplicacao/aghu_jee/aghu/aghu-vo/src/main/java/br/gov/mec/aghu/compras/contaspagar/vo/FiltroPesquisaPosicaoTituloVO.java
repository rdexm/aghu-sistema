package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;

/**
 * VO da pesquisa em #37797 – Consultar posição do Título
 * 
 * @author aghu
 *
 */
public class FiltroPesquisaPosicaoTituloVO implements Serializable {

	private static final long serialVersionUID = -7217997457838074807L;

	private Integer numero; // TTL.SEQ AS "NÚMERO"
	private Short parcela; // TTL.NRO_PARCELA AS "PARCELA"
	private DominioSituacaoTitulo situacao; // TTL.IND_SITUACAO AS "SITUAÇÃO"
	private Integer notaRecebimento; // TTL.NRS_SEQ AS "NR"
	private Integer numeroAF; // AFS.PFR_LCT_NUMERO AS "NÚMERO AF"
	private Short complemento; // AFS.NRO_COMPLEMENTO AS "COMPLEMENTO"
	private Integer bo;
	private Integer numeroContrato; // AFS.NRO_CONTRATO AS "NRO CONTRATO"
	private Date dataInicio; // TTL.DT_GERACAO AS "DATA GERAÇÃO"
	private Date dataFim; // TTL.DT_GERACAO AS "DATA GERAÇÃO"
	private Integer documento; // PGT.NRO_DOCUMENTO -- FIELDSET DOCUMENTO
	private String observacao;

	// Instâncias da SuggestionBox
	private RapServidores geradoPor; // SUGGESTIONBOX Gerado por
	private ScoFornecedor fornecedor; // SUGGESTIONBOX Fornecedor
	private RapServidores estornadoPor; // SUGGESTIONBOX Estornado Por
	private RapServidores pagoPor; // SUGGESTIONBOX Estornado Por

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Short getParcela() {
		return parcela;
	}

	public RapServidores getGeradoPor() {
		return geradoPor;
	}

	public void setGeradoPor(RapServidores geradoPor) {
		this.geradoPor = geradoPor;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public RapServidores getEstornadoPor() {
		return estornadoPor;
	}

	public void setEstornadoPor(RapServidores estornadoPor) {
		this.estornadoPor = estornadoPor;
	}

	public RapServidores getPagoPor() {
		return pagoPor;
	}

	public void setPagoPor(RapServidores pagoPor) {
		this.pagoPor = pagoPor;
	}

	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}

	public DominioSituacaoTitulo getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoTitulo situacao) {
		this.situacao = situacao;
	}

	public Integer getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(Integer notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getBo() {
		return bo;
	}

	public void setBo(Integer bo) {
		this.bo = bo;
	}

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getDocumento() {
		return documento;
	}

	public void setDocumento(Integer documento) {
		this.documento = documento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
