package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioDiaSemanaAbreviado;
import br.gov.mec.aghu.dominio.DominioFormaProgramacao;
import br.gov.mec.aghu.dominio.DominioParcela;
import br.gov.mec.aghu.dominio.DominioUrgencia;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;

public class ProgramacaoManualParcelasEntregaFiltroVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054020593896336204L;
	
	private AutorizacaoFornVO autorizacaoForn;
	private Short complemento;
	private ScoFornecedor fornecedor;
	private Boolean situacaoProgramacao;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private Date dtPrimeiraEntrega;
	private DominioFormaProgramacao formaProgramacao;
	private Integer qtdeLimiteInteiro;
	private Double qtdeLimiteMonetario;
	private DominioUrgencia urgencia;
	private Integer quantidade;
	private DominioParcela tipoParcela;
	private Integer numeroDias;
	private Integer qtdeParcelas;
	private List<DominioDiaSemanaAbreviado> listaDiasSemana = new ArrayList<DominioDiaSemanaAbreviado>();
	private List<HorarioSemanaVO> listaHorarioSabado = new ArrayList<HorarioSemanaVO>();
	private List<HorarioSemanaVO> listaHorarioDomingo = new ArrayList<HorarioSemanaVO>();
	private List<HorarioSemanaVO> listaHorarioSegunda = new ArrayList<HorarioSemanaVO>();
	private List<HorarioSemanaVO> listaHorarioTerca = new ArrayList<HorarioSemanaVO>();
	private List<HorarioSemanaVO> listaHorarioQuarta = new ArrayList<HorarioSemanaVO>();
	private List<HorarioSemanaVO> listaHorarioQuinta = new ArrayList<HorarioSemanaVO>();
	private List<HorarioSemanaVO> listaHorarioSexta = new ArrayList<HorarioSemanaVO>();
	
	
	public ProgramacaoManualParcelasEntregaFiltroVO() {
		super();
	}

	public AutorizacaoFornVO getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(AutorizacaoFornVO autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Boolean getSituacaoProgramacao() {
		return situacaoProgramacao;
	}

	public void setSituacaoProgramacao(Boolean situacaoProgramacao) {
		this.situacaoProgramacao = situacaoProgramacao;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public Date getDtPrimeiraEntrega() {
		return dtPrimeiraEntrega;
	}

	public void setDtPrimeiraEntrega(Date dtPrimeiraEntrega) {
		this.dtPrimeiraEntrega = dtPrimeiraEntrega;
	}

	public DominioFormaProgramacao getFormaProgramacao() {
		return formaProgramacao;
	}

	public void setFormaProgramacao(DominioFormaProgramacao formaProgramacao) {
		this.formaProgramacao = formaProgramacao;
	}

	public Integer getQtdeLimiteInteiro() {
		return qtdeLimiteInteiro;
	}

	public void setQtdeLimiteInteiro(Integer qtdeLimiteInteiro) {
		this.qtdeLimiteInteiro = qtdeLimiteInteiro;
	}

	public Double getQtdeLimiteMonetario() {
		return qtdeLimiteMonetario;
	}

	public void setQtdeLimiteMonetario(Double qtdeLimiteMonetario) {
		this.qtdeLimiteMonetario = qtdeLimiteMonetario;
	}

	public DominioUrgencia getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(DominioUrgencia urgencia) {
		this.urgencia = urgencia;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public DominioParcela getTipoParcela() {
		return tipoParcela;
	}

	public void setTipoParcela(DominioParcela tipoParcela) {
		this.tipoParcela = tipoParcela;
	}

	public Integer getNumeroDias() {
		return numeroDias;
	}

	public void setNumeroDias(Integer numeroDias) {
		this.numeroDias = numeroDias;
	}

	public Integer getQtdeParcelas() {
		return qtdeParcelas;
	}

	public void setQtdeParcelas(Integer qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}
	
	public List<DominioDiaSemanaAbreviado> getListaDiasSemana() {
		return listaDiasSemana;
	}

	public void setListaDiasSemana(List<DominioDiaSemanaAbreviado> listaDiasSemana) {
		this.listaDiasSemana = listaDiasSemana;
	}

	public List<HorarioSemanaVO> getListaHorarioSabado() {
		return listaHorarioSabado;
	}

	public void setListaHorarioSabado(List<HorarioSemanaVO> listaHorarioSabado) {
		this.listaHorarioSabado = listaHorarioSabado;
	}

	public List<HorarioSemanaVO> getListaHorarioDomingo() {
		return listaHorarioDomingo;
	}

	public void setListaHorarioDomingo(List<HorarioSemanaVO> listaHorarioDomingo) {
		this.listaHorarioDomingo = listaHorarioDomingo;
	}

	public List<HorarioSemanaVO> getListaHorarioSegunda() {
		return listaHorarioSegunda;
	}

	public void setListaHorarioSegunda(List<HorarioSemanaVO> listaHorarioSegunda) {
		this.listaHorarioSegunda = listaHorarioSegunda;
	}

	public List<HorarioSemanaVO> getListaHorarioTerca() {
		return listaHorarioTerca;
	}

	public void setListaHorarioTerca(List<HorarioSemanaVO> listaHorarioTerca) {
		this.listaHorarioTerca = listaHorarioTerca;
	}

	public List<HorarioSemanaVO> getListaHorarioQuarta() {
		return listaHorarioQuarta;
	}

	public void setListaHorarioQuarta(List<HorarioSemanaVO> listaHorarioQuarta) {
		this.listaHorarioQuarta = listaHorarioQuarta;
	}

	public List<HorarioSemanaVO> getListaHorarioQuinta() {
		return listaHorarioQuinta;
	}

	public void setListaHorarioQuinta(List<HorarioSemanaVO> listaHorarioQuinta) {
		this.listaHorarioQuinta = listaHorarioQuinta;
	}

	public List<HorarioSemanaVO> getListaHorarioSexta() {
		return listaHorarioSexta;
	}

	public void setListaHorarioSexta(List<HorarioSemanaVO> listaHorarioSexta) {
		this.listaHorarioSexta = listaHorarioSexta;
	}
}
