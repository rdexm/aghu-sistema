package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
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
import br.gov.mec.aghu.sicon.dao.ScoCatalogoSiconDAO;
import br.gov.mec.aghu.sicon.dao.ScoCriterioReajusteContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoMaterialSiconDAO;
import br.gov.mec.aghu.sicon.dao.ScoServicoSiconDAO;
import br.gov.mec.aghu.sicon.dao.ScoTipoContratoSiconDAO;

/**
 * Classe para acesso às funcionalidades do sub módulo de cadastros básicos do
 * módulo "SICON"
 * 
 * @author ptneto
 * 
 */
@Modulo(ModuloEnum.SICON)
@Stateless
public class CadastrosBasicosSiconFacade extends BaseFacade implements ICadastrosBasicosSiconFacade {

	@EJB
	private ManterServicoSiconON manterServicoSiconON;

	@EJB
	private ManterTipoContratoON manterTipoContratoON;

	@EJB
	private CriterioReajusteContratoON criterioReajusteContratoON;

	@EJB
	private ManterMaterialSiconON manterMaterialSiconON;

	@EJB
	private ManterCatalogoSiconON manterCatalogoSiconON;
	
	@EJB
	private AnalisePerformaceON analisePerformaceON;

	@Inject
	private ScoTipoContratoSiconDAO scoTipoContratoSiconDAO;

	@Inject
	private ScoCriterioReajusteContratoDAO scoCriterioReajusteContratoDAO;

	@Inject
	private ScoCatalogoSiconDAO scoCatalogoSiconDAO;

	@Inject
	private ScoServicoSiconDAO scoServicoSiconDAO;

	@Inject
	private ScoMaterialSiconDAO scoMaterialSiconDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7293998022385468716L;

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
	@Override
	public List<ScoTipoContratoSicon> pesquisarTiposContrato(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc,
			Integer _codigoSicon, String _descricao, DominioSituacao _situacao) {

		return this.getManterTipoContratoON().pesquisarTiposContrato(_firstResult, _maxResult, _orderProperty, _asc, _codigoSicon,
				_descricao, _situacao);
	}

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
	@Override
	public List<ScoCatalogoSicon> pesquisarItensCatalogo(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc,
			Integer _codigoSicon, String _descricao, DominioTipoItemContrato _tipoItemContrato, DominioSituacao _situacao) {

		return this.getScoCatalogoSiconDAO().pesquisar(_firstResult, _maxResult, _orderProperty, _asc, _codigoSicon, _descricao,
				_tipoItemContrato, _situacao);
	}

	/**
	 * Retorna a contagem de registros de Tipo de Contratos para o paginator.
	 * 
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _situacao
	 * @return Número total de registros encontrados
	 */
	@Override
	public Long listarTipoContratoCount(Integer _codigoSicon, String _descricao, DominioSituacao _situacao) {
		return this.getScoTipoContratoSiconDAO().listarTiposContratoCount(_codigoSicon, _descricao, _situacao);
	}

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
	@Override
	public Long listarItensCatalogoCount(Integer _codigoSicon, String _descricao, DominioTipoItemContrato _tipoItemContrato,
			DominioSituacao _situacao) {
		return this.getScoCatalogoSiconDAO().pesquisarCatalogoSiconCount(_codigoSicon, _descricao, _tipoItemContrato, _situacao);
	}

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
	@Override
	public List<ScoMaterialSicon> pesquisarMateriaisSicon(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc,
			Integer _codigoSicon, ScoMaterial _material, DominioSituacao _situacao, ScoGrupoMaterial _grupoMaterial) {

		return this.getScoMaterialSiconDAO().pesquisarMateriaisSicon(_firstResult, _maxResult, _orderProperty, _asc, _codigoSicon,
				_material, _situacao, _grupoMaterial);
	}

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
	@Override
	public ScoMaterialSicon pesquisarMaterialSicon(Integer _codigoSicon, ScoMaterial _material, DominioSituacao _situacao,
			ScoGrupoMaterial _grupoMaterial) {

		return this.getScoMaterialSiconDAO().pesquisarMaterialSicon(_codigoSicon, _material, _grupoMaterial, _situacao);
	}

	/**
	 * Faz a contagem de registros na tabela {@code ScoMaterialSicon}.
	 * 
	 * @param _codigoSicon
	 * @param _material
	 * @param _situacao
	 * @param _grupoMaterial
	 * @return Inteiro indicando o número de registros encontrados.
	 */
	@Override
	public Long listarMateriaisSiconCount(Integer _codigoSicon, ScoMaterial _material, DominioSituacao _situacao,
			ScoGrupoMaterial _grupoMaterial) {

		return this.getScoMaterialSiconDAO().listarMaterialSiconCount(_codigoSicon, _material, _situacao, _grupoMaterial);
	}

