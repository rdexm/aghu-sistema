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

import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxExtSq1", sequenceName="AGH.MTX_EXT_SQ1", allocationSize = 1)
@Table(name = "MTX_EXTRATO_TRANSPLANTES", schema = "AGH")
public class MtxExtratoTransplantes extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 4837282085826340615L;
	
	private Integer seq;
	private MtxTransplantes mtxTransplante;
	private Date dataOcorrencia;
	private DominioSituacaoTransplante situacaoTransplante;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private MtxMotivoAlteraSituacao motivoAlteraSituacao;
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxExtSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRP_SEQ", nullable = false)
	public MtxTransplantes getMtxTransplante() {
		return mtxTransplante;
	}
	public void setMtxTransplante(MtxTransplantes mtxTransplante) {
		this.mtxTransplante = mtxTransplante;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_OCORRENCIA", nullable = false)
	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}
	
	@Column(name = "SITUACAO_TRANSPLANTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoTransplante getSituacaoTransplante() {
		return situacaoTransplante;
	}
	public void setSituacaoTransplante(
			DominioSituacaoTransplante situacaoTransplante) {
		this.situacaoTransplante = situacaoTransplante;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAS_SEQ")
	public MtxMotivoAlteraSituacao getMotivoAlteraSituacao() {
		return motivoAlteraSituacao;
	}
	public void setMotivoAlteraSituacao(MtxMotivoAlteraSituacao motivoAlteraSituacao) {
		this.motivoAlteraSituacao = motivoAlteraSituacao;
	}

	public enum Fields {
		
		SEQ("seq"),
		MTX_TRANSPLANTE("mtxTransplante"),
		MTX_TRANSPLANTE_SEQ("mtxTransplante.seq"),
		DATA_OCORRENCIA("dataOcorrencia"),
		SITUACAO_TRANSPLANTE("situacaoTransplante"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SER_MATRICULA("servidor.id.matricula"),
		MTX_MOTIVO_ALTERA_SITUACAO("motivoAlteraSituacao"),
		MTX_MOTIVO_ALTERA_SITUACAO_SEQ("motivoAlteraSituacao.seq"),
		TRP_SEQ("mtxTransplante");

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
