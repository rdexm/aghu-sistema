package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfEmpenhada;

public class PesquisarProgEntregaItensAFParcelasVO {

	private Integer numeroAf;
	private Short nroComplemento;
	private Short numeroItem;
	private String unidFornecimento;
	private Integer fatorConversao;
	private String unidEstoque;
	private Integer parcela;
	private Date dtPrevEntrega;
	private Integer qtde;
	private Double valorTotal;
	private Boolean indCancelada;
	private Boolean indAssinatura;
	private Boolean indPlanejamento;
	private DominioAfEmpenhada indEmpenhada;
	private Boolean indEnvioFornecedor;
	private Boolean indRecalculoAutomatico;
	private Boolean indRecalculoManual;
	private Integer qtdeEntrada;
	private Integer codigoMaterial;
	private Short codigoJustificativa;
	private Integer numeroFornecedor;
	private String corCelula;
	private Date dtVencContrato;

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public String getUnidFornecimento() {
		return unidFornecimento;
	}

	public void setUnidFornecimento(String unidFornecimento) {
		this.unidFornecimento = unidFornecimento;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public String getUnidEstoque() {
		return unidEstoque;
	}

	public void setUnidEstoque(String unidEstoque) {
		this.unidEstoque = unidEstoque;
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Boolean getIndCancelada() {
		return indCancelada;
	}

	public void setIndCancelada(Boolean indCancelada) {
		this.indCancelada = indCancelada;
	}

	public Boolean getIndAssinatura() {
		return indAssinatura;
	}

	public void setIndAssinatura(Boolean indAssinatura) {
		this.indAssinatura = indAssinatura;
	}

	public Boolean getIndPlanejamento() {
		return indPlanejamento;
	}

	public void setIndPlanejamento(Boolean indPlanejamento) {
		this.indPlanejamento = indPlanejamento;
	}

	public DominioAfEmpenhada getIndEmpenhada() {
		return indEmpenhada;
	}

	public void setIndEmpenhada(DominioAfEmpenhada indEmpenhada) {
		this.indEmpenhada = indEmpenhada;
	}

	public Boolean getIndEnvioFornecedor() {
		return indEnvioFornecedor;
	}

	public void setIndEnvioFornecedor(Boolean indEnvioFornecedor) {
		this.indEnvioFornecedor = indEnvioFornecedor;
	}

	public Boolean getIndRecalculoAutomatico() {
		return indRecalculoAutomatico;
	}

	public void setIndRecalculoAutomatico(Boolean indRecalculoAutomatico) {
		this.indRecalculoAutomatico = indRecalculoAutomatico;
	}

	public Boolean getIndRecalculoManual() {
		return indRecalculoManual;
	}

	public void setIndRecalculoManual(Boolean indRecalculoManual) {
		this.indRecalculoManual = indRecalculoManual;
	}

	public Integer getQtdeEntrada() {
		return qtdeEntrada;
	}

	public void setQtdeEntrada(Integer qtdeEntrada) {
		this.qtdeEntrada = qtdeEntrada;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Short getCodigoJustificativa() {
		return codigoJustificativa;
	}

	public void setCodigoJustificativa(Short codigoJustificativa) {
		this.codigoJustificativa = codigoJustificativa;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getCorCelula() {
		return corCelula;
	}

	public void setCorCelula(String corCelula) {
		this.corCelula = corCelula;
	}

	public Date getDtVencContrato() {
		return dtVencContrato;
	}

	public void setDtVencContrato(Date dtVencContrato) {
		this.dtVencContrato = dtVencContrato;
	}

}
