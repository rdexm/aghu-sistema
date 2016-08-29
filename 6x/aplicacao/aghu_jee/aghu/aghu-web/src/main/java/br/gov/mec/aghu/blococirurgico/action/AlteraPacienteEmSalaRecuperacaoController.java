package br.gov.mec.aghu.blococirurgico.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AlteraPacienteEmSalaRecuperacaoController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final long serialVersionUID = 1001254663147702012L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private MbcCirurgias cirurgia;
	
	private String localizacao;
	
	private String nomeCirurgiao;

	private Integer crgSeq;
	private String cameFrom;
	private String prontuarioFormatado;
	
	public void inicio() {
	 

	 


		cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(crgSeq);
		
		AghUnidadesFuncionais unidadeFuncional = (cirurgia.getUnidadeFuncional() != null) ? this.aghuFacade.obterUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq()) : null;
		
		if (unidadeFuncional != null){
    		cirurgia.setUnidadeFuncional(unidadeFuncional);
		}
		
		AipPacientes paciente = (cirurgia.getPaciente() != null) ? this.pacienteFacade.obterAipPacientesPorChavePrimaria(cirurgia.getPaciente().getCodigo()) : null;
		
		if (paciente != null) {
			cirurgia.setPaciente(paciente);
			this.setProntuarioFormatado(CoreUtil.formataProntuario(paciente.getProntuario()));
		}
				
		
		localizacao = getBlocoCirurgicoFacade().setarLocalizacao(cirurgia.getPaciente().getCodigo());
		
		nomeCirurgiao = getBlocoCirurgicoFacade().setarCirurgiao(crgSeq);		
		
				
	
	}
	
	
	public String cancelar() {
		return this.cameFrom;
	}
	
	public String gravar(){
	
		String msg = " ";
		try {
				msg = blocoCirurgicoFacade.gravarMbcCirurgias(cirurgia);
				apresentarMsgNegocio(Severity.INFO, msg);				
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return cancelar();
	}
	
	
	//Get e Set
	
	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public String getNomeCirurgiao() {
		return nomeCirurgiao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public void setNomeCirurgiao(String nomeCirurgiao) {
		this.nomeCirurgiao = nomeCirurgiao;
	}


	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}


	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

}
