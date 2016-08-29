package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;

public class UnidadeCuidadoRotinaVO implements Serializable {

	private static final long serialVersionUID = -497727341130031411L;

	private Short codigo; // unf_seq
	private String situacao; // ind_situacao
	private String andarAlaDescricao; // andar_ala_descricao
	
	public Short getCodigo() {
		return codigo;
	}
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}
	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}
}
