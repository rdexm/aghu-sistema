package br.gov.mec.aghu.model;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MBC_ITENS_REQUISICAO_OPMES", schema = "AGH")
@SequenceGenerator(name="mbcIroSq1", sequenceName="AGH.MBC_IRO_SQ1", allocationSize = 1)
public class MbcItensRequisicaoOpmes extends BaseEntitySeq<Short> implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4579076290497981629L;

	private Short seq;
	
	private MbcRequisicaoOpmes requisicaoOpmes;
	private FatItensProcedHospitalar fatItensProcedHospitalar;
	
	private DominioRequeridoItemRequisicao requerido;
	private Boolean indCompativel;
	private Boolean indAutorizado;
	private Boolean indConsumido;
	
	private Short quantidadeAutorizadaSus;
	private Integer quantidadeSolicitada;
	private Integer quantidadeAutorizadaHospital;
	private String descricaoIncompativel;
	private String solicitacaoNovoMaterial;
	private String especificacaoNovoMaterial;
	private Integer quantidadeConsumida;
	private byte[] anexoOrcamento;
	
	private Date criadoEm;
	private Date modificadoEm;
	private RapServidores rapServidores;
	private RapServidores rapServidoresModificacao;	
	private Integer version;	
	private List<MbcMateriaisItemOpmes> materiaisItemOpmes;	
	private BigDecimal valorUnitarioIph;
	private BigDecimal valorNovoMaterial;
	private transient String nomeAnexoOrcamento;
	
	public MbcItensRequisicaoOpmes() {
	}
	
	public MbcItensRequisicaoOpmes(FatItensProcedHospitalar cmp, MbcRequisicaoOpmes requisicaoOpmes) {
		
		this.setFatItensProcedHospitalar(cmp);
		this.setRequerido(DominioRequeridoItemRequisicao.NRQ);
		this.setIndCompativel(Boolean.TRUE);
		this.setIndAutorizado(Boolean.TRUE);
		this.setIndConsumido(Boolean.FALSE);
		
		this.setQuantidadeAutorizadaSus(BigDecimal.ZERO.shortValue());
		this.setQuantidadeSolicitada(0);
		this.setValorUnitarioIph(BigDecimal.ZERO);
		
		this.setRequisicaoOpmes(requisicaoOpmes);
		
		this.setMateriaisItemOpmes(new ArrayList<MbcMateriaisItemOpmes>());
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcIroSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROP_SEQ")
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
	public RapServidores getRapServidoresModificacao() {
		return rapServidoresModificacao;
	}


	public void setRapServidoresModificacao(RapServidores rapServidoresModificacao) {
		this.rapServidoresModificacao = rapServidoresModificacao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIACAO", referencedColumnName = "MATRICULA", nullable = false),
				   @JoinColumn(name = "SER_VIN_CODIGO_CRIACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
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
	
	@Column(name = "IND_COMPATIVEL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCompativel() {
		return indCompativel;
	}

	public void setIndCompativel(Boolean indCompativel) {
		this.indCompativel = indCompativel;
	}

	@Column(name = "IND_AUTORIZADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAutorizado() {
		return indAutorizado;
	}

	public void setIndAutorizado(Boolean indAutorizado) {
		this.indAutorizado = indAutorizado;
	}

	@Column(name = "IND_REQUERIDO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioRequeridoItemRequisicao getRequerido() {
		return requerido;
	}

	public void setRequerido(DominioRequeridoItemRequisicao requerido) {
		this.requerido = requerido;
	}

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

	@Column(name = "DESC_INCOMPAT", nullable = true, length = 500)
	public String getDescricaoIncompativel() {
		return descricaoIncompativel;
	}

	public void setDescricaoIncompativel(String descricaoIncompativel) {
		this.descricaoIncompativel = descricaoIncompativel;
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
	
	@Lob
	@Type(type="org.hibernate.type.BinaryType") 
	@Column(name = "ANEXO_ORCAMENTO")
	public byte[] getAnexoOrcamento() {
		return ArrayUtils.clone(anexoOrcamento);
	}

	public void setAnexoOrcamento(byte[] anexoOrcamento) {
		this.anexoOrcamento = anexoOrcamento;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itensRequisicaoOpmes")
	public List<MbcMateriaisItemOpmes> getMateriaisItemOpmes() {
		return materiaisItemOpmes;
	}

	public void setMateriaisItemOpmes(List<MbcMateriaisItemOpmes> materiaisItemOpmes) {
		this.materiaisItemOpmes = materiaisItemOpmes;
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
	
	@Transient
	public String getSituacao() {
		if(valorNovoMaterial == null) {
			return  "Pendente";
		} else {
			return "Orçado";
		}
	}

	public void setNomeAnexoOrcamento(String nomeAnexoOrcamento) {
		this.nomeAnexoOrcamento = nomeAnexoOrcamento;
	}
	
	@Transient
	public String getNomeAnexoOrcamento() {
		return nomeAnexoOrcamento;
	}

	public enum Fields {
		
		ID("seq"),
		GRUPO_ALCADA_SEQ("grupoAlcada.seq"),
		NIVEL_ALCADA("nivelAlcada"),
		DESCRICAO("descricao"),
		VALOR_MINIMO("valorMinimo"),
		VALOR_MAXIMO("valorMaximo"),
		CRIADO_EM("criadoEm"),
		MODIFICADO_EM("modificadoEm"),
		RAP_SERVIDORES("rapServidores"),
		RAP_SERVIDORES_MODIFICACAO("rapServidoresModificacao"),
		MATERIAIS_ITEM_OPME("materiaisItemOpmes"),
		REQUISICAO_OPMES("requisicaoOpmes"),
		REQUISICAO_OPMES_SEQ("requisicaoOpmes.seq"),
		VLR_UNIT_IPH("valorUnitarioIph"),
		QTD_SOLC("quantidadeSolicitada"),
		QTD_AUTR_SUS("quantidadeAutorizadaSus"),
		QTD_AUTR_HSP("quantidadeAutorizadaHospital"),
		ITENS_PROCED_HOSPITALAR("fatItensProcedHospitalar"),
		ITENS_PROCED_HOSPITALAR_SEQ("fatItensProcedHospitalar.seq"),
		IND_AUTORIZADO("indAutorizado"),
		IND_REQUERIDO("requerido"),
		IND_COMPATIVEL("indCompativel"),
		SOLC_NOVO_MAT("solicitacaoNovoMaterial"), 
		IPH_PHO_SEQ("fatItensProcedHospitalar.id.phoSeq"),
		IPH_SEQ("fatItensProcedHospitalar.id.seq"),
		ROP_SEQ("requisicaoOpmes.seq"),
		VLR_NOVO_MAT("valorNovoMaterial"),
		ANEXO_ORCAMENTO("anexoOrcamento");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result + ((solicitacaoNovoMaterial == null) ? 0 : solicitacaoNovoMaterial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MbcItensRequisicaoOpmes other = (MbcItensRequisicaoOpmes) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getSolicitacaoNovoMaterial(), other.getSolicitacaoNovoMaterial());
		umEqualsBuilder.append(this.getDescricaoIncompativel(), other.getDescricaoIncompativel());
		umEqualsBuilder.append(this.getQuantidadeSolicitada(), other.getQuantidadeSolicitada());
		
		return umEqualsBuilder.isEquals();
	}


	

}




