package br.gov.mec.aghu.faturamento.vo;

import java.math.BigInteger;
import java.util.Date;


/**
 * @author marcosilva
 *
 */
public class CursorCEAIVO {

	private Integer cthSeq;
	private Integer seqp;
	private String dciCodigoDcih;
	private String qtLote;
	private String apresLote;
	private String seqLote;
	private String orgEmisAih;
	private Integer cnesHcpa;
	private Long numeroAih;
	private Short tahSeq;
	private Byte especialidadeDcih;
	private String municipioInstituicao;
	private Short iphPhoSeqRealiz;
	private Integer iphSeqRealiz;
	private Date dataInternacaoTipoDate;
	private Date dataSaidaTipoDate;
	private String dataInternacao;
	private String dataSaida;
	private Long iphCodSusSolic;
	private Short nroSeqaih5;
	private Long numeroAihPosterior;
	private Long numeroAihAnterior;
	private String aihDthrEmissao;
	private String cpfMedicoSolicRespons;
	private String cpfMedicoAuditor;
	private String cpfDirCli;
	private String cnsMedicoAuditor;
	private String pacNome;
	private String pacDtNascimento;
	private String pacSexo;
	private String pacCor;
	private String pacNomeMae;
	private String nomeResponsavelPac;
	private Byte indDocPac;
	private String exclusaoCritica;
	private Integer pacProntuario;
	private Short nacionalidadePac;
	private String endLogradouroPac;
	private Integer endNroLogradouroPac;
	private String endCmplLogradouroPac;
	private String endBairroPac;
	private Integer codIbgeCidadePac;
	private String endUfPac;
	private Integer endCepPac;
	private Byte nascidosVivos;
	private Byte nascidosMortos;
	private Byte saidasAlta;
	private Byte saidasTransferencia;
	private Byte saidasObito;
	private Integer grauInstrucaoPac;
	private Long nroSisprenatal;
	private Short endTipCodigo;
	private String mudanca;
	private Long iphCodSusRealiz;
	private Byte tciCodSus;
	private String motivoCobranca;
	private Integer tipoDocumento;
	private String cidPrimario;
	private String cidSecundario;
	private String filler;
	private String cidObito;
	private String etniaIndigena;
	private BigInteger cartaoSUS;
	private String enfermaria;
	private String leito;
	private Integer saidaUtineo;
	private String qteFilhos;
	private String pacTelefone;
	private String cidIndicacao;
	private String metodoContraceptivo;
	private String gestacaoAltoRisco;
	private String pacNroCartaoSaude2;
	private String pacRG;
	
	private String modalidade;
	
	public enum Fields { 

