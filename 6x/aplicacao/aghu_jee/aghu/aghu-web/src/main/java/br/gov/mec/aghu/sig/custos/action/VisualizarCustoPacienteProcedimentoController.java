package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioProcedimentoCustoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoCompetencia;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.CalculoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.ConvenioPagadorVO;
import br.gov.mec.aghu.sig.custos.vo.ItemProcedHospVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;


public class VisualizarCustoPacienteProcedimentoController extends ActionController {

/**
	 * 
	 */
	private static final long serialVersionUID = -7005302754525000541L;
/**
/**
	 * 
	 */
	private SigProcessamentoCusto competencia;
	private List<SigProcessamentoCusto> listaCompetencias;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private List<ItemProcedHospVO> listaProcedimento;
	
	private String procedimentoPrincipal;
	
	private DominioProcedimentoCustoPaciente tipo = DominioProcedimentoCustoPaciente.Q;
	
	private List<CalculoProcedimentoVO> listagemPrimeiroNivel;
	
	private TreeNode root;
	
	private TreeNode rootGrupoConvenio;

	private ItemProcedHospVO procedimentoSelecionado;
	
	private static final String VISUALIZAR_CUSTO_PACIENTE_ARVORE_APRESENTACAO_DADOS = "visualizarCustoPacienteArvoreApresentacaoDados";
	
	@Inject
	private VisualizarCustoPacienteArvoreApresentacaoDadosController visualizarCustoPacienteArvoreApresentacaoDadosController;
     
	private ItemProcedHospVO procedimento;
	
	private Integer activeIndex = -1;
	
	private Integer activeIndexGrupoConvenio = -1;
	
	private TreeNode[] nodosSelecionados;
	
	private List<Short> conveniosSelecionados;
	
	private DominioTipoCompetencia tipoCompetencia = DominioTipoCompetencia.CUSTOS;
	
	private Long totalQuantidade = 0L;
	
	private BigDecimal totalCusto = BigDecimal.ZERO;
	
	private BigDecimal totalReceita = BigDecimal.ZERO;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Metodo de setup chamado pelo page.xml
	 */
	public void iniciar(){
		this.listaCompetencias = this.custosSigFacade.obterListaCompetencias(DominioVisaoCustoPaciente.COMPETENCIA);
		
		if(this.listaProcedimento == null){
			this.listaProcedimento = new ArrayList<ItemProcedHospVO>();
		}
		
		if(this.competencia == null){
			this.competencia = listaCompetencias.get(0);
		}
		
		if(this.rootGrupoConvenio == null){
			this.criarArvoreConvenios();
		}
	}
	
	public void criarArvoreConvenios(){
		this.rootGrupoConvenio = new CheckboxTreeNode();
		for(AacPagador pagador: this.ambulatorioFacade.listarPagadoresAtivos()){
			List<FatConvenioSaude> convenios = this.faturamentoFacade.listarConveniosSaudeAtivosPorPgdSeq(pagador.getSeq());
			if(convenios != null && !convenios.isEmpty()){
				TreeNode nodoPai = new CheckboxTreeNode(new ConvenioPagadorVO(pagador), this.rootGrupoConvenio);
	        	nodoPai.setSelected(false);
	        	for(FatConvenioSaude convenio : convenios){
		        	TreeNode nodoFilho = new CheckboxTreeNode(new ConvenioPagadorVO(convenio), nodoPai);
		        	nodoFilho.setSelected(false);
	        	}
			}
        }
	}
	
	public void limpar(){
		this.competencia = listaCompetencias.get(0);
		this.tipo = DominioProcedimentoCustoPaciente.Q;
		this.procedimentoSelecionado =  null;
		this.listaProcedimento.clear();
		this.procedimentoPrincipal = null;
		this.listagemPrimeiroNivel = new ArrayList<CalculoProcedimentoVO>();
		this.totalQuantidade = 0L;
		this.totalCusto = BigDecimal.ZERO;
		this.totalReceita = BigDecimal.ZERO;
		this.root = null;
		this.activeIndex = -1;
		this.activeIndexGrupoConvenio = -1;
		this.criarArvoreConvenios();
		this.tipoCompetencia = DominioTipoCompetencia.CUSTOS;
	}
	
