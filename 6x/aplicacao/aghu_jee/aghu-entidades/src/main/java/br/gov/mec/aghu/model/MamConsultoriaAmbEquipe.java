package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MamConsultoriaAmbEquipe generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mamCbeSq1", sequenceName="AGH.MAM_CBE_SQ1", allocationSize = 1)
@Table(name = "MAM_CONSULTORIA_AMB_EQUIPES", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = { "esp_seq",
		"eqp_seq" }))
public class MamConsultoriaAmbEquipe extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6598650405565763732L;
	private Integer seq;
	private Integer version;
	private AghEspecialidades aghEspecialidades;
	private RapServidores rapServidoresByMamCbeSerFk1;
	private RapServidores rapServidoresByMamCbeSerFk2;
	private AghEquipes aghEquipes;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Date alteradoEm;

	public MamConsultoriaAmbEquipe() {
	}

	public MamConsultoriaAmbEquipe(Integer seq, AghEspecialidades aghEspecialidades, RapServidores rapServidoresByMamCbeSerFk1,
			AghEquipes aghEquipes, Date criadoEm, DominioSituacao indSituacao) {
		this.seq = seq;
		this.aghEspecialidades = aghEspecialidades;
		this.rapServidoresByMamCbeSerFk1 = rapServidoresByMamCbeSerFk1;
		this.aghEquipes = aghEquipes;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MamConsultoriaAmbEquipe(Integer seq, AghEspecialidades aghEspecialidades, RapServidores rapServidoresByMamCbeSerFk1,
			RapServidores rapServidoresByMamCbeSerFk2, AghEquipes aghEquipes, Date criadoEm, DominioSituacao indSituacao, Date alteradoEm) {
		this.seq = seq;
		this.aghEspecialidades = aghEspecialidades;
		this.rapServidoresByMamCbeSerFk1 = rapServidoresByMamCbeSerFk1;
		this.rapServidoresByMamCbeSerFk2 = rapServidoresByMamCbeSerFk2;
		this.aghEquipes = aghEquipes;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.alteradoEm = alteradoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamCbeSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = false)
	public AghEspecialidades getAghEspecialidades() {
		return this.aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByMamCbeSerFk1() {
		return this.rapServidoresByMamCbeSerFk1;
	}

	public void setRapServidoresByMamCbeSerFk1(RapServidores rapServidoresByMamCbeSerFk1) {
		this.rapServidoresByMamCbeSerFk1 = rapServidoresByMamCbeSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMamCbeSerFk2() {
		return this.rapServidoresByMamCbeSerFk2;
	}

	public void setRapServidoresByMamCbeSerFk2(RapServidores rapServidoresByMamCbeSerFk2) {
		this.rapServidoresByMamCbeSerFk2 = rapServidoresByMamCbeSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQP_SEQ", nullable = false)
	public AghEquipes getAghEquipes() {
		return this.aghEquipes;
	}

	public void setAghEquipes(AghEquipes aghEquipes) {
		this.aghEquipes = aghEquipes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao  getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AGH_ESPECIALIDADES("aghEspecialidades"),
		ESP_SEQ("aghEspecialidades.seq"),
		RAP_SERVIDORES_BY_MAM_CBE_SER_FK1("rapServidoresByMamCbeSerFk1"),
		RAP_SERVIDORES_BY_MAM_CBE_SER_FK2("rapServidoresByMamCbeSerFk2"),
		AGH_EQUIPES("aghEquipes"),
		EQP_SEQ("aghEquipes.seq"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		ALTERADO_EM("alteradoEm");

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
		if (!(obj instanceof MamConsultoriaAmbEquipe)) {
			return false;
		}
		MamConsultoriaAmbEquipe other = (MamConsultoriaAmbEquipe) obj;
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
