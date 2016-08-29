package br.gov.mec.aghu.casca.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "CSC_USUARIO_JN", schema = "CASCA")
@SequenceGenerator(name = "cscUserJnSeq", sequenceName = "casca.casca_user_jn_sq1", allocationSize = 1)
@Immutable
public class UsuarioJn extends BaseJournal implements java.io.Serializable {
	private static final long serialVersionUID = -3413507927642842501L;

	private Integer id;
	private String nome;
	private String login;
	private String email;
	private Boolean ativo;
	private boolean delegarPerfil;
	private Integer tempoSessaoMinutos;
	

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscUserJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "NOME", unique = false, nullable = false, length = 250)
	@Length(max = 250, message = "Nome do usuário pode conter no máximo 250 caracteres")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column(name = "LOGIN", unique = false, nullable = false, length = 50)
	@Length(max = 50, message = "Login pode conter no máximo 50 caracteres")
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "EMAIL", length = 250)
	@Length(max = 250, message = "Email do usuário pode conter no máximo 250 caracteres")
	@Email(message = "Email fora do padrão, informe um email do tipo <usuario>@<endereco>")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "ATIVO", nullable = false)
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	@Column(name = "DELEGAR_PERFIL", nullable = false)
	public boolean isDelegarPerfil() {
		return delegarPerfil;
	}

	public void setDelegarPerfil(boolean delegarPerfil) {
		this.delegarPerfil = delegarPerfil;
	}
	
	@Column(name = "TEMPO_SESSAO_MINUTOS", nullable = false)
	public Integer getTempoSessaoMinutos() {
		return tempoSessaoMinutos;
	}

	public void setTempoSessaoMinutos(Integer tempoSessao) {
		this.tempoSessaoMinutos = tempoSessao;
	}

	public enum Fields {
		SEQ_JN("seqJn"),
		ID("id"),
		LOGIN("login"),
		NOME_USUARIO("nomeUsuario"),
		DATA_ALTERACAO("dataAlteracao"),
		OPERACAO("operacao"),
		DELEGAR_PERFIL("delegarPerfil"),
		TEMPO_SESSAO_MINUTOS("tempoSessaoMinutos"),
		;
		
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
