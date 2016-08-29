package br.gov.mec.aghu.faturamento.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatLogErrorVO;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.core.action.ActionController;



public class ConsultarFatLogErrorController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3371409887056837765L;

	private final String PageConsultarFatErrorList = "consultarFatLogErrorList";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatLogError fatLogErrorView;

	private FatLogErrorVO fatLogErrorVOView;
	
	private Integer seq;
	
	private Boolean onlyView;
	
	private Integer pacCodigo;
	private String pacNome;
	private Integer pacProntuario;
	
	public void iniciarPagina() {
	 

		fatLogErrorView = faturamentoFacade.obterFatLogError(seq);
		
		if(fatLogErrorView != null){
			fatLogErrorVOView = faturamentoFacade.obterFatLogErrorVo( fatLogErrorView.getIphPhoSeqItem1(), 		fatLogErrorView.getIphSeqItem1(),
																	  fatLogErrorView.getIphPhoSeqItem2(), 		fatLogErrorView.getIphSeqItem2(), 
																	  fatLogErrorView.getIphPhoSeqRealizado(),  fatLogErrorView.getIphSeqRealizado(), 
																	  fatLogErrorView.getIphPhoSeq(), 			fatLogErrorView.getIphSeq()); 
		}
	
	}

	public String voltar() {
		return PageConsultarFatErrorList;
	}
	
	public FatLogError getFatLogErrorView() {
		return fatLogErrorView;
	}

	public void setFatLogErrorView(FatLogError fatLogErrorView) {
		this.fatLogErrorView = fatLogErrorView;
	}

	public FatLogErrorVO getFatLogErrorVOView() {
		return fatLogErrorVOView;
	}

	public void setFatLogErrorVOView(FatLogErrorVO fatLogErrorVOView) {
		this.fatLogErrorVOView = fatLogErrorVOView;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Boolean getOnlyView() {
		return onlyView;
	}

	public void setOnlyView(Boolean onlyView) {
		this.onlyView = onlyView;
	}

	public String getPageConsultarFatErrorList() {
		return PageConsultarFatErrorList;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
}