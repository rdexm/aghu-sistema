package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class RelatorioMensalMaterialVO implements Serializable{

	
	
	
	private static final long serialVersionUID = 7004422426990886900L;
	private Integer codigo;
	private String descricao;
	private Long estocavel;
	private Long totalEstocavel;
	private Long naoEstocavel;
	private Long totalNaoEstocavel;
	private BigDecimal  vlrInicial;
	private BigDecimal  totalVlrInicial;
	private BigDecimal  vlrNr;
	private BigDecimal  totalVlrNr;
	private BigDecimal  vlrDa;
	private BigDecimal  totalVlrDa;
	private BigDecimal  vlrRm;
	private BigDecimal  totalVlrRm;
	private BigDecimal  vlrDf;
	private BigDecimal  totalVlrDf;
	private BigDecimal  vlrAjste;
	private BigDecimal  totalVlrAjste;
	private BigDecimal  vlrAjsts;
	private BigDecimal  totalVlrAjsts;
	private BigDecimal  vlrPi;
	private BigDecimal  totalVlrPi;
	private BigDecimal  outrosEntrada;
	private BigDecimal  totalOutrosEntrada;
	private BigDecimal  outrosSaida;
	private BigDecimal  totalOutrosSaida;
	private BigDecimal  vlrAtual;
	private BigDecimal  totalVlrAtual;
	private String  totValor;
	private String fechamentoMensal;
    private String  cobertDias;
    private String  totalCobertDias;
	private String  totCons;
	
	
	
	public String getTotalCobertDias() {
		return totalCobertDias;
	}
	public void setTotalCobertDias(String totalCobertDias) {
		this.totalCobertDias = totalCobertDias;
	}
	
	public String getCobertDias() {
		return cobertDias;
	}
	public void setCobertDias(String cobertDias) {
		this.cobertDias = cobertDias;
	}
	public String getTotCons() {
		return totCons;
	}
	public void setTotCons(String totCons) {
		this.totCons = totCons;
	}
	public String getFechamentoMensal() {
		return fechamentoMensal;
	}
	public void setFechamentoMensal(String fechamentoMensal) {
		this.fechamentoMensal = fechamentoMensal;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Long getEstocavel() {
		return estocavel;
	}
	public void setEstocavel(Long estocavel) {
		this.estocavel = estocavel;
	}
	public Long getNaoEstocavel() {
		return naoEstocavel;
	}
	public void setNaoEstocavel(Long naoEstocavel) {
		this.naoEstocavel = naoEstocavel;
	}
	
	public Long getTotalEstocavel() {
		return totalEstocavel;
	}
	public void setTotalEstocavel(Long totalEstocavel) {
		this.totalEstocavel = totalEstocavel;
	}
	public Long getTotalNaoEstocavel() {
		return totalNaoEstocavel;
	}
	public void setTotalNaoEstocavel(Long totalNaoEstocavel) {
		this.totalNaoEstocavel = totalNaoEstocavel;
	}
	public BigDecimal getVlrNr() {
		return vlrNr;
	}
	public void setVlrNr(BigDecimal vlrNr) {
		this.vlrNr = vlrNr;
	}
	public BigDecimal getTotalVlrNr() {
		return totalVlrNr;
	}
	public void setTotalVlrNr(BigDecimal totalVlrNr) {
		this.totalVlrNr = totalVlrNr;
	}
	public BigDecimal getVlrDa() {
		return vlrDa;
	}
	public void setVlrDa(BigDecimal vlrDa) {
		this.vlrDa = vlrDa;
	}
	public BigDecimal getTotalVlrDa() {
		return totalVlrDa;
	}
	public void setTotalVlrDa(BigDecimal totalVlrDa) {
		this.totalVlrDa = totalVlrDa;
	}
	public BigDecimal getVlrRm() {
		return vlrRm;
	}
	public void setVlrRm(BigDecimal vlrRm) {
		this.vlrRm = vlrRm;
	}
	public BigDecimal getTotalVlrRm() {
		return totalVlrRm;
	}
	public void setTotalVlrRm(BigDecimal totalVlrRm) {
		this.totalVlrRm = totalVlrRm;
	}
	public BigDecimal getVlrDf() {
		return vlrDf;
	}
	public void setVlrDf(BigDecimal vlrDf) {
		this.vlrDf = vlrDf;
	}
	public BigDecimal getTotalVlrDf() {
		return totalVlrDf;
	}
	public void setTotalVlrDf(BigDecimal totalVlrDf) {
		this.totalVlrDf = totalVlrDf;
	}
	public BigDecimal getVlrAjste() {
		return vlrAjste;
	}
	public void setVlrAjste(BigDecimal vlrAjste) {
		this.vlrAjste = vlrAjste;
	}
	public BigDecimal getTotalVlrAjste() {
		return totalVlrAjste;
	}
	public void setTotalVlrAjste(BigDecimal totalVlrAjste) {
		this.totalVlrAjste = totalVlrAjste;
	}
	public BigDecimal getVlrAjsts() {
		return vlrAjsts;
	}
	public void setVlrAjsts(BigDecimal vlrAjsts) {
		this.vlrAjsts = vlrAjsts;
	}
	public BigDecimal getTotalVlrAjsts() {
		return totalVlrAjsts;
	}
	public void setTotalVlrAjsts(BigDecimal totalVlrAjsts) {
		this.totalVlrAjsts = totalVlrAjsts;
	}
	public BigDecimal getVlrPi() {
		return vlrPi;
	}
	public void setVlrPi(BigDecimal vlrPi) {
		this.vlrPi = vlrPi;
	}
	public BigDecimal getTotalVlrPi() {
		return totalVlrPi;
	}
	public void setTotalVlrPi(BigDecimal totalVlrPi) {
		this.totalVlrPi = totalVlrPi;
	}
	public BigDecimal getOutrosEntrada() {
		return outrosEntrada;
	}
	public void setOutrosEntrada(BigDecimal outrosEntrada) {
		this.outrosEntrada = outrosEntrada;
	}
	public BigDecimal getTotalOutrosEntrada() {
		return totalOutrosEntrada;
	}
	public void setTotalOutrosEntrada(BigDecimal totalOutrosEntrada) {
		this.totalOutrosEntrada = totalOutrosEntrada;
	}
	public BigDecimal getOutrosSaida() {
		return outrosSaida;
	}
	public void setOutrosSaida(BigDecimal outrosSaida) {
		this.outrosSaida = outrosSaida;
	}
	public BigDecimal getTotalOutrosSaida() {
		return totalOutrosSaida;
	}
	public void setTotalOutrosSaida(BigDecimal totalOutrosSaida) {
		this.totalOutrosSaida = totalOutrosSaida;
	}
	public BigDecimal getVlrAtual() {
		return vlrAtual;
	}
	public void setVlrAtual(BigDecimal vlrAtual) {
		this.vlrAtual = vlrAtual;
	}
	public BigDecimal getVlrInicial() {
		return vlrInicial;
	}
	public void setVlrInicial(BigDecimal vlrInicial) {
		this.vlrInicial = vlrInicial;
	}
	public BigDecimal getTotalVlrInicial() {
		return totalVlrInicial;
	}
	public void setTotalVlrInicial(BigDecimal totalVlrInicial) {
		this.totalVlrInicial = totalVlrInicial;
	}
	public BigDecimal getTotalVlrAtual() {
		return totalVlrAtual;
	}
	public void setTotalVlrAtual(BigDecimal totalVlrAtual) {
		this.totalVlrAtual = totalVlrAtual;
	}
	public String getTotValor() {
		return totValor;
	}
	public void setTotValor(String totValor) {
		this.totValor = totValor;
	}
	

	
	
	
}
