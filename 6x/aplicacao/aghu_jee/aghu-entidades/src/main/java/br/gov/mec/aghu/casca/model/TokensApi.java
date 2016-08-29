package br.gov.mec.aghu.casca.model;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@SequenceGenerator(name = "cscTokenApiSeq", sequenceName = "casca.casca_tokens_api_sq1", allocationSize = 1)
@Table(name = "CSC_TOKENS_API", schema = "CASCA")
public class TokensApi extends BaseEntityId<Integer> {

	private static final long serialVersionUID = -8972890180265414193L;

	private Integer id;
	private UsuarioApi usuarioApi;
	private String hostCliente;
	private String token;
	private Date dataCriacao;
	private Date dataExpiracao;
	private Integer limiteOperacoes;
	private String refreshToken;
	private Integer indRefresh;
	private String loginHcpa;
	private Integer version;
	
	public TokensApi() {
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 13)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscTokenApiSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_USUARIO", nullable = false, updatable = false)
	public UsuarioApi getUsuarioApi() {
		return this.usuarioApi;
	}

	public void setUsuarioApi(UsuarioApi usuarioApi) {
		this.usuarioApi = usuarioApi;
	}
	
	@Column(name = "HOST_CLIENTE")
	public String getHostCliente() {
		return this.hostCliente;
	}

	public void setHostCliente(String hostCliente) {
		this.hostCliente = hostCliente;
	}

	@Column(name = "TOKEN")
	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_EXPIRACAO")
	public Date getDataExpiracao() {
		return this.dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	@Column(name = "LIMITE_OPERACOES")
	public Integer getLimiteOperacoes() {
		return limiteOperacoes;
	}

	public void setLimiteOperacoes(Integer limiteOperacoes) {
		this.limiteOperacoes = limiteOperacoes;
	}

	@Column(name = "REFRESH_TOKEN")
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Column(name = "IND_REFRESH")
	public Integer getIndRefresh() {
		return indRefresh;
	}

	public void setIndRefresh(Integer indRefresh) {
		this.indRefresh = indRefresh;
	}

	@Column(name = "LOGIN_HCPA")
	public String getLoginHcpa() {
		return loginHcpa;
	}

	public void setLoginHcpa(String loginHcpa) {
		this.loginHcpa = loginHcpa;
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
		USUARIO_API("usuarioApi"), HOST_CLIENTE("hostCliente"), LIMITE_OPERACOES("limiteOperacoes"),
		USUARIO_API_SEQ("usuarioApi.id"), SEQ("id"), DATA_EXPIRACAO("dataExpiracao"), TOKEN("token"),
		REFRESH_TOKEN("refreshToken"), IND_REFRESH("indRefresh"), LOGIN_HCPA("loginHcpa");
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