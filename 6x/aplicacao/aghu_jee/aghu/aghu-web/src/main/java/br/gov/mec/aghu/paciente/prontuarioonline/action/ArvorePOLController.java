package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioNodoPOL;
import br.gov.mec.aghu.model.AghNodoPol;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.ParametrosPOLEnum;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

@SessionScoped
public class ArvorePOLController extends ActionController {

   	private static final String ACESSAR = "acessar",
   							    RENDER = "render",
   							    SEPARADOR =  "','",
   							    POL_PAGE = "/paciente/prontuarioonline/arvorePOL.xhtml",
   							    PESQUISA_AVANCADA="pol-pesquisaAvancada";
   	
	private static final long serialVersionUID = 7807563208907890394L;
	private final Integer NUMERO_MAXIMO_PRONTUARIOS=10;
	private final int NODO_DADOS_PACIENTES=0;  
	private final int NODO_DADOS_EXAMES=7;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IAghuFacade aghuFacade;	
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SecurityController securityController;

	
	private Integer prontuario;
	private Integer codigo;
	private Integer activeTab=0;
	private List<AghNodoPol> nodoOriginalList;
	private Map<Integer, TreeNode> pacientesMap;
	private NodoPOLVO nodoSelecionado;
	private String descricaoExpandedNode;
	private Map<ParametrosPOLEnum, Object> parametros;
	private List<AipPacientes> pacienteList;
	private String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	private String pagOrigem;
	private AipPacientes pacienteRemove;
	
	//private String paramRedirecionaAghWeb;
	private String paramCertificadoDigital;
	
	private Boolean redirecionaAghWeb = false;	
	private String banco = null;
	private String urlBaseWebForms = null;
	private Boolean isHcpa = false;
	
	private Boolean chamadaExames = false;
	
	private boolean paramAtivaDigitalizacaoPOL;
	private Boolean modoPesquisaPOL=false;
	private Boolean chamadaForaPOL=false;
	private Boolean habilitarPol;
	
	@PostConstruct
	protected void init(){
		
		habilitarPol = securityController.usuarioTemPermissao(POL_PAGE, RENDER, false);
		
		pacientesMap=new LinkedHashMap<>();
		parametros = new HashMap<>();
		pacienteList=new ArrayList<AipPacientes>();
		
		this.isHcpa = this.aghuFacade.isHCPA();
		
		// #Merge 158568
		this.popularParametrosAghWeb(); 
	//	this.inicializaRedirecionamentoAghWeb();
		this.inicializaParametroCertificadoDigital();	
		this.inicializaParametroDigitalizacao();		
	}
	
	public void removerPacienteArvore(){
		
		pacientesMap.remove(pacienteRemove.getProntuario());
		int index = pacienteList.indexOf(pacienteRemove);
		pacienteList.remove(index);
		
		pacienteRemove=null;
		nodoSelecionado=null;
		modoPesquisaPOL=false;
		
		if(!pacienteList.isEmpty()){
			if(index == 0){
				prontuario = pacienteList.get(0).getProntuario();
			} else {
				prontuario = pacienteList.get(--index).getProntuario();
			}
			activeTab = index;			
			nodoSelecionado = (NodoPOLVO) pacientesMap.get(prontuario).getChildren().get(NODO_DADOS_PACIENTES).getData();
		}else{
			modoPesquisaPOL=true;
		}
	}
	
	private DefaultTreeNode createRoot(List<NodoPOLVO> polItems, AipPacientes paciente){
		DefaultTreeNode tree = new DefaultTreeNode(prontuario, null);
		for (NodoPOLVO nodo : polItems){
			nodo.setNomePaciente(paciente.getNome());
			recursiveNodo(nodo, tree,paciente.getNome());
		}
		return tree;
	}
	
