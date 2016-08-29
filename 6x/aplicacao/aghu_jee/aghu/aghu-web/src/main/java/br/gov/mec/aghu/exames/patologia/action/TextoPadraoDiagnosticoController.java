package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TextoPadraoDiagnosticoController extends ActionController {

	private static final long serialVersionUID = -9105033098798036127L;

	private static final String GRUPO_TEXTO_PADRAO_DIAGNOSTICO = "grupoTextoPadraoDiagnostico";

	private static final String GRUPO_DIAGNOSTICO_LACUNA = "grupoDiagnosticoLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short seqAelGrpTxtPadraoDiag;

	private List<AelTextoPadraoDiags> lista;

	// Pai
	private AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags;

	// Filho
	private AelTextoPadraoDiags aelTextoPadraoDiags;

	private boolean editando;
	
	private AelTextoPadraoDiagsId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		editando = false;
		this.aelGrpTxtPadraoDiags = this.examesPatologiaFacade.obterAelGrpTxtPadraoDiags(this.seqAelGrpTxtPadraoDiag);
		
		if(aelGrpTxtPadraoDiags == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		this.criarObjetoInsersao();
		this.pesquisar();
		
		return null;
	
	}

	/**
	 * Método usado no botão pesquisar
	 */
	public void pesquisar() {
		this.lista = this.examesPatologiaFacade.pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(this.seqAelGrpTxtPadraoDiag);
	}

	public void gravar() {
		final boolean novo = this.aelTextoPadraoDiags.getId() == null || this.aelTextoPadraoDiags.getId().getSeqp() == null;
		try {
			
			examesPatologiaFacade.persistirAelTextoPadraoDiags(this.aelTextoPadraoDiags);

			apresentarMsgNegocio(Severity.INFO, novo ? "MENSAGEM_AEL_TEXTO_PADRAO_DIAGNOSTICO_INSERT_SUCESSO" : "MENSAGEM_AEL_TEXTO_PADRAO_DIAGNOSTICO_UPDATE_SUCESSO", 
								 aelTextoPadraoDiags.getDescricao());
			if (!novo) {
				this.cancelarEdicao();
			}

			this.criarObjetoInsersao();
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}

	public void editar(final AelTextoPadraoDiags aelTextoPadraoDiags) {
		this.editando = true;
		this.aelTextoPadraoDiags = aelTextoPadraoDiags;
	}

	public void cancelarEdicao() {
		this.editando = false;
		this.criarObjetoInsersao();
	}
	
	private void criarObjetoInsersao() {
		this.aelTextoPadraoDiags = new AelTextoPadraoDiags();
		this.aelTextoPadraoDiags.setAelGrpTxtPadraoDiags(this.aelGrpTxtPadraoDiags);
	}
	
	public void excluir() {
		try {
			final AelTextoPadraoDiags aelTextoPadraoDiagsExcluir = this.examesPatologiaFacade.obterAelTextoPadraoDiags(idExcluir);
			idExcluir = null;
			
			if(aelTextoPadraoDiagsExcluir == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			this.examesPatologiaFacade.excluirAelTextoPadraoDiags(aelTextoPadraoDiagsExcluir.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_DIAGNOSTICO_DELETE_SUCESSO", aelTextoPadraoDiagsExcluir.getDescricao());
			this.pesquisar();
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void ativarInativar(final AelTextoPadraoDiags elemento) {
		try {
			String msg;
			if(DominioSituacao.A.equals(elemento.getIndSituacao())){
				elemento.setIndSituacao(DominioSituacao.I);
				msg = "MENSAGEM_AEL_TEXTO_PADRAO_DIAGNOSTICO_INATIVADO_SUCESSO";
				
			} else {
				elemento.setIndSituacao(DominioSituacao.A);
				msg = "MENSAGEM_AEL_TEXTO_PADRAO_DIAGNOSTICO_ATIVADO_SUCESSO";
			}
			
			examesPatologiaFacade.persistirAelTextoPadraoDiags(elemento);
			apresentarMsgNegocio( Severity.INFO, msg, elemento.getDescricao());

			criarObjetoInsersao();
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}
	
	public String detalharTextoPadraoDiagnostico(){
		return GRUPO_DIAGNOSTICO_LACUNA;
	}

	public String voltar() {
		return GRUPO_TEXTO_PADRAO_DIAGNOSTICO;
	}

	public Short getSeqAelGrpTxtPadraoDiag() {
		return seqAelGrpTxtPadraoDiag;
	}

	public void setSeqAelGrpTxtPadraoDiag(final Short seqAelGrpTxtPadraoDiag) {
		this.seqAelGrpTxtPadraoDiag = seqAelGrpTxtPadraoDiag;
	}

	public List<AelTextoPadraoDiags> getLista() {
		return lista;
	}

	public void setLista(final List<AelTextoPadraoDiags> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoDiags getAelGrpTxtPadraoDiags() {
		return aelGrpTxtPadraoDiags;
	}

	public void setAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		this.aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiags;
	}

	public AelTextoPadraoDiags getAelTextoPadraoDiags() {
		return aelTextoPadraoDiags;
	}

	public void setAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) {
		this.aelTextoPadraoDiags = aelTextoPadraoDiags;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(final boolean editando) {
		this.editando = editando;
	} 

	public AelTextoPadraoDiagsId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTextoPadraoDiagsId idExcluir) {
		this.idExcluir = idExcluir;
	}
}