package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface ICadastrosBasicosSiconFacade extends Serializable {

	/**
	 * Pesquisa por todos os tipos de contrato.
	 * 
	 * @param _firstResult
	 * @param _maxResult
	 * @param _orderProperty
	 * @param _asc
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _situacao
	 * @return Listagem de tipos de contrato encontrados no sistema.
	 */
	List<ScoTipoContratoSicon> pesquisarTiposContrato(Integer _firstResult, Integer _maxResult, String _orderProperty,
			boolean _asc, Integer _codigoSicon, String _descricao, DominioSituacao _situacao);

	/**
	 * Pesquisa por todos os itens da tabela {@code ScoCatalogoSicon}. Método
	 * utilizado para paginator.
	 * 
	 * @param _firstResult
	 * @param _maxResult
	 * @param _orderProperty
	 * @param _asc
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _tipoItemContrato
	 * @param _situacao
	 * @return Conjunto de itens de catálogo armazenados no sistema.
	 */
	List<ScoCatalogoSicon> pesquisarItensCatalogo(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc,
			Integer _codigoSicon, String _descricao, DominioTipoItemContrato _tipoItemContrato, DominioSituacao _situacao);

	/**
	 * Retorna a contagem de registros de Tipo de Contratos para o paginator.
	 * 
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _situacao
	 * @return Número total de registros encontrados
	 */
	Long listarTipoContratoCount(Integer _codigoSicon, String _descricao, DominioSituacao _situacao);

	/**
	 * Retorna a contagem de registros de {@code ScoCatalogoSicon} para
	 * utilização no paginator.
	 * 
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _tipoItemContrato
	 * @param _situacao
	 * @return Número total de registros.
	 */
	Long listarItensCatalogoCount(Integer _codigoSicon, String _descricao, DominioTipoItemContrato _tipoItemContrato,
			DominioSituacao _situacao);

	/**
	 * Pesquisa geral de registros na tabela {@code ScoMaterialSicon}. Pode ser
	 * filtrada por parâmetros de entrada.
	 * 
	 * @param _firstResult
	 * @param _maxResult
	 * @param _orderProperty
	 * @param _asc
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Listagem contendo os registros encontrados.
	 */
	List<ScoMaterialSicon> pesquisarMateriaisSicon(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc,
			Integer _codigoSicon, ScoMaterial _material, DominioSituacao _situacao, ScoGrupoMaterial _grupoMaterial);

	/**
	 * Pesquisa e traz apenas um material sicon através dos parâmetros
	 * informados.
	 * 
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Registro de material sicon
	 */
	ScoMaterialSicon pesquisarMaterialSicon(Integer _codigoSicon, ScoMaterial _material, DominioSituacao _situacao,
			ScoGrupoMaterial _grupoMaterial);

	/**
	 * Faz a contagem de registros na tabela {@code ScoMaterialSicon}.
	 * 
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Inteiro indicando o número de registros encontrados.
	 */
	Long listarMateriaisSiconCount(Integer _codigoSicon, ScoMaterial _material, DominioSituacao _situacao,
			ScoGrupoMaterial _grupoMaterial);

	/**
	 * Encontra os tipos de contrato através do código sicon
	 * 
	 * @param _codigoSicon
	 * @return
	 */
	ScoTipoContratoSicon obterTipoContratoSicon(Integer _seqTipoContrato);

	/**
	 * Encontra o registro de {@code ScoCatalogoSicon} através da chave
	 * primária.
	 * 
	 * @param _seqItemCatalogo
	 * @return
	 */
	ScoCatalogoSicon obterCatalogoSicon(Integer _seqItemCatalogo);

	/**
	 * Obtém o registro de {@code ScoMaterialSicon} a partir do
	 * {@code codigoSicon}.
	 * 
	 * @param _seqSicon
	 * @return Registro único encontrado.
	 */
	ScoMaterialSicon obterMaterialSicon(Integer _seqSicon);

	List<ScoMaterialSicon> obterPorMaterial(ScoMaterial material);

	ScoMaterialSicon obterPorCodigoSicon(Integer _codigoSicon);

	/**
	 * Insere um novo registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void inserirTipoContrato(ScoTipoContratoSicon _tipoContrato) throws ApplicationBusinessException;

	/**
	 * Insere um novo registro de material SICON.
	 * 
	 * @param _materialSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void inserirMaterialSicon(ScoMaterialSicon _materialSicon) throws ApplicationBusinessException;

	/**
	 * Insere um novo registro em {@code ScoCatalogoSicon}
	 * 
	 * @param _catalogoSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void inserirItemCatalogo(ScoCatalogoSicon _catalogoSicon) throws ApplicationBusinessException;

	/**
	 * Altera um registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void alterarTipoContrato(ScoTipoContratoSicon _tipoContrato) throws ApplicationBusinessException;

	/**
	 * Altera um registro de material SICON.
	 * 
	 * @param _materialSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void alterarMaterialSicon(ScoMaterialSicon _materialSicon) throws ApplicationBusinessException;

	/**
	 * Atualiza um registro de {@code ScoCatalogoSicon}
	 * 
	 * @param _catalogoSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	void alterarItemCatalogo(ScoCatalogoSicon _catalogoSicon) throws ApplicationBusinessException;

	/**
	 * Remove um registro de tipo de contrato.	
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	public void excluirTipoContrato(Integer seq) throws ApplicationBusinessException ;

	/**
	 * Verifica se um determinado tipo de contrato já tem vínculo com algum
	 * contrato.
	 * 
	 * @param _tipoContrato
	 * @return {@code true} para vínculo existente e {@code false} para caso
	 *         contrário.
	 */
	boolean verificarVinculoContrato(ScoTipoContratoSicon _tipoContrato);

	List<ScoCriterioReajusteContrato> pesquisarCriterioReajusteContrato(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Integer seq, String descricao, DominioSituacao situacao);

	void alterarCriterioReajusteContrato(ScoCriterioReajusteContrato scoCriterioReajusteContrato)
			throws ApplicationBusinessException;

	void inserirCriterioReajusteContrato(ScoCriterioReajusteContrato scoCriterioReajusteContrato)
			throws ApplicationBusinessException;

	/**
	 * Retorna a contagem de reajustes de contratos
	 * 
	 * @param seq
	 * @param descricao
	 * @param situacao
	 * @return Número total de reajustes de contratos
	 */
	Long pesquisarCriterioReajusteContratoCount(Integer seq, String descricao, DominioSituacao situacao);

	void excluirCriterioReajusteContrato(Integer seq) throws BaseException;

	boolean verificarAssociacaoContratoComReajuste(ScoCriterioReajusteContrato scoCriterioReajusteContrato);

	List<ScoMaterial> pesquisarMateriaisPorFiltro(Object _input) throws BaseException;

	List<ScoCatalogoSicon> listarCodigoSiconServicoAtivo(Object pesquisa) throws BaseException;
	
	public Long listarCodigoSiconServicoAtivoCount(Object pesquisa) throws BaseException;

	List<ScoServico> listarServicosAtivos(Object pesquisa) throws BaseException;
	
	public Long listarServicosAtivosCount(Object pesquisa) throws BaseException;

	public List<ScoServico> listarServicosDeGruposAtivos(Object pesquisa, ScoGrupoServico grupoSrv) throws BaseException;
	
	public Long listarServicosDeGruposAtivosCount(Object pesquisa, ScoGrupoServico grupoSrv) throws BaseException;

	List<ScoCatalogoSicon> listarCatalogoSiconServicoAtivo(Object pesquisa);

	public Long listarCatalogoSiconServicoAtivoCount(Object pesquisa);
	
	List<ScoCatalogoSicon> listarCatalogoSiconMaterialAtivo(Object pesquisa);

	public Long listarCatalogoSiconMaterialAtivoCount(Object pesquisa);

	List<ScoServicoSicon> pesquisarServicoSicon(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigoSicon, ScoServico servico, DominioSituacao situacao, ScoGrupoServico grupoServico);

	Long pesquisarServicoSiconCount(Integer codigoSicon, ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico);

	ScoServicoSicon pesquisarServicoSicon(Integer codigoSicon, ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico);

	ScoServicoSicon obterServicoSicon(Integer seq);

	ScoServicoSicon obterServicoCodigoSicon(Integer codSicon);

	List<ScoServicoSicon> obterPorCodigoServico(ScoServico servico);

	void alterarServicoSicon(ScoServicoSicon scoServicoSicon) throws ApplicationBusinessException;

	void inserirServicoSicon(ScoServicoSicon scoServicoSicon) throws ApplicationBusinessException;

	List<ScoTipoContratoSicon> listarTiposContrato(Object pesquisa);

	List<ScoTipoContratoSicon> listarTiposContratoSemAditivoInsItens(Object pesquisa);

	List<ScoTipoContratoSicon> listarTiposContratoComAditivo(Object pesquisa);

	List<ScoTipoContratoSicon> listarTodosTiposContratoAtivos(Object pesquisa);

	List<ScoCriterioReajusteContrato> listarCriteriosReajusteContratoAtivos(Object pesquisa);

	ScoCriterioReajusteContrato obterCriterioReajusteContrato(Integer seq);
	
	List<String> executarCenario1();

	List<String> executarCenario11();

	List<String> executarCenario12();

	List<String> executarCenario2();

	List<String> executarCenario3();

	List<String> executarCenario31();


}