	private void recursiveNodo(NodoPOLVO nodoPOL, TreeNode nodeView, String nomePaciente){
		DefaultTreeNode novo = new DefaultTreeNode(nodoPOL, null);
		if (nodoPOL != null && nodoPOL.getOrdem() != null && nodoPOL.getOrdem().intValue() == NODO_DADOS_EXAMES && chamadaExames) {
			novo.setExpanded(true);
			try {
				NodoPOLVO polvo = (NodoPOLVO) novo.getData();
				int count = polvo.getNodos().size();
				prontuarioOnlineFacade.expandirNodosPOL(polvo, parametros);
				if (polvo.getNodos().size() > count){
					if (!polvo.getNodos().get(0).getAtivo()){
						polvo.getNodos().remove(0);
					}	
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		}
		if (!nodoPOL.getNodos().isEmpty()){
			for (NodoPOLVO nodo : nodoPOL.getNodos()){
				nodo.setNomePaciente(nomePaciente);
				recursiveNodo(nodo, novo, nomePaciente);
			}	
		}
		
		if (descricaoExpandedNode != null) {
			String[] tokens = descricaoExpandedNode.split(",");
			Boolean expand = Boolean.FALSE;
			for(String str : tokens) {
				if (str.trim().equalsIgnoreCase(nodoPOL.getDescricao().trim())) {
					expand = Boolean.TRUE;
				}
			}
			if (expand) {
				novo.setExpanded(true);
				
				nodoSelecionado = nodoPOL;
				
				try {
					int count = nodoPOL.getNodos().size();
					prontuarioOnlineFacade.expandirNodosPOL(nodoPOL, parametros);
					if (nodoPOL.getNodos().size() > count){
						if (!nodoPOL.getNodos().get(0).getAtivo()){
							novo.getChildren().remove(0);
							nodoPOL.getNodos().remove(0);
						}
						for (NodoPOLVO nodo : nodoPOL.getNodos()){
							recursiveNodo(nodo, novo, nodoPOL.getNomePaciente());
						}	
					}
	
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
		nodeView.getChildren().add(novo);
	}
	
	/*private void inicializaRedirecionamentoAghWeb() {
		try {
			if (isHcpa && !validarUrlBaseWebFormsBanco()){
				paramRedirecionaAghWeb =  parametroFacade.buscarValorTexto(AghuParametrosEnum.P_REDIRECIONA_POL_AGHWEB);

				
				if (StringUtils.isNotBlank(paramRedirecionaAghWeb) && "S".equalsIgnoreCase(paramRedirecionaAghWeb)) {
					
					String strListaUsuariosPolAGHU = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_LISTA_USUARIO_POL);
					strListaUsuariosPolAGHU = strListaUsuariosPolAGHU.replaceAll(" ", "");
					String[] listaUsuariosPolAGHU = strListaUsuariosPolAGHU.split(",");
					
					if (Arrays.asList(listaUsuariosPolAGHU).contains(obterLoginUsuarioLogado().toUpperCase())){
						redirecionaAghWeb  = false;
					} else {
						redirecionaAghWeb = true;
					}
					
				} else if (StringUtils.isNotBlank(paramRedirecionaAghWeb) && "N".equalsIgnoreCase(paramRedirecionaAghWeb)) {
					redirecionaAghWeb = false;
				} else {
					redirecionaAghWeb = true;
				}
				
			} else {
				redirecionaAghWeb = false;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			redirecionaAghWeb = true;
		}
		
	}*/
	
	private void popularParametrosAghWeb() {
		try {
			if (isHcpa){
				AghParametros aghParametroUrlBaseWebForms = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
				if (aghParametroUrlBaseWebForms != null) {
					urlBaseWebForms = aghParametroUrlBaseWebForms.getVlrTexto();
				}
				AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void inicializaParametroCertificadoDigital() {
		if (paramCertificadoDigital == null) {
			try {
				paramCertificadoDigital = parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL)
						.getVlrTexto();
				if (StringUtils.isBlank(paramCertificadoDigital)) {
					// Assume como 'N' caso o parâmetro não esteja definido
					this.setParamCertificadoDigital("N");
				}
			} catch (ApplicationBusinessException e) {
				// Assume como 'N' caso o parâmetro nãonodoNivel3 exista
				this.setParamCertificadoDigital("N");
			}
		}
	}
	
	private void inicializaParametroDigitalizacao() {
		try {
			String paramAtivaDigitalizacaoPOLString = parametroFacade.buscarAghParametro(
					AghuParametrosEnum.P_ATIVA_DIGITALIZACAO_POL).getVlrTexto();
			if (StringUtils.isBlank(paramAtivaDigitalizacaoPOLString)) {
				// Assume como 'N' caso o parâmetro não esteja definido
				this.paramAtivaDigitalizacaoPOL = false;
			} else {
				this.paramAtivaDigitalizacaoPOL = "S".equalsIgnoreCase(paramAtivaDigitalizacaoPOLString.trim()) ? true
						: false;
			}
		} catch (ApplicationBusinessException e) {
			// Assume como 'N' em caso de erro ao ler o parametro
			this.paramAtivaDigitalizacaoPOL = false;
			return;
		}
	}
		
	
	public Boolean validarUrlBaseWebFormsBanco(){
		return StringUtils.isBlank(urlBaseWebForms) || StringUtils.isBlank(banco);
	}
	
	//------[ABRIR PÁGINAS DO POL]
	public String openPage(){
		if (modoPesquisaPOL || pacientesMap.isEmpty() || nodoSelecionado==null){
			return contextPath.concat("/pages/paciente/prontuarioonline/pesquisarPaciente.xhtml");
		}else if(nodoSelecionado!=null){
			FacesContext context = FacesContext.getCurrentInstance();
			ConfigurableNavigationHandler configNavHandler = (ConfigurableNavigationHandler)context.getApplication().getNavigationHandler(); 
			NavigationCase navCase = configNavHandler.getNavigationCase(context,null,nodoSelecionado.getPagina());
			
			return contextPath.concat(navCase.getToViewId(context));
		}else{
			return contextPath.concat("/pages/paciente/prontuarioonline/consultarDadosPaciente.xhtml");
		}
	}

	public String abrirPesquisaAvancada(){
		return PESQUISA_AVANCADA;
	}
	
	//------[INJETAR O NODO EM OUTRAS TELAS]
	@Produces @RequestScoped @SelectionQualifier
	public NodoPOLVO obterNodoSelecionado(){
		return nodoSelecionado;
	}

	//------[ADICIONA PACIENTES AO POL]
	public void addPOLPacienteMenu(){
		modoPesquisaPOL=false;
		addPOLPaciente();
	}
	
	public void addPOLPaciente(){
		AipPacientes paciente = null;
		nodoSelecionado=null;
		
		if(!redirecionaAghWeb){
			
			try {
				if (!pacientesMap.containsKey(prontuario)){
					if (prontuario!=null){
						paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
					} else if(codigo!=null){
						paciente = pacienteFacade.obterPacientePorCodigo(codigo);
					}
					
					if (paciente==null){
						pesquisaPOL();
						throw new ApplicationBusinessException("AIP_PACIENTE_NAO_ENCONTRADO", Severity.ERROR);
					}	
					
					verificarPermissoes(paciente);
					verificarPermissaoPOLUBS(paciente);
					if (nodoOriginalList==null || nodoOriginalList.isEmpty()){
						nodoOriginalList = aghuFacade.recuperarAghNodoPolPorOrdem(DominioNodoPOL.getArrayTipo(true));					
					}
					
					List<NodoPOLVO> polItems = prontuarioOnlineFacade.montarListaPOL(paciente.getProntuario(), paciente.getCodigo(), nodoOriginalList, parametros, paciente.getDtObito()!=null);
					TreeNode root = createRoot(polItems, paciente);
					pacienteList.add(paciente);
					pacientesMap.put(paciente.getProntuario(), root);
					
					if (!modoPesquisaPOL){
						if (chamadaExames) {
							nodoSelecionado = polItems.get(NODO_DADOS_EXAMES);
						} else {
							if (descricaoExpandedNode == null) {
								nodoSelecionado = polItems.get(NODO_DADOS_PACIENTES);
							}
						}
						activeTab=pacienteList.size()-1;
											
					}
					
					if ((!modoPesquisaPOL && pacienteList.size()==1) || chamadaForaPOL){
						chamadaForaPOL=false;
						if (chamadaExames) {
							RequestContext.getCurrentInstance().execute("window.parent.tab.openPOL(true);");
						} else {
							RequestContext.getCurrentInstance().execute("tab.openPOL(true);");
							
						}
					}
				}else if(pacientesMap.size()>=NUMERO_MAXIMO_PRONTUARIOS){
					throw new ApplicationBusinessException("MSG_EXCEDEU_PRONTUARIOS_ABERTOS", Severity.ERROR, NUMERO_MAXIMO_PRONTUARIOS);
				}else{
					throw new ApplicationBusinessException("MSG_PRONTUARIO_JA_ABERTO", Severity.INFO, prontuario.toString());
				}
				
			} catch (BaseListException e){
				apresentarExcecaoNegocio(e);
			}	catch (BaseException e){
				apresentarExcecaoNegocio(e);
			}	
			prontuario=null;
			codigo=null;
			
		} else {	
			
			String fnc;
			if(modoPesquisaPOL){
				fnc = "parent.parent.pol_aghweb('"+urlBaseWebForms+SEPARADOR+prontuario+SEPARADOR+ obterTokenUsuarioLogado()+ SEPARADOR+banco+"');" ;

			} else {
				fnc = "pol_aghweb('"+urlBaseWebForms+SEPARADOR+prontuario+SEPARADOR+ obterTokenUsuarioLogado()+ SEPARADOR+banco+"'); " ;
				prontuario = null;
			}
			apresentarMsgNegocio("ABRIR_PAGINA_AGHWEB");
			RequestContext.getCurrentInstance().execute(fnc);
		}
	}
	
	//------[ABRIR PESQUISA AVANÇADA DO POL]	
	public void pesquisaPOL(){
		if (pacientesMap.isEmpty()){
			modoPesquisaPOL=true;
		}
		if (modoPesquisaPOL){
			nodoSelecionado=null;
		}
	}
	
	
	//------[EXCLUIR PACIENTES DO POL]	
	public void limparTudo(){
		nodoSelecionado=null;
		descricaoExpandedNode = null;
		prontuario=null;
		pacientesMap=new LinkedHashMap<>();
		pacienteList=new ArrayList<AipPacientes>();
		nodoOriginalList=new ArrayList<AghNodoPol>();
		activeTab=0;
		modoPesquisaPOL=false;
		chamadaExames=false;
	}	
	
	
	//------[SEGURANÇA POL]
	private void verificarPermissoes(AipPacientes paciente) throws BaseListException, ApplicationBusinessException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		}catch(UnknownHostException e){
			throw new ApplicationBusinessException("AFA_01454", Severity.ERROR);
		}
		
		if (parametros.isEmpty()){
			//--[CONFIGURA SEGURANCA]
			parametros.put(ParametrosPOLEnum.ACESSO_LIVRE_POL, securityController.usuarioTemPermissao("acessoLivrePOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_COMISSAO_POL, securityController.usuarioTemPermissao("acessoComissaoPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_ESPECIAL_POL, securityController.usuarioTemPermissao("acessoEspecialPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_ADMIN_POL, securityController.usuarioTemPermissao("acessoAdminPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_MONITOR_PROJ_PESQ_POL, securityController.usuarioTemPermissao("acessoMonitorProjPesqPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_PESQUISADOR_PROJ_POL, securityController.usuarioTemPermissao("acessoPesquisadorProjPesqPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_VER_FLUXOGRAMA, securityController.usuarioTemPermissao("verFluxograma", "pesquisar", false));
			parametros.put(ParametrosPOLEnum.DOCUMENTOS_CERTIFICADOS_POL, securityController.usuarioTemPermissao("documentosCertificadosPOL", "pesquisar", false));	
			parametros.put(ParametrosPOLEnum.PAGINA_ORIGEM, pagOrigem);
			
			// Permissoes-digitalizacao: Merge #158568
			parametros.put(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_AMB_POL, securityController.usuarioTemPermissao("acessoDocsDigitalizadosAmbPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_EME_POL, securityController.usuarioTemPermissao("acessoDocsDigitalizadosEmePOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_EXA_POL, securityController.usuarioTemPermissao("acessoDocsDigitalizadosExaPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_INT_POL, securityController.usuarioTemPermissao("acessoDocsDigitalizadosIntPOL", ACESSAR, false));
			parametros.put(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_INATIVOS, securityController.usuarioTemPermissao("acessoDocsDigitalizadosInativosPOL", ACESSAR, false));
			
			/*
			 * Permissoes-digitalizacao: Merge #158568
			 * Mudanças:
			 * 1. O tratamento do parâmetro paramAtivaDigitalizacaoPOL é populado pelo método inicializaParametroDigitalizacao (chamado no início da conversação)
			 * 2. O paramAtivaDigitalizacaoPOL é repassado para AghNodoPolON que é responsável pela construção dos nodos da POL.
			 */
			parametros.put(ParametrosPOLEnum.ATIVA_DIGITALIZACAO_POL, paramAtivaDigitalizacaoPOL);
			parametros.put(ParametrosPOLEnum.ATIVA_CERTIFICACAO_DIGITAL_POL, ("S".equalsIgnoreCase(paramCertificadoDigital)? true : false));
			
			
		}
		
		Short processoConsultaPOL = getParametroProcessoConsultaPOL();
			
		if(processoConsultaPOL != null){
			prontuarioOnlineFacade.validarPermissoesPOL(paciente, parametros, nomeMicrocomputador, processoConsultaPOL);
		}
	}	
	
	
	/**
	 * Verifica se o acesso está sendo feito pelo perfil ESP08, caso em que deverá ser testado o microcomputador e o paciente (ambos devem ser da UBS)
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	private void verificarPermissaoPOLUBS(AipPacientes paciente) throws ApplicationBusinessException{
		try {
			String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			prontuarioOnlineFacade.verificarPerfilUBS(this.obterLoginUsuarioLogado(), nomeMicrocomputador, paciente);
		} catch (UnknownHostException e) {
			throw new ApplicationBusinessException("AFA_01454", Severity.ERROR);
		}
	}
	
	private Short getParametroProcessoConsultaPOL() throws ApplicationBusinessException {
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_CONS_POL);
		if (parametro != null && parametro.getVlrNumerico() != null) {
			return Short.valueOf(parametro.getVlrNumerico().toString());
		} else {
			throw new ApplicationBusinessException("PARAMETRO_INVALIDO", Severity.ERROR);
		}
	}	
	
	
	//------[EVENTOS POL]	
	public void onNodeExpand(NodeExpandEvent event) {
		try{
			TreeNode node = event.getTreeNode();
			NodoPOLVO polvo = (NodoPOLVO) node.getData();
			int count = polvo.getNodos().size();
			prontuarioOnlineFacade.expandirNodosPOL(polvo, parametros);
			if (polvo.getNodos().size() > count){
				if (!polvo.getNodos().get(0).getAtivo()){
					node.getChildren().remove(0);
					polvo.getNodos().remove(0);
				}	
				for (NodoPOLVO nodo : polvo.getNodos()){
					recursiveNodo(nodo, node, polvo.getNomePaciente());
				}	
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}					
	}

    public void onTabChange(TabChangeEvent event) {
		modoPesquisaPOL=false;

        Integer pmap = Integer.valueOf(event.getTab().getTitle().split("-")[0].trim());
		nodoSelecionado = (NodoPOLVO) pacientesMap.get(pmap).getChildren().get(NODO_DADOS_PACIENTES).getData();
    }
    
    public void onTabClose(TabCloseEvent event) {
		modoPesquisaPOL=false;
    	
		nodoSelecionado = null;
    }
	
	
	//------[CRIA COMPONENTES JSF]	
	
	// ------[GETTERS AND SETTERS]
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public List<AghNodoPol> getNodoOriginalList() {
		return nodoOriginalList;
	}

	public void setNodoOriginalList(List<AghNodoPol> nodoOriginalList) {
		this.nodoOriginalList = nodoOriginalList;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public NodoPOLVO getNodoSelecionado() {
		return nodoSelecionado;
	}

	public void setNodoSelecionado(NodoPOLVO nodoSelecionado) {
		modoPesquisaPOL=false;
		this.nodoSelecionado = nodoSelecionado;
	}

	public void setParamAtivaDigitalizacaoPOL(Boolean paramAtivaDigitalizacaoPOL) {
		this.paramAtivaDigitalizacaoPOL = paramAtivaDigitalizacaoPOL;
	}

	public Boolean getParamAtivaDigitalizacaoPOL() {
		return paramAtivaDigitalizacaoPOL;
	}

	/*public void setParamRedirecionaAghWeb(String paramRedirecionaAghWeb) {
		this.paramRedirecionaAghWeb = paramRedirecionaAghWeb;
	}*/

	public String getParamCertificadoDigital() {
		return paramCertificadoDigital;
	}

	public void setParamCertificadoDigital(String paramCertificadoDigital) {
		this.paramCertificadoDigital = paramCertificadoDigital;
	}

	public Boolean getRedirecionaAghWeb() {
		return redirecionaAghWeb;
	}

	public void setRedirecionaAghWeb(Boolean redirecionaAghWeb) {
		this.redirecionaAghWeb = redirecionaAghWeb;
	}

	public Map<Integer, TreeNode> getPacientesMap() {
		return pacientesMap;
	}

	public void setPacientesMap(Map<Integer, TreeNode> pacientesMap) {
		this.pacientesMap = pacientesMap;
	}

	public List<AipPacientes> getPacienteList() {
		return pacienteList;
	}

	public void setPacienteList(List<AipPacientes> pacienteList) {
		this.pacienteList = pacienteList;
	}

	public Boolean getModoPesquisaPOL() {
		return modoPesquisaPOL;
	}

	public void setModoPesquisaPOL(Boolean modoPesquisaPOL) {
		this.modoPesquisaPOL = modoPesquisaPOL;
	}

	public Integer getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(Integer activeTab) {
		this.activeTab = activeTab;
	}

	public String getPagOrigem() {
		return pagOrigem;
	}

	public void setPagOrigem(String pagOrigem) {
		this.pagOrigem = pagOrigem;
	}

	public Boolean getHabilitarPol() {
		return habilitarPol;
	}

	public void setHabilitarPol(Boolean habilitarPol) {
		this.habilitarPol = habilitarPol;
	}
	
	public AipPacientes getPacienteRemove() {
		return pacienteRemove;
	}

	public void setPacienteRemove(AipPacientes pacienteRemove) {
		this.pacienteRemove = pacienteRemove;
	}

	public Boolean getChamadaForaPOL() {
		return chamadaForaPOL;
	}

	public void setChamadaForaPOL(Boolean chamadaForaPOL) {
		this.chamadaForaPOL = chamadaForaPOL;
	}

	public String getDescricaoExpandedNode() {
		return descricaoExpandedNode;
	}

	public void setDescricaoExpandedNode(String descricaoExpandedNode) {
		this.descricaoExpandedNode = descricaoExpandedNode;
	}

	public Boolean getChamadaExames() {
		return chamadaExames;
	}

	public void setChamadaExames(Boolean chamadaExames) {
		this.chamadaExames = chamadaExames;
	}

	
}