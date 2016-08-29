package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "ECP_SERVIDOR_UNID_FUNCIONAIS", schema = "AGH")
@SequenceGenerator(name = "ecpSufSq1", sequenceName = "AGH.ECP_SUF_SQ1", allocationSize = 1)
public class EcpServidorUnidFuncional extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2463753671821312685L;
	private Integer seq;
	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;

	// construtores

	public EcpServidorUnidFuncional() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 5, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpSufSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "UNF_SEQ", referencedColumnName = "SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpServidorUnidFuncional)) {
			return false;
		}
		EcpServidorUnidFuncional castOther = (EcpServidorUnidFuncional) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"),
		RAP_SERVIDORES("servidor"),
		AGH_UNIDADES_FUNCIONAIS("unidadeFuncional");

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
