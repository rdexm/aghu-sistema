package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class GeraSolicCompraEstoqueVO {
	private String destino;
	private ScoGrupoMaterial gmtCodigo;
	private SceAlmoxarifado almoxarifado;
	private ScoMaterial matCodigo;
	private Integer seq;
	private Integer qtdeDisponivel;
	private Integer qtdeBloqueada;
	private Integer qtdePontoPedido;
	private ScoSolicitacaoDeCompra slcNumero;
	private ScoUnidadeMedida umdCodigo;
	private FccCentroCustos cctCodigoAplic;
	private Boolean indCentral;
	
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public ScoGrupoMaterial getGmtCodigo() {
		return gmtCodigo;
	}
	public void setGmtCodigo(ScoGrupoMaterial gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}
	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	public ScoMaterial getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(ScoMaterial matCodigo) {
		this.matCodigo = matCodigo;
	}	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getQtdeDisponivel() {
		return qtdeDisponivel;
	}
	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}
	public Integer getQtdeBloqueada() {
		return qtdeBloqueada;
	}
	public void setQtdeBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}
	public Integer getQtdePontoPedido() {
		return qtdePontoPedido;
	}
	public void setQtdePontoPedido(Integer qtdePontoPedido) {
		this.qtdePontoPedido = qtdePontoPedido;
	}
	public ScoSolicitacaoDeCompra getSlcNumero() {
		return slcNumero;
	}
	public void setSlcNumero(ScoSolicitacaoDeCompra slcNumero) {
		this.slcNumero = slcNumero;
	}
	public ScoUnidadeMedida getUmdCodigo() {
		return umdCodigo;
	}
	public void setUmdCodigo(ScoUnidadeMedida umdCodigo) {
		this.umdCodigo = umdCodigo;
	}
	public FccCentroCustos getCctCodigoAplic() {
		return cctCodigoAplic;
	}
	public void setCctCodigoAplic(FccCentroCustos cctCodigoAplic) {
		this.cctCodigoAplic = cctCodigoAplic;
	}
	public Boolean getIndCentral() {
		return indCentral;
	}
	public void setIndCentral(Boolean indCentral) {
		this.indCentral = indCentral;
	}
}
