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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoRescicaoContrato;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "SCO_RES_CONTRATOS_JN", schema="AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ_JN"))
@SequenceGenerator(name = "scoResContratosJnSq1", sequenceName = "AGH.SCO_RECO_JN_SEQ", allocationSize = 1)
@Immutable
public class ScoResContratoJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3151582976173301605L;
	private Integer seqJn;
	private Integer seq;
	private ScoContrato scoContrato;
	private String justificativa;
	private DominioTipoRescicaoContrato indTipoRescisao;
	private Date dtAssinatura;
	private Date dtPublicacao;
	private DominioSituacaoEnvioContrato indSituacao;


	// construtores
	
	public ScoResContratoJn(){
	}


	// getters & setters
	@Id
	@Column(name = "SEQ_JN", length= 7, unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoResContratosJnSq1")
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
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CONT_SEQ")
	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}		
		
	@Column(name = "JUSTIFICATIVA", length= 80, nullable = false)	 
	public String getJustificativa(){
		return this.justificativa;
	}
	
	public void setJustificativa(String justificativa){
		this.justificativa = justificativa;
	}
		
	@Column(name = "IND_TIPO_RESCISAO", length= 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoRescicaoContrato getIndTipoRescisao() {
		return indTipoRescisao;
	}

	public void setIndTipoRescisao(DominioTipoRescicaoContrato indTipoRescisao) {
		this.indTipoRescisao = indTipoRescisao;
	}
		
	@Column(name = "DT_ASSINATURA")	 
	public Date getDtAssinatura(){
		return this.dtAssinatura;
	}
	
	public void setDtAssinatura(Date dtAssinatura){
		this.dtAssinatura = dtAssinatura;
	}
		
	@Column(name = "DT_PUBLICACAO")	 
	public Date getDtPublicacao(){
		return this.dtPublicacao;
	}
	
	public void setDtPublicacao(Date dtPublicacao){
		this.dtPublicacao = dtPublicacao;
	}

	@Column(name = "IND_SITUACAO", length= 2, nullable = false)	 
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEnvioContrato getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoEnvioContrato indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public enum Fields {

		SEQ_JN("seqJn"),
		SEQ("seq"),
		SCO_CONTRATO("scoContrato"),
		JUSTIFICATIVA("justificativa"),
		IND_TIPO_RESCISAO("indTipoRescisao"),
		DT_ASSINATURA("dtAssinatura"),
		DT_PUBLICACAO("dtPublicacao"),
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