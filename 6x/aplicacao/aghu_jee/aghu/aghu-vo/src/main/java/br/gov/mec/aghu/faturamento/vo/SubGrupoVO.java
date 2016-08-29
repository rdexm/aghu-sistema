package br.gov.mec.aghu.faturamento.vo;

public class SubGrupoVO {
	
	private Short grupo;
	private Byte subGrupo;
	private String descricao;
	
	public SubGrupoVO() {
	}

	public SubGrupoVO(Short grupo, Byte subGrupo, String descricao) {
		super();
		this.grupo = grupo;
		this.subGrupo = subGrupo;
		this.descricao = descricao;
	}

	public Short getGrupo() {
		return grupo;
	}

	public void setGrupo(Short grupo) {
		this.grupo = grupo;
	}

	public Byte getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(Byte subGrupo) {
		this.subGrupo = subGrupo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	

}
