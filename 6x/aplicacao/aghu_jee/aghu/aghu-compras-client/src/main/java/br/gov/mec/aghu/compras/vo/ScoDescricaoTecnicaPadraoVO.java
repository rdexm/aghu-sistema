package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class ScoDescricaoTecnicaPadraoVO {

	private Short codigo;
	private String titulo;
	private DominioSimNao liberadaPublicacao;
	private String descricao;
	private Integer codigoMaterial;
	
	public ScoDescricaoTecnicaPadraoVO() {}
	
	public Short getCodigo() {
		return codigo;
	}
	public String getTitulo() {
		return titulo;
	}
	public DominioSimNao getLiberadaPublicacao() {
		return liberadaPublicacao;
	}
	public String getDescricao() {
		return descricao;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public void setLiberadaPublicacao(DominioSimNao liberadaPublicacao) {
		this.liberadaPublicacao = liberadaPublicacao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
}