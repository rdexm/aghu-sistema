package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * Critérios de Busca para Ramos Comerciais
 * 
 * @author mlcruz
 */
public class ScoRamoComercialCriteriaVO {
	private Short codigo;				// Código
	private String descricao;			// Descrição
	private DominioSituacao situacao;	// Situação
	
	public Short getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
}
