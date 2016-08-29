package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunasId;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMatsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Textos
 * padrão Descrição de Materiais
 * 
 */


public class GrupoDescMatsLacunaController extends ActionController {

	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

//	private static final Log LOG = LogFactory.getLog(GrupoDescMatsLacunaController.class);

	private static final String VOLTAR = "textoPadraoDescMats";
	private static final String DETALHES = "textoDescMatsLacuna";
	
	private static final long serialVersionUID = 7565616194563254483L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short aelTxtDescMatsGtmSeq;

	private Short aelTxtDescMatsSeqp;

	private List<AelGrpDescMatLacunas> lista;

	// Avo
	private AelGrpTxtDescMats aelGrpTxtDescMats;

	// Pai
	private AelTxtDescMats aelTxtDescMats;

	// Filho
	private AelGrpDescMatLacunas aelGrpDescMatLacunas = new AelGrpDescMatLacunas();

	private AelGrpDescMatLacunasId idExcluir;
	
	private boolean editando;

	private Short seqp;

	public void inicio() {
	 

		editando = false;
		aelTxtDescMats = examesPatologiaFacade.obterAelTxtDescMats(new AelTxtDescMatsId(aelTxtDescMatsGtmSeq, aelTxtDescMatsSeqp));
		aelGrpTxtDescMats = examesPatologiaFacade.obterAelGrpTxtDescMats(aelTxtDescMats.getAelGrpTxtDescMats().getSeq());
		criaObjetoInsercao();
		pesquisar();
	
	}

	private void criaObjetoInsercao() {
		aelGrpDescMatLacunas = new AelGrpDescMatLacunas();
		aelGrpDescMatLacunas.setAelTxtDescMats(aelTxtDescMats);
		AelGrpDescMatLacunasId id = new AelGrpDescMatLacunasId();
		id.setGtmSeq(aelTxtDescMats.getId().getGtmSeq());
		id.setLdaSeq(aelTxtDescMats.getId().getSeqp());
		aelGrpDescMatLacunas.setId(id);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(aelTxtDescMatsGtmSeq, aelTxtDescMatsSeqp, null);
	}

	public void gravar() {
		try {
			if (aelGrpDescMatLacunas.getId() != null && aelGrpDescMatLacunas.getId().getSeqp() != null) {
				examesPatologiaFacade.alterarAelGrpDescMatLacunas(aelGrpDescMatLacunas);
//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_DESC_MATS_LACUNAS_UPDATE_SUCESSO", aelGrpDescMatLacunas.getLacuna());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelGrpDescMatLacunas(aelGrpDescMatLacunas);

//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_DESC_MATS_LACUNAS_INSERT_SUCESSO",
						aelGrpDescMatLacunas.getLacuna());
			}

			criaObjetoInsercao();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}

	public void editar(final Short seqp) {
		editando = true;
		aelGrpDescMatLacunas = examesPatologiaFacade.obterAelGrpDescMatLacunas(new AelGrpDescMatLacunasId(aelTxtDescMats.getId()
				.getGtmSeq(), aelTxtDescMats.getId().getSeqp(), seqp));
	}

	public void cancelarEdicao() {
		editando = false;
//		examesPatologiaFacade.refresh(aelGrpDescMatLacunas);
		pesquisar();
		criaObjetoInsercao();
	}

	public void excluir() {
		try {
			this.aelGrpDescMatLacunas = this.examesPatologiaFacade.obterAelGrpDescMatLacunas(idExcluir);
			examesPatologiaFacade.excluirAelGrpDescMatLacunas(aelGrpDescMatLacunas);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_DESC_MATS_LACUNAS_DELETE_SUCESSO",
					aelGrpDescMatLacunas.getLacuna());

//			examesPatologiaFacade.flush();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		criaObjetoInsercao();
		pesquisar();
	}

	public void ativarInativar(final Short seqp) {
		try {
			if (seqp != null) {
				aelGrpDescMatLacunas = examesPatologiaFacade.obterAelGrpDescMatLacunas(new AelGrpDescMatLacunasId(aelTxtDescMats.getId()
						.getGtmSeq(), aelTxtDescMats.getId().getSeqp(), seqp));

				aelGrpDescMatLacunas.setIndSituacao((DominioSituacao.A.equals(aelGrpDescMatLacunas.getIndSituacao()) ? DominioSituacao.I
						: DominioSituacao.A));

				examesPatologiaFacade.alterarAelGrpDescMatLacunas(aelGrpDescMatLacunas);
//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO,
						(DominioSituacao.A.equals(aelGrpDescMatLacunas.getIndSituacao()) ? "MENSAGEM_AEL_GRP_DESC_MATS_LACUNAS_INATIVADO_SUCESSO"
								: "MENSAGEM_AEL_GRP_DESC_MATS_LACUNAS_ATIVADO_SUCESSO"), aelGrpDescMatLacunas.getLacuna());

				criaObjetoInsercao();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}

	public String voltar() {
		return VOLTAR;
	}
	
	public String detalhes() {
		return DETALHES;
	}

	public Short getAelTxtDescMatsGtmSeq() {
		return aelTxtDescMatsGtmSeq;
	}

	public void setAelTxtDescMatsGtmSeq(Short aelTxtDescMatsGtmSeq) {
		this.aelTxtDescMatsGtmSeq = aelTxtDescMatsGtmSeq;
	}

	public Short getAelTxtDescMatsSeqp() {
		return aelTxtDescMatsSeqp;
	}

	public void setAelTxtDescMatsSeqp(Short aelTxtDescMatsSeqp) {
		this.aelTxtDescMatsSeqp = aelTxtDescMatsSeqp;
	}

	public List<AelGrpDescMatLacunas> getLista() {
		return lista;
	}

	public void setLista(List<AelGrpDescMatLacunas> lista) {
		this.lista = lista;
	}

	public AelTxtDescMats getAelTxtDescMats() {
		return aelTxtDescMats;
	}

	public void setAelTxtDescMats(AelTxtDescMats aelTxtDescMats) {
		this.aelTxtDescMats = aelTxtDescMats;
	}

	public AelGrpDescMatLacunas getAelGrpDescMatLacunas() {
		return aelGrpDescMatLacunas;
	}

	public void setAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatLacunas) {
		this.aelGrpDescMatLacunas = aelGrpDescMatLacunas;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelGrpTxtDescMats getAelGrpTxtDescMats() {
		return aelGrpTxtDescMats;
	}

	public void setAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtDescMats) {
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public AelGrpDescMatLacunasId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelGrpDescMatLacunasId idExcluir) {
		this.idExcluir = idExcluir;
	}

}
