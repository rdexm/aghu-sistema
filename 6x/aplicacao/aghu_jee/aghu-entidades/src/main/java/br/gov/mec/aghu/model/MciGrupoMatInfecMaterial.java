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
import org.hibernate.validator.constraints.Length;

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
@Table(name = "MCI_GRUPO_MAT_INFEC_MATERIAIS", schema = "AGH")
public class MciGrupoMatInfecMaterial extends BaseEntityId<MciGrupoMatInfecMaterialId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5265567617476594488L;
	private MciGrupoMatInfecMaterialId id;
	private Integer version;
	private MciMaterialInfectante mciMaterialInfectante;
	private MciGrupoMatInfectante mciGrupoMatInfectante;
	private RapServidores rapServidoresByMciGmmSerFk2;
	private RapServidores rapServidoresByMciGmmSerFk1;
	private String indSituacao;
	private Date criadoEm;
	private Date alteradoEm;

	public MciGrupoMatInfecMaterial() {
	}

	public MciGrupoMatInfecMaterial(MciGrupoMatInfecMaterialId id, MciMaterialInfectante mciMaterialInfectante,
			MciGrupoMatInfectante mciGrupoMatInfectante, RapServidores rapServidoresByMciGmmSerFk2, String indSituacao, Date criadoEm) {
		this.id = id;
		this.mciMaterialInfectante = mciMaterialInfectante;
		this.mciGrupoMatInfectante = mciGrupoMatInfectante;
		this.rapServidoresByMciGmmSerFk2 = rapServidoresByMciGmmSerFk2;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MciGrupoMatInfecMaterial(MciGrupoMatInfecMaterialId id, MciMaterialInfectante mciMaterialInfectante,
			MciGrupoMatInfectante mciGrupoMatInfectante, RapServidores rapServidoresByMciGmmSerFk2,
			RapServidores rapServidoresByMciGmmSerFk1, String indSituacao, Date criadoEm, Date alteradoEm) {
		this.id = id;
		this.mciMaterialInfectante = mciMaterialInfectante;
		this.mciGrupoMatInfectante = mciGrupoMatInfectante;
		this.rapServidoresByMciGmmSerFk2 = rapServidoresByMciGmmSerFk2;
		this.rapServidoresByMciGmmSerFk1 = rapServidoresByMciGmmSerFk1;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "gmiSeq", column = @Column(name = "GMI_SEQ", nullable = false)),
			@AttributeOverride(name = "maiSeq", column = @Column(name = "MAI_SEQ", nullable = false)) })
	public MciGrupoMatInfecMaterialId getId() {
		return this.id;
	}

	public void setId(MciGrupoMatInfecMaterialId id) {
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
	@JoinColumn(name = "MAI_SEQ", nullable = false, insertable = false, updatable = false)
	public MciMaterialInfectante getMciMaterialInfectante() {
		return this.mciMaterialInfectante;
	}

	public void setMciMaterialInfectante(MciMaterialInfectante mciMaterialInfectante) {
		this.mciMaterialInfectante = mciMaterialInfectante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GMI_SEQ", nullable = false, insertable = false, updatable = false)
	public MciGrupoMatInfectante getMciGrupoMatInfectante() {
		return this.mciGrupoMatInfectante;
	}

	public void setMciGrupoMatInfectante(MciGrupoMatInfectante mciGrupoMatInfectante) {
		this.mciGrupoMatInfectante = mciGrupoMatInfectante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByMciGmmSerFk2() {
		return this.rapServidoresByMciGmmSerFk2;
	}

	public void setRapServidoresByMciGmmSerFk2(RapServidores rapServidoresByMciGmmSerFk2) {
		this.rapServidoresByMciGmmSerFk2 = rapServidoresByMciGmmSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMciGmmSerFk1() {
		return this.rapServidoresByMciGmmSerFk1;
	}

	public void setRapServidoresByMciGmmSerFk1(RapServidores rapServidoresByMciGmmSerFk1) {
		this.rapServidoresByMciGmmSerFk1 = rapServidoresByMciGmmSerFk1;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
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
		MCI_MATERIAL_INFECTANTES("mciMaterialInfectante"),
		MCI_GRUPO_MAT_INFECTANTES("mciGrupoMatInfectante"),
		RAP_SERVIDORES_BY_MCI_GMM_SER_FK2("rapServidoresByMciGmmSerFk2"),
		RAP_SERVIDORES_BY_MCI_GMM_SER_FK1("rapServidoresByMciGmmSerFk1"),
		IND_SITUACAO("indSituacao"),
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
		if (!(obj instanceof MciGrupoMatInfecMaterial)) {
			return false;
		}
		MciGrupoMatInfecMaterial other = (MciGrupoMatInfecMaterial) obj;
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
