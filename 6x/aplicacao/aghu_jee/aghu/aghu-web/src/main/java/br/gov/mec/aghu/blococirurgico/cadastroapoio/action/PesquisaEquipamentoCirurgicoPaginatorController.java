package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisaEquipamentoCirurgicoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 this.setEquipamentoCirurgicoSelecionado(new MbcEquipamentoCirurgico());
	}

	@Inject @Paginator
	private DynamicDataModel<MbcEquipamentoCirurgico> dataModel;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8755652074344311298L;
	
	//Campos filtro
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;

	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	//Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;
	
	//private final String ordenacaoColuna = " asc";
	
	private MbcEquipamentoCirurgico equipamentoCirurgicoSelecionado;
	
	private final String PAGE_CAD_EQUIP_CIRUR =  "equipamentoCirurgicoCRUD";
	
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		/*this.setOrder(
				MbcEquipamentoCirurgico.Fields.DESCRICAO.toString() 
					+ this.ordenacaoColuna);*/
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limpar() {
		//Limpa filtro
		codigo = null;
		descricao = null;
		situacao = null;
		exibirBotaoNovo = false;
		this.dataModel.limparPesquisa();
		
	}
	
	@Override
	public List<MbcEquipamentoCirurgico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return blocoCirurgicoFacade.pesquisarEquipamentoCirurgico(
				this.codigo, this.descricao, this.situacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long recuperarCount() {
		return blocoCirurgicoFacade.
			pesquisarEquipamentoCirurgicoCount(this.codigo, this.descricao, this.situacao);
	}
	
	/**
	 * Chama a tela de inclusão
	 * @return
	 */
	public String novo(){		
		this.setEquipamentoCirurgicoSelecionado(null);
		return PAGE_CAD_EQUIP_CIRUR;
	}
	public String editar(){		
		return PAGE_CAD_EQUIP_CIRUR;
	}
	
	

	/** GET/SET **/
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
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

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
 


	public DynamicDataModel<MbcEquipamentoCirurgico> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcEquipamentoCirurgico> dataModel) {
	 this.dataModel = dataModel;
	}

	public MbcEquipamentoCirurgico getEquipamentoCirurgicoSelecionado() {
		return equipamentoCirurgicoSelecionado;
	}

	public void setEquipamentoCirurgicoSelecionado(
			MbcEquipamentoCirurgico equipamentoCirurgicoSelecionado) {
		this.equipamentoCirurgicoSelecionado = equipamentoCirurgicoSelecionado;
	}
}
