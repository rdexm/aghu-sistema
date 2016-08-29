package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.vo.LogradouroVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controladora na tela de pesquisa de logradouro.
 * 
 * @author Marcelo Tocchetto
 */
public class LogradouroPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 2017202180619335211L;
	private static final Log LOG = LogFactory.getLog(LogradouroPaginatorController.class);
	private static final String REDIRECT_MANTER_LOGRADOURO = "logradouroCRUD";
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	private AipLogradouros aipLogradouro = new AipLogradouros();
	
	private LogradouroVO logradouroSelecionado;
	
	private boolean exibirBotaoNovo = false;
	
	@Inject
	private LogradouroController logradouroController;
	
	@Inject @Paginator
	private DynamicDataModel<LogradouroVO> dataModel;
	
	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}

	
	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	public String iniciarInclusao(){
		logradouroController.inicio();
		return REDIRECT_MANTER_LOGRADOURO;
	}

	public String editar(){
		logradouroController.getAipLogradouro().setCodigo(logradouroSelecionado.getCodigoLogradouro());
		logradouroController.inicio();
		return REDIRECT_MANTER_LOGRADOURO;
	}
	
	public void ocultarBotaoNovo() {
		exibirBotaoNovo = false;
	}

	
	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
		
	}
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.obterLogradouroVOCount(aipLogradouro.getNome(), aipLogradouro.getAipCidade());
	}

	@Override
	public List<LogradouroVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarLogradouroVO(firstResult, maxResult, aipLogradouro.getNome(), aipLogradouro.getAipCidade());
	}
	
	/**
	 * Método que realiza a ação do botão excluir
	 * na tela de Pesquisa de Logradouros.
	 */
	public void excluir() {
		try {
			Integer codigoLogradouro = logradouroSelecionado.getCodigoLogradouro(); 
			Integer cep = logradouroSelecionado.getAipCLICep() == null ? 0 : logradouroSelecionado.getAipCLICep(); 
			Integer codigoBairro = logradouroSelecionado.getCodigoBairro() == null ? 0 : logradouroSelecionado.getCodigoBairro();
			
			AipLogradouros aipLogradouro = cadastrosBasicosPacienteFacade.obterLogradouroPorCodigo(codigoLogradouro);
			
			if (codigoBairro.intValue() == 0) {
				cadastrosBasicosPacienteFacade.excluirLogradouro(codigoLogradouro);
			} else {
				cadastrosBasicosPacienteFacade.excluirBairroCepLogradouro(codigoLogradouro, cep, codigoBairro);
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_LOGRADOURO", aipLogradouro.getNome());
			reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			LOG.debug("Erro ao remover o vinculo entre bairro, cep e logradouro:", e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparPesquisa() {
		aipLogradouro = new AipLogradouros();
		exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	public void setAipLogradouro(AipLogradouros aipLogradouro) {
		this.aipLogradouro = aipLogradouro;
	}

	public AipLogradouros getAipLogradouro() {
		return aipLogradouro;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}
	
	public List<AipCidades> pesquisarCidade(String descricao) {
		return cadastrosBasicosPacienteFacade.pesquisarCidadesParaLogradouro(descricao);
	}


	public LogradouroVO getLogradouroSelecionado() {
		return logradouroSelecionado;
	}


	public void setLogradouroSelecionado(LogradouroVO logradouroSelecionado) {
		this.logradouroSelecionado = logradouroSelecionado;
	}


	public DynamicDataModel<LogradouroVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<LogradouroVO> dataModel) {
		this.dataModel = dataModel;
	}


	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
}