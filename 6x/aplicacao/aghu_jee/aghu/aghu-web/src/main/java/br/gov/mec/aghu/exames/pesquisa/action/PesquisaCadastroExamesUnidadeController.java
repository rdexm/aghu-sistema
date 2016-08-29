package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisaCadastroExamesUnidadeController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3468695741120101875L;

	private static final String MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";
	private static final String PESQUISA_CADASTRO_EXAMES_UNIDADE = "exames-pesquisaCadastroExamesUnidade";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;		
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;	
	
	@Inject
	private SecurityController securityController;

	
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	
	private Boolean possuiPermissaoManterExames;
	
	//Filtros pesquisa
	private AghUnidadesFuncionais unidadeFuncional;
	private String siglaPesquisa;
	private String material;
	private String nomeUsual;
	private DominioSituacao indSituacao;
	
	private String sigla;
	private String origem;

	@Inject @Paginator
	private DynamicDataModel<VAelUnfExecutaExames> dataModel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		possuiPermissaoManterExames = securityController.usuarioTemPermissao("manterExames", "confirmar");
	}
	
	public void iniciar() {
	 

		try {
			Date dt = new Date();
			
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(
					registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), dt).getId());			
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}		
		
		if(this.usuarioUnidadeExecutora != null){
			// Resgata a unidade executora associada ao usuario
			this.unidadeFuncional = this.usuarioUnidadeExecutora.getUnfSeq();
			
			if(this.unidadeFuncional != null){
				persistirIdentificacaoUnidadeExecutora();
			}
		}
	
	}
	
	
	/**
	 * Chamado automaticamente na acao "posSelectionAction" da Sugestion Box de unidade executora
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeFuncional);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String codigo) {
		return examesFacade.pesquisarUnidadeFuncionalPorSeqDescricao((String) codigo);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		setUnidadeFuncional(null);
		setSigla(null);
		setSiglaPesquisa(null);
		setMaterial(null);
		setNomeUsual(null);
		setIndSituacao(null);
	}

	public String editarUnidadeExecutora() {
		setOrigem(PESQUISA_CADASTRO_EXAMES_UNIDADE);
		return MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}
	
	public Boolean getPossuiPermissaoManterExames() {
		return possuiPermissaoManterExames;
	}

	public void setPossuiPermissaoManterExames(Boolean possuiPermissaoManterExames) {
		this.possuiPermissaoManterExames = possuiPermissaoManterExames;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	private String obterSituacaoParaPesquisa() {
		if(indSituacao != null) {
			return indSituacao.toString();
		}
		return null;
	}
	
	@Override
	public List<VAelUnfExecutaExames> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		if(unidadeFuncional == null){
			return new ArrayList<>();
		}
		
		return examesFacade.pesquisarExamePorSeqUnidadeExecutora(
				getUnidadeFuncional().getSeq(), siglaPesquisa,
				material, nomeUsual, obterSituacaoParaPesquisa(), firstResult, maxResult, orderProperty, asc);		
				
	}

	@Override
	public Long recuperarCount() {
		if(unidadeFuncional == null){
			return 0l;
		}
		return examesFacade.pesquisarExamePorSeqUnidadeExecutoraCount(getUnidadeFuncional().getSeq(),
				siglaPesquisa, material, nomeUsual, obterSituacaoParaPesquisa());
	}

	public String getSiglaPesquisa() {
		return siglaPesquisa;
	}

	public void setSiglaPesquisa(String siglaPesquisa) {
		this.siglaPesquisa = siglaPesquisa;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public boolean isExibirBotaoImprimir() {
		if ( getUnidadeFuncional() != null ){
			Long totalRegistros = recuperarCount();
			if ( totalRegistros != null && totalRegistros > 0l ){
				return true;
			}
		}
		return false;
	} 


	public DynamicDataModel<VAelUnfExecutaExames> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAelUnfExecutaExames> dataModel) {
	 this.dataModel = dataModel;
	}
}
