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
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mpt_protocolo_cuidados database table.
 * 
 */
@Entity
@SequenceGenerator(name="mptPcuSeq", sequenceName="AGH.MPT_PCU_SEQ", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_CUIDADOS", schema = "AGH")
public class MptProtocoloCuidados extends BaseEntitySeq<Integer> implements java.io.Serializable {	
	
	private static final long serialVersionUID = -6840340579120983049L;
	
	private Integer seq;	
	private MpmCuidadoUsual cuidadoUsual;
	private String complemento;	
	private Date criadoEm;
	private Integer frequencia;
	private Short ordem;	
	private RapServidores rapServidores;
	private Date tempo;	
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private Integer version;	
	private MptVersaoProtocoloSessao versaoProtocoloSessao;
	private Integer vpsSeq;

	public MptProtocoloCuidados() {
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPcuSeq")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 6, scale = 0)
	public Integer getSeq() {		 
		return this.seq;
	}
	
	public void setSeq(Integer seq) {		 
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDU_SEQ")
	@NotNull	
	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}

	@Column(name="COMPLEMENTO", nullable = false)
	public String getComplemento() {
		return this.complemento;
	}	

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name="CRIADO_EM", nullable = false)	
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name="FREQUENCIA")
	public Integer getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name="ORDEM", nullable = false)
	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TEMPO")	
	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")	
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "SER_MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPS_SEQ")
	@NotNull
	public MptVersaoProtocoloSessao getVersaoProtocoloSessao() {
		return versaoProtocoloSessao;
	}

	public void setVersaoProtocoloSessao(MptVersaoProtocoloSessao versaoProtocoloSessao) {
		this.versaoProtocoloSessao = versaoProtocoloSessao;
	}

	@Column(name = "VPS_SEQ", insertable = false, updatable = false)	
	public Integer getVpsSeq() {
		return vpsSeq;
	}

	public void setVpsSeq(Integer vpsSeq) {
		this.vpsSeq = vpsSeq;
	}

	public enum Fields {		
		SEQ("seq"),
		VERSAO_PROTOCOLO_SESSAO("versaoProtocoloSessao"),
		TIPO_FREQUENCIA_APRAZAMENTO("tipoFrequenciaAprazamento"),
		CUIDADO_USUAL("cuidadoUsual"),
		FREQUENCIA("frequencia"),
		COMPLEMENTO("complemento"),
		ORDEM("ordem"),
		TEMPO("tempo"),
		VPS_SEQ("vpsSeq");
		
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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MptProtocoloCuidados)) {
			return false;
		}
		MptProtocoloCuidados other = (MptProtocoloCuidados) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
}