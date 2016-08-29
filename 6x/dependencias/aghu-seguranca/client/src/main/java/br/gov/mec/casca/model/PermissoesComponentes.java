package br.gov.mec.casca.model;

// Generated May 1, 2010 9:46:52 AM by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

/**
 * OBS: Se a sequence utilizada para gerar a chave primária desta entidade 
 * ficar maior que o tamanho limite da coluna, ela pode ser zerada a qualquer
 * momento uma vez que o script apaga todos os registros que esta classe 
 * mapeia no início da execução.
 */
@Entity
@Table(name = "CSC_PERMISSOES_COMPONENTES", schema = "CASCA")
@SequenceGenerator(name="cscPermCompSeq", sequenceName="casca.casca_perm_comp_sq1")
public class PermissoesComponentes implements java.io.Serializable {

	private static final long serialVersionUID = -8858568250340971953L;

	private Integer id;
	private Permissao permissao;
	private Metodo metodo;
	private Componente componente;

	public PermissoesComponentes() {
	}

	public PermissoesComponentes(Integer id, Permissao permissao, Metodo metodo,
			Componente componente) {
		this.id = id;
		this.permissao = permissao;
		this.metodo = metodo;
		this.componente = componente;
	}

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="cscPermCompSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PERMISSAO", nullable = false)
	@NotNull
	public Permissao getPermissao() {
		return this.permissao;
	}

	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_METODO", nullable = false)
	@NotNull
	public Metodo getMetodo() {
		return this.metodo;
	}

	public void setMetodo(Metodo metodo) {
		this.metodo = metodo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_COMPONENTE", nullable = false)
	@NotNull
	public Componente getComponente() {
		return this.componente;
	}

	public void setComponente(Componente componente) {
		this.componente = componente;
	}
	
	public enum Fields {
		COMPONENTE_ID("componente.id"), METODO_ID("metodo.id"), PERMISSAO_ID("permissao.id"),
		COMPONENTE("componente"), METODO("metodo"), PERMISSAO("permissao");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}	

}
