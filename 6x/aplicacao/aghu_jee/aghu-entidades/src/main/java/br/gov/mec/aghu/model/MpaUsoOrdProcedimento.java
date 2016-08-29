package br.gov.mec.aghu.model;


import java.io.Serializable;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacaoOrdProtocolo;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mpa_uso_ord_procedimentos database table.
 * 
 */
@Entity
@SequenceGenerator(name="mpaUopSq1", sequenceName="AGH.MPA_UOP_SQ1", allocationSize = 1)
@Table(name="MPA_USO_ORD_PROCEDIMENTOS")
public class MpaUsoOrdProcedimento extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7714969118582737694L;

	private Integer seq;
	
	private MpaCadOrdProcedimentos cadOrdProcedimentos;
	
	private Date criadoEm;
	private DominioSituacaoOrdProtocolo indSituacao;
	private String infComplementares;
	private String justificativa;
	private ScoMaterial material;
	
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	
	private MpmProcedEspecialDiversos  procedimentoEspecial;
	
	private MpmPrescricaoMdto prescricaoMedicamento;
	private MpmPrescricaoMdto prescricaoMedicamentoAnt;

	private Integer quantidade;
	
	private RapServidores servidor;
	
	private MpaUsoCuidado usoCuidado;
	
	private MpaUsoMedicacao usoMedicacao;

	private MpaUsoOrdProcedimento usoOrdProcedimento;
	
	//TODO: Criar no banco o campo version
	//private Integer version;
	
	
	private enum UsoOrdProcedimentoExceptionCode implements BusinessExceptionCode {
		MPA_UOP_CK1, MPA_UOP_CK2
	}
	

    public MpaUsoOrdProcedimento() {
    }

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpaUopSq1")
	@Column(name = "SEQ", nullable=false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="COP_SEQ")
	public MpaCadOrdProcedimentos getCadOrdProcedimentos() {
		return this.cadOrdProcedimentos;
	}

	public void setCadOrdProcedimentos(MpaCadOrdProcedimentos cadOrdProcedimentos) {
		this.cadOrdProcedimentos = cadOrdProcedimentos;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "IND_SITUACAO", nullable=false, length=2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoOrdProtocolo getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoOrdProtocolo indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(name="INF_COMPLEMENTARES", length=240)
	public String getInfComplementares() {
		return this.infComplementares;
	}

	public void setInfComplementares(String infComplementares) {
		this.infComplementares = infComplementares;
	}


	@Column(length=240)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO")
	public ScoMaterial getMaterial() {
		return this.material;
	}


	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PCI_SEQ")
	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return this.procedimentoCirurgico;
	}


	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PED_SEQ")
	public MpmProcedEspecialDiversos getProcedimentoEspecial() {
		return this.procedimentoEspecial;
	}


	public void setProcedimentoEspecial(
			MpmProcedEspecialDiversos procedimentoEspecial) {
		this.procedimentoEspecial = procedimentoEspecial;
	}


	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PPR_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PPR_SEQ", referencedColumnName = "SEQ") })
	public MpmPrescricaoMdto getPrescricaoMedicamento() {
		return this.prescricaoMedicamento;
	}


	public void setPrescricaoMedicamento(MpmPrescricaoMdto prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}
	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PPR_ATD_SEQ_ANT", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PPR_SEQ_ANT", referencedColumnName = "SEQ") })
	public MpmPrescricaoMdto getPrescricaoMedicamentoAnt() {
		return this.prescricaoMedicamentoAnt;
	}


	public void setPrescricaoMedicamentoAnt(
			MpmPrescricaoMdto prescricaoMedicamentoAnt) {
		this.prescricaoMedicamentoAnt = prescricaoMedicamentoAnt;
	}


	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "UCU_USP_APA_ATD_SEQ", referencedColumnName = "USP_APA_ATD_SEQ", nullable = false),
			@JoinColumn(name = "UCU_USP_APA_SEQ", referencedColumnName = "USP_APA_SEQ", nullable = false),
			@JoinColumn(name = "UCU_USP_VPA_PTA_SEQ", referencedColumnName = "USP_VPA_PTA_SEQ", nullable = false),
			@JoinColumn(name = "UCU_USP_VPA_SEQP", referencedColumnName = "USP_VPA_SEQP", nullable = false),
			@JoinColumn(name = "UCU_USP_SEQ", referencedColumnName = "USP_SEQ", nullable = false),
			@JoinColumn(name = "UCU_CCD_CIT_VPA_PTA_SEQ", referencedColumnName = "CCD_CIT_VPA_PTA_SEQ", nullable = false),
			@JoinColumn(name = "UCU_CCD_CIT_VPA_SEQP", referencedColumnName = "CCD_CIT_VPA_SEQP", nullable = false),
			@JoinColumn(name = "UCU_CCD_CIT_SEQP", referencedColumnName = "CCD_CIT_SEQP", nullable = false),
			@JoinColumn(name = "UCU_CCD_SEQP", referencedColumnName = "CCD_SEQP", nullable = false)
			})		
	public MpaUsoCuidado getUsoCuidado() {
		return this.usoCuidado;
	}

	public void setUsoCuidado(MpaUsoCuidado usoCuidado) {
		this.usoCuidado = usoCuidado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "UME_USP_APA_ATD_SEQ", referencedColumnName = "USP_APA_ATD_SEQ", nullable = false),
			@JoinColumn(name = "UME_USP_APA_SEQ", referencedColumnName = "USP_APA_SEQ", nullable = false), 
			@JoinColumn(name = "UME_USP_VPA_PTA_SEQ", referencedColumnName = "USP_VPA_PTA_SEQ", nullable = false), 
			@JoinColumn(name = "UME_USP_VPA_SEQP", referencedColumnName = "USP_VPA_SEQP", nullable = false), 
			@JoinColumn(name = "UME_USP_SEQ", referencedColumnName = "USP_SEQ", nullable = false), 
			@JoinColumn(name = "UME_CAM_CIT_VPA_PTA_SEQ", referencedColumnName = "CAM_CIT_VPA_PTA_SEQ", nullable = false), 
			@JoinColumn(name = "UME_CAM_CIT_VPA_SEQP", referencedColumnName = "CAM_CIT_VPA_SEQP", nullable = false), 
			@JoinColumn(name = "UME_CAM_CIT_SEQP", referencedColumnName = "CAM_CIT_SEQP", nullable = false), 
			@JoinColumn(name = "UME_CAM_SEQP", referencedColumnName = "CAM_SEQP", nullable = false) 
			})		
	public MpaUsoMedicacao getUsoMedicacao() {
		return this.usoMedicacao;
	}

	public void setUsoMedicacao(MpaUsoMedicacao usoMedicacao) {
		this.usoMedicacao = usoMedicacao;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="UOP_SEQ")
	public MpaUsoOrdProcedimento getUsoOrdProcedimento() {
		return this.usoOrdProcedimento;
	}

	public void setUsoOrdProcedimento(MpaUsoOrdProcedimento usoOrdProcedimento) {
		this.usoOrdProcedimento = usoOrdProcedimento;
	}
	
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarUsoOrdProcedimento(){
		
		/*
		 * RESTRIÇÕES (CONSTRAINTS)
		 */
		// CONSTRAINT MPA_UOP_CK1 --> CONSISTE MPA_USO_MEDICACOES E
		// MPA_USO_CUIDADOS
		
		if (this.getUsoMedicacao() != null && this.getUsoCuidado() == null){
			throw new BaseRuntimeException(UsoOrdProcedimentoExceptionCode.MPA_UOP_CK1);
		}
		
		if (this.getUsoMedicacao() == null && this.getUsoCuidado() != null){
			throw new BaseRuntimeException(UsoOrdProcedimentoExceptionCode.MPA_UOP_CK1);
		}		
		
		// CONSTRAINT MPA_UOP_CK2 --> CONSISTE Procedimento Cirúrgico,
		// Procedimento Especial e Materiais		
		if (!(this.procedimentoCirurgico != null && this.material == null && this.procedimentoEspecial == null)
				&& !(this.procedimentoCirurgico == null && this.material != null && this.procedimentoEspecial == null)
				&& !(this.procedimentoCirurgico == null && this.material == null && this.procedimentoEspecial != null)) {
			throw new BaseRuntimeException(UsoOrdProcedimentoExceptionCode.MPA_UOP_CK2);
		}		
	}
	
	public enum Fields {
		
		SEQ("seq"), // 
		PRESCRICAO_MEDICAMENTO("prescricaoMedicamento"), //
		PPR_ATD_SEQ("prescricaoMedicamento.id.atdSeq"), // 
		PPR_SEQ("prescricaoMedicamento.id.seq"), //
		USO_CUIDADO("usoCuidado"), //
		USO_MEDICACAO("usoMedicacao"), //	
		USO_ORD_PROCEDIMENTO("usoOrdProcedimento") //
		;	
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.seq == null) ? 0 : this.seq.hashCode());
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
		MpaUsoOrdProcedimento other = (MpaUsoOrdProcedimento) obj;
		if (this.seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!this.seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
}