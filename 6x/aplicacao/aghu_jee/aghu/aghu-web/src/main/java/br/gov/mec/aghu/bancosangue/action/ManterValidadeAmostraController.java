package br.gov.mec.aghu.bancosangue.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsValidAmostrasComponenteId;
import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;


public class ManterValidadeAmostraController extends ActionController {

	private static final long serialVersionUID = 5301265832804340302L;

	private static final String PESQUISAR_VALIDADE_AMOSTRA = "bancodesangue-pesquisarValidadeAmostra";

	private AbsValidAmostrasComponentes amostraComp;
	
	private AbsComponenteSanguineo componenteSanguineo;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	protected enum EnumManterValidadeAmostraMessagens {
		MSG_VALIDADE_AMOSTRA_GRAVADO_SUCESSO,
		MSG_VALIDADE_AMOSTRA_ALTERADO_SUCESSO;
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public String iniciar(){
	 

	 

       if(amostraComp != null && amostraComp.getId() != null){
    	   amostraComp = bancoDeSangueFacade.obterValidadeAmostraPorCodigo(amostraComp.getId());

			if(amostraComp == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
       }else{
    	   amostraComp = new AbsValidAmostrasComponentes();
    	   amostraComp.setId(new AbsValidAmostrasComponenteId());
    	   amostraComp.setComponenteSanguineo(componenteSanguineo);
    	   amostraComp.getId().setCsaCodigo(componenteSanguineo.getCodigo());

       }
       
       return null;
    
	}
	
	public String gravar(){
		try{
			String msgPrt;
			if(amostraComp.getId().getSeqp() == null){
				msgPrt = EnumManterValidadeAmostraMessagens.MSG_VALIDADE_AMOSTRA_GRAVADO_SUCESSO.toString();
			}else{
				msgPrt = EnumManterValidadeAmostraMessagens.MSG_VALIDADE_AMOSTRA_ALTERADO_SUCESSO.toString();
			}
			
			bancoDeSangueFacade.persistOrUpdate(amostraComp);
			
			this.apresentarMsgNegocio(Severity.INFO,msgPrt);
			
			return cancelar();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	public String cancelar(){
		amostraComp = null;
		return  PESQUISAR_VALIDADE_AMOSTRA;
	}
	
	public AbsValidAmostrasComponentes getAmostraComp() {
		return amostraComp;
	}

	public void setAmostraComp(AbsValidAmostrasComponentes amostraComp) {
		this.amostraComp = amostraComp;
	}
	
	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	
}