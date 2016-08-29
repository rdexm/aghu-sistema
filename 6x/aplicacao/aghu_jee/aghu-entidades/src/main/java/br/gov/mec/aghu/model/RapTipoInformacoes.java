package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="rapTiiSq1", sequenceName="AGH.RAP_TII_SQ1", allocationSize = 1)
@Table(name = "RAP_TIPO_INFORMACOES", schema = "AGH")
public class RapTipoInformacoes extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4870520929884263236L;
	private Integer seq;
	private String descricao;
	private Integer version;

	
	public RapTipoInformacoes() {
	}

	public RapTipoInformacoes(Integer seq, String descricao, Integer version ) {
		this.seq = seq;
		this.descricao = descricao;
		this.version = version;
	}	
	
	// getters & setters
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapTiiSq1")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		if(descricao != null){
			this.descricao = descricao;
		}
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	

	
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapTipoInformacoes)) {
			return false;
		}
		RapTipoInformacoes castOther = (RapTipoInformacoes) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), DESCRICAO("descricao");

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