	/**
	 * Encontra os tipos de contrato através do código sicon
	 * 
	 * @param _codigoSicon
	 * @return
	 */
	@Override
	public ScoTipoContratoSicon obterTipoContratoSicon(Integer _seqTipoContrato) {
		// return this.getScoTipoContratoSiconDAO().obterPorCodigoSicon(
		// _codigoSicon);
		return this.getScoTipoContratoSiconDAO().obterPorChavePrimaria(_seqTipoContrato);
	}

	/**
	 * Encontra o registro de {@code ScoCatalogoSicon} através da chave
	 * primária.
	 * 
	 * @param _seqItemCatalogo
	 * @return
	 */
	@Override
	public ScoCatalogoSicon obterCatalogoSicon(Integer _seqItemCatalogo) {
		return this.getScoCatalogoSiconDAO().obterPorChavePrimaria(_seqItemCatalogo);
	}

	/**
	 * Obtém o registro de {@code ScoMaterialSicon} a partir do
	 * {@code codigoSicon}.
	 * 
	 * @param _seqSicon
	 * @return Registro único encontrado.
	 */
	@Override
	public ScoMaterialSicon obterMaterialSicon(Integer _seqSicon) {
		Enum[] fetch = { ScoMaterialSicon.Fields.MATERIAL };
		return this.getScoMaterialSiconDAO().obterPorChavePrimaria(_seqSicon, null, fetch);
	}

	@Override
	public List<ScoMaterialSicon> obterPorMaterial(ScoMaterial material) {
		return this.getScoMaterialSiconDAO().obterPorMaterial(material);
	}

	@Override
	public ScoMaterialSicon obterPorCodigoSicon(Integer _codigoSicon) {
		return this.getScoMaterialSiconDAO().obterPorCodigoSicon(_codigoSicon);
	}

	/**
	 * Insere um novo registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void inserirTipoContrato(ScoTipoContratoSicon _tipoContrato) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterTipoContratoON().inserir(_tipoContrato);
	}

	/**
	 * Insere um novo registro de material SICON.
	 * 
	 * @param _materialSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void inserirMaterialSicon(ScoMaterialSicon _materialSicon) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterMaterialSiconON().inserir(_materialSicon);
	}

	/**
	 * Insere um novo registro em {@code ScoCatalogoSicon}
	 * 
	 * @param _catalogoSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void inserirItemCatalogo(ScoCatalogoSicon _catalogoSicon) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterCatalogoSiconON().inserir(_catalogoSicon);
	}

	/**
	 * Altera um registro de tipo de contrato.
	 * 
	 * @param _tipoContrato
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void alterarTipoContrato(ScoTipoContratoSicon _tipoContrato) throws ApplicationBusinessException, ApplicationBusinessException {
		manterTipoContratoON.alterar(_tipoContrato);
	}

	/**
	 * Altera um registro de material SICON.
	 * 
	 * @param _materialSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void alterarMaterialSicon(ScoMaterialSicon _materialSicon) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterMaterialSiconON().alterar(_materialSicon);
	}

	/**
	 * Atualiza um registro de {@code ScoCatalogoSicon}
	 * 
	 * @param _catalogoSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void alterarItemCatalogo(ScoCatalogoSicon _catalogoSicon) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterCatalogoSiconON().alterar(_catalogoSicon);
	}

	/**
	 * Remove um registro de tipo de contrato.
	 * 
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void excluirTipoContrato(Integer seq) throws ApplicationBusinessException {
		manterTipoContratoON.remover(seq);
	}

	/**
	 * Verifica se um determinado tipo de contrato já tem vínculo com algum
	 * contrato.
	 * 
	 * @param _tipoContrato
	 * @return {@code true} para vínculo existente e {@code false} para caso
	 *         contrário.
	 */
	@Override
	public boolean verificarVinculoContrato(ScoTipoContratoSicon _tipoContrato) {

		return this.getManterTipoContratoON().verificarAssociacaoContrato(_tipoContrato);
	}

	@Override
	public List<ScoCriterioReajusteContrato> pesquisarCriterioReajusteContrato(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Integer seq, String descricao, DominioSituacao situacao) {
		return this.getCriterioReajusteContratoON().pesquisarCriterioReajusteContrato(firstResult, maxResult, orderProperty, asc, seq,
				descricao, situacao);
	}

	@Override
	public void alterarCriterioReajusteContrato(ScoCriterioReajusteContrato scoCriterioReajusteContrato)
			throws ApplicationBusinessException {
		this.getCriterioReajusteContratoON().alterar(scoCriterioReajusteContrato);
	}

	@Override
	public void inserirCriterioReajusteContrato(ScoCriterioReajusteContrato scoCriterioReajusteContrato)
			throws ApplicationBusinessException, ApplicationBusinessException {

		this.getCriterioReajusteContratoON().inserir(scoCriterioReajusteContrato);
	}

