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
@Table(name = "MAM_CATEGORIA_FIGURAS", schema = "AGH")
public class MamCategoriaFigura extends BaseEntityId<MamCategoriaFiguraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4678161621228293320L;
	private MamCategoriaFiguraId id;
	private Integer version;
	private MamCategoria mamCategoria;
	private RapServidores rapServidores;
	private MamFigura mamFigura;
	private String indSituacao;
	private Date criadoEm;

	public MamCategoriaFigura() {
	}

	public MamCategoriaFigura(MamCategoriaFiguraId id, MamCategoria mamCategoria, RapServidores rapServidores,
			MamFigura mamFigura, String indSituacao, Date criadoEm) {
		this.id = id;
		this.mamCategoria = mamCategoria;
		this.rapServidores = rapServidores;
		this.mamFigura = mamFigura;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ctgSeq", column = @Column(name = "CTG_SEQ", nullable = false)),
			@AttributeOverride(name = "figSeq", column = @Column(name = "FIG_SEQ", nullable = false)) })
	public MamCategoriaFiguraId getId() {
		return this.id;
	}

	public void setId(MamCategoriaFiguraId id) {
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
	@JoinColumn(name = "CTG_SEQ", nullable = false, insertable = false, updatable = false)
	public MamCategoria getMamCategoria() {
		return this.mamCategoria;
	}

	public void setMamCategoria(MamCategoria mamCategoria) {
		this.mamCategoria = mamCategoria;
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
	@JoinColumn(name = "FIG_SEQ", nullable = false, insertable = false, updatable = false)
	public MamFigura getMamFigura() {
		return this.mamFigura;
	}

	public void setMamFigura(MamFigura mamFigura) {
		this.mamFigura = mamFigura;
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

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MAM_CATEGORIAS("mamCategoria"),
		RAP_SERVIDORES("rapServidores"),
		MAM_FIGURAS("mamFigura"),
		IND_SITUACAO("indSituacao"),
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
		if (!(obj instanceof MamCategoriaFigura)) {
			return false;
		}
		MamCategoriaFigura other = (MamCategoriaFigura) obj;
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
