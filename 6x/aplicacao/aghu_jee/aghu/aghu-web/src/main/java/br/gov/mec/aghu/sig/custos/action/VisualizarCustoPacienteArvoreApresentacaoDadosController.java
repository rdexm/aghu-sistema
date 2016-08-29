package br.gov.mec.aghu.sig.custos.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.dominio.DominioTipoNodoCustos;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.CalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.ConsumoPacienteNodoVO;
import br.gov.mec.aghu.sig.custos.vo.ConsumoPacienteRootVO;
import br.gov.mec.aghu.sig.custos.vo.PesquisarConsumoPacienteVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

@SessionScoped
public class VisualizarCustoPacienteArvoreApresentacaoDadosController extends ActionController{

	

	private static final String VISUALIZAR_CUSTO_PACIENTE = "visualizarCustoPaciente";
	private static final String VISUALIZAR_CUSTO_PACIENTE_POR_DIAGNOSTICO = "visualizarCustoPacientePorDiagnostico";
	private static final String VISUALIZAR_CUSTO_PACIENTE_POR_PROCEDIMENTO = "visualizarCustoPacientePorProcedimento";

	private static final long serialVersionUID = -9119994962916198444L;
	
	private static final String NODO_INTERNACAO = "Internações";
	
	private static final String ICONE_INTERNACAO = "/resources/img/icons/internacao_menu.png";
	
	private static final String DATA_HORA_ATENDIMENTO = "/resources/img/icons/clock.png";
	
	private static final String DESCRICAO_NODO_VAZIO = "Sem Registro"; 
	
	
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	private Integer atdSeq;
	private Integer prontuario;
	private Integer pacCodigoFonetica;
	private String nomePaciente;
	private List<Integer> codigoCentroCustos;
	private List<Short> codigoEspecialidades;
	private List<Integer> matriculaResponsavel;
	private List<Short> vinCodigoResponsavel;
	
	private List<Integer> pacCodigos;

	private List<ConsumoPacienteRootVO> listaPacienteCuConsumoPacienteVO;
	private List<br.gov.mec.aghu.vo.AghAtendimentosVO> listagemCustoPaciente;
	
	private List<FccCentroCustos> listaCentroCusto;
	private List<AghEspecialidades>listaEspecialidades;
	private List<AghEquipes> listaEquipes;
	
	private Integer activeTab=0;
	private final Integer TAB0 = 0;
	private DominioVisaoCustoPaciente visao;
	private Boolean pacienteEmAlta;
	
	private SigProcessamentoCusto processoCusto;
	
	private Map<String, List<ConsumoPacienteNodoVO>> mapConsumoPacienteNodoVO;
	
	private List<AipPacientes> pacienteList;
	private Map<Integer, TreeNode> pacientesMap;
	private TreeNode nodoSelecionado; 

	private List<ConsumoPacienteNodoVO> lista;
	private List<ConsumoPacienteNodoVO> listaAgrupador;
	
	private PesquisarConsumoPacienteVO pesquisaVO;
	
	@Inject
	private VisualizarCustoGeralPacienteArvoreController visualizarCustoGeralPacienteArvoreController;
	
	@Inject
	private VisualizarCustoPacienteController visualizarCustoPacienteController;
	
	private Integer prontuarioSelecionado;
	
	private List<CalculoAtendimentoPacienteVO> listaCentroCustoPorCategoria;
	
	private Boolean exibirDadosInternacao = Boolean.FALSE;
	
	private Boolean exibirAbas = Boolean.FALSE;
	
