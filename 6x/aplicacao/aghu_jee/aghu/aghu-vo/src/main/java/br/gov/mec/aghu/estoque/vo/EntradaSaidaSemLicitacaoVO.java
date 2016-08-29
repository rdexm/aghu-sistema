package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class EntradaSaidaSemLicitacaoVO implements BaseBean {
	
	/***/
	private static final long serialVersionUID = -5503589777121334487L;
	
	private Integer matGmtCodigo;
	private Date mmtDtGeracao;
	private Boolean mmtIndEstorno;
	private Short mmtAlm;
	private String mmtUmd;
	private Integer mmtMatCodigo;
	private Integer mmtQuantidade;
	private String tmvSigla;
	private String tmv1Sigla;
	private Integer matCodigo;
	private String matDescricao;
	private String matNome;
	private Integer eslSeq;
	private Date eslDtGeracao;
	private Boolean eslIndEncerrado;
	private Boolean eslIndEstorno;
	private Integer eslSeqFe;
	private Integer eslDfeSeq;
	private Integer eslNumero;
	private Integer eslFrnNumero;
	private Integer islEslSeq;
	private Integer islNumeroSolic;
	private Short islSeqAlm;
	private Integer islCodigoMat;
	private String islCodigoUm;
	private Integer islQuantidade;
	private Integer islQtdeDevolvida;
	private Integer islSlcNumero;
	private BigDecimal mmtValor;
	private Integer nroDocGeracao;
	private Integer fevSeq;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	
	@Override
	public int hashCode() {

		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getMatGmtCodigo());
		umHashCodeBuilder.append(this.getMmtDtGeracao());
		umHashCodeBuilder.append(this.getMmtIndEstorno());
		umHashCodeBuilder.append(this.getMmtAlm());
		umHashCodeBuilder.append(this.getMmtUmd());
		umHashCodeBuilder.append(this.getMmtMatCodigo());
		umHashCodeBuilder.append(this.getMmtQuantidade());
		umHashCodeBuilder.append(this.getTmvSigla());
		umHashCodeBuilder.append(this.getTmv1Sigla());
		umHashCodeBuilder.append(this.getMatCodigo());
		umHashCodeBuilder.append(this.getMatDescricao());
		umHashCodeBuilder.append(this.getMatNome());
		umHashCodeBuilder.append(this.getEslSeq());
		umHashCodeBuilder.append(this.getEslDtGeracao());
		umHashCodeBuilder.append(this.getEslIndEncerrado());
		umHashCodeBuilder.append(this.getEslIndEstorno());
		umHashCodeBuilder.append(this.getEslSeqFe());
		umHashCodeBuilder.append(this.getEslDfeSeq());
		umHashCodeBuilder.append(this.getEslNumero());
		umHashCodeBuilder.append(this.getIslEslSeq());
		umHashCodeBuilder.append(this.getIslNumeroSolic());
		umHashCodeBuilder.append(this.getIslSeqAlm());
		umHashCodeBuilder.append(this.getIslCodigoMat());
		umHashCodeBuilder.append(this.getIslCodigoUm());
		umHashCodeBuilder.append(this.getIslQuantidade());
		umHashCodeBuilder.append(this.getIslQtdeDevolvida());
		umHashCodeBuilder.append(this.getIslSlcNumero());
		umHashCodeBuilder.append(this.getMmtValor());
		umHashCodeBuilder.append(this.getNroDocGeracao());
		umHashCodeBuilder.append(this.getFevSeq());
		umHashCodeBuilder.append(this.getIafAfnNumero());
		umHashCodeBuilder.append(this.getIafNumero());

		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EntradaSaidaSemLicitacaoVO)) {
			return false;
		}
		EntradaSaidaSemLicitacaoVO other = (EntradaSaidaSemLicitacaoVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getMatGmtCodigo(), other.getMatGmtCodigo());
		umEqualsBuilder.append(this.getMmtDtGeracao(), other.getMmtDtGeracao());
		umEqualsBuilder.append(this.getMmtIndEstorno(), other.getMmtIndEstorno());
		umEqualsBuilder.append(this.getMmtAlm(), other.getMmtAlm());
		umEqualsBuilder.append(this.getMmtUmd(), other.getMmtUmd());
		umEqualsBuilder.append(this.getMmtMatCodigo(), other.getMmtMatCodigo());
		umEqualsBuilder.append(this.getMmtQuantidade(), other.getMmtQuantidade());
		umEqualsBuilder.append(this.getTmvSigla(), other.getTmvSigla());
		umEqualsBuilder.append(this.getTmv1Sigla(), other.getTmv1Sigla());
		umEqualsBuilder.append(this.getMatCodigo(), other.getMatCodigo());
		umEqualsBuilder.append(this.getMatDescricao(), other.getMatDescricao());
		umEqualsBuilder.append(this.getMatNome(), other.getMatNome());
		umEqualsBuilder.append(this.getEslSeq(), other.getEslSeq());
		umEqualsBuilder.append(this.getEslDtGeracao(), other.getEslDtGeracao());
		umEqualsBuilder.append(this.getEslIndEncerrado(), other.getEslIndEncerrado());
		umEqualsBuilder.append(this.getEslIndEstorno(), other.getEslIndEstorno());
		umEqualsBuilder.append(this.getEslSeqFe(), other.getEslSeqFe());
		umEqualsBuilder.append(this.getEslDfeSeq(), other.getEslDfeSeq());
		umEqualsBuilder.append(this.getEslNumero(), other.getEslNumero());
		umEqualsBuilder.append(this.getIslEslSeq(), other.getIslEslSeq());
		umEqualsBuilder.append(this.getIslNumeroSolic(), other.getIslNumeroSolic());
		umEqualsBuilder.append(this.getIslSeqAlm(), other.getIslSeqAlm());
		umEqualsBuilder.append(this.getIslCodigoMat(), other.getIslCodigoMat());
		umEqualsBuilder.append(this.getIslCodigoUm(), other.getIslCodigoUm());
		umEqualsBuilder.append(this.getIslQuantidade(), other.getIslQuantidade());
		umEqualsBuilder.append(this.getIslQtdeDevolvida(), other.getIslQtdeDevolvida());
		umEqualsBuilder.append(this.getIslSlcNumero(), other.getIslSlcNumero());
		umEqualsBuilder.append(this.getMmtValor(), other.getMmtValor());
		umEqualsBuilder.append(this.getNroDocGeracao(), other.getNroDocGeracao());
		umEqualsBuilder.append(this.getFevSeq(), other.getFevSeq());
		umEqualsBuilder.append(this.getIafAfnNumero(), other.getIafAfnNumero());
		umEqualsBuilder.append(this.getIafNumero(), other.getIafNumero());

		return umEqualsBuilder.isEquals();
	}
	

	//Filtro
	private Date mmtDataComp;
	private Date dataInicial;
	private Date dataFinal;
	private Boolean adiantamentoAF;
	private Short[] listaTipoMovimento = null;
	private Short[] listaTipoMovimentoOrigem = null;
	private Map<String,Object> mapTipoMovimento = new LinkedHashMap<String,Object>();
	
	//VO da Consulta Secundária para preencher corretamente os campos – FORNECEDOR(V_RAZAO_SOCIAL) e CNPJ( V_CNPJ) do relatório.
	private Long frnCgcC2;
	private Long frnCpfC2;
	private Integer fevSeqC2;
	private String frnRazaoSocialC2;
	private	String fevRrazaoSocialC2;
	private Long v_cnpj;
	private	String v_razao_social ;
	
	//VO da C3
	private Long nroNfC3;
	//VO da C4
	private String afC4;
	private Integer pfr_lct_numeroC4;
	private Short nro_complementoC4;
	//VO da C5
	private Short itemAFC5;


	public EntradaSaidaSemLicitacaoVO() {
		
	}
	
	public EntradaSaidaSemLicitacaoVO(Integer matGmtCodigo, String tmvSigla, Integer nroDocGeracao,
			Integer eslSeq, String tmv1Sigla, Integer islSlcNumero, Date mmtDtGeracao,
			Boolean mmtIndEstorno, Short mmtAlm, Integer mmtMatCodigo, String matNome, 
			String mmtUmd, Integer mmtQuantidade, Integer islQtdeDevolvida, BigDecimal mmtValor,
			Integer eslDfeSeq, Integer eslFrnNumero, Integer fevSeq, Integer iafAfnNumero, Integer iafNumero) {
		
		this.eslIndEncerrado = true;
		this.matGmtCodigo = matGmtCodigo;
		this.tmvSigla = tmvSigla;
		this.nroDocGeracao = nroDocGeracao;
		this.eslSeq = eslSeq;
		this.tmv1Sigla = tmv1Sigla;
		this.islSlcNumero = islSlcNumero;
		this.mmtDtGeracao = mmtDtGeracao;
		this.mmtIndEstorno = mmtIndEstorno;
		this.mmtAlm = mmtAlm;
		this.mmtMatCodigo = mmtMatCodigo;
		this.matNome = matNome;
		this.mmtUmd = mmtUmd;
		this.mmtQuantidade = mmtQuantidade;
		this.islQtdeDevolvida = islQtdeDevolvida;
		this.mmtValor = mmtValor;
		this.eslDfeSeq = eslDfeSeq;
		this.eslFrnNumero = eslFrnNumero;
		this.fevSeq = fevSeq;
		this.iafAfnNumero = iafAfnNumero;
		this.iafNumero = iafNumero;
		
	}
	
	
	public enum Fields {
		
		MATERIAL_CODIGO_GMT("matGmtCodigo"),
		MATERIAL_CODIGO("matCodigo"),
		MMT_DT_GERACAO("mmtDtGeracao"),
		MMT_DOC_GERACAO("nroDocGeracao"),
		MMT_IND_ESTORNO("mmtIndEstorno"),
		MMT_MAT_CODIGO("mmtMatCodigo"),
		MMT_ALMOXARIFADO("mmtAlm"),
		MMT_UMD("mmtUmd"),
		MMT_QUANTIDADE("mmtQuantidade"),
		MATERIAL_DESCRICAO("matDescricao"),
		MATERIAL_NOME("matNome"),
		SIGLA_TMV("tmvSigla"),
		SIGLA_TMV1("tmv1Sigla"),
		ESL_SEQ("eslSeq"),
		ESL_DATA_GERACAO("eslDtGeracao"),
		ESL_IND_ENCERRADO("eslIndEncerrado"),
		ESL_IND_ESTORNO("eslIndEstorno"),
		ESL_SEQ_FORNECEDOR_EVENTUAL("eslSeqFe"),
		ESL_NUMERO("eslNumero"),
		ESL_FRN_NUMERO("eslFrnNumero"),
		ESL_DFE_SEQ("eslDfeSeq"),
		ISL_ESL_SEQ("islEslSeq"),
		ISL_NUMERO_SOLIC("islNumeroSolic"),
		ISL_SEQ_ALMOXARIFADO("islSeqAlm"),
		ISL_CODIGO_MATERIAL("islCodigoMat"),
		ISL_CODIGO_UNIDADE_MEDIDA("islCodigoUm"),
		ISL_QUANTIDADE("islQuantidade"),
		ISL_SLC_NUMERO("islSlcNumero"),
		ISL_QUANTIDADE_DEVOLVIDA("islQtdeDevolvida"),
		MOVIMENTO_MATERIAIS_VALOR("mmtValor"),
		FEV_SEQ("fevSeq"),
		IAF_AFN_NUMERO("iafAfnNumero"),
		IAF_NUMERO("iafNumero"),
		
		//Fields C2
		C2_FRN_CGC("frnCgcC2"),
		C2_FRN_CPF("frnCpfC2"),
		C2_FEV_SEQ("fevSeqC2"),
		C2_FRN_RAZAO("frnRazaoSocialC2"),
		C2_FEV_RAZAO("fevRrazaoSocialC2"),
		V_CNPJ("v_cnpj"),
		V_RAZAP_SOCIAL("v_razao_social"),
		
		//Fields C3
		C3_DFE_NUMERO("nroNfC3"),
		//Fields C4
		C4_AF("afC4"),
		C4_AFN_PFR_LCT_NUMERO("pfr_lct_numeroC4"),
		C4_AFN_NRO_COMPLEMENTO("nro_complementoC4"),
		//Fields C5
		C5_FSL1_ITL_NUMERO("itemAFC5")
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
	
	public Integer getMatCodigo() {
		return matCodigo;
	}


	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}


	public String getMatDescricao() {
		return matDescricao;
	}


	public void setMatDescricao(String matDescricao) {
		this.matDescricao = matDescricao;
	}


	public String getTmvSigla() {
		return tmvSigla;
	}


	public void setTmvSigla(String tmvSigla) {
		this.tmvSigla = tmvSigla;
	}


	public Integer getEslSeq() {
		return eslSeq;
	}


	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}


	public Date getEslDtGeracao() {
		return eslDtGeracao;
	}


	public void setEslDtGeracao(Date eslDtGeracao) {
		this.eslDtGeracao = eslDtGeracao;
	}

	public Boolean getEslIndEncerrado() {
		return eslIndEncerrado;
	}
	
	public void setEslIndEncerrado(Boolean eslIndEncerrado) {
		this.eslIndEncerrado = eslIndEncerrado;
	}
	
	public Boolean getEslIndEstorno() {
		return eslIndEstorno;
	}

	public void setEslIndEstorno(Boolean eslIndEstorno) {
		this.eslIndEstorno = eslIndEstorno;
	}

	public Integer getEslSeqFe() {
		return eslSeqFe;
	}

	public void setEslSeqFe(Integer eslSeqFe) {
		this.eslSeqFe = eslSeqFe;
	}

	public Integer getEslNumero() {
		return eslNumero;
	}

	public void setEslNumero(Integer eslNumero) {
		this.eslNumero = eslNumero;
	}

	public Integer getIslEslSeq() {
		return islEslSeq;
	}

	public void setIslEslSeq(Integer islEslSeq) {
		this.islEslSeq = islEslSeq;
	}
	
	public Integer getIslNumeroSolic() {
		return islNumeroSolic;
	}

	public void setIslNumeroSolic(Integer islNumeroSolic) {
		this.islNumeroSolic = islNumeroSolic;
	}

	public Short getIslSeqAlm() {
		return islSeqAlm;
	}

	public void setIslSeqAlm(Short islSeqAlm) {
		this.islSeqAlm = islSeqAlm;
	}

	public Integer getIslCodigoMat() {
		return islCodigoMat;
	}

	public void setIslCodigoMat(Integer islCodigoMat) {
		this.islCodigoMat = islCodigoMat;
	}

	public String getIslCodigoUm() {
		return islCodigoUm;
	}

	public void setIslCodigoUm(String islCodigoUm) {
		this.islCodigoUm = islCodigoUm;
	}

	public Integer getIslQuantidade() {
		return islQuantidade;
	}

	public void setIslQuantidade(Integer islQuantidade) {
		this.islQuantidade = islQuantidade;
	}
	
	public Integer getIslQtdeDevolvida() {
		return islQtdeDevolvida;
	}

	public void setIslQtdeDevolvida(Integer islQtdeDevolvida) {
		this.islQtdeDevolvida = islQtdeDevolvida;
	}


	public BigDecimal getMmtValor() {
		return mmtValor;
	}

	public void setMmtValor(BigDecimal mmtValor) {
		this.mmtValor = mmtValor;
	}
	
	public Integer getMatGmtCodigo() {
		return matGmtCodigo;
	}

	public void setMatGmtCodigo(Integer matGmtCodigo) {
		this.matGmtCodigo = matGmtCodigo;
	}

	public String getTmv1Sigla() {
		return tmv1Sigla;
	}

	public void setTmv1Sigla(String tmv1Sigla) {
		this.tmv1Sigla = tmv1Sigla;
	}
	
	public Date getMmtDtGeracao() {
		return mmtDtGeracao;
	}

	public void setMmtDtGeracao(Date mmtDtGeracao) {
		this.mmtDtGeracao = mmtDtGeracao;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public Integer getEslDfeSeq() {
		return eslDfeSeq;
	}

	public void setEslDfeSeq(Integer eslDfeSeq) {
		this.eslDfeSeq = eslDfeSeq;
	}
	
	public Integer getEslFrnNumero() {
		return eslFrnNumero;
	}

	public void setEslFrnNumero(Integer eslFrnNumero) {
		this.eslFrnNumero = eslFrnNumero;
	}

	public Integer getNroDocGeracao() {
		return nroDocGeracao;
	}

	public void setNroDocGeracao(Integer nroDocGeracao) {
		this.nroDocGeracao = nroDocGeracao;
	}
	
	public Boolean getMmtIndEstorno() {
		return mmtIndEstorno;
	}

	public void setMmtIndEstorno(Boolean mmtIndEstorno) {
		this.mmtIndEstorno = mmtIndEstorno;
	}

	public Integer getFevSeq() {
		return fevSeq;
	}

	public void setFevSeq(Integer fevSeq) {
		this.fevSeq = fevSeq;
	}
	
	public Integer getMmtMatCodigo() {
		return mmtMatCodigo;
	}

	public void setMmtMatCodigo(Integer mmtMatCodigo) {
		this.mmtMatCodigo = mmtMatCodigo;
	}

	public Short getMmtAlm() {
		return mmtAlm;
	}

	public void setMmtAlm(Short mmtAlm) {
		this.mmtAlm = mmtAlm;
	}

	public String getMmtUmd() {
		return mmtUmd;
	}

	public void setMmtUmd(String mmtUmd) {
		this.mmtUmd = mmtUmd;
	}
	
	public Integer getMmtQuantidade() {
		return mmtQuantidade;
	}

	public void setMmtQuantidade(Integer mmtQuantidade) {
		this.mmtQuantidade = mmtQuantidade;
	}

	public Integer getIslSlcNumero() {
		return islSlcNumero;
	}

	public void setIslSlcNumero(Integer islSlcNumero) {
		this.islSlcNumero = islSlcNumero;
	}
	
	
	
	
	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}


	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}


	public Integer getIafNumero() {
		return iafNumero;
	}


	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}


	// Filtros
	public Date getMmtDataComp() {
		return mmtDataComp;
	}
	
	public void setMmtDataComp(Date mmtDataComp) {
		this.mmtDataComp = mmtDataComp;
	}
	
	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public Boolean getAdiantamentoAF() {
		return adiantamentoAF;
	}

	public void setAdiantamentoAF(Boolean adiantamentoAF) {
		this.adiantamentoAF = adiantamentoAF;
	}

	public Short[] getListaTipoMovimento() {
		return listaTipoMovimento;
	}

	public void setListaTipoMovimento(Short[] listaTipoMovimento) {
		this.listaTipoMovimento = listaTipoMovimento;
	}

	public Short[] getListaTipoMovimentoOrigem() {
		return listaTipoMovimentoOrigem;
	}

	public void setListaTipoMovimentoOrigem(Short[] listaTipoMovimentoOrigem) {
		this.listaTipoMovimentoOrigem = listaTipoMovimentoOrigem;
	}

	public Map<String, Object> getMapTipoMovimento() {
		return mapTipoMovimento;
	}

	public void setMapTipoMovimento(Map<String, Object> mapTipoMovimento) {
		this.mapTipoMovimento = mapTipoMovimento;
	}

	//VO C2
	public Long getFrnCgcC2() {
		return frnCgcC2;
	}

	public void setFrnCgcC2(Long frnCgcC2) {
		this.frnCgcC2 = frnCgcC2;
	}

	public Long getFrnCpfC2() {
		return frnCpfC2;
	}

	public void setFrnCpfC2(Long frnCpfC2) {
		this.frnCpfC2 = frnCpfC2;
	}

	public Integer getFevSeqC2() {
		return fevSeqC2;
	}

	public void setFevSeqC2(Integer fevSeqC2) {
		this.fevSeqC2 = fevSeqC2;
	}

	public String getFrnRazaoSocialC2() {
		return frnRazaoSocialC2;
	}

	public void setFrnRazaoSocialC2(String frnRazaoSocialC2) {
		this.frnRazaoSocialC2 = frnRazaoSocialC2;
	}

	public String getFevRrazaoSocialC2() {
		return fevRrazaoSocialC2;
	}

	public void setFevRrazaoSocialC2(String fevRrazaoSocialC2) {
		this.fevRrazaoSocialC2 = fevRrazaoSocialC2;
	}

	
	//Retono da C2
	public Long getVcnpj() {
		
		return v_cnpj;
	}
	
	public String getVcnpjConcatenado() {
		
		if(this.frnCgcC2 == null ){
			if(this.frnCpfC2 == null){
				if(this.fevSeqC2 == null){
					return StringUtils.EMPTY;
				}
				return this.fevSeqC2.toString();
			}
			return this.frnCpfC2.toString();
		}
	return this.frnCgcC2.toString();
		
	}

	public void setVCnpj(Long v_cnpj) {
		this.v_cnpj = v_cnpj;
	}

	public String getVRazaoSocial() {
		return v_razao_social;
	}

	public String getVRazaoSocialConcatenada() {
		if(this.frnRazaoSocialC2 == null){
			if(this.fevRrazaoSocialC2 == null){
				return StringUtils.EMPTY;
			}
			return this.fevRrazaoSocialC2;
		}
		return this.frnRazaoSocialC2;
	}
	
	public void setVRazaoSocial(String v_razao_social) {
		this.v_razao_social = v_razao_social;
	}
	
	public Long getNroNfC3() {
		return nroNfC3;
	}

	public void setNroNfC3(Long nroNfC3) {
		this.nroNfC3 = nroNfC3;
	}

	public String getAfC4() {
		
		if(this.pfr_lct_numeroC4 != null && this.nro_complementoC4 != null){
			return this.pfr_lct_numeroC4.toString() +"/"+this.nro_complementoC4.toString();
		}
		return afC4;
	}

	public void setAfC4(String afC4) {
		this.afC4 = afC4;
	}

	public Integer getPfrLctNumeroC4() {
		return pfr_lct_numeroC4;
	}

	public void setPfrLctNumeroC4(Integer pfr_lct_numeroC4) {
		this.pfr_lct_numeroC4 = pfr_lct_numeroC4;
	}


	public Short getNroComplementoC4() {
		return nro_complementoC4;
	}


	public void setNroComplementoC4(Short nro_complementoC4) {
		this.nro_complementoC4 = nro_complementoC4;
	}


	public Short getItemAFC5() {
		return itemAFC5;
	}

	public void setItemAFC5(Short itemAFC5) {
		this.itemAFC5 = itemAFC5;
	}
	
	
	//Campos da Projeção

	public Integer getCampo00() {
		return this.matGmtCodigo;
	}
	
	public Integer getCampo000() {
		
		String sigla = this.tmvSigla != null ? this.tmv1Sigla != null ? this.tmvSigla + this.tmv1Sigla  : ""  : "";  
				
		switch (sigla) {
		case "FF":
			return 1;
		case "DO":
			return 2;
		case "EMPS":
			return 3;
		case "INSL":
			return 4;
		case "TRS":
			return 5;
		case "CONSE":
			return 6;
		case "DVMVEMPC":
			return 7;
		case "DVMVEMPS":
			return 8;
		case "CONSSCONSE":
			return 9;
		case "EMPC":
			return 10;
		default:
			return 11;
		}
	}
	
	public String getCampo01() {
		
		String sigla = this.tmvSigla != null ? this.tmv1Sigla != null ? this.tmvSigla + this.tmv1Sigla  : ""  : "";  
		
		switch (sigla) {
		case "DVMVEMPC":
			return "DVMVE";
		case "DVMVEMPS":
			return "DVMVS";
		default:
			return this.tmvSigla;
		}
	}

	public Integer getCampo02() {
		if(this.eslSeq == null){
			if(this.nroDocGeracao == null){
				return null;
			}
			return this.nroDocGeracao;
		}
		return this.eslSeq;
	}

	public Integer getCampo03() {
		if(this.islEslSeq == null){
				return null;
		}
		return this.islEslSeq;
	}

	public String getCampo04() {
		if(this.tmv1Sigla == null){
			return null;
		}
		return this.tmv1Sigla;
	}

	public Integer getCampo05() {
		if(this.islSlcNumero == null){
			return null;
		}
		return this.islSlcNumero;
	}

	public Date getCampo06() {
		if(this.eslDtGeracao == null){
			if(this.mmtDtGeracao == null){
				return null;
			}
			return this.mmtDtGeracao;
		}
		return this.eslDtGeracao;
	}

	public String getCampo07() {
		if(this.eslIndEncerrado == null){
			return "";
		}
		else{
			if(this.eslIndEncerrado){
				return "S";
			}else{
				return "N";
			}
		}
	}

	public String getCampo08() {
		if(this.eslIndEstorno == null){
			return "";
		}
		else{
			if(this.eslIndEstorno){
				return "S";
			}else{
				return "N";
			}
		}
	}

	public Short getCampo09() {
		if(this.islSeqAlm == null){
			if(this.mmtAlm == null){
				return null;
			}
			return this.mmtAlm;
		}
		return this.islSeqAlm;
	}


	public Integer getCampo10() {
		if(this.islCodigoMat == null){
			if(this.mmtMatCodigo == null){
				return null;
			}
			return this.mmtMatCodigo;
		}
		return this.islCodigoMat;
	}

	public String getCampo11() {
		return this.matNome;
	}

	public String getCampo12() {
		if(this.islCodigoUm == null){
			if(this.mmtUmd == null){
				return "";
			}
			return this.mmtUmd;
		}
		return this.islCodigoUm;
	}

	public Integer getCampo13() {
		if(this.islQuantidade == null){
			if(this.mmtQuantidade == null){
				return null;
			}
			return this.mmtQuantidade;
		}
		return this.islQuantidade;
	}

	public Integer getCampo14() {
		
		if(this.islQtdeDevolvida == null){
			return 0;
		}
		return this.islQtdeDevolvida;
	}

	public BigDecimal getCampo15() {
		return this.mmtValor;
	}

	public Integer getCampo16() {
		if(this.eslDfeSeq == null){
			return null;
		}
	
		return this.eslDfeSeq;
	}

	public Integer getCampo17() {
		if(this.eslFrnNumero == null){
			return null;
		}
	
		return this.eslFrnNumero;
	}

	public Integer getCampo18() {
		if(this.fevSeq == null){
			return null;
		}
	
		return this.fevSeq;
	}

}
