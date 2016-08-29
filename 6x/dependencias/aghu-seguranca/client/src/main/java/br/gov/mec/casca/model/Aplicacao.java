package br.gov.mec.casca.model;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

@Entity
@Table(name = "CSC_APLICACAO", schema = "CASCA")
@SequenceGenerator(name = "cscAplicSeq", sequenceName = "casca.casca_aplic_sq1")
@Name("aplicacao")
public class Aplicacao implements Serializable {
	private static final long serialVersionUID = 6490349648338958147L;
	
	private Integer id;
	private String servidor;
	private Integer porta;
	private Protocolo protocolo;
	private String contexto;
	private String nome;
	private String descricao;
	private Boolean externo;
	private String urlInicial;
	
	private Set<Menu> menus = new HashSet<Menu>(0);
	private Set<Modulo> modulos = new HashSet<Modulo>(0);
	private Set<Componente> componentes = new HashSet<Componente>(0);

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscAplicSeq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "SERVIDOR", length = 20)
	@Length(max = 20, message = "Endereço do servidor da aplicação pode conter no máximo 20 caracteres")
	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	@Column(name = "PORTA")
	public Integer getPorta() {
		return porta;
	}

	public void setPorta(Integer porta) {
		this.porta = porta;
	}

	@Column(name = "PROTOCOLO", length = 6)
	@Enumerated(EnumType.STRING)
	@Length(max = 6, message = "Protocolo da aplicação pode conter no máximo 6 caracteres")
	public Protocolo getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Protocolo protocolo) {
		this.protocolo = protocolo;
	}

	@Column(name = "CONTEXTO", length = 25)
	@Length(max = 25, message = "Contexto da aplicação pode conter no máximo 25 caracteres")
	public String getContexto() {
		return contexto;
	}

	public void setContexto(String contexto) {
		this.contexto = contexto;
	}

	@Column(name = "NOME", length = 50, nullable = false)
	@NotNull(message = "Nome da aplicação não informado")
	@Length(max = 50, message = "Nome da aplicação pode conter no máximo 50 caracteres")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", length = 500)
	@Length(max = 500, message = "Descrição da aplicação pode conter no máximo 500 caracteres")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "EXTERNO", nullable = false, length = 1)
	@NotNull(message = "Indicador de aplicação externa não informado")
	@Type(type="br.gov.mec.seam.hibernate.type.BooleanUserType")
	public Boolean getExterno() {
		return externo;
	}

	public void setExterno(Boolean externo) {
		this.externo = externo;
	}

	/**
	 * @return the urlInicial
	 */
	@Column(name = "URLINICIAL", length = 30)
	@Length(max = 30, message = "URL inicial da aplicação pode conter no máximo 30 caracteres")
	public String getUrlInicial() {
		return urlInicial;
	}

	/**
	 * @param urlInicial the urlInicial to set
	 */
	public void setUrlInicial(String urlInicial) {
		this.urlInicial = urlInicial;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aplicacao")
	@OrderBy("ordem")
	@Cascade({org.hibernate.annotations.CascadeType.DELETE})
	public Set<Menu> getMenus() {
		return menus;
	}

	public void setMenus(Set<Menu> menus) {
		this.menus = menus;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aplicacao")
	@Cascade({org.hibernate.annotations.CascadeType.DELETE})
	public Set<Modulo> getModulos() {
		return modulos;
	}

	public void setModulos(Set<Modulo> modulos) {
		this.modulos = modulos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aplicacao")
	@Cascade({org.hibernate.annotations.CascadeType.DELETE})
	public Set<Componente> getComponentes() {
		return componentes;
	}

	public void setComponentes(Set<Componente> componentes) {
		this.componentes = componentes;
	}	

	public enum Fields {
		ID("id"), SERVIDOR("servidor"), PORTA("porta"), CONTEXTO("contexto"), NOME("nome"),
		EXTERNO("externo"), URLINICIAL("urlInicial");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		public String toString() {
			return this.fields;
		}
	}
	
	@Override
	public String toString() {
		return "Aplicação [id=" + id + ", nome=" + nome + "]";
	}
}