package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.paginator.CapitulosCidPaginator;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class CapituloCidGrupoCidPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -867001583721412576L;

	private static final String PAGE_GRUPO_CID_LIST = "grupoCidList";

	private AghCapitulosCid capituloCid = new AghCapitulosCid();

	private Integer aghCapituloCidSeq;

	@Inject
	private CapitulosCidPaginator paginator;

	@Inject
	private GrupoCidPaginatorController grupoCidPaginatorController;

	@Inject @Paginator
	private DynamicDataModel<AghCapitulosCid> dataModel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		// Limpando as mensagens de erro, que estao renderizadas.
		// getStatusMessages().clear();
		getDataModel().reiniciarPaginator();

		paginator.setNumero(this.capituloCid.getNumero());
		paginator.setDescricao(this.capituloCid.getDescricao());

		paginator.setIndExigeCidSecundario(this.capituloCid.getIndExigeCidSecundario());
		paginator.setIndSituacao(this.capituloCid.getIndSituacao());
	}

	public void limparCampos() {
		capituloCid.setNumero(null);
		capituloCid.setDescricao("");
		capituloCid.setIndExigeCidSecundario(null);
		capituloCid.setIndSituacao(null);
		getDataModel().limparPesquisa();
	}

	public String editar(Integer capituloCidSeq) {
		grupoCidPaginatorController.setCapituloCidSeq(capituloCidSeq);
		grupoCidPaginatorController.setRetornoCrud(false);
		grupoCidPaginatorController.inicio();
		return PAGE_GRUPO_CID_LIST;
	}

	// SET's e GET's
	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void setCapituloCid(AghCapitulosCid capitulosCid) {
		this.capituloCid = capitulosCid;
	}

	public Integer getAghCapituloCidSeq() {
		return aghCapituloCidSeq;
	}

	public void setAghCapituloCidSeq(Integer aghCapituloCidSeq) {
		this.aghCapituloCidSeq = aghCapituloCidSeq;
	}

	public CapitulosCidPaginator getPaginator() {
		return paginator;
	}

	public void setPaginator(CapitulosCidPaginator paginator) {
		this.paginator = paginator;
	}

	@Override
	public Long recuperarCount() {
		return paginator.recuperarCount();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AghCapitulosCid> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return paginator.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc);
	}

	public DynamicDataModel<AghCapitulosCid> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghCapitulosCid> dataModel) {
		this.dataModel = dataModel;
	}

}
