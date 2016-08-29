package br.gov.mec.aghu.exames.patologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelItemConfigExameId;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterItemConfiguracaoExamesPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelItemConfigExame> dataModel;

	private static final long serialVersionUID = -7194628993807554445L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Integer lu2Seq;
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AghUnidadesFuncionais unidadeExecutora;
	private AelExamesMaterialAnalise exame;
	private AelItemConfigExame item;
	private AelItemConfigExame itemOld;
	private Boolean sair = false;
	
	private List<AelItemConfigExame> listaItens = new ArrayList<AelItemConfigExame>(0);
	private String exaSigla;
	
	public enum ManterItemConfiguracaoExamesPaginatorControllerExceptionCode implements
	BusinessExceptionCode {
		EXAME_ITEM_CONFIG_EXAME_JA_CADASTRADO;
	}

	public void inicio() {
		if(!sair) {
			item = new AelItemConfigExame();
			exame = null;
			
			// Obtem o usuario da unidade executora
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				usuarioUnidadeExecutora=null;
			}
	
			// Resgata a unidade executora associada ao usuario
			if(this.usuarioUnidadeExecutora != null){
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
			sair = true;
		}
	}
	
	@Override
	public Long recuperarCount() {
		return this.examesPatologiaFacade.listarItemConfigExamesCount(lu2Seq);
	}

	@Override
	public List<AelItemConfigExame> recuperarListaPaginada(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc) {
		listaItens = this.examesPatologiaFacade.listarItemConfigExames(firstResult, maxResults, lu2Seq);
		return listaItens;
	}

	public List<AelExamesMaterialAnalise> buscarExamesMaterialAnaliseUnfExecExames(Object objPesquisa){
		return this.examesPatologiaFacade.listarExamesMaterialAnaliseUnfExecExames(objPesquisa, unidadeExecutora.getSeq());
	}

	public void excluirItem() {
		try {
			for(AelItemConfigExame i : listaItens) {
				if(i.getId().getUfeEmaExaSigla().equals(exaSigla)) {
					this.item = this.examesPatologiaFacade.obterItemConfigExame(i.getId());
					break;
				}
			}
			
			itemOld = this.examesPatologiaFacade.clonarItemConfigExame(item);
			this.examesPatologiaFacade.excluirItemConfigExame(item, itemOld);
			dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio( Severity.INFO, 
			"MENSAGEM_AEL_ITEM_CONFIG_EXAMES_EXCLUSAO_SUCESSO");
			exaSigla = null;
			this.item = null;
		}
		catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravar() {
		try {
			item = new AelItemConfigExame();
			if(exame != null) {
				item.setId(new AelItemConfigExameId(lu2Seq, exame.getId().getExaSigla(), exame.getId().getManSeq(), this.unidadeExecutora.getSeq()));
			}
			
			if(this.examesPatologiaFacade.obterItemConfigExame(item.getId()) != null) {
				item = null;
				throw new ApplicationBusinessException(ManterItemConfiguracaoExamesPaginatorControllerExceptionCode.EXAME_ITEM_CONFIG_EXAME_JA_CADASTRADO);
			}
			
			this.examesPatologiaFacade.persistirItemConfigExame(item, null, true);
			dataModel.reiniciarPaginator();
			exame = null;
			
			this.apresentarMsgNegocio( Severity.INFO, 
					"MENSAGEM_AEL_ITEM_CONFIG_EXAMES_INCLUSAO_SUCESSO");				
		}
		catch(BaseException e) {
			item = null;
			apresentarExcecaoNegocio(e);
		}
	}

	
	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

	public AelExamesMaterialAnalise getExame() {
		return exame;
	}

	public void setExame(AelExamesMaterialAnalise exame) {
		this.exame = exame;
	}

	public Boolean getSair() {
		return sair;
	}

	public void setSair(Boolean sair) {
		this.sair = sair;
	}

	public String getExaSigla() {
		return exaSigla;
	}

	public void setExaSigla(String exaSigla) {
		this.exaSigla = exaSigla;
	} 


	public DynamicDataModel<AelItemConfigExame> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelItemConfigExame> dataModel) {
	 this.dataModel = dataModel;
	}
}
