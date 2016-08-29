package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaComposicaoNptPadrao;
import br.gov.mec.aghu.model.AfaComposicaoNptPadraoId;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaItemNptPadrao;
import br.gov.mec.aghu.model.AfaItemNptPadraoId;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AfaComposicaoNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaItemNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.TipoComposicaoComponenteVMpmDosagemVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarFormulaNPTController extends ActionController{	/**
	 * 
	 */
	private static final long serialVersionUID = -7843121208904763371L;

	private List<DominioSituacao> situacoes;

	private Short codigo;
	
	private String descricao = StringUtils.EMPTY;
	
	private String observacao = StringUtils.EMPTY;
	
	private String descricaoToolTip = StringUtils.EMPTY; 
	
	private DominioSimNao padrao;
	
	private DominioSimNao pediatrico;
	
	private DominioSituacao situacao;
	
	private AfaComposicaoNptPadrao composicaoNptPadraoExcluir = new AfaComposicaoNptPadrao();
	
	private AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVOExcluir = new AfaComposicaoNptPadraoVO();
	
	private AfaItemNptPadraoVO afaItemNptPadraoVOExcluir = new AfaItemNptPadraoVO();
	
	private AfaItemNptPadrao afaItemNptPadraoExcluir = new AfaItemNptPadrao();
	
	private AfaFormulaNptPadrao afaFormulaNptPadrao = new AfaFormulaNptPadrao();
	
	private AfaFormulaNptPadrao formulaNptPadraoExcluir = new AfaFormulaNptPadrao();
	
	private AfaFormulaNptPadrao afaFormulaNptPadraoTemp = new AfaFormulaNptPadrao();
	
	private AfaFormaDosagem afaFormaDosagemUP = new AfaFormaDosagem();
	
	private AfaComposicaoNptPadrao afaComposicaoNptPadraoTemp = new AfaComposicaoNptPadrao();
	
	private AfaComposicaoNptPadraoId afaComposicaoNptPadraoIdTemp = new AfaComposicaoNptPadraoId();
	
	private AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVO = new AfaComposicaoNptPadraoVO();
	
	private AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVOTemp = new AfaComposicaoNptPadraoVO();
	
	private AfaItemNptPadraoId afaItemNptPadraoId = new AfaItemNptPadraoId();
	
	private Short seqComposicaoParametroComponente;
	
	private Short fnpSeqComposicao;
	
	private Short seqPComposicao;
	
	private AfaItemNptPadrao afaItemNptPadrao = new AfaItemNptPadrao();
	
	private VMpmDosagem vMpmDosagemTemp = new VMpmDosagem();
	
	private boolean pesquisaAtiva = false;
	
	private boolean pesquisaAtiva2 = false;
	
	private boolean pesquisaAtiva3 = false;

	private List<AfaFormulaNptPadrao> lista = new ArrayList<AfaFormulaNptPadrao>();
	
	private List<AfaComposicaoNptPadraoVO> lista2 = new ArrayList<AfaComposicaoNptPadraoVO>();
	
	private List<AfaItemNptPadraoVO> lista3 = new ArrayList<AfaItemNptPadraoVO>();
	
	private List<AfaItemNptPadraoVO> lista4 = new ArrayList<AfaItemNptPadraoVO>();
	
	private List<AfaItemNptPadraoVO> listaComponenteVO = new ArrayList<AfaItemNptPadraoVO>();
	
	private RapServidores servidorLogado;
	
	private AfaTipoComposicoes afaTipoComposicoes;
	
	private Short velocidadeAdministracao;
	
	private Double qtdeComponente;
	
	private TipoComposicaoComponenteVMpmDosagemVO tipoComposicaoComponenteVMpmDosagemVO;
	
	private AfaTipoVelocAdministracoes afaTipoVelocAdministracoes;
	
	private VMpmDosagem vMpmDosagem;
	
	private boolean situacaoManter;
	
	private boolean situacaoManter2;
	
	private boolean edicaoComposicao = false;
	
	private boolean edicaoComponente = false;

	private Boolean permissao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
    private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
    private IRegistroColaboradorFacade iRegistroColaboradorFacade;

//	@EJB
//	private AfaFormulaNtpPadraoRN afaFormulaNtpPadraoRN;

	private Boolean modoEdicao = false;
	
	private Boolean modoEdicao2 = false;
	
	private Boolean retornoFormulaCrud = false;
	
	private static final String CRUD_NPT = "prescricaomedica-formulaNPTCRUD";
	private static final String COMPONENTES_NTP = "prescricaomedica-pesquisaComponentesNPT";
	private static final String GRUPOS_COMPONENTES_NTP = "prescricaomedica-pesquisarGruposComponentesNPT";
	private static final String vazio= "";              

	
	
	@PostConstruct
	public void inicio() {
		begin(conversation, true);
	}
	
	public String redirectComponente(){
		return COMPONENTES_NTP;
	}
	
	public String redirectGrupo(){
		return GRUPOS_COMPONENTES_NTP;
	}

	public void init() throws ApplicationBusinessException {

		permissao = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterCadastrosNPT", "manter");
		setServidorLogado(getServidorLogadoFacade().obterServidorLogado());
//		limparPesquisa();
		servidorLogado = obterServidor();
		if(retornoFormulaCrud){
			pesquisar();
		}

	}
	
	private RapServidores obterServidor() throws ApplicationBusinessException{

        return iRegistroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());

	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
        return this.servidorLogadoFacade;
	}
	
	public String novaFormulaNpt(){
//		codigo
//		situacao
//		pediatrico
//		padrao
//		descricao
//		observacao
		return CRUD_NPT;
	}
	
	public void limpar() {
		limparPesquisa();
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
	}
	
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}


	
	public void limparPesquisa() {
		afaFormulaNptPadrao = new AfaFormulaNptPadrao();
		afaComposicaoNptPadraoVO = new AfaComposicaoNptPadraoVO();
		codigo = null;
		descricao = vazio;
		situacao = null;
		observacao = vazio;
		padrao = null;
		pediatrico = null;
		lista.clear();
		lista2.clear();
		lista3.clear();
		pesquisaAtiva = false;
		pesquisaAtiva2 = false;
		pesquisaAtiva3 = false;
	}
	
	public void pesquisar() {
		afaFormulaNptPadrao = new AfaFormulaNptPadrao();
		afaFormulaNptPadrao.setSeq(codigo);
		if(descricao == null){
			afaFormulaNptPadrao.setDescricao(vazio);
		}else{
			afaFormulaNptPadrao.setDescricao(descricao);
		}
		
		if(pediatrico != null){
			afaFormulaNptPadrao.setIndFormulaPediatrica(pediatrico.getDescricao());
		}
		
		if(padrao != null){
			afaFormulaNptPadrao.setIndPadrao(padrao.getDescricao());
		}
		
		if(situacao != null){
			afaFormulaNptPadrao.setIndSituacao(situacao.getDescricao());
		}
		
		if(observacao == null){
			afaFormulaNptPadrao.setObservacao(vazio);
		}else{
			afaFormulaNptPadrao.setObservacao(observacao);
		}
		
				
		lista = prescricaoMedicaFacade.obterFormulaNptPadrao(afaFormulaNptPadrao);
		if(!lista.isEmpty()){
			setPesquisaAtiva(true);
		}
	}
	
	public void exluiFormulaNptPadrao(){
		try {
			if(formulaNptPadraoExcluir != null){
				prescricaoMedicaFacade.excluiFormulaNptPadrao(formulaNptPadraoExcluir);
				lista.remove(formulaNptPadraoExcluir);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		
	}
	
	
	/**
	 * Lista de grupos de composições relacionados com a linha selecionada na grid principal
	 */
	public void pesquisarListaComposicao(){
		afaTipoComposicoes = null;
		velocidadeAdministracao = null;
		afaTipoVelocAdministracoes = null;
		lista2.clear();
		lista3.clear();
		lista2 = prescricaoMedicaFacade.obterListaComposicaoNptPadraoVO(afaComposicaoNptPadraoVO.getFormulaSeq());
		if(!lista2.isEmpty()){
			setPesquisaAtiva2(true);
		}
	}
	
	
	public void adicionarComposicao() throws ApplicationBusinessException{
		if(preInsertComposicao()){
			AfaComposicaoNptPadrao afaComposicaoPadraoAdd = new AfaComposicaoNptPadrao();
			AfaComposicaoNptPadraoId afaComposicaoNptPadraoIdAdd = new AfaComposicaoNptPadraoId();
			
			if(edicaoComposicao == false){
				afaComposicaoNptPadraoIdAdd.setFnpSeq(afaComposicaoNptPadraoVO.getFormulaSeq());
				afaComposicaoNptPadraoIdAdd.setSeqp(afaTipoComposicoes.getSeq());
				
				afaComposicaoPadraoAdd.setAfaTipoComposicoes(afaTipoComposicoes);
				afaComposicaoPadraoAdd.setId(afaComposicaoNptPadraoIdAdd);
				afaComposicaoPadraoAdd.setVelocidadeAdministracao(velocidadeAdministracao);
				afaComposicaoPadraoAdd.setAfaTipoVelocAdministracoes(afaTipoVelocAdministracoes);
				afaComposicaoPadraoAdd.setRapServidores(servidorLogado);
				afaComposicaoPadraoAdd.setAlteradoEm(new Date());
				afaComposicaoPadraoAdd.setCriadoEm(new Date());
				afaComposicaoPadraoAdd.setVersion(0);
				prescricaoMedicaFacade.salvarComposicao(afaComposicaoPadraoAdd);
				
				afaTipoComposicoes = null;
				velocidadeAdministracao = null;
				afaTipoVelocAdministracoes = null;
					
			}else{
				edicaoComposicao = false;
				
				afaComposicaoPadraoAdd.setId(afaComposicaoNptPadraoIdTemp);
				
				afaComposicaoPadraoAdd.setAfaTipoComposicoes(afaTipoComposicoes);
				afaComposicaoPadraoAdd.setVelocidadeAdministracao(velocidadeAdministracao);
				afaComposicaoPadraoAdd.setAfaTipoVelocAdministracoes(afaTipoVelocAdministracoes);
				afaComposicaoPadraoAdd.setRapServidores(servidorLogado);
				afaComposicaoPadraoAdd.setAlteradoEm(new Date());
				afaComposicaoPadraoAdd.setVersion(1);
				
				prescricaoMedicaFacade.atualizarComposicao(afaComposicaoPadraoAdd);
				
				afaTipoComposicoes = null;
				velocidadeAdministracao = null;
				afaTipoVelocAdministracoes = null;
				modoEdicao = false;
				
				
			}
			
			lista2 = prescricaoMedicaFacade.obterListaComposicaoNptPadraoVO(afaComposicaoNptPadraoVO.getFormulaSeq());
		}
		
			
		}
	
	private boolean preInsertComposicao() throws ApplicationBusinessException{
		if(afaTipoComposicoes == null || velocidadeAdministracao == null || afaTipoVelocAdministracoes == null){
			apresentarMsgNegocio(Severity.INFO, "VALIDA_CAMPOS");
			return false;
		}
		return true;
	}
		
		
		
	
	public void editarComposicao(AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVO){
		afaTipoComposicoes = prescricaoMedicaFacade.obterTipoPorSeq(afaComposicaoNptPadraoVO.getSeqComposicao());
		velocidadeAdministracao = afaComposicaoNptPadraoVO.getVelocidadeAdministracao();
		afaTipoVelocAdministracoes = prescricaoMedicaFacade.obterTipoVelocidadeAdministracaoPorSeqTipoVelocAdministracao(afaComposicaoNptPadraoVO.getSeqVelAdministracao());
		edicaoComposicao = true;
		modoEdicao = true;
		afaComposicaoNptPadraoIdTemp.setFnpSeq(afaComposicaoNptPadraoVO.getIdComposicaoFnpSeq());
		afaComposicaoNptPadraoIdTemp.setSeqp(afaComposicaoNptPadraoVO.getIdComposicaoSeqP());
		
	}
	
	public void exluiComposicaoNptPadrao(){
		lista4 = prescricaoMedicaFacade.obterListaAfaItemNptPadrao(afaComposicaoNptPadraoVOExcluir.getIdComposicaoSeqP(), afaComposicaoNptPadraoVO.getIdComposicaoFnpSeq());
		if(!lista4.isEmpty()){
			this.apresentarMsgNegocio(Severity.INFO, "ERRO_EXCLUIR_COMPOSIÇAO");
		}else{
			try {
				if(afaComposicaoNptPadraoVOExcluir != null){
					AfaComposicaoNptPadraoId id = new AfaComposicaoNptPadraoId(afaComposicaoNptPadraoVOExcluir.getIdComposicaoFnpSeq(), 
																			   afaComposicaoNptPadraoVOExcluir.getIdComposicaoSeqP());
					composicaoNptPadraoExcluir.setId(id);
					composicaoNptPadraoExcluir = prescricaoMedicaFacade.obterComposicaoNptPadrao(id);
					prescricaoMedicaFacade.excluiComposicaoNptPadrao(composicaoNptPadraoExcluir);
					lista2.remove(afaComposicaoNptPadraoVOExcluir);
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} 
		}
		
	}
	
	public void cancelarComposicao(){
		afaTipoComposicoes = null;
		velocidadeAdministracao = null;
		afaTipoVelocAdministracoes = null;
		modoEdicao = false;
	}
	
	public void pesquisarListaComponentes(){
		tipoComposicaoComponenteVMpmDosagemVO = null;
		qtdeComponente = null;
		vMpmDosagem = null;
		modoEdicao2 = false;
		lista3.clear();
		lista3 = prescricaoMedicaFacade.obterListaAfaItemNptPadrao(seqPComposicao, fnpSeqComposicao);
		if(!lista3.isEmpty()){
			setPesquisaAtiva3(true);
		}
		
	}
	
	public void adicionarComponente() throws ApplicationBusinessException{
		if(preInsertComponente()){
			if(edicaoComponente == false){
				AfaItemNptPadrao afaItemNptPadraoAdd = new AfaItemNptPadrao();
				AfaComposicaoNptPadraoId afaComposicaoNptPadraoIdAdd = new AfaComposicaoNptPadraoId();
				AfaComposicaoNptPadrao afaComposicaoNptPadraoAdd = new AfaComposicaoNptPadrao();
				AfaItemNptPadraoId afaItemNptPadraoIdAdd = new AfaItemNptPadraoId();
				AfaComponenteNpt afaComponenteNptAdd = new AfaComponenteNpt();
				
				afaComposicaoNptPadraoIdAdd.setFnpSeq(afaComposicaoNptPadraoVO.getFormulaSeq());
				afaComposicaoNptPadraoIdAdd.setSeqp(seqComposicaoParametroComponente);
				
				
				afaItemNptPadraoIdAdd.setCntFnpSeq(afaComposicaoNptPadraoVO.getFormulaSeq());
				afaItemNptPadraoIdAdd.setCntSeqp(seqComposicaoParametroComponente);
				afaItemNptPadraoIdAdd.setSeqp(tipoComposicaoComponenteVMpmDosagemVO.getMetMatCodigo());
				
				afaComposicaoNptPadraoAdd = prescricaoMedicaFacade.obterComposicaoNptPadrao(afaComposicaoNptPadraoIdAdd);
				
				afaComponenteNptAdd = prescricaoMedicaFacade.obterComponentePorId(tipoComposicaoComponenteVMpmDosagemVO.getMetMatCodigo());
				
				afaItemNptPadraoAdd.setAfaComponenteNpt(afaComponenteNptAdd);
				afaItemNptPadraoAdd.setAfaComposicaoNptPadrao(afaComposicaoNptPadraoAdd);
				afaItemNptPadraoAdd.setAfaFormaDosagem(vMpmDosagem.getFormaDosagem());
				afaItemNptPadraoAdd.setAlteradoEm(new Date());
				afaItemNptPadraoAdd.setCriadoEm(new Date());
				afaItemNptPadraoAdd.setId(afaItemNptPadraoIdAdd);
				afaItemNptPadraoAdd.setQtde(qtdeComponente);
				afaItemNptPadraoAdd.setRapServidores(servidorLogado);
				afaItemNptPadraoAdd.setVersion(0);
				
				prescricaoMedicaFacade.salvarComponente(afaItemNptPadraoAdd);
				
				tipoComposicaoComponenteVMpmDosagemVO = null;
				qtdeComponente = null;
				vMpmDosagem = null;
				modoEdicao2 = false;
				
			}else{
				modoEdicao2 = false;
				edicaoComponente = false;
				AfaItemNptPadrao afaItemNptPadraoUP = new AfaItemNptPadrao();
				AfaComposicaoNptPadraoId afaComposicaoNptPadraoIdUP = new AfaComposicaoNptPadraoId();
				AfaComposicaoNptPadrao afaComposicaoNptPadraoUP = new AfaComposicaoNptPadrao();
				AfaComponenteNpt afaComponenteNptUP = new AfaComponenteNpt();
				
				
				afaComposicaoNptPadraoIdUP.setFnpSeq(this.afaComposicaoNptPadraoVO.getFormulaSeq());
				afaComposicaoNptPadraoIdUP.setSeqp(seqComposicaoParametroComponente);
				afaComposicaoNptPadraoUP = prescricaoMedicaFacade.obterComposicaoNptPadrao(afaComposicaoNptPadraoIdUP);
				afaComponenteNptUP = prescricaoMedicaFacade.obterComponentePorId(tipoComposicaoComponenteVMpmDosagemVO.getMetMatCodigo());
				
				
				afaItemNptPadraoUP.setAfaComponenteNpt(afaComponenteNptUP);
				afaItemNptPadraoUP.setAfaComposicaoNptPadrao(afaComposicaoNptPadraoUP);
				
				
				afaItemNptPadraoUP.setAfaFormaDosagem(afaFormaDosagemUP);
				afaItemNptPadraoUP.setAlteradoEm(new Date());
				afaItemNptPadraoUP.setId(afaItemNptPadraoId);
				afaItemNptPadraoUP.setQtde(qtdeComponente);
				afaItemNptPadraoUP.setRapServidores(servidorLogado);
				afaItemNptPadraoUP.setVersion(1);
				
				prescricaoMedicaFacade.atualizarItemNptPadrao(afaItemNptPadraoUP);
				
				tipoComposicaoComponenteVMpmDosagemVO = null;
				qtdeComponente = null;
				vMpmDosagem = null;
				modoEdicao2 = false;
			}
		}
		
		
		lista3 = prescricaoMedicaFacade.obterListaAfaItemNptPadrao(seqPComposicao, fnpSeqComposicao);
	}
	
	private boolean preInsertComponente() throws ApplicationBusinessException{
		if(tipoComposicaoComponenteVMpmDosagemVO == null || qtdeComponente == null || vMpmDosagem == null){
			apresentarMsgNegocio(Severity.INFO, "VALIDA_CAMPOS");
			return false;
		}
		return true;
	}
	
	public void editarComponente(AfaItemNptPadraoVO afaItemNptPadraoVO){
		
		AfaComponenteNpt afaComponenteNptTeste = new AfaComponenteNpt();
		vMpmDosagem = new VMpmDosagem();
		
		afaItemNptPadraoId.setCntFnpSeq(this.afaComposicaoNptPadraoVO.getFormulaSeq());
		afaItemNptPadraoId.setCntSeqp(seqComposicaoParametroComponente);
		afaItemNptPadraoId.setSeqp(afaItemNptPadraoVO.getId().getSeqp());
		
		
		afaComponenteNptTeste=	prescricaoMedicaFacade.obterComponentePorId(afaItemNptPadraoVO.getMedMatCodigoComponenteNpts());
		tipoComposicaoComponenteVMpmDosagemVO = new TipoComposicaoComponenteVMpmDosagemVO();
		tipoComposicaoComponenteVMpmDosagemVO.setDescricao(afaComponenteNptTeste.getDescricao());
		tipoComposicaoComponenteVMpmDosagemVO.setMetMatCodigo(afaComponenteNptTeste.getMedMatCodigo());
				
		afaFormaDosagemUP = prescricaoMedicaFacade.obterAfaFormaDosagemPorId(afaItemNptPadraoVO.getSeqVMpmDosagem());
		
		vMpmDosagem.setSeqDosagem(afaItemNptPadraoVO.getSeqVMpmDosagem()); 
		vMpmDosagem.setSeqUnidade(afaItemNptPadraoVO.getSeqUnidadeVMpmDosagem());
		qtdeComponente = afaItemNptPadraoVO.getQtdItemNpt();
		edicaoComponente = true;
		modoEdicao2 = true;
	}
	
	public void exluirComponenteNptPadrao(){
		try {
			if(afaItemNptPadraoVOExcluir != null){
				afaItemNptPadraoExcluir = prescricaoMedicaFacade.obterItemNptPadrao(afaItemNptPadraoVOExcluir.getId());
				prescricaoMedicaFacade.excluiItemNptPadrao(afaItemNptPadraoExcluir.getId());
				lista3.remove(afaItemNptPadraoVOExcluir);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void cancelarComponente(){
		tipoComposicaoComponenteVMpmDosagemVO = null;
		qtdeComponente = null;
		vMpmDosagem = null;
		modoEdicao2 = false;
	}
	
	public String truncaDescricao(String desc){
		if(!desc.isEmpty() && (desc.length() > 46)){
			desc = desc.substring(0, 45) + " ...";
		}
		return desc;
	}
	
	
	/**
	 * SuggestionBoexes
	 * @param filter
	 * @return
	 */
	public List<AfaTipoComposicoes> getlistarAfaTipoComposicoes(String filter){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.pesquisaAfaTipoComposicoesPorFiltro(filter),getlistarAfaTipoComposicoesCount(filter));
	}
	
	public List<AfaTipoVelocAdministracoes> getlistarAfaTipoVelocAdministracoesAtivos(String filter){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.pesquisaAfaTipoVelocAdministracoesAtivos(filter),getlistarAfaTipoVelocAdministracoesAtivosCount(filter));
	}
	
	public List<TipoComposicaoComponenteVMpmDosagemVO> getlistarTipoComposicaoComposicaoComponenteVMpmDosagemVO(String filter){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.pesquisaComponenteVinculadoComposicaoFormula(seqComposicaoParametroComponente,filter),getlistarlistarTipoComposicaoComposicaoComponenteVMpmDosagemVOCount(filter));
	}
	
	public List<VMpmDosagem> getlistarVMpmDosagem(String filter){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.pesquisarVMpmDosagemPorfiltro(tipoComposicaoComponenteVMpmDosagemVO.getMetMatCodigo(),filter),getlistarVMpmDosagemCount(filter));
	}
	

	

	private long getlistarAfaTipoVelocAdministracoesAtivosCount(String filter) {
		return prescricaoMedicaFacade.pesquisaAfaTipoVelocAdministracoesAtivosCount(filter);
	}
	public long getlistarAfaTipoComposicoesCount(String filter){
		return this.prescricaoMedicaFacade.pesquisaAfaTipoComposicoesPorFiltroCount(filter);
	}
	private long getlistarlistarTipoComposicaoComposicaoComponenteVMpmDosagemVOCount(String filter) {
		return this.prescricaoMedicaFacade.pesquisaComponenteVinculadoComposicaoFormulaCount(seqComposicaoParametroComponente, filter);
	}
	private long getlistarVMpmDosagemCount(Object filter) {
		return prescricaoMedicaFacade.pesquisarVMpmDosagemPorfiltroCount(tipoComposicaoComponenteVMpmDosagemVO.getMetMatCodigo(),filter);
	}
	
	//--Fim
	

	public Boolean getPermissao() {
		return permissao;
	}

	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}

	public List<DominioSituacao> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<DominioSituacao> situacoes) {
		this.situacoes = situacoes;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

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

	public boolean isSituacaoManter() {
		return situacaoManter;
	}

	public void setSituacaoManter(boolean situacaoManter) {
		this.situacaoManter = situacaoManter;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isSituacaoManter2() {
		return situacaoManter2;
	}

	public void setSituacaoManter2(boolean situacaoManter2) {
		this.situacaoManter2 = situacaoManter2;
	}

	public Boolean getModoEdicao2() {
		return modoEdicao2;
	}

	public void setModoEdicao2(Boolean modoEdicao2) {
		this.modoEdicao2 = modoEdicao2;
	}

	public void setPadrao(DominioSimNao padrao) {
		this.padrao = padrao;
	}

	public DominioSimNao getPediatrico() {
		return pediatrico;
	}

	public void setPediatrico(DominioSimNao pediatrico) {
		this.pediatrico = pediatrico;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public AfaFormulaNptPadrao getAfaFormulaNptPadrao() {
		return afaFormulaNptPadrao;
	}

	public void setAfaFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao) {
		this.afaFormulaNptPadrao = afaFormulaNptPadrao;
	}

	public List<AfaComposicaoNptPadraoVO> getLista2() {
		return lista2;
	}

	public void setLista2(List<AfaComposicaoNptPadraoVO> lista2) {
		this.lista2 = lista2;
	}
	
	public List<AfaFormulaNptPadrao> getLista() {
		return lista;
	}

	public void setLista(List<AfaFormulaNptPadrao> lista) {
		this.lista = lista;
	}

	public DominioSimNao getPadrao() {
		return padrao;
	}

	public static String getVazio() {
		return vazio;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public boolean isPesquisaAtiva2() {
		return pesquisaAtiva2;
	}

	public void setPesquisaAtiva2(boolean pesquisaAtiva2) {
		this.pesquisaAtiva2 = pesquisaAtiva2;
	}

	public AfaComposicaoNptPadraoVO getAfaComposicaoNptPadraoVO() {
		return afaComposicaoNptPadraoVO;
	}

	public void setAfaComposicaoNptPadraoVO(AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVO) {
		this.afaComposicaoNptPadraoVO = afaComposicaoNptPadraoVO;
	}

	public AfaComposicaoNptPadrao getAfaComposicaoNptPadraoTemp() {
		return afaComposicaoNptPadraoTemp;
	}

	public void setAfaComposicaoNptPadraoTemp(AfaComposicaoNptPadrao afaComposicaoNptPadraoTemp) {
		this.afaComposicaoNptPadraoTemp = afaComposicaoNptPadraoTemp;
	}

	public String getDescricaoToolTip() {
		return descricaoToolTip;
	}

	public void setDescricaoToolTip(String descricaoToolTip) {
		this.descricaoToolTip = descricaoToolTip;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public AfaFormulaNptPadrao getAfaFormulaNptPadraoTemp() {
		return afaFormulaNptPadraoTemp;
	}

	public void setAfaFormulaNptPadraoTemp(AfaFormulaNptPadrao afaFormulaNptPadraoTemp) {
		this.afaFormulaNptPadraoTemp = afaFormulaNptPadraoTemp;
	}

	public AfaFormulaNptPadrao getFormulaNptPadraoExcluir() {
		return formulaNptPadraoExcluir;
	}

	public void setFormulaNptPadraoExcluir(AfaFormulaNptPadrao formulaNptPadraoExcluir) {
		this.formulaNptPadraoExcluir = formulaNptPadraoExcluir;
	}

	public AfaComposicaoNptPadrao getComposicaoNptPadraoExcluir() {
		return composicaoNptPadraoExcluir;
	}

	public void setComposicaoNptPadraoExcluir(AfaComposicaoNptPadrao composicaoNptPadraoExcluir) {
		this.composicaoNptPadraoExcluir = composicaoNptPadraoExcluir;
	}

	public AfaComposicaoNptPadraoVO getAfaComposicaoNptPadraoVOExcluir() {
		return afaComposicaoNptPadraoVOExcluir;
	}

	public void setAfaComposicaoNptPadraoVOExcluir(
			AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVOExcluir) {
		this.afaComposicaoNptPadraoVOExcluir = afaComposicaoNptPadraoVOExcluir;
	}

	public AfaItemNptPadrao getAfaItemNptPadrao() {
		return afaItemNptPadrao;
	}

	public void setAfaItemNptPadrao(AfaItemNptPadrao afaItemNptPadrao) {
		this.afaItemNptPadrao = afaItemNptPadrao;
	}

	public boolean isPesquisaAtiva3() {
		return pesquisaAtiva3;
	}

	public void setPesquisaAtiva3(boolean pesquisaAtiva3) {
		this.pesquisaAtiva3 = pesquisaAtiva3;
	}

	public VMpmDosagem getvMpmDosagemTemp() {
		return vMpmDosagemTemp;
	}

	public void setvMpmDosagemTemp(VMpmDosagem vMpmDosagemTemp) {
		this.vMpmDosagemTemp = vMpmDosagemTemp;
	}

	public List<AfaItemNptPadraoVO> getListaComponenteVO() {
		return listaComponenteVO;
	}

	public void setListaComponenteVO(List<AfaItemNptPadraoVO> listaComponenteVO) {
		this.listaComponenteVO = listaComponenteVO;
	}

	public List<AfaItemNptPadraoVO> getlista3() {
		return lista3;
	}

	public void setlista3(List<AfaItemNptPadraoVO> lista3) {
		this.lista3 = lista3;
	}

	public AfaTipoComposicoes getAfaTipoComposicoes() {
		return afaTipoComposicoes;
	}

	public void setAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) {
		this.afaTipoComposicoes = afaTipoComposicoes;
	}
	
	public TipoComposicaoComponenteVMpmDosagemVO getTipoComposicaoComponenteVMpmDosagemVO() {
		return tipoComposicaoComponenteVMpmDosagemVO;
	}

	public void setTipoComposicaoComponenteVMpmDosagemVO(
			TipoComposicaoComponenteVMpmDosagemVO tipoComposicaoComponenteVMpmDosagemVO) {
		this.tipoComposicaoComponenteVMpmDosagemVO = tipoComposicaoComponenteVMpmDosagemVO;
	}

	public AfaTipoVelocAdministracoes getAfaTipoVelocAdministracoes() {
		return afaTipoVelocAdministracoes;
	}

	public void setAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes afaTipoVelocAdministracoes) {
		this.afaTipoVelocAdministracoes = afaTipoVelocAdministracoes;
	}

	public VMpmDosagem getvMpmDosagem() {
		return vMpmDosagem;
	}

	public void setvMpmDosagem(VMpmDosagem vMpmDosagem) {
		this.vMpmDosagem = vMpmDosagem;
	}

	public Short getSeqComposicaoParametroComponente() {
		return seqComposicaoParametroComponente;
	}

	public void setSeqComposicaoParametroComponente(
			Short seqComposicaoParametroComponente) {
		this.seqComposicaoParametroComponente = seqComposicaoParametroComponente;
	}

	public Short getVelocidadeAdministracao() {
		return velocidadeAdministracao;
	}

	public void setVelocidadeAdministracao(Short velocidadeAdministracao) {
		this.velocidadeAdministracao = velocidadeAdministracao;
	}

	public Double getQtdeComponente() {
		return qtdeComponente;
	}

	public void setQtdeComponente(Double qtdeComponente) {
		this.qtdeComponente = qtdeComponente;
	}

	public AfaComposicaoNptPadraoVO getAfaComposicaoNptPadraoVOTemp() {
		return afaComposicaoNptPadraoVOTemp;
	}

	public void setAfaComposicaoNptPadraoVOTemp(
			AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVOTemp) {
		this.afaComposicaoNptPadraoVOTemp = afaComposicaoNptPadraoVOTemp;
	}

	public AfaComposicaoNptPadraoId getAfaComposicaoNptPadraoIdTemp() {
		return afaComposicaoNptPadraoIdTemp;
	}

	public void setAfaComposicaoNptPadraoIdTemp(
			AfaComposicaoNptPadraoId afaComposicaoNptPadraoIdTemp) {
		this.afaComposicaoNptPadraoIdTemp = afaComposicaoNptPadraoIdTemp;
	}

	public AfaItemNptPadraoVO getAfaItemNptPadraoVOExcluir() {
		return afaItemNptPadraoVOExcluir;
	}

	public void setAfaItemNptPadraoVOExcluir(
			AfaItemNptPadraoVO afaItemNptPadraoVOExcluir) {
		this.afaItemNptPadraoVOExcluir = afaItemNptPadraoVOExcluir;
	}

	public AfaItemNptPadrao getAfaItemNptPadraoExcluir() {
		return afaItemNptPadraoExcluir;
	}

	public void setAfaItemNptPadraoExcluir(AfaItemNptPadrao afaItemNptPadraoExcluir) {
		this.afaItemNptPadraoExcluir = afaItemNptPadraoExcluir;
	}

	public boolean isEdicaoComposicao() {
		return edicaoComposicao;
	}

	public void setEdicaoComposicao(boolean edicaoComposicao) {
		this.edicaoComposicao = edicaoComposicao;
	}

	public boolean isEdicaoComponente() {
		return edicaoComponente;
	}

	public void setEdicaoComponente(boolean edicaoComponente) {
		this.edicaoComponente = edicaoComponente;
	}

	public AfaItemNptPadraoId getAfaItemNptPadraoId() {
		return afaItemNptPadraoId;
	}

	public void setAfaItemNptPadraoId(AfaItemNptPadraoId afaItemNptPadraoId) {
		this.afaItemNptPadraoId = afaItemNptPadraoId;
	}

	public AfaFormaDosagem getAfaFormaDosagemUP() {
		return afaFormaDosagemUP;
	}

	public void setAfaFormaDosagemUP(AfaFormaDosagem afaFormaDosagemUP) {
		this.afaFormaDosagemUP = afaFormaDosagemUP;
	}

	public List<AfaItemNptPadraoVO> getLista3() {
		return lista3;
	}

	public void setLista3(List<AfaItemNptPadraoVO> lista3) {
		this.lista3 = lista3;
	}

	public List<AfaItemNptPadraoVO> getLista4() {
		return lista4;
	}

	public void setLista4(List<AfaItemNptPadraoVO> lista4) {
		this.lista4 = lista4;
	}

	public Boolean getRetornoFormulaCrud() {
		return retornoFormulaCrud;
	}

	public void setRetornoFormulaCrud(Boolean retornoFormulaCrud) {
		this.retornoFormulaCrud = retornoFormulaCrud;
	}

	public Short getFnpSeqComposicao() {
		return fnpSeqComposicao;
	}

	public void setFnpSeqComposicao(Short fnpSeqComposicao) {
		this.fnpSeqComposicao = fnpSeqComposicao;
	}

	public Short getSeqPComposicao() {
		return seqPComposicao;
	}

	public void setSeqPComposicao(Short seqPComposicao) {
		this.seqPComposicao = seqPComposicao;
	}

	

}
