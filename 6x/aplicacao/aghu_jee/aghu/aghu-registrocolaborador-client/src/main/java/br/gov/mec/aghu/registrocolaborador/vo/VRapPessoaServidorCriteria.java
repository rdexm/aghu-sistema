package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;

public class VRapPessoaServidorCriteria implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3586816114729602445L;
	
	
	private Object filtro;
	private FiltroRestriction filtroRestriction;
	private Set<DominioSituacaoVinculo> situacoesVinculo;
	private Date fimVinculoBase;

	public Object getFiltro() {
		return filtro;
	}

	public void setFiltro(Object filtro) {
		this.filtro = filtro;
	}

	public Set<DominioSituacaoVinculo> getSituacoesVinculo() {
		return situacoesVinculo;
	}

	public void setSituacoesVinculo(Set<DominioSituacaoVinculo> situacoesVinculo) {
		this.situacoesVinculo = situacoesVinculo;
	}

	public Date getFimVinculoBase() {
		return fimVinculoBase;
	}

	public void setFimVinculoBase(Date fimVinculoBase) {
		this.fimVinculoBase = fimVinculoBase;
	}
	
	public FiltroRestriction getFiltroRestriction() {
		return filtroRestriction;
	}

	public void setFiltroRestriction(FiltroRestriction filtroRestriction) {
		this.filtroRestriction = filtroRestriction;
	}

	public static enum FiltroRestriction {
		FULL, PARTIAL;
	}

}
