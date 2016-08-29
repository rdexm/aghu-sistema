package br.gov.mec.aghu.faturamento.vo;

import java.math.BigInteger;



public class CursorCBuscaRegCivilVO {

	private Long numeroDn;
	private String nomeRecemNascido;
	private String razaoSocialCartorio;
	private String livro;
	private Short folhas;
	private Integer termo;
	private String dataEmissao;
	private Short seqArqSus;
	private BigInteger regCivil;
	
	public enum Fields { 

		NUMERO_DN("numeroDn"),
		NOME_RECEM_NASCIDO("nomeRecemNascido"),
		RAZAO_SOCIAL_CARTORIO("razaoSocialCartorio"),
		LIVRO("livro"), 
		FOLHAS("folhas"),
		TERMO("termo"), 
		DATA_EMISSAO("dataEmissao"), 
		SEQ_ARQ_SUS("seqArqSus"),
		REG_CIVIL("regCivil")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getNumeroDn() {
		return numeroDn;
	}

	public void setNumeroDn(Long numeroDn) {
		this.numeroDn = numeroDn;
	}

	public String getNomeRecemNascido() {
		return nomeRecemNascido;
	}

	public void setNomeRecemNascido(String nomeRecemNascido) {
		this.nomeRecemNascido = nomeRecemNascido;
	}

	public String getRazaoSocialCartorio() {
		return razaoSocialCartorio;
	}

	public void setRazaoSocialCartorio(String razaoSocialCartorio) {
		this.razaoSocialCartorio = razaoSocialCartorio;
	}

	public String getLivro() {
		return livro;
	}

	public void setLivro(String livro) {
		this.livro = livro;
	}

	public Short getFolhas() {
		return folhas;
	}

	public void setFolhas(Short folhas) {
		this.folhas = folhas;
	}

	public Integer getTermo() {
		return termo;
	}

	public void setTermo(Integer termo) {
		this.termo = termo;
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public Short getSeqArqSus() {
		return seqArqSus;
	}

	public void setSeqArqSus(Short seqArqSus) {
		this.seqArqSus = seqArqSus;
	}

	public BigInteger getRegCivil() {
		return regCivil;
	}

	public void setRegCivil(BigInteger regCivil) {
		this.regCivil = regCivil;
	}
}