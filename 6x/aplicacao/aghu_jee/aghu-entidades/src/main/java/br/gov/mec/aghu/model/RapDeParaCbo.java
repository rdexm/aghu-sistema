package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "RapDeParaCboSequence", sequenceName = "AGH.RAP_DPC_SQ1", allocationSize = 1)
@Table(name = "RAP_DEPARA_CBO", schema = "AGH")
public class RapDeParaCbo extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3160077029239340913L;
	private Integer seq;
	private String codigoCboAntigo;
	private String descricaoCboAntigo;
	private String codigoCboNovo;
	private String descricaoCboNovo;

	@GeneratedValue(strategy = GenerationType.AUTO, generator = "RapDeParaCboSequence")
	@Id
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "COD_CBO_ANTIGO", length = 6)
	@Length(max = 6)
	public String getCodigoCboAntigo() {
		return codigoCboAntigo;
	}

	public void setCodigoCboAntigo(String codigoCboAntigo) {
		this.codigoCboAntigo = codigoCboAntigo;
	}

	@Column(name = "DESCR_CBO_ANTIGO", length = 600)
	@Length(max = 600)
	public String getDescricaoCboAntigo() {
		return descricaoCboAntigo;
	}

	public void setDescricaoCboAntigo(String descricaoCboAntigo) {
		this.descricaoCboAntigo = descricaoCboAntigo;
	}

	@Column(name = "COD_CBO_NOVO", length = 6)
	@Length(max = 6)
	public String getCodigoCboNovo() {
		return codigoCboNovo;
	}

	public void setCodigoCboNovo(String codigoCboNovo) {
		this.codigoCboNovo = codigoCboNovo;
	}

	@Column(name = "DESCR_CBO_NOVO", length = 600)
	@Length(max = 600)
	public String getDescricaoCboNovo() {
		return descricaoCboNovo;
	}

	public void setDescricaoCboNovo(String descricaoCboNovo) {
		this.descricaoCboNovo = descricaoCboNovo;
	}

	public enum Fields {
		SEQ("seq"), COD_CBO_NOVO("codigoCboNovo"), COD_CBO_ANTIGO("codigoCboAntigo") ;

		private final String field;

		private Fields(final String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		RapDeParaCbo other = (RapDeParaCbo) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
