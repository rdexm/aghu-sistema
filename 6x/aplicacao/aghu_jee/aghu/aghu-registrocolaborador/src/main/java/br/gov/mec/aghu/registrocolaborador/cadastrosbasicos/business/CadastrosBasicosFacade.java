package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapTipoAfastamento;
import br.gov.mec.aghu.model.RapTipoAtuacao;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.dao.RapCargosDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapConselhosProfissionaisDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapGrupoFuncionalDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapInstituicaoQualificadoraDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapOcupacaoCargoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapRamalTelefonicoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoAfastamentoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoAtuacaoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoInformacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoQualificacaoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapVinculosDAO;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;

/**
 * Classe de fachada que disponibiliza uma interface para as funcionalidades do
 * sub módulo Cadastros básicos do módulo Registro do colaborador.
 */


@Modulo(ModuloEnum.REGISTRO_COLABORADOR)
@Stateless
public class CadastrosBasicosFacade extends BaseFacade implements ICadastrosBasicosFacade {

	
	@EJB
	private GraduacaoON graduacaoON;
	
	@EJB
	private TipoInformacoesON tipoInformacoesON;
	
	@EJB
	private InstituicaoQualificadoraON instituicaoQualificadoraON;
	
	@EJB
	private GrupoFuncionalON grupoFuncionalON;
	
	@EJB
	private TipoAfastamentoON tipoAfastamentoON;
	
	@EJB
	private OcupacaoCargoON ocupacaoCargoON;
	
	@EJB
	private ConselhoProfissionalON conselhoProfissionalON;
	
	@EJB
	private RamalTelefonicoON ramalTelefonicoON;
	
	@EJB
	private CargoON cargoON;
	
	@EJB
	private VinculoON vinculoON;
	
	@Inject
	private RapTipoAtuacaoDAO rapTipoAtuacaoDAO;
	
	@Inject
	private RapRamalTelefonicoDAO rapRamalTelefonicoDAO;
	
	@Inject
	private RapOcupacaoCargoDAO rapOcupacaoCargoDAO;
	
	@Inject
	private RapConselhosProfissionaisDAO rapConselhosProfissionaisDAO;
	
	@Inject
	private RapTipoInformacoesDAO rapTipoInformacoesDAO;
	
	@Inject
	private RapVinculosDAO rapVinculosDAO;
	
	@Inject
	private RapGrupoFuncionalDAO rapGrupoFuncionalDAO;
	
	@Inject
	private RapCargosDAO rapCargosDAO;
	
	@Inject
	private RapTipoAfastamentoDAO  RapTipoAfastamentoDAO;
	
	@Inject
	private RapInstituicaoQualificadoraDAO rapInstituicaoQualificadoraDAO;
	
	@Inject
	private RapTipoQualificacaoDAO rapTipoQualificacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4780570893157124785L;

	// Vinculo
	@Override
	public RapVinculos obterVinculo(Short codigoVinculo) throws ApplicationBusinessException {
		return getVinculosDAO().obterVinculo(codigoVinculo);
	}
	
	@Override
	public Long pesquisarVinculosCount(Short codigo, String descricao,
			DominioSituacao indSituacao) {
		return getVinculosDAO().pesquisarVinculosCount(codigo, descricao, indSituacao);
	}
	
