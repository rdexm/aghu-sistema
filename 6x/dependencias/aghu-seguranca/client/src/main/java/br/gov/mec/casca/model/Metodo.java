package br.gov.mec.casca.model;

// Generated May 1, 2010 9:46:52 AM by Hibernate Tools 3.3.0.GA

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.gov.mec.aghu.dominio.DominioSimNao;

/**
 * OBS: Se a sequence utilizada para gerar a chave primária desta entidade 
 * ficar maior que o tamanho limite da coluna, ela pode ser zerada a qualquer
 * momento uma vez que o script apaga todos os registros que esta classe 
 * mapeia no início da execução.
 */
@Entity
@Table(name = "CSC_METODO", schema = "CASCA")
@SequenceGenerator(name = "cscMetodoSeq", sequenceName = "casca.casca_metodo_sq1")
public class Metodo implements java.io.Serializable {

	private static final long serialVersionUID = 9001990427323530844L;

	private Integer id;
	private Componente componente;
	private String nome;
	private String descriao;
	// FIXME Trocar por enum (DominioSituacao)
	private DominioSimNao ativo;
	private Set<PermissoesComponentes> permissoesComponenteses = new HashSet<PermissoesComponentes>(0);

	public Metodo() {
	}

	public Metodo(Integer id, Componente componente, String nome,
			String descriao, DominioSimNao ativo) {
		this.id = id;
		this.componente = componente;
		this.nome = nome;
		this.descriao = descriao;
		this.ativo = ativo;
	}

	public Metodo(Integer id, Componente componente, String nome,
			String descriao, DominioSimNao ativo,
			Set<PermissoesComponentes> permissoesComponenteses) {
		this.id = id;
		this.componente = componente;
		this.nome = nome;
		this.descriao = descriao;
		this.ativo = ativo;
		this.permissoesComponenteses = permissoesComponenteses;
	}

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscMetodoSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_COMPONENTE", nullable = false)
	@NotNull(message = "Componente do método não informado")
	public Componente getComponente() {
		return this.componente;
	}

	public void setComponente(Componente componente) {
		this.componente = componente;
	}

	@Column(name = "NOME", nullable = false, length = 250)
	@NotNull(message = "Nome do método não informado")
	@Length(max = 250, message = "Nome do método pode conter no máximo 250 caracteres")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRIAO", nullable = false, length = 1000)
	@NotNull(message = "Descrição do método não informada")
	@Length(max = 1000, message = "Descrição do método pode conter no máximo 1000 caracteres")	
	public String getDescriao() {
		return this.descriao;
	}

	public void setDescriao(String descriao) {
		this.descriao = descriao;
	}

	@Column(name = "ATIVO", nullable = false, length = 1)
	@NotNull(message = "Indicador de (in)ativo do método não informado")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getAtivo() {
		return this.ativo;
	}

	public void setAtivo(DominioSimNao ativo) {
		this.ativo = ativo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "metodo")
	public Set<PermissoesComponentes> getPermissoesComponenteses() {
		return this.permissoesComponenteses;
	}

	public void setPermissoesComponenteses(
			Set<PermissoesComponentes> permissoesComponenteses) {
		this.permissoesComponenteses = permissoesComponenteses;
	}

	public enum Fields {
		// FIXME Trocar COMPONENTE por COMPONENTE_ID e COMPONENTE_SEM_ID para COMPONENTE
		ID("id"), COMPONENTE("componente.id"), COMPONENTE_SEM_ID("componente"), NOME("nome"), DESCRICAO(
				"descriao"), ATIVO("ativo"), PERMISSOES_COMPONENTES(
				"permissoesComponenteses");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public String toString() {
		return "Metodo [id=" + id + ", componente.nome=" + componente.getNome() + ", nome=" + nome + "]";
	}
}