	public String redirecionarCustoPacienteArvore(CalculoProcedimentoVO procedimento){
		List<AghAtendimentosVO> listagem = new ArrayList<AghAtendimentosVO>();
		AghAtendimentosVO vo = new AghAtendimentosVO();
		vo.setProntuario(procedimento.getProntuario());
		listagem.add(vo);
		visualizarCustoPacienteArvoreApresentacaoDadosController.setListagemCustoPaciente(listagem);
		visualizarCustoPacienteArvoreApresentacaoDadosController.setOrigem("visualizarCustoPacientePorProcedimento");
		visualizarCustoPacienteArvoreApresentacaoDadosController.setVisao(DominioVisaoCustoPaciente.PACIENTE);
		visualizarCustoPacienteArvoreApresentacaoDadosController.setAtdSeq(procedimento.getAtdSeq());
		return VISUALIZAR_CUSTO_PACIENTE_ARVORE_APRESENTACAO_DADOS;
	}
	
	// suggestion - procedimento
	public List<ItemProcedHospVO> pesquisarProcedimento(String param) {
		return this.returnSGWithCount(this.custosSigFacade.buscarProcedimentos(this.competencia.getSeq(), param),pesquisarProcedimentoCount(competencia.getSeq(), param));
	}
	
	public Long pesquisarProcedimentoCount(Integer pmuSeq, String param) {
		return this.custosSigFacade.buscarProcedimentosCount(pmuSeq, param);
	}
	
