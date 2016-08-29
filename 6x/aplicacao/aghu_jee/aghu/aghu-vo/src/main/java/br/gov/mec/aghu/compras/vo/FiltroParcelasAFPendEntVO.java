package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;

public class FiltroParcelasAFPendEntVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;

	private Integer numeroAF;
	private Short complemento;
	private DominioAfpPublicado publicacao;
	private ScoFornecedor fornecedor;
	private RapServidores gestor;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private DominioSimNao entregaAtrasada;
	private Integer qtdDiasEntrega;
	private Date dataEntregaInicial;
	private Date dataEntregaFinal;

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
	public DominioAfpPublicado getPublicacao() {
		return publicacao;
	}
	public void setPublicacao(DominioAfpPublicado publicacao) {
		this.publicacao = publicacao;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public void setGestor(RapServidores gestor) {
		this.gestor = gestor;
	}
	public RapServidores getGestor() {
		return gestor;
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
	public void setEntregaAtrasada(DominioSimNao entregaAtrasada) {
		this.entregaAtrasada = entregaAtrasada;
	}
	public DominioSimNao getEntregaAtrasada() {
		return entregaAtrasada;
	}
	public void setQtdDiasEntrega(Integer qtdDiasEntrega) {
		this.qtdDiasEntrega = qtdDiasEntrega;
	}
	public Integer getQtdDiasEntrega() {
		return qtdDiasEntrega;
	}
	public void setDataEntregaInicial(Date dataEntregaInicial) {
		this.dataEntregaInicial = dataEntregaInicial;
	}
	public Date getDataEntregaInicial() {
		return dataEntregaInicial;
	}
	public void setDataEntregaFinal(Date dataEntregaFinal) {
		this.dataEntregaFinal = dataEntregaFinal;
	}
	public Date getDataEntregaFinal() {
		return dataEntregaFinal;
	}
	

}
