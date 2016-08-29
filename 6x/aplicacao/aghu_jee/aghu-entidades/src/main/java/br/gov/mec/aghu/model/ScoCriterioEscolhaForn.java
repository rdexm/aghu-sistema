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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "SCO_CRITERIO_ESCOLHA_FORN", schema = "AGH")
@SequenceGenerator(name = "scoCefSq1", sequenceName = "AGH.SCO_CEF_SQ1", allocationSize=1)
public class ScoCriterioEscolhaForn extends BaseEntityCodigo<Short> implements Serializable {

	private static final long serialVersionUID = -8762701051346038434L;
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;

	public ScoCriterioEscolhaForn() {
	}

	@Id
	@Column(name = "CODIGO", length = 3, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoCefSq1")
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoCriterioEscolhaForn)){
			return false;
		}
		ScoCriterioEscolhaForn castOther = (ScoCriterioEscolhaForn) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	public enum Fields {
		CODIGO("codigo"), 
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
