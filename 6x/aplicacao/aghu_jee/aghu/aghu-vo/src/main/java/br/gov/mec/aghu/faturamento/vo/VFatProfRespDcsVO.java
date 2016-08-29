package br.gov.mec.aghu.faturamento.vo;


public class VFatProfRespDcsVO implements java.io.Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3395231900625652731L;

	private String nome;

	private Short serVinCodigo;

	private Integer serMatricula;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public String getDsSuggestion() {
		return (serMatricula != null ? serMatricula : "") + " - " + (nome != null ? nome : "");
	}

}
