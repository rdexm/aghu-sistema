package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPtdSq1", sequenceName="AGH.MPT_PTD_SQ1", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_MDTOS_DIA", schema = "AGH")
public class MptProtocoloMedicamentosDia extends BaseEntitySeq<Integer> implements Serializable {
	
	private static final long serialVersionUID = 5224741634779283972L;

	private Integer seq;
	
	private MptProtocoloMedicamentos protocoloMedicamentos;
	
	private String descricao;
	
	private MptVersaoProtocoloSessao versaoProtocoloSessao;
	
	private Short tfqSeq;
	
	private String vadSigla;
	
	private Short tvaSeq;
	
	private Boolean indSeNecessario;
	
	private Boolean indSolucao;
	
	private Short frequencia;
	
	private Short qtdHorasCorrer;
	
	private DominioUnidadeHorasMinutos unidHorasCorrer;
	
	private Boolean indBombaInfusao;
	
	private Boolean indInfusorPortatil;
	
	private String complemento;
	
	private String observacao;
	
	private Boolean indUsoDomiciliar;
	
	private Short diasUsoDomiciliar;
	
	private Date tempo;
	
	private BigDecimal gotejo;
	
	private BigDecimal volumeMl;
	
	private Boolean modificado;
	
	private Short dia;
	
	private Date criadoEm;	
	
	private RapServidores servidor;
	
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPtdSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PTM_SEQ")
	@NotNull
	public MptProtocoloMedicamentos getProtocoloMedicamentos() {
		return protocoloMedicamentos;
	}

	public void setProtocoloMedicamentos(
			MptProtocoloMedicamentos protocoloMedicamentos) {
		this.protocoloMedicamentos = protocoloMedicamentos;
	}

	@Column(name = "DESCRICAO")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPS_SEQ")
	@NotNull
	public MptVersaoProtocoloSessao getVersaoProtocoloSessao() {
		return versaoProtocoloSessao;
	}

	public void setVersaoProtocoloSessao(
			MptVersaoProtocoloSessao versaoProtocoloSessao) {
		this.versaoProtocoloSessao = versaoProtocoloSessao;
	}

	@Column(name = "TFQ_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getTfqSeq() {
		return tfqSeq;
	}

	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}

	@Column(name = "VAD_SIGLA", nullable = false)
	public String getVadSigla() {
		return vadSigla;
	}

	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}

	@Column(name = "TVA_SEQ", precision = 3, scale = 0)
	public Short getTvaSeq() {
		return tvaSeq;
	}

	public void setTvaSeq(Short tvaSeq) {
		this.tvaSeq = tvaSeq;
	}

	@Column(name = "IND_SE_NECESSARIO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSeNecessario() {
		return indSeNecessario;
	}

	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}

	@Column(name = "IND_SOLUCAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSolucao() {
		return indSolucao;
	}

	public void setIndSolucao(Boolean indSolucao) {
		this.indSolucao = indSolucao;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name = "QTDE_HORAS_CORRER", precision = 2, scale = 0)
	public Short getQtdHorasCorrer() {
		return qtdHorasCorrer;
	}
	
	public void setQtdHorasCorrer(Short qtdHorasCorrer) {
		this.qtdHorasCorrer = qtdHorasCorrer;
	}

	@Column(name = "UNID_HORAS_CORRER")
	@Enumerated(EnumType.STRING)
	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}

	@Column(name = "IND_BOMBA_INFUSAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndBombaInfusao() {
		return indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}

	@Column(name = "IND_INFUSOR_PORTATIL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInfusorPortatil() {
		return indInfusorPortatil;
	}

	public void setIndInfusorPortatil(Boolean indInfusorPortatil) {
		this.indInfusorPortatil = indInfusorPortatil;
	}

	@Column(name = "COMPLEMENTO")
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "OBSERVACAO")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_USO_DOMICILIAR", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoDomiciliar() {
		return indUsoDomiciliar;
	}

	public void setIndUsoDomiciliar(Boolean indUsoDomiciliar) {
		this.indUsoDomiciliar = indUsoDomiciliar;
	}

	@Column(name = "DIAS_DE_USO_DOMICILIAR", precision = 2, scale = 0)
	public Short getDiasUsoDomiciliar() {
		return diasUsoDomiciliar;
	}

	public void setDiasUsoDomiciliar(Short diasUsoDomiciliar) {
		this.diasUsoDomiciliar = diasUsoDomiciliar;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO")
	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@Column(name = "GOTEJO", precision = 5, scale = 2)
	public BigDecimal getGotejo() {
		return gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	@Column(name = "VOLUME_ML", precision = 8, scale = 3)
	public BigDecimal getVolumeMl() {
		return volumeMl;
	}

	public void setVolumeMl(BigDecimal volumeMl) {
		this.volumeMl = volumeMl;
	}

	@Column(name = "MODIFICADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getModificado() {
		return modificado;
	}

	public void setModificado(Boolean modificado) {
		this.modificado = modificado;
	}

	@Column(name = "DIA", precision = 3, scale = 0)
	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
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
	
	public enum Fields {		
		SEQ("seq"),
		PROTOCOLO_MEDICAMENTOS("protocoloMedicamentos"),
		PROTOCOLO_MEDICAMENTOS_SEQ("protocoloMedicamentos.seq"),
		VERSAO_PROTOCOLO_SESSAO("versaoProtocoloSessao"),
		DIA("dia"),
		IND_USO_DOMICILIAR("indUsoDomiciliar"),
		MODIFICADO("modificado");
		
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
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.getSeq());
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MptProtocoloMedicamentosDia)) {
			return false;
		}
		MptProtocoloMedicamentosDia other = (MptProtocoloMedicamentosDia) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());	
		return equalsBuilder.isEquals();
	}
}
