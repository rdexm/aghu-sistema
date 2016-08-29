package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelIndiceBlocoAp;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBloco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class IndiceBlocoLaudoUnicoController extends ActionController  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = -5208209208045120739L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	
	private AelKitIndiceBloco aelKitIndiceBloco;
	
	private AelIndiceBlocoAp indiceBlocoAp;
	
	private String descricaoIndiceBloco;
	
	private List<AelIndiceBlocoAp> listaIndiceBlocoAp;
	
	private Long seqExcluirAelIndiceBlocoAp;
	
	private Boolean editando;
	
	private TelaLaudoUnicoVO tela;
	
	public void inicio(final TelaLaudoUnicoVO tela) {
		this.tela = tela;
		indiceBlocoAp = new AelIndiceBlocoAp();
		listaIndiceBlocoAp = examesPatologiaFacade.listarAelIndiceBlocoApPorAelExameAps(tela.getAelExameAp().getSeq());
		
		editando = Boolean.FALSE;
	}
	
	public void limpar() {
		aelKitIndiceBloco = null;
		indiceBlocoAp = null;
		descricaoIndiceBloco = null;
		seqExcluirAelIndiceBlocoAp = null;		
	}
	
	public void adicionarAelIndiceBlocoAp(){	
		boolean inseriu = false;
		if (aelKitIndiceBloco != null) {
			for (AelKitItemIndiceBloco item : aelKitIndiceBloco.getAelKitItemIndiceBlocoes()) {
				if (DominioSituacao.A.equals(item.getIndSituacao())) {
					descricaoIndiceBloco = item.getDescricao();				
					gravarIndiceBlocoAp(false);
					inseriu = true;
				}
			}
			
			listaIndiceBlocoAp = examesPatologiaFacade.listarAelIndiceBlocoApPorAelExameAps(tela.getAelExameAp().getSeq());
			if (inseriu) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSERT_INDICE_BLOCO_AP");
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_INDICE_BLOCO_AP");
			}
		}
	}

	public void gravarIndiceBlocoAp(boolean mostrarMensagem){
		String descricaoAux = indiceBlocoAp.getIndiceDeBloco();
		
		try {
			
			indiceBlocoAp.setIndiceDeBloco(descricaoIndiceBloco);
			
			final boolean isInsert = indiceBlocoAp.getSeq() == null;
			
			if (indiceBlocoAp.getAelExameAp() == null) {
				indiceBlocoAp.setAelExameAp(tela.getAelExameAp());
			}

			examesPatologiaFacade.persistir(indiceBlocoAp);
			
			editando = Boolean.FALSE;
			listaIndiceBlocoAp = examesPatologiaFacade.listarAelIndiceBlocoApPorAelExameAps(tela.getAelExameAp().getSeq());
			if (mostrarMensagem) {
				if (isInsert) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSERT_INDICE_BLOCO_AP");
				}
				else {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_UPDATE_INDICE_BLOCO_AP");
				}
			}
		}
		catch (BaseException e) {
			indiceBlocoAp.setIndiceDeBloco(descricaoAux);
			apresentarExcecaoNegocio(e);
		}
	
		aelKitIndiceBloco = null;
		descricaoIndiceBloco = null;
		indiceBlocoAp = new AelIndiceBlocoAp();
	}
	
	public void cancelarEdicaoIndiceBloco() {
		editando = Boolean.FALSE;
		descricaoIndiceBloco = null;		
	}
	
	public void excluirIndiceBlocoAp() {	
		AelIndiceBlocoAp aelIndiceBlocoAp = examesPatologiaFacade.obterAelIndiceBlocoApPorChavePrimaria(seqExcluirAelIndiceBlocoAp);

		try {
			examesPatologiaFacade.excluir(aelIndiceBlocoAp);
			listaIndiceBlocoAp = examesPatologiaFacade.listarAelIndiceBlocoApPorAelExameAps(tela.getAelExameAp().getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DELETE_INDICE_BLOCO_AP");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editarKitIndiceBlocoAp(AelIndiceBlocoAp indiceBlocoAp) {
		this.indiceBlocoAp = indiceBlocoAp;
		this.descricaoIndiceBloco = indiceBlocoAp.getIndiceDeBloco();

		editando = Boolean.TRUE;
	}
	
	public List<AelKitIndiceBloco> pesquisarAelKitIndiceBloco(){
		return examesPatologiaFacade.pesquisarAelKitIndiceBloco(null, null, DominioSituacao.A); 
	}

	public Long pesquisarAelKitIndiceBlocoCount(final Object filtro){
		return examesPatologiaFacade.pesquisarAelKitIndiceBlocoCount(null, (String) filtro, DominioSituacao.A); 
	}

	public AelKitIndiceBloco getAelKitIndiceBloco() {
		return aelKitIndiceBloco;
	}

	public void setAelKitIndiceBloco(AelKitIndiceBloco aelKitIndiceBloco) {
		this.aelKitIndiceBloco = aelKitIndiceBloco;
	}

	public String getDescricaoIndiceBloco() {
		return descricaoIndiceBloco;
	}

	public void setDescricaoIndiceBloco(String descricaoIndiceBloco) {
		this.descricaoIndiceBloco = descricaoIndiceBloco;
	}

	public AelIndiceBlocoAp getIndiceBlocoAp() {
		return indiceBlocoAp;
	}

	public void setIndiceBlocoAp(AelIndiceBlocoAp indiceBlocoAp) {
		this.indiceBlocoAp = indiceBlocoAp;
	}

	public List<AelIndiceBlocoAp> getListaIndiceBlocoAp() {
		return listaIndiceBlocoAp;
	}

	public void setListaIndiceBlocoAp(List<AelIndiceBlocoAp> listaIndiceBlocoAp) {
		this.listaIndiceBlocoAp = listaIndiceBlocoAp;
	}

	public Long getSeqExcluirAelIndiceBlocoAp() {
		return seqExcluirAelIndiceBlocoAp;
	}

	public void setSeqExcluirAelIndiceBlocoAp(Long seqExcluirAelIndiceBlocoAp) {
		this.seqExcluirAelIndiceBlocoAp = seqExcluirAelIndiceBlocoAp;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public TelaLaudoUnicoVO getTela() {
		return tela;
	}

	public void setTela(TelaLaudoUnicoVO tela) {
		this.tela = tela;
	}

	
}