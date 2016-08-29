package br.gov.mec.aghu.suprimentos.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;


public class VersaoAnteriorAutorizacaoFornVO {
	
	private String nroAF;
	private Integer nroComplemento;
	private Integer seq;
	private DominioSituacaoAutorizacaoFornecimento indSituacao;
	private String modalidadeLicitacao;
	private String fornecedor;
	private String cpf;
	private Date dtGeracao;
	private Date dtPrevEntrega;
	private Date dtAlteracao;
	private Date dtExclusao;
	private Date dtEstorno;
	private ScoMotivoAlteracaoAf motivoAlteracao;
	private Boolean indExclusao;
	private Boolean indGeracao;
	private DominioAprovadaAutorizacaoForn indAprovada;
	private Integer cvfCodigo;
	private Integer modalidadeEmpenho;
	private FsoNaturezaDespesa naturezaDespesa;
	private String nroEmpenho;
	private Double valorEmpenho;
	private FormaPagamentoAFJNVO formaPagamento;
	private String prazos;
	private Integer nroContrato;
	private Double valorBruto;
	private Double valorIPI;
	private Double valorLiqd;
	private Double valorDesc;
	private Double valorAcres;
	private Double valorEfetivo;
	private Double valorFrete;
	private Double valorTotal;
	private String observacao;
	
	public String getNroAF() {
		return nroAF;
	}
	public void setNroAF(String nroAF) {
		this.nroAF = nroAF;
	}
	public Integer getNroComplemento() {
		return nroComplemento;
	}
	public void setNroComplemento(Integer nroComplemento) {
		this.nroComplemento = nroComplemento;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public DominioSituacaoAutorizacaoFornecimento getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacaoAutorizacaoFornecimento indSituacao) {
		this.indSituacao = indSituacao;
	}
	public String getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}
	public void setModalidadelicitacao(String modalidadelicitacao) {
		this.modalidadeLicitacao = modalidadelicitacao;
	}
	public String getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public Date getDtGeracao() {
		return dtGeracao;
	}
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}
	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}
	public Date getDtAlteracao() {
		return dtAlteracao;
	}
	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}
	public Date getDtExclusao() {
		return dtExclusao;
	}
	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}
	public Date getDtEstorno() {
		return dtEstorno;
	}
	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}
	public ScoMotivoAlteracaoAf getMotivoAlteracao() {
		return motivoAlteracao;
	}
	public void setMotivoAlteracao(ScoMotivoAlteracaoAf motivoAlteracao) {
		this.motivoAlteracao = motivoAlteracao;
	}
	public Boolean getIndExclusao() {
		return indExclusao;
	}
	public void setindExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}
	public Boolean getIndGeracao() {
		return indGeracao;
	}
	public void setIndGeracao(Boolean indGeracao) {
		this.indGeracao = indGeracao;
	}
	public DominioAprovadaAutorizacaoForn getIndAprovada() {
		return indAprovada;
	}
	public void setIndAprovada(DominioAprovadaAutorizacaoForn indAprovada) {
		this.indAprovada = indAprovada;
	}
	public Integer getCvfCodigo() {
		return cvfCodigo;
	}
	public void setCvfCodigo(Integer cvfCodigo) {
		this.cvfCodigo = cvfCodigo;
	}
	public Integer getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}
	public void setModalidadeEmpenho(Integer modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}
	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}
	public String getNroEmpenho() {
		return nroEmpenho;
	}
	public void setNroEmpenho(String nroEmpenho) {
		this.nroEmpenho = nroEmpenho;
	}
	public Double getValorEmpenho() {
		return valorEmpenho;
	}
	public void setValorEmpenho(Double valorEmpenho) {
		this.valorEmpenho = valorEmpenho;
	}
	public FormaPagamentoAFJNVO getFormaPagamento() {
		return formaPagamento;
	}
	public void setFormaPagamento(FormaPagamentoAFJNVO formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	public String getPrazos() {
		return prazos;
	}
	public void setPrazos(String prazos) {
		this.prazos = prazos;
	}
	public Integer getNroContrato() {
		return nroContrato;
	}
	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}
	public Double getValorBruto() {
		return valorBruto;
	}
	public void setValorBruto(Double valorBruto) {
		this.valorBruto = valorBruto;
	}
	public Double getValorIPI() {
		return valorIPI;
	}
	public void setValorIPI(Double valorIPI) {
		this.valorIPI = valorIPI;
	}
	public Double getValorLiqd() {
		return valorLiqd;
	}
	public void setValorLiqd(Double valorLiqd) {
		this.valorLiqd = valorLiqd;
	}
	public Double getValorDesc() {
		return valorDesc;
	}
	public void setValorDesc(Double valorDesc) {
		this.valorDesc = valorDesc;
	}
	public Double getValorAcres() {
		return valorAcres;
	}
	public void setValorAcres(Double valorAcres) {
		this.valorAcres = valorAcres;
	}
	public Double getValorEfetivo() {
		return valorEfetivo;
	}
	public void setValorEfetivo(Double valorEfetivo) {
		this.valorEfetivo = valorEfetivo;
	}
	public Double getValorFrete() {
		return valorFrete;
	}
	public void setValorFrete(Double valorFrete) {
		this.valorFrete = valorFrete;
	}
	public Double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
}