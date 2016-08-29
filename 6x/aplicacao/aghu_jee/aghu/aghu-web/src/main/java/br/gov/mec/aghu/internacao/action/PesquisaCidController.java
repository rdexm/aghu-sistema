package br.gov.mec.aghu.internacao.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.ambulatorio.action.CadastroAtestadoFgtsPisPasepAmbulatorioController;
import br.gov.mec.aghu.ambulatorio.action.CadastroComparecimentoAmbulatorioController;
import br.gov.mec.aghu.blococirurgico.action.DescricaoCirurgicaDiagnosticoController;
import br.gov.mec.aghu.blococirurgico.action.procdiagterap.DescricaoProcDiagTerapController;
import br.gov.mec.aghu.blococirurgico.action.procdiagterap.DescricaoProcDiagTerapResultadoController;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.patologia.action.CadastroLaudoUnicoController;
import br.gov.mec.aghu.exames.patologia.action.LaudoUnicoController;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.CapituloCidVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.Cid1VO;
import br.gov.mec.aghu.internacao.pesquisa.vo.Cid2VO;
import br.gov.mec.aghu.internacao.pesquisa.vo.GrupoCidVO;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.prescricaomedica.action.ManterDiagnosticoPacienteCtiController;
import br.gov.mec.aghu.transplante.action.CriteriosPriorizacaoAtendimentoController;
import br.gov.mec.aghu.transplante.action.CriteriosPriorizacaoAtendimentoPaginatorController;
import br.gov.mec.aghu.prescricaomedica.action.CadastroAtestadoFgtsPisPasepController;
import br.gov.mec.aghu.prescricaomedica.action.CadastroComparecimentoController;
import br.gov.mec.aghu.transplante.action.IncluirPacienteListaTransplanteTMOController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaCidController extends ActionController {

	private static final long serialVersionUID = -7427634266641727480L;

	private static final String PAGE_CONFIRMADO_CID_USUAL_POR_UNIDADE = "prescricaomedica-manterCidUsualPorUnidade";
	private static final String PAGE_CADASTRO_INTERNACAO = "cadastroInternacao";
	private static final String PAGE_MANTER_DIAGNOSTICOS_PACIENTE = "prescricaomedica-manterDiagnosticosPaciente";
	private static final String PAGE_MANTER_DIAGNOSTICOS_ATENDIMENTO = "prescricaomedica-manterDiagnosticosAtendimento";
	private static final String PAGE_MANTER_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";
	private static final String PAGE_MANTER_SUMARIO_OBITO = "prescricaomedica-manterSumarioObito";
	private static final String PAGE_DESCRICAO_CIRURGICA = "blococirurgico-descricaoCirurgica";
	private static final String PAGE_DESCRICAO_CIRURGICA_PDT = "blococirurgico-descricaoProcDiagTerap";
	private static final String PAGE_INCLUIR_PACIENTE_LISTA_TRANSPLANTE_TMO = "transplante-incluirPacienteListaTransplanteTMOCRUD";
	private static final String PAGE_CRITERIOS_PRIORIZACAO_ATENDIMENTO_CRUD = "transplante-criteriosPriorizacaoAtendimentoCRUD";
	private static final String PAGE_CRITERIOS_PRIORIZACAO_ATENDIMENTO_LIST = "transplante-criteriosPriorizacaoAtendimentoList";
	private static final String PAGE_DIAGNOSTICO_PACIENTE_CTI =  "prescricaomedica-manterDiagnosticoPacienteCti"; 
	private static final String PAGE_ATESTADO_FGTS =  "prescricaomedica-cadastroComparecimento";
	private static final String PAGE_ATESTADO_FGTS_AMBULATORIO =  "ambulatorio-cadastroComparecimentoAmbulatorio";
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICidFacade cidFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	//private static final Log LOG = LogFactory.getLog(PesquisaCidController.class);

	private Integer seqCapituloCid;
	private Integer seqGrupoCid;

	private boolean fromPageManterCidUsualPorUnidade = false;
	private boolean fromPageManterDiagnosticos = false;
	private boolean fromPageCadastroInternacao = false;	
	private boolean fromPageDescCirurPdtAbaResultado = false;
	private boolean fromPageManterDiagnosticosPaciente = false;
	private boolean fromPageDiagnosticolaudos = false;
	private boolean fromPageManterSumarioAlta = false;
	private boolean fromPageManterSumarioObito = false;
	private boolean fromPageDescricaoCirurgica = false;
	private boolean fromPageDescricaoCirurgicaPDT = false;
	private boolean fromPageDiagnosticoPacienteCti = false;
	private boolean fromPageAtestadoFgts = false;
	private boolean fromPageAtestadoFgtsAmbulatorio = false;
	private boolean fromPageResultadoDescricao = false;
	
	private boolean parametroDescricaoCirPosPre = false;

	// @Out(required = false)
	// @In(required = false)
	private Boolean fromPageObitoCausasDiretas = false;

	// @Out(required = false)
	// @In(required = false)
	private Boolean fromPageObitoCausaAntecedentes = false;

	// @Out(required = false)
	// @In(required = false)
	private Boolean fromPageObitoOutrasCausas = false;

	// @Out(required = false)
	// @In(required = false)
	private Integer currentSliderInformacoesObito; // Slider corrente da aba
	// "Informações de Óbito"
	private Integer seqCid;
	private Integer codigoPaciente;
	

	// @Out(required = false)
	private AghCid aghCid = null;	

	private List<CapituloCidVO> capituloCidList = new ArrayList<CapituloCidVO>();

	private String tipoSumario;

	private Integer currentTabIndex;
	
	private String voltarParaUrl;

	private boolean limpar;
	
	private String telaAnterior;
	
	//retorno tela diagnostico-emergencia
	private Integer seqDiagnostico;
	private boolean emergencia = false;
	
	@Inject
	private CadastroLaudoUnicoController cadastroLaudoUnicoController;
	
	@Inject
	private LaudoUnicoController laudoUnicoController;
	
	@Inject
	private DescricaoCirurgicaDiagnosticoController descricaoCirurgicaDiagnosticoController;
	
	@Inject
	private IncluirPacienteListaTransplanteTMOController incluirPacienteListaTransplanteTMOController;
	
	@Inject
	private CriteriosPriorizacaoAtendimentoPaginatorController criteriosPriorizacaoAtendimentoPaginatorController;
	
	@Inject
	private CriteriosPriorizacaoAtendimentoController criteriosPriorizacaoAtendimentoController;
	
	@Inject
	private ManterDiagnosticoPacienteCtiController manterDiagnosticoPacienteCtiController;
	
	@Inject
	private CadastroAtestadoFgtsPisPasepController cadastroAtestadoFgtsPisPasepController;	
	
	@Inject
	private CadastroAtestadoFgtsPisPasepAmbulatorioController cadastroAtestadoFgtsPisPasepAmbulatorioController;	
	
	@Inject
	private CadastroComparecimentoController cadastroComparecimentoController;
	
	@Inject
	private CadastroComparecimentoAmbulatorioController cadastroComparecimentoAmbulatorioController;
	
	@Inject
	private DescricaoProcDiagTerapController descricaoProcDiagTerapController;
	
	@Inject
	private DescricaoProcDiagTerapResultadoController descricaoProcDiagTerapResultadoController;
	
	private TreeNode root;
	
	private TreeNode selectedNode;
	
	private TreeNode nodoGrupo;
	private TreeNode nodoCid;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);	
		
		if(StringUtils.isNotBlank(getRequestParameter("emergencia"))){
			carregaParamentrosEmergencia();
		}
	}
	
	private void carregaParamentrosEmergencia() {
		
		if(StringUtils.isNotBlank(getRequestParameter("emergencia"))){
			this.setEmergencia(Boolean.valueOf(this.getRequestParameter("emergencia")));
		}
		
		if(StringUtils.isNotBlank(getRequestParameter("seqDiagnostico"))){
			this.setSeqDiagnostico(Integer.valueOf(this.getRequestParameter("seqDiagnostico")));
		}
		
		if(StringUtils.isNotBlank(getRequestParameter("telaOrigem"))){
			this.setVoltarParaUrl(this.getRequestParameter("telaOrigem"));
		}
		

		this.codigoPaciente = 0;
	}

	public void onNodeExpand(NodeExpandEvent event) {  
	       	        
	        TreeNode nodo = event.getTreeNode();
	        
	        if (nodo == null) {
				return;
			}	
	        
	        Object no = nodo.getData();
	      
	        if (nodo.getType().equals("capituloCid")){	        
				CapituloCidVO capituloCidVO = (CapituloCidVO) no;
				this.seqCapituloCid = capituloCidVO.getCapituloCid().getSeq();				
	        	this.detalharNodoGrupoCid(nodo.getChildren());
	        }else if (nodo.getType().equals("grupoCid")) {				
					GrupoCidVO grupoCidVO = (GrupoCidVO) no;
					this.seqGrupoCid = grupoCidVO.getGrupoCid().getId()
							.getSeq();
					this.detalharNodoCid1(nodo);				

			}	       
	}
	
	public void onNodeSelect(NodeSelectEvent event){
		  TreeNode nodo = event.getTreeNode();
		  if (nodo == null) {
				return;
		  }	
	        
	       Object no = nodo.getData();
	       
	       if (nodo.getType().equals("cid1")){	        
				Cid1VO cid = (Cid1VO) no;				
				this.selecionarCidArvore(cid.getCid1().getSeq(), this.codigoPaciente);
				
	        }else if (nodo.getType().equals("cid2")) {	
	        	Cid2VO cid = (Cid2VO) no;	        	
	        	String retorno = this.selecionarCidArvoreEConfirmar(cid.getCid2().getSeq(), this.codigoPaciente);
	        	NavigationHandler navigationHandler = FacesContext.
	               getCurrentInstance().getApplication().getNavigationHandler();
	        	navigationHandler.handleNavigation(FacesContext.getCurrentInstance(), null, retorno);
			  }      
        
    }
	
	@Produces
	@RequestScoped
	@SelectionQualifier
	public PesquisaCidVO obterPacienteSelecionado() {
		PesquisaCidVO vo = new PesquisaCidVO();
		if (this.aghCid != null) {
			vo.setCid(this.aghCid);
			vo.setCodigoPaciente(this.codigoPaciente);
		}
		if (Boolean.TRUE.equals(this.fromPageCadastroInternacao)) {
			vo.setRetornouTelaAssociada(true);
		}
		return vo;
	}

	public void iniciar() { 


		if (this.fromPageCadastroInternacao
				|| this.fromPageManterCidUsualPorUnidade
				|| this.fromPageManterDiagnosticosPaciente
				|| this.fromPageDescricaoCirurgica
				|| this.fromPageManterDiagnosticos
				|| this.fromPageDiagnosticoPacienteCti) {
			this.seqCid = null;
			this.aghCid = null;
			capituloCidList = new ArrayList<CapituloCidVO>();
		}

		if (this.seqCid == null) {
			this.aghCid = null;
		} else {
			// seqCid é passado por parametro para a tela cadastroInternacao
			this.seqCid = this.aghCid == null ? null : this.aghCid.getSeq();
		}

		 
		
		if (this.capituloCidList == null || this.capituloCidList.size() == 0) {
			root = new DefaultTreeNode("Root", null); 			
			this.detalharNodoCapituloCid();
		}
	
	}
	

	/* METODOS PARA SUGGESTION */
	public List<AghCid> pesquisarCids(String param) {
		try {
			return this.aghuFacade
					.pesquisarCidsSemSubCategoriaPorDescricaoOuId(
							param, Integer.valueOf(300));
		} catch (ApplicationBusinessException ane) {
			this.apresentarExcecaoNegocio(ane);
			return null;
		}
	}

	public void atribuirCid(AghCid cid) {
		this.aghCid = cid;
	}

	/* -- FIM SUGGESTION -- */

	/* METODOS PARA TREEVIEW */
	public void selecionarCidArvore(final Integer seqCid,
			final Integer codigoPaciente) {
		this.seqCid = seqCid;
		this.codigoPaciente = codigoPaciente;
		this.aghCid = this.aghuFacade.obterCid(this.seqCid);
		
	}
	

	public void detalharNodoCapituloCid() {
		List<AghCapitulosCid> capitulosCid = this.cidFacade
				.pesquisarCapitulosCidsAtivo();
		this.capituloCidList.clear();

		CapituloCidVO capituloCidVO = null;
		for (AghCapitulosCid capituloCid : capitulosCid) {
			capituloCidVO = new CapituloCidVO();
			capituloCidVO.setCapituloCid(capituloCid);
			
			TreeNode nodoCapitulo = new DefaultTreeNode("capituloCid", capituloCidVO , root);
			
			capituloCidVO.setGrupoCidList(this.pesquisarGrupoCid(capituloCid
					.getSeq(), nodoCapitulo));
			this.capituloCidList.add(capituloCidVO);			
			
		}		
	}

	/**
	 * Método para buscar lista de grupoCid de um capituloCid e retornar essa
	 * coleção convertida em List<GrupoCidVO>.
	 * 
	 * @param seqCapituloCid
	 * @return
	 */
	private List<GrupoCidVO> pesquisarGrupoCid(Integer seqCapituloCid , TreeNode nodoCapitulo) {
		List<AghGrupoCids> gruposCid = this.cidFacade
				.pesquisarGrupoCidPorCapituloCid(seqCapituloCid);

		// Cria lista de VOs para grupoCid
		List<GrupoCidVO> gruposCidVOList = new ArrayList<GrupoCidVO>();
		GrupoCidVO grupoCidVO = null;
		for (AghGrupoCids grupoCid : gruposCid) {
			grupoCidVO = new GrupoCidVO();
			grupoCidVO.setGrupoCid(grupoCid);
			gruposCidVOList.add(grupoCidVO);
			setNodoGrupo(new DefaultTreeNode("grupoCid", grupoCidVO , nodoCapitulo));
		}

		return gruposCidVOList.size() == 0 ? null : gruposCidVOList;
	}

	/**
	 * Método para buscar o AghCapituloCid selecionado na árvore e atribuir a
	 * ele os elementos AghGruposCid correspondentes.
	 */
	public void detalharNodoGrupoCid(List<TreeNode> grupos) {

			for (Iterator<TreeNode> iterator = grupos.iterator();iterator.hasNext();) { 
					TreeNode nodoGrupo = iterator.next();
					GrupoCidVO grupoCidVO = (GrupoCidVO)  nodoGrupo.getData();					
					grupoCidVO.setCid1List(this.pesquisarCid1(grupoCidVO
							.getGrupoCid().getId().getSeq(), nodoGrupo));
					
				}
	}

	private List<Cid1VO> pesquisarCid1(Integer seqGrupoCid, TreeNode nodoGrupo) {
		List<AghCid> cids = this.aghuFacade.pesquisarCidsPorGrupo(seqGrupoCid);

		// Cria lista de VOs para grupoCid
		List<Cid1VO> cidVOList = new ArrayList<Cid1VO>();
		Cid1VO cid1VO = null;
		for (AghCid cid : cids) {
			cid1VO = new Cid1VO();
			cid1VO.setCid1(cid);
			cidVOList.add(cid1VO);
			setNodoCid(new DefaultTreeNode("cid1", cid1VO , nodoGrupo));
		}

		return cidVOList.size() == 0 ? null : cidVOList;
	}

	/**
	 * Método para buscar o AghCapituloCid e AghGrupoCid selecionados na árvore
	 * e atribuir a eles os elementos AghCids correspondentes.
	 */
	public void detalharNodoCid1(TreeNode nodoGrupo) {
		
		List<TreeNode> cids =	nodoGrupo.getChildren();

		if (nodoGrupo.getChildren().size() == 0 &&
			nodoGrupo.getData() != null){
			Cid2VO cidVO = (Cid2VO)  nodoGrupo.getData();				
			this.pesquisarCid2(cidVO.getCid2().getSeq(), nodoGrupo);
			
		}
		else {
			for (Iterator<TreeNode> iterator = cids.iterator();iterator.hasNext();) { 
				TreeNode nodoCid = iterator.next();
				if (nodoCid.getType().equals("cid1")){
					Cid1VO cidVO = (Cid1VO)  nodoCid.getData();				
					this.pesquisarCid2(cidVO.getCid1().getSeq(), nodoCid);
				}
				
			}  
		}		
	}

	private List<Cid2VO> pesquisarCid2(Integer seqCid, TreeNode nodoCid) {
		List<AghCid> cids = this.aghuFacade.pesquisarCidsRelacionados(seqCid);

		// Cria lista de VOs para grupoCid
		List<Cid2VO> cidVOList = new ArrayList<Cid2VO>();
		Cid2VO cid2VO = null;
		for (AghCid cid : cids) {
			cid2VO = new Cid2VO();
			cid2VO.setCid2(cid);
			cidVOList.add(cid2VO);
			setNodoCid(new DefaultTreeNode("cid2", cid2VO , nodoCid));			
		}

		return cidVOList.size() == 0 ? null : cidVOList;
	}	

	/* -- FIM TREEVIEW -- */

	public String confirmar() {
		if (this.aghCid == null) {
			this.seqCid = null;
			this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_CID_NAO_SELECIONADO");
			return null;
		} else {
			this.seqCid = this.aghCid.getSeq();

			// Verifica se existem cids em subniveis do cid escolhido (isso só é
			// possível se um cid for escolhido via suggestion)
			List<AghCid> cidList = this.aghuFacade
					.pesquisarCidsRelacionados(this.aghCid.getSeq());
			
			if(cidList == null || cidList.size() == 0 && emergencia) {
				return "";
			}
			
			if (cidList == null || cidList.size() == 0) {              
				
				if (this.fromPageManterDiagnosticos) {
					return PAGE_MANTER_DIAGNOSTICOS_ATENDIMENTO;
				} else if (this.fromPageCadastroInternacao) {
					return PAGE_CADASTRO_INTERNACAO;
				} else if (this.fromPageDescCirurPdtAbaResultado) {
					return "fromPageDescCirurPdtAbaResultado";
				} else if (this.fromPageManterCidUsualPorUnidade) {
					// TODO Migração Pendência: As demais migrações devem
					// utilizar a navegação conforme a constante
					// PAGE_CONFIRMADO_CID_USUAL_POR_UNIDADE
					return PAGE_CONFIRMADO_CID_USUAL_POR_UNIDADE;
				} else if (this.fromPageManterDiagnosticosPaciente) {
					return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
					/*
					 * } else if (this.getFromPageObitoCausasDiretas() != null
					 * && this.getFromPageObitoCausasDiretas()) {
					 * this.setFromPageObitoCausasDiretas(false);
					 * currentSliderInformacoesObito = 0; return
					 * "confirmadoObitoInformacoesObito"; } else if
					 * (this.fromPageObitoCausaAntecedentes != null &&
					 * this.fromPageObitoCausaAntecedentes) {
					 * this.fromPageObitoCausaAntecedentes = false;
					 * currentSliderInformacoesObito = 1; return
					 * "confirmadoObitoInformacoesObito"; } else if
					 * (this.getFromPageObitoOutrasCausas() != null &&
					 * this.getFromPageObitoOutrasCausas()) {
					 * this.setFromPageObitoOutrasCausas(false);
					 * currentSliderInformacoesObito = 2; return
					 * "confirmadoObitoInformacoesObito"; } else if (tipoSumario
					 * != null && tipoSumario.equals("OBITO")) { return
					 * "confirmadoObito"; } else if (tipoSumario != null &&
					 * tipoSumario.equals("ANTECIPAR SUMARIO")) { return
					 * "confirmadoAnteciparSumario"; } else if
					 * (this.fromPageDiagnosticolaudos) { return
					 * "redirecionarDiagnosticoLaudos";
					 */
				} else if (this.fromPageManterSumarioAlta) {
					return PAGE_MANTER_SUMARIO_ALTA;
				} else if (this.fromPageManterSumarioObito) {
					return PAGE_MANTER_SUMARIO_OBITO;
				}else if(this.voltarParaUrl != null){
					return "voltarPara";
				}	
				else if (this.fromPageDiagnosticolaudos) {
					cadastroLaudoUnicoController.setSeqCid(seqCid);
					laudoUnicoController.getTelaVo().setSelectedTab(1);
					
					this.aghCid = null;   
					this.seqCid = null;
					this.capituloCidList.clear();
					
					return "exames-laudounico";
				}else if (this.fromPageDescricaoCirurgica) {
					if(!this.parametroDescricaoCirPosPre){
						descricaoCirurgicaDiagnosticoController.setCidPre(this.aghuFacade.obterCid(this.seqCid));		
						descricaoCirurgicaDiagnosticoController.salvarCidPre();
					} else {
						descricaoCirurgicaDiagnosticoController.setCidPos(this.aghuFacade.obterCid(this.seqCid));
						descricaoCirurgicaDiagnosticoController.salvarCidPos();
					}
					
					return PAGE_DESCRICAO_CIRURGICA;					
				}else if(this.fromPageDiagnosticoPacienteCti){
					manterDiagnosticoPacienteCtiController.setAghCid(aghCid);
					return PAGE_DIAGNOSTICO_PACIENTE_CTI;
				}else if(this.fromPageAtestadoFgts){
					cadastroAtestadoFgtsPisPasepController.getMamAtestado().setAghCid(aghCid);
					cadastroComparecimentoController.setIndexSelecionado(2);
					cadastroComparecimentoController.setBuscaArvoreCid(true);
					return PAGE_ATESTADO_FGTS;
				} else if (this.fromPageDescricaoCirurgicaPDT) {
					descricaoProcDiagTerapResultadoController.setCid(aghCid);
					descricaoProcDiagTerapResultadoController.gravar();
					descricaoProcDiagTerapController.setAbaSelecionada(5);
					return PAGE_DESCRICAO_CIRURGICA_PDT;
				} else if(this.fromPageAtestadoFgtsAmbulatorio){
					cadastroAtestadoFgtsPisPasepAmbulatorioController.getMamAtestado().setAghCid(aghCid);
					cadastroComparecimentoAmbulatorioController.setIndexSelecionado(3);
					cadastroComparecimentoAmbulatorioController.setBuscaArvoreCid(true);
					return PAGE_ATESTADO_FGTS_AMBULATORIO;
				}
				
				return null;
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_CID_SELECIONADO_COM_SUBNIVEIS");
				return null;
			}
		}
	}

	public String cancelar() {	
			
		if (this.fromPageManterSumarioObito) {
			return PAGE_MANTER_SUMARIO_OBITO;
		} else if (tipoSumario != null && tipoSumario.equals("OBITO")) {
			return "voltarParaObito";
		} else if (this.fromPageManterSumarioAlta) {
			return PAGE_MANTER_SUMARIO_ALTA;
		} else if (this.fromPageDiagnosticolaudos) {
			laudoUnicoController.getTelaVo().setSelectedTab(1);
			return "exames-laudounico";
	    } else if (this.fromPageDescricaoCirurgica) {
	    	return PAGE_DESCRICAO_CIRURGICA;
	    } else if (this.fromPageDescricaoCirurgicaPDT){
			descricaoProcDiagTerapController.setAbaSelecionada(5);
	    	return PAGE_DESCRICAO_CIRURGICA_PDT;
	    } else if (this.fromPageDiagnosticoPacienteCti){
	    	return PAGE_DIAGNOSTICO_PACIENTE_CTI;
	    } else if(this.voltarParaUrl != null){
			return "voltarPara";
		} else{
			return "voltarParaAlta";
		}
	}

	// public String selecionarCidArvoreEConfirmar() {
	// this.aghCid = this.aghuFacade.obterCid(this.seqCid);
	// return this.confirmar();
	// }

	public String selecionarCidArvoreEConfirmar(final Integer seqCid,
			final Integer codigoPaciente) {
		this.seqCid = seqCid;
		this.codigoPaciente = codigoPaciente;
		this.aghCid = this.aghuFacade.obterCid(this.seqCid);
		//return null;
		return this.confirmar();
	}

	/*
	 * Métodos do cancelar
	 */

	public String redirecionarCidUsualPorUnidade() {
		this.limparTela();
		return PAGE_CONFIRMADO_CID_USUAL_POR_UNIDADE;
	}

	public String redirecionarCadastroInternacao() {
		this.limparTela();
		return PAGE_CADASTRO_INTERNACAO;
	}

	public String redirecionarDiagnosticosPaciente() {
		this.limparTela();
		return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
	}

	public String redirecionarDiagnosticos() {
		this.limparTela();
		return PAGE_MANTER_DIAGNOSTICOS_ATENDIMENTO;
	}
	
	public String redirecionarAtestadoFgts() {
		this.limparTela();
		cadastroComparecimentoController.setIndexSelecionado(2);
		return PAGE_ATESTADO_FGTS;
	}
	
	public String redirecionarAtestadoFgtsAmbulatorio() {
		this.limparTela();
		cadastroComparecimentoAmbulatorioController.setIndexSelecionado(3);
		cadastroComparecimentoAmbulatorioController.setBuscaArvoreCid(true);
		return PAGE_ATESTADO_FGTS_AMBULATORIO;
	}	
	
	public String redirecionarLaudoUnico() {
		this.limparTela();
		laudoUnicoController.getTelaVo().setSelectedTab(1);
		return "exames-laudounico";
	}
	
	public String redirecionaPageDescCirurAbaDiagnostico(){
		this.limparTela();
		return PAGE_DESCRICAO_CIRURGICA;
	}

	public String redirecionaPageDescCirurPDT(){
		this.limparTela();
		return PAGE_DESCRICAO_CIRURGICA_PDT;
	}
	
	public String redirecionaPageDiagnosticoPacienteCti(){
		this.limparTela();
		return PAGE_DIAGNOSTICO_PACIENTE_CTI;
	}
	
	public void limparTela() {
		this.fromPageManterCidUsualPorUnidade = false;
		this.fromPageManterDiagnosticos = false;
		this.fromPageCadastroInternacao = false;
		this.fromPageDescricaoCirurgica = false;
		this.fromPageDescricaoCirurgicaPDT = false;
		this.fromPageDiagnosticoPacienteCti = false;
		this.parametroDescricaoCirPosPre = false;
		this.fromPageDescCirurPdtAbaResultado = false;
		this.fromPageManterDiagnosticosPaciente = false;
		this.fromPageDiagnosticolaudos = false;
		this.fromPageCadastroInternacao = false;
		this.fromPageObitoCausasDiretas = false;
		this.fromPageObitoCausaAntecedentes = false;
		this.fromPageObitoOutrasCausas = false;
		this.currentSliderInformacoesObito = null;
		this.seqCid = null;
		this.codigoPaciente = null;		
		this.aghCid = null;		
		this.capituloCidList = new ArrayList<CapituloCidVO>();
		this.tipoSumario = null;
		this.currentTabIndex = null;
		this.limpar = false;
		this.selectedNode = null;
		this.fromPageResultadoDescricao = false;
	}

	public String retornarCid(){

		if(telaAnterior != null){
			if(telaAnterior.equals(PAGE_CRITERIOS_PRIORIZACAO_ATENDIMENTO_CRUD)){
				criteriosPriorizacaoAtendimentoController.setCid(aghCid);
				return telaAnterior;
			}
			else if(telaAnterior.equals(PAGE_CRITERIOS_PRIORIZACAO_ATENDIMENTO_LIST)){
				criteriosPriorizacaoAtendimentoPaginatorController.setCid(aghCid);
				return telaAnterior;
			}
			else if(telaAnterior.equals(PAGE_INCLUIR_PACIENTE_LISTA_TRANSPLANTE_TMO)){
				incluirPacienteListaTransplanteTMOController.setAghCid(aghCid);
				return telaAnterior;
			}
	
		}
		return null;

	}
	
	// GETTERS E SETTERS
	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public Integer getSeqCapituloCid() {
		return this.seqCapituloCid;
	}

	public void setSeqCapituloCid(Integer seqCapituloCid) {
		this.seqCapituloCid = seqCapituloCid;
	}

	public Integer getSeqGrupoCid() {
		return this.seqGrupoCid;
	}

	public void setSeqGrupoCid(Integer seqGrupoCid) {
		this.seqGrupoCid = seqGrupoCid;
	}

	public List<CapituloCidVO> getCapituloCidList() {
		return this.capituloCidList;
	}

	public void setCapituloCidList(List<CapituloCidVO> capituloCidList) {
		this.capituloCidList = capituloCidList;
	}

	public Integer getSeqCid() {
		return this.seqCid;
	}

	public void setSeqCid(Integer seqCid) {
		this.seqCid = seqCid;
	}

	public Integer getCodigoPaciente() {
		return this.codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public void setFromPageManterDiagnosticos(boolean fromPageManterDiagnosticos) {
		this.fromPageManterDiagnosticos = fromPageManterDiagnosticos;
	}

	public boolean isFromPageManterDiagnosticos() {
		return this.fromPageManterDiagnosticos;
	}

	public boolean isFromPageCadastroInternacao() {
		return fromPageCadastroInternacao;
	}

	public void setFromPageCadastroInternacao(boolean fromPageCadastroInternacao) {
		this.fromPageCadastroInternacao = fromPageCadastroInternacao;
	}

	public void setFromPageManterDiagnosticosPaciente(
			boolean fromPageManterDiagnosticosPaciente) {
		this.fromPageManterDiagnosticosPaciente = fromPageManterDiagnosticosPaciente;
	}

	public boolean isFromPageManterCidUsualPorUnidade() {
		return fromPageManterCidUsualPorUnidade;
	}

	public void setFromPageManterCidUsualPorUnidade(
			boolean fromPageManterCidUsualPorUnidade) {
		this.fromPageManterCidUsualPorUnidade = fromPageManterCidUsualPorUnidade;
	}

	public boolean isFromPageManterDiagnosticosPaciente() {
		return fromPageManterDiagnosticosPaciente;
	}

	public boolean isFromPageObitoCausaAntecedentes() {
		return fromPageObitoCausaAntecedentes;
	}

	public void setFromPageObitoCausaAntecedentes(
			boolean fromPageObitoCausaAntecedentes) {
		this.fromPageObitoCausaAntecedentes = fromPageObitoCausaAntecedentes;
	}

	public Integer getCurrentSliderInformacoesObito() {
		return currentSliderInformacoesObito;
	}

	public void setCurrentSliderInformacoesObito(
			Integer currentSliderInformacoesObito) {
		this.currentSliderInformacoesObito = currentSliderInformacoesObito;
	}

	public void setFromPageObitoCausasDiretas(Boolean fromPageObitoCausasDiretas) {
		this.fromPageObitoCausasDiretas = fromPageObitoCausasDiretas;
	}

	public Boolean getFromPageObitoCausasDiretas() {
		return fromPageObitoCausasDiretas;
	}

	public void setFromPageObitoOutrasCausas(Boolean fromPageObitoOutrasCausas) {
		this.fromPageObitoOutrasCausas = fromPageObitoOutrasCausas;
	}

	public Boolean getFromPageObitoOutrasCausas() {
		return fromPageObitoOutrasCausas;
	}

	public String getTipoSumario() {
		return tipoSumario;
	}

	public void setTipoSumario(String tipoSumario) {
		this.tipoSumario = tipoSumario;
	}

	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}
	

	public boolean isFromPageDescCirurPdtAbaResultado() {
		return fromPageDescCirurPdtAbaResultado;
	}

	public void setFromPageDescCirurPdtAbaResultado(
			boolean fromPageDescCirurPdtAbaResultado) {
		this.fromPageDescCirurPdtAbaResultado = fromPageDescCirurPdtAbaResultado;
	}

	public boolean isFromPageDiagnosticolaudos() {
		return fromPageDiagnosticolaudos;
	}

	public void setFromPageDiagnosticolaudos(boolean fromPageDiagnosticolaudos) {
		this.fromPageDiagnosticolaudos = fromPageDiagnosticolaudos;
	}

	public boolean isLimpar() {
		return limpar;
	}

	public void setLimpar(boolean limpar) {
		this.limpar = limpar;
	}

	public boolean isFromPageManterSumarioAlta() {
		return fromPageManterSumarioAlta;
	}

	public void setFromPageManterSumarioAlta(boolean fromPageManterSumarioAlta) {
		this.fromPageManterSumarioAlta = fromPageManterSumarioAlta;
	}

	public boolean isFromPageManterSumarioObito() {
		return fromPageManterSumarioObito;
	}

	public void setFromPageManterSumarioObito(boolean fromPageManterSumarioObito) {
		this.fromPageManterSumarioObito = fromPageManterSumarioObito;
	}
	
	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public boolean isFromPageDescricaoCirurgica() {
		return fromPageDescricaoCirurgica;
	}

	public void setFromPageDescricaoCirurgica(boolean fromPageDescricaoCirurgica) {
		this.fromPageDescricaoCirurgica = fromPageDescricaoCirurgica;
	}

	public boolean isParametroDescricaoCirPosPre() {
		return parametroDescricaoCirPosPre;
	}

	public void setParametroDescricaoCirPosPre(boolean parametroDescricaoCirPosPre) {
		this.parametroDescricaoCirPosPre = parametroDescricaoCirPosPre;
	}

	public boolean isFromPageDescricaoCirurgicaPDT() {
		return fromPageDescricaoCirurgicaPDT;
	}

	public void setFromPageDescricaoCirurgicaPDT(boolean fromPageDescricaoCirurgicaPDT) {
		this.fromPageDescricaoCirurgicaPDT = fromPageDescricaoCirurgicaPDT;
	}
	
	public boolean isFromPageDiagnosticoPacienteCti() {
		return fromPageDiagnosticoPacienteCti;
	}
	
	public void setFromPageDiagnosticoPacienteCti(boolean fromPageDiagnosticoPacienteCti) {
		this.fromPageDiagnosticoPacienteCti = fromPageDiagnosticoPacienteCti;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public TreeNode getNodoGrupo() {
		return nodoGrupo;
	}

	public void setNodoGrupo(TreeNode nodoGrupo) {
		this.nodoGrupo = nodoGrupo;
	}

	public TreeNode getNodoCid() {
		return nodoCid;
	}

	public void setNodoCid(TreeNode nodoCid) {
		this.nodoCid = nodoCid;
	}
	
	public Integer getSeqDiagnostico() {
		return seqDiagnostico;
	}

	public void setSeqDiagnostico(Integer seqDiagnostico) {
		this.seqDiagnostico = seqDiagnostico;
	}

	public boolean isFromPageAtestadoFgts() {
		return fromPageAtestadoFgts;
	}

	public void setFromPageAtestadoFgts(boolean fromPageAtestadoFgts) {
		this.fromPageAtestadoFgts = fromPageAtestadoFgts;
	}	public boolean isEmergencia() {
		return emergencia;
	}

	public void setEmergencia(boolean emergencia) {
		this.emergencia = emergencia;
	}

	public String getTelaAnterior() {
		return telaAnterior;
	}

	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}

	public boolean isFromPageResultadoDescricao() {
		return fromPageResultadoDescricao;
	}

	public void setFromPageResultadoDescricao(boolean fromPageResultadoDescricao) {
		this.fromPageResultadoDescricao = fromPageResultadoDescricao;
	}
	
	public boolean isFromPageAtestadoFgtsAmbulatorio() {
		return fromPageAtestadoFgtsAmbulatorio;
	}

	public void setFromPageAtestadoFgtsAmbulatorio(
			boolean fromPageAtestadoFgtsAmbulatorio) {
		this.fromPageAtestadoFgtsAmbulatorio = fromPageAtestadoFgtsAmbulatorio;
	}
}
