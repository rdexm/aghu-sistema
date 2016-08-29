package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.paginator.GruposCidPaginator;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de grupos de Capítulos.
 */
public class GrupoCidPaginatorController extends ActionController implements ActionPaginator {

	private static final Log LOG = LogFactory.getLog(GrupoCidPaginatorController.class);

	private static final long serialVersionUID = -4814481432787212903L;

	private static final String PAGE_GRUPO_CID_CRUD = "grupoCidCRUD";
	private static final String PAGE_CAPITULO_CID_GRUPO_CID_LIST = "capituloCidGrupoCidList";

	@Inject @Paginator
	private DynamicDataModel<AghGrupoCids> dataModel;

	@EJB
	private ICidFacade cidFacade;

	@Inject
	private GruposCidPaginator paginator;

	@Inject
	private GrupoCidController grupoCidController;

	private Integer codigoGrupoPesquisa;

	private boolean retornoCrud = false;
	/**
	 * Atributo referente a chave.
	 */
	private Integer seqGrupoCids;

	private AghCapitulosCid capituloCid = new AghCapitulosCid();

	private AghGrupoCids grupoCid = new AghGrupoCids();

	private Integer capituloCidSeq;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		capituloCid = cidFacade.obterCapituloCid(this.getCapituloCidSeq());
		getDataModel().reiniciarPaginator();
		if (!retornoCrud) {
			paginator.setCapituloCid(capituloCid);
			paginator.setCodigo(codigoGrupoPesquisa);
			paginator.setSigla(null);
			paginator.setDescricao(null);
			paginator.setSituacao(null);
		}
	}

	public void pesquisar() {
		// Limpando as mensagens de erro, que estao renderizadas.
		// getStatusMessages().clear();
		getDataModel().reiniciarPaginator();
		paginator.setCapituloCid(capituloCid);
		paginator.setCodigo(codigoGrupoPesquisa);
		paginator.setSigla(this.grupoCid.getSigla());
		paginator.setDescricao(this.grupoCid.getDescricao());
		paginator.setSituacao(this.grupoCid.getIndSituacao());
	}

	public void limparPesquisa() {
		this.codigoGrupoPesquisa = null;
		grupoCid.setSigla(null);
		grupoCid.setDescricao(null);
		grupoCid.setIndSituacao(null);
		getDataModel().limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de Grupo
	 * para especialidade.
	 */
	public void excluir() {
		getDataModel().reiniciarPaginator();
		AghGrupoCids aghGrupoCid = this.cidFacade.obterGrupoCidPorId(capituloCidSeq, seqGrupoCids);
		try {
			if (aghGrupoCid != null) {
				this.cidFacade.removerGrupoCid(aghGrupoCid);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_CID", aghGrupoCid.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_GRUPO_CID_INVALIDA");
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de pesquisa de grupo
	 * para capítulo
	 */
	public String cancelar() {
		LOG.info("Cancelado");
		limparPesquisa();
		getDataModel().reiniciarPaginator();
		return PAGE_CAPITULO_CID_GRUPO_CID_LIST;
	}

	public String editar(Integer seqGrupoCids) {
		grupoCidController.setSeqGrupoCids(seqGrupoCids);
		grupoCidController.inicio();
		return PAGE_GRUPO_CID_CRUD;
	}

	// ### GETs e SETs ### 

	public Integer getCodigoGrupoPesquisa() {
		return codigoGrupoPesquisa;
	}

	public void setCodigoGrupoPesquisa(Integer codigoGrupoPesquisa) {
		this.codigoGrupoPesquisa = codigoGrupoPesquisa;
	}

	public Integer getCapituloCidSeq() {
		return capituloCidSeq;
	}

	public void setCapituloCidSeq(Integer capituloCidSeq) {
		this.capituloCidSeq = capituloCidSeq;
	}

	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void setCapituloCid(AghCapitulosCid capituloCid) {
		this.capituloCid = capituloCid;
	}

	public Integer getSeqGrupoCids() {
		return seqGrupoCids;
	}

	public void setSeqGrupoCids(Integer seqGrupoCids) {
		this.seqGrupoCids = seqGrupoCids;
	}

	public AghGrupoCids getGrupoCid() {
		return grupoCid;
	}

	public void setGrupoCid(AghGrupoCids grupoCid) {
		this.grupoCid = grupoCid;
	}

	public boolean isRetornoCrud() {
		return retornoCrud;
	}

	public void setRetornoCrud(boolean retornoCrud) {
		this.retornoCrud = retornoCrud;
	}

	public GruposCidPaginator getPaginator() {
		return paginator;
	}

	public void setPaginator(GruposCidPaginator paginator) {
		this.paginator = paginator;
	}

	@Override
	public Long recuperarCount() {
		return paginator.recuperarCount();
	}

	@Override
	public List<AghGrupoCids> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return paginator.recuperarListaPaginada(firstResult, maxResults, orderProperty, asc);
	}

	public DynamicDataModel<AghGrupoCids> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghGrupoCids> dataModel) {
		this.dataModel = dataModel;
	}

}
