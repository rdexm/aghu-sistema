package br.gov.mec.aghu.exames.vo;

public class CartaRecoletaVO {

	private Integer soeSeq;
	private String nome;
	private String endereco;
	private String texto;
	private String observacao;
	private Short jejum;
	private String descJejum;
	private String exame;
	private Integer prontuario;
	
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEndereco() {
		return endereco;
	}
	
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public Short getJejum() {
		return jejum;
	}
	
	public void setJejum(Short jejum) {
		this.jejum = jejum;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getDescJejum() {
		return descJejum;
	}

	public void setDescJejum(String descJejum) {
		this.descJejum = descJejum;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
}
