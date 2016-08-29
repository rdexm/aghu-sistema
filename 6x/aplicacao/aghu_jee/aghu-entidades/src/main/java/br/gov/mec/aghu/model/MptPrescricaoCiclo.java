package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
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

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioTipoSessao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptSesSq1", sequenceName="AGH.MPT_SES_SQ1", allocationSize = 1)
@Table(name = "MPT_PRESCRICAO_CICLO", schema = "AGH")

public class MptPrescricaoCiclo extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 3934601518964037741L;
	
	private Integer seq;
	private AghAtendimentos aghAtendimentos;
	private Short ciclo;
	private Integer lote;
	private Date dtPrevista;
	private MptParamCalculoPrescricao mptParamCalculoPrescricao;
	private DominioTipoSessao indTipo;
	private Boolean indConcomitante;
	private Date criadoEm;
	private RapServidores servidor;
	private Set<MptSessao> listSessao = new HashSet<MptSessao>(0);
	private Set<MptProtocoloCiclo> listProtocoloCiclo = new HashSet<MptProtocoloCiclo>(0);

	
	public MptPrescricaoCiclo() {
		
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptSesSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false)
	public AghAtendimentos getAghAtendimentos() {
		return aghAtendimentos;
	}


	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}
	
	@Column(name = "CICLO", precision = 7, scale = 0)
	public Short getCiclo() {
		return ciclo;
	}


	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}


	@Column(name = "LOTE", precision = 7, scale = 0)
	public Integer getLote() {
		return lote;
	}


	public void setLote(Integer lote) {
		this.lote = lote;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PREVISTA", nullable = false, length = 7)
	public Date getDtPrevista() {
		return dtPrevista;
	}


	public void setDtPrevista(Date dtPrevista) {
		this.dtPrevista = dtPrevista;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "PCR_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
		@JoinColumn(name = "PCR_SEQP", referencedColumnName = "SEQP") })
	public MptParamCalculoPrescricao getMptParamCalculoPrescricao() {
		return mptParamCalculoPrescricao;
	}


	public void setMptParamCalculoPrescricao(
			MptParamCalculoPrescricao mptParamCalculoPrescricao) {
		this.mptParamCalculoPrescricao = mptParamCalculoPrescricao;
	}

	@Column(name = "IND_TIPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoSessao getIndTipo() {
		return indTipo;
	}
	
	public void setIndTipo(DominioTipoSessao indTipo) {
		this.indTipo = indTipo;
	}

	@Column(name = "IND_CONCOMITANTE", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConcomitante() {
		return indConcomitante;
	}
	
	public void setIndConcomitante(Boolean indConcomitante) {
		this.indConcomitante = indConcomitante;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mptPrescricaoCiclo")
	public Set<MptSessao> getListSessao() {
		return listSessao;
	}

	public void setListSessao(Set<MptSessao> listSessao) {
		this.listSessao = listSessao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mptPrescricaoCiclo")
	public Set<MptProtocoloCiclo> getListProtocoloCiclo() {
		return listProtocoloCiclo;
	}


	public void setListProtocoloCiclo(Set<MptProtocoloCiclo> listProtocoloCiclo) {
		this.listProtocoloCiclo = listProtocoloCiclo;
	}

	public enum Fields {
		
		SEQ("seq"),
		ATENDIMENTOS("aghAtendimentos"),
		CICLO("ciclo"),
		LOTE("lote"),
		DTPREVISTA("dtPrevista"),
		MPT_PARAM_CALCULO_PRESCRICAO("mptParamCalculoPrescricao"),
		TIPO("indTipo"),
		CONCOMITANTE("indConcomitante"),
		CRIADO_EM("criadoEm"),
		MPT_SESSAO("listSessao"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		PROTOCOLO_CICLO("listProtocoloCiclo");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

//	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof MptPrescricaoCiclo)) {
			return false;
		}
		MptPrescricaoCiclo other = (MptPrescricaoCiclo) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