	@Override
	public List<RapVinculos> pesquisarVinculos(Short codigo, String descricao,
			DominioSituacao indSituacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getVinculosDAO().pesquisarVinculos(codigo, descricao, indSituacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public void excluirVinculo(Short codigoVinculo) throws ApplicationBusinessException {
		getVinculoON().excluirVinculo(codigoVinculo);
	}
	
	/**
	 * PopulaTabela tabela RAP_COD_STARH_LIVRES, caso esteja vazia
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void populaTabela() throws ApplicationBusinessException{
		getVinculoON().populaTabela();
	}
	
	/**
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public RapVinculos buscarVinculos(Short codigoVinculo, boolean somenteAtivo)
			throws ApplicationBusinessException {
		return getVinculoON().buscarVinculos(codigoVinculo, somenteAtivo);
	}
	
	/**
	 * Retorna vínculos de acrodo com o código ou descrição informados por ordem
	 * de codigo
	 * 
	 * @param vinculo
	 *            ou descricao
	 * @return vínculos encontrados lista vazia se não encontrado
	 */
	@Override
	public List<RapVinculos> pesquisarVinculoPorCodigoDescricao(Object vinculo,
			boolean somenteAtivo) throws ApplicationBusinessException {
		return getVinculoON().pesquisarVinculoPorCodigoDescricao(vinculo, somenteAtivo);
	}
	
	// Tipo Informações
	@Override
	public RapTipoInformacoes obterTipoInformacoes(Integer TipoInformacoesCodigo) throws ApplicationBusinessException {
		return getTipoInformacoesON().obterTipoInformacoes(TipoInformacoesCodigo);
	}
	
	@Override
	public void persistirTipoInformacoes(RapTipoInformacoes rapTipoInformacoes) throws ApplicationBusinessException {
		getTipoInformacoesON().persistirTipoInformacoes(rapTipoInformacoes);
	}
	
	@Override
	public List<RapTipoInformacoes> pesquisarTipoInformacoesSuggestion(Object objPesquisa) {
		return getTipoInformacoesDAO().pesquisarTipoInformacoesSuggestion(objPesquisa);
	}	
	
	@Override
	public List<Integer> pesquisarTipoInformacoesPorDescricao(String descricao) {
		return getTipoInformacoesDAO().pesquisarTipoInformacoesPorDescricao(descricao);
	}
	
	@Override
	public Long pesquisarTipoInformacoesSuggestionCount(Object objPesquisa) {
		return getTipoInformacoesDAO().pesquisarTipoInformacoesSuggestionCount(objPesquisa);
	}
	
	@Override
	public Long obterTipoInformacoesCount(Integer codigo, String descricao) {
		return getTipoInformacoesDAO().obterTipoInformacoesCount(codigo, descricao);
	}
	
	@Override
	public List<RapTipoInformacoes> pesquisarTipoInformacoes(
			Integer firstResult, Integer maxResults, Integer codigo,
			String descricao) {
		return getTipoInformacoesDAO().pesquisarTipoInformacoes(firstResult, maxResults, codigo, descricao);
	}
	
	@Override
	public void excluirTipoInformacoes(Integer tipoInformacoesCodigo) throws ApplicationBusinessException {
		getTipoInformacoesON().excluirTipoInformacoes(tipoInformacoesCodigo);
	}

	// Ocupação Cargo
	
	/**
	 * Retorna instância pelo codigo da ocupação.
	 * 
	 * @param codigoOcupacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public RapOcupacaoCargo obterOcupacaoCargoPorCodigo(Integer codigoOcupacao,
			boolean somenteAtivo) throws ApplicationBusinessException {
		return getOcupacaoCargoON().obterOcupacaoCargoPorCodigo(codigoOcupacao, somenteAtivo);
	}	
	
	@Override
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(Integer codigo,
			String carCodigo, String descricao, DominioSituacao situacao,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getRapOcupacaoCargoDAO().pesquisarOcupacaoCargo(codigo, carCodigo, descricao, situacao, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(String parametroPesquisa, DominioSituacao situacao) {
		return getRapOcupacaoCargoDAO().pesquisarOcupacaoCargo(parametroPesquisa, situacao);
	}
	
	@Override
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(Integer codigo, String descricao, String carCodigo, DominioSituacao situacao){
		return getRapOcupacaoCargoDAO().pesquisarOcupacaoCargo(codigo, descricao, carCodigo, situacao);
	}

	
	/**
	 * Retorna instância pelo id.
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public RapOcupacaoCargo obterOcupacaoCargo(RapOcupacoesCargoId id) throws ApplicationBusinessException {
		return getRapOcupacaoCargoDAO().obterOcupacaoCargo(id);
	}
	
	@Override
	public void alterar(RapOcupacaoCargo ocupacaoCargo) {
		getRapOcupacaoCargoDAO().merge(ocupacaoCargo);
	}
	
	@Override
	public void remover(RapOcupacoesCargoId id) throws ApplicationBusinessException {
		final RapOcupacaoCargo ocupacaoCargo = obterOcupacaoCargo(id);
		getRapOcupacaoCargoDAO().remover(ocupacaoCargo);
	}
	
	@Override
	public Long pesquisarOcupacaoCargoCount(Integer codigo,
			String carCodigo, String descricao, DominioSituacao situacao) {
		return getRapOcupacaoCargoDAO().pesquisarOcupacaoCargoCount(codigo, carCodigo, descricao, situacao);
	}
	
	/**
	 * Retorna os cargos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	@Override
	public List<OcupacaoCargoVO> pesquisarOcupacaoPorCodigo(
			String ocupacaoCargo, boolean somenteAtivos) throws ApplicationBusinessException {
		return getOcupacaoCargoON().pesquisarOcupacaoPorCodigo(ocupacaoCargo, somenteAtivos);
	}
	
	@Override
	public RapGrupoFuncional pesquisarGrupoFuncionalPorCodigo(short codigo)	throws ApplicationBusinessException {
		return getGrupoFuncionalON().pesquisarGrupoFuncionalPorCodigo(codigo);
	}
	
	@Override
	public Long montarConsultaCount(Short codigo, String descricao) {
		return getGrupoFuncionalDAO().montarConsultaCount(codigo, descricao);
	}
	
	@Override
	public List<RapGrupoFuncional> pesquisarGrupoFuncional(Short codigo,
			String descricao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getGrupoFuncionalDAO().pesquisarGrupoFuncional(codigo, descricao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public void editar(RapGrupoFuncional rapGrupoFuncional) throws ApplicationBusinessException {
		getGrupoFuncionalON().atualizarRapGrupoFuncional(rapGrupoFuncional);
	}
	@Override
	public RapGrupoFuncional obterRapGrupoFuncional(Short codigo){
		return getGrupoFuncionalDAO().obterPorChavePrimaria(codigo);
	}
	
	@Override
	public void gravar(RapGrupoFuncional rapGrupoFuncional) throws ApplicationBusinessException {
		getGrupoFuncionalON().inserirRapGrupoFuncional(rapGrupoFuncional);
	}
	
	@Override
	public void excluir(Short codigoRapGrupoFuncional) throws ApplicationBusinessException {
		getGrupoFuncionalON().excluirGrupoFuncional(codigoRapGrupoFuncional);
	}
	
	/**
	 * Retorna um lista de grupos funcionais encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	@Override
	public List<RapGrupoFuncional> pesquisarGrupoFuncional(
			String grupoFuncional) {
		return getGrupoFuncionalDAO().pesquisarGrupoFuncional(grupoFuncional);
	}
	
	/**
	 * Pesquisar ramais telefonicos pelo numero do ramal
	 * @param paramPesquisa
	 * @return
	 */
	@Override
	public List<RapRamalTelefonico> pesquisarRamalTelefonicoPorNroRamal(Object paramPesquisa){
		return getRamalTelefonicoDAO().pesquisarRamalTelefonicoPorNroRamal(paramPesquisa);
	}
	
	@Override
	public void salvar(RapRamalTelefonico rapRamalTelefonico, boolean alteracao) throws ApplicationBusinessException {
		getRamalTelefonicoON().salvar(rapRamalTelefonico, alteracao);
	}
	
	@Override
	public void excluirRamalTelefonico(Integer nrRamalTelefonico) throws ApplicationBusinessException  {
		getRamalTelefonicoON().excluirRamalTelefonico(nrRamalTelefonico);
	}
	
	@Override
	public List<RapRamalTelefonico> pesquisarRamalTelefonico(Integer numeroRamal,
			String indUrgencia, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getRamalTelefonicoDAO().pesquisarRamalTelefonico(numeroRamal, indUrgencia, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public RapRamalTelefonico obterRamalTelefonico(Integer id) throws ApplicationBusinessException {
		return getRamalTelefonicoON().obterRamalTelefonico(id);
	}
	
	@Override
	public Long pesquisarRamalTelefonicoCount(Integer numeroRamal,
			String indUrgencia) {
		return getRamalTelefonicoDAO().pesquisarRamalTelefonicoCount(numeroRamal, indUrgencia);
	}
	
	/**
	 * Método para salvar um novo vínculo
	 */
	@Override
	public void salvar(RapVinculos vinculo) throws ApplicationBusinessException {
		getVinculosDAO().salvar(vinculo);
	}
	
	/**
	 * Método para alterar um vínculo
	 */
	@Override
	public void alterar(RapVinculos vinculo) throws ApplicationBusinessException {
		getVinculosDAO().alterar(vinculo);
	}
	
	@Override
	public void enviaEmail(Short codigo, String descricao,
			DominioSimNao geraMatricula) throws ApplicationBusinessException {
		getVinculoON().enviaEmail(codigo, descricao, geraMatricula);
	}
	
	// Conselho Profissional
	
	/**
	 * Método para buscar um ConselhoProfissional na base de dados
	 */
	@Override
	public RapConselhosProfissionais obterConselhoProfissional(Short codigoConselhoProfissional)
			throws ApplicationBusinessException {
		return getConselhoProfissionalON().obterConselhoProfissional(codigoConselhoProfissional);		
	}
	
	@Override
	public void excluirConselhoProfissional(Short seq) throws ApplicationBusinessException {
		getConselhoProfissionalON().excluirConselhoProfissional(seq);
	}
	
	/**
	 * Método para pesquisar ConselhoProfissional na base de dados
	 */
	@Override
	public List<RapConselhosProfissionais> pesquisarConselhosProfissionais( Short codigo, String nome, 
			String sigla,
			DominioSituacao indSituacao,String tituloMasculino,String tituloFeminino, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, List<String> conselhosLimitados) {
		return getRapConselhosProfissionaisDAO().pesquisarConselhosProfissionais(codigo, nome, sigla, indSituacao, tituloMasculino, tituloFeminino, firstResult, maxResult, orderProperty, asc,conselhosLimitados);
	}
	
	public String obterSiglaConselho(Short codigo){
		return getRapConselhosProfissionaisDAO().obterSiglaConselho(codigo);
	}
	
	/**
	 * Método para contar a quantidade de Conselho Profissionais pesquisados
	 */
	@Override
	public Long pesquisarConselhosProfissionaisCount(Short codigo, String nome, String sigla,
			DominioSituacao  indSituacao , String tituloMasculino,String tituloFeminino, List<String> conselhosLimitados ) {
		return getRapConselhosProfissionaisDAO().pesquisarConselhosProfissionaisCount(codigo, nome, sigla, indSituacao, tituloMasculino, tituloFeminino,conselhosLimitados);
	}
	
	/**
	 * Método para alterar um ConselhoProfissional
	 */
	@Override
	public void alterar(RapConselhosProfissionais conselhoProfissional) throws ApplicationBusinessException {
		getRapConselhosProfissionaisDAO().alterar(conselhoProfissional);
	}
	
	/**
	 * Método para salvar um novo ConselhoProfissional
	 */
	@Override
	public void salvar(RapConselhosProfissionais conselhoProfissional ) throws ApplicationBusinessException {
		getRapConselhosProfissionaisDAO().salvar(conselhoProfissional);
	}
	
	// Graduação
	
	@Override
	public Long pesquisarGraduacaoCount(Integer codigo, String descricao,
			DominioSituacao situacao, DominioTipoQualificacao tipo,
			RapConselhosProfissionais conselho) {
		return getRapTipoQualificacaoDAO().pesquisarGraduacaoCount(codigo, descricao, situacao, tipo, conselho);
	}	
	
	@Override
	public List<RapTipoQualificacao> pesquisarGraduacao(Integer codigo,
			String descricao, DominioSituacao situacao,
			DominioTipoQualificacao tipo, RapConselhosProfissionais conselho,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getRapTipoQualificacaoDAO().pesquisarGraduacao(codigo, descricao, situacao, tipo, conselho, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public void removerGraduacao(Integer codigo) throws ApplicationBusinessException {
		getGraduacaoON().removerGraduacao(codigo);
	}
	
	/**
	 * Verificar se um novo registro de graduação deve ser inserido no banco. Quando o campo "tipo" for "CSC" e o nome do conselho tiver sido escolhido,
	 * a gravação
	 * 
	 * @param _rapTipoQualificacao Objeto com os tipos de qualificação retornados na pesquisa.
	 * @return
	 */
	@Override
	public void validarRegistroGraduacaoConselho (RapTipoQualificacao _rapTipoQualificacao) 
		throws ApplicationBusinessException{
		getGraduacaoON().validarRegistroGraduacaoConselho(_rapTipoQualificacao);
	}
	
	@Override
	public void alterar(RapTipoQualificacao rapTipoQualificacao) throws ApplicationBusinessException {
		getGraduacaoON().alterarGraduacao(rapTipoQualificacao);
	}
	
	@Override
	public void salvar(RapTipoQualificacao rapTipoQualificacao) throws ApplicationBusinessException {
		getGraduacaoON().salvarGraduacao(rapTipoQualificacao);
	}
	
	@Override
	public RapTipoQualificacao obterGraduacao(Integer codGraduacao, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin)	throws ApplicationBusinessException {
		return getGraduacaoON().obterGraduacao(codGraduacao, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	/**
	 * Retorna os conselhos ativos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	@Override
	public List<RapConselhosProfissionais> pesquisarConselhosAtivosPorDescricao(String valor) {
		return getRapTipoQualificacaoDAO().pesquisarConselhosAtivosPorDescricao(valor);
	}
	
	@Override
	public Long pesquisarConselhosAtivosPorDescricaoCount(String valor) {
		return getRapTipoQualificacaoDAO().pesquisarConselhosAtivosPorDescricaoCount(valor);
	}
	
	/**
	 * Retorna os conselhos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	@Override
	public List<RapConselhosProfissionais> pesquisarConselhosPorDescricao(
			String valor) {
		return getRapTipoQualificacaoDAO().pesquisarConselhosPorDescricao(valor);
	}
	
	// Ocupação Cargo
	
	/**
	 * Retorna os cargos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	@Override
	public List<RapCargos> pesquisarCargosPorDescricao(String valor) {
		return getRapOcupacaoCargoDAO().pesquisarCargosPorDescricao(valor);
	}
	
	// Tipo Afastamento
	
	@Override
	public void salvar(RapTipoAfastamento rapTipoAfastamento, boolean alteracao) throws ApplicationBusinessException {
		getTipoAfastamentoON().salvar(rapTipoAfastamento, alteracao);
	}
	
	@Override
	public RapTipoAfastamento obterTipoAfastamento(String codigo) throws ApplicationBusinessException {
		return getTipoAfastamentoON().obterTipoAfastamento(codigo);
	}
	
	@Override
	public Long pesquisarTipoAfastamentoCount(String codigo, String descricao,
			DominioSituacao indSituacao) {
		return getTipoAfastamentoDAO().pesquisarTipoAfastamentoCount(codigo, descricao, indSituacao);
	}
	
	@Override
	public List<RapTipoAfastamento> pesquisarTipoAfastamento(String codigo,
			String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getTipoAfastamentoDAO().pesquisarTipoAfastamento(codigo, descricao, indSituacao, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public void excluirTipoAfastamento(RapTipoAfastamento tipoAfastamento) throws ApplicationBusinessException {
		getTipoAfastamentoON().excluirTipoAfastamento(tipoAfastamento);
	}
	
	// Instituição Qualificadora 
	
	@Override
	public Long pesquisarInstituicaoQualificadoraCount(Integer codigo,
			String descricao, DominioSimNao indInterno, Boolean indUsoGppg) {
		return getRapInstituicaoQualificadoraDAO().pesquisarInstituicaoQualificadoraCount(codigo, descricao, indInterno, indUsoGppg);
	}
	
	@Override
	public void excluirInstituicaoQualificadora(Integer codInstituicaoQualificadora) throws ApplicationBusinessException  {
		getInstituicaoQualificadoraON().excluirInstituicaoQualificadora(codInstituicaoQualificadora);
	}
	
	@Override
	public RapInstituicaoQualificadora obterInstituicaoQualificadora(
			Integer id) {
		return getInstituicaoQualificadoraON().obterInstituicaoQualificadora(id);
	}
	
	@Override
	public List<RapInstituicaoQualificadora> pesquisarInstituicaoQualificadora(
			Integer codigo, String descricao, DominioSimNao indInterno,
			Boolean indUsoGppg, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getRapInstituicaoQualificadoraDAO().pesquisarInstituicaoQualificadora(codigo, descricao, indInterno, indUsoGppg, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public void salvar(RapInstituicaoQualificadora rapInstituicaoQualificadora, boolean alteracao) throws ApplicationBusinessException {
		getInstituicaoQualificadoraON().salvar(rapInstituicaoQualificadora, alteracao);
	}
	
	/**
	 * Retorna os cursos de graduação.
	 * 
	 * @param curso
	 * @return retorna apenas os 100 primeiros
	 */
	@Override
	public List<RapTipoQualificacao> pesquisarCursoGraduacao(Object curso) {
		return getRapTipoQualificacaoDAO().pesquisarCursoGraduacao(curso);
	}	
	
	/**
	 * Retorna instituições qualificadoras com os parâmetros fornecidos.
	 * 
	 * @param instituicaoObject
	 * @return retorna apenas os 100 primeiros
	 */
	@Override
	public List<RapInstituicaoQualificadora> pesquisarInstituicao(
			Object instituicaoObject) {
		return getRapInstituicaoQualificadoraDAO().pesquisarInstituicao(instituicaoObject);
	}
	
	// Cargo
	
	@Override
	public Long pesquisarCargoCount(String codigo, String descricao,
			DominioSituacao situacao) {
		return getCargosDAO().pesquisarCargoCount(codigo, descricao, situacao);
	}
	
	@Override
	public List<RapCargos> pesquisarCargo(String codigo, String descricao,
			DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getCargosDAO().pesquisarCargo(codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public RapCargos obterCargo(String codCargo) throws ApplicationBusinessException {
		return getCargoON().obterCargo(codCargo);
	}
	
	@Override
	public void removerCargo(String codCargo) throws ApplicationBusinessException {
		getCargoON().removerCargo(codCargo);
	}
	
	@Override
	public void alterar(RapCargos rapCargos) throws ApplicationBusinessException {
		getCargoON().alterarCargo(rapCargos);
	}
	
	@Override
	public void salvar(RapCargos rapCargos) throws ApplicationBusinessException {
		getCargoON().salvarCargo(rapCargos);
	}	
	
	@Override
	public RapTipoQualificacao obterTipoQualificacao(Integer chavePrimaria){
		return this.getRapTipoQualificacaoDAO().obterPorChavePrimaria(chavePrimaria);
	}
	
	@Override
	public void incluirOcupacaoCargo(RapOcupacaoCargo ocupacaoCargo) throws ApplicationBusinessException {
		getOcupacaoCargoON().incluir(ocupacaoCargo);
	}	
	
	// getters e setters de ON / RN / DAO
	
	protected VinculoON getVinculoON() {
		return vinculoON;
	}
	
	protected RapVinculosDAO getVinculosDAO() {
		return rapVinculosDAO;
	}	
	
	protected TipoInformacoesON getTipoInformacoesON() {
		return tipoInformacoesON;
	}
	
	protected RapTipoInformacoesDAO getTipoInformacoesDAO() {
		return rapTipoInformacoesDAO;
	}	
	
	protected TipoAfastamentoON getTipoAfastamentoON() {
		return tipoAfastamentoON;
	}		
	
	protected RapTipoAfastamentoDAO getTipoAfastamentoDAO() {
		return  RapTipoAfastamentoDAO;
	}	
	
	protected OcupacaoCargoON getOcupacaoCargoON() {
		return ocupacaoCargoON;
	}
	
	protected GrupoFuncionalON getGrupoFuncionalON() {
		return grupoFuncionalON;
	}
	
	protected RapGrupoFuncionalDAO getGrupoFuncionalDAO() {
		return rapGrupoFuncionalDAO;
	}	
		
	protected RamalTelefonicoON getRamalTelefonicoON() {
		return ramalTelefonicoON;
	}
	
	protected RapRamalTelefonicoDAO getRamalTelefonicoDAO() {
		return rapRamalTelefonicoDAO;
	}
	
	protected ConselhoProfissionalON getConselhoProfissionalON() {
		return conselhoProfissionalON;
	}
	
	protected GraduacaoON getGraduacaoON() {
		return graduacaoON;
	}
	
	protected RapOcupacaoCargoDAO getRapOcupacaoCargoDAO() {
		return rapOcupacaoCargoDAO;
	}
	
	protected InstituicaoQualificadoraON getInstituicaoQualificadoraON() {
		return instituicaoQualificadoraON;
	}
	
	protected CargoON getCargoON() {
		return cargoON;
	}
	
	protected RapCargosDAO getCargosDAO() {
		return rapCargosDAO;
	}
	
	protected RapTipoQualificacaoDAO getRapTipoQualificacaoDAO() {
		return rapTipoQualificacaoDAO;
	}	
	
	protected RapInstituicaoQualificadoraDAO getRapInstituicaoQualificadoraDAO() {
		return rapInstituicaoQualificadoraDAO;
	}

	@Override
	public RapConselhosProfissionais obterConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao) {
		return getRapConselhosProfissionaisDAO().obterConselhoProfissionalComNroRegConselho(matricula, vinCodigo, situacao);
	}	
	
	@Override
	public List<RapConselhosProfissionais> listarConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao) {
		return getRapConselhosProfissionaisDAO().listarConselhoProfissionalComNroRegConselho(matricula, vinCodigo, situacao);
	}	
	
	protected RapTipoAtuacaoDAO getRapTipoAtuacaoDAO() {
		return rapTipoAtuacaoDAO;
	}

	@Override
	public RapTipoAtuacao obterTipoAtuacao(short codigo) {
		return getRapTipoAtuacaoDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public RapTipoAtuacao getTipoAtuacaoDiscente() throws ApplicationBusinessException {
		return getRapTipoAtuacaoDAO().getTipoAtuacaoDiscente();
	}	
	
	protected RapConselhosProfissionaisDAO getRapConselhosProfissionaisDAO(){
		return rapConselhosProfissionaisDAO;
	}

	@Override
	public String obterRapQualificaoComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao) {
		return getRapConselhosProfissionaisDAO().obterNroRegConselhoProfissional(matricula, vinCodigo, situacao);
	}
	
	@Override
	public RapTipoQualificacao obterQualificacaoEConselhoPorCodigo(Integer codigo){
		return rapTipoQualificacaoDAO.obterQualificacaoEConselhoPorCodigo(codigo);
		
	}
}