package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class UnidadeMedidaController extends ActionController{

	private static final long serialVersionUID = 1102428187259308449L;

	private static final String UNIDADE_MEDIDA_LIST = "unidadeMedidaList";

	@EJB  
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private ScoUnidadeMedida unMedida;
	
	private boolean visualizar;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 


		if (unMedida != null && unMedida.getCodigo() != null) {
			this.unMedida = this.comprasCadastrosBasicosFacade.obterUnidadeMedida(unMedida.getCodigo());

			if(unMedida == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
		} else {
			unMedida = new ScoUnidadeMedida();
			unMedida.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String salvar() {
		try {
		
			this.comprasCadastrosBasicosFacade.cadastrarUnidadeMedida(this.unMedida);
			
			if (this.unMedida.getVersion()>0 ) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_UN_MEDIDA" , this.unMedida.getCodigo());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_UN_MEDIDA" , this.unMedida.getCodigo());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return this.voltar();
	}
	
	
	public String voltar() {
		this.unMedida = null;
		return UNIDADE_MEDIDA_LIST;
	}

	public ScoUnidadeMedida getUnMedida() {
		return unMedida;
	}

	public void setUnMedida(ScoUnidadeMedida unMedida) {
		this.unMedida = unMedida;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}
}