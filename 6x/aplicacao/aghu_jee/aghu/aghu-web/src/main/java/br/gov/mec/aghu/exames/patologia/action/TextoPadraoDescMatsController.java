package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMatsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Textos padrão Descrição de Materiais
 * 
 */


public class TextoPadraoDescMatsController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

//	private static final Log LOG = LogFactory.getLog(TextoPadraoDescMatsController.class);
	private static final String GRUPO = "grupoDescMatsLacuna";
	private static final String VOLTAR = "grupoTextoPadraoDescMats";

	private static final long serialVersionUID = 1625264870242498430L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short seqAelGrpTxtDescMats;
	
	private List<AelTxtDescMats> lista;
	
	//Pai
	private AelGrpTxtDescMats aelGrpTxtDescMats = new AelGrpTxtDescMats();
	
	//Filho
	private AelTxtDescMats aelTxtDescMats = new AelTxtDescMats();
	
	private boolean editando;
	
	private Short seqp;

	private AelTxtDescMatsId idExcluir;

	public void inicio() {
	 

		editando = false;
		aelGrpTxtDescMats = this.examesPatologiaFacade.obterAelGrpTxtDescMats(seqAelGrpTxtDescMats);
		criaObjetoInsercao();
		pesquisar();
	
	}
	
	private void criaObjetoInsercao() {
		aelTxtDescMats = new AelTxtDescMats();
		AelTxtDescMatsId id = new AelTxtDescMatsId();
		id.setGtmSeq(aelGrpTxtDescMats.getSeq());
		aelTxtDescMats.setId(id);
		aelTxtDescMats.setAelGrpTxtDescMats(aelGrpTxtDescMats);
		
	}

	/** Método usado no botão pesquisar
	 * 
	 * @return
	 */
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarTextoPadraoDescMatsPorAelGrpTxtDescMats(seqAelGrpTxtDescMats);
	}
	
	public void gravar() {
		try {
//			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			if(aelTxtDescMats.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelTxtDescMats(aelTxtDescMats);
//				examesPatologiaFacade.flush();
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_DESC_MATS_UPDATE_SUCESSO", aelTxtDescMats.getDescricao());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelTxtDescMats(aelTxtDescMats);
				
//				examesPatologiaFacade.flush();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_DESC_MATS_INSERT_SUCESSO", aelTxtDescMats.getDescricao());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public String grupo() {
		return GRUPO;
	}
	
	public void editar(final Short seqp) {
		editando = true;
		aelTxtDescMats = examesPatologiaFacade.obterAelTxtDescMats(new AelTxtDescMatsId(aelGrpTxtDescMats.getSeq(), seqp));
	}
		
	public void cancelarEdicao() {
		editando = false;
//		examesPatologiaFacade.refresh(aelTxtDescMats);
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try {
			this.aelTxtDescMats = this.examesPatologiaFacade.obterAelTxtDescMats(idExcluir);
			examesPatologiaFacade.excluirAelTxtDescMats(aelTxtDescMats);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_DESC_MATS_DELETE_SUCESSO", aelGrpTxtDescMats.getDescricao());
			
//			examesPatologiaFacade.flush();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		criaObjetoInsercao();
		pesquisar();
	}
	
	public void ativarInativar(final Short seqp) {
		
		try {
		
			if(aelGrpTxtDescMats.getSeq() != null && seqp != null){
				aelTxtDescMats = examesPatologiaFacade.obterAelTxtDescMats(new AelTxtDescMatsId(aelGrpTxtDescMats.getSeq(), seqp));
				aelTxtDescMats.setIndSituacao( (DominioSituacao.A.equals(aelTxtDescMats.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
				
//				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				
				examesPatologiaFacade.alterarAelTxtDescMats(aelTxtDescMats);
//				examesPatologiaFacade.flush();
				
				apresentarMsgNegocio(Severity.INFO,
						(DominioSituacao.A.equals(aelTxtDescMats.getIndSituacao()) ? "MENSAGEM_AEL_TEXTO_PADRAO_DESC_MATS_INATIVADO_SUCESSO"
								: "MENSAGEM_AEL_TEXTO_PADRAO_DESC_MATS_ATIVADO_SUCESSO"), aelGrpTxtDescMats.getDescricao());
				
				criaObjetoInsercao();
			}
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}

	public String voltar(){
		return VOLTAR;
	}
	
	public Short getSeqAelGrpTxtDescMats() {
		return seqAelGrpTxtDescMats;
	}

	public void setSeqAelGrpTxtDescMats(Short seqAelGrpTxtDescMats) {
		this.seqAelGrpTxtDescMats = seqAelGrpTxtDescMats;
	}

	public List<AelTxtDescMats> getLista() {
		return lista;
	}

	public void setLista(List<AelTxtDescMats> lista) {
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

	public AelTxtDescMatsId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTxtDescMatsId idExcluir) {
		this.idExcluir = idExcluir;
	}
	
	
}
