package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class DadosEdificacaoVO implements Serializable {

	private static final long serialVersionUID = -8746335428066297127L;
	
	private Integer seq;
	private String nome;
	private String descricao;
	private double latitude;
	private double longitude;
	private Integer numero;
	private String complemento;
	private Long numeroBem;
	private DominioSituacao indSituacao;
	private String nomeCidade;
	private Integer cep;
	private String logradouro;
	private String bairro;
	private String siglaUf;
	
	private String enderecoCompleto;
	
	public enum Fields {
		
		SEQ("seq"),
		NOME("nome"),
		DESCRICAO("descricao"),
		LATITUDE("latitude"),
		LONGITUDE("longitude"),
		NUMERO("numero"),
		COMPLEMENTO("complemento"),
		NUMERO_BEM("numeroBem"),
		IND_SITUACAO("indSituacao"),
		NOME_CIDADE("nomeCidade"),
		CEP("cep"),
		LOGRADOURO("logradouro"),
		BAIRRO("bairro"),
		SIGLA_UF("siglaUf");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Long getNumeroBem() {
		return numeroBem;
	}

	public void setNumeroBem(Long numeroBem) {
		this.numeroBem = numeroBem;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getNomeCidade() {
		return nomeCidade;
	}

	public void setNomeCidade(String nomeCidade) {
		this.nomeCidade = nomeCidade;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getSiglaUf() {
		return siglaUf;
	}

	public void setSiglaUf(String siglaUf) {
		this.siglaUf = siglaUf;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getEnderecoCompleto() {
		
		enderecoCompleto = this.logradouro.concat(", "+this.bairro).concat(", "+this.nomeCidade).concat(" - "+this.siglaUf);
		
		return enderecoCompleto;
	}

	public void setEnderecoCompleto(String enderecoCompleto) {
		this.enderecoCompleto = enderecoCompleto;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
}
