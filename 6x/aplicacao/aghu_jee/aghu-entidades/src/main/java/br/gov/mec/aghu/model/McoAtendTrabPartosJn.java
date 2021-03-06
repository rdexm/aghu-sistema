package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioBossa;
import br.gov.mec.aghu.dominio.DominioCardiotocografiaPartos;
import br.gov.mec.aghu.dominio.DominioDelee;
import br.gov.mec.aghu.dominio.DominioDinamicaUterina;
import br.gov.mec.aghu.dominio.DominioIntensidadeDinamicaUterina;
import br.gov.mec.aghu.dominio.DominioPosicaoAtendTrabParto;
import br.gov.mec.aghu.dominio.DominioPosicaoTrabalhoParto;
//import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * McoAtendTrabPartoJn generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mcoAtendJn", sequenceName="AGH.mco_tbp_jn_seq", allocationSize = 1)
@Table(name = "MCO_ATEND_TRAB_PARTOS_JN", schema = "AGH")
@Immutable
public class McoAtendTrabPartosJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3396827740122825917L;

	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Integer seqp;
	private Date dataHoraAtendimento;
	private DominioDinamicaUterina dominioDinamicaUterina;
	private DominioIntensidadeDinamicaUterina dominioIntensidadeDinamicaUterina;
	private Short dilatacao;
	private Short batimentoCardiacoFetal;
	private DominioDelee planoDelee;
	private DominioBossa bossa;
	private DominioCardiotocografiaPartos dominioCardiotocografiaPartos;
	private Boolean indicadorTaquicardia;
	private Boolean semAceleracaoTransitoria;
	private Boolean variabilidadeBatidaMenorQueDez;
	private Boolean indicadorAnalgediaBpd;
	private Boolean indicadorAnalgediaBsd;
	private Date criadoEm;
	private Integer servidorMatricula;
	private Short servidorVinCodigo;
	private DominioPosicaoAtendTrabParto dominioPosicaoAtendTrabParto;
	private DominioPosicaoTrabalhoParto dominioPosicaoTrabalhoParto;
	private Short batimentoCardiacoFetal2;
	private Short batimentoCardiacoFetal3;
	private Short batimentoCardiacoFetal4;
	private Short batimentoCardiacoFetal5;
	private Short batimentoCardiacoFetal6;


	public McoAtendTrabPartosJn() {
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mcoAtendJn")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	
	
	@Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	@NotNull
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	@Column(name = "GSO_SEQP", nullable = false, precision = 8, scale = 0)
	@NotNull
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 8, scale = 0)
	@NotNull
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ATEND", nullable = false)
	public Date getDataHoraAtendimento() {
		return dataHoraAtendimento;
	}

	public void setDataHoraAtendimento(Date dataHoraAtendimento) {
		this.dataHoraAtendimento = dataHoraAtendimento;
	}

	@Column(name = "DIN_UTERINA", nullable = true, length = 6)
	@org.hibernate.annotations.Type(parameters = {
			@Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioDinamicaUterina") }, 
			type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioDinamicaUterina getDominioDinamicaUterina() {
		return dominioDinamicaUterina;
	}

	public void setDominioDinamicaUterina(
			DominioDinamicaUterina dominioDinamicaUterina) {
		this.dominioDinamicaUterina = dominioDinamicaUterina;
	}

	@Column(name = "INTENSIDADE_DIN_UTERINA", nullable = true, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioIntensidadeDinamicaUterina getDominioIntensidadeDinamicaUterina() {
		return dominioIntensidadeDinamicaUterina;
	}

	public void setDominioIntensidadeDinamicaUterina(
			DominioIntensidadeDinamicaUterina dominioIntensidadeDinamicaUterina) {
		this.dominioIntensidadeDinamicaUterina = dominioIntensidadeDinamicaUterina;
	}

	@Column(name = "DILATACAO", nullable = true)
	public Short getDilatacao() {
		return dilatacao;
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
	public DominioCardiotocografiaPartos getDominioCardiotocografiaPartos() {
		return dominioCardiotocografiaPartos;
	}

	public void setDominioCardiotocografiaPartos(
			DominioCardiotocografiaPartos dominioCardiotocografiaPartos) {
		this.dominioCardiotocografiaPartos = dominioCardiotocografiaPartos;
	}

	@Column(name = "IND_TAQUICARDIA", nullable = true, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndicadorTaquicardia() {
		return indicadorTaquicardia;
	}

	public void setIndicadorTaquicardia(Boolean indicadorTaquicardia) {
		this.indicadorTaquicardia = indicadorTaquicardia;
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
	@Column(name = "CRIADO_EM", nullable = false)
	@NotNull
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getServidorMatricula() {
		return servidorMatricula;
	}

	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getServidorVinCodigo() {
		return servidorVinCodigo;
	}

	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}

	@Column(name = "POSICAO", nullable = true, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioPosicaoAtendTrabParto getDominioPosicaoAtendTrabParto() {
		return dominioPosicaoAtendTrabParto;
	}

	public void setDominioPosicaoAtendTrabParto(
			DominioPosicaoAtendTrabParto dominioPosicaoAtendTrabParto) {
		this.dominioPosicaoAtendTrabParto = dominioPosicaoAtendTrabParto;
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

	
}
