package br.gov.mec.aghu.model;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "SCO_AF_CONTRATOS_JN", schema="AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ_JN"))
@SequenceGenerator(name = "scoAfCoJnSq1", sequenceName = "AGH.SCO_AFCO_jn_seq", allocationSize = 1)
@Immutable
public class ScoAfContratoJn extends BaseJournal implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6835523591856821520L;
	private Integer seqJn;
	private Integer seq;
	private ScoAutorizacaoForn scoAutorizacoesForn;
	private ScoContrato scoContrato;


	// construtores
	
	public ScoAfContratoJn(){
	}


	// getters & setters
	@Id
	@Column(name = "SEQ_JN", length= 7, unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoAfCoJnSq1")
	@Override	
	public Integer getSeqJn(){
		return this.seqJn;
	}
	
	public void setSeqJn(Integer seqJn){
		this.seqJn = seqJn;
	}
		
	@Column(name = "SEQ", length= 7, nullable = false)	 
	public Integer getSeq(){
		return this.seq;
	}
	
	public void setSeq(Integer seq){
		this.seq = seq;
	}
		
	// bi-directional many-to-one association to ScoAutorizacoesForn
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO")
	public ScoAutorizacaoForn getScoAutorizacoesForn() {
		return this.scoAutorizacoesForn;
	}

	public void setScoAutorizacoesForn(ScoAutorizacaoForn scoAutorizacoesForn) {
		this.scoAutorizacoesForn = scoAutorizacoesForn;
	}
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CONT_SEQ")
	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}		
	
	public enum Fields {

		SEQ_JN("seqJn"),
		SEQ("seq"),
		SCO_AUTORIZACOES_FORN("scoAutorizacoesForn"),
		SCO_CONTRATO("scoContrato");

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