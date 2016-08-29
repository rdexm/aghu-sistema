package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaFluxogramaController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;



public class CancelarExamesResponsabilidadeSolicitanteController extends ActionController {

	private static final Log LOG = LogFactory.getLog(CancelarExamesResponsabilidadeSolicitanteController.class);
	private static final String PAGE_CANCELAR_EXAMES = "pesquisaCancelarExamesResponsabilidadeSolicitante.xhtml";

	/**
	 * 
	 */
	private static final long serialVersionUID = -1018431615250945238L;

	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private PesquisaFluxogramaController pesquisaFluxogramaController;
	
	/*	fitro da tela de pesquisa	*/
	private VAelSolicAtendsVO  vAelSolicAtends;
	private Integer soeSeq;
	List<ItemSolicitacaoExameCancelamentoVO> listaItensSolicExames;
	
	/**
	 * Controle de paginação
	 */
	private String voltarPara;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	

	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void inicio() {
	 


		try {
			if(this.soeSeq != null){
				this.vAelSolicAtends = this.solicitacaoExameFacade.buscaExameCancelamentoSolicRespons(soeSeq);
				this.carregarListExames();
			}

		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
		} 


	
	}
	
	public String voltar() {
		
		pesquisaFluxogramaController.setProntuario(null);
		
		if (voltarPara != null) {
			
			return this.voltarPara; //"voltarParaPesquisaExames";
			
		} 
		
		return PAGE_CANCELAR_EXAMES;
		
	}

	public void carregarListExames(){

		try {
			
			listaItensSolicExames = this.solicitacaoExameFacade.listarItensExameCancelamentoSolicitante(soeSeq);

		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
		} 

	}
	
	public void cancelarItensExamesSelecionados() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
		
			List<ItemSolicitacaoExameCancelamentoVO> itens = this.listaItensSolicExames;
			this.examesBeanFacade.excluirItensExamesSelecionados(itens, nomeMicrocomputador);
			listaItensSolicExames = this.solicitacaoExameFacade.listarItensExameCancelamentoSolicitante(soeSeq);
			
		} catch (BaseException e) {
		
			apresentarExcecaoNegocio(e);
		
		} 
			
	}

	public List<ItemSolicitacaoExameCancelamentoVO> getListaItensSolicExames() {
		return listaItensSolicExames;
	}

	public void setListaItensSolicExames(
			List<ItemSolicitacaoExameCancelamentoVO> l) {
		this.listaItensSolicExames = l;
	}

	public VAelSolicAtendsVO getvAelSolicAtends() {
		return vAelSolicAtends;
	}

	public void setvAelSolicAtends(VAelSolicAtendsVO vAelSolicAtends) {
		this.vAelSolicAtends = vAelSolicAtends;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}


	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	


}