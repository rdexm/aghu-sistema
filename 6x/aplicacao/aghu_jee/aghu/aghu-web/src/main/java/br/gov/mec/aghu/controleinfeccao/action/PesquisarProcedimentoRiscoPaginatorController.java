package br.gov.mec.aghu.controleinfeccao.action;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.MciDuracaoMedidaPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarProcedimentoRiscoPaginatorController extends ActionController implements ActionPaginator {

	private static final String PAGINA_MANTER_PROCEDIMENTO_RISCO = "controleinfeccao-cadastroProcedimentoRisco";
	private static final long serialVersionUID = -5159107032113993399L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	// formulario
	private String descricao;
	
	private Short codigo;
	
	private DominioSituacao situacao;

	// lista
	@Inject @Paginator
	private DynamicDataModel<MciProcedimentoRisco> dataModel;
	
	private MciProcedimentoRisco selecionado = new MciProcedimentoRisco();
	
	private List<MciProcedimentoRisco> lista = new ArrayList<MciProcedimentoRisco>();
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterProcRisco", "manter");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}


	public void limpar() {	
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.dataModel.setPesquisaAtiva(false);
		dataModel.limparPesquisa();
		lista = new ArrayList<MciProcedimentoRisco>();
	}
	
	
	public String novo(){
		return PAGINA_MANTER_PROCEDIMENTO_RISCO;
	}
	
	public String editar(){
		return PAGINA_MANTER_PROCEDIMENTO_RISCO;
	}
	
	public void excluir()  {
		try {
			String descricao = selecionado.getDescricao();
			controleInfeccaoFacade.validarRemoverProcedimentoRisco(selecionado.getSeq(), selecionado.getCriadoEm());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PR",descricao);
		}catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}	
	
	@Override
	public Long recuperarCount() {
		Long count = controleInfeccaoFacade.pesquisarMciProcedRiscoPorSeqDescricaoSituacaoCount(this.codigo, this.descricao, this.situacao);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public  List<MciProcedimentoRisco> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		lista = controleInfeccaoFacade.pesquisarMciProcedRiscoPorSeqDescricaoSituacao(this.codigo, this.descricao, this.situacao, firstResult, maxResult, orderProperty, asc);
		return lista;
		//return populaVO(lista);
	}
	

	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public DominioSituacao getSituacao() {
		return situacao;
	}


	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public MciProcedimentoRisco getSelecionado() {
		return selecionado;
	}


	public void setSelecionado(MciProcedimentoRisco selecionado) {
		this.selecionado = selecionado;
	}


	public DynamicDataModel<MciProcedimentoRisco> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MciProcedimentoRisco> dataModel) {
		this.dataModel = dataModel;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public List<MciDuracaoMedidaPreventivasVO> populaVO(List<MciDuracaoMedidaPreventiva> lista){
		List<MciDuracaoMedidaPreventivasVO> listaVO = new ArrayList<MciDuracaoMedidaPreventivasVO>();
		for (MciDuracaoMedidaPreventiva linha : lista) {
			MciDuracaoMedidaPreventivasVO item = new MciDuracaoMedidaPreventivasVO();
			item.setSeq(linha.getSeq());
			item.setDescricao(linha.getDescricao());
			item.setCriadoEm(linha.getCriadoEm());
			item.setSituacao(linha.getSituacao());	
			if(linha.getAlteradoEm() != null){
				item.setCriadoAlterado(linha.getAlteradoEm());	
			}else if(linha.getCriadoEm() != null){
				item.setCriadoAlterado(linha.getCriadoEm());	
			}else{
				item.setCriadoAlterado(null);
			}
			listaVO.add(item);
		}
		return listaVO;
	}


	public List<MciProcedimentoRisco> getLista() {
		return lista;
	}


	public void setLista(List<MciProcedimentoRisco> lista) {
		this.lista = lista;
	}
}
