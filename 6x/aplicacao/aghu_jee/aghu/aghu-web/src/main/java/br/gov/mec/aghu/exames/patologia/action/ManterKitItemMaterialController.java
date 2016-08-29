package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelKitItemMatPatologia;
import br.gov.mec.aghu.model.AelKitItemMatPatologiaId;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterKitItemMaterialController extends ActionController {

	private static final long serialVersionUID = 8009240986791499806L;

	private static final String MANTER_KIT_MATERIAL = "manterKitMaterial";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Integer seqAelKitMatPatologia;
	
	private List<AelKitItemMatPatologia> lista;
	
	private AelKitItemMatPatologia itemMatPatologiaSelecionado = new AelKitItemMatPatologia();
	
	//Pai
	private AelKitMatPatologia aelKitMatPatologia;
	
	//Filho
	private AelKitItemMatPatologia aelKitItemMatPatologia;
	
	private Short seqp;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		aelKitMatPatologia = this.examesPatologiaFacade.obterAelKitMatPatologiaPorChavePrimaria(seqAelKitMatPatologia);
		criaObjetoInsercao();
		pesquisar();
	
	}
	
	private void criaObjetoInsercao() {
		aelKitItemMatPatologia = new AelKitItemMatPatologia();
		AelKitItemMatPatologiaId id = new AelKitItemMatPatologiaId();
		id.setLukSeq(aelKitMatPatologia.getSeq());
		aelKitItemMatPatologia.setId(id);
		aelKitItemMatPatologia.setAelKitMatPatologia(aelKitMatPatologia);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelKitItemMatPatologia(aelKitMatPatologia);
	}
	
	public void gravar() {
		try {
			
			if(aelKitItemMatPatologia.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelKitItemMatPatologia(aelKitItemMatPatologia);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_ITEM_MATERIAL_UPDATE_SUCESSO", aelKitItemMatPatologia.getDescricao());

			} else {
				examesPatologiaFacade.inserirAelKitItemMatPatologia(aelKitItemMatPatologia);
				apresentarMsgNegocio( Severity.INFO,"MENSAGEM_KIT_ITEM_MATERIAL_INSERT_SUCESSO", aelKitItemMatPatologia.getDescricao());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void excluir() {
		try {
			examesPatologiaFacade.excluirAelKitItemMatPatologia(aelKitItemMatPatologia.getId());
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_ITEM_MATERIAL_DELETE_SUCESSO",aelKitItemMatPatologia.getDescricao());
			criaObjetoInsercao();
			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_KIT_MATERIAL_DELETE_ERRO",aelKitItemMatPatologia.getDescricao());
				criaObjetoInsercao();
			}
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			criaObjetoInsercao();
		}
	}
	
	public void ativarInativar(final AelKitItemMatPatologia aelKitItemMatPatologia) {
		try {
	
			aelKitItemMatPatologia.setIndSituacao( (DominioSituacao.A.equals(aelKitItemMatPatologia.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelKitItemMatPatologia(aelKitItemMatPatologia);
			
			apresentarMsgNegocio( Severity.INFO, ( DominioSituacao.A.equals(aelKitItemMatPatologia.getIndSituacao()) 
											    	 ? "MENSAGEM_KIT_ITEM_MATERIAL_INATIVADO_SUCESSO" 
													 : "MENSAGEM_KIT_ITEM_MATERIAL_ATIVADO_SUCESSO" 
											       ), aelKitItemMatPatologia.getDescricao());
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		return MANTER_KIT_MATERIAL;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getSeqAelKitMatPatologia() {
		return seqAelKitMatPatologia;
	}

	public void setSeqAelKitMatPatologia(Integer seqAelKitMatPatologia) {
		this.seqAelKitMatPatologia = seqAelKitMatPatologia;
	}

	public AelKitMatPatologia getAelKitMatPatologia() {
		return aelKitMatPatologia;
	}

	public void setAelKitMatPatologia(AelKitMatPatologia aelKitMatPatologia) {
		this.aelKitMatPatologia = aelKitMatPatologia;
	}

	public AelKitItemMatPatologia getAelKitItemMatPatologia() {
		return aelKitItemMatPatologia;
	}

	public void setAelKitItemMatPatologia(
			AelKitItemMatPatologia aelKitItemMatPatologia) {
		this.aelKitItemMatPatologia = aelKitItemMatPatologia;
	}

	public void setLista(List<AelKitItemMatPatologia> lista) {
		this.lista = lista;
	}

	public List<AelKitItemMatPatologia> getLista() {
		return lista;
	}

	public AelKitItemMatPatologia getItemMatPatologiaSelecionado() {
		return itemMatPatologiaSelecionado;
	}

	public void setItemMatPatologiaSelecionado(
			AelKitItemMatPatologia itemMatPatologiaSelecionado) {
		this.itemMatPatologiaSelecionado = itemMatPatologiaSelecionado;
	}

}