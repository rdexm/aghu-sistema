package br.gov.mec.aghu.exames.patologia.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.CidOTreeNodeVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.Cid1VO;
import br.gov.mec.aghu.internacao.pesquisa.vo.GrupoCidVO;
import br.gov.mec.aghu.model.AelTopografiaCidOs;
import br.gov.mec.aghu.model.AelTopografiaGrupoCidOs;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaCidOController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -7427634266641727480L;

	@EJB
	private IExamesPatologiaFacade patologiaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private CadastroLaudoUnicoController cadastroLaudoUnicoController;
	
	@Inject
	private LaudoUnicoController laudoUnicoController;
	
	private TreeNode nodoGrupo;
	private TreeNode nodoCid;
	private TreeNode nodoSelecionado;

	private Long seqGrupoCidO;

	private Long seqCidO;

	private Boolean mostrarGrupoCidO = true;
	private Boolean mostrarSubGrupos = false;
	private Boolean mostraNodoCidO = false;

	private AelTopografiaCidOs cidO = null;

	private Set<Object> nosExpandidos = new HashSet<Object>();

	private List<CidOTreeNodeVO> listaGruposRaiz = new ArrayList<CidOTreeNodeVO>();

	private TreeNode root;
	//private TreeNode raiz;
	//private Long seqNoSelecionado;

	private String tipoSumario;

	private List<AelTopografiaGrupoCidOs> gruposCidO = new ArrayList<AelTopografiaGrupoCidOs>();

	public void iniciar() {
		seqCidO = null;
		cidO = null;

		nosExpandidos = new HashSet<Object>();

		this.montaArvore();
	}
	
    
	public void montaArvore() {

		root = new DefaultTreeNode("Root", null);
		this.gruposCidO = patologiaFacade.listarTopografiaGrupoCidOsNodosRaiz();

		if (gruposCidO != null && !gruposCidO.isEmpty()) {
			for (AelTopografiaGrupoCidOs grupoCidO : gruposCidO) {
				CidOTreeNodeVO cidOTreeNodeVO = montaTreeNodePorGrupo(grupoCidO, true);
				cidOTreeNodeVO.setFilhos(detalharSubGruposCidO(grupoCidO.getSeq()));
				if(!cidOTreeNodeVO.getFilhos().isEmpty()){
					TreeNode capituloCID = new DefaultTreeNode("cidCapitulo", cidOTreeNodeVO, root);
					new DefaultTreeNode("cidGrupo", cidOTreeNodeVO, capituloCID);
				}
				//root.getChildren().add(gruposCID);
			}
		}

		configurarApresentacaoNodos(true, false, false);
	}
	
	private CidOTreeNodeVO montaTreeNodePorGrupo(
			AelTopografiaGrupoCidOs grupoCidO, boolean isRaiz) {
		CidOTreeNodeVO cidOTreeNodeVO = new CidOTreeNodeVO();
		cidOTreeNodeVO.setSeqTopografiaGrupoCidOs(grupoCidO.getSeq());
		cidOTreeNodeVO.setValor(grupoCidO);
		cidOTreeNodeVO.setRaiz(isRaiz);
		cidOTreeNodeVO.setLabel(grupoCidO.getDescricao() + "("+ grupoCidO.getSigla() + ")");
		return cidOTreeNodeVO;
	}

	public void onNodeExpand(NodeExpandEvent event) {

		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}

		CidOTreeNodeVO no = (CidOTreeNodeVO) nodo.getData();
		
