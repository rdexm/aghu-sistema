package br.gov.mec.aghu.casca.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "CSC_APLICACAO", schema = "CASCA", uniqueConstraints = @UniqueConstraint(columnNames = {"NOME"}))
@SequenceGenerator(name = "cscAplicSeq", sequenceName = "casca.casca_aplic_sq1", allocationSize = 1)
@org.hibernate.annotations.Cache(usage =org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class Aplicacao extends BaseEntityId<Integer> {
	
	// teste qa 1
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

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscAplicSeq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "SERVIDOR", length = 100)
	@Length(max = 100, message = "Endereço do servidor da aplicação pode conter no máximo 100 caracteres")
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
	public Protocolo getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Protocolo protocolo) {
		this.protocolo = protocolo;
	}

	@Column(name = "CONTEXTO", length = 100)
	@Length(max = 100, message = "Contexto da aplicação pode conter no máximo 100 caracteres")
	public String getContexto() {
		return contexto;
	}

	public void setContexto(String contexto) {
		this.contexto = contexto;
	}

	@Column(name = "NOME", length = 50, nullable = false, unique = true)
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
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
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
}