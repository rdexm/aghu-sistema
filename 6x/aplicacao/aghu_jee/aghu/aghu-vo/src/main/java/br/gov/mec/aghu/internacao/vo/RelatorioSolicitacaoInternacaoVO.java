package br.gov.mec.aghu.internacao.vo;

public class RelatorioSolicitacaoInternacaoVO {

	private String codigo;
	private String nome;
	private String idade;
	private String prontuario;
	private String prevInternacao;
	private String sigla;
	private String ssm;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getPrevInternacao() {
		return prevInternacao;
	}

	public void setPrevInternacao(String prevInternacao) {
		this.prevInternacao = prevInternacao;
	}
	
	public String getSsm() {
		return ssm;
	}

	public void setSsm(String ssm) {
		this.ssm = ssm;
	}
	
	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

}
