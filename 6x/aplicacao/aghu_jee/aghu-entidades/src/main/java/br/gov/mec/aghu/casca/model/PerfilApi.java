package br.gov.mec.aghu.casca.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "CSC_PERFIL_API", schema = "CASCA")
@SequenceGenerator(name="cscPerfApiSeq", sequenceName="casca.csc_perfil_api_sq1", allocationSize = 1)
public class PerfilApi extends BaseEntityId<Integer> {

	private static final long serialVersionUID = 1232433426625732429L;

	private Integer id;
	private String nome;
	private String descricao;
	private String descricaoResumida;
	private Date dataCriacao;
	private DominioSituacao situacao;
	private Integer version;
	
	public PerfilApi() {
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="cscPerfApiSeq")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "NOME", nullable = false)	
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO")	
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DESCRICAO_RESUMIDA")	
	public String getDescricaoResumida() {
		return this.descricaoResumida;
	}

	public void setDescricaoResumida(String descricaoResumida) {
		this.descricaoResumida = descricaoResumida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
		
	@Column(name = "SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
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
		ID("id"), NOME("nome"), DESCRICAO("descricao"), DESCRICAO_RESUMIDA(
				"descricaoResumida"), DATA_CRIACAO("dataCriacao"), SITUACAO(
				"situacao");
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