package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class ParametrosGEDVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 902175319558214529L;
	protected static final String _TAG_NUMERICO = "[n]";
	protected static final String _TAG_DATA = "[d]";
	private String usuarioLogado;
	private String prontuario;
	private Integer pacCodigo;
	private String nome;

	public ParametrosGEDVO() {
	}

	public ParametrosGEDVO(String prontuario) {
		super();
		this.prontuario = prontuario;
	}

	public ParametrosGEDVO(Integer pacCodigo, String prontuario, String nome, String usuarioLogado) {
		super();
		this.prontuario = prontuario;
		this.nome = nome;
		this.pacCodigo = pacCodigo;
		this.usuarioLogado = usuarioLogado;
	}

	public String getCamposFicha() {
		StringBuffer campos = new StringBuffer();
		if (StringUtils.isNotBlank(prontuario)) {
			campos.append(prontuario);
		}
		return campos.toString();
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

}