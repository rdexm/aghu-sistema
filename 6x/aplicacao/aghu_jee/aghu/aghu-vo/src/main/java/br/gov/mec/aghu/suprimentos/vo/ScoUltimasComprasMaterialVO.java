package br.gov.mec.aghu.suprimentos.vo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

public class ScoUltimasComprasMaterialVO implements BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8407716449467906174L;
	private Integer solicitacao;
	private Integer nroPAC;
	private String modl;
	private String modlDesc;
	private Date dtAbertura;
	private Short cp;
	private Integer nroAF;
	private Integer numeroNr;
	private Date dataNr;
	private Long notaFiscal;
	private String formaPgto;
	private Integer quantidade;
	private Double valor;
	private Integer numeroFornecedor;
	private String razaoSocial;
	private Long cnpj;
	private Long cpf;
	private Short ddi;
	private Short ddd;
	private Long fone;
	private String marcaComercial;
	private String nomeComercial;
	private Integer afnNumero;
	
	//#5735
	private String inciso;
	private String pgto;
	private String af;
	private BigDecimal valorUnit;
	
	
	public ScoUltimasComprasMaterialVO() {
		
	}

	
	public enum Fields {
		SOLICITACAO("solicitacao"),
		NRO_PAC("nroPAC"),
		MLC_CODIGO("modl"),
		MLC_DESC("modlDesc"),
		DT_ABERTURA("dtAbertura"),
		NRO_AF("nroAF"),
		CP("cp"),
		NUMERO_NR("numeroNr"),
		DATA_NR("dataNr"),
		NOTA_FISCAL("notaFiscal"),
		FORMA_PGTO("formaPgto"),
		QTDE("quantidade"),
		VALOR("valor"),
		FORNECEDOR_NRO("numeroFornecedor"),
		RAZAO_SOCIAL("razaoSocial"),
		CNPJ("cnpj"),
		CPF("cpf"),
		DDI("ddi"),
		DDD("ddd"),
		FONE("fone"),
		MARCA_COMERCIAL("marcaComercial"),
		NOME_COMERCIAL("nomeComercial"),
		AFN_NUMERO("afnNumero"),
		INCISO_ARTIGO_LICITACAO("inciso"),
		SIGLA("pgto"),
		NRO_COMPLEMENTO("nroComplemento");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getNroPAC() {
		return nroPAC;
	}

	public void setNroPAC(Integer nroPAC) {
		this.nroPAC = nroPAC;
	}
	
	
	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getModl() {
		return modl;
	}

	public void setModl(String modl) {
		this.modl = modl;
	}

	public String getModlDesc() {
		return modlDesc;
	}

	public void setModlDesc(String modlDesc) {
		this.modlDesc = modlDesc;
	}

	public Date getDtAbertura() {
		return dtAbertura;
	}

	public void setDtAbertura(Date dtAbertura) {
		this.dtAbertura = dtAbertura;
	}

	public Integer getNumeroNr() {
		return numeroNr;
	}

	public void setNumeroNr(Integer numeroNr) {
		this.numeroNr = numeroNr;
	}

	public Date getDataNr() {
		return dataNr;
	}

	public void setDataNr(Date dataNr) {
		this.dataNr = dataNr;
	}

	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public String getFormaPgto() {
		return formaPgto;
	}

	public void setFormaPgto(String formaPgto) {
		this.formaPgto = formaPgto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Short getDdi() {
		return ddi;
	}

	public void setDdi(Short ddi) {
		this.ddi = ddi;
	}

	public Short getDdd() {
		return ddd;
	}

	public void setDdd(Short ddd) {
		this.ddd = ddd;
	}

	public Long getFone() {
		return fone;
	}

	public void setFone(Long fone) {
		this.fone = fone;
	}

	public String getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(String marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public String getNomeComercial() {
		return nomeComercial;
	}

	public void setNomeComercial(String nomeComercial) {
		this.nomeComercial = nomeComercial;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public String getInciso() {
		return inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public String getAf() {
		af = getNroAF().toString()+"/"+getCp().toString();
		return af;
	}

	public String getPgto() {
		return pgto;
	}

	public void setPgto(String pgto) {
		this.pgto = pgto;
	}

	public BigDecimal getValorUnit() {
		if(getValor() != null && getQuantidade() != null){
			BigDecimal valor = new BigDecimal(getValor());
			BigDecimal quantidade = new BigDecimal(getQuantidade());
			valorUnit = valor.divide(quantidade, new MathContext(5));
		}else{
			valorUnit = null;
		}
		return valorUnit;
	}
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getValorUnitario() {
		String valor = AghuNumberFormat.formatarValor(getValorUnit(), "##0.0000");
		return valor;
	}

	public void setAf(String af) {
		this.af = af;
	}

	public void setValorUnit(BigDecimal valorUnit) {
		this.valorUnit = valorUnit;
	}
}
