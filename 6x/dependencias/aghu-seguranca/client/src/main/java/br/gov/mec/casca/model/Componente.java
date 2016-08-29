package br.gov.mec.casca.model;

// Generated May 1, 2010 9:46:52 AM by Hibernate Tools 3.3.0.GA

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * OBS: Se a sequence utilizada para gerar a chave primária desta entidade 
 * ficar maior que o tamanho limite da coluna, ela pode ser zerada a qualquer
 * momento uma vez que o script apaga todos os registros que esta classe 
 * mapeia no início da execução.
 */
@Entity
@Table(name = "CSC_COMPONENTE", schema = "CASCA",  uniqueConstraints = 
	@UniqueConstraint(columnNames = {"ID_APLICACAO", "NOME"}))
@SequenceGenerator(name="cscCompSeq", sequenceName="casca.casca_componente_sq1")
public class Componente implements java.io.Serializable {

	private static final long serialVersionUID = 2308756072762531833L;

	private Integer id;

	private String nome;

	private Aplicacao aplicacao;
	
	private Set<Metodo> metodos = new HashSet<Metodo>(0);

	private Set<PermissoesComponentes> permissoesComponenteses = new HashSet<PermissoesComponentes>(0);

	public Componente() {
	}

	public Componente(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public Componente(Integer id, String nome, Set<Metodo> metodos,
			Set<PermissoesComponentes> permissoesComponenteses) {
		this.id = id;
		this.nome = nome;
		this.metodos = metodos;
		this.permissoesComponenteses = permissoesComponenteses;
	}

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="cscCompSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "NOME", nullable = false, length = 250)
	@NotNull(message = "Nome do componente não informado")
	@Length(max = 250, message = "Nome do componente pode conter no máximo 250 caracteres")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "componente")
	public Set<Metodo> getMetodos() {
		return this.metodos;
	}

	public void setMetodos(Set<Metodo> metodos) {
		this.metodos = metodos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "componente")
	public Set<PermissoesComponentes> getPermissoesComponenteses() {
		return this.permissoesComponenteses;
	}

	public void setPermissoesComponenteses(
			Set<PermissoesComponentes> permissoesComponenteses) {
		this.permissoesComponenteses = permissoesComponenteses;
	}
	
	public enum Fields {
		ID("id"), NOME("nome"), METODOS("metodos"), PERMISSOES_COMPONENTES(
				"permissoesComponenteses"), APLICACAO("aplicacao");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_APLICACAO", nullable = false)
	@NotNull
	public Aplicacao getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(Aplicacao aplicacao) {
		this.aplicacao = aplicacao;
	}

	@Override
	public String toString() {
		return "Componente [id=" + id + ", nome=" + nome + "]";
	}
}
