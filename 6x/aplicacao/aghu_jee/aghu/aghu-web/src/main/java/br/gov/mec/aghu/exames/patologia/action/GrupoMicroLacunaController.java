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
import br.gov.mec.aghu.model.AelTextoPadraoMicroId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoMicroLacunaController extends ActionController {

	private static final long serialVersionUID = 8013463625855893682L;

	private static final String TEXTO_PADRAO_MICROSCOPIA = "textoPadraoMicroscopia";

	private static final String TEXTO_MICRO_LACUNA = "textoMicroLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private Short aelTextoPadraoMicroLubSeq;
	
	private Short aelTextoPadraoMicroSeqp;
	
	private List<AelGrpMicroLacuna> lista;

	//Avo
	private AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro;
	
	//Pai
	private AelTextoPadraoMicro aelTextoPadraoMicro;
	
	//Filho
	private AelGrpMicroLacuna aelGrpMicroLacuna;
	
	private boolean editando;
	
	private AelGrpMicroLacunaId idExcluir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		aelTextoPadraoMicro = examesPatologiaFacade.obterAelTextoPadraoMicro(new AelTextoPadraoMicroId(aelTextoPadraoMicroLubSeq, aelTextoPadraoMicroSeqp));

		if(aelTextoPadraoMicro == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelGrpMicroLacuna = new AelGrpMicroLacuna();
		aelGrpMicroLacuna.setAelTextoPadraoMicros(aelTextoPadraoMicro);
		AelGrpMicroLacunaId id = new AelGrpMicroLacunaId();
		id.setLuvLuuSeq(aelTextoPadraoMicro.getId().getLuuSeq());
		id.setLuvSeqp(aelTextoPadraoMicro.getId().getSeqp());
		aelGrpMicroLacuna.setId(id);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelGrpMicroLacunaPorTextoPadraoMicro(aelTextoPadraoMicroLubSeq, aelTextoPadraoMicroSeqp);
	}
	
	public void gravar() {
		try {
			if(aelGrpMicroLacuna.getId() != null && aelGrpMicroLacuna.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelGrpMicroLacuna(aelGrpMicroLacuna);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_MICRO_LACUNAS_UPDATE_SUCESSO", aelGrpMicroLacuna.getLacuna());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelGrpMicroLacuna(aelGrpMicroLacuna);
				apresentarMsgNegocio( Severity.INFO,"MENSAGEM_AEL_GRP_MICRO_LACUNAS_INSERT_SUCESSO", aelGrpMicroLacuna.getLacuna());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelGrpMicroLacuna aelGrpMicroLacuna) {
		editando = true;
		this.aelGrpMicroLacuna = aelGrpMicroLacuna;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		criaObjetoInsercao();
	}		
	
	public void excluir() {
		try {
			aelGrpMicroLacuna = examesPatologiaFacade.obterAelGrpMicroLacuna(idExcluir);
			idExcluir = null;
			
			if(aelGrpMicroLacuna == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelGrpMicroLacuna(aelGrpMicroLacuna.getId());
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_MICRO_LACUNAS_DELETE_SUCESSO", aelGrpMicroLacuna.getLacuna());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
		criaObjetoInsercao();
		pesquisar();
	}
	
	public void ativarInativar(final AelGrpMicroLacuna elemento) {
		
		try {
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelGrpMicroLacuna(elemento);
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
										    	 ? "MENSAGEM_AEL_GRP_MICRO_LACUNAS_INATIVADO_SUCESSO" 
												 : "MENSAGEM_AEL_GRP_MICRO_LACUNAS_ATIVADO_SUCESSO" 
										       ), elemento.getLacuna());
	
			criaObjetoInsercao();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public String detalharGrupoMicroLacuna(){
		return TEXTO_MICRO_LACUNA;
	}
	
	public String voltar(){
		return TEXTO_PADRAO_MICROSCOPIA;
	}

	public Short getAelTextoPadraoMicroLubSeq() {
		return aelTextoPadraoMicroLubSeq;
	}

	public void setAelTextoPadraoMicroLubSeq(Short aelTextoPadraoMicroLubSeq) {
		this.aelTextoPadraoMicroLubSeq = aelTextoPadraoMicroLubSeq;
	}

	public Short getAelTextoPadraoMicroSeqp() {
		return aelTextoPadraoMicroSeqp;
	}

	public void setAelTextoPadraoMicroSeqp(Short aelTextoPadraoMicroSeqp) {
		this.aelTextoPadraoMicroSeqp = aelTextoPadraoMicroSeqp;
	}

	public List<AelGrpMicroLacuna> getLista() {
		return lista;
	}

	public void setLista(List<AelGrpMicroLacuna> lista) {
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

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelGrpMicroLacunaId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelGrpMicroLacunaId idExcluir) {
		this.idExcluir = idExcluir;
	}
}
