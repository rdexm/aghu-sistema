package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
@Table(name = "FCP_VALOR_MOEDAS", schema = "AGH")
public class FcpValorMoeda extends BaseEntityId<FcpValorMoedaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -682932839237785558L;
	private FcpValorMoedaId id;
	private Integer version;
	private FcpMoeda fcpMoeda;
	private Double valor;

	public FcpValorMoeda() {
	}

	public FcpValorMoeda(FcpValorMoedaId id, FcpMoeda fcpMoeda, Double valor) {
		this.id = id;
		this.fcpMoeda = fcpMoeda;
		this.valor = valor;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "mdaCodigo", column = @Column(name = "MDA_CODIGO", nullable = false)),
			@AttributeOverride(name = "dtCompetencia", column = @Column(name = "DT_COMPETENCIA", nullable = false, length = 29)) })
	public FcpValorMoedaId getId() {
		return this.id;
	}

	public void setId(FcpValorMoedaId id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDA_CODIGO", nullable = false, insertable = false, updatable = false)
	public FcpMoeda getFcpMoeda() {
		return this.fcpMoeda;
	}

	public void setFcpMoeda(FcpMoeda fcpMoeda) {
		this.fcpMoeda = fcpMoeda;
	}

	@Column(name = "VALOR", nullable = false, precision = 17, scale = 17)
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		FCP_MOEDA("fcpMoeda"),
		VALOR("valor");

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
		if (!(obj instanceof FcpValorMoeda)) {
			return false;
		}
		FcpValorMoeda other = (FcpValorMoeda) obj;
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
