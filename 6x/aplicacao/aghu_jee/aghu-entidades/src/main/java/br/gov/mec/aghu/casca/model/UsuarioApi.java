package br.gov.mec.aghu.casca.model;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@SequenceGenerator(name = "cscUserApiSeq", sequenceName = "casca.casca_usuarios_api_sq1", allocationSize = 1)
@Table(name = "CSC_USUARIOS_API", schema = "CASCA")
public class UsuarioApi extends BaseEntityId<Integer> {

	private static final long serialVersionUID = -8972860170235414190L;

	private Integer id;
	private String nome;
	private String loginHcpa;
	private Date dataCriacao;
	private String email;
	private Date dataUltimoAcesso;
	private boolean ativo;
	private Short tempoTokenMinutos;
	private Short limiteTokensAtivos;
	private String authUsuario;
	private String authKey;
	private Integer version;
	private Set<TokensApi> tokens = new HashSet<TokensApi>(0);
		
	public UsuarioApi() {
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 8)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscUserApiSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "NOME")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "LOGIN_HCPA", length = 30)
	@Length(max = 30, message = "Login pode conter no máximo 30 caracteres")
	public String getLoginHcpa() {
		return this.loginHcpa;
	}

	public void setLoginHcpa(String loginHcpa) {
		this.loginHcpa = loginHcpa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	@Column(name = "EMAIL", length = 250)
	@Email(message = "Email fora do padrão, informe um email do tipo <usuario>@<endereco>")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMO_ACESSO", length = 7)
	public Date getDataUltimoAcesso() {
		return this.dataUltimoAcesso;
	}

	public void setDataUltimoAcesso(Date dataUltimoAcesso) {
		this.dataUltimoAcesso = dataUltimoAcesso;
	}

	@Column(name = "ATIVO", nullable = false)
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	@Column(name = "TEMPO_TOKEN_MINUTOS")
	public Short getTempoTokenMinutos() {
		return tempoTokenMinutos;
	}

	public void setTempoTokenMinutos(Short tempoTokenMinutos) {
		this.tempoTokenMinutos = tempoTokenMinutos;
	}

	@Column(name = "LIMITE_TOKENS_ATIVOS")
	public Short getLimiteTokensAtivos() {
		return limiteTokensAtivos;
	}

	public void setLimiteTokensAtivos(Short limiteTokensAtivos) {
		this.limiteTokensAtivos = limiteTokensAtivos;
	}
	
	@Column(name = "AUTH_USUARIO", unique = true, nullable = false, length = 250)
	public String getAuthUsuario() {
		return this.authUsuario;
	}

	public void setAuthUsuario(String authUsuario) {
		this.authUsuario = authUsuario;
	}
	
	@Column(name = "AUTH_KEY", unique = true, nullable = false, length = 250)
	public String getAuthKey() {
		return this.authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioApi")
	public Set<TokensApi> getTokens() {
		return this.tokens;
	}

	public void setTokens(Set<TokensApi> tokens) {
		this.tokens = tokens;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		NOME("nome"), LOGIN_HCPA("loginHcpa"), ATIVO("ativo"), 
		ID("id"), AUTH_USUARIO("authUsuario"), AUTH_KEY("authKey"), EMAIL("email"), TOKENS("tokens");
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