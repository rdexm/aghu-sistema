package br.gov.mec.aghu.registrocolaborador.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.vo.ProfissionalHospitalVO;
import br.gov.mec.aghu.ambulatorio.vo.VRapPessoaServidorVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.controlepaciente.vo.DescricaoControlePacienteVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.dominio.DominioTipoRemuneracao;
import br.gov.mec.aghu.exames.vo.VRapServidorConselhoVO;
import br.gov.mec.aghu.faturamento.vo.VFatProfRespDcsVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.RapServidoresTransPacienteVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.CsePerfilProcessoLocal;
import br.gov.mec.aghu.model.CsePerfilProcessos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapChefias;
import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapDependentesId;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoesId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapQualificacoesId;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.model.RarCandidatoPrograma;
import br.gov.mec.aghu.model.RarCandidatos;
import br.gov.mec.aghu.model.RarPrograma;
import br.gov.mec.aghu.model.RarProgramaDuracao;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.VMpmServConselhoGeralVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RapServidoresProcedimentoTerapeuticoVO;
import br.gov.mec.aghu.registrocolaborador.dao.CseCategoriaPerfilDAO;
import br.gov.mec.aghu.registrocolaborador.dao.CsePerfilProcessoLocalDAO;
import br.gov.mec.aghu.registrocolaborador.dao.CsePerfilProcessosDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapCodStarhLivresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapConselhosProfissionaisDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapDependentesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapOcupacaoCargoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoaTipoInformacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapQualificacaoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapRamaisCentroCustoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoQualificacaoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapVinculosDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RarCandidatoProgramaDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RarCandidatosDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RarProgramaDuracaoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapPessoaServidorDAO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapServidorConselhoDAO;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;
import br.gov.mec.aghu.registrocolaborador.vo.CursorBuscaCboVO;
import br.gov.mec.aghu.registrocolaborador.vo.CursorCurPreVO;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.registrocolaborador.vo.PessoaTipoInformacoesVO;
import br.gov.mec.aghu.registrocolaborador.vo.RapPessoalServidorVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * Classe de fachada que disponibiliza uma interface para as funcionalidades do
 * módulo Registro do colaborador.
 */
