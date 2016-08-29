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
@SequenceGenerator(name="csePrvSq1", sequenceName="AGH.CSE_PRV_SQ1", allocationSize = 1)
@Table(name = "CSE_PRIVILEGIOS", schema = "AGH")
public class CsePrivilegio extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2254196878281056523L;
	private Short seq;
	private Integer version;
	private String privilegio;
	private String indAtivo;
	private Set<CsePrivilegioTipoUsuario> csePrivilegioTipoUsuarios = new HashSet<CsePrivilegioTipoUsuario>(0);

	public CsePrivilegio() {
	}

	public CsePrivilegio(Short seq, String privilegio) {
		this.seq = seq;
		this.privilegio = privilegio;
	}

	public CsePrivilegio(Short seq, String privilegio, String indAtivo, Set<CsePrivilegioTipoUsuario> csePrivilegioTipoUsuarios) {
		this.seq = seq;
		this.privilegio = privilegio;
		this.indAtivo = indAtivo;
		this.csePrivilegioTipoUsuarios = csePrivilegioTipoUsuarios;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "csePrvSq1")
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

	@Column(name = "PRIVILEGIO", nullable = false, length = 40)
	@Length(max = 40)
	public String getPrivilegio() {
		return this.privilegio;
	}

	public void setPrivilegio(String privilegio) {
		this.privilegio = privilegio;
	}

	@Column(name = "IND_ATIVO", length = 1)
	@Length(max = 1)
	public String getIndAtivo() {
		return this.indAtivo;
	}

	public void setIndAtivo(String indAtivo) {
		this.indAtivo = indAtivo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "csePrivilegio")
	public Set<CsePrivilegioTipoUsuario> getCsePrivilegioTipoUsuarios() {
		return this.csePrivilegioTipoUsuarios;
	}

	public void setCsePrivilegioTipoUsuarios(Set<CsePrivilegioTipoUsuario> csePrivilegioTipoUsuarios) {
		this.csePrivilegioTipoUsuarios = csePrivilegioTipoUsuarios;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		PRIVILEGIO("privilegio"),
		IND_ATIVO("indAtivo"),
		CSE_PRIVILEGIO_TIPO_USUARIOS("csePrivilegioTipoUsuarios");

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
		if (!(obj instanceof CsePrivilegio)) {
			return false;
		}
		CsePrivilegio other = (CsePrivilegio) obj;
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
