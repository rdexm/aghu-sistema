package br.gov.mec.aghu.financeiro.centrocusto.business;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.centrocusto.dao.FcuGrupoCentroCustosDAO;
import br.gov.mec.aghu.dominio.DominioArea;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacaoJn;
import br.gov.mec.aghu.model.RapChefias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecnicaAvaliacaoJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class CentroCustoON extends BaseBusiness {

@EJB
private CentroCustoJournalON centroCustoJournalON;

	private static final Log LOG = LogFactory.getLog(CentroCustoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FcuGrupoCentroCustosDAO fcuGrupoCentroCustosDAO;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	private PtmAreaTecAvaliacao ptmAreaTecAvaliacao;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private PerfilDAO perfilDAO;
	
	@Inject
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@Inject
	private PtmAreaTecnicaAvaliacaoJnDAO ptmAreaTecnicaAvaliacaoJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2376004504900687827L;

	private enum CentroCustoONExceptionCode implements BusinessExceptionCode {
		AIP_USUARIO_NAO_CADASTRADO,
		FCC_SUBTIPO_DESPESA_NAO_INFORMADO,
		CENTRO_CUSTO_DESCRICAO_DUPLICADA,
		CENTRO_CUSTO_CODIGO_NAO_INFORMADO,
		CENTRO_CUSTO_DESCRICAO_NAO_INFORMADA,
		CENTRO_CUSTO_GRUPO_NAO_INFORMADO,
		CENTRO_CUSTO_AREA_PESO,
		INATIVACAO_CENTRO_CUSTOS_SERVIDORES_LOTADOS,
		CENTRO_CUSTO_SUPERIOR,
		CHEFIA_DATA_INICIO_NULA,
		CENTRO_CUSTO_CODIGO_DUPLICADO,
		FCC_EXCLUSAO_NAO_PERMITIDA,
		MENSAGEM_ERRO_MODULO_INATIVO_PATRIMONIO
	}
	
	public enum PtmAreaTecAvaliacaoRNExceptionCode implements BusinessExceptionCode {
		RESPONSAVEL_TECNICO_NAO_ENCONTRADO, MENSAGEM_RESTRICAO_SEM_ITEM_PROVISORIO, MENSAGEM_ENVIAR_PARAMETRO,
		AGH_PARAMETRO_NAO_EXISTENTE_PTM_AREA_TEC_AVALIACAO, MENSAGEM_ERRO_MODULO_INATIVO_PATRIMONIO;
	}

	/**
	 * Filtro para o campo centro de custo superior
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosSuperior(String strPesquisa) {
		return getFccCentroCustosDAO().pesquisarCentroCustosSuperior(strPesquisa);
	}

	public Long pesquisarCentroCustosSuperiorCount(String strPesquisa) {
		return getFccCentroCustosDAO().pesquisarCentroCustosSuperiorCount(strPesquisa);
	}

	/**
	 * Pesquisar Centro de Custo através da Matrícula e Vínculo do Servidor
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return centroCusto
	 */
	public FccCentroCustos pesquisarCentroCustosPorMatriculaVinculo(Integer matricula, Short vinCodigo) {
		if (matricula == null || vinCodigo == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios.");
		}

		return getFccCentroCustosDAO().pesquisarCentroCustosPorMatriculaVinculo(matricula, vinCodigo);
	}

	/**
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param centroCusto
	 * @param grupoCentroCusto
	 * @param centroCustoSuperior
	 * @param servidor
	 * @return
	 */
	public Long obterFccCentroCustoCount(FccCentroCustos centroCusto, FcuGrupoCentroCustos grupoCentroCusto, FccCentroCustos centroCustoSuperior, RapServidores servidorChefia, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return getFccCentroCustosDAO().obterFccCentroCustoCount(centroCusto, grupoCentroCusto, centroCustoSuperior, servidorChefia,tiposCentroProducao);
	}

	/**
	 * Método para efetuar a pesquisa de centros de custo. Foi decidido com o
	 * Vicente e Geraldo para essa caso passar os objetos internos ao invés dos
	 * campos da interface por se tratar de uma tela com uma grande quantidade
	 * de filtros
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param centroCusto
	 * @param grupoCentroCusto
	 * @param centroCustoSuperior
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustos(Integer firstResult, Integer maxResults, FccCentroCustos centroCusto, FcuGrupoCentroCustos grupoCentroCusto,
			FccCentroCustos centroCustoSuperior, RapServidores servidorChefia, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return getFccCentroCustosDAO().pesquisarCentroCustos(firstResult, maxResults, centroCusto, grupoCentroCusto, centroCustoSuperior, servidorChefia,tiposCentroProducao );
	}

	public FccCentroCustos obterFccCentroCustos(Integer codigo) {
		return getFccCentroCustosDAO().obterFccCentroCustos(codigo);
	}
	
	public FccCentroCustos obterCentroCusto(Integer codigo) {
		return getFccCentroCustosDAO().obterCentroCusto(codigo);
	}
	
	public FccCentroCustos obterFccCentroCustosAtivos(Integer codigo) {
		return getFccCentroCustosDAO().obterFccCentroCustosAtivos(codigo);
	}

	public List<FcuGrupoCentroCustos> pesquisarGruposCentroCustos(String filtro) {
		return getFcuGrupoCentroCustosDAO().pesquisarGruposCentroCustos(filtro);
	}

	public Long pesquisarGruposCentroCustosCount(String filtro) {
		return getFcuGrupoCentroCustosDAO().pesquisarGruposCentroCustosCount(filtro);
	}

	/**
	 * 
	 * @dbtables FcuGrupoCentroCustos select
	 * 
	 * @param seq
	 * @return
	 */
	public FcuGrupoCentroCustos obterGrupoCentroCusto(Short seq) {
		if (seq == null || seq.equals(Short.valueOf("0"))) {
			return null;
		} else {
			return getFcuGrupoCentroCustosDAO().obterGrupoCentroCusto(seq);
		}
	}

	/**
	 * Método responsável pela persistencia do centro de custo.
	 * 
	 * @dbtables FccCentroCustos insert,update
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	public void persistirCentroCusto(FccCentroCustos centroCusto, Boolean isEdicao) throws ApplicationBusinessException, BaseException {
		this.verificarDescricaoJaExistente(centroCusto);
		if (isEdicao) {
			this.atualizarCentroDeCusto(centroCusto);
		} else {
			this.incluirCentroDeCusto(centroCusto);
		}
	}

	/**
	 * Método responsável por fazer a persistência de um novo centro de custo.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void incluirCentroDeCusto(FccCentroCustos centroCusto) throws ApplicationBusinessException {

		FccCentroCustosDAO fccCentroCustosDAO = getFccCentroCustosDAO();

		this.verificarCodigoJaExistente(centroCusto);

		this.atribuirValoresIniciais(centroCusto);

		this.validarCentroCusto(centroCusto);

		fccCentroCustosDAO.persistir(centroCusto);

		this.gerarChefiaCentroCusto(centroCusto);

		getCentroCustoJournalON().observarPersistenciaCentroCusto(centroCusto, DominioOperacoesJournal.INS);

		fccCentroCustosDAO.flush();

	}

	/**
	 * Verifica se o Centro de Custo sendo persistido possui um código já existente
	 * @param centroCusto
	 * @throws ApplicationBusinessException 
	 */
	private void verificarCodigoJaExistente(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		FccCentroCustosDAO fccCentroCustosDAO = getFccCentroCustosDAO();
		FccCentroCustos centroCustoBanco = fccCentroCustosDAO.obterPorChavePrimaria(centroCusto.getCodigo());
		if (centroCustoBanco != null) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_CODIGO_DUPLICADO, centroCusto.getCodigo());
		}
	}

	/**
	 * Verifica se o Centro de Custo sendo persistido possui uma descrição já existente
	 * @param centroCusto
	 * @throws ApplicationBusinessException 
	 */
	private void verificarDescricaoJaExistente(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		FccCentroCustosDAO fccCentroCustosDAO = getFccCentroCustosDAO();
		List<FccCentroCustos> listaCentroCustos = fccCentroCustosDAO
				.pesquisarCentroCustoDescricaoReplicada(centroCusto.getCodigo(), centroCusto.getDescricao());
		if (!listaCentroCustos.isEmpty()) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_DESCRICAO_DUPLICADA, centroCusto.getDescricao());
		}
	}

	/**
	 * Atribui os valores default do banco, caso não tenham sido infomados.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException 
	 */
	private void atribuirValoresIniciais(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		
		if (centroCusto.getIndArea() == null) {
			centroCusto.setIndArea(DominioArea.G);
		}

		if (centroCusto.getIndSituacao() == null) {
			centroCusto.setIndSituacao(DominioSituacao.A);
		}

		if (centroCusto.getIndSolicitaCompra() == null) {
			centroCusto.setIndSolicitaCompra(false);
		}

		if (centroCusto.getIndAvaliacaoTecnica() == null) {
			centroCusto.setIndAvaliacaoTecnica(false);
		}

		if (centroCusto.getIndAprovaSolicitacao() == null) {
			centroCusto.setIndAprovaSolicitacao(false);
		}

		if (centroCusto.getIndAbsentSmo() == null) {
			centroCusto.setIndAbsentSmo(false);
		}

		//Atribui o servidor logado
		centroCusto.setRapServidor(servidorLogado);

	}

	/**
	 * Método que gerencia a criação de chefia p/ o centro de custo sendo
	 * incluido.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void gerarChefiaCentroCusto(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		if (centroCusto.getRapServidor() != null) {
			RapChefias chefia = new RapChefias();
			chefia.setServidor(centroCusto.getRapServidor());
			chefia.setCentroCusto(centroCusto);
			chefia.setDtInicio(new Date());

			getRegistroColaboradorFacade().incluirChefia(chefia);
		}

		// TODO: enviar email de notificação de chefia de acorod com trigger
		// FCCT_CCT_ARI e procedure RAPP_EMAIL_CONSULT (em 09/02/2010)

	}

	/**
	 * Método usado para atualizar centros de custo.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void atualizarCentroDeCusto(FccCentroCustos centroCusto) throws ApplicationBusinessException, BaseException {

		this.validarCentroCustoAtualizacao(centroCusto);

		this.atualizarEspecialidades(centroCusto);

		this.atualizarChefiaCentroCusto(centroCusto);

		getCentroCustoJournalON().observarPersistenciaCentroCusto(centroCusto, DominioOperacoesJournal.UPD);

		getFccCentroCustosDAO().flush();
	}

	/**
	 * Método responsável por atualizar as chefias na edição de um centro de
	 * custo.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void atualizarChefiaCentroCusto(FccCentroCustos centroCusto) throws ApplicationBusinessException {

		if (this.verificarMudancaChefia(centroCusto)) {

			if (this.obterChefiaAtivaCentroCusto(centroCusto) != null) {
				RapChefias chefiaAtiva = this.obterChefiaAtivaCentroCusto(centroCusto);
				chefiaAtiva.setDtFim(new Date());

			}
			this.gerarChefiaCentroCusto(centroCusto);
		}

	}

	/**
	 * Obtem a chefia ativa p/ o centro de custos.
	 * 
	 * @param centroCusto
	 * @return
	 */
	private RapChefias obterChefiaAtivaCentroCusto(FccCentroCustos centroCusto) {
		return getFccCentroCustosDAO().obterChefiaAtivaCentroCusto(centroCusto);
	}

	/**
	 * Atualiza a chefia nas especialidade relacionadas a este centro de custo.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void atualizarEspecialidades(FccCentroCustos centroCusto) throws ApplicationBusinessException {

		if (this.verificarMudancaChefia(centroCusto)) {
			List<AghEspecialidades> listaEspecialidades = pesquisarEspecialidadesCentroCusto(centroCusto);
			for (AghEspecialidades especialidade : listaEspecialidades) {
				especialidade.setServidorChefe(centroCusto.getRapServidor());
			}
		}

	}

	/**
	 * Método que lista especialidade relacionadas ao centro de custo TODO:
	 * verificar melhor ON p/ receber este método.
	 * 
	 * @param centroCusto
	 * @return
	 */
	private List<AghEspecialidades> pesquisarEspecialidadesCentroCusto(FccCentroCustos centroCusto) {
		return getFccCentroCustosDAO().pesquisarEspecialidadesCentroCusto(centroCusto);
	}

	/**
	 * Verifica se houve mudança na chefia do centro de custo.
	 * 
	 * @param centroCusto
	 * @return
	 */
	private boolean verificarMudancaChefia(FccCentroCustos centroCusto) {
		RapServidoresId idChefiaAnterior = obterChaveChefiaAnterior(centroCusto);
		boolean retorno = false;

		if (idChefiaAnterior == null) {
			if (centroCusto.getRapServidor() != null) {
				retorno = true;
			}
		} else {
			if (centroCusto.getRapServidor() == null || !idChefiaAnterior.equals(centroCusto.getRapServidor().getId())) {
				retorno = true;
			}
		}

		return retorno;
	}

	/**
	 * Retorna a chave da chefia anterior p/ este centro de custo, ou null, caso
	 * não haja chefia anterior.
	 * 
	 * @param centroCusto
	 * @return
	 */
	private RapServidoresId obterChaveChefiaAnterior(FccCentroCustos centroCusto) {
		return getFccCentroCustosDAO().obterChaveChefiaAnterior(centroCusto);
	}

	/**
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
 void validarCentroCustoAtualizacao(FccCentroCustos centroCusto) throws ApplicationBusinessException, BaseException {
		this.validarCentroCusto(centroCusto);

		this.validarInativacaoCentroCustos(centroCusto);

		this.validarCentroCustoSuperior(centroCusto);
		
		this.validarChefia(centroCusto);
	}

 	/**
 	 * #44276
 	 * RN01
 	 * @param centroCusto
 	 */
	public void validarChefia(FccCentroCustos centroCusto) throws ApplicationBusinessException, BaseException{				
		FccCentroCustos centroCustoAntigo = this.fccCentroCustosDAO.obterCentroCustoAntigo(centroCusto.getCodigo());		
		if (verificaSeModuloEstaAtivo()){				
			if(centroCusto != null ){					
				if (centroCusto.getIndSituacao() != centroCustoAntigo.getIndSituacao() && (centroCusto.getRapServidor() != null && centroCustoAntigo.getRapServidor() != null) && centroCusto.getRapServidor().equals(centroCustoAntigo.getRapServidor())){
					//Executar RN07 passando centroCustoAntigo e atual.
					verificarInclusaoPerfil(centroCusto, centroCustoAntigo);
				}else if(centroCustoAntigo.getRapServidor() != null && centroCusto.getRapServidor() == null){
					//Executar RN08 passando o centroCusto
					verificarChefiaUsuario(centroCustoAntigo);
				}else if((centroCustoAntigo.getRapServidor() == null && centroCusto.getRapServidor() != null) ||  (centroCusto.getRapServidor() != null && !centroCusto.getRapServidor().equals(centroCustoAntigo.getRapServidor()))){
					//Executar RN03 passando centroCustoAntigo e atual.
					refletirAlteracaoChefia(centroCusto, centroCustoAntigo);
				}
			}
  		}		
	}
	

	/**
	 * Valida que um centro de custo não pode ser seu próprio superior.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void validarCentroCustoSuperior(FccCentroCustos centroCusto) throws ApplicationBusinessException {

		if (centroCusto.getCentroCusto() != null && centroCusto.getCentroCusto().equals(centroCusto)) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_SUPERIOR);
		}

	}

	/**
	 * Método que valida ser um centro de custo pode ser inativado.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void validarInativacaoCentroCustos(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		if (obterSituacaoAnteriorCentroCusto(centroCusto) == DominioSituacao.A && centroCusto.getIndSituacao() == DominioSituacao.I
				&& verificarCentroCustosServidoresLotados(centroCusto)) {

			throw new ApplicationBusinessException(CentroCustoONExceptionCode.INATIVACAO_CENTRO_CUSTOS_SERVIDORES_LOTADOS);

		}

	}

	/**
	 * Método que verifica se existe algum servidor lotado no centro de custo.
	 * 
	 * @param centroCusto
	 * @return
	 */
	private boolean verificarCentroCustosServidoresLotados(FccCentroCustos centroCusto) {
		return getFccCentroCustosDAO().verificarCentroCustosServidoresLotados(centroCusto);
	}

	/**
	 * Método que retorna a situação anterior de um centro de custo. por
	 * anterior entenda-se antes de uma possível alteração pelo usuário. Esta
	 * query ignora a cache de primeiro nível do hibernate!!!
	 * 
	 * @param centroCusto
	 * @return
	 */
	private DominioSituacao obterSituacaoAnteriorCentroCusto(FccCentroCustos centroCusto) {
		return getFccCentroCustosDAO().obterSituacaoAnteriorCentroCusto(centroCusto);
	}

	/**
	 * Método que faz as validações necessária sobre o centro de custos.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void validarCentroCusto(FccCentroCustos centroCusto) throws ApplicationBusinessException {

		if (centroCusto.getCodigo() == null) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_CODIGO_NAO_INFORMADO);

		}
		if (centroCusto.getDescricao() == null) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_DESCRICAO_NAO_INFORMADA);

		}
		if (centroCusto.getGrupoCentroCusto() == null) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_GRUPO_NAO_INFORMADO);

		}

		this.validarTipoDespesaCentroCusto(centroCusto);

		if (centroCusto.getArea() == null && centroCusto.getPeso() != null) {
			throw new ApplicationBusinessException(CentroCustoONExceptionCode.CENTRO_CUSTO_AREA_PESO);
		}

	}

	/**
	 * Método que valida o centro de custo sendo persistido co relação aos tipos
	 * de despesas.
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 */
	private void validarTipoDespesaCentroCusto(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		if (centroCusto.getIndTipoDespesa() == DominioTipoDespesa.O) {
			if (centroCusto.getIndSubTipoDespesa() == null) {
				throw new ApplicationBusinessException(CentroCustoONExceptionCode.FCC_SUBTIPO_DESPESA_NAO_INFORMADO);
			}
		}

	}

	/**
	 * Método que pesquisa por código ou descrição do Centro de Custo.
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @dbtables FccCentroCustos select
	 * @param filtro
	 * @return Lista de <code>FccCentroCustos</code>.
	 */
	public List<FccCentroCustos> pesquisarCentroCustosPorCodigoDescricao(String filtro) {
		return getFccCentroCustosDAO().pesquisarCentroCustosPorCodigoDescricao(filtro);
	}

	public Long pesquisarCentroCustosPorCodigoDescricaoCount(String filtro) {
		return getFccCentroCustosDAO().pesquisarCentroCustosPorCodigoDescricaoCount(filtro);
	}

	/**
	 * Método que pesquisa por código ou descrição do Centro de Custo. Somente
	 * centros de custos ativos e com chefia
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 */
	public List<FccCentroCustos> pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(String filtro) {
		return getFccCentroCustosDAO().pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(filtro);
	}

	public Long pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(String filtro) {
		return getFccCentroCustosDAO().pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(filtro);
	}

	protected FccCentroCustosDAO getFccCentroCustosDAO() {
		return fccCentroCustosDAO;
	}

	protected FcuGrupoCentroCustosDAO getFcuGrupoCentroCustosDAO() {
		return fcuGrupoCentroCustosDAO;
	}

	protected CentroCustoJournalON getCentroCustoJournalON() {
		return centroCustoJournalON;
	}

	/**
	 * Retorna os centros custos ativos
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosAtivos(Object parametro) {
		return getFccCentroCustosDAO().pesquisarCentroCustosAtivos(parametro);
	}

	/**
	 * Este método não funciona no Oracle
	 * *********************************************************
	 * Pesquisa centros de custos do usuário, tanto o centro de custo de atuação do usuário,
	 * como os centros de custo filhos deste
	 * @param parametro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(Object parametro) {
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		Integer codigoCentroCusto = this.aghuFacade.getCcustAtuacao(servidorLogado);
		return getFccCentroCustosDAO().pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(parametro, codigoCentroCusto);
	}

	/**
	 * Retorna centro de custo do usuário
	 * @author clayton.bras
	 * @return
	 */
	public FccCentroCustos obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao(RapServidores user) {
		//obtém o código do centro de custo de atuação do usuário
		//Integer codigoCentroCusto = this.aghuFacade.getCcustAtuacao(user);
		
		if(user.getCentroCustoAtuacao() == null && user.getCentroCustoLotacao() == null){
			user = registroColaboradorFacade.obterServidor(user);
			
		}
		Integer codigoCentroCusto = null;
		if(user.getCentroCustoAtuacao() != null){
			codigoCentroCusto = user.getCentroCustoAtuacao().getCodigo();
		}else if(user.getCentroCustoLotacao() != null){
			codigoCentroCusto = user.getCentroCustoLotacao().getCodigo();
		}
		
		if (codigoCentroCusto != null) {
			return getFccCentroCustosDAO().obterPorChavePrimaria(codigoCentroCusto);
		}
		//usuario sem centro de custo definido
		return null;
	}

	/**
	 * Pesquisa os centros de custo ativos, do usuário ou filhoes do ccusto do
	 * usuario, inclui ccusto Atuação e lotacao.
	 * @param parametro
	 * @param matricula
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public List<FccCentroCustos> pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(Object parametro, final DominioCaracteristicaCentroCusto caracteristica) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		final FccCentroCustosDAO centroCustosDAO = getFccCentroCustosDAO();
		final List<FccCentroCustos> ccs = centroCustosDAO.pesquisarCentroCustoUsuarioCaracteristica(servidorLogado, caracteristica);

		final List<Integer> ccsCodigos = this.getCodigosCentrosCusto(ccs);
		final String parPesq = (String) parametro;

		final List<FccCentroCustos> result = centroCustosDAO.pesquisarCentrosCustosUsuarioHierarquiaParte1(parPesq, DominioSituacao.A, ccsCodigos);
		result.addAll(centroCustosDAO.pesquisarCentrosCustosUsuarioHierarquiaParte2(servidorLogado, parPesq, DominioSimNao.S, caracteristica, ccsCodigos));
		result.addAll(centroCustosDAO.pesquisarCentrosCustosUsuarioHierarquiaParte3(servidorLogado, parPesq, DominioSituacao.A, DominioSimNao.S,
				caracteristica, ccsCodigos));

		Collections.sort(result);
		return result;
	}

	public List<Integer> getCodigosCentrosCusto(List<FccCentroCustos> ccs) {
		if (ccs == null || ccs.isEmpty()) {
			return null;
		}

		final List<Integer> result = new ArrayList<Integer>(ccs.size());
		for (final FccCentroCustos cc : ccs) {
			result.add(cc.getCodigo());
		}

		return result;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(Object paramPesquisa) {

		RapServidores servidor = this.servidorLogadoFacade.obterServidorLogado();
		if (this.cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCComprador")
				|| this.cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCPlanejamento") 
				|| this.cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCGeral")) {
			return getFccCentroCustosDAO().pesquisarCentroCustosPorCodigoDescricao(paramPesquisa != null ? paramPesquisa.toString() : null);
		}

		else {
			return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioGerarSCSuggestion(paramPesquisa, servidor, DominioCaracteristicaCentroCusto.GERAR_SC);
		}

	}

	
	public Long pesquisarCentroCustoUsuarioGerarSCSuggestionCount(Object paramPesquisa) {
		RapServidores servidor = this.servidorLogadoFacade.obterServidorLogado();
		if (this.cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCComprador")
				|| this.cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCPlanejamento") || this.cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCGeral")) {
			return getFccCentroCustosDAO().pesquisarCentroCustosPorCodigoDescricaoCount(paramPesquisa != null ? paramPesquisa.toString() : null);
		}
		else {
				return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioGerarSCSuggestionCount(paramPesquisa, servidor, DominioCaracteristicaCentroCusto.GERAR_SC);
		}
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSSSuggestion(Object paramPesquisa, RapServidores servidor) {

		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioGerarSSSuggestion(paramPesquisa, servidor);
	}

	/**
	 * Busca todos os centros de custos levando em consideração os filtros informados por parametro e adiciona o filtro de ser apenas Centros de custo
	 * que não sejam do grupo de obras.
	 * 
	 * @author rmalvezzi
	 * @param paramPesquisa				Possiveis filtros de Código ou Descrição do Centro de Custo (NULL se for para desconsiderar esse parametro).
	 * @param seqCentroProducao			Filtro pelo Código do Centro de Produção (NULL se for para desconsiderar esse parametro).
	 * @param situacao					Filtro pela Situação do Centro de Custo (NULL se for para desconsiderar esse parametro).
	 * @return							Retorna Lista dos Centros de Custos que correspondem aos filtros informados por parametro.   
	 */
	public List<FccCentroCustos> pesquisarCentroCustosPorCentroProdExcluindoGcc(Object paramPesquisa, Integer seqCentroProducao, DominioSituacao situacao) {
		Integer codGrupoCentroCustoObras = null;
		if (this.parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.P_GR_CCUSTO_OBR_AND)) {
			BigDecimal codGrupoCentroCustoObrasAux = this.parametroFacade.obterValorNumericoAghParametros(
					AghuParametrosEnum.P_GR_CCUSTO_OBR_AND.toString());
			if (codGrupoCentroCustoObrasAux != null) {
				codGrupoCentroCustoObras = codGrupoCentroCustoObrasAux.intValue();
			}
		}
		return this.getFccCentroCustosDAO()
				.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, codGrupoCentroCustoObras, seqCentroProducao, situacao);
	}
	
	/** 
	 * #44276
	 * Verificar se módulo “Patrimônio�? de sigla “PTM�? está habilitado.
	 */	
	public boolean verificaSeModuloEstaAtivo() throws ApplicationBusinessException {
		
		String patrimonio = "patrimonio";
		boolean moduloAtivo = cascaFacade.verificarSeModuloEstaAtivo(patrimonio);
//        if(!moduloAtivo){
//            throw new ApplicationBusinessException(PtmAreaTecAvaliacaoRNExceptionCode.MENSAGEM_ERRO_MODULO_INATIVO_PATRIMONIO, "Patrimônio"); 
//        } 
        return moduloAtivo;
        
    } 
	
	/**
	 * #44276
	 * RN02
	 */
	public void enviarEmailNotificacaoChefe(FccCentroCustos centroCusto) throws ApplicationBusinessException{
		
		if (verificaSeModuloEstaAtivo()){
				montarEmailNotificacaoChefe(centroCusto);
		}
	}
	
	/**
	 * #44276
	 * RN03
	 */
	public void refletirAlteracaoChefia(FccCentroCustos centroCusto, FccCentroCustos centroCustoAntigo)throws ApplicationBusinessException, BaseException{
		
		PtmAreaTecAvaliacao areaTecAvaliacao;
		if (verificaSeModuloEstaAtivo()){
				areaTecAvaliacao = ptmAreaTecAvaliacaoDAO.obterAreaTecnicaPorCodigo(centroCustoAntigo.getCodigo());
				
				if(areaTecAvaliacao != null){	
					areaTecAvaliacao.setServidorCC(centroCusto.getRapServidor());
					ptmAreaTecAvaliacaoDAO.atualizar(areaTecAvaliacao);
					inserirJournalPtmAreaTecnicaAvaliacao(areaTecAvaliacao, DominioOperacoesJournal.UPD);
				}
				
		}
		enviarEmailNotificacaoChefe(centroCusto);
		
	}
	
	/**
	 * #44276
	 * RN05
	 */
	public void adicionarPerfilChefia(FccCentroCustos centroCusto) throws ApplicationBusinessException{
		
		RapServidores rapServidores;
		Usuario usuario; Perfil perfil;
		
			if (verificaSeModuloEstaAtivo()){
				if(centroCusto.getRapServidor() != null){
					//Buscar usuário no sistema na tabela RAP_SERVIDORES
					rapServidores = rapServidoresDAO.buscarUsuarioSistema(centroCusto.getRapServidor().getId().getMatricula(), centroCusto.getRapServidor().getId().getVinCodigo());
					//Busca usuário na tabela CSC_USUARIO
					//usuario = usuarioDAO.buscarUsuarioPorLogin(rapServidores.getUsuario());
					usuario = cascaFacade.obterUsuario(rapServidores.getUsuario());
					//AghParametros nomePerfil = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PERFIL_CHEFE_CENTRO_CUSTO);
					//Busca perfil na tabela CSC_PERFIL
					String nomePerfil = "ADM71"; // Corresponde a P_AGHU_PERFIL_CHEFE_CENTRO_CUSTO
					perfil = perfilDAO.buscarPerfil(nomePerfil);
					
					boolean flagPossuiPerfil = false;
					
					for (PerfisUsuarios listaIDs : usuario.getPerfisUsuario()) {
						if(listaIDs.getId().equals(perfil.getId())){
							flagPossuiPerfil = true;
						}
					}
					
					if (!flagPossuiPerfil){
					
						PerfisUsuarios perfisUsuarios = new PerfisUsuarios(null, usuario, perfil, new Date());
						perfisUsuariosDAO.persistir(perfisUsuarios);
						//Com o usuário do chefe do centro de custo vai ser feito um insert na tabela CSC_PERFIS_USUARIOS 
						//insert into CSC_PERFIS_USUARIOS(ID_PERFIL,ID_USUARIO, DATA_CRIACAO) values (ID_PERFIL,ID_USUARIO,DATA_CRIACAO)
						//•	ID_USUARIO deve vir de CSC_USUARIO campo ID 
						//•	ID_PERFIL deve vir de CSC_PERFIL campo ID 
						//•	DATA_CRIACAO –  formato ‘dd/mm/aaaa’
					}
				}	
			}
	}
	
	/**
	 * #44276
	 * RN07
	 */
	public void verificarInclusaoPerfil(FccCentroCustos centroCusto, FccCentroCustos centroCustoAntigo) throws ApplicationBusinessException{
			
		if (verificaSeModuloEstaAtivo()){
				if (!centroCustoAntigo.getIndSituacao().isAtivo() && centroCusto.getIndSituacao().isAtivo()){
					//Executar RN05
					adicionarPerfilChefia(centroCustoAntigo);
				}
				if (centroCustoAntigo.getIndSituacao().isAtivo() && !centroCusto.getIndSituacao().isAtivo()){
					//Executar RN08
					verificarChefiaUsuario(centroCustoAntigo);
				}
			}
	}
	
	/**
	 * #44276
	 * RN08
	 */
	public void verificarChefiaUsuario(FccCentroCustos centroCusto) throws ApplicationBusinessException{
		
		List<FccCentroCustos> centroCustoObtido = new ArrayList<FccCentroCustos>();
	
			if (verificaSeModuloEstaAtivo()){
				if (centroCusto.getRapServidor() != null){
					//Executar C5
					centroCustoObtido = fccCentroCustosDAO.obterCentroCustoResponsavelPorUsuario(centroCusto.getRapServidor().getId().getMatricula(), centroCusto.getRapServidor().getId().getVinCodigo());
					if (centroCustoObtido.size() == 1){
						if (centroCustoObtido.get(0).getDescricao().equalsIgnoreCase(centroCusto.getDescricao())){
							//Executar RN09
							deletarPerfilChefia(centroCusto);
						}
					}
				}
			}
	}
	
	/**
	 * #44276
	 * RN09
	 */
	public void deletarPerfilChefia(FccCentroCustos centroCusto)throws ApplicationBusinessException{
		
		RapServidores rapServidores;
		Usuario usuario;
		Perfil perfil;

			if (verificaSeModuloEstaAtivo()){
				if (centroCusto.getRapServidor() != null){
					
					//Buscar usuário no sistema na tabela RAP_SERVIDORES
					rapServidores = rapServidoresDAO.buscarUsuarioSistema(centroCusto.getRapServidor().getId().getMatricula(), centroCusto.getRapServidor().getId().getVinCodigo());
					//Busca usuário na tabela CSC_USUARIO
					//usuario = usuarioDAO.buscarUsuarioPorLogin(rapServidores.getUsuario());
					usuario = cascaFacade.obterUsuario(rapServidores.getUsuario());
	
					//AghParametros nomePerfil = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PERFIL_CHEFE_CENTRO_CUSTO);
					//Busca perfil na tabela CSC_PERFIL
					String nomePerfil = "ADM71"; // Corresponde a P_AGHU_PERFIL_CHEFE_CENTRO_CUSTO
					perfil = perfilDAO.buscarPerfil(nomePerfil);
					
					//DELETA
					PerfisUsuarios perfisUsuarios = perfisUsuariosDAO.pesquisarPerfisUsuariosPorUsuarioPerfil(usuario, perfil);
					if (perfisUsuarios != null){
					perfisUsuariosDAO.removerPorId(perfisUsuarios.getId());
					}
					//delete CSC_PERFIS_USUARIOS
					//ID_PERFIL = resultado consulta na tabela csc_perfil
					//ID_USUARIO = resultado consulta na tabela csc_usuario
					
				}	
			}
	}
	
	
	/** 
	 * #44276
	 * Envia email notificando chefe de patrimônio
	 */	
	public void montarEmailNotificacaoChefe(FccCentroCustos centroCusto) throws ApplicationBusinessException {
				
		String codigo = centroCusto.getCodigo().toString();		
		String nomeCentroCusto = centroCusto.getDescricao();
		String nomeServidorChefia = "Nenhuma chefia cadastrada.";
		if (centroCusto.getRapServidor().toString() != null){
			nomeServidorChefia = centroCusto.getRapServidor().getPessoaFisica().getNome().toString();
		}
		
		String conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_NOTIFICACAO_CHEFE_PATRIMONIO");	
		String email = MessageFormat.format(conteudo, 
				codigo, nomeCentroCusto, nomeServidorChefia);
		
		String conteudoAssunto = super.getResourceBundleValue("CONTEUDO_ASSUNTO_EMAIL");	
		String assunto = MessageFormat.format(conteudoAssunto, 
				codigo, nomeCentroCusto);
		
		AghParametros paramEmailDestinatatio = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_CHEFE_PATRIMONIO);
		AghParametros paramEmailRemetente = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_PATRIMONIO);
	
		emailUtil.enviaEmail(paramEmailRemetente.getVlrTexto(), paramEmailDestinatatio.getVlrTexto(), null, assunto, email);
	}
	
	/**
     * Registro das operacoes em PtmAreaTecAvaliacaoJN
     * @param areaTecAvaliacao
     * @param operacao (insert, update)
     */
    protected void inserirJournalPtmAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecAvaliacao, DominioOperacoesJournal operacao)throws BaseException{
          
    	  RapServidores servidor = rapServidoresDAO.obterServidorPorUsuario(obterLoginUsuarioLogado());
    	
          PtmAreaTecAvaliacaoJn areaTecAvaliacaoJn = BaseJournalFactory.getBaseJournal(operacao, PtmAreaTecAvaliacaoJn.class, servidor != null ? servidor.getUsuario() : null);
          areaTecAvaliacaoJn.setFccCentroCustos(areaTecAvaliacao.getFccCentroCustos());
          areaTecAvaliacaoJn.setNomeAreaTecAvaliacao(areaTecAvaliacao.getNomeAreaTecAvaliacao());
          areaTecAvaliacaoJn.setServidorCC(areaTecAvaliacao.getServidorCC());
          areaTecAvaliacaoJn.setSituacao(areaTecAvaliacao.getSituacao());
          areaTecAvaliacaoJn.setMensagem(areaTecAvaliacao.getMensagem());
          areaTecAvaliacaoJn.setServidor(areaTecAvaliacao.getServidor());
          areaTecAvaliacaoJn.setOperacao(operacao);
          areaTecAvaliacaoJn.setSeq(areaTecAvaliacao.getSeq());
          ptmAreaTecnicaAvaliacaoJnDAO.persistir(areaTecAvaliacaoJn);
    }

	public PtmAreaTecAvaliacao getPtmAreaTecAvaliacao() {
		return ptmAreaTecAvaliacao;
	}

	public void setPtmAreaTecAvaliacao(PtmAreaTecAvaliacao ptmAreaTecAvaliacao) {
		this.ptmAreaTecAvaliacao = ptmAreaTecAvaliacao;
	}
}