package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
@Stateless
public class ProjetosPesquisaPOLON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ProjetosPesquisaPOLON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
 
	private static final long serialVersionUID = 6243946292992456339L;

	public List<AelProjetoPacientes> pesquisarProjetosPesquisaPacientePOL(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigo) throws BaseException  {
		
		List<AelProjetoPacientes> listaProjetosPesquisaPOL;
				
		if(testaPermissaoServidorLogado()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			listaProjetosPesquisaPOL = getExamesLaudosFacade().pesquisarProjetosPesquisaPacientePOL(firstResult, 
					maxResult, orderProperty, asc, codigo, servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo());
		}else{
			listaProjetosPesquisaPOL = getExamesLaudosFacade().pesquisarProjetosPesquisaPacientePOL(firstResult, 
					maxResult, orderProperty, asc, codigo, null, null);
		}
		
//		List<AelProjetoPacientes> listaProjetosPesquisaPOL = getExamesLaudosFacade().pesquisarProjetosPesquisaPacientePOL(firstResult, 
//				maxResult, orderProperty, asc, codigo, matricula, vinCodigo);
		
 		for (AelProjetoPacientes projeto: listaProjetosPesquisaPOL){	
 			 					
 			if(projeto.getProjetoPesquisa() != null ){

 				if (projeto.getProjetoPesquisa().getNome() != null) {

 					StringBuffer pesquisador = new StringBuffer(); 					
 					pesquisador.append(projeto.getProjetoPesquisa().getNome()).append(" / ");

 					projeto.setNomeProjetoPesquisador(pesquisador.toString());
 				}

 				if(projeto.getProjetoPesquisa().getNomeResponsavel() != null){

 					StringBuffer pesquisador = new StringBuffer();
 					pesquisador.append(projeto.getNomeProjetoPesquisador()).append(projeto.getProjetoPesquisa().getNomeResponsavel());

 					projeto.setNomeProjetoPesquisador(pesquisador.toString());

 				}
 		}
 			
 		}
		return listaProjetosPesquisaPOL;
		
	}
	
	public Long pesquisarProjetosPesquisaPacientePOLCount(Integer codigo) throws BaseException  {
		if(testaPermissaoServidorLogado()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			RapServidores servidor = servidorLogado;
			return getExamesLaudosFacade().pesquisarProjetosPesquisaPacientePOLCount(codigo, servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		}else{
			return getExamesLaudosFacade().pesquisarProjetosPesquisaPacientePOLCount(codigo, null, null);
		}
	}
	
	private Boolean testaPermissaoServidorLogado() {			
		if(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "acessoMonitorProjPesqPOL", "acessar")){						
			return Boolean.TRUE;
		}
		return Boolean.FALSE;		
	}

	protected IExamesLaudosFacade getExamesLaudosFacade(){
		return this.examesLaudosFacade;
	}

	private IPermissionService getPermissionService() {
		return this.permissionService;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}