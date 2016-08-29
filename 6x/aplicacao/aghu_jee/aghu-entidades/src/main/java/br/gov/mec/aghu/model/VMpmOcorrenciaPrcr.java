package br.gov.mec.aghu.model;

// Generated 03/02/2011 17:20:07 by Hibernate Tools 3.2.5.Beta

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioMpmOcorrenciaPrcr;
import br.gov.mec.aghu.dominio.DominioSituacaoOcorencia;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "V_MPM_OCORRENCIA_PRCR", schema = "AGH")
@Immutable
public class VMpmOcorrenciaPrcr extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 4190777147643193088L;
	
	
	private Integer phiPrcr;
	private DominioMpmOcorrenciaPrcr tipoItem;
	private Integer atdSeq;
	private Integer pmeSeq; 
	private Long pcuSeq;
	private Long ipdPdtSeq;
	private Long prpSeq;
	private Integer ipdTidSeq;
	private Long cptPnpSeq;
	private Short cptSeqp;
	private String descricaoItem;
	private String descricaoSintaxe;
	private Integer matcodigo;
	private String descricaoPhi;
	private Integer phiSeqPrcrAtual;
	private Integer matcodigoDiluente;
	private String descricaoPrcrDiluente;
	private Integer phiSeqSugerido;
	private Integer cduSeqSugerido;
	private Integer matcodigoSugerido;
	private String descricaoDiluenteSugerido;
	private Double quantidadeSugerida;
	private BigDecimal volumeSugerido;
	private Integer ocoSeq;
	private Date criadoEmOcorrencia;
	private Date alteradoEmOcorrencia;
	private RapServidores rapServidores;
	private Integer cduSeqOcorrencia;
	private DominioSituacaoOcorencia situacaoOcorrencia;


	@Column(name = "SITUACAO_OCO")
	@Enumerated(EnumType.STRING)
	public DominioSituacaoOcorencia getSituacaoOcorrencia() {
		return this.situacaoOcorrencia;
	}

	public void setSituacaoOcorrencia(DominioSituacaoOcorencia situacaoOcorrencia) {
		this.situacaoOcorrencia = situacaoOcorrencia;
	}
	
	@Column(name = "CDU_SEQ_OCO", precision = 6, scale = 0)
	public Integer getCduSeqOcorrencia() {
		return this.cduSeqOcorrencia;
	}

	public void setCduSeqOcorrencia(Integer cduSeqOcorrencia) {
		this.cduSeqOcorrencia = cduSeqOcorrencia;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_OCO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_OCO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM_OCO", length = 29)
	public Date getAlteradoEmOcorrencia() {
		return this.alteradoEmOcorrencia;
	}

	public void setAlteradoEmOcorrencia(Date alteradoEmOcorrencia) {
		this.alteradoEmOcorrencia = alteradoEmOcorrencia;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM_OCO")
	public Date getCriadoEmOcorrencia() {
		return this.criadoEmOcorrencia;
	}

	public void setCriadoEmOcorrencia(Date criadoEmOcorrencia) {
		this.criadoEmOcorrencia = criadoEmOcorrencia;
	}
	
	@Id
	@Column(name = "OCO_SEQ")
	public Integer getOcoSeq() {
		return this.ocoSeq;
	}

	public void setOcoSeq(Integer ocoSeq) {
		this.ocoSeq = ocoSeq;
	}

	@Column(name = "VOLUME_SUGERIDO", precision = 14, scale = 4)
	public BigDecimal getVolumeSugerido() {
		return this.volumeSugerido;
	}

	public void setVolumeSugerido(BigDecimal volumeSugerido) {
		this.volumeSugerido = volumeSugerido;
	}
	
	@Column(name = "QTDE_SUGERIDA", precision = 17, scale = 17)
	public Double getQuantidadeSugerida() {
		return this.quantidadeSugerida;
	}

	public void setQuantidadeSugerida(Double quantidadeSugerida) {
		this.quantidadeSugerida = quantidadeSugerida;
	}
	
	@Column(name = "DESCRICAO_DILUENTE_SUGERIDO", length = 200)
	public String getDescricaoDiluenteSugerido() {
		return this.descricaoDiluenteSugerido;
	}

	public void setDescricaoDiluenteSugerido(String descricaoDiluenteSugerido) {
		this.descricaoDiluenteSugerido = descricaoDiluenteSugerido;
	}
	
	@Column(name = "MAT_CODIGO_SUGERIDO", precision = 6, scale = 0)
	public Integer getMatCodigoSugerido() {
		return this.matcodigoSugerido;
	}

	public void setMatCodigoSugerido(Integer matcodigoSugerido) {
		this.matcodigoSugerido = matcodigoSugerido;
	}
	
	@Column(name = "CDU_SEQ_SUGERIDO", precision = 6, scale = 0)
	public Integer getCduSeqSugerido() {
		return this.cduSeqSugerido;
	}

	public void setCduSeqSugerido(Integer cduSeqSugerido) {
		this.cduSeqSugerido = cduSeqSugerido;
	}
	
	@Column(name = "PHI_SEQ_SUGERIDO", precision = 6)
	public Integer getPhiSeqSugerido() {
		return this.phiSeqSugerido;
	}	

	public void setPhiSeqSugerido(Integer phiSeqSugerido){
		this.phiSeqSugerido = phiSeqSugerido;
	}
	
	@Column(name = "DESCRICAO_PRCR_DILUENTE", length = 200)
	public String getDescricaoPrcrDiluente() {
		return this.descricaoPrcrDiluente;
	}

	public void setDescricaoPrcrDiluente(String descricaoPrcrDiluente) {
		this.descricaoPrcrDiluente = descricaoPrcrDiluente;
	}
	
	@Column(name = "MAT_CODIGO_DILUENTE", precision = 6, scale = 0)
	public Integer getMatCodigoDiluente() {
		return this.matcodigoDiluente;
	}

	public void setMatCodigoDiluente(Integer matcodigoDiluente) {
		this.matcodigoDiluente = matcodigoDiluente;
	}
	
	
	@Column(name = "PHI_SEQ_PRCR_ATUAL", precision = 6)
	public Integer getPhiSeqPrcrAtual() {
		return this.phiSeqPrcrAtual;
	}	

	public void setPhiSeqPrcrAtual(Integer phiSeqPrcrAtual){
		this.phiSeqPrcrAtual = phiSeqPrcrAtual;
	}
	
	@Column(name = "DESCRICAO_PHI", length = 200)
	public String getDescricaoPhi() {
		return this.descricaoPhi;
	}

	public void setDescricaoPhi(String descricaoPhi) {
		this.descricaoPhi = descricaoPhi;
	}
	
	@Column(name = "MAT_CODIGO", precision = 6, scale = 0)
	public Integer getMatCodigo() {
		return this.matcodigo;
	}

	public void setMatCodigo(Integer matcodigo) {
		this.matcodigo = matcodigo;
	}
	
	@Column(name = "PHI_PRCR", precision = 6)
	public Integer getPhiPrcr() {
		return this.phiPrcr;
	}	

	public void setPhiPrcr(Integer phiPrcr){
		this.phiPrcr = phiPrcr;
	}
	
	public void setDescricaoSintaxe(String descricaoSintaxe) {
		this.descricaoSintaxe = descricaoSintaxe;
	}

	@Column(name = "DESCRICAO_SINTAXE")
	public String getDescricaoSintaxe() {
		return descricaoSintaxe;
	}

	@Column(name = "DESCRICAO_ITEM", length = 4000)
	public String getDescricaoItem() {
		return this.descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}
	
	@Column(name = "CPT_SEQP", precision = 3, scale = 0)
	public Short getCptSeqp() {
		return this.cptSeqp;
	}

	public void setCptSeqp(Short cptSeqp) {
		this.cptSeqp = cptSeqp;
	}
	
	@Column(name = "CPT_PNP_SEQ", precision = 14, scale = 0)
	public Long getCptPnpSeq() {
		return this.cptPnpSeq;
	}

	public void setCptPnpSeq(Long cptPnpSeq) {
		this.cptPnpSeq = cptPnpSeq;
	}

	@Column(name = "IPD_TID_SEQ", precision = 5, scale = 0)
	public Integer getIpdTidSeq() {
		return this.ipdTidSeq;
	}

	public void setIpdTidSeq(Integer ipdTidSeq) {
		this.ipdTidSeq = ipdTidSeq;
	}

	@Column(name = "PPR_SEQ", precision = 8, scale = 0)
	public Long getPrpSeq() {
		return this.prpSeq;
	}

	public void setPrpSeq(Long prpSeq) {
		this.prpSeq = prpSeq;
	}
	
	@Column(name = "IPD_PDT_SEQ", precision = 14, scale = 0)
	public Long getIpdPdtSeq() {
		return this.ipdPdtSeq;
	}

	public void setIpdPdtSeq(Long ipdPdtSeq) {
		this.ipdPdtSeq = ipdPdtSeq;
	}
	
	@Column(name = "PCU_SEQ", precision = 14, scale = 0)
	public Long getPcuSeq() {
		return this.pcuSeq;
	}

	public void setPcuSeq(Long pcuSeq) {
		this.pcuSeq = pcuSeq;
	}
	
	@Column(name = "PME_SEQ", precision = 8, scale = 0)
	public Integer getPmeSeq() {
		return this.pmeSeq;
	}

	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@Column(name = "ATD_SEQ", precision = 7, scale = 0)
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setTipoItem(DominioMpmOcorrenciaPrcr tipoItem) {
		this.tipoItem = tipoItem;
	}

	@Column(name = "TIPO_ITEM", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioMpmOcorrenciaPrcr getTipoItem() {
		return tipoItem;
	}

	public enum Fields {
		ATD_SEQ ("atdSeq"),
		ALTERADO_EM_OCO ("alteradoEmOcorrencia"),
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


 

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getOcoSeq() == null) ? 0 : getOcoSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VMpmOcorrenciaPrcr)) {
			return false;
		}
		VMpmOcorrenciaPrcr other = (VMpmOcorrenciaPrcr) obj;
		if (getOcoSeq() == null) {
			if (other.getOcoSeq() != null) {
				return false;
			}
		} else if (!getOcoSeq().equals(other.getOcoSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Integer getSeq(){ return this.getOcoSeq();} 
 public void setSeq(Integer seq){ this.setOcoSeq(seq);}
}
