package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;

public class FiltroGrupoMaterialEntregaProgramadaVO {

	private ScoGrupoMaterial grupoMaterial;
	private ScoFornecedor fornecedor;
	private DominioSimNao materialEstocavel;
	private Date dataInicioEntrega;
	private Date dataFimEntrega;
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public DominioSimNao getMaterialEstocavel() {
		return materialEstocavel;
	}
	public void setMaterialEstocavel(DominioSimNao materialEstocavel) {
		this.materialEstocavel = materialEstocavel;
	}
	public Date getDataInicioEntrega() {
		return dataInicioEntrega;
	}
	public void setDataInicioEntrega(Date dataInicioEntrega) {
		this.dataInicioEntrega = dataInicioEntrega;
	}
	public Date getDataFimEntrega() {
		return dataFimEntrega;
	}
	public void setDataFimEntrega(Date dataFimEntrega) {
		this.dataFimEntrega = dataFimEntrega;
	}
	public Integer getGmtCodigo() {
		return grupoMaterial ==  null ? null : grupoMaterial.getCodigo();
	}
	public String getGmtDescricao() {
		return grupoMaterial ==  null ? null : grupoMaterial.getDescricao();
	}
	public Integer getFrnNumero() {
		return fornecedor == null ? null : fornecedor.getNumero();
	}
}