package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@Table(name = "AEL_OCORRENCIAS_REDOME", schema = "AGH")
public class AelOcorrenciaRedome extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8597617324036463340L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private AelListaPacRedome aelListaPacRedome;
	private AelPlacaPacRedome aelPlacaPacRedome;
	private Date criadoEm;
	private Date alteradoEm;

	public AelOcorrenciaRedome() {
	}

	public AelOcorrenciaRedome(Short seq, RapServidores rapServidores, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
	}

	public AelOcorrenciaRedome(Short seq, RapServidores rapServidores, AelListaPacRedome aelListaPacRedome,
			AelPlacaPacRedome aelPlacaPacRedome, Date criadoEm, Date alteradoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.aelListaPacRedome = aelListaPacRedome;
		this.aelPlacaPacRedome = aelPlacaPacRedome;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LPR_SEQ")
	public AelListaPacRedome getAelListaPacRedome() {
		return this.aelListaPacRedome;
	}

	public void setAelListaPacRedome(AelListaPacRedome aelListaPacRedome) {
		this.aelListaPacRedome = aelListaPacRedome;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PC2_SEQ")
	public AelPlacaPacRedome getAelPlacaPacRedome() {
		return this.aelPlacaPacRedome;
	}

	public void setAelPlacaPacRedome(AelPlacaPacRedome aelPlacaPacRedome) {
		this.aelPlacaPacRedome = aelPlacaPacRedome;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
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

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		AEL_LISTA_PAC_REDOMES("aelListaPacRedome"),
		AEL_PLACA_PAC_REDOMES("aelPlacaPacRedome"),
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
		if (!(obj instanceof AelOcorrenciaRedome)) {
			return false;
		}
		AelOcorrenciaRedome other = (AelOcorrenciaRedome) obj;
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