	public void adicionarProcedimentoNaLista(){
		try {
			this.custosSigFacade.adicionarProcedimentoNaLista(this.listaProcedimento, this.getProcedimento());
			this.procedimento = null;
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getCode().toString());
		}
	}
	
	
	public void deletarProcedimentoDaLista(ItemProcedHospVO procedimento){
		if(procedimentoSelecionado!=null && procedimento.getIphSeq().equals(procedimentoSelecionado.getIphSeq()) && procedimento.getIphPhoSeq().equals(procedimentoSelecionado.getIphPhoSeq())){
			this.procedimentoSelecionado = null;	
		}
		this.custosSigFacade.deletarProcedimentoDaLista(this.listaProcedimento, procedimento);
	}
	
	public void pesquisar(){
		
		this.conveniosSelecionados = new ArrayList<Short>();
		if(nodosSelecionados!= null){
			for (TreeNode nodo: nodosSelecionados) {
				ConvenioPagadorVO vo = (ConvenioPagadorVO) nodo.getData();
				if(vo.isConvenio()){
					conveniosSelecionados.add(vo.getCodigo());
				}
			}
		}
		
		this.listagemPrimeiroNivel = this.getCustosSigFacade().buscarProcedimentosPrimeiroNivel(competencia, tipoCompetencia, procedimentoSelecionado, tipo, listaProcedimento, conveniosSelecionados);
		root = criarArvore();
    }
    
	public TreeNode criarArvore(){
	        TreeNode root = new DefaultTreeNode(new CalculoProcedimentoVO(),null);
	        for(CalculoProcedimentoVO vo: listagemPrimeiroNivel){
	        	TreeNode nodo = new DefaultTreeNode(vo, root);
	        	TreeNode nodoVazio = new DefaultTreeNode(null, nodo);
	        	nodo.getChildren().add(nodoVazio);
	        	root.getChildren().add(nodo);
	        	
	        	if(vo.getQuantidade()!= null){
	        		this.totalQuantidade += vo.getQuantidade();
	        	}
	        	
	        	if(vo.getCustoTotal() != null){
	        		this.totalCusto = this.totalCusto.add(vo.getCustoTotal());
	        	}
	        	
	        	if(vo.getReceitaTotal() != null){
	        		this.totalReceita = this.totalReceita.add(vo.getReceitaTotal());
	        	}
	        }
	        return root;
	}
	
	
	public void onNodeExpand(NodeExpandEvent event){
		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}
		nodo.getChildren().remove(0);
		CalculoProcedimentoVO no = (CalculoProcedimentoVO)nodo.getData();
		if(no.getSegundoNivel()==null || !no.getSegundoNivel()){
			List<ItemProcedHospVO> listaProcedimentoAux = new ArrayList<ItemProcedHospVO>();
			
			for(ItemProcedHospVO procedimento: this.listaProcedimento){
				if(!(no.getIphSeq().equals(procedimento.getIphSeq())&& no.getIphPhoSeq().equals(procedimento.getIphPhoSeq()))){
					listaProcedimentoAux.add(procedimento);
				}
			}
			List<CalculoProcedimentoVO> listagemSegundoNivel = this.custosSigFacade.buscarProcedimentosSegundoNivel(competencia, tipoCompetencia, no.getIphPhoSeq(), no.getIphSeq(), listaProcedimentoAux, this.listaProcedimento.size(), tipo, conveniosSelecionados);
			for(CalculoProcedimentoVO vo: listagemSegundoNivel){
				vo.setTerceiroNivel(false);
				TreeNode nodoSegundoNivel = new DefaultTreeNode(vo, nodo);
	        	TreeNode nodoVazio = new DefaultTreeNode(null, nodo);
	        	nodoSegundoNivel.getChildren().add(nodoVazio);
				nodo.getChildren().add(nodoSegundoNivel);
			}	
		} else {
			List<CalculoProcedimentoVO> listagemTerceiroNivel = this.custosSigFacade.buscarPacientesProcedimentos(competencia, tipoCompetencia, no.getListaAtdSeq(), conveniosSelecionados);
			for(CalculoProcedimentoVO vo: listagemTerceiroNivel){
				vo.setTerceiroNivel(true);
				nodo.getChildren().add(new DefaultTreeNode(vo,nodo));
			}
		}
	}
	
	public void onNodeCollapse(NodeCollapseEvent event){
		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}
		nodo.getChildren().clear();
		TreeNode nodoVazio = new DefaultTreeNode(null, nodo);
    	nodo.getChildren().add(nodoVazio);
	}
	
	
	public String buscarContasPaciente(Integer atdSeq){
		List<Integer> contas = this.custosSigFacade.buscarContasPaciente(competencia, tipoCompetencia, atdSeq, conveniosSelecionados);
		
		if(contas != null){
			if(contas.size() == 1){
				return "( Conta: "+contas.get(0)+" )";
			}
			else{
				return "( Contas: "+contas.toString().replace("[", "").replace("]", "")+" )";
			}
		}
		return "";
	}
	
	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public List<SigProcessamentoCusto> getListaCompetencias() {
		return listaCompetencias;
	}

	public void setListaCompetencias(List<SigProcessamentoCusto> listaCompetencias) {
		this.listaCompetencias = listaCompetencias;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public ICustosSigFacade getCustosSigFacade() {
		return custosSigFacade;
	}

	public void setCustosSigFacade(ICustosSigFacade custosSigFacade) {
		this.custosSigFacade = custosSigFacade;
	}

	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}

	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		if(procedimentoPrincipal.equals(this.procedimentoPrincipal)){
			this.procedimentoPrincipal = null;
		} else {
			this.procedimentoPrincipal = procedimentoPrincipal;
		}
	}
	
	public DominioProcedimentoCustoPaciente getTipo() {
		return tipo;
	}

	public void setTipo(DominioProcedimentoCustoPaciente tipo) {
		this.tipo = tipo;
	}

	
	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public ItemProcedHospVO getProcedimentoSelecionado() {
        return procedimentoSelecionado;
    }

	 public void setProcedimentoSelecionado(ItemProcedHospVO procedimentoSelecionado) {
        if(this.procedimentoSelecionado != null && procedimentoSelecionado!=null && this.procedimentoSelecionado.getIphPhoSeq().equals(procedimentoSelecionado.getIphPhoSeq()) &&this.procedimentoSelecionado.getIphSeq().equals(procedimentoSelecionado.getIphSeq()) ){
            this.procedimentoSelecionado = null;
        }
        else{
            this.procedimentoSelecionado = procedimentoSelecionado;
        }
    }

	public List<ItemProcedHospVO> getListaProcedimento() {
		return listaProcedimento;
	}

	public void setListaProcedimento(List<ItemProcedHospVO> listaProcedimento) {
		this.listaProcedimento = listaProcedimento;
	}

	public List<CalculoProcedimentoVO> getListagemPrimeiroNivel() {
		return listagemPrimeiroNivel;
	}

	public void setListagemPrimeiroNivel(
			List<CalculoProcedimentoVO> listagemPrimeiroNivel) {
		this.listagemPrimeiroNivel = listagemPrimeiroNivel;
	}

	public VisualizarCustoPacienteArvoreApresentacaoDadosController getVisualizarCustoPacienteArvoreApresentacaoDadosController() {
		return visualizarCustoPacienteArvoreApresentacaoDadosController;
	}

	public void setVisualizarCustoPacienteArvoreApresentacaoDadosController(
			VisualizarCustoPacienteArvoreApresentacaoDadosController visualizarCustoPacienteArvoreApresentacaoDadosController) {
		this.visualizarCustoPacienteArvoreApresentacaoDadosController = visualizarCustoPacienteArvoreApresentacaoDadosController;
	}

	public ItemProcedHospVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ItemProcedHospVO procedimento) {
		this.procedimento = procedimento;
	}
	
	public Integer getActiveIndex() {
		if(listaProcedimento == null || listaProcedimento.isEmpty()){
			return -1;
		}
		else{
			return activeIndex;
		}
	}

	public void setActiveIndex(Integer activeIndex) {
		this.activeIndex = activeIndex;
	}

	public Integer getActiveIndexGrupoConvenio() {
		return activeIndexGrupoConvenio;
	}

	public void setActiveIndexGrupoConvenio(Integer activeIndexGrupoConvenio) {
		this.activeIndexGrupoConvenio = activeIndexGrupoConvenio;
	}

	public TreeNode[] getNodosSelecionados() {
		return nodosSelecionados;
	}

	public void setNodosSelecionados(TreeNode[] nodosSelecionados) {
		this.nodosSelecionados = nodosSelecionados;
	}

	public List<Short> getConveniosSelecionados() {
		return conveniosSelecionados;
	}

	public void setConveniosSelecionados(List<Short> conveniosSelecionados) {
		this.conveniosSelecionados = conveniosSelecionados;
	}

	public DominioTipoCompetencia getTipoCompetencia() {
		return tipoCompetencia;
	}

	public void setTipoCompetencia(DominioTipoCompetencia tipoCompetencia) {
		this.tipoCompetencia = tipoCompetencia;
	}

	public Long getTotalQuantidade() {
		return totalQuantidade;
	}

	public void setTotalQuantidade(Long totalQuantidade) {
		this.totalQuantidade = totalQuantidade;
	}

	public BigDecimal getTotalCusto() {
		return totalCusto;
	}

	public void setTotalCusto(BigDecimal totalCusto) {
		this.totalCusto = totalCusto;
	}

	public BigDecimal getTotalReceita() {
		return totalReceita;
	}

	public void setTotalReceita(BigDecimal totalReceita) {
		this.totalReceita = totalReceita;
	}              

	public TreeNode getRootGrupoConvenio() {
		return rootGrupoConvenio;
	}

	public void setRootGrupoConvenio(TreeNode rootGrupoConvenio) {
		this.rootGrupoConvenio = rootGrupoConvenio;
	}
	
}
