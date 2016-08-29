package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpMicroLacuna;
import br.gov.mec.aghu.model.AelGrpMicroLacunaId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicro;
import br.gov.mec.aghu.model.AelTxtMicroLacuna;
import br.gov.mec.aghu.model.AelTxtMicroLacunaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TextoMicroLacunaController extends ActionController {

	private static final long serialVersionUID = -7652028685537055585L;

	private static final String GRUPO_MICRO_LACUNA = "grupoMicroLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Short aelGrpMicroLacunaLufLubSeq;

	private Short aelGrpMicroLacunaLufSeqp;
	
	private Short aelGrpMicroLacunaSeqp;
	
	private List<AelTxtMicroLacuna> lista;

	//Bisavo
	private AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro;
	
	//Avo
	private AelTextoPadraoMicro aelTextoPadraoMicro;
	
	//Pai
	private AelGrpMicroLacuna aelGrpMicroLacuna;
	
	//Filho
	private AelTxtMicroLacuna aelTxtMicroLacuna;
	
	private boolean editando;
	
	private AelTxtMicroLacunaId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		aelGrpMicroLacuna = examesPatologiaFacade.obterAelGrpMicroLacuna(new AelGrpMicroLacunaId( aelGrpMicroLacunaLufLubSeq, aelGrpMicroLacunaLufSeqp, aelGrpMicroLacunaSeqp));
		
		if(aelGrpMicroLacuna == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelTxtMicroLacuna = new AelTxtMicroLacuna();
		aelTxtMicroLacuna.setAelGrpMicroLacunas(aelGrpMicroLacuna);
		
		AelTxtMicroLacunaId id = new AelTxtMicroLacunaId();
		id.setLu9LuvLuuSeq(aelGrpMicroLacuna.getId().getLuvLuuSeq());
		id.setLu9LuvSeqp(aelGrpMicroLacuna.getId().getLuvSeqp());
		id.setLu9Seqp(aelGrpMicroLacuna.getId().getSeqp());
		
		aelTxtMicroLacuna.setId(id);
	}

	public void pesquisar() {	
		lista = examesPatologiaFacade.pesquisarAelTxtMicroLacunaPorAelGrpMicroLacuna(aelGrpMicroLacuna);
	}
	
	public void gravar() {
		try {
			if(aelTxtMicroLacuna.getId() != null && aelTxtMicroLacuna.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelTxtMicroLacuna(aelTxtMicroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TXT_MICRO_LACUNAS_UPDATE_SUCESSO", aelTxtMicroLacuna.getTextoLacuna());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelTxtMicroLacuna(aelTxtMicroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TXT_MICRO_LACUNAS_INSERT_SUCESSO", aelTxtMicroLacuna.getTextoLacuna());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelTxtMicroLacuna aelTxtMicroLacuna) {
		editando = true;
		this.aelTxtMicroLacuna = aelTxtMicroLacuna;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try { 
			aelTxtMicroLacuna = examesPatologiaFacade.obterAelTxtMicroLacuna(idExcluir);
			idExcluir = null;
			
			if(aelTxtMicroLacuna == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelTxtMicroLacuna(aelTxtMicroLacuna);
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TXT_MICRO_LACUNAS_DELETE_SUCESSO", aelTxtMicroLacuna.getTextoLacuna());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
		criaObjetoInsercao();
		pesquisar();
	}
	
	public void ativarInativar(final AelTxtMicroLacuna elemento) {
		
		try {
			
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			
			examesPatologiaFacade.alterarAelTxtMicroLacuna(elemento);
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
										    	 ? "MENSAGEM_AEL_TXT_MICRO_LACUNAS_INATIVADO_SUCESSO" 
												 : "MENSAGEM_AEL_TXT_MICRO_LACUNAS_ATIVADO_SUCESSO" 
										       ), elemento.getTextoLacuna());
			
			criaObjetoInsercao();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public String voltar(){
		return GRUPO_MICRO_LACUNA;
	}

	public Short getAelGrpMicroLacunaLufLubSeq() {
		return aelGrpMicroLacunaLufLubSeq;
	}

	public void setAelGrpMicroLacunaLufLubSeq(Short aelGrpMicroLacunaLufLubSeq) {
		this.aelGrpMicroLacunaLufLubSeq = aelGrpMicroLacunaLufLubSeq;
	}

	public Short getAelGrpMicroLacunaLufSeqp() {
		return aelGrpMicroLacunaLufSeqp;
	}

	public void setAelGrpMicroLacunaLufSeqp(Short aelGrpMicroLacunaLufSeqp) {
		this.aelGrpMicroLacunaLufSeqp = aelGrpMicroLacunaLufSeqp;
	}

	public Short getAelGrpMicroLacunaSeqp() {
		return aelGrpMicroLacunaSeqp;
	}

	public void setAelGrpMicroLacunaSeqp(Short aelGrpMicroLacunaSeqp) {
		this.aelGrpMicroLacunaSeqp = aelGrpMicroLacunaSeqp;
	}

	public List<AelTxtMicroLacuna> getLista() {
		return lista;
	}

	public void setLista(List<AelTxtMicroLacuna> lista) {
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

	public AelGrpMicroLacuna getAelGrpMicroLacuna() {
		return aelGrpMicroLacuna;
	}

	public void setAelGrpMicroLacuna(AelGrpMicroLacuna aelGrpMicroLacuna) {
		this.aelGrpMicroLacuna = aelGrpMicroLacuna;
	}

	public AelTxtMicroLacuna getAelTxtMicroLacuna() {
		return aelTxtMicroLacuna;
	}

	public void setAelTxtMicroLacuna(AelTxtMicroLacuna aelTxtMicroLacuna) {
		this.aelTxtMicroLacuna = aelTxtMicroLacuna;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelTxtMicroLacunaId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelTxtMicroLacunaId idExcluir) {
		this.idExcluir = idExcluir;
	}
}
