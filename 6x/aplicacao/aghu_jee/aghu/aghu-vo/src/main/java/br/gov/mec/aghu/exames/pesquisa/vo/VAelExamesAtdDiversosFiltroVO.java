package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndImpressoLaudo;

public class VAelExamesAtdDiversosFiltroVO implements Serializable {

	private static final long serialVersionUID = 8460786210198236532L;

	private Integer pjqSeq;
	private Integer laeSeq;
	private Integer ccqSeq;
	private Integer ddvSeq;
	private Integer cadSeq;
	private Integer soeSeq;
	private Integer pacCodigo;
	private Integer atvPacCodigo;
	private Long numeroAp;
	private Integer lu2Seq;
	private DominioIndImpressoLaudo[] indImpressoLaudo;
	private String sitCodigo;
	private String sitCodigoCancelado;
	private String sitCodigoPendente;
	private boolean somenteLaboratorial;
	private Date dtInicio;
	private Date dtFinal;
	
	public Integer getPjqSeq() {
		return pjqSeq;
	}

	public void setPjqSeq(Integer pjqSeq) {
		this.pjqSeq = pjqSeq;
	}

	public Integer getLaeSeq() {
		return laeSeq;
	}

	public void setLaeSeq(Integer laeSeq) {
		this.laeSeq = laeSeq;
	}

	public Integer getCcqSeq() {
		return ccqSeq;
	}

	public void setCcqSeq(Integer ccqSeq) {
		this.ccqSeq = ccqSeq;
	}

	public Integer getDdvSeq() {
		return ddvSeq;
	}

	public void setDdvSeq(Integer ddvSeq) {
		this.ddvSeq = ddvSeq;
	}

	public Integer getCadSeq() {
		return cadSeq;
	}

	public void setCadSeq(Integer cadSeq) {
		this.cadSeq = cadSeq;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getAtvPacCodigo() {
		return atvPacCodigo;
	}

	public void setAtvPacCodigo(Integer atvPacCodigo) {
		this.atvPacCodigo = atvPacCodigo;
	}

	public DominioIndImpressoLaudo[] getIndImpressoLaudo() {
		return indImpressoLaudo;
	}

	public void setIndImpressoLaudo(DominioIndImpressoLaudo[] indImpressoLaudo) {
		this.indImpressoLaudo = indImpressoLaudo;
	}

	public String getSitCodigo() {
		return sitCodigo;
	}

	public void setSitCodigo(String sitCodigo) {
		this.sitCodigo = sitCodigo;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setSitCodigoCancelado(String sitCodigoCancelado) {
		this.sitCodigoCancelado = sitCodigoCancelado;
	}

	public String getSitCodigoCancelado() {
		return sitCodigoCancelado;
	}

	public void setSitCodigoPendente(String sitCodigoPendente) {
		this.sitCodigoPendente = sitCodigoPendente;
	}

	public String getSitCodigoPendente() {
		return sitCodigoPendente;
	}

	public void setSomenteLaboratorial(boolean somenteLaboratorial) {
		this.somenteLaboratorial = somenteLaboratorial;
	}

	public boolean isSomenteLaboratorial() {
		return somenteLaboratorial;
	}
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public Date getDtInicio() {
		return dtInicio;
	}
	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}
	public Date getDtFinal() {
		return dtFinal;
	}
	
	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}
	
}