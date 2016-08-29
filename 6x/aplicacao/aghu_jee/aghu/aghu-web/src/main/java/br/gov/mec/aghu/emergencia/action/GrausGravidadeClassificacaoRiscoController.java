package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de graus de gravidade de protocolos de classificação de risco
 * 
 * @author luismoura
 * 
 */
public class GrausGravidadeClassificacaoRiscoController extends ActionController {
	private static final long serialVersionUID = 5569508444354554895L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private MamProtClassifRisco mamProtClassifRisco;

	private List<MamGravidade> dataModel = new ArrayList<MamGravidade>();
	private MamGravidade itemDataModelSelecionado;
	private boolean pesquisaAtiva;

	private MamGravidade mamGravidade;
	private Boolean indSituacao = true;
	private Boolean indUsoTriagem = true;
	private Boolean indPermiteSaida = true;
	
	private String descricao = null;
	private Short ordem = null;
	private Date tempoEspera = null;
	private String codCor = null;
	
	private boolean permissaoManter = false;

	@PostConstruct
	public void init() {
		begin(conversation);
		this.permissaoManter = this.usuarioTemPermissao("manterGrausGravidadeClassificacaoRisco", "gravar");
	}

	public void inicio() {
		this.permissaoManter = this.usuarioTemPermissao("manterGrausGravidadeClassificacaoRisco", "gravar");
	}

	/**
	 * Usado para popular a SUGGESTION
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<MamProtClassifRisco> pesquisarMamProtClassifRisco(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(param, 100),pesquisarMamProtClassifRiscoCount(param));
	}

	/**
	 * Usado para popular a SUGGESTION
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarMamProtClassifRiscoCount(String param) {
		return emergenciaFacade.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(param);
	}

	/**
	 * Ação do botão PESQUISAR
	 */
	public void pesquisar() {
		if (this.mamProtClassifRisco == null) {
			this.dataModel.clear();
		} else {
			this.pesquisaAtiva = true;
			this.dataModel = emergenciaFacade.pesquisarGravidadePorProtocolo(this.mamProtClassifRisco);
		}
		this.limparEdicao();
	}

	public void limpar() {
		this.mamProtClassifRisco = null;
		this.limparPesquisa();
	}

	/**
	 * Ação do botão LIMPAR
	 */
	public void limparPesquisa() {
		this.pesquisaAtiva = false;
		this.dataModel.clear();
		this.limparEdicao();
	}

	/**
	 * Ação do botão EDITAR
	 * 
	 * @return
	 */
	public void editar() {
		this.indSituacao = this.getBoolSituacao(mamGravidade.getIndSituacao());
		this.indUsoTriagem = mamGravidade.getIndUsoTriagem();
		this.indPermiteSaida = mamGravidade.getIndPermiteSaida();
		this.descricao = mamGravidade.getDescricao();
		this.ordem = mamGravidade.getOrdem();
		this.tempoEspera = mamGravidade.getTempoEspera();
		this.codCor = mamGravidade.getCodCor();
	}

