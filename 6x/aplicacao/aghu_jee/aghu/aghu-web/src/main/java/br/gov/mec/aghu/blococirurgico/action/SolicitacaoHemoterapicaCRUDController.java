package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendadaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SolicitacaoHemoterapicaCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final String PAGE_DETALHA_REGISTRO_CIRURGIA = "detalhaRegistroCirurgia";
	/**
	 * 
	 */
	private static final long serialVersionUID = -3566582839368074664L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
		
	private Integer crgSeq;
	
	private String csaCodigo;
	
	private MbcSolicHemoCirgAgendada solicHemoCirgAgendada = new MbcSolicHemoCirgAgendada();
	
	private String voltarPara;
	
	@Inject
	private DetalhaRegistroCirurgiaController detalhaRegistroCirurgiaController;
	
	
	public List<AbsComponenteSanguineo> listarComponenteSanguineo(String objPesquisa) {
		return this.returnSGWithCount(bancoDeSangueFacade.listarAbsComponenteSanguineosNaoExisteNaCirurgia(objPesquisa, crgSeq),listarComponenteSanguineoCount(objPesquisa));
	}
	
	public Long listarComponenteSanguineoCount(String objPesquisa) {
		return bancoDeSangueFacade.listarAbsComponenteSanguineosNaoExisteNaCirurgiaCount(objPesquisa, crgSeq);
	}

	public void iniciar() {
	 

	 

		if(crgSeq != null && csaCodigo!= null) {
			MbcSolicHemoCirgAgendadaId solicHemoId = new MbcSolicHemoCirgAgendadaId();
			solicHemoId.setCrgSeq(crgSeq);
			solicHemoId.setCsaCodigo(csaCodigo);
			solicHemoCirgAgendada = blocoCirurgicoFacade.obterMbcSolicHemoCirgAgendadaById(crgSeq,csaCodigo);
		}else{
			resetarSolicitacao();
		}
	
	}
	
	
	private void resetarSolicitacao() {
		solicHemoCirgAgendada = new MbcSolicHemoCirgAgendada();
		MbcSolicHemoCirgAgendadaId id = new MbcSolicHemoCirgAgendadaId();
		id.setCrgSeq(crgSeq);		
		solicHemoCirgAgendada.setId(id);
		csaCodigo = null;
	}
	

	/**
	 * Método chamado ao cancelar a tela de cadastro de solicitacao hemoterapica
	 */
	public String voltar() {
		if(csaCodigo == null) {
			solicHemoCirgAgendada = new MbcSolicHemoCirgAgendada();
		}
		if(PAGE_DETALHA_REGISTRO_CIRURGIA.equalsIgnoreCase(voltarPara)){
			detalhaRegistroCirurgiaController.setAbaAtiva(4);
		}
		return voltarPara;
	}
	
	
	public String gravar() {
		try {
			solicHemoCirgAgendada.getId().setCsaCodigo(solicHemoCirgAgendada.getAbsComponenteSanguineo().getCodigo());
			String mensagemSucesso = this.blocoCirurgicoFacade.persistirMbcSolicHemoCirgAgendada
				(solicHemoCirgAgendada);	
			
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);	
			return null;
		}
		return voltar();
	}
	
	public void limpar() {
		resetarSolicitacao();
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getCsaCodigo() {
		return csaCodigo;
	}

	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}

	public void setSolicHemoCirgAgendada(
			MbcSolicHemoCirgAgendada solicHemoCirgAgendada) {
		this.solicHemoCirgAgendada = solicHemoCirgAgendada;
	}

	public MbcSolicHemoCirgAgendada getSolicHemoCirgAgendada() {
		return solicHemoCirgAgendada;
	}

	public void setSalaCirurgica(MbcSolicHemoCirgAgendada solicHemoCirgAgendada) {
		this.solicHemoCirgAgendada = solicHemoCirgAgendada;
	}

}
