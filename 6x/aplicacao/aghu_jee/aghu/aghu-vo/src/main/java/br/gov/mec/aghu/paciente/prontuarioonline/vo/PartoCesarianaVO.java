package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.BaseBean;


public class PartoCesarianaVO implements BaseBean{
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -8202576437255477043L;
	private String fDtNascimento; // 27   
	private String fTipo; // 28			
	private String fModo; // 29
	private String fEpisiotomia; //30	 
	private String fPeriodoDilatacao; //31
	private String fPeriodoExpulsivo; //32
	private String fDuracao; //33
	private List<LinhaReportVO> listaForcipe; // 34, 35, 36 		
	private List<LinhaReportVO> listaIndUsoCesareana; // 37
	private String fHoraDuracao; // 38
	private String fLaparotomia; // 39	 
	private String fHisterotomia; // 40
	private String fHisterorrafia; // 41
	private String fContaminacao; // 42
	private String fLaqueadura; // 43
	private String fRafia; // 44
	private String fLavagem; // 45
	private String fDreno; // 46	
	private String fSexoRn; // 47	 
	private String fPesoRn; // 48
	private String fApgar1; // 49
	private String fApgar5; // 50
	private String fRnClassificacao; // 51 
	private String fPesoPlac; // 52 
	private String fCompCordao; // 53
	private String fObsNas; //54
	private List<LinhaReportVO> listaIntercorrencias; // 55, 56, 57 	
	private String usoTemporario;	
	private Integer fIndiceNascimento; //
		
	public PartoCesarianaVO(){}

	public String getfDtNascimento() {
		return fDtNascimento;
	}

	public void setfDtNascimento(String fDtNascimento) {
		this.fDtNascimento = fDtNascimento;
	}

	public String getfTipo() {
		return fTipo;
	}

	public void setfTipo(String fTipo) {
		this.fTipo = fTipo;
	}

	public String getfModo() {
		return fModo;
	}

	public void setfModo(String fModo) {
		this.fModo = fModo;
	}

	public String getfEpisiotomia() {
		return fEpisiotomia;
	}

	public void setfEpisiotomia(String fEpisiotomia) {
		this.fEpisiotomia = fEpisiotomia;
	}

	public String getfPeriodoDilatacao() {
		return fPeriodoDilatacao;
	}

	public void setfPeriodoDilatacao(String fPeriodoDilatacao) {
		this.fPeriodoDilatacao = fPeriodoDilatacao;
	}

	public String getfPeriodoExpulsivo() {
		return fPeriodoExpulsivo;
	}

	public void setfPeriodoExpulsivo(String fPeriodoExpulsivo) {
		this.fPeriodoExpulsivo = fPeriodoExpulsivo;
	}

	public String getfDuracao() {
		return fDuracao;
	}

	public void setfDuracao(String fDuracao) {
		this.fDuracao = fDuracao;
	}
	
	public String getfSexoRn() {
		return fSexoRn;
	}

	public void setfSexoRn(String fSexoRn) {
		this.fSexoRn = fSexoRn;
	}

	public String getfPesoRn() {
		return fPesoRn;
	}

	public void setfPesoRn(String fPesoRn) {
		this.fPesoRn = fPesoRn;
	}

	public String getfApgar1() {
		return fApgar1;
	}

	public void setfApgar1(String fApgar1) {
		this.fApgar1 = fApgar1;
	}

	public String getfApgar5() {
		return fApgar5;
	}

	public void setfApgar5(String fApgar5) {
		this.fApgar5 = fApgar5;
	}

	public String getfRnClassificacao() {
		return fRnClassificacao;
	}

	public void setfRnClassificacao(String fRnClassificacao) {
		this.fRnClassificacao = fRnClassificacao;
	}

	public String getfPesoPlac() {
		return fPesoPlac;
	}

	public void setfPesoPlac(String fPesoPlac) {
		this.fPesoPlac = fPesoPlac;
	}

	public String getfCompCordao() {
		return fCompCordao;
	}

	public void setfCompCordao(String fCompCordao) {
		this.fCompCordao = fCompCordao;
	}

	public String getfObsNas() {
		return fObsNas;
	}

	public void setfObsNas(String fObsNas) {
		this.fObsNas = fObsNas;
	}

	public List<LinhaReportVO> getListaForcipe() {
		return listaForcipe;
	}

	public void setListaForcipe(List<LinhaReportVO> listaForcipe) {
		this.listaForcipe = listaForcipe;
	}

//	public List<LinhaReportVO> getListaCesariana() {
//		return listaCesariana;
//	}
//
//	public void setListaCesariana(List<LinhaReportVO> listaCesareana) {
//		this.listaCesariana = listaCesareana;
//	}

	public String getUsoTemporario() {
		return usoTemporario;
	}

	public void setUsoTemporario(String usoTemporario) {
		this.usoTemporario = usoTemporario;
	}	

	public String getfHoraDuracao() {
		return fHoraDuracao;
	}

	public void setfHoraDuracao(String fHoraDuracao) {
		this.fHoraDuracao = fHoraDuracao;
	}

	public String getfLaparotomia() {
		return fLaparotomia;
	}

	public void setfLaparotomia(String fLaparotomia) {
		this.fLaparotomia = fLaparotomia;
	}

	public String getfHisterotomia() {
		return fHisterotomia;
	}

	public void setfHisterotomia(String fHisterotomia) {
		this.fHisterotomia = fHisterotomia;
	}

	public String getfHisterorrafia() {
		return fHisterorrafia;
	}

	public void setfHisterorrafia(String fHisterorrafia) {
		this.fHisterorrafia = fHisterorrafia;
	}

	public String getfContaminacao() {
		return fContaminacao;
	}

	public void setfContaminacao(String fContaminacao) {
		this.fContaminacao = fContaminacao;
	}

	public String getfLaqueadura() {
		return fLaqueadura;
	}

	public void setfLaqueadura(String fLaqueadura) {
		this.fLaqueadura = fLaqueadura;
	}

	public String getfRafia() {
		return fRafia;
	}

	public void setfRafia(String fRafia) {
		this.fRafia = fRafia;
	}

	public String getfLavagem() {
		return fLavagem;
	}

	public void setfLavagem(String fLavagem) {
		this.fLavagem = fLavagem;
	}

	public String getfDreno() {
		return fDreno;
	}

	public void setfDreno(String fDreno) {
		this.fDreno = fDreno;
	}

	public List<LinhaReportVO> getListaIntercorrencias() {
		return listaIntercorrencias;
	}

	public void setListaIntercorrencias(List<LinhaReportVO> listaIntercorrencias) {
		this.listaIntercorrencias = listaIntercorrencias;
	}

	public void setfIndiceNascimento(Integer fIndiceNascimento) {
		this.fIndiceNascimento = fIndiceNascimento;
	}

	public Integer getfIndiceNascimento() {
		return fIndiceNascimento;
	}

	public List<LinhaReportVO> getListaIndUsoCesareana() {
		return listaIndUsoCesareana;
	}

	public void setListaIndUsoCesareana(List<LinhaReportVO> listaIndUsoCesareana) {
		this.listaIndUsoCesareana = listaIndUsoCesareana;
	}

	
	
}