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
@Table(name = "AEL_EXTRATO_ITEM_SOLICS", schema = "AGH")
@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class AelExtratoItemSolicitacao extends BaseEntityId<AelExtratoItemSolicitacaoId> implements java.io.Serializable, IAelExtratoItemSolicitacao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676232562993579969L;
	private AelExtratoItemSolicitacaoId id;
	private AelSitItemSolicitacoes aelSitItemSolicitacoes;
	private Date dataHoraEvento;
	private Date criadoEm;
	private RapServidores servidor;
	private AelMotivoCancelaExames aelMotivoCancelaExames;
	private RapServidores servidorEhResponsabilide;
	private String complementoMotCanc;
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	
	public AelExtratoItemSolicitacao() {
	}

	public AelExtratoItemSolicitacao(AelExtratoItemSolicitacaoId id,
			AelSitItemSolicitacoes aelSitItemSolicitacoes, Date dataHoraEvento, Date criadoEm,
			RapServidores servidor, RapServidores servidorEhResponsabilide) {
		this.id = id;
		this.aelSitItemSolicitacoes = aelSitItemSolicitacoes;
		this.dataHoraEvento = dataHoraEvento;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.servidorEhResponsabilide = servidorEhResponsabilide;
	}

	public AelExtratoItemSolicitacao(AelExtratoItemSolicitacaoId id, AelSitItemSolicitacoes aelSitItemSolicitacoes,
			Date dataHoraEvento, Date criadoEm, AelMotivoCancelaExames aelMotivoCancelaExames,
			Integer serMatriculaEhResponsabilid, String complementoMotCanc,
			RapServidores servidor, RapServidores servidorEhResponsabilide) {
		this.id = id;
		this.aelSitItemSolicitacoes = aelSitItemSolicitacoes;
		this.dataHoraEvento = dataHoraEvento;
		this.criadoEm = criadoEm;
		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
		this.complementoMotCanc = complementoMotCanc;
		this.servidor = servidor;
		this.servidorEhResponsabilide = servidorEhResponsabilide;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public AelExtratoItemSolicitacaoId getId() {
		return this.id;
	}

	public void setId(AelExtratoItemSolicitacaoId id) {
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
	@Column(name = "DTHR_EVENTO", nullable = false)
	public Date getDataHoraEvento() {
		return this.dataHoraEvento;
	}

	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
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


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MOC_SEQ")
	public AelMotivoCancelaExames getAelMotivoCancelaExames() {
		return this.aelMotivoCancelaExames;
	}

	public void setAelMotivoCancelaExames(AelMotivoCancelaExames aelMotivoCancelaExames) {
		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
	}

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
	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
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
		AelExtratoItemSolicitacao other = (AelExtratoItemSolicitacao) obj;
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
		DTHR_EVENTO("dataHoraEvento"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		MOC_SEQ("aelMotivoCancelaExames.seq"),
		AEL_MOTIVO_CANCELAR_EXAMES("aelMotivoCancelaExames"),
		SERVIDOR_EH_RESPONSABILIDE("servidorEhResponsabilide"),
		SERVIDOR_EH_RESPONSABILIDE_MATRICULA("servidorEhResponsabilide.id.matricula"),
		SERVIDOR_EH_RESPONSABILIDE_VIN_CODIGO("servidorEhResponsabilide.id.vinCodigo"),
		COMPLEMENTO_MOT_CANC("complementoMotCanc"),
		ITEM_SOLICITACAO_EXAME("itemSolicitacaoExame"),
		ISE_SOE_SEQ("itemSolicitacaoExame.id.soeSeq"),
		ISE_SEQP("itemSolicitacaoExame.id.seqp"),
		SEQP("id.seqp"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		MATRICULA("servidor.id.matricula")
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
