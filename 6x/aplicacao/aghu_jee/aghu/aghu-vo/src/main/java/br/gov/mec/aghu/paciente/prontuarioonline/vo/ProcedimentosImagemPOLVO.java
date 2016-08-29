package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;

public class ProcedimentosImagemPOLVO implements Serializable {

	private static final long serialVersionUID = 8963915843270724844L;
	private byte[] imagem;
	private String descricaoImagem;
	private String tituloImagem;
	private String nomeImagem;

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public String getDescricaoImagem() {
		return descricaoImagem;
	}

	public void setDescricaoImagem(String descricaoImagem) {
		this.descricaoImagem = descricaoImagem;
	}

	public String getTituloImagem() {
		return tituloImagem;
	}

	public void setTituloImagem(String tituloImagem) {
		this.tituloImagem = tituloImagem;
	}

	public String getNomeImagem() {
		return nomeImagem;
	}

	public void setNomeImagem(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}

}
