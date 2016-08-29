package br.gov.mec.aghu.compras.contaspagar.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioMotivoEstornoTitulo;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de #37797 – Consultar posição do Título
 * 
 * @author aghu
 *
 */
public class PosicaoTituloVO implements BaseBean {

	private static final long serialVersionUID = 3920366604874922316L;

	/*
	 * Atributos da consulta C1
	 */
	private Integer numero; // TTL.SEQ AS "NÚMERO"
	private Short parcela; // TTL.NRO_PARCELA AS "PARCELA"
	private DominioSituacaoTitulo situacao; // TTL.IND_SITUACAO AS "SITUAÇÃO"
	private Integer notaRecebimento; // TTL.NRS_SEQ AS "NR"
	private Integer numeroAF; // AFS.PFR_LCT_NUMERO AS "NÚMERO AF"
	private String numeroAFFormatada;
	private String serie;
	private Long numeroNF;
	private Short complemento; // AFS.NRO_COMPLEMENTO AS "COMPLEMENTO"
	private Integer numeroContrato; // AFS.NRO_CONTRATO AS "NRO CONTRATO"
	private Date dataGeracao; // TTL.DT_GERACAO AS "DATA GERAÇÃO"

	//private String responsavelTitulo;
	// private Integer servidorMatriculaTitulo; // TTL.SER_MATRICULA
	// private Short servidorVinCodigoTitulo; // TTL.SER_VIN_CODIGO

	private Date dataVencimento; // TTL.DT_VENCIMENTO AS "DATA VENCTO"
	private Double valor; // TTL.VALOR AS "VALOR"
	private String empenho; // AFS.NRO_EMPENHO AS "EMPENHO"
	private BigDecimal valorEmpenho; // AFS.VALOR_EMPENHO AS "VALOR EMPENHO"
	private String fornecedor; // FRN.RAZAO_SOCIAL AS "FORNECEDOR",
	private Long cgc; // COALESCE(FRN.CGC, FRN.CPF) AS "CPF/CNPJ"
	private Long cpf;// COALESCE(FRN.CGC, FRN.CPF) AS "CPF/CNPJ"
	private Boolean estorno; // TTL.IND_ESTORNO
	private DominioMotivoEstornoTitulo motivoEstorno; // TTL.MOTIVO_ESTORNO AS "MOTIVO"
	private Date dataEstorno; // DATA

	private String responsavelEstornado;
	// private Integer servidorMatriculaEstornado; //
	// TTL.SER_MATRICULA_ESTORNADO
	// private Short servidorVinCodigoEstornado; // TTL.SER_VIN_CODIGO_ESTORNADO

	private Short tipoDocPagamento; // PGT.TDP_SEQ
	private Integer documento; // PGT.NRO_DOCUMENTO -- FIELDSET DOCUMENTO
	private Integer banco; // PGT.CNF_AGB_BCO_CODIGO
	private String bancoNome; // FCP_BANCOS.NOME   (C5)
	private Integer agencia; // PGT.CNF_AGB_CODIGO AS "AGÊNCIA"
	private String conta; // PGT.CNF_CONTA_CORRENTE AS "CONTA"
	private Date pagoEm; // PGT.DT_PAGAMENTO AS "PAGO EM"

	private String responsavelPagamento; // PGT.SER_MATRICULA PGT.SER_VIN_CODIGO
	// private Integer servidorMatriculaPagamento; // PGT.SER_MATRICULA
	// private Short servidorVinCodigoPagamento; // PGT.SER_VIN_CODIGO

	private Double acrescimo; // PGT.VLR_ACRESCIMO AS "ACRÉSCIMO"
	private Double valorPagamento; // TTL.VALOR AS "VALOR"
	private Double desconto; // PGT.VLR_DESCONTO AS "DESCONTO"
	private String observacao; // PGT.OBSERVACAO AS "OBSERVAÇÃO"

