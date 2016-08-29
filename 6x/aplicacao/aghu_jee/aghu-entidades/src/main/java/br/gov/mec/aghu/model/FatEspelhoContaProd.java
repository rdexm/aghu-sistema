package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoProducao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity()
@SequenceGenerator(name = "fatEaipSq1", sequenceName = "AGH.FAT_EAIP_SEQ1", allocationSize = 1)
@Table(name = "FAT_ESPELHOS_CONTA_PROD", schema = "AGH")
@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class FatEspelhoContaProd extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -2143077319327539609L;

	private Integer seq;

	private Byte nroDiasMesInicialUti;
	private Byte nroDiasMesAnteriorUti;
	private Byte nroDiasMesAltaUti;
	private Byte nroDiariasAcompanhante;
	private Short tahSeq;
	private String alteradoPor;
	private String criadoPor;
	private Date alteradoEm;
	private Date criadoEm;
	private Date aihDthrEmissao;
	private String cidPrimario;
	private String cidSecundario;
	private Integer codIbgeCidadePac;
	private Long cpfMedicoAuditor;
	private Long cpfMedicoSolicRespons;
	private Date dataInternacao;
	private Date dataPrevia;
	private Date dataSaida;
	private String dciCodigoDcih;
	private Short dciCpeAno;
	private Date dciCpeDtHrInicio;
	private Byte dciCpeMes;
	private DominioModuloCompetencia dciCpeModulo;
	private Integer endCepPac;
	private String endCidadePac;
	private String endCmplLogradouroPac;
	private String endLogradouroPac;
	private Integer endNroLogradouroPac;
	private String endUfPac;
	private String enfermaria;
	private Byte especialidadeAih;
	private Byte especialidadeDcih;
	private String exclusaoCritica;
	private DominioGrauInstrucao grauInstrucaoPac;
	private String indConsistente;
	private Byte indDocPac;
	private String infeccaoHospitalar;
	private Long iphCodSusRealiz;
	private Long iphCodSusSolic;
	private Short iphPhoSeqRealiz;
	private Short iphPhoSeqSolic;
	private Integer iphSeqRealiz;
	private Integer iphSeqSolic;
	private String leito;
	private String motivoCobranca;
	private Short nacionalidadePac;
	private Byte nascidosMortos;
	private Byte nascidosVivos;
	private String nomeResponsavelPac;
	private Long numeroAih;
	private Long numeroAihAnterior;
	private Long numeroAihPosterior;
	private Long pacCpf;
	private Date pacDtNascimento;
	private String pacNome;
	private BigInteger pacNroCartaoSaude;
	private Integer pacProntuario;
	private String pacSexo;
	private Byte saidasAlta;
	private Byte saidasObito;
	private Byte saidasTransferencia;
	private Byte tciCodSus;
	private BigDecimal valorAnestRealiz;
	private BigDecimal valorProcedRealiz;
	private BigDecimal valorSadtRealiz;
	private BigDecimal valorShRealiz;
	private BigDecimal valorSpRealiz;
	private Short nroSeqaih5;
	private Long nroSisprenatal;
	private String pacNomeMae;
	private String pacCor;
	private Short endTipCodigo;
	private String endBairroPac;
	private Integer dadosRn;
	private String indBcoCapac;
	private Integer conCodCentral;
	private String dauSenha;
	private Long cnsMedicoAuditor;
	private Short competenciaProd;
	private BigDecimal valorTotalProd;
	private DominioSituacaoProducao indSituacaoProd;
	private Integer version;

	private FatContasHospitalares contaHospitalar;

	// construtores

	/**
	 * Cria objeto com os dados da FatEspelhoAih.
	 * 
	 * @param espelho
	 * @param situacao
	 * @param alteradoPor
	 */
	public FatEspelhoContaProd(FatEspelhoAih espelho,
			DominioSituacaoProducao situacao, String usuario,
			BigDecimal valorTotalProducao) {
		Date agora = new Date();
		//
		this.nroDiasMesInicialUti = espelho.getNroDiasMesInicialUti();
		this.nroDiasMesAnteriorUti = espelho.getNroDiasMesAnteriorUti();
		this.nroDiasMesAltaUti = espelho.getNroDiasMesAltaUti();
		this.nroDiariasAcompanhante = espelho.getNroDiariasAcompanhante();
		this.tahSeq = espelho.getTahSeq();
		this.alteradoPor = usuario;
		this.criadoPor = usuario;
		this.alteradoEm = agora;
		this.criadoEm = agora;
		this.aihDthrEmissao = espelho.getAihDthrEmissao();
		this.cidPrimario = espelho.getCidPrimario();
		this.cidSecundario = espelho.getCidSecundario();
		this.codIbgeCidadePac = espelho.getCodIbgeCidadePac();
		this.cpfMedicoAuditor = espelho.getCpfMedicoAuditor();
		this.cpfMedicoSolicRespons = espelho.getCpfMedicoSolicRespons();
		this.dataInternacao = espelho.getDataInternacao();
		this.dataPrevia = espelho.getDataPrevia();
		this.dataSaida = espelho.getDataSaida();
		this.dciCodigoDcih = espelho.getDciCodigoDcih();
		this.dciCpeAno = espelho.getDciCpeAno();
		this.dciCpeDtHrInicio = espelho.getDciCpeDtHrInicio();
		this.dciCpeMes = espelho.getDciCpeMes();
		this.dciCpeModulo = espelho.getDciCpeModulo();
		this.endCepPac = espelho.getEndCepPac();
		this.endCidadePac = espelho.getEndCidadePac();
		this.endCmplLogradouroPac = espelho.getEndCmplLogradouroPac();
		this.endLogradouroPac = espelho.getEndLogradouroPac();
		this.endNroLogradouroPac = espelho.getEndNroLogradouroPac();
		this.endUfPac = espelho.getEndUfPac();
		this.enfermaria = espelho.getEnfermaria();
		this.especialidadeAih = espelho.getEspecialidadeAih();
		this.especialidadeDcih = espelho.getEspecialidadeDcih();
		this.exclusaoCritica = espelho.getExclusaoCritica();
		this.grauInstrucaoPac = espelho.getGrauInstrucaoPac();
		this.indConsistente = espelho.getIndConsistente();
		this.indDocPac = espelho.getIndDocPac();
		this.infeccaoHospitalar = espelho.getInfeccaoHospitalar();
		this.iphCodSusRealiz = espelho.getIphCodSusRealiz();
		this.iphCodSusSolic = espelho.getIphCodSusSolic();
		this.iphPhoSeqRealiz = espelho.getIphPhoSeqRealiz();
		this.iphPhoSeqSolic = espelho.getIphPhoSeqSolic();
		this.iphSeqRealiz = espelho.getIphSeqRealiz();
		this.iphSeqSolic = espelho.getIphSeqSolic();
		this.leito = espelho.getLeito();
		this.motivoCobranca = espelho.getMotivoCobranca();
		this.nacionalidadePac = espelho.getNacionalidadePac();
		this.nascidosMortos = espelho.getNascidosMortos();
		this.nascidosVivos = espelho.getNascidosVivos();
		this.nomeResponsavelPac = espelho.getNomeResponsavelPac();
		this.numeroAih = espelho.getNumeroAih();
		this.numeroAihAnterior = espelho.getNumeroAihAnterior();
		this.numeroAihPosterior = espelho.getNumeroAihPosterior();
		this.pacCpf = espelho.getPacCpf();
		this.pacDtNascimento = espelho.getPacDtNascimento();
		this.pacNome = espelho.getPacNome();
		this.pacNroCartaoSaude = espelho.getPacNroCartaoSaude();
		this.pacProntuario = espelho.getPacProntuario();
		this.pacSexo = espelho.getPacSexo();
		this.saidasAlta = espelho.getSaidasAlta();
		this.saidasObito = espelho.getSaidasObito();
		this.saidasTransferencia = espelho.getSaidasTransferencia();
		this.tciCodSus = espelho.getTciCodSus();
		this.valorAnestRealiz = espelho.getValorAnestRealiz();
		this.valorProcedRealiz = espelho.getValorProcedRealiz();
		this.valorSadtRealiz = espelho.getValorSadtRealiz();
		this.valorShRealiz = espelho.getValorShRealiz();
		this.valorSpRealiz = espelho.getValorSpRealiz();
		this.nroSeqaih5 = espelho.getNroSeqaih5();
		this.nroSisprenatal = espelho.getNroSisprenatal();
		this.pacNomeMae = espelho.getPacNomeMae();
		this.pacCor = espelho.getPacCor();
		this.endTipCodigo = espelho.getEndTipCodigo();
		this.endBairroPac = espelho.getEndBairroPac();
		this.dadosRn = espelho.getDadosRn();
		this.indBcoCapac = espelho.getIndBcoCapac();
		this.conCodCentral = espelho.getConCodCentral();
		this.dauSenha = espelho.getDauSenha();
		this.cnsMedicoAuditor = espelho.getCnsMedicoAuditor();
		this.competenciaProd = espelho.getContaHospitalar()
				.getCompetenciaProd().getSeq();
		this.valorTotalProd = valorTotalProducao;
		this.indSituacaoProd = situacao;
		this.contaHospitalar = espelho.getContaHospitalar();

	}

	public FatEspelhoContaProd() {
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public FatEspelhoContaProd(Integer seq, Byte nroDiasMesInicialUti,
			Byte nroDiasMesAnteriorUti, Byte nroDiasMesAltaUti,
			Byte nroDiariasAcompanhante, Short tahSeq, String alteradoPor,
			String criadoPor, Date alteradoEm, Date criadoEm,
			Date aihDthrEmissao, String cidPrimario, String cidSecundario,

			Integer codIbgeCidadePac, Long cpfMedicoAuditor,
			Long cpfMedicoSolicRespons, Date dataInternacao, Date dataPrevia,
			Date dataSaida, String dciCodigoDcih, Short dciCpeAno,
			Date dciCpeDtHrInicio, Byte dciCpeMes,
			DominioModuloCompetencia dciCpeModulo, Integer endCepPac,
			String endCidadePac, String endCmplLogradouroPac,
			String endLogradouroPac, Integer endNroLogradouroPac,
			String endUfPac, String enfermaria, Byte especialidadeAih,
			Byte especialidadeDcih, String exclusaoCritica,
			DominioGrauInstrucao grauInstrucaoPac, String indConsistente,
			Byte indDocPac, String infeccaoHospitalar, Long iphCodSusRealiz,
			Long iphCodSusSolic, Short iphPhoSeqRealiz, Short iphPhoSeqSolic,
			Integer iphSeqRealiz, Integer iphSeqSolic, String leito,
			String motivoCobranca, Short nacionalidadePac, Byte nascidosMortos,
			Byte nascidosVivos, String nomeResponsavelPac, Long numeroAih,
			Long numeroAihAnterior, Long numeroAihPosterior, Long pacCpf,
			Date pacDtNascimento, String pacNome, BigInteger pacNroCartaoSaude,
			Integer pacProntuario, String pacSexo, Byte saidasAlta,
			Byte saidasObito, Byte saidasTransferencia, Byte tciCodSus,
			BigDecimal valorAnestRealiz, BigDecimal valorProcedRealiz,
			BigDecimal valorSadtRealiz, BigDecimal valorShRealiz,
			BigDecimal valorSpRealiz, Short nroSeqaih5, Long nroSisprenatal,
			String pacNomeMae, String pacCor, Short endTipCodigo,
			String endBairroPac, Integer dadosRn, String indBcoCapac,
			Integer conCodCentral, String dauSenha, Long cnsMedicoAuditor,
			Short competenciaProd, BigDecimal valorTotalProd,
			DominioSituacaoProducao indSituacaoProd,
			FatContasHospitalares contaHospitalar) {

		this.seq = seq;
		this.nroDiasMesInicialUti = nroDiasMesInicialUti;
		this.nroDiasMesAnteriorUti = nroDiasMesAnteriorUti;
		this.nroDiasMesAltaUti = nroDiasMesAltaUti;
		this.nroDiariasAcompanhante = nroDiariasAcompanhante;
		this.tahSeq = tahSeq;
		this.alteradoPor = alteradoPor;
		this.criadoPor = criadoPor;
		this.alteradoEm = alteradoEm;
		this.criadoEm = criadoEm;
		this.aihDthrEmissao = aihDthrEmissao;
		this.cidPrimario = cidPrimario;
		this.cidSecundario = cidSecundario;
		this.codIbgeCidadePac = codIbgeCidadePac;
		this.cpfMedicoAuditor = cpfMedicoAuditor;
		this.cpfMedicoSolicRespons = cpfMedicoSolicRespons;
		this.dataInternacao = dataInternacao;
		this.dataPrevia = dataPrevia;
		this.dataSaida = dataSaida;
		this.dciCodigoDcih = dciCodigoDcih;
		this.dciCpeAno = dciCpeAno;
		this.dciCpeDtHrInicio = dciCpeDtHrInicio;
		this.dciCpeMes = dciCpeMes;
		this.dciCpeModulo = dciCpeModulo;
		this.endCepPac = endCepPac;
		this.endCidadePac = endCidadePac;
		this.endCmplLogradouroPac = endCmplLogradouroPac;
		this.endLogradouroPac = endLogradouroPac;
		this.endNroLogradouroPac = endNroLogradouroPac;
		this.endUfPac = endUfPac;
		this.enfermaria = enfermaria;
		this.especialidadeAih = especialidadeAih;
		this.especialidadeDcih = especialidadeDcih;
		this.exclusaoCritica = exclusaoCritica;
		this.grauInstrucaoPac = grauInstrucaoPac;
		this.indConsistente = indConsistente;
		this.indDocPac = indDocPac;
		this.infeccaoHospitalar = infeccaoHospitalar;
		this.iphCodSusRealiz = iphCodSusRealiz;
		this.iphCodSusSolic = iphCodSusSolic;
		this.iphPhoSeqRealiz = iphPhoSeqRealiz;
		this.iphPhoSeqSolic = iphPhoSeqSolic;
		this.iphSeqRealiz = iphSeqRealiz;
		this.iphSeqSolic = iphSeqSolic;
		this.leito = leito;
		this.motivoCobranca = motivoCobranca;
		this.nacionalidadePac = nacionalidadePac;
		this.nascidosMortos = nascidosMortos;
		this.nascidosVivos = nascidosVivos;
		this.nomeResponsavelPac = nomeResponsavelPac;
		this.numeroAih = numeroAih;
		this.numeroAihAnterior = numeroAihAnterior;
		this.numeroAihPosterior = numeroAihPosterior;
		this.pacCpf = pacCpf;
		this.pacDtNascimento = pacDtNascimento;
		this.pacNome = pacNome;
		this.pacNroCartaoSaude = pacNroCartaoSaude;
		this.pacProntuario = pacProntuario;
		this.pacSexo = pacSexo;
		this.saidasAlta = saidasAlta;
		this.saidasObito = saidasObito;
		this.saidasTransferencia = saidasTransferencia;
		this.tciCodSus = tciCodSus;
		this.valorAnestRealiz = valorAnestRealiz;
		this.valorProcedRealiz = valorProcedRealiz;
		this.valorSadtRealiz = valorSadtRealiz;
		this.valorShRealiz = valorShRealiz;
		this.valorSpRealiz = valorSpRealiz;
		this.nroSeqaih5 = nroSeqaih5;
		this.nroSisprenatal = nroSisprenatal;
		this.pacNomeMae = pacNomeMae;
		this.pacCor = pacCor;
		this.endTipCodigo = endTipCodigo;
		this.endBairroPac = endBairroPac;
		this.dadosRn = dadosRn;
		this.indBcoCapac = indBcoCapac;
		this.conCodCentral = conCodCentral;
		this.dauSenha = dauSenha;
		this.cnsMedicoAuditor = cnsMedicoAuditor;
		this.competenciaProd = competenciaProd;
		this.valorTotalProd = valorTotalProd;
		this.indSituacaoProd = indSituacaoProd;
		this.contaHospitalar = contaHospitalar;
	}

	// getters & setters
	@Id()
	@Column(name = "SEQ", length = 8, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatEaipSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NRO_DIAS_MES_INICIAL_UTI", length = 2)
	public Byte getNroDiasMesInicialUti() {
		return this.nroDiasMesInicialUti;
	}

	public void setNroDiasMesInicialUti(Byte nroDiasMesInicialUti) {
		this.nroDiasMesInicialUti = nroDiasMesInicialUti;
	}

	@Column(name = "NRO_DIAS_MES_ANTERIOR_UTI", length = 2)
	public Byte getNroDiasMesAnteriorUti() {
		return this.nroDiasMesAnteriorUti;
	}

	public void setNroDiasMesAnteriorUti(Byte nroDiasMesAnteriorUti) {
		this.nroDiasMesAnteriorUti = nroDiasMesAnteriorUti;
	}

	@Column(name = "NRO_DIAS_MES_ALTA_UTI", length = 2)
	public Byte getNroDiasMesAltaUti() {
		return this.nroDiasMesAltaUti;
	}

	public void setNroDiasMesAltaUti(Byte nroDiasMesAltaUti) {
		this.nroDiasMesAltaUti = nroDiasMesAltaUti;
	}

	@Column(name = "NRO_DIARIAS_ACOMPANHANTE", length = 2)
	public Byte getNroDiariasAcompanhante() {
		return this.nroDiariasAcompanhante;
	}

	public void setNroDiariasAcompanhante(Byte nroDiariasAcompanhante) {
		this.nroDiariasAcompanhante = nroDiariasAcompanhante;
	}

	@Column(name = "TAH_SEQ", length = 1)
	public Short getTahSeq() {
		return this.tahSeq;
	}

	public void setTahSeq(Short tahSeq) {
		this.tahSeq = tahSeq;
	}

	@Column(name = "ALTERADO_POR", length = 30, nullable = false)
	public String getAlteradoPor() {
		return this.alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	@Column(name = "CRIADO_POR", length = 30, nullable = false)
	public String getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", nullable = false)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "AIH_DTHR_EMISSAO", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAihDthrEmissao() {
		return this.aihDthrEmissao;
	}

	public void setAihDthrEmissao(Date aihDthrEmissao) {
		this.aihDthrEmissao = aihDthrEmissao;
	}

	@Column(name = "CID_PRIMARIO", length = 4)
	@Length(max = 4)
	public String getCidPrimario() {
		return this.cidPrimario;
	}

	public void setCidPrimario(String cidPrimario) {
		this.cidPrimario = cidPrimario;
	}

	@Column(name = "CID_SECUNDARIO", length = 4)
	@Length(max = 4)
	public String getCidSecundario() {
		return this.cidSecundario;
	}

	public void setCidSecundario(String cidSecundario) {
		this.cidSecundario = cidSecundario;
	}

	@Column(name = "COD_IBGE_CIDADE_PAC", length = 6)
	public Integer getCodIbgeCidadePac() {
		return this.codIbgeCidadePac;
	}

	public void setCodIbgeCidadePac(Integer codIbgeCidadePac) {
		this.codIbgeCidadePac = codIbgeCidadePac;
	}

	@Column(name = "CPF_MEDICO_AUDITOR", length = 11)
	public Long getCpfMedicoAuditor() {
		return this.cpfMedicoAuditor;
	}

	public void setCpfMedicoAuditor(Long cpfMedicoAuditor) {
		this.cpfMedicoAuditor = cpfMedicoAuditor;
	}

	@Column(name = "CPF_MEDICO_SOLIC_RESPONS", length = 11)
	public Long getCpfMedicoSolicRespons() {
		return this.cpfMedicoSolicRespons;
	}

	public void setCpfMedicoSolicRespons(Long cpfMedicoSolicRespons) {
		this.cpfMedicoSolicRespons = cpfMedicoSolicRespons;
	}

	@Column(name = "DATA_INTERNACAO", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataInternacao() {
		return this.dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	@Column(name = "DATA_PREVIA", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataPrevia() {
		return this.dataPrevia;
	}

	public void setDataPrevia(Date dataPrevia) {
		this.dataPrevia = dataPrevia;
	}

	@Column(name = "DATA_SAIDA", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataSaida() {
		return this.dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	@Column(name = "DCI_CODIGO_DCIH", length = 7)
	public String getDciCodigoDcih() {
		return this.dciCodigoDcih;
	}

	public void setDciCodigoDcih(String dciCodigoDcih) {
		this.dciCodigoDcih = dciCodigoDcih;
	}

	@Column(name = "DCI_CPE_ANO", length = 4)
	public Short getDciCpeAno() {
		return this.dciCpeAno;
	}

	public void setDciCpeAno(Short dciCpeAno) {
		this.dciCpeAno = dciCpeAno;
	}

	@Column(name = "DCI_CPE_DT_HR_INICIO", length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDciCpeDtHrInicio() {
		return this.dciCpeDtHrInicio;
	}

	public void setDciCpeDtHrInicio(Date dciCpeDtHrInicio) {
		this.dciCpeDtHrInicio = dciCpeDtHrInicio;
	}

	@Column(name = "DCI_CPE_MES", length = 2)
	public Byte getDciCpeMes() {
		return this.dciCpeMes;
	}

	public void setDciCpeMes(Byte dciCpeMes) {
		this.dciCpeMes = dciCpeMes;
	}

	@Column(name = "DCI_CPE_MODULO", length = 4)
	@Enumerated(EnumType.STRING)
	public DominioModuloCompetencia getDciCpeModulo() {
		return this.dciCpeModulo;
	}

	public void setDciCpeModulo(final DominioModuloCompetencia dciCpeModulo) {
		this.dciCpeModulo = dciCpeModulo;
	}

	@Column(name = "END_CEP_PAC", length = 8)
	public Integer getEndCepPac() {
		return this.endCepPac;
	}

	public void setEndCepPac(Integer endCepPac) {
		this.endCepPac = endCepPac;
	}

	@Column(name = "END_CIDADE_PAC", length = 20)
	public String getEndCidadePac() {
		return this.endCidadePac;
	}

	public void setEndCidadePac(String endCidadePac) {
		this.endCidadePac = endCidadePac;
	}

	@Column(name = "END_CMPL_LOGRADOURO_PAC", length = 15)
	public String getEndCmplLogradouroPac() {
		return this.endCmplLogradouroPac;
	}

	public void setEndCmplLogradouroPac(String endCmplLogradouroPac) {
		this.endCmplLogradouroPac = endCmplLogradouroPac;
	}

	@Column(name = "END_LOGRADOURO_PAC", length = 25)
	public String getEndLogradouroPac() {
		return this.endLogradouroPac;
	}

	public void setEndLogradouroPac(String endLogradouroPac) {
		this.endLogradouroPac = endLogradouroPac;
	}

	@Column(name = "END_NRO_LOGRADOURO_PAC", length = 5)
	public Integer getEndNroLogradouroPac() {
		return this.endNroLogradouroPac;
	}

	public void setEndNroLogradouroPac(Integer endNroLogradouroPac) {
		this.endNroLogradouroPac = endNroLogradouroPac;
	}

	@Column(name = "END_UF_PAC", length = 2)
	public String getEndUfPac() {
		return this.endUfPac;
	}

	public void setEndUfPac(String endUfPac) {
		this.endUfPac = endUfPac;
	}

	@Column(name = "ENFERMARIA", length = 4)
	@Length(max = 4)
	public String getEnfermaria() {
		return this.enfermaria;
	}

	public void setEnfermaria(String enfermaria) {
		this.enfermaria = enfermaria;
	}

	@Column(name = "ESPECIALIDADE_AIH", length = 2)
	public Byte getEspecialidadeAih() {
		return this.especialidadeAih;
	}

	public void setEspecialidadeAih(Byte especialidadeAih) {
		this.especialidadeAih = especialidadeAih;
	}

	@Column(name = "ESPECIALIDADE_DCIH", length = 2)
	public Byte getEspecialidadeDcih() {
		return this.especialidadeDcih;
	}

	public void setEspecialidadeDcih(Byte especialidadeDcih) {
		this.especialidadeDcih = especialidadeDcih;
	}

	@Column(name = "EXCLUSAO_CRITICA", length = 3)
	public String getExclusaoCritica() {
		return this.exclusaoCritica;
	}

	public void setExclusaoCritica(String exclusaoCritica) {
		this.exclusaoCritica = exclusaoCritica;
	}

	@Column(name = "GRAU_INSTRUCAO_PAC", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioGrauInstrucao") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioGrauInstrucao getGrauInstrucaoPac() {
		return this.grauInstrucaoPac;
	}

	public void setGrauInstrucaoPac(final DominioGrauInstrucao grauInstrucaoPac) {
		this.grauInstrucaoPac = grauInstrucaoPac;
	}

	@Column(name = "IND_DOC_PAC", length = 1)
	public Byte getIndDocPac() {
		return this.indDocPac;
	}

	public void setIndDocPac(Byte indDocPac) {
		this.indDocPac = indDocPac;
	}

	@Column(name = "INFECCAO_HOSPITALAR", length = 1)
	public String getInfeccaoHospitalar() {
		return this.infeccaoHospitalar;
	}

	public void setInfeccaoHospitalar(String infeccaoHospitalar) {
		this.infeccaoHospitalar = infeccaoHospitalar;
	}

	@Column(name = "IPH_COD_SUS_REALIZ", length = 10)
	public Long getIphCodSusRealiz() {
		return this.iphCodSusRealiz;
	}

	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}

	@Column(name = "IPH_COD_SUS_SOLIC", length = 10)
	public Long getIphCodSusSolic() {
		return this.iphCodSusSolic;
	}

	public void setIphCodSusSolic(Long iphCodSusSolic) {
		this.iphCodSusSolic = iphCodSusSolic;
	}

	@Column(name = "IPH_PHO_SEQ_REALIZ", length = 4)
	public Short getIphPhoSeqRealiz() {
		return this.iphPhoSeqRealiz;
	}

	public void setIphPhoSeqRealiz(Short iphPhoSeqRealiz) {
		this.iphPhoSeqRealiz = iphPhoSeqRealiz;
	}

	@Column(name = "IPH_PHO_SEQ_SOLIC", length = 4)
	public Short getIphPhoSeqSolic() {
		return this.iphPhoSeqSolic;
	}

	public void setIphPhoSeqSolic(Short iphPhoSeqSolic) {
		this.iphPhoSeqSolic = iphPhoSeqSolic;
	}

	@Column(name = "IPH_SEQ_REALIZ", length = 8)
	public Integer getIphSeqRealiz() {
		return this.iphSeqRealiz;
	}

	public void setIphSeqRealiz(Integer iphSeqRealiz) {
		this.iphSeqRealiz = iphSeqRealiz;
	}

	@Column(name = "IPH_SEQ_SOLIC", length = 8)
	public Integer getIphSeqSolic() {
		return this.iphSeqSolic;
	}

	public void setIphSeqSolic(Integer iphSeqSolic) {
		this.iphSeqSolic = iphSeqSolic;
	}

	@Column(name = "LEITO", length = 14)
	public String getLeito() {
		return this.leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	@Column(name = "MOTIVO_COBRANCA", length = 2)
	public String getMotivoCobranca() {
		return this.motivoCobranca;
	}

	public void setMotivoCobranca(String motivoCobranca) {
		this.motivoCobranca = motivoCobranca;
	}

	@Column(name = "NACIONALIDADE_PAC", length = 3)
	public Short getNacionalidadePac() {
		return this.nacionalidadePac;
	}

	public void setNacionalidadePac(Short nacionalidadePac) {
		this.nacionalidadePac = nacionalidadePac;
	}

	@Column(name = "NASCIDOS_MORTOS", length = 1)
	public Byte getNascidosMortos() {
		return this.nascidosMortos;
	}

	public void setNascidosMortos(Byte nascidosMortos) {
		this.nascidosMortos = nascidosMortos;
	}

	@Column(name = "NASCIDOS_VIVOS", length = 1)
	public Byte getNascidosVivos() {
		return this.nascidosVivos;
	}

	public void setNascidosVivos(Byte nascidosVivos) {
		this.nascidosVivos = nascidosVivos;
	}

	@Column(name = "NOME_RESPONSAVEL_PAC", length = 60)
	public String getNomeResponsavelPac() {
		return this.nomeResponsavelPac;
	}

	public void setNomeResponsavelPac(String nomeResponsavelPac) {
		this.nomeResponsavelPac = nomeResponsavelPac;
	}

	@Column(name = "NUMERO_AIH", length = 13)
	public Long getNumeroAih() {
		return this.numeroAih;
	}

	public void setNumeroAih(Long numeroAih) {
		this.numeroAih = numeroAih;
	}

	@Column(name = "NUMERO_AIH_ANTERIOR", length = 13)
	public Long getNumeroAihAnterior() {
		return this.numeroAihAnterior;
	}

	public void setNumeroAihAnterior(Long numeroAihAnterior) {
		this.numeroAihAnterior = numeroAihAnterior;
	}

	@Column(name = "NUMERO_AIH_POSTERIOR", length = 13)
	public Long getNumeroAihPosterior() {
		return this.numeroAihPosterior;
	}

	public void setNumeroAihPosterior(Long numeroAihPosterior) {
		this.numeroAihPosterior = numeroAihPosterior;
	}

	@Column(name = "PAC_CPF", length = 11)
	public Long getPacCpf() {
		return this.pacCpf;
	}

	public void setPacCpf(Long pacCpf) {
		this.pacCpf = pacCpf;
	}

	@Column(name = "PAC_DT_NASCIMENTO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getPacDtNascimento() {
		return this.pacDtNascimento;
	}

	public void setPacDtNascimento(Date pacDtNascimento) {
		this.pacDtNascimento = pacDtNascimento;
	}

	@Column(name = "PAC_NOME", length = 60)
	public String getPacNome() {
		return this.pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	@Column(name = "PAC_NRO_CARTAO_SAUDE", length = 32)
	public BigInteger getPacNroCartaoSaude() {
		return this.pacNroCartaoSaude;
	}

	public void setPacNroCartaoSaude(BigInteger pacNroCartaoSaude) {
		this.pacNroCartaoSaude = pacNroCartaoSaude;
	}

	@Column(name = "PAC_PRONTUARIO", length = 8)
	public Integer getPacProntuario() {
		return this.pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	@Column(name = "PAC_SEXO", length = 1)
	public String getPacSexo() {
		return this.pacSexo;
	}

	public void setPacSexo(String pacSexo) {
		this.pacSexo = pacSexo;
	}

	@Column(name = "SAIDAS_ALTA", length = 1)
	public Byte getSaidasAlta() {
		return this.saidasAlta;
	}

	public void setSaidasAlta(Byte saidasAlta) {
		this.saidasAlta = saidasAlta;
	}

	@Column(name = "SAIDAS_OBITO", length = 1)
	public Byte getSaidasObito() {
		return this.saidasObito;
	}

	public void setSaidasObito(Byte saidasObito) {
		this.saidasObito = saidasObito;
	}

	@Column(name = "SAIDAS_TRANSFERENCIA", length = 1)
	public Byte getSaidasTransferencia() {
		return this.saidasTransferencia;
	}

	public void setSaidasTransferencia(Byte saidasTransferencia) {
		this.saidasTransferencia = saidasTransferencia;
	}

	@Column(name = "TCI_COD_SUS", length = 2)
	public Byte getTciCodSus() {
		return this.tciCodSus;
	}

	public void setTciCodSus(Byte tciCodSus) {
		this.tciCodSus = tciCodSus;
	}

	@Column(name = "VALOR_ANEST_REALIZ", length = 14)
	public BigDecimal getValorAnestRealiz() {
		return this.valorAnestRealiz;
	}

	public void setValorAnestRealiz(BigDecimal valorAnestRealiz) {
		this.valorAnestRealiz = valorAnestRealiz;
	}

	@Column(name = "VALOR_PROCED_REALIZ", length = 14)
	public BigDecimal getValorProcedRealiz() {
		return this.valorProcedRealiz;
	}

	public void setValorProcedRealiz(BigDecimal valorProcedRealiz) {
		this.valorProcedRealiz = valorProcedRealiz;
	}

	@Column(name = "VALOR_SADT_REALIZ", length = 14)
	public BigDecimal getValorSadtRealiz() {
		return this.valorSadtRealiz;
	}

	public void setValorSadtRealiz(BigDecimal valorSadtRealiz) {
		this.valorSadtRealiz = valorSadtRealiz;
	}

	@Column(name = "VALOR_SH_REALIZ", length = 14)
	public BigDecimal getValorShRealiz() {
		return this.valorShRealiz;
	}

	public void setValorShRealiz(BigDecimal valorShRealiz) {
		this.valorShRealiz = valorShRealiz;
	}

	@Column(name = "VALOR_SP_REALIZ", length = 14)
	public BigDecimal getValorSpRealiz() {
		return this.valorSpRealiz;
	}

	public void setValorSpRealiz(BigDecimal valorSpRealiz) {
		this.valorSpRealiz = valorSpRealiz;
	}

	@Column(name = "NRO_SEQAIH5", length = 3)
	public Short getNroSeqaih5() {
		return this.nroSeqaih5;
	}

	public void setNroSeqaih5(Short nroSeqaih5) {
		this.nroSeqaih5 = nroSeqaih5;
	}

	@Column(name = "NRO_SISPRENATAL", length = 12)
	public Long getNroSisprenatal() {
		return this.nroSisprenatal;
	}

	public void setNroSisprenatal(Long nroSisprenatal) {
		this.nroSisprenatal = nroSisprenatal;
	}

	@Column(name = "PAC_NOME_MAE", length = 50)
	public String getPacNomeMae() {
		return this.pacNomeMae;
	}

	public void setPacNomeMae(String pacNomeMae) {
		this.pacNomeMae = pacNomeMae;
	}

	@Column(name = "PAC_COR", length = 2)
	public String getPacCor() {
		return this.pacCor;
	}

	public void setPacCor(String pacCor) {
		this.pacCor = pacCor;
	}

	@Column(name = "END_TIP_CODIGO", length = 3)
	public Short getEndTipCodigo() {
		return this.endTipCodigo;
	}

	public void setEndTipCodigo(Short endTipCodigo) {
		this.endTipCodigo = endTipCodigo;
	}

	@Column(name = "END_BAIRRO_PAC", length = 30)
	public String getEndBairroPac() {
		return this.endBairroPac;
	}

	public void setEndBairroPac(String endBairroPac) {
		this.endBairroPac = endBairroPac;
	}

	@Column(name = "DADOS_RN", length = 6)
	public Integer getDadosRn() {
		return this.dadosRn;
	}

	public void setDadosRn(Integer dadosRn) {
		this.dadosRn = dadosRn;
	}

	@Column(name = "IND_BCO_CAPAC", length = 1)
	public String getIndBcoCapac() {
		return this.indBcoCapac;
	}

	public void setIndBcoCapac(String indBcoCapac) {
		this.indBcoCapac = indBcoCapac;
	}

	@Column(name = "CON_COD_CENTRAL", length = 9)
	public Integer getConCodCentral() {
		return this.conCodCentral;
	}

	public void setConCodCentral(Integer conCodCentral) {
		this.conCodCentral = conCodCentral;
	}

	@Column(name = "DAU_SENHA", length = 20)
	@Length(max = 20)
	public String getDauSenha() {
		return this.dauSenha;
	}

	public void setDauSenha(String dauSenha) {
		this.dauSenha = dauSenha;
	}

	@Column(name = "CNS_MEDICO_AUDITOR", length = 15)
	public Long getCnsMedicoAuditor() {
		return this.cnsMedicoAuditor;
	}

	public void setCnsMedicoAuditor(Long cnsMedicoAuditor) {
		this.cnsMedicoAuditor = cnsMedicoAuditor;
	}

	@Column(name = "COMPETENCIA_PROD", length = 4, nullable = false)
	public Short getCompetenciaProd() {
		return this.competenciaProd;
	}

	public void setCompetenciaProd(Short competenciaProd) {
		this.competenciaProd = competenciaProd;
	}

	@Column(name = "VALOR_TOTAL_PROD", length = 14, nullable = false)
	public BigDecimal getValorTotalProd() {
		return this.valorTotalProd;
	}

	public void setValorTotalProd(BigDecimal valorTotalProd) {
		this.valorTotalProd = valorTotalProd;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTH_SEQ", referencedColumnName = "SEQ", nullable = false)
	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(final FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	@Column(name = "IND_CONSISTENTE", length = 1)
	@Length(max = 1)
	public String getIndConsistente() {
		return this.indConsistente;
	}

	public void setIndConsistente(String indConsistente) {
		this.indConsistente = indConsistente;
	}

	@Column(name = "IND_SITUACAO_PROD", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoProducao getIndSituacaoProd() {
		return indSituacaoProd;
	}

	public void setIndSituacaoProd(DominioSituacaoProducao indSituacaoProd) {
		this.indSituacaoProd = indSituacaoProd;
	}

	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	// outros

	public boolean equals(Object other) {
		if (!(other instanceof FatEspelhoContaProd)) {
			return false;
		}
		FatEspelhoContaProd castOther = (FatEspelhoContaProd) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), CONTA_HOSPITALAR("contaHospitalar"), CONTANRO_DIAS_MES_INICIAL_UTI(
				"nroDiasMesInicialUti"), NRO_DIAS_MES_ANTERIOR_UTI(
				"nroDiasMesAnteriorUti"), NRO_DIAS_MES_ALTA_UTI(
				"nroDiasMesAltaUti"), NRO_DIARIAS_ACOMPANHANTE(
				"nroDiariasAcompanhante"), TAH_SEQ("tahSeq"), ALTERADO_POR(
				"alteradoPor"), CRIADO_POR("criadoPor"), ALTERADO_EM(
				"alteradoEm"), CRIADO_EM("criadoEm"), AIH_DTHR_EMISSAO(
				"aihDthrEmissao"), CID_PRIMARIO("cidPrimario"), CID_SECUNDARIO(
				"cidSecundario"), COD_IBGE_CIDADE_PAC("codIbgeCidadePac"), CPF_MEDICO_AUDITOR(
				"cpfMedicoAuditor"), CPF_MEDICO_SOLIC_RESPONS(
				"cpfMedicoSolicRespons"), DATA_INTERNACAO("dataInternacao"), DATA_PREVIA(
				"dataPrevia"), DATA_SAIDA("dataSaida"), DCI_CODIGO_DCIH(
				"dciCodigoDcih"), DCI_CPE_ANO("dciCpeAno"), DCI_CPE_DT_HR_INICIO(
				"dciCpeDtHrInicio"), DCI_CPE_MES("dciCpeMes"), DCI_CPE_MODULO(
				"dciCpeModulo"), END_CEP_PAC("endCepPac"), END_CIDADE_PAC(
				"endCidadePac"), END_CMPL_LOGRADOURO_PAC("endCmplLogradouroPac"), END_LOGRADOURO_PAC(
				"endLogradouroPac"), END_NRO_LOGRADOURO_PAC(
				"endNroLogradouroPac"), END_UF_PAC("endUfPac"), ENFERMARIA(
				"enfermaria"), ESPECIALIDADE_AIH("especialidadeAih"), ESPECIALIDADE_DCIH(
				"especialidadeDcih"), EXCLUSAO_CRITICA("exclusaoCritica"), GRAU_INSTRUCAO_PAC(
				"grauInstrucaoPac"), IND_CONSISTENTE("indConsistente"), IND_DOC_PAC(
				"indDocPac"), INFECCAO_HOSPITALAR("infeccaoHospitalar"), IPH_COD_SUS_REALIZ(
				"iphCodSusRealiz"), IPH_COD_SUS_SOLIC("iphCodSusSolic"), IPH_PHO_SEQ_REALIZ(
				"iphPhoSeqRealiz"), IPH_PHO_SEQ_SOLIC("iphPhoSeqSolic"), IPH_SEQ_REALIZ(
				"iphSeqRealiz"), IPH_SEQ_SOLIC("iphSeqSolic"), LEITO("leito"), MOTIVO_COBRANCA(
				"motivoCobranca"), NACIONALIDADE_PAC("nacionalidadePac"), NASCIDOS_MORTOS(
				"nascidosMortos"), NASCIDOS_VIVOS("nascidosVivos"), NOME_RESPONSAVEL_PAC(
				"nomeResponsavelPac"), NUMERO_AIH("numeroAih"), NUMERO_AIH_ANTERIOR(
				"numeroAihAnterior"), NUMERO_AIH_POSTERIOR("numeroAihPosterior"), PAC_CPF(
				"pacCpf"), PAC_DT_NASCIMENTO("pacDtNascimento"), PAC_NOME(
				"pacNome"), PAC_NRO_CARTAO_SAUDE("pacNroCartaoSaude"), PAC_PRONTUARIO(
				"pacProntuario"), PAC_SEXO("pacSexo"), SAIDAS_ALTA("saidasAlta"), SAIDAS_OBITO(
				"saidasObito"), SAIDAS_TRANSFERENCIA("saidasTransferencia"), TCI_COD_SUS(
				"tciCodSus"), VALOR_ANEST_REALIZ("valorAnestRealiz"), VALOR_PROCED_REALIZ(
				"valorProcedRealiz"), VALOR_SADT_REALIZ("valorSadtRealiz"), VALOR_SH_REALIZ(
				"valorShRealiz"), VALOR_SP_REALIZ("valorSpRealiz"), NRO_SEQAIH5(
				"nroSeqaih5"), NRO_SISPRENATAL("nroSisprenatal"), PAC_NOME_MAE(
				"pacNomeMae"), PAC_COR("pacCor"), END_TIP_CODIGO("endTipCodigo"), END_BAIRRO_PAC(
				"endBairroPac"), DADOS_RN("dadosRn"), IND_BCO_CAPAC(
				"indBcoCapac"), CON_COD_CENTRAL("conCodCentral"), DAU_SENHA(
				"dauSenha"), CNS_MEDICO_AUDITOR("cnsMedicoAuditor"), COMPETENCIA_PROD(
				"competenciaProd"), VALOR_TOTAL_PROD("valorTotalProd"), IND_SITUACAO_PROD(
				"indSituacaoProd");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}

}