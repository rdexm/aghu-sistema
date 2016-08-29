package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.internacao.dao.AinAcomodacoesDAO;
import br.gov.mec.aghu.internacao.dao.AinCaracteristicaLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinObservacoesPacAltaDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinTipoCaracteristicaLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposAltaMedicaDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposCaraterInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMvtoInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.internacao.vo.AinQuartosVO;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.model.FatTiposDocumento;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.cups.ImpImpressora;

/**
 * Porta de entrada do sub-módulo Cadastros básicos do módulo de internação.
 * 
 * @author gmneto
 * 
 */

@Modulo(ModuloEnum.INTERNACAO)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class CadastrosBasicosInternacaoFacade extends BaseFacade implements ICadastrosBasicosInternacaoFacade {

	@EJB
	private TiposMvtoInternacaoCRUD tiposMvtoInternacaoCRUD;

	@EJB
	private AcomodacaoCRUD acomodacaoCRUD;

	@EJB
	private ProfConveniosCRUD profConveniosCRUD;

	@EJB
	private ProfConveniosListON profConveniosListON;

	@EJB
	private LeitosCRUD leitosCRUD;

	@EJB
	private EspecialidadeCRUD especialidadeCRUD;

	@EJB
	private ImpressoraPadraoON impressoraPadraoON;

	@EJB
	private ClinicaCRUD clinicaCRUD;

	@EJB
	private AlaCRUD alaCRUD;

	@EJB
	private TiposDocumentoCRUD tiposDocumentoCRUD;

	@EJB
	private OrigemEventoCRUD origemEventoCRUD;

	@EJB
	private TiposSituacaoLeitoCRUD tiposSituacaoLeitoCRUD;

	@EJB
	private TiposUnidadeFuncionalCRUD tiposUnidadeFuncionalCRUD;

	@EJB
	private UnidadeFuncionalCRUD unidadeFuncionalCRUD;

	@EJB
	private DiariaAutorizadaCRUD diariaAutorizadaCRUD;

	@EJB
	private ProfEspecialidadesCRUD profEspecialidadesCRUD;

	@EJB
	private TiposCaraterInternacaoCRUD tiposCaraterInternacaoCRUD;

	@EJB
	private OrigemInternacaoCRUD origemInternacaoCRUD;

	@EJB
	private ObservacoesPacAltaCRUD observacoesPacAltaCRUD;

	@EJB
	private ProfissionaisEquipeCRUD profissionaisEquipeCRUD;

	@EJB
	private QuartoCRUD quartoCRUD;

	@EJB
	private TiposAltaMedicaCRUD tiposAltaMedicaCRUD;

	@EJB
	private InstituicaoHospitalarCRUD instituicaoHospitalarCRUD;

	@EJB
	private TiposCaracteristicaLeitoCRUD tiposCaracteristicaLeitoCRUD;

	@Inject
	private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;

	@Inject
	private AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO;

	@Inject
	private AinLeitosDAO ainLeitosDAO;

	@Inject
	private AinCaracteristicaLeitoDAO ainCaracteristicaLeitoDAO;

	@Inject
	private AinQuartosDAO ainQuartosDAO;

	@Inject
	private AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO;

	@Inject
	private AinTipoCaracteristicaLeitoDAO ainTipoCaracteristicaLeitoDAO;

	@Inject
	private AinObservacoesPacAltaDAO ainObservacoesPacAltaDAO;

	@Inject
	private AinAcomodacoesDAO ainAcomodacoesDAO;

	@Inject
	private AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO;

	// Manter Quartos e Leitos
	// =================================================================
	// QuartoCRUD

	/**
	 * 
	 */
	private static final long serialVersionUID = 4402193472550732118L;

	@Override
	@Secure("#{s:hasPermission('quarto','alterar')}")
	public AinQuartos atualizarQuarto(final AinQuartos quarto, final boolean flush) throws ApplicationBusinessException {
		final AinQuartos ret = this.getQuartoCRUD().mergeAinQuartos(quarto);
		if (flush) {
			this.flush();
		}
		return ret;
	}

	@Override
	public AinAcomodacoes obterAcomodacao(Object pk, Enum... fetchEnums) {
		return ainAcomodacoesDAO.obterPorChavePrimaria(pk, fetchEnums);
	}

	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param numero
	 * @param clinica
	 * @param excInfec
	 * @param consCli
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<AinQuartos> pesquisaQuartos(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final Short codigoQuartoPesquisa, final AghClinicas clinicaPesquisa, final DominioSimNao excInfecPesquisa,
			final DominioSimNao consCliPesquisa, final String descricao) {
		return getAinQuartosDAO().pesquisaQuartos(firstResult, maxResults, orderProperty, asc, codigoQuartoPesquisa, clinicaPesquisa,
				excInfecPesquisa, consCliPesquisa, descricao);
	}

	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<AinQuartosVO> pesquisaQuartosNew(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short numero,
			AghClinicas clinica, DominioSimNao excInfec, DominioSimNao consCli, String descricao) {
		return getAinQuartosDAO().pesquisaQuartosNew(firstResult, maxResults, orderProperty, asc, numero, clinica, excInfec, consCli,
				descricao);
	}

	@Override
	public Long pesquisaQuartosCount(final Short codigoQuartoPesquisa, final AghClinicas clinicaPesquisa,
			final DominioSimNao excInfecPesquisa, final DominioSimNao consCliPesquisa, final String descricao) {
		return getAinQuartosDAO().pesquisaQuartosCount(codigoQuartoPesquisa, clinicaPesquisa, excInfecPesquisa, consCliPesquisa, descricao);
	}

	/**
	 * 
	 * @dbtable AinQuartos select
	 * 
	 * @param ainQuartosCodigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public AinQuartos obterQuarto(final Short ainQuartosCodigo) {
		return getQuartoCRUD().obterQuarto(ainQuartosCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public AinQuartos obterQuarto(final Short ainQuartosCodigo, Enum[] innerFields, Enum[] leftFields) {
		return getAinQuartosDAO().obterPorChavePrimaria(ainQuartosCodigo, innerFields, leftFields);
	}

	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public AinQuartos obterQuartosLeitosPorId(final Short numero) {
		return getAinQuartosDAO().obterQuartosLeitosPorId(numero);
	}

	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<AinQuartos> pesquisarQuartos(final String strPesquisa) {

		// Tenta buscar pelo ID único do quarto e caso encontre 1 elemento
		// válido, então retorna apenas ele.
		if (StringUtils.isNotBlank(strPesquisa) && StringUtils.isNumeric(strPesquisa)) {
			AinQuartos quarto = getAinQuartosDAO().pesquisaQuartoPorNumero(Short.valueOf(strPesquisa));
			if (quarto != null) {
				List<AinQuartos> lista = new ArrayList<AinQuartos>(1);
				lista.add(quarto);
				return lista;
			}
		}

		// Caso não ache 1 elemento pelo ID, tenta buscar por outras informações
		return getAinQuartosDAO().pesquisarQuartos(strPesquisa);
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	/**
	 * Método para obter a descrição completa do quarto (numero, andar, ala,
	 * descrição da unidade funcional)
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * @dbtables AinQuartos select
	 * 
	 * @param numeroQuarto
	 * @return Descrição completa do quarto
	 */
	@Override
	public String obterDescricaoCompletaQuarto(final Short numeroQuarto) {
		return getQuartoCRUD().obterDescricaoCompletaQuarto(numeroQuarto);
	}

	@Override
	public void persistirQuarto(final AinQuartos quarto, final List<AinLeitosVO> leitosQuarto) throws ApplicationBusinessException {
		getQuartoCRUD().persistir(quarto, leitosQuarto);
	}

	@Override
	public void validarAtualizacaoLeito(final AinLeitos leito) throws ApplicationBusinessException {
		getQuartoCRUD().validarAtualizacaoLeito(leito);
	}

	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<AinQuartos> pesquisarQuartoSolicitacaoInternacao(final String strPesquisa, final DominioSexo sexoPaciente) {
		return getQuartoCRUD().pesquisarQuartoSolicitacaoInternacao(strPesquisa, sexoPaciente);
	}

	@Override
	public void validarLeitoExistente(final String idLeito, final AinQuartos ainQuartos) throws ApplicationBusinessException {
		getQuartoCRUD().validarLeitoExistente(idLeito, ainQuartos);
	}
	
	@Override
	public Long pesquisaCount(Short codigo, String descricao, String sigla, AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala) {
		return this.getUnidadeFuncionalCRUD().pesquisaCount(codigo, descricao, sigla, clinica, centroCusto, unidadeFuncionalPai, situacao,
				andar, ala);
	}
	
	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(Integer firstResult, Integer maxResults, String orderProperty, Boolean asc,
			Short codigo, String descricao, String sigla, AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadesFuncionais(firstResult, maxResults, orderProperty, asc, codigo, descricao,
				sigla, clinica, centroCusto, unidadeFuncionalPai, situacao, andar, ala);
	}
	
	@Override
	public void validaCaracteristicaPrincipal(final List<AinCaracteristicaLeito> ainCaracteristicas) throws ApplicationBusinessException {
		getQuartoCRUD().validaCaracteristicaPrincipal(ainCaracteristicas);
	}

	@Override
	public void validarQuartoExistente(final String descricao) throws ApplicationBusinessException {
		getQuartoCRUD().validarQuartoExistente(descricao);
	}

	@Override
	public void desatacharQuarto(final AinQuartos ainQuartos) {
		getQuartoCRUD().desatacharQuarto(ainQuartos);
	}

	/**
	 * 
	 * @dbtables AghEspecialidades select
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadeSiglaEDescricao(final Object parametro) {
		return getQuartoCRUD().pesquisarEspecialidadeSiglaEDescricao(parametro);
	}

	/**
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaracteristicaLeito','pesquisar')}")
	public List<AinTipoCaracteristicaLeito> pesquisarTiposCaracteristicasPorCodigoOuDescricao(final Object parametro) {
		return getQuartoCRUD().pesquisarTiposCaracteristicasPorCodigoOuDescricao(parametro);
	}

	/**
	 * Valida se existe clínica com o código informado.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean codigoClinicaExistente(final Integer codigo) {
		return getClinicaCRUD().codigoExistente(codigo);
	}

	/**
	 * Método responsável por inserir uma clínica
	 * 
	 * @dbtables AghClinicas select,insert
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','alterar')}")
	public void criarClinica(final AghClinicas clinica) throws ApplicationBusinessException {
		getClinicaCRUD().criar(clinica);
	}

	/**
	 * Método responsável por atualizar uma clínica
	 * 
	 * @dbtables AghClinicas select,update
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','alterar')}")
	public void atualizarClinica(final AghClinicas clinica) throws ApplicationBusinessException {
		getClinicaCRUD().atualizar(clinica);
	}

	/**
	 * Apaga uma clínica do banco de dados.
	 * 
	 * @dbtables AghClinicas delete
	 * 
	 * @param clinica
	 *            Clínica a ser removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','excluir')}")
	public void removerClinica(final Integer codigo) throws ApplicationBusinessException {
		getClinicaCRUD().remover(codigo);
	}

	protected ClinicaCRUD getClinicaCRUD() {
		return clinicaCRUD;
	}

	// Manter Tipos de Caracteristicas de Leito.
	// =================================================================
	// TiposCaracteristicaLeitoCRUD

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param ainTiposCaracteristicaLeitosCodigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaracteristicaLeito','pesquisar')}")
	public AinTipoCaracteristicaLeito obterTiposCaracteristicaLeitos(final Integer ainTipoCaracteristicaLeitoCodigo) {
		return getAinTipoCaracteristicaLeitoDAO().obterTiposCaracteristicaLeitos(ainTipoCaracteristicaLeitoCodigo);
	}

	/**
	 * Método responsável pela persistência de um tipo de característica de
	 * leito.
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select,insert,update
	 * 
	 * @param tipo
	 *            de característica de leito.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaracteristicaLeito','alterar')}")
	public void persistirTiposCaracteristicaLeito(final AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito)
			throws ApplicationBusinessException {
		getTiposCaracteristicaLeitoCRUD().persistirTiposCaracteristicaLeito(ainTipoCaracteristicaLeito);
	}

	/**
	 * Apaga um tipo de característica de leito do banco de dados.
	 * 
	 * @dbtables AinTipoCaracteristicaLeito delete
	 * 
	 * @param tipo
	 *            de característica de leito. Tipo de característica de leito a
	 *            ser removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaracteristicaLeito','excluir')}")
	public void removerTiposCaracteristicaLeito(final Integer codigo) throws ApplicationBusinessException {
		AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito = this.obterTiposCaracteristicaLeitos(codigo);
		getTiposCaracteristicaLeitoCRUD().removerTiposCaracteristicaLeito(ainTipoCaracteristicaLeito);
	}

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	@Override
	public Long pesquisaTiposCaracteristicaLeitoCount(final Integer codigo, final String descricao) {
		return getAinTipoCaracteristicaLeitoDAO().pesquisaTiposCaracteristicaLeitoCount(codigo, descricao);
	}

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaracteristicaLeito','pesquisar')}")
	public List<AinTipoCaracteristicaLeito> pesquisaTiposCaracteristicaLeito(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposCaracteristicaLeito,
			final String descricaoPesquisaTiposCaracteristicaLeito) {
		return getAinTipoCaracteristicaLeitoDAO().pesquisaTiposCaracteristicaLeito(firstResult, maxResults, orderProperty, asc,
				codigoPesquisaTiposCaracteristicaLeito, descricaoPesquisaTiposCaracteristicaLeito);
	}

	/**
	 * Retorna uma lista de tipos de caracteríticas de leitos
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaracteristicaLeito','pesquisar')}")
	public List<AinTipoCaracteristicaLeito> pesquisarTipoCaracteristicaPorCodigoOuDescricao(final String string) {
		return getAinTipoCaracteristicaLeitoDAO().pesquisarTipoCaracteristicaPorCodigoOuDescricao(string);
	}

	protected TiposCaracteristicaLeitoCRUD getTiposCaracteristicaLeitoCRUD() {
		return tiposCaracteristicaLeitoCRUD;
	}

	protected AinTipoCaracteristicaLeitoDAO getAinTipoCaracteristicaLeitoDAO() {
		return ainTipoCaracteristicaLeitoDAO;
	}

	// Manter Tipos de Altas Medicas
	// =================================================================
	// TiposAltaMedicaCRUD/

	/**
	 * Método responsável pela persistência.
	 * 
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','alterar')}")
	public void persistirTipoAltaMedica(final AinTiposAltaMedica tipoAltaMedica, final boolean edicaoOuInclusao)
			throws ApplicationBusinessException {
		getTiposAltaMedicaCRUD().persistirTipoAltaMedica(tipoAltaMedica, edicaoOuInclusao);
	}

	/**
	 * Retorna um TipoAltaMedica com base na chave primária.
	 * 
	 * @param seq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public AinTiposAltaMedica obterTipoAltaMedica(final String codigo) {
		return getAinTiposAltaMedicaDAO().obterTipoAltaMedica(codigo);
	}

	/**
	 * Método responsável por remover um registro do tipo TipoAltaMedica.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','excluir')}")
	public void removerTipoAltaMedica(final String codigo) throws ApplicationBusinessException {
		getTiposAltaMedicaCRUD().removerTipoAltaMedica(codigo);
	}

	@Override
	public Long pesquisaTiposAltaMedicaCount(final String codigo, final MpmMotivoAltaMedica motivoAltaMedica, final DominioSituacao situacao) {
		return getAinTiposAltaMedicaDAO().pesquisaTiposAltaMedicaCount(codigo, motivoAltaMedica, situacao);
	}

	/**
	 * Busca tipos de altas médicas, conformes os parâmetros passados.
	 * 
	 * @param codigo
	 * @param motivoAltaMedica
	 * @param indSituacao
	 * @return List<AinTiposAltaMedica>
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public List<AinTiposAltaMedica> pesquisaTiposAltaMedica(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final String codigo, final MpmMotivoAltaMedica motivoAltaMedica,
			final DominioSituacao situacao) {
		return getAinTiposAltaMedicaDAO().pesquisaTiposAltaMedica(firstResult, maxResults, orderProperty, asc, codigo, motivoAltaMedica,
				situacao);

	}

	/**
	 * Metodo que busca objetos MotivosAltasMedicas pela descricao OU seq.
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public List<MpmMotivoAltaMedica> pesquisarMotivosAltaMedica(final String strPesq) {
		return getAinTiposAltaMedicaDAO().pesquisarMotivosAltaMedica(strPesq);
	}

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a descrição
	 * passada como parêmetro. É utilizado pelo converter
	 * MpmMotivoAltaMedicasConverter.
	 * 
	 * @param descricao
	 * @return MpmMotivoAltaMedicas
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public MpmMotivoAltaMedica pesquisarMotivosAltaMedicaPorDescricao(final String valor) {
		return getAinTiposAltaMedicaDAO().pesquisarMotivosAltaMedicaPorDescricao(valor);
	}

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a string
	 * passada como parêmetro, que é comparada com o codigo
	 * 
	 * @param codigo
	 *            , idsIgnorados (ids de tipos alta medica que não devem ser
	 *            retornados pela consulta)
	 * @return Tipo Alta Médica
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public AinTiposAltaMedica pesquisarTipoAltaMedicaPorCodigo(final String strPesquisa, final String[] idsIgnorados) {
		return getAinTiposAltaMedicaDAO().pesquisarTipoAltaMedicaPorCodigo(strPesquisa, idsIgnorados);
	}

	/**
	 * Método resposável por buscar um Tipo Alta Médica, conforme a string
	 * passada como parâmetro, que é comparada com o codigo e a descricao do
	 * Tipo Alta Médica É utilizado pelo converter AinTiposAltaMedicaConverter.
	 * 
	 * @param descricao
	 *            ou codigo, idsFiltrados (ids de tipos alta medica que podem
	 *            estar entre os retornados pela consulta)
	 * @return Lista de Tipos Alta Médica
	 */
	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(final String param, final String[] idsFiltrados) {
		return getAinTiposAltaMedicaDAO().pesquisarTipoAltaMedicaPorCodigoEDescricao(param, idsFiltrados);
	}

	@Override
	@Secure("#{s:hasPermission('tipoAltaMedica','pesquisar')}")
	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(final String codDescLov) {
		return getAinTiposAltaMedicaDAO().pesquisarTipoAltaMedicaPorCodigoEDescricao(codDescLov);
	}

	protected TiposAltaMedicaCRUD getTiposAltaMedicaCRUD() {
		return tiposAltaMedicaCRUD;
	}

	protected AinTiposAltaMedicaDAO getAinTiposAltaMedicaDAO() {
		return ainTiposAltaMedicaDAO;
	}

	// Manter Tipos de Situação de Leitos
	// =================================================================
	// TiposSituacaoLeitoCRUD

	/**
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public AinTiposMovimentoLeito obterTipoSituacaoLeito(final short shortValue) {
		return getAinTiposMovimentoLeitoDAO().obterTipoSituacaoLeito(shortValue);
	}

	/**
	 * Método responsável pela persistência de um tipo de situação de leito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select,insert
	 * 
	 * @param tipoSituacaoLeito
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','alterar')}")
	public void criarTipoSituacaoLeito(final AinTiposMovimentoLeito ainTipoSituacaoLeito) throws ApplicationBusinessException {
		getTiposSituacaoLeitoCRUD().criarTipoSituacaoLeito(ainTipoSituacaoLeito);
	}

	/**
	 * Método responsável pela alteração de um tipo de situação de leito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select,update
	 * 
	 * @param tipoSituacaoLeito
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','alterar')}")
	public void alterarTipoSituacaoLeito(final AinTiposMovimentoLeito ainTipoSituacaoLeito) throws ApplicationBusinessException {
		getTiposSituacaoLeitoCRUD().alterarTipoSituacaoLeito(ainTipoSituacaoLeito);
	}

	/**
	 * Apaga um tipo de situação de leito do banco de dados.
	 * 
	 * @dbtables AinTiposMovimentoLeito delete
	 * 
	 * @param tipoSituacaoLeito
	 *            Tipo de Situação de Leito a ser removido.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','excluir')}")
	public void removerTipoSituacaoLeito(final Short codigo) throws ApplicationBusinessException {
		getTiposSituacaoLeitoCRUD().removerTipoSituacaoLeito(codigo);
	}

	@Override
	public Long pesquisaTipoSituacaoLeitoCount(final Short codigoPesquisaTipoSituacaoLeito,
			final String descricaoPesquisaTipoSituacaoLeito, final DominioMovimentoLeito grupoMvtoLeitoPesquisaTipoSituacaoLeito,
			final DominioSimNao indNecessitaLimpezaPesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustificativaPesquisaTipoSituacaoLeito,
			final DominioSimNao indBloqueioPacientePesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustLiberacaoPesquisaTipoSituacaoLeito) {
		return getAinTiposMovimentoLeitoDAO().pesquisaCount(codigoPesquisaTipoSituacaoLeito, descricaoPesquisaTipoSituacaoLeito,
				grupoMvtoLeitoPesquisaTipoSituacaoLeito, indNecessitaLimpezaPesquisaTipoSituacaoLeito,
				indExigeJustificativaPesquisaTipoSituacaoLeito, indBloqueioPacientePesquisaTipoSituacaoLeito,
				indExigeJustLiberacaoPesquisaTipoSituacaoLeito);

	}

	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public List<AinTiposMovimentoLeito> pesquisaTipoSituacaoLeito(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Short codigoPesquisaTipoSituacaoLeito,
			final String descricaoPesquisaTipoSituacaoLeito, final DominioMovimentoLeito grupoMvtoLeitoPesquisaTipoSituacaoLeito,
			final DominioSimNao indNecessitaLimpezaPesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustificativaPesquisaTipoSituacaoLeito,
			final DominioSimNao indBloqueioPacientePesquisaTipoSituacaoLeito,
			final DominioSimNao indExigeJustLiberacaoPesquisaTipoSituacaoLeito) {
		return getAinTiposMovimentoLeitoDAO().pesquisa(firstResult, maxResults, AinTiposMovimentoLeito.Fields.CODIGO.toString(), true,
				codigoPesquisaTipoSituacaoLeito, descricaoPesquisaTipoSituacaoLeito, grupoMvtoLeitoPesquisaTipoSituacaoLeito,
				indNecessitaLimpezaPesquisaTipoSituacaoLeito, indExigeJustificativaPesquisaTipoSituacaoLeito,
				indBloqueioPacientePesquisaTipoSituacaoLeito, indExigeJustLiberacaoPesquisaTipoSituacaoLeito);
	}

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * com situação de bloqueio limpeza (BL)<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * Ordernado por codigo.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public AinTiposMovimentoLeito pesquisarTipoSituacaoBloqueioLimpezaPorDescricao(final String desc) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoBloqueioLimpezaPorDescricao(desc);
	}

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * 
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoBloqueados(final Short codigo) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoBloqueados(codigo);
	}

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * Ordenado por grupoMovimentoLeito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoBloqueadosPorDescricao(final String descricaoStatus) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoBloqueadosPorDescricao(descricaoStatus);
	}
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * E codigo.<br>
	 * Ordenado por grupoMovimentoLeito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param descricao
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoBloqueadosPorDescricaoOuCodigo(final String descricao) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoBloqueadosPorDescricaoOuCodigo(descricao);
	}

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametro.<br>
	 * E grupoMovimentoLeito: L.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoDesocupado(final Short codigo) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoDesocupado(codigo);
	}

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: L.<br>
	 * Ordenado por grupoMovimentoLeito.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoDesocupadosPorDescricao(final String descricaoStatus) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoDesocupadosPorDescricao(descricaoStatus);
	}

	/**
	 * Retorna AinTiposMovimentoLeito para Tipos de Reserva. Filtros: codigo<br>
	 * grupoMovimentoLeito: L.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoReservados(final Short codigo) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoReservados(codigo);
	}

	/**
	 * Retorna AinTiposMovimentoLeito para Tipos de Reserva.<br>
	 * Filtros: Descricao,<br>
	 * grupoMovimentoLeito: R<br>
	 * Ordenado por Grupo movimento Leito.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoReservadosPorDescricao(final String descricaoTipoReserva) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoReservadosPorDescricao(descricaoTipoReserva);
	}

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoSituacao: B, BI, BL, D, O, L, R.<br>
	 * Ordenado por codigo.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentacaoLeito','pesquisar')}")
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoPorDescricao(final String descricaoStatus) {
		return getAinTiposMovimentoLeitoDAO().pesquisarTipoSituacaoLeitoPorDescricao(descricaoStatus);
	}

	// Manter Tipos de Movimentos de Internações
	// =================================================================
	// TiposMvtoInternacaoCRUD

	@Override
	@Secure("#{s:hasPermission('tipoMovimentoInternacao','pesquisar')}")
	public AinTiposMvtoInternacao obterTiposMvtoInternacao(final Integer ainTiposMvtoInternacaoCodigo) {
		return getAinTiposMvtoInternacaoDAO().obterTiposMvtoInternacao(ainTiposMvtoInternacaoCodigo);
	}

	/**
	 * Método responsável pela persistência de um tipo de movimento de
	 * internação.
	 * 
	 * @param tipo
	 *            de movimento de internação
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentoInternacao','alterar')}")
	public void persistirTiposMvtoInternacao(final AinTiposMvtoInternacao ainTiposMvtoInternacao) throws ApplicationBusinessException {
		getTiposMvtoInternacaoCRUD().persistirTiposMvtoInternacao(ainTiposMvtoInternacao);
	}

	/**
	 * Apaga um tipo de movimento de internação do banco de dados.
	 * 
	 * @param tipo
	 *            de movimento de internação Tipo de movimento de internação a
	 *            ser removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoMovimentoInternacao','excluir')}")
	public void removerTiposMvtoInternacao(final Integer codigo) throws ApplicationBusinessException {
		getTiposMvtoInternacaoCRUD().removerTiposMvtoInternacao(codigo);
	}

	@Override
	public Long pesquisaTiposMvtoInternacaoCount(final Integer codigoPesquisaTiposMvtoInternacao,
			final String descricaoPesquisaTiposMvtoInternacao) {
		return getAinTiposMvtoInternacaoDAO().pesquisaTiposMvtoInternacaoCount(codigoPesquisaTiposMvtoInternacao,
				descricaoPesquisaTiposMvtoInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('tipoMovimentoInternacao','pesquisar')}")
	public List<AinTiposMvtoInternacao> pesquisaTiposMvtoInternacao(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposMvtoInternacao,
			final String descricaoPesquisaTiposMvtoInternacao) {
		return getAinTiposMvtoInternacaoDAO().pesquisaTiposMvtoInternacao(firstResult, maxResults, orderProperty, asc,
				codigoPesquisaTiposMvtoInternacao, descricaoPesquisaTiposMvtoInternacao);
	}

	// Manter Observações para Alta de Paciente
	// =================================================================
	// ObservacoesPacAltaCRUD

	@Override
	@Secure("#{s:hasPermission('observacaoAltaPaciente','pesquisar')}")
	public AinObservacoesPacAlta obterObservacoesPacAlta(final Integer ainObservacoesPacAltaCodigo) {
		return getAinObservacoesPacAltaDAO().obterObservacoesPacAlta(ainObservacoesPacAltaCodigo);
	}

	/**
	 * Método responsável pela persistência de uma observação de alta do
	 * paciente.
	 * 
	 * @param observação
	 *            de alta do paciente
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('observacaoAltaPaciente','alterar')}")
	public void persistirObservacoesPacAlta(final AinObservacoesPacAlta ainObservacoesPacAlta) throws ApplicationBusinessException {
		getObservacoesPacAltaCRUD().persistirObservacoesPacAlta(ainObservacoesPacAlta);
	}

	/**
	 * Apaga uma observação de alta do paciente do banco de dados.
	 * 
	 * @param observação
	 *            de alta do paciente Observação de alta do paciente a ser
	 *            removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('observacaoAltaPaciente','excluir')}")
	public void removerObservacoesPacAlta(final Integer codigo) throws ApplicationBusinessException {
		getObservacoesPacAltaCRUD().removerObservacoesPacAlta(codigo);
	}

	@Override
	public Long pesquisaObservacoesPacAltaCount(final Integer codigoPesquisaObservacoesPacAlta,
			final String descricaoPesquisaObservacoesPacAlta, final DominioSituacao situacaoPesquisaObservacoesPacAlta) {
		return getAinObservacoesPacAltaDAO().pesquisaObservacoesPacAltaCount(codigoPesquisaObservacoesPacAlta,
				descricaoPesquisaObservacoesPacAlta, situacaoPesquisaObservacoesPacAlta);
	}

	@Override
	@Secure("#{s:hasPermission('observacaoAltaPaciente','pesquisar')}")
	public List<AinObservacoesPacAlta> pesquisaObservacoesPacAlta(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaObservacoesPacAlta,
			final String descricaoPesquisaObservacoesPacAlta, final DominioSituacao situacaoPesquisaObservacoesPacAlta) {
		return getAinObservacoesPacAltaDAO().pesquisaObservacoesPacAlta(firstResult, maxResults, orderProperty, asc,
				codigoPesquisaObservacoesPacAlta, descricaoPesquisaObservacoesPacAlta, situacaoPesquisaObservacoesPacAlta);
	}

	@Override
	@Secure("#{s:hasPermission('observacaoAltaPaciente','pesquisar')}")
	public List<AinObservacoesPacAlta> pesquisarObservacoesPacAlta(final Object parametro) {
		return getAinObservacoesPacAltaDAO().pesquisarObservacoesPacAlta(parametro);
	}

	// Manter Tipos Carater de Internação
	// =================================================================
	// getAinTiposCaraterInternacaoDAO
	// TiposCaraterInternacaoCRUD

	@Override
	@Secure("#{s:hasPermission('tipoCaraterInternacao','pesquisar')}")
	public AinTiposCaraterInternacao obterTiposCaraterInternacao(final Integer ainTiposCaraterInternacaoCodigo) {
		return getAinTiposCaraterInternacaoDAO().obterTiposCaraterInternacao(ainTiposCaraterInternacaoCodigo);
	}

	/**
	 * Método responsável pela persistência de um tipo de caráter de internação.
	 * 
	 * @param tipo
	 *            de caráter de internação
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaraterInternacao','alterar')}")
	public void persistirTiposCaraterInternacao(final AinTiposCaraterInternacao ainTiposCaraterInternacao)
			throws ApplicationBusinessException {
		getTiposCaraterInternacaoCRUD().persistirTiposCaraterInternacao(ainTiposCaraterInternacao);
	}

	/**
	 * Apaga um tipo de caráter de internação do banco de dados.
	 * 
	 * @param tipo
	 *            de caráter de internação tipo de caráter de internação a ser
	 *            removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoCaraterInternacao','excluir')}")
	public void removerTiposCaraterInternacao(final Integer codigo) throws ApplicationBusinessException {
		getTiposCaraterInternacaoCRUD().removerTiposCaraterInternacao(codigo);
	}

	@Override
	public Long pesquisaTiposCaraterInternacaoCount(final Integer codigoPesquisaTiposCaraterInternacao,
			final String descricaoPesquisaTiposCaraterInternacao, final Integer codigoSUSPesquisaTiposCaraterInternacao,
			final DominioSimNao caraterPesquisaTiposCaraterInternacao) {
		return getAinTiposCaraterInternacaoDAO().pesquisaTiposCaraterInternacaoCount(codigoPesquisaTiposCaraterInternacao,
				descricaoPesquisaTiposCaraterInternacao, codigoSUSPesquisaTiposCaraterInternacao, caraterPesquisaTiposCaraterInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('tipoCaraterInternacao','pesquisar')}")
	public List<AinTiposCaraterInternacao> pesquisaTiposCaraterInternacao(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposCaraterInternacao,
			final String descricaoPesquisaTiposCaraterInternacao, final Integer codigoSUSPesquisaTiposCaraterInternacao,
			final DominioSimNao caraterPesquisaTiposCaraterInternacao) {
		return getAinTiposCaraterInternacaoDAO().pesquisaTiposCaraterInternacao(firstResult, maxResults, orderProperty, asc,
				codigoPesquisaTiposCaraterInternacao, descricaoPesquisaTiposCaraterInternacao, codigoSUSPesquisaTiposCaraterInternacao,
				caraterPesquisaTiposCaraterInternacao);
	}

	// Manter Tipos de Unidades Funcionais
	// =================================================================

	/**
	 * Método responsável pela persistência de um tipo de unidade funcional.
	 * 
	 * @param tipo
	 *            de unidade funcional
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoUnidadeFuncional','alterar')}")
	public void persistirTiposUnidadeFuncional(final AghTiposUnidadeFuncional aghTiposUnidadeFuncional) throws ApplicationBusinessException {
		getTiposUnidadeFuncionalCRUD().persistirTiposUnidadeFuncional(aghTiposUnidadeFuncional);
	}

	/**
	 * Apaga um tipo de unidade funcional do banco de dados.
	 * 
	 * @param tipo
	 *            de unidade funcional Tipo de unidade funcional a ser removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('tipoUnidadeFuncional','excluir')}")
	public void removerTiposUnidadeFuncional(final Integer codigoTipoUnidFunc) throws ApplicationBusinessException {
		getTiposUnidadeFuncionalCRUD().removerTiposUnidadeFuncional(codigoTipoUnidFunc);
	}

	@Override
	public Long pesquisaTiposUnidadeFuncionalCount(final Integer codigoPesquisaTiposUnidadeFuncional,
			final String descricaoPesquisaTiposUnidadeFuncional) {
		return getTiposUnidadeFuncionalCRUD().pesquisaTiposUnidadeFuncionalCount(codigoPesquisaTiposUnidadeFuncional,
				descricaoPesquisaTiposUnidadeFuncional);
	}

	@Override
	@Secure("#{s:hasPermission('tipoUnidadeFuncional','pesquisar')}")
	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final Integer codigoPesquisaTiposUnidadeFuncional,
			final String descricaoPesquisaTiposUnidadeFuncional) {
		return getTiposUnidadeFuncionalCRUD().pesquisaTiposUnidadeFuncional(firstResult, maxResults, orderProperty, asc,
				codigoPesquisaTiposUnidadeFuncional, descricaoPesquisaTiposUnidadeFuncional);
	}

	@Override
	@Secure("#{s:hasPermission('tipoUnidadeFuncional','pesquisar')}")
	public List<AghTiposUnidadeFuncional> listarPorNomeOuCodigo(final String strPesquisa) {
		return getTiposUnidadeFuncionalCRUD().listarPorNomeOuCodigo(strPesquisa);
	}

	// Manter Acomodacoes
	// =================================================================

	/**
	 * Retorna uma acomodação com base na chave primária.
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param seq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('acomodacao','pesquisar')}")
	public AinAcomodacoes obterAcomodacao(final Integer ainAcomodacaoCodigo) {
		return this.getAcomodacaoCRUD().obterAcomodacao(ainAcomodacaoCodigo);
	}

	/**
	 * Método responsável pela persistência de uma Acomodação.
	 * 
	 * @dbtables AinAcomodacoes select,insert,update
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('acomodacao','alterar')}")
	public void persistirAcomodacoes(final AinAcomodacoes ainAcomodacao) throws ApplicationBusinessException {
		this.getAcomodacaoCRUD().persistirAcomodacoes(ainAcomodacao);
	}

	@Override
	public Long pesquisaAcomodacoesCount(final Integer codigoPesquisaAcomocadao, final String descricaoPesquisaAcomodacao) {
		return this.getAcomodacaoCRUD().pesquisaAcomodacoesCount(codigoPesquisaAcomocadao, descricaoPesquisaAcomodacao);
	}

	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('acomodacao','pesquisar')}")
	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricao(final Object parametro) {
		return getAinAcomodacoesDAO().pesquisarAcomodacoesPorCodigoOuDescricao(parametro == null ? null : parametro.toString());
	}

	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('acomodacao','pesquisar')}")
	public List<AinAcomodacoes> pesquisarAcomodacoes(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Integer codigoPesquisaAcomocadao, final String descricaoPesquisaAcomodacao) {
		return this.getAcomodacaoCRUD().pesquisarAcomodacoes(firstResult, maxResult, orderProperty, asc, codigoPesquisaAcomocadao,
				descricaoPesquisaAcomodacao);
	}

	/**
	 * Método responsável pela remoção de uma Acomodação.
	 * 
	 * @dbtables AinAcomodacoes delete
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('acomodacao','excluir')}")
	public void removerAcomodacao(final Integer codigo) throws ApplicationBusinessException {
		this.getAcomodacaoCRUD().removerAcomodacao(codigo);
	}

	/**
	 * Retorna uma lista de acomodações ordenado por descrição
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('acomodacao','pesquisar')}")
	public List<AinAcomodacoes> pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(final Object objParam) {
		return getAinAcomodacoesDAO().pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(objParam);
	}

	// =================================================================

	/**
	 * Método responsável pela persistência de uma Instituição Hospitalar.
	 * 
	 * @param Instituição
	 *            Hospitalar
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('instituicaoHospitalar','alterar')}")
	public void persistirInstituicao(final AghInstituicoesHospitalares instituicao) throws ApplicationBusinessException {
		this.getInstituicaoHospitalarCRUD().persistirInstituicao(instituicao);

	}

	@Override
	@Secure("#{s:hasPermission('instituicaoHospitalar','excluir')}")
	public void removerInstituicaoHospitalar(
			final AghInstituicoesHospitalares instituicao)
			throws ApplicationBusinessException {		
		this.getInstituicaoHospitalarCRUD().removerInstituicao(instituicao);
	}

	/**
	 * Indica qual a instituição está usando o sistema.
	 * 
	 * @return
	 */
	@Override
	public String recuperarNomeInstituicaoHospitalarLocal() {

		return this.getInstituicaoHospitalarCRUD().recuperarNomeInstituicaoLocal();

	}

	// Geters e Setters

	/**
	 * Obtem a classe CRUD relativa ao pojo AghInstituicoesHospitalares
	 * 
	 * @return
	 */
	private InstituicaoHospitalarCRUD getInstituicaoHospitalarCRUD() {
		return instituicaoHospitalarCRUD;
	}

	private AcomodacaoCRUD getAcomodacaoCRUD() {
		return acomodacaoCRUD;
	}

	protected AinAcomodacoesDAO getAinAcomodacoesDAO() {
		return ainAcomodacoesDAO;
	}

	protected TiposUnidadeFuncionalCRUD getTiposUnidadeFuncionalCRUD() {
		return tiposUnidadeFuncionalCRUD;
	}

	protected TiposCaraterInternacaoCRUD getTiposCaraterInternacaoCRUD() {
		return tiposCaraterInternacaoCRUD;
	}

	protected AinTiposCaraterInternacaoDAO getAinTiposCaraterInternacaoDAO() {
		return ainTiposCaraterInternacaoDAO;
	}

	protected ObservacoesPacAltaCRUD getObservacoesPacAltaCRUD() {
		return observacoesPacAltaCRUD;
	}

	protected AinObservacoesPacAltaDAO getAinObservacoesPacAltaDAO() {
		return ainObservacoesPacAltaDAO;
	}

	protected LeitosCRUD getLeitosCRUD() {
		return leitosCRUD;
	}

	protected AlaCRUD getAlaCRUD() {
		return alaCRUD;
	}

	protected QuartoCRUD getQuartoCRUD() {
		return quartoCRUD;
	}

	protected EspecialidadeCRUD getEspecialidadeCRUD() {
		return especialidadeCRUD;
	}

	protected TiposMvtoInternacaoCRUD getTiposMvtoInternacaoCRUD() {
		return tiposMvtoInternacaoCRUD;
	}

	protected AinTiposMvtoInternacaoDAO getAinTiposMvtoInternacaoDAO() {
		return ainTiposMvtoInternacaoDAO;
	}

	protected TiposSituacaoLeitoCRUD getTiposSituacaoLeitoCRUD() {
		return tiposSituacaoLeitoCRUD;
	}

	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}

	@Override
	public void cancelarImpressao() throws ApplicationBusinessException {
		this.getImpressoraPadraoON().cancelarImpressao();
	}

	protected ImpressoraPadraoON getImpressoraPadraoON() {
		return impressoraPadraoON;
	}

	@Override
	public String obterDescricaoTipoMovtoInternacao(final Byte codigo) {
		return getAinTiposMvtoInternacaoDAO().obterDescricaoTipoMovtoInternacao(codigo);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(final Object objPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(objPesquisa);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncional(strPesquisa);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(String strPesquisa){
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalInternacaoAtiva(strPesquisa);
		
	}

	@Override
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(final Object objPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalVOPorCodigoEDescricao(objPesquisa);
	}

	@Override
	public AghUnidadesFuncionaisVO popularUnidadeFuncionalVO(final AghUnidadesFuncionais unidadeFuncional) {
		return this.getUnidadeFuncionalCRUD().popularUnidadeFuncionalVO(unidadeFuncional);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public AghUnidadesFuncionaisVO obterUnidadeFuncionalVO(final Short codigo) {
		return this.getUnidadeFuncionalCRUD().obterUnidadeFuncionalVO(codigo);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final Object objPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoEDescricao(objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas(final Object objPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas(objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergencia(
			final Object objPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergencia(
				objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoCaractInternacaoOuEmergenciaAtivasInativas(
			final Object objPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoDescricaoCaractInternacaoOuEmergenciaAtivasInativas(
				objPesquisa);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(final String param) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(param);
	}
	
	@Override
	public Long pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(
			String param) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(param);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalOrdenado(final String strPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalOrdenado(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','alterar')}")
	public void atualizarUnidadeFuncionalidade(final AghUnidadesFuncionais unidadeFuncional, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException {
		this.getUnidadeFuncionalCRUD().atualizarUnidadeFuncionalidade(unidadeFuncional, caracteristicas);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','alterar')}")
	public void incluirUnidadeFuncional(final AghUnidadesFuncionais unidade, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException {
		this.getUnidadeFuncionalCRUD().incluirUnidadeFuncional(unidade, caracteristicas);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','excluir')}")
	public void excluirUnidade(final Short seq) throws ApplicationBusinessException {
		this.getUnidadeFuncionalCRUD().excluirUnidade(seq);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(final Object param) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames((String) param);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(
			final Object param) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(
				(String) param);
	}

	@Override
	public Integer pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(final Object param) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoEDescricaoCount((String) param);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesAtivasOrdenadaDescricao(final Object param) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesAtivasOrdenadaDescricao((String) param);
	}

	protected UnidadeFuncionalCRUD getUnidadeFuncionalCRUD() {
		return unidadeFuncionalCRUD;
	}

	/**
	 * método que obtem um leito através da chave primária.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param id
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos obterLeitoPorId(final String id) {
		return getLeitosCRUD().obterLeitoPorId(
				id,
				new Enum[] { AinLeitos.Fields.UNIDADE_FUNCIONAL },
				new Enum[] { AinLeitos.Fields.QUARTO, AinLeitos.Fields.TIPO_MOVIMENTO_LEITO, AinLeitos.Fields.ACOMODACAO,
						AinLeitos.Fields.CLINICA });
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos obterLeitoPorId(final String id, Enum[] innerFields, Enum[] leftFields) {
		return getLeitosCRUD().obterLeitoPorId(id, innerFields, leftFields);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos obterLeitoPorId(String id, Enum... fields) {
		return getLeitosCRUD().obterLeitoPorId(id, fields);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos obterLeitoInternacaoPorId(final String id) {
		AinLeitos leito = getLeitosCRUD().obterLeitoPorId(id, new Enum[] { AinLeitos.Fields.TIPO_MOVIMENTO_LEITO },
				new Enum[] { AinLeitos.Fields.INTERNACAO });
		if (leito != null && leito.getInternacao() != null) {
			leito.getInternacao().getPaciente();
		}

		return leito;
	}

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitos(final Object paramentro) {
		return getLeitosCRUD().pesquisarLeitos(paramentro);
	}

	@Override
	public boolean permitirInternar(final String leitoId) {
		return getLeitosCRUD().permitirInternar(leitoId);
	}

	/**
	 * Método para obter a descrição completa do leito (leito, andar, ala,
	 * descrição da unidade funcional)
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param numeroQuarto
	 * @return Descrição completa do quarto
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public String obterDescricaoCompletaLeito(final String leitoId) {
		return getLeitosCRUD().obterDescricaoCompletaLeito(leitoId);
	}

	@Override
	public void verificarInternar(final String leitoId) throws ApplicationBusinessException {
		getLeitosCRUD().verificarInternar(leitoId);
	}

	/**
	 * @dbtables AghEspecialidades select
	 * @param seq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public AghEspecialidades obterEspecialidade(final Short seq) {
		return getEspecialidadeCRUD().obterEspecialidade(seq);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public AghEspecialidades obterAghEspecialidades(Short chavePrimaria, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getEspecialidadeCRUD().obterAghEspecialidadesPorChavePrimaria(chavePrimaria, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosOrdenado(final Object paramentro) {
		return getLeitosCRUD().pesquisarLeitosOrdenado(paramentro);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisaLeitoPeloLeitoId(final String leito) {
		return getLeitosCRUD().pesquisaLeitoPeloLeitoId(leito);
	}

	@Override
	public ImpImpressora obterImpressora(final AghParametros aghParam, final TipoDocumentoImpressao tipoImpressora)
			throws ApplicationBusinessException {
		return this.getImpressoraPadraoON().obterImpressora(aghParam, tipoImpressora);
	}

	@Override
	public ImpImpressora obterImpressora(final Short unfSeq, final TipoDocumentoImpressao tipoImpressora)
			throws ApplicationBusinessException {
		return this.getImpressoraPadraoON().obterImpressora(unfSeq, tipoImpressora);
	}

	@Override
	public Boolean verificaExisteImpressoraPadrao(final Short unfSeq, final TipoDocumentoImpressao tipoImpressora)
			throws ApplicationBusinessException {
		return this.getImpressoraPadraoON().verificaExisteImpressoraPadrao(unfSeq, tipoImpressora);
	}

	/**
	 * @param strPesquisa
	 * @param idadePaciente
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadeSolicitacaoInternacao(final String strPesquisa, final Short idadePaciente) {
		return getEspecialidadeCRUD().pesquisarEspecialidadeSolicitacaoInternacao(strPesquisa, idadePaciente);
	}

	/**
	 * Pesquisa de especialidade genérica por nome ou código
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadeGenerica(final String strPesquisa) {
		return getEspecialidadeCRUD().pesquisarEspecialidadeGenerica(strPesquisa);
	}

	@Override
	public List<AghEspecialidades> pesquisarTodasEspecialidades(String strPesquisa) {
		return getEspecialidadeCRUD().pesquisarTodasEspecialidades(strPesquisa);
	}

	@Override
	public Long pesquisarTodasEspecialidadesCount(String strPesquisa) {
		return getEspecialidadeCRUD().pesquisarTodasEspecialidadesCount(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public Long pesquisarEspecialidadeGenericaCount(final String strPesquisa) {
		return getEspecialidadeCRUD().pesquisarEspecialidadeGenericaCount(strPesquisa);
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla
	 * 
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSigla(final Object paramPesquisa) {
		return this.getProfEspecialidadesCRUD().pesquisarEspecialidadesInternasPorSigla(paramPesquisa);
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla e nome
	 * 
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(final Object paramPesquisa) {
		return this.getProfEspecialidadesCRUD().pesquisarEspecialidadesInternasPorSiglaENome(paramPesquisa);
	}

	@Override
	public Long pesquisarEspecialidadesInternasPorSiglaENomeCount(final Object paramPesquisa) {
		return this.getProfEspecialidadesCRUD().pesquisarEspecialidadesInternasPorSiglaENomeCount(paramPesquisa);
	}

	/**
	 * Retorna leito desocupado.
	 * 
	 * @param paramentro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos obterLeitoDesocupado(final String leito) {
		return this.getLeitosCRUD().obterLeitoDesocupado(leito);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param paramentro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosDesocupados(final String paramentro) {
		return this.getLeitosCRUD().pesquisarLeitosDesocupados(paramentro);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosPorSituacoesDoLeito(Object strParamentro, DominioMovimentoLeito[] situacoesLeito) {
		return this.getLeitosCRUD().pesquisarLeitosPorSituacoesDoLeito(strParamentro, situacoesLeito);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param paramentro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos obterLeitoBloqueado(final String leito) {
		return this.getLeitosCRUD().obterLeitoBloqueado(leito);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param paramentro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosBloqueados(final Object paramentro) {
		return this.getLeitosCRUD().pesquisarLeitosBloqueados(paramentro);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosDesocupadosPorLeito(final Object parametro) {
		return this.getLeitosCRUD().pesquisarLeitosDesocupadosPorLeito(parametro);
	}

	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}

	@Override
	public Short buscarSeqUnidadeFuncionalSeqDoLeito(final String leitoID) {
		return getAinLeitosDAO().buscarSeqUnidadeFuncionalSeqDoLeito(leitoID);
	}

	@Override
	public Short buscarSeqUnidadeFuncionalSeqDoQuarto(final Short qrtNumero) {
		return getAinQuartosDAO().buscarSeqUnidadeFuncionalSeqDoQuarto(qrtNumero);
	}

	/**
	 * Método usado para remover as diárias da internação.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('diariaAutorizada','excluir')}")
	public void removerDiariasAutorizada(final List<AinDiariasAutorizadas> diariasAutorizadas) throws ApplicationBusinessException {
		getDiariaAutorizadaCRUD().removerDiariasAutorizada(diariasAutorizadas);
	}

	protected DiariaAutorizadaCRUD getDiariaAutorizadaCRUD() {
		return diariaAutorizadaCRUD;
	}

	/**
	 * Pesquisa de especialidade genérica por nome ou código levando em conta a
	 * idade do paciente internado e o índice de especialidade interna
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 *            , idadePaciente
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadeInternacao(final String strPesquisa, final Short idadePaciente,
			final DominioSimNao indUnidadeEmergencia) {
		return getEspecialidadeCRUD().pesquisarEspecialidadeInternacao(strPesquisa, idadePaciente, indUnidadeEmergencia);
	}

	/**
	 * Retorna uma lista de Diarias Hospitalares recebendo o codigo da
	 * internação como parametro
	 * 
	 * @param seqInternacao
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('diariaAutorizada','pesquisar')}")
	public List<AinDiariasAutorizadas> pesquisarDiariaPorCodigoInternacao(final Integer seqInternacao) {
		return getDiariaAutorizadaCRUD().pesquisarDiariaPorCodigoInternacao(seqInternacao);
	}

	@Override
	public String recuperarNomeInstituicaoLocal() {
		return this.getInstituicaoHospitalarCRUD().recuperarNomeInstituicaoLocal();
	}

	@Override
	@Secure("#{s:hasPermission('tipoDocumento','pesquisar')}")
	public List<FatTiposDocumento> obterTiposDocs(final String seqDesc) {
		return this.getTiposDocumentoCRUD().obterTiposDocs(seqDesc);
	}

	protected TiposDocumentoCRUD getTiposDocumentoCRUD() {
		return tiposDocumentoCRUD;
	}

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#pesquisarRapServidores(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String, boolean, java.lang.Integer,
	 *      java.lang.Integer, java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<RapServidores> pesquisarRapServidores(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer matricula, final Integer vinculo, final String nome) {
		return this.getProfEspecialidadesCRUD().pesquisarRapServidores(firstResult, maxResults, orderProperty, asc, matricula, vinculo,
				nome);
	}

	/**
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#pesquisarRapServidoresCount(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String)
	 */
	@Override
	public Long pesquisarRapServidoresCount(final Integer matricula, final Integer vinculo, final String nome) {
		return this.getProfEspecialidadesCRUD().pesquisarRapServidoresCount(matricula, vinculo, nome);
	}

	/**
	 * @param profEspecialidades
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#persistirProfEspecialidades(java.util.List)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidadeParaProfissional','alterar')}")
	public void persistirProfEspecialidades(final List<AghProfEspecialidades> profEspecialidades, RapServidoresId rapServidorId) throws ApplicationBusinessException {
		this.getProfEspecialidadesCRUD().persistirProfEspecialidades(profEspecialidades, rapServidorId);
	}

	/**
	 * @param profEspecialidades
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#validarDados(br.gov.mec.aghu.model.AghProfEspecialidades)
	 */
	@Override
	public void validarDados(final AghProfEspecialidades profEspecialidades) throws ApplicationBusinessException {
		this.getProfEspecialidadesCRUD().validarDados(profEspecialidades);
	}

	/**
	 * @param paramPesquisa
	 * @param ordemPorSigla
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#listarEspecialidadesAtivasPorSiglaOuDescricao(java.lang.Object,
	 *      boolean)
	 */
	@Override
	public List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(final Object paramPesquisa, final boolean ordemPorSigla) {
		return this.getProfEspecialidadesCRUD().listarEspecialidadesAtivasPorSiglaOuDescricao(paramPesquisa, ordemPorSigla);
	}

	/**
	 * @param strPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#pesquisarEspecialidadePorSiglaNome(java.lang.Object)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(final Object strPesquisa) {
		return this.getProfEspecialidadesCRUD().pesquisarEspecialidadePorSiglaNome(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public Long pesquisarEspecialidadePorSiglaNomeCount(final Object strPesquisa) {
		return this.getProfEspecialidadesCRUD().pesquisarEspecialidadePorSiglaNomeCount(strPesquisa);
	}

	/**
	 * @param aghProfEspecialidadesId
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfEspecialidadesCRUD#obterProfEspecialidades(br.gov.mec.aghu.model.AghProfEspecialidadesId)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidadeParaProfissional','pesquisar')}")
	public AghProfEspecialidades obterProfEspecialidades(final AghProfEspecialidadesId aghProfEspecialidadesId) {
		return this.getProfEspecialidadesCRUD().obterProfEspecialidades(aghProfEspecialidadesId);
	}

	/**
	 * @param rapServidor
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfissionaisEquipeCRUD#persistirRapServidor(br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	@Secure("#{s:hasPermission('equipeParaProfissionais','alterar')}")
	public void persistirRapServidor(final RapServidores rapServidor) throws ApplicationBusinessException {
		this.getProfissionaisEquipeCRUD().persistirRapServidor(rapServidor);
	}

	@Override
	public Integer pesquisaProfConveniosListCount(final Integer vinCodigo, final Integer matricula, final String nome, final Long cpf,
			final String siglaEspecialidade) {
		return this.getProfConveniosListON().pesquisaProfConveniosListCount(vinCodigo, matricula, nome, cpf, siglaEspecialidade);
	}

	@Override
	@Secure("#{s:hasPermission('convenioParaProfissional','pesquisar')}")
	public List<ProfConveniosListVO> pesquisaProfConvenioslist(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer vinCodigo, final Integer matricula, final String nome,
			final Long cpf, final String siglaEspecialidade) {
		return this.getProfConveniosListON().pesquisaProfConvenioslist(firstResult, maxResult, orderProperty, asc, vinCodigo, matricula,
				nome, cpf, siglaEspecialidade);
	}

	/**
	 * @param matricula
	 * @param vinculo
	 * @param esp
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfConveniosCRUD#obterAghProfEspecialidades(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('convenioParaProfissional','pesquisar')}")
	public AghProfEspecialidades obterAghProfEspecialidades(final Integer matricula, final Integer vinculo, final Integer esp) {
		return this.getProfConveniosCRUD().obterAghProfEspecialidades(matricula, vinculo, esp);
	}

	/**
	 * @param item
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfConveniosCRUD#removerAghProfEspConvenios(br.gov.mec.aghu.model.AghProfissionaisEspConvenio)
	 */
	@Override
	@Secure("#{s:hasPermission('convenioParaProfissional','excluir')}")
	public void removerAghProfEspConvenios(final AghProfissionaisEspConvenio item) throws ApplicationBusinessException {
		this.getProfConveniosCRUD().removerAghProfEspConvenios(item);
	}

	@Override
	public void inserirAghProfEspConvenios(AghProfissionaisEspConvenio aghProfissionaisEspConvenio) throws ApplicationBusinessException {
		this.getProfConveniosCRUD().inserirAghProfEspConvenios(aghProfissionaisEspConvenio);
	}

	@Override
	public void atualizarAghProfEspConvenios(AghProfissionaisEspConvenio aghProfissionaisEspConvenio) throws ApplicationBusinessException {
		this.getProfConveniosCRUD().atualizarAghProfEspConvenios(aghProfissionaisEspConvenio);
	}

	/**
	 * @param matricula
	 * @param vinculo
	 * @param esp
	 * @param convenio
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.ProfConveniosCRUD#verificarExclusao(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer, java.lang.Short)
	 */
	@Override
	@Secure("#{s:hasPermission('convenioParaProfissional','excluir')}")
	public Boolean verificarExclusao(final Integer matricula, final Integer vinculo, final Integer esp, final Short convenio) {
		return this.getProfConveniosCRUD().verificarExclusao(matricula, vinculo, esp, convenio);
	}

	@Override
	@Secure("#{s:hasPermission('origemEvento','pesquisar')}")
	public List<AghOrigemEventos> pesquisarOrigemEventoPorCodigoEDescricao(final Object objPesquisa) {
		return this.getOrigemEventoCRUD().pesquisarOrigemEventoPorCodigoEDescricao(objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('origemEvento','pesquisar')}")
	public AghOrigemEventos obterOrigemInternacao(final Short codigo) {
		return this.getOrigemEventoCRUD().obterOrigemInternacao(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacao(final Integer codigo) {
		AinInternacao internacao = this.getDiariaAutorizadaCRUD().obterInternacao(codigo);
		return inicializarAtributosInternacao(internacao);
	}

	private AinInternacao inicializarAtributosInternacao(AinInternacao internacao) {
		Hibernate.initialize(internacao.getEspecialidade());
		if (internacao.getEspecialidade() != null) {
			Hibernate.initialize(internacao.getEspecialidade().getClinica());
		}
		Hibernate.initialize(internacao.getServidorProfessor());
		if (internacao.getServidorProfessor() != null) {
			Hibernate.initialize(internacao.getServidorProfessor().getPessoaFisica());
			if (internacao.getServidorProfessor().getPessoaFisica() != null) {
				Hibernate.initialize(internacao.getServidorProfessor().getPessoaFisica().getQualificacoes());
			}
		}
		Hibernate.initialize(internacao.getPaciente());
		Hibernate.initialize(internacao.getLeito());
		if (internacao.getLeito() != null) {
			Hibernate.initialize(internacao.getLeito().getQuarto().getClinica());
		}
		Hibernate.initialize(internacao.getQuarto());
		Hibernate.initialize(internacao.getUnidadesFuncionais());
		Hibernate.initialize(internacao.getConvenioSaude());
		Hibernate.initialize(internacao.getConvenioSaudePlano());
		Hibernate.initialize(internacao.getDiariasAutorizadas());
		if (internacao.getDiariasAutorizadas() != null) {
			for (AinDiariasAutorizadas diaria : internacao.getDiariasAutorizadas()) {
				Hibernate.initialize(diaria.getSerMatricula());
				if (diaria.getSerMatricula() != null) {
					Hibernate.initialize(diaria.getSerMatricula().getPessoaFisica());
				}
			}
		}
		return internacao;
	}

	@Override
	@Secure("#{s:hasPermission('diariaAutorizada','excluir') && s:hasPermission('diariaAutorizada','alterar')}")
	public void atualizarListaDiariasAutorizadas(final List<AinDiariasAutorizadas> listaDiariasAutorizadas,
			final List<AinDiariasAutorizadas> listaDiariasOld) throws ApplicationBusinessException {
		this.getDiariaAutorizadaCRUD().atualizarListaDiariasAutorizadas(listaDiariasAutorizadas, listaDiariasOld);
	}

	@Override
	public List<AghImpressoraPadraoUnids> obterAghImpressoraPadraoUnids(final Short seq) {
		return this.getImpressoraPadraoON().obterAghImpressoraPadraoUnids(seq);
	}

	@Override
	public void validaCamposImpressoraPadrao(final TipoDocumentoImpressao tipoImpressao) throws ApplicationBusinessException {
		this.getImpressoraPadraoON().validaCamposImpressoraPadrao(tipoImpressao);
	}

	@Override
	public void incluirImpressora(final List<AghImpressoraPadraoUnids> listaImpressoras,
			final List<AghImpressoraPadraoUnids> listaImpressorasOld, final AghUnidadesFuncionais unidade)
			throws ApplicationBusinessException {
		this.getImpressoraPadraoON().incluirImpressora(listaImpressoras, listaImpressorasOld, unidade);
	}

	protected ProfConveniosListON getProfConveniosListON() {
		return profConveniosListON;
	}

	protected ProfissionaisEquipeCRUD getProfissionaisEquipeCRUD() {
		return profissionaisEquipeCRUD;
	}

	protected ProfEspecialidadesCRUD getProfEspecialidadesCRUD() {
		return profEspecialidadesCRUD;
	}

	protected ProfConveniosCRUD getProfConveniosCRUD() {
		return profConveniosCRUD;
	}

	protected OrigemEventoCRUD getOrigemEventoCRUD() {
		return origemEventoCRUD;
	}

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigoEspecialidade
	 * @param nomeEspecialidade
	 * @param siglaEspecialidade
	 * @param codigoEspGenerica
	 * @param centroCusto
	 * @param clinica
	 * @param situacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#pesquisarEspecialidades(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String, boolean, java.lang.Short,
	 *      java.lang.String, java.lang.String, java.lang.Short,
	 *      java.lang.Integer, java.lang.Integer,
	 *      br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidades(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Short codigoEspecialidade, final String nomeEspecialidade, final String siglaEspecialidade,
			final Short codigoEspGenerica, final Integer centroCusto, final Integer clinica, final DominioSituacao situacao) {
		return this.getEspecialidadeCRUD().pesquisarEspecialidades(firstResult, maxResult, orderProperty, asc, codigoEspecialidade,
				nomeEspecialidade, siglaEspecialidade, codigoEspGenerica, centroCusto, clinica, situacao);
	}

	/**
	 * @param codigo
	 * @param nomeEspecialidade
	 * @param siglaEspecialidade
	 * @param codigoEspGenerica
	 * @param centroCusto
	 * @param clinica
	 * @param situacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#pesquisarEspecialidadesCount(java.lang.Short,
	 *      java.lang.String, java.lang.String, java.lang.Short,
	 *      java.lang.Integer, java.lang.Integer,
	 *      br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public Long pesquisarEspecialidadesCount(final Short codigo, final String nomeEspecialidade, final String siglaEspecialidade,
			final Short codigoEspGenerica, final Integer centroCusto, final Integer clinica, final DominioSituacao situacao) {
		return this.getEspecialidadeCRUD().pesquisarEspecialidadesCount(codigo, nomeEspecialidade, siglaEspecialidade, codigoEspGenerica,
				centroCusto, clinica, situacao);
	}

	/**
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#gerarNovaEspecialidade()
	 */
	@Override
	public AghEspecialidades gerarNovaEspecialidade() {
		return this.getEspecialidadeCRUD().gerarNovaEspecialidade();
	}

	/**
	 * @param especialidade
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#persistirEspecialidade(br.gov.mec.aghu.model.AghEspecialidades)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','alterar')}")
	public void persistirEspecialidade(final AghEspecialidades especialidade) throws ApplicationBusinessException {
		this.getEspecialidadeCRUD().persistirEspecialidade(especialidade);
	}

	/**
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#obterEspecialidadeAtiva(short)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public AghEspecialidades obterEspecialidadeAtiva(final short seq) {
		return this.getEspecialidadeCRUD().obterEspecialidadeAtiva(seq);
	}

	/**
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.EspecialidadeCRUD#obterEspecialidadePediatria()
	 */
	@Override
	public List<AghEspecialidades> obterEspecialidadePediatria() {
		return this.getEspecialidadeCRUD().obterEspecialidadePediatria();
	}

	/**
	 * @param strPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.business.LeitosCRUD#pesquisarLeitosAtivosDesocupados(java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosAtivosDesocupados(final String strPesquisa) {
		return this.getLeitosCRUD().pesquisarLeitosAtivosDesocupados(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('ala','pesquisar')}")
	public AghAla buscaAghAlaPorId(final String codigo) {
		return getAlaCRUD().buscaAghAlaPorId(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('ala','excluir')}")
	public void excluirAghAla(final String codigo) throws ApplicationBusinessException {
		getAlaCRUD().excluirAghAla(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('ala','pesquisar')}")
	public List<AghAla> pesquisaAlaList(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final String codigo, final String descricao) {
		return getAlaCRUD().pesquisaAlaList(firstResult, maxResult, orderProperty, asc, codigo, descricao);
	}

	@Override
	@Secure("#{s:hasPermission('ala','pesquisar')}")
	public Long pesquisaAlaListCount(final String codigo, final String descricao) {
		return getAlaCRUD().pesquisaAlaListCount(codigo, descricao);
	}

	@Override
	@Secure("#{s:hasPermission('ala','alterar')}")
	public AghAla persistirAghAla(final AghAla ala, final boolean isUpdate) throws ApplicationBusinessException {
		return getAlaCRUD().persistirAghAla(ala, isUpdate);
	}

	@Override
	public void removerOrigemEventos(final AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		getOrigemInternacaoCRUD().removerOrigemEventos(origemEventos);
	}

	@Override
	public void persistirOrigemEventos(final AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		getOrigemInternacaoCRUD().persistirOrigemEventos(origemEventos);
	}

	protected OrigemInternacaoCRUD getOrigemInternacaoCRUD() {
		return origemInternacaoCRUD;
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String strPesquisa) {
		return this.getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalPorCodigoEDescricao(strPesquisa); 
	}

	@Override
	public Short pesquisarUnidadeFuncionalTriagemRecepcao(List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) {
		return getUnidadeFuncionalCRUD().pesquisarUnidadeFuncionalTriagemRecepcao(listaUnfSeqTriagemRecepcao, unfSeqMicroComputador);
	}
	
	/**
	 * @dbtables AinQuartos select
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param numero
	 * @return
	 */
	// @ORADB: RN_LTOP_VER_MED_PREV
	@BypassInactiveModule
	@Override
	public void validaLeitoMedidaPreventiva(Short numero, Short codigoMvtLeito) throws ApplicationBusinessException {
		getQuartoCRUD().validaLeitoMedidaPreventiva(numero, codigoMvtLeito);
	}

	@Override
	public List<AghProfEspecialidades> listarProfEspecialidadesPorServidor(RapServidores usuarioResponsavel) {
		return getProfEspecialidadesCRUD().listarProfEspecialidadesPorServidor(usuarioResponsavel);
	}

	@Override
	public List<AinLeitosVO> pesquisaLeitosPorNroQuarto(Short nroQuarto) {
		return this.getAinLeitosDAO().pesquisaLeitosPorNroQuarto(nroQuarto);
	}

	@Override
	public List<AinLeitos> pesquisaAinLeitosPorNroQuarto(Short nroQuarto) {
		return this.getAinLeitosDAO().pesquisaAinLeitosPorNroQuarto(nroQuarto);
	}

	@Override
	public List<AinCaracteristicaLeito> obterCaracteristicasDoLeito(String ltoId) {
		return getAinCaracteristicaLeitoDAO().obterCaracteristicasDoLeito(ltoId);
	}

	public AinCaracteristicaLeitoDAO getAinCaracteristicaLeitoDAO() {
		return ainCaracteristicaLeitoDAO;
	}
	
	@Override
	public List<AinLeitos> getLeitosPorQuarto(AinQuartos quarto) {
		return this.getLeitosCRUD().getLeitosPorQuarto(quarto);
	}
	
	@Override
	public void excluirLeitoSemMovimentacao(AinLeitos leito) throws ApplicationBusinessException{
		getLeitosCRUD().excluirLeitoSemMovimentacao(leito);

	}
	
	@Override
	public Boolean leitoPossuiMovimentacao(AinLeitosVO leito) throws ApplicationBusinessException {
		return getLeitosCRUD().leitoPossuiMovimentacao(leito);

	}

	@Override
	public List<AghEspecialidades> obterEspecialidadePorSiglas(List<String> siglas) {
		return this.getEspecialidadeCRUD().obterEspecialidadePorSiglas(siglas);
	}
}