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
import javax.persistence.Transient;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@Table(name = "AFA_VIA_ADM_MDTOS_JN", schema = "AGH")
@SequenceGenerator(name = "afaVamJnSeq", sequenceName = "AGH.AFA_VAM_JN_SEQ", allocationSize = 1)
public class AfaViaAdministracaoMedicamentoJN extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1154587116614175139L;
	private Integer medMatCodigo;
	private String vadSigla;
	private RapServidores servidor;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Boolean permiteBI;
	private Boolean defaultBI;
	
	//Transient
	private String descricaoViaAdministracao;
	private String nomeResponsavel;
	
	public AfaViaAdministracaoMedicamentoJN() {
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaVamJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	public AfaViaAdministracaoMedicamentoJN(
			AfaViaAdministracaoMedicamento viaAdministracaoMedicamento) {
		this.medMatCodigo = viaAdministracaoMedicamento.getId()
				.getMedMatCodigo();
		this.vadSigla = viaAdministracaoMedicamento.getId().getVadSigla();
		this.servidor = viaAdministracaoMedicamento.getServidor();
		this.criadoEm = viaAdministracaoMedicamento.getCriadoEm();
		this.indSituacao = viaAdministracaoMedicamento.getSituacao();
		this.permiteBI = viaAdministracaoMedicamento.getPermiteBi();
		this.defaultBI = viaAdministracaoMedicamento.getDefaultBi();
	}

	@Column(name = "MED_MAT_CODIGO", nullable = false, precision = 6, scale = 0)
	public Integer getMedMatCodigo() {
		return this.medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	@Column(name = "VAD_SIGLA", nullable = false, scale = 0)
	public String getVadSigla() {
		return this.vadSigla;
	}

	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "JIND_PERMITE_BI", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPermiteBI() {
		return permiteBI;
	}

	public void setPermiteBI(Boolean permiteBI) {
		this.permiteBI = permiteBI;
	}

	@Column(name = "IND_DEFAULT_BI", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDefaultBI() {
		return defaultBI;
	}

	public void setDefaultBI(Boolean defaultBI) {
		this.defaultBI = defaultBI;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {
		if (this.indSituacao == null) {
			this.indSituacao = DominioSituacao.A;
		}
	}
	
	public enum Fields {

		MED_MAT_CODIGO("medMatCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Transient
	public String getDescricaoViaAdministracao() {
		return descricaoViaAdministracao;
	}

	public void setDescricaoViaAdministracao(String descricaoViaAdministracao) {
		this.descricaoViaAdministracao = descricaoViaAdministracao;
	}

	@Transient
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

}
