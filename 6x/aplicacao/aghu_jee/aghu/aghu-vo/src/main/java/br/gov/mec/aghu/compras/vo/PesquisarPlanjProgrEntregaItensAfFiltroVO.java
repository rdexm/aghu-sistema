package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioVizAutForn;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.VScoFornecedor;

public class PesquisarPlanjProgrEntregaItensAfFiltroVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054020593896336204L;
	
	private Integer numeroAF;
	private Short complemento;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private VScoFornecedor fornecedorAF;
	private FccCentroCustos centroCustoSolicitante;
	private FccCentroCustos centroCustoAplicacao;
	private ScoModalidadeLicitacao modalidadeCompra;
	private DominioSimNao efetivada;
	private DominioClassifABC curvaABC;
	private DominioSimNao estocavel;
	private DominioSimNao servico;
	private Date dataInicioVencimentoContrato;
	private Date dataFimVencimentoContrato;
	private Date dataInicioPrevisaoEntrega;
	private Date dataFimPrevisaoEntrega;
	private DominioSimNao progAutomatica;
	private DominioSimNao planejada;
	private DominioSimNao assinada;
	private DominioSimNao empenhada;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private DominioVizAutForn visualizarAutForn;
	private Date dataPrevisaoEntrega;
	
	
	
	public PesquisarPlanjProgrEntregaItensAfFiltroVO() {
		super();
	}

	public PesquisarPlanjProgrEntregaItensAfFiltroVO(DominioSimNao efetivada,
			DominioSimNao estocavel, DominioSimNao servico) {
		super();
		this.efetivada = efetivada;
		this.estocavel = estocavel;
		this.servico = servico;
	}



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

	public VScoFornecedor getFornecedorAF() {
		return fornecedorAF;
	}

	public void setFornecedorAF(VScoFornecedor fornecedorAF) {
		this.fornecedorAF = fornecedorAF;
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

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public DominioSimNao getEfetivada() {
		return efetivada;
	}

	public void setEfetivada(DominioSimNao efetivada) {
		this.efetivada = efetivada;
	}

	public DominioClassifABC getCurvaABC() {
		return curvaABC;
	}

	public void setCurvaABC(DominioClassifABC curvaABC) {
		this.curvaABC = curvaABC;
	}

	public DominioSimNao getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(DominioSimNao estocavel) {
		this.estocavel = estocavel;
	}

	public DominioSimNao getServico() {
		return servico;
	}

	public void setServico(DominioSimNao servico) {
		this.servico = servico;
	}

	public Date getDataInicioVencimentoContrato() {
		return dataInicioVencimentoContrato;
	}

	public void setDataInicioVencimentoContrato(Date dataInicioVencimentoContrato) {
		this.dataInicioVencimentoContrato = dataInicioVencimentoContrato;
	}

	public Date getDataFimVencimentoContrato() {
		return dataFimVencimentoContrato;
	}

	public void setDataFimVencimentoContrato(Date dataFimVencimentoContrato) {
		this.dataFimVencimentoContrato = dataFimVencimentoContrato;
	}

	public Date getDataInicioPrevisaoEntrega() {
		return dataInicioPrevisaoEntrega;
	}

	public void setDataInicioPrevisaoEntrega(Date dataInicioPrevisaoEntrega) {
		this.dataInicioPrevisaoEntrega = dataInicioPrevisaoEntrega;
	}

	public Date getDataFimPrevisaoEntrega() {
		return dataFimPrevisaoEntrega;
	}

	public void setDataFimPrevisaoEntrega(Date dataFimPrevisaoEntrega) {
		this.dataFimPrevisaoEntrega = dataFimPrevisaoEntrega;
	}

	public DominioSimNao getProgAutomatica() {
		return progAutomatica;
	}

	public void setProgAutomatica(DominioSimNao progAutomatica) {
		this.progAutomatica = progAutomatica;
	}

	public DominioSimNao getPlanejada() {
		return planejada;
	}

	public void setPlanejada(DominioSimNao planejada) {
		this.planejada = planejada;
	}

	public DominioSimNao getAssinada() {
		return assinada;
	}

	public void setAssinada(DominioSimNao assinada) {
		this.assinada = assinada;
	}

	public DominioSimNao getEmpenhada() {
		return empenhada;
	}

	public void setEmpenhada(DominioSimNao empenhada) {
		this.empenhada = empenhada;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public DominioVizAutForn getVisualizarAutForn() {
		return visualizarAutForn;
	}

	public void setVisualizarAutForn(DominioVizAutForn visualizarAutForn) {
		this.visualizarAutForn = visualizarAutForn;
	}

	public Date getDataPrevisaoEntrega() {
		return dataPrevisaoEntrega;
	}

	public void setDataPrevisaoEntrega(Date dataPrevisaoEntrega) {
		this.dataPrevisaoEntrega = dataPrevisaoEntrega;
	} 
}
