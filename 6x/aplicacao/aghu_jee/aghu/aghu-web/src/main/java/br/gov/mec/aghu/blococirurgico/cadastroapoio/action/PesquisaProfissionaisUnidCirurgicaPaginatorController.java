package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaProfissionaisUnidCirurgicaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 unidadeFuncional = carregarUnidadeFuncionalInicial();
	}

	@Inject @Paginator
	private DynamicDataModel<MbcProfAtuaUnidCirgs> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisaProfissionaisUnidCirurgicaPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	// Campos de filtro
	private AghUnidadesFuncionais unidadeFuncional;
	private Integer matricula;
	private Short vinCodigo;
	private String nome;
	private DominioFuncaoProfissional funcaoProfissional;//dominioFuncaoProfissionalItens
	private DominioSituacao situacaoProfissional;
	
	private MbcProfAtuaUnidCirgs itemSelecionado;
	
	private final String PAGE_CAD_PROF_UNI_CIR = "cadastroProfissionaisUnidCirurgica";
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	
	/*public void inicio() {

		if (unidadeFuncional == null && this.getFirstResult() == 0) {

			unidadeFuncional = carregarUnidadeFuncionalInicial();
		}		
	}*/

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {	
		this.dataModel.reiniciarPaginator();	
		exibirBotaoNovo = true;
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.unidadeFuncional = carregarUnidadeFuncionalInicial();
		matricula = null;
		vinCodigo = null;
		nome = null;
		funcaoProfissional = null;	
		situacaoProfissional = null;
		this.exibirBotaoNovo = false;
		this.dataModel.limparPesquisa();
	}
	
	public String novo(){		
		this.setItemSelecionado(null);
		return PAGE_CAD_PROF_UNI_CIR;
	}
	public String editar(){		
		return PAGE_CAD_PROF_UNI_CIR;
	}
	
	public void excluir(){
		try{	
			itemSelecionado.setServidorAlteradoPor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));
			blocoCirurgicoCadastroApoioFacade.excluirProfiUnidade(itemSelecionado);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PROFISSIONAL_UNIDADE");
			
			this.dataModel.reiniciarPaginator();	
			exibirBotaoNovo=true;
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		String nomeMicrocomputador = null;
			
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e1) {
			this.LOG.error("Exceção capturada:", e1);
		}
		return null;
	}
	
	@Override
	public List<MbcProfAtuaUnidCirgs> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoCadastroApoioFacade.listarProfissionaisUnidCirFiltro(firstResult, maxResult, orderProperty, asc, unidadeFuncional, matricula, vinCodigo, nome, funcaoProfissional, situacaoProfissional);
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoCadastroApoioFacade.listarProfissionaisUnidCirFiltroCount(unidadeFuncional, matricula, vinCodigo, nome, funcaoProfissional, situacaoProfissional);
	}

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeFuncional(String filtro) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(filtro);
	}
	
    public Long obterUnidadeFuncionalCount(Object filtro) {
        return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro);
    }

//	// Metódo para Suggestion Box de profissional
//	public List<RapServidores> obterProfissionalUnidCirur(Object objPesquisa){
//		return registroColaboradorFacade.pesquisarServidoresAtivosPendentes(objPesquisa);
//	}
//
//	public Integer obterProfissionalUnidCirurCount(Object objPesquisa){
//		return registroColaboradorFacade.pesquisarServidoresAtivosPendentesCount(objPesquisa);
//	}

	/*
	 * Acessadores
	 */

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public DominioSituacao getSituacaoProfissional() {
		return situacaoProfissional;
	}

	public void setSituacaoProfissional(DominioSituacao situacaoProfissional) {
		this.situacaoProfissional = situacaoProfissional;
	}
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	} 
	public DynamicDataModel<MbcProfAtuaUnidCirgs> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcProfAtuaUnidCirgs> dataModel) {
	 this.dataModel = dataModel;
	}

	public MbcProfAtuaUnidCirgs getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MbcProfAtuaUnidCirgs itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}