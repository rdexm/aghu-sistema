package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AreaTecnicaAvaliacaoController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6899793556147295384L;
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	@EJB 
	private IParametroFacade parametroFacade;
	@EJB
	private IPermissionService permissionService;
	
	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;	
	private final String PAGE_PESQUISAR_AREA_TECNICA = "areaTecnicaAvaliacaoList";		
	private boolean isUpdate;		
	private boolean checked;
	private Integer codigoCcOriginal;	
	private List<RapServidores> rapServidoresList = new ArrayList<RapServidores>();	
	private Long rapServidoresListCount ;
	
	private boolean pm01;
	private boolean pm02;
	private boolean pm03;
	private static final String ACAO_EXECUTAR = "executar";
	private static final String PERMISSAO_CRUD_AREA_TECNICA = "manterOficinas";
	private static final String PERMISSAO_CRUD_MENSAGEM_EMAIL = "manterMensagemEmail";
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
		areaTecnicaAvaliacao=new PtmAreaTecAvaliacao();
	}
	
	public void iniciar(){
		if(isUpdate){
			if(areaTecnicaAvaliacao.getSituacao() != null){
				checked = areaTecnicaAvaliacao.getSituacao().isAtivo();
			}
		}else{
			checked = Boolean.TRUE;
		}
		
		carregarPermissoes();
	}
	
	/**
	 * Verifica as permissões que o usuario possui.
	 */
	private void carregarPermissoes() {
		this.pm01 = usuarioTemPermissao(PERMISSAO_CRUD_AREA_TECNICA);
		this.pm02 = usuarioTemPermissao(PERMISSAO_CRUD_MENSAGEM_EMAIL);
	}
	
	/**
	 * Invoca metodo da arquitetura para validar permissão do usuario.
	 */
	private boolean usuarioTemPermissao(String componente) {
		return this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), componente, ACAO_EXECUTAR);
	}
	
	public String confirmar() throws ApplicationBusinessException{
		
		try{
			String mensagemSucesso = null;
			Boolean ccustoComArea = false;

			List<PtmAreaTecAvaliacao> listaAreaCentroCusto = patrimonioFacade.listarAreaTecAvaliacaoPorCodigoCentroCusto(areaTecnicaAvaliacao.getFccCentroCustos().getCodigo());
			
			if(areaTecnicaAvaliacao.getSeq() == null){
				mensagemSucesso = "MENSAGEM_SUCESSO_GRAVAR_OFICINA";
				if(!listaAreaCentroCusto.isEmpty()){
					ccustoComArea = true;
				}
			}else{
				mensagemSucesso = "MENSAGEM_SUCESSO_EDITAR_OFICINA";
				if(!listaAreaCentroCusto.isEmpty() && !listaAreaCentroCusto.get(0).getFccCentroCustos().getCodigo().equals(codigoCcOriginal)){
					ccustoComArea = true;
				}
			}
			
			if(checked){
				areaTecnicaAvaliacao.setSituacao(DominioSituacao.A);
			}else{
				if(patrimonioFacade.pesquisarQtdeItemRecebAssociadoAreaTecnica(areaTecnicaAvaliacao.getSeq()) > 0){
					apresentarMsgNegocio(Severity.INFO, "DESATIVA_AREA_TECNICA");
					return null;
				}
				areaTecnicaAvaliacao.setSituacao(DominioSituacao.I);
			}
			
			if(areaTecnicaAvaliacao.getServidorCC() != null && areaTecnicaAvaliacao.getServidorCC().getUsuario() != null && !ccustoComArea){
				try {
					patrimonioFacade.persistirAreaTecnicaAvaliacao(areaTecnicaAvaliacao, AghuParametrosEnum.P_AGHU_PERFIL_CHEFE_AREA_AVAL);
				} catch (ApplicationBusinessException e) {
					apresentarMsgNegocio(Severity.ERROR, e.getMessage());
					return null;
				}
				
			}else if((areaTecnicaAvaliacao.getServidorCC() != null && areaTecnicaAvaliacao.getServidorCC().getUsuario() == null) || areaTecnicaAvaliacao.getServidorCC() == null) {
				apresentarMsgNegocio(Severity.INFO, "USUARIO_NAO_ENCONTRADO");
				return null;
			}
			else{
				apresentarMsgNegocio(Severity.INFO, "CENTRO_CUSTO_DUPLICADO");
				return null;
			}
			
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			
			return cancelar();
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
			return "exception/erro";
		}
	}
	
	public String cancelar() {
		rapServidoresList.clear();
		areaTecnicaAvaliacao=new PtmAreaTecAvaliacao();
		return PAGE_PESQUISAR_AREA_TECNICA;
	}
	
	/**
	 * 
	 * Obtem servidor reponsavel pela area tecnica do suggestion
	 */
	public List<RapServidores> obterResponsavelAreaTec(final String objPesquisa){		
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){			
			return	this.registroColaboradorFacade.pesquisarServidorPorVinculoOuMatriculaOuNome(objPesquisa);
		}else {			
			return this.registroColaboradorFacade.listarTodosServidoresOrdernadosPorNome();
		}
	}
	
	public Long obterResponsavelAreaTecCount(final String objPesquisa){
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidorPorVinculoOuMatriculaOuNomeCount(objPesquisa);
		}else {
			return this.registroColaboradorFacade.pesquisarRapServidoresCount();
		}
	}
	
	/**
	 * 
	 * Obtem o centro de custo da suggestion
	 */
	public List<FccCentroCustos> obterCentroCusto(final String objPesquisa){
		if (objPesquisa!=null && !StringUtils.EMPTY.equalsIgnoreCase((String) objPesquisa)){
			return returnSGWithCount(
					this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemOuDescricao(objPesquisa),			
					this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemOuDescricaoCount(objPesquisa));
		}else {
			return returnSGWithCount(
					this.centroCustoFacade.pesquisarCentroCustosAtivos(objPesquisa),
					this.centroCustoFacade.obterCentroCustoAtivosCount());
		}
	}
	
	public Long obterCentroCustoCount(final String objPesquisa){
		if (objPesquisa!=null && !StringUtils.EMPTY.equalsIgnoreCase((String) objPesquisa)){
			return  this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricaoCountL(objPesquisa);
		}else {
			return this.centroCustoFacade.pesquisarCentroCustosAtivosCount(objPesquisa);
		}
	}
	
	/**
	 * RN12 #43443 
	 *  Ao fazer a busca no SB de centro de custo, busca o responsavel mais acima ate encontrar com centro de custo anterior ao presidente (caso extremamente raro)
	 * @throws ApplicationBusinessException 
	 */
	public void popularSBResponsavelAreaTec() throws ApplicationBusinessException{
		try{
			rapServidoresList.clear();
			FccCentroCustos fccCentroCustos = this.centroCustoFacade.pesquisarCentroCustoAtivoPorCodigo(areaTecnicaAvaliacao.getFccCentroCustos().getCodigo());
			areaTecnicaAvaliacao.setNomeAreaTecAvaliacao(fccCentroCustos.getDescricao());	
			fccCentroCustos = patrimonioFacade.buscarCentroCustoResponsavelSuperior(fccCentroCustos, AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PRESIDENCIA);
			
			if(fccCentroCustos.getRapServidor() != null){
				rapServidoresList = this.registroColaboradorFacade.pesquisarServidorPorMatriculaNome(fccCentroCustos.getRapServidor().getId().getMatricula().toString());
				areaTecnicaAvaliacao.setServidorCC(rapServidoresList.get(0));
				rapServidoresList.clear();
			}
			RequestContext.getCurrentInstance().update("nomeAreaTecnica");
			RequestContext.getCurrentInstance().update("sbResponsavelArea");
		}catch(BaseException e){
			RequestContext.getCurrentInstance().update("nomeAreaTecnica");
			RequestContext.getCurrentInstance().update("sbResponsavelArea");
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * verifica se ha lista de responsaveis preenchida pelo SB do centro de custo, caso estiver vazia busca todos
	 * @param objPesquisa
	 * @return
	 */
	public List<RapServidores> obterListaResponsavelPorCentroCusto(final String objPesquisa){
		if(getRapServidoresList().isEmpty() || getRapServidoresList() == null){
			return returnSGWithCount(
					this.obterResponsavelAreaTec(objPesquisa),

					this.obterResponsavelAreaTecCount(objPesquisa));
			
		}else{
			return returnSGWithCount(
					this.getRapServidoresList(),

					this.rapServidoresListCount);
		}
	}
	
	public PtmAreaTecAvaliacao getAreaTecnicaAvaliacao() {
		return areaTecnicaAvaliacao;
	}

	public void setAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao) {
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
	}
	
	public IPatrimonioFacade getPatrimonioFacade() {
		return patrimonioFacade;
	}


	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}


	public boolean isUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<RapServidores> getRapServidoresList() {
		return rapServidoresList;
	}

	public void setRapServidoresList(List<RapServidores> rapServidoresList) {
		this.rapServidoresList = rapServidoresList;
	}

	public Long getRapServidoresListCount() {
		return rapServidoresListCount;
	}

	public void setRapServidoresListCount(Long rapServidoresListCount) {
		this.rapServidoresListCount = rapServidoresListCount;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public Integer getCodigoCcOriginal() {
		return codigoCcOriginal;
	}

	public void setCodigoCcOriginal(Integer codigoCcOriginal) {
		this.codigoCcOriginal = codigoCcOriginal;
	}

	public boolean isPm01() {
		return pm01;
	}

	public void setPm01(boolean pm01) {
		this.pm01 = pm01;
	}

	public boolean isPm02() {
		return pm02;
	}

	public void setPm02(boolean pm02) {
		this.pm02 = pm02;
	}

	public boolean isPm03() {
		return pm03;
	}

	public void setPm03(boolean pm03) {
		this.pm03 = pm03;
	}
	
}
