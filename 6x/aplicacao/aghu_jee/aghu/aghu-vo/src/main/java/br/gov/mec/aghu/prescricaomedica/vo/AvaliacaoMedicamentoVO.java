package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;

public class AvaliacaoMedicamentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4324371423624692388L;
	
	/* Campos retornados pela C3 (#45269) */
	private Integer jumSeq; 									// C3.SEQ
	private Short gupSeq;
	private String gestante;									
	private String funcRenalComprometida;						
	private String pacImunodeprimido;							
	private String indicacao; 									// C3.INDICACAO
	private String infeccaoTratar; 								// C3.INFECCAO_TRATAR 
	private String tratAntimicrobAnt;				 			// C3.TRAT_ANTIMICROB_ANT 
	private String internacaoPrevia; 							// C3.INTERNACAO_PREVIA 
	private String inicioInfeccao; 								// C3.INICIO_INFECCAO 
	private String infecRelProcedInvasivo;					 	// C3.INFEC_REL_PROCED_INVASIVO 
	private String sondaVesicalDemora; 							
	private BigDecimal pesoEstimado; 							// C3.PESO_ESTIMADO
	private String condutaBaseProtAssist;					
	private String insufHepatica;		 					 
	private String vantagemNsPadronizacao; 						// C3.VANTAGEM_NS_PADRONIZACAO
	private String usoCronicoPrevInt;						
	private BigDecimal custoDiarioEstReal; 						// C3.CUSTO_DIARIO_EST_REAL
	private String refBibliograficas; 							// C3.REF_BIBLIOGRAFICAS
	private String diagnostico; 								// C3.DIAGNOSTICO
	private String ecog; 										// C3.ECOG
	private String intencaoTrat; 								// C3.INTENCAO_TRAT
	private String linhaTrat; 									// C3.LINHA_TRAT
	private String tratAntCirurgia; 							// C3.TRAT_ANT_CIRURGIA
	private String tratAntRadio; 								// C3.TRAT_ANT_RADIO
	private String tratAntQuimio; 								// C3.TRAT_ANT_QUIMIO
	private Date mesAnoUltCiclo; 								// C3.MES_ANO_ULT_CICLO
	private String tratAntHormonio; 							// C3.TRAT_ANT_HORMONIO
	private String tratAntOutros; 								// C3.TRAT_ANT_OUTROS
	private Date criadoEm;			 							// C3.CRIADO_EM  
	private String tipoInfeccao; 								// C3.TIPO_INFECCAO2
	private String sitAntibiograma; 							// C3.SIT_ANTIBIOGRAMA2
	private String nomeGerme;									// C3.NOME_GERME
	private String sensibilidadeAntibiotico;					// C3.SENSIBILIDADE_ANTIBIOTICO
	private String orientacaoAvaliador;						
	private String justificativa; 								// C3.JUSTIFICATIVA 
	private String situacao;
	private Integer prontuario; 								// C3.PRONTUARIO1
	private String nome; 										// C3.NOME
	private String sexo; 										// C3.SEXO
	private Date dtNascimento; 									// C3.DT_NASCIMENTO2
	private String ltoLtoId;
	private Short qrtNumero;
	private Short unfSeq;
	private Integer intSeq;
	private Integer atuSeq;
	private Integer hodSeq;
	private Integer atdSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String clcDescricao; 								// C3.DESCRICAO6
	private String tebDescricao; 								// C3.DESCRICAO1
	private String tetDescricao; 								// C3.DESCRICAO
	private Date dtPrevAlta;	 								// C3.DT_PREV_ALTA
	private DominioIndRespAvaliacao responsavelAvaliacao;
	
	/* Campo obtido atraves da concatenacao de tebDescricao e tetDescricao */
	private String descricao6;									// C3.DESCRICAO6
	
	/* Campo obtido atraves da MPMC_IDA_ANO_MES_DIA */
	private String idade; 										// C3.NOME1
	
	/* Campo obtido atraves da MPMC_LOCAL_PAC */
	private String localizacao; 								// C3.LTO_LTO_ID1
	
	/* Campo obtido atraves da MPMC_VER_DT_INI_ATD */
	private Date dtHrInternacao; 								// C3.DTHR_INTERNACAO
	
	/* Campo obtido atraves da MPMC_VER_CONVENIO */
	private String convenio;									// C3.DESCRICAO1
	
	/* Campo obtido atraves da AFAC_GET_NOME_SERV */
	private String equipe; 										// C3.NOME2
	
	/* Campo obtido atraves da AFAC_GET_REG_SERV */
	private String nroRegConselho; 								// C3.NRO_REG_CONSELHO
	
	/* Campo obtido atraves da AFAC_GET_SIGLA_SERV */
	private String cprSigla; 									// C3.CPR_SIGLA
	
	/* Campo obtido atraves da F9 (#45269) */
	private String cfSolicitante;
	
	/* Campo obtido atraves da C2 (#45269) */
	private String descricao3; 									// C2.DESCRICAO3
	
	/* Campos obtidos atraves da C4 (#45269) */
	private String medDescricaoEdit; 							// C4.MED_DESCRICAO_EDIT
	private String descricao5; 									// C4.DESCRICAO5
	private String observacao;									// C4.OBSERVACAO3
	private String duracaoTrat; 									// C4.DURACAO_TRAT
	private String duracaoTratAprov; 							// C4.DURACAO_TRAT_APROV
	private Date dtHrInicioTratamento;							// C4.DTHR_INICIO_TRATAMENTO
	private Date dtHrParecer;									// C4.DTHR_PARECER
	private String nomeAval;									// C4.NOME_AVAL
	private String nroRegConselhoAval;							// C4.NRO_REG_CONSELHO_AVAL
	private String cprSiglaAval;								// C4.CPR_SIGLA_AVAL
	
	/* Campo obtido atraves da F1 (#45269) */
	private boolean exibeJustificativa;
	
	/* Campo obtido atraves da F2 (#45269) */
	private boolean exibeUsoRestrAntimicrobianoIgualN;
	
	/* Campo obtido atraves da F3 (#45269) */
	private boolean exibeUsoRestrAntimicrobianoIgualS;
	
	/* Campo obtido atraves da F4 (#45269) */
	private boolean exibeNaoPadronAntimicrobianoIgualN;
	
	/* Campo obtido atraves da F5 (#45269) */
	private boolean exibeNaoPadronAntimicrobianoIgualS;
	
	/* Campo obtido atraves da F6 (#45269) */
	private boolean exibeQuimioterapico;
	
	/* Campo obtido atraves da C3 (#45250) */
	private String descricao2;
	
	/* Campo obtido atraves da F8 (#45250) */
	private String titulo;
	
	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Date getDtHrInternacao() {
		return dtHrInternacao;
	}

	public void setDtHrInternacao(Date dtHrInternacao) {
		this.dtHrInternacao = dtHrInternacao;
	}

	public Date getDtPrevAlta() {
		return dtPrevAlta;
	}

	public void setDtPrevAlta(Date dtPrevAlta) {
		this.dtPrevAlta = dtPrevAlta;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getCprSigla() {
		return cprSigla;
	}

	public void setCprSigla(String cprSigla) {
		this.cprSigla = cprSigla;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCfSolicitante() {
		return cfSolicitante;
	}

	public void setCfSolicitante(String cfSolicitante) {
		this.cfSolicitante = cfSolicitante;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getTipoInfeccao() {
		return tipoInfeccao;
	}

	public void setTipoInfeccao(String tipoInfeccao) {
		this.tipoInfeccao = tipoInfeccao;
	}

	public String getSitAntibiograma() {
		return sitAntibiograma;
	}

	public void setSitAntibiograma(String sitAntibiograma) {
		this.sitAntibiograma = sitAntibiograma;
	}

	public String getNomeGerme() {
		return nomeGerme;
	}

	public void setNomeGerme(String nomeGerme) {
		this.nomeGerme = nomeGerme;
	}

	public String getSensibilidadeAntibiotico() {
		return sensibilidadeAntibiotico;
	}

	public void setSensibilidadeAntibiotico(String sensibilidadeAntibiotico) {
		this.sensibilidadeAntibiotico = sensibilidadeAntibiotico;
	}

	public String getIndicacao() {
		return indicacao;
	}

	public void setIndicacao(String indicacao) {
		this.indicacao = indicacao;
	}

	public String getInfeccaoTratar() {
		return infeccaoTratar;
	}

	public void setInfeccaoTratar(String infeccaoTratar) {
		this.infeccaoTratar = infeccaoTratar;
	}

	public String getTratAntimicrobAnt() {
		return tratAntimicrobAnt;
	}

	public void setTratAntimicrobAnt(String tratAntimicrobAnt) {
		this.tratAntimicrobAnt = tratAntimicrobAnt;
	}

	public String getInternacaoPrevia() {
		return internacaoPrevia;
	}

	public void setInternacaoPrevia(String internacaoPrevia) {
		this.internacaoPrevia = internacaoPrevia;
	}

	public String getInicioInfeccao() {
		return inicioInfeccao;
	}

	public void setInicioInfeccao(String inicioInfeccao) {
		this.inicioInfeccao = inicioInfeccao;
	}

	public String getInfecRelProcedInvasivo() {
		return infecRelProcedInvasivo;
	}

	public void setInfecRelProcedInvasivo(String infecRelProcedInvasivo) {
		this.infecRelProcedInvasivo = infecRelProcedInvasivo;
	}

	public BigDecimal getPesoEstimado() {
		return pesoEstimado;
	}

	public void setPesoEstimado(BigDecimal pesoEstimado) {
		this.pesoEstimado = pesoEstimado;
	}

	public String getVantagemNsPadronizacao() {
		return vantagemNsPadronizacao;
	}

	public void setVantagemNsPadronizacao(String vantagemNsPadronizacao) {
		this.vantagemNsPadronizacao = vantagemNsPadronizacao;
	}

	public BigDecimal getCustoDiarioEstReal() {
		return custoDiarioEstReal;
	}

	public void setCustoDiarioEstReal(BigDecimal custoDiarioEstReal) {
		this.custoDiarioEstReal = custoDiarioEstReal;
	}

	public String getRefBibliograficas() {
		return refBibliograficas;
	}

	public void setRefBibliograficas(String refBibliograficas) {
		this.refBibliograficas = refBibliograficas;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getEcog() {
		return ecog;
	}

	public void setEcog(String ecog) {
		this.ecog = ecog;
	}

	public String getIntencaoTrat() {
		return intencaoTrat;
	}

	public void setIntencaoTrat(String intencaoTrat) {
		this.intencaoTrat = intencaoTrat;
	}

	public String getLinhaTrat() {
		return linhaTrat;
	}

	public void setLinhaTrat(String linhaTrat) {
		this.linhaTrat = linhaTrat;
	}

	public String getTratAntCirurgia() {
		return tratAntCirurgia;
	}

	public void setTratAntCirurgia(String tratAntCirurgia) {
		this.tratAntCirurgia = tratAntCirurgia;
	}

	public String getTratAntRadio() {
		return tratAntRadio;
	}

	public void setTratAntRadio(String tratAntRadio) {
		this.tratAntRadio = tratAntRadio;
	}

	public String getTratAntQuimio() {
		return tratAntQuimio;
	}

	public void setTratAntQuimio(String tratAntQuimio) {
		this.tratAntQuimio = tratAntQuimio;
	}

	public Date getMesAnoUltCiclo() {
		return mesAnoUltCiclo;
	}

	public void setMesAnoUltCiclo(Date mesAnoUltCiclo) {
		this.mesAnoUltCiclo = mesAnoUltCiclo;
	}

	public String getTratAntHormonio() {
		return tratAntHormonio;
	}

	public void setTratAntHormonio(String tratAntHormonio) {
		this.tratAntHormonio = tratAntHormonio;
	}

	public String getTratAntOutros() {
		return tratAntOutros;
	}

	public void setTratAntOutros(String tratAntOutros) {
		this.tratAntOutros = tratAntOutros;
	}

	public String getDescricao3() {
		return descricao3;
	}

	public void setDescricao3(String descricao3) {
		this.descricao3 = descricao3;
	}

	public String getMedDescricaoEdit() {
		return medDescricaoEdit;
	}

	public void setMedDescricaoEdit(String medDescricaoEdit) {
		this.medDescricaoEdit = medDescricaoEdit;
	}

	public String getDescricao5() {
		return descricao5;
	}

	public void setDescricao5(String descricao5) {
		this.descricao5 = descricao5;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getDuracaoTrat() {
		return duracaoTrat;
	}

	public void setDuracaoTrat(String duracaoTrat) {
		this.duracaoTrat = duracaoTrat;
	}

	public String getDuracaoTratAprov() {
		return duracaoTratAprov;
	}

	public void setDuracaoTratAprov(String duracaoTratAprov) {
		this.duracaoTratAprov = duracaoTratAprov;
	}

	public Date getDtHrInicioTratamento() {
		return dtHrInicioTratamento;
	}

	public void setDtHrInicioTratamento(Date dtHrInicioTratamento) {
		this.dtHrInicioTratamento = dtHrInicioTratamento;
	}

	public Date getDtHrParecer() {
		return dtHrParecer;
	}

	public void setDtHrParecer(Date dtHrParecer) {
		this.dtHrParecer = dtHrParecer;
	}

	public String getNomeAval() {
		return nomeAval;
	}

	public void setNomeAval(String nomeAval) {
		this.nomeAval = nomeAval;
	}

	public String getNroRegConselhoAval() {
		return nroRegConselhoAval;
	}

	public void setNroRegConselhoAval(String nroRegConselhoAval) {
		this.nroRegConselhoAval = nroRegConselhoAval;
	}

	public String getCprSiglaAval() {
		return cprSiglaAval;
	}

	public void setCprSiglaAval(String cprSiglaAval) {
		this.cprSiglaAval = cprSiglaAval;
	}

	public Short getGupSeq() {
		return gupSeq;
	}

	public void setGupSeq(Short gupSeq) {
		this.gupSeq = gupSeq;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Integer getAtuSeq() {
		return atuSeq;
	}

	public void setAtuSeq(Integer atuSeq) {
		this.atuSeq = atuSeq;
	}

	public Integer getHodSeq() {
		return hodSeq;
	}

	public void setHodSeq(Integer hodSeq) {
		this.hodSeq = hodSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getGestante() {
		return gestante;
	}

	public void setGestante(String gestante) {
		this.gestante = gestante;
	}

	public String getFuncRenalComprometida() {
		return funcRenalComprometida;
	}

	public void setFuncRenalComprometida(String funcRenalComprometida) {
		this.funcRenalComprometida = funcRenalComprometida;
	}

	public String getPacImunodeprimido() {
		return pacImunodeprimido;
	}

	public void setPacImunodeprimido(String pacImunodeprimido) {
		this.pacImunodeprimido = pacImunodeprimido;
	}

	public String getSondaVesicalDemora() {
		return sondaVesicalDemora;
	}

	public void setSondaVesicalDemora(String sondaVesicalDemora) {
		this.sondaVesicalDemora = sondaVesicalDemora;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getClcDescricao() {
		return clcDescricao;
	}

	public void setClcDescricao(String clcDescricao) {
		this.clcDescricao = clcDescricao;
	}

	public String getTebDescricao() {
		return tebDescricao;
	}

	public void setTebDescricao(String tebDescricao) {
		this.tebDescricao = tebDescricao;
	}

	public String getTetDescricao() {
		return tetDescricao;
	}

	public void setTetDescricao(String tetDescricao) {
		this.tetDescricao = tetDescricao;
	}

	public String getCondutaBaseProtAssist() {
		return condutaBaseProtAssist;
	}

	public void setCondutaBaseProtAssist(String condutaBaseProtAssist) {
		this.condutaBaseProtAssist = condutaBaseProtAssist;
	}

	public String getInsufHepatica() {
		return insufHepatica;
	}

	public void setInsufHepatica(String insufHepatica) {
		this.insufHepatica = insufHepatica;
	}

	public String getUsoCronicoPrevInt() {
		return usoCronicoPrevInt;
	}

	public void setUsoCronicoPrevInt(String usoCronicoPrevInt) {
		this.usoCronicoPrevInt = usoCronicoPrevInt;
	}

	public String getOrientacaoAvaliador() {
		return orientacaoAvaliador;
	}

	public void setOrientacaoAvaliador(String orientacaoAvaliador) {
		this.orientacaoAvaliador = orientacaoAvaliador;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getDescricao6() {
		return descricao6;
	}

	public void setDescricao6(String descricao6) {
		this.descricao6 = descricao6;
	}

	public boolean isExibeJustificativa() {
		return exibeJustificativa;
	}

	public void setExibeJustificativa(boolean exibeJustificativa) {
		this.exibeJustificativa = exibeJustificativa;
	}

	public boolean isExibeUsoRestrAntimicrobianoIgualN() {
		return exibeUsoRestrAntimicrobianoIgualN;
	}

	public void setExibeUsoRestrAntimicrobianoIgualN(
			boolean exibeUsoRestrAntimicrobianoIgualN) {
		this.exibeUsoRestrAntimicrobianoIgualN = exibeUsoRestrAntimicrobianoIgualN;
	}

	public boolean isExibeUsoRestrAntimicrobianoIgualS() {
		return exibeUsoRestrAntimicrobianoIgualS;
	}

	public void setExibeUsoRestrAntimicrobianoIgualS(
			boolean exibeUsoRestrAntimicrobianoIgualS) {
		this.exibeUsoRestrAntimicrobianoIgualS = exibeUsoRestrAntimicrobianoIgualS;
	}

	public boolean isExibeNaoPadronAntimicrobianoIgualN() {
		return exibeNaoPadronAntimicrobianoIgualN;
	}

	public void setExibeNaoPadronAntimicrobianoIgualN(
			boolean exibeNaoPadronAntimicrobianoIgualN) {
		this.exibeNaoPadronAntimicrobianoIgualN = exibeNaoPadronAntimicrobianoIgualN;
	}

	public boolean isExibeNaoPadronAntimicrobianoIgualS() {
		return exibeNaoPadronAntimicrobianoIgualS;
	}

	public void setExibeNaoPadronAntimicrobianoIgualS(
			boolean exibeNaoPadronAntimicrobianoIgualS) {
		this.exibeNaoPadronAntimicrobianoIgualS = exibeNaoPadronAntimicrobianoIgualS;
	}

	public boolean isExibeQuimioterapico() {
		return exibeQuimioterapico;
	}

	public void setExibeQuimioterapico(boolean exibeQuimioterapico) {
		this.exibeQuimioterapico = exibeQuimioterapico;
	}
	
	public DominioIndRespAvaliacao getResponsavelAvaliacao() {
		return responsavelAvaliacao;
	}

	public void setResponsavelAvaliacao(DominioIndRespAvaliacao responsavelAvaliacao) {
		this.responsavelAvaliacao = responsavelAvaliacao;
	}

	public String getDescricao2() {
		return descricao2;
	}

	public void setDescricao2(String descricao2) {
		this.descricao2 = descricao2;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}
