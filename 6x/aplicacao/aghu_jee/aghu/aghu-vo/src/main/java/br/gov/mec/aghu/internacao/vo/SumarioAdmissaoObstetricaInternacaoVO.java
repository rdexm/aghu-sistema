package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.paciente.vo.DescricaoIntercorrenciaVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO;
import br.gov.mec.aghu.core.commons.NumberUtil;


public class SumarioAdmissaoObstetricaInternacaoVO implements Serializable{

	private static final long serialVersionUID = -5453167297689161953L;

	//CABEÇALHO / RODAPE
	private String leito;
	
	//PACIENTE
	private String nome; 
	private String prontuario;
	private String idade;
	private String altura;
	private Date dataAtendimento;
	private String pesoMae;
	private String convenio;
	private String pacCodigo;
	private String acv;
	private String ar;
	
	
	//MOTIVO DA CONSULTA / ANAMNESE
	private String motivoConsulta;
	
	//GESTACAO ATUAL
	private Byte gesta;
	private Byte para;
	private Byte cesarea;
	private Byte aborto;
	private Byte ectopica;
	private String gemelar;
	private Date dum;
	private Date dtProvavelParto;
	private Date dtPrimeiraEco;
	private String idadeGestPrimeiraEco; 
	private String idadeGestPrimeiraEcoDias;
	private Date dtInformadaIG;
	private String idadeGestacionalInformada; 
	private Byte nroConsultasPreNatal;
	private Date dtPrimeiraConsulta;
	private String tipoSangueMae;
	private String coombs;
	private String vatCompleta;
	private List<IntercorrenciaAtualVO> intercorrenciasAtuais = new ArrayList<IntercorrenciaAtualVO>();	
	private List<DescricaoIntercorrenciaVO> intercorrenciasPassadas = new ArrayList<DescricaoIntercorrenciaVO>();	
	private String descricaoInterPassada;
	private String complementoInterPassada;
	private String usoMedicamentos;
	private String justificativa;
	
	
	

	//GESTACOES ANTERIORES
	private List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores;
	
	//ANTECEDENTES FAMILIARES
	private String antecedenteMae;
	private String antecedenteIrma;
	private String diabeteFamilia;
	private String doencasCongenitas;
	private String hifObservacao;
	
	//EXAME FISICO
	private String pa;
	private String paRepouso;
	private String freqCardiaca;
	private String bcf;
	private String bcf2;
	private String temp;
	private String altUterina;
	private String dinamUterina;
	private String freqResp;
	private String edema;
	private String sitFetal;
	private String exameEspecular;
	private String cervice;
	private String apag;
	private String dilatacao;
	private String apres;
	private Byte plDeLee;
	private String liqAminiotico;
	private String odor;
	private String amnioscopia;
	private String acvAr;
	private String exFisGeral;
	private String acelTrans;
	private String movFetal;
	private String formaRuptura;
	private String dthrRompimento;
	

	//EXAMES REALIZADOS
	private List<SumarioAdmissaoObstetricaExamesRealizadosVO> examesRealizados;
	private String observacaoExame;
		
	//CONDUTA
	private List<SumarioAdmissaoObstetricaExamesCondutaVO> condutas = new ArrayList<SumarioAdmissaoObstetricaExamesCondutaVO>();
	
	//DIAGNOSTICO PRINCIPAL DA INTERNACAO
	private String motivoInternacao;
	private String cidInternacao;
	
	//OBSERVACAO/DIAGNOSTICOS SECUNDARIOS
	private String gravidez;
	private String observacaoDiagnostico;
	private String dataHoraObservacao;
	private String nomeProfissional; 
	
	//NOTAS ADICIONAIS
	private String notaAdicional;
	private String dataNota;
	private String nomeRespNota;
		
	//MAP DE PARAMETROS
	@SuppressWarnings("rawtypes")
	public Map parametrosHQL = new HashMap();

	public enum ParametrosReportEnum {
	    
		P_MATRICULA,
		P_VIN_CODIGO,
		P_PAC_CODIGO,
		P_CON_NUMERO,
		P_GSO_SEQP,
		P_DTHR_MOVIMENTO,
		QPAC_PAC_CODIGO,  
		QING_ING_OPA_SEQ,  
		QPASSADAS_ING_OPA_SEQ,  
		QGESTACAO_GSO_PAC_CODIGO,  
		QGESTACAO_GSO_SEQP,
		QGESTACAO_GESTA,
		QGESTACAO_CRIADO_EM,
		QGPA_GPA_INA_SEQ,
		QPAC_BA,
		QPAC_EFI_CID_SEQ,
		QPAC_ACV,
		QPAC_AR,
		QPAC_DT_ULT_ALTA,
		QPAC_DT_ULT_INTERNACAO,
		QPAC_LEITO;
		
	}
	
	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

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

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getAltura() {
		return altura;
	}

