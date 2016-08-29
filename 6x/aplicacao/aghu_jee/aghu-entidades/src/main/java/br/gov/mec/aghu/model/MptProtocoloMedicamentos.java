package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPtmSq1", sequenceName="AGH.MPT_PTM_SQ1", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_MDTOS", schema = "AGH")
public class MptProtocoloMedicamentos extends BaseEntitySeq<Long> implements Serializable {
	
	private static final long serialVersionUID = -6274967888865476226L;
		
	private Long seq;
	
	private MptVersaoProtocoloSessao versaoProtocoloSessao;
	
	private Set<MptProtocoloItemMedicamentos> protocoloItemMedicamentos;
	
	private String descricao;
	private MpmTipoFrequenciaAprazamento tfqSeq;
	private AfaViaAdministracao vadSigla;
	private Short tvaSeq;
	private Boolean indSeNecessario;
	private Boolean indSolucao;
	private Short frequencia;
	private Short qtdeHorasCorrer;
	private DominioUnidadeHorasMinutos unidHorasCorrer;
	private Boolean indBombaInfusao;
	private Boolean indInfusorPortatil;
	private String complemento;
	private String observacao;
	private Boolean indDomiciliar;
	private Short diasDeUsoDomiciliar;
	private Date tempo;
	private BigDecimal gotejo;
	private BigDecimal volumeMl;
	private Boolean modificado;
	private Short ordem;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private Boolean indNaoPermiteAlteracao;
	private AfaTipoVelocAdministracoes tipoVelocAdministracoes;
	private Integer vpsSeq;
	private Integer medMatCodigoDiluente;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPtmSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPS_SEQ", nullable = false)
	public MptVersaoProtocoloSessao getVersaoProtocoloSessao() {
		return versaoProtocoloSessao;
	}

	public void setVersaoProtocoloSessao(MptVersaoProtocoloSessao versaoProtocoloSessao) {
		this.versaoProtocoloSessao = versaoProtocoloSessao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "protocoloMedicamentos")
	public Set<MptProtocoloItemMedicamentos> getProtocoloItemMedicamentos() {
		return protocoloItemMedicamentos;
	}

	public void setProtocoloItemMedicamentos(Set<MptProtocoloItemMedicamentos> protocoloItemMedicamentos) {
		this.protocoloItemMedicamentos = protocoloItemMedicamentos;
	}
	
	@Column(name = "DESCRICAO", length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getTfqSeq() {
		return tfqSeq;
	}

	public void setTfqSeq(MpmTipoFrequenciaAprazamento tfqSeq) {
		this.tfqSeq = tfqSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA")
	public AfaViaAdministracao getVadSigla() {
		return vadSigla;
	}

	public void setVadSigla(AfaViaAdministracao vadSigla) {
		this.vadSigla = vadSigla;
	}

	@Column(name = "TVA_SEQ", insertable = false, updatable = false)
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

	@Column(name = "FREQUENCIA")
	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name = "QTDE_HORAS_CORRER")
	public Short getQtdeHorasCorrer() {
		return qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Short qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
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

	@Column(name = "COMPLEMENTO", length = 100)	
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "OBSERVACAO", length = 240)	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_USO_DOMICILIAR", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDomiciliar() {
		return indDomiciliar;
	}

	public void setIndDomiciliar(Boolean indDomiciliar) {
		this.indDomiciliar = indDomiciliar;
	}

	@Column(name = "DIAS_DE_USO_DOMICILIAR")
	public Short getDiasDeUsoDomiciliar() {
		return diasDeUsoDomiciliar;
	}

	public void setDiasDeUsoDomiciliar(Short diasDeUsoDomiciliar) {
		this.diasDeUsoDomiciliar = diasDeUsoDomiciliar;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO")
	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@Column(name = "GOTEJO")
	public BigDecimal getGotejo() {
		return gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	@Column(name = "VOLUME_ML")
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

	@Column(name = "ORDEM")
	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
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
	public RapServidores getServidor() {
		return this.servidor;
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

	@Column(name = "IND_NAO_PERMITE_ALTERACAO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndNaoPermiteAlteracao() {
		return indNaoPermiteAlteracao;
	}

	public void setIndNaoPermiteAlteracao(Boolean indNaoPermiteAlteracao) {
		this.indNaoPermiteAlteracao = indNaoPermiteAlteracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVA_SEQ")
	public AfaTipoVelocAdministracoes getTipoVelocAdministracoes() {
		return tipoVelocAdministracoes;
	}

	public void setTipoVelocAdministracoes(AfaTipoVelocAdministracoes tipoVelocAdministracoes) {
		this.tipoVelocAdministracoes = tipoVelocAdministracoes;
	}
	
	@Column(name = "VPS_SEQ", insertable = false, updatable = false)
	public Integer getVpsSeq() {
		return vpsSeq;
	}

	public void setVpsSeq(Integer vpsSeq) {
		this.vpsSeq = vpsSeq;
	}
		
	@Column(name = "MED_MAT_CODIGO_DILUENTE")
	public Integer getMedMatCodigoDiluente() {
		return medMatCodigoDiluente;
	}

	public void setMedMatCodigoDiluente(Integer medMatCodigoDiluente) {
		this.medMatCodigoDiluente = medMatCodigoDiluente;
	}





	public enum Fields {		
		SEQ("seq"),
		PROTOCOLO_ITEM_MEDICAMENTOS("protocoloItemMedicamentos"),
		VERSAO_PROTOCOLO_SESSAO("versaoProtocoloSessao"),
		DESCRICAO("descricao"),
		TFQ_SEQ("tfqSeq"),
		VAD_SIGLA("vadSigla"),
		TVA_SEQ("tvaSeq"),
		TIPO_VELOC_ADMINISTRACOES_SEQ("tvaSeq.seq"),
		IND_SE_NECESSARIO("indSeNecessario"),
		IND_SOLUCAO("indSolucao"),
		FREQUENCIA("frequencia"),
		QTDE_HORAS_CORRER("qtdeHorasCorrer"),
		UNID_HORAS_CORRER("unidHorasCorrer"),
		IND_BOMBA_INFUSAO("indBombaInfusao"),
		IND_INFUSOR_PORTATIL("indInfusorPortatil"),
		COMPLEMENTO("complemento"),
		OBSERVACAO("observacao"),
		IND_DOMICILIAR("indDomiciliar"),
		DIAS_DE_USO_DOMICILIAR("diasDeUsoDomiciliar"),
		TEMPO("tempo"),
		GOTEJO("gotejo"),
		VOLUME_ML("volumeMl"),
		MODIFICADO("modificado"),
		ORDEM("ordem"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		TIPO_FREQUENCIA_APRAZAMENTO("tfqSeq"),
		VIA_ADMINISTRACAO("vadSigla"),
		IND_NAO_PERMITE_ALTERACAO("indNaoPermiteAlteracao"),
		TIPO_VELOC_ADMINISTRACOES("tipoVelocAdministracoes"),
		VPS_SEQ("vpsSeq"),
		MED_MAT_CODIGO_DILUENTE("medMatCodigoDiluente");
		
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MptProtocoloMedicamentos other = (MptProtocoloMedicamentos) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());
		return equalsBuilder.isEquals();
	}

}
