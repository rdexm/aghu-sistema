package br.gov.mec.aghu.indicadores.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "V_AIN_PES_REF_CLI_ESP", schema = "AGH")
@Immutable
public class ReferencialClinicaEspecialidadeVO extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3962040173138946582L;
	private Integer ordem;
	private Integer espClcCodigo;
	private Integer espSeq;
	private String espSigla;
	private Integer drvEspCapacReferencial;
	private Integer inhMediaPermanencia;
	private Integer inhPercentualOcupacao;

	public ReferencialClinicaEspecialidadeVO(){
		
	}
	
	public ReferencialClinicaEspecialidadeVO(Integer ordem,
			Integer espClcCodigo, Integer espSeq, String espSigla,
			Integer drvEspCapacReferencial, Integer inhMediaPermanencia,
			Integer inhPercentualOcupacao) {
		super();
		this.ordem = ordem;
		this.espClcCodigo = espClcCodigo;
		this.espSeq = espSeq;
		this.espSigla = espSigla;
		this.drvEspCapacReferencial = drvEspCapacReferencial;
		this.inhMediaPermanencia = inhMediaPermanencia;
		this.inhPercentualOcupacao = inhPercentualOcupacao;
	}

	@Column(name = "ESP_SIGLA", nullable = true, length = 3)
	@Length(max = 3)
	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	@Column(name = "INH_PERCENTUAL_OCUPACAO", nullable = true)
	public Integer getInhPercentualOcupacao() {
		return inhPercentualOcupacao;
	}

	public void setInhPercentualOcupacao(Integer inhPercentualOcupacao) {
		this.inhPercentualOcupacao = inhPercentualOcupacao;
	}

	@Column(name = "INH_MEDIA_PERMANENCIA", nullable = true)
	public Integer getInhMediaPermanencia() {
		return inhMediaPermanencia;
	}

	public void setInhMediaPermanencia(Integer inhMediaPermanencia) {
		this.inhMediaPermanencia = inhMediaPermanencia;
	}

	@Column(name = "ORDEM", nullable = true)
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "ESP_CLC_CODIGO", nullable = true)
	public Integer getEspClcCodigo() {
		return espClcCodigo;
	}

	public void setEspClcCodigo(Integer espClcCodigo) {
		this.espClcCodigo = espClcCodigo;
	}

	@Id
	@Column(name = "ESP_SEQ", nullable = true, precision = 2, scale = 0)
	public Integer getEspSeq() {
		return espSeq;
	}

	@Column(name = "ESP_CAPAC_REFERENCIAL", nullable = true)
	public Integer getDrvEspCapacReferencial() {
		return drvEspCapacReferencial;
	}

	public void setDrvEspCapacReferencial(Integer drvEspCapacReferencial) {
		this.drvEspCapacReferencial = drvEspCapacReferencial;
	}

	public void setEspSeq(Integer espSeq) {
		this.espSeq = espSeq;
	}
	 
 @Transient public Integer getSeq(){ return this.getEspSeq();} 
 public void setSeq(Integer seq){ this.setEspSeq(seq);}
}
