package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;


public class ConvenioPlanoVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7945275700753969098L;
	private Short cnvCodigo;
	private Byte plano;
	private String descConv;
	private String descPlan;
	private Boolean indPermissaoInternacao;
	private Boolean indVerfEscalaProfInt;
	private Boolean indExigeNumMatr;
	private Boolean indSelAutomProf;
	private DominioSituacao indSituacao;
	private Boolean indRestringeProf;
	private DominioGrupoConvenio grupoConvenio;
	private DominioTipoPlano indTipoPlano;
	private DominioSituacao planoIndSituacao;
	private Short pgdSeq;
	
	
	/**
	 * @return the indPermissaoInternacao
	 */
	public Boolean getIndPermissaoInternacao() {
		return indPermissaoInternacao;
	}
	/**
	 * @param indPermissaoInternacao the indPermissaoInternacao to set
	 */
	public void setIndPermissaoInternacao(Boolean indPermissaoInternacao) {
		this.indPermissaoInternacao = indPermissaoInternacao;
	}
	/**
	 * @return the indVerfEscalaProfInt
	 */
	public Boolean getIndVerfEscalaProfInt() {
		return indVerfEscalaProfInt;
	}
	/**
	 * @param indVerfEscalaProfInt the indVerfEscalaProfInt to set
	 */
	public void setIndVerfEscalaProfInt(Boolean indVerfEscalaProfInt) {
		this.indVerfEscalaProfInt = indVerfEscalaProfInt;
	}
	/**
	 * @return the indExigeNumMatr
	 */
	public Boolean getIndExigeNumMatr() {
		return indExigeNumMatr;
	}
	/**
	 * @param indExigeNumMatr the indExigeNumMatr to set
	 */
	public void setIndExigeNumMatr(Boolean indExigeNumMatr) {
		this.indExigeNumMatr = indExigeNumMatr;
	}
	/**
	 * @return the indSelAutomProf
	 */
	public Boolean getIndSelAutomProf() {
		return indSelAutomProf;
	}
	/**
	 * @param indSelAutomProf the indSelAutomProf to set
	 */
	public void setIndSelAutomProf(Boolean indSelAutomProf) {
		this.indSelAutomProf = indSelAutomProf;
	}

	
	/**
	 * @return the indRestringeProf
	 */
	public Boolean getIndRestringeProf() {
		return indRestringeProf;
	}
	/**
	 * @param indRestringeProf the indRestringeProf to set
	 */
	public void setIndRestringeProf(Boolean indRestringeProf) {
		this.indRestringeProf = indRestringeProf;
	}
	/**
	 * @return the grupoConvenio
	 */
	public DominioGrupoConvenio getGrupoConvenio() {
		return grupoConvenio;
	}
	/**
	 * @param grupoConvenio the grupoConvenio to set
	 */
	public void setGrupoConvenio(DominioGrupoConvenio grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}
	/**
	 * @return the indTipoPlano
	 */
	public DominioTipoPlano getIndTipoPlano() {
		return indTipoPlano;
	}
	/**
	 * @param indTipoPlano the indTipoPlano to set
	 */
	public void setIndTipoPlano(DominioTipoPlano indTipoPlano) {
		this.indTipoPlano = indTipoPlano;
	}
	
	
	/**
	 * @return the pgdSeq
	 */
	public Short getPgdSeq() {
		return pgdSeq;
	}
	/**
	 * @param pgdSeq the pgdSeq to set
	 */
	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}
	/**
	 * @return the cnvCodigo
	 */
	public Short getCnvCodigo() {
		return cnvCodigo;
	}
	/**
	 * @param cnvCodigo the cnvCodigo to set
	 */
	public void setCnvCodigo(Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}
	/**
	 * @return the plano
	 */
	public Byte getPlano() {
		return plano;
	}
	/**
	 * @param plano the plano to set
	 */
	public void setPlano(Byte plano) {
		this.plano = plano;
	}
	/**
	 * @return the descConv
	 */
	public String getDescConv() {
		return descConv;
	}
	/**
	 * @param descConv the descConv to set
	 */
	public void setDescConv(String descConv) {
		this.descConv = descConv;
	}
	/**
	 * @return the descPlan
	 */
	public String getDescPlan() {
		return descPlan;
	}
	/**
	 * @param descPlan the descPlan to set
	 */
	public void setDescPlan(String descPlan) {
		this.descPlan = descPlan;
	}
	/**
	 * @return the convenioPlano
	 */
	public String getConvenioPlano() {
		return getPlano().toString() + " - " + getDescConv() + " - " + getDescPlan();
	}
	/**
	 * @return the indSituacao
	 */
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	/**
	 * @param indSituacao the indSituacao to set
	 */
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	/**
	 * @return the planoIndSituacao
	 */
	public DominioSituacao getPlanoIndSituacao() {
		return planoIndSituacao;
	}
	/**
	 * @param planoIndSituacao the planoIndSituacao to set
	 */
	public void setPlanoIndSituacao(DominioSituacao planoIndSituacao) {
		this.planoIndSituacao = planoIndSituacao;
	}

}
