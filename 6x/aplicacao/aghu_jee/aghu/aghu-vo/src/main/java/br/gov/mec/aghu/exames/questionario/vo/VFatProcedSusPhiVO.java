
package br.gov.mec.aghu.exames.questionario.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioModoLancamentoFat;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class VFatProcedSusPhiVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6008558002175424383L;
	
	private Integer phiSeq;
	private Integer iphSeq;
	private Short iphPhoSeq;
	private Short cpgCphCspCnvCodigo;
	private Byte cpgCphCspSeq;
	private Short cpgCphPhoSeq;
	private Short cpgGrcSeq;
	private Long codTabela;
	private String descricao;
	private Integer idadeMin;
	private Integer idadeMax;
	private DominioSituacao situacao;
	private Boolean procPrincipalApac;
	private Integer seq;
	private Boolean internacao;
	private Boolean tipoAih5;
	private Boolean consulta;
	private Boolean exigeConsulta;
	private Boolean cobrancaApac;
	private Boolean cobrancaConta;
	private Boolean cobrancaAmbulatorio;
	private DominioModoLancamentoFat modoLancamentoFat;
	private Short codProcedimento;
	private String descricaoProcedHospInterno;
	private DominioSituacao situacaoProcedHospInterno;
	private Boolean procedPrincipal;
	private Boolean procedSecundario;
	
	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Integer getIphSeq() {
		return iphSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	public Short getCpgCphCspCnvCodigo() {
		return cpgCphCspCnvCodigo;
	}
	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {
		this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;
	}
	public Byte getCpgCphCspSeq() {
		return cpgCphCspSeq;
	}
	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {
		this.cpgCphCspSeq = cpgCphCspSeq;
	}
	public Short getCpgCphPhoSeq() {
		return cpgCphPhoSeq;
	}
	public void setCpgCphPhoSeq(Short cpgCphPhoSeq) {
		this.cpgCphPhoSeq = cpgCphPhoSeq;
	}
	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}
	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}
	public Long getCodTabela() {
		return codTabela;
	}
	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getIdadeMin() {
		return idadeMin;
	}
	public void setIdadeMin(Integer idadeMin) {
		this.idadeMin = idadeMin;
	}
	public Integer getIdadeMax() {
		return idadeMax;
	}
	public void setIdadeMax(Integer idadeMax) {
		this.idadeMax = idadeMax;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public Boolean getProcPrincipalApac() {
		return procPrincipalApac;
	}
	public void setProcPrincipalApac(Boolean procPrincipalApac) {
		this.procPrincipalApac = procPrincipalApac;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Boolean getInternacao() {
		return internacao;
	}
	public void setInternacao(Boolean internacao) {
		this.internacao = internacao;
	}
	public Boolean getTipoAih5() {
		return tipoAih5;
	}
	public void setTipoAih5(Boolean tipoAih5) {
		this.tipoAih5 = tipoAih5;
	}
	public Boolean getConsulta() {
		return consulta;
	}
	public void setConsulta(Boolean consulta) {
		this.consulta = consulta;
	}
	public Boolean getExigeConsulta() {
		return exigeConsulta;
	}
	public void setExigeConsulta(Boolean exigeConsulta) {
		this.exigeConsulta = exigeConsulta;
	}
	public Boolean getCobrancaApac() {
		return cobrancaApac;
	}
	public void setCobrancaApac(Boolean cobrancaApac) {
		this.cobrancaApac = cobrancaApac;
	}
	public Boolean getCobrancaConta() {
		return cobrancaConta;
	}
	public void setCobrancaConta(Boolean cobrancaConta) {
		this.cobrancaConta = cobrancaConta;
	}
	public Boolean getCobrancaAmbulatorio() {
		return cobrancaAmbulatorio;
	}
	public void setCobrancaAmbulatorio(Boolean cobrancaAmbulatorio) {
		this.cobrancaAmbulatorio = cobrancaAmbulatorio;
	}
	public DominioModoLancamentoFat getModoLancamentoFat() {
		return modoLancamentoFat;
	}
	public void setModoLancamentoFat(DominioModoLancamentoFat modoLancamentoFat) {
		this.modoLancamentoFat = modoLancamentoFat;
	}
	public Short getCodProcedimento() {
		return codProcedimento;
	}
	public void setCodProcedimento(Short codProcedimento) {
		this.codProcedimento = codProcedimento;
	}
	public String getDescricaoProcedHospInterno() {
		return descricaoProcedHospInterno;
	}
	public void setDescricaoProcedHospInterno(String descricaoProcedHospInterno) {
		this.descricaoProcedHospInterno = descricaoProcedHospInterno;
	}
	public DominioSituacao getSituacaoProcedHospInterno() {
		return situacaoProcedHospInterno;
	}
	public void setSituacaoProcedHospInterno(
			DominioSituacao situacaoProcedHospInterno) {
		this.situacaoProcedHospInterno = situacaoProcedHospInterno;
	}
	public Boolean getProcedPrincipal() {
		return procedPrincipal;
	}
	public void setProcedPrincipal(Boolean procedPrincipal) {
		this.procedPrincipal = procedPrincipal;
	}
	public Boolean getProcedSecundario() {
		return procedSecundario;
	}
	public void setProcedSecundario(Boolean procedSecundario) {
		this.procedSecundario = procedSecundario;
	}
}
