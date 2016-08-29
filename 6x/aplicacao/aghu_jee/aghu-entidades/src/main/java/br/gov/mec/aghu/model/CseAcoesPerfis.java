package br.gov.mec.aghu.model;

// Generated 20/01/2011 10:42:11 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * TODO Realizar refactoring para não usar mais esta classe conforme proposto nas atividades:
 * http://redmine.mec.gov.br/issues/9679
 * http://redmine.mec.gov.br/issues/9680
 * 
 * OBSERVAÇÃO: Trocar o uso desta classe por chamadas da API do CASCA
 * @see CascaService
 * 
 * Vicente 16/08/2011
 */
@Entity
@Table(name = "CSE_ACOES_PERFIS", schema = "AGH")
public class CseAcoesPerfis extends BaseEntityId<CseAcoesPerfisId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5783323953563138444L;

	private CseAcoesPerfisId id;
	
	private CseAcoes acao;
	
	private String perfil;

	public CseAcoesPerfis() {
	}

	public CseAcoesPerfis(CseAcoesPerfisId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "perNome", column = @Column(name = "PER_NOME", nullable = false, length = 30)),
			@AttributeOverride(name = "acoSeq", column = @Column(name = "ACO_SEQ", nullable = false, precision = 4, scale = 0)) })
	public CseAcoesPerfisId getId() {
		return this.id;
	}

	public void setId(CseAcoesPerfisId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACO_SEQ", nullable = false, insertable = false, updatable = false)
	public CseAcoes getAcao() {
		return acao;
	}

	public void setAcao(CseAcoes acao) {
		this.acao = acao;
	}
	
	@Column(name = "PER_NOME", nullable = false, insertable = false, updatable = false)
	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}


	public enum Fields {
		ACAO("acao"), PERFIL("perfil");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof CseAcoesPerfis)) {
			return false;
		}
		CseAcoesPerfis other = (CseAcoesPerfis) obj;
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
