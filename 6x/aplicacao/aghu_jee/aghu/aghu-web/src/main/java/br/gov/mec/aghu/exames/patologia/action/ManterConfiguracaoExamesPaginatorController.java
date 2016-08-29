package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaConfigExamesVO;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
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


public class ManterConfiguracaoExamesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2535574616062197785L;


	private static final String MANTER_CONFIGURACAO_EXAMES = "manterConfiguracaoExames";


	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	

	
	@Inject @Paginator
	private DynamicDataModel<AelConfigExLaudoUnico> dataModel;

	
	private AelConfigExLaudoUnico selecionado;
	
	private AghUnidadesFuncionais unidadeExecutora;
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	private ConsultaConfigExamesVO consulta;

	public void inicializaUnidadeExecutora() {
	 

		try {
			// Obtem o usuario da unidade executora
			usuarioUnidadeExecutora = examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			
			// Resgata a unidade executora associada ao usuario
			if(this.usuarioUnidadeExecutora != null){
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		consulta = new ConsultaConfigExamesVO();
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		consulta = new ConsultaConfigExamesVO();
	}

	public String inserir(){
		return MANTER_CONFIGURACAO_EXAMES;
	}

	public String editar(){
		return MANTER_CONFIGURACAO_EXAMES;
	}

	public void excluir() {
		if (selecionado.getSeq() != null) {
			try {
				AelConfigExLaudoUnico configExame = this.examesPatologiaFacade.obterConfigExameLaudoUncioPorChavePrimaria(selecionado.getSeq());
				this.examesPatologiaFacade.excluirConfigLaudo(configExame.getSeq());
				this.dataModel.reiniciarPaginator();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_CONFIG_EX_LAUDO_UNICOS_EXCLUSAO_SUCESSO");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

			selecionado.setSeq(null);
		}
	}

	@Override
	public Long recuperarCount() {
		return this.examesPatologiaFacade.listarConfigExamesCount(consulta);
	}

	@Override
	public List<AelConfigExLaudoUnico> recuperarListaPaginada(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		consulta.setFirstResult(firstResult);
		consulta.setMaxResult(maxResults);
		return this.examesPatologiaFacade.listarConfigExames(consulta);
	}
	

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}
	
	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
			dataModel.reiniciarPaginator();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void persistirIdentificacaoUnidadeExecutoraNula() {
		dataModel.limparPesquisa();
	}


	public DynamicDataModel<AelConfigExLaudoUnico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelConfigExLaudoUnico> dataModel) {
		this.dataModel = dataModel;
	}

	public AelConfigExLaudoUnico getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelConfigExLaudoUnico selecionado) {
		this.selecionado = selecionado;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public ConsultaConfigExamesVO getConsulta() {
		return consulta;
	}

	public void setConsulta(ConsultaConfigExamesVO consulta) {
		this.consulta = consulta;
	}	
	
	
	
	
	
	
	
	
	
	
	
	
 
}