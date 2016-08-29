package br.gov.mec.aghu.suprimentos.vo;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

public class ParcelaItemAutFornecimentoVO {

	private Integer rowId;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Integer seq;
	private Integer parcela;
	private Integer qtdeDetalhada;
	private Double valorDetalhado;
	private Integer qtdeEntregue;
	private Double valorEfetivado;
	private Short indPrioridade;
	private ScoMaterial material;
	private FsoNaturezaDespesa naturezaDespesa;
	private ScoServico servico;
	private FccCentroCustos centroCusto;
	private FsoVerbaGestao verbaGestao;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoSolicitacaoServico solicitacaoServico;
	private Long seqDetalhe;
	private TipoProgramacaoEntrega tipoProgramacaoEntrega;
	private Boolean indPrioridadeAlterada;
	private Boolean geradoAutomaticamente;
	
	public ParcelaItemAutFornecimentoVO() {
		
	}
	
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rowId == null) ? 0 : rowId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ParcelaItemAutFornecimentoVO other = (ParcelaItemAutFornecimentoVO) obj;
		if (rowId == null) {
			if (other.rowId != null){
				return false;
			}
		} else if (!rowId.equals(other.rowId)){
			return false;
		}
		return true;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
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

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public Short getIndPrioridade() {
		return indPrioridade;
	}

	public void setIndPrioridade(Short indPrioridade) {
		this.indPrioridade = indPrioridade;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public Integer getQtdeDetalhada() {
		return qtdeDetalhada;
	}


	public void setQtdeDetalhada(Integer qtdeDetalhada) {
		this.qtdeDetalhada = qtdeDetalhada;
	}


	public Double getValorDetalhado() {
		return valorDetalhado;
	}


	public void setValorDetalhado(Double valorDetalhado) {
		this.valorDetalhado = valorDetalhado;
	}


	public Integer getQtdeEntregue() {
		return qtdeEntregue;
	}


	public void setQtdeEntregue(Integer qtdeEntregue) {
		this.qtdeEntregue = qtdeEntregue;
	}


	public Double getValorEfetivado() {
		return valorEfetivado;
	}


	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}


	public Long getSeqDetalhe() {
		return seqDetalhe;
	}


	public void setSeqDetalhe(Long seqDetalhe) {
		this.seqDetalhe = seqDetalhe;
	}
	
	public TipoProgramacaoEntrega getTipoProgramacaoEntrega() {
		return tipoProgramacaoEntrega;
	}

	public void setTipoProgramacaoEntrega(
			TipoProgramacaoEntrega tipoProgramacaoEntrega) {
		this.tipoProgramacaoEntrega = tipoProgramacaoEntrega;
	}

	public Boolean getIndPrioridadeAlterada() {
		return indPrioridadeAlterada;
	}


	public void setIndPrioridadeAlterada(Boolean indPrioridadeAlterada) {
		this.indPrioridadeAlterada = indPrioridadeAlterada;
	}

	public Boolean getGeradoAutomaticamente() {
		return geradoAutomaticamente;
	}

	public void setGeradoAutomaticamente(Boolean geradoAutomaticamente) {
		this.geradoAutomaticamente = geradoAutomaticamente;
	}

	/** Tipo de Programação de Entrega */
	public static enum TipoProgramacaoEntrega {
		/** Altera solicitação. */
		ALTERAR_SOLICITACAO, 
		
		/** Gera solicitação. */
		GERAR_SOLICITACAO, 
		
		/** Gera solicitação, PAC e AF. */
		GERAR_TUDO,
		
		/** Gera item do PAC e AF. */
		GERAR_ITEM_PAC_AF
	}
}
