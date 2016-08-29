package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.StringUtil;



public class RelatorioLaudoUnicoVO {

	private Long lumSeq;
	private Long luxSeq;

	private Long lumNumeroAp;
	private String numeApFormatado;
	
	//buscar
	private Long lumNumeroApOrigem;
	private String numeApOrigemFormatado;

	private String exame;
	private Date dtExtrato;
	private Date dtAssinatura;
	private Integer atvSeq;
	private Integer atdSeq;

	private String nomePac;

	private Integer prontuario;
	
	private String prontuarioFormatado;
	
	private String convenio;
	
	private String projeto;

	private String luxMateriais;
	private String luxEtapasLaudo;
	private String luxSituacao;
	private Integer lu2Seq;
	private Integer luxSerMatriculaRespLaudo;
	private Short luxSerVinCodigoRespLaudo;
	

	private String pLinhaCab1;
	private String pLinhaCab2;
	private String pLinhaCab3;
	private String pLinhaCab4;
	private String pLinhaCab5;
	private String pLinhaCab6;
	private String pLinhaCab7;

	private String macroscopia;
	private String diagnostico;
	

	private String vCrm1;
	private String vFuncao1;
	private String vPatologista1;
	private String colResp1;
	
	private String vCrm2;
	private String vFuncao2;
	private String vPatologista2;
	private String colResp2;
	
	private String vCrm3;
	private String vFuncao3;
	private String vPatologista3;
	private String colResp3;
	
	private String medicoSolicitante;
	private String leito;
	private String unidadeFuncional;
	
	private String informacoesClinicas;
	
	private String notasAdicionais;
	
	public RelatorioLaudoUnicoVO() {}
	
	public RelatorioLaudoUnicoVO(final VAelApXPatologiaAghu vAghu) {
		super();
		this.lumSeq = vAghu.getId().getLumSeq();
		this.luxSeq = vAghu.getId().getLuxSeq();
		setLumNumeroAp(vAghu.getId().getLumNumeroAp());
		this.exame = vAghu.getId().getLu2Nome();
		this.atvSeq = vAghu.getId().getAtvSeq();
		this.atdSeq = vAghu.getId().getAtdSeq();
		this.nomePac = 
			(vAghu.getId().getNomePac() != null && vAghu.getId().getNomePac().length() > 40) ?
					vAghu.getId().getNomePac().substring(0,40) : 
					vAghu.getId().getNomePac();
					
		this.convenio = (vAghu.getConvenio() != null && vAghu.getConvenio().length() > 55) ? vAghu.getConvenio().substring(0,55) : vAghu.getConvenio(); 
		this.luxEtapasLaudo = vAghu.getId().getLuxEtapasLaudo().toString();
		this.luxSituacao = vAghu.getId().getLuxSituacao();
		this.lu2Seq = vAghu.getId().getLu2Seq();
		this.luxSerMatriculaRespLaudo = vAghu.getId().getLuxSerMatriculaRespLaudo();
		this.luxSerVinCodigoRespLaudo = vAghu.getId().getLuxSerVinCodigoRespLaudo();
	}
	
	public void createCabecalho(final AelpCabecalhoLaudoVO cabecalho){
		this.pLinhaCab1 = cabecalho.getpLinhaCab1();
		this.pLinhaCab2 = cabecalho.getpLinhaCab2();
		this.pLinhaCab3 = cabecalho.getpLinhaCab3();
		this.pLinhaCab4 = cabecalho.getpLinhaCab4();
		this.pLinhaCab5 = cabecalho.getpLinhaCab5();
		this.pLinhaCab6 = cabecalho.getpLinhaCab6();
		this.pLinhaCab7 = cabecalho.getpLinhaCab7();
	}

	public Long getLumSeq() {
		return lumSeq;
	}

	public void setLumSeq(Long lumSeq) {
		this.lumSeq = lumSeq;
	}

	public Long getLuxSeq() {
		return luxSeq;
	}

	public void setLuxSeq(Long luxSeq) {
		this.luxSeq = luxSeq;
	}

	public Long getLumNumeroAp() {
		return lumNumeroAp;
	}

	public void setLumNumeroAp(Long lumNumeroAp) {
		if(lumNumeroAp != null){
			setNumeApFormatado(StringUtil.formataNumeroAp(lumNumeroAp));
		}
		this.lumNumeroAp = lumNumeroAp;
	}

	public Long getLumNumeroApOrigem() {
		return lumNumeroApOrigem;
	}

