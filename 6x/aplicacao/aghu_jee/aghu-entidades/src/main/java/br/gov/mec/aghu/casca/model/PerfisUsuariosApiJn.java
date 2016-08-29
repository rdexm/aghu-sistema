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

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "CSC_PERFIS_USUARIOS_API_JN", schema = "CASCA")
@SequenceGenerator(name = "cscPerfUsuApiJnSeq", sequenceName = "casca.casca_perfil_user_jn_sq1", allocationSize = 1)
public class PerfisUsuariosApiJn extends BaseJournal implements
		java.io.Serializable {
	private static final long serialVersionUID = -4201144719442666228L;

	private Integer id;
	private Integer idUsuario;
	private Integer idPerfil;
	private Date dataCriacao;	

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscPerfUsuApiJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ID_USUARIO_API")
	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	@Column(name = "ID_PERFIL", nullable = false)
	public Integer getIdPerfil() {
		return this.idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ATRIBUICAO")
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public enum Fields {
		SEQ_JN("seqJn"),
		ID_USUARIO("idUsuario"),
		ID_PERFIL("idPerfil"),
		OPERACAO("operacao")
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