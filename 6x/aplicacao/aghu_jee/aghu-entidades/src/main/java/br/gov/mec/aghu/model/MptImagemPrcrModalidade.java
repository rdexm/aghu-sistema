package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

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
@Table(name = "MPT_IMAGEM_PRCR_MODALIDADES", schema = "AGH")
public class MptImagemPrcrModalidade extends BaseEntityId<MptItemPrcrModalidadeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2353323647488489519L;
	private MptItemPrcrModalidadeId id;
	private Integer version;
	private MptItemPrcrModalidade mptItemPrcrModalidade;
	private RapServidores rapServidores;
	private byte[] imagem;

	public MptImagemPrcrModalidade() {
	}

	public MptImagemPrcrModalidade(MptItemPrcrModalidade mptItemPrcrModalidade, RapServidores rapServidores, byte[] imagem) {
		this.mptItemPrcrModalidade = mptItemPrcrModalidade;
		this.rapServidores = rapServidores;
		this.imagem = imagem;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "seqp", column = @Column(name = "ITM_SEQP", nullable = false)),
			@AttributeOverride(name = "pteAtdSeq", column = @Column(name = "ITM_PTE_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "pteSeq", column = @Column(name = "ITM_PTE_SEQ", nullable = false)) })
	public MptItemPrcrModalidadeId getId() {
		return this.id;
	}

	public void setId(MptItemPrcrModalidadeId id) {
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

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public MptItemPrcrModalidade getMptItemPrcrModalidade() {
		return this.mptItemPrcrModalidade;
	}

	public void setMptItemPrcrModalidade(MptItemPrcrModalidade mptItemPrcrModalidade) {
		this.mptItemPrcrModalidade = mptItemPrcrModalidade;
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

	@Column(name = "IMAGEM", nullable = false)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	public byte[] getImagem() {
		return this.imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MPT_ITEM_PRCR_MODALIDADES("mptItemPrcrModalidade"),
		RAP_SERVIDORES("rapServidores"),
		IMAGEM("imagem");

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
		if (!(obj instanceof MptImagemPrcrModalidade)) {
			return false;
		}
		MptImagemPrcrModalidade other = (MptImagemPrcrModalidade) obj;
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
