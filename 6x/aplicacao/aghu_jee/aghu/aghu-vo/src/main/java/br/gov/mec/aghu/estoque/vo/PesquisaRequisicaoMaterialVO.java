package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;

/**
 * Serve para o filtro de pequisa de requisição de material
 * 
 * @author Fábio Winck
 */
public class PesquisaRequisicaoMaterialVO implements Serializable {

	private static final long serialVersionUID = -3850795585966650429L;
	private Integer numRM;
	private DominioSituacaoRequisicaoMaterial indSituacao;
	private SceAlmoxarifado almoxarifado;
	private FccCentroCustos centroCustosReq;
	private FccCentroCustos centroCustosApl;
	private ScoGrupoMaterial grupoMaterial;
	private DominioSimNao indEstorno;
	private DominioSimNao indAutomatica;
	private List<FccCentroCustos> centrosCustosRequisicaoHierarquia;
	private RapServidores servidorLogado;

	public Integer getNumRM() {
		return numRM;
	}

	public void setNumRM(Integer numRM) {
		this.numRM = numRM;
	}

	public DominioSituacaoRequisicaoMaterial getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoRequisicaoMaterial indSituacao) {
		this.indSituacao = indSituacao;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public FccCentroCustos getCentroCustosReq() {
		return centroCustosReq;
	}

	public void setCentroCustosReq(FccCentroCustos centroCustosReq) {
		this.centroCustosReq = centroCustosReq;
	}

	public FccCentroCustos getCentroCustosApl() {
		return centroCustosApl;
	}

	public void setCentroCustosApl(FccCentroCustos centroCustosApl) {
		this.centroCustosApl = centroCustosApl;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public DominioSimNao getIndEstorno() {
		return indEstorno;
	}

	public void setIndEstorno(DominioSimNao indEstorno) {
		this.indEstorno = indEstorno;
	}

	public List<FccCentroCustos> getCentrosCustosRequisicaoHierarquia() {
		return centrosCustosRequisicaoHierarquia;
	}

	public void setCentrosCustosRequisicaoHierarquia(List<FccCentroCustos> centrosCustosRequisicaoHierarquia) {
		this.centrosCustosRequisicaoHierarquia = centrosCustosRequisicaoHierarquia;
	}

	public DominioSimNao getIndAutomatica() {
		return indAutomatica;
	}

	public void setIndAutomatica(DominioSimNao indAutomatica) {
		this.indAutomatica = indAutomatica;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
}
