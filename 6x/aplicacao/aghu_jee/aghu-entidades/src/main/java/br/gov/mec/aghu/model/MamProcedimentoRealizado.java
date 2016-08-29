package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Transient;


import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the mam_proc_realizados database table.
 * 
 */
@Entity
@SequenceGenerator(name="mamProcRealizSq1", sequenceName="AGH.MAM_POL_SQ1", allocationSize = 1)
@Table(name = "MAM_PROC_REALIZADOS")
public class MamProcedimentoRealizado extends BaseEntitySeq<Long> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6781729955884780881L;

	private Long seq;

	private AghCid cid;

	private AacConsultas consulta;

	private Date dthrCriacao;

	private Date dthrMovimento;

	private Date dthrValida;

	private Date dthrValidaMovimento;

	private Boolean padraoConsulta;

	private DominioIndPendenteAmbulatorio pendente;

	private DominioSituacao situacao;
	
	private AipPacientes paciente;

	/**
	 * Chave estrangeira para a tabela MAM_PROCEDIMENTOS
	 */
	private MamProcedimento procedimento;

	private Byte quantidade;

	private RapServidores servidor;

	private RapServidores servidorValida;

	private RapServidores servidorMovimento;

	private RapServidores servidorValidaMovimento;
	
	
	
	private MamProcedimentoRealizado procedimentoRealizado;

	private Set<MamProcedimentoRealizado> mamProcRealizados;
	
	private Boolean validado;

	public MamProcedimentoRealizado() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamProcRealizSq1")	
	@Column(unique = true, nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID_SEQ")
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CON_NUMERO")
	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	@Column(name = "DTHR_CRIACAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	@Column(name = "DTHR_MVTO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDthrMovimento() {
		return this.dthrMovimento;
	}

	public void setDthrMovimento(Date dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}

	@Column(name = "DTHR_VALIDA", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDthrValida() {
		return this.dthrValida;
	}

	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}

	@Column(name = "DTHR_VALIDA_MVTO", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDthrValidaMovimento() {
		return this.dthrValidaMovimento;
	}

	public void setDthrValidaMovimento(Date dthrValidaMovimento) {
		this.dthrValidaMovimento = dthrValidaMovimento;
	}

	@Column(name = "IND_PADRAO_CONSULTA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPadraoConsulta() {
		return this.padraoConsulta;
	}

	public void setPadraoConsulta(Boolean padraoConsulta) {
		this.padraoConsulta = padraoConsulta;
	}

	@Column(name = "IND_PENDENTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getPendente() {
		return this.pendente;
	}

	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO")
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRD_SEQ", nullable = false)
	public MamProcedimento getProcedimento() {
		return this.procedimento;
	}

	public void setProcedimento(MamProcedimento procedimento) {
		this.procedimento = procedimento;
	}

	@Column(name = "QUANTIDADE", precision = 2, scale = 0)
	public Byte getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Byte quantidade) {
		this.quantidade = quantidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_VALIDA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_VALIDA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorValida() {
		return servidorValida;
	}

	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_MVTO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MVTO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorMovimento() {
		return servidorMovimento;
	}

	public void setServidorMovimento(RapServidores servidorMovimento) {
		this.servidorMovimento = servidorMovimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_VALIDA_MVTO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_VALIDA_MVTO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorValidaMovimento() {
		return servidorValidaMovimento;
	}

	public void setServidorValidaMovimento(RapServidores servidorValidaMovimento) {
		this.servidorValidaMovimento = servidorValidaMovimento;
	}
	
	
	// bi-directional many-to-one association to MamProcRealizado
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POL_SEQ")
	public MamProcedimentoRealizado getProcedimentoRealizado() {
		return this.procedimentoRealizado;
	}

	public void setProcedimentoRealizado(MamProcedimentoRealizado procedimentoRealizado) {
		this.procedimentoRealizado = procedimentoRealizado;
	}

	// bi-directional many-to-one association to MamProcRealizado
	@OneToMany(mappedBy = "procedimentoRealizado")
	public Set<MamProcedimentoRealizado> getMamProcRealizados() {
		return this.mamProcRealizados;
	}

	public void setMamProcRealizados(
			Set<MamProcedimentoRealizado> mamProcRealizados) {
		this.mamProcRealizados = mamProcRealizados;
	}
	
	@Transient
	public Boolean getValidado() {
		validado = dthrMovimento != null && pendente.equals(DominioIndPendenteAmbulatorio.A);
		return validado;
	}

	public void setValidado(Boolean validado) {
		this.validado = validado;
	}		

	public enum Fields {
		SEQ("seq"),
		CON_NUMERO("consulta.numero"),
		PROCEDIMENTO("procedimento"),
		PROCEDIMENTO_SEQ("procedimento.seq"),
		PROCEDIMENTO_REALIZADO_SEQ("procedimentoRealizado.seq"),
		SITUACAO("situacao"),
		DTHR_CRIACAO("dthrCriacao"), 
		PENDENTE("pendente"),
		DTHR_MOVIMENTO("dthrMovimento"),
		DTHR_VALIDA_MOVIMENTO("dthrValidaMovimento"),
		QUANTIDADE("quantidade"),
		PADRAO_CONSULTA("padraoConsulta"),
		CID_SEQ("cid.seq"),
		CID("cid"),
		PROCEDIMENTOS_REALIZADOS("mamProcRealizados"),
		PAC_CODIGO("paciente.codigo");

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
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof MamProcedimentoRealizado)) {
			return false;
		}
		MamProcedimentoRealizado other = (MamProcedimentoRealizado) obj;
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