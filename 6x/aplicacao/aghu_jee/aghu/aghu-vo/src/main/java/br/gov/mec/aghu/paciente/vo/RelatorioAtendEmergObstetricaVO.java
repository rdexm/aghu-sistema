package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author daniel.silva
 * @since 20/08/2012
 * VO utilizado na estoria #17321 - POL: emitir relatório de antendimentos na emergência obstétrica
 */

public class RelatorioAtendEmergObstetricaVO implements Serializable {

	private static final long serialVersionUID = -498717331130030311L;
	//Identificação
	private String nomePac; //NOME_PAC de QPAC
	private String idade; //IDADE de QPAC
	private String prontuario; //PRONTUARIO de QPAC
	private String altura; //ALTURA de QALTURA
	private String dthrAtendimento; //DTHR_ATENDIMENTO de QPAC
	private String peso; //PESO de QPESO
	private String convenio; //CONVENIO de QPAC
	private Integer pacCodigo; //PAC_CODIGO de QPAC
	private Integer conNumero; //BA de QPAC
	
	//Motivo da Consulta / Anamnese (HDA,HMP,RS)
	private String motivoConsulta; //MOTIVO_CONSULTA de QPAC
	
	//Gestação atual
	private Byte gesta; //GESTA de QGESTACAO
	private Byte para; //PARA de QGESTACAO
	private Byte cesarea; //CESAREA de QGESTACAO
	private Byte aborto; //ABORTO de QGESTACAO
	private Byte ectopica; //ECTOPICA de QGESTACAO
	private String gemelar; //GEMELAR de QGESTACAO
	private String dum; //DUM de QGESTACAO
	private String dtProvavelParto; //DT_PROVAVEL_PARTO de QGESTACAO
	private String dtPrimEco; //DT_PRIM_ECO de QGESTACAO
	private String igPrimEco; //IG_PRIM_ECO de QGESTACAO
	private String diasEco; //Retorno de CF_DIAS_ECOFormula.sql sendo: :DIAS_ECO de QGESTACAO
	private String dtInformadaIg; //DT_INFORMADA_IG de QGESTACAO
	private String idadeGestacional; //IDADE_GESTACIONAL de QGESTACAO
	private Byte numConsPrn; //NUM_CONS_PRN de QGESTACAO
	private String dtPrimConsPrn; //DT_PRIM_CONS_PRN de QGESTACAO
	private String tipoSangueMae; //TIPO_SANGUE_MAE de QGESTACAO
	private String coombs; //COOMBS de QGESTACAO
	private String vatCompleta; //VAT_COMPLETA de QGESTACAO
	private String usoMedicamentos; //USO_MEDICAMENTOS de QGESTACAO
	private List<RelatorioAtendEmergObstetricaIntercorrenciasVO> intercorrencias = new ArrayList<RelatorioAtendEmergObstetricaIntercorrenciasVO>();
	
	//Exame Físico
	private String pressaoArt; //PA de QPAC
	private String pressaoRepouso; //PAR de QPAC
	private String freqCardiaca; //FREQ_CARDIACA de QPAC
	private String batimentoCardiacoFetal; //BCF de QPAC
	private String temperatura; //TEMPERATURA de QPAC
	private String alturaUterina; //ALTURA_UTERINA de QPAC
	private String dinamUterEIntens; //DINAM_UTER_E_INTENS de QPAC
	private String freqRespiratoria; //FREQ_RESPIRATORIA de QPAC
	private String batimentoCardiacoFetalDois; //BCF2 de QPAC
	private String edema; //EDEMA de QPAC
	private String sitFeral; //SIT_FETAL de QPAC
	private String exameEspecular; //EXAME_ESPECULAR de QPAC
	private String acelTrans; //ACEL_TRANS de QPAC
	private String movFetal; //MOV_FETAL de QPAC
	private String espessuraCervice; //ESPESSURA_CERVICE de QPAC
	private String posicaoCervice; //POSICAO_CERVICE de QPAC
	private String apagamento; //APAGAMENTO de QPAC
	private String dilatacao; //DILATACAO de QPAC
	private String apresentacao; //APRESENTACAO de QPAC
	private Byte planoDelee; //PLANO_DELEE de QPAC
	private String formaRuptura; //FORMA_RUPTURA de QPAC
	private String dthrRompimento; //DTHR_ROMPIMENTO de QPAC
	private String liquidoAmniotico; //LIQUIDO_AMNIOTICO de QPAC
	private String odor; //ODOR de QPAC
	private String indAmnioscopia; //IND_AMNIOSCOPIA de QPAC
	private String acv; //ACV de QPAC
	private String ar; //AR de QPAC
	private String exFisicoGeral; //EX_FISICO_GERAL de QPAC
	
	//Conduta
	private List<RelatorioAtendEmergObstetricaCondutaVO> condutas = new ArrayList<RelatorioAtendEmergObstetricaCondutaVO>();
	
	//Diagnóstico
	private String cidDescricao; //CID_DESCRICAO de QCID
	private String cidCodigo; //CID_CODIGO de QCID
	
