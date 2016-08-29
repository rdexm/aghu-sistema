package br.gov.mec.aghu.compras.vo;

import java.util.Date;

public class ScoFornecedorVO {
	private Long cgc;
	private Long cpf;
	private Integer crc;
	private Date dtValidadeCrc;
	private Date dtValidadeFgts;
	private Date dtValidadeInss;
	private Date dtValidadeRecEst;//DT_VALIDADE_RECEST
	private Date dtValidadeBal; //DT_VALIDADE_BAL
	private Date dtValidadeAvs;
	private Date dtValidadeRecMun; //DT_VALIDADE_RECMUN
	private Date dtValidadeCndt;//DT_VALIDADE_CNDT
	private Date dtValidadeRecFed;
	private String indAfe;
	private String logradouro;
	private String nroLogradouro;
	private String bairro;
	private Integer cep;
	private String cidade;
	private String ufSigla;
	private String rvMeaning;
	private String descricao;
	private String hospital;
	private String hospitalLogradouro;
	private String hospitalCep;
	private String hospitalCidadeEstado;
	private String nomeCoordenadorComissaoLicitacao;
	private String comissaoLicitacao;
	private String cidadeDataAtualPorExtenso;
	private String logoHospital;
	private String razaoSocial;
	private Integer numeroFornecedor;
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	public String getClassificacaoEconomicaFornecedor() {
		return classificacaoEconomicaFornecedor;
	}
	public void setClassificacaoEconomicaFornecedor(String classificacaoEconomicaFornecedor) {
		this.classificacaoEconomicaFornecedor = classificacaoEconomicaFornecedor;
	}
	public Short getRmcCodigo() {
		return rmcCodigo;
	}
	public void setRmcCodigo(Short rmcCodigo) {
		this.rmcCodigo = rmcCodigo;
	}
	private String classificacaoEconomicaFornecedor;
	private Short rmcCodigo;
	
	public enum Fields{
		
		CGC("cgc"),
		CPF("cpf"),
		CRC("crc"),
		DT_VALIDADE_CRC("dtValidadeCrc"),
		DT_VALIDADE_FGTS("dtValidadeFgts"),
		DT_VALIDADE_INSS("dtValidadeInss"),
		DT_VALIDADE_RECEST("dtValidadeRecEst"),
		DT_VALIDADE_BAL("dtValidadeBal"),
		DT_VALIDADE_AVS("dtValidadeAvs"),
		DT_VALIDADE_RECMUN("dtValidadeRecMun"),
		DT_VALIDADE_CNDT("dtValidadeCndt"),
		DT_VALIDADE_RECFED("dtValidadeRecFed"),
		IND_AFE("indAfe"),
		LOGRADOURO("logradouro"),
		NRO_LOGRADOURO("nroLogradouro"),
		BAIRRO("bairro"),
		CEP("cep"),
		CIDADE("cidade"),
		UF_SIGLA("ufSigla"),
		RV_MEANING("rvMeaning"),
		DESCRICAO("descricao"),
		RAZAO_SOCIAL("razaoSocial"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		CLASSIFICACAO_ECONOMICA_FORNECEDOR("classificacaoEconomicaFornecedor"),
		RMC_CODIGO("rmcCodigo");
		
		
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public Integer getCrc() {
		return crc;
	}
	public void setCrc(Integer crc) {
		this.crc = crc;
	}
	public Date getDtValidadeCrc() {
		return dtValidadeCrc;
	}
	public void setDtValidadeCrc(Date dtValidadeCrc) {
		this.dtValidadeCrc = dtValidadeCrc;
	}
	public Date getDtValidadeFgts() {
		return dtValidadeFgts;
	}
	public void setDtValidadeFgts(Date dtValidadeFgts) {
		this.dtValidadeFgts = dtValidadeFgts;
	}
	public Date getDtValidadeInss() {
		return dtValidadeInss;
	}
	public void setDtValidadeInss(Date dtValidadeInss) {
		this.dtValidadeInss = dtValidadeInss;
	}
	public Date getDtValidadeRecEst() {
		return dtValidadeRecEst;
	}
	public void setDtValidadeRecEst(Date dtValidadeRecEst) {
		this.dtValidadeRecEst = dtValidadeRecEst;
	}
	public Date getDtValidadeBal() {
		return dtValidadeBal;
	}
	public void setDtValidadeBal(Date dtValidadeBal) {
		this.dtValidadeBal = dtValidadeBal;
	}
	public Date getDtValidadeAvs() {
		return dtValidadeAvs;
	}
	public void setDtValidadeAvs(Date dtValidadeAvs) {
		this.dtValidadeAvs = dtValidadeAvs;
	}
	public Date getDtValidadeRecMun() {
		return dtValidadeRecMun;
	}
	public void setDtValidadeRecMun(Date dtValidadeRecMun) {
		this.dtValidadeRecMun = dtValidadeRecMun;
	}
	public Date getDtValidadeCndt() {
		return dtValidadeCndt;
	}
	public void setDtValidadeCndt(Date dtValidadeCndt) {
		this.dtValidadeCndt = dtValidadeCndt;
	}
	public Date getDtValidadeRecFed() {
		return dtValidadeRecFed;
	}
	public void setDtValidadeRecFed(Date dtValidadeRecFed) {
		this.dtValidadeRecFed = dtValidadeRecFed;
	}
	public String getIndAfe() {
		return indAfe;
	}
	public void setIndAfe(String indAfe) {
		this.indAfe = indAfe;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getNroLogradouro() {
		return nroLogradouro;
	}
	public void setNroLogradouro(String nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public Integer getCep() {
		return cep;
	}
	public void setCep(Integer cep) {
		this.cep = cep;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getUfSigla() {
		return ufSigla;
	}
	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}
	public String getRvMeaning() {
		return rvMeaning;
	}
	public void setRvMeaning(String rvMeaning) {
		this.rvMeaning = rvMeaning;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public String getHospitalLogradouro() {
		return hospitalLogradouro;
	}
	public void setHospitalLogradouro(String hospitalLogradouro) {
		this.hospitalLogradouro = hospitalLogradouro;
	}
	public String getHospitalCep() {
		return hospitalCep;
	}
	public void setHospitalCep(String hospitalCep) {
		this.hospitalCep = hospitalCep;
	}
	public String getHospitalCidadeEstado() {
		return hospitalCidadeEstado;
	}
	public void setHospitalCidadeEstado(String hospitalCidadeEstado) {
		this.hospitalCidadeEstado = hospitalCidadeEstado;
	}
	public String getNomeCoordenadorComissaoLicitacao() {
		return nomeCoordenadorComissaoLicitacao;
	}
	public void setNomeCoordenadorComissaoLicitacao(String nomeCoordenadorComissaoLicitacao) {
		this.nomeCoordenadorComissaoLicitacao = nomeCoordenadorComissaoLicitacao;
	}
	public String getComissaoLicitacao() {
		return comissaoLicitacao;
	}
	public void setComissaoLicitacao(String comissaoLicitacao) {
		this.comissaoLicitacao = comissaoLicitacao;
	}
	public String getCidadeDataAtualPorExtenso() {
		return cidadeDataAtualPorExtenso;
	}
	public void setCidadeDataAtualPorExtenso(String cidadeDataAtualPorExtenso) {
		this.cidadeDataAtualPorExtenso = cidadeDataAtualPorExtenso;
	}
	public String getLogoHospital() {
		return logoHospital;
	}
	public void setLogoHospital(String logoHospital) {
		this.logoHospital = logoHospital;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}
	public Long getCgc() {
		return cgc;
	}	
}
