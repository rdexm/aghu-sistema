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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mciNtcSq1", sequenceName="AGH.MCI_NTC_SQ1", allocationSize = 1)
@Table(name = "MCI_NOTAS_CCIH", schema = "AGH")

public class MciNotasCCIH extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2739542736470164714L;
	private Integer seq;
	private AipPacientes paciente;
	private String descricao;
	private Date dtHrInicio;
	private Date dtHrEncerramento;
	private Date criadoEm;
	private RapServidores servidor;
	private Date alteradoEm;
	private RapServidores servidorMovimentado;
	private Integer version;

	public MciNotasCCIH() {
	}

	public MciNotasCCIH(Integer seq, AipPacientes paciente, String descricao, Date dtHrInicio,
			Date dtHrEncerramento, Date criadoEm, RapServidores servidor, Date alteradoEm,
			RapServidores servidorMovimentado) {
		this.seq = seq;
		this.paciente = paciente;
		this.descricao = descricao;
		this.dtHrInicio = dtHrInicio;
		this.dtHrEncerramento = dtHrEncerramento;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.alteradoEm = alteradoEm;
		this.servidorMovimentado = servidorMovimentado;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciNtcSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PAC_CODIGO", nullable = false, referencedColumnName = "CODIGO")
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false)
	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENCERRAMENTO")
	public Date getDtHrEncerramento() {
		return dtHrEncerramento;
	}

	public void setDtHrEncerramento(Date dtHrEncerramento) {
		this.dtHrEncerramento = dtHrEncerramento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorMovimentado() {
		return this.servidorMovimentado;
	}

	public void setServidorMovimentado(RapServidores servidorMovimentado) {
		this.servidorMovimentado = servidorMovimentado;
	}

	@Version
	@Column(name = "VERSION", nullable = false) 
	public Integer getVersion() {
	 	return this.version; 
	}
	  
	public void setVersion(Integer version) { 
		this.version = version; 
	}

	public enum Fields {
		SEQ("seq"),
		COD_PACIENTE("paciente.codigo"),
		DESCRICAO("descricao"),
		DT_HR_INICIO("dtHrInicio"),
		DT_HR_ENCERRAMENTO("dtHrEncerramento"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SERVIDOR_MOVIMENTADO("servidorMovimentado"),
		ALTERADO_EM("alteradoEm");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
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
		if (!(obj instanceof MciNotasCCIH)) {
			return false;
		}
		MciNotasCCIH other = (MciNotasCCIH) obj;
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
