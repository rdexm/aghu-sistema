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

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "CSC_PERFIS_USUARIOS_API", schema = "CASCA")
@SequenceGenerator(name="cscPerfUsuApiSeq", sequenceName="casca.csc_perfis_usuarios_api_sq1", allocationSize = 1)
public class PerfisUsuariosApi extends BaseEntityId<Integer> {

	private static final long serialVersionUID = -2367821415274641201L;
	
	private Integer id;
	private UsuarioApi usuarioApi;
	private PerfilApi perfilApi;
	private Date dataCriacao;

	public PerfisUsuariosApi() {
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="cscPerfUsuApiSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO_API")
	public UsuarioApi getUsuarioApi() {
		return this.usuarioApi;
	}

	public void setUsuarioApi(UsuarioApi usuarioApi) {
		this.usuarioApi = usuarioApi;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PERFIL")
	public PerfilApi getPerfilApi() {
		return this.perfilApi;
	}

	public void setPerfilApi(PerfilApi perfilApi) {
		this.perfilApi = perfilApi;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ATRIBUICAO")
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public enum Fields {
		ID("id"), 
		USUARIO("usuarioApi"),
		PERFIL("perfilApi"),
		DATA_CRIACAO("dataCriacao");
		
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