//		List<CidOTreeNodeVO> listaFilhos = this.detalharSubGruposCidO(no.getSeqTopografiaGrupoCidOs());
		List<CidOTreeNodeVO> listaFilhos = no.getFilhos();

			nodo.getChildren().clear();
				
			for(CidOTreeNodeVO filho: listaFilhos){
				
				//filho.setLabel(no.getLabel());
				//filho.setSeqTopografiaGrupoCidOs(no.getSeqTopografiaGrupoCidOs());
				//filho.setValor(no.getValor());
				
				DefaultTreeNode novoGrupo = new DefaultTreeNode("cidGrupo", filho , nodo);
				if (filho.isRaiz()){
					new DefaultTreeNode("cid1", filho , novoGrupo);
				}
				//nodo.getChildren().add(novoFilho);
			}
	}
	
	/**
	 * Método para buscar o AghCapituloCid selecionado na árvore e atribuir a
	 * ele os elementos AghGruposCid correspondentes.
	 */
	public void detalharNodoGrupoCid(List<TreeNode> grupos) {

		for (Iterator<TreeNode> iterator = grupos.iterator(); iterator
				.hasNext();) {
			TreeNode nodoGrupo = iterator.next();
			GrupoCidVO grupoCidVO = (GrupoCidVO) nodoGrupo.getData();
			grupoCidVO.setCid1List(this.pesquisarCid1(grupoCidVO.getGrupoCid()
					.getId().getSeq(), nodoGrupo));
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

	public void onNodeSelect(NodeSelectEvent event) {
		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}
		CidOTreeNodeVO no = (CidOTreeNodeVO)nodo.getData(); 
		this.seqCidO = no.getSeqTopografiaGrupoCidOs();
		if (no.isRaiz() || seqCidO == null){
			return;
		} else {
			String retorno = this.selecionarCidArvoreEConfirmar();
			
        	NavigationHandler navigationHandler = FacesContext.
 	               getCurrentInstance().getApplication().getNavigationHandler();
 	        	navigationHandler.handleNavigation(FacesContext.getCurrentInstance(), null, retorno);

		}
	}

	private CidOTreeNodeVO montaTreeNodePorCidO(AelTopografiaCidOs cidO) {
		CidOTreeNodeVO cidOTreeNodeVO = new CidOTreeNodeVO();
		cidOTreeNodeVO.setValor(cidO);
		cidOTreeNodeVO.setFolha(true);
		cidOTreeNodeVO.setSeqTopografiaGrupoCidOs(cidO.getSeq());
		cidOTreeNodeVO.setLabel(cidO.getDescricao() + "(" + cidO.getCodigo()
				+ ")");
		return cidOTreeNodeVO;
	}

	private List<CidOTreeNodeVO> detalharSubGruposCidO(Long seqGrupo) {

		List<CidOTreeNodeVO> listaFilhos = new ArrayList<CidOTreeNodeVO>();

		List<AelTopografiaGrupoCidOs> listaSubGrupos = patologiaFacade
				.listarTopografiaGrupoCidOsPorGrupo(seqGrupo);
		for (AelTopografiaGrupoCidOs subGrp : listaSubGrupos) {
			CidOTreeNodeVO nodeSubGrp = montaTreeNodePorGrupo(subGrp, true);
			nodeSubGrp.setFilhos(detalharSubGruposCidO(subGrp.getSeq()));
			if ((!nodeSubGrp.getFilhos().isEmpty())) {
				listaFilhos.add(nodeSubGrp);
			}
		}
		List<AelTopografiaCidOs> listaCidOsSubGrupo = patologiaFacade
				.listarTopografiaCidOsPorGrupo(seqGrupo);
		for (AelTopografiaCidOs subCidO : listaCidOsSubGrupo) {
			CidOTreeNodeVO nodeSubCidO = montaTreeNodePorCidO(subCidO);
			listaFilhos.add(nodeSubCidO);
		}

		return listaFilhos;
	}

	/**
	 * Método para controlar quais áreas da árvore (TreeView) irão aparecer.
	 * 
	 * @param mostrarGrupoCidO
	 *            (nivel 0 da árvore)
	 * @param mostrarSubGrupos
	 *            (nivel 1 da árvore)
	 * @param mostrarCidO
	 *            (nivel 2 da árvore)
	 */
	private void configurarApresentacaoNodos(boolean mostrarGrupoCidO,
			boolean mostrarSubGrupos, boolean mostraNodoCidO) {
		this.mostrarGrupoCidO = mostrarGrupoCidO;
		this.mostrarSubGrupos = mostrarSubGrupos;
		this.mostraNodoCidO = mostraNodoCidO;
	}

	public String confirmar() {
		if (cidO == null) {
			seqCidO = null;
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CID_NAO_SELECIONADO");
			return null;
		} else {
			cadastroLaudoUnicoController.setCidOSeq(seqCidO);
			laudoUnicoController.getTelaVo().setSelectedTab(1);
			return "laudoUnico";// return
								// EnumTargetPesquisaCidO.CONFIRMADO.toString();
		}
	}

	public String cancelar() {
		return "laudoUnico";// return
							// EnumTargetPesquisaCidO.CANCELADO.toString();
	}

	public String selecionarCidArvoreEConfirmar() {
		cidO = patologiaFacade.obterCidOPorChavePrimaria(seqCidO);
		return confirmar();
	}

	// GETTERS E SETTERS

	public Set<Object> getNosExpandidos() {
		return this.nosExpandidos;
	}

	public void setNosExpandidos(Set<Object> nosExpandidos) {
		this.nosExpandidos = nosExpandidos;
	}

	public String getTipoSumario() {
		return tipoSumario;
	}

	public void setTipoSumario(String tipoSumario) {
		this.tipoSumario = tipoSumario;
	}

	public AelTopografiaCidOs getCidO() {
		return cidO;
	}

	public void setCidO(AelTopografiaCidOs cidO) {
		this.cidO = cidO;
	}

	public IExamesPatologiaFacade getPatologiaFacade() {
		return patologiaFacade;
	}

	public void setPatologiaFacade(IExamesPatologiaFacade patologiaFacade) {
		this.patologiaFacade = patologiaFacade;
	}

	public Long getSeqGrupoCidO() {
		return seqGrupoCidO;
	}

	public void setSeqGrupoCidO(Long seqGrupoCidO) {
		this.seqGrupoCidO = seqGrupoCidO;
	}

	public Long getSeqCidO() {
		return seqCidO;
	}

	public void setSeqCidO(Long seqCidO) {
		this.seqCidO = seqCidO;
	}

	public Boolean getMostrarGrupoCidO() {
		return mostrarGrupoCidO;
	}

	public void setMostrarGrupoCidO(Boolean mostrarGrupoCidO) {
		this.mostrarGrupoCidO = mostrarGrupoCidO;
	}

	public Boolean getMostrarSubGrupos() {
		return mostrarSubGrupos;
	}

	public void setMostrarSubGrupos(Boolean mostrarSubGrupos) {
		this.mostrarSubGrupos = mostrarSubGrupos;
	}

	public Boolean getMostraNodoCidO() {
		return mostraNodoCidO;
	}

	public void setMostraNodoCidO(Boolean mostraNodoCidO) {
		this.mostraNodoCidO = mostraNodoCidO;
	}

	public List<CidOTreeNodeVO> getListaGruposRaiz() {
		return listaGruposRaiz;
	}

	public void setListaGruposRaiz(List<CidOTreeNodeVO> listaGruposRaiz) {
		this.listaGruposRaiz = listaGruposRaiz;
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

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public List<AelTopografiaGrupoCidOs> getGruposCidO() {
		return gruposCidO;
	}

	public void setGruposCidO(List<AelTopografiaGrupoCidOs> gruposCidO) {
		this.gruposCidO = gruposCidO;
	}

	public TreeNode getNodoSelecionado() {
		return nodoSelecionado;
	}

	public void setNodoSelecionado(TreeNode nodoSelecionado) {
		this.nodoSelecionado = nodoSelecionado;
	}


	/*public TreeNode getRaiz() {
		return raiz;
	}


	public void setRaiz(TreeNode raiz) {
		this.raiz = raiz;
	}


	public Long getSeqNoSelecionado() {
		return seqNoSelecionado;
	}


	public void setSeqNoSelecionado(Long seqNoSelecionado) {
		this.seqNoSelecionado = seqNoSelecionado;
	}*/

}
