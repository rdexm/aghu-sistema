package br.gov.mec.aghu.model;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@Table(name = "SCO_CONTRATOS_JN", schema="AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ_JN"))
@SequenceGenerator(name = "scoContJnSq1", sequenceName = "AGH.SCO_CONT_jn_seq", allocationSize = 1)
@Immutable
public class ScoContratoJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6278859662291854694L;
	private Integer seq;
	private ScoCriterioReajusteContrato criterioReajusteContrato;
	private ScoTipoContratoSicon tipoContratoSicon;
	private Long nrContrato;
	private ScoFornecedor fornecedor;
	private Integer uasgSubrog;
	private Integer uasgLicit;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private String inciso;
	private Date dtPublicacao;
	private String objetoContrato;
	private String fundamentoLegal;
	private Date dtInicioVigencia;
	private Date dtFimVigencia;
	private Date dtAssinatura;
	private BigDecimal valorTotal;
	private DominioSituacaoEnvioContrato situacao;
	private ScoLicitacao licitacao;
	private RapServidores servidorGestor;
	private RapServidores servidorFiscal;



	// construtores
	
	public ScoContratoJn(){
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoContJnSq1")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	

	// getters & setters	
	@Column(name = "SEQ", length= 7, nullable = false  )	 
	public Integer getSeq(){
		return this.seq;
	}
	
	public void setSeq(Integer seq){
		this.seq = seq;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RCON_SEQ", referencedColumnName = "SEQ")
	public ScoCriterioReajusteContrato getCriterioReajusteContrato() {
		return criterioReajusteContrato;
	}

	public void setCriterioReajusteContrato(
			ScoCriterioReajusteContrato criterioReajusteContrato) {
		this.criterioReajusteContrato = criterioReajusteContrato;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TCON_SEQ", referencedColumnName = "SEQ")
	public ScoTipoContratoSicon getTipoContratoSicon() {
		return tipoContratoSicon;
	}

	public void setTipoContratoSicon(ScoTipoContratoSicon tipoContratoSicon) {
		this.tipoContratoSicon = tipoContratoSicon;
	}
		
	@Column(name = "NR_CONTRATO", length= 15, nullable = false)	 
	public Long getNrContrato(){
		return this.nrContrato;
	}
	
	public void setNrContrato(Long nrContrato){
		this.nrContrato = nrContrato;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO")
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
		
	@Column(name = "UASG_SUBROG", length= 7)	 
	public Integer getUasgSubrog(){
		return this.uasgSubrog;
	}
	
	public void setUasgSubrog(Integer uasgSubrog){
		this.uasgSubrog = uasgSubrog;
	}
		
	@Column(name = "UASG_LICIT", length= 7)	 
	public Integer getUasgLicit(){
		return this.uasgLicit;
	}
	
	public void setUasgLicit(Integer uasgLicit){
		this.uasgLicit = uasgLicit;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MLC_CODIGO", referencedColumnName = "CODIGO")
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(
			ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}
		
	@Column(name = "INCISO", length= 2)	 
	public String getInciso(){
		return this.inciso;
	}
	
	public void setInciso(String inciso){
		this.inciso = inciso;
	}
		
	@Column(name = "DT_PUBLICACAO")	 
	public Date getDtPublicacao(){
		return this.dtPublicacao;
	}
	
	public void setDtPublicacao(Date dtPublicacao){
		this.dtPublicacao = dtPublicacao;
	}
		
	@Column(name = "OBJETO_CONTRATO", length= 509, nullable = false)	 
	public String getObjetoContrato(){
		return this.objetoContrato;
	}
	
	public void setObjetoContrato(String objetoContrato){
		this.objetoContrato = objetoContrato;
	}
		
	@Column(name = "FUNDAMENTO_LEGAL", length= 141, nullable = false)	 
	public String getFundamentoLegal(){
		return this.fundamentoLegal;
	}
	
	public void setFundamentoLegal(String fundamentoLegal){
		this.fundamentoLegal = fundamentoLegal;
	}
		
	@Column(name = "DT_INICIO_VIGENCIA", nullable = false)	 
	public Date getDtInicioVigencia(){
		return this.dtInicioVigencia;
	}
	
	public void setDtInicioVigencia(Date dtInicioVigencia){
		this.dtInicioVigencia = dtInicioVigencia;
	}
		
	@Column(name = "DT_FIM_VIGENCIA", nullable = false)	 
	public Date getDtFimVigencia(){
		return this.dtFimVigencia;
	}
	
	public void setDtFimVigencia(Date dtFimVigencia){
		this.dtFimVigencia = dtFimVigencia;
	}
		
	@Column(name = "DT_ASSINATURA")	 
	public Date getDtAssinatura(){
		return this.dtAssinatura;
	}
	
	public void setDtAssinatura(Date dtAssinatura){
		this.dtAssinatura = dtAssinatura;
	}
		
	@Column(name = "VALOR_TOTAL", length= 15, scale = 2, nullable = false)	 
	public BigDecimal getValorTotal(){
		return this.valorTotal;
	}
	
	public void setValorTotal(BigDecimal valorTotal){
		this.valorTotal = valorTotal;
	}
		
	@Column(name = "IND_SITUACAO", length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEnvioContrato getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoEnvioContrato situacao) {
		this.situacao = situacao;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LCT_NUMERO", referencedColumnName = "NUMERO")
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_GESTOR", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_GESTOR", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_FISCAL", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_FISCAL", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorFiscal() {
		return servidorFiscal;
	}

	public void setServidorFiscal(RapServidores servidorFiscal) {
		this.servidorFiscal = servidorFiscal;
	}
	
	public enum Fields {

		SEQ("seq"),
		CRITERIO_REAJUSTE_CONTRATO("criterioReajusteContrato"),
		TIPO_CONTRATO_SICON("tipoContratoSicon"),
		NR_CONTRATO("nrContrato"),
		FORNECEDOR("fornecedor"),
		UASG_SUBROG("uasgSubrog"),
		UASG_LICIT("uasgLicit"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		INCISO("inciso"),
		DT_PUBLICACAO("dtPublicacao"),
		OBJETO_CONTRATO("objetoContrato"),
		FUNDAMENTO_LEGAL("fundamentoLegal"),
		DT_INICIO_VIGENCIA("dtInicioVigencia"),
		DT_FIM_VIGENCIA("dtFimVigencia"),
		DT_ASSINATURA("dtAssinatura"),
		VALOR_TOTAL("valorTotal"),
		SITUACAO("situacao"),
		LICITACAO("licitacao"),
		SERVIDOR_GESTOR("servidorGestor"),
		SERVIDOR_FISCAL("servidorFiscal");

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