package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mtx_criterio_priorizacoes_tmo database table.
 * 
 */
@Entity
@SequenceGenerator(name="MTX_CRITERIO_PRIORIZACOES_TMO_SEQ_GENERATOR", sequenceName="AGH.MTX_CPT_SQ1", allocationSize = 1)
@Table(name="mtx_criterio_priorizacoes_tmo", schema = "AGH")
public class MtxCriterioPriorizacaoTmo extends BaseEntitySeq<Integer> implements Serializable {


	private static final long serialVersionUID = 5978750282345106800L;
	private Integer seq;
//	private AghCid cidSeq;
	private Date criadoEm;
	private Integer criticidade;
	private Integer	gravidade;
	private DominioSituacao indSituacao;
	private Integer version;
	private RapServidores servidor;
	private DominioSituacaoTmo tipoTmo;
	private String status;
	
	public MtxCriterioPriorizacaoTmo() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MTX_CRITERIO_PRIORIZACOES_TMO_SEQ_GENERATOR")	
	@Column(name="SEQ", unique=true, nullable=false )
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="CID_SEQ", referencedColumnName = "SEQ", nullable = false)
//	public AghCid getCidSeq() {
//		return this.cidSeq;
//	}

//	public void setCidSeq(AghCid cidSeq) {
//		this.cidSeq = cidSeq;
//	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name="CRITICIDADE", nullable = false)
	public Integer getCriticidade() {
		return this.criticidade;
	}

	public void setCriticidade(Integer criticidade) {
		this.criticidade = criticidade;
	}

	@Column(name="GRAVIDADE", nullable = false)
	public Integer getGravidade() {
		return this.gravidade;
	}

	public void setGravidade(Integer gravidade) {
		this.gravidade = gravidade;
	}


	@Column(name="IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Version
	@Column(name="VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="IND_TIPO_TMO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}


	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}
	
	@Column(name="DESCRICAO", nullable = false, length = 200)
	@Length(max = 200)
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public enum Fields{
		SEQ("seq"),
//		CID("cidSeq"),
//		CID_SEQ("cidSeq.seq"),
		CRIADO_EM("criadoEm"),
		CRITICIDADE("criticidade"),
		GRAVIDADE("gravidade"),
		IND_SITUACAO("indSituacao"),
		SERVIDOR("servidor"),
		TIPO_TMO("tipoTmo"),
		STATUS_TMO("status");
		
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