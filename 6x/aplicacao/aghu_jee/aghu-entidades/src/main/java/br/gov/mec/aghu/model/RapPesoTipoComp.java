package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "RAP_PESOS_TIPO_COMP", schema = "AGH")
public class RapPesoTipoComp extends BaseEntityId<RapPesoTipoCompId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1422114521337669986L;
	private RapPesoTipoCompId id;
	private Integer version;
	private RapTipoAvaliacao rapTipoAvaliacao;
	private RapTipoCompetencia rapTipoCompetencia;
	private Short peso;

	public RapPesoTipoComp() {
	}

	public RapPesoTipoComp(RapPesoTipoCompId id, RapTipoAvaliacao rapTipoAvaliacao, RapTipoCompetencia rapTipoCompetencia,
			Short peso) {
		this.id = id;
		this.rapTipoAvaliacao = rapTipoAvaliacao;
		this.rapTipoCompetencia = rapTipoCompetencia;
		this.peso = peso;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "tavCodigo", column = @Column(name = "TAV_CODIGO", nullable = false)),
			@AttributeOverride(name = "tcmCodigo", column = @Column(name = "TCM_CODIGO", nullable = false, length = 2)) })
	public RapPesoTipoCompId getId() {
		return this.id;
	}

	public void setId(RapPesoTipoCompId id) {
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
	@JoinColumn(name = "TAV_CODIGO", nullable = false, insertable = false, updatable = false)
	public RapTipoAvaliacao getRapTipoAvaliacao() {
		return this.rapTipoAvaliacao;
	}

	public void setRapTipoAvaliacao(RapTipoAvaliacao rapTipoAvaliacao) {
		this.rapTipoAvaliacao = rapTipoAvaliacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TCM_CODIGO", nullable = false, insertable = false, updatable = false)
	public RapTipoCompetencia getRapTipoCompetencia() {
		return this.rapTipoCompetencia;
	}

	public void setRapTipoCompetencia(RapTipoCompetencia rapTipoCompetencia) {
		this.rapTipoCompetencia = rapTipoCompetencia;
	}

	@Column(name = "PESO", nullable = false)
	public Short getPeso() {
		return this.peso;
	}

	public void setPeso(Short peso) {
		this.peso = peso;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_TIPOS_AVALIACAO("rapTipoAvaliacao"),
		RAP_TIPOS_COMPETENCIA("rapTipoCompetencia"),
		PESO("peso");

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
		if (!(obj instanceof RapPesoTipoComp)) {
			return false;
		}
		RapPesoTipoComp other = (RapPesoTipoComp) obj;
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
