package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "V_MPM_ITEM_PRCR_MDTOS", schema = "AGH")
@Immutable
public class VMpmItemPrcrMdtos extends BaseEntityId<VMpmItemPrcrMdtosId> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -387891098788694354L;
	
	
	private VMpmItemPrcrMdtosId id;
	
	private Integer pacCodigo;
	
	private Integer prontuario;
	
	private Integer medMatCodigo;
	
	private Integer pmdAtdSeq;
	
	private Integer jumSeq;
	
	private String nomePaciente;
	
	private String descricaoMed;
	
	private String nomeSolicitante;
	
	private String ltoLtoId;
	
	private Integer qrtNumero;
	
	private Integer unfSeq;
	
	private Date dthrFim;
	
	private Date criadoEm;
	
	private String indSituacao;
	
	

	public VMpmItemPrcrMdtos() {
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pmdAtdSeq", column = @Column(name = "PMD_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "pmdSeq", column = @Column(name = "PMD_SEQ", nullable = false)),
			@AttributeOverride(name = "seqP", column = @Column(name = "SEQP")),
			@AttributeOverride(name = "observacao", column = @Column(name = "OBSERVACAO")),
			@AttributeOverride(name = "medMatCodigo", column = @Column(name = "MED_MAT_CODIGO")),
			@AttributeOverride(name = "jumSeq", column = @Column(name = "JUM_SEQ", nullable = false)),
			@AttributeOverride(name = "IndOrigemJustif", column = @Column(name = "IND_ORIGEM_JUSTIF", nullable = false)),
			@AttributeOverride(name = "descricaoMed", column = @Column(name = "DESCRICAO_MED")),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", nullable = false)),
			@AttributeOverride(name = "indSituacao", column = @Column(name = "IND_SITUACAO", nullable = false)),
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "serMatriculaValida", column = @Column(name = "SER_MATRICULA_VALIDA", precision = 2)),
			@AttributeOverride(name = "serVinCodigoValida", column = @Column(name = "SER_VIN_CODIGO_VALIDA", nullable = false, precision = 2)),
			@AttributeOverride(name = "nomeSolicitante", column = @Column(name = "NOME_SOLICITANTE", precision = 4)),
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false)),
			@AttributeOverride(name = "ltoLtoId", column = @Column(name = "LTO_LTO_ID", nullable = false)),
			@AttributeOverride(name = "qrtNumero", column = @Column(name = "QRT_NUMERO", nullable = false, length = 1)),
			@AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ", nullable = false, length = 1)),
			@AttributeOverride(name = "dthrFim", column = @Column(name = "DTHR_FIM", nullable = false)),
			@AttributeOverride(name = "nomePaciente", column = @Column(name = "NOME_PACIENTE", precision = 2, scale = 0)),
			@AttributeOverride(name = "prontuario", column = @Column(name = "PRONTUARIO", precision = 2, scale = 0)) })
	public VMpmItemPrcrMdtosId getId() {
		return this.id;
	}
	
	public  void setId(VMpmItemPrcrMdtosId id) {
		this.id = id;
	}

	@Column(name = "PAC_CODIGO", insertable = false, updatable = false)
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "PRONTUARIO", insertable = false, updatable = false)
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	@Column(name = "MED_MAT_CODIGO", insertable = false, updatable = false)
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	@Column(name = "PMD_ATD_SEQ", insertable = false, updatable = false)
	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	@Column(name = "JUM_SEQ", insertable = false, updatable = false)
	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}
	
	@Column(name = "NOME_PACIENTE", insertable = false, updatable = false)
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	
	@Column(name = "DESCRICAO_MED", insertable = false, updatable = false)
	public String getDescricaoMed() {
		return descricaoMed;
	}

	public void setDescricaoMed(String descricaoMed) {
		this.descricaoMed = descricaoMed;
	}

	@Column(name = "NOME_SOLICITANTE", insertable = false, updatable = false)
	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	@Column(name = "LTO_LTO_ID", insertable = false, updatable = false)
	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Column(name = "QRT_NUMERO", insertable = false, updatable = false)
	public Integer getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Integer qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	@Column(name = "UNF_SEQ", insertable = false, updatable = false)
	public Integer getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Integer unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", insertable = false, updatable = false)
	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	
	@Column(name = "IND_SITUACAO", insertable = false, updatable = false)
	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", insertable = false, updatable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		MED_MAT_CODIGO("medMatCodigo"),
		PMD_ATD_SEQ("pmdAtdSeq"),
		JUM_SEQ("jumSeq"),
		NOME_PACIENTE("nomePaciente"),
		DESCRICAO_MED("descricaoMed"),
		NOME_SOLICITANTE("nomeSolicitante"),
		LTO_LTO_ID("ltoLtoId"),
		QRT_NUMERO("qrtNumero"),
		DTHR_FIM("dthrFim"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		UNF_SEQ("unfSeq");
		

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
