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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacaoOrdProtocolo;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mpa_uso_ord_nutricoes database table.
 * 
 */
@Entity
@SequenceGenerator(name="mpaUonSq1", sequenceName="AGH.MPA_UON_SQ1", allocationSize = 1)   
@Table(name="MPA_USO_ORD_NUTRICOES")
public class MpaUsoOrdNutricao extends BaseEntitySeq<Integer> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2892229537450005784L;

	private Integer seq;
	
	/*------------------------------------------------------------------------
	* TODO: Substituir o atributo a seguir pelo pojo da tabela MPA_CAD_ORD_NUTRICOES 
	* quanto este for criado
	* */
	private Integer corSeq;
	
	
	private Date criadoEm;
	private Boolean indAvalNutricionista;
	private DominioSituacaoOrdProtocolo indSituacao;
	private String observacao;
	
	private MpmPrescricaoDieta prescricaoDieta;
	
	private MpmPrescricaoDieta prescricaoDietaAnt;
	
	private RapServidores servidor;
	
	/*------------------------------------------------------------------------
	* TODO: Substituir os atributos a seguir pelo pojo da tabela MPA_USO_NUTRICOES  
	* quanto este for criado
	* */
	private Integer unuCnuCitSeqp;
	private Integer unuCnuCitVpaPtaSeq;
	private Integer unuCnuCitVpaSeqp;
	private Integer unuCnuSeqp;
	private Integer unuUspApaAtdSeq;
	private Integer unuUspApaSeq;
	private Integer unuUspSeq;
	private Integer unuUspVpaPtaSeq;
	private Integer unuUspVpaSeqp;
	
	private MpaUsoOrdNutricao usoOrdNutricao;
	
	//TODO: Criar no banco o campo version
	//private Integer version;

    public MpaUsoOrdNutricao() {
    }


    @Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpaUonSq1")
	@Column(name = "SEQ", nullable=false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Column(name="COR_SEQ")
	public Integer getCorSeq() {
		return this.corSeq;
	}

	public void setCorSeq(Integer corSeq) {
		this.corSeq = corSeq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "IND_AVAL_NUTRICIONISTA", nullable=false, length=1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAvalNutricionista() {
		return this.indAvalNutricionista;
	}

	public void setIndAvalNutricionista(Boolean indAvalNutricionista) {
		this.indAvalNutricionista = indAvalNutricionista;
	}
	
	@Column(name = "IND_SITUACAO", nullable=false, length=2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoOrdProtocolo getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoOrdProtocolo indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(length=500)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PDT_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PDT_SEQ", referencedColumnName = "SEQ") })
	public MpmPrescricaoDieta getPrescricaoDieta() {
		return prescricaoDieta;
	}


	public void setPrescricaoDieta(MpmPrescricaoDieta prescricaoDieta) {
		this.prescricaoDieta = prescricaoDieta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PDT_ATD_SEQ_ANT", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PDT_SEQ_ANT", referencedColumnName = "SEQ") })
	public MpmPrescricaoDieta getPrescricaoDietaAnt() {
		return prescricaoDietaAnt;
	}


	public void setPrescricaoDietaAnt(MpmPrescricaoDieta prescricaoDietaAnt) {
		this.prescricaoDietaAnt = prescricaoDietaAnt;
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

	@Column(name="UNU_CNU_CIT_SEQP", nullable=false)
	public Integer getUnuCnuCitSeqp() {
		return this.unuCnuCitSeqp;
	}

	public void setUnuCnuCitSeqp(Integer unuCnuCitSeqp) {
		this.unuCnuCitSeqp = unuCnuCitSeqp;
	}


	@Column(name="UNU_CNU_CIT_VPA_PTA_SEQ", nullable=false)
	public Integer getUnuCnuCitVpaPtaSeq() {
		return this.unuCnuCitVpaPtaSeq;
	}

	public void setUnuCnuCitVpaPtaSeq(Integer unuCnuCitVpaPtaSeq) {
		this.unuCnuCitVpaPtaSeq = unuCnuCitVpaPtaSeq;
	}


	@Column(name="UNU_CNU_CIT_VPA_SEQP", nullable=false)
	public Integer getUnuCnuCitVpaSeqp() {
		return this.unuCnuCitVpaSeqp;
	}

	public void setUnuCnuCitVpaSeqp(Integer unuCnuCitVpaSeqp) {
		this.unuCnuCitVpaSeqp = unuCnuCitVpaSeqp;
	}


	@Column(name="UNU_CNU_SEQP", nullable=false)
	public Integer getUnuCnuSeqp() {
		return this.unuCnuSeqp;
	}

	public void setUnuCnuSeqp(Integer unuCnuSeqp) {
		this.unuCnuSeqp = unuCnuSeqp;
	}


	@Column(name="UNU_USP_APA_ATD_SEQ", nullable=false)
	public Integer getUnuUspApaAtdSeq() {
		return this.unuUspApaAtdSeq;
	}

	public void setUnuUspApaAtdSeq(Integer unuUspApaAtdSeq) {
		this.unuUspApaAtdSeq = unuUspApaAtdSeq;
	}


	@Column(name="UNU_USP_APA_SEQ", nullable=false)
	public Integer getUnuUspApaSeq() {
		return this.unuUspApaSeq;
	}

	public void setUnuUspApaSeq(Integer unuUspApaSeq) {
		this.unuUspApaSeq = unuUspApaSeq;
	}


	@Column(name="UNU_USP_SEQ", nullable=false)
	public Integer getUnuUspSeq() {
		return this.unuUspSeq;
	}

	public void setUnuUspSeq(Integer unuUspSeq) {
		this.unuUspSeq = unuUspSeq;
	}


	@Column(name="UNU_USP_VPA_PTA_SEQ", nullable=false)
	public Integer getUnuUspVpaPtaSeq() {
		return this.unuUspVpaPtaSeq;
	}

	public void setUnuUspVpaPtaSeq(Integer unuUspVpaPtaSeq) {
		this.unuUspVpaPtaSeq = unuUspVpaPtaSeq;
	}


	@Column(name="UNU_USP_VPA_SEQP", nullable=false)
	public Integer getUnuUspVpaSeqp() {
		return this.unuUspVpaSeqp;
	}

	public void setUnuUspVpaSeqp(Integer unuUspVpaSeqp) {
		this.unuUspVpaSeqp = unuUspVpaSeqp;
	}

	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="UON_SEQ")
	public MpaUsoOrdNutricao getUsoOrdNutricao() {
		return usoOrdNutricao;
	}


	public void setUsoOrdNutricao(MpaUsoOrdNutricao usoOrdNutricao) {
		this.usoOrdNutricao = usoOrdNutricao;
	}

	
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarUsoOrdNutricao(){
		/*
		 * VALORES PADRÃO
		 */
		if (this.indAvalNutricionista == null) {
			this.indAvalNutricionista = false;
		}
	}
	
	public enum Fields {
		
		SEQ("seq"), PDT_ATD_SEQ("prescricaoDieta.id.atdSeq"), PDT_SEQ("prescricaoDieta.id.seq")
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
		MpaUsoOrdNutricao other = (MpaUsoOrdNutricao) obj;
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