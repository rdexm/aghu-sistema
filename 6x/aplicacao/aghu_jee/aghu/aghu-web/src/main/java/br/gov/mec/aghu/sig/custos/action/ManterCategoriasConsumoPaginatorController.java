package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCategoriasConsumoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<SigCategoriaConsumos> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterCategoriasConsumoPaginatorController.class);

	private static final long serialVersionUID = -1036600425549438959L;

	private Integer seq;
	private SigCategoriaConsumos parametroSelecionado;

	private String categoriaConsumo;
	private DominioIndContagem contagemConsumo;
	private DominioSituacao indSituacao = DominioSituacao.A;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@Override
	public Long recuperarCount() {
		return getCustosSigCadastrosBasicosFacade().buscaCategoriasDeConsumoCount(categoriaConsumo, contagemConsumo, indSituacao);
	}

	@Override
	public List<SigCategoriaConsumos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getCustosSigCadastrosBasicosFacade().buscaCategoriasDeConsumo(firstResult, maxResult, orderProperty, asc, categoriaConsumo,
				contagemConsumo, indSituacao);
	}

	public String editar() {
		return "cadastrarCategoriasConsumo";
	}

	public String cadastrarCategoriaConsumo() {
		return "cadastrarCategoriasConsumo";
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true);
	}

	public void limpar() {
		setCategoriaConsumo(null);
		setContagemConsumo(null);
		setIndSituacao(DominioSituacao.A);
		this.dataModel.setPesquisaAtiva(false);
	}

	public void excluir() {
		this.dataModel.reiniciarPaginator();
		try {
			this.custosSigCadastrosBasicosFacade.excluirCategoriaConsumo(parametroSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CATEGORIA_CONSUMO", parametroSelecionado.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	public void setCategoriaConsumo(String categoriaConsumo) {
		this.categoriaConsumo = categoriaConsumo;
	}

	public String getCategoriaConsumo() {
		return categoriaConsumo;
	}

	public void setContagemConsumo(DominioIndContagem contagemConsumo) {
		this.contagemConsumo = contagemConsumo;
	}

	public DominioIndContagem getContagemConsumo() {
		return contagemConsumo;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setCustosSigCadastrosBasicosFacade(ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade) {
		this.custosSigCadastrosBasicosFacade = custosSigCadastrosBasicosFacade;
	}

	public ICustosSigCadastrosBasicosFacade getCustosSigCadastrosBasicosFacade() {
		return custosSigCadastrosBasicosFacade;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public DynamicDataModel<SigCategoriaConsumos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigCategoriaConsumos> dataModel) {
		this.dataModel = dataModel;
	}

	public SigCategoriaConsumos getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SigCategoriaConsumos parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
