package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="fcuAmtSq1", sequenceName="AGH.FCU_AMT_SQ1", allocationSize = 1)
@Table(name = "FCU_AGRUPA_GRUPO_MATERIAIS", schema = "AGH")

public class FcuAgrupaGrupoMaterial extends BaseEntitySeq<Short> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7117431973236388647L;
	private Short seq;
	private String descricao;
	private DominioSituacao situacao;

	// construtores

	public FcuAgrupaGrupoMaterial() {
	}

	// getters & setters
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fcuAmtSq1")
	@Column(name = "SEQ", length = 3, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FcuAgrupaGrupoMaterial)){
			return false;
		}
		FcuAgrupaGrupoMaterial castOther = (FcuAgrupaGrupoMaterial) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), 
		DESCRICAO("descricao"), 
		SITUACAO("situacao");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}