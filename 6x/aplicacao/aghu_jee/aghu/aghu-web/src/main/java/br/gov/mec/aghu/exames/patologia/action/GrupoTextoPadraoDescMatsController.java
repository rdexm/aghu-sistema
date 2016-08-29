package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Grupos de textos padrão Descrição de Materiais
 * 
 */


public class GrupoTextoPadraoDescMatsController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

//	private static final Log LOG = LogFactory.getLog(GrupoTextoPadraoDescMatsController.class);


	private static final long serialVersionUID = 1986388899457928589L;

	private static final String INICIAR_INCLUSAO = "textoPadraoDescMats";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private boolean ativo;

	//usado para pesquisar
	private Short seq;
	
	private String descricao;
	
	private DominioSituacao situacao;
	
	private List<AelGrpTxtDescMats> lista;
	
	//Para Adicionar itens
	private AelGrpTxtDescMats aelGrpTxtDescMats;
	
	private boolean editando;

	private Short seqExcluir;
	
	public GrupoTextoPadraoDescMatsController() {
		aelGrpTxtDescMats = new AelGrpTxtDescMats();
	}
	
	/** Método usado no botão pesquisar
	 * 
	 * @return
	 */
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarGrupoTextoPadraoDescMats(seq, descricao, situacao);
		editando = false;
 		aelGrpTxtDescMats = new AelGrpTxtDescMats(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		editando = false;
 		aelGrpTxtDescMats = new AelGrpTxtDescMats(); 
	}
	
	public String iniciarInclusao() {
		return INICIAR_INCLUSAO;
	}
	
	public void gravar() {
		try {
//			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			if(aelGrpTxtDescMats.getSeq() != null){
				examesPatologiaFacade.alterarAelGrpTxtDescMats(aelGrpTxtDescMats);
//				examesPatologiaFacade.flush();
				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_DESC_MATS_UPDATE_SUCESSO", aelGrpTxtDescMats.getDescricao());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelGrpTxtDescMats(aelGrpTxtDescMats);
				
//				examesPatologiaFacade.flush();
				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_DESC_MATS_INSERT_SUCESSO", aelGrpTxtDescMats.getDescricao());
			}
			
			aelGrpTxtDescMats = new AelGrpTxtDescMats();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final Short seq) {
		this.editando = true;
		this.aelGrpTxtDescMats = this.examesPatologiaFacade.obterAelGrpTxtDescMats(seq);
	}
		
	public void cancelarEdicao() {
		editando = false;
//		examesPatologiaFacade.refresh(aelGrpTxtDescMats);
		pesquisar();
		aelGrpTxtDescMats = new AelGrpTxtDescMats();
	}		
	
	public void excluir() {
		try {
			this.aelGrpTxtDescMats = this.examesPatologiaFacade.obterAelGrpTxtDescMats(seqExcluir);
			examesPatologiaFacade.excluirAelGrpTxtDescMats(aelGrpTxtDescMats);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_DESC_MATS_DELETE_SUCESSO", aelGrpTxtDescMats.getDescricao());
			
//			examesPatologiaFacade.flush();
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		} 
		pesquisar();
	}
	
	public void ativarInativar(final Short seq) {
		try {
			if(seq != null){
//				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				
				aelGrpTxtDescMats = this.examesPatologiaFacade.obterAelGrpTxtDescMats(seq);
				aelGrpTxtDescMats.setIndSituacao( (DominioSituacao.A.equals(aelGrpTxtDescMats.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
				
				examesPatologiaFacade.alterarAelGrpTxtDescMats(aelGrpTxtDescMats);
//				examesPatologiaFacade.flush();
				
				this.apresentarMsgNegocio(Severity.INFO,
						(DominioSituacao.A.equals(aelGrpTxtDescMats.getIndSituacao()) ? "MENSAGEM_AEL_GRP_TXT_PADRAO_DESC_MATS_INATIVADO_SUCESSO"
								: "MENSAGEM_AEL_GRP_TXT_PADRAO_DESC_MATS_ATIVADO_SUCESSO"), aelGrpTxtDescMats.getDescricao());
				
				aelGrpTxtDescMats = new AelGrpTxtDescMats();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AelGrpTxtDescMats> getLista() {
		return lista;
	}

	public void setLista(List<AelGrpTxtDescMats> lista) {
		this.lista = lista;
	}

	public AelGrpTxtDescMats getAelGrpTxtDescMats() {
		return aelGrpTxtDescMats;
	}

	public void setAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtDescMats) {
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public Short getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Short seqExcluir) {
		this.seqExcluir = seqExcluir;
	}
	
	
}
