package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "RAR_SELECAO_CANDIDATOS", schema = "AGH")
public class RarSelecaoCandidato extends BaseEntityId<RarSelecaoCandidatoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3362906197505510759L;
	private RarSelecaoCandidatoId id;
	private Integer version;
	private RapServidores rapServidoresByRarScaSerFk2;
	private RarCandidatoPrograma rarCandidatoPrograma;
	private RapServidores rapServidoresByRarScaSerFk1;
	private RarBancaExaminadora rarBancaExaminadora;
	private Double escore;
	private Double nota;
	private Date criadoEm;
	private Date alteradoEm;

	public RarSelecaoCandidato() {
	}

	public RarSelecaoCandidato(RarSelecaoCandidatoId id, RarCandidatoPrograma rarCandidatoPrograma) {
		this.id = id;
		this.rarCandidatoPrograma = rarCandidatoPrograma;
	}

	public RarSelecaoCandidato(RarSelecaoCandidatoId id, RapServidores rapServidoresByRarScaSerFk2,
			RarCandidatoPrograma rarCandidatoPrograma, RapServidores rapServidoresByRarScaSerFk1,
			RarBancaExaminadora rarBancaExaminadora, Double escore, Double nota, Date criadoEm, Date alteradoEm) {
		this.id = id;
		this.rapServidoresByRarScaSerFk2 = rapServidoresByRarScaSerFk2;
		this.rarCandidatoPrograma = rarCandidatoPrograma;
		this.rapServidoresByRarScaSerFk1 = rapServidoresByRarScaSerFk1;
		this.rarBancaExaminadora = rarBancaExaminadora;
		this.escore = escore;
		this.nota = nota;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "cpmPgaSeq", column = @Column(name = "CPM_PGA_SEQ", nullable = false)),
			@AttributeOverride(name = "cpmCndSeq", column = @Column(name = "CPM_CND_SEQ", nullable = false)),
			@AttributeOverride(name = "data", column = @Column(name = "DATA", nullable = false, length = 29)),
			@AttributeOverride(name = "tipo", column = @Column(name = "TIPO", nullable = false, length = 2)) })
	public RarSelecaoCandidatoId getId() {
		return this.id;
	}

	public void setId(RarSelecaoCandidatoId id) {
		this.id = id;
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByRarScaSerFk2() {
		return this.rapServidoresByRarScaSerFk2;
	}

	public void setRapServidoresByRarScaSerFk2(RapServidores rapServidoresByRarScaSerFk2) {
		this.rapServidoresByRarScaSerFk2 = rapServidoresByRarScaSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "CPM_PGA_SEQ", referencedColumnName = "PGA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CPM_CND_SEQ", referencedColumnName = "CND_SEQ", nullable = false, insertable = false, updatable = false) })
	public RarCandidatoPrograma getRarCandidatoPrograma() {
		return this.rarCandidatoPrograma;
	}

	public void setRarCandidatoPrograma(RarCandidatoPrograma rarCandidatoPrograma) {
		this.rarCandidatoPrograma = rarCandidatoPrograma;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByRarScaSerFk1() {
		return this.rapServidoresByRarScaSerFk1;
	}

	public void setRapServidoresByRarScaSerFk1(RapServidores rapServidoresByRarScaSerFk1) {
		this.rapServidoresByRarScaSerFk1 = rapServidoresByRarScaSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "BEX_PGA_SEQ", referencedColumnName = "PGA_SEQ"),
			@JoinColumn(name = "BEX_SEQ", referencedColumnName = "SEQ") })
	public RarBancaExaminadora getRarBancaExaminadora() {
		return this.rarBancaExaminadora;
	}

	public void setRarBancaExaminadora(RarBancaExaminadora rarBancaExaminadora) {
		this.rarBancaExaminadora = rarBancaExaminadora;
	}

	@Column(name = "ESCORE", precision = 17, scale = 17)
	public Double getEscore() {
		return this.escore;
	}

	public void setEscore(Double escore) {
		this.escore = escore;
	}

	@Column(name = "NOTA", precision = 17, scale = 17)
	public Double getNota() {
		return this.nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_RAR_SCA_SER_FK2("rapServidoresByRarScaSerFk2"),
		RAR_CANDIDATOS_PROGRAMA("rarCandidatoPrograma"),
		RAP_SERVIDORES_BY_RAR_SCA_SER_FK1("rapServidoresByRarScaSerFk1"),
		RAR_BANCAS_EXAMINADORAS("rarBancaExaminadora"),
		ESCORE("escore"),
		NOTA("nota"),
		CRIADO_EM("criadoEm"),
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
		if (!(obj instanceof RarSelecaoCandidato)) {
			return false;
		}
		RarSelecaoCandidato other = (RarSelecaoCandidato) obj;
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
