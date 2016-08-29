package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.awt.image.BufferedImage;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.BaseBean;


public class SumarioAssistenciaPartoVO implements BaseBean{ 
	
	private static final long serialVersionUID = -4655390890419066248L;

	private String fNome; // 1 
	private String fProntuario; // 2 
	private String fIdade; // 3 
	private String fAltura; // 4 
	private String fConvenio; // 5 
	private String fPeso; // 6 
	//private LinhaReportVO sumarioAssPartoOutrosDadosVO; // "4 a 6", 10, 16, "21 a 26" *************** NOVA VO  
	private List<LinhaReportVO> listaEquipe; // 7 	
	private transient BufferedImage grafico1;
	private transient BufferedImage grafico2;  	
	private String fJustificativa; // 10 
	private String fFormaRuptura; //11.1
	private String fDthrRompimento; // 11.2
	private String fLabel; //12
	private String fLiquidoAmniotico; //13
	private String fOdor; //14
	private String fAmnioscopia; //15
	private String fCardiotocografia; //16
	private List<LinhaReportVO> listaMedicamentos; // 17, 18, 19, 20 	
	private String fDthrIniCtg; // 21
	private String fIndicacoes; // 22
	private String fTipoParto; // 23
	private String fDescrInd; // 24
	private String fTbpPosicao; //25
	private String fObservacao; // 26
	private List<PartoCesarianaVO> listaPartoCesarianaVO; // 27 a 57		 
	private List<LinhaReportVO> listaProfissionais; // 58, 59	
	private String f8; // 60 
	private String f9; // 61 
	private List<LinhaReportVO> listaNotasAdicionais; // 62, 63, 64 		 
	private String fLeito; // 66		 
	private String fCurrentDate; // 68
	private Integer fPacCodigo; // 69
	private Integer fConNumero; // 70
	private List<LinhaReportVO> listaIntercorrencias; // 55, 56, 57
	
	
	public SumarioAssistenciaPartoVO(){}

	public String getfNome() {
		return fNome;
	}

	public void setfNome(String fNome) {
		this.fNome = fNome;
	}

	public String getfProntuario() {
		return fProntuario;
	}

	public void setfProntuario(String fProntuario) {
		this.fProntuario = fProntuario;
	}

	public String getfIdade() {
		return fIdade;
	}

	public void setfIdade(String fIdade) {
		this.fIdade = fIdade;
	}

	public String getfLiquidoAmniotico() {
		return fLiquidoAmniotico;
	}

	public void setfLiquidoAmniotico(String fLiquidoAmniotico) {
		this.fLiquidoAmniotico = fLiquidoAmniotico;
	}

	public String getfOdor() {
		return fOdor;
	}

	public void setfOdor(String fOdor) {
		this.fOdor = fOdor;
	}

	public String getfAmnioscopia() {
		return fAmnioscopia;
	}

	public void setfAmnioscopia(String fAmnioscopia) {
		this.fAmnioscopia = fAmnioscopia;
	}
	
	public String getfLeito() {
		return fLeito;
	}

	public void setfLeito(String fLeito) {
		this.fLeito = fLeito;
	}

	public List<LinhaReportVO> getListaEquipe() {
		return listaEquipe;
	}

	public void setListaEquipe(List<LinhaReportVO> listaEquipe) {
		this.listaEquipe = listaEquipe;
	}

	public String getfLabel() {
		return fLabel;
	}

	public void setfLabel(String fLabel) {
		this.fLabel = fLabel;
	}

