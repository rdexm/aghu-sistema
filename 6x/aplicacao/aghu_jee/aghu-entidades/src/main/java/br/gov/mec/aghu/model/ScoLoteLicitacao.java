package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "SCO_LOTES_LICITACAO", schema = "AGH")
public class ScoLoteLicitacao extends BaseEntityId<ScoLoteLicitacaoId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2521460894934973689L;
	private ScoLoteLicitacaoId id;
	private String descricao;
	private ScoLicitacao scoLicitacao;
	private Integer version;

	// construtores

	public ScoLoteLicitacao() {
	}

	public ScoLoteLicitacao(ScoLoteLicitacaoId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "LCT_NUMERO", column = @Column(name = "LCT_NUMERO", nullable = false, length = 7)),
			@AttributeOverride(name = "NUMERO", column = @Column(name = "NUMERO", nullable = false, length = 3)) })
	public ScoLoteLicitacaoId getId() {
		return this.id;
	}

	public void setId(ScoLoteLicitacaoId id) {
		this.id = id;
	}

	@Column(name = "DESCRICAO", length = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LCT_NUMERO", referencedColumnName = "NUMERO", insertable=false, updatable=false)
	public ScoLicitacao getScoLicitacao() {
		return scoLicitacao;
	}

	public void setScoLicitacao(ScoLicitacao scoLicitacao) {
		this.scoLicitacao = scoLicitacao;
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
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoLoteLicitacao)){
			return false;
		}
		ScoLoteLicitacao castOther = (ScoLoteLicitacao) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		NUMERO("id.numero"), NUMERO_LCT("id.lctNumero"), DESCRICAO("descricao"), LICITACAO("scoLicitacao"),VERSION("version") ;

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