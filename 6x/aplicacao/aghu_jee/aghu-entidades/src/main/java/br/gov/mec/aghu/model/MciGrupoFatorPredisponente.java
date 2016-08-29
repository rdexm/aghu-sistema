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
@Table(name = "MCI_GRUPO_FATOR_PREDISPONENTES", schema = "AGH")
public class MciGrupoFatorPredisponente extends BaseEntityId<MciGrupoFatorPredisponenteId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4012743083559859396L;
	private MciGrupoFatorPredisponenteId id;
	private Integer version;
	private MciFatorPredisponentes mciFatorPredisponentes;
	private RapServidores rapServidoresByMciGfpSerFk1;
	private MciTipGrpFatPredisponente mciTipGrpFatPredisponente;
	private RapServidores rapServidoresByMciGfpSerFk2;
	private String indSituacao;
	private Date criadoEm;
	private Date alteradoEm;

	public MciGrupoFatorPredisponente() {
	}

	public MciGrupoFatorPredisponente(MciGrupoFatorPredisponenteId id, MciFatorPredisponentes mciFatorPredisponentes,
			RapServidores rapServidoresByMciGfpSerFk1, MciTipGrpFatPredisponente mciTipGrpFatPredisponente, String indSituacao,
			Date criadoEm) {
		this.id = id;
		this.mciFatorPredisponentes = mciFatorPredisponentes;
		this.rapServidoresByMciGfpSerFk1 = rapServidoresByMciGfpSerFk1;
		this.mciTipGrpFatPredisponente = mciTipGrpFatPredisponente;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MciGrupoFatorPredisponente(MciGrupoFatorPredisponenteId id, MciFatorPredisponentes mciFatorPredisponentes,
			RapServidores rapServidoresByMciGfpSerFk1, MciTipGrpFatPredisponente mciTipGrpFatPredisponente,
			RapServidores rapServidoresByMciGfpSerFk2, String indSituacao, Date criadoEm, Date alteradoEm) {
		this.id = id;
		this.mciFatorPredisponentes = mciFatorPredisponentes;
		this.rapServidoresByMciGfpSerFk1 = rapServidoresByMciGfpSerFk1;
		this.mciTipGrpFatPredisponente = mciTipGrpFatPredisponente;
		this.rapServidoresByMciGfpSerFk2 = rapServidoresByMciGfpSerFk2;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "tgfSeq", column = @Column(name = "TGF_SEQ", nullable = false)),
			@AttributeOverride(name = "fpdSeq", column = @Column(name = "FPD_SEQ", nullable = false)) })
	public MciGrupoFatorPredisponenteId getId() {
		return this.id;
	}

	public void setId(MciGrupoFatorPredisponenteId id) {
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
	@JoinColumn(name = "FPD_SEQ", nullable = false, insertable = false, updatable = false)
	public MciFatorPredisponentes getMciFatorPredisponentes() {
		return this.mciFatorPredisponentes;
	}

	public void setMciFatorPredisponentes(MciFatorPredisponentes mciFatorPredisponentes) {
		this.mciFatorPredisponentes = mciFatorPredisponentes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByMciGfpSerFk1() {
		return this.rapServidoresByMciGfpSerFk1;
	}

	public void setRapServidoresByMciGfpSerFk1(RapServidores rapServidoresByMciGfpSerFk1) {
		this.rapServidoresByMciGfpSerFk1 = rapServidoresByMciGfpSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TGF_SEQ", nullable = false, insertable = false, updatable = false)
	public MciTipGrpFatPredisponente getMciTipGrpFatPredisponente() {
		return this.mciTipGrpFatPredisponente;
	}

	public void setMciTipGrpFatPredisponente(MciTipGrpFatPredisponente mciTipGrpFatPredisponente) {
		this.mciTipGrpFatPredisponente = mciTipGrpFatPredisponente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMciGfpSerFk2() {
		return this.rapServidoresByMciGfpSerFk2;
	}

	public void setRapServidoresByMciGfpSerFk2(RapServidores rapServidoresByMciGfpSerFk2) {
		this.rapServidoresByMciGfpSerFk2 = rapServidoresByMciGfpSerFk2;
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
		MCI_FATOR_PREDISPONENTES("mciFatorPredisponentes"),
		RAP_SERVIDORES_BY_MCI_GFP_SER_FK1("rapServidoresByMciGfpSerFk1"),
		MCI_TIP_GRP_FAT_PREDISPONENTES("mciTipGrpFatPredisponente"),
		RAP_SERVIDORES_BY_MCI_GFP_SER_FK2("rapServidoresByMciGfpSerFk2"),
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
		if (!(obj instanceof MciGrupoFatorPredisponente)) {
			return false;
		}
		MciGrupoFatorPredisponente other = (MciGrupoFatorPredisponente) obj;
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