	public void setAltura(String altura) {
		this.altura = altura;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public String getPesoMae() {
		return pesoMae;
	}

	public void setPesoMae(String pesoMae) {
		this.pesoMae = pesoMae;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	
	public String getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(String pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getMotivoConsulta() {
		return motivoConsulta;
	}

	public void setMotivoConsulta(String motivoConsulta) {
		this.motivoConsulta = motivoConsulta;
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

	public Date getDtProvavelParto() {
		return dtProvavelParto;
	}

	public void setDtProvavelParto(Date dtProvavelParto) {
		this.dtProvavelParto = dtProvavelParto;
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

	public Date getDtInformadaIG() {
		return dtInformadaIG;
	}

	public void setDtInformadaIG(Date dtInformadaIG) {
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

	public String getCoombs() {
		return coombs;
	}

	public void setCoombs(String coombs) {
		this.coombs = coombs;
	}

	public String getVatCompleta() {
		return vatCompleta;
	}

	public void setVatCompleta(String vatCompleta) {
		this.vatCompleta = vatCompleta;
	}

	

	public String getDescricaoInterPassada() {
		return descricaoInterPassada;
	}

	public void setDescricaoInterPassada(String descricaoInterPassada) {
		this.descricaoInterPassada = descricaoInterPassada;
	}

	public String getComplementoInterPassada() {
		return complementoInterPassada;
	}

	public void setComplementoInterPassada(String complementoInterPassada) {
		this.complementoInterPassada = complementoInterPassada;
	}

	public String getUsoMedicamentos() {
		return usoMedicamentos;
	}

	public void setUsoMedicamentos(String usoMedicamentos) {
		this.usoMedicamentos = usoMedicamentos;
	}

	
	public List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> getGestacoesAnteriores() {
		
		if(this.gestacoesAnteriores == null) {
			this.gestacoesAnteriores  = new ArrayList<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO>();
		}
		
		return this.gestacoesAnteriores ;
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

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPaRepouso() {
		return paRepouso;
	}

	public void setPaRepouso(String paRepouso) {
		this.paRepouso = paRepouso;
	}

	public String getFreqCardiaca() {
		return freqCardiaca;
	}

	public void setFreqCardiaca(String freqCardiaca) {
		if(freqCardiaca != null) {
			this.freqCardiaca = freqCardiaca + " bpm";
		} else {
			this.freqCardiaca = freqCardiaca;
		}
	}

	public String getBcf() {
		return bcf;
	}

	public void setBcf(String bcf) {
		if(bcf != null) {
			this.bcf = bcf + " bpm" ;
		}else {
			this.bcf = bcf;
		}
	}

	public String getBcf2() {
		return bcf2;
	}

	public void setBcf2(String bcf2) {
		this.bcf2 = bcf2;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		if(temp != null) {
			this.temp = NumberUtil.truncate(new Double(temp), 1) + " ºC";
			this.temp = this.temp.replace(".", ",");
		} else {
			this.temp = this.temp.replace(".", ",");
		}
	}

	public String getAltUterina() {
		return altUterina;
	}

	public void setAltUterina(String altUterina) {
		if(altUterina != null) {
			this.altUterina = altUterina + " cm";
		}else {
			this.altUterina = altUterina;
		}
	}

	public String getDinamUterina() {
		return dinamUterina;
	}

	public void setDinamUterina(String dinamUterina) {
		this.dinamUterina = dinamUterina;
	}

	public String getFreqResp() {
		return freqResp;
	}

	public void setFreqResp(String freqResp) {
		this.freqResp = freqResp;
	}

	public String getEdema() {
		return edema;
	}

	public void setEdema(String edema) {
		this.edema = edema;
	}

	public String getSitFetal() {
		return sitFetal;
	}

	public void setSitFetal(String sitFetal) {
		this.sitFetal = sitFetal;
	}

	public String getExameEspecular() {
		return exameEspecular;
	}

	public void setExameEspecular(String exameEspecular) {
		this.exameEspecular = exameEspecular;
	}

	public String getCervice() {
		return cervice;
	}

	public void setCervice(String cervice) {
		this.cervice = cervice;
	}

	public String getApag() {
		return apag;
	}

	public void setApag(String apag) {
		if(StringUtils.isNotBlank(apag)) {
			this.apag = apag + "%";
		} else {
			this.apag = apag;
		}
	}

	public String getDilatacao() {
		return dilatacao;
	}

	public void setDilatacao(String dilatacao) {
		if(StringUtils.isNotBlank(dilatacao)) {
			this.dilatacao = dilatacao + " cm";	
		} else {
			this.dilatacao = dilatacao;
		}
		
	}

	public String getApres() {
		return apres;
	}

	public void setApres(String apres) {
		this.apres = apres;
	}

	public Byte getPlDeLee() {
		return plDeLee;
	}

	public void setPlDeLee(Byte plDeLee) {
		this.plDeLee = plDeLee;
	}

	public String getLiqAminiotico() {
		return liqAminiotico;
	}

	public void setLiqAminiotico(String liqAminiotico) {
		this.liqAminiotico = liqAminiotico;
	}

	public String getOdor() {
		return odor;
	}

	public void setOdor(String odor) {
		this.odor = odor;
	}

	public String getAmnioscopia() {
		return amnioscopia;
	}

	public void setAmnioscopia(String amnioscopia) {
		this.amnioscopia = amnioscopia;
	}

	public String getAcvAr() {
		return acvAr;
	}

	public void setAcvAr(String acvAr) {
		this.acvAr = acvAr;
	}

	public String getExFisGeral() {
		return exFisGeral;
	}

	public void setExFisGeral(String exFisGeral) {
		this.exFisGeral = exFisGeral;
	}

	public String getAcelTrans() {
		return acelTrans;
	}

	public void setAcelTrans(String acelTrans) {
		this.acelTrans = acelTrans;
	}

	public String getMovFetal() {
		return movFetal;
	}

	public void setMovFetal(String movFetal) {
		this.movFetal = movFetal;
	}
	
	public String getFormaRuptura() {
		return formaRuptura;
	}

	public void setFormaRuptura(String formaRuptura) {
		this.formaRuptura = formaRuptura;
	}

	public String getDthrRompimento() {
		return dthrRompimento;
	}

	public void setDthrRompimento(String dthrRompimento) {
		this.dthrRompimento = dthrRompimento;
	}
	
	public List<SumarioAdmissaoObstetricaExamesRealizadosVO> getExamesRealizados() {
		
		if(examesRealizados  == null) {
			this.examesRealizados = new ArrayList<SumarioAdmissaoObstetricaExamesRealizadosVO>();
		}
		
		return examesRealizados;
	}

	public void setExamesRealizados(
			List<SumarioAdmissaoObstetricaExamesRealizadosVO> examesRealizados) {
		
		this.examesRealizados = examesRealizados;
	}

	public String getMotivoInternacao() {
		return motivoInternacao;
	}

	public void setMotivoInternacao(String motivoInternacao) {
		this.motivoInternacao = motivoInternacao;
	}

	public String getCidInternacao() {
		return cidInternacao;
	}

	public void setCidInternacao(String cidInternacao) {
		this.cidInternacao = cidInternacao;
	}

	public String getGravidez() {
		return gravidez;
	}

	public void setGravidez(String gravidez) {
		this.gravidez = gravidez;
	}

	public String getObservacaoDiagnostico() {
		return observacaoDiagnostico;
	}

	public void setObservacaoDiagnostico(String observacaoDiagnostico) {
		this.observacaoDiagnostico = observacaoDiagnostico;
	}

	public String getDataHoraObservacao() {
		return dataHoraObservacao;
	}

	public void setDataHoraObservacao(String dataHoraObservacao) {
		this.dataHoraObservacao = dataHoraObservacao;
	}

	public String getNomeProfissional() {
		return nomeProfissional;
	}

	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}

	public String getNotaAdicional() {
		return notaAdicional;
	}

	public void setNotaAdicional(String notaAdicional) {
		this.notaAdicional = notaAdicional;
	}

	public String getDataNota() {
		return dataNota;
	}

	public void setDataNota(String dataNota) {
		this.dataNota = dataNota;
	}

	public String getNomeRespNota() {
		return nomeRespNota;
	}

	public void setNomeRespNota(String nomeRespNota) {
		this.nomeRespNota = nomeRespNota;
	}

	public String getIdadeGestPrimeiraEcoDias() {
		return idadeGestPrimeiraEcoDias;
	}

	public void setIdadeGestPrimeiraEcoDias(String idadeGestPrimeiraEcoDias) {
		this.idadeGestPrimeiraEcoDias = idadeGestPrimeiraEcoDias;
	}
	
	public List<SumarioAdmissaoObstetricaExamesCondutaVO> getCondutas() {
		return condutas;
	}

	public void setCondutas(List<SumarioAdmissaoObstetricaExamesCondutaVO> condutas) {
		this.condutas = condutas;
	}

	@SuppressWarnings("rawtypes")
	public Map getParametrosHQL() {
		return parametrosHQL;
	}

	@SuppressWarnings("rawtypes")
	public void setParametrosHQL(Map parametrosHQL) {
		this.parametrosHQL = parametrosHQL;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getAcv() {
		return acv;
	}

	public void setAcv(String acv) {
		this.acv = acv;
	}

	public String getAr() {
		return ar;
	}

	public void setAr(String ar) {
		this.ar = ar;
	}
	
	public String getObservacaoExame() {
		return observacaoExame;
	}

	public void setObservacaoExame(String observacaoExame) {
		this.observacaoExame = observacaoExame;
	}

	public List<IntercorrenciaAtualVO> getIntercorrenciasAtuais() {
		return intercorrenciasAtuais;
	}

	public void setIntercorrenciasAtuais(
			List<IntercorrenciaAtualVO> intecorrenciasAtuais) {
		this.intercorrenciasAtuais = intecorrenciasAtuais;
	}

	public List<DescricaoIntercorrenciaVO> getIntercorrenciasPassadas() {
		return intercorrenciasPassadas;
	}

	public void setIntercorrenciasPassadas(List<DescricaoIntercorrenciaVO> intercorrenciasPassadas) {
		this.intercorrenciasPassadas = intercorrenciasPassadas;
	}
	
}