	//Observação / Diagnósticos Secundários
	private String gravidez; //GRAVIDEZ de QGESTACAO
	private String observacao; //OBSERVAÇÃO de QPAC
	private String dthrMovimento;
	private String responsavel; //:CP_RESPONSAVEL resultante de CSEP_BUSCA_CONS_PROF(P_MATRICULA,:P_VINCULO);
	
	//Notas Adicionais
	private String nadNotaAdicional; //NAD_NOTA_ADICIONAL de Q1
	private String nadCriadoEm; //NAD_CRIADO_EM de Q1 
	private String nadNomeProf; //NAD_NOME_PROF de Q1
	
	//Parametros report
	private String logo; //Logo padrão de reports
	private String leito; //Retorno de CF_LEITOFormula.sql
	
	//Parametros de pesquisa
	private Short gsoSeqp;
	
	//Parametros auxiliares - utilizados durante o processamento dos dados
	private Integer efiCidSeq; //EFI_CID_SEQ de QPAC

	public Boolean getExibirTituloToque() {
		//Não exibir titulo “Toque” se campos de 46 a 51 forem NULL
		return StringUtils.isNotEmpty(getEspessuraCervice())
				|| StringUtils.isNotEmpty(getPosicaoCervice())
				|| StringUtils.isNotEmpty(getApagamento())
				|| StringUtils.isNotEmpty(getDilatacao())
				|| StringUtils.isNotEmpty(getApresentacao())
				|| getPlanoDelee() != null;
	}

	public String getExibirDataRompimentoIgnorada() {
		//Se FORMA_RUPTURA de QPAC = “Amniorrexis” e DTHR_ROMPIMENTO de QPAC for null, exibir texto “Data de rompimento: IGNORADA”
		String retorno = null;
		if (StringUtils.equalsIgnoreCase(getFormaRuptura(), "Amniorrexis") && getDthrRompimento() == null) {
			retorno = "Data de rompimento: IGNORADA";
		}
		return retorno;
	}
	
	public String getDataAtual() {
		return DateUtil.dataToString(new Date(), "dd/MM/yyyy HH:mm:ss");
	}
	