@Modulo(ModuloEnum.REGISTRO_COLABORADOR)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class RegistroColaboradorFacade extends BaseFacade implements IRegistroColaboradorFacade {

	@EJB
	private ServidorON servidorON;
	
	@EJB
	private PessoaFisicaON pessoaFisicaON;
	
	@EJB
	private PessoaTipoInformacoesON pessoaTipoInformacoesON;
	
	@EJB
	private DependenteRN dependenteRN;
	
	@EJB
	private CentroCustoAtuacaoON centroCustoAtuacaoON;
	
	@EJB
	private RapCodStarhLivresRN rapCodStarhLivresRN;
	
	@EJB
	private DependenteON dependenteON;
	
	@EJB
	private QualificacaoON qualificacaoON;
	
	@EJB
	private QualificacaoRN qualificacaoRN;
	
	@EJB
	private ServidorRN servidorRN;
	
	@EJB
	private RapServidoresRN rapServidoresRN;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private RapConselhosProfissionaisDAO rapConselhosProfissionaisDAO;
	
	@Inject
	private RarProgramaDuracaoDAO rarProgramaDuracaoDAO;

	@Inject
	private RapPessoaTipoInformacoesDAO rapPessoaTipoInformacoesDAO;

	@Inject
	private RapCodStarhLivresDAO rapCodStarhLivresDAO;

	@Inject
	private RapServidoresDAO rapServidoresDAO;

	@Inject
	private CseCategoriaPerfilDAO cseCategoriaPerfilDAO;

	@Inject
	private CsePerfilProcessosDAO csePerfilProcessosDAO;

	@Inject
	private RapRamaisCentroCustoDAO rapRamaisCentroCustoDAO;

	@Inject
	private RarCandidatoProgramaDAO rarCandidatoProgramaDAO;

	@Inject
	private VRapPessoaServidorDAO vRapPessoaServidorDAO;

	@Inject
	private CsePerfilProcessoLocalDAO csePerfilProcessoLocalDAO;

	@Inject
	private RapDependentesDAO rapDependentesDAO;

	@Inject
	private VRapServidorConselhoDAO vRapServidorConselhoDAO;

	@Inject
	private RarCandidatosDAO rarCandidatosDAO;

	@Inject
	private RapQualificacaoDAO rapQualificacaoDAO;

	@Inject
	private RapPessoasFisicasDAO rapPessoasFisicasDAO;

	@Inject
	private RapTipoQualificacaoDAO rapTipoQualificacaoDAO;

	@Inject
	private RapVinculosDAO rapVinculosDAO;
	
	@Inject
	private RapOcupacaoCargoDAO rapOcupacaoCargoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2851906271631869838L;

	/**
	 * Retorna o servidor com o id fornecido.
	 * 
	 * @param id
	 * @return null se não encontrado
	 */
	@Override
	public RapServidores obterServidor(RapServidoresId id) {
		return getServidorON().obterServidor(id);
	}
	
	/**
	 * Retorna um servidor ativo no sistema conforme o login do usuário
	 * 
	 * @param login
	 *            Usuário do servidor no sistema AGHU
	 * @return Servidor ativo
	 */
	public RapServidores obterServidorAtivoPorUsuario(final String login)
			throws ApplicationBusinessException {
		return getRapServidoresDAO().obterServidorAtivoPorUsuario(login);
	}

	public RapServidores obterServidorAtivoPorUsuario(final String login,
			final Date dtFimVinculo) throws ApplicationBusinessException {
		return getRapServidoresDAO().obterServidorAtivoPorUsuario(login,
				dtFimVinculo);
	}

	public Long pesquisarServidoresAtivosPendentesCount(Object servidor) {
		return getRapServidoresDAO().pesquisarServidoresAtivosPendentesCount(
				servidor);
	}

	public List<RapServidores> pesquisarServidoresAtivosPendentes(
			Object servidor) {
		return getRapServidoresDAO().pesquisarServidoresAtivosPendentes(
				servidor);
	}

	public List<RapServidores> pesquisarServidoresAtivosPendentesMedicoProfessoreUnidade(
			Object servidor, short unfSeq) {
		return getRapServidoresDAO()
				.pesquisarServidoresAtivosPendentesMedicoProfessoreUnidade(
						servidor, unfSeq);
	}

	@Override
	public Long pesquisarServidoresAtivosPendentesMedicoProfessoreUnidadeCount(
			Object servidor, short unfSeq) {
		return getRapServidoresDAO()
				.pesquisarServidoresAtivosPendentesMedicoProfessoreUnidadeCount(
						servidor, unfSeq);
	}

	/**
	 * Retorna um servidor no sistema conforme o seu login de usuário
	 * 
	 * @param login
	 *            Usuário do Servidor no sistema AGHU
	 * @return Servidor ativo ou inativo
	 */
	public RapServidores obterServidorPorUsuario(final String login)
			throws ApplicationBusinessException {
		return getRapServidoresDAO().obterServidorPorUsuario(login);
	}

	@Override
	public RapServidores obterServidor(RapCodStarhLivres rapCodStarhLivres) {
		return getRapServidoresDAO().obterServidor(rapCodStarhLivres);
	}

	@Override
	public RapPessoasFisicas obterPessoaFisica(Integer codigo)
			throws ApplicationBusinessException {
		return getPessoaFisicaON().obterPessoaFisica(codigo);
	}
	
	
	public RapPessoasFisicas obterPessoaFisicaCRUD(Integer codigo)
			throws ApplicationBusinessException {
		return getPessoaFisicaON().obterPessoaFisicaCRUD(codigo);
	}

	@Override
	public RapDependentes obterDependente(RapDependentesId id){
		return getRapDependentesDAO().obterPorChavePrimaria(id);
	}

	@Override
	public RapDependentes obterDependente(RapDependentesId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin){
		return getRapDependentesDAO().obterPorChavePrimaria(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	public RapServidores obterServidorAtivoPorUsuarioSemCache(final String loginUsuario) throws ApplicationBusinessException {
		return getRapServidoresDAO().obterServidorAtivoPorUsuarioSemCache(loginUsuario, new Date());
	}
	
	/**
	 * Retorna o primeiro servidor encontrado com os parâmetros fornecidos.
	 * 
	 * @param pesCodigo
	 *            - codigo da Pessoa
	 * 
	 * @return null se não encontrado
	 */
	@Override
	public RapServidores buscarServidorPesCodigo(Integer pesCodigo) {
		return getRapServidoresDAO().buscarServidor(pesCodigo);
	}

	@Override
	public void salvarDependente(RapDependentes dependentes) throws ApplicationBusinessException {
		getDependenteON().salvarDependente(dependentes);
	}

	@Override
	public Long pesquisarDependenteCount(Integer pesCodigo, Integer codigo,
			Integer serMatricula, Short serVinCodigo)
			throws ApplicationBusinessException {
		return getDependenteRN().pesquisarDependenteCount(pesCodigo, codigo,
				serMatricula, serVinCodigo);
	}

	@Override
	public List<RapDependentes> pesquisarDependente(Integer pesCodigo,
			Integer codigo, Integer serMatricula, Short serVinCodigo,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {
		return getDependenteRN().pesquisarDependente(pesCodigo, codigo,
				serMatricula, serVinCodigo, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public void alterar(RapDependentes dependente, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		getDependenteON().alterar(dependente, servidorLogado);
	}

	@Override
	public void alterar(RapServidores servidor, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		getServidorON().alterar(servidor);
	}

	@Override
	public void inserir(RapServidores servidor)
			throws ApplicationBusinessException {
		getServidorON().inserir(servidor);
	}

	@Override
	public String obterDescricaoOcupacaoTabelaSTARH(Integer codigoOcupacaoCargo) throws ApplicationBusinessException{
		return getServidorON().obterDescricaoOcupacaoTabelaSTARH(codigoOcupacaoCargo);
	}

	@Override
	public void removerDependente(RapDependentesId id) throws ApplicationBusinessException {
		getDependenteON().removerDependente(id);
	}

	@Override
	public List<RapPessoasFisicas> pesquisarPessoaFisica(String paramPesquisa) {
		return getRapPessoasFisicasDAO().buscarPessoaFisica(paramPesquisa);
	}

	@Override
	public Long pesquisarPessoaFisicaCount(Object paramPesquisa) {
		return getRapPessoasFisicasDAO().buscarPessoaFisicaCount(paramPesquisa);
	}

	@Override
	public List<RapPessoasFisicas> pesquisarPessoaFisica(Integer codigo,
			String nome, Long cpf, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getRapPessoasFisicasDAO().pesquisarPessoaFisica(codigo, nome,
				cpf, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Retorna pessoa física de acordo com o código ou nome informados
	 * 
	 * @dbtables RapPessoasFisicas select
	 * 
	 * @param codigo
	 *            ou nome
	 * @return pessoas físicas encontradas ou lista vazia se não encontrado
	 */
	@Override
	public List<RapPessoasFisicas> pesquisarPessoaFisicaPorCodigoNome(
			String pessoaFisica) {
		return getRapPessoasFisicasDAO().pesquisarPessoaFisicaPorCodigoNome(
				pessoaFisica);
	}

	@Override
	public Long pesquisarPessoaFisicaCount(Integer codigo, String nome, Long cpf) {
		return getRapPessoasFisicasDAO().pesquisarPessoaFisicaCount(codigo,
				nome, cpf);
	}

	@Override
	public Integer obterNumeroDependentes(RapPessoasFisicas pessoaFisica) {
		return getRapPessoasFisicasDAO().obterNumeroDependentes(
				pessoaFisica.getCodigo());
	}

	@Override
	public void desatachar(RapPessoasFisicas pessoaFisica) {
		getRapPessoasFisicasDAO().desatachar(pessoaFisica);
	}

	@Override
	public boolean existeVinculoDependentesPessoa(RapPessoasFisicas pf)
			throws ApplicationBusinessException {
		return getPessoaFisicaON().existeVinculoDependentesPessoa(
				pf.getCodigo());
	}

	@Override
	public void alterar(RapPessoasFisicas pessoaFisica, RapServidores servidorLogado) throws ApplicationBusinessException {
		getPessoaFisicaON().alterar(pessoaFisica, servidorLogado);
	}

	@Override
	public void salvar(RapPessoasFisicas pessoaFisica)
			throws ApplicationBusinessException {
		getPessoaFisicaON().salvar(pessoaFisica);
	}

	/**
	 * Retorna o primeiro servidor encontrado com os parâmetros fornecidos.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param nome
	 * 
	 * @return null se não encontrado
	 */
	@Override
	public RapServidores buscarServidor(Short vinculo, Integer matricula,
			String nome) {
		return getRapServidoresDAO().buscarServidor(vinculo, matricula, nome);
	}

	@Override
	public String obterIdadeExtenso(Date dataParametro)
			throws ApplicationBusinessException {
		return getServidorRN().obterIdadeExtenso(dataParametro);
	}

	@Override
	public String obterTempoExtenso(Date dataInicioVinculo, Date dataFimVinculo)
			throws ApplicationBusinessException {
		return getServidorRN().obterTempoExtenso(dataInicioVinculo,
				dataFimVinculo);
	}

	/**
	 * Retornar infomação de afastamento
	 * 
	 * @param dataFimVinculo
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@Override
	public String obterAfastamento(Date dataFimVinculo, Integer matricula,
			Short vinCodigo) {
		return getServidorRN().obterAfastamento(dataFimVinculo, matricula,
				vinCodigo);
	}

	/**
	 * Montar as confirmações que serão apresentadas na tela conforme regra de
	 * negócio
	 * 
	 * @param novo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String montarConfirmacoes(RapServidores servidor, boolean edicao)
			throws ApplicationBusinessException {
		return getServidorRN().montarConfirmacoes(servidor, edicao);
	}

	/**
	 * Retorna o servidor com os argumentos fornecidos.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return null se não encontrado
	 */
	@Override
	public RapServidores obterServidor(Short vinculo, Integer matricula) {
		return getServidorON().obterServidor(vinculo, matricula);
	}

	/**
	 * Retorna servidores de acordo com a matricula ou nome fornecido.
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param servidor
	 *            matricula ou nome
	 * @return servidores encontrados em ordem alfabética ou lista vazia se não
	 *         encontrado
	 */
	@Override
	public List<RapServidores> pesquisarServidor(Object servidor) {
		return getRapServidoresDAO().pesquisarServidorPorMatriculaOuNome(
				servidor);
	}

	@Override
	public List<RapServidores> pesquisarServidorPontoServidor(Object servidor) {
		return getRapServidoresDAO()
				.pesquisarServidorPorMatriculaOuNomePontoServidor(servidor);
	}

	@Override
	public List<RapServidores> pesquisarServidorVinculoAtivoEProgramadoAtual(
			Object servidor) {
		return getRapServidoresDAO()
				.pesquisarServidorVinculoAtivoEProgramadoAtual(servidor);
	}

	@Override
	public List<RapServidores> pesquisarServidor(Short vinCodigo, Integer matricula, DominioSituacaoVinculo indSituacao,
			RapPessoasFisicas pessoaFisica, String usuario,	FccCentroCustos codigoCCustoLotacao, FccCentroCustos codigoCCustoAtuacao,
			DominioTipoRemuneracao tipoRemuneracao, OcupacaoCargoVO ocupacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Date dataAtual, Date proximaData) {

		return getRapServidoresDAO().pesquisarServidor(vinCodigo, matricula,indSituacao, pessoaFisica, usuario, codigoCCustoLotacao,
				codigoCCustoAtuacao, tipoRemuneracao, ocupacao,firstResult, maxResult, orderProperty, asc, dataAtual, proximaData);

	}

	@Override
	public Long pesquisarServidorCount(Short vinCodigo, Integer matricula,
			DominioSituacaoVinculo indSituacao, RapPessoasFisicas pessoaFisica,	String usuario, FccCentroCustos codigoCCustoLotacao,
			FccCentroCustos codigoCCustoAtuacao, DominioTipoRemuneracao tipoRemuneracao,OcupacaoCargoVO ocupacao, Date dataAtual, Date proximaData) {
		return getRapServidoresDAO().pesquisarServidorCount(vinCodigo, matricula, indSituacao, pessoaFisica, usuario,
				codigoCCustoLotacao, codigoCCustoAtuacao, tipoRemuneracao, ocupacao, dataAtual, proximaData);

	}


	@Override
	public List<RapPessoaTipoInformacoes> listarPorPessoaFisicaTipoInformacao(
			Integer pesCodigo, Short[] codTipoInformacoes,
			final Date dtRealizado) {
		return getRapPessoaTipoInformacoesDAO()
				.listarPorPessoaFisicaTipoInformacao(pesCodigo,
						codTipoInformacoes, dtRealizado);

	}

	@Override
	public String listarPorPessoaFisicaValorTipoInformacao(Integer pesCodigo,
			String[] valorTipoInformacao, final Date dtRealizado) {
		return getRapPessoaTipoInformacoesDAO()
				.listarPorPessoaFisicaValorTipoInformacao(pesCodigo,
						valorTipoInformacao, dtRealizado);
	}

	@Override
	public RapServidores obterRapServidoresPorChavePrimaria(RapServidoresId chavePrimaria) {
		return getRapServidoresDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public void insert(RapCodStarhLivres rapCodStarhLivres)
			throws ApplicationBusinessException {
		getRapCodStarhLivresRN().insert(rapCodStarhLivres);
	}

	/**
	 * Verifica se o tipo de informação passado é um CBO
	 */
	@Override
	public boolean isCbo(Short tiiSeq) throws ApplicationBusinessException {
		return getPessoaTipoInformacoesON().isCbo(tiiSeq);
	}

	@Override
	public RapPessoaTipoInformacoes obterPessoaTipoInformacoes(
			RapPessoaTipoInformacoesId rapPessoaTipoInformacoesId)
			throws ApplicationBusinessException {

		final RapPessoaTipoInformacoes result = getRapPessoaTipoInformacoesDAO()
				.obterPorChavePrimaria(rapPessoaTipoInformacoesId);
		getRapPessoaTipoInformacoesDAO().refresh(result);

		return result;
	}

	@Override
	public RapPessoaTipoInformacoes obterPessoaTipoInformacoesSemRefresh(RapPessoaTipoInformacoesId rapPessoaTipoInformacoesId){
		return getRapPessoaTipoInformacoesDAO().obterPorChavePrimaria(rapPessoaTipoInformacoesId);
	}

	@Override
	public String obterRapPessoaTipoInformacoesPorPesCPFTiiSeq(final Long cpf,
			final Short tiiSeq) {
		return getRapPessoaTipoInformacoesDAO()
				.obterRapPessoaTipoInformacoesPorPesCPFTiiSeq(cpf, tiiSeq);
	}

	@Override
	public List<PessoaTipoInformacoesVO> pesquisarPessoaTipoInformacoes(
			Integer pesCodigo, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getRapPessoaTipoInformacoesDAO().pesquisarPessoaTipoInformacoes(
				pesCodigo, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarPessoaTipoInformacoesCount(Integer pesCodigo) {
		return getRapPessoaTipoInformacoesDAO()
				.pesquisarPessoaTipoInformacoesCount(pesCodigo);
	}

	@Override
	public void salvar(RapPessoaTipoInformacoes pessoaTipoInformacoes,
			boolean alterar)
			throws ApplicationBusinessException {
		getPessoaTipoInformacoesON().salvar(pessoaTipoInformacoes, alterar);
	}

	/**
	 * Retorna o servidor pelo id.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return
	 */
	@Override
	public RapServidores buscarServidor(Short vinculo, Integer matricula) {
		return getPessoaTipoInformacoesON().buscarServidor(vinculo, matricula);
	}

	@Override
	public void excluirPessoaTipoInformacao(RapPessoaTipoInformacoesId id)
			throws ApplicationBusinessException {
		getPessoaTipoInformacoesON().excluirPessoaTipoInformacao(id);
	}

	// Qualificação

	/**
	 * Retorna a quantidade de registros de graduação encontrados com os
	 * parâmetros fornecidos.
	 * 
	 * @param pesCodigo
	 * @param serMatricula
	 * @param serVinCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Long pesquisarGraduacaoCount(Integer pesCodigo, Integer serMatricula, Short serVinCodigo){
		return getQualificacaoRN().pesquisarGraduacaoCount(pesCodigo, serMatricula, serVinCodigo);
	}

	/**
	 * Retorna as graduações com os parâmetros fornecidos.
	 * 
	 * @param object
	 * @param matricula
	 * @param vinculo
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<RapQualificacao> pesquisarGraduacao(Integer pesCodigo, Integer matricula, Short vinculo, Integer firstResult,
													Integer maxResult, String orderProperty, boolean asc){
		return getQualificacaoRN().pesquisarGraduacao(pesCodigo, matricula, vinculo, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Altera a graduação do servidor.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void alterar(RapQualificacao qualificacao,RapServidores servidorLogado) throws ApplicationBusinessException {
		getQualificacaoON().alterarGraduacao(qualificacao, servidorLogado);
	}

	/**
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void remover(RapQualificacao qualificacao) throws ApplicationBusinessException {
		getQualificacaoON().remover(qualificacao);
	}

	/**
	 * Inclui graduação do servidor.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void incluir(RapQualificacao qualificacao) throws ApplicationBusinessException {
		getQualificacaoON().incluirGraduacao(qualificacao);
	}

	@Override
	public RapQualificacao obterQualificacao(RapQualificacoesId id) {
		return getQualificacaoON().obterQualificacao(id);
	}

	@Override
	public RapQualificacao obterQualificacao(RapQualificacoesId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getQualificacaoON().obterQualificacao(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	// Registro Conselho

	@Override
	public Long pesquisarQualificacoesCount(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho) {
		return getRapQualificacaoDAO().pesquisarQualificacoesCount(
				codigoPessoa, nomePessoa, numRegistro, siglaConselho);
	}

	@Override
	public List<RapQualificacao> pesquisarQualificacoes(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho,
			Integer firstResult, Integer maxResults) {
		return getRapQualificacaoDAO()
				.pesquisarQualificacoes(codigoPessoa, nomePessoa, numRegistro,
						siglaConselho, firstResult, maxResults);
	}

	// Centro Custo Atuação

	@Override
	public Long pesquisarServidoresCount(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao) throws ApplicationBusinessException {
		return getCentroCustoAtuacaoON().pesquisarServidoresCount(
				codigoCCLotacao, codVinculo, matricula, nomeServidor,
				codigoCCAtuacao);
	}

	@Override
	public FccCentroCustos obterCentroCusto(FccCentroCustos codigo)
			throws ApplicationBusinessException {
		return getCentroCustoAtuacaoON().obterCentroCusto(codigo);
	}

	@Override
	public void salvar(RapServidores rapServidor)
			throws ApplicationBusinessException {
		getCentroCustoAtuacaoON().salvar(rapServidor);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemDescricao(
			Object centroCusto) {
		return getCentroCustoAtuacaoON()
				.pesquisarCentroCustosAtivosOrdemDescricao(centroCusto);
	}

	@Override
	public List<RapServidores> pesquisarServidores(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc)
			throws ApplicationBusinessException {
		return getCentroCustoAtuacaoON().pesquisarServidores(codigoCCLotacao,
				codVinculo, matricula, nomeServidor, codigoCCAtuacao,
				firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosOrdemDescricao(
			Object centroCusto) {
		return getCentroCustoAtuacaoON()
				.pesquisarCentroCustosAtivosOrdemDescricao(centroCusto);
	}

	/**
	 * ORADB: Function RAPC_BUSCA_COD_STARH
	 * 
	 * Versão java da Function RAPC_BUSCA_COD_STARH
	 * 
	 * @param
	 * @return codStarh
	 * @throws ApplicationBusinessException
	 * @see {@link ObjetosOracleDAO}
	 */
	@Override
	public Integer obterProximoCodStarhLivre() {
		return getRapCodStarhLivresDAO().obterProximoCodStarhLivre();
	}

	/**
	 * Pesquisa todos os servidores a partir de um nome, ou parte dele, e por
	 * uma lista de usuários Este método foi feito para reproduzir, em parte, a
	 * view V_FAT_MEDICOS_AUDITORES
	 * 
	 * @param nome
	 *            Nome, ou parte, de uma servidor
	 * @param listaUsuarios
	 *            Lista contendo logins de usuários do sistema
	 * @param ativo
	 *            Indicador que informa se deve retornar apenas os ativos
	 *            (true), ou todos (false)
	 * @return Servidores que contemplem os filtros de pesquisa ou lista vazia
	 *         caso contrário
	 */
	@Override
	public List<RapServidores> pesquisarServidorPorNomeEUsuarios(String nome,
			Collection<String> listaUsuarios, Boolean ativo) {
		return getRapServidoresDAO().pesquisarServidorPorNomeEUsuarios(nome,
				listaUsuarios, true);
	}
	
    @Override
    public List<RapServidores> pesquisarServidoresPorTipoInformacao(final String filtro, final DominioSituacaoVinculo situacao, final String tipoInformacao){
    	return getRapServidoresDAO().pesquisarServidoresPorTipoInformacao(filtro, situacao, tipoInformacao);
    }
    
	@Override
	public Long pesquisarServidoresPorTipoInformacaoCount(final String filtro, final DominioSituacaoVinculo situacao, final String tipoInformacao){
		return getRapServidoresDAO().pesquisarServidoresPorTipoInformacaoCount(filtro, situacao, tipoInformacao);
	}

	@Override
	public Long pesquisarServidorPorNomeEUsuariosCount(String nome,
			Collection<String> listaUsuarios, Boolean ativo) {
		return getRapServidoresDAO().pesquisarServidorPorNomeEUsuariosCount(
				nome, listaUsuarios, true);
	}

	@Override
	public String obterPrimeiroNroRegistroConselho(Integer matricula,
			Short vinculo) {
		return getRapServidoresDAO().obterPrimeiroNroRegistroConselho(
				matricula, vinculo);
	}

	@Override
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula) {
		return getRapServidoresDAO().buscarNroRegistroConselho(vinCodigo,
				matricula);
	}

	@Override
	public String obterNomeServidor(Integer matricula, Short vinculo) {
		return getRapServidoresDAO().obterNomeServidor(matricula, vinculo);
	}

	// APOIO RESIDENTES

	@Override
	public Long pesquisarCandidatosPorNacionalidadeCount(
			AipNacionalidades nacionalidade) {
		return getRarCandidatosDAO().pesquisarCandidatosPorNacionalidadeCount(
				nacionalidade);
	}

	/**
	 * Retorna rapServidores de acordo com o codigo passado por parametro
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	public RapServidores pesquisarResponsavel(Short codigo, Integer matricula,
			String nomeResponsavel) {
		return getRapServidoresDAO().pesquisarResponsavel(codigo, matricula,
				nomeResponsavel);
	}

	/**
	 * Retorna rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param responsavel
	 * @return
	 */
	@Override
	public List<RapServidores> pesquisarResponsaveis(Object responsavel) {
		return getRapServidoresDAO().pesquisarResponsaveis(responsavel);
	}

	@Override
	public List<ServidorConselhoVO> pesquisarServidorConselhoVOPorNomeeCRM(
			Object strPesquisa) {
		return getRapServidoresDAO().pesquisarServidorConselhoVOPorNomeeCRM(
				strPesquisa);
	}

	@Override
	public List<ServidoresCRMVO> pesquisarServidorCRMVOPorNomeeCRM(
			Object strPesquisa) {
		return getRapServidoresDAO().pesquisarServidorCRMVOPorNomeeCRM(
				strPesquisa);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade#obterServidorConselhoGeralPorIdServidor(java.lang.Integer, java.lang.Short)
	 */
	@Override
	public VMpmServConselhoGeralVO obterServidorConselhoGeralPorIdServidor(Integer matricula, Short vinCodigo) {

		return getRapQualificacaoDAO().obterServidorConselhoGeralPorIdServidor(matricula, vinCodigo);
	}

	protected RapQualificacaoDAO getRapQualificacaoDAO() {
		return rapQualificacaoDAO;
	}

	protected QualificacaoRN getQualificacaoRN() {
		return qualificacaoRN;
	}

	protected ServidorON getServidorON() {
		return servidorON;
	}

	protected ServidorRN getServidorRN() {
		return servidorRN;
	}

	protected PessoaFisicaON getPessoaFisicaON() {
		return pessoaFisicaON;
	}

	protected RapPessoasFisicasDAO getRapPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}

	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	protected RapPessoaTipoInformacoesDAO getRapPessoaTipoInformacoesDAO() {
		return rapPessoaTipoInformacoesDAO;
	}

	protected DependenteON getDependenteON() {
		return dependenteON;
	}

	protected RapDependentesDAO getRapDependentesDAO() {
		return rapDependentesDAO;
	}

	protected DependenteRN getDependenteRN() {
		return dependenteRN;
	}

	protected RapCodStarhLivresRN getRapCodStarhLivresRN() {
		return rapCodStarhLivresRN;
	}

	protected PessoaTipoInformacoesON getPessoaTipoInformacoesON() {
		return pessoaTipoInformacoesON;
	}

	protected QualificacaoON getQualificacaoON() {
		return qualificacaoON;
	}

	protected CentroCustoAtuacaoON getCentroCustoAtuacaoON() {
		return centroCustoAtuacaoON;
	}

	protected RapCodStarhLivresDAO getRapCodStarhLivresDAO() {
		return rapCodStarhLivresDAO;
	}

	protected RarCandidatosDAO getRarCandidatosDAO() {
		return rarCandidatosDAO;
	}

	protected RapTipoQualificacaoDAO getRapTipoQualificacaoDAO() {
		return rapTipoQualificacaoDAO;
	}

	@Override
	public RapServidores pesquisaRapServidorPorSituacaoPacienteVO(
			SituacaoPacienteVO situacaoPacienteVO) {
		return this.getRapServidoresDAO()
				.pesquisaRapServidorPorSituacaoPacienteVO(situacaoPacienteVO);
	}

	@Override
	public List<EspCrmVO> pesquisaProfissionalEspecialidade(
			AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException {

		List<Object[]> resultList = getRapQualificacaoDAO()
				.pesquisaProfissionalEspecialidade(especialidade, strPesquisa);

		List<EspCrmVO> profissionais = new ArrayList<EspCrmVO>(0);

		for (Object[] object : resultList) {
			EspCrmVO vo = new EspCrmVO();

			if (object[0] != null) {
				vo.setNroRegConselho(object[0].toString());
			}

			if (object[1] != null) {
				vo.setNomeMedico(object[1].toString());
			}

			profissionais.add(vo);
		}

		return profissionais;
	}

	@Override
	public List<RapServidores> pesquisarServidores(String paramPesquisa,
			AghEspecialidades especialidade, FatConvenioSaude convenio,
			Integer[] tipoQualificacao, Integer maxResults)
			throws ApplicationBusinessException {
		return getRapServidoresDAO().pesquisarServidores(paramPesquisa,
				especialidade, convenio, tipoQualificacao, maxResults);
	}

	@Override
	public List<RapServidoresTransPacienteVO> pesquisarServidoresPorNomeOuCRM(
			String paramPesquisa, AghEspecialidades especialidade,
			FatConvenioSaude convenio, Integer[] tipoQualificacao,
			Integer maxResults) throws ApplicationBusinessException {
		return getRapServidoresDAO().pesquisarServidoresPorNomeOuCRM(
				paramPesquisa, especialidade, convenio, tipoQualificacao,
				maxResults);
	}

	/**
	 * Retorna o número de registro no conselho profissional.
	 * 
	 * @param pessoa
	 *            id da pessoa física
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String buscaNumeroRegistro(Integer pessoa,
			Integer[] tiposQualificacao) throws ApplicationBusinessException {
		return getRapQualificacaoDAO().buscaNumeroRegistro(pessoa,
				tiposQualificacao);
	}

	@Override
	public RapServidores obterSubstituto(Short vinculoSubstituto,
			Integer matriculaSubstituto) {
		return getRapServidoresDAO().obterSubstituto(vinculoSubstituto,
				matriculaSubstituto);
	}

	/**
	 * Procura profissional substituto para os parâmetros fornecidos.<br>
	 * Substitutos são os profissionais que tem escala para a especialidade e
	 * convênio no dia fornecido.
	 * 
	 * @param especialidadeId
	 *            id da especialidade
	 * @param convenioId
	 *            id do convênio
	 * @param data
	 * @return matriculas(RapServidores) dos profissionais substitutos
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<RapServidores> pesquisarProfissionaisSubstitutos(
			Short especialidadeId, Short convenioId, Date data,
			Object substitutoPesquisaLOV, Integer medicinaId,
			Integer odontologiaId) throws ApplicationBusinessException {
		return getRapServidoresDAO().pesquisarProfissionaisSubstitutos(
				especialidadeId, convenioId, data, substitutoPesquisaLOV,
				medicinaId, odontologiaId);
	}

	@Override
	public List<RapServidores> pesquisarRapServidoresPorCodigoDescricao(
			Object objPesquisa) {
		return getServidorON().pesquisarRapServidoresPorCodigoDescricao(
				objPesquisa);
	}

	/**
	 * @param objPesquisa
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidoresVOPorCodigoDescricao(java.lang.Object)
	 */
	@Override
	public List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao(
			Object objPesquisa) {
		return this.getServidorON().pesquisarRapServidoresVOPorCodigoDescricao(
				objPesquisa);
	}

	/**
	 * @param id
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#obterRapServidor(br.gov.mec.aghu.model.RapServidoresId)
	 */
	@Override
	public RapServidores obterRapServidor(RapServidoresId id) {
		return this.getServidorON().obterRapServidor(id);
	}

	/**
	 * @param id
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#obterRapPessoasFisicas(br.gov.mec.aghu.model.RapServidoresId)
	 */
	@Override
	public RapPessoasFisicas obterRapPessoasFisicas(RapServidoresId id) {
		return this.getServidorON().obterRapPessoasFisicas(id);
	}
	
	@Override
	public RapPessoasFisicas obterRapPessoasFisicas(Integer codigo) {
		return this.getRapPessoasFisicasDAO().obterPorChavePrimaria(codigo);
	}
	

	/**
	 * @param codigoPessoa
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidoresPorCodigoPessoa(java.lang.Integer)
	 */
	@Override
	public List<RapServidores> pesquisarRapServidoresPorCodigoPessoa(
			Integer codigoPessoa) {
		return this.getServidorON().pesquisarRapServidoresPorCodigoPessoa(
				codigoPessoa);
	}

	/**
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#buscaConselhosProfissionalServidor(java.lang.Integer,
	 *      java.lang.Short)
	 */
	@Override
	public List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidor(
			Integer matricula, Short vinculo) {
		return getRapServidoresDAO().pesquisarConselhoProfissional(matricula,
				vinculo);
	}

	@Override
	public List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidorAtivoInativo(
			Integer matricula, Short vinculo) {
		return getRapServidoresDAO().pesquisarConselhoProfissionalAtivoInativo(matricula, vinculo);
	}		
	
	@Override
	public List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidor(
			Integer matricula, Short vinculo, Boolean testaDataFimVinculo) {
		return getRapServidoresDAO().pesquisarConselhoProfissional(matricula,
				vinculo, testaDataFimVinculo);
	}

	/**
	 * @param param
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidores(java.lang.Object)
	 */
	@Override
	public List<RapServidores> pesquisarRapServidores(Object param) {
		return this.getServidorON().pesquisarRapServidores(param);
	}

	/**
	 * @param param
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#pesquisarRapServidoresCount(java.lang.Object)
	 */
	@Override
	public Long pesquisarRapServidoresCount(Object param) {
		return this.getServidorON().pesquisarRapServidoresCount(param);
	}

	/**
	 * @param servidor
	 * @return
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#obterServidor(br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public RapServidores obterServidor(RapServidores servidor) {
		return this.getServidorON().obterServidor(servidor);
	}
	
	@Override
	public RapServidores obterServidorPessoa(RapServidores servidor) {
		return this.getServidorON().obterServidorPessoa(servidor);
	}

	/**
	 * @param chefia
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.registrocolaborador.business.ServidorON#incluirChefia(br.gov.mec.aghu.model.RapChefias)
	 */
	@Override
	@Secure("#{s:hasPermission('colaborador','alterar')}")
	public void incluirChefia(RapChefias chefia)
			throws ApplicationBusinessException {
		this.getServidorON().incluirChefia(chefia);
	}

	/**
	 * Chama validações para averiguar se o vínculo do servidor ainda é válido.
	 * ATENÇÃO: NÃO colocar restrição de segurança neste método pois ele é
	 * chamado no momento de login, quando o contexto de segurança ainda não
	 * está pronto.
	 * 
	 * @param servidor
	 *            Servidor a ser verificado
	 * @throws ApplicationBusinessException
	 *             Em caso de alguma validação de negócio falhar
	 */
	@Override
	public void verificarVinculoServidor(RapServidores servidor)
			throws ApplicationBusinessException {
		getServidorRN().verificarVinculoServidor(servidor);
	}

	/**
	 * Chama validações para averiguar se o vínculo do servidor ainda é válido.
	 * ATENÇÃO: NÃO colocar restrição de segurança neste método pois ele é
	 * chamado no momento de login, quando o contexto de segurança ainda não
	 * está pronto.
	 * 
	 * @param login
	 *            Usuário do AGHU a ser verificado
	 * @throws ApplicationBusinessException
	 *             Em caso de alguma validação de negócio falhar
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void verificarVinculoServidor(String login)
			throws ApplicationBusinessException {
		this.verificarVinculoServidor(this.obterServidorAtivoPorUsuario(login));
	}

	@Override
	public List<RapPessoasFisicas> suggestionPessoasFisicasPorCPFNome(
			final Object objPesquisa) {
		return getRapPessoasFisicasDAO().suggestionPessoasFisicasPorCPFNome(
				objPesquisa);
	}

	@Override
	public Long suggestionPessoasFisicasPorCPFNomeCount(final Object objPesquisa) {
		return getRapPessoasFisicasDAO()
				.suggestionPessoasFisicasPorCPFNomeCount(objPesquisa);
	}

	@Override
	public List<RapServidores> pesquisarServidorAtivoPorVinculos(
			Object paramPesquisa, List<Short> vinculos) {
		return getRapServidoresDAO().pesquisarServidorAtivoPorVinculos(
				paramPesquisa, vinculos);
	}

	@Override
	public RapServidores buscaServidor(final RapServidoresId servidorId) {
		return getRapServidoresDAO().obter(servidorId);
	}

	@Override
	public List<RapServidores> pesquisarServidorCertificadoDigital(
			final Object paramPesquisa, final FccCentroCustos centroCusto) {
		return this.getRapServidoresDAO().pesquisarServidorCertificadoDigital(
				paramPesquisa, centroCusto);
	}

	@Override
	public ConselhoProfissionalServidorVO buscaConselhoProfissional(
			Integer matricula, Short vinCodigo) {
		return getRapServidoresDAO().obterConselhoProfissional(matricula,
				vinCodigo);
	}
	
	@Override
	public List<VRapServidorConselho> pesquisarConselhoPorMatriculaVinculo(Integer matricula, Short vinculo) {
		return getVRapServidorConselhoDAO().pesquisarConselhoPorMatriculaVinculo(matricula,vinculo);
	}

	/**
	 * 
	 * @param parametro
	 * @param first
	 * @param max
	 * @param order
	 * @param asc
	 * @return
	 */
	@Override
	public List<RapServidores> pesquisarCompradoresAtivos(Object parametro,
			Integer first, Integer max, String order, Boolean asc) {
		return getRapServidoresDAO().pesquisarCompradoresAtivos(parametro,
				first, max, order, asc);
	}

	@Override
	public Long pesquisarServidorCount(RapRamalTelefonico ramalTelefonico) {
		return this.getRapServidoresDAO().pesquisarServidorCount(
				ramalTelefonico);
	}

	/**
	 * 
	 * @return
	 */
	protected VRapPessoaServidorDAO getVRapPessoaServidorDAO() {
		return vRapPessoaServidorDAO;
	}

	@Override
	public List<RapServidores> listarServidoresComPessoaFisicaPorNome(
			String parametro) {
		return getRapServidoresDAO().obterServidoresComPessoaFisicaPorNome(
				parametro);
	}

	@Override
	public String obterNomePessoaServidor(Short vinCodigo, Integer matricula) {
		return getVRapPessoaServidorDAO().obterNomePessoaServidor(vinCodigo,
				matricula);
	}

	protected RapRamaisCentroCustoDAO getRapRamaisCentroCustoDAO() {
		return rapRamaisCentroCustoDAO;
	}

	@Override
	public String obterNomePessoaServidor(Integer cod, Integer matricula) {
		return this.getVRapPessoaServidorDAO().obterNomePessoaServidor(cod,
				matricula);
	}

	/**
	 * Efetua as pesquisas de servidores ativos/ programados por Vínculo e
	 * matrícula; descrição do vínculo ou nome
	 * 
	 * @param pesquisa
	 *            Vínculo e matrícula; descrição do vínculo ou nome
	 * @return List<RapServidores> (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#pesquisarServidoresPorVinculoMatriculaDescVinculoNome(java.lang.Object)
	 */
	@Override
	public List<RapServidores> pesquisarServidoresPorVinculoMatriculaDescVinculoNome(
			Object objetoPesquisa) {
		return getVRapPessoaServidorDAO()
				.pesquisarServidoresPorCodigoDescricao(objetoPesquisa);
	}

	@Override
	public Integer pesquisarServidoresPorVinculoMatriculaDescVinculoNomeCount(
			Object objetoPesquisa) {
		return getVRapPessoaServidorDAO()
				.pesquisarServidoresPorCodigoDescricaoCount(objetoPesquisa);
	}

	@Override
	public List<RapServidores> pesquisarServidoresPorCodigoOuDescricao(
			Object objetoPesquisa) {
		return getVRapPessoaServidorDAO()
				.pesquisarServidoresPorCodigoOuDescricao(objetoPesquisa);
	}
	
	@Override
	public Long pesquisarServidoresPorCodigoOuDescricaoCount(
			Object objetoPesquisa) {
		return getVRapPessoaServidorDAO().pesquisarServidoresPorCodigoOuDescricaoCount(objetoPesquisa);
	}
	
	@Override
	public RapServidores obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
			Integer matricula, Short vinCodigo) {
		return getRapServidoresDAO()
				.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
						matricula, vinCodigo);
	}

	@Override
	public RapServidores obterServidorAtivoPorCpf(Long cpf) {
		return getRapServidoresDAO().obterServidorAtivoPorCpf(cpf);
	}

	@Override
	public List<RapServidores> pesquisarServidorSuggestion(Object servidor,
			String emaExaSigla, Integer emaManSeq) {
		return getRapServidoresDAO().pesquisarServidorSuggestion(servidor,
				emaExaSigla, emaManSeq);
	}

	@Override
	public List<RapServidoresVO> pesquisarServidor(final Object strPesq,
			final Short tipoCBO) throws BaseException {
		return getRapServidoresDAO().pesquisarServidor(strPesq, tipoCBO);
	}

	@Override
	public Long pesquisarServidorCount(Object strPesq, final Short tipoCBO)
			throws BaseException {
		return getRapServidoresDAO().pesquisarServidorCount(strPesq, tipoCBO);
	}

	@Override
	public List<RapServidoresVO> pesquisarServidorPorCbo(Object servidor,
			Short tipoCBO, List<String> cbos) {
		return getRapServidoresDAO().pesquisarServidorPorCBO(servidor, tipoCBO, cbos);
	}

	@Override
	public Long pesquisarServidorPorCboCount(Object servidor, Short tipoCBO,
			List<String> cbos) {
		return getRapServidoresDAO().pesquisarServidorPorCBOCount(servidor, tipoCBO, cbos);
	}
	
	
	@Override
	public RapServidoresVO obterServidorVO(final Integer pesCodigo,
			final Integer matricula, final Short vinCodigo, final Short tipoCBO)
			throws BaseException {
		return getRapServidoresDAO().obterServidorVO(pesCodigo, matricula,
				vinCodigo, tipoCBO);
	}

	@Override
	public List<RapServidores> pesquisarServidorPorSituacaoAtivo(Object strPesq)
			throws BaseException {
		return getRapServidoresDAO().pesquisarServidorPorSituacaoAtivo(strPesq);
	}

	@Override
	public List<RapServidores> pesquisarServidorPorSituacaoAtivoComUsuario(Object strPesq)
			throws BaseException {
		return getRapServidoresDAO().pesquisarServidorPorSituacaoAtivoComUsuario(strPesq);
	}

	@Override
	public List<RapServidores> pesquisarServidores(Object strPesq) {
		return getRapServidoresDAO().pesquisarServidores(strPesq);
	}

	@Override
	public Long pesquisarServidoresCount(Object strPesq) {
		return getRapServidoresDAO().pesquisarServidoresCount(strPesq);
	}

	@Override
	public RapServidores obterServidorPeloProntuarioPeloVinculoEMatricula(
			Integer prontuario, Short vinCodigo, Integer matricula) {
		return getRapServidoresDAO()
				.obterServidorPeloProntuarioPeloVinculoEMatricula(prontuario,
						vinCodigo, matricula);
	}

	/**
	 * @dbtables RapServidores select
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	@Override
	public List<RapServidores> pesquisarRapServidores(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer matricula, Integer vinculo, String nome) {
		return getRapServidoresDAO().pesquisarRapServidores(firstResult,
				maxResults, orderProperty, asc, matricula, vinculo, nome);
	}

	/**
	 * @dbtables RapServidores select
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	@Override
	public Long pesquisarRapServidoresCount(Integer matricula, Integer vinculo,
			String nome) {
		return getRapServidoresDAO().pesquisarRapServidoresCount(matricula,
				vinculo, nome);
	}

	@Override
	public RapServidores atualizarRapServidores(RapServidores rapServidor) {
		return getRapServidoresDAO().atualizar(rapServidor);
	}

	@Override
	public List<String> pesquisarServidorAtivo(Object paramPesquisa) {
		return getRapServidoresDAO().pesquisarServidorAtivo(paramPesquisa);
	}

	/**
	 * obj[0] - rap_pessoas_fisicas.nome obj[1] - rap_pessoas_fisicas.sexo
	 * obj[2] - rap_vinculos.titulo_masculino obj[3] -
	 * rap_vinculos.titulo_feminino obj[4] -
	 * rap_conselhos_profissionais.titulo_masculino obj[5] -
	 * rap_conselhos_profissionais.titulo_feminino
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @return
	 */
	@Override
	public Object[] obtemDadosServidor(Short vinCodigo, Integer matricula) {
		return getRapServidoresDAO().obtemDadosServidor(vinCodigo, matricula);
	}

	/**
	 * Responsavel por buscar uma lista de RapServidores que contenham
	 * <b>nomeServidor</b> como parte do nome.<br>
	 * Busca migrada conforme view:<br>
	 * 
	 * ORADB view AGH.V_RAP_SERV_SOL_EXME.<br>
	 * 
	 * Joins foram adaptados devido ao nao utilizacao das tabelas CSE_* que
	 * forma trocadas pra utilizacao de CSC_*.<br>
	 * 
	 * <b>Todas a regras do AGH atualmente foram migradas.</b><br>
	 * 
	 * <b>Melhoria Solicitada:</b> Regras pra Conselho e Qualificacao. Espera
	 * por definicao com Arquitetura.<br>
	 * 
	 * @param nomeServidor
	 * @param diasServidorFimVinculoPermitidoSolicitarExame
	 *            default 365 dias.
	 * @return
	 */
	@Override
	public List<RapServidores> pesquisarServidoresSolicitacaoExame(
			String nomeServidor,
			Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return getRapServidoresDAO().pesquisarServidoresSolicitacaoExame(
				nomeServidor, diasServidorFimVinculoPermitidoSolicitarExame);
	}

	@Override
	public List<RapServidores> pesquisarServidoresExameView(Short vinculo,
			Integer matricula,
			Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return getRapServidoresDAO().pesquisarServidoresExameView(vinculo,
				matricula, diasServidorFimVinculoPermitidoSolicitarExame);
	}

	@Override
	public List<RapServidores> pesquisarServidoresSolicitacaoExame(
			Short vinculo, Integer matricula,
			Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return getRapServidoresDAO().pesquisarServidoresSolicitacaoExame(
				vinculo, matricula,
				diasServidorFimVinculoPermitidoSolicitarExame);
	}

	@Override
	public List<RapQualificacao> pesquisarQualificacoesSolicitacaoExameSemPermissao(
			Short vinculo, Integer matricula,
			Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return getRapServidoresDAO()
				.pesquisarQualificacoesSolicitacaoExameSemPermissao(vinculo,
						matricula,
						diasServidorFimVinculoPermitidoSolicitarExame);
	}

	/**
	 * Obtém RapServidor por Vínculo ou Matrícula
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@Override
	public RapServidores obterRapServidorPorVinculoMatricula(Integer matricula,
			Short vinCodigo) {
		return getRapServidoresDAO().obterRapServidorPorVinculoMatricula(
				matricula, vinCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('transcreverResultadoExames','pesquisarServidorLiberaExames')}")
	public List<RapServidores> pesquisarServidorLiberaExames(
			final Object paramPesquisa) {
		return getRapServidoresDAO().pesquisarServidorLiberaExames(
				paramPesquisa, "realizarExames");
	}

	@Override
	public RapServidores pesquisarServidorLiberaExames(final Integer matricula,
			final Short vinCodigo) {
		return getRapServidoresDAO().pesquisarServidorLiberaExames(matricula,
				vinCodigo, "realizarExames");
	}

	@Override
	public RapServidores buscaVRapServSolExme(final Short vinculo,
			final Integer matricula,
			final Integer diasServidorFimVinculoPermitidoSolicitarExame,
			final String numeroConselho, final String siglaConselho)
			throws ApplicationBusinessException {
		return getRapServidoresDAO().buscaVRapServSolExme(vinculo, matricula,
				diasServidorFimVinculoPermitidoSolicitarExame, numeroConselho,
				siglaConselho);
	}

	@Override
	public List<RapQualificacao> pesquisarQualificacoes(
			RapPessoasFisicas pessoaFisica) {
		return getRapQualificacaoDAO().pesquisarQualificacoes(pessoaFisica);
	}

	@Override
	public RapQualificacao obterRapQualificacaoPorServidor(
			RapServidores servidor) {
		return getRapQualificacaoDAO()
				.obterRapQualificacaoPorServidor(servidor);
	}

	@Override
	public List<RapQualificacao> pesquisarQualificacaoConselhoProfissionalPorServidor(
			Integer serMatricula, Short serVinCodigo) {
		return getRapQualificacaoDAO()
				.pesquisarQualificacaoConselhoProfissionalPorServidor(
						serMatricula, serVinCodigo);
	}
	
	@Override
	public List<RapConselhosProfissionais> listarRapConselhosProfissionaisPorPessoaFisica(
			RapPessoasFisicas pessoaFisica) {
		return getRapQualificacaoDAO()
				.listarRapConselhosProfissionaisPorPessoaFisica(pessoaFisica);
	}

	@Override
	public List<RapServidoresVO> listarRapConselhosProfissionaisPorServidor(
			List<RapServidoresId> servidores) {
		return getRapQualificacaoDAO()
				.listarRapConselhosProfissionaisPorServidor(servidores);
	}

	@Override
	public List<RapServidores> pesquisarServidoresCompradorAtivo(
			Integer matricula, Short vinCodigo, Integer codigoOcupacao) {
		return getVRapPessoaServidorDAO().pesquisarServidoresCompradorAtivo(
				matricula, vinCodigo, codigoOcupacao);
	}

	@Override
	public List<RapServidores> pesquisarServidoresCompradorAtivoPorMatriculaNome(
			Object objPesquisa) {
		return getVRapPessoaServidorDAO()
				.pesquisarServidoresCompradorAtivoPorMatriculaNome(objPesquisa);
	}

	@Override
	public List<RapServidores> pesquisarServidoresCompradorPorMatriculaNome(
			Object objPesquisa) {
		return getVRapPessoaServidorDAO()
				.pesquisarServidoresCompradorPorMatriculaNome(objPesquisa);
	}

	@Override
	public String obterNomePessoaServidorPorAelExameApItemSolics(
			final Long luxSeq) {
		return getVRapPessoaServidorDAO()
				.obterNomePessoaServidorPorAelExameApItemSolics(luxSeq);
	}

	@Override
	public boolean isServidorCompradorAtivoPorVinculoMatricula(Short vinculo,
			Integer matricula) {
		return getVRapPessoaServidorDAO()
				.isServidorCompradorAtivoPorVinculoMatricula(vinculo, matricula);
	}

	@Override
	public RapDependentes obterDependentePeloPacCodigoPeloVinculoEMatricula(
			Integer codigo, Short vinCodigo, Integer matricula) {
		return getRapDependentesDAO()
				.obterDependentePeloPacCodigoPeloVinculoEMatricula(codigo,
						vinCodigo, matricula);
	}

	@Override
	public List<RapDependentes> listarDependentesPorCodigoPaciente(
			Integer pacCodigo) {
		return getRapDependentesDAO().listarDependentesPorCodigoPaciente(
				pacCodigo);
	}

	@Override
	public RapDependentes atualizarRapDependentes(RapDependentes rapDependentes) {
		return getRapDependentesDAO().atualizar(rapDependentes);
	}

	@Override
	public List<RapPessoaTipoInformacoes> listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(
			final Integer pesCodigo, final Short tiiSeq) {
		return getRapPessoaTipoInformacoesDAO()
				.listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(pesCodigo,
						tiiSeq);
	}

	/**
	 * 
	 * ORADB: FATK_CAP_UNI.RN_CAPC_CBO_PROC_RES.c_get_valor_cbo
	 * 
	 * @param pesCodigo
	 * @param tipoInf
	 * @param tipoInfSec
	 * @return
	 */
	@Override
	public List<CursorBuscaCboVO> listarPessoaTipoInformacoes(
			final Integer pesCodigo, final Date dtRealizado,
			List<Short> resultSeqTipoInformacaoShort, Date ultimoDiaMes) {
		return getRapPessoaTipoInformacoesDAO().listarPessoaTipoInformacoes(
				pesCodigo, dtRealizado, resultSeqTipoInformacaoShort,
				ultimoDiaMes);
	}

	@Override
	public boolean existePessoaFisicaComTipoInformacao(Integer pesCodigo,
			Short[] codTipoInformacoes) {
		return getRapPessoaTipoInformacoesDAO()
				.existePessoaFisicaComTipoInformacao(pesCodigo,
						codTipoInformacoes);
	}

	@Override
	public String buscaCbo(Integer matricula, Short vinCodigo,
			Short[] codTipoInformacoes) {
		return getRapPessoaTipoInformacoesDAO().buscaCbo(matricula, vinCodigo,
				codTipoInformacoes);

	}

	/**
	 * @deprecated usar {@link #listarPessoasFisicasPorCodigoPaciente(Integer)}
	 * @param pacCodigo
	 * @return
	 */
	@Override
	public List<RapPessoasFisicas> obterPessoaFisicaPorCodigoPaciente(
			Integer pacCodigo) {
		return getRapPessoasFisicasDAO().obterPessoaFisicaPorCodigoPaciente(
				pacCodigo);
	}

	@Override
	public List<RapPessoasFisicas> listarPessoasFisicasPorCodigoPaciente(
			Integer pacCodigo) {
		return getRapPessoasFisicasDAO().listarPessoasFisicasPorCodigoPaciente(
				pacCodigo);
	}

	@Override
	public void persistirRapPessoasFisicas(RapPessoasFisicas rapPessoasFisicas) {
		getRapPessoasFisicasDAO().persistir(rapPessoasFisicas);
	}

	@Override
	public Long pesquisarPessoasFisicasPorNacionalidadeCount(
			AipNacionalidades nacionalidade) {
		return this.getRapPessoasFisicasDAO()
				.pesquisarPessoasFisicasPorNacionalidadeCount(nacionalidade);
	}

	@Override
	public List<VFatProfRespDcsVO> pesquisarVFatProfRespDcsVO(
			final String[] sigla, final Object filtros, final boolean isLoad) {
		return getRapTipoQualificacaoDAO().pesquisarVFatProfRespDcsVO(sigla,
				filtros, isLoad);
	}

	@Override
	public Long pesquisarVFatProfRespDcsVOCount(final String[] sigla,
			final Object filtros) {
		return getRapTipoQualificacaoDAO().pesquisarVFatProfRespDcsVOCount(
				sigla, filtros);
	}

	@Override
	public List<RapServidoresVO> pesquisarRapServidoresPorUnidFuncESala(
			Object param, Short unfSeq, Short seqp) {
		return getRapServidoresDAO().pesquisarRapServidoresPorUnidFuncESala(
				param, unfSeq, seqp);
	}

	@Override
	public List<RapServidores> pesquisarServidoresVinculados(Object servidor)
			throws BaseException {
		return getRapServidoresDAO().pesquisarServidoresVinculados(servidor);
	}

    @Override
    public RapServidores obterServidorComPessoaFisicaByUsuario(String login){
        return getRapServidoresDAO().obterServidorComPessoaFisicaByUsuario(login);
    }

    @Override
	public VRapServidorConselho obterVRapServidorConselhoPeloId(int matricula,
			short vinCodigo, String sigla) {
		return getVRapServidorConselhoDAO().obterVRapServidorConselhoPeloId(
				matricula, vinCodigo, sigla);
	}

	@Override
	public Long pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmerCount(
			final Object strPesquisa, final Short unfSeq) {
		return getVRapServidorConselhoDAO()
				.pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmerCount(
						strPesquisa, unfSeq);
	}

	@Override
	public List<VRapServidorConselho> pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmer(
			final Object strPesquisa, final Short unfSeq) {
		return getVRapServidorConselhoDAO()
				.pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmer(
						strPesquisa, unfSeq);
	}

	@Override
	public Long obterVRapServidorConselhoExamePeloId(int matricula,
			short vinCodigo, String emaExaSigla, Integer emaExaManSeq) {
		return getVRapServidorConselhoDAO()
				.obterVRapServidorConselhoExamePeloId(matricula, vinCodigo,
						emaExaSigla, emaExaManSeq);
	}

	@Override
	public List<VRapServidorConselhoVO> obterVRapServidorConselhoVO(Long luxSeq) {
		return getVRapServidorConselhoDAO().obterVRapServidorConselhoVO(luxSeq);
	}

	@Override
	public Boolean validarPermissaoAmbulatorioItensPorServidor(String login,
			DominioAnamneseEvolucao dominio) {
		return getCseCategoriaPerfilDAO()
				.validarPermissaoAmbulatorioItensPorServidor(login, dominio);
	}

	protected CseCategoriaPerfilDAO getCseCategoriaPerfilDAO() {
		return cseCategoriaPerfilDAO;
	}

	protected VRapServidorConselhoDAO getVRapServidorConselhoDAO() {
		return vRapServidorConselhoDAO;
	}

	protected CsePerfilProcessoLocalDAO getCsePerfilProcessoLocalDAO() {
		return csePerfilProcessoLocalDAO;
	}

	protected CsePerfilProcessosDAO getCsePerfilProcessosDAO() {
		return csePerfilProcessosDAO;
	}

	@Override
	public List<CsePerfilProcessoLocal> pesquisarPerfisProcessoLocal(
			DominioSimNao indConsulta, DominioSituacao indSituacao,
			Short paramSeqProcConsPol, Integer codigoCentroCustoLotacao) {
		return getCsePerfilProcessoLocalDAO().pesquisarPerfisProcessoLocal(
				indConsulta, indSituacao, paramSeqProcConsPol,
				codigoCentroCustoLotacao);
	}

	@Override
	public List<CsePerfilProcessos> pesquisarPerfisProcessos(
			Boolean indConsulta, DominioSituacao indSituacao,
			Short paramSeqProcConsPol) {
		return getCsePerfilProcessosDAO().pesquisarPerfisProcesso(indConsulta,
				indSituacao, paramSeqProcConsPol);
	}

	protected RarCandidatoProgramaDAO getRarCandidatoProgramaDAO() {
		return rarCandidatoProgramaDAO;
	}

	protected RarProgramaDuracaoDAO getRarProgramaDuracaoDAO() {
		return rarProgramaDuracaoDAO;
	}

	@Override
	public RarCandidatoPrograma obterRarCandidatoProgramaPorServidorData(
			RapServidores rapServidor, Date data) {
		return getRarCandidatoProgramaDAO()
				.obterRarCandidatoProgramaPorServidorData(rapServidor, data);
	}

	@Override
	public RarCandidatoPrograma obterRarCandidatoProgramaPorCandidatoData(
			RarCandidatos candidatos, Date data) {
		return getRarCandidatoProgramaDAO()
				.obterRarCandidatoProgramaPorCandidatoData(candidatos, data);
	}

	@Override
	public RarCandidatoPrograma obterRarCandidatoProgramaPorProgramaCandidato(
			RarCandidatos candidato, RarPrograma programa) {
		return getRarCandidatoProgramaDAO()
				.obterRarCandidatoProgramaPorProgramaCandidato(candidato,
						programa);
	}

	@Override
	public RarCandidatoPrograma obterUltimoRarCandidatoProgramaPorCandidato(
			RarCandidatos candidatos) {
		return getRarCandidatoProgramaDAO()
				.obterUltimoRarCandidatoProgramaPorCandidato(candidatos);
	}

	@Override
	public RarCandidatos obterRarCandidatoPorChavePrimaria(Integer cndSeq) {
		return getRarCandidatosDAO().obterPorChavePrimaria(cndSeq);
	}

	@Override
	public RarProgramaDuracao obterRarProgramaDuracaoPorRarProgramaAtualDataInicio(
			RarPrograma programa, Date dataInicio, Boolean utilizaSysDate) {
		return getRarProgramaDuracaoDAO()
				.obterRarProgramaDuracaoPorRarProgramaAtualDataInicio(programa,
						dataInicio, utilizaSysDate);
	}

	@Override
	public List<RapServidores> pesquisarRapServidores() {
		return getRapServidoresDAO().listarTodosServidores();
	}

	@Override
	public Long pesquisarRapServidoresCount() {
		return getRapServidoresDAO().pesquisarServidoresCount();
	}

	@Override
	public Long pesquisarServidorPorSituacaoAtivoCount(Object strPesq)
			throws BaseException {
		return getRapServidoresDAO().pesquisarServidorPorSituacaoAtivoCount(
				strPesq);
	}

	@Override
	public Integer pesquisarServidoresSolicitacaoExameCount(
			String nomeServidor,
			Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return getRapServidoresDAO().pesquisarServidoresSolicitacaoExameCount(
				nomeServidor, diasServidorFimVinculoPermitidoSolicitarExame);
	}

	@Override
	public List<RapServidores> pesquisarServidorPorVinculo(Object servidor) {
		return getRapServidoresDAO().pesquisarServidorPorVinculo(servidor);
	}

	@Override
	public List<RapServidores> pesquisarServidorPorMatriculaNome(Object servidor) {
		return getRapServidoresDAO()
				.pesquisarServidorPorMatriculaNome(servidor);
	}

	@Override
	public Long pesquisarServidorPorVinculoCount(Object servidor) {
		return getRapServidoresDAO().pesquisarServidorPorVinculoCount(servidor);
	}

	@Override
	public Long pesquisarServidorPorMatriculaNomeCount(Object servidor) {
		return getRapServidoresDAO().pesquisarServidorPorMatriculaNomeCount(
				servidor);
	}

	@Override
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula,
			boolean verificaDataFimVinculo) {
		return getRapServidoresDAO().buscarNroRegistroConselho(vinCodigo,
				matricula, verificaDataFimVinculo);
	}

	@Override
	public List<VRapServCrmAelVO> pesquisarViewRapServCrmAelVO(
			VRapServCrmAelVO filtro, String[] parametros) {
		return getRapServidoresDAO().pesquisarViewRapServCrmAelVO(filtro,
				parametros);
	}
	
	@Override
	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisRegistroCirurgiaRealizada(Object strPesquisa, Short unfSeq) {
		return getVRapServidorConselhoDAO().pesquisarProfissionaisRegistroCirurgiaRealizada(strPesquisa, unfSeq);
	}

	@Override
	public Long pesquisarProfissionaisRegistroCirurgiaRealizadaCount(Object strPesquisa, Short unfSeq) {
		return getVRapServidorConselhoDAO().pesquisarProfissionaisRegistroCirurgiaRealizadaCount(strPesquisa, unfSeq);
	}
	
	@Override
	public List<RapServidores> pesquisarServidoresParaSuggestion(Object objPesquisa) {
		return getRapServidoresDAO().pesquisarServidoresParaSuggestion(objPesquisa, 0, 100, null, true);
	}

	@Override
	public Long pesquisarServidoresParaSuggestionCount(Object objPesquisa) {
		return getRapServidoresDAO().pesquisarServidoresParaSuggestionCount(objPesquisa);
	}
	
	@Override
	public DescricaoControlePacienteVO buscarDescricaoAnotacaoControlePaciente(
			Short vinCodigo, Integer matricula) {
		return getRapServidoresDAO().buscarDescricaoAnotacaoControlePaciente(
				vinCodigo, matricula);
	}

	@Override
	public Boolean existeQualificacoesUsuarioSemNumeroRegistroConselho(
			DominioTipoQualificacao tipoQualificacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return this.getRapServidoresDAO()
				.existeQualificacoesUsuarioSemNumeroRegistroConselho(servidorLogado, tipoQualificacao);
	}

	@Override
	public List<RapServidores> pesquisarResidente(Object obj, Short vincFunc,
			Integer matricula) {
		return getVRapPessoaServidorDAO().pesquisarResidente(obj, vincFunc,
				matricula);
	}

	@Override
	public Long pesquisarResidenteCount(Object obj, Short vincFunc,
			Integer matricula) {
		return getVRapPessoaServidorDAO().pesquisarResidenteCount(obj,
				vincFunc, matricula);
	}

	@Override
	public List<ConselhoProfissionalServidorVO> buscaConselhosProfissionalServidorRegConselhoNotNull(
			Integer matricula, Short vinCodigo, DominioSituacao dominioSituacao) {
		return getRapServidoresDAO()
				.pesquisarConselhoProfissionalRegConselhoNotNull(matricula,
						vinCodigo, dominioSituacao);
	}

	@Override
	public String buscarNomeEquipePorPucSerMatricula(Integer serMatricula) {
		return getRapServidoresDAO().buscarNomeEquipePorPucSerMatricula(
				serMatricula);
	}

	@Override
	public List<RapServidores> pesquisarServidorPorSituacaoAtivoParaProcedimentos(
			Object servidor, Short unfSeq) throws BaseException {
		return this.getRapServidoresDAO().listarMbcConselho(servidor, unfSeq);
	}

	@Override
	public Integer pesquisarServidorPorSituacaoAtivoParaProcedimentosCount(
			Object servidor, Short unfSeq) throws BaseException {
		return this.getRapServidoresDAO().listarMbcConselhoCount(servidor,
				unfSeq);
	}

	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoNroRegNaoNuloListaSigla(
			List<String> listaSigla, Object objPesquisa) {
		return getVRapServidorConselhoDAO()
				.pesquisarServidorConselhoNroRegNaoNuloListaSigla(listaSigla,
						objPesquisa);
	}
	
	@Override
	public Long pesquisarServidorConselhoNroRegNaoNuloListaSiglaCount(List<String> listaSigla, Object objPesquisa) {
		return getVRapServidorConselhoDAO()
				.pesquisarServidorConselhoNroRegNaoNuloListaSiglaCount(listaSigla,
						objPesquisa);
	}

	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoNroRegNuloListaSigla(
			List<String> listaSigla, Object objPesquisa) {
		return getVRapServidorConselhoDAO()
				.pesquisarServidorConselhoNroRegNuloListaSigla(listaSigla,
						objPesquisa);
	}

	@Override
	public Long pesquisarServidorConselhoNroRegNuloListaSiglaCount(List<String> listaSigla, Object objPesquisa) {
		return getVRapServidorConselhoDAO()
				.pesquisarServidorConselhoNroRegNuloListaSiglaCount(listaSigla,
						objPesquisa);
	}
	
	
	@Override
	public List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoListaSiglaPorServidor(
			List<String> listaSigla, Integer serMatricula, Short serVinCodigo) {
		return getVRapServidorConselhoDAO()
				.pesquisarServidorConselhoListaSiglaPorServidor(listaSigla,
						serMatricula, serVinCodigo);
	}

	/**
	 * Busca o servidor de um AelExtratoItemSolicitacao pelos campos
	 * ISE_SOE_SEQ, ISE_SEQP e SITUACAO_ITEM_SOLICITACAO_CODIGO
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param codSit
	 * @return
	 */
	@Override
	public RapServidores buscarRapServidorDeAelExtratoItemSolicitacao(
			Integer iseSoeSeq, Short iseSeqp, String codSit) {
		return getRapServidoresDAO().buscaRapServidor(iseSoeSeq, iseSeqp,
				codSit);
	}

	@Override
	public List<RapServidores> obterServidorPorCPF(Long cpf) {
		return getRapServidoresDAO().obterServidorPorCPF(cpf);
	}

	@Override
	public Long buscaNroFuncionariosPorCentroCusto(FccCentroCustos centroCustos) {
		return this.getRapServidoresDAO().buscaNroFuncionariosPorCentroCusto(
				centroCustos);
	}

	@Override
	public Long buscaNroLoginsPorCentroCusto(FccCentroCustos centroCustos) {
		return this.getRapServidoresDAO().buscaNroLoginsPorCentroCusto(
				centroCustos);
	}

	@Override
	public Long buscaNroDependentesAbaixo7AnosPorCentroCusto(
			FccCentroCustos centroCusto) {
		return getRapDependentesDAO()
				.buscaNroDependentesAbaixo7AnosPorCentroCusto(centroCusto);
	}

	/**
	 * Retorna a qtde de rapServidores de acordo com a matricula ou nome passado
	 * por parametro
	 * 
	 * @dbtables RapServidores select count
	 * 
	 * @param responsavel
	 * @return qtde
	 */
	@Override
	public Long pesquisarResponsaveisCount(Object responsavel) {
		return getRapServidoresDAO().pesquisarResponsaveisCount(responsavel);
	}

	@Override
	public List<RapPessoasFisicas> listarRapPessoasFisicasPorConselhoProfissional(
			String strPesquisa, List<String> listaConselhoProfissional) {
		return getRapPessoasFisicasDAO()
				.listarRapPessoasFisicasPorConselhoProf(strPesquisa,
						listaConselhoProfissional);
	}

	@Override
	public Long listarRapPessoasFisicasPorConselhoProfCount(String strPesquisa,
			List<String> listaConselhoProfissional) {
		return getRapPessoasFisicasDAO()
				.listarRapPessoasFisicasPorConselhoProfCount(strPesquisa,
						listaConselhoProfissional);
	}

	@Override
	public String obterNomeEquipeProfissionalMonitorCirurgia(
			MbcProfCirurgias profCirurgias) {
		return getVRapPessoaServidorDAO()
				.obterNomeEquipeProfissionalMonitorCirurgia(profCirurgias);
	}

		
	@Override
	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaProcedimentoENotaConsumo(String strPesquisa, final Short unfSeq, List<String> listaConselhos, boolean agenda){
		return getVRapServidorConselhoDAO().pesquisarProfissionaisAgendaProcedimentoENotaConsumo(strPesquisa, unfSeq, listaConselhos, agenda);
	}
	
	@Override
	public Long pesquisarProfissionaisAgendaProcedimentoENotaConsumoCount(String strPesquisa, Short unfSeq, List<String> listaConselhos, boolean agenda) {
		return getVRapServidorConselhoDAO().pesquisarProfissionaisAgendaProcedimentoENotaConsumoCount(strPesquisa, unfSeq, listaConselhos, agenda);
	}	

	@Override
	public Long pesquisarProfissionaisAgendaProcedimentoCount(
			String strPesquisa, Short unfSeq) {
		return getVRapServidorConselhoDAO()
				.pesquisarProfissionaisAgendaProcedimentoCount(strPesquisa,
						unfSeq);
	}

	/**
	 * Verifica se servidor está no mesmo centro de custo
	 * 
	 * 
	 * @param pesquisa matricula e centro de custo aplicada
	 * @return true or false
	 */
	public boolean isServidorVinculadoCentroCusto(Short vinculo, Integer matricula, Integer cctCodAplicada) {
		return getVRapPessoaServidorDAO().isServidorVinculadoCentroCusto(vinculo, matricula, cctCodAplicada);
	}
	
	@Override
	public String obterChefeComprasPorMatriculaVinculo(int matricula, short vinculo) {
		return getVRapPessoaServidorDAO().obterChefeComprasPorMatriculaVinculo(matricula, vinculo);
	}

	@Override
	public RapPessoasFisicas obterChefeServicoComprasPorAghParametros() {
		return getRapPessoasFisicasDAO().obterChefeServicoComprasPorAghParametros();
	}
	
	@Override
	public RapPessoasFisicas obterRapPessoasFisicasPorServidor(RapServidoresId id) {
		return getRapPessoasFisicasDAO().obterRapPessoasFisicasPorServidor(id);
	}

	@Override
	public List<RapServidores> listarUsuariosNotificaveis(Integer codigoCentroCusto) {
		return this.getRapServidoresDAO().listarUsuariosNotificaveis(codigoCentroCusto);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	@Override
	public List<RapServidores> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome, Integer maxResults){
		return getRapServidoresDAO().pesquisarServidoresAtivos(vinCodigo, matricula, nome, maxResults);
	}
	
	@Override
	public Long pesquisarServidoresAtivosCount(Short vinCodigo, Integer matricula, String nome){
		return getRapServidoresDAO().pesquisarServidoresAtivosCount(vinCodigo, matricula, nome);
	}
	
	@Override
	public List<RapServidores> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome) {
		return getRapServidoresDAO().pesquisarMedicosEmergencia(vinCodigos, matriculas, nome);
	}
	
	@Override
	public List<RapServidores> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		return getRapServidoresDAO().pesquisarMedicosEmergencia(vinCodigos, matriculas, nome, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarMedicosEmergenciaCount(List<Short> vinCodigos, List<Integer> matriculas, String nome) {
		return getRapServidoresDAO().pesquisarMedicosEmergenciaCount(vinCodigos, matriculas, nome);
	}
	/**
	 * #36698 - Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	@Override
	public List<VRapServidorConselho> pesquisarServidoresConselho(final String strPesquisa,List<Integer> centroCusto){
		return getVRapServidorConselhoDAO().pesquisarServidoresConselho(strPesquisa, centroCusto);
	}
	/**
	 * #36698 - Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	@Override
	public Long pesquisarServidoresConselhoCount(final String strPesquisa,List<Integer> centroCusto){
		return getVRapServidorConselhoDAO().pesquisarServidoresConselhoCount(strPesquisa, centroCusto);
	}

	@Override
	public List<VRapServidorConselho> pesquisarServidoresConselhoPorSiglaCentroCusto(List<String> siglas, List<Integer> centroCusto, Integer maxResults) {
		return getVRapServidorConselhoDAO().pesquisarServidoresConselhoPorSiglaCentroCusto(siglas, centroCusto, maxResults);
	}

	@Override
	public Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(List<String> siglas, List<Integer> centroCusto) {
		return getVRapServidorConselhoDAO().pesquisarServidoresConselhoPorSiglaCentroCustoCount(siglas, centroCusto);
	}

	@Override
	public VRapServidorConselho obterServidorConselhoPorSiglaMatriculaVinculo(List<String> siglas, Integer matricula, Short vinculo) {
		return getVRapServidorConselhoDAO().obterServidorConselhoPorSiglaMatriculaVinculo(siglas, matricula, vinculo);
	}
	
	/**
	 * #36699 - Serviço para obter pessoa fisica
	 * @param vinculos
	 * @param matriculas
	 * @return
	 */
	@Override
	public List<RapPessoasFisicas> obterRapPessoasFisicasPorMatriculaVinculo(List<Short> vinculos, List<Integer> matriculas) {
		return getRapPessoasFisicasDAO().obterRapPessoasFisicasPorMatriculaVinculo(vinculos, matriculas);	
	}
	
	@Override
	public String buscarNomeResponsavelPorMatricula(Short codigo, Integer matricula) {
		return getRapPessoasFisicasDAO().buscarNomeResponsavelPorMatricula(codigo, matricula);
	}

	@Override
	public boolean verificarServidorHUCadastradoPorCpf(Long numCpf) {
		return getRapServidoresDAO().verificarServidorHUCadastradoPorCpf(numCpf);
	}

	@Override
	public boolean verificarServidorHUCadastradoPorRg(String numRg) {
		return getRapServidoresDAO().verificarServidorHUCadastradoPorRg(numRg);
	}

	public boolean usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas){
		return getRapServidoresDAO().usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(matricula, vinCodigo, siglas);
	}
	
	public VRapServidorConselho usuarioRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas){
		return getRapServidoresDAO().usuarioRelatoriosDefinitivos(matricula, vinCodigo, siglas);
	}
	
	@Override
	public List<RapServidores> pesquisarServidoresOrdenadoPorVinCodigo(Object strPesq) {
		return getRapServidoresDAO().pesquisarServidoresOrdenadoPorVinCodigo(strPesq);
	}
	
	public boolean isAtivo(RapServidores servidor, java.util.Date aData) {
		return this.getServidorON().isAtivo(servidor, aData);
	}

	public boolean isAtivo(RapServidores servidor) {
		return this.getServidorON().isAtivo(servidor);
	}
	
	/**
	 * Obtem Map com o conselho servidor através dos atributos da ID atraves de listas.
	 * 
	 * map key: matricula-vinculo-sigla
	 * 
	 * @return
	 */
	public Map<String, String>  obterMapRegistroVRapServidorConselho(List<Integer> listMatricula, List<Short> listVinCodigo, List<String> listSigla) {
		return getVRapServidorConselhoDAO().obterMapRegistroVRapServidorConselho(listMatricula, listVinCodigo, listSigla);
	}
	
	public RapVinculosDAO getRapVinculosDAO() {
		return rapVinculosDAO;
	}
	
	
	@Override
	public List<RapServidores> pesquisarServidorPorVinculoMatriculaNome(Object servidor) {
		return getRapServidoresDAO().pesquisarServidorPorVinculoMatriculaNome(servidor);
	}

	@Override
	public Long pesquisarServidorPorVinculoMatriculaNomeCount(Object servidor) {
		return getRapServidoresDAO().pesquisarServidorPorVinculoMatriculaNomeCount(servidor);

	}
	
	@Override
	public String obterRegistroVRapServidorConselhoPeloId(int matricula,
			short vinCodigo, String sigla) {

		return getVRapServidorConselhoDAO().obterRegistroVRapServidorConselhoPeloId(
				matricula, vinCodigo, sigla);

	}
	
	@Override
	public Long pesquisarServidorCertificadoDigitalCount(String paramPesquisa,
			FccCentroCustos centroCusto) {
		return this.getRapServidoresDAO().pesquisarServidorCertificadoDigitalCount(
				paramPesquisa, centroCusto);
	}


	@Override
	public RapServidoresVO pesquisarProfissionalPorServidor(RapServidores servidor) {
		return rapServidoresRN.pesquisarProfissionalPorServidor(servidor);
	}
	
	@Override
	public List<RapServidoresVO> pesquisarProfissionaisPorVinculoMatriculaNome(String strPesquisa, Integer count) {
		return rapServidoresRN.pesquisarProfissionaisPorVinculoMatriculaNome(strPesquisa, count);
	}
	
	@Override
	public Long pesquisarProfissionaisPorVinculoMatriculaNomeCount(String strPesquisa, Integer count) {
		return rapServidoresRN.pesquisarProfissionaisPorVinculoMatriculaNomeCount(strPesquisa, count);
	}
	@Override
	public Boolean verificarServidorExiste(Short codigo, Integer matricula) {		
		return rapServidoresDAO.verificarServidorExiste(codigo,matricula);
	}

	@Override
	public Object[] obterVRapPessoaServidorPorVinCodigoMatricula(
			Integer matricula, Short vinCodigo) {
		return vRapPessoaServidorDAO.obterVRapPessoaServidorPorVinCodigoMatricula(matricula, vinCodigo);
	}
	
	@Override
	public Servidor obterVRapPessoaServidorPorVinCodMatricula(Integer matricula, Short vinCodigo) {
		return rapServidoresRN.obterVRapPessoaServidorPorVinCodigoMatricula(matricula, vinCodigo);
	}

	@Override
	public List<Object[]> pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome) {
		return vRapPessoaServidorDAO.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(vinCodigos, cctCodigos, matriculaVinCodigo, nome);
	}

	@Override
	public Long pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(
			List<Short> vinCodigos, List<Integer> cctCodigos,
			Integer matriculaVinCodigo, String nome) {
		return vRapPessoaServidorDAO.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(vinCodigos, cctCodigos, matriculaVinCodigo, nome);
	}


	/**
	 * #39000 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		return servidorON.existeServidorCategoriaProfMedico(matricula, vinculo);
	}

	@Override
	public boolean verificarProfissionalPossuiCBOAnestesista(Short vinCodigo, Integer matricula, String[] siglasConselhos, String[] codigosTipoInformacoes) {
		return rapServidoresDAO.verificarProfissionalPossuiCBOAnestesista(vinCodigo, matricula, siglasConselhos, codigosTipoInformacoes);		
	}

	@Override
	public List<RapVinculos> pesquisarVinculoFuncionario(Object filtro) {
		return this.getRapVinculosDAO().pesquisarVinculosFuncionario(filtro);
	}

	@Override
	public Long pesquisarVinculoFuncionarioCount(Object filtro) {
		return this.getRapVinculosDAO().pesquisarVinculosFuncionarioCount(filtro);
	}
	
	public RapOcupacaoCargoDAO getRapOcupacaoCargoDAO() {
		return rapOcupacaoCargoDAO;
	}

	@Override
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargoFuncionario(Object filtro) {
		return this.getRapOcupacaoCargoDAO().pesquisarOcupacaoCargoFuncionario(filtro);
	}

	@Override
	public Long pesquisarOcupacaoCargoFuncionarioCount(Object filtro) {
		return this.getRapOcupacaoCargoDAO().pesquisarOcupacaoCargoFuncionarioCount(filtro);
	}
	

	public RapServidoresRN getRapServidoresRN() {
		return rapServidoresRN;
	}

	@Override
	public boolean isHospitalHCPA() {
		return getRapServidoresRN().isHospitalHCPA();
	}
	
	@Override
	public List<RapServidoresVO> pesquisarFuncionarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			RapServidores filtro) {
		return getRapServidoresRN().pesquisarFuncionarios(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	@Override
	public Long pesquisarFuncionariosCount(RapServidores filtro) {
		return getRapServidoresDAO().pesquisarFuncionariosCount(filtro);
	}

	@Override
	public VRapServidorConselho obterValoresPrescricaoMedica(Integer matricula,
			Short vinCodigo) {
		return vRapServidorConselhoDAO.obterValoresPrescricaoMedica(matricula, vinCodigo);
	}

	@Override
	public RapPessoalServidorVO obterValoresPrescricaoMedicaPessoaServidor(
			Integer matriculaServidorVerificado,
			Short vinCodigoServidorVerificado) {
		return vRapPessoaServidorDAO.obterValoresPrescricaoMedica(matriculaServidorVerificado, vinCodigoServidorVerificado);
	}
	@Override
	public List<RapServidoresProcedimentoTerapeuticoVO> obterListaServidoresAtivos(String descricao) {
		return this.rapServidoresDAO.obterListaServidoresAtivos(descricao);
	}
	
	@Override
	public List<RapServidores> obterServidorPorMatriculaVinculoOuNome(String parametro) {
		return rapServidoresDAO.obterListaServidorPorMatriculaVinculoOuNome(parametro);
	}
	
	@Override
	public RapPessoaTipoInformacoes obterRapPessoa(Integer codigo) {
		return rapPessoaTipoInformacoesDAO.obterRapPessoa(codigo);
	}
	
	@Override
	public Long obterListaServidoresAtivosCount(String descricao) {
		return this.rapServidoresDAO.obterListaServidoresAtivosCount(descricao);
	}
	
	@Override
	public List<RapServidores> pesquisarServidorPorVinculoOuMatriculaOuNome(String objPesquisa) {
		return this.rapServidoresDAO.pesquisarServidorPorVinculoOuMatriculaOuNome(objPesquisa);
	}

	@Override
	public Long pesquisarServidorPorVinculoOuMatriculaOuNomeCount(String objPesquisa) {
		return this.rapServidoresDAO.pesquisarServidorPorVinculoOuMatriculaOuNomeCount(objPesquisa);
	}

	@Override
	public List<RapServidores> listarTodosServidoresOrdernadosPorNome() {
		return rapServidoresDAO.listarTodosServidoresOrdernadosPorNome();
	}

	@Override
	public List<RapServidores> obterServidorPorAreaTecnicaAvaliacao(Integer ataSeq) {
		return this.rapServidoresDAO.obterServidorPorAreaTecnicaAvaliacao(ataSeq);
	}

	@Override
	public List<RapServidores> obterServidorTecnicoPorItemRecebimento(Integer recebimento, Integer itemRecebimento) {
		return this.rapServidoresDAO.obterServidorTecnicoPorItemRecebimento(recebimento, itemRecebimento);
	}

	
	@Override
	public RapPessoaTipoInformacoes obterTipoInformacao(Integer pesCodigo, Short seqTipoInf){
		return getRapPessoaTipoInformacoesDAO().obterTipoInformacao(pesCodigo, seqTipoInf);
	}

	@Override
	public Long obterServidorPorMatriculaVinculoOuNomeCount(String parametro) {
		return rapServidoresDAO.obterCountServidorPorMatriculaVinculoOuNome(parametro);
}

	@Override
	public List<CursorCurPreVO> obterCursorCurPreVO(Integer matricula, Short vinCodigo) {
		return rapServidoresDAO.obterCursorCurPreVO(matricula, vinCodigo);
	}

	@Override
	public List<CursorCurPreVO> obterCursorCurRapVO(Integer pMatricula, Short pVinCodigo) {
		return rapServidoresDAO.obterCursorCurRapVO(pMatricula, pVinCodigo);
	}

	@Override
	public List<VRapPessoaServidorVO> pesquisarPessoasServidores(String pesquisa) throws ApplicationBusinessException{
		return vRapPessoaServidorDAO.pesquisarPessoasServidores(pesquisa);
}
	
	@Override
	public Long pesquisarPessoasServidoresCount(String pesquisa) throws ApplicationBusinessException{
		return vRapPessoaServidorDAO.pesquisarPessoasServidoresCount(pesquisa);
	}
	
	@Override
	public Long obterCountProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho) {
		return rapPessoasFisicasDAO.obterCountProfissionaisHospital(profissional, vinculo, conselho);
	}
	
	@Override
	public List<ProfissionalHospitalVO> obterListaProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return rapPessoasFisicasDAO.obterListaProfissionaisHospital(profissional, vinculo, conselho, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public boolean permiteSerResponsSolicExame(Short vinCodigo, Integer matricula) throws ApplicationBusinessException {
		return servidorON.permiteSerResponsSolicExame(vinCodigo, matricula);
	}
	
	@Override
	public List<RapServidores> obterListaProfissionalPorMatriculaVinculoOuNome(String parametro) {
		return this.rapServidoresDAO.obterListaProfissionalPorMatriculaVinculoOuNome(parametro);
	}

	@Override
	public Long obterCountProfissionalPorMatriculaVinculoOuNome(String parametro) {
		return this.rapServidoresDAO.obterCountProfissionalPorMatriculaVinculoOuNome(parametro);
	}

	@Override
	public List<RapVinculos> obterListaVinculoPorCodigoOuDescricao(String parametro) {
		return this.rapVinculosDAO.obterListaVinculoPorCodigoOuDescricao(parametro);
	}

	@Override
	public Long obterCountVinculoPorCodigoOuDescricao(String parametro) {
		return this.rapVinculosDAO.obterCountVinculoPorCodigoOuDescricao(parametro);
	}

	@Override
	public List<RapConselhosProfissionais> obterListaConselhoPorCodigoOuSigla(String parametro) {
		return this.rapConselhosProfissionaisDAO.obterListaConselhoPorCodigoOuSigla(parametro);
	}

	@Override
	public Long obterCountConselhoPorCodigoOuSigla(String parametro) {
		return this.rapConselhosProfissionaisDAO.obterCountConselhoPorCodigoOuSigla(parametro);
	}
	
	@Override
	public List<RapServidores> listarServidoresComPessoaFisicaPorEquipe(
			String parametro, AghEquipes equipe) {
		return getRapServidoresDAO().obterServidoresComPessoaFisicaPorEquipe(
				parametro, equipe);
	}
	
}
