package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;

public class AutorizacaoFornecimentoPendenteEntregaVO {
	private Integer numeroAF;
	private Integer complemento;
	private Integer numeroAFP;
	private Date periodoEntregaInicio;
	private Date periodoEntregaFim;
	private ScoFornecedor fornecedores;
	private DominioSimNao entregaAtrasada;
	private DominioSimNao empenhada;
	private DominioSimNao recebido;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private Integer cP;
	
	private Boolean manterDesabilitado;
	
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	public Integer getNumeroAF() {
		return numeroAF;
	}
	public void setComplemento(Integer complemento) {
		this.complemento = complemento;
	}
	public Integer getComplemento() {
		return complemento;
	}
	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}
	public Integer getNumeroAFP() {
		return numeroAFP;
	}
	public void setPeriodoEntregaInicio(Date periodoEntregaInicio) {
		this.periodoEntregaInicio = periodoEntregaInicio;
	}
	public Date getPeriodoEntregaInicio() {
		return periodoEntregaInicio;
	}
	public void setPeriodoEntregaFim(Date periodoEntregaFim) {
		this.periodoEntregaFim = periodoEntregaFim;
	}
	public Date getPeriodoEntregaFim() {
		return periodoEntregaFim;
	}
	public void setManterDesabilitado(Boolean manterDesabilitado) {
		this.manterDesabilitado = manterDesabilitado;
	}
	public Boolean getManterDesabilitado() {
		return manterDesabilitado;
	}
	public void setEntregaAtrasada(DominioSimNao entregaAtrasada) {
		this.entregaAtrasada = entregaAtrasada;
	}
	public DominioSimNao getEntregaAtrasada() {
		return entregaAtrasada;
	}
	public void setEmpenhada(DominioSimNao empenhada) {
		this.empenhada = empenhada;
	}
	public DominioSimNao getEmpenhada() {
		return empenhada;
	}
	public void setFornecedores(ScoFornecedor fornecedores) {
		this.fornecedores = fornecedores;
	}
	public ScoFornecedor getFornecedores() {
		return fornecedores;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	public void setRecebido(DominioSimNao recebido) {
		this.recebido = recebido;
	}
	public DominioSimNao getRecebido() {
		return recebido;
	}
	public void setcP(Integer cP) {
		this.cP = cP;
	}
	public Integer getcP() {
		return cP;
	}
	
}
