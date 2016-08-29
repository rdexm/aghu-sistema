package br.gov.mec.aghu.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Digits;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoAditivo;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "SCO_ADIT_CONTRATOS", schema="AGH")
public class ScoAditContrato extends BaseEntityId<ScoAditContratoId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2776890013571615296L;
	private ScoAditContratoId id;
	private ScoContrato cont;
	private Date dtInicioVigencia;
	private String objetoContrato;
	private Date dtFimVigencia;
	private BigDecimal vlAditivado;
	private DominioSituacaoEnvioContrato situacao;
	private String observacao;
	private String justificativa;
	private Date alteradoEm;
	private Date dataRescicao;
	private Date dataAssinatura;
	private Date dataPublicacao;
	private Date criadoEm;
	private Integer version;
	private ScoTipoContratoSicon tipoContratoSicon;	
	private RapServidores servidor;	
	private DominioTipoAditivo indTipoAditivo;

	// construtores
	
	public ScoAditContrato(){
	}

	// getters & setters

	
	@EmbeddedId
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
	public ScoContrato getCont() {
		return cont;
	}

	public void setCont(ScoContrato cont) {
		this.cont = cont;
	}

	@Column(name = "DT_INICIO_VIGENCIA")	 
	@Temporal(TemporalType.DATE)
	public Date getDtInicioVigencia(){
		return this.dtInicioVigencia;
	}
	
	public void setDtInicioVigencia(Date dtInicioVigencia){
		this.dtInicioVigencia = dtInicioVigencia;
	}
		
	@Column(name = "OBJETO_CONTRATO", length= 509)	 
	public String getObjetoContrato(){
		return this.objetoContrato;
	}
	
	public void setObjetoContrato(String objetoContrato){
		this.objetoContrato = objetoContrato;
	}
		
	@Column(name = "DT_FIM_VIGENCIA")	
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDtFimVigencia(){
		return this.dtFimVigencia;
	}
	
	public void setDtFimVigencia(Date dtFimVigencia){
		this.dtFimVigencia = dtFimVigencia;
	}
		
	@Column(name = "VL_ADITIVADO", precision = 15, scale = 2)	 
	@Digits(integer=13, fraction=2, message="Valor aditivado dever ter no máximo 13 números inteiros e 2 decimais")
	public BigDecimal getVlAditivado(){
		return this.vlAditivado;
	}
	
	public void setVlAditivado(BigDecimal vlAditivado){
		this.vlAditivado = vlAditivado;
	}
		
	@Column(name = "IND_SITUACAO", length= 2)	 
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEnvioContrato getSituacao(){
		return this.situacao;
	}
	
	public void setSituacao(DominioSituacaoEnvioContrato situacao){
		this.situacao = situacao;
	}
		
	@Column(name = "OBSERVACAO", length= 80)	 
	public String getObservacao(){
		return this.observacao;
	}
	
	public void setObservacao(String observacao){
		this.observacao = observacao;
	}
	
	@Column(name = "JUSTIFICATIVA", length= 80, nullable=false)		
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "ALTERADO_EM")	
    @Temporal(TemporalType.TIMESTAMP)	
	public Date getAlteradoEm(){
		return this.alteradoEm;
	}
	
	public void setAlteradoEm(Date alteradoEm){
		this.alteradoEm = alteradoEm;
	}
	@Column(name = "DT_RESCISAO")	
    @Temporal(TemporalType.TIMESTAMP)			
	public Date getDataRescicao() {
		return dataRescicao;
	}

	public void setDataRescicao(Date dataRescicao) {
		this.dataRescicao = dataRescicao;
	}

	@Column(name = "DT_ASSINATURA")	
    @Temporal(TemporalType.TIMESTAMP)	
	public Date getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(Date dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	@Column(name = "DT_PUBLICACAO")	
    @Temporal(TemporalType.TIMESTAMP)	
	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	@Column(name = "CRIADO_EM")	 
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm(){
		return this.criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm){
		this.criadoEm = criadoEm;
	}
		
	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion(){
		return this.version;
	}
	
	public void setVersion(Integer version){
		this.version = version;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TCON_SEQ", referencedColumnName = "SEQ")
	public ScoTipoContratoSicon getTipoContratoSicon(){
		return tipoContratoSicon;
	}
	
	public void setTipoContratoSicon(ScoTipoContratoSicon tipoContratoSicon ){
		this.tipoContratoSicon = tipoContratoSicon;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO"),
	    @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA") })
	public RapServidores getServidor(){
		return servidor;
	}
	
	public void setServidor(RapServidores servidor ){
		this.servidor = servidor;
	}

	@Column(name = "IND_TIPO_ADITIVO", length= 2)	 
	@Enumerated(EnumType.STRING)
	public DominioTipoAditivo getIndTipoAditivo() {
		return indTipoAditivo;
	}

	public void setIndTipoAditivo(DominioTipoAditivo indTipoAditivo) {
		this.indTipoAditivo = indTipoAditivo;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoAditContrato)){
			return false;
		}			
		ScoAditContrato castOther = (ScoAditContrato) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
			NUMERO("id.seq"), 
			CONT_SEQ("cont.seq"), 
			DT_INICIO_VIGENCIA("dtInicioVigencia"), 
			OBJETO_CONTRATO("objetoContrato"), 
			DT_FIM_VIGENCIA("dtFimVigencia"), 
			VL_ADITIVADO("vlAditivado"), 
			SITUACAO("situacao"), 
			OBSERVACAO("observacao"), 
			ALTERADO_EM("alteradoEm"), 
			CRIADO_EM("criadoEm"), 
			VERSION("version"),
			TIPO_CONTRATO_SICON("tipoContratoSicon"), 
			SERVIDOR("servidor"), 
			DT_RECISAO("dataRescicao"),
			CONTRATO("cont"),
			CONT_NUM("cont.nrContrato")
		;
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}	
	
}