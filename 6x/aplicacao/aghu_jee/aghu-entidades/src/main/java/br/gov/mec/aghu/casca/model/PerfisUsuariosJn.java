package br.gov.mec.aghu.casca.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "CSC_PERFIS_USUARIOS_JN", schema = "CASCA")
@SequenceGenerator(name = "cscPerfUsuJnSeq", sequenceName = "casca.casca_perfil_user_jn_sq1", allocationSize = 1)
@Immutable
public class PerfisUsuariosJn extends BaseJournal implements
		java.io.Serializable {
	private static final long serialVersionUID = -4201144719442666228L;

	private Integer id;
	private Integer idUsuario;
	private String login;
	private Integer idPerfil;
	private String nomePerfil;
	private Date dataExpiracao;
	private String motivoDelegacao;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscPerfUsuJnSeq")
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

	@Column(name = "ID_USUARIO", nullable = false)
	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	@Column(name = "LOGIN", unique = false, nullable = false, length = 50)
	@Length(max = 50, message = "Login pode conter no máximo 50 caracteres")
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "ID_PERFIL", nullable = false)
	public Integer getIdPerfil() {
		return this.idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}
	
	@Column(name = "NOME_PERFIL", nullable = false, length = 500)
	@Length(max = 500, message = "Nome do perfil pode conter no máximo 500 caracteres")
	public String getNomePerfil() {
		return this.nomePerfil;
	}

	public void setNomePerfil(String nomePerfil) {
		this.nomePerfil = nomePerfil;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_EXPIRACAO", nullable = true)
	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	@Column(name = "MOTIVO_DELEGACAO")
	public String getMotivoDelegacao() {
		return motivoDelegacao;
	}

	public void setMotivoDelegacao(String motivoDelegacao) {
		this.motivoDelegacao = motivoDelegacao;
	}

	public enum Fields {
		SEQ_JN("seqJn"),
		ID_USUARIO("idUsuario"),
		ID_PERFIL("idPerfil"),
		LOGIN("login"),
		NOME_PERFIL("nomePerfil"),
		NOME_USUARIO("nomeUsuario"),
		DATA_ALTERACAO("dataAlteracao"),
		DATA_EXPIRACAO("dataExpiracao"),
		OPERACAO("operacao"),
		MOTIVO_DELEGACAO("motivoDelegacao")
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