	/**
	 * Ação do segundo botão ADICIONAR/ALTERAR
	 */
	public void confirmar() {
		try {
			// Monta o objeto
			mamGravidade.setIndSituacao(DominioSituacao.getInstance(this.getIndSituacao()));
			mamGravidade.setIndUsoTriagem(this.getIndUsoTriagem());
			mamGravidade.setIndPermiteSaida(this.getIndPermiteSaida());
			mamGravidade.setDescricao(this.getDescricao());
			mamGravidade.setOrdem(this.getOrdem());
			mamGravidade.setTempoEspera(this.getTempoEspera());
			mamGravidade.setCodCor(this.getCodCor());
			mamGravidade.setProtClassifRisco(mamProtClassifRisco);

			boolean create = (mamGravidade.getSeq() == null);
			String descricao = mamGravidade.getDescricao();

			// Chama RN01 para persistir
			this.emergenciaFacade.persistirMamGravidade(mamGravidade);

			// Refaz a pesquisa
			this.pesquisar();

			// Apresenta mensagem de sucesso
			if (create) {
				super.apresentarMsgNegocio(Severity.INFO, "MAM_SUCESSO_CAD_GRAU_GRAVIDADE", descricao);
			} else {
				super.apresentarMsgNegocio(Severity.INFO, "MAM_SUCESSO_ALTERACAO_GRAU_GRAVIDADE", descricao);
			}

		} catch (ApplicationBusinessException e) {
			//this.pesquisar();
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão TROCAR ORDEM ACIMA ^
	 */
	public void subirOrdem() {
		try {

			// Chama RN04 para persistir
			boolean trocouOrdem = this.emergenciaFacade.subirOrdemMamGravidade(mamGravidade, dataModel);

			if (trocouOrdem) {
				// Refaz a pesquisa
				this.pesquisar();

				// Apresenta mensagem de sucesso
				super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ORDEM_GRAVIDADE");
			} else {
				this.limparEdicao();
			}

		} catch (ApplicationBusinessException e) {
			this.pesquisar();
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão TROCAR ORDEM ABAIXO v
	 */
	public void descerOrdem() {
		try {

			// Chama RN05 para persistir
			boolean trocouOrdem = this.emergenciaFacade.descerOrdemMamGravidade(mamGravidade, dataModel);

			if (trocouOrdem) {

				// Refaz a pesquisa
				this.pesquisar();

				// Apresenta mensagem de sucesso
				super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ORDEM_GRAVIDADE");
			} else {
				this.limparEdicao();
			}

		} catch (ApplicationBusinessException e) {
			this.pesquisar();
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Limpa os campos de edição
	 */
	public void limparEdicao() {
		this.mamGravidade = new MamGravidade();
		this.indSituacao = true;
		this.indUsoTriagem = true;
		this.indPermiteSaida = true;
		
		this.descricao = null;
		this.ordem = null;
		this.tempoEspera = null;
		this.codCor = null;
	}

	/**
	 * Cria um Boolean à partir de um string de A ou I
	 * 
	 * @param situacao
	 * @return
	 */
	public Boolean getBoolSituacao(DominioSituacao situacao) {
		return situacao != null && situacao.isAtivo();
	}

	public Boolean permissaoAlterar() {
		return this.permissaoManter && !this.mamProtClassifRisco.getIndBloqueado().isAtivo();
	}

	public boolean usuarioTemPermissao(String componente, String metodo) {
		return getPermissionService().usuarioTemPermissao(this.obterLoginUsuarioLogado(), componente, metodo);
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	// ----- GETS e SETS

	public MamProtClassifRisco getMamProtClassifRisco() {
		return mamProtClassifRisco;
	}

	public void setMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) {
		this.mamProtClassifRisco = mamProtClassifRisco;
		this.limparPesquisa();
	}

	public List<MamGravidade> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<MamGravidade> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public MamGravidade getMamGravidade() {
		return mamGravidade;
	}

	public void setMamGravidade(MamGravidade mamGravidade) {
		this.mamGravidade = mamGravidade;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getIndUsoTriagem() {
		return indUsoTriagem;
	}

	public void setIndUsoTriagem(Boolean indUsoTriagem) {
		this.indUsoTriagem = indUsoTriagem;
	}

	public Boolean getIndPermiteSaida() {
		return indPermiteSaida;
	}

	public void setIndPermiteSaida(Boolean indPermiteSaida) {
		this.indPermiteSaida = indPermiteSaida;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	public Date getTempoEspera() {
		return tempoEspera;
	}

	public void setTempoEspera(Date tempoEspera) {
		this.tempoEspera = tempoEspera;
	}

	public String getCodCor() {
		if(codCor != null && !codCor.contains("#")){
			return "#".concat(codCor);			
		}
		return codCor;		
	}

	public void setCodCor(String codCor) {
		this.codCor = codCor;
	}

	public boolean isPermissaoManter() {
		return permissaoManter;
	}

	public void setPermissaoManter(boolean permissaoManter) {
		this.permissaoManter = permissaoManter;
	}

	public MamGravidade getItemDataModelSelecionado() {
		return itemDataModelSelecionado;
	}

	public void setItemDataModelSelecionado(MamGravidade itemDataModelSelecionado) {
		this.itemDataModelSelecionado = itemDataModelSelecionado;
	}
}
