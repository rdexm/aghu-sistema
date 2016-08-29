package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class PdtDescricaoProcedimentoCirurgiaVO {
	
	private String  f30;//C1RN5
	private String  f07;//C21(C25)RN6
	private String  f01;//C2
	private String f02;//C2
	private String  fNome;//C2
	private String fProntuario;//C2
	private String  fIdade;//C2
	private String  fSexo;//C2
	private String  f10;//C2
	private String  fConvenio;//C2
	private String  f24;//C31RN17
	private String  f28;//C31RN16
	private List<LinhaReportVO> listaC03;//f11, f12, f13
	private String  f03;//C30
	private String  fMotivo;//C4
	private String  fSolicitadoPor;//C4
	private String  fddtComplemento;//C4
	private String  f20;//C5
	private String  f21;//C5
	private String  f22;//C5	
	private List<LinhaReportVO> listaC09C10;//f06, f23, f29 
	private String  f08;//C6
	private String  f09;//C5	
	private String  f14;//C7
	private String  f15;//C8
	private String  f16;//C5
	private String  f25;//C5
	private String  f26;//C11
	private List<LinhaReportVO> listaC15;//f27, f19, f17
	private List<LinhaReportVO> listaC22C24RN8;//f33, f34, f35, f37
	private List<LinhaReportVO> listaC26C27;//f42, f43	
	private List<LinhaReportVO> listaC29;//f44
	private String  f38;//C1
	private String  f39;//RN9
	//private String numero;//C21(C25)RN22
	private Boolean notaAdicional;//C21(C25)RN14
	private List<LinhaReportVO> listaC19C21C25;//f18, f31, f40, f41	
	private String f40;
	private String f41;
	private String fPaciente;//C2
	private String fLeito;//C2	
	private String fCurrentDate;
	private Integer fPacCo;//C2
	private Integer f32;//seqPdtDescricao via #15825 
	
	private Integer countRegistros;
	
	
	//Avaliação pre-sedação
	private String descViaAereas;
	private DominioAsa asa;
	private String avaliacaoClinica;
	private String comorbidades;
	private String exameFisico;
	private String nroRegConselho;	
	private String nome;
	

	
	public PdtDescricaoProcedimentoCirurgiaVO(){}

	public String getF30() {
		return f30;
	}

	public String getF07() {
		return f07;
	}

	public String getF01() {
		return f01;
	}

	public String getfNome() {
		return fNome;
	}

	public String getfProntuario() {
		return fProntuario;
	}

	public String getfIdade() {
		return fIdade;
	}

	public String getfSexo() {
		return fSexo;
	}

	public String getF10() {
		return f10;
	}

	public String getfConvenio() {
		return fConvenio;
	}

	public String getF24() {
		return f24;
	}

	public String getF28() {
		return f28;
	}

	public List<LinhaReportVO> getListaC03() {
		return listaC03;
	}

	public String getfMotivo() {
		return fMotivo;
	}

	public String getfSolicitadoPor() {
		return fSolicitadoPor;
	}

	public String getFddtComplemento() {
		return fddtComplemento;
	}

	public String getF20() {
		return f20;
	}

	public String getF21() {
		return f21;
	}

	public String getF22() {
		return f22;
	}

	public String getF08() {
		return f08;
	}

	public String getF09() {
		return f09;
	}

	public String getF14() {
		return f14;
	}

	public String getF15() {
		return f15;
	}

	public String getF16() {
		return f16;
	}

	public String getF25() {
		return f25;
	}

	public String getF26() {
		return f26;
	}

	public List<LinhaReportVO> getListaC15() {
		return listaC15;
	}

	public List<LinhaReportVO> getListaC29() {
		return listaC29;
	}

	public String getF38() {
		return f38;
	}

	public String getF39() {
		return f39;
	}

	public Boolean getNotaAdicional() {
		return notaAdicional;
	}
	
	public String getfPaciente() {
		return fPaciente;
	}

	public String getfLeito() {
		return fLeito;
	}

	public String getfCurrentDate() {
		return fCurrentDate;
	}

	public Integer getfPacCo() {
		return fPacCo;
	}

	public Integer getF32() {
		return f32;
	}

	public void setF30(String f30) {
		this.f30 = f30;
	}

	public void setF07(String f07) {
		this.f07 = f07;
	}

	public void setF01(String f01) {
		this.f01 = f01;
	}

	public void setfNome(String fNome) {
		this.fNome = fNome;
	}

	public void setfProntuario(String fProntuario) {
		this.fProntuario = fProntuario;
	}

	public void setfIdade(String fIdade) {
		this.fIdade = fIdade;
	}

	public void setfSexo(String fSexo) {
		this.fSexo = fSexo;
	}

	public void setF10(String f10) {
		this.f10 = f10;
	}

	public void setfConvenio(String fConvenio) {
		this.fConvenio = fConvenio;
	}

	public void setF24(String f24) {
		this.f24 = f24;
	}

	public void setF28(String f28) {
		this.f28 = f28;
	}

	public void setListaC03(List<LinhaReportVO> listaC03) {
		this.listaC03 = listaC03;
	}

	public void setfMotivo(String fMotivo) {
		this.fMotivo = fMotivo;
	}

	public void setfSolicitadoPor(String fSolicitadoPor) {
		this.fSolicitadoPor = fSolicitadoPor;
	}

	public void setFddtComplemento(String fddtComplemento) {
		this.fddtComplemento = fddtComplemento;
	}

	public void setF20(String f20) {
		this.f20 = f20;
	}

	public void setF21(String f21) {
		this.f21 = f21;
	}

	public void setF22(String f22) {
		this.f22 = f22;
	}

	public void setF08(String f08) {
		this.f08 = f08;
	}

	public void setF09(String f09) {
		this.f09 = f09;
	}

	public void setF14(String f14) {
		this.f14 = f14;
	}

	public void setF15(String f15) {
		this.f15 = f15;
	}

	public void setF16(String f16) {
		this.f16 = f16;
	}

	public void setF25(String f25) {
		this.f25 = f25;
	}

	public void setF26(String f26) {
		this.f26 = f26;
	}

	public void setListaC15(List<LinhaReportVO> listaC15) {
		this.listaC15 = listaC15;
	}

	public void setListaC29(List<LinhaReportVO> listaC29) {
		this.listaC29 = listaC29;
	}

	public void setF38(String f38) {
		this.f38 = f38;
	}

	public void setF39(String f39) {
		this.f39 = f39;
	}

	public void setNotaAdicional(Boolean notaAdicional) {
		this.notaAdicional = notaAdicional;
	}
	
	public void setfPaciente(String fPaciente) {
		this.fPaciente = fPaciente;
	}

	public void setfLeito(String fLeito) {
		this.fLeito = fLeito;
	}

	public void setfCurrentDate(String fCurrentDate) {
		this.fCurrentDate = fCurrentDate;
	}

	public void setfPacCo(Integer fPacCo) {
		this.fPacCo = fPacCo;
	}

	public void setF32(Integer f32) {
		this.f32 = f32;
	}

	public String getF02() {
		return f02;
	}

	public void setF02(String f02) {
		this.f02 = f02;
	}

	public String getF03() {
		return f03;
	}

	public void setF03(String f03) {
		this.f03 = f03;
	}

	public List<LinhaReportVO> getListaC09C10() {
		return listaC09C10;
	}

	public void setListaC09C10(List<LinhaReportVO> listaC09C10) {
		this.listaC09C10 = listaC09C10;
	}

	public List<LinhaReportVO> getListaC22C24RN8() {
		return listaC22C24RN8;
	}

	public void setListaC22C24RN8(List<LinhaReportVO> listaC22C24RN8) {
		this.listaC22C24RN8 = listaC22C24RN8;
	}

	public List<LinhaReportVO> getListaC19C21C25() {
		return listaC19C21C25;
	}

	public void setListaC19C21C25(List<LinhaReportVO> listaC19C21C25) {
		this.listaC19C21C25 = listaC19C21C25;
	}

//	public String getNumero() {
//		return numero;
//	}
//
//	public void setNumero(String numero) {
//		this.numero = numero;
//	}

	public List<LinhaReportVO> getListaC26C27() {
		return listaC26C27;
	}

	public void setListaC26C27(List<LinhaReportVO> listaC26C27) {
		this.listaC26C27 = listaC26C27;
	}

	public void setF40(String f40) {
		this.f40 = f40;
	}

	public String getF40() {
		return f40;
	}

	public void setF41(String f41) {
		this.f41 = f41;
	}

	public String getF41() {
		return f41;
	}
	
	public Integer getCountRegistros() {

		return countRegistros;

	}

	public void setCountRegistros(Integer countRegistros) {

		this.countRegistros = countRegistros;

	}

	public String getDescViaAereas() {
		return descViaAereas;
	}

	public void setDescViaAereas(String descViaAereas) {
		this.descViaAereas = descViaAereas;
	}

	public DominioAsa getAsa() {
		return asa;
	}

	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}

	public String getAvaliacaoClinica() {
		return avaliacaoClinica;
	}

	public void setAvaliacaoClinica(String avaliacaoClinica) {
		this.avaliacaoClinica = avaliacaoClinica;
	}

	public String getComorbidades() {
		return comorbidades;
	}

	public void setComorbidades(String comorbidades) {
		this.comorbidades = comorbidades;
	}

	public String getExameFisico() {
		return exameFisico;
	}

	public void setExameFisico(String exameFisico) {
		this.exameFisico = exameFisico;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


}