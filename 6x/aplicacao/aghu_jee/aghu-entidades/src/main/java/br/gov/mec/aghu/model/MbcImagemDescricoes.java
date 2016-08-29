package br.gov.mec.aghu.model;

// Generated 19/04/2012 16:57:27 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "MBC_IMAGEM_DESCRICOES", schema = "AGH")
public class MbcImagemDescricoes extends BaseEntityId<MbcFiguraDescricoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8573949277684816143L;
	private MbcFiguraDescricoesId id;
	private Integer version;
	private MbcFiguraDescricoes mbcFiguraDescricoes;
	private byte[] imagem;

	public MbcImagemDescricoes() {
	}

	public MbcImagemDescricoes(MbcFiguraDescricoes mbcFiguraDescricoes,
			byte[] imagem) {
		this.mbcFiguraDescricoes = mbcFiguraDescricoes;
		this.imagem = imagem;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "dcgCrgSeq", column = @Column(name = "FDC_DCG_CRG_SEQ", nullable = false)),
			@AttributeOverride(name = "dcgSeqp", column = @Column(name = "FDC_DCG_SEQP", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "FDC_SEQP", nullable = false)) })
	public MbcFiguraDescricoesId getId() {
		return this.id;
	}

	public void setId(MbcFiguraDescricoesId id) {
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
	public MbcFiguraDescricoes getMbcFiguraDescricoes() {
		return this.mbcFiguraDescricoes;
	}

	public void setMbcFiguraDescricoes(MbcFiguraDescricoes mbcFiguraDescricoes) {
		this.mbcFiguraDescricoes = mbcFiguraDescricoes;
	}

	@Column(name = "IMAGEM", nullable = false)
	public byte[] getImagem() {
		return this.imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public enum Fields {
		FDC_DCG_CRG_SEQ("id.dcgCrgSeq"),
		FDC_DCG_SEQP("id.dcgSeqp"),
		FDC_SEQP("id.seqp");
		
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
		if (!(obj instanceof MbcImagemDescricoes)) {
			return false;
		}
		MbcImagemDescricoes other = (MbcImagemDescricoes) obj;
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
