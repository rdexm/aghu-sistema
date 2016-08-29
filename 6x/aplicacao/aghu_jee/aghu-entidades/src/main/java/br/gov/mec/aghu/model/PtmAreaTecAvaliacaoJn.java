package br.gov.mec.aghu.model;

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
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name="PTM_AREA_TEC_AVALIACAO_JN", schema = "AGH")
@SequenceGenerator(name="ptmAreaTecAvaliacaoJnSeq", sequenceName="AGH.PTM_AREA_TEC_AVALIACAO_JN_SEQ", allocationSize = 1)
@Immutable
public class PtmAreaTecAvaliacaoJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6534598425972669347L;
	
	/* ATUALIZADOR JOURNALS - PROPERTIES	
	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private String nomeAreaTecAvaliacao;
	private FccCentroCustos fccCentroCustos;
	private RapServidores servidorCC;
	private DominioSituacao situacao;
	private String mensagem;
	private RapServidores servidor;
	private Integer version;
	private Boolean indEmailSumarizado;
	
	public PtmAreaTecAvaliacaoJn() {
	}
	
	public PtmAreaTecAvaliacaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
		/* ATUALIZADOR JOURNALS - contrutor	
		this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public PtmAreaTecAvaliacaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, String nomeAreaTecAvaliacao,
			FccCentroCustos fccCentroCustos, RapServidores servidorCC,
			DominioSituacao situacao, String mensagem, RapServidores servidor,
			Integer version) {
		/* ATUALIZADOR JOURNALS - contrutor	
		this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
		this.fccCentroCustos = fccCentroCustos;
		this.servidorCC = servidorCC;
		this.situacao = situacao;
		this.mensagem = mensagem;
		this.servidor = servidor;
		this.version = version;
	}
	
	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmAreaTecAvaliacaoJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	// ATUALIZADOR JOURNALS - ID
		
	/* ATUALIZADOR JOURNALS - Get / Set	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	@Length(max = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@Length(max = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME_AREA_TEC_AVALIACAO", length = 50)
	@Length(max = 50)
	public String getNomeAreaTecAvaliacao() {
		return nomeAreaTecAvaliacao;
	}

	public void setNomeAreaTecAvaliacao(String nomeAreaTecAvaliacao) {
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COD_CENTRO_CUSTO", referencedColumnName = "CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_CC", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CC", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCC() {
		return servidorCC;
	}

	public void setServidorCC(RapServidores servidorCC) {
		this.servidorCC = servidorCC;
	}

	@Column(name = "SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "MENSAGEM", length = 2000)
	@Length(max = 2000)
	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "IND_EMAIL_SUMARIZADO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEmailSumarizado() {
		return indEmailSumarizado;
	}

	public void setIndEmailSumarizado(Boolean indEmailSumarizado) {
		this.indEmailSumarizado = indEmailSumarizado;
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

		/* ATUALIZADOR JOURNALS - Fields	
		SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		NOME_AREA_TEC_AVALIACAO("nomeAreaTecAvaliacao"),
		FCC_CENTRO_CUSTOS("fccCentroCustos"),
		SERVIDOR_CC("servidorCC"),
		SITUACAO("situacao"),
		MENSAGEM("mensagem"),
		SERVIDOR("servidor"),
		VERSION("version");

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
