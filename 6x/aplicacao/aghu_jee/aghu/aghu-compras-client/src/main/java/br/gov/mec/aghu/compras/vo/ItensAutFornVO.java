package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

/**
 * Os dados armazenados nesse objeto representam os Itens de uma Autorizacao de Fornecimento
 * 
 * @author flavio rutkowski
 */
public class ItensAutFornVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;

	private Integer afnNumero;
	private Integer numero;	
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo modeloComercial;
	private ScoUnidadeMedida umdCodigoForn;
	private ScoUnidadeMedida unidadeMedida;
	private Integer fatorConversao;
	private Integer fatorConversaoForn;
	private Double valorUnitario;
	private Double valorEfetivado;
	private Double percDescontoItem;
	private Double percDesconto;
	private Double percAcrescimoItem;
	private Double percAcrescimo;
	private Float percVarPreco;
	private Double percIpi;
	private DominioSituacaoAutorizacaoFornecedor indSituacao;
	private Date dtExclusao;
	private Boolean indExclusao;
	private Boolean indEstorno;
	private Boolean indRecebimento;
	private Boolean indContrato;
	private Boolean indConsignado;
	private Boolean indProgrEntgAuto;
	private Boolean indAnaliseProgrPlanej;
	private Boolean indPreferencialCum;
	private Integer qtdeRecebida;
	private Integer qtdeSolicitada;	
	private List<ScoFaseSolicitacao> scoFaseSolicitacao;
	private Boolean pendente;
	private boolean desabilitarEdicaoItem;	
	private Short numeroLicitacao;
	private QtdeRpVO qtdeRp;
	private String nomeMicrocomputador = null;
	private String hintQtdAF;
	private String hintValorUnitario;
	private String hintEmbalagemFornecedor;	
	private Double valorTotal;
	private MaterialServicoVO materialServicoVO;
	private Double valorSaldo;
	private Integer numSolicitacaoOriginal;
	
	// GETs and SETs

	public Integer getAfnNumero() {
		return afnNumero;
	}
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}
	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}
	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}
	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}
	public ScoUnidadeMedida getUmdCodigoForn() {
		return umdCodigoForn;
	}
	public void setUmdCodigoForn(ScoUnidadeMedida umdCodigoForn) {
		this.umdCodigoForn = umdCodigoForn;
	}
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	public Integer getFatorConversao() {
		return  fatorConversao != null ? fatorConversao : 0;
	}
	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}
	public Integer getFatorConversaoForn() {
		return fatorConversaoForn != null ? fatorConversaoForn : 0;
	}
	public void setFatorConversaoForn(Integer fatorConversaoForn) {
		this.fatorConversaoForn = fatorConversaoForn;		
	}
	public Double getValorEfetivado() {
		return valorEfetivado;
	}
	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}
	public Double getPercDescontoItem() {
		return percDescontoItem;
	}
	public void setPercDescontoItem(Double percDescontoItem) {
		this.percDescontoItem = percDescontoItem;
	}
	public Double getPercDesconto() {
		return percDesconto;
	}
	public void setPercDesconto(Double percDesconto) {
		this.percDesconto = percDesconto;
	}
	public Double getPercAcrescimoItem() {
		return percAcrescimoItem;
	}
	public void setPercAcrescimoItem(Double percAcrescimoItem) {
		this.percAcrescimoItem = percAcrescimoItem;
	}
	public Double getPercAcrescimo() {
		return percAcrescimo;
	}
	public void setPercAcrescimo(Double percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}
	public Float getPercVarPreco() {
		return percVarPreco;
	}
	public void setPercVarPreco(Float percVarPreco) {
		this.percVarPreco = percVarPreco;
	}
	public Double getPercIpi() {
		return percIpi;
	}
	public void setPercIpi(Double percIpi) {
		this.percIpi = percIpi;
	}
	public DominioSituacaoAutorizacaoFornecedor getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacaoAutorizacaoFornecedor indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Boolean getIndExclusao() {
		return indExclusao;
	}
	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}
	public Boolean getIndEstorno() {
		return indEstorno;
	}
	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}
	public Boolean getIndRecebimento() {
		return indRecebimento;
	}
	public void setIndRecebimento(Boolean indRecebimento) {
		this.indRecebimento = indRecebimento;
	}
	public Boolean getIndContrato() {
		return indContrato;
	}
	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}
	public Boolean getIndConsignado() {
		return indConsignado;
	}
	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}
	public Boolean getIndProgrEntgAuto() {
		return indProgrEntgAuto;
	}
	public void setIndProgrEntgAuto(Boolean indProgrEntgAuto) {
		this.indProgrEntgAuto = indProgrEntgAuto;
	}
	public Boolean getIndAnaliseProgrPlanej() {
		return indAnaliseProgrPlanej;
	}
	public void setIndAnaliseProgrPlanej(Boolean indAnaliseProgrPlanej) {
		this.indAnaliseProgrPlanej = indAnaliseProgrPlanej;
	}
	public Boolean getIndPreferencialCum() {
		return indPreferencialCum;
	}
	public void setIndPreferencialCum(Boolean indPreferencialCum) {
		this.indPreferencialCum = indPreferencialCum;
	}
	public Integer getQtdeRecebida() {
		return qtdeRecebida;
	}
	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}
	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	public List<ScoFaseSolicitacao> getScoFaseSolicitacao() {
		return scoFaseSolicitacao;
	}
	public void setScoFaseSolicitacao(List<ScoFaseSolicitacao> scoFaseSolicitacao) {
		this.scoFaseSolicitacao = scoFaseSolicitacao;
	}
	public Boolean getPendente() {
		return pendente;
	}
	public void setPendente(Boolean pendente) {
		this.pendente = pendente;
	}
	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
	
	public boolean isDesabilitarEdicaoItem() {
		return desabilitarEdicaoItem;
	}
	public void setDesabilitarEdicaoItem(boolean desabilitarEdicaoItem) {
		this.desabilitarEdicaoItem = desabilitarEdicaoItem;
	}

	public Date getDtExclusao() {
		return dtExclusao;
	}
	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	public Short getNumeroLicitacao() {
		return numeroLicitacao;
	}
	public void setNumeroLicitacao(Short numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}
	public QtdeRpVO getQtdeRp() {
		return qtdeRp;
	}
	public void setQtdeRp(QtdeRpVO qtdeRp) {
		this.qtdeRp = qtdeRp;
	}
	public Double getValorSaldo() {
		return valorSaldo;
	}
	public void setValorSaldo(Double valorSaldo) {
		this.valorSaldo = valorSaldo;
	}	
	public Boolean desabilitarItemEdicaoSS(){
		List<ScoFaseSolicitacao> listaFaseSolicitacao = getScoFaseSolicitacao();

		if (listaFaseSolicitacao != null && !listaFaseSolicitacao.isEmpty()) {
			ScoFaseSolicitacao faseSolicitacao = listaFaseSolicitacao.get(0);
			

			return (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.S)); 	
		}
		
		return false;
	}
	
	public String getNomeMicrocomputador() {
		return nomeMicrocomputador;
	}
	public void setNomeMicrocomputador(String nomeMicrocomputador) {
		this.nomeMicrocomputador = nomeMicrocomputador;
	}
	public String getHintQtdAF() {
		return hintQtdAF;
	}
	public void setHintQtdAF(String hintQtdAF) {
		this.hintQtdAF = hintQtdAF;
	}
	public String getHintValorUnitario() {
		return hintValorUnitario;
	}
	public void setHintValorUnitario(String hintValorUnitario) {
		this.hintValorUnitario = hintValorUnitario;
	}
	public String getHintEmbalagemFornecedor() {
		return hintEmbalagemFornecedor;
	}
	public void setHintEmbalagemFornecedor(String hintEmbalagemFornecedor) {
		this.hintEmbalagemFornecedor = hintEmbalagemFornecedor;
	}	
	public Double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public MaterialServicoVO getMaterialServicoVO() {
		return materialServicoVO;
	}
	public void setMaterialServicoVO(MaterialServicoVO materialServicoVO) {
		this.materialServicoVO = materialServicoVO;
	}	
	public Integer getNumSolicitacaoOriginal() {
		return numSolicitacaoOriginal;
	}
	public void setNumSolicitacaoOriginal(Integer numSolicitacaoOriginal) {
		this.numSolicitacaoOriginal = numSolicitacaoOriginal;
	}	
	
}