	private String origem;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String iniciar() {
		this.pacCodigos = new ArrayList<Integer>();
		pacienteList=new ArrayList<AipPacientes>();
		pacientesMap=new LinkedHashMap<>();
		this.listaPacienteCuConsumoPacienteVO = new ArrayList<ConsumoPacienteRootVO>();
		this.mapConsumoPacienteNodoVO = new HashMap<String, List<ConsumoPacienteNodoVO>>();
		this.codigoEspecialidades = new ArrayList<Short>();
		this.matriculaResponsavel = new ArrayList<Integer>();
		this.vinCodigoResponsavel = new ArrayList<Short>();
		this.codigoCentroCustos = new ArrayList<Integer>();
		listaAgrupador = new ArrayList<ConsumoPacienteNodoVO>();

		
		if(visao == null){
			visao = DominioVisaoCustoPaciente.PACIENTE;
		}
		
		bindListasPojoParaListasWraper();
		for(br.gov.mec.aghu.vo.AghAtendimentosVO vo : this.listagemCustoPaciente){
			this.pacCodigos = new ArrayList<Integer>();
			AipPacientes paciente = this.pacienteFacade.pesquisarPacientePorProntuario(vo.getProntuario());
			this.pacCodigos.add(paciente.getCodigo());
			pesquisaVO = new PesquisarConsumoPacienteVO();
			pesquisaVO.setCodigoPacientes(this.pacCodigos);
			pesquisaVO.setCodigoCentroCustos(codigoCentroCustos);
			pesquisaVO.setCodigoEspecialidades(codigoEspecialidades);
			pesquisaVO.setListaMatriculas(matriculaResponsavel);
			pesquisaVO.setListaVinCodigo(vinCodigoResponsavel);
			pesquisaVO.setVisao(visao);
			pesquisaVO.setPacienteEmAlta(pacienteEmAlta);
			pesquisaVO.setProcessoCusto(processoCusto);
			
			lista = this.custosSigFacade.pesquisarConsumoPaciente(pesquisaVO);
			
			try{
				criarArvore(vo, lista, paciente);	
			} catch(ApplicationBusinessException e){
				this.apresentarExcecaoNegocio(e);
			}
			
			if(lista == null || lista.size() == 0){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_PROCESSAMENTO_CUSTOS_NAO_ENCONTRADO");
				if(origem!=null && origem.equals(VISUALIZAR_CUSTO_PACIENTE_POR_DIAGNOSTICO)){
					this.origem = null;
					return VISUALIZAR_CUSTO_PACIENTE_POR_DIAGNOSTICO;
				} 
				else if(origem!=null && origem.equals(VISUALIZAR_CUSTO_PACIENTE_POR_PROCEDIMENTO)){
					this.origem = null;
					return VISUALIZAR_CUSTO_PACIENTE_POR_PROCEDIMENTO;
				}
				else if(origem!=null && origem.equals(VISUALIZAR_CUSTO_PACIENTE)){
					this.origem = null;
					return VISUALIZAR_CUSTO_PACIENTE;	
				}
			} 
		}
		return null;
	}
	
	public void limparDados(){
		this.pacCodigos = new ArrayList<Integer>();
		this.pacienteList=new ArrayList<AipPacientes>();
		this.pacientesMap=new LinkedHashMap<>();
		this.listaPacienteCuConsumoPacienteVO = new ArrayList<ConsumoPacienteRootVO>();
		this.mapConsumoPacienteNodoVO = new HashMap<String, List<ConsumoPacienteNodoVO>>();
		this.codigoEspecialidades = new ArrayList<Short>();
		this.matriculaResponsavel = new ArrayList<Integer>();
		this.vinCodigoResponsavel = new ArrayList<Short>();
		this.codigoCentroCustos = new ArrayList<Integer>();
		this.listaAgrupador = new ArrayList<ConsumoPacienteNodoVO>();
		this.visao = DominioVisaoCustoPaciente.PACIENTE;
		this.listagemCustoPaciente= new ArrayList<AghAtendimentosVO>();
		this.pacCodigos = new ArrayList<Integer>();
		this.listaCentroCusto = new ArrayList<FccCentroCustos>();
		this.codigoCentroCustos = new ArrayList<Integer>();
		this.listaEspecialidades = new ArrayList<AghEspecialidades>();
		this.codigoEspecialidades = new ArrayList<Short>();
		this.listaEquipes = new ArrayList<AghEquipes>();
		this.matriculaResponsavel = new ArrayList<Integer>();
		this.vinCodigoResponsavel = new ArrayList<Short>();
		this.pacienteEmAlta = false;
		this.pesquisaVO =  new PesquisarConsumoPacienteVO();
		this.processoCusto = new SigProcessamentoCusto();
		this.lista = new ArrayList<ConsumoPacienteNodoVO>();
		this.atdSeq = null;
	}
	
	
	public void onNodeSelect(NodeSelectEvent event){
		this.setActiveTab(0);
		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}	
		ConsumoPacienteNodoVO no = (ConsumoPacienteNodoVO)nodo.getData();
		List<Integer> seqCategorias = null;		
		
