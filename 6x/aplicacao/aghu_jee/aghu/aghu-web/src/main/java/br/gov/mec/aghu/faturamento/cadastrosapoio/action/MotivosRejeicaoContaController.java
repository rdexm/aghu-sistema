package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivosRejeicaoContaController extends ActionController {

	private static final long serialVersionUID = 6329924356969769940L;
	
	private static final String PAGE_MOTIVOS_REJEICAO_CONTA_LIST = "faturamento-motivosRejeicaoContaList";
	
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_MOTIVO_REJEICAO_CONTA";
	
	private static final String MENSAGEM_SUCESSO_INCLUSAO = "MENSAGEM_SUCESSO_INCLUSAO_MOTIVO_REJEICAO_CONTA";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	private FatMotivoRejeicaoConta entity;
	
	private Boolean situacao;
	
	private boolean modoEdicao;
	
	/**
	 * Inicializar
	 */
	@PostConstruct
	protected void inicializar() {
		
		begin(conversation);
	}
	
	/**
	 * Valida se a tela foi acessada para edição ou inclusão de um registro
	 */
	public void iniciar() {
		
		if (super.isValidInitMethod()) {
		
			limpar();
		}
	}

	/**
	 * Preenche os valores dos campos conforme caso de inclusão ou alteração
	 */
	private void limpar() {
		
		if (this.modoEdicao) {
			
			this.situacao = this.entity.getSituacao().isAtivo();
			
		} else {
			
			this.situacao = true;
			this.entity = new FatMotivoRejeicaoConta();
		}
	}
	
	/**
	 * Ação do botão Gravar.
	 */
	public void gravar() {
		
		this.entity.setSituacao(DominioSituacao.getInstance(this.situacao));
		
		try {
			
			this.faturamentoApoioFacade.gravarMotivoRejeicaoConta(this.entity);

			if (this.modoEdicao) {
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				
			} else {
			
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO);
			}
			
			limpar();
			
		} catch (ApplicationBusinessException e) {
			
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método responsável pela navegação para a página anterior.
	 * 
	 * @return
	 */
	public String voltar() {
		
		this.entity = null;
		this.situacao = true;
		this.modoEdicao = false;
		
		return PAGE_MOTIVOS_REJEICAO_CONTA_LIST;
	}

	/**
	 * 
	 * GET's and SET's
	 * 
	 */
	public FatMotivoRejeicaoConta getEntity() {
		return entity;
	}

	public void setEntity(FatMotivoRejeicaoConta entity) {
		this.entity = entity;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

}