	/**
	 * Retorna a contagem de reajustes de contratos
	 * 
	 * @param seq
	 * @param descricao
	 * @param situacao
	 * @return Número total de reajustes de contratos
	 */
	@Override
	public Long pesquisarCriterioReajusteContratoCount(Integer seq, String descricao, DominioSituacao situacao) {

		return this.getCriterioReajusteContratoON().pesquisarCriterioReajusteContratoCount(seq, descricao, situacao);
	}

	@Override
	public void excluirCriterioReajusteContrato(Integer seq) throws BaseException {
		criterioReajusteContratoON.excluirCriterioReajusteContrato(seq);
	}

	@Override
	public boolean verificarAssociacaoContratoComReajuste(ScoCriterioReajusteContrato scoCriterioReajusteContrato) {
		return this.getCriterioReajusteContratoON().verificarAssociacaoContratoComReajuste(scoCriterioReajusteContrato);
	}

	@Override
	public List<ScoMaterial> pesquisarMateriaisPorFiltro(Object _input) throws BaseException {

		return this.getManterMaterialSiconON().pesquisarMateriaisPorFiltro(_input);

	}

	@Override
	public List<ScoCatalogoSicon> listarCodigoSiconServicoAtivo(Object pesquisa) throws BaseException {

		return manterMaterialSiconON.listarCodigoSiconServicoAtivo(pesquisa);
	}

	@Override
	public Long listarCodigoSiconServicoAtivoCount(Object pesquisa) throws BaseException {

		return manterMaterialSiconON.listarCodigoSiconServicoAtivoCount(pesquisa);
	}

	@Override
	public List<ScoServico> listarServicosAtivos(Object pesquisa) throws BaseException {
		List<ScoServico> servicos = manterServicoSiconON.listarServicosAtivos(pesquisa);
		return servicos;
	}

	@Override
	public Long listarServicosAtivosCount(Object pesquisa) throws BaseException {
		return manterServicoSiconON.listarServicosAtivosCount(pesquisa);

	}

	@Override
	public List<ScoServico> listarServicosDeGruposAtivos(Object pesquisa, ScoGrupoServico grupoSrv) throws BaseException {
		return manterServicoSiconON.listarServicosDeGruposAtivos(pesquisa, grupoSrv);
	}

	@Override
	public Long listarServicosDeGruposAtivosCount(Object pesquisa, ScoGrupoServico grupoSrv) throws BaseException {
		return manterServicoSiconON.listarServicosDeGruposAtivosCount(pesquisa, grupoSrv);
	}

	@Override
	public List<ScoCatalogoSicon> listarCatalogoSiconServicoAtivo(Object pesquisa) {
		return scoCatalogoSiconDAO.listarCatalogoSiconServicoAtivo(pesquisa);
	}

	@Override
	public Long listarCatalogoSiconServicoAtivoCount(Object pesquisa) {
		return scoCatalogoSiconDAO.listarCatalogoSiconServicoAtivoCount(pesquisa);
	}

	@Override
	public List<ScoCatalogoSicon> listarCatalogoSiconMaterialAtivo(Object pesquisa) {
		List<ScoCatalogoSicon> CatalogoSiconMaterial = scoCatalogoSiconDAO.listarCatalogoSiconMaterialAtivo(pesquisa);
		return CatalogoSiconMaterial;
	}

	@Override
	public Long listarCatalogoSiconMaterialAtivoCount(Object pesquisa) {
		return scoCatalogoSiconDAO.listarCatalogoSiconMaterialAtivoCount(pesquisa);
	}

	@Override
	public List<ScoServicoSicon> pesquisarServicoSicon(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigoSicon, ScoServico servico, DominioSituacao situacao, ScoGrupoServico grupoServico) {
		return this.getManterServicoSiconON().pesquisarServicoSicon(firstResult, maxResult, orderProperty, asc, codigoSicon, servico,
				situacao, grupoServico);
	}

	@Override
	public Long pesquisarServicoSiconCount(Integer codigoSicon, ScoServico servico, DominioSituacao situacao, ScoGrupoServico grupoServico) {

		return this.getManterServicoSiconON().pesquisarServicoSiconCount(codigoSicon, servico, situacao, grupoServico);
	}

