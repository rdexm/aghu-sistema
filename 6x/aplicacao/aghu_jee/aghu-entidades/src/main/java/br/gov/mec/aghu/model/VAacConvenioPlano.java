package br.gov.mec.aghu.model;

// Generated 05/04/2011 11:18:40 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Convênios de ambulatório.
 */
@Entity
@Table(name = "V_AAC_CONVENIO_PLANO", schema = "AGH")
@Immutable
public class VAacConvenioPlano extends BaseEntityId<VAacConvenioPlanoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5331032322776441593L;
	private VAacConvenioPlanoId id;
	private String descConv;
	private String descPlan;
	private String convenioPlano;
	private String indPermissaoInternacao;
	private String indVerfEscalaProfInt;
	private String indExigeNumMatr;
	private String indSelAutomProf;
	private String indSituacao;
	private String cspIndSituacao;
	private String indRestringeProf;
	private String grupoConvenio;

	public VAacConvenioPlano() {
	}

	public VAacConvenioPlano(VAacConvenioPlanoId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cnvCodigo", column = @Column(name = "CNV_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "plano", column = @Column(name = "PLANO", nullable = false, precision = 2, scale = 0)) })
	public VAacConvenioPlanoId getId() {
		return this.id;
	}

	public void setId(VAacConvenioPlanoId id) {
		this.id = id;
	}

	@Column(name = "DESC_CONV", nullable = false, length = 60)
	public String getDescConv() {
		return this.descConv;
	}

	public void setDescConv(String descConv) {
		this.descConv = descConv;
	}

	@Column(name = "DESC_PLAN", nullable = false, length = 60)
	public String getDescPlan() {
		return this.descPlan;
	}

	public void setDescPlan(String descPlan) {
		this.descPlan = descPlan;
	}

	@Column(name = "CONVENIO_PLANO", length = 60)
	public String getConvenioPlano() {
		return this.convenioPlano;
	}

	public void setConvenioPlano(String convenioPlano) {
		this.convenioPlano = convenioPlano;
	}

	@Column(name = "IND_PERMISSAO_INTERNACAO", nullable = false, length = 1)
	public String getIndPermissaoInternacao() {
		return this.indPermissaoInternacao;
	}

	public void setIndPermissaoInternacao(String indPermissaoInternacao) {
		this.indPermissaoInternacao = indPermissaoInternacao;
	}

	@Column(name = "IND_VERF_ESCALA_PROF_INT", nullable = false, length = 1)
	public String getIndVerfEscalaProfInt() {
		return this.indVerfEscalaProfInt;
	}

	public void setIndVerfEscalaProfInt(String indVerfEscalaProfInt) {
		this.indVerfEscalaProfInt = indVerfEscalaProfInt;
	}

	@Column(name = "IND_EXIGE_NUM_MATR", nullable = false, length = 1)
	public String getIndExigeNumMatr() {
		return this.indExigeNumMatr;
	}

	public void setIndExigeNumMatr(String indExigeNumMatr) {
		this.indExigeNumMatr = indExigeNumMatr;
	}

	@Column(name = "IND_SEL_AUTOM_PROF", nullable = false, length = 1)
	public String getIndSelAutomProf() {
		return this.indSelAutomProf;
	}

	public void setIndSelAutomProf(String indSelAutomProf) {
		this.indSelAutomProf = indSelAutomProf;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "CSP_IND_SITUACAO", length = 1)
	public String getCspIndSituacao() {
		return this.cspIndSituacao;
	}

	public void setCspIndSituacao(String cspIndSituacao) {
		this.cspIndSituacao = cspIndSituacao;
	}

	@Column(name = "IND_RESTRINGE_PROF", nullable = false, length = 1)
	public String getIndRestringeProf() {
		return this.indRestringeProf;
	}

	public void setIndRestringeProf(String indRestringeProf) {
		this.indRestringeProf = indRestringeProf;
	}

	@Column(name = "GRUPO_CONVENIO", nullable = false, length = 1)
	public String getGrupoConvenio() {
		return this.grupoConvenio;
	}

	public void setGrupoConvenio(String grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}


	public enum Fields {
		CNV_CODIGO("id.cnvCodigo"), 
		IND_SITUACAO("indSituacao"), 
		PLANO("id.plano"), 
		DESC_CONV("descConv"), 
		CONVENIO_PLANO("convenioPlano"), 
		CSP_IND_SITUACAO("cspIndSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VAacConvenioPlano)) {
			return false;
		}
		VAacConvenioPlano other = (VAacConvenioPlano) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
