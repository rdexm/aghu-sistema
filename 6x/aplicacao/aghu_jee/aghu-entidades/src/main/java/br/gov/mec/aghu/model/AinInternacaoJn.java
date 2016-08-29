package br.gov.mec.aghu.model;

// Generated 31/05/2010 12:45:56 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AIN_INTERNACOES_JN", schema = "AGH")
@SequenceGenerator(name = "ainIntJnSeq", sequenceName = "AGH.AIN_INT_JN_SEQ", allocationSize = 1)

@Immutable
public class AinInternacaoJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5021997812962820312L;
	private Integer seq;
	private Integer pacCodigo;
	private Short espSeq;
	private Integer serMatriculaDigita;
	private Short serVinCodigoDigita;
	private Integer serMatriculaProfessor;
	private Short serVinCodigoProfessor;
	private Date dthrInternacao;
	private Boolean envProntUnidInt;
	private Integer tciSeq;
	private Short cspCnvCodigo;
	private Byte cspSeq;
	private Short oevSeq;
	private Boolean indSaidaPac;
	private Boolean indDifClasse;
	private Boolean indPacienteInternado;
	private DominioLocalPaciente indLocalPaciente;
	private String ltoLtoId;
	private Short qrtNumero;
	private Short unfSeq;
	private String tamCodigo;
	private Integer atuSeq;
	private Date dtPrevAlta;
	private Date dthrAltaMedica;
	private Date dtSaidaPaciente;
	private Integer ihoSeqOrigem;
	private Integer ihoSeqTransferencia;
	private String justificativaAltDel;
	private Date dthrPrimeiroEvento;
	private Date dthrUltimoEvento;
	private Integer iphSeq;
	private Short iphPhoSeq;
	private Date dthrAvisoSamis;
	private String tipoTransacao;

	public AinInternacaoJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ainIntJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "ESP_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Column(name = "SER_MATRICULA_DIGITA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatriculaDigita() {
		return this.serMatriculaDigita;
	}

	public void setSerMatriculaDigita(Integer serMatriculaDigita) {
		this.serMatriculaDigita = serMatriculaDigita;
	}

	@Column(name = "SER_VIN_CODIGO_DIGITA", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigoDigita() {
		return this.serVinCodigoDigita;
	}

	public void setSerVinCodigoDigita(Short serVinCodigoDigita) {
		this.serVinCodigoDigita = serVinCodigoDigita;
	}

	@Column(name = "SER_MATRICULA_PROFESSOR", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatriculaProfessor() {
		return this.serMatriculaProfessor;
	}

	public void setSerMatriculaProfessor(Integer serMatriculaProfessor) {
		this.serMatriculaProfessor = serMatriculaProfessor;
	}

	@Column(name = "SER_VIN_CODIGO_PROFESSOR", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigoProfessor() {
		return this.serVinCodigoProfessor;
	}

	public void setSerVinCodigoProfessor(Short serVinCodigoProfessor) {
		this.serVinCodigoProfessor = serVinCodigoProfessor;
	}

	@Column(name = "DTHR_INTERNACAO", nullable = false, length = 7)
	public Date getDthrInternacao() {
		return this.dthrInternacao;
	}

	public void setDthrInternacao(Date dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	@Column(name = "ENV_PRONT_UNID_INT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEnvProntUnidInt() {
		return this.envProntUnidInt;
	}

	public void setEnvProntUnidInt(Boolean envProntUnidInt) {
		this.envProntUnidInt = envProntUnidInt;
	}

	@Column(name = "TCI_SEQ", nullable = false, precision = 2, scale = 0)
	public Integer getTciSeq() {
		return this.tciSeq;
	}

	public void setTciSeq(Integer tciSeq) {
		this.tciSeq = tciSeq;
	}

	@Column(name = "CSP_CNV_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getCspCnvCodigo() {
		return this.cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	@Column(name = "CSP_SEQ", nullable = false, precision = 2, scale = 0)
	public Byte getCspSeq() {
		return this.cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	@Column(name = "OEV_SEQ", nullable = false, precision = 2, scale = 0)
	public Short getOevSeq() {
		return this.oevSeq;
	}

	public void setOevSeq(Short oevSeq) {
		this.oevSeq = oevSeq;
	}

	@Column(name = "IND_SAIDA_PAC", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSaidaPac() {
		return this.indSaidaPac;
	}

	public void setIndSaidaPac(Boolean indSaidaPac) {
		this.indSaidaPac = indSaidaPac;
	}

	@Column(name = "IND_DIF_CLASSE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDifClasse() {
		return this.indDifClasse;
	}

	public void setIndDifClasse(Boolean indDifClasse) {
		this.indDifClasse = indDifClasse;
	}

	@Column(name = "IND_PACIENTE_INTERNADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPacienteInternado() {
		return this.indPacienteInternado;
	}

	public void setIndPacienteInternado(Boolean indPacienteInternado) {
		this.indPacienteInternado = indPacienteInternado;
	}

	@Column(name = "IND_LOCAL_PACIENTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioLocalPaciente getIndLocalPaciente() {
		return this.indLocalPaciente;
	}

	public void setIndLocalPaciente(DominioLocalPaciente indLocalPaciente) {
		this.indLocalPaciente = indLocalPaciente;
	}

	@Column(name = "LTO_LTO_ID", length = 14)
	@Length(max = 14)
	public String getLtoLtoId() {
		return this.ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Column(name = "QRT_NUMERO", precision = 4, scale = 0)
	public Short getQrtNumero() {
		return this.qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	@Column(name = "UNF_SEQ", precision = 4, scale = 0)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "TAM_CODIGO", length = 3)
	@Length(max = 3)
	public String getTamCodigo() {
		return this.tamCodigo;
	}

	public void setTamCodigo(String tamCodigo) {
		this.tamCodigo = tamCodigo;
	}

	@Column(name = "ATU_SEQ", precision = 8, scale = 0)
	public Integer getAtuSeq() {
		return this.atuSeq;
	}

	public void setAtuSeq(Integer atuSeq) {
		this.atuSeq = atuSeq;
	}

	@Column(name = "DT_PREV_ALTA", length = 7)
	public Date getDtPrevAlta() {
		return this.dtPrevAlta;
	}

	public void setDtPrevAlta(Date dtPrevAlta) {
		this.dtPrevAlta = dtPrevAlta;
	}

	@Column(name = "DTHR_ALTA_MEDICA", length = 7)
	public Date getDthrAltaMedica() {
		return this.dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	@Column(name = "DT_SAIDA_PACIENTE", length = 7)
	public Date getDtSaidaPaciente() {
		return this.dtSaidaPaciente;
	}

	public void setDtSaidaPaciente(Date dtSaidaPaciente) {
		this.dtSaidaPaciente = dtSaidaPaciente;
	}

	@Column(name = "IHO_SEQ_ORIGEM", precision = 2, scale = 0)
	public Integer getIhoSeqOrigem() {
		return this.ihoSeqOrigem;
	}

	public void setIhoSeqOrigem(Integer ihoSeqOrigem) {
		this.ihoSeqOrigem = ihoSeqOrigem;
	}

	@Column(name = "IHO_SEQ_TRANSFERENCIA", precision = 2, scale = 0)
	public Integer getIhoSeqTransferencia() {
		return this.ihoSeqTransferencia;
	}

	public void setIhoSeqTransferencia(Integer ihoSeqTransferencia) {
		this.ihoSeqTransferencia = ihoSeqTransferencia;
	}

	@Column(name = "JUSTIFICATIVA_ALT_DEL", length = 240)
	@Length(max = 240)
	public String getJustificativaAltDel() {
		return this.justificativaAltDel;
	}

	public void setJustificativaAltDel(String justificativaAltDel) {
		this.justificativaAltDel = justificativaAltDel;
	}

	@Column(name = "DTHR_PRIMEIRO_EVENTO", length = 7)
	public Date getDthrPrimeiroEvento() {
		return this.dthrPrimeiroEvento;
	}

	public void setDthrPrimeiroEvento(Date dthrPrimeiroEvento) {
		this.dthrPrimeiroEvento = dthrPrimeiroEvento;
	}

	@Column(name = "DTHR_ULTIMO_EVENTO", length = 7)
	public Date getDthrUltimoEvento() {
		return this.dthrUltimoEvento;
	}

	public void setDthrUltimoEvento(Date dthrUltimoEvento) {
		this.dthrUltimoEvento = dthrUltimoEvento;
	}

	@Column(name = "IPH_SEQ", precision = 8, scale = 0)
	public Integer getIphSeq() {
		return this.iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	@Column(name = "IPH_PHO_SEQ", precision = 4, scale = 0)
	public Short getIphPhoSeq() {
		return this.iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	@Column(name = "DTHR_AVISO_SAMIS", length = 7)
	public Date getDthrAvisoSamis() {
		return this.dthrAvisoSamis;
	}

	public void setDthrAvisoSamis(Date dthrAvisoSamis) {
		this.dthrAvisoSamis = dthrAvisoSamis;
	}
	
	@Column(name = "TP_TRANSACAO_ALTA", length = 15)
	@Length(max = 15)
	public String getTipoTransacao() {
		return tipoTransacao;
	}

	public void setTipoTransacao(String tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeqJn() == null) ? 0 : getSeqJn().hashCode());
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
		AinInternacaoJn other = (AinInternacaoJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}

	public enum Fields {

		SEQ("seq"),
		PAC_CODIGO("pacCodigo"),
		ESP_SEQ("espSeq"),
		SER_MATRICULA_DIGITA("serMatriculaDigita"),
		SER_VIN_CODIGO_DIGITA("serVinCodigoDigita"),
		SER_MATRICULA_PROFESSOR("serMatriculaProfessor"),
		SER_VIN_CODIGO_PROFESSOR("serVinCodigoProfessor"),
		DTHR_INTERNACAO("dthrInternacao"),
		ENV_PRONT_UNID_INT("envProntUnidInt"),
		TCI_SEQ("tciSeq"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		OEV_SEQ("oevSeq"),
		IND_SAIDA_PAC("indSaidaPac"),
		IND_DIF_CLASSE("indDifClasse"),
		IND_PACIENTE_INTERNADO("indPacienteInternado"),
		IND_LOCAL_PACIENTE("indLocalPaciente"),
		LTO_LTO_ID("ltoLtoId"),
		QRT_NUMERO("qrtNumero"),
		UNF_SEQ("unfSeq"),
		TAM_CODIGO("tamCodigo"),
		ATU_SEQ("atuSeq"),
		DT_PREV_ALTA("dtPrevAlta"),
		DTHR_ALTA_MEDICA("dthrAltaMedica"),
		DT_SAIDA_PACIENTE("dtSaidaPaciente"),
		IHO_SEQ_ORIGEM("ihoSeqOrigem"),
		IHO_SEQ_TRANSFERENCIA("ihoSeqTransferencia"),
		JUSTIFICATIVA_ALT_DEL("justificativaAltDel"),
		DTHR_PRIMEIRO_EVENTO("dthrPrimeiroEvento"),
		DTHR_ULTIMO_EVENTO("dthrUltimoEvento"),
		IPH_SEQ("iphSeq"),
		IPH_PHO_SEQ("iphPhoSeq"),
		DTHR_AVISO_SAMIS("dthrAvisoSamis"),
		TIPO_TRANSACAO("tipoTransacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
