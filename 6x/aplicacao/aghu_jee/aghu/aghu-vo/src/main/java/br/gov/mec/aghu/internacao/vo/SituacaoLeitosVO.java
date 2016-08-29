package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;

/**
 * @author dlaks
 *
 */
public class SituacaoLeitosVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6670052467372119210L;

	private Integer codigoClinica;
	
	private Short codigoTipoMovimentoLeito;
	
	private String descricaoTipoMovimentoLeito;
	
	private DominioMovimentoLeito grupoTipoMovimentoLeito;
	
	private Integer quantidade;

	public Integer getCodigoClinica() {
		return codigoClinica;
	}

	public void setCodigoClinica(Integer codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	public Short getCodigoTipoMovimentoLeito() {
		return codigoTipoMovimentoLeito;
	}

	public void setCodigoTipoMovimentoLeito(Short codigoTipoMovimentoLeito) {
		this.codigoTipoMovimentoLeito = codigoTipoMovimentoLeito;
	}

	public String getDescricaoTipoMovimentoLeito() {
		return descricaoTipoMovimentoLeito;
	}

	public void setDescricaoTipoMovimentoLeito(String descricaoTipoMovimentoLeito) {
		this.descricaoTipoMovimentoLeito = descricaoTipoMovimentoLeito;
	}

	public DominioMovimentoLeito getGrupoTipoMovimentoLeito() {
		return grupoTipoMovimentoLeito;
	}

	public void setGrupoTipoMovimentoLeito(
			DominioMovimentoLeito grupoTipoMovimentoLeito) {
		this.grupoTipoMovimentoLeito = grupoTipoMovimentoLeito;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	 
}
