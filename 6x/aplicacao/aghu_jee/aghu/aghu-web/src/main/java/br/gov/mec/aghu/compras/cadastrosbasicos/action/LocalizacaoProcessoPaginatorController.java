package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class LocalizacaoProcessoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7219325029549482914L;

	private static final String LOCALIZACAO_PROCESSO_CRUD = "localizacaoProcessoCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private SecurityController securityController;	

	private ScoLocalizacaoProcesso locProcesso = new ScoLocalizacaoProcesso();
	private RapServidores servidorResponsavel;
	private RapRamalTelefonico ramal;
	private DominioSimNao localOriginario;
	private DominioSimNao arquivoMorto;
	private Short codigo;
	
	@Inject @Paginator
	private DynamicDataModel<ScoLocalizacaoProcesso> dataModel;
	
	private ScoLocalizacaoProcesso selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarLocaisProcessos,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}

	@Override
	public Long recuperarCount() {
		
		//Verifica os campos localOriginario e arquivoMorto para converter de DominioSimNao para Booleano e popular o objeto locProcesso
		if (this.localOriginario != null) {
			this.getLocProcesso().setIndLocalOriginario((this.localOriginario == DominioSimNao.S ?true:false));
		}
		else { this.getLocProcesso().setIndLocalOriginario(null);
		}
		
		if (this.arquivoMorto != null) {
			this.getLocProcesso().setIndArquivoMorto((this.arquivoMorto == DominioSimNao.S ?true:false));
		}
		else { this.getLocProcesso().setIndArquivoMorto(null);
		}
		
		return comprasCadastrosBasicosFacade.listarLocalizacaoProcessoCount(locProcesso);
	}

	@Override
	public List<ScoLocalizacaoProcesso> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.listarLocalizacaoProcesso(firstResult, maxResult, orderProperty, asc, locProcesso);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setLocProcesso(new ScoLocalizacaoProcesso());
		this.getLocProcesso().setIndSituacao(DominioSituacao.A);
		this.servidorResponsavel = null;
		this.ramal = null;
		this.localOriginario = null;
		this.arquivoMorto = null;
	}
	
	//Método para carregar Suggestion de ramais telefônicos
	public List<RapRamalTelefonico> pesquisarRamais(String objParam) {
		return cadastrosBasicosFacade.pesquisarRamalTelefonicoPorNroRamal(objParam);
	}
	
	//Método para carrega Suggestion Servidor Responsável
	public List<RapServidores> listarServidores(String objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidor(objPesquisa);
			
		}else {
			return this.registroColaboradorFacade.pesquisarRapServidores();
		}
	}
	
	public String inserir() {
		return LOCALIZACAO_PROCESSO_CRUD;
	}
	
	public String editar() {
		return LOCALIZACAO_PROCESSO_CRUD;
	}
	
	public String visualizar() {
		return LOCALIZACAO_PROCESSO_CRUD;
	}
	
	public void excluir() {
		try{
			comprasCadastrosBasicosFacade.excluirLocalizacaoProcesso(selecionado.getCodigo());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LOC_PROCESSO_DELETE_SUCESSO");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public ScoLocalizacaoProcesso getLocProcesso() {
		return locProcesso;
	}

	public void setLocProcesso(ScoLocalizacaoProcesso locProcesso) {
		this.locProcesso = locProcesso;
	}

	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}

	public RapRamalTelefonico getRamal() {
		return ramal;
	}

	public void setRamal(RapRamalTelefonico ramal) {
		this.ramal = ramal;
	}

	public DominioSimNao getLocalOriginario() {
		return localOriginario;
	}

	public void setLocalOriginario(DominioSimNao localOriginario) {
		this.localOriginario = localOriginario;
	}

	public DominioSimNao getArquivoMorto() {
		return arquivoMorto;
	}

	public void setArquivoMorto(DominioSimNao arquivoMorto) {
		this.arquivoMorto = arquivoMorto;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public Short getCodigo() {
		return codigo;
	}

	public DynamicDataModel<ScoLocalizacaoProcesso> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLocalizacaoProcesso> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoLocalizacaoProcesso getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoLocalizacaoProcesso selecionado) {
		this.selecionado = selecionado;
	}
}