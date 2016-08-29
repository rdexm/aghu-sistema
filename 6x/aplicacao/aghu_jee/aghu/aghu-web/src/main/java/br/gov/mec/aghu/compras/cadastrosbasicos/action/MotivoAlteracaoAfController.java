package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivoAlteracaoAfController extends ActionController{


	private static final long serialVersionUID = -8915932045976673620L;

	private static final String MOTIVO_ALTERACAO_AF_LIST = "motivoAlteracaoAfList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private Short codigo;
	
	private ScoMotivoAlteracaoAf scoMotivoAlteracaoAf;
	
	private Boolean edicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 


		if(this.getCodigo() == null){
			this.setScoMotivoAlteracaoAf(new ScoMotivoAlteracaoAf());
			
			this.getScoMotivoAlteracaoAf().setSituacao(DominioSituacao.A);
			this.setEdicao(false);
			
		} else{
			scoMotivoAlteracaoAf = comprasCadastrosBasicosFacade.obterScoMotivoAlteracaoAf(this.getCodigo());

			if(scoMotivoAlteracaoAf == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			this.setEdicao(true);
		}
		return null;
	
	}
		
	public String gravar() {
		
		try {
			this.comprasCadastrosBasicosFacade.persistirScoMotivoAlteracaoAf(this.getScoMotivoAlteracaoAf());

			if (!edicao) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MOTIVO_ALTERACAO", this.scoMotivoAlteracaoAf.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MOTIVO_ALTERACAO", this.scoMotivoAlteracaoAf.getDescricao());
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		return MOTIVO_ALTERACAO_AF_LIST;
	}
	
	public ScoMotivoAlteracaoAf getScoMotivoAlteracaoAf() {
		return scoMotivoAlteracaoAf;
	}

	public void setScoMotivoAlteracaoAf(ScoMotivoAlteracaoAf scoMotivoAlteracaoAf) {
		this.scoMotivoAlteracaoAf = scoMotivoAlteracaoAf;
	}
	
	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}
	
	public void setCodigo(Short codigo){
		this.codigo = codigo;
	}
	
	public Short getCodigo(){
		return this.codigo;
	}
	
}
