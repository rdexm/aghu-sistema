package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.model.AghGrupoCidsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de grupo para
 * capítulo.
 */
public class GrupoCidController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(GrupoCidController.class);

	private static final long serialVersionUID = 6153845896498690748L;

	private static final String PAGE_GRUPO_CID_LIST = "grupoCidList";
	private static final String PAGE_GRUPO_CID_CRUD = "grupoCidCRUD";

	@Inject
	private GrupoCidPaginatorController grupoCidPaginatorController;

	@EJB
	private ICidFacade cidFacade;

	/**
	 * Grupo para capítulo a ser criado/editado
	 */
	private AghGrupoCids grupoCid;

	/**
	 * Capítulo
	 */
	private AghCapitulosCid capituloCid;

	/**
	 * Codigo do grupo para capítula, obtido via page parameter.
	 */
	private Integer seqGrupoCids;

	private boolean modoEdicao = false;

	private boolean retornoCrud = true;

	public String iniciarInclusao() {
		this.seqGrupoCids = null;
		this.grupoCid = new AghGrupoCids();
		AghGrupoCidsId id = new AghGrupoCidsId();
		// id.setCpcSeq(capituloCid.getSeq());
		id.setCpcSeq(grupoCidPaginatorController.getCapituloCid().getSeq());
		grupoCid.setId(id);
		grupoCid.setAtivo(true);
		this.modoEdicao = false;
		return PAGE_GRUPO_CID_CRUD;
	}

	public void inicio() {
		retornoCrud = true;
		if (this.seqGrupoCids != null) {
			// this.grupoCid =
			// this.cidFacade.obterGrupoCidPorId(capituloCid.getSeq(),
			// seqGrupoCids);
			this.grupoCid = this.cidFacade.obterGrupoCidPorId(grupoCidPaginatorController.getCapituloCid().getSeq(), seqGrupoCids);
			this.modoEdicao = true;
		}
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de grupo
	 * para capítulo
	 */
	public String confirmar() {
		this.grupoCidPaginatorController.getDataModel().reiniciarPaginator();

		try {
			boolean create = this.grupoCid.getId().getSeq() == null;
			this.cidFacade.persistirGrupoCid(grupoCid);

			if (create) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_CID", this.grupoCid.getSigla());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_CID", this.grupoCid.getSigla());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		grupoCidPaginatorController.setRetornoCrud(retornoCrud);
		grupoCidPaginatorController.inicio();

		return PAGE_GRUPO_CID_LIST;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de grupo
	 * para especialidade
	 */
	public String cancelar() {
		this.seqGrupoCids = null;
		LOG.info("Cancelado");
		grupoCidPaginatorController.setRetornoCrud(retornoCrud);
		grupoCidPaginatorController.inicio();

		return PAGE_GRUPO_CID_LIST;
	}

	// ### GETs e SETs ###
	public AghGrupoCids getGrupoCid() {
		return grupoCid;
	}

	public void setGrupoCid(AghGrupoCids grupoCid) {
		this.grupoCid = grupoCid;
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

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isRetornoCrud() {
		return retornoCrud;
	}

	public void setRetornoCrud(boolean retornoCrud) {
		this.retornoCrud = retornoCrud;
	}

}
