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
@Table(name = "RAP_PPP_RESPS_LEGAL", schema = "AGH")
public class RapPppRespLegal extends BaseEntityId<RapPppRespLegalId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1502557954887788945L;
	private RapPppRespLegalId id;
	private Integer version;
	private RapServidores rapServidoresByRapRleSerFk3;
	private RapServidores rapServidoresByRapRleSerFk1;
	private RapServidores rapServidoresByRapRleSerFk2;
	private Date dtFim;
	private Date criadoEm;
	private Date alteradoEm;

	public RapPppRespLegal() {
	}

	public RapPppRespLegal(RapPppRespLegalId id, RapServidores rapServidoresByRapRleSerFk1,
			RapServidores rapServidoresByRapRleSerFk2, Date criadoEm) {
		this.id = id;
		this.rapServidoresByRapRleSerFk1 = rapServidoresByRapRleSerFk1;
		this.rapServidoresByRapRleSerFk2 = rapServidoresByRapRleSerFk2;
		this.criadoEm = criadoEm;
	}

	public RapPppRespLegal(RapPppRespLegalId id, RapServidores rapServidoresByRapRleSerFk3,
			RapServidores rapServidoresByRapRleSerFk1, RapServidores rapServidoresByRapRleSerFk2, Date dtFim, Date criadoEm,
			Date alteradoEm) {
		this.id = id;
		this.rapServidoresByRapRleSerFk3 = rapServidoresByRapRleSerFk3;
		this.rapServidoresByRapRleSerFk1 = rapServidoresByRapRleSerFk1;
		this.rapServidoresByRapRleSerFk2 = rapServidoresByRapRleSerFk2;
		this.dtFim = dtFim;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false)),
			@AttributeOverride(name = "dtInicio", column = @Column(name = "DT_INICIO", nullable = false, length = 29)) })
	public RapPppRespLegalId getId() {
		return this.id;
	}

	public void setId(RapPppRespLegalId id) {
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
	public RapServidores getRapServidoresByRapRleSerFk3() {
		return this.rapServidoresByRapRleSerFk3;
	}

	public void setRapServidoresByRapRleSerFk3(RapServidores rapServidoresByRapRleSerFk3) {
		this.rapServidoresByRapRleSerFk3 = rapServidoresByRapRleSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false, insertable = false, updatable = false) })
	public RapServidores getRapServidoresByRapRleSerFk1() {
		return this.rapServidoresByRapRleSerFk1;
	}

	public void setRapServidoresByRapRleSerFk1(RapServidores rapServidoresByRapRleSerFk1) {
		this.rapServidoresByRapRleSerFk1 = rapServidoresByRapRleSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByRapRleSerFk2() {
		return this.rapServidoresByRapRleSerFk2;
	}

	public void setRapServidoresByRapRleSerFk2(RapServidores rapServidoresByRapRleSerFk2) {
		this.rapServidoresByRapRleSerFk2 = rapServidoresByRapRleSerFk2;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
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

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_RAP_RLE_SER_FK3("rapServidoresByRapRleSerFk3"),
		RAP_SERVIDORES_BY_RAP_RLE_SER_FK1("rapServidoresByRapRleSerFk1"),
		RAP_SERVIDORES_BY_RAP_RLE_SER_FK2("rapServidoresByRapRleSerFk2"),
		DT_FIM("dtFim"),
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
		if (!(obj instanceof RapPppRespLegal)) {
			return false;
		}
		RapPppRespLegal other = (RapPppRespLegal) obj;
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
