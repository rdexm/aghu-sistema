package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class SumarioAtdRecemNascidoSlPartoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3622872087452402263L;

	//ID RN
	private String nome;
	private String prontuario;
	private Date dtHrNascimento;
	private String sexo;
	
	//ID Mae
	private String nomeMae;
	private String prontuarioMae;
	private String idadeMae;
	private String alturaMae;
	private Date dtAtendimentoMae;
	private String pesoMae;
	private String convenioMae;
	
	//Gestacao Atual
	private Byte gesta;
	private Byte para;
	private Byte cesarea;
	private Byte aborto;
	private Byte ectopica;
	private String gemelar;
	private Date dum;
	private Date dtPrimeiraEco;
	private String idadeGestPrimeiraEco; // campos 20 e 21 do relatorio (concatenados)
	private String dtInformadaIG;
	private String idadeGestacionalInformada; // x semanas / x dias
	private Byte nroConsultasPreNatal;
	private Date dtPrimeiraConsulta;
	private String tipoSangueMae;
	private String tipoSanguePai;
	private String coombs;
	private String descricaoInterAtual;
	private String complementoInterAtual;
	private String descricaoInterPassada;
	private Boolean exibirIdadeGestacao;
	private String usoMedicamento;
	private List<DescricaoIntercorrenciaVO> intercorrenciasPassadas;
	
	//Gestações Anteriores
	private List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores;

	//Antecedentes Familiares
	private String antecedenteMae;
	private String antecedenteIrma;
	private String diabeteFamilia;
	private String doencasCongenitas;
	private String hifObservacao;
	
	//Exames realizados Mae
	private List<SumarioAtdRecemNascidoSlPartoExamesMaeVO> examesMae = new ArrayList<SumarioAtdRecemNascidoSlPartoExamesMaeVO>();
	private String observacao; // Observacao exame(subExamesRealizadosMae)
	
	//Parto/Cesariana
	private String tempoBolsaRota;
	private String formaRuptura;
	private Date dtHrRompimento;
	private String liquidoAminiotico;
	private String odorLiquido;
	private String indAmnioscopia;
	private String monitoramentos;
	private Date dtNascimento;
	private String tipoNascimento;
	private String modoNascimento;
	private String episiotomia;
	private Short periodoDilatacao;
	private Short periodoExpulsivo;
	private String duracaoParto;
	private String tipoForcipe;
	private String tamanhoForcipe;
	private String indForcipeRotacao;
	private String indCesariana;
	private String duracaoCesaria;
	private String laparotomia;
	private String histerotomia;
	private String histerorrafia;
	private String contaminacao;
	private String indLaqueaduraTubaria;
	private String indRafiaPeritonial;
	private String indLavagemCavidade;
	private String indDrenos;
	private String pesoPlacenta;
	private String comprimentoCordao;
	private String observacaoNascimento;
	
	//Recem Nascido
	private String corRecemNascido;
	private String pesoRecemNascido;
	private String classificacaoRecemNascido;
	private String apgarUmMin;
	private String apgarCincoMin;
	private String apgarDezMin;
	private String mensagemCordao;
	private String reanimacao;
	private String indObito;
	private String descricaoPni;
	private String doseRnr;
	private String unidadeRnr;
	private String vadSiglaRnr;
	private List<SumarioAtdRecemNascidoMedicamentosVO> medicamentosRecemNascido;
	
	private String informacoesComplementaresRN;
	private String volGastrico;
	private String aspectoGastr;
	private String odorFetidoGastr;
	private String observacaoRecemNascido;
	private String dtHrMovimento;
	private Date dtHrMovimentoOriginal;
	private String responsavel;
	private String evacuou;
	private String surfactante;
	private String urinou;
	private String lavadoGastrico;
	private String amamentado;
	private String aspiracaoTet;
	private String inalatorio;
	private String ventPorMascara;
	private String massaCardiacaExt;
	private String aspiracao;
	private String nasPeriodoDilatacao;
	private String nasPeriodoExpulsivo;
	
	//Monitoramento
	private String taquicardia;
	private String semAcelerarTrans;
	private String analgesiaBpd;
	private String analgesiaBsd;
	private String varBatidaMenor10;
	private String cardiotocografia;
	
	
	private List<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO> profissionaisEnvolvidos;

	//Notas adicionais
	/*private String notaAdicional;
	private Date criadoEm;
	private String nomeProf;
	private String nadCriadoEm;*/
	private List<LinhaReportVO> listaNotasAdicionais;
	
	//Cesariana indicação
	private String cesarianaIndicacao;
	
	// ########## parametros ###########
	private Integer pacCodigo;
	private Integer pacCodigoRN;
	private Byte seqp;
	private Short gsoSeqp;
	private McoGestacoes gestacao;
	
	private Integer conNumero;
	// mco_nascimento
	private McoNascimentos nascimento;
	
	// mco_recem_nascido
	private McoRecemNascidos recemNascido;
	
	
	// mco_gestacao_paciente
	private Integer gpaInaSeq;
	
	
	
	// mco_indicacao_nascimentos
	private Integer naiInaSeq;
	private String naiSeq;
	private String naiDescIndForcipe;
	
	private BigDecimal mcoInterGesOpaSeq;
	private String mcoInterGesOcorrencia;
	private String mcoInterGesComplemento;
	
	private BigDecimal mcoInterGesOpaSeqPassada;
	private String mcoInterGesOcorrenciaPassada;
	private String mcoInterGesComplementoPassada;
	
	
	//mco_intercor_pasatus
	private Integer mcoInterPasatusSeq;
	private String mcoInterPasatusDescricao;
	private String mcoInterPasatusSituacao;
	private String mcoInterPasatusMsgAlerta;
	private Integer mcoInterPasatusCidSeq;

	// agh_atendimentos
	private Integer atdSeq;
	private String atdIndPac;
	
	// ael_solicitacao_exames
	private Integer soeNroSolExa;
	
	// ael_item_solicitacao_exames
	private Integer codSangueCordao;

	private String  mcoNascIndNaiSeqp;
	private String mcoNascIndNaiInaSeq;
	
	private McoCesarianas cesariana;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public Date getDtHrNascimento() {
		return dtHrNascimento;
	}
	public void setDtHrNascimento(Date dtHrNascimento) {
		this.dtHrNascimento = dtHrNascimento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	public String getProntuarioMae() {
		return prontuarioMae;
	}
	public void setProntuarioMae(String prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}
	public String getIdadeMae() {
		return idadeMae;
	}
	public void setIdadeMae(String idadeMae) {
		this.idadeMae = idadeMae;
	}
	public String getAlturaMae() {
		return alturaMae;
	}
	public void setAlturaMae(String alturaMae) {
		this.alturaMae = alturaMae;
	}
	public Date getDtAtendimentoMae() {
		return dtAtendimentoMae;
	}
	public void setDtAtendimentoMae(Date dtAtendimentoMae) {
		this.dtAtendimentoMae = dtAtendimentoMae;
	}
	public String getPesoMae() {
		return pesoMae;
	}
	public void setPesoMae(String pesoMae) {
		this.pesoMae = pesoMae;
	}
	public String getConvenioMae() {
		return convenioMae;
	}
	public void setConvenioMae(String convenioMae) {
		this.convenioMae = convenioMae;
	}
	public Byte getGesta() {
		return gesta;
	}
	public void setGesta(Byte gesta) {
		this.gesta = gesta;
	}
	public Byte getPara() {
		return para;
	}
	public void setPara(Byte para) {
		this.para = para;
	}
	public Byte getCesarea() {
		return cesarea;
	}
	public void setCesarea(Byte cesarea) {
		this.cesarea = cesarea;
	}
	public Byte getAborto() {
		return aborto;
	}
	public void setAborto(Byte aborto) {
		this.aborto = aborto;
	}
	public Byte getEctopica() {
		return ectopica;
	}
	public void setEctopica(Byte ectopica) {
		this.ectopica = ectopica;
	}
	public String getGemelar() {
		return gemelar;
	}
	public void setGemelar(String gemelar) {
		this.gemelar = gemelar;
	}
	public Date getDum() {
		return dum;
	}
	public void setDum(Date dum) {
		this.dum = dum;
	}
	public Date getDtPrimeiraEco() {
		return dtPrimeiraEco;
	}
	public void setDtPrimeiraEco(Date dtPrimeiraEco) {
		this.dtPrimeiraEco = dtPrimeiraEco;
	}
	public String getIdadeGestPrimeiraEco() {
		return idadeGestPrimeiraEco;
	}
	public void setIdadeGestPrimeiraEco(String idadeGestPrimeiraEco) {
		this.idadeGestPrimeiraEco = idadeGestPrimeiraEco;
	}
	public String getDtInformadaIG() {
		return dtInformadaIG;
	}
	public void setDtInformadaIG(String dtInformadaIG) {
		this.dtInformadaIG = dtInformadaIG;
	}
	public String getIdadeGestacionalInformada() {
		return idadeGestacionalInformada;
	}
	public void setIdadeGestacionalInformada(String idadeGestacionalInformada) {
		this.idadeGestacionalInformada = idadeGestacionalInformada;
	}
	public Byte getNroConsultasPreNatal() {
		return nroConsultasPreNatal;
	}
	public void setNroConsultasPreNatal(Byte nroConsultasPreNatal) {
		this.nroConsultasPreNatal = nroConsultasPreNatal;
	}
	public Date getDtPrimeiraConsulta() {
		return dtPrimeiraConsulta;
	}
	public void setDtPrimeiraConsulta(Date dtPrimeiraConsulta) {
		this.dtPrimeiraConsulta = dtPrimeiraConsulta;
	}
	public String getTipoSangueMae() {
		return tipoSangueMae;
	}
	public void setTipoSangueMae(String tipoSangueMae) {
		this.tipoSangueMae = tipoSangueMae;
	}
	public String getTipoSanguePai() {
		return tipoSanguePai;
	}
	public void setTipoSanguePai(String tipoSanguePai) {
		this.tipoSanguePai = tipoSanguePai;
	}
	public String getCoombs() {
		return coombs;
	}
	public void setCoombs(String coombs) {
		this.coombs = coombs;
	}
	public String getDescricaoInterAtual() {
		return descricaoInterAtual;
	}
	public void setDescricaoInterAtual(String descricaoInterAtual) {
		this.descricaoInterAtual = descricaoInterAtual;
	}
	public String getComplementoInterAtual() {
		return complementoInterAtual;
	}
	public void setComplementoInterAtual(String complementoInterAtual) {
		this.complementoInterAtual = complementoInterAtual;
	}
	public String getDescricaoInterPassada() {
		return descricaoInterPassada;
	}
	public void setDescricaoInterPassada(String descricaoInterPassada) {
		this.descricaoInterPassada = descricaoInterPassada;
	}
	public List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> getGestacoesAnteriores() {
		return gestacoesAnteriores;
	}
	public void setGestacoesAnteriores(
			List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores) {
		this.gestacoesAnteriores = gestacoesAnteriores;
	}
	public String getAntecedenteMae() {
		return antecedenteMae;
	}
	public void setAntecedenteMae(String antecedenteMae) {
		this.antecedenteMae = antecedenteMae;
	}
	public String getAntecedenteIrma() {
		return antecedenteIrma;
	}
	public void setAntecedenteIrma(String antecedenteIrma) {
		this.antecedenteIrma = antecedenteIrma;
	}
	public String getDiabeteFamilia() {
		return diabeteFamilia;
	}
	public void setDiabeteFamilia(String diabeteFamilia) {
		this.diabeteFamilia = diabeteFamilia;
	}
	public String getDoencasCongenitas() {
		return doencasCongenitas;
	}
	public void setDoencasCongenitas(String doencasCongenitas) {
		this.doencasCongenitas = doencasCongenitas;
	}
	public String getHifObservacao() {
		return hifObservacao;
	}
	public void setHifObservacao(String hifObservacao) {
		this.hifObservacao = hifObservacao;
	}
	public List<SumarioAtdRecemNascidoSlPartoExamesMaeVO> getExamesMae() {
		return examesMae;
	}
	public void setExamesMae(List<SumarioAtdRecemNascidoSlPartoExamesMaeVO> examesMae) {
		this.examesMae = examesMae;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getTempoBolsaRota() {
		return tempoBolsaRota;
	}
	public void setTempoBolsaRota(String tempoBolsaRota) {
		this.tempoBolsaRota = tempoBolsaRota;
	}
	public String getFormaRuptura() {
		return formaRuptura;
	}
	public void setFormaRuptura(String formaRuptura) {
		this.formaRuptura = formaRuptura;
	}
	public Date getDtHrRompimento() {
		return dtHrRompimento;
	}
	public void setDtHrRompimento(Date dtHrRompimento) {
		this.dtHrRompimento = dtHrRompimento;
	}
	public String getLiquidoAminiotico() {
		return liquidoAminiotico;
	}
	public void setLiquidoAminiotico(String liquidoAminiotico) {
		this.liquidoAminiotico = liquidoAminiotico;
	}
	public String getOdorLiquido() {
		return odorLiquido;
	}
	public void setOdorLiquido(String odorLiquido) {
		this.odorLiquido = odorLiquido;
	}
	public String getIndAmnioscopia() {
		return indAmnioscopia;
	}
	public void setIndAmnioscopia(String indAmnioscopia) {
		this.indAmnioscopia = indAmnioscopia;
	}
	public String getMonitoramentos() {
		return monitoramentos;
	}
	public void setMonitoramentos(String monitoramentos) {
		this.monitoramentos = monitoramentos;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public String getTipoNascimento() {
		return tipoNascimento;
	}
	public void setTipoNascimento(String tipoNascimento) {
		this.tipoNascimento = tipoNascimento;
	}
	public String getModoNascimento() {
		return modoNascimento;
	}
	public void setModoNascimento(String modoNascimento) {
		this.modoNascimento = modoNascimento;
	}
	public String getEpisiotomia() {
		return episiotomia;
	}
	public void setEpisiotomia(String episiotomia) {
		this.episiotomia = episiotomia;
	}
	public Short getPeriodoDilatacao() {
		return periodoDilatacao;
	}
	public void setPeriodoDilatacao(Short periodoDilatacao) {
		this.periodoDilatacao = periodoDilatacao;
	}
	public Short getPeriodoExpulsivo() {
		return periodoExpulsivo;
	}
	public void setPeriodoExpulsivo(Short periodoExpulsivo) {
		this.periodoExpulsivo = periodoExpulsivo;
	}
	public String getDuracaoParto() {
		return duracaoParto;
	}
	public void setDuracaoParto(String duracaoParto) {
		this.duracaoParto = duracaoParto;
	}
	public String getIndForcipe() {
		return getNaiDescIndForcipe();
	}
	public String getTipoForcipe() {
		return tipoForcipe;
	}
	public void setTipoForcipe(String tipoForcipe) {
		this.tipoForcipe = tipoForcipe;
	}
	
	public String getTamanhoForcipe() {
		return tamanhoForcipe;
	}
	public void setTamanhoForcipe(String tamanhoForcipe) {
		this.tamanhoForcipe = tamanhoForcipe;
	}
	
	public String getIndForcipeRotacao() {
		return indForcipeRotacao;
	}
	public void setIndForcipeRotacao(String indForcipeRotacao) {
		this.indForcipeRotacao = indForcipeRotacao;
	}
	public String getIndCesariana() {
		return indCesariana;
	}
	public void setIndCesariana(String indCesariana) {
		this.indCesariana = indCesariana;
	}
	public String getDuracaoCesaria() {
		return duracaoCesaria;
	}
	public void setDuracaoCesaria(String duracaoCesaria) {
		this.duracaoCesaria = duracaoCesaria;
	}
	public String getLaparotomia() {
		return laparotomia;
	}
	public void setLaparotomia(String laparotomia) {
		this.laparotomia = laparotomia;
	}
	public String getHisterotomia() {
		return histerotomia;
	}
	public void setHisterotomia(String histerotomia) {
		this.histerotomia = histerotomia;
	}
	public String getHisterorrafia() {
		return histerorrafia;
	}
	public void setHisterorrafia(String histerorrafia) {
		this.histerorrafia = histerorrafia;
	}
	public String getContaminacao() {
		return contaminacao;
	}
	public void setContaminacao(String contaminacao) {
		this.contaminacao = contaminacao;
	}
	public String getIndLaqueaduraTubaria() {
		return indLaqueaduraTubaria;
	}
	public void setIndLaqueaduraTubaria(String indLaqueaduraTubaria) {
		this.indLaqueaduraTubaria = indLaqueaduraTubaria;
	}
	public String getIndRafiaPeritonial() {
		return indRafiaPeritonial;
	}
	public void setIndRafiaPeritonial(String indRafiaPeritonial) {
		this.indRafiaPeritonial = indRafiaPeritonial;
	}
	public String getIndLavagemCavidade() {
		return indLavagemCavidade;
	}
	public void setIndLavagemCavidade(String indLavagemCavidade) {
		this.indLavagemCavidade = indLavagemCavidade;
	}
	public String getIndDrenos() {
		return indDrenos;
	}
	public void setIndDrenos(String indDrenos) {
		this.indDrenos = indDrenos;
	}
	public String getPesoPlacenta() {
		return pesoPlacenta;
	}
	public void setPesoPlacenta(String pesoPlacenta) {
		this.pesoPlacenta = pesoPlacenta;
	}
	public String getComprimentoCordao() {
		return comprimentoCordao;
	}
	public void setComprimentoCordao(String comprimentoCordao) {
		this.comprimentoCordao = comprimentoCordao;
	}
	public String getObservacaoNascimento() {
		return observacaoNascimento;
	}
	public void setObservacaoNascimento(String observacaoNascimento) {
		this.observacaoNascimento = observacaoNascimento;
	}
	public String getCorRecemNascido() {
		return corRecemNascido;
	}
	public void setCorRecemNascido(String corRecemNascido) {
		this.corRecemNascido = corRecemNascido;
	}
	public String getPesoRecemNascido() {
		return pesoRecemNascido;
	}
	public void setPesoRecemNascido(String pesoRecemNascido) {
		this.pesoRecemNascido = pesoRecemNascido;
	}
	public String getClassificacaoRecemNascido() {
		return classificacaoRecemNascido;
	}
	public void setClassificacaoRecemNascido(String classificacaoRecemNascido) {
		this.classificacaoRecemNascido = classificacaoRecemNascido;
	}
	public String getApgarUmMin() {
		return apgarUmMin;
	}
	public void setApgarUmMin(String apgarUmMin) {
		this.apgarUmMin = apgarUmMin;
	}
	public String getApgarCincoMin() {
		return apgarCincoMin;
	}
	public void setApgarCincoMin(String apgarCincoMin) {
		this.apgarCincoMin = apgarCincoMin;
	}
	public String getApgarDezMin() {
		return apgarDezMin;
	}
	public void setApgarDezMin(String apgarDezMin) {
		this.apgarDezMin = apgarDezMin;
	}
	public String getMensagemCordao() {
		return mensagemCordao;
	}
	public void setMensagemCordao(String mensagemCordao) {
		this.mensagemCordao = mensagemCordao;
	}
	public String getReanimacao() {
		return reanimacao;
	}
	public void setReanimacao(String reanimacao) {
		this.reanimacao = reanimacao;
	}
	public String getIndObito() {
		return indObito;
	}
	public void setIndObito(String indObito) {
		this.indObito = indObito;
	}
	public String getDescricaoPni() {
		return descricaoPni;
	}
	public void setDescricaoPni(String descricaoPni) {
		this.descricaoPni = descricaoPni;
	}
	public String getDoseRnr() {
		return doseRnr;
	}
	public void setDoseRnr(String doseRnr) {
		this.doseRnr = doseRnr;
	}
	public String getUnidadeRnr() {
		return unidadeRnr;
	}
	public void setUnidadeRnr(String unidadeRnr) {
		this.unidadeRnr = unidadeRnr;
	}
	public String getVadSiglaRnr() {
		return vadSiglaRnr;
	}
	public void setVadSiglaRnr(String vadSiglaRnr) {
		this.vadSiglaRnr = vadSiglaRnr;
	}
	public String getInformacoesComplementaresRN() {
		return informacoesComplementaresRN;
	}
	public void setInformacoesComplementaresRN(String informacoesComplementaresRN) {
		this.informacoesComplementaresRN = informacoesComplementaresRN;
	}
	public String getVolGastrico() {
		return volGastrico;
	}
	public void setVolGastrico(String volGastrico) {
		this.volGastrico = volGastrico;
	}
	public String getAspectoGastr() {
		return aspectoGastr;
	}
	public void setAspectoGastr(String aspectoGastr) {
		this.aspectoGastr = aspectoGastr;
	}
	public String getOdorFetidoGastr() {
		return odorFetidoGastr;
	}
	public void setOdorFetidoGastr(String odorFetidoGastr) {
		this.odorFetidoGastr = odorFetidoGastr;
	}
	public String getObservacaoRecemNascido() {
		return observacaoRecemNascido;
	}
	public void setObservacaoRecemNascido(String observacaoRecemNascido) {
		this.observacaoRecemNascido = observacaoRecemNascido;
	}
	public String getDtHrMovimento() {
		return dtHrMovimento;
	}
	public void setDtHrMovimento(String dtHrMovimento) {
		this.dtHrMovimento = dtHrMovimento;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	public String getEvacuou() {
		return evacuou;
	}
	public void setEvacuou(String evacuou) {
		this.evacuou = evacuou;
	}
	public String getSurfactante() {
		return surfactante;
	}
	public void setSurfactante(String surfactante) {
		this.surfactante = surfactante;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Byte getSeqp() {
		return seqp;
	}
	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	/*
	public Integer getHifPacCodigo() {
		return hifPacCodigo;
	}
	public void setHifPacCodigo(Integer hifPacCodigo) {
		this.hifPacCodigo = hifPacCodigo;
	}
	*/
	public String getUrinou() {
		return urinou;
	}
	public void setUrinou(String urinou) {
		this.urinou = urinou;
	}
	public String getLavadoGastrico() {
		return lavadoGastrico;
	}
	public void setLavadoGastrico(String lavadoGastrico) {
		this.lavadoGastrico = lavadoGastrico;
	}
	public String getAmamentado() {
		return amamentado;
	}
	public void setAmamentado(String amamentado) {
		this.amamentado = amamentado;
	}
	public String getAspiracaoTet() {
		return aspiracaoTet;
	}
	public void setAspiracaoTet(String aspiracaoTet) {
		this.aspiracaoTet = aspiracaoTet;
	}
	public String getInalatorio() {
		return inalatorio;
	}
	public void setInalatorio(String inalatorio) {
		this.inalatorio = inalatorio;
	}
	public String getVentPorMascara() {
		return ventPorMascara;
	}
	public void setVentPorMascara(String ventPorMascara) {
		this.ventPorMascara = ventPorMascara;
	}
	public String getMassaCardiacaExt() {
		return massaCardiacaExt;
	}
	public void setMassaCardiacaExt(String massaCardiacaExt) {
		this.massaCardiacaExt = massaCardiacaExt;
	}
	public String getAspiracao() {
		return aspiracao;
	}
	public void setAspiracao(String aspiracao) {
		this.aspiracao = aspiracao;
	}
	public String getNasPeriodoDilatacao() {
		return nasPeriodoDilatacao;
	}
	public void setNasPeriodoDilatacao(String nasPeriodoDilatacao) {
		this.nasPeriodoDilatacao = nasPeriodoDilatacao;
	}
	public String getNasPeriodoExpulsivo() {
		return nasPeriodoExpulsivo;
	}
	public void setNasPeriodoExpulsivo(String nasPeriodoExpulsivo) {
		this.nasPeriodoExpulsivo = nasPeriodoExpulsivo;
	}
	public Integer getGpaInaSeq() {
		return gpaInaSeq;
	}
	public void setGpaInaSeq(Integer gpaInaSeq) {
		this.gpaInaSeq = gpaInaSeq;
	}
	public Integer getNaiInaSeq() {
		return naiInaSeq;
	}
	public void setNaiInaSeq(Integer naiInaSeq) {
		this.naiInaSeq = naiInaSeq;
	}
	public String getNaiSeq() {
		return naiSeq;
	}
	public void setNaiSeq(String naiSeq) {
		this.naiSeq = naiSeq;
	}
	public BigDecimal getMcoInterGesOpaSeq() {
		return mcoInterGesOpaSeq;
	}
	public void setMcoInterGesOpaSeq(BigDecimal mcoInterGesOpaSeq) {
		this.mcoInterGesOpaSeq = mcoInterGesOpaSeq;
	}
	public String getMcoInterGesOcorrencia() {
		return mcoInterGesOcorrencia;
	}
	public void setMcoInterGesOcorrencia(String mcoInterGesOcorrencia) {
		this.mcoInterGesOcorrencia = mcoInterGesOcorrencia;
	}
	public String getMcoInterGesComplemento() {
		return mcoInterGesComplemento;
	}
	public void setMcoInterGesComplemento(String mcoInterGesComplemento) {
		this.mcoInterGesComplemento = mcoInterGesComplemento;
	}
	
	public Integer getMcoInterPasatusSeq() {
		return mcoInterPasatusSeq;
	}
	public void setMcoInterPasatusSeq(Integer mcoInterPasatusSeq) {
		this.mcoInterPasatusSeq = mcoInterPasatusSeq;
	}
	public String getMcoInterPasatusDescricao() {
		return mcoInterPasatusDescricao;
	}
	public void setMcoInterPasatusDescricao(String mcoInterPasatusDescricao) {
		this.mcoInterPasatusDescricao = mcoInterPasatusDescricao;
	}
	public String getMcoInterPasatusSituacao() {
		return mcoInterPasatusSituacao;
	}
	public void setMcoInterPasatusSituacao(String mcoInterPasatusSituacao) {
		this.mcoInterPasatusSituacao = mcoInterPasatusSituacao;
	}
	public String getMcoInterPasatusMsgAlerta() {
		return mcoInterPasatusMsgAlerta;
	}
	public void setMcoInterPasatusMsgAlerta(String mcoInterPasatusMsgAlerta) {
		this.mcoInterPasatusMsgAlerta = mcoInterPasatusMsgAlerta;
	}
	public Integer getMcoInterPasatusCidSeq() {
		return mcoInterPasatusCidSeq;
	}
	public void setMcoInterPasatusCidSeq(Integer mcoInterPasatusCidSeq) {
		this.mcoInterPasatusCidSeq = mcoInterPasatusCidSeq;
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public String getAtdIndPac() {
		return atdIndPac;
	}
	public void setAtdIndPac(String atdIndPac) {
		this.atdIndPac = atdIndPac;
	}
	public Integer getSoeNroSolExa() {
		return soeNroSolExa;
	}
	public void setSoeNroSolExa(Integer soeNroSolExa) {
		this.soeNroSolExa = soeNroSolExa;
	}
	public String getTaquicardia() {
		return taquicardia;
	}
	public void setTaquicardia(String taquicardia) {
		this.taquicardia = taquicardia;
	}
	public String getSemAcelerarTrans() {
		return semAcelerarTrans;
	}
	public void setSemAcelerarTrans(String semAcelerarTrans) {
		this.semAcelerarTrans = semAcelerarTrans;
	}
	public String getAnalgesiaBpd() {
		return analgesiaBpd;
	}
	public void setAnalgesiaBpd(String analgesiaBpd) {
		this.analgesiaBpd = analgesiaBpd;
	}
	public String getAnalgesiaBsd() {
		return analgesiaBsd;
	}
	public void setAnalgesiaBsd(String analgesiaBsd) {
		this.analgesiaBsd = analgesiaBsd;
	}
	public String getVarBatidaMenor10() {
		return varBatidaMenor10;
	}
	public void setVarBatidaMenor10(String varBatidaMenor10) {
		this.varBatidaMenor10 = varBatidaMenor10;
	}
	public String getCardiotocografia() {
		return cardiotocografia;
	}
	public void setCardiotocografia(String cardiotocografia) {
		this.cardiotocografia = cardiotocografia;
	}
	
	public Short getGsoSeqp() {
		return gsoSeqp;
	}
	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}
	public McoGestacoes getGestacao() {
		return gestacao;
	}
	public void setGestacao(McoGestacoes gestacao) {
		this.gestacao = gestacao;
	}
	
	public String getCesarianaIndicacao() {
		return cesarianaIndicacao;
	}
	public void setCesarianaIndicacao(String cesarianaIndicacao) {
		this.cesarianaIndicacao = cesarianaIndicacao;
	}
	public Integer getCodSangueCordao() {
		return codSangueCordao;
	}
	public void setCodSangueCordao(Integer codSangueCordao) {
		this.codSangueCordao = codSangueCordao;
	}
	public McoNascimentos getNascimento() {
		return nascimento;
	}
	public void setNascimento(McoNascimentos nascimento) {
		this.nascimento = nascimento;
	}
	public McoRecemNascidos getRecemNascido() {
		return recemNascido;
	}
	public void setRecemNascido(McoRecemNascidos recemNascido) {
		this.recemNascido = recemNascido;
	}
	public String getMcoNascIndNaiSeqp() {
		return mcoNascIndNaiSeqp;
	}
	public void setMcoNascIndNaiSeqp(String mcoNascIndNaiSeqp) {
		this.mcoNascIndNaiSeqp = mcoNascIndNaiSeqp;
	}
	public String getMcoNascIndNaiInaSeq() {
		return mcoNascIndNaiInaSeq;
	}
	public void setMcoNascIndNaiInaSeq(String mcoNascIndNaiInaSeq) {
		this.mcoNascIndNaiInaSeq = mcoNascIndNaiInaSeq;
	}
	public String getNaiDescIndForcipe() {
		return naiDescIndForcipe;
	}
	public void setNaiDescIndForcipe(String naiDescIndForcipe) {
		this.naiDescIndForcipe = naiDescIndForcipe;
	}
	public List<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO> getProfissionaisEnvolvidos() {
		return profissionaisEnvolvidos;
	}
	public void setProfissionaisEnvolvidos(List<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO> profissionaisEnvolvidos) {
		this.profissionaisEnvolvidos = profissionaisEnvolvidos;
	}
	
	public Boolean getImprimirReanimacao() {
		return StringUtils.isNotEmpty(getAspiracaoTet())
				|| StringUtils.isNotEmpty(getInalatorio())
				|| StringUtils.isNotEmpty(getVentPorMascara())
				|| StringUtils.isNotEmpty(getMassaCardiacaExt())
				|| StringUtils.isNotEmpty(getAspiracao());
	}
	
	public Boolean getImprimirInformacoesComplementaresRN() {
		return StringUtils.isNotEmpty(getEvacuou())
				|| StringUtils.isNotEmpty(getSurfactante())
				|| StringUtils.isNotEmpty(getUrinou())
				|| StringUtils.isNotEmpty(getLavadoGastrico())
				|| StringUtils.isNotEmpty(getAmamentado());
	}
	public Boolean getImprimirAntecedentesFamiliares() {
		return StringUtils.isNotEmpty(getAntecedenteMae())
				|| StringUtils.isNotEmpty(getAntecedenteIrma())
				|| StringUtils.isNotEmpty(getDiabeteFamilia())
				|| StringUtils.isNotEmpty(getDoencasCongenitas())
				|| StringUtils.isNotEmpty(getHifObservacao());
	}
	
	public Boolean getImprimirCirurgias() {
		return StringUtils.isNotEmpty(getContaminacao())
				|| StringUtils.isNotEmpty(getIndLaqueaduraTubaria())
				|| StringUtils.isNotEmpty(getIndRafiaPeritonial())
				|| StringUtils.isNotEmpty(getIndLavagemCavidade())
				|| StringUtils.isNotEmpty(getIndDrenos());
	}
	
	public McoCesarianas getCesariana() {
		return cesariana;
	}
	public void setCesariana(McoCesarianas cesariana) {
		this.cesariana = cesariana;
	}
	public Integer getPacCodigoRN() {
		return pacCodigoRN;
	}
	public void setPacCodigoRN(Integer pacCodigoRN) {
		this.pacCodigoRN = pacCodigoRN;
	}
	public List<SumarioAtdRecemNascidoMedicamentosVO> getMedicamentosRecemNascido() {
		return medicamentosRecemNascido;
	}
	public void setMedicamentosRecemNascido(
			List<SumarioAtdRecemNascidoMedicamentosVO> medicamentosRecemNascido) {
		this.medicamentosRecemNascido = medicamentosRecemNascido;
	}
	public void setExibirIdadeGestacao(Boolean exibirIdadeGestacao) {
		this.exibirIdadeGestacao = exibirIdadeGestacao;
	}
	public Boolean getExibirIdadeGestacao() {
		return exibirIdadeGestacao;
	}
	public String getUsoMedicamento() {
		return usoMedicamento;
	}
	public void setUsoMedicamento(String usoMedicamento) {
		this.usoMedicamento = usoMedicamento;
	}
	public Date getDtHrMovimentoOriginal() {
		return dtHrMovimentoOriginal;
	}
	public void setDtHrMovimentoOriginal(Date dtHrMovimentoOriginal) {
		this.dtHrMovimentoOriginal = dtHrMovimentoOriginal;
	}
	
	public List<DescricaoIntercorrenciaVO> getIntercorrenciasPassadas() {
		return intercorrenciasPassadas;
	}
	public void setIntercorrenciasPassadas(
			List<DescricaoIntercorrenciaVO> intercorrenciasPassadas) {
		this.intercorrenciasPassadas = intercorrenciasPassadas;
	}
	

	public BigDecimal getMcoInterGesOpaSeqPassada() {
		return mcoInterGesOpaSeqPassada;
	}
	public void setMcoInterGesOpaSeqPassada(BigDecimal mcoInterGesOpaSeqPassada) {
		this.mcoInterGesOpaSeqPassada = mcoInterGesOpaSeqPassada;
	}
	public String getMcoInterGesOcorrenciaPassada() {
		return mcoInterGesOcorrenciaPassada;
	}
	public void setMcoInterGesOcorrenciaPassada(String mcoInterGesOcorrenciaPassada) {
		this.mcoInterGesOcorrenciaPassada = mcoInterGesOcorrenciaPassada;
	}
	public String getMcoInterGesComplementoPassada() {
		return mcoInterGesComplementoPassada;
	}
	public void setMcoInterGesComplementoPassada(
			String mcoInterGesComplementoPassada) {
		this.mcoInterGesComplementoPassada = mcoInterGesComplementoPassada;
	}
	
	public List<LinhaReportVO> getListaNotasAdicionais() {
		return listaNotasAdicionais;
	}
	public void setListaNotasAdicionais(List<LinhaReportVO> listaNotasAdicionais) {
		this.listaNotasAdicionais = listaNotasAdicionais;
	}

}