		CTH_SEQ("cthSeq"), 
		SEQP("seqp"),
		DCI_CODIGO_DCIH("dciCodigoDcih"),
		QT_LOTE("qtLote"),
		APRES_LOTE("apresLote"),
		SEQ_LOTE("seqLote"),
		ORG_EMIS_AIH("orgEmisAih"),
		CNES_HCPA("cnesHcpa"),
		NUMERO_AIH("numeroAih"),
		TAH_SEQ("tahSeq"),
		ESPECIALIDADE_DCIH("especialidadeDcih"),
		MUNICIPIO_INSTITUICAO("municipioInstituicao"),
		IPH_PHO_SEQ_REALIZ("iphPhoSeqRealiz"),
		IPH_SEQ_REALIZ("iphSeqRealiz"),
		DATA_INTERNACAO_TIPO_DATE("dataInternacaoTipoDate"),
		DATA_SAIDA_TIPO_DATE("dataSaidaTipoDate"),
		DATA_INTERNACAO("dataInternacao"),
		DATA_SAIDA("dataSaida"),
		IPH_COD_SUS_SOLIC("iphCodSusSolic"),
		NRO_SEQAIH5("nroSeqaih5"), 
		NUMERO_AIH_POSTERIOR("numeroAihPosterior"), 
		NUMERO_AIH_ANTERIOR("numeroAihAnterior"), 
		AIH_DTHR_EMISSAO("aihDthrEmissao"), 
		CPF_MEDICO_SOLIC_RESPONS("cpfMedicoSolicRespons"),
		CPF_MEDICO_AUDITOR("cpfMedicoAuditor"), 
		CPF_DIR_CLI("cpfDirCli"), 
		PAC_NOME("pacNome"), 
		PAC_DT_NASCIMENTO("pacDtNascimento"),
		PAC_SEXO("pacSexo"), 
		PAC_COR("pacCor"), 
		PAC_NOME_MAE("pacNomeMae"), 
		NOME_RESPONSAVEL_PAC("nomeResponsavelPac"), 
		IND_DOC_PAC("indDocPac"), 
		EXCLUSAO_CRITICA("exclusaoCritica"), 
		PAC_PRONTUARIO("pacProntuario"), 
		NACIONALIDADE_PAC("nacionalidadePac"), 
		END_LOGRADOURO_PAC("endLogradouroPac"),
		END_NRO_LOGRADOURO_PAC("endNroLogradouroPac"), 
		END_CMPL_LOGRADOURO_PAC("endCmplLogradouroPac"), 
		END_BAIRRO_PAC("endBairroPac"), 
		COD_IBGE_CIDADE_PAC("codIbgeCidadePac"),
		END_UF_PAC("endUfPac"), 
		END_CEP_PAC("endCepPac"), 
		NASCIDOS_VIVOS("nascidosVivos"), 
		NASCIDOS_MORTOS("nascidosMortos"), 
		SAIDAS_ALTA("saidasAlta"), 
		SAIDAS_TRANSFERENCIA("saidasTransferencia"), 
		SAIDAS_OBITO("saidasObito"), 
		GRAU_INSTRUCAO_PAC("grauInstrucaoPac"), 
		NRO_SISPRENATAL("nroSisprenatal"), 
		END_TIP_CODIGO("endTipCodigo"),

		MUDANCA("mudanca"),
		IPH_COD_SUS_REALIZ("iphCodSusRealiz"),
		TCI_COD_SUS("tciCodSus"),
		MOTIVO_COBRANCA("motivoCobranca"),
		TIPO_DOCUMENTO("tipoDocumento"),
		CID_PRIMARIO("cidPrimario"),
		CID_SECUNDARIO("cidSecundario"),
		CID_OBITO("cidObito"), 
		FILLER("filler"),
		ETNIA_INDIGENA("etniaIndigena"),
		CARTAO_SUS("cartaoSUS"), 
		ENFERMARIA("enfermaria"),
		LEITO("leito"), 
		SAIDA_UTINEO("saidaUtineo"),
		QTE_FILHOS("qteFilhos"),
		PAC_TELEFONE("pacTelefone"),
		CID_INDICACAO("cidIndicacao"),
		METODO_CONTRACEPTIVO("metodoContraceptivo"),
		GESTACAO_ALTO_RISCO("gestacaoAltoRisco"),
		PAC_NRO_CARTAO_SAUDE_2("pacNroCartaoSaude2"),
		
		MODALIDADE("modalidade"),
		
		PAC_RG("pacRG"),
		
		CNS_MEDICO_AUDITOR("cnsMedicoAuditor");
		
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public String getDciCodigoDcih() {
		return dciCodigoDcih;
	}

	public void setDciCodigoDcih(String dciCodigoDcih) {
		this.dciCodigoDcih = dciCodigoDcih;
	}

	public String getQtLote() {
		return qtLote;
	}

	public void setQtLote(String qtLote) {
		this.qtLote = qtLote;
	}

	public String getApresLote() {
		return apresLote;
	}

	public void setApresLote(String apresLote) {
		this.apresLote = apresLote;
	}

	public String getSeqLote() {
		return seqLote;
	}

	public void setSeqLote(String seqLote) {
		this.seqLote = seqLote;
	}

	public String getOrgEmisAih() {
		return orgEmisAih;
	}

