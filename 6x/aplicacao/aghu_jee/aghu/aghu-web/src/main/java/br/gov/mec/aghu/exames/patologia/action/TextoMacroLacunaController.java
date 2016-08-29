package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelGrpMacroLacunaId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;
import br.gov.mec.aghu.model.AelTxtMacroLacunaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TextoMacroLacunaController extends ActionController {

	private static final long serialVersionUID = 2397479408205843543L;

	private static final String GRUPO_MACRO_LACUNA = "grupoMacroLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short aelGrpMacroLacunaLufLubSeq;

	private Short aelGrpMacroLacunaLufSeqp;
	
	private Short aelGrpMacroLacunaSeqp;
	
	private List<AelTxtMacroLacuna> lista;

	//Bisavo
	private AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro;
	
	//Avo
	private AelTextoPadraoMacro aelTextoPadraoMacro;
	
	//Pai
	private AelGrpMacroLacuna aelGrpMacroLacuna;
	
	//Filho
	private AelTxtMacroLacuna aelTxtMacroLacuna;
	
	private boolean editando;
	
	private AelTxtMacroLacunaId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String inicio() {
	 

		editando = false;
		aelGrpMacroLacuna = examesPatologiaFacade.obterAelGrpMacroLacuna(new AelGrpMacroLacunaId( aelGrpMacroLacunaLufLubSeq, 
																								  aelGrpMacroLacunaLufSeqp, 
																								  aelGrpMacroLacunaSeqp));
		if(aelGrpMacroLacuna == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelTxtMacroLacuna = new AelTxtMacroLacuna();
		aelTxtMacroLacuna.setAelGrpMacroLacunas(aelGrpMacroLacuna);
		
		AelTxtMacroLacunaId id = new AelTxtMacroLacunaId();
		id.setLo3LufLubSeq(aelGrpMacroLacuna.getId().getLufLubSeq());
		id.setLo3LufSeqp(aelGrpMacroLacuna.getId().getLufSeqp());
		id.setLo3Seqp(aelGrpMacroLacuna.getId().getSeqp());
		
		aelTxtMacroLacuna.setId(id);
	}

	public void pesquisar() {	
		lista = examesPatologiaFacade.pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(aelGrpMacroLacuna, null);
	}
	
	public void gravar() {
		try {
			if(aelTxtMacroLacuna.getId() != null && aelTxtMacroLacuna.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelTxtMacroLacuna(aelTxtMacroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TXT_MACRO_LACUNAS_UPDATE_SUCESSO", aelTxtMacroLacuna.getTextoLacuna());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelTxtMacroLacuna(aelTxtMacroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TXT_MACRO_LACUNAS_INSERT_SUCESSO", aelTxtMacroLacuna.getTextoLacuna());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelTxtMacroLacuna aelTxtMacroLacuna) {
		editando = true;
		this.aelTxtMacroLacuna = aelTxtMacroLacuna;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try {
			aelTxtMacroLacuna = examesPatologiaFacade.obterAelTxtMacroLacuna(idExcluir);
			
			if(aelTextoPadraoMacro == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelTxtMacroLacuna(idExcluir);
			idExcluir = null;
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TXT_MACRO_LACUNAS_DELETE_SUCESSO", aelTxtMacroLacuna.getTextoLacuna());
			criaObjetoInsercao();
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
	}
	
	public void ativarInativar(final AelTxtMacroLacuna elemento) {
		
		try {

			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelTxtMacroLacuna(elemento);
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
										    	 ? "MENSAGEM_AEL_TXT_MACRO_LACUNAS_INATIVADO_SUCESSO" 
												 : "MENSAGEM_AEL_TXT_MACRO_LACUNAS_ATIVADO_SUCESSO" 
										       ), elemento.getTextoLacuna());
			criaObjetoInsercao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public String voltar(){
		return GRUPO_MACRO_LACUNA;
	}

	public Short getAelGrpMacroLacunaLufLubSeq() {
		return aelGrpMacroLacunaLufLubSeq;
	}

	public void setAelGrpMacroLacunaLufLubSeq(Short aelGrpMacroLacunaLufLubSeq) {
		this.aelGrpMacroLacunaLufLubSeq = aelGrpMacroLacunaLufLubSeq;
	}

	public Short getAelGrpMacroLacunaLufSeqp() {
		return aelGrpMacroLacunaLufSeqp;
	}

	public void setAelGrpMacroLacunaLufSeqp(Short aelGrpMacroLacunaLufSeqp) {
		this.aelGrpMacroLacunaLufSeqp = aelGrpMacroLacunaLufSeqp;
	}

	public Short getAelGrpMacroLacunaSeqp() {
		return aelGrpMacroLacunaSeqp;
	}

	public void setAelGrpMacroLacunaSeqp(Short aelGrpMacroLacunaSeqp) {
		this.aelGrpMacroLacunaSeqp = aelGrpMacroLacunaSeqp;
	}

	public List<AelTxtMacroLacuna> getLista() {
		return lista;
	}

	public void setLista(List<AelTxtMacroLacuna> lista) {
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

	public AelGrpMacroLacuna getAelGrpMacroLacuna() {
		return aelGrpMacroLacuna;
	}

	public void setAelGrpMacroLacuna(AelGrpMacroLacuna aelGrpMacroLacuna) {
		this.aelGrpMacroLacuna = aelGrpMacroLacuna;
	}

	public AelTxtMacroLacuna getAelTxtMacroLacuna() {
		return aelTxtMacroLacuna;
	}

	public void setAelTxtMacroLacuna(AelTxtMacroLacuna aelTxtMacroLacuna) {
		this.aelTxtMacroLacuna = aelTxtMacroLacuna;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	} 

	public AelTxtMacroLacunaId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTxtMacroLacunaId idExcluir) {
		this.idExcluir = idExcluir;
	}
	
	
}
