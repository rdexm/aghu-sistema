package br.gov.mec.aghu.model;

// Generated 11/06/2010 10:31:39 by Hibernate Tools 3.2.5.Beta

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@SequenceGenerator(name="mciGrupoProcedRiscoJn", sequenceName="AGH.MCI_GRS_JN_SEQ", allocationSize = 1)
@Table(name = "MCI_GRUPO_PROCED_RISCOS_JN", schema = "AGH")
public class MciGrupoProcedRiscoJn extends BaseJournal{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1930912809095151884L;
	private Short porSeq;
	private Short tgpSeq;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	
	public MciGrupoProcedRiscoJn() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciGrupoProcedRiscoJn")
	@Column(name = "SEQ_JN", nullable = false, precision = 12, scale = 0)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
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
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 4, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "POR_SEQ", precision = 4, scale = 0)
	public Short getPorSeq() {
		return porSeq;
	}


	public void setPorSeq(Short porSeq) {
		this.porSeq = porSeq;
	}

	@Column(name = "TGP_SEQ", precision = 4, scale = 0)
	public Short getTgpSeq() {
		return tgpSeq;
	}


	public void setTgpSeq(Short tgpSeq) {
		this.tgpSeq = tgpSeq;
	}


	public enum Fields {

		SEQ_JN("seq"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
