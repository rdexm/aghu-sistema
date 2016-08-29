package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;

public class PesquisaItemAFPVO implements Serializable {

	private static final long serialVersionUID = 153076812409796729L;

	// ScoProgEntregaItemAutorizacaoFornecimentoId
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Integer seq;
	private Integer parcela;

	private Short numeroItemLicitacao;
	private Integer qtde;
	private String unidadeMedida;
	private Date dtPrevEntrega;
	private Integer codigoMaterial;
	private Integer codigoServico;
	private Date dtGeracao;
	private DominioSituacaoAutorizacaoFornecedor indSituacao;
	private Integer qtdeSolicitada;
	
	private Double valorUnitario;
	private Date dtVenctoContrato;
	private Boolean indContrato;

	private ScoServico servico;
	private ScoMaterial material;

	
	public PesquisaItemAFPVO() {

	}

	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}

	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public DominioSituacaoAutorizacaoFornecedor getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAutorizacaoFornecedor indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Date getDtVenctoContrato() {
		return dtVenctoContrato;
	}

	public void setDtVenctoContrato(Date dtVenctoContrato) {
		this.dtVenctoContrato = dtVenctoContrato;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	
	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public enum Fields {
		PEA_IAF_AFN_NUMERO("iafAfnNumero"),
		PEA_IAF_NUMERO("iafNumero"),
		PEA_SEQ("seq"),
		PEA_PARCELA("parcela"),
		IPF_ITL_NUMERO("numeroItemLicitacao"),
		PEA_QTDE("qtde"),
		IAF_UMD_CODIGO("unidadeMedida"),
		PEA_DT_PREV_ENTREGA("dtPrevEntrega"),
		SLC_MAT_CODIGO("codigoMaterial"),
		SLS_SRV_CODIGO("codigoServico"),
		PEA_DT_GERACAO("dtGeracao"),
		IAF_IND_SITUACAO("indSituacao"),
		IAF_QTDE_SOLICITADA("qtdeSolicitada"),
		IAF_FATOR_CONVERSAO("fatorConversao"),
		IAF_VALOR_UNITARIO("valorUnitario"),
		AFN_DT_VENCTO_CONTRATO("dtVenctoContrato"),
		IAF_IND_CONTRATO("indContrato"),
 MATERIAL(
				"material"), SERVICO("servico"),
		;
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}
}
