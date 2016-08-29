package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

/**
 * Utilizado como resultado da C6 de #5554 - Programação automática de Parcelas AF
 * 
 * 
 * @author luismoura
 * 
 */
public class GrupoMaterialNumeroSolicitacaoVO implements Serializable {
	private static final long serialVersionUID = 854714040040241604L;

	private Integer codigoMaterial;
	private Integer codigoGrupoMaterial;
	private Integer numeroSolicitacao;

	public GrupoMaterialNumeroSolicitacaoVO() {

	}

	public GrupoMaterialNumeroSolicitacaoVO(Integer codigoMaterial, Integer codigoGrupoMaterial, Integer numeroSolicitacao) {
		this.codigoMaterial = codigoMaterial;
		this.codigoGrupoMaterial = codigoGrupoMaterial;
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public enum Fields {
		CODIGO_MATERIAL("codigoMaterial"), //
		CODIGO_GRUPO_MATERIAL("codigoGrupoMaterial"), //
		NUMERO_SOLICITACAO("numeroSolicitacao"), //
		;
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
