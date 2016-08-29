package br.gov.mec.aghu.internacao.pesquisa.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class DetalhaSolicitacaoInternacaoController extends ActionController {
	
	private static final long serialVersionUID = -1761368947497772045L;
	private static final String REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO = "pesquisaSolicitacaoInternacao";

	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	private AinSolicitacoesInternacao solicitacaoInternacao;

	private EspCrmVO espCrmVO;

	private Integer seqSolicitacao;

	public void inicio() {
		this.solicitacaoInternacao = solicitacaoInternacaoFacade.obterAinSolicitacoesInternacao(seqSolicitacao);
		try {
			this.espCrmVO = solicitacaoInternacaoFacade.obterCrmENomeMedicoPorServidor(
					this.solicitacaoInternacao.getServidor(),
					this.solicitacaoInternacao.getEspecialidade());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String cancelar() {
		return REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO;
	}

	public AinSolicitacoesInternacao getSolicitacaoInternacao() {
		return solicitacaoInternacao;
	}

	public void setSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao) {
		this.solicitacaoInternacao = solicitacaoInternacao;
	}

	public Integer getSeqSolicitacao() {
		return seqSolicitacao;
	}

	public void setSeqSolicitacao(Integer seqSolicitacao) {
		this.seqSolicitacao = seqSolicitacao;
	}

	public EspCrmVO getEspCrmVO() {
		return espCrmVO;
	}

	public void setEspCrmVO(EspCrmVO espCrmVO) {
		this.espCrmVO = espCrmVO;
	}

}
