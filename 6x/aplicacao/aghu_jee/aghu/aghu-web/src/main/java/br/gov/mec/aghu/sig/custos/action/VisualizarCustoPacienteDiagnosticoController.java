package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCidCustoPacienteDiagnostico;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.VSigCustosCalculoCidVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class VisualizarCustoPacienteDiagnosticoController extends ActionController {

/**
	 * 
	 */
	private static final long serialVersionUID = -6686248326272445467L;
	private SigProcessamentoCusto competencia;
	private List<SigProcessamentoCusto> listaCompetencias;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	private List<AghCid> listaCID;
	
	private String cidPrincipal;
	
	private DominioCidCustoPacienteDiagnostico tipo = DominioCidCustoPacienteDiagnostico.Q;
	
	private List<VSigCustosCalculoCidVO> listagemPrimeiroNivel;
	
	private TreeNode root;
	
	private AghCid cidSelecionado;
	
	private DataTable dataTable;
	
	private static final String VISUALIZAR_CUSTO_PACIENTE_ARVORE_APRESENTACAO_DADOS = "visualizarCustoPacienteArvoreApresentacaoDados";
	
	@Inject
	private VisualizarCustoPacienteArvoreApresentacaoDadosController visualizarCustoPacienteArvoreApresentacaoDadosController;
     
	private AghCid cid;
	
	private Integer activeIndex;
	
	private Long totalQuantidade = 0L;
	
	private BigDecimal totalCusto = BigDecimal.ZERO;
	
	private BigDecimal totalReceita = BigDecimal.ZERO;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		this.setDataTable(new DataTable());
	}
	
	public void limpar(){
		this.competencia = listaCompetencias.get(0);
		this.tipo = DominioCidCustoPacienteDiagnostico.Q;
		this.setCidSelecionado(null);
		this.listaCID.clear();
		this.cidPrincipal = null;
		this.listagemPrimeiroNivel = new ArrayList<VSigCustosCalculoCidVO>();
		this.totalQuantidade = 0L;
		this.totalCusto = BigDecimal.ZERO;
		this.totalReceita = BigDecimal.ZERO;
		this.root = null;
		
	}
	
	public String redirecionarCustoPacienteArvore(VSigCustosCalculoCidVO diagnostico){
		List<AghAtendimentosVO> listagem = new ArrayList<AghAtendimentosVO>();
		AghAtendimentosVO vo = new AghAtendimentosVO();
		vo.setProntuario(diagnostico.getProntuario());
		listagem.add(vo);
		visualizarCustoPacienteArvoreApresentacaoDadosController.setListagemCustoPaciente(listagem);
		visualizarCustoPacienteArvoreApresentacaoDadosController.setOrigem("visualizarCustoPacientePorDiagnostico");
		visualizarCustoPacienteArvoreApresentacaoDadosController.setVisao(DominioVisaoCustoPaciente.PACIENTE);
		visualizarCustoPacienteArvoreApresentacaoDadosController.setAtdSeq(diagnostico.getAtdSeq());
		return VISUALIZAR_CUSTO_PACIENTE_ARVORE_APRESENTACAO_DADOS;
	}
	
	
	/**
	 * Metodo de setup chamado pelo page.xml
	 */
	public void iniciar(){
		this.listaCompetencias = this.custosSigFacade.obterListaCompetencias(DominioVisaoCustoPaciente.COMPETENCIA);
		
		if(this.listaCID == null){
			this.listaCID = new ArrayList<AghCid>();
		}
		
		if(this.competencia == null){
			this.competencia = listaCompetencias.get(0);
		}
	}
	
	
	// suggestion - cid
	public List<AghCid> pesquisarCid(String param) {
		return this.returnSGWithCount(this.aghuFacade.obterCidPorNomeCodigoAtivaPaginado((String)param, this.competencia),pesquisarCidCount(param));
	}
	
	public Long pesquisarCidCount(String param) {
		return this.aghuFacade.obterCidPorNomeCodigoAtivaCount((String)param, this.competencia);
	}
	
	public void getAdicionarCidNaLista(){
		try {
			this.custosSigFacade.adicionarCIDNaLista(this.listaCID, this.getCid());
			this.cid = null;
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getCode().toString());
		}
	}
	
	
	public void deletarCIDDaLista(AghCid cid){
		if(cidSelecionado!=null && cid.getSeq().equals(cidSelecionado.getSeq())){
			this.setCidSelecionado(null);	
		}
		this.custosSigFacade.deletarCIDDaLista(this.listaCID, cid);
	}
	
	public void pesquisar(){
		String descricaoCidSelecionado = null;
		if(cidSelecionado!=null){
			descricaoCidSelecionado = cidSelecionado.getDescricao();
		}
		this.listagemPrimeiroNivel = this.getCustosSigFacade().pesquisarDiagnosticosPrimeiroNivel(competencia.getSeq(), descricaoCidSelecionado, listaCID, tipo);
		root = criarArvore();
    }
    
	public TreeNode criarArvore(){
	        TreeNode root = new DefaultTreeNode(new VSigCustosCalculoCidVO(),null);
	        for(VSigCustosCalculoCidVO vo: listagemPrimeiroNivel){
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
		VSigCustosCalculoCidVO no = (VSigCustosCalculoCidVO)nodo.getData();
		if(no.getSegundoNivel()==null || !no.getSegundoNivel()){
			List<Integer> listaSeq = new ArrayList<Integer>();
			for(AghCid cid: listaCID){
				if(!no.getCidSeq().equals(cid.getSeq())){
					listaSeq.add(cid.getSeq());	
				}
			}
			List<VSigCustosCalculoCidVO> listagemSegundoNivel = this.custosSigFacade.pesquisarDiagnosticosSegundoNivel(competencia.getSeq(), no.getCidSeq(), listaSeq, listaCID.size(), tipo);
			for(VSigCustosCalculoCidVO vo: listagemSegundoNivel){
				vo.setTerceiroNivel(false);
				TreeNode nodoSegundoNivel = new DefaultTreeNode(vo, nodo);
	        	TreeNode nodoVazio = new DefaultTreeNode(null, nodo);
	        	nodoSegundoNivel.getChildren().add(nodoVazio);
				nodo.getChildren().add(nodoSegundoNivel);
			}	
		} else {
			List<VSigCustosCalculoCidVO> listagemTerceiroNivel = this.custosSigFacade.obterListaPacientes(competencia.getSeq(), no.getListaAtdSeq(), no.getCidSeq());
			for(VSigCustosCalculoCidVO vo: listagemTerceiroNivel){
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
	
	public List<AghCid> getListaCID() {
		return listaCID;
	}

	public void setListaCID(List<AghCid> listaCID) {
		this.listaCID = listaCID;
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

	public String getCidPrincipal() {
		return cidPrincipal;
	}

	public void setCidPrincipal(String cidPrincipal) {
		if(cidPrincipal.equals(this.cidPrincipal)){
			this.cidPrincipal = null;
		} else {
			this.cidPrincipal = cidPrincipal;
		}
	}
	
	public DominioCidCustoPacienteDiagnostico getTipo() {
		return tipo;
	}

	public void setTipo(DominioCidCustoPacienteDiagnostico tipo) {
		this.tipo = tipo;
	}

	public List<VSigCustosCalculoCidVO> getListagemPrimeiroNivel() {
		return listagemPrimeiroNivel;
	}

	public void setListagemPrimeiroNivel(
			List<VSigCustosCalculoCidVO> listagemPrimeiroNivel) {
		this.listagemPrimeiroNivel = listagemPrimeiroNivel;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public AghCid getCidSelecionado() {
        return cidSelecionado;
    }

	 public void setCidSelecionado(AghCid cidSelecionado) {
        if(this.cidSelecionado != null && cidSelecionado!=null && this.cidSelecionado.getSeq().equals(cidSelecionado.getSeq()) ){
            this.cidSelecionado = null;
        }
        else{
            this.cidSelecionado = cidSelecionado;
        }
        this.dataTable.setSelection(cidSelecionado);
    }

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public Integer getActiveIndex() {
		
		if(listaCID == null || listaCID.isEmpty()){
			return -1;
		}
		else{
			return activeIndex;
		}
	}

	public void setActiveIndex(Integer activeIndex) {
		this.activeIndex = activeIndex;
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
}
