package br.gov.mec.aghu.compras.contaspagar.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Retorno do cursor <code>BUSCA_CONTA</code> de <code>FATF_ARQ_TXT_INT</code>
 * implementado em GeracaoArquivoTextoInternacaoRN
 * 
 * @author alejandro
 *
 */

public class TituloBloqueadoVO implements BaseBean {

	private static final long serialVersionUID = 607025872814687081L;

	private Integer bo;//BO
	private Integer seq;//Título
	private DominioBoletimOcorrencias indSituacao;//Situação
	private Date dtVencimento;//Dt Vencto
	private Double valorTitulo;//Valor Título
	private Integer nrsSeq;//NR
	private Integer nrsSeqOrigemNf;//NR Origem NF
	private String motivo;//Motivo
	private String fornecedor;//Fornecedor
	private Long cnpj;//CNPJ
	private Long cpf;//CPF
	private BigDecimal valorBo;//Valor BO
	private Double valorNr;
	
	public TituloBloqueadoVO(){
		
	}
	
	public TituloBloqueadoVO(Integer bo, Integer seq,
			DominioBoletimOcorrencias indSituacao, Date dtVencimento,
			Double valorTitulo, Integer nrsSeq, Integer nrsSeqOrigemNf,
			String motivo, String fornecedor, Long cnpj, Long cpf,
			BigDecimal valorBo, Double valorNr) {
		super();
		this.bo = bo;
		this.seq = seq;
		this.indSituacao = indSituacao;
		this.dtVencimento = dtVencimento;
		this.valorTitulo = valorTitulo;
		this.nrsSeq = nrsSeq;
		this.nrsSeqOrigemNf = nrsSeqOrigemNf;
		this.motivo = motivo;
		this.fornecedor = fornecedor;
		this.cnpj = cnpj;
		this.cpf = cpf;
		this.valorBo = valorBo;
		this.valorNr = valorNr;
	}
	
	public Integer getBo() {
		return bo;
	}
	public void setBo(Integer bo) {
		this.bo = bo;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public DominioBoletimOcorrencias getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioBoletimOcorrencias indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Date getDtVencimento() {
		return dtVencimento;
	}
	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}
	public BigDecimal getValorBo() {
		return valorBo;
	}
	public void setValorBo(BigDecimal valorBo) {
		this.valorBo = valorBo;
	}
	public Double getValorTitulo() {
		return valorTitulo;
	}
	public void setValorTitulo(Double valorTitulo) {
		this.valorTitulo = valorTitulo;
	}
	public Integer getNrsSeq() {
		return nrsSeq;
	}
	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}
	public Integer getNrsSeqOrigemNf() {
		return nrsSeqOrigemNf;
	}
	public void setNrsSeqOrigemNf(Integer nrsSeqOrigemNf) {
		this.nrsSeqOrigemNf = nrsSeqOrigemNf;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
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
	public Double getValorNr() {
		return valorNr;
	}
	public void setValorNr(Double valorNr) {
		this.valorNr = valorNr;
	}
		
}
