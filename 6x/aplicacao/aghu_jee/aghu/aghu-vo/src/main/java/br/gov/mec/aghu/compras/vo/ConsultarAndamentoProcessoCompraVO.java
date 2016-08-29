package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoLicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;

public class ConsultarAndamentoProcessoCompraVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;
	
	private Integer numeroPac;
	private Boolean pendente;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private DominioTipoLicitacao tipo;
	private ScoLocalizacaoProcesso local;
	private String objeto;
	private String gestor;
	private Date dataGeracaoInicial;
	private Date dataGeracaoFinal;
	private DominioSimNao pacAF;
	private DominioSimNao pacIncompleto;
	private DominioSimNao pacAFPendente;
	private DominioSimNao pacEncerrado;
	private DominioSimNao pacInvestimento;
	private Integer numeroAF;
	private Integer complemento;
	private Integer sc;
	private Integer ss;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private FccCentroCustos centroCustoSolicitante;	
	private FccCentroCustos centroCustoAplicacao;
	private ScoServico servico;
	private Date dataVencimentoInicial;
	private Date dataVencimentoFinal;
	private ScoMarcaComercial marcaComercial;
	private ScoFornecedor fornecedor;
	
	private AghParametros codigosNaturezaParam;
	private AghParametros nomeComissaoParam;
	
	public Integer getNumeroPac() {
		return numeroPac;
	}
	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}
	public Boolean getPendente() {
		return pendente;
	}
	public void setPendente(Boolean pendente) {
		this.pendente = pendente;
	}
	public DominioTipoLicitacao getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoLicitacao tipo) {
		this.tipo = tipo;
	}
	public ScoLocalizacaoProcesso getLocal() {
		return local;
	}
	public void setLocal(ScoLocalizacaoProcesso local) {
		this.local = local;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}
	public String getGestor() {
		return gestor;
	}
	public void setGestor(String gestor) {
		this.gestor = gestor;
	}
	public Date getDataGeracaoInicial() {
		return dataGeracaoInicial;
	}
	public void setDataGeracaoInicial(Date dataGeracaoInicial) {
		this.dataGeracaoInicial = dataGeracaoInicial;
	}
	public Date getDataGeracaoFinal() {
		return dataGeracaoFinal;
	}
	public void setDataGeracaoFinal(Date dataGeracaoFinal) {
		this.dataGeracaoFinal = dataGeracaoFinal;
	}
	public DominioSimNao getPacAF() {
		return pacAF;
	}
	public void setPacAF(DominioSimNao pacAF) {
		this.pacAF = pacAF;
	}
	public DominioSimNao getPacIncompleto() {
		return pacIncompleto;
	}
	public void setPacIncompleto(DominioSimNao pacIncompleto) {
		this.pacIncompleto = pacIncompleto;
	}
	public DominioSimNao getPacAFPendente() {
		return pacAFPendente;
	}
	public void setPacAFPendente(DominioSimNao pacAFPendente) {
		this.pacAFPendente = pacAFPendente;
	}
	public DominioSimNao getPacEncerrado() {
		return pacEncerrado;
	}
	public void setPacEncerrado(DominioSimNao pacEncerrado) {
		this.pacEncerrado = pacEncerrado;
	}
	public DominioSimNao getPacInvestimento() {
		return pacInvestimento;
	}
	public void setPacInvestimento(DominioSimNao pacInvestimento) {
		this.pacInvestimento = pacInvestimento;
	}
	public Integer getNumeroAF() {
		return numeroAF;
	}
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	public Integer getComplemento() {
		return complemento;
	}
	public void setComplemento(Integer complemento) {
		this.complemento = complemento;
	}
	public Integer getSc() {
		return sc;
	}
	public void setSc(Integer sc) {
		this.sc = sc;
	}
	public Integer getSs() {
		return ss;
	}
	public void setSs(Integer ss) {
		this.ss = ss;
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
	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}
	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}
	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}
	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}
	public ScoServico getServico() {
		return servico;
	}
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
	public Date getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}
	public void setDataVencimentoInicial(Date dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}
	public Date getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}
	public void setDataVencimentoFinal(Date dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}
	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public AghParametros getCodigosNaturezaParam() {
		return codigosNaturezaParam;
	}
	public void setCodigosNaturezaParam(AghParametros codigosNaturezaParam) {
		this.codigosNaturezaParam = codigosNaturezaParam;
	}
	public AghParametros getNomeComissaoParam() {
		return nomeComissaoParam;
	}
	public void setNomeComissaoParam(AghParametros nomeComissaoParam) {
		this.nomeComissaoParam = nomeComissaoParam;
	}
	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}
	
}