		if(DominioTipoNodoCustos.DTHR_ATENDIMENTO.equals(no.getTipoNodo())) {
			exibirDadosInternacao = Boolean.TRUE;
			exibirAbas = Boolean.TRUE;
		} else {
			exibirDadosInternacao = Boolean.FALSE;
		}
		//Nodos de categoria ou centro de custo
		if(no.getTipoNodo() == null || DominioTipoNodoCustos.CENTRO_CUSTO.equals(no.getTipoNodo())) {
			seqCategorias = new ArrayList<Integer>();
			if(no.getListaNodos().isEmpty()||no.getListaNodos().get(0).getDescricao().equals(DESCRICAO_NODO_VAZIO)){
				seqCategorias.add(no.getSeqCategoria());
			} else {
				for(ConsumoPacienteNodoVO nodoAux: no.getListaNodos()){
					if(nodoAux.getTipoNodo()==null || DominioTipoNodoCustos.CENTRO_CUSTO.equals(nodoAux.getTipoNodo())){
						seqCategorias.add(nodoAux.getSeqCategoria());	
					}
				}
			}
			this.exibirAbas = Boolean.TRUE;
		} else if(no.getTipoNodo().equals(DominioTipoNodoCustos.INTERNACAO)) {
			this.exibirAbas = Boolean.FALSE;
			return;
		}
		
		//atribui valores para a controller das abas
		visualizarCustoGeralPacienteArvoreController.setSeqCategorias(seqCategorias);
		visualizarCustoGeralPacienteArvoreController.setProntuario(no.getProntuario());
		visualizarCustoGeralPacienteArvoreController.setCodigoCentroCusto(no.getCodigoCentroCusto());
		visualizarCustoGeralPacienteArvoreController.setAtdSeq(no.getAtdSeq());
		
		if(processoCusto != null){
			visualizarCustoGeralPacienteArvoreController.setPmuSeq(this.processoCusto.getSeq());
		}
		
