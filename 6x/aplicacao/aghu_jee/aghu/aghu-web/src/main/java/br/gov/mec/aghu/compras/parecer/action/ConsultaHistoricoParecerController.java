package br.gov.mec.aghu.compras.parecer.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.compras.vo.PareceresAvaliacaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.core.action.ActionController;


/**
 * Classe responsável por controlar as ações do criação e edição de Parecer
 * 
 */


public class ConsultaHistoricoParecerController extends ActionController {

	private static final String PARECER_OCORRENCIA_CRUD = "parecerOcorrenciaCRUD";

	private static final String PARECER_AVALIACAO_CRUD = "parecerAvaliacaoCRUD";

	private static final String PARECER_CRUD = "compras-parecerCRUD";


	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5886046098191190732L;

	@EJB
	protected IComprasFacade comprasFacade;	
	
	@EJB
	protected IParecerFacade parecerFacade;	
	private ScoParecerMaterial  parecerMaterial = new ScoParecerMaterial();
	private List<ScoParecerOcorrencia> listaParecerOcorrencia = new ArrayList<ScoParecerOcorrencia>();
	private List<PareceresAvaliacaoVO> listaParecerAvaliacaoVo = new ArrayList<PareceresAvaliacaoVO>();	
	private Integer codigoParecerMaterial;	

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void inicio() {
	 

	 
		 
		
		if (this.getCodigoParecerMaterial() != null){
			this.setParecerMaterial(this.parecerFacade.obterParecer(this
					.getCodigoParecerMaterial()));			
			this.setListaParecerOcorrencia(this.parecerFacade.listaOcorrenciaParecer(this.getParecerMaterial(), null));
			this.setListaParecerAvaliacaoVo(this.parecerFacade.listaAvaliacaoParecer(this.getParecerMaterial()));
		}		
		
	
	}
			
	
	
	public Boolean exibirEditarOcorrencia(ScoParecerOcorrencia scoParecerOcorrencia){
		
		if (scoParecerOcorrencia.getIndSituacao() != null){
			return scoParecerOcorrencia.getIndSituacao().equals(DominioSituacao.A);
		}		
		return false;
		
	}
	
	public Boolean isParecerInativo(){
		if(this.getParecerMaterial().getIndSituacao().equals(DominioSituacao.A)){
			return false;
		} else {
			return true;
		}
	}
	
    public Boolean exibirEditarAvaliacao(PareceresAvaliacaoVO parecerAvaliacaoVo){
		
		return (this.getListaParecerAvaliacaoVo().indexOf(parecerAvaliacaoVo) == 0);
		
	}
    
    public String voltar(){
    	return PARECER_CRUD;
    }
    
    public String redirecionarParecerAvaliacaoCrud(){
    	return PARECER_AVALIACAO_CRUD;
    }
    
    public String redirecionarParecerOcorrenciaCRUD(){
    	return PARECER_OCORRENCIA_CRUD;
    }
   
	// ### GETs e SETs ###
	


	public ScoParecerMaterial getParecerMaterial() {
		return parecerMaterial;
	}

	public void setParecerMaterial(ScoParecerMaterial parecerMaterial) {
		this.parecerMaterial = parecerMaterial;
	}	

	public List<ScoParecerOcorrencia> getListaParecerOcorrencia() {
		return listaParecerOcorrencia;
	}


	public void setListaParecerOcorrencia(
			List<ScoParecerOcorrencia> listaParecerOcorrencia) {
		this.listaParecerOcorrencia = listaParecerOcorrencia;
	}


	public List<PareceresAvaliacaoVO> getListaParecerAvaliacaoVo() {
		return listaParecerAvaliacaoVo;
	}


	public void setListaParecerAvaliacaoVo(
			List<PareceresAvaliacaoVO> listaParecerAvaliacaoVo) {
		this.listaParecerAvaliacaoVo = listaParecerAvaliacaoVo;
	}


	public Integer getCodigoParecerMaterial() {
		return codigoParecerMaterial;
	}



	public void setCodigoParecerMaterial(Integer codigoParecerMaterial) {
		this.codigoParecerMaterial = codigoParecerMaterial;
	}

	
}