	public void setOrgEmisAih(String orgEmisAih) {
		this.orgEmisAih = orgEmisAih;
	}

	public Integer getCnesHcpa() {
		return cnesHcpa;
	}

	public void setCnesHcpa(Integer cnesHcpa) {
		this.cnesHcpa = cnesHcpa;
	}

	public Long getNumeroAih() {
		return numeroAih;
	}

	public void setNumeroAih(Long numeroAih) {
		this.numeroAih = numeroAih;
	}

	public Short getTahSeq() {
		return tahSeq;
	}

	public void setTahSeq(Short tahSeq) {
		this.tahSeq = tahSeq;
	}

	public Byte getEspecialidadeDcih() {
		return especialidadeDcih;
	}

	public void setEspecialidadeDcih(Byte especialidadeDcih) {
		this.especialidadeDcih = especialidadeDcih;
	}

	public String getMunicipioInstituicao() {
		return municipioInstituicao;
	}

	public void setMunicipioInstituicao(String municipioInstituicao) {
		this.municipioInstituicao = municipioInstituicao;
	}

	public Short getIphPhoSeqRealiz() {
		return iphPhoSeqRealiz;
	}

	public void setIphPhoSeqRealiz(Short iphPhoSeqRealiz) {
		this.iphPhoSeqRealiz = iphPhoSeqRealiz;
	}

	public Integer getIphSeqRealiz() {
		return iphSeqRealiz;
	}

	public void setIphSeqRealiz(Integer iphSeqRealiz) {
		this.iphSeqRealiz = iphSeqRealiz;
	}

