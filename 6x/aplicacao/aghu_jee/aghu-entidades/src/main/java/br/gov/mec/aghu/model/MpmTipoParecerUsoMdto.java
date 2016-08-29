package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MpmTipoParecerUsoMdto generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mpmTpmSq1", sequenceName="AGH.MPM_TPM_SQ1", allocationSize = 1)
@Table(name = "MPM_TIPO_PARECER_USO_MDTOS", schema = "AGH")
public class MpmTipoParecerUsoMdto extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 607829254657528770L;
	private Short seq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private String indSituacao;
	private Date criadoEm;
	private String indMostraParecerAutomatico;
	private String indDigitaObservacao;
	private String indExigeAltDuracTratamento;
	private String indZeraDuracAprovada;
	private String indAvalPorPrescricao;
	private String indCategTipoParecer;
	private Set<MpmParecerUsoMdto> mpmParecerUsoMdtoses = new HashSet<MpmParecerUsoMdto>(
			0);

	public MpmTipoParecerUsoMdto() {
	}

	public MpmTipoParecerUsoMdto(Short seq, Integer serMatricula,
			Short serVinCodigo, String descricao, String indSituacao,
			Date criadoEm, String indMostraParecerAutomatico,
			String indDigitaObservacao, String indExigeAltDuracTratamento,
			String indZeraDuracAprovada, String indAvalPorPrescricao,
			String indCategTipoParecer) {
		this.seq = seq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.indMostraParecerAutomatico = indMostraParecerAutomatico;
		this.indDigitaObservacao = indDigitaObservacao;
		this.indExigeAltDuracTratamento = indExigeAltDuracTratamento;
		this.indZeraDuracAprovada = indZeraDuracAprovada;
		this.indAvalPorPrescricao = indAvalPorPrescricao;
		this.indCategTipoParecer = indCategTipoParecer;
	}

	public MpmTipoParecerUsoMdto(Short seq, Integer serMatricula,
			Short serVinCodigo, String descricao, String indSituacao,
			Date criadoEm, String indMostraParecerAutomatico,
			String indDigitaObservacao, String indExigeAltDuracTratamento,
			String indZeraDuracAprovada, String indAvalPorPrescricao,
			String indCategTipoParecer,
			Set<MpmParecerUsoMdto> mpmParecerUsoMdtoses) {
		this.seq = seq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.indMostraParecerAutomatico = indMostraParecerAutomatico;
		this.indDigitaObservacao = indDigitaObservacao;
		this.indExigeAltDuracTratamento = indExigeAltDuracTratamento;
		this.indZeraDuracAprovada = indZeraDuracAprovada;
		this.indAvalPorPrescricao = indAvalPorPrescricao;
		this.indCategTipoParecer = indCategTipoParecer;
		this.mpmParecerUsoMdtoses = mpmParecerUsoMdtoses;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmTpmSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_MOSTRA_PARECER_AUTOMATICO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndMostraParecerAutomatico() {
		return this.indMostraParecerAutomatico;
	}

	public void setIndMostraParecerAutomatico(String indMostraParecerAutomatico) {
		this.indMostraParecerAutomatico = indMostraParecerAutomatico;
	}

	@Column(name = "IND_DIGITA_OBSERVACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndDigitaObservacao() {
		return this.indDigitaObservacao;
	}

	public void setIndDigitaObservacao(String indDigitaObservacao) {
		this.indDigitaObservacao = indDigitaObservacao;
	}

	@Column(name = "IND_EXIGE_ALT_DURAC_TRATAMENTO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndExigeAltDuracTratamento() {
		return this.indExigeAltDuracTratamento;
	}

	public void setIndExigeAltDuracTratamento(String indExigeAltDuracTratamento) {
		this.indExigeAltDuracTratamento = indExigeAltDuracTratamento;
	}

	@Column(name = "IND_ZERA_DURAC_APROVADA", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndZeraDuracAprovada() {
		return this.indZeraDuracAprovada;
	}

	public void setIndZeraDuracAprovada(String indZeraDuracAprovada) {
		this.indZeraDuracAprovada = indZeraDuracAprovada;
	}

	@Column(name = "IND_AVAL_POR_PRESCRICAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndAvalPorPrescricao() {
		return this.indAvalPorPrescricao;
	}

	public void setIndAvalPorPrescricao(String indAvalPorPrescricao) {
		this.indAvalPorPrescricao = indAvalPorPrescricao;
	}

	@Column(name = "IND_CATEG_TIPO_PARECER", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndCategTipoParecer() {
		return this.indCategTipoParecer;
	}

	public void setIndCategTipoParecer(String indCategTipoParecer) {
		this.indCategTipoParecer = indCategTipoParecer;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpmTipoParecerUsoMdtos")
	public Set<MpmParecerUsoMdto> getMpmParecerUsoMdtoses() {
		return this.mpmParecerUsoMdtoses;
	}

	public void setMpmParecerUsoMdtoses(
			Set<MpmParecerUsoMdto> mpmParecerUsoMdtoses) {
		this.mpmParecerUsoMdtoses = mpmParecerUsoMdtoses;
	}

	public enum Fields {

		SEQ("seq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		IND_MOSTRA_PARECER_AUTOMATICO("indMostraParecerAutomatico"),
		IND_DIGITA_OBSERVACAO("indDigitaObservacao"),
		IND_EXIGE_ALT_DURAC_TRATAMENTO("indExigeAltDuracTratamento"),
		IND_ZERA_DURAC_APROVADA("indZeraDuracAprovada"),
		IND_AVAL_POR_PRESCRICAO("indAvalPorPrescricao"),
		IND_CATEG_TIPO_PARECER("indCategTipoParecer"),
		MPM_PARECER_USO_MDTOSES("mpmParecerUsoMdtoses");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MpmTipoParecerUsoMdto)) {
			return false;
		}
		MpmTipoParecerUsoMdto other = (MpmTipoParecerUsoMdto) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}