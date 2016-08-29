package br.gov.mec.aghu.model;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mamKapJnSeq", sequenceName="AGH.MAM_KAP_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_CAPACIDADE_ATENDS_JN", schema = "AGH")
@Immutable
public class MamCapacidadeAtendJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8870060540711102039L;
	private Integer seq;
	private Short eepEspSeq;
	private Short qtdeInicialPac;
	private Short qtdeFinalPac;
	private Short capacidadeAtend;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamCapacidadeAtendJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamKapJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "EEP_ESP_SEQ")
	public Short getEepEspSeq() {
		return eepEspSeq;
	}

	public void setEepEspSeq(Short eepEspSeq) {
		this.eepEspSeq = eepEspSeq;
	}

	@Column(name = "QTDE_INICIAL_PAC")
	public Short getQtdeInicialPac() {
		return qtdeInicialPac;
	}

	public void setQtdeInicialPac(Short qtdeInicialPac) {
		this.qtdeInicialPac = qtdeInicialPac;
	}

	@Column(name = "QTDE_FINAL_PAC")
	public Short getQtdeFinalPac() {
		return qtdeFinalPac;
	}

	public void setQtdeFinalPac(Short qtdeFinalPac) {
		this.qtdeFinalPac = qtdeFinalPac;
	}

	@Column(name = "CAPACIDADE_ATEND")
	public Short getCapacidadeAtend() {
		return capacidadeAtend;
	}

	public void setCapacidadeAtend(Short capacidadeAtend) {
		this.capacidadeAtend = capacidadeAtend;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {

		SEQ("seq"),
		EEP_ESP_SEQ("eepEspSeq"),
		QTDE_INICIAL_PAC("qtdeInicialPac"),
		QTDE_FINAL_PAC("qtdeFinalPac"),
		CAPACIDADE_ATEND("capacidadeAtend"),
		IND_SITUACAO("indSituacao");

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
