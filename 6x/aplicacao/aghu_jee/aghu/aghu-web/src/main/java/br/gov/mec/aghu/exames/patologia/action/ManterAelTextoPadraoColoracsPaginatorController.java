package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelTextoPadraoColoracs;

public class ManterAelTextoPadraoColoracsPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5128182244602157361L;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private AelTextoPadraoColoracs aelTextoPadraoColoracs = new AelTextoPadraoColoracs();
	
	private AelTextoPadraoColoracs aelTextoPadraoColoracsEdicao = new AelTextoPadraoColoracs();
	
	@Inject @Paginator
	private DynamicDataModel<AelTextoPadraoColoracs> dataModel;
	
	private AelTextoPadraoColoracs selecionado;

	private boolean ativo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void cancelar(){
		aelTextoPadraoColoracs =  new AelTextoPadraoColoracs();
	}

	public void excluir(){
		try {
			examesPatologiaFacade.excluirAelTextoPadraoColoracs(selecionado.getSeq());
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_COLORACS_DELETE_SUCESSO", selecionado.getDescricao());
			limpar();
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void ativarInativar(final AelTextoPadraoColoracs aelTextoPadraoColoracsEdicao) {
		try {
			aelTextoPadraoColoracsEdicao.setIndSituacao( (DominioSituacao.A.equals(aelTextoPadraoColoracsEdicao.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelTextoPadraoColoracs(aelTextoPadraoColoracsEdicao);
			
			this.apresentarMsgNegocio( Severity.INFO,  ( DominioSituacao.A.equals(aelTextoPadraoColoracsEdicao.getIndSituacao()) 
														   	? "MENSAGEM_AEL_TEXTO_PADRAO_COLORACS_INATIVADO_SUCESSO" 
															: "MENSAGEM_AEL_TEXTO_PADRAO_COLORACS_ATIVADO_SUCESSO" 
														), aelTextoPadraoColoracsEdicao.getDescricao());
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String confirmar() {
		try {
			
			if(aelTextoPadraoColoracsEdicao.getSeq() != null){
				examesPatologiaFacade.alterarAelTextoPadraoColoracs(aelTextoPadraoColoracsEdicao);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_COLORACS_UPDATE_SUCESSO", aelTextoPadraoColoracsEdicao.getDescricao());
				cancelar();

			} else {
				examesPatologiaFacade.inserirAelTextoPadraoColoracs(aelTextoPadraoColoracsEdicao);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_TEXTO_PADRAO_COLORACS_INSERT_SUCESSO", aelTextoPadraoColoracsEdicao.getDescricao());
			}
			
			aelTextoPadraoColoracsEdicao = new AelTextoPadraoColoracs();
			dataModel.reiniciarPaginator();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		return null;		
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		ativo = true;
	}

	public void limpar() {
		dataModel.limparPesquisa(); 
		aelTextoPadraoColoracs =  new AelTextoPadraoColoracs();
		aelTextoPadraoColoracsEdicao = new AelTextoPadraoColoracs();
		selecionado = null;
		ativo = false;
	}
	
	@Override
	public Long recuperarCount() {
		return examesPatologiaFacade.pesquisarAelTextoPadraoColoracsCount(aelTextoPadraoColoracs);
	}

	@Override
	public List<AelTextoPadraoColoracs> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return examesPatologiaFacade.pesquisarAelTextoPadraoColoracs(firstResult, maxResults, orderProperty, asc, aelTextoPadraoColoracs);
	}

	public AelTextoPadraoColoracs getAelTextoPadraoColoracs() {
		return aelTextoPadraoColoracs;
	}

	public void setAelTextoPadraoColoracs(
			AelTextoPadraoColoracs aelTextoPadraoColoracs) {
		this.aelTextoPadraoColoracs = aelTextoPadraoColoracs;
	}

	public AelTextoPadraoColoracs getAelTextoPadraoColoracsEdicao() {
		return aelTextoPadraoColoracsEdicao;
	}

	public void setAelTextoPadraoColoracsEdicao(
			AelTextoPadraoColoracs aelTextoPadraoColoracsEdicao) {
		this.aelTextoPadraoColoracsEdicao = aelTextoPadraoColoracsEdicao;
	}

	public DynamicDataModel<AelTextoPadraoColoracs> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelTextoPadraoColoracs> dataModel) {
		this.dataModel = dataModel;
	}

	public AelTextoPadraoColoracs getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelTextoPadraoColoracs selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}