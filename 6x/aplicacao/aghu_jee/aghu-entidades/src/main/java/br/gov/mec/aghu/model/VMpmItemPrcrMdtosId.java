package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
@Embeddable
public class VMpmItemPrcrMdtosId implements EntityCompositeId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4882503767385508494L;

	private Integer pmdAtdSeq;
	
	private Integer pmdSeq;
	
	private Integer seqP;
	
	private String observacao;
	
	private Integer medMatCodigo;
	
	private Integer jumSeq;
	
	private String IndOrigemJustif;
	
	private String descricaoMed;
	
	private Date criadoEm;
	
	private String indSituacao;
	
	private Integer atdSeq;
	
	private Integer serMatriculaValida;
	
	private Integer serVinCodigoValida;
	
	private String nomeSolicitante;
	
	private Integer pacCodigo;
	
	private String ltoLtoId;
	
	private Integer qrtNumero;
	
	private Integer unfSeq;
	
	private Date dthrFim;
	
	private String nomePaciente;
	
	private Integer prontuario;
	
	public enum Fields {
		PMD_ATD_SEQ("pmdAtdSeq"),
		PMD_SEQ("pmdSeq"),
		SEQP("seqP"),
		OBSERVACAO("observacao"),
		MED_MAT_CODIGO("medMatCodigo"),
		JUM_SEQ("jumSeq"),
		IND_ORIGEM_JUSTIF("IndOrigemJustif"),
		DESCRICAO_MED("descricaoMed"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		ATD_SEQ("atdSeq"),
		SER_MATRICULA_VALIDA("serMatriculaValida"),
		SER_VIN_CODIGO_VALIDA("serVinCodigoValida"),
		NOME_SOLICITANTE("nomeSolicitante"),
		PAC_CODIGO("pacCodigo"),
		LTO_LTO_ID("ltoLtoId"),
		QRT_NUMERO("qrtNumero"),
		UNF_SEQ("unfSeq"),
		DTHR_FIM("dthrFim"),
		NOME_PACIENTE("nomePaciente"),
		PRONTUARIO("prontuario");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Column(name = "PMD_ATD_SEQ", nullable = false)
	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	@Column(name = "PMD_SEQ", nullable = false)
	public Integer getPmdSeq() {
		return pmdSeq;
	}

	public void setPmdSeq(Integer pmdSeq) {
		this.pmdSeq = pmdSeq;
	}

	@Column(name = "SEQP", nullable = false)
	public Integer getSeqP() {
		return seqP;
	}

	public void setSeqP(Integer seqP) {
		this.seqP = seqP;
	}

	@Column(name = "OBSERVACAO", nullable = false)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "MED_MAT_CODIGO", nullable = false)
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	@Column(name = "JUM_SEQ", nullable = false)
	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	@Column(name = "IND_ORIGEM_JUSTIF", nullable = false)
	public String getIndOrigemJustif() {
		return IndOrigemJustif;
	}

	public void setIndOrigemJustif(String indOrigemJustif) {
		IndOrigemJustif = indOrigemJustif;
	}

	@Column(name = "DESCRICAO_MED", nullable = false)
	public String getDescricaoMed() {
		return descricaoMed;
	}

	public void setDescricaoMed(String descricaoMed) {
		this.descricaoMed = descricaoMed;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false)
	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "ATD_SEQ", nullable = false)
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@Column(name = "SER_MATRICULA_VALIDA", nullable = false)
	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}

	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}

	@Column(name = "SER_VIN_CODIGO_VALIDA", nullable = false)
	public Integer getSerVinCodigoValida() {
		return serVinCodigoValida;
	}

	public void setSerVinCodigoValida(Integer serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}

	@Column(name = "NOME_SOLICITANTE", nullable = false)
	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	@Column(name = "PAC_CODIGO", nullable = false)
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "LTO_LTO_ID", nullable = false)
	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Column(name = "QRT_NUMERO", nullable = false)
	public Integer getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Integer qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	@Column(name = "UNF_SEQ", nullable = false)
	public Integer getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Integer unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "DTHR_FIM", nullable = false)
	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Column(name = "NOME_PACIENTE", nullable = false)
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	@Column(name = "PRONTUARIO", nullable = false)
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result + ((jumSeq == null) ? 0 : jumSeq.hashCode());
		result = prime * result
				+ ((ltoLtoId == null) ? 0 : ltoLtoId.hashCode());
		result = prime * result
				+ ((medMatCodigo == null) ? 0 : medMatCodigo.hashCode());
		result = prime * result
				+ ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		result = prime * result
				+ ((pmdAtdSeq == null) ? 0 : pmdAtdSeq.hashCode());
		result = prime * result + ((pmdSeq == null) ? 0 : pmdSeq.hashCode());
		result = prime * result
				+ ((prontuario == null) ? 0 : prontuario.hashCode());
		result = prime * result
				+ ((qrtNumero == null) ? 0 : qrtNumero.hashCode());
		result = prime * result + ((seqP == null) ? 0 : seqP.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
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
		if (!(obj instanceof VMpmItemPrcrMdtosId)) {
			return false;
		}
		VMpmItemPrcrMdtosId other = (VMpmItemPrcrMdtosId) obj;
		if (atdSeq == null) {
			if (other.atdSeq != null) {
				return false;
			}
		} else if (!atdSeq.equals(other.atdSeq)) {
			return false;
		}
		if (jumSeq == null) {
			if (other.jumSeq != null) {
				return false;
			}
		} else if (!jumSeq.equals(other.jumSeq)) {
			return false;
		}
		if (ltoLtoId == null) {
			if (other.ltoLtoId != null) {
				return false;
			}
		} else if (!ltoLtoId.equals(other.ltoLtoId)) {
			return false;
		}
		if (medMatCodigo == null) {
			if (other.medMatCodigo != null) {
				return false;
			}
		} else if (!medMatCodigo.equals(other.medMatCodigo)) {
			return false;
		}
		if (pacCodigo == null) {
			if (other.pacCodigo != null) {
				return false;
			}
		} else if (!pacCodigo.equals(other.pacCodigo)) {
			return false;
		}
		if (pmdAtdSeq == null) {
			if (other.pmdAtdSeq != null) {
				return false;
			}
		} else if (!pmdAtdSeq.equals(other.pmdAtdSeq)) {
			return false;
		}
		if (pmdSeq == null) {
			if (other.pmdSeq != null) {
				return false;
			}
		} else if (!pmdSeq.equals(other.pmdSeq)) {
			return false;
		}
		if (prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!prontuario.equals(other.prontuario)) {
			return false;
		}
		if (qrtNumero == null) {
			if (other.qrtNumero != null) {
				return false;
			}
		} else if (!qrtNumero.equals(other.qrtNumero)) {
			return false;
		}
		if (seqP == null) {
			if (other.seqP != null) {
				return false;
			}
		} else if (!seqP.equals(other.seqP)) {
			return false;
		}
		if (unfSeq == null) {
			if (other.unfSeq != null) {
				return false;
			}
		} else if (!unfSeq.equals(other.unfSeq)) {
			return false;
		}
		return true;
	}
	
	
}
