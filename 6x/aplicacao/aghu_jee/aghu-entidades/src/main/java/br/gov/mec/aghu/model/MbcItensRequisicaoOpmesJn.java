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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.ArrayUtils;

import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MBC_ITENS_REQUISICAO_OPMES_JN", schema = "AGH")
@SequenceGenerator(name="mbcIroJnSeq", sequenceName="AGH.mbc_iro_jn_seq",  allocationSize = 1)
public class MbcItensRequisicaoOpmesJn extends BaseJournal implements Serializable {

	private static final long serialVersionUID = -4389718557918157510L;

	private Short seq;
	
	private MbcRequisicaoOpmes requisicaoOpmes;
	private FatItensProcedHospitalar fatItensProcedHospitalar;
	private DominioRequeridoItemRequisicao requerido;
	private Boolean indCompativel;
	private Boolean indAutorizado;
	private Boolean indConsumido;
	private Short quantidadeAutorizadaSus;
	private BigDecimal valorUnitarioIph;
	private Integer quantidadeSolicitada;
	private Integer quantidadeAutorizadaHospital;
	private String solicitacaoNovoMaterial;
	private BigDecimal valorNovoMaterial;
	private String especificacaoNovoMaterial;
	private Integer quantidadeConsumida;
	private byte[] anexoOrcamento;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Date modificadoEm;
	private RapServidores rapServidoresModificacao;
	
	public MbcItensRequisicaoOpmesJn() {}	