	/*
	 * Atributos adicionais
	 */

	private String geradoPor; // Responsável pela geração
	private Integer pagamentoTituloSeq; // PGT.TTL_SEQ
	private Short pagamentoNumero; // PGT.NUMERO
	private Integer bo; // BO
	private String tipoDocPagamentoDescricao; // PGT.DESCRICAO

	/*
	 * Getters e setters
	 */

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Short getParcela() {
		return parcela;
	}

	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}

	public DominioSituacaoTitulo getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoTitulo situacao) {
		this.situacao = situacao;
	}

	public Integer getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(Integer notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}
	
	public String getNumeroAFFormatada() {
		numeroAFFormatada = (getNumeroAF().toString()==null?"":getNumeroAF().toString()) + "/" + (getComplemento().toString()==null?"":getComplemento().toString());
		return numeroAFFormatada;
	}
	
	public void setNumeroAFFormatada(String numeroAFFormatada) {
		this.numeroAFFormatada = numeroAFFormatada;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}
	
	public Long getNumeroNF() {
		return numeroNF;
	}

	public void setNumeroNF(Long numeroNF) {
		this.numeroNF = numeroNF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getEmpenho() {
		return empenho;
	}

	public void setEmpenho(String empenho) {
		this.empenho = empenho;
	}

	public BigDecimal getValorEmpenho() {
		return valorEmpenho;
	}

	public void setValorEmpenho(BigDecimal valorEmpenho) {
		this.valorEmpenho = valorEmpenho;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Boolean getEstorno() {
		return estorno;
	}

	public void setEstorno(Boolean estorno) {
		this.estorno = estorno;
	}

	public DominioMotivoEstornoTitulo getMotivoEstorno() {
		return motivoEstorno;
	}

	public void setMotivoEstorno(DominioMotivoEstornoTitulo motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}

	public Date getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	public String getResponsavelEstornado() {
		return responsavelEstornado;
	}

	public void setResponsavelEstornado(String responsavelEstornado) {
		this.responsavelEstornado = responsavelEstornado;
	}

	public Short getTipoDocPagamento() {
		return tipoDocPagamento;
	}

	public void setTipoDocPagamento(Short tipoDocPagamento) {
		this.tipoDocPagamento = tipoDocPagamento;
	}

	public Integer getDocumento() {
		return documento;
	}

	public void setDocumento(Integer documento) {
		this.documento = documento;
	}

	public Integer getBanco() {
		return banco;
	}

	public void setBanco(Integer banco) {
		this.banco = banco;
	}

	public Integer getAgencia() {
		return agencia;
	}

	public void setAgencia(Integer agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public Date getPagoEm() {
		return pagoEm;
	}

	public void setPagoEm(Date pagoEm) {
		this.pagoEm = pagoEm;
	}

	public String getResponsavelPagamento() {
		return responsavelPagamento;
	}

	public void setResponsavelPagamento(String responsavelPagamento) {
		this.responsavelPagamento = responsavelPagamento;
	}

	public Double getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(Double acrescimo) {
		this.acrescimo = acrescimo;
	}

	public Double getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(Double valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getGeradoPor() {
		return geradoPor;
	}

	public void setGeradoPor(String geradoPor) {
		this.geradoPor = geradoPor;
	}

	public Integer getPagamentoTituloSeq() {
		return pagamentoTituloSeq;
	}

	public void setPagamentoTituloSeq(Integer pagamentoTituloSeq) {
		this.pagamentoTituloSeq = pagamentoTituloSeq;
	}

	public Short getPagamentoNumero() {
		return pagamentoNumero;
	}

	public void setPagamentoNumero(Short pagamentoNumero) {
		this.pagamentoNumero = pagamentoNumero;
	}
	
	public Integer getBo() {
		return bo;
	}
	
	
	public void setBo(Integer bo) {
		this.bo = bo;
	}

	public String getBancoNome() {
		return bancoNome;
	}

	public void setBancoNome(String bancoNome) {
		this.bancoNome = bancoNome;
	}

	public String getTipoDocPagamentoDescricao() {
		return tipoDocPagamentoDescricao;
	}
	
	public void setTipoDocPagamentoDescricao(String tipoDocPagamentoDescricao) {
		this.tipoDocPagamentoDescricao = tipoDocPagamentoDescricao;
	}
	
	/*
	 * Métodos utilitários
	 */

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getNumero());
		umHashCodeBuilder.append(this.getParcela());
		umHashCodeBuilder.append(this.getSituacao());
		umHashCodeBuilder.append(this.getNotaRecebimento());
		umHashCodeBuilder.append(this.getNumeroAF());
		umHashCodeBuilder.append(this.getComplemento());
		umHashCodeBuilder.append(this.getNumeroContrato());
		umHashCodeBuilder.append(this.getDataGeracao());
		umHashCodeBuilder.append(this.getDataVencimento());
		umHashCodeBuilder.append(this.getValor());
		umHashCodeBuilder.append(this.getEmpenho());
		umHashCodeBuilder.append(this.getValorEmpenho());
		umHashCodeBuilder.append(this.getFornecedor());
		umHashCodeBuilder.append(this.getCgc());
		umHashCodeBuilder.append(this.getCpf());
		umHashCodeBuilder.append(this.getEstorno());
		umHashCodeBuilder.append(this.getMotivoEstorno());
		umHashCodeBuilder.append(this.getDataEstorno());
		umHashCodeBuilder.append(this.getResponsavelEstornado());
		umHashCodeBuilder.append(this.getTipoDocPagamento());
		umHashCodeBuilder.append(this.getDocumento());
		umHashCodeBuilder.append(this.getBanco());
		umHashCodeBuilder.append(this.getBancoNome());
		umHashCodeBuilder.append(this.getAgencia());
		umHashCodeBuilder.append(this.getConta());
		umHashCodeBuilder.append(this.getPagoEm());
		umHashCodeBuilder.append(this.getResponsavelPagamento());
		umHashCodeBuilder.append(this.getAcrescimo());
		umHashCodeBuilder.append(this.getValorPagamento());
		umHashCodeBuilder.append(this.getDesconto());
		umHashCodeBuilder.append(this.getObservacao());
		umHashCodeBuilder.append(this.getGeradoPor());
		umHashCodeBuilder.append(this.getPagamentoTituloSeq());
		umHashCodeBuilder.append(this.getPagamentoNumero());
		umHashCodeBuilder.append(this.getBo());
		umHashCodeBuilder.append(this.getTipoDocPagamentoDescricao());		
		
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
		if (!(obj instanceof PosicaoTituloVO)) {
			return false;
		}
		PosicaoTituloVO other = (PosicaoTituloVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getNumero(), other.getNumero());
		umEqualsBuilder.append(this.getParcela(), other.getParcela());
		umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
		umEqualsBuilder.append(this.getNotaRecebimento(), other.getNotaRecebimento());
		umEqualsBuilder.append(this.getNumeroAF(), other.getNumeroAF());
		umEqualsBuilder.append(this.getComplemento(), other.getComplemento());
		umEqualsBuilder.append(this.getNumeroContrato(), other.getNumeroContrato());
		umEqualsBuilder.append(this.getDataGeracao(), other.getDataGeracao());
		umEqualsBuilder.append(this.getDataVencimento(), other.getDataVencimento());
		umEqualsBuilder.append(this.getValor(), other.getValor());
		umEqualsBuilder.append(this.getEmpenho(), other.getEmpenho());
		umEqualsBuilder.append(this.getValorEmpenho(), other.getValorEmpenho());
		umEqualsBuilder.append(this.getFornecedor(), other.getFornecedor());
		umEqualsBuilder.append(this.getCgc(), other.getCgc());
		umEqualsBuilder.append(this.getCpf(), other.getCpf());
		umEqualsBuilder.append(this.getEstorno(), other.getEstorno());
		umEqualsBuilder.append(this.getMotivoEstorno(), other.getMotivoEstorno());
		umEqualsBuilder.append(this.getDataEstorno(), other.getDataEstorno());
		umEqualsBuilder.append(this.getResponsavelEstornado(), other.getResponsavelEstornado());
		umEqualsBuilder.append(this.getTipoDocPagamento(), other.getTipoDocPagamento());
		umEqualsBuilder.append(this.getDocumento(), other.getDocumento());
		umEqualsBuilder.append(this.getBanco(), other.getBanco());
		umEqualsBuilder.append(this.getBancoNome(), other.getBancoNome());
		umEqualsBuilder.append(this.getAgencia(), other.getAgencia());
		umEqualsBuilder.append(this.getConta(), other.getConta());
		umEqualsBuilder.append(this.getPagoEm(), other.getPagoEm());
		umEqualsBuilder.append(this.getResponsavelPagamento(), other.getResponsavelPagamento());
		umEqualsBuilder.append(this.getAcrescimo(), other.getAcrescimo());
		umEqualsBuilder.append(this.getValorPagamento(), other.getValorPagamento());
		umEqualsBuilder.append(this.getDesconto(), other.getDesconto());
		umEqualsBuilder.append(this.getObservacao(), other.getObservacao());
		umEqualsBuilder.append(this.getGeradoPor(), other.getGeradoPor());
		umEqualsBuilder.append(this.getPagamentoTituloSeq(), other.getPagamentoTituloSeq());
		umEqualsBuilder.append(this.getPagamentoNumero(), other.getPagamentoNumero());
		umEqualsBuilder.append(this.getBo(), other.getBo());
		umEqualsBuilder.append(this.getTipoDocPagamentoDescricao(), other.getTipoDocPagamentoDescricao());		
				
		return umEqualsBuilder.isEquals();
	}
	
	public enum Fields {
		ACRESCIMO("acrescimo"),
		AGENCIA("agencia"),
		BANCO("banco"),
		BANCO_NOME("bancoNome"),
		CGC("cgc"),
		COMPLEMENTO("complemento"),
		CONTA("conta"),
		CPF("cpf"),
		DATA_ESTORNO("dataEstorno"),
		DATA_GERACAO("dataGeracao"),
		DATA_VENCIMENTO("dataVencimento"),
		DESCONTO("desconto"),
		DOCUMENTO("documento"),
		EMPENHO("empenho"),
		ESTORNO("estorno"),
		FORNECEDOR("fornecedor"),
		GERADO_POR("geradoPor"),
		MOTIVO_ESTORNO("motivoEstorno"),
		NOTA_RECEBIMENTO("notaRecebimento"),
		NUMERO("numero"),
		NUMERO_AF("numeroAF"),
		NUMERO_CONTRATO("numeroContrato"),
		OBSERVACAO("observacao"),
		PAGAMENTO_NUMERO("pagamentoNumero"),
		PAGAMENTO_TITULO_SEQ("pagamentoTituloSeq"),
		PAGO_EM("pagoEm"),
		PARCELA("parcela"),
		RESPONSAVEL_ESTORNADO("responsavelEstornado"),
		RESPONSAVEL_PAGAMENTO("responsavelPagamento"),
		SITUACAO("situacao"),
		TIPO_DOC_PAGAMENTO("tipoDocPagamento"),
		VALOR("valor"),
		VALOR_EMPENHO("valorEmpenho"),
		VALOR_PAGAMENTO("valorPagamento"),
		SERIE("serie"),
		NUMERO_NF("numeroNF"),
		BO("bo"),
		TIPO_DOC_PAGAMENTO_DESCRICAO("tipoDocPagamentoDescricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
