package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelDescMatLacunasId;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunasId;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TextoDescMatsLacunaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

//	private static final Log LOG = LogFactory.getLog(TextoDescMatsLacunaController.class);

	private static final long serialVersionUID = 2397479408205843543L;

	private static final String VOLTAR = "grupoDescMatsLacuna";
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short aelGrpDescMatLacunasGtmSeq;

	private Short aelGrpDescMatLacunasLdaSeq;

	private Short aelGrpDescMatLacunasSeqp;

	private List<AelDescMatLacunas> lista;

	// Bisavo
	private AelGrpTxtDescMats aelGrpTxtDescMats;

	// Avo
	private AelTxtDescMats aelTxtDescMats;

	// Pai
	private AelGrpDescMatLacunas aelGrpDescMatLacunas;

	// Filho
	private AelDescMatLacunas aelDescMatLacunas;

	private AelDescMatLacunasId idExcluir;
	
	private boolean editando;

	private Short seqp;

	public void inicio() {
	 

		editando = false;
		aelGrpDescMatLacunas = examesPatologiaFacade.obterAelGrpDescMatLacunas(new AelGrpDescMatLacunasId(aelGrpDescMatLacunasGtmSeq, 
				  aelGrpDescMatLacunasLdaSeq,  aelGrpDescMatLacunasSeqp));
		aelTxtDescMats = examesPatologiaFacade.obterAelTxtDescMats(aelGrpDescMatLacunas.getAelTxtDescMats().getId());
		aelGrpTxtDescMats = examesPatologiaFacade.obterAelGrpTxtDescMats(aelTxtDescMats.getAelGrpTxtDescMats().getSeq());
		criaObjetoInsercao();
		pesquisar();
	
	}

	private void criaObjetoInsercao() {
		aelDescMatLacunas = new AelDescMatLacunas();
		aelDescMatLacunas.setAelGrpDescMatLacunas(aelGrpDescMatLacunas);

		AelDescMatLacunasId id = new AelDescMatLacunasId();
		id.setGtmSeq(aelGrpDescMatLacunas.getId().getGtmSeq());
		id.setLdaSeq(aelGrpDescMatLacunas.getId().getLdaSeq());
		id.setGmlSeq(aelGrpDescMatLacunas.getId().getSeqp());

		aelDescMatLacunas.setId(id);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelDescMatLacunasPorAelGrpDescMatLacunas(aelGrpDescMatLacunas, null);
	}

	public void gravar() {
		try {
//			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());

			if (aelDescMatLacunas.getId() != null && aelDescMatLacunas.getId().getSeqp() != null) {
				examesPatologiaFacade.alterarAelDescMatLacunas(aelDescMatLacunas);
//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TXT_DESC_MATS_LACUNAS_UPDATE_SUCESSO", aelDescMatLacunas.getTextoLacuna());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelDescMatLacunas(aelDescMatLacunas);

//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TXT_DESC_MATS_LACUNAS_INSERT_SUCESSO", aelDescMatLacunas.getTextoLacuna());
			}

			criaObjetoInsercao();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}

	public void editar(final Short seqp) {
		editando = true;
		aelDescMatLacunas = examesPatologiaFacade.obterAelDescMatLacunas(new AelDescMatLacunasId(aelGrpDescMatLacunasGtmSeq,
				aelGrpDescMatLacunasLdaSeq, aelGrpDescMatLacunasSeqp, seqp));
	}

	public void cancelarEdicao() {
		editando = false;
//		examesPatologiaFacade.refresh(aelDescMatLacunas);
		pesquisar();
		criaObjetoInsercao();
	}

	public void excluir() {
		try {
			aelDescMatLacunas = examesPatologiaFacade.obterAelDescMatLacunas(idExcluir);
			examesPatologiaFacade.excluirAelDescMatLacunas(aelDescMatLacunas);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TXT_DESC_MATS_LACUNAS_DELETE_SUCESSO", aelDescMatLacunas.getTextoLacuna());

//			examesPatologiaFacade.flush();

			criaObjetoInsercao();
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void ativarInativar(final Short seqp) {

		try {

			if (seqp != null) {

				aelDescMatLacunas = examesPatologiaFacade.obterAelDescMatLacunas(new AelDescMatLacunasId(aelGrpDescMatLacunasGtmSeq,
						aelGrpDescMatLacunasLdaSeq, aelGrpDescMatLacunasSeqp, seqp));

				aelDescMatLacunas.setIndSituacao((DominioSituacao.A.equals(aelDescMatLacunas.getIndSituacao()) ? DominioSituacao.I
						: DominioSituacao.A));

//				RapServidores servidorLogado = registroColaboradorFacade
//						.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());

				examesPatologiaFacade.alterarAelDescMatLacunas(aelDescMatLacunas);
//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO,
						(DominioSituacao.A.equals(aelDescMatLacunas.getIndSituacao()) ? "MENSAGEM_AEL_TXT_DESC_MATS_LACUNAS_INATIVADO_SUCESSO"
								: "MENSAGEM_AEL_TXT_DESC_MATS_LACUNAS_ATIVADO_SUCESSO"), aelDescMatLacunas.getTextoLacuna());

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
	
	public Short getAelGrpDescMatLacunasGtmSeq() {
		return aelGrpDescMatLacunasGtmSeq;
	}

	public void setAelGrpDescMatLacunasGtmSeq(Short aelGrpDescMatLacunasGtmSeq) {
		this.aelGrpDescMatLacunasGtmSeq = aelGrpDescMatLacunasGtmSeq;
	}

	public Short getAelGrpDescMatLacunasLdaSeq() {
		return aelGrpDescMatLacunasLdaSeq;
	}

	public void setAelGrpDescMatLacunasLdaSeq(Short aelGrpDescMatLacunasLdaSeq) {
		this.aelGrpDescMatLacunasLdaSeq = aelGrpDescMatLacunasLdaSeq;
	}

	public void setAelGrpDescMatLacunasSeqp(Short aelGrpDescMatLacunasSeqp) {
		this.aelGrpDescMatLacunasSeqp = aelGrpDescMatLacunasSeqp;
	}

	public List<AelDescMatLacunas> getLista() {
		return lista;
	}

	public void setLista(List<AelDescMatLacunas> lista) {
		this.lista = lista;
	}

	public AelGrpTxtDescMats getAelGrpTxtDescMats() {
		return aelGrpTxtDescMats;
	}

	public void setAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtDescMats) {
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
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

	public AelDescMatLacunas getAelDescMatLacunas() {
		return aelDescMatLacunas;
	}

	public void setAelDescMatLacunas(AelDescMatLacunas aelDescMatLacunas) {
		this.aelDescMatLacunas = aelDescMatLacunas;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public AelDescMatLacunasId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelDescMatLacunasId idExcluir) {
		this.idExcluir = idExcluir;
	}

}
