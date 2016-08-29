package br.gov.mec.aghu.compras.vo;



import java.util.Date;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioOpcoesResultadoAF;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;


public class FiltroPesquisaGeralAFVO {
	
	/*** Filtro ***/
	private Integer numeroAF;
	private Short complemento;
	private Short sequencia;
	private Integer numeroAFP;	
	private Integer item;
	private ScoModalidadeLicitacao modalidadeCompra;
	private RapServidores servidorGestor;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Date dataGeracaoInicial;
	private Date dataGeracaoFinal;
	private Date previsaoEntregaInicial;
	private Date previsaoEntregaFinal;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private VScoFornecedor fornecedor;
	private ScoFornecedor fornecedorFull;
	private String termoLivre;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private ScoGrupoServico grupoServico;
	private ScoServico servico;
	private FccCentroCustos centroCustoSolicitante;
	private FccCentroCustos centroCustoAplicacao;
	private FsoVerbaGestao verbaGestao;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private FsoNaturezaDespesa naturezaDespesa;
	private Integer numeroContrato;
	private Boolean entregaAtrasada;
	private Integer diasVencimento;
	private DominioOpcoesResultadoAF opcaoResultado;
	
	
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
	public Short getSequencia() {
		return sequencia;
	}
	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}
	public Integer getNumeroAFP() {
		return numeroAFP;
	}
	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}
	public Integer getItem() {
		return item;
	}
	public void setItem(Integer item) {
		this.item = item;
	}
	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}
	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}
	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
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
	public Date getPrevisaoEntregaInicial() {
		return previsaoEntregaInicial;
	}
	public void setPrevisaoEntregaInicial(Date previsaoEntregaInicial) {
		this.previsaoEntregaInicial = previsaoEntregaInicial;
	}
	public Date getPrevisaoEntregaFinal() {
		return previsaoEntregaFinal;
	}
	public void setPrevisaoEntregaFinal(Date previsaoEntregaFinal) {
		this.previsaoEntregaFinal = previsaoEntregaFinal;
	}
	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}
	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}
	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getTermoLivre() {
		return termoLivre;
	}
	public void setTermoLivre(String termoLivre) {
		this.termoLivre = termoLivre;
	}
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}
	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}
	public ScoServico getServico() {
		return servico;
	}
	public void setServico(ScoServico servico) {
		this.servico = servico;
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
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}
	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}
	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}
	public Integer getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public Boolean getEntregaAtrasada() {
		return entregaAtrasada;
	}
	public void setEntregaAtrasada(Boolean entregaAtrasada) {
		this.entregaAtrasada = entregaAtrasada;
	}
	public Integer getDiasVencimento() {
		return diasVencimento;
	}
	public void setDiasVencimento(Integer diasVencimento) {
		this.diasVencimento = diasVencimento;
	}
	public DominioOpcoesResultadoAF getOpcaoResultado() {
		return opcaoResultado;
	}
	public void setOpcaoResultado(DominioOpcoesResultadoAF opcaoResultado) {
		this.opcaoResultado = opcaoResultado;
	}
	public ScoFornecedor getFornecedorFull() {
		return fornecedorFull;
	}
	public void setFornecedorFull(ScoFornecedor fornecedorFull) {
		this.fornecedorFull = fornecedorFull;
	}
}
