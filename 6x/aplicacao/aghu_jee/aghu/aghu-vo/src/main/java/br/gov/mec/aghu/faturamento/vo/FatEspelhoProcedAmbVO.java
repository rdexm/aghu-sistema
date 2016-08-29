package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSexo;


public class FatEspelhoProcedAmbVO implements Serializable {
	
	
	private static final long serialVersionUID = -2494713444332054255L;
	
	private BigInteger nroCartaoSaude;
	private DominioSexo sexo;
	private Date dtNascimento;
	private DominioCor cor;
	private String codigoCid;
	private Long seq;
	private Date dthrRealizado;
	private DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca;
	private Short quantidade;
	private BigDecimal valor;
	private Integer mes;
	private Integer ano;
	private Date dtHrInicio;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Integer prhConNumero;
	private Integer prhPhiSeq;
	private Integer phiSeq;
	private Short unfSeq;
	private Short espSeq;
	private Integer codigoPac;
	private String nomePac;
	private Short cspCnvCodigo;
	private Byte cspSeq;
	private DominioOrigemProcedimentoAmbulatorialRealizado indOrigem;
	private Integer matriculaResp;
	private Short vinCodigoResp;
	// MbcCirurgias
	private Integer ppcCrgSeq; // ppc_crg_seq parte de MbcProcEspPorCirurgias
	// AipNacionalidades
	private Integer codigoNac; //  -- Marina 24/01/2011

	private Date dthrRealizadoTruncado;
	
	private Integer idade;

	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}

	public void setNroCartaoSaude(final BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(final DominioSexo sexo) {
		this.sexo = sexo;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(final Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public DominioCor getCor() {
		return cor;
	}

	public void setCor(final DominioCor cor) {
		this.cor = cor;
	}

	public String getCodigoCid() {
		return codigoCid;
	}

	public void setCodigoCid(final String codigoCid) {
		this.codigoCid = codigoCid;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(final Long seq) {
		this.seq = seq;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(final Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public DominioLocalCobrancaProcedimentoAmbulatorialRealizado getLocalCobranca() {
		return localCobranca;
	}

	public void setLocalCobranca(final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca) {
		this.localCobranca = localCobranca;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(final Short quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(final BigDecimal valor) {
		this.valor = valor;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(final Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(final Integer ano) {
		this.ano = ano;
	}

	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(final Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(final Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(final Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Integer getPrhConNumero() {
		return prhConNumero;
	}

	public void setPrhConNumero(final Integer prhConNumero) {
		this.prhConNumero = prhConNumero;
	}

	public Integer getPrhPhiSeq() {
		return prhPhiSeq;
	}

	public void setPrhPhiSeq(final Integer prhPhiSeq) {
		this.prhPhiSeq = prhPhiSeq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(final Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(final Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(final Short espSeq) {
		this.espSeq = espSeq;
	}

	public Integer getCodigoPac() {
		return codigoPac;
	}

	public void setCodigoPac(final Integer codigoPac) {
		this.codigoPac = codigoPac;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(final Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(final Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	public DominioOrigemProcedimentoAmbulatorialRealizado getIndOrigem() {
		return indOrigem;
	}

	public void setIndOrigem(final DominioOrigemProcedimentoAmbulatorialRealizado indOrigem) {
		this.indOrigem = indOrigem;
	}

	public Integer getMatriculaResp() {
		return matriculaResp;
	}

	public void setMatriculaResp(final Integer matriculaResp) {
		this.matriculaResp = matriculaResp;
	}

	public Short getVinCodigoResp() {
		return vinCodigoResp;
	}

	public void setVinCodigoResp(final Short vinCodigoResp) {
		this.vinCodigoResp = vinCodigoResp;
	}

	public Integer getPpcCrgSeq() {
		return ppcCrgSeq;
	}

	public void setPpcCrgSeq(final Integer ppcCrgSeq) {
		this.ppcCrgSeq = ppcCrgSeq;
	}

	public Integer getCodigoNac() {
		return codigoNac;
	}

	public void setCodigoNac(final Integer codigoNac) {
		this.codigoNac = codigoNac;
	}

	public Date getDthrRealizadoTruncado() {
		return dthrRealizadoTruncado;
	}

	public void setDthrRealizadoTruncado(final Date dthrRealizadoTruncado) {
		this.dthrRealizadoTruncado = dthrRealizadoTruncado;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	
}
