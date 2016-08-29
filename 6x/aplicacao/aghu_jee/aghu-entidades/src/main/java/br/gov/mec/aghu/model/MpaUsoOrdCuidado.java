package br.gov.mec.aghu.model;


import java.io.Serializable;
import java.sql.Timestamp;

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


import br.gov.mec.aghu.dominio.DominioSituacaoOrdProtocolo;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mpa_uso_ord_cuidados database table.
 * 
 */
@Entity
@SequenceGenerator(name="mpaUocSq1", sequenceName="AGH.MPA_UOC_SQ1", allocationSize = 1) 
@Table(name="MPA_USO_ORD_CUIDADOS")

public class MpaUsoOrdCuidado extends BaseEntitySeq<Integer> implements Serializable {
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3375083961608372194L;

	private Integer seq;
	
	private MpmCuidadoUsual cuidadoUsual;

	//TODO: Mapear para o POJO da tabela MPA_CAD_ORD_CUIDADOS quando este for criado
	private Integer couSeq;

	private Timestamp criadoEm;

	private String descricao;

	private Integer frequencia;

	private DominioSituacaoOrdProtocolo indSituacao;	
	
	private MpmPrescricaoCuidado prescricaoCuidado;

	private MpmPrescricaoCuidado prescricaoCuidadoAnt;
	
	private RapServidores servidor;
	
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	
	/*------------------------------------------------------------------------
	* TODO: Substituir os atributos a seguir pelo pojo da tabela MPA_USO_ATIVIDADES 
	* quanto este for criado
	* */

	private Integer uatCafCitSeqp;

	private Integer uatCafCitVpaPtaSeq;

	private Integer uatCafCitVpaSeqp;

	private Integer uatCafSeqp;

	private Integer uatUspApaAtdSeq;

	private Integer uatUspApaSeq;

	private Integer uatUspSeq;

	private Integer uatUspVpaPtaSeq;

	private Integer uatUspVpaSeqp;
	
	/*------------------------------------------------------------------------
	* TODO: Substituir os atributos a seguir pelo pojo da tabela MPA_USO_AVALIACOES 
	* quanto este for criado
	* */

	private Integer uavCvlCitSeqp;

	private Integer uavCvlCitVpaPtaSeq;

	private Integer uavCvlCitVpaSeqp;

	private Integer uavCvlSeqp;

	private Integer uavUspApaAtdSeq;

	private Integer uavUspApaSeq;

	private Integer uavUspSeq;

	private Integer uavUspVpaPtaSeq;

	private Integer uavUspVpaSeqp;
	
	/*------------------------------------------------------------------------
	* TODO: Substituir os atributos a seguir pelo pojo da tabela MPA_USO_CUIDADOS  
	* quanto este for criado
	* */

	private Integer ucuCcdCitSeqp;

	private Integer ucuCcdCitVpaPtaSeq;

	private Integer ucuCcdCitVpaSeqp;

	private Integer ucuCcdSeqp;

	private Integer ucuUspApaAtdSeq;

	private Integer ucuUspApaSeq;

	private Integer ucuUspSeq;

	private Integer ucuUspVpaPtaSeq;

	private Integer ucuUspVpaSeqp;
	
	/*-------------------------------------------------*/

	private MpaUsoOrdCuidado usoOrdCuidado;
	
	//TODO: Criar no banco o campo version
	//private Integer version;


    public MpaUsoOrdCuidado() {
    }

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpaUocSq1")
	@Column(name = "SEQ", nullable=false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDU_SEQ", referencedColumnName="SEQ", nullable = false)
	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}


	@Column(name="COU_SEQ")
	public Integer getCouSeq() {
		return this.couSeq;
	}

	public void setCouSeq(Integer couSeq) {
		this.couSeq = couSeq;
	}

