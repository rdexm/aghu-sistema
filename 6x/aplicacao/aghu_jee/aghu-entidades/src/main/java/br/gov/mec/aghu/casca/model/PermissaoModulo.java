/**
 * Contem as permissoes por Componente de cada modulo
 */
package br.gov.mec.aghu.casca.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * @author marcelofilho
 *
 */
@Entity
@Immutable
@Table(name = "CSC_PERMISSAO_MODULO", schema = "CASCA", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_MODULO", "ID_PERMISSAO"}))
@SequenceGenerator(name = "cscPermModSeq", sequenceName = "casca.casca_perm_mod_sq1", allocationSize = 1)
public class PermissaoModulo extends BaseEntityId<Integer> {

	private static final long serialVersionUID = 471742977343281355L;

	private Integer id;
	
	private Permissao permissao;
	
	private Modulo modulo;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscPermModSeq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="ID_PERMISSAO", nullable=false, updatable = false)
	public Permissao getPermissao() {
		return permissao;
	}

	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}

	@ManyToOne
	@JoinColumn(name="ID_MODULO", nullable=false, updatable = false)
	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}
	
	public enum Fields {

		ID("id"), 
		MODULO("modulo"),
		MODULO_ID("modulo.id"),
		MODULO_NOME("modulo.nome"),
		PERMISSAO("permissao"),
		PERMISSAO_ID("permissao.id"),
		PERMISSAO_NOME("permissao.nome");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}

		public String getFields() {
			return this.fields;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.fields;
		}
	}
}