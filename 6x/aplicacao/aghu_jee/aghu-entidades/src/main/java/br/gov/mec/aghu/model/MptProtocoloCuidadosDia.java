package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPcdSeq", sequenceName="AGH.MPT_PCD_SEQ", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_CUIDADOS_DIA", schema = "AGH")
public class MptProtocoloCuidadosDia extends BaseEntitySeq<Integer> implements Serializable {
	
	private static final long serialVersionUID = -4418678817477025697L;

	private Integer seq;
	
	private MptProtocoloCuidados protocoloCuidados;
	
	private MptVersaoProtocoloSessao versaoProtocoloSessao;
	
	private Short tfqSeq;
	
	private Integer cduSeq;
	
	private Integer frequencia;
	
	private String complemento;
	
	private Date tempo;
	
	private Short dia;
	
	private Boolean modificado;
	
	private RapServidores servidor;
		
	private Date criadoEm;
	
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPcdSeq")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCU_SEQ")
	@NotNull
	public MptProtocoloCuidados getProtocoloCuidados() {
		return protocoloCuidados;
	}

	public void setProtocoloCuidados(MptProtocoloCuidados protocoloCuidados) {
		this.protocoloCuidados = protocoloCuidados;
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

	@Column(name = "CDU_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getCduSeq() {
		return cduSeq;
	}

	public void setCduSeq(Integer cduSeq) {
		this.cduSeq = cduSeq;
	}

	@Column(name = "FREQUENCIA", precision = 5, scale = 0)
	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name = "COMPLEMENTO")
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO")
	public Date getTempo() {
		return tempo;
	}
	
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@Column(name = "DIA", precision = 3, scale = 0)
	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	@Column(name = "MODIFICADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getModificado() {
		return modificado;
	}

	public void setModificado(Boolean modificado) {
		this.modificado = modificado;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		PROTOCOLO_CUIDADOS("protocoloCuidados"),
		PROTOCOLO_CUIDADOS_SEQ("protocoloCuidados.seq"),
		VERSAO_PROTOCOLO_SESSAO("versaoProtocoloSessao"),		
		DIA("dia"),
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
		if (!(obj instanceof MptProtocoloCuidadosDia)) {
			return false;
		}
		MptProtocoloCuidadosDia other = (MptProtocoloCuidadosDia) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());	
		return equalsBuilder.isEquals();
	}
}
