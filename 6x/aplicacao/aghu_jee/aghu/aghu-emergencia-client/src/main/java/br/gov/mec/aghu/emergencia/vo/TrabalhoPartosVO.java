package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoParto;

public class TrabalhoPartosVO implements Serializable {

	private static final long serialVersionUID = 1303841910675500134L;

	private Integer pacCodigo;
	private Short seqp;
	private Integer servidorMatricula;
	private Short servidorVinCodigo;
	private Integer servidorMatriculaIndicado;
	private Short servidorVinCodigoIndicado;
	private Integer servidorMatriculaIndicado2;
	private Short servidorVinCodigoIndicado2;
	private Boolean indicadorPartoInduzido;
	private String justificativa;
	private String observacao;
	private Integer inaSeq;
	private DominioTipoParto tipoParto;
	private String indicacoesCtg;
	private Date dataHoraInicialCtg;

	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Integer getServidorMatricula() {
		return servidorMatricula;
	}
	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}
	public Short getServidorVinCodigo() {
		return servidorVinCodigo;
	}
	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}
	public Integer getServidorMatriculaIndicado() {
		return servidorMatriculaIndicado;
	}
	public void setServidorMatriculaIndicado(Integer servidorMatriculaIndicado) {
		this.servidorMatriculaIndicado = servidorMatriculaIndicado;
	}
	public Short getServidorVinCodigoIndicado() {
		return servidorVinCodigoIndicado;
	}
	public void setServidorVinCodigoIndicado(Short servidorVinCodigoIndicado) {
		this.servidorVinCodigoIndicado = servidorVinCodigoIndicado;
	}
	public Integer getServidorMatriculaIndicado2() {
		return servidorMatriculaIndicado2;
	}
	public void setServidorMatriculaIndicado2(Integer servidorMatriculaIndicado2) {
		this.servidorMatriculaIndicado2 = servidorMatriculaIndicado2;
	}
	public Short getServidorVinCodigoIndicado2() {
		return servidorVinCodigoIndicado2;
	}
	public void setServidorVinCodigoIndicado2(Short servidorVinCodigoIndicado2) {
		this.servidorVinCodigoIndicado2 = servidorVinCodigoIndicado2;
	}
	public Boolean getIndicadorPartoInduzido() {
		return indicadorPartoInduzido;
	}
	public void setIndicadorPartoInduzido(Boolean indicadorPartoInduzido) {
		this.indicadorPartoInduzido = indicadorPartoInduzido; 
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Integer getInaSeq() {
		return inaSeq;
	}
	public void setInaSeq(Integer inaSeq) {
		this.inaSeq = inaSeq;
	}
	public DominioTipoParto getTipoParto() {
		return tipoParto;
	}
	public void setTipoParto(DominioTipoParto tipoParto) {
		this.tipoParto = tipoParto;
	}
	public String getIndicacoesCtg() {
		return indicacoesCtg;
	}
	public void setIndicacoesCtg(String indicacoesCtg) {
		this.indicacoesCtg = indicacoesCtg;
	}
	public Date getDataHoraInicialCtg() {
		return dataHoraInicialCtg;
	}
	public void setDataHoraInicialCtg(Date dataHoraInicialCtg) {
		this.dataHoraInicialCtg = dataHoraInicialCtg;
	}
}
