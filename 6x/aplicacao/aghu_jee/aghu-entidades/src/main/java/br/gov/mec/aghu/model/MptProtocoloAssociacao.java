package br.gov.mec.aghu.model;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPraSeq", sequenceName="AGH.MPT_PRA_SEQ", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_ASSOCIACAO", schema = "AGH")
public class MptProtocoloAssociacao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -6618655970716955897L;
	
	private Integer seq;
	
	private MptProtocoloSessao protocoloSessao;
	
	private Integer agrupador;
	
	private Date criadoEm;
	
	private RapServidores servidor;
	
	private Integer version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPraSeq")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}	
	
	@ManyToOne
	@JoinColumn(name = "PSE_SEQ", nullable = false)
	public MptProtocoloSessao getProtocoloSessao() {
		return protocoloSessao;
	}

	public void setProtocoloSessao(MptProtocoloSessao protocoloSessao) {
		this.protocoloSessao = protocoloSessao;
	}
	
	@Column(name = "AGRUPADOR")
	public Integer getAgrupador() {
		return agrupador;
	}

	public void setAgrupador(Integer agrupador) {
		this.agrupador = agrupador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
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

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "version", nullable = false)	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		SEQ_PROTOCOLO_SESSAO("protocoloSessao.seq"),
		PROTOCOLO_SESSAO("protocoloSessao"),
		AGRUPADOR("agrupador");
		
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
		MptProtocoloAssociacao other = (MptProtocoloAssociacao) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());	
		return equalsBuilder.isEquals();
	}
}
