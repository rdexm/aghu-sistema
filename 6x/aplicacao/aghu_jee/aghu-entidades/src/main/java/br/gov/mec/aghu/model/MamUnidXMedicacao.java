package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
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
@Table(name = "MAM_UNID_X_MEDICACOES", schema = "AGH")
public class MamUnidXMedicacao extends BaseEntityId<MamUnidXMedicacaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1431687588222160134L;
	private MamUnidXMedicacaoId id;
	private Integer version;
	private MamUnidAtendem mamUnidAtendem;
	private RapServidores rapServidores;
	private MamItemMedicacao mamItemMedicacao;
	private Boolean indObrigatorio;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private String micNome;
	private Set<MamMedicUnidGrav> mamMedicUnidGraves = new HashSet<MamMedicUnidGrav>(0);

	public MamUnidXMedicacao() {
	}

	public MamUnidXMedicacao(MamUnidXMedicacaoId id, MamUnidAtendem mamUnidAtendem, RapServidores rapServidores,
			MamItemMedicacao mamItemMedicacao, Boolean indObrigatorio, Date criadoEm, DominioSituacao indSituacao) {
		this.id = id;
		this.mamUnidAtendem = mamUnidAtendem;
		this.rapServidores = rapServidores;
		this.mamItemMedicacao = mamItemMedicacao;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MamUnidXMedicacao(MamUnidXMedicacaoId id, MamUnidAtendem mamUnidAtendem, RapServidores rapServidores,
			MamItemMedicacao mamItemMedicacao, Boolean indObrigatorio, Date criadoEm, DominioSituacao indSituacao, String micNome,
			Set<MamMedicUnidGrav> mamMedicUnidGraves) {
		this.id = id;
		this.mamUnidAtendem = mamUnidAtendem;
		this.rapServidores = rapServidores;
		this.mamItemMedicacao = mamItemMedicacao;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.micNome = micNome;
		this.mamMedicUnidGraves = mamMedicUnidGraves;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "mdmSeq", column = @Column(name = "MDM_SEQ", nullable = false)),
			@AttributeOverride(name = "uanUnfSeq", column = @Column(name = "UAN_UNF_SEQ", nullable = false)) })
	@NotNull
	public MamUnidXMedicacaoId getId() {
		return this.id;
	}

	public void setId(MamUnidXMedicacaoId id) {
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
	@JoinColumn(name = "UAN_UNF_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamUnidAtendem getMamUnidAtendem() {
		return this.mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
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
	@JoinColumn(name = "MDM_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamItemMedicacao getMamItemMedicacao() {
		return this.mamItemMedicacao;
	}

	public void setMamItemMedicacao(MamItemMedicacao mamItemMedicacao) {
		this.mamItemMedicacao = mamItemMedicacao;
	}

	@Column(name = "IND_OBRIGATORIO", nullable = false, length = 1)
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndObrigatorio() {
		return this.indObrigatorio;
	}

	public void setIndObrigatorio(Boolean indObrigatorio) {
		this.indObrigatorio = indObrigatorio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamUnidXMedicacao")
	public Set<MamMedicUnidGrav> getMamMedicUnidGraves() {
		return this.mamMedicUnidGraves;
	}

	public void setMamMedicUnidGraves(Set<MamMedicUnidGrav> mamMedicUnidGraves) {
		this.mamMedicUnidGraves = mamMedicUnidGraves;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MAM_UNID_ATENDEM("mamUnidAtendem"),
		UAN_UNF_SEQ("mamUnidAtendem.unfSeq"),
		RAP_SERVIDORES("rapServidores"),
		MAM_ITEM_MEDICACAO("mamItemMedicacao"),
		MDM_SEQ("mamItemMedicacao.seq"),
		IND_OBRIGATORIO("indObrigatorio"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MIC_NOME("micNome"),
		MAM_MEDIC_UNID_GRAVES("mamMedicUnidGraves");

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
		if (!(obj instanceof MamUnidXMedicacao)) {
			return false;
		}
		MamUnidXMedicacao other = (MamUnidXMedicacao) obj;
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