	public MbcItensRequisicaoOpmesJn(Short seq,
			MbcRequisicaoOpmes requisicaoOpmes,
			FatItensProcedHospitalar fatItensProcedHospitalar,
			DominioRequeridoItemRequisicao requerido, 
			Boolean indCompativel,
			Boolean indAutorizado, Boolean indConsumido,
			Short quantidadeAutorizadaSus, BigDecimal valorUnitarioIph,
			Integer quantidadeSolicitada, Integer quantidadeAutorizadaHospital,
			String solicitacaoNovoMaterial, BigDecimal valorNovoMaterial,
			String especificacaoNovoMaterial, Integer quantidadeConsumida,
			byte[] anexoOrcamento, Date criadoEm, RapServidores rapServidores,
			Date modificadoEm, RapServidores rapServidoresModificacao) {
		super();
		this.seq = seq;
		this.requisicaoOpmes = requisicaoOpmes;
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
		this.requerido = requerido;
		this.indCompativel = indCompativel;
		this.indAutorizado = indAutorizado;
		this.indConsumido = indConsumido;
		this.quantidadeAutorizadaSus = quantidadeAutorizadaSus;
		this.valorUnitarioIph = valorUnitarioIph;
		this.quantidadeSolicitada = quantidadeSolicitada;
		this.quantidadeAutorizadaHospital = quantidadeAutorizadaHospital;
		this.solicitacaoNovoMaterial = solicitacaoNovoMaterial;
		this.valorNovoMaterial = valorNovoMaterial;
		this.especificacaoNovoMaterial = especificacaoNovoMaterial;
		this.quantidadeConsumida = quantidadeConsumida;
		this.anexoOrcamento = anexoOrcamento;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.modificadoEm = modificadoEm;
		this.rapServidoresModificacao = rapServidoresModificacao;
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcIroJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROP_SEQ")
	@NotNull
	public MbcRequisicaoOpmes getRequisicaoOpmes() {
		return requisicaoOpmes;
	}

	public void setRequisicaoOpmes(MbcRequisicaoOpmes requisicaoOpmes) {
		this.requisicaoOpmes = requisicaoOpmes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "IPH_PHO_SEQ", referencedColumnName = "PHO_SEQ", nullable=true),
			@JoinColumn(name = "IPH_SEQ", referencedColumnName = "SEQ", nullable=true) })
	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}

	public void setFatItensProcedHospitalar(FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFICADO_EM", length = 29)
	public Date getModificadoEm() {
		return modificadoEm;
	}


	public void setModificadoEm(Date modificadoEm) {
		this.modificadoEm = modificadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MODIFICACAO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_MODIFICACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getRapServidoresModificacao() {
		return rapServidoresModificacao;
	}


	public void setRapServidoresModificacao(RapServidores rapServidoresModificacao) {
		this.rapServidoresModificacao = rapServidoresModificacao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIACAO", referencedColumnName = "MATRICULA", nullable = false),
				   @JoinColumn(name = "SER_VIN_CODIGO_CRIACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@NotNull
	@Column(name = "IND_COMPATIVEL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCompativel() {
		return indCompativel;
	}

	public void setIndCompativel(Boolean indCompativel) {
		this.indCompativel = indCompativel;
	}

	@NotNull
	@Column(name = "IND_AUTORIZADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAutorizado() {
		return indAutorizado;
	}

	public void setIndAutorizado(Boolean indAutorizado) {
		this.indAutorizado = indAutorizado;
	}

	@Column(name = "IND_REQUERIDO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioRequeridoItemRequisicao getRequerido() {
		return requerido;
	}

	public void setRequerido(DominioRequeridoItemRequisicao requerido) {
		this.requerido = requerido;
	}

	@NotNull
	@Column(name = "IND_CONSUMIDO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsumido() {
		return indConsumido;
	}

	public void setIndConsumido(Boolean indConsumido) {
		this.indConsumido = indConsumido;
	}

	@Column(name="QTD_AUTR_SUS")
	public Short getQuantidadeAutorizadaSus() {
		return quantidadeAutorizadaSus;
	}

	public void setQuantidadeAutorizadaSus(Short quantidadeAutorizadaSus) {
		this.quantidadeAutorizadaSus = quantidadeAutorizadaSus;
	}

	@Column(name="QTD_SOLC")
	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	@Column(name="QTD_AUTR_HSP")
	public Integer getQuantidadeAutorizadaHospital() {
		return quantidadeAutorizadaHospital;
	}

	public void setQuantidadeAutorizadaHospital(Integer quantidadeAutorizadaHospital) {
		this.quantidadeAutorizadaHospital = quantidadeAutorizadaHospital;
	}

	@Column(name = "SOLC_NOVO_MAT", length = 2000)
	public String getSolicitacaoNovoMaterial() {
		return solicitacaoNovoMaterial;
	}

	public void setSolicitacaoNovoMaterial(String solicitacaoNovoMaterial) {
		this.solicitacaoNovoMaterial = solicitacaoNovoMaterial;
	}

	@Column(name = "ESPEC_NOVO_MAT", length = 2000)
	public String getEspecificacaoNovoMaterial() {
		return especificacaoNovoMaterial;
	}

	public void setEspecificacaoNovoMaterial(String especificacaoNovoMaterial) {
		this.especificacaoNovoMaterial = especificacaoNovoMaterial;
	}

	@Column(name="QTD_CONS")
	public Integer getQuantidadeConsumida() {
		return quantidadeConsumida;
	}

	public void setQuantidadeConsumida(Integer quantidadeConsumida) {
		this.quantidadeConsumida = quantidadeConsumida;
	}
	
//	@Type(type="org.hibernate.type.BinaryType") 
	@Column(name = "ANEXO_ORCAMENTO")
	public byte[] getAnexoOrcamento() {
		return ArrayUtils.clone(anexoOrcamento);
	}

	public void setAnexoOrcamento(byte[] anexoOrcamento) {
		this.anexoOrcamento = anexoOrcamento;
	}
	
	@Column(name = "VLR_UNIT", precision = 14, scale = 2)
	public BigDecimal getValorUnitarioIph() {
		return valorUnitarioIph;
	}

	public void setValorUnitarioIph(BigDecimal valorUnitarioIph) {
		this.valorUnitarioIph = valorUnitarioIph;
	}
	
	public void setValorNovoMaterial(BigDecimal valorNovoMaterial) {
		this.valorNovoMaterial = valorNovoMaterial;
	}
	
	@Column(name = "VLR_NOVO_MAT", precision = 14, scale = 2)
	public BigDecimal getValorNovoMaterial() {
		return valorNovoMaterial;
	}
}
