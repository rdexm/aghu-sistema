package br.gov.mec.aghu.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoAditivo;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "SCO_ADIT_CONTRATOS_JN", schema="AGH")   
@SequenceGenerator(name = "scoAditContratosJnSq1", sequenceName = "AGH.SCO_ACON_JN_SEQ", allocationSize = 1)
@Immutable
public class ScoAditContratoJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7317562304040596434L;
	private Integer seqJn;
	private ScoAditContratoId id;
	private ScoContrato contrato;
	private ScoTipoContratoSicon tipoContratoSicon;	
	private Date dtInicioVigencia;
	private String objetoContrato;
	private Date dtFimVigencia;
	private BigDecimal vlAditivado;
	private Date dtAssinatura;
	private Date dtPublicacao;
	private Date dtRescisao;
	private DominioSituacaoEnvioContrato indSituacao;
	private String justificativa;
	private DominioTipoAditivo indTipoAditivo;


	// construtores
	
	public ScoAditContratoJn(){
	}


	// getters & setters
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoAditContratosJnSq1")
	@Override
	public Integer getSeqJn(){
		return this.seqJn;
	}
	
	public void setSeqJn(Integer seqJn){
		this.seqJn = seqJn;
	}

		
	//@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "contSeq", column = @Column(name = "CONT_SEQ", nullable = false, length = 7)),
		@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, length = 7)) })
	public ScoAditContratoId getId() {
		return id;
	}

	public void setId(ScoAditContratoId id) {
		this.id = id;
	}	
		
	@ManyToOne
	@JoinColumn(name = "CONT_SEQ", nullable=false,insertable=false, updatable=false)	 		
	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TCON_SEQ", referencedColumnName = "SEQ")
	public ScoTipoContratoSicon getTipoContratoSicon(){
		return tipoContratoSicon;
	}
	
	public void setTipoContratoSicon(ScoTipoContratoSicon tipoContratoSicon ){
		this.tipoContratoSicon = tipoContratoSicon;
	}
		
	@Column(name = "DT_INICIO_VIGENCIA")	 
	public Date getDtInicioVigencia(){
		return this.dtInicioVigencia;
	}
	
	public void setDtInicioVigencia(Date dtInicioVigencia){
		this.dtInicioVigencia = dtInicioVigencia;
	}
		
	@Column(name = "OBJETO_CONTRATO", length= 509, nullable = false)	 
	public String getObjetoContrato(){
		return this.objetoContrato;
	}
	
	public void setObjetoContrato(String objetoContrato){
		this.objetoContrato = objetoContrato;
	}
		
	@Column(name = "DT_FIM_VIGENCIA")	 
	public Date getDtFimVigencia(){
		return this.dtFimVigencia;
	}
	
	public void setDtFimVigencia(Date dtFimVigencia){
		this.dtFimVigencia = dtFimVigencia;
	}
		
	@Column(name = "VL_ADITIVADO", length= 15)	 
	public BigDecimal getVlAditivado(){
		return this.vlAditivado;
	}
	
	public void setVlAditivado(BigDecimal vlAditivado){
		this.vlAditivado = vlAditivado;
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
		
	@Column(name = "DT_RESCISAO")	 
	public Date getDtRescisao(){
		return this.dtRescisao;
	}
	
	public void setDtRescisao(Date dtRescisao){
		this.dtRescisao = dtRescisao;
	}
			
	@Column(name = "IND_SITUACAO", length= 2)	 
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEnvioContrato getIndSituacao(){
		return this.indSituacao;
	}
	
	public void setIndSituacao(DominioSituacaoEnvioContrato indSituacao){
		this.indSituacao = indSituacao;
	}	
		
	@Column(name = "JUSTIFICATIVA", length= 80, nullable = false)	 
	public String getJustificativa(){
		return this.justificativa;
	}
	
	public void setJustificativa(String justificativa){
		this.justificativa = justificativa;
	}
		
	@Column(name = "IND_TIPO_ADITIVO", length= 2)	 
	@Enumerated(EnumType.STRING)
	public DominioTipoAditivo getIndTipoAditivo() {
		return indTipoAditivo;
	}

	public void setIndTipoAditivo(DominioTipoAditivo indTipoAditivo) {
		this.indTipoAditivo = indTipoAditivo;
	}
			
	public enum Fields {

		SEQ_JN("seqJn"),
		ID("id"),
		CONTRATO("contrato"),
		TIPO_CONTRATO_SICON("tipoContratoSicon"),
		DT_INICIO_VIGENCIA("dtInicioVigencia"),
		OBJETO_CONTRATO("objetoContrato"),
		DT_FIM_VIGENCIA("dtFimVigencia"),
		VL_ADITIVADO("vlAditivado"),
		DT_ASSINATURA("dtAssinatura"),
		DT_PUBLICACAO("dtPublicacao"),
		DT_RESCISAO("dtRescisao"),
		IND_SITUACAO("indSituacao"),
		JUSTIFICATIVA("justificativa"),
		IND_TIPO_ADITIVO("indTipoAditivo");

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