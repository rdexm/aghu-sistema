package br.gov.mec.aghu.exames.patologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
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
import br.gov.mec.aghu.core.exception.Severity;


public class ManterConfiguracaoExamesController extends ActionController  implements ActionPaginator {

	private static final long serialVersionUID = -2535574616062197785L;
	
	private static final String MANTER_CONFIGURACAO_EXAMES_LIST = "manterConfiguracaoExamesList";
	private static final String MANTER_CARACTERISTICA_MATERIAL_ANALISE = "exames-manterCaracteristicaMaterialAnalise";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IExamesFacade examesFacade;
	
	
	private AelConfigExLaudoUnico configExame;

	private Boolean laudoAnteriorCad;

	
	@Inject @Paginator
	private DynamicDataModel<AelItemConfigExame> dataModel;
	
	private AelItemConfigExame selecionado;

	private AelItemConfigExame item;
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	
	private AghUnidadesFuncionais unidadeExecutora;

	private AelExamesMaterialAnalise exame;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(configExame != null && configExame.getSeq() != null) {
			this.configExame = this.examesPatologiaFacade.obterConfigExameLaudoUncioPorChavePrimaria(configExame.getSeq());
			
			if(configExame == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}

			item = new AelItemConfigExame();
			this.laudoAnteriorCad = this.configExame.getLaudoAnterior();
			
			dataModel.reiniciarPaginator();
			
		} else {
			this.configExame = new AelConfigExLaudoUnico();
			this.configExame.setSituacao(DominioSituacao.A);
			this.laudoAnteriorCad = Boolean.TRUE;
			item = new AelItemConfigExame();
		}
		
		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			usuarioUnidadeExecutora=null;
		}
		
		// Resgata a unidade executora associada ao usuario
		if(this.usuarioUnidadeExecutora != null){
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
		
		return null;
	
	}

	
	public String voltar() {
		this.configExame = null;
		exame = null;
		return MANTER_CONFIGURACAO_EXAMES_LIST;
	}
	
	public String manterCaracteristicaMaterialAnalise() {
		return MANTER_CARACTERISTICA_MATERIAL_ANALISE;
	}

	
	public String gravar() {
		try {

			boolean inclusao = configExame.getSeq() == null;
			configExame.setLaudoAnterior((laudoAnteriorCad!=null) ? true : false);
			configExame = this.examesPatologiaFacade.persistirConfigLaudoUnico(configExame);
			if(inclusao) {
				this.examesPatologiaFacade.persisteSecaoConfigurcaoExames(configExame);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AEL_CONFIG_EX_LAUDO_UNICOS_INCLUSAO_SUCESSO");
				
			} else {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AEL_CONFIG_EX_LAUDO_UNICOS_EDICAO_SUCESSO");								
			}
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	
	// ###############  ITEM ############### 

	@Override
	public List<AelItemConfigExame> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(configExame == null){
			return new ArrayList<>();
		}
		return examesPatologiaFacade.listarItemConfigExames(firstResult, maxResult, configExame.getSeq());
	}

	@Override
	public Long recuperarCount() {
		if(configExame == null){
			return 0l;
		}
		return examesPatologiaFacade.listarItemConfigExamesCount(configExame.getSeq());
	}
	
	public List<AelExamesMaterialAnalise> buscarExamesMaterialAnaliseUnfExecExames(String objPesquisa){
		return examesPatologiaFacade.listarExamesMaterialAnaliseUnfExecExames(objPesquisa, unidadeExecutora.getSeq());
	}

	public void excluirItem() {
		try {			
			this.examesPatologiaFacade.excluirItemConfigExame(selecionado, selecionado);
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AEL_ITEM_CONFIG_EXAMES_EXCLUSAO_SUCESSO");
			selecionado = null;
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarItem() {
		try {
			item = new AelItemConfigExame();
			if(configExame != null) {
				item.setId(new AelItemConfigExameId( configExame.getSeq(), 
													 exame.getId().getExaSigla(), exame.getId().getManSeq(), 
													 unidadeExecutora.getSeq()));
			}
			
			if(examesPatologiaFacade.obterItemConfigExame(item.getId()) != null) {
				item = null;
				apresentarMsgNegocio(Severity.ERROR, "EXAME_ITEM_CONFIG_EXAME_JA_CADASTRADO");
				return;
			}

			examesPatologiaFacade.persistirItemConfigExame(item, null, true);
			dataModel.reiniciarPaginator();
			exame = null;
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_ITEM_CONFIG_EXAMES_INCLUSAO_SUCESSO");				
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	
	public AelConfigExLaudoUnico getConfigExame() {
		return configExame;
	}

	public void setConfigExame(AelConfigExLaudoUnico configExame) {
		this.configExame = configExame;
	}

	public Boolean getLaudoAnteriorCad() {
		return laudoAnteriorCad;
	}

	public void setLaudoAnteriorCad(Boolean laudoAnteriorCad) {
		this.laudoAnteriorCad = laudoAnteriorCad;
	}

	public DynamicDataModel<AelItemConfigExame> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelItemConfigExame> dataModel) {
		this.dataModel = dataModel;
	}

	public AelItemConfigExame getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelItemConfigExame selecionado) {
		this.selecionado = selecionado;
	}

	public AelItemConfigExame getItem() {
		return item;
	}

	public void setItem(AelItemConfigExame item) {
		this.item = item;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelExamesMaterialAnalise getExame() {
		return exame;
	}

	public void setExame(AelExamesMaterialAnalise exame) {
		this.exame = exame;
	}
}