package br.gov.mec.aghu.exames.patologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.vo.AelIdentificarGuicheVO;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class GuichePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6311943654433755902L;

	private static final String GUICHE_CRUD = "guicheCRUD";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private AelCadGuiche guiche = new AelCadGuiche();

	private AghTiposUnidadeFuncional aghTiposUnidadeFuncional;


	@Inject @Paginator
	private DynamicDataModel<AelIdentificarGuicheVO> dataModel;
	
	private AelIdentificarGuicheVO selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setGuiche(new AelCadGuiche());
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String parametro) {
		if (this.aghTiposUnidadeFuncional == null) {
			try {
				final AghParametros aghParametros = parametroFacade.obterAghParametro(AghuParametrosEnum.P_TIPO_UNIDADE_FUNCIONAL_LABORATORIO);
				aghTiposUnidadeFuncional = aghuFacade.obterTiposUnidadeFuncional(aghParametros.getVlrNumerico().intValue());
			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(parametro,
				aghTiposUnidadeFuncional, 100);
	}
	
	public String inserir(){
		return GUICHE_CRUD;
	}
	
	public String editar(){
		return GUICHE_CRUD;
	}
	
	public void excluir() {
		try {
			examesPatologiaFacade.excluirAelCadGuiche(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_CAD_GUICHE_DELETE_SUCESSO", selecionado.getDescricao());
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return examesPatologiaFacade.pesquisarAelCadGuicheCount(this.getGuiche());
	}

	@Override
	public List<AelIdentificarGuicheVO> recuperarListaPaginada(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc) {

		List<AelIdentificarGuicheVO> result = this.examesPatologiaFacade.pesquisarAelCadGuiche(firstResult, maxResults, AelCadGuiche.Fields.DESCRICAO.toString(), true, this.getGuiche());

		if (result == null) {
			result = new ArrayList<AelIdentificarGuicheVO>();
		}

		return result;
	}

	public void setGuiche(final AelCadGuiche guiche) {
		this.guiche = guiche;
	}

	public AelCadGuiche getGuiche() {
		return guiche;
	}

	public DynamicDataModel<AelIdentificarGuicheVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelIdentificarGuicheVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AelIdentificarGuicheVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelIdentificarGuicheVO selecionado) {
		this.selecionado = selecionado;
	}
}
