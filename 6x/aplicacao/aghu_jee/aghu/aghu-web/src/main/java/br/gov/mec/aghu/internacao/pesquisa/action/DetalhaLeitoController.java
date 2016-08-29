package br.gov.mec.aghu.internacao.pesquisa.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Classe responsável por detalhar as informações do leito
 */

public class DetalhaLeitoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5861552749229018563L;
	
	private final String PAGE_PESQUISAR_LEITOS = "pesquisaLeitos";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade ;

	/**
	 * Código do leito
	 */
	private String codigoLeitos;

	/**
	 * Leito a ser detalhado
	 */
	private AinLeitos ainLeitos;
	
	/**
	 * Ultimo extrato gerado
	 */
	private AinExtratoLeitos extratoLeito;

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void iniciar(){
	 

		if (ainLeitos == null) {
			ainLeitos = this.cadastrosBasicosInternacaoFacade.obterLeitoPorId(codigoLeitos) ;
			extratoLeito = this.pesquisaInternacaoFacade.obterUltimoExtratoLeito(codigoLeitos);
		}
	
	}

	public String cancelar() {
		ainLeitos=null;
		extratoLeito = new AinExtratoLeitos();
		return PAGE_PESQUISAR_LEITOS;
	}
	
	// GET e SET
	
	public AinLeitos getAinLeitos() {
		return ainLeitos;
	}

	public void setAinLeitos(AinLeitos ainLeitos) {
		this.ainLeitos = ainLeitos;
	}
	
	public void setCodigoLeitos(String codigoLeitos) {
		this.codigoLeitos = codigoLeitos;
	}

	public String getCodigoLeitos() {
		return codigoLeitos;
	}

	public AinExtratoLeitos getExtratoLeito() {
		return extratoLeito;
	}

	public void setExtratoLeito(AinExtratoLeitos extratoLeito) {
		this.extratoLeito = extratoLeito;
	}
	
}
