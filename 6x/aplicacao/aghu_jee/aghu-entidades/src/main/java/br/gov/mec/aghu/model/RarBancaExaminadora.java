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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "RAR_BANCAS_EXAMINADORAS", schema = "AGH")
public class RarBancaExaminadora extends BaseEntityId<RarBancaExaminadoraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1523483619927412234L;
	private RarBancaExaminadoraId id;
	private Integer version;
	private RarPrograma rarPrograma;
	private String descricao;
	private Date dtInicio;
	private Date dtFim;
	private Set<RarComponenteBanca> rarComponenteBancas = new HashSet<RarComponenteBanca>(0);
	private Set<RarSelecaoCandidato> rarSelecaoCandidatoes = new HashSet<RarSelecaoCandidato>(0);

	public RarBancaExaminadora() {
	}

	public RarBancaExaminadora(RarBancaExaminadoraId id, RarPrograma rarPrograma, String descricao, Date dtInicio) {
		this.id = id;
		this.rarPrograma = rarPrograma;
		this.descricao = descricao;
		this.dtInicio = dtInicio;
	}

	public RarBancaExaminadora(RarBancaExaminadoraId id, RarPrograma rarPrograma, String descricao, Date dtInicio, Date dtFim,
			Set<RarComponenteBanca> rarComponenteBancas, Set<RarSelecaoCandidato> rarSelecaoCandidatoes) {
		this.id = id;
		this.rarPrograma = rarPrograma;
		this.descricao = descricao;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
		this.rarComponenteBancas = rarComponenteBancas;
		this.rarSelecaoCandidatoes = rarSelecaoCandidatoes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pgaSeq", column = @Column(name = "PGA_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public RarBancaExaminadoraId getId() {
		return this.id;
	}

	public void setId(RarBancaExaminadoraId id) {
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
	@JoinColumn(name = "PGA_SEQ", nullable = false, insertable = false, updatable = false)
	public RarPrograma getRarPrograma() {
		return this.rarPrograma;
	}

	public void setRarPrograma(RarPrograma rarPrograma) {
		this.rarPrograma = rarPrograma;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO", nullable = false, length = 29)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarBancaExaminadora")
	public Set<RarComponenteBanca> getRarComponenteBancas() {
		return this.rarComponenteBancas;
	}

	public void setRarComponenteBancas(Set<RarComponenteBanca> rarComponenteBancas) {
		this.rarComponenteBancas = rarComponenteBancas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarBancaExaminadora")
	public Set<RarSelecaoCandidato> getRarSelecaoCandidatoes() {
		return this.rarSelecaoCandidatoes;
	}

	public void setRarSelecaoCandidatoes(Set<RarSelecaoCandidato> rarSelecaoCandidatoes) {
		this.rarSelecaoCandidatoes = rarSelecaoCandidatoes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAR_PROGRAMAS("rarPrograma"),
		DESCRICAO("descricao"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		RAR_COMPONENTE_BANCAS("rarComponenteBancas"),
		RAR_SELECAO_CANDIDATOES("rarSelecaoCandidatoes");

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
		if (!(obj instanceof RarBancaExaminadora)) {
			return false;
		}
		RarBancaExaminadora other = (RarBancaExaminadora) obj;
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
