package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptVpsSq1", sequenceName="AGH.MPT_VPS_SQ1", allocationSize = 1)
@Table(name = "MPT_VERSAO_PROTOCOLO_SESSAO", schema = "AGH")

public class MptVersaoProtocoloSessao extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 7136877534967025517L;
	
	private Integer seq;
	private Integer versao;
	private DominioSituacaoProtocolo indSituacao;
	private String justificativa;
	private MptProtocoloSessao protocoloSessao;
	private Date criadoEm;
	private RapServidores servidor;
	private RapServidores servidorResponsavel;	
	private Integer version;
	private Integer seqProtocoloSessao;
	private Set<MptProtocoloMedicamentos> protocoloMedicamentos;
	private Short diasTratamento;
	private Integer qtdCiclos;
	
	public MptVersaoProtocoloSessao() {
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptVpsSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "VERSAO", nullable = false, length = 3)
	public Integer getVersao() {
		return versao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PSE_SEQ", nullable = false)
	public MptProtocoloSessao getProtocoloSessao() {
		return protocoloSessao;
	}

	public void setProtocoloSessao(MptProtocoloSessao protocoloSessao) {
		this.protocoloSessao = protocoloSessao;
	}
	
	@Column(name="PSE_SEQ", insertable=false, updatable=false)
	public Integer getSeqProtocoloSessao() {
		return seqProtocoloSessao;
	}

	public void setSeqProtocoloSessao(Integer seqProtocoloSessao) {
		this.seqProtocoloSessao = seqProtocoloSessao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoProtocolo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoProtocolo indSituacao) {
		this.indSituacao = indSituacao;
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
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "JUSTIF_HOMOLOG_COMEDI", nullable = false, length = 500)
	@Length(max = 500)
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "versaoProtocoloSessao")
	public Set<MptProtocoloMedicamentos> getProtocoloMedicamentos() {
		return protocoloMedicamentos;
	}

	public void setProtocoloMedicamentos(
			Set<MptProtocoloMedicamentos> protocoloMedicamentos) {
		this.protocoloMedicamentos = protocoloMedicamentos;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_RESP", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO_RESP", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}
	
	@Column(name = "DIAS_TRATAMENTO")
	public Short getDiasTratamento() {
		return diasTratamento;
	}

	public void setDiasTratamento(Short diasTratamento) {
		this.diasTratamento = diasTratamento;
	}
	
	@Column(name = "QTD_CICLOS")
	public Integer getQtdCiclos() {
		return qtdCiclos;
	}

	public void setQtdCiclos(Integer qtdCiclos) {
		this.qtdCiclos = qtdCiclos;
	}

	public enum Fields {
		
		SEQ("seq"),
		PROTOCOLO_SESSAO("protocoloSessao"),
		SEQ_PROTOCOLO_SESSAO("seqProtocoloSessao"),
		IND_SITUACAO("indSituacao"),
		VERSAO("versao"),
		PROTOCOLO_MEDICAMENTOS("protocoloMedicamentos"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		DIAS_TRATAMENTO("diasTratamento"),
		QTD_CICLOS("qtdCiclos");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		MptVersaoProtocoloSessao other = (MptVersaoProtocoloSessao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
