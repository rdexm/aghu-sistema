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
@Table(name = "AFA_PREP_REAPROVEITADOS", schema = "AGH")
public class AfaPrepReaproveitado extends BaseEntityId<AfaPrepReaproveitadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6598515575182862445L;
	private AfaPrepReaproveitadoId id;
	private Integer version;
	private RapServidores rapServidoresByAfaPvtSerFk2;
	private RapServidores rapServidoresByAfaPvtSerFk1;
	private AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk1;
	private AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk2;
	private Date criadoEm;

	public AfaPrepReaproveitado() {
	}

	public AfaPrepReaproveitado(AfaPrepReaproveitadoId id, RapServidores rapServidoresByAfaPvtSerFk1,
			AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk1, AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk2, Date criadoEm) {
		this.id = id;
		this.rapServidoresByAfaPvtSerFk1 = rapServidoresByAfaPvtSerFk1;
		this.afaItemPreparoMdtosByAfaPvtItoFk1 = afaItemPreparoMdtosByAfaPvtItoFk1;
		this.afaItemPreparoMdtosByAfaPvtItoFk2 = afaItemPreparoMdtosByAfaPvtItoFk2;
		this.criadoEm = criadoEm;
	}

	public AfaPrepReaproveitado(AfaPrepReaproveitadoId id, RapServidores rapServidoresByAfaPvtSerFk2,
			RapServidores rapServidoresByAfaPvtSerFk1, AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk1,
			AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk2, Date criadoEm) {
		this.id = id;
		this.rapServidoresByAfaPvtSerFk2 = rapServidoresByAfaPvtSerFk2;
		this.rapServidoresByAfaPvtSerFk1 = rapServidoresByAfaPvtSerFk1;
		this.afaItemPreparoMdtosByAfaPvtItoFk1 = afaItemPreparoMdtosByAfaPvtItoFk1;
		this.afaItemPreparoMdtosByAfaPvtItoFk2 = afaItemPreparoMdtosByAfaPvtItoFk2;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "itoPtoSeq", column = @Column(name = "ITO_PTO_SEQ", nullable = false)),
			@AttributeOverride(name = "itoSeqp", column = @Column(name = "ITO_SEQP", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AfaPrepReaproveitadoId getId() {
		return this.id;
	}

	public void setId(AfaPrepReaproveitadoId id) {
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
	public RapServidores getRapServidoresByAfaPvtSerFk2() {
		return this.rapServidoresByAfaPvtSerFk2;
	}

	public void setRapServidoresByAfaPvtSerFk2(RapServidores rapServidoresByAfaPvtSerFk2) {
		this.rapServidoresByAfaPvtSerFk2 = rapServidoresByAfaPvtSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByAfaPvtSerFk1() {
		return this.rapServidoresByAfaPvtSerFk1;
	}

	public void setRapServidoresByAfaPvtSerFk1(RapServidores rapServidoresByAfaPvtSerFk1) {
		this.rapServidoresByAfaPvtSerFk1 = rapServidoresByAfaPvtSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ITO_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "ITO_PTO_SEQ", referencedColumnName = "PTO_SEQ", nullable = false, insertable = false, updatable = false) })
	public AfaItemPreparoMdto getAfaItemPreparoMdtosByAfaPvtItoFk1() {
		return this.afaItemPreparoMdtosByAfaPvtItoFk1;
	}

	public void setAfaItemPreparoMdtosByAfaPvtItoFk1(AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk1) {
		this.afaItemPreparoMdtosByAfaPvtItoFk1 = afaItemPreparoMdtosByAfaPvtItoFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ITO_SEQP_REAPROVEITA", referencedColumnName = "SEQP", nullable = false),
			@JoinColumn(name = "ITO_PTO_SEQ_REAPROVEITA", referencedColumnName = "PTO_SEQ", nullable = false) })
	public AfaItemPreparoMdto getAfaItemPreparoMdtosByAfaPvtItoFk2() {
		return this.afaItemPreparoMdtosByAfaPvtItoFk2;
	}

	public void setAfaItemPreparoMdtosByAfaPvtItoFk2(AfaItemPreparoMdto afaItemPreparoMdtosByAfaPvtItoFk2) {
		this.afaItemPreparoMdtosByAfaPvtItoFk2 = afaItemPreparoMdtosByAfaPvtItoFk2;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_AFA_PVT_SER_FK2("rapServidoresByAfaPvtSerFk2"),
		RAP_SERVIDORES_BY_AFA_PVT_SER_FK1("rapServidoresByAfaPvtSerFk1"),
		AFA_ITEM_PREPARO_MDTOS_BY_AFA_PVT_ITO_FK1("afaItemPreparoMdtosByAfaPvtItoFk1"),
		AFA_ITEM_PREPARO_MDTOS_BY_AFA_PVT_ITO_FK2("afaItemPreparoMdtosByAfaPvtItoFk2"),
		CRIADO_EM("criadoEm");

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
		if (!(obj instanceof AfaPrepReaproveitado)) {
			return false;
		}
		AfaPrepReaproveitado other = (AfaPrepReaproveitado) obj;
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