	public Boolean getExibirTituloIntercorrenciasAtuais() {
		//TODO - Não exibir titulo “Intercorrencias Atuais” se QOPA for NULL.
		return null;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getAltura() {
		return altura;
	}

	public void setAltura(String altura) {
		this.altura = altura;
	}

	public String getDthrAtendimento() {
		return dthrAtendimento;
	}

	public void setDthrAtendimento(String dthrAtendimento) {
		this.dthrAtendimento = dthrAtendimento;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
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

	public String getDum() {
		return dum;
	}

	public void setDum(String dum) {
		this.dum = dum;
	}

	public String getDtProvavelParto() {
		return dtProvavelParto;
	}

	public void setDtProvavelParto(String dtProvavelParto) {
		this.dtProvavelParto = dtProvavelParto;
	}

	public String getDtPrimEco() {
		return dtPrimEco;
	}

	public void setDtPrimEco(String dtPrimEco) {
		this.dtPrimEco = dtPrimEco;
	}

	public String getIgPrimEco() {
		return igPrimEco;
	}

	public void setIgPrimEco(String igPrimEco) {
		this.igPrimEco = igPrimEco;
	}

	public String getDiasEco() {
		return diasEco;
	}

	public void setDiasEco(String diasEco) {
		this.diasEco = diasEco;
	}

	public String getDtInformadaIg() {
		return dtInformadaIg;
	}

	public void setDtInformadaIg(String dtInformadaIg) {
		this.dtInformadaIg = dtInformadaIg;
	}

	public String getIdadeGestacional() {
		return idadeGestacional;
	}

	public void setIdadeGestacional(String idadeGestacional) {
		this.idadeGestacional = idadeGestacional;
	}

	public Byte getNumConsPrn() {
		return numConsPrn;
	}

	public void setNumConsPrn(Byte numConsPrn) {
		this.numConsPrn = numConsPrn;
	}

	public String getDtPrimConsPrn() {
		return dtPrimConsPrn;
	}

	public void setDtPrimConsPrn(String dtPrimConsPrn) {
		this.dtPrimConsPrn = dtPrimConsPrn;
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

	public List<RelatorioAtendEmergObstetricaIntercorrenciasVO> getIntercorrencias() {
		return intercorrencias;
	}
	
	public void setIntercorrencias(List<RelatorioAtendEmergObstetricaIntercorrenciasVO> intercorrencias) {
		this.intercorrencias = intercorrencias;
	}

	public String getUsoMedicamentos() {
		return usoMedicamentos;
	}

	public void setUsoMedicamentos(String usoMedicamentos) {
		this.usoMedicamentos = usoMedicamentos;
	}

	public String getPressaoArt() {
		return pressaoArt;
	}

	public void setPressaoArt(String pressaoArt) {
		this.pressaoArt = pressaoArt;
	}

	public String getPressaoRepouso() {
		return pressaoRepouso;
	}

	public void setPressaoRepouso(String pressaoRepouso) {
		this.pressaoRepouso = pressaoRepouso;
	}

	public String getFreqCardiaca() {
		return freqCardiaca;
	}

	public void setFreqCardiaca(String freqCardiaca) {
		this.freqCardiaca = freqCardiaca;
	}

	public String getBatimentoCardiacoFetal() {
		return batimentoCardiacoFetal;
	}

	public void setBatimentoCardiacoFetal(String batimentoCardiacoFetal) {
		this.batimentoCardiacoFetal = batimentoCardiacoFetal;
	}

	public String getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(String temperatura) {
		this.temperatura = temperatura;
	}

	public String getAlturaUterina() {
		return alturaUterina;
	}

	public void setAlturaUterina(String alturaUterina) {
		this.alturaUterina = alturaUterina;
	}

	public String getDinamUterEIntens() {
		return dinamUterEIntens;
	}

	public void setDinamUterEIntens(String dinamUterEIntens) {
		this.dinamUterEIntens = dinamUterEIntens;
	}

	public String getFreqRespiratoria() {
		return freqRespiratoria;
	}

	public void setFreqRespiratoria(String freqRespiratoria) {
		this.freqRespiratoria = freqRespiratoria;
	}

	public String getBatimentoCardiacoFetalDois() {
		return batimentoCardiacoFetalDois;
	}

	public void setBatimentoCardiacoFetalDois(String batimentoCardiacoFetalDois) {
		this.batimentoCardiacoFetalDois = batimentoCardiacoFetalDois;
	}

	public String getEdema() {
		return edema;
	}

	public void setEdema(String edema) {
		this.edema = edema;
	}

	public String getSitFeral() {
		return sitFeral;
	}

	public void setSitFeral(String sitFeral) {
		this.sitFeral = sitFeral;
	}

	public String getExameEspecular() {
		return exameEspecular;
	}

	public void setExameEspecular(String exameEspecular) {
		this.exameEspecular = exameEspecular;
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

	public String getEspessuraCervice() {
		return espessuraCervice;
	}

	public void setEspessuraCervice(String espessuraCervice) {
		this.espessuraCervice = espessuraCervice;
	}

	public String getPosicaoCervice() {
		return posicaoCervice;
	}

	public void setPosicaoCervice(String posicaoCervice) {
		this.posicaoCervice = posicaoCervice;
	}

	public String getApagamento() {
		return apagamento;
	}

	public void setApagamento(String apagamento) {
		this.apagamento = apagamento;
	}

	public String getDilatacao() {
		return dilatacao;
	}

	public void setDilatacao(String dilatacao) {
		this.dilatacao = dilatacao;
	}

	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public Byte getPlanoDelee() {
		return planoDelee;
	}

	public void setPlanoDelee(Byte planoDelee) {
		this.planoDelee = planoDelee;
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

	public String getLiquidoAmniotico() {
		return liquidoAmniotico;
	}

	public void setLiquidoAmniotico(String liquidoAmniotico) {
		this.liquidoAmniotico = liquidoAmniotico;
	}

	public String getOdor() {
		return odor;
	}

	public void setOdor(String odor) {
		this.odor = odor;
	}

	public String getIndAmnioscopia() {
		return indAmnioscopia;
	}

	public void setIndAmnioscopia(String indAmnioscopia) {
		this.indAmnioscopia = indAmnioscopia;
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

	public String getExFisicoGeral() {
		return exFisicoGeral;
	}

	public void setExFisicoGeral(String exFisicoGeral) {
		this.exFisicoGeral = exFisicoGeral;
	}

	public List<RelatorioAtendEmergObstetricaCondutaVO> getCondutas() {
		return condutas;
	}

	public void setCondutas(List<RelatorioAtendEmergObstetricaCondutaVO> condutas) {
		this.condutas = condutas;
	}

	public String getGravidez() {
		return gravidez;
	}

	public void setGravidez(String gravidez) {
		this.gravidez = gravidez;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getDthrMovimento() {
		return dthrMovimento;
	}

	public void setDthrMovimento(String dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getNadNotaAdicional() {
		return nadNotaAdicional;
	}

	public void setNadNotaAdicional(String nadNotaAdicional) {
		this.nadNotaAdicional = nadNotaAdicional;
	}

	public String getNadCriadoEm() {
		return nadCriadoEm;
	}

	public void setNadCriadoEm(String nadCriadoEm) {
		this.nadCriadoEm = nadCriadoEm;
	}

	public String getNadNomeProf() {
		return nadNomeProf;
	}

	public void setNadNomeProf(String nadNomeProf) {
		this.nadNomeProf = nadNomeProf;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getEfiCidSeq() {
		return efiCidSeq;
	}

	public void setEfiCidSeq(Integer efiCidSeq) {
		this.efiCidSeq = efiCidSeq;
	}

	public String getCidDescricao() {
		return cidDescricao;
	}

	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}

	public String getCidCodigo() {
		return cidCodigo;
	}

	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}
}
