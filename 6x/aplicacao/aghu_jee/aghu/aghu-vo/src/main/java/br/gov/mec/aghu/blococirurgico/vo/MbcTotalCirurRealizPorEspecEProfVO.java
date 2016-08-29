package br.gov.mec.aghu.blococirurgico.vo;


public class MbcTotalCirurRealizPorEspecEProfVO {

	private Short seqEspecialidade;
	private String  nomeEspecialidade;
	private String  nomePessoaFisica;
	private String  nomeUsualPessoaFisica;
	private String  cirurgiao;
	private Long numeroDeCirurgias;
	
	@SuppressWarnings("ucd")
	public enum Fields {
		ESP_SEQ("seqEspecialidade"),
	    ESP_NOME_ESPECIALIDADE("nomeEspecialidade"),
	    PES_NOME("nomePessoaFisica"),
	    PES_NOME_USUAL("nomeUsualPessoaFisica"),
	    CIRURGIAO("cirurgiao"),
	    NUMERO_CIRURGIAS("numeroDeCirurgias");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}


	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}


	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}


	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}


	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}


	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}


	public String getNomeUsualPessoaFisica() {
		return nomeUsualPessoaFisica;
	}


	public void setNomeUsualPessoaFisica(String nomeUsualPessoaFisica) {
		this.nomeUsualPessoaFisica = nomeUsualPessoaFisica;
	}


	public String getCirurgiao() {
		if (cirurgiao == null){
			return getNomeUsualPessoaFisica() == null? getNomePessoaFisica(): getNomeUsualPessoaFisica();
		}else {
			return cirurgiao;
		}
	}


	public void setCirurgiao(String cirurgiao) {
		this.cirurgiao = cirurgiao;
	}


	public Long getNumeroDeCirurgias() {
		return numeroDeCirurgias;
	}


	public void setNumeroDeCirurgias(Long numeroDeCirurgias) {
		this.numeroDeCirurgias = numeroDeCirurgias;
	}


	@Override
	public String toString() {
		return "MbcTotalCirurRealizPorEspecEProfVO [seqEspecialidade="
				+ seqEspecialidade + ", nomeEspecialidade=" + nomeEspecialidade
				+ ", nomePessoaFisica=" + nomePessoaFisica
				+ ", nomeUsualPessoaFisica=" + nomeUsualPessoaFisica
				+ ", cirurgiao=" + cirurgiao + ", numeroDeCirurgias="
				+ numeroDeCirurgias + "]";
	}

	
}