	public String getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(String dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public String getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(String dataSaida) {
		this.dataSaida = dataSaida;
	}

	public Long getIphCodSusSolic() {
		return iphCodSusSolic;
	}

	public void setIphCodSusSolic(Long iphCodSusSolic) {
		this.iphCodSusSolic = iphCodSusSolic;
	}

	public Short getNroSeqaih5() {
		return nroSeqaih5;
	}

	public void setNroSeqaih5(Short nroSeqaih5) {
		this.nroSeqaih5 = nroSeqaih5;
	}

	public Long getNumeroAihPosterior() {
		return numeroAihPosterior;
	}

	public void setNumeroAihPosterior(Long numeroAihPosterior) {
		this.numeroAihPosterior = numeroAihPosterior;
	}

	public Long getNumeroAihAnterior() {
		return numeroAihAnterior;
	}

	public void setNumeroAihAnterior(Long numeroAihAnterior) {
		this.numeroAihAnterior = numeroAihAnterior;
	}

	public String getAihDthrEmissao() {
		return aihDthrEmissao;
	}

	public void setAihDthrEmissao(String aihDthrEmissao) {
		this.aihDthrEmissao = aihDthrEmissao;
	}

	public String getCpfMedicoSolicRespons() {
		return cpfMedicoSolicRespons;
	}

	public void setCpfMedicoSolicRespons(String cpfMedicoSolicRespons) {
		this.cpfMedicoSolicRespons = cpfMedicoSolicRespons;
	}

	public String getCpfMedicoAuditor() {
		return cpfMedicoAuditor;
	}

	public void setCpfMedicoAuditor(String cpfMedicoAuditor) {
		this.cpfMedicoAuditor = cpfMedicoAuditor;
	}

	public String getCpfDirCli() {
		return cpfDirCli;
	}

	public void setCpfDirCli(String cpfDirCli) {
		this.cpfDirCli = cpfDirCli;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public String getPacDtNascimento() {
		return pacDtNascimento;
	}

	public void setPacDtNascimento(String pacDtNascimento) {
		this.pacDtNascimento = pacDtNascimento;
	}

	public String getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(String pacSexo) {
		this.pacSexo = pacSexo;
	}

	public String getPacCor() {
		return pacCor;
	}

	public void setPacCor(String pacCor) {
		this.pacCor = pacCor;
	}

	public String getPacNomeMae() {
		return pacNomeMae;
	}

	public void setPacNomeMae(String pacNomeMae) {
		this.pacNomeMae = pacNomeMae;
	}

	public String getNomeResponsavelPac() {
		return nomeResponsavelPac;
	}

	public void setNomeResponsavelPac(String nomeResponsavelPac) {
		this.nomeResponsavelPac = nomeResponsavelPac;
	}

	public Byte getIndDocPac() {
		return indDocPac;
	}

	public void setIndDocPac(Byte indDocPac) {
		this.indDocPac = indDocPac;
	}

	public String getExclusaoCritica() {
		return exclusaoCritica;
	}

	public void setExclusaoCritica(String exclusaoCritica) {
		this.exclusaoCritica = exclusaoCritica;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Short getNacionalidadePac() {
		return nacionalidadePac;
	}

	public void setNacionalidadePac(Short nacionalidadePac) {
		this.nacionalidadePac = nacionalidadePac;
	}

	public String getEndLogradouroPac() {
		return endLogradouroPac;
	}

	public void setEndLogradouroPac(String endLogradouroPac) {
		this.endLogradouroPac = endLogradouroPac;
	}

	public Integer getEndNroLogradouroPac() {
		return endNroLogradouroPac;
	}

	public void setEndNroLogradouroPac(Integer endNroLogradouroPac) {
		this.endNroLogradouroPac = endNroLogradouroPac;
	}

	public String getEndCmplLogradouroPac() {
		return endCmplLogradouroPac;
	}

	public void setEndCmplLogradouroPac(String endCmplLogradouroPac) {
		this.endCmplLogradouroPac = endCmplLogradouroPac;
	}

	public String getEndBairroPac() {
		return endBairroPac;
	}

	public void setEndBairroPac(String endBairroPac) {
		this.endBairroPac = endBairroPac;
	}

	public Integer getCodIbgeCidadePac() {
		return codIbgeCidadePac;
	}

	public void setCodIbgeCidadePac(Integer codIbgeCidadePac) {
		this.codIbgeCidadePac = codIbgeCidadePac;
	}

	public String getEndUfPac() {
		return endUfPac;
	}

	public void setEndUfPac(String endUfPac) {
		this.endUfPac = endUfPac;
	}

	public Integer getEndCepPac() {
		return endCepPac;
	}

	public void setEndCepPac(Integer endCepPac) {
		this.endCepPac = endCepPac;
	}

	public Byte getNascidosVivos() {
		return nascidosVivos;
	}

	public void setNascidosVivos(Byte nascidosVivos) {
		this.nascidosVivos = nascidosVivos;
	}

	public Byte getNascidosMortos() {
		return nascidosMortos;
	}

	public void setNascidosMortos(Byte nascidosMortos) {
		this.nascidosMortos = nascidosMortos;
	}

	public Byte getSaidasAlta() {
		return saidasAlta;
	}

	public void setSaidasAlta(Byte saidasAlta) {
		this.saidasAlta = saidasAlta;
	}

	public Byte getSaidasTransferencia() {
		return saidasTransferencia;
	}

	public void setSaidasTransferencia(Byte saidasTransferencia) {
		this.saidasTransferencia = saidasTransferencia;
	}

	public Byte getSaidasObito() {
		return saidasObito;
	}

	public void setSaidasObito(Byte saidasObito) {
		this.saidasObito = saidasObito;
	}

	public Integer getGrauInstrucaoPac() {
		return grauInstrucaoPac;
	}

	public void setGrauInstrucaoPac(Integer grauInstrucaoPac) {
		this.grauInstrucaoPac = grauInstrucaoPac;
	}

	public Long getNroSisprenatal() {
		return nroSisprenatal;
	}

	public void setNroSisprenatal(Long nroSisprenatal) {
		this.nroSisprenatal = nroSisprenatal;
	}

	public Short getEndTipCodigo() {
		return endTipCodigo;
	}

	public void setEndTipCodigo(Short endTipCodigo) {
		this.endTipCodigo = endTipCodigo;
	}

	public String getMudanca() {
		return mudanca;
	}

	public void setMudanca(String mudanca) {
		this.mudanca = mudanca;
	}

	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}

	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}

	public Byte getTciCodSus() {
		return tciCodSus;
	}

	public void setTciCodSus(Byte tciCodSus) {
		this.tciCodSus = tciCodSus;
	}

	public String getMotivoCobranca() {
		return motivoCobranca;
	}

	public void setMotivoCobranca(String motivoCobranca) {
		this.motivoCobranca = motivoCobranca;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getCidPrimario() {
		return cidPrimario;
	}

	public void setCidPrimario(String cidPrimario) {
		this.cidPrimario = cidPrimario;
	}

	public String getCidSecundario() {
		return cidSecundario;
	}

	public void setCidSecundario(String cidSecundario) {
		this.cidSecundario = cidSecundario;
	}

	public String getFiller() {
		return filler;
	}

	public void setFiller(String filler) {
		this.filler = filler;
	}

	public String getCidObito() {
		return cidObito;
	}

	public void setCidObito(String cidObito) {
		this.cidObito = cidObito;
	}

	public String getEtniaIndigena() {
		return etniaIndigena;
	}

	public void setEtniaIndigena(String etniaIndigena) {
		this.etniaIndigena = etniaIndigena;
	}

	public BigInteger getCartaoSUS() {
		return cartaoSUS;
	}

	public void setCartaoSUS(BigInteger cartaoSUS) {
		this.cartaoSUS = cartaoSUS;
	}

	public String getEnfermaria() {
		return enfermaria;
	}

	public void setEnfermaria(String enfermaria) {
		this.enfermaria = enfermaria;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getSaidaUtineo() {
		return saidaUtineo;
	}

	public void setSaidaUtineo(Integer saidaUtineo) {
		this.saidaUtineo = saidaUtineo;
	}

	public String getQteFilhos() {
		return qteFilhos;
	}

	public void setQteFilhos(String qteFilhos) {
		this.qteFilhos = qteFilhos;
	}

	public String getPacTelefone() {
		return pacTelefone;
	}

	public void setPacTelefone(String pacTelefone) {
		this.pacTelefone = pacTelefone;
	}

	public String getCidIndicacao() {
		return cidIndicacao;
	}

	public void setCidIndicacao(String cidIndicacao) {
		this.cidIndicacao = cidIndicacao;
	}

	public String getMetodoContraceptivo() {
		return metodoContraceptivo;
	}

	public void setMetodoContraceptivo(String metodoContraceptivo) {
		this.metodoContraceptivo = metodoContraceptivo;
	}

	public String getGestacaoAltoRisco() {
		return gestacaoAltoRisco;
	}

	public void setGestacaoAltoRisco(String gestacaoAltoRisco) {
		this.gestacaoAltoRisco = gestacaoAltoRisco;
	}

	public String getPacNroCartaoSaude2() {
		return pacNroCartaoSaude2;
	}

	public void setPacNroCartaoSaude2(String pacNroCartaoSaude2) {
		this.pacNroCartaoSaude2 = pacNroCartaoSaude2;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public Date getDataInternacaoTipoDate() {
		return dataInternacaoTipoDate;
	}

	public void setDataInternacaoTipoDate(Date dataInternacaoTipoDate) {
		this.dataInternacaoTipoDate = dataInternacaoTipoDate;
	}

	public Date getDataSaidaTipoDate() {
		return dataSaidaTipoDate;
	}

	public void setDataSaidaTipoDate(Date dataSaidaTipoDate) {
		this.dataSaidaTipoDate = dataSaidaTipoDate;
	}

	public String getCnsMedicoAuditor() {
		return cnsMedicoAuditor;
	}

	public void setCnsMedicoAuditor(String cnsMedicoAuditor) {
		this.cnsMedicoAuditor = cnsMedicoAuditor;
	}
	
	public String getPacRG() {
		return pacRG;
	}
	
	public void setPacRG(String pacRG) {
		this.pacRG = pacRG;
	}
  
}
