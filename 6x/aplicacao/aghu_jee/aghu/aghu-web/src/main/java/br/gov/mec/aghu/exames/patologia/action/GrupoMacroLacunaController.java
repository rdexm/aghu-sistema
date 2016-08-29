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
import br.gov.mec.aghu.model.AelTextoPadraoMacroId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoMacroLacunaController extends ActionController {

	private static final long serialVersionUID = 7565616194563254483L;

	private static final String TEXTO_PADRAO_MACROSCOPIA = "textoPadraoMacroscopia";

	private static final String TEXTO_MACRO_LACUNA = "textoMacroLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private Short aelTextoPadraoMacroLubSeq;
	
	private Short aelTextoPadraoMacroSeqp;
	
	private List<AelGrpMacroLacuna> lista;

	//Avo
	private AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro;
	
	//Pai
	private AelTextoPadraoMacro aelTextoPadraoMacro;
	
	//Filho
	private AelGrpMacroLacuna aelGrpMacroLacuna;
	
	private boolean editando;
	
	private AelGrpMacroLacunaId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		editando = false;
		aelTextoPadraoMacro = examesPatologiaFacade.obterAelTextoPadraoMacro(new AelTextoPadraoMacroId(aelTextoPadraoMacroLubSeq, aelTextoPadraoMacroSeqp));

		if(aelTextoPadraoMacro == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelGrpMacroLacuna = new AelGrpMacroLacuna();
		aelGrpMacroLacuna.setAelTextoPadraoMacros(aelTextoPadraoMacro);
		AelGrpMacroLacunaId id = new AelGrpMacroLacunaId();
		id.setLufLubSeq(aelTextoPadraoMacro.getId().getLubSeq());
		id.setLufSeqp(aelTextoPadraoMacro.getId().getSeqp());
		aelGrpMacroLacuna.setId(id);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(aelTextoPadraoMacroLubSeq, aelTextoPadraoMacroSeqp, null);
	}
	
	public void gravar() {
		try {
			if(aelGrpMacroLacuna.getId() != null && aelGrpMacroLacuna.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelGrpMacroLacuna(aelGrpMacroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_MACRO_LACUNAS_UPDATE_SUCESSO", aelGrpMacroLacuna.getLacuna());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelGrpMacroLacuna(aelGrpMacroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_MACRO_LACUNAS_INSERT_SUCESSO", aelGrpMacroLacuna.getLacuna());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelGrpMacroLacuna aelGrpMacroLacuna) {
		editando = true;
		this.aelGrpMacroLacuna = aelGrpMacroLacuna;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try {
			aelGrpMacroLacuna = examesPatologiaFacade.obterAelGrpMacroLacuna(idExcluir);
			idExcluir = null;
			
			if(aelGrpMacroLacuna == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelGrpMacroLacuna(aelGrpMacroLacuna.getId());
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_MACRO_LACUNAS_DELETE_SUCESSO", aelGrpMacroLacuna.getLacuna());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
		criaObjetoInsercao();
		pesquisar();
	}
	
	public void ativarInativar(final AelGrpMacroLacuna elemento) {
		try {
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelGrpMacroLacuna(elemento);
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
										    	 ? "MENSAGEM_AEL_GRP_MACRO_LACUNAS_INATIVADO_SUCESSO" 
												 : "MENSAGEM_AEL_GRP_MACRO_LACUNAS_ATIVADO_SUCESSO" 
										       ), elemento.getLacuna());
			
			criaObjetoInsercao();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public String detalharGrupoMacroLacuna(){
		return TEXTO_MACRO_LACUNA;
	}
	
	public String voltar(){
		return TEXTO_PADRAO_MACROSCOPIA;
	}

	public Short getAelTextoPadraoMacroLubSeq() {
		return aelTextoPadraoMacroLubSeq;
	}

	public void setAelTextoPadraoMacroLubSeq(Short aelTextoPadraoMacroLubSeq) {
		this.aelTextoPadraoMacroLubSeq = aelTextoPadraoMacroLubSeq;
	}

	public Short getAelTextoPadraoMacroSeqp() {
		return aelTextoPadraoMacroSeqp;
	}

	public void setAelTextoPadraoMacroSeqp(Short aelTextoPadraoMacroSeqp) {
		this.aelTextoPadraoMacroSeqp = aelTextoPadraoMacroSeqp;
	}

	public List<AelGrpMacroLacuna> getLista() {
		return lista;
	}

	public void setLista(List<AelGrpMacroLacuna> lista) {
		this.lista = lista;
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

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelGrpTxtPadraoMacro getAelGrpTxtPadraoMacro() {
		return aelGrpTxtPadraoMacro;
	}

	public void setAelGrpTxtPadraoMacro(AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) {
		this.aelGrpTxtPadraoMacro = aelGrpTxtPadraoMacro;
	}

	public AelGrpMacroLacunaId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelGrpMacroLacunaId idExcluir) {
		this.idExcluir = idExcluir;
	}

	
}
