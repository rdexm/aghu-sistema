package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "ael_extrato_item_solics", schema = "hist")
public class AelExtratoItemSolicHist extends BaseEntityId<AelExtratoItemSolicHistId> implements java.io.Serializable, IAelExtratoItemSolicitacao {

	private static final long serialVersionUID = 4676232562993579969L;
	private AelExtratoItemSolicHistId id;
	private AelSitItemSolicitacoes aelSitItemSolicitacoes;
	private Date dataHoraEvento;
	private Date criadoEm;
	private RapServidores servidor;
//	private AelMotivoCancelaExamesHist aelMotivoCancelaExames;
	private RapServidores servidorEhResponsabilide;
	private String complementoMotCanc;
	private AelItemSolicExameHist itemSolicitacaoExame;
	
	private Short mocSeq;
	
	public AelExtratoItemSolicHist() {
	}

	public AelExtratoItemSolicHist(AelExtratoItemSolicHistId id, AelSitItemSolicitacoes aelSitItemSolicitacoes, Date dataHoraEvento, Date criadoEm, String complementoMotCanc, AelItemSolicExameHist itemSolicitacaoExame) {
		super();
		this.id = id;
		this.aelSitItemSolicitacoes = aelSitItemSolicitacoes;
		this.dataHoraEvento = dataHoraEvento;
		this.criadoEm = criadoEm;
		this.complementoMotCanc = complementoMotCanc;
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}



	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public AelExtratoItemSolicHistId getId() {
		return this.id;
	}

	public void setId(AelExtratoItemSolicHistId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIT_CODIGO", nullable = false)
	public AelSitItemSolicitacoes getAelSitItemSolicitacoes() {
		return aelSitItemSolicitacoes;
	}

	public void setAelSitItemSolicitacoes(AelSitItemSolicitacoes aelSitItemSolicitacoes) {
		this.aelSitItemSolicitacoes = aelSitItemSolicitacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_EVENTO", nullable = false, length = 7)
	public Date getDataHoraEvento() {
		return this.dataHoraEvento;
	}

	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "MOC_SEQ")
//	public AelMotivoCancelaExamesHist getAelMotivoCancelaExames() {
//		return this.aelMotivoCancelaExames;
//	}
//
//	public void setAelMotivoCancelaExames(AelMotivoCancelaExamesHist aelMotivoCancelaExames) {
//		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
//	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_EH_RESPONSABILID", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EH_RESPONSABILI", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEhResponsabilide() {
		return servidorEhResponsabilide;
	}

	public void setServidorEhResponsabilide(RapServidores servidorEhResponsabilide) {
		this.servidorEhResponsabilide = servidorEhResponsabilide;
	}
	
	@Column(name = "COMPLEMENTO_MOT_CANC", length = 2000)
	@Length(max = 2000)
	public String getComplementoMotCanc() {
		return this.complementoMotCanc;
	}

	public void setComplementoMotCanc(String complementoMotCanc) {
		this.complementoMotCanc = complementoMotCanc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelItemSolicExameHist getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(
			AelItemSolicExameHist itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AelExtratoItemSolicHist other = (AelExtratoItemSolicHist) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {

		ID("id"),
		SITUACAO_ITEM_SOLICITACAO("aelSitItemSolicitacoes"),
		SITUACAO_ITEM_SOLICITACAO_CODIGO("aelSitItemSolicitacoes.codigo"),
		DATA_HORA_EVENTO("dataHoraEvento"),
		CRIADO_EM("criadoEm"),
		COMPLEMENTO_MOT_CANC("complementoMotCanc"),
		ITEM_SOLICITACAO_EXAME("itemSolicitacaoExame"),
		ISE_SOE_SEQ("itemSolicitacaoExame.id.soeSeq"),
		ISE_SEQP("itemSolicitacaoExame.id.seqp"),
		SEQP("id.seqp");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	
//	@Column(name = "ser_matricula")
//	public Integer getSerMatricula() {
//		return this.serMatricula;
//	}
//
//	public void setSerMatricula(Integer serMatricula) {
//		this.serMatricula = serMatricula;
//	}
//
//	@Column(name = "ser_vin_codigo")
//	public Short getSerVinCodigo() {
//		return this.serVinCodigo;
//	}
//
//	public void setSerVinCodigo(Short serVinCodigo) {
//		this.serVinCodigo = serVinCodigo;
//	}

	@Column(name = "moc_seq")
	public Short getMocSeq() {
		return this.mocSeq;
	}

	public void setMocSeq(Short mocSeq) {
		this.mocSeq = mocSeq;
	}

//	@Column(name = "ser_matricula_eh_responsabilid")
//	public Integer getSerMatriculaEhResponsabilid() {
//		return this.serMatriculaEhResponsabilid;
//	}
//
//	public void setSerMatriculaEhResponsabilid(
//			Integer serMatriculaEhResponsabilid) {
//		this.serMatriculaEhResponsabilid = serMatriculaEhResponsabilid;
//	}
//
//	@Column(name = "ser_vin_codigo_eh_responsabili")
//	public Short getSerVinCodigoEhResponsabili() {
//		return this.serVinCodigoEhResponsabili;
//	}
//
//	public void setSerVinCodigoEhResponsabili(Short serVinCodigoEhResponsabili) {
//		this.serVinCodigoEhResponsabili = serVinCodigoEhResponsabili;
//	}

}
