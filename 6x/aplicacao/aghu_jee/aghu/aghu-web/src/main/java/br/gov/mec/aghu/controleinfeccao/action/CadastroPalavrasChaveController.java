package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciPalavraChavePatologiaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author israel.haas
 */

public class CadastroPalavrasChaveController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4765828374012728206L;
	
	private static final String REDIRECIONA_CAD_PATOLOGIAS_INFECCAO = "cadastroPatologiasInfeccao";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	private Integer codigoPatologia;
	private String descricaoPatologia;
	private Boolean situacao;
	
	private List<MciPalavraChavePatologia> listaPalavrasChavePatologia;
	private MciPalavraChavePatologia palavraChavePatologia = new MciPalavraChavePatologia();
	private MciPalavraChavePatologia palavraChaveExclusao = new MciPalavraChavePatologia();
	
	private Boolean mostraModalConfirmacaoExclusao;
	
	//integracao com estoria #41035
	private boolean permConsultaTela = false;
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		this.mostraModalConfirmacaoExclusao = Boolean.FALSE;
		this.situacao = Boolean.TRUE;
		this.palavraChavePatologia.setId(new MciPalavraChavePatologiaId());
		
		this.setListaPalavrasChavePatologia(this.controleInfeccaoFacade.listarPalavraChavePatologia(this.codigoPatologia));
	
	}
	

	public void adicionarPalavraChave() {
		this.palavraChavePatologia.setIndSituacao(this.situacao ? DominioSituacao.A : DominioSituacao.I);
				
		this.controleInfeccaoFacade.inserirPalavraChavePatologia(this.palavraChavePatologia, this.codigoPatologia);
			
		this.setListaPalavrasChavePatologia(this.controleInfeccaoFacade.listarPalavraChavePatologia(this.codigoPatologia));
				
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_PALAVRA_CHAVE",
				this.palavraChavePatologia.getDescricao());
				
		this.cancelarEdicao();
	}
	
	public void alterarPalavraChave() {
		this.palavraChavePatologia.setIndSituacao(this.situacao ? DominioSituacao.A : DominioSituacao.I);
		
		try {
			this.controleInfeccaoFacade.atualizarPalavraChavePatologia(this.palavraChavePatologia);
			
			this.setListaPalavrasChavePatologia(this.controleInfeccaoFacade.listarPalavraChavePatologia(this.codigoPatologia));
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_PALAVRA_CHAVE",
					this.palavraChavePatologia.getDescricao());
			
			this.cancelarEdicao();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir() {
		try {
			this.controleInfeccaoFacade.excluirPalavraChavePatologia(this.palavraChaveExclusao);
			
			this.setListaPalavrasChavePatologia(this.controleInfeccaoFacade.listarPalavraChavePatologia(this.codigoPatologia));
				
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PALAVRA_CHAVE",
					this.palavraChaveExclusao.getDescricao());
			
			this.cancelarEdicao();
			
		} catch (ApplicationBusinessException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editar(MciPalavraChavePatologia item) {
		this.setPalavraChavePatologia(item);
		this.setSituacao(this.palavraChavePatologia.getIndSituacao().isAtivo());
	}
	
	public void cancelarEdicao() {
		this.mostraModalConfirmacaoExclusao = Boolean.FALSE;
		this.palavraChavePatologia = new MciPalavraChavePatologia();
		this.palavraChavePatologia.setId(new MciPalavraChavePatologiaId());
		this.situacao = Boolean.TRUE;
	}
	
	private void limparTudo() {
		this.codigoPatologia = null;
		this.descricaoPatologia = null;
		cancelarEdicao();
	}
	
	public String voltar() {
		limparTudo();
		this.permConsultaTela = false;
		return REDIRECIONA_CAD_PATOLOGIAS_INFECCAO;
	}
	
	// ### GETs e SETs ###

	public Integer getCodigoPatologia() {
		return codigoPatologia;
	}

	public void setCodigoPatologia(Integer codigoPatologia) {
		this.codigoPatologia = codigoPatologia;
	}

	public String getDescricaoPatologia() {
		return descricaoPatologia;
	}

	public void setDescricaoPatologia(String descricaoPatologia) {
		this.descricaoPatologia = descricaoPatologia;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public List<MciPalavraChavePatologia> getListaPalavrasChavePatologia() {
		return listaPalavrasChavePatologia;
	}

	public void setListaPalavrasChavePatologia(
			List<MciPalavraChavePatologia> listaPalavrasChavePatologia) {
		this.listaPalavrasChavePatologia = listaPalavrasChavePatologia;
	}

	public MciPalavraChavePatologia getPalavraChavePatologia() {
		return palavraChavePatologia;
	}

	public void setPalavraChavePatologia(
			MciPalavraChavePatologia palavraChavePatologia) {
		this.palavraChavePatologia = palavraChavePatologia;
	}

	public MciPalavraChavePatologia getPalavraChaveExclusao() {
		return palavraChaveExclusao;
	}

	public void setPalavraChaveExclusao(
			MciPalavraChavePatologia palavraChaveExclusao) {
		this.palavraChaveExclusao = palavraChaveExclusao;
	}

	public Boolean getMostraModalConfirmacaoExclusao() {
		return mostraModalConfirmacaoExclusao;
	}

	public void setMostraModalConfirmacaoExclusao(
			Boolean mostraModalConfirmacaoExclusao) {
		this.mostraModalConfirmacaoExclusao = mostraModalConfirmacaoExclusao;
	}

	public boolean isPermConsultaTela() {
		return permConsultaTela;
	}

	public void setPermConsultaTela(boolean permConsultaTela) {
		this.permConsultaTela = permConsultaTela;
	}
}
