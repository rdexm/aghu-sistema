package br.gov.mec.aghu.model;

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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import br.gov.mec.aghu.core.lucene.Fonetizador;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MAM_ORIGEM_PACIENTE", schema = "AGH")
@SequenceGenerator(name = "aghMamOrPacSeq", sequenceName = "AGH.mam_orp_sq1", allocationSize = 1)
@Indexed
public class MamOrigemPaciente extends BaseEntitySeq<Integer> implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6459246481569042594L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serVincCodigoInclusao;
	private Integer serMatriculaInclusao;
	private Integer serVincCodigoAlteracao;
	private Integer serMatriculaAlteracao;
	private Integer version;

	public MamOrigemPaciente() {
		super();
	}



	public MamOrigemPaciente(String descricao,
			DominioSituacao indSituacao, Date criadoEm,
			Integer serVincCodigoInclusao, Integer serMatriculaInclusao) {
		super();
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.serVincCodigoInclusao = serVincCodigoInclusao;
		this.serMatriculaInclusao = serMatriculaInclusao;
	}



	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMamOrPacSeq")
	@Column(name = "SEQ", nullable = false, length = 5)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Transient
	@Field(index = Index.YES, store = Store.YES)
	public String getDescricaoFonetica() {
		return Fonetizador.fonetizar(descricao);
	}
	
	@Transient
	@Field(index = Index.NO , store = Store.YES)
	public String getDescricaoOrdenacao(){
		return this.descricao;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	@NotNull
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_VIN_CODIGO_INCLUSAO", nullable = false, length = 3)
	public Integer getSerVincCodigoInclusao() {
		return serVincCodigoInclusao;
	}

	public void setSerVincCodigoInclusao(Integer serVincCodigoInclusao) {
		this.serVincCodigoInclusao = serVincCodigoInclusao;
	}

	@Column(name = "SER_MATRICULA_INCLUSAO", nullable = false, length = 7)
	public Integer getSerMatriculaInclusao() {
		return serMatriculaInclusao;
	}

	public void setSerMatriculaInclusao(Integer serMatriculaInclusao) {
		this.serMatriculaInclusao = serMatriculaInclusao;
	}

	@Column(name = "SER_VIN_CODIGO_ALTERACAO", length = 3)
	public Integer getSerVincCodigoAlteracao() {
		return serVincCodigoAlteracao;
	}

	public void setSerVincCodigoAlteracao(Integer serVincCodigoAlteracao) {
		this.serVincCodigoAlteracao = serVincCodigoAlteracao;
	}

	@Column(name = "SER_MATRICULA_ALTERACAO", length = 7)
	public Integer getSerMatriculaAlteracao() {
		return serMatriculaAlteracao;
	}

	public void setSerMatriculaAlteracao(Integer serMatriculaAlteracao) {
		this.serMatriculaAlteracao = serMatriculaAlteracao;
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
		SEQ("seq"), VERSION("version"), IND_SITUACAO("indSituacao"), DESCRICAO(
				"descricao"), DESCRICAO_FONETICA("descricaoFonetica"), DESCRICAO_ORDENACAO("descricaoOrdenacao"),
				CRIADO_EM("criadoEm"), ALTERADO_EM("AlteradoEm"), SER_MATRICULA_INCLUSAO(
				"serMatriculaInclusao"), SER_MATRICULA_ALTERACAO(
				"serMatriculaAlteracao"), SER_VIC_CODIGO_INCLUSAO(
				"serVicCodigoInclusao"), SER_VIC_CODIGO_ALTERACAO(
				"serVincCodigoAlteracao");
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
