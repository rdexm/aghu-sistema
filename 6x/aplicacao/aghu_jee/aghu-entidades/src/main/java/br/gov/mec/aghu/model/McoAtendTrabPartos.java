package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioBossa;
import br.gov.mec.aghu.dominio.DominioCardiotocografiaPartos;
import br.gov.mec.aghu.dominio.DominioDelee;
import br.gov.mec.aghu.dominio.DominioDinamicaUterina;
import br.gov.mec.aghu.dominio.DominioIntensidadeDinamicaUterina;
import br.gov.mec.aghu.dominio.DominioPosicaoAtendTrabParto;
import br.gov.mec.aghu.dominio.DominioPosicaoTrabalhoParto;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "MCO_ATEND_TRAB_PARTOS", schema = "AGH")
public class McoAtendTrabPartos extends BaseEntityId<McoAtendTrabPartosId> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 523278572977891687L;
	
	private McoAtendTrabPartosId id;
	private McoGestacoes mcoGestacoes;
	private Date dthrAtend;
	private DominioIntensidadeDinamicaUterina intensidadeDinUterina;
	private DominioDinamicaUterina dinUterina;
	private Short dilatacao;
	private DominioDelee planoDelee;
	private DominioBossa bossa;
	private DominioCardiotocografiaPartos cardiotocografia;
	private Boolean indTaquicardia;
	private Boolean semAceleracaoTransitoria;
	private Boolean variabilidadeBatidaMenorQueDez;
	private Boolean indicadorAnalgediaBpd;
	private Boolean indicadorAnalgediaBsd;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private DominioPosicaoAtendTrabParto posicao;
	
	private DominioPosicaoTrabalhoParto dominioPosicaoTrabalhoParto;
	
	private Short batimentoCardiacoFetal;
	private Short batimentoCardiacoFetal2;
	private Short batimentoCardiacoFetal3;
	private Short batimentoCardiacoFetal4;
	private Short batimentoCardiacoFetal5;
	private Short batimentoCardiacoFetal6;
	private Integer version;
	
	public McoAtendTrabPartos() {
	}

	public McoAtendTrabPartos(McoAtendTrabPartosId id,
			McoGestacoes mcoGestacoes, Date dthrAtend, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.mcoGestacoes = mcoGestacoes;
		this.dthrAtend = dthrAtend;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public McoAtendTrabPartos(McoAtendTrabPartosId id,
			McoGestacoes mcoGestacoes, Date dthrAtend, DominioDinamicaUterina dinUterina,
			DominioIntensidadeDinamicaUterina intensidadeDinUterina, Short dilatacao,
			Short batCardiacoFetal, DominioDelee planoDelee, DominioBossa bossa,
			DominioCardiotocografiaPartos cardiotocografia, Boolean indTaquicardia,
			Boolean indSemAcelerTrans, Boolean indVarBatidaMenor10,
			Boolean indAnalgesiaBpd, Boolean indAnalgesiaBsd, Date criadoEm,
			Integer serMatricula, Short serVinCodigo, DominioPosicaoAtendTrabParto posicao,
			Short batCardiacoFetal2) {
		this.id = id;
		this.mcoGestacoes = mcoGestacoes;
		this.dthrAtend = dthrAtend;
		this.dinUterina = dinUterina;
		this.intensidadeDinUterina = intensidadeDinUterina;
		this.dilatacao = dilatacao;
		this.batimentoCardiacoFetal = batCardiacoFetal;
		this.planoDelee = planoDelee;
		this.bossa = bossa;
		this.cardiotocografia = cardiotocografia;
		this.indTaquicardia = indTaquicardia;
		this.semAceleracaoTransitoria = indSemAcelerTrans;
		this.variabilidadeBatidaMenorQueDez = indVarBatidaMenor10;
		this.indicadorAnalgediaBpd = indAnalgesiaBpd;
		this.indicadorAnalgediaBsd = indAnalgesiaBsd;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.posicao = posicao;
		this.batimentoCardiacoFetal2 = batCardiacoFetal2;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "gsoPacCodigo", column = @Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "gsoSeqp", column = @Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)) })
	public McoAtendTrabPartosId getId() {
		return this.id;
	}

	public void setId(McoAtendTrabPartosId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "GSO_PAC_CODIGO", referencedColumnName = "PAC_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "GSO_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public McoGestacoes getMcoGestacoes() {
		return this.mcoGestacoes;
	}

	public void setMcoGestacoes(McoGestacoes mcoGestacoes) {
		this.mcoGestacoes = mcoGestacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ATEND", nullable = false)
	public Date getDthrAtend() {
		return this.dthrAtend;
	}

	public void setDthrAtend(Date dthrAtend) {
		this.dthrAtend = dthrAtend;
	}

	@Column(name = "DIN_UTERINA", nullable = true, length = 6)
	@org.hibernate.annotations.Type(parameters = {
			@Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioDinamicaUterina") }, 
			type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioDinamicaUterina getDinUterina() {
		return dinUterina;
	}

	public void setDinUterina(
			DominioDinamicaUterina dinUterina) {
		this.dinUterina = dinUterina;
	}

	@Column(name = "INTENSIDADE_DIN_UTERINA", nullable = true, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioIntensidadeDinamicaUterina getIntensidadeDinUterina() {
		return intensidadeDinUterina;
	}

	public void setIntensidadeDinUterina(DominioIntensidadeDinamicaUterina intensidadeDinUterina) {
		this.intensidadeDinUterina = intensidadeDinUterina;
	}

	@Column(name = "DILATACAO", precision = 2, scale = 0)
	public Short getDilatacao() {
		return this.dilatacao;
	}

	public void setDilatacao(Short dilatacao) {
		this.dilatacao = dilatacao;
	}

	@Column(name = "PLANO_DELEE", nullable = true, length = 3)
	@org.hibernate.annotations.Type(parameters = {
			@Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioDelee") }, 
			type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioDelee getPlanoDelee() {
		return planoDelee;
	}

	public void setPlanoDelee(DominioDelee planoDelee) {
		this.planoDelee = planoDelee;
	}

	@Column(name = "BOSSA", nullable = true, length = 4)
	@org.hibernate.annotations.Type(parameters = {
			@Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioBossa") }, 
			type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioBossa getBossa() {
		return bossa;
	}

	public void setBossa(DominioBossa bossa) {
		this.bossa = bossa;
	}

	@Column(name = "CARDIOTOCOGRAFIA", nullable = true, length = 7)
	@Enumerated(EnumType.STRING)
	public DominioCardiotocografiaPartos getCardiotocografia() {
		return this.cardiotocografia;
	}

	public void setCardiotocografia(DominioCardiotocografiaPartos cardiotocografia) {
		this.cardiotocografia = cardiotocografia;
	}

	@Column(name = "IND_TAQUICARDIA", nullable = true, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTaquicardia() {
		return this.indTaquicardia;
	}

	public void setIndTaquicardia(Boolean indTaquicardia) {
		this.indTaquicardia = indTaquicardia;
	}

	@Column(name = "IND_SEM_ACELER_TRANS", length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getSemAceleracaoTransitoria() {
		return semAceleracaoTransitoria;
	}

	public void setSemAceleracaoTransitoria(Boolean semAceleracaoTransitoria) {
		this.semAceleracaoTransitoria = semAceleracaoTransitoria;
	}

	@Column(name = "IND_VAR_BATIDA_MENOR10", length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getVariabilidadeBatidaMenorQueDez() {
		return variabilidadeBatidaMenorQueDez;
	}

	public void setVariabilidadeBatidaMenorQueDez(
			Boolean variabilidadeBatidaMenorQueDez) {
		this.variabilidadeBatidaMenorQueDez = variabilidadeBatidaMenorQueDez;
	}

	@Column(name = "IND_ANALGESIA_BPD", nullable = true, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndicadorAnalgediaBpd() {
		return indicadorAnalgediaBpd;
	}

	public void setIndicadorAnalgediaBpd(Boolean indicadorAnalgediaBpd) {
		this.indicadorAnalgediaBpd = indicadorAnalgediaBpd;
	}

	@Column(name = "IND_ANALGESIA_BSD", nullable = true, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndicadorAnalgediaBsd() {
		return indicadorAnalgediaBsd;
	}

	public void setIndicadorAnalgediaBsd(Boolean indicadorAnalgediaBsd) {
		this.indicadorAnalgediaBsd = indicadorAnalgediaBsd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "POSICAO", nullable = true, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioPosicaoAtendTrabParto getPosicao() {
		return posicao;
	}

	public void setPosicao(DominioPosicaoAtendTrabParto posicao) {
		this.posicao = posicao;
	}
	
	@Column(name = "POSICAO_TRAB_PARTO", nullable = true, length = 20)
	@Enumerated(EnumType.STRING)
	public DominioPosicaoTrabalhoParto getDominioPosicaoTrabalhoParto() {
		return dominioPosicaoTrabalhoParto;
	}

	public void setDominioPosicaoTrabalhoParto(
			DominioPosicaoTrabalhoParto dominioPosicaoTrabalhoParto) {
		this.dominioPosicaoTrabalhoParto = dominioPosicaoTrabalhoParto;
	}

	@Column(name = "BAT_CARDIACO_FETAL", nullable = true, length = 3)
	public Short getBatimentoCardiacoFetal() {
		return batimentoCardiacoFetal;
	}

	public void setBatimentoCardiacoFetal(Short batimentoCardiacoFetal) {
		this.batimentoCardiacoFetal = batimentoCardiacoFetal;
	}

	@Column(name = "BAT_CARDIACO_FETAL2", nullable = true, length = 3)
	public Short getBatimentoCardiacoFetal2() {
		return batimentoCardiacoFetal2;
	}

	public void setBatimentoCardiacoFetal2(Short batimentoCardiacoFetal2) {
		this.batimentoCardiacoFetal2 = batimentoCardiacoFetal2;
	}
	
	@Column(name = "BAT_CARDIACO_FETAL3", nullable = true, length = 3)
	public Short getBatimentoCardiacoFetal3() {
		return batimentoCardiacoFetal3;
	}

	public void setBatimentoCardiacoFetal3(Short batimentoCardiacoFetal3) {
		this.batimentoCardiacoFetal3 = batimentoCardiacoFetal3;
	}

	@Column(name = "BAT_CARDIACO_FETAL4", nullable = true, length = 3)
	public Short getBatimentoCardiacoFetal4() {
		return batimentoCardiacoFetal4;
	}

	public void setBatimentoCardiacoFetal4(Short batimentoCardiacoFetal4) {
		this.batimentoCardiacoFetal4 = batimentoCardiacoFetal4;
	}

	@Column(name = "BAT_CARDIACO_FETAL5", nullable = true, length = 3)
	public Short getBatimentoCardiacoFetal5() {
		return batimentoCardiacoFetal5;
	}

	public void setBatimentoCardiacoFetal5(Short batimentoCardiacoFetal5) {
		this.batimentoCardiacoFetal5 = batimentoCardiacoFetal5;
	}

	@Column(name = "BAT_CARDIACO_FETAL6", nullable = true, length = 3)
	public Short getBatimentoCardiacoFetal6() {
		return batimentoCardiacoFetal6;
	}

	public void setBatimentoCardiacoFetal6(Short batimentoCardiacoFetal6) {
		this.batimentoCardiacoFetal6 = batimentoCardiacoFetal6;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		ID("id"),
		ID_GSO_PAC_CODIGO("id.gsoPacCodigo"),
		ID_GSO_SEQP("id.gsoSeqp"),
		ID_SEQP("id.seqp"),
		GSO_PAC_CODIGO("id.gsoPacCodigo"), 
		GSO_SEQP("id.gsoSeqp"),
		SEQP("id.seqp"),
		DTHR_ATEND("dthrAtend"),
		IND_ANALGESIA_BPD("indicadorAnalgediaBpd"),
		IND_ANALGESIA_BSD("indicadorAnalgediaBsd");
		
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		McoAtendTrabPartos other = (McoAtendTrabPartos) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}	
}
