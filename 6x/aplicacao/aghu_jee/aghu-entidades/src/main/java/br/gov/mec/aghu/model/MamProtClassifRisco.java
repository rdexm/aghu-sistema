package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "MAM_PROT_CLASSIF_RISCO", schema = "AGH")
@SequenceGenerator(name="aghMamPcrSeq", sequenceName="AGH.MAM_PCR_SEQ", allocationSize = 1)
public class MamProtClassifRisco extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 5660024045181842297L;
	private Integer seq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private DominioSituacao indBloqueado;
	private DominioSituacao indPermiteChecagem;
	private Integer version;
	
	private List<MamUnidAtendem> unidadesAtendimento; 
		
	public MamProtClassifRisco() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMamPcrSeq")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)	
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "IND_BLOQUEADO", length = 1)	
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndBloqueado() {
		return this.indBloqueado;
	}

	public void setIndBloqueado(DominioSituacao indBloqueado) {
		this.indBloqueado = indBloqueado;
	}
	
	@Column(name = "IND_PERMITE_CHECAGEM", length = 1)	
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndPermiteChecagem() {
		return this.indPermiteChecagem;
	}

	public void setIndPermiteChecagem(DominioSituacao indPermiteChecagem) {
		this.indPermiteChecagem = indPermiteChecagem;
	}
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamProtClassifRisco")
	public List<MamUnidAtendem> getUnidadesAtendimento() {
		return unidadesAtendimento;
	}

	public void setUnidadesAtendimento(List<MamUnidAtendem> unidadesAtendimento) {
		this.unidadesAtendimento = unidadesAtendimento;
	}



	public enum Fields {
		SEQ("seq"),
		VERSION("version"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_RECEPCAO("indRecepcao"),
		IND_BLOQUEADO("indBloqueado"),
		MAM_UNID_ATEND("unidadesAtendimento");
		
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
