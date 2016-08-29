package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioBaseAnaliseReposicao;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.VScoClasMaterial;


public class FiltroReposicaoMaterialVO {

	private DominioTipoMaterial tipoMaterial;
	private DominioBaseAnaliseReposicao base;
	private ScoGrupoMaterial grupoMaterial;
	private VScoClasMaterial classificacaoMaterial;
	private List<DominioClassifABC> listaClassAbc;
	private FccCentroCustos centroCustoAplicacao;
	private DominioSimNao indLicitacao;
	private DominioSimNao indProducaoInterna;
	private Date dataVigencia;
	private ScoModalidadeLicitacao modalidade;
	private Date dataInicio;
	private Date dataFim;
	private DominioSimNao indComLicitacao;
	private Integer frequencia;
	private BigDecimal vlrInicial;
	private BigDecimal vlrFinal;
	private AghParametros almoxarifadoPadrao;
	private AghParametros fornecedorPadrao;
	private AghParametros competencia;
	private Boolean somentePesquisa;
	private Boolean semClassificacaoAbc;
	private DominioSimNao indEmSc;
	private DominioSimNao indEmPac;
	private DominioSimNao indEmAf;
	private DominioSimNao indPontoPedido;
	private Integer cobertura;
	private DominioSimNao indAfContrato;
	private DominioSimNao indAfVencida;
	private DominioSimNao indItemContrato;
	

	public DominioTipoMaterial getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(DominioTipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public DominioBaseAnaliseReposicao getBase() {
		return base;
	}

	public void setBase(DominioBaseAnaliseReposicao base) {
		this.base = base;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}

	public List<DominioClassifABC> getListaClassAbc() {
		return listaClassAbc;
	}

	public void setListaClassAbc(List<DominioClassifABC> listaClassAbc) {
		this.listaClassAbc = listaClassAbc;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public DominioSimNao getIndLicitacao() {
		return indLicitacao;
	}

	public void setIndLicitacao(DominioSimNao indLicitacao) {
		this.indLicitacao = indLicitacao;
	}

	public Date getDataVigencia() {
		return dataVigencia;
	}

	public void setDataVigencia(Date dataVigencia) {
		this.dataVigencia = dataVigencia;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
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

	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public BigDecimal getVlrInicial() {
		return vlrInicial;
	}

	public void setVlrInicial(BigDecimal vlrInicial) {
		this.vlrInicial = vlrInicial;
	}

	public BigDecimal getVlrFinal() {
		return vlrFinal;
	}

	public void setVlrFinal(BigDecimal vlrFinal) {
		this.vlrFinal = vlrFinal;
	}

	public AghParametros getFornecedorPadrao() {
		return fornecedorPadrao;
	}

	public void setFornecedorPadrao(AghParametros fornecedorPadrao) {
		this.fornecedorPadrao = fornecedorPadrao;
	}

	public AghParametros getCompetencia() {
		return competencia;
	}

	public void setCompetencia(AghParametros competencia) {
		this.competencia = competencia;
	}

	public AghParametros getAlmoxarifadoPadrao() {
		return almoxarifadoPadrao;
	}

	public void setAlmoxarifadoPadrao(AghParametros almoxarifadoPadrao) {
		this.almoxarifadoPadrao = almoxarifadoPadrao;
	}

	public DominioSimNao getIndProducaoInterna() {
		return indProducaoInterna;
	}

	public void setIndProducaoInterna(DominioSimNao indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}

	public Boolean getSomentePesquisa() {
		return somentePesquisa;
	}

	public void setSomentePesquisa(Boolean somentePesquisa) {
		this.somentePesquisa = somentePesquisa;
	}

	public Boolean getSemClassificacaoAbc() {
		return semClassificacaoAbc;
	}

	public void setSemClassificacaoAbc(Boolean semClassificacaoAbc) {
		this.semClassificacaoAbc = semClassificacaoAbc;
	}

	public DominioSimNao getIndEmSc() {
		return indEmSc;
	}

	public void setIndEmSc(DominioSimNao indEmSc) {
		this.indEmSc = indEmSc;
	}

	public DominioSimNao getIndEmPac() {
		return indEmPac;
	}

	public void setIndEmPac(DominioSimNao indEmPac) {
		this.indEmPac = indEmPac;
	}

	public DominioSimNao getIndEmAf() {
		return indEmAf;
	}

	public void setIndEmAf(DominioSimNao indEmAf) {
		this.indEmAf = indEmAf;
	}
	
	public DominioSimNao getIndPontoPedido() {
		return indPontoPedido;
	}

	public void setIndPontoPedido(DominioSimNao indPontoPedido) {
		this.indPontoPedido = indPontoPedido;
	}

	public Integer getCobertura() {
		return cobertura;
	}

	public void setCobertura(Integer cobertura) {
		this.cobertura = cobertura;
	}

	public DominioSimNao getIndComLicitacao() {
		return indComLicitacao;
	}

	public void setIndComLicitacao(DominioSimNao indComLicitacao) {
		this.indComLicitacao = indComLicitacao;
	}

	public DominioSimNao getIndAfContrato() {
		return indAfContrato;
	}

	public void setIndAfContrato(DominioSimNao indAfContrato) {
		this.indAfContrato = indAfContrato;
	}

	public DominioSimNao getIndAfVencida() {
		return indAfVencida;
	}

	public void setIndAfVencida(DominioSimNao indAfVencida) {
		this.indAfVencida = indAfVencida;
	}

	public DominioSimNao getIndItemContrato() {
		return indItemContrato;
	}

	public void setIndItemContrato(DominioSimNao indItemContrato) {
		this.indItemContrato = indItemContrato;
	}
}