	@Override
	public ScoServicoSicon pesquisarServicoSicon(Integer codigoSicon, ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {

		return this.scoServicoSiconDAO.pesquisarServicoSicon(codigoSicon, servico, situacao, grupoServico);
	}

	@Override
	public ScoServicoSicon obterServicoSicon(Integer seq) {
		Enum[] fetch = { ScoServicoSicon.Fields.SERVICO };
		return this.getScoServicoSiconDAO().obterPorChavePrimaria(seq, null, fetch);
	}

	@Override
	public ScoServicoSicon obterServicoCodigoSicon(Integer codSicon) {
		return this.getScoServicoSiconDAO().obterServicoCodigoSicon(codSicon);
	}

	@Override
	public List<ScoServicoSicon> obterPorCodigoServico(ScoServico servico) {
		return this.getScoServicoSiconDAO().obterPorCodigoServico(servico);
	}

	@Override
	public void alterarServicoSicon(ScoServicoSicon scoServicoSicon) throws ApplicationBusinessException {
		this.getManterServicoSiconON().alterar(scoServicoSicon);
	}

	@Override
	public void inserirServicoSicon(ScoServicoSicon scoServicoSicon) throws ApplicationBusinessException, ApplicationBusinessException {
		this.getManterServicoSiconON().inserir(scoServicoSicon);
	}

	@Override
	public List<ScoTipoContratoSicon> listarTiposContrato(Object pesquisa) {
		return getScoTipoContratoSiconDAO().listarTiposContrato(pesquisa);
	}

	@Override
	public List<ScoTipoContratoSicon> listarTiposContratoSemAditivoInsItens(Object pesquisa) {
		return getScoTipoContratoSiconDAO().listarTiposContratoSemAditivoInsItems(pesquisa);
	}

	@Override
	public List<ScoTipoContratoSicon> listarTiposContratoComAditivo(Object pesquisa) {
		return getScoTipoContratoSiconDAO().listarTiposContratoAtivosComAditivo(pesquisa);
	}

	@Override
	public List<ScoTipoContratoSicon> listarTodosTiposContratoAtivos(Object pesquisa) {
		return getScoTipoContratoSiconDAO().listarTodosTiposContratoAtivos(pesquisa);
	}

	@Override
	public List<ScoCriterioReajusteContrato> listarCriteriosReajusteContratoAtivos(Object pesquisa) {
		return getScoCriterioReajusteContratoDAO().listarCriteriosReajusteContratoAtivos(pesquisa);
	}

	@Override
	public ScoCriterioReajusteContrato obterCriterioReajusteContrato(Integer seq) {
		return this.getScoCriterioReajusteContratoDAO().obterPorChavePrimaria(seq);
	}

	/**
	 * Instancia um novo objeto de {@code ManterTipoContratoON}.
	 * 
	 * @return
	 */
	protected ManterTipoContratoON getManterTipoContratoON() {
		return manterTipoContratoON;
	}

	/**
	 * Instancia um novo objeto de {@code ScoTipoContratoSiconDAO}
	 * 
	 * @return
	 */
	protected ScoTipoContratoSiconDAO getScoTipoContratoSiconDAO() {
		return scoTipoContratoSiconDAO;
	}

	/**
	 * Instancia um novo objeto de {@code ScoMaterialSiconDAO}.
	 * 
	 * @return
	 */
	protected ScoMaterialSiconDAO getScoMaterialSiconDAO() {
		return scoMaterialSiconDAO;
	}

	protected ManterMaterialSiconON getManterMaterialSiconON() {
		return manterMaterialSiconON;
	}

	/**
	 * Instancia um novo objeto de {@code ScoCriterioReajusteContratoON}
	 * 
	 * @return
	 */
	private CriterioReajusteContratoON getCriterioReajusteContratoON() {
		return criterioReajusteContratoON;
	}

	protected ScoCatalogoSiconDAO getScoCatalogoSiconDAO() {
		return scoCatalogoSiconDAO;
	}

	protected ScoServicoSiconDAO getScoServicoSiconDAO() {
		return scoServicoSiconDAO;
	}

	private ManterServicoSiconON getManterServicoSiconON() {
		return manterServicoSiconON;
	}

	protected ManterCatalogoSiconON getManterCatalogoSiconON() {
		return manterCatalogoSiconON;
	}

	protected ScoCriterioReajusteContratoDAO getScoCriterioReajusteContratoDAO() {
		return scoCriterioReajusteContratoDAO;
	}
	
	protected AnalisePerformaceON getAnalisePerformaceON() {
		return this.analisePerformaceON;
	}

	public List<String> executarCenario1(){
		return this.getAnalisePerformaceON().executarCenario1();
	}

	public List<String> executarCenario11(){
		return this.getAnalisePerformaceON().executarCenario11();
	}

	public List<String> executarCenario12(){
		return this.getAnalisePerformaceON().executarCenario12();
	}

	public List<String> executarCenario2(){
		return this.getAnalisePerformaceON().executarCenario2();
	}

	public List<String> executarCenario3(){
		return this.getAnalisePerformaceON().executarCenario3();
	}

	public List<String> executarCenario31(){
		return this.getAnalisePerformaceON().executarCenario31();
	}

}