	public List<LinhaReportVO> getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(List<LinhaReportVO> listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public List<PartoCesarianaVO> getListaPartoCesarianaVO() {
		return listaPartoCesarianaVO;
	}

	public void setListaPartoCesarianaVO(
			List<PartoCesarianaVO> listaPartoCesarianaVO) {
		this.listaPartoCesarianaVO = listaPartoCesarianaVO;
	}

	public String getfCurrentDate() {
		return fCurrentDate;
	}

	public void setfCurrentDate(String fCurrentDate) {
		this.fCurrentDate = fCurrentDate;
	}

	public List<LinhaReportVO> getListaProfissionais() {
		return listaProfissionais;
	}

	public void setListaProfissionais(List<LinhaReportVO> listaProfissionais) {
		this.listaProfissionais = listaProfissionais;
	}

	public List<LinhaReportVO> getListaNotasAdicionais() {
		return listaNotasAdicionais;
	}

	public void setListaNotasAdicionais(List<LinhaReportVO> listaNotasAdicionais) {
		this.listaNotasAdicionais = listaNotasAdicionais;
	}

	public void setGrafico1(BufferedImage grafico1) {
		this.grafico1 = grafico1;
	}

	public BufferedImage getGrafico1() {
		return grafico1;
	}

	public Integer getfPacCodigo() {
		return fPacCodigo;
	}

	public void setfPacCodigo(Integer fPacCodigo) {
		this.fPacCodigo = fPacCodigo;
	}

	public Integer getfConNumero() {
		return fConNumero;
	}

	public void setfConNumero(Integer fConNumero) {
		this.fConNumero = fConNumero;
	}

	public void setfFormaRuptura(String fFormaRuptura) {
		this.fFormaRuptura = fFormaRuptura;
	}

	public String getfFormaRuptura() {
		return fFormaRuptura;
	}

	public void setfDthrRompimento(String fDthrRompimento) {
		this.fDthrRompimento = fDthrRompimento;
	}

	public String getfDthrRompimento() {
		return fDthrRompimento;
	}

	public String getF8() {
		return f8;
	}

	public void setF8(String f8) {
		this.f8 = f8;
	}

	public String getF9() {
		return f9;
	}

	public void setF9(String f9) {
		this.f9 = f9;
	}

	public String getfAltura() {
		return fAltura;
	}

	public void setfAltura(String fAltura) {
		this.fAltura = fAltura;
	}

	public String getfConvenio() {
		return fConvenio;
	}

	public void setfConvenio(String fConvenio) {
		this.fConvenio = fConvenio;
	}

	public String getfPeso() {
		return fPeso;
	}

	public void setfPeso(String fPeso) {
		this.fPeso = fPeso;
	}

	public String getfJustificativa() {
		return fJustificativa;
	}

	public void setfJustificativa(String fJustificativa) {
		this.fJustificativa = fJustificativa;
	}

	public String getfCardiotocografia() {
		return fCardiotocografia;
	}

	public void setfCardiotocografia(String fCardiotocografia) {
		this.fCardiotocografia = fCardiotocografia;
	}

	public String getfDthrIniCtg() {
		return fDthrIniCtg;
	}

	public void setfDthrIniCtg(String fDthrIniCtg) {
		this.fDthrIniCtg = fDthrIniCtg;
	}

	public String getfIndicacoes() {
		return fIndicacoes;
	}

	public void setfIndicacoes(String fIndicacoes) {
		this.fIndicacoes = fIndicacoes;
	}

	public String getfTipoParto() {
		return fTipoParto;
	}

	public void setfTipoParto(String fTipoParto) {
		this.fTipoParto = fTipoParto;
	}

	public String getfDescrInd() {
		return fDescrInd;
	}

	public void setfDescrInd(String fDescrInd) {
		this.fDescrInd = fDescrInd;
	}

	public String getfTbpPosicao() {
		return fTbpPosicao;
	}

	public void setfTbpPosicao(String fTbpPosicao) {
		this.fTbpPosicao = fTbpPosicao;
	}

	public String getfObservacao() {
		return fObservacao;
	}

	public void setfObservacao(String fObservacao) {
		this.fObservacao = fObservacao;
	}

	public List<LinhaReportVO> getListaIntercorrencias() {
		return listaIntercorrencias;
	}

	public void setListaIntercorrencias(List<LinhaReportVO> listaIntercorrencias) {
		this.listaIntercorrencias = listaIntercorrencias;
	}

	public BufferedImage getGrafico2() {
		return grafico2;
	}

	public void setGrafico2(BufferedImage grafico2) {
		this.grafico2 = grafico2;
	}
	
public enum Fields {
		

		CON_SEQ("fConNumero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}