	public void setLumNumeroApOrigem(Long lumNumeroApOrigem) {
		if(lumNumeroApOrigem != null){
			setNumeApOrigemFormatado(StringUtil.formataNumeroAp(lumNumeroApOrigem));
		}
		this.lumNumeroApOrigem = lumNumeroApOrigem;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public Integer getAtvSeq() {
		return atvSeq;
	}

	public void setAtvSeq(Integer atvSeq) {
		this.atvSeq = atvSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		if(prontuario != null){
			setProntuarioFormatado(CoreUtil.formataProntuario(prontuario));
		}
		this.prontuario = prontuario;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getProjeto() {
		return projeto;
	}

	public void setProjeto(String projeto) {
		this.projeto = projeto;
	}

	public String getLuxMateriais() {
		return luxMateriais;
	}

	public void setLuxMateriais(String luxMateriais) {
		this.luxMateriais = luxMateriais;
	}

	public String getLuxEtapasLaudo() {
		return luxEtapasLaudo;
	}

	public void setLuxEtapasLaudo(String luxEtapasLaudo) {
		this.luxEtapasLaudo = luxEtapasLaudo;
	}

	public String getLuxSituacao() {
		return luxSituacao;
	}

	public void setLuxSituacao(String luxSituacao) {
		this.luxSituacao = luxSituacao;
	}

	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

	public Integer getLuxSerMatriculaRespLaudo() {
		return luxSerMatriculaRespLaudo;
	}

	public void setLuxSerMatriculaRespLaudo(Integer luxSerMatriculaRespLaudo) {
		this.luxSerMatriculaRespLaudo = luxSerMatriculaRespLaudo;
	}

	public Short getLuxSerVinCodigoRespLaudo() {
		return luxSerVinCodigoRespLaudo;
	}

	public void setLuxSerVinCodigoRespLaudo(Short luxSerVinCodigoRespLaudo) {
		this.luxSerVinCodigoRespLaudo = luxSerVinCodigoRespLaudo;
	}

	public String getpLinhaCab1() {
		return pLinhaCab1;
	}

	public String getpLinhaCab2() {
		return pLinhaCab2;
	}

	public String getpLinhaCab3() {
		return pLinhaCab3;
	}

	public String getpLinhaCab4() {
		return pLinhaCab4;
	}

	public String getpLinhaCab5() {
		return pLinhaCab5;
	}

	public String getpLinhaCab6() {
		return pLinhaCab6;
	}

	public String getpLinhaCab7() {
		return pLinhaCab7;
	}

	public void setpLinhaCab1(String pLinhaCab1) {
		this.pLinhaCab1 = pLinhaCab1;
	}

	public void setpLinhaCab2(String pLinhaCab2) {
		this.pLinhaCab2 = pLinhaCab2;
	}

	public void setpLinhaCab3(String pLinhaCab3) {
		this.pLinhaCab3 = pLinhaCab3;
	}

	public void setpLinhaCab4(String pLinhaCab4) {
		this.pLinhaCab4 = pLinhaCab4;
	}

	public void setpLinhaCab5(String pLinhaCab5) {
		this.pLinhaCab5 = pLinhaCab5;
	}

	public void setpLinhaCab6(String pLinhaCab6) {
		this.pLinhaCab6 = pLinhaCab6;
	}

	public void setpLinhaCab7(String pLinhaCab7) {
		this.pLinhaCab7 = pLinhaCab7;
	}

	public String getNumeApFormatado() {
		return numeApFormatado;
	}

	public void setNumeApFormatado(String numeApFormatado) {
		this.numeApFormatado = numeApFormatado;
	}

	public String getNumeApOrigemFormatado() {
		return numeApOrigemFormatado;
	}

	public void setNumeApOrigemFormatado(String numeApOrigemFormatado) {
		this.numeApOrigemFormatado = numeApOrigemFormatado;
	}

	public Date getDtExtrato() {
		return dtExtrato;
	}

	public void setDtExtrato(Date dtExtrato) {
		this.dtExtrato = dtExtrato;
	}

	public String getMacroscopia() {
		return macroscopia;
	}

	public void setMacroscopia(String macroscopia) {
		this.macroscopia = macroscopia;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getvCrm1() {
		return vCrm1;
	}

	public String getvFuncao1() {
		return vFuncao1;
	}

	public String getvPatologista1() {
		return vPatologista1;
	}

	public String getvCrm2() {
		return vCrm2;
	}

	public String getvFuncao2() {
		return vFuncao2;
	}

	public String getvPatologista2() {
		return vPatologista2;
	}

	public String getvCrm3() {
		return vCrm3;
	}

	public String getvFuncao3() {
		return vFuncao3;
	}

	public String getvPatologista3() {
		return vPatologista3;
	}

	public void setvCrm1(String vCrm1) {
		this.vCrm1 = vCrm1;
	}

	public void setvFuncao1(String vFuncao1) {
		this.vFuncao1 = vFuncao1;
	}

	public void setvPatologista1(String vPatologista1) {
		this.vPatologista1 = vPatologista1;
	}

	public void setvCrm2(String vCrm2) {
		this.vCrm2 = vCrm2;
	}

	public void setvFuncao2(String vFuncao2) {
		this.vFuncao2 = vFuncao2;
	}

	public void setvPatologista2(String vPatologista2) {
		this.vPatologista2 = vPatologista2;
	}

	public void setvCrm3(String vCrm3) {
		this.vCrm3 = vCrm3;
	}

	public void setvFuncao3(String vFuncao3) {
		this.vFuncao3 = vFuncao3;
	}

	public void setvPatologista3(String vPatologista3) {
		this.vPatologista3 = vPatologista3;
	}

	public String getColResp1() {
		return colResp1;
	}

	public String getColResp2() {
		return colResp2;
	}

	public String getColResp3() {
		return colResp3;
	}

	public void setColResp1(String colResp1) {
		this.colResp1 = colResp1;
	}

	public void setColResp2(String colResp2) {
		this.colResp2 = colResp2;
	}

	public void setColResp3(String colResp3) {
		this.colResp3 = colResp3;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public String getMedicoSolicitante() {
		return medicoSolicitante;
	}

	public void setMedicoSolicitante(String medicoSolicitante) {
		this.medicoSolicitante = medicoSolicitante;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}

	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}

	public String getNotasAdicionais() {
		return notasAdicionais;
	}

	public void setNotasAdicionais(String notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}

	public Date getDtAssinatura() {
		return dtAssinatura;
	}

	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}
}


