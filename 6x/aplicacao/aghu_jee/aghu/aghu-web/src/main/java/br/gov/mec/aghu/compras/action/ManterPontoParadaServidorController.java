package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPontoServidorId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterPontoParadaServidorController extends ActionController implements ActionPaginator {

	
	private static final long serialVersionUID = -8299246367088798545L;

	private static final String MANTER_PONTO_PARADA_SERVIDOR = "manterPontoParadaServidor";
	private static final String PESQUISAR_PONTO_PARADA_SERVIDOR = "pesquisarPontoParadaServidor";
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private SecurityController securityController;	
	
	private ScoPontoServidor pontoServidor;
	private ScoPontoServidor pontoServidorPesquisa;
	
	@Inject @Paginator
	private DynamicDataModel<ScoPontoServidor> dataModel;
	
	private ScoPontoServidor selecionado;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
		limpar();
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
        dataModel.limparPesquisa();
		setPontoServidorPesquisa(new ScoPontoServidor());
		getPontoServidorPesquisa().setId(new ScoPontoServidorId());
	}
	
	public String inserir() {
		setPontoServidor(new ScoPontoServidor());
		getPontoServidor().setId(new ScoPontoServidorId());	
		return MANTER_PONTO_PARADA_SERVIDOR;
	}

	public String cancelar() {
		limpar();
		return PESQUISAR_PONTO_PARADA_SERVIDOR;
	}

	@Override
	public Long recuperarCount() {
		Short codigo = null;
		if(pontoServidorPesquisa.getPontoParadaSolicitacao() != null) {
			codigo = getPontoServidorPesquisa().getPontoParadaSolicitacao().getCodigo(); 
		}
		Integer matricula = null;
		if(pontoServidorPesquisa.getServidor() != null) {
			matricula = getPontoServidorPesquisa().getServidor().getId().getMatricula();
		}
		Short vinCodigo = null;
		if(pontoServidorPesquisa.getServidor() != null) {
			vinCodigo = getPontoServidorPesquisa().getServidor().getId().getVinCodigo();
		}
		return comprasFacade.pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(codigo, matricula, vinCodigo);
	}

	@Override
	public List<ScoPontoServidor> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short codigo = null;
		if(getPontoServidorPesquisa().getPontoParadaSolicitacao() != null) {
			codigo = getPontoServidorPesquisa().getPontoParadaSolicitacao().getCodigo(); 
		}
		Integer matricula = null;
		if(getPontoServidorPesquisa().getServidor() != null) {
			matricula = getPontoServidorPesquisa().getServidor().getId().getMatricula();
		}
		Short vinCodigo = null;
		if(getPontoServidorPesquisa().getServidor() != null) {
			vinCodigo = getPontoServidorPesquisa().getServidor().getId().getVinCodigo();
		}		
		return comprasFacade.pesquisarPontoParadaServidorCodigoMatriculaVinculo(firstResult, maxResult, orderProperty, asc, codigo, matricula, vinCodigo);
	}
	
	
	/**
	 * Verifica os dados inseridos na tela e insere novo ponto parada servidor.
	 * @return
	 */
	public String salvarPontoServidor(){
		try {
			if(getPontoServidor() != null && getPontoServidor().getServidor() != null &&
					getPontoServidor().getPontoParadaSolicitacao() != null) {
				ScoPontoParadaSolicitacao pontoParadaSolicitacao = getPontoServidor().getPontoParadaSolicitacao();
				RapServidores servidor = registroColaboradorFacade.obterRapServidor(getPontoServidor().getServidor().getId());
				getPontoServidor().setPontoParadaSolicitacao(pontoParadaSolicitacao);
				getPontoServidor().setServidor(servidor);
				getPontoServidor().getId().setCodigoPontoParadaSolicitacao(pontoParadaSolicitacao.getCodigo());
				getPontoServidor().getId().setVinCodigo(getPontoServidor().getServidor().getId().getVinCodigo());
				getPontoServidor().getId().setMatricula(getPontoServidor().getServidor().getId().getMatricula());
			}
			comprasFacade.inserirPontoParadaServidor(getPontoServidor());
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_GRAVACAO_PONTO_PARADA_SERVIDOR");
			
			return cancelar();
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public void excluir() {
		try {
			comprasFacade.removerPontoParadaServidor(selecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUSAO_PONTO_PARADA_SERVIDOR");
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	 
	/**
	 * Utilizado pelo suggestionbox para pesquisar o ponto de parada
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontosParada(String parametro) {
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao((String) parametro);
	}
	
	/**
	 * Utilizado pelo suggestionbox para pesquisar o servidor
	 */
	public List<RapServidores> pesquisarServidores(String parametro)
			throws BaseException {
		return registroColaboradorFacade
				.pesquisarServidoresVinculados(parametro);
	}

	public ScoPontoServidor getPontoServidor() {
		return pontoServidor;
	}

	public void setPontoServidor(ScoPontoServidor pontoServidor) {
		this.pontoServidor = pontoServidor;
	}

	public ScoPontoServidor getPontoServidorPesquisa() {
		return pontoServidorPesquisa;
	}

	public void setPontoServidorPesquisa(ScoPontoServidor pontoServidorPesquisa) {
		this.pontoServidorPesquisa = pontoServidorPesquisa;
	}

	public DynamicDataModel<ScoPontoServidor> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoPontoServidor> dataModel) {
	 this.dataModel = dataModel;
	}

	public ScoPontoServidor getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoPontoServidor selecionado) {
		this.selecionado = selecionado;
	}
}