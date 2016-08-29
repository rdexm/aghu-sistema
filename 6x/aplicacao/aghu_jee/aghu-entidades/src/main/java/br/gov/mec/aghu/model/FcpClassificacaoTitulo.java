package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@SequenceGenerator(name="fcpCltSq1", sequenceName="AGH.FCP_CLT_SQ1", allocationSize = 1)
@Table(name = "FCP_CLASSIF_TITULO", schema = "AGH")
public class FcpClassificacaoTitulo extends BaseEntityCodigo<Short> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3432175436817755004L;

	private Short codigo;
	
	private String descricao;
	
	private DominioSituacao indSituacao;
	
	private Integer version;

	public FcpClassificacaoTitulo() {
	}

	public FcpClassificacaoTitulo(Short codigo, String descricao,
			DominioSituacao indSituacao, Integer version) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.version = version;
	}

	@Id
	@Column(name = "CODIGO", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fcpCltSq1")
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		CODIGO("codigo"), 
		DESCRICAO("descricao"), 
		IND_SITUACAO("indSituacao");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FcpClassificacaoTitulo)){
			return false;
		}
		FcpClassificacaoTitulo castOther = (FcpClassificacaoTitulo) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}
}
