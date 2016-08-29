package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

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
@Table(name = "MPM_JUSTIF_TROCA_ESQUEMA_TBS", schema = "AGH")
public class MpmJustifTrocaEsquemaTb extends BaseEntityId<MpmJustifTrocaEsquemaTbId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3897527861605068931L;
	private MpmJustifTrocaEsquemaTbId id;
	private MpmTrocaEsquemaTb trocaEsquemaTb;
	private MpmTipoJustifTrocaEsqTb tipoJustifTrocaEsqTb;
	private RapServidores servidor;
	private Date criadoEm;
	private String descricao;

	public MpmJustifTrocaEsquemaTb() {
	}

	public MpmJustifTrocaEsquemaTb(MpmJustifTrocaEsquemaTbId id,
			MpmTrocaEsquemaTb trocaEsquemaTb,
			MpmTipoJustifTrocaEsqTb tipoJustifTrocaEsqTb,
			RapServidores servidor, Date criadoEm) {
		this.id = id;
		this.trocaEsquemaTb = trocaEsquemaTb;
		this.tipoJustifTrocaEsqTb = tipoJustifTrocaEsqTb;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
	}

	public MpmJustifTrocaEsquemaTb(MpmJustifTrocaEsquemaTbId id,
			MpmTrocaEsquemaTb trocaEsquemaTb,
			MpmTipoJustifTrocaEsqTb tipoJustifTrocaEsqTb,
			RapServidores servidor, Date criadoEm,
			String descricao) {
		this.id = id;
		this.trocaEsquemaTb = trocaEsquemaTb;
		this.tipoJustifTrocaEsqTb = tipoJustifTrocaEsqTb;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.descricao = descricao;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "tebJumSeq", column = @Column(name = "TEB_JUM_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "tteSeq", column = @Column(name = "TTE_SEQ", nullable = false, precision = 3, scale = 0)) })
	public MpmJustifTrocaEsquemaTbId getId() {
		return this.id;
	}

	public void setId(MpmJustifTrocaEsquemaTbId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEB_JUM_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmTrocaEsquemaTb getTrocaEsquemaTb() {
		return this.trocaEsquemaTb;
	}

	public void setTrocaEsquemaTb(MpmTrocaEsquemaTb trocaEsquemaTb) {
		this.trocaEsquemaTb = trocaEsquemaTb;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TTE_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmTipoJustifTrocaEsqTb getTipoJustifTrocaEsqTb() {
		return this.tipoJustifTrocaEsqTb;
	}

	public void setTipoJustifTrocaEsqTb(
			MpmTipoJustifTrocaEsqTb tipoJustifTrocaEsqTb) {
		this.tipoJustifTrocaEsqTb = tipoJustifTrocaEsqTb;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCRICAO", length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpmJustifTrocaEsquemaTb other = (MpmJustifTrocaEsquemaTb) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	
	public enum Fields {

		ID("id"),
		TEB_JUM_SEQ("id.tebJumSeq"),
		TTE_SEQ("id.tteSeq"),
		TROCA_ESQUEMA_TB("trocaEsquemaTb"),
		TIPO_JUSTIF_TROCA_ESQ_TB("tipoJustifTrocaEsqTb"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
