package br.gov.mec.aghu.parametrosistema.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioFiltroParametrosPreenchidos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.parametrosistema.vo.AghSistemaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IParametroSistemaFacade extends Serializable {

	List<AghSistemas> pesquisarSistemaPorNome(String nomeSistema);

	AghSistemas buscaAghSistemaPorId(String id);

	void persistirAghSistema(AghSistemas umAghSistema, boolean edicao)
			throws ApplicationBusinessException;
	
	boolean executarParametroSistema(final AghJobDetail job, final String descricao, final Boolean executar, final RapServidores servidor) throws ApplicationBusinessException;


	Long pesquisaParametroSistemaListCount(String sigla, String nome);

	List<AghSistemaVO> pesquisaParametroSistemaList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String sigla,
			String nome);

	void excluirAghSistema(AghSistemas sistema) throws ApplicationBusinessException;

	void excluirAghParametro(Integer seq) throws ApplicationBusinessException;

	AghParametros obterParametroPorId(Integer sequencial);
	
	AghParametros obterParametroPorId(Integer sequencial, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	List<AghModuloAghu> obterTodosModulosAGHU();

	/**
	 * Obtém os módulos existentes no sistema
	 * @author clayton.bras
	 * @return
	 */
	List<AghModuloAghu> pesquisarModulosParametroSistemas();

	/**
	 * Obtem os parâmetros que não possuem nenhum valor associado a nenhum dos campos de valor (Data, numérico nem texto).
	 * @return
	 * @author bruno.mourao
	 * @author clayton.bras
	 * @since 04/11/2011
	 */
	List<AghParametros> obterParametrosSemQualquerValorAssociado();

	/**
	 * Obtem os parâmetros que possuem pelo menos um campo de valor (data, nuḿerico ou texto) preenchido.
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	List<AghParametros> obterParametrosComValorAssociado();

	/**
	 * Realiza o persistir ou alterar da Entidade Parâmetro.
	 * 
	 * @param parametro
	 * @author bruno.mourao
	 *  
	 * @since 04/11/2011
	 */
	void persistirParametro(AghParametros parametro) throws BaseException;

	/**
	 * Obtém a quantidade de paâmetros que não possuem valores associados,
	 * em qualquer dos campos vlrData, vlrNumerico, vlrTexto
	 * @author clayton.bras
	 * @return
	 */
	Long obterNumeroParametrosSemQualquerValorAssociado();

	/**
	 * Retorna a lista de parâmetros encontrados na pesquisa
	 * @author clayton.bras
	 */
	List<AghParametros> pesquisarParametrosPorNomeModuloValorFiltro(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String nome, List<AghModuloAghu> modulos,
			Object valor, DominioFiltroParametrosPreenchidos filtroPreenchidos);

	/**
	 * Retorna a quantidade de parâmetros encontrados na pesquisa
	 * @author clayton.bras
	 */
	Long pesquisarParametrosPorNomeModuloValorFiltroCount(String nome,
			List<AghModuloAghu> modulos, Object valor,
			DominioFiltroParametrosPreenchidos filtroPreenchidos);

	/**
	 * Copia e persiste o conteúdo presente em algum campo de valor padrão
	 * para o respectivo campo valor, de acordo com o tipo
	 * @author clayton.bras
	 * @param seq
	 * @param nomePessoa 
	 *  
	 */
	void copiarValorPadraoCampoValor(Integer seq, String nomePessoa)
			throws ApplicationBusinessException;

	/**
	 * Atualiza o campo valor, diretamente da respectiva coluna, no resultado
	 * da pesquisa
	 * @author clayton.bras
	 * @param nomePessoa 
	 * @param seq, valor
	 *  
	 */
	void atualizarValorParametro(Integer seq, Object valor, String nomePessoa)
			throws ApplicationBusinessException;

	/**
	 * Atualiza o parâmetro a partir da tela de edição
	 * @param parametro
	 * @param nomePessoa 
	 * @return
	 *  
	 */
	AghParametros atualizarParametroSistema(AghParametros parametro,
			String nomePessoa) throws ApplicationBusinessException;

	List<AghParametros> obterTodosParametros();

	/**
	 * Obtém os módulos referentes a um parâemtro
	 * @param seqParametro
	 * @return
	 */
	List<AghModuloAghu> pesquisarModulosPorParametro(Integer seqParametro);

	Long pesquisaParametroListCount(AghParametros parametro);

	List<AghParametroVO> pesquisaParametroList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghParametros parametro);

	void excluirAghSistema(String sigla) throws ApplicationBusinessException;

}