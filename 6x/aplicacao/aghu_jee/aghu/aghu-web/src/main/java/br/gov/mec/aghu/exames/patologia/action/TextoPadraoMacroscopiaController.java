package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacroId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TextoPadraoMacroscopiaController extends ActionController {

	private static final long serialVersionUID = 1625264870242498430L;

	private static final String GRUPO_TEXTO_PADRAO_MACROSCOPIA = "grupoTextoPadraoMacroscopia";

	private static final String GRUPO_MACRO_LACUNA = "grupoMacroLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short seqAelGrpTxtPadraoMacro;
	
	private List<AelTextoPadraoMacro> lista;
	
	//Pai
	private AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro;
	
	//Filho
	private AelTextoPadraoMacro aelTextoPadraoMacro;
	
	private AelTextoPadraoMacroId idExcluir;
	
	private boolean editando;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		editando = false;
		aelGrpTxtPadraoMacro = this.examesPatologiaFacade.obterAelGrpTxtPadraoMacro(seqAelGrpTxtPadraoMacro);

		if(aelGrpTxtPadraoMacro == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelTextoPadraoMacro = new AelTextoPadraoMacro();
		AelTextoPadraoMacroId id = new AelTextoPadraoMacroId();
		id.setLubSeq(aelGrpTxtPadraoMacro.getSeq());
		aelTextoPadraoMacro.setId(id);
		aelTextoPadraoMacro.setAelGrpTxtPadraoMacros(aelGrpTxtPadraoMacro);
		
	}

	/** Método usado no botão pesquisar  */
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarTextoPadraoMacroPorAelGrpTxtPadraoMacro(seqAelGrpTxtPadraoMacro);
	}
	
	public void gravar() {
		try {
			if(aelTextoPadraoMacro.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelTextoPadraoMacro(aelTextoPadraoMacro);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_MACROS_UPDATE_SUCESSO", aelTextoPadraoMacro.getDescricao());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelTextoPadraoMacro(aelTextoPadraoMacro);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_MACROS_INSERT_SUCESSO", aelTextoPadraoMacro.getDescricao());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelTextoPadraoMacro aelTextoPadraoMacro) {
		editando = true;
		this.aelTextoPadraoMacro = aelTextoPadraoMacro;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try {
			aelTextoPadraoMacro = examesPatologiaFacade.obterAelTextoPadraoMacro(idExcluir);
			idExcluir = null;
			
			if(aelTextoPadraoMacro == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelTextoPadraoMacro(aelTextoPadraoMacro.getId());
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_MACROS_DELETE_SUCESSO", aelGrpTxtPadraoMacro.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		criaObjetoInsercao();
		pesquisar();
	}
	
	public void ativarInativar(final AelTextoPadraoMacro elemento) {
		
		try {
			
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelTextoPadraoMacro(elemento);
			
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
										    	 ? "MENSAGEM_AEL_TEXTO_PADRAO_MACROS_INATIVADO_SUCESSO" 
												 : "MENSAGEM_AEL_TEXTO_PADRAO_MACROS_ATIVADO_SUCESSO" 
										       ), aelGrpTxtPadraoMacro.getDescricao());
			
			criaObjetoInsercao();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}

	public String detalharTextoPadraoMacroscopia(){
		return GRUPO_MACRO_LACUNA;
	}
	
	public String voltar(){
		return GRUPO_TEXTO_PADRAO_MACROSCOPIA;
	}
	
	public Short getSeqAelGrpTxtPadraoMacro() {
		return seqAelGrpTxtPadraoMacro;
	}

	public void setSeqAelGrpTxtPadraoMacro(Short seqAelGrpTxtPadraoMacro) {
		this.seqAelGrpTxtPadraoMacro = seqAelGrpTxtPadraoMacro;
	}

	public List<AelTextoPadraoMacro> getLista() {
		return lista;
	}

	public void setLista(List<AelTextoPadraoMacro> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoMacro getAelGrpTxtPadraoMacro() {
		return aelGrpTxtPadraoMacro;
	}

	public void setAelGrpTxtPadraoMacro(AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) {
		this.aelGrpTxtPadraoMacro = aelGrpTxtPadraoMacro;
	}

	public AelTextoPadraoMacro getAelTextoPadraoMacro() {
		return aelTextoPadraoMacro;
	}

	public void setAelTextoPadraoMacro(AelTextoPadraoMacro aelTextoPadraoMacro) {
		this.aelTextoPadraoMacro = aelTextoPadraoMacro;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelTextoPadraoMacroId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTextoPadraoMacroId idExcluir) {
		this.idExcluir = idExcluir;
	}
	
	
}