	@Column(name="CRIADO_EM", nullable=false)
	public Timestamp getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Timestamp criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name="DESCRICAO", length=120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name="FREQUENCIA")
	public Integer getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}
	
	@Column(name = "IND_SITUACAO", nullable=false, length=2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoOrdProtocolo getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoOrdProtocolo indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PCU_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PCU_SEQ", referencedColumnName = "SEQ") })
	public MpmPrescricaoCuidado getPrescricaoCuidado() {
		return prescricaoCuidado;
	}

	public void setPrescricaoCuidado(MpmPrescricaoCuidado prescricaoCuidado) {
		this.prescricaoCuidado = prescricaoCuidado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PCU_ATD_SEQ_ANT", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PCU_SEQ_ANT", referencedColumnName = "SEQ") })
	public MpmPrescricaoCuidado getPrescricaoCuidadoAnt() {
		return prescricaoCuidadoAnt;
	}

	public void setPrescricaoCuidadoAnt(MpmPrescricaoCuidado prescricaoCuidadoAnt) {
		this.prescricaoCuidadoAnt = prescricaoCuidadoAnt;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ", referencedColumnName="SEQ")
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	@Column(name="UAT_CAF_CIT_SEQP")
	public Integer getUatCafCitSeqp() {
		return this.uatCafCitSeqp;
	}

	public void setUatCafCitSeqp(Integer uatCafCitSeqp) {
		this.uatCafCitSeqp = uatCafCitSeqp;
	}

	@Column(name="UAT_CAF_CIT_VPA_PTA_SEQ")
	public Integer getUatCafCitVpaPtaSeq() {
		return this.uatCafCitVpaPtaSeq;
	}

	public void setUatCafCitVpaPtaSeq(Integer uatCafCitVpaPtaSeq) {
		this.uatCafCitVpaPtaSeq = uatCafCitVpaPtaSeq;
	}

	@Column(name="UAT_CAF_CIT_VPA_SEQP")
	public Integer getUatCafCitVpaSeqp() {
		return this.uatCafCitVpaSeqp;
	}

	public void setUatCafCitVpaSeqp(Integer uatCafCitVpaSeqp) {
		this.uatCafCitVpaSeqp = uatCafCitVpaSeqp;
	}

	@Column(name="UAT_CAF_SEQP")
	public Integer getUatCafSeqp() {
		return this.uatCafSeqp;
	}

	public void setUatCafSeqp(Integer uatCafSeqp) {
		this.uatCafSeqp = uatCafSeqp;
	}

	@Column(name="UAT_USP_APA_ATD_SEQ")
	public Integer getUatUspApaAtdSeq() {
		return this.uatUspApaAtdSeq;
	}

	public void setUatUspApaAtdSeq(Integer uatUspApaAtdSeq) {
		this.uatUspApaAtdSeq = uatUspApaAtdSeq;
	}

	@Column(name="UAT_USP_APA_SEQ")
	public Integer getUatUspApaSeq() {
		return this.uatUspApaSeq;
	}

	public void setUatUspApaSeq(Integer uatUspApaSeq) {
		this.uatUspApaSeq = uatUspApaSeq;
	}

	@Column(name="UAT_USP_SEQ")
	public Integer getUatUspSeq() {
		return this.uatUspSeq;
	}

	public void setUatUspSeq(Integer uatUspSeq) {
		this.uatUspSeq = uatUspSeq;
	}

	@Column(name="UAT_USP_VPA_PTA_SEQ")
	public Integer getUatUspVpaPtaSeq() {
		return this.uatUspVpaPtaSeq;
	}

	public void setUatUspVpaPtaSeq(Integer uatUspVpaPtaSeq) {
		this.uatUspVpaPtaSeq = uatUspVpaPtaSeq;
	}

	@Column(name="UAT_USP_VPA_SEQP")
	public Integer getUatUspVpaSeqp() {
		return this.uatUspVpaSeqp;
	}

	public void setUatUspVpaSeqp(Integer uatUspVpaSeqp) {
		this.uatUspVpaSeqp = uatUspVpaSeqp;
	}

	@Column(name="UAV_CVL_CIT_SEQP")
	public Integer getUavCvlCitSeqp() {
		return this.uavCvlCitSeqp;
	}

	public void setUavCvlCitSeqp(Integer uavCvlCitSeqp) {
		this.uavCvlCitSeqp = uavCvlCitSeqp;
	}

	@Column(name="UAV_CVL_CIT_VPA_PTA_SEQ")
	public Integer getUavCvlCitVpaPtaSeq() {
		return this.uavCvlCitVpaPtaSeq;
	}

	public void setUavCvlCitVpaPtaSeq(Integer uavCvlCitVpaPtaSeq) {
		this.uavCvlCitVpaPtaSeq = uavCvlCitVpaPtaSeq;
	}

	@Column(name="UAV_CVL_CIT_VPA_SEQP")
	public Integer getUavCvlCitVpaSeqp() {
		return this.uavCvlCitVpaSeqp;
	}

	public void setUavCvlCitVpaSeqp(Integer uavCvlCitVpaSeqp) {
		this.uavCvlCitVpaSeqp = uavCvlCitVpaSeqp;
	}

	@Column(name="UAV_CVL_SEQP")
	public Integer getUavCvlSeqp() {
		return this.uavCvlSeqp;
	}

	public void setUavCvlSeqp(Integer uavCvlSeqp) {
		this.uavCvlSeqp = uavCvlSeqp;
	}

	@Column(name="UAV_USP_APA_ATD_SEQ")
	public Integer getUavUspApaAtdSeq() {
		return this.uavUspApaAtdSeq;
	}

	public void setUavUspApaAtdSeq(Integer uavUspApaAtdSeq) {
		this.uavUspApaAtdSeq = uavUspApaAtdSeq;
	}

	@Column(name="UAV_USP_APA_SEQ")
	public Integer getUavUspApaSeq() {
		return this.uavUspApaSeq;
	}

	public void setUavUspApaSeq(Integer uavUspApaSeq) {
		this.uavUspApaSeq = uavUspApaSeq;
	}

	@Column(name="UAV_USP_SEQ")
	public Integer getUavUspSeq() {
		return this.uavUspSeq;
	}

	public void setUavUspSeq(Integer uavUspSeq) {
		this.uavUspSeq = uavUspSeq;
	}

	@Column(name="UAV_USP_VPA_PTA_SEQ")
	public Integer getUavUspVpaPtaSeq() {
		return this.uavUspVpaPtaSeq;
	}

	public void setUavUspVpaPtaSeq(Integer uavUspVpaPtaSeq) {
		this.uavUspVpaPtaSeq = uavUspVpaPtaSeq;
	}

	@Column(name="UAV_USP_VPA_SEQP")
	public Integer getUavUspVpaSeqp() {
		return this.uavUspVpaSeqp;
	}

	public void setUavUspVpaSeqp(Integer uavUspVpaSeqp) {
		this.uavUspVpaSeqp = uavUspVpaSeqp;
	}

	@Column(name="UCU_CCD_CIT_SEQP")
	public Integer getUcuCcdCitSeqp() {
		return this.ucuCcdCitSeqp;
	}

	public void setUcuCcdCitSeqp(Integer ucuCcdCitSeqp) {
		this.ucuCcdCitSeqp = ucuCcdCitSeqp;
	}

	@Column(name="UCU_CCD_CIT_VPA_PTA_SEQ")
	public Integer getUcuCcdCitVpaPtaSeq() {
		return this.ucuCcdCitVpaPtaSeq;
	}

	public void setUcuCcdCitVpaPtaSeq(Integer ucuCcdCitVpaPtaSeq) {
		this.ucuCcdCitVpaPtaSeq = ucuCcdCitVpaPtaSeq;
	}

	@Column(name="UCU_CCD_CIT_VPA_SEQP")
	public Integer getUcuCcdCitVpaSeqp() {
		return this.ucuCcdCitVpaSeqp;
	}

	public void setUcuCcdCitVpaSeqp(Integer ucuCcdCitVpaSeqp) {
		this.ucuCcdCitVpaSeqp = ucuCcdCitVpaSeqp;
	}

	@Column(name="UCU_CCD_SEQP")
	public Integer getUcuCcdSeqp() {
		return this.ucuCcdSeqp;
	}

	public void setUcuCcdSeqp(Integer ucuCcdSeqp) {
		this.ucuCcdSeqp = ucuCcdSeqp;
	}

	@Column(name="UCU_USP_APA_ATD_SEQ")
	public Integer getUcuUspApaAtdSeq() {
		return this.ucuUspApaAtdSeq;
	}

	public void setUcuUspApaAtdSeq(Integer ucuUspApaAtdSeq) {
		this.ucuUspApaAtdSeq = ucuUspApaAtdSeq;
	}

	@Column(name="UCU_USP_APA_SEQ")
	public Integer getUcuUspApaSeq() {
		return this.ucuUspApaSeq;
	}

	public void setUcuUspApaSeq(Integer ucuUspApaSeq) {
		this.ucuUspApaSeq = ucuUspApaSeq;
	}

	@Column(name="UCU_USP_SEQ")
	public Integer getUcuUspSeq() {
		return this.ucuUspSeq;
	}

	public void setUcuUspSeq(Integer ucuUspSeq) {
		this.ucuUspSeq = ucuUspSeq;
	}

	@Column(name="UCU_USP_VPA_PTA_SEQ")
	public Integer getUcuUspVpaPtaSeq() {
		return this.ucuUspVpaPtaSeq;
	}

	public void setUcuUspVpaPtaSeq(Integer ucuUspVpaPtaSeq) {
		this.ucuUspVpaPtaSeq = ucuUspVpaPtaSeq;
	}

	@Column(name="UCU_USP_VPA_SEQP")
	public Integer getUcuUspVpaSeqp() {
		return this.ucuUspVpaSeqp;
	}

	public void setUcuUspVpaSeqp(Integer ucuUspVpaSeqp) {
		this.ucuUspVpaSeqp = ucuUspVpaSeqp;
	}

	//bi-directional many-to-one association to MpaUsoOrdCuidado
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="UOC_SEQ")
	public MpaUsoOrdCuidado getUsoOrdCuidado() {
		return this.usoOrdCuidado;
	}

	public void setUsoOrdCuidado(MpaUsoOrdCuidado usoOrdCuidado) {
		this.usoOrdCuidado = usoOrdCuidado;
	}

	
	public enum Fields {
		
		SEQ("seq"), PCU_ATD_SEQ("prescricaoCuidado.id.atdSeq"), PCU_SEQ("prescricaoCuidado.id.seq"), 
		CDU("cuidadoUsual");	

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
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpaUsoOrdCuidado other = (MpaUsoOrdCuidado) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}