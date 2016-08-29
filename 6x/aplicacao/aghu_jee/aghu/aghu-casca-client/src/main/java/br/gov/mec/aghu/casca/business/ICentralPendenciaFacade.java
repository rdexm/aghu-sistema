package br.gov.mec.aghu.casca.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ICentralPendenciaFacade extends Serializable {

	/**
	 * Este método retorna uma lista de pendências de acordo com o servidor autenticado no sistema
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	List<PendenciaVO> getListaPendencias() throws ApplicationBusinessException;
	
	/**
	 * Este método exclui logicamente a pendência informada
	 * @param seqCaixaPostal
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void excluirPendencia(Long seqCaixaPostal) throws ApplicationBusinessException;
	
	/**
	 * Este método cria uma pendência do tipo ação, onde a ação será disparada através da url informada em uma nova aba.
	 * @param mensagem texto que aparecerá na central de pendência 
	 * @param url link completo, já com os parâmetros, que irá abrir uma nova aba 
	 * @param descricaoAba texto que irá aparecer na nova aba aberta 
	 * @param listaServidores servidores que poderão ver a pendência
	 * @param enviarEmail informa se deve enviar um email para os servidores com a mesma mensagem da pendência
	 * @throws ApplicationBusinessException
	 */
	void adicionarPendenciaAcao(String mensagem, String url, String descricaoAba, List<RapServidores> listaServidores, Boolean enviarEmail) throws ApplicationBusinessException;

	/**
	 * Este método cria uma pendência do tipo informativa, onde somente uma mensagem será exibida na central de pendência.
	 * @param mensagem texto que aparecerá na central de pendência 
	 * @param listaServidores servidores que poderão ver a pendência
	 * @param enviarEmail informa se deve enviar um email para os servidores com a mesma mensagem da pendência
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	void adicionarPendenciaInformacao(String mensagem, List<RapServidores> listaServidores, Boolean enviarEmail) throws ApplicationBusinessException;
	
	
	/**
	 * Este método cria uma pendência genérica, onde todos os campos da caixa postal podem ser configurados.
	 * @param caixaPostal pode ser um objeto que já existe no banco, ou um novo objeto da caixa postal
	 * @param listaServidores  servidores que poderão ver a pendência
	 * @param enviarEmail informa se deve enviar um email para os servidores com a mesma mensagem da pendência
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	void adicionarPendenciaParaServidores(AghCaixaPostal caixaPostal, List<RapServidores> listaServidores, Boolean enviarEmail) throws ApplicationBusinessException;

	void adicionarPendenciaAcao(String mensagem, String url, String descricaoAba, RapServidores servidor, Boolean enviarEmail) throws ApplicationBusinessException;
	
	Integer buscaTempoRefreshPendencias(RapServidores servidor);

	void excluirPendenciaComUsuarioSelecionado(Long seq, RapServidores usuarioSelecionado);

}