package br.gov.mec.aghu.model;

// Generated 22/04/2010 18:28:15 by Hibernate Tools 3.2.5.Beta

import java.math.BigDecimal;
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
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioApresentacao;
import br.gov.mec.aghu.dominio.DominioEdema;
import br.gov.mec.aghu.dominio.DominioEspessuraCervice;
import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.dominio.DominioIntensidadeDinamicaUterina;
import br.gov.mec.aghu.dominio.DominioLiquidoAmniotico;
import br.gov.mec.aghu.dominio.DominioPosicaoCervice;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoFetal;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * @author jccouto (Jean Couto)
 * @since 30/07/2014
 */
@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "MCO_ANAMNESE_EFS_JN", schema = "AGH")
@SequenceGenerator(name = "mcoAefJnSeq", sequenceName = "AGH.MCO_AEF_JN_SEQ", allocationSize = 1)

@Immutable
public class McoAnamneseEfsJn extends BaseJournal implements
		java.io.Serializable {

	private static final long serialVersionUID = 2443797056428383625L;
	private Date dthrConsulta;
	private String motivo;
	private Short pressaoArtSistolica;
	private Short pressaoArtDiastolica;
	private Short freqCardiaca;
	private Short freqRespiratoria;
	private BigDecimal temperatura;
	private DominioEdema edema;
	private Byte alturaUterina;
	private String dinamicaUterina;
	private DominioIntensidadeDinamicaUterina intensidadeDinUterina;
	private Short batimentoCardiacoFetal;
	private DominioSimNao indAcelTrans;
	private DominioSituacaoFetal sitFetal;
	private String exameEspecular;
	private DominioPosicaoCervice posicaoCervice;
	private DominioEspessuraCervice espessuraCervice;
	private String apagamento;
	private String dilatacao;
	private DominioApresentacao apresentacao;
	private Integer planoDelee;
	private Boolean indPromontorioAcessivel;
	private Boolean indEspCiaticaSaliente;
	private Boolean indSubPubicoMenor90;
	private String acv;
	private String ar;
	private String observacao;
	private Date criadoEm;
	private Integer conNumero;
	private Short gsoSeqp;
	private Integer gsoPacCodigo;
	private Integer digSeq;
	private Integer cidSeq;
	private DominioSimNao indMovFetal;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String exFisicoGeral;
	private Short batimentoCardiacoFetal2;
	private Short pressaoSistRepouso;
	private Short pressaoDiastRepouso;
	private Short batimentoCardiacoFetal3;
	private Short batimentoCardiacoFetal4;
	private Short batimentoCardiacoFetal5;
	private Short batimentoCardiacoFetal6;
	private DominioFormaRupturaBolsaRota formaRuptura;
	private Date dataHoraRompimento;
	private Boolean indDthrIgnorada;
	private DominioLiquidoAmniotico liquidoAmniotico;
	private Boolean indOdorFetido;

	public McoAnamneseEfsJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mcoAefJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONSULTA", length = 7)
	public Date getDthrConsulta() {
		return this.dthrConsulta;
	}

	public void setDthrConsulta(Date dthrConsulta) {
		this.dthrConsulta = dthrConsulta;
	}

	@Column(name = "MOTIVO", length = 4000)
	@Length(max = 4000)
	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	@Column(name = "PRESSAO_ART_SISTOLICA", precision = 3, scale = 0)
	public Short getPressaoArtSistolica() {
		return this.pressaoArtSistolica;
	}

	public void setPressaoArtSistolica(Short pressaoArtSistolica) {
		this.pressaoArtSistolica = pressaoArtSistolica;
	}

	@Column(name = "PRESSAO_ART_DIASTOLICA", precision = 3, scale = 0)
	public Short getPressaoArtDiastolica() {
		return this.pressaoArtDiastolica;
	}

	public void setPressaoArtDiastolica(Short pressaoArtDiastolica) {
		this.pressaoArtDiastolica = pressaoArtDiastolica;
	}

	@Column(name = "FREQ_CARDIACA", precision = 3, scale = 0)
	public Short getFreqCardiaca() {
		return this.freqCardiaca;
	}

	public void setFreqCardiaca(Short freqCardiaca) {
		this.freqCardiaca = freqCardiaca;
	}

	@Column(name = "FREQ_RESPIRATORIA", precision = 3, scale = 0)
	public Short getFreqRespiratoria() {
		return this.freqRespiratoria;
	}

	public void setFreqRespiratoria(Short freqRespiratoria) {
		this.freqRespiratoria = freqRespiratoria;
	}

	@Column(name = "TEMPERATURA", precision = 3, scale = 1)
	public BigDecimal getTemperatura() {
		return this.temperatura;
	}

	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}

	@Column(name = "EDEMA")
	@Enumerated(EnumType.STRING)
	public DominioEdema getEdema() {
		return this.edema;
	}

	public void setEdema(DominioEdema edema) {
		this.edema = edema;
	}

	@Column(name = "ALTURA_UTERINA", precision = 2, scale = 0)
	public Byte getAlturaUterina() {
		return this.alturaUterina;
	}

	public void setAlturaUterina(Byte alturaUterina) {
		this.alturaUterina = alturaUterina;
	}

	@Column(name = "DINAMICA_UTERINA")
	public String getDinamicaUterina() {
		return this.dinamicaUterina;
	}

	public void setDinamicaUterina(String dinamicaUterina) {
		this.dinamicaUterina = dinamicaUterina;
	}

	@Column(name = "INTENSIDADE_DIN_UTERINA", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioIntensidadeDinamicaUterina getIntensidadeDinUterina() {
		return this.intensidadeDinUterina;
	}

	public void setIntensidadeDinUterina(DominioIntensidadeDinamicaUterina intensidadeDinUterina) {
		this.intensidadeDinUterina = intensidadeDinUterina;
	}

	@Column(name = "BATIMENTO_CARDIACO_FETAL", precision = 3, scale = 0)
	public Short getBatimentoCardiacoFetal() {
		return this.batimentoCardiacoFetal;
	}

	public void setBatimentoCardiacoFetal(Short batimentoCardiacoFetal) {
		this.batimentoCardiacoFetal = batimentoCardiacoFetal;
	}

	@Column(name = "IND_ACEL_TRANS")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAcelTrans() {
		return this.indAcelTrans;
	}

	public void setIndAcelTrans(DominioSimNao indAcelTrans) {
		this.indAcelTrans = indAcelTrans;
	}

	@Column(name = "SIT_FETAL")
	@Enumerated(EnumType.STRING)
	public DominioSituacaoFetal getSitFetal() {
		return this.sitFetal;
	}

	public void setSitFetal(DominioSituacaoFetal sitFetal) {
		this.sitFetal = sitFetal;
	}

	@Column(name = "EXAME_ESPECULAR", length = 250)
	@Length(max = 250)
	public String getExameEspecular() {
		return this.exameEspecular;
	}

	public void setExameEspecular(String exameEspecular) {
		this.exameEspecular = exameEspecular;
	}

	@Column(name = "POSICAO_CERVICE")
	@Enumerated(EnumType.STRING)
	public DominioPosicaoCervice getPosicaoCervice() {
		return this.posicaoCervice;
	}

	public void setPosicaoCervice(DominioPosicaoCervice posicaoCervice) {
		this.posicaoCervice = posicaoCervice;
	}

	@Column(name = "ESPESSURA_CERVICE")
	@Enumerated(EnumType.STRING)
	public DominioEspessuraCervice getEspessuraCervice() {
		return this.espessuraCervice;
	}

	public void setEspessuraCervice(DominioEspessuraCervice espessuraCervice) {
		this.espessuraCervice = espessuraCervice;
	}

	@Column(name = "APAGAMENTO", length = 3)
	@Length(max = 3)
	public String getApagamento() {
		return this.apagamento;
	}

	public void setApagamento(String apagamento) {
		this.apagamento = apagamento;
	}

	@Column(name = "DILATACAO", length = 3)
	@Length(max = 3)
	public String getDilatacao() {
		return this.dilatacao;
	}

	public void setDilatacao(String dilatacao) {
		this.dilatacao = dilatacao;
	}

	@Column(name = "APRESENTACAO")
	@Enumerated(EnumType.STRING)
	public DominioApresentacao getApresentacao() {
		return this.apresentacao;
	}

	public void setApresentacao(DominioApresentacao apresentacao) {
		this.apresentacao = apresentacao;
	}

	@Column(name = "PLANO_DELEE", precision = 2, scale = 0)
	public Integer getPlanoDelee() {
		return this.planoDelee;
	}

	public void setPlanoDelee(Integer planoDelee) {
		this.planoDelee = planoDelee;
	}

	@Column(name = "IND_PROMONTORIO_ACESSIVEL")
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPromontorioAcessivel() {
		return this.indPromontorioAcessivel;
	}

	public void setIndPromontorioAcessivel(Boolean indPromontorioAcessivel) {
		this.indPromontorioAcessivel = indPromontorioAcessivel;
	}

	@Column(name = "IND_ESP_CIATICA_SALIENTE")
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEspCiaticaSaliente() {
		return this.indEspCiaticaSaliente;
	}

	public void setIndEspCiaticaSaliente(Boolean indEspCiaticaSaliente) {
		this.indEspCiaticaSaliente = indEspCiaticaSaliente;
	}

	@Column(name = "IND_SUB_PUBICO_MENOR90")
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSubPubicoMenor90() {
		return this.indSubPubicoMenor90;
	}

	public void setIndSubPubicoMenor90(Boolean indSubPubicoMenor90) {
		this.indSubPubicoMenor90 = indSubPubicoMenor90;
	}

	@Column(name = "ACV", length = 120)
	@Length(max = 120)
	public String getAcv() {
		return this.acv;
	}

	public void setAcv(String acv) {
		this.acv = acv;
	}

	@Column(name = "AR", length = 100)
	@Length(max = 100)
	public String getAr() {
		return this.ar;
	}

	public void setAr(String ar) {
		this.ar = ar;
	}

	@Column(name = "OBSERVACAO", length = 2000)
	@Length(max = 2000)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "CON_NUMERO", nullable = false, precision = 8, scale = 0)
	public Integer getConNumero() {
		return this.conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	@Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	@Column(name = "DIG_SEQ", precision = 8, scale = 0)
	public Integer getDigSeq() {
		return this.digSeq;
	}

	public void setDigSeq(Integer digSeq) {
		this.digSeq = digSeq;
	}

	@Column(name = "CID_SEQ", precision = 5, scale = 0)
	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	@Column(name = "IND_MOV_FETAL")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndMovFetal() {
		return this.indMovFetal;
	}

	public void setIndMovFetal(DominioSimNao indMovFetal) {
		this.indMovFetal = indMovFetal;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "EX_FISICO_GERAL", length = 500)
	@Length(max = 500)
	public String getExFisicoGeral() {
		return this.exFisicoGeral;
	}

	public void setExFisicoGeral(String exFisicoGeral) {
		this.exFisicoGeral = exFisicoGeral;
	}

	@Column(name = "BATIMENTO_CARDIACO_FETAL2", precision = 3, scale = 0)
	public Short getBatimentoCardiacoFetal2() {
		return this.batimentoCardiacoFetal2;
	}

	public void setBatimentoCardiacoFetal2(Short batimentoCardiacoFetal2) {
		this.batimentoCardiacoFetal2 = batimentoCardiacoFetal2;
	}

	@Column(name = "PRESSAO_SIST_REPOUSO", precision = 3, scale = 0)
	public Short getPressaoSistRepouso() {
		return this.pressaoSistRepouso;
	}

	public void setPressaoSistRepouso(Short pressaoSistRepouso) {
		this.pressaoSistRepouso = pressaoSistRepouso;
	}

	@Column(name = "PRESSAO_DIAST_REPOUSO", precision = 3, scale = 0)
	public Short getPressaoDiastRepouso() {
		return this.pressaoDiastRepouso;
	}

	public void setPressaoDiastRepouso(Short pressaoDiastRepouso) {
		this.pressaoDiastRepouso = pressaoDiastRepouso;
	}
	
	@Column(name = "BATIMENTO_CARDIACO_FETAL3", precision = 3, scale = 0)
	public Short getBatimentoCardiacoFetal3() {
		return batimentoCardiacoFetal3;
	}


	public void setBatimentoCardiacoFetal3(Short batimentoCardiacoFetal3) {
		this.batimentoCardiacoFetal3 = batimentoCardiacoFetal3;
	}




	@Column(name = "BATIMENTO_CARDIACO_FETAL4", precision = 3, scale = 0)
	public Short getBatimentoCardiacoFetal4() {
		return batimentoCardiacoFetal4;
	}





	public void setBatimentoCardiacoFetal4(Short batimentoCardiacoFetal4) {
		this.batimentoCardiacoFetal4 = batimentoCardiacoFetal4;
	}

	@Column(name = "BATIMENTO_CARDIACO_FETAL5", precision = 3, scale = 0)
	public Short getBatimentoCardiacoFetal5() {
		return batimentoCardiacoFetal5;
	}

	public void setBatimentoCardiacoFetal5(Short batimentoCardiacoFetal5) {
		this.batimentoCardiacoFetal5 = batimentoCardiacoFetal5;
	}

	@Column(name = "BATIMENTO_CARDIACO_FETAL6", precision = 3, scale = 0)
	public Short getBatimentoCardiacoFetal6() {
		return batimentoCardiacoFetal6;
	}

	public void setBatimentoCardiacoFetal6(Short batimentoCardiacoFetal6) {
		this.batimentoCardiacoFetal6 = batimentoCardiacoFetal6;
	}
	
	@Column(name = "FORMA_RUPTURA")
	@Enumerated(EnumType.STRING)
	public DominioFormaRupturaBolsaRota getFormaRuptura() {
		return formaRuptura;
	}

	public void setFormaRuptura(DominioFormaRupturaBolsaRota formaRuptura) {
		this.formaRuptura = formaRuptura;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ROMPIMENTO", nullable = false, length = 7)
	public Date getDataHoraRompimento() {
		return dataHoraRompimento;
	}

	public void setDataHoraRompimento(Date dataHoraRompimento) {
		this.dataHoraRompimento = dataHoraRompimento;
	}
	
	@Column(name = "IND_DTHR_IGNORADA", length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDthrIgnorada() {
		return indDthrIgnorada;
	}

	public void setIndDthrIgnorada(Boolean indDthrIgnorada) {
		this.indDthrIgnorada = indDthrIgnorada;
	}

	@Column(name = "LIQUIDO_AMNIOTICO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioLiquidoAmniotico getLiquidoAmniotico() {
		return liquidoAmniotico;
	}

	public void setLiquidoAmniotico(DominioLiquidoAmniotico liquidoAmniotico) {
		this.liquidoAmniotico = liquidoAmniotico;
	}

	@Column(name = "IND_ODOR_FETIDO", length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndOdorFetido() {
		return indOdorFetido;
	}

	public void setIndOdorFetido(Boolean indOdorFetido) {
		this.indOdorFetido = indOdorFetido;
	}

	public enum Fields {
		DATA_ALTERACAO("dataAlteracao"), JN_OPERATION("operacao");

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