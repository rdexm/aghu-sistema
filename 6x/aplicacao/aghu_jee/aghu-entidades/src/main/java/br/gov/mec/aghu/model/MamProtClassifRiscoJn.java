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
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mamPcrJnSq1", sequenceName="AGH.MAM_PCR_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_PROT_CLASSIF_RISCO_JN", schema = "AGH")
public class MamProtClassifRiscoJn extends BaseJournal {


	private static final long serialVersionUID = 5660024045081842297L;
	private Integer seq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private DominioSituacao indBloqueado;
	private DominioSituacao indPermiteChecagem;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamPcrJnSq1")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}	
	
	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
		
	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "IND_BLOQUEADO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndBloqueado() {
		return indBloqueado;
	}

	public void setIndBloqueado(DominioSituacao indBloqueado) {
		this.indBloqueado = indBloqueado;
	}

	@Column(name = "IND_PERMITE_CHECAGEM", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndPermiteChecagem() {
		return indPermiteChecagem;
	}

	public void setIndPermiteChecagem(DominioSituacao indPermiteChecagem) {
		this.indPermiteChecagem = indPermiteChecagem;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public enum Fields {
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		IND_SITUACAO("indSituacao"),
		IND_BLOQUEADO("indBloqueado"),
		IND_PERMITE_CHECAGEM("indPermiteChecagem");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
