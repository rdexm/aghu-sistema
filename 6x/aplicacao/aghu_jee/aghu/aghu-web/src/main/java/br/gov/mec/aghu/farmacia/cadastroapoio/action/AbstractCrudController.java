package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;

import br.gov.mec.aghu.farmacia.cadastroapoio.action.MedicamentoPaginatorController.RetornoAcaoStrEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public abstract class AbstractCrudController<E> extends ActionController {

	private static final long serialVersionUID = -6801416695725066469L;

	protected abstract String getPaginaInclusao();
	protected abstract String getPaginaConfirmado();
	protected abstract String getPaginaCancelado();
	protected abstract String getPaginaErro();

	private Boolean isUpdate;
	private E entidade;	
	
	protected abstract List<String> obterRazoesExcecao();

	/**
	 * Obtem a entidade original correspondente a entidade informada no argumento.
	 * @param entidade
	 * @return
	 */
	protected abstract E obterEntidadeOriginalViaEntidade(E entidade);
	
	/**
	 * Efetua a inclusao de uma nova entidade(supostamente atravez da ON)
	 * @param entidade
	 * @throws IllegalStateException
	 * @throws BaseException 
	 * @throws MECBaseException
	 */	
	protected abstract void efetuarInclusao(E entidade) throws IllegalStateException, ApplicationBusinessException, BaseException;
	
	/**
	 * Efetua a alteracao de uma nova entidade(supostamente atravez da ON)
	 * @param entidade
	 * @throws IllegalStateException
	 * @throws BaseException 
	 * @throws MECBaseException
	 */	
	protected abstract void efetuarAlteracao(E entidade) throws  IllegalStateException, ApplicationBusinessException, BaseException;
	
	/**
	 * Efetua a remocao da entidade (supostamente atravez da ON)
	 * @param entidade
	 * @throws IllegalStateException
	 * @throws BaseException 
	 * @throws MECBaseException
	 */
	protected abstract void efetuarRemocao(E entidade) throws IllegalStateException, ApplicationBusinessException, BaseException;

	public E getEntidade() {
		
		return this.entidade;
	}

	public void setEntidade(E entidade) {
		
		this.entidade = entidade;
	}
	
	public Boolean getIsUpdate() {
		
		return this.isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		
		this.isUpdate = isUpdate;
	}

	/**
	 * Providencia uma nova entidade para ser trabalhada.
	 */
	@PostConstruct
	public abstract void init();
	
	/**
	 * <p>
	 * Enviar mensagem ao usuario.<br/>
	 * Exemplo:
	 * </p>
	 * <p>
	 * <code>
	 * this.getStatusMessages().addFromResourceBundle(<br/>
	 * Severity.INFO,<br/>
	 * MsgLogStrEnum.SUCESSO_INCLUSAO.toString(),<br/>
	 * params);<br/>
	 * </code>
	 * </p>
	 * @param entidade
	 */
	protected abstract void informarInclusaoSucesso(E entidade);
	
	/**
	 * <p>
	 * Enviar mensagem ao usuario.<br/>
	 * Exemplo:
	 * </p>
	 * <p>
	 * <code>
	 * this.getStatusMessages().addFromResourceBundle(<br/>
	 * Severity.INFO,<br/>
	 * MsgLogStrEnum.SUCESSO_ALTERACAO.toString(),<br/>
	 * params);<br/>
	 * </code>
	 * </p>
	 * @param entidade
	 */
	protected abstract void informarAlteracaoSucesso(E entidade);
	
	/**
	 * Informa o usuario sobre erro ao tratar a entidade
	 * @param entidade
	 * @param e
	 */
	protected void informarExcecao(E entidade, Exception e) {
		
		List<String> razoes = null;
		String razao = null;

		if ((entidade != null) && (e != null)) {
			razoes = this.obterRazoesExcecao();
			if (!razoes.isEmpty()) {
				razao = razoes.get(razoes.size() - 1);
				this.apresentarMsgNegocio(Severity.ERROR, razao);
			}
		}		
	}
	
	/**
	 * <p>
	 * Enviar mensagem ao usuario.<br/>
	 * Exemplo:
	 * </p>
	 * <p>
	 * <code>
	 * this.getStatusMessages().addFromResourceBundle(<br/>
	 * Severity.INFO,<br/>
	 * MsgLogStrEnum.SUCESSO_REMOCAO.toString(),<br/>
	 * params);<br/>
	 * </code>
	 * </p>
	 * @param entidade
	 */
	protected abstract void informarExclusaoSucesso(E entidade);

	/**
	 * <p>
	 * Enviar mensagem ao usuario.<br/>
	 * Exemplo:
	 * </p>
	 * <p>
	 * <code>
	 * this.getStatusMessages().addFromResourceBundle(<br/>
	 * Severity.INFO,<br/>
	 * MsgLogStrEnum.ERRO_REMOCAO.toString(),<br/>
	 * params);<br/>
	 * </code>
	 * </p>
	 * @param entidade
	 */
	protected abstract void informarExclusaoErro(E entidade);
	
	/**
	 * <p>
	 * Encapsulamento da chamada nativa com a classe final como argument<br/>
	 * </p>
	 * <p>
	 * this.reiniciarPaginator(FormaDosagemPaginatorController.class);
	 * </p>
	 * @see #reiniciarPaginator(Class)
	 */
	protected abstract void reiniciarPaginatorController();
	
	/**
	 * <p>
	 * Metodo chamado antes da exclusao, com sucesso, da entidade.
	 * </p>
	 * <p>
	 * Exemplo:<br/>
	 * <code>
	 * this.iniciarPagina();	
	 * </code>
	 * </p>
	 * 
	 */
	protected abstract void procederPreExclusao();

	/**
	 * <p>
	 * Metodo chamado apos a exclusao, com sucesso, da entidade.
	 * </p>
	 * <p>
	 * Exemplo:<br/>
	 * <code>
	 * this.setSeq(null);		
	 * </code>
	 * </p>
	 * 
	 */
	protected abstract void procederPosExclusao();

	/**
	 * Logica de remocao de entidade.<br/>
	 * Captura e trata {@link MECBaseException}<br/>
	 * @see #reiniciarPaginatorController()
	 * @see #obterEntidadeOriginalViaEntidade(Object)
	 * @see #efetuarRemocao(Object)
	 * @see AbstractCrudOn#obterPorChavePrimaria(Object)
	 * @see AbstractCrudOn#remover(Object)
	 * @see #informarExclusaoSucesso(Object)
	 * @see #informarExclusaoErro(Object)
	 * @see #apresentarExcecaoNegocio(MECBaseException)
	 * @see AbstractCrudOn#obterPorChavePrimariaViaEntidade(Object)
	 * @see #procederPosExclusao()
	 */
	public void excluir() {
		
		E entidade = null;
		
		this.procederPreExclusao();
		this.reiniciarPaginatorController();
		entidade = this.obterEntidadeOriginalViaEntidade(this.getEntidade());
		if (entidade != null) {
			try {
				this.efetuarRemocao(entidade);
				this.informarExclusaoSucesso(entidade);
			} catch (ApplicationBusinessException e1) {
				this.apresentarExcecaoNegocio(e1);
			} catch (Exception e2) {
				this.informarExclusaoErro(entidade);
			}
		} else {
			this.informarExclusaoErro(null);
		}
		this.procederPosExclusao();
	}
	
	/**
	 * <p>
	 * Metodo chamado para
	 * </p>
	 * <p>
	 * Exemplo:<br/>
	 * <code>
	 * this.setSeq(null);		
	 * </code>
	 * </p>
	 */
	protected abstract void prepararInclusao();
	
	/**
	 * Prepara inclusao
	 * @return
	 * @see #prepararInclusao()
	 * @see RetornoAcaoStrEnum#INICIAR_INCLUSAO
	 */
	public String iniciarInclusao() {
		
		this.setEntidade(null);
		this.setIsUpdate(Boolean.FALSE);
		this.prepararInclusao();
		
		return getPaginaInclusao();
	}
	
	/**
	 * Se returnar <code>true</code> o metodo {@link #confirmar()} executa uma inclusao, caso <code>false</code> executa uma alteracao
	 * @return
	 */
	protected abstract boolean efetuarInclusao();

	/**
	 * Logica de inclusao E atualizacao<br/>
	 * Captura e trata:<br/>
	 * {@link MECBaseException}<br/>
	 * {@link IllegalStateException}<br/>
	 * @return
	 * @see #reiniciarPaginatorController()
	 * @see AbstractCrudOn#inserir(Object)
	 * @see AbstractCrudOn#atualizarDepreciado(Object)
	 * @see #informarInclusaoSucesso(Object)
	 * @see #informarAlteracaoSucesso(Object)
	 * @see #efetuarInclusao()
	 * @see RetornoAcaoStrEnum#ERRO
	 * @see RetornoAcaoStrEnum#CONFIRMADO
	 */
	public String confirmar() {
		
		String result = getPaginaErro();
		E entidade = null;		
		
		this.reiniciarPaginatorController();
		entidade = this.getEntidade();
		try {			
			result = getPaginaConfirmado();
			if (this.efetuarInclusao()) {
				// Inclusão
				this.efetuarInclusao(entidade);
				this.informarInclusaoSucesso(entidade);
			} else {
				// Alteração
				this.efetuarAlteracao(entidade);
				this.informarAlteracaoSucesso(entidade);
			}
		} catch (IllegalStateException e1) {
			this.informarExcecao(entidade, e1);
			result = getPaginaErro();
		} catch (ApplicationBusinessException e2) {
			this.apresentarExcecaoNegocio(e2);
			result = getPaginaErro();
		} catch (BaseException e3) {
			this.apresentarExcecaoNegocio(e3);
			result = getPaginaErro();
		}  

		return result;
	}
	
	protected abstract void prepararCancelamento();

	/**
	 * 
	 * @return
	 * @see #prepararCancelamento()
	 * @see RetornoAcaoStrEnum#CANCELADO
	 */
	public String cancelar() {
		
		this.prepararCancelamento();
		
		return getPaginaCancelado();
	}
}
