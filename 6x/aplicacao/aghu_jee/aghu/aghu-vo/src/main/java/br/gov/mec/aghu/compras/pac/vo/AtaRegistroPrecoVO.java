package br.gov.mec.aghu.compras.pac.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AtaRegistroPrecoVO {
	private Integer licitacao;
	private Integer docLicitacao;
	private Date dtLimiteAceiteProposta;
	private String descricaoLicitacao;
	private Date dthrAberturaProposta;
	private String razaoSocialForn;
	private String cnpj;
	private String cgc;
	private String logradouroForn;
	private String nroLogradouro;
	private String nome;
	private String cidade;
	private String ufSigla;
	private String siglaHospital;
	private String presidenteHospital;
	
	public String getEstadoHospital() {
		return estadoHospital;
	}

	public void setEstadoHospital(String estadoHospital) {
		this.estadoHospital = estadoHospital;
	}

	public String getLogotipoCabecalho() {
		return logotipoCabecalho;
	}

	public void setLogotipoCabecalho(String logotipoCabecalho) {
		this.logotipoCabecalho = logotipoCabecalho;
	}

	public String getTelefoneHospital() {
		return telefoneHospital;
	}

	public void setTelefoneHospital(String telefoneHospital) {
		this.telefoneHospital = telefoneHospital;
	}

	public String getFaxHospital() {
		return faxHospital;
	}

	public void setFaxHospital(String faxHospital) {
		this.faxHospital = faxHospital;
	}

	public String getEnderecoHospital() {
		return enderecoHospital;
	}

	public void setEnderecoHospital(String enderecoHospital) {
		this.enderecoHospital = enderecoHospital;
	}

	public String getCidadeHospital() {
		return cidadeHospital;
	}

	public void setCidadeHospital(String cidadeHospital) {
		this.cidadeHospital = cidadeHospital;
	}

	public String getSiglaEstadoHospital() {
		return siglaEstadoHospital;
	}

	public void setSiglaEstadoHospital(String siglaEstadoHospital) {
		this.siglaEstadoHospital = siglaEstadoHospital;
	}

	public String getCepHospital() {
		return cepHospital;
	}

	public void setCepHospital(String cepHospital) {
		this.cepHospital = cepHospital;
	}

	public String getSiteHospital() {
		return siteHospital;
	}

	public void setSiteHospital(String siteHospital) {
		this.siteHospital = siteHospital;
	}

	private String estadoHospital;
	private String logotipoCabecalho;
	private String telefoneHospital;
	private String faxHospital;
	private String enderecoHospital;
	private String cidadeHospital;
	private String siglaEstadoHospital;
	private String cepHospital;
	private String siteHospital;
	private String razaoSocialHospital;
	
	public AtaRegistroPrecoVO(){}
	
	public Date getDtLimiteAceiteProposta() {
		return dtLimiteAceiteProposta;
	}
	public void setDtLimiteAceiteProposta(Date dtLimiteAceiteProposta) {
		this.dtLimiteAceiteProposta = dtLimiteAceiteProposta;
	}
	
	public Date getDthrAberturaProposta() {
		return dthrAberturaProposta;
	}
	
	public String getDthrAberturaPropostaStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(dthrAberturaProposta != null ? dthrAberturaProposta : new Date());
	}
	
	public void setDthrAberturaProposta(Date dthrAberturaProposta) {
		this.dthrAberturaProposta = dthrAberturaProposta;
	}
	
	public String getRazaoSocialForn() {
		return razaoSocialForn;
	}

	public void setRazaoSocialForn(String razaoSocialForn) {
		this.razaoSocialForn = razaoSocialForn;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getUfSigla() {
		return ufSigla;
	}
	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}
	public void setLicitacao(Integer licitacao) {
		this.licitacao = licitacao;
	}



	public Integer getLicitacao() {
		return licitacao;
	}
	public void setDescricaoLicitacao(String descricaoLicitacao) {
		this.descricaoLicitacao = descricaoLicitacao;
	}

	public String getDescricaoLicitacao() {
		return descricaoLicitacao;
	}
	public void setLogradouroForn(String logradouroForn) {
		this.logradouroForn = logradouroForn;
	}

	public String getLogradouroForn() {
		return logradouroForn;
	}

	public String getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(String nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	public void setRazaoSocialHospital(String razaoSocialHospital) {
		this.razaoSocialHospital = razaoSocialHospital;
	}

	public String getRazaoSocialHospital() {
		return razaoSocialHospital;
	}

	public void setSiglaHospital(String siglaHospital) {
		this.siglaHospital = siglaHospital;
	}

	public String getSiglaHospital() {
		return siglaHospital;
	}

	public void setPresidenteHospital(String presidenteHospital) {
		this.presidenteHospital = presidenteHospital;
	}

	public String getPresidenteHospital() {
		return presidenteHospital;
	}

	public enum Fields{
		
		LICITACAO("licitacao"),
		DOC_LICITACAO("docLicitacao"),
		DT_LIMITE_ACEITE_PROPOSTA("dtLimiteAceiteProposta"),
		DESCRICAO_LICITACAO("descricaoLicitacao"),
		DT_HR_ABERTURA_PROPOSTA("dthrAberturaProposta"),
		RAZAO_SOCIAL_FORN("razaoSocialForn"),
		CNPJ("cnpj"),
		LOGRADOURO_FORN("logradouroForn"),
		NRO_LOGRADOURO("nroLogradouro"),
		NOME("nome"),
		UF_SIGLA("ufSigla"),
		CIDADE("cidade"),
		CGC("cgc");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public String getDtLimiteAceitePropostaStr(){
		if(dtLimiteAceiteProposta != null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			return sdf.format(dtLimiteAceiteProposta);
		}
		return "";
	}

	public void setDocLicitacao(Integer docLicitacao) {
		this.docLicitacao = docLicitacao;
	}

	public Integer getDocLicitacao() {
		return docLicitacao;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCgc(String cgc) {
		this.cgc = cgc;
	}

	public String getCgc() {
		return cgc;
	}
}
