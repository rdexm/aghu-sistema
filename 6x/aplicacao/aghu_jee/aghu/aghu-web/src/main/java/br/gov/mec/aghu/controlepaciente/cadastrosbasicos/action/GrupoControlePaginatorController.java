package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoControlePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5177544999075176700L;

	private static final String MANTER_GRUPO_CONTROLE = "manterGrupoControle";

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	private EcpGrupoControle ecpGrupoControle = new EcpGrupoControle();
	
	private String descricao;
	
	@Inject @Paginator
	private DynamicDataModel<EcpGrupoControle> dataModel;
	
	private EcpGrupoControle selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<EcpGrupoControle> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,boolean asc) {
		return cadastrosBasicosControlePacienteFacade.pesquisarGruposControle( firstResult, maxResult, orderProperty,asc, 
																			   ecpGrupoControle.getSeq(), 
																			   descricao, null, 
																			   ecpGrupoControle.getSituacao(),
																			   ecpGrupoControle.getTipo());
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosControlePacienteFacade.listarGruposControleCount( ecpGrupoControle.getSeq(),
																				 descricao, null, 
																			     ecpGrupoControle.getSituacao(), 
																			     ecpGrupoControle.getTipo());
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String editar(){
		return MANTER_GRUPO_CONTROLE;
	}

	public String inserir(){
		return MANTER_GRUPO_CONTROLE;
	}

	public void limpar() {
		dataModel.limparPesquisa();
		this.ecpGrupoControle = new EcpGrupoControle();
		setDescricao(null);
	} 

	public void excluir() {
		if(selecionado != null){
			try {
				cadastrosBasicosControlePacienteFacade.excluirGrupoControle(selecionado.getSeq());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_MODELO_BASICO_PRESCRICAO",selecionado.getSeq());
			} catch (ApplicationBusinessException e) {	
				apresentarExcecaoNegocio(e);
			}
			this.selecionado = null;
			this.limpar();
			this.dataModel.reiniciarPaginator();
		}
	} 

	public EcpGrupoControle getEcpGrupoControle() {
		return ecpGrupoControle;
	}

	public void setEcpGrupoControle(EcpGrupoControle ecpGrupoControle) {
		this.ecpGrupoControle = ecpGrupoControle;
	} 

	public DynamicDataModel<EcpGrupoControle> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EcpGrupoControle> dataModel) {
		this.dataModel = dataModel;
	}

	public EcpGrupoControle getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(EcpGrupoControle selecionado) {
		this.selecionado = selecionado;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
