package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicroId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TextoPadraoMicroscopiaController extends ActionController {

	private static final long serialVersionUID = -412555805694513692L;

	private static final String GRUPO_TEXTO_PADRAO_MICROSCOPIA = "grupoTextoPadraoMicroscopia";

	private static final String GRUPO_MICRO_LACUNA = "grupoMicroLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short seqAelGrpTxtPadraoMicro;
	
	private List<AelTextoPadraoMicro> lista;
	
	//Pai
	private AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro;
	
	//Filho
	private AelTextoPadraoMicro aelTextoPadraoMicro;
	
	private boolean editando;
	
	private AelTextoPadraoMicroId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		aelGrpTxtPadraoMicro = this.examesPatologiaFacade.obterAelGrpTxtPadraoMicro(seqAelGrpTxtPadraoMicro);
		
		if(aelGrpTxtPadraoMicro == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelTextoPadraoMicro = new AelTextoPadraoMicro();
		AelTextoPadraoMicroId id = new AelTextoPadraoMicroId();
		id.setLuuSeq(aelGrpTxtPadraoMicro.getSeq());
		aelTextoPadraoMicro.setId(id);
		aelTextoPadraoMicro.setAelGrpTxtPadraoMicros(aelGrpTxtPadraoMicro);
		
	}

	/** Método usado no botão pesquisar */
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarTextoPadraoMicroPorAelGrpTxtPadraoMicro(seqAelGrpTxtPadraoMicro);
	}
	
	public void gravar() {
		try {
			if(aelTextoPadraoMicro.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelTextoPadraoMicro(aelTextoPadraoMicro);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_MICROS_UPDATE_SUCESSO", aelTextoPadraoMicro.getDescricao());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelTextoPadraoMicro(aelTextoPadraoMicro);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_MICROS_INSERT_SUCESSO", aelTextoPadraoMicro.getDescricao());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelTextoPadraoMicro aelTextoPadraoMicro) {
		editando = true;
		this.aelTextoPadraoMicro = aelTextoPadraoMicro;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try {
			aelTextoPadraoMicro = examesPatologiaFacade.obterAelTextoPadraoMicro(idExcluir);
			idExcluir = null;
			
			if(aelTextoPadraoMicro == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			examesPatologiaFacade.excluirAelTextoPadraoMicro(aelTextoPadraoMicro.getId());
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_MICROS_DELETE_SUCESSO", aelGrpTxtPadraoMicro.getDescricao());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		criaObjetoInsercao();
		pesquisar();
	}
	
	public void ativarInativar(final AelTextoPadraoMicro elemento) {
		try {
			
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelTextoPadraoMicro(elemento);
			
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
										    	 ? "MENSAGEM_AEL_TEXTO_PADRAO_MICROS_INATIVADO_SUCESSO" 
												 : "MENSAGEM_AEL_TEXTO_PADRAO_MICROS_ATIVADO_SUCESSO" 
										       ), elemento.getDescricao());
			
			criaObjetoInsercao();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}

	public String detalharTextoPadraoMicroscopia(){
		return GRUPO_MICRO_LACUNA;
	}

	public String voltar(){
		return GRUPO_TEXTO_PADRAO_MICROSCOPIA;
	}

	public Short getSeqAelGrpTxtPadraoMicro() {
		return seqAelGrpTxtPadraoMicro;
	}

	public void setSeqAelGrpTxtPadraoMicro(Short seqAelGrpTxtPadraoMicro) {
		this.seqAelGrpTxtPadraoMicro = seqAelGrpTxtPadraoMicro;
	}

	public List<AelTextoPadraoMicro> getLista() {
		return lista;
	}

	public void setLista(List<AelTextoPadraoMicro> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoMicro getAelGrpTxtPadraoMicro() {
		return aelGrpTxtPadraoMicro;
	}

	public void setAelGrpTxtPadraoMicro(AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) {
		this.aelGrpTxtPadraoMicro = aelGrpTxtPadraoMicro;
	}

	public AelTextoPadraoMicro getAelTextoPadraoMicro() {
		return aelTextoPadraoMicro;
	}

	public void setAelTextoPadraoMicro(AelTextoPadraoMicro aelTextoPadraoMicro) {
		this.aelTextoPadraoMicro = aelTextoPadraoMicro;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelTextoPadraoMicroId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTextoPadraoMicroId idExcluir) {
		this.idExcluir = idExcluir;
	}
}