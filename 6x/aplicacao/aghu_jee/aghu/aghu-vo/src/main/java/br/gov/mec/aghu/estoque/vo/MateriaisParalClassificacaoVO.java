package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.dominio.DominioIndProducaoInterna;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class MateriaisParalClassificacaoVO {

	private String nomeMaterial;
	private Integer codMaterial;
	private String material;
	private String nomeGrupo;
	private Integer codGrupo;
	private String grupo;
	private String unidade;
	private DominioSituacao ativo;
	private DominioIndProducaoInterna producaoInterna;
	private Boolean estocavel;
	private Boolean selecionado;

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Integer getCodMaterial() {
		return codMaterial;
	}

	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getNomeGrupo() {
		return nomeGrupo;
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	public Integer getCodGrupo() {
		return codGrupo;
	}

	public void setCodGrupo(Integer codGrupo) {
		this.codGrupo = codGrupo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public DominioSituacao getAtivo() {
		return ativo;
	}

	public void setAtivo(DominioSituacao ativo) {
		this.ativo = ativo;
	}

	public DominioIndProducaoInterna getProducaoInterna() {
		return producaoInterna;
	}

	public void setProducaoInterna(DominioIndProducaoInterna producaoInterna) {
		this.producaoInterna = producaoInterna;
	}

	public Boolean getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(Boolean estocavel) {
		this.estocavel = estocavel;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public enum Fields {

		NOME_MATERIAL("nomeMaterial"), COD_MATERIAL("codMaterial"), MATERIAL("material"), NOME_GRUPO("nomeGrupo"), COD_GRUPO("codGrupo"), GRUPO(
				"grupo"), UNIDADE("unidade"), ATIVO("ativo"), PRODUCAO_INTERNA("producaoInterna"), ESTOCAVEL("estocavel");

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
