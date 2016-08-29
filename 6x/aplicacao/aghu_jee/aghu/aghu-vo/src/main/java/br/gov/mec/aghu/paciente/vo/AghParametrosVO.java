package br.gov.mec.aghu.paciente.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AghParametrosVO implements BaseBean {

	private static final long serialVersionUID = 4772731568907096410L;
	
	private int seq;
	private String sisSigla;
	private String nome;
	private String mantemHistorico;
	private Date criadoEm;
	private String criadoPor;
	private Date alteradoEm;
	private String alteradoPor;
	private Date vlrData;
	private BigDecimal vlrNumerico;
	private String vlrTexto;
	private String descricao;
	private String rotinaConsistencia;
	private String msg;
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getSisSigla() {
		return sisSigla;
	}
	public void setSisSigla(String sisSigla) {
		this.sisSigla = sisSigla;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMantemHistorico() {
		return mantemHistorico;
	}
	public void setMantemHistorico(String mantemHistorico) {
		this.mantemHistorico = mantemHistorico;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getCriadoPor() {
		return criadoPor;
	}
	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}
	public Date getAlteradoEm() {
		return alteradoEm;
	}
	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	public String getAlteradoPor() {
		return alteradoPor;
	}
	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}
	public Date getVlrData() {
		return vlrData;
	}
	public void setVlrData(Date vlrData) {
		this.vlrData = vlrData;
	}
	public BigDecimal getVlrNumerico() {
		return vlrNumerico;
	}
	public void setVlrNumerico(BigDecimal vlrNumerico) {
		this.vlrNumerico = vlrNumerico;
	}
	public String getVlrTexto() {
		return vlrTexto;
	}
	public void setVlrTexto(String vlrTexto) {
		this.vlrTexto = vlrTexto;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getRotinaConsistencia() {
		return rotinaConsistencia;
	}
	public void setRotinaConsistencia(String rotinaConsistencia) {
		this.rotinaConsistencia = rotinaConsistencia;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
