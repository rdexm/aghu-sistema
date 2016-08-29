package br.gov.mec.aghu.model;

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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptLoaSq1", sequenceName="AGH.MPT_LOA_SQ1", allocationSize = 1)
@Table(name = "MPT_LOCAL_ATENDIMENTO", schema = "AGH")

public class MptLocalAtendimento extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = -6812441901110101241L;
	
	private Short seq;
	private String descricao;
	private MptSalas sala;
	private Boolean indReserva;
	private DominioTipoAcomodacao tipoLocal;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version; 	
	
	public MptLocalAtendimento() {
		
	}

	public MptLocalAtendimento(Short seq, String descricao, MptSalas sala, DominioTipoAcomodacao tipoLocal,
			DominioSituacao indSituacao, Date criadoEm, RapServidores servidor) {
		this.seq = seq;
		this.descricao = descricao;
		this.sala = sala;
		this.tipoLocal = tipoLocal;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptLoaSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_SAL", nullable = false)
	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	@Column(name = "IND_RESERVA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndReserva() {
		return indReserva;
	}

	public void setIndReserva(Boolean indReserva) {
		this.indReserva = indReserva;
	}

	@Column(name = "TIPO_LOCAL", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoAcomodacao getTipoLocal() {
		return tipoLocal;
	}

	public void setTipoLocal(DominioTipoAcomodacao tipoLocal) {
		this.tipoLocal = tipoLocal;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
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

	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		SALA("sala"),
		SAL_SEQ("sala.seq"),
		IND_RESERVA("indReserva"),
		TIPO_LOCAL("tipoLocal"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo");
		
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
		MptLocalAtendimento other = (MptLocalAtendimento) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