		visualizarCustoGeralPacienteArvoreController.inicializarAbas();
	}
	
	public Boolean exibirMensagemNodo(ConsumoPacienteNodoVO nodo){
		if(nodo!=null&&nodo.getTipoNodo()!=null){
			if(nodo.getTipoNodo().equals(DominioTipoNodoCustos.INTERNACAO)){
				return true;
			}	
		}
		return false;
	}
	
	public void onTabChange(TabChangeEvent event) {
		this.exibirAbas = Boolean.FALSE;
	}
	
	public void onNodeExpand(NodeExpandEvent event){
		this.setActiveTab(0);
		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}	
		ConsumoPacienteNodoVO no = (ConsumoPacienteNodoVO)nodo.getData();
		List<Integer> seqCategorias;		
		//nodo categoria que nao seja agrupador
		if((no.getIsAgrupador()==null || !no.getIsAgrupador())&&no.getTipoNodo()==null){
			seqCategorias = new ArrayList<Integer>();
			seqCategorias.add(no.getSeqCategoria());
			Integer pmuSeq=null;
			if(processoCusto!=null){
				pmuSeq = processoCusto.getSeq();
			}
			//limpa nodos sem registro
			nodo.getChildren().clear();
				
			//cria os centros de custo por categoria
			listaCentroCustoPorCategoria = custosSigFacade.buscarCentrosCustoVisaoGeral(no.getProntuario(), pmuSeq, null, seqCategorias, no.getAtdSeq());
			List<ConsumoPacienteNodoVO> listaCentroCustoPorCategoriaAux = new ArrayList<ConsumoPacienteNodoVO>();
			ConsumoPacienteNodoVO consumoPacienteNodoVO;
			for(CalculoAtendimentoPacienteVO centroCustoPorCategoria: listaCentroCustoPorCategoria){
				String descricao = centroCustoPorCategoria.getCodCentroCusto()+" - "+centroCustoPorCategoria.getDescricaoCentroCusto(); 
				consumoPacienteNodoVO =  new ConsumoPacienteNodoVO(no.getAtdSeq(),no.getDthrInicio(), no.getDthrFim(),descricao, null, null, false);
				consumoPacienteNodoVO.setTipoNodo(DominioTipoNodoCustos.CENTRO_CUSTO);
				consumoPacienteNodoVO.setTipoIcone("/resources/img/icons/notas.png");
				consumoPacienteNodoVO.setSeqCategoria(seqCategorias.get(0));
				consumoPacienteNodoVO.setProntuario(no.getProntuario());
				consumoPacienteNodoVO.setCodigoCentroCusto(centroCustoPorCategoria.getCodCentroCusto());
				listaCentroCustoPorCategoriaAux.add(consumoPacienteNodoVO);
				DefaultTreeNode novo = new DefaultTreeNode(consumoPacienteNodoVO, null);
				nodo.getChildren().add(novo);
			}
			no.setListaNodos(listaCentroCustoPorCategoriaAux);
		}
	}
	public String voltar(){
		this.limparDados();
		visualizarCustoGeralPacienteArvoreController.resetar();
		this.setExibirAbas(false);
		if(origem!=null && origem.equals(VISUALIZAR_CUSTO_PACIENTE_POR_DIAGNOSTICO)){
			this.origem = null;
			return VISUALIZAR_CUSTO_PACIENTE_POR_DIAGNOSTICO;
		} else if(origem!=null && origem.equals(VISUALIZAR_CUSTO_PACIENTE_POR_PROCEDIMENTO)){
			this.origem = null;
			return VISUALIZAR_CUSTO_PACIENTE_POR_PROCEDIMENTO;
		} else if(origem!=null && origem.equals(VISUALIZAR_CUSTO_PACIENTE)){
			this.origem = null;
			return VISUALIZAR_CUSTO_PACIENTE;
		}
		return null;
	}
	
	private void bindListasPojoParaListasWraper(){
		bindCentroCusto();
		bindEspecialidade();
		bindResponsavel();
	}
	
	protected void criarArvore(br.gov.mec.aghu.vo.AghAtendimentosVO vo, List<ConsumoPacienteNodoVO> lista, AipPacientes paciente) throws ApplicationBusinessException {
		List<ConsumoPacienteNodoVO> listaPrincipal =  new ArrayList<ConsumoPacienteNodoVO>();
		List<ConsumoPacienteNodoVO> listaSegundoNivel =  new ArrayList<ConsumoPacienteNodoVO>();
		Map<String, List<ConsumoPacienteNodoVO>> mapAgrupador =  new LinkedHashMap<String, List<ConsumoPacienteNodoVO>>();
		
		//Nodo Internações
		ConsumoPacienteNodoVO nodoPrincipal = new ConsumoPacienteNodoVO();
		nodoPrincipal.setDescricao(NODO_INTERNACAO);
		nodoPrincipal.setTipoIcone(ICONE_INTERNACAO);
		nodoPrincipal.setTipoNodo(DominioTipoNodoCustos.INTERNACAO);
		
		if(lista != null && !lista.isEmpty()){
			for(ConsumoPacienteNodoVO nodoVO : lista){
				if(this.atdSeq == null || this.atdSeq.equals(nodoVO.getAtdSeq())){
					if(this.mapConsumoPacienteNodoVO != null){
						if(!this.mapConsumoPacienteNodoVO.containsKey(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString())&&StringUtils.isBlank(nodoVO.getAgrupador())){
							List<ConsumoPacienteNodoVO> listaNodo = new ArrayList<ConsumoPacienteNodoVO>();
							listaNodo.add(nodoVO);
							this.mapConsumoPacienteNodoVO.put(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString(), listaNodo);
						} else if (this.mapConsumoPacienteNodoVO.containsKey(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString())&&StringUtils.isBlank(nodoVO.getAgrupador())) {
							List<ConsumoPacienteNodoVO> listaNodo = this.mapConsumoPacienteNodoVO.get(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString());
							listaNodo.add(nodoVO);
						} else if(!this.mapConsumoPacienteNodoVO.containsKey(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString())&&StringUtils.isNotBlank(nodoVO.getAgrupador())) {
							ConsumoPacienteNodoVO nodoAgrupador = new ConsumoPacienteNodoVO(nodoVO.getAtdSeq(),nodoVO.getDthrInicio(), nodoVO.getDthrFim(),nodoVO.getAgrupador(),nodoVO.getOrdemVisualizacao(), nodoVO.getIndContagem(),true);
							nodoAgrupador.setProntuario(paciente.getProntuario());
							List<ConsumoPacienteNodoVO> listaNodo = new ArrayList<ConsumoPacienteNodoVO>();
							listaNodo.add(nodoAgrupador);
							listaAgrupador.add(nodoAgrupador);
							this.mapConsumoPacienteNodoVO.put(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString(), listaNodo);
							if(!mapAgrupador.containsKey(nodoVO.getAgrupador()+nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString())){
								List<ConsumoPacienteNodoVO> listaNodoAgrupador = new ArrayList<ConsumoPacienteNodoVO>();
								listaNodoAgrupador.add(nodoVO);
								mapAgrupador.put(nodoVO.getAgrupador()+nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString(), listaNodoAgrupador);
							} else {
								List<ConsumoPacienteNodoVO> listaNodoAgrupador = mapAgrupador.get(nodoVO.getAgrupador()+nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString());
								listaNodoAgrupador.add(nodoVO);
							}
						}
						else if(this.mapConsumoPacienteNodoVO.containsKey(nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString())&&StringUtils.isNotBlank(nodoVO.getAgrupador())) {
							ConsumoPacienteNodoVO nodoAgrupador = new ConsumoPacienteNodoVO(nodoVO.getAtdSeq(),nodoVO.getDthrInicio(), nodoVO.getDthrFim(),nodoVO.getAgrupador(),nodoVO.getOrdemVisualizacao(), nodoVO.getIndContagem(), true);
							nodoAgrupador.setProntuario(paciente.getProntuario());
				//			List<ConsumoPacienteNodoVO> listaNodo = this.mapConsumoPacienteNodoVO.get(lista.get(i).getDthrInicioStr()+lista.get(i).getAtdSeq().toString());
							//listaNodo.add(nodoAgrupador);
							//listaAgrupador.add(nodoAgrupador);
							if(!mapAgrupador.containsKey(nodoVO.getAgrupador()+nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString())){
								List<ConsumoPacienteNodoVO> listaNodoAgrupador = new ArrayList<ConsumoPacienteNodoVO>();
								listaNodoAgrupador.add(nodoVO);
								mapAgrupador.put(nodoVO.getAgrupador()+nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString(), listaNodoAgrupador);
							} else {
								List<ConsumoPacienteNodoVO> listaNodoAgrupador = mapAgrupador.get(nodoVO.getAgrupador()+nodoVO.getDthrInicioStr()+nodoVO.getAtdSeq().toString());
								listaNodoAgrupador.add(nodoVO);
							}
						}
					}
				}
			}
			
			for(ConsumoPacienteNodoVO nodoAgrupador:listaAgrupador){
				List<ConsumoPacienteNodoVO> nodosAgrupados = mapAgrupador.get(nodoAgrupador.getDescricao()+nodoAgrupador.getDthrInicioStr()+nodoAgrupador.getAtdSeq().toString());
				nodoAgrupador.setListaNodos(nodosAgrupados);
				List<ConsumoPacienteNodoVO> listaAux =  mapConsumoPacienteNodoVO.get(nodoAgrupador.getDthrInicioStr()+nodoAgrupador.getAtdSeq().toString());
				if(listaAux!=null){
					for(ConsumoPacienteNodoVO aux: listaAux){
						if(aux.getDescricao()==nodoAgrupador.getDescricao()){
							aux =  nodoAgrupador;
						}
						break;
					}
					mapConsumoPacienteNodoVO.put(nodoAgrupador.getDthrInicioStr()+nodoAgrupador.getAtdSeq().toString(),listaAux);	
				}
			}
			
			
			for (Map.Entry<String, List<ConsumoPacienteNodoVO> > entry : mapConsumoPacienteNodoVO.entrySet()){
				//Categorias referentes a cada internacao
				List<ConsumoPacienteNodoVO> listaNodos = entry.getValue();
				ConsumoPacienteNodoVO consumoPacienteNodo = listaNodos.get(0);
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				Short idx = 0;
				String descricao = null;
				if (consumoPacienteNodo.getDthrInicio() != null && consumoPacienteNodo.getDthrFim() != null) {
					descricao = df.format(consumoPacienteNodo.getDthrInicio()) + " - " + df.format(consumoPacienteNodo.getDthrFim());
				} else if (consumoPacienteNodo.getDthrInicio() != null) {
					descricao = df.format(consumoPacienteNodo.getDthrInicio());
				} else if (consumoPacienteNodo.getDthrFim() != null) {
					descricao = df.format(consumoPacienteNodo.getDthrFim());
				}
			    // Cria nodo de data/hora do atendimento
				ConsumoPacienteNodoVO nodoInternacao =  new ConsumoPacienteNodoVO(consumoPacienteNodo.getAtdSeq(), consumoPacienteNodo.getDthrInicio(), consumoPacienteNodo.getDthrFim(), descricao, idx++, null, false);
				nodoInternacao.setTipoIcone(DATA_HORA_ATENDIMENTO);
				this.tratarNodoVazio(consumoPacienteNodo, listaNodos);
				
				nodoInternacao.setListaNodos(listaNodos);
				nodoInternacao.setTipoNodo(DominioTipoNodoCustos.DTHR_ATENDIMENTO);
				nodoInternacao.setProntuario(consumoPacienteNodo.getProntuario());
				listaSegundoNivel.add(nodoInternacao);
			}
			this.mapConsumoPacienteNodoVO.clear();
		}
		nodoPrincipal.setListaNodos(listaSegundoNivel);
		listaPrincipal.add(nodoPrincipal);
		if (!pacientesMap.containsKey(paciente.getProntuario())){
			TreeNode root = createRoot(listaPrincipal, paciente);
			pacienteList.add(paciente);
			pacientesMap.put(paciente.getProntuario(), root);
		}	
		prontuario=null;
	}
	
	public void tratarNodoVazio(ConsumoPacienteNodoVO consumoPacienteNodo, List<ConsumoPacienteNodoVO> listaNodos){
		ConsumoPacienteNodoVO nodoVazio =  new ConsumoPacienteNodoVO(consumoPacienteNodo.getAtdSeq(), consumoPacienteNodo.getDthrInicio(), consumoPacienteNodo.getDthrFim(), DESCRICAO_NODO_VAZIO, null, null, false);
		nodoVazio.setTipoIcone("/resources/img/icons/bin_closed.png");
		List<ConsumoPacienteNodoVO> listaVazia = new ArrayList<ConsumoPacienteNodoVO>();
		listaVazia.add(nodoVazio);
		for(ConsumoPacienteNodoVO nodo: listaNodos){
			new ArrayList<ConsumoPacienteNodoVO>().add(nodoVazio);
			if(nodo.getIsAgrupador()!=null && nodo.getIsAgrupador()){
				for(ConsumoPacienteNodoVO nodoAgrupado: nodo.getListaNodos()){
					nodoAgrupado.setListaNodos(listaVazia);	
				}
			}
			else if((nodo.getIsAgrupador()!=null && !nodo.getIsAgrupador())||nodo.getIsAgrupador()==null){
					nodo.setListaNodos(listaVazia);	
			}
		}
	}
	
	private DefaultTreeNode createRoot(List<ConsumoPacienteNodoVO> lista, AipPacientes paciente){
		DefaultTreeNode root = new DefaultTreeNode(paciente.getProntuario(), null);
		for (ConsumoPacienteNodoVO nodo : lista){
			recursiveNodo(nodo, root,paciente.getNome());
		}
		return root;
	}
	
	/**
	 * Método recursivo para a criação da estrutura da árvore
	 * @param nodo
	 * @param nodeView
	 * @param nomePaciente
	 */
	private void recursiveNodo(ConsumoPacienteNodoVO nodo, TreeNode nodeView, String nomePaciente){
		DefaultTreeNode novo = new DefaultTreeNode(nodo, null);
		if (!nodo.getListaNodos().isEmpty()){
			for (ConsumoPacienteNodoVO nodoAux : nodo.getListaNodos()){
				recursiveNodo(nodoAux, novo, nomePaciente);
			}	
		}
		nodeView.getChildren().add(novo);
	}
	

	
	public List<ConsumoPacienteNodoVO> getLista() {
		return lista;
	}

	public void setLista(List<ConsumoPacienteNodoVO> lista) {
		this.lista = lista;
	}

	
	protected void bindResponsavel() {
		if(listaEquipes != null && !listaEquipes.isEmpty()){
			for(AghEquipes pojo : listaEquipes){
				RapServidores responsavel = pojo.getProfissionalResponsavel();
				if(responsavel != null){
					matriculaResponsavel.add(responsavel.getId().getMatricula());
					vinCodigoResponsavel.add(responsavel.getId().getVinCodigo());
				}	
			}
		}
	}

	protected void bindEspecialidade() {
		if(listaEspecialidades != null && !listaEspecialidades.isEmpty()){
			for(AghEspecialidades pojo : listaEspecialidades){
				this.codigoEspecialidades.add(pojo.getSeq());
			}
		} 
	}

	protected void bindCentroCusto() {
		if(this.listaCentroCusto != null && !listaCentroCusto.isEmpty()){
			for(FccCentroCustos centroCusto : listaCentroCusto){
				this.codigoCentroCustos.add(centroCusto.getCodigo());
			}
		} 
	}
	
	public void setListagemCustoPaciente(List<br.gov.mec.aghu.vo.AghAtendimentosVO> listagemCustoPaciente) {
		this.listagemCustoPaciente = listagemCustoPaciente;
	}

	public List<br.gov.mec.aghu.vo.AghAtendimentosVO> getListagemCustoPaciente() {
		return listagemCustoPaciente;
	}	

	public List<Integer> getMatriculaResponsavel() {
		return matriculaResponsavel;
	}

	public void setMatriculaResponsavel(List<Integer> matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public List<Short> getVinCodigoResponsavel() {
		return vinCodigoResponsavel;
	}

	public void setVinCodigoResponsavel(List<Short> vinCodigoResponsavel) {
		this.vinCodigoResponsavel = vinCodigoResponsavel;
	}

	public List<FccCentroCustos> getListaCentroCusto() {
		return listaCentroCusto;
	}

	public void setListaCentroCusto(List<FccCentroCustos> listaCentroCusto) {
		this.listaCentroCusto = listaCentroCusto;
	}

	public List<AghEquipes> getListaEquipes() {
		return listaEquipes;
	}

	public void setListaEquipes(List<AghEquipes> listaEquipes) {
		this.listaEquipes = listaEquipes;
	}

	public void setVisao(DominioVisaoCustoPaciente visao) {
		this.visao = visao;
	}

	public DominioVisaoCustoPaciente getVisao() {
		return visao;
	}

	public void setPacienteEmAlta(Boolean pacienteEmAlta) {
		this.pacienteEmAlta = pacienteEmAlta;
	}

	public Boolean getPacienteEmAlta() {
		return pacienteEmAlta;
	}

	public void setProcessoCusto(SigProcessamentoCusto processoCusto) {
		this.processoCusto = processoCusto;
	}

	public SigProcessamentoCusto getProcessoCusto() {
		return processoCusto;
	}
	
	
	public List<AghEspecialidades> getListaEspecialidades() {
		return listaEspecialidades;
	}

	public void setListaEspecialidades(List<AghEspecialidades> listaEspecialidades) {
		this.listaEspecialidades = listaEspecialidades;
	}	

	public List<Integer> getCodigoCentroCustos() {
		return codigoCentroCustos;
	}

	public void setCodigoCentroCustos(List<Integer> codigoCentroCustos) {
		this.codigoCentroCustos = codigoCentroCustos;
	}

	public List<Short> getCodigoEspecialidades() {
		return codigoEspecialidades;
	}

	public void setCodigoEspecialidades(List<Short> codigoEspecialidades) {
		this.codigoEspecialidades = codigoEspecialidades;
	}

	public List<ConsumoPacienteRootVO> getListaPacienteCuConsumoPacienteVO() {
		return listaPacienteCuConsumoPacienteVO;
	}

	public void setListaPacienteCuConsumoPacienteVO(List<ConsumoPacienteRootVO> listaPacienteCuConsumoPacienteVO) {
		this.listaPacienteCuConsumoPacienteVO = listaPacienteCuConsumoPacienteVO;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigos(List<Integer> pacCodigos) {
		this.pacCodigos = pacCodigos;
	}

	public List<Integer> getPacCodigos() {
		return pacCodigos;
	}

	public List<AipPacientes> getPacienteList() {
		return pacienteList;
	}

	public void setPacienteList(List<AipPacientes> pacienteList) {
		this.pacienteList = pacienteList;
	}

	public Integer getTAB0() {
		return TAB0;
	}

	public Map<Integer, TreeNode> getPacientesMap() {
		return pacientesMap;
	}

	public void setPacientesMap(Map<Integer, TreeNode> pacientesMap) {
		this.pacientesMap = pacientesMap;
	}

	
	
	public PesquisarConsumoPacienteVO getPesquisaVO() {
		return pesquisaVO;
	}

	public void setPesquisaVO(PesquisarConsumoPacienteVO pesquisaVO) {
		this.pesquisaVO = pesquisaVO;
	}

	public Integer getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(Integer activeTab) {
		this.activeTab = activeTab;
	}

	public VisualizarCustoGeralPacienteArvoreController getVisualizarCustoGeralPacienteArvoreController() {
		return visualizarCustoGeralPacienteArvoreController;
	}

	public void setVisualizarCustoGeralPacienteArvoreController(
			VisualizarCustoGeralPacienteArvoreController visualizarCustoGeralPacienteArvoreController) {
		this.visualizarCustoGeralPacienteArvoreController = visualizarCustoGeralPacienteArvoreController;
	}

	public Integer getProntuarioSelecionado() {
		return prontuarioSelecionado;
	}

	public void setProntuarioSelecionado(Integer prontuarioSelecionado) {
		this.prontuarioSelecionado = prontuarioSelecionado;
	}

	public TreeNode getNodoSelecionado() {
		return nodoSelecionado;
	}

	public void setNodoSelecionado(TreeNode nodoSelecionado) {
		this.nodoSelecionado = nodoSelecionado;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCentroCustoPorCategoria() {
		return listaCentroCustoPorCategoria;
	}

	public void setListaCentroCustoPorCategoria(
			List<CalculoAtendimentoPacienteVO> listaCentroCustoPorCategoria) {
		this.listaCentroCustoPorCategoria = listaCentroCustoPorCategoria;
	}

	public Boolean getExibirDadosInternacao() {
		return exibirDadosInternacao;
	}

	public void setExibirDadosInternacao(Boolean exibirDadosInternacao) {
		this.exibirDadosInternacao = exibirDadosInternacao;
	}

	public Boolean getExibirAbas() {
		return exibirAbas;
	}

	public void setExibirAbas(Boolean exibirAbas) {
		this.exibirAbas = exibirAbas;
	}

	public VisualizarCustoPacienteController getVisualizarCustoPacienteController() {
		return visualizarCustoPacienteController;
	}

	public void setVisualizarCustoPacienteController(
			VisualizarCustoPacienteController visualizarCustoPacienteController) {
		this.visualizarCustoPacienteController = visualizarCustoPacienteController;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	
	
}
