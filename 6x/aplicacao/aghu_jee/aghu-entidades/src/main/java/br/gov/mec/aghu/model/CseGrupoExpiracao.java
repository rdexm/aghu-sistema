package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="cseGueSq1", sequenceName="AGH.CSE_GUE_SQ1", allocationSize = 1)
@Table(name = "CSE_GRUPO_EXPIRACOES", schema = "AGH")
public class CseGrupoExpiracao extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2811183184274058126L;
	private Short seq;
	private Integer version;
	private String descricao;
	private Short dias;
	private Set<CseGrupoExpDetalhe> cseGrupoExpDetalhees = new HashSet<CseGrupoExpDetalhe>(0);

	public CseGrupoExpiracao() {
	}

	public CseGrupoExpiracao(Short seq, String descricao) {
		this.seq = seq;
		this.descricao = descricao;
	}

	public CseGrupoExpiracao(Short seq, String descricao, Short dias, Set<CseGrupoExpDetalhe> cseGrupoExpDetalhees) {
		this.seq = seq;
		this.descricao = descricao;
		this.dias = dias;
		this.cseGrupoExpDetalhees = cseGrupoExpDetalhees;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cseGueSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DIAS")
	public Short getDias() {
		return this.dias;
	}

	public void setDias(Short dias) {
		this.dias = dias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cseGrupoExpiracao")
	public Set<CseGrupoExpDetalhe> getCseGrupoExpDetalhees() {
		return this.cseGrupoExpDetalhees;
	}

	public void setCseGrupoExpDetalhees(Set<CseGrupoExpDetalhe> cseGrupoExpDetalhees) {
		this.cseGrupoExpDetalhees = cseGrupoExpDetalhees;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		DESCRICAO("descricao"),
		DIAS("dias"),
		CSE_GRUPO_EXP_DETALHEES("cseGrupoExpDetalhees");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof CseGrupoExpiracao)) {
			return false;
		}
		CseGrupoExpiracao other = (CseGrupoExpiracao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
