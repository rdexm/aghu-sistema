package br.gov.mec.aghu.model;

// Generated 26/02/2010 17:37:25 by Hibernate Tools 3.2.5.Beta

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * McoLogImpressoes generated by hbm2java
 */
@Entity
@Table(name = "MCO_LOG_IMPRESSOES", schema = "AGH")
public class McoLogImpressoes extends BaseEntityId<McoLogImpressoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4816487111330330792L;
	private McoLogImpressoesId id;
	private McoGestacoes mcoGestacoes;
	private McoRecemNascidos recemNascido;
	private String evento;
	private Date criadoEm;
	private Byte rnaSeqp;
	private RapServidores servidor;
	private AacConsultas consulta;

	public McoLogImpressoes() {
	}

	public McoLogImpressoes(McoLogImpressoesId id, McoGestacoes mcoGestacoes,
			String evento, Date criadoEm, Byte rnaSeqp, RapServidores servidor,
			AacConsultas consulta) {
		super();
		this.id = id;
		this.mcoGestacoes = mcoGestacoes;
		this.evento = evento;
		this.criadoEm = criadoEm;
		this.rnaSeqp = rnaSeqp;
		this.servidor = servidor;
		this.consulta = consulta;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "gsoPacCodigo", column = @Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "gsoSeqp", column = @Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)) })
	@NotNull
	public McoLogImpressoesId getId() {
		return this.id;
	}

	public void setId(McoLogImpressoesId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "GSO_PAC_CODIGO", referencedColumnName = "PAC_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "GSO_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	@NotNull
	public McoGestacoes getMcoGestacoes() {
		return this.mcoGestacoes;
	}

	public void setMcoGestacoes(McoGestacoes mcoGestacoes) {
		this.mcoGestacoes = mcoGestacoes;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "GSO_PAC_CODIGO", referencedColumnName = "GSO_PAC_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "GSO_SEQP", referencedColumnName = "GSO_SEQP", insertable = false, updatable = false),
			@JoinColumn(name = "RNA_SEQP", referencedColumnName = "SEQP", insertable = false, updatable = false)})
	public McoRecemNascidos getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(McoRecemNascidos recemNascido) {
		this.recemNascido = recemNascido;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "RNA_SEQP", precision = 2, scale = 0)
	public Byte getRnaSeqp() {
		return this.rnaSeqp;
	}

	public void setRnaSeqp(Byte rnaSeqp) {
		this.rnaSeqp = rnaSeqp;
	}

	public enum Fields {
		CODIGO_PACIENTE("id.gsoPacCodigo"), 
		SEQUENCE("id.gsoSeqp"),
		SEQ("id.seqp"),
		RNA_SEQP("rnaSeqp"),
		CON_NUMERO("consulta.numero"),
		EVENTO("evento"),
		CRIADO_EM("criadoEm"),
		RECEM_NASCIDO("recemNascido");       
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}	
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof McoLogImpressoes)) {
			return false;
		}
		McoLogImpressoes other = (McoLogImpressoes) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CON_NUMERO")
	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	@Column(name = "EVENTO", nullable = false, length = 30)
	@NotNull
	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}
	
	@Transient
	public DominioEventoLogImpressao getEventoDominio(String evento){
		if(getEvento() != null){
			return DominioEventoLogImpressao.valueOf(getEvento());
		}else{
			return null;
		}
	}
	
}