/**
 * 
 */
package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.faturamento.business.FatExclusaoCriticaRN;
import br.gov.mec.aghu.faturamento.dao.FatCadCidNascimentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatExclusaoCriticaDAO;
import br.gov.mec.aghu.faturamento.dao.FatFormaOrganizacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatGrupoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatMetaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoRejeicaoContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatSubGrupoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoTratamentosDAO;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FatCadCidNascimento;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatCompetenciaProd;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoes;
import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatExclusaoCritica;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatPeriodosEmissao;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPacienteId;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatTipoTratamentos;

/**
 * @author marcelofilho
 * 
 */

@Modulo(ModuloEnum.FATURAMENTO)
@Stateless
public class FaturamentoApoioFacade extends BaseFacade implements IFaturamentoApoioFacade {

@EJB
private AtualizarMetaON atualizarMetaON;

@EJB
private FatCompetenciaProdON fatCompetenciaProdON;

@EJB
private ConvenioSaudeON convenioSaudeON;

@EJB
private FatCadCidNascimentoRN fatCadCidNascimentoRN;

@EJB
private FatMotivoRejeicaoContaON fatMotivoRejeicaoContaON;

@EJB
private FatExclusaoCriticaRN fatExclusaoCriticaRN;

@EJB
private FatMotivoSaidaPacienteON fatMotivoSaidaPacienteON;

@EJB
private FatSituacaoSaidaPacienteON fatSituacaoSaidaPacienteON;

	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;
	
	@Inject
	private FatMetaDAO fatMetaDAO;
	
	@Inject
	private FatSubGrupoDAO fatSubGrupoDAO;
	
	@Inject
	private FatFormaOrganizacaoDAO fatFormaOrganizacaoDAO;
	
	@Inject
	private FatGrupoDAO fatGrupoDAO;
	
	@Inject
	private FatCboDAO fatCboDAO;
	
	@Inject
	private FatExclusaoCriticaDAO fatExclusaoCriticaDAO;
	
	@Inject
	private FatTipoTratamentosDAO fatTipoTratamentosDAO;
	
	@Inject
	private FatCadCidNascimentoDAO fatCadCidNascimentoDAO;
	
	@Inject
	private FatMotivoRejeicaoContaDAO fatMotivoRejeicaoContaDAO;
	
	@Inject
	private FatMotivoSaidaPacienteDAO fatMotivoSaidaPacienteDAO;
	
	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;
	
	private static final long serialVersionUID = -6582430157795381846L;
//	private static final String ENTITY_MANAGER = "entityManager";
//	private static final String TRANSACTION = "org.jboss.seam.transaction.transaction";	
	private static final int TRANSACTION_TIMEOUT_20_MINUTOS = 20 * 60; //= 20 minutos

	@Override
	@BypassInactiveModule
	public Long pesquisarCountConvenioSaudePlanos(final String filtro) {
		return getConvenioSaudeON().pesquisarCountConvenioSaudePlanos(filtro);
	}

	/**
	 * Método que pesquisa os planos de saude com base na descrição, código ou
	 * identificador do convenio. TODO: VERIFICAR OS COMENTÁRIOS DESTA QUERY.
	 * PARECE ESTAR INCOMPLETA.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param filtro
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(
			final String filtro) {
		return getConvenioSaudeON().pesquisarConvenioSaudePlanos(filtro);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarConvenioSaudePlanosCount(final String filtro) {
		return getConvenioSaudeON().pesquisarConvenioSaudePlanosCount(filtro);
	}
	
	@Override
	@BypassInactiveModule
	public List<FatConvenioSaudePlano> pesquisarConvenioPorGrupoConvenio(final String filtro, DominioGrupoConvenio grupoConvenio) {
		return getConvenioSaudeON().pesquisarConvenioPorGrupoConvenio(filtro, grupoConvenio);
	}
	
	@Override
	@BypassInactiveModule
	public FatConvenioSaudePlano obterConvenioPorGrupoConvenio(final Byte seqConvenioSaudePlano, final Short codConvenioSaude, DominioGrupoConvenio grupoConvenioSUS) {
		return getConvenioSaudeON().obterConvenioPorGrupoConvenio(seqConvenioSaudePlano, codConvenioSaude, grupoConvenioSUS);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarConvenioPorGrupoConvenioCount(final String filtro, DominioGrupoConvenio grupoConvenio) {
		return getConvenioSaudeON().pesquisarConvenioPorGrupoConvenioCount(filtro, grupoConvenio);
	}

	/**
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param filtro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosSolicitacaoInternacao(
			final String filtro) {
		return getConvenioSaudeON()
				.pesquisarConvenioSaudePlanosSolicitacaoInternacao(filtro);
	}

	/**
	 * Método que busca os convênios planos aplicando restrições específicas da
	 * internação
	 * 
	 * @param filtro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosInternacao(
			final String filtro) {
		return getConvenioSaudeON().pesquisarConvenioSaudePlanosInternacao(
				filtro);
	}

	/**
	 * Método que obtem um FatConvenioSaudePlano através de sua chave primária.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param seqConvenioSaudePlano
	 * @param codConvenioSaude
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public FatConvenioSaudePlano obterPlanoPorId(
			final Byte seqConvenioSaudePlano, final Short codConvenioSaude) {
		return getConvenioSaudeON().obterPlanoPorId(seqConvenioSaudePlano,
				codConvenioSaude);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public FatConvenioSaudePlano obterPlanoPorIdConvenioInternacao(
			final Byte seqConvenioSaudePlano, final Short codConvenioSaude) {
		return getConvenioSaudeON().obterPlanoPorIdConvenioInternacao(
				seqConvenioSaudePlano, codConvenioSaude);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public FatConvenioSaudePlano obterPlanoPorIdSolicitacaoInternacao(
			final Byte seqConvenioSaudePlano, final Short codConvenioSaude) {
		return getConvenioSaudeON().obterPlanoPorIdSolicitacaoInternacao(
				seqConvenioSaudePlano, codConvenioSaude);
	}

	/**
	 * Retorna FatConveniosSaude com base na chave primária
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public FatConvenioSaude obterConvenioSaude(final Short codigo) {
		return getConvenioSaudeON().obterConvenioSaude(codigo);
	}

	/**
	 * Metódo de consulta de ConveniosSaude por Codigo ou Descrição
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	@BypassInactiveModule
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricao(
			final String codDescConvSaude) {
		return getConvenioSaudeON()
				.pesquisarConveniosSaudePorCodigoOuDescricao(codDescConvSaude);
	}

	/**
	 * Método responsável pela persistência.
	 * 
	 * @dbtables FatConvenioSaude insert,update
	 * 
	 * @param objeto
	 * @throws ApplicationBusinessException
	 */
	@Override
	
	@Secure("#{s:hasPermission('convenioSaude','alterar')}")
	public void persistir(final FatConvenioSaude fatConveniosSaude)
			throws ApplicationBusinessException {
		getConvenioSaudeON().persistir(fatConveniosSaude);
	}

	/**
	 * Remove entidade.
	 * 
	 * @dbtables FatConvenioSaude delete
	 * 
	 * @param tipo
	 *            de característica de leito. Tipo de característica de leito a
	 *            ser removida.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','excluir')}")
	public void remover(final FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException {
		getConvenioSaudeON().remover(fatConvenioSaude);
	}

	/**
	 * Método usado pelo mec:serverDataTable.
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param grupoConvenio
	 * @param csAtivo
	 * @return
	 */
	@Override
	public Long pesquisaCount(final Short codigo, final String descricao,
			final DominioSituacao situacao) {
		return getConvenioSaudeON().pesquisaCount(codigo, descricao, situacao);
	}

	/**
	 * Método principal de pesquisa.
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param grupoConvenio
	 * @param csAtivo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public List<FatConvenioSaude> pesquisar(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final Short codigo, final String descricao,
			final DominioSituacao situacao) {
		return getConvenioSaudeON().pesquisar(firstResult, maxResults,
				orderProperty, asc, codigo, descricao, situacao);
	}

	/**
	 * Valida periodos de Emissão.
	 * 
	 * @param lista
	 *            de periodos
	 * @param dominio
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','validar')}")
	public void validaPeriodos(final List<FatPeriodosEmissao> periodos,
			final DominioPeriodicidade dominio) throws ApplicationBusinessException {
		getConvenioSaudeON().validaPeriodos(periodos, dominio);
	}

	/**
	 * Valida periodos, tradução da PROGRAM UNIT VALIDA_PERIODOS em
	 * FATF_ATU_CONVENIOS.fmb.
	 * 
	 * @param diaInicio
	 * @param diaFim
	 * @param diaSemana
	 * @param dominio
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','validar')}")
	public void validaPeriodo(final Integer diaInicio, final Integer diaFim,
			final Integer diaSemana, final DominioPeriodicidade dominio)
			throws ApplicationBusinessException {
		getConvenioSaudeON().validaPeriodo(diaInicio, diaFim, diaSemana,
				dominio);
	}

	/**
	 * Método usado para persistir as associações entre ConvênioPlano.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * @dbtables FatPeriodosEmissao select
	 * 
	 * @param planosPaciente
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','alterar')}")
	public FatConvenioSaudePlano persistirConvenioPlano(
			final FatConvenioSaude convenioSaude,
			final FatConvenioSaudePlano convenioSaudePlano,
			final List<FatPeriodosEmissao> convPeriodos,
			final List<FatConvTipoDocumentos> convTipoDocumentos,
			final List<FatConvPlanoAcomodacoes> convPlanoAcomodacoes)
			throws ApplicationBusinessException {
		return getConvenioSaudeON().persistirConvenioPlano(convenioSaude,
				convenioSaudePlano, convPeriodos, convTipoDocumentos,
				convPlanoAcomodacoes);
	}

	/**
	 * Método para buscar FatConvenioSaudePlano que tenha um FatConvenioSaude
	 * com permissao de internação.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param cnvCodigo
	 * @param seq
	 * @param cspCnvCodigo
	 * @return
	 */
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public FatConvenioSaudePlano obterConvenioSaudePlanoParaInternacao(
			final Short cnvCodigo, final Byte seq) {
		return getConvenioSaudeON().obterConvenioSaudePlanoParaInternacao(
				cnvCodigo, seq);
	}

	/**
	 * Método para buscar FatConvenioSaudePlano pelo ID.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param cnvCodigo
	 * @param seq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public FatConvenioSaudePlano obterConvenioSaudePlano(final Short cnvCodigo,
			final Byte seq) {
		return getConvenioSaudeON().obterConvenioSaudePlano(cnvCodigo, seq);
	}

	/**
	 * Definir Grupo Convênio a partir do Pagador. Issue #3704.
	 * 
	 * @param aacPagador
	 * @return DominioGrupoConvenio
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public DominioGrupoConvenio obterGrupoConvenio(final AacPagador aacPagador) {
		return getConvenioSaudeON().obterGrupoConvenio(aacPagador);
	}

	@Override
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(
			final String codDescConvSaude) {
		return getConvenioSaudeON()
				.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(
						codDescConvSaude);
	}
	
	@Override
	public Long pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(
			String codDescConvSaude) {
		return getConvenioSaudeON()
				.pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(
						codDescConvSaude);
	}
	
	@Override
	public FatTipoTratamentos obterFatTipoTratamentosPorChavePrimaria(Integer seq) {
		return this.fatTipoTratamentosDAO.obterPorChavePrimaria(seq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * gravarFatCompetenciaProd(br.gov.mec.aghu.model.FatCompetenciaProd)
	 */
	@Override
	public void gravarFatCompetenciaProd(
			FatCompetenciaProd competenciaProducao,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		this.getFatCompetenciaProdON().gravarFatCompetenciaProd(
				competenciaProducao, dataFimVinculoServidor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#gravarProducao()
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	//@TransactionTimeout(TRANSACTION_TIMEOUT_20_MINUTOS)	
	public void gravarProducao(final Date dataFimVinculoServidor) throws BaseException {
		setTimeout(TRANSACTION_TIMEOUT_20_MINUTOS);
		this.getFatCompetenciaProdON().gravarProducao(dataFimVinculoServidor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#iniciarNovaCompetencia
	 * ()
	 */

	@Override
	public void iniciarNovaCompetencia(final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.getFatCompetenciaProdON().iniciarNovaCompetencia(dataFimVinculoServidor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#
	 * atualizarCompetenciaProducao()
	 */
	@Override
	public void atualizarCompetenciaProducao(final Date dataFimVinculoServidor) {
		this.getFatCompetenciaProdON().atualizarCompetenciaProducao(dataFimVinculoServidor);

	}

	protected FatCompetenciaProdON getFatCompetenciaProdON() {
		return fatCompetenciaProdON;
	}

	protected ConvenioSaudeON getConvenioSaudeON() {
		return convenioSaudeON;
	}

	protected AtualizarMetaON getAtualizarMetaON() {
		return atualizarMetaON;
	}

	protected FatMetaDAO getFatMetaDAO() {
		return fatMetaDAO;
	}

	protected FatGrupoDAO getFatGrupoDAO() {
		return fatGrupoDAO;
	}

	protected FatSubGrupoDAO getFatSubGrupoDAO() {
		return fatSubGrupoDAO;
	}

	protected FatFormaOrganizacaoDAO getFatFormaOrganizacaoDAO() {
		return fatFormaOrganizacaoDAO;
	}

	protected FatCaractFinanciamentoDAO getFatCaractFinanciamentoDAO() {
		return fatCaractFinanciamentoDAO;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}
	
	protected FatCboDAO getFatCboDAO(){
		return fatCboDAO;
	}

	protected FatExclusaoCriticaRN getFatExclusaoCriticaRN() {
		return fatExclusaoCriticaRN;
	}

	protected FatExclusaoCriticaDAO getFatExclusaoCriticaDAO() {
		return fatExclusaoCriticaDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # pesquisarFatMetaCount
	 */
	@Override
	public Long pesquisarFatMetaCount(final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean indInternacao,
			final Boolean indAmbulatorio) {
		return getFatMetaDAO().pesquisarFatMetaCount(grupo, subGrupo,
				formaOrganizacao, financiamento, procedimento, indInternacao, indAmbulatorio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # pesquisarFatMetaComDthrFimVig
	 */
	@Override
	public boolean pesquisarFatMetaComDthrFimVig(final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean ambulatorio,
			final Boolean internacao) {
		return getFatMetaDAO().pesquisarFatMetaComDthrFimVig(grupo, subGrupo,
				formaOrganizacao, financiamento, procedimento, ambulatorio, internacao);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # pesquisarFatMeta
	 */
	@Override
	public List<FatMeta> pesquisarFatMeta(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean indInternacao,
			final Boolean indAmbulatorio) {
		return getFatMetaDAO().pesquisarFatMeta(firstResult, maxResult,
				orderProperty, asc, grupo, subGrupo, formaOrganizacao,
				financiamento, procedimento, indInternacao, indAmbulatorio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # listarGruposPorCodigoOuDescricaoAtivos(java.lang.Object)
	 */
	@Override
	public List<FatGrupo> listarGruposAtivosPorCodigoOuDescricao(
			final Object objPesquisa) {
		return getFatGrupoDAO().listarGruposAtivosPorCodigoOuDescricao(objPesquisa);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # listarSubGruposAtivosPorCodigoOuDescricao(java.lang.Object, java.lang.Short)
	 */
	@Override
	public List<FatSubGrupo> listarSubGruposAtivosPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq) {
		return this.getFatSubGrupoDAO().listarSubGruposAtivosPorCodigoOuDescricao(
				objPesquisa, grpSeq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # listarFormasOrganizacaoAtivosPorCodigoOuDescricao(java.lang.Object,
	 * java.lang.Short, java.lang.Byte)
	 */
	@Override
	public List<FatFormaOrganizacao> listarFormasOrganizacaoAtivosPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq, final Byte subGrupo) {
		return this.getFatFormaOrganizacaoDAO()
				.listarFormasOrganizacaoAtivosPorCodigoOuDescricao(objPesquisa,
						grpSeq, subGrupo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # listarFinanciamentosAtivos(java.lang.Object, java.lang.Short)
	 */
	@Override
	public List<FatCaractFinanciamento> listarFinanciamentosAtivosPorCodigoOuDescricao(
			final Object objPesquisa) {

		return this.getFatCaractFinanciamentoDAO()
				.listarFinanciamentosAtivosPorCodigoOuDescricao(objPesquisa);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # listarFatItensProcedHospitalarAtivos(java.lang.Object)
	 */
	@Override
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarAtivosPorCodigoOuDescricao(
			final Object objPesquisa, final FatFormaOrganizacao formaOrganizacao,
			final FatGrupo grupo, final FatSubGrupo subGrupo) {
		
		return getFatItensProcedHospitalarDAO().listarFatItensProcedHospitalarAtivosPorCodigoOuDescricao(
				objPesquisa, formaOrganizacao, grupo, subGrupo);
	}
	
	@Override
	public FatMeta obterMeta(Integer seq) {
		return getFatMetaDAO().obterPorChavePrimaria(seq);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.cadastroapoio.business.IFaturamentoApoioFacade
	 * # atualizarMeta (final FatMeta meta)
	 */
	@Override
	public void atualizarMeta(final FatMeta meta, final Date dataFimVinculoServidor) throws BaseException   {
		this.getAtualizarMetaON().atualizarMeta(meta, dataFimVinculoServidor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#inserirMeta()
	 */
	@Override
	public void inserirMeta(final FatMeta meta, final Date dataFimVinculoServidor) throws BaseException  {
		this.getAtualizarMetaON().inserirMeta(meta, dataFimVinculoServidor);
	}
	
	
	
	public void setTimeout(Integer timeout) throws ApplicationBusinessException {
//		UserTransaction userTx = this.getUserTransaction();
//		try {
//			EntityManager em = this.getEntityManager();
//			if (userTx.isNoTransaction() || !userTx.isActive()) {
//				userTx.begin();
//			}
//			if(timeout != null){
//				userTx.setTransactionTimeout(timeout);
//			}
//			em.joinTransaction();
//		} catch (Exception e) {
//			logError(e.getMessage(), e);
//		}
	}

	@Override
	public List<FatCadCidNascimento> pesquisarFatCadCidNascimento(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final DominioVivoMorto vivo,
			final DominioVivoMorto morto, final String cid) {
		return this.fatCadCidNascimentoDAO.pesquisar(firstResult, maxResult, orderProperty, asc, vivo, morto, cid);
	}

	@Override
	public Long pesquisaCountFatCadCidNascimento(final DominioVivoMorto vivo,
			final DominioVivoMorto morto, final String cid) {
		return this.fatCadCidNascimentoDAO.pesquisarCount(vivo, morto, cid);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public FatCadCidNascimento gravarCidPorNascimento(final FatCadCidNascimento fatCadCidNascimento)
			throws ApplicationBusinessException {
		return this.fatCadCidNascimentoRN.gravarCidPorNascimento(fatCadCidNascimento);
	}
	
	@Override
	public List<FatMotivoRejeicaoConta> recuperarListaPaginadaMotivosRejeicaoConta(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoRejeicaoConta fatMotivoRejeicaoConta) {
		return this.fatMotivoRejeicaoContaDAO.pesquisar(firstResult, maxResult, orderProperty, asc, fatMotivoRejeicaoConta);
	}

	@Override
	public Long recuperarCountMotivosRejeicaoConta(final FatMotivoRejeicaoConta fatMotivoRejeicaoConta) {
		return this.fatMotivoRejeicaoContaDAO.pesquisarCount(fatMotivoRejeicaoConta);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void gravarMotivoRejeicaoConta(final FatMotivoRejeicaoConta fatMotivoRejeicaoConta) throws ApplicationBusinessException {
		this.fatMotivoRejeicaoContaON.gravarMotivoRejeicaoConta(fatMotivoRejeicaoConta);
	}
	
	@Override
	public Long pesquisarCadastroBrasileiroOcupacoesCount(FatCbos filtro) {
		return fatCboDAO.pesquisarCadastroBrasileiroOcupacoesCount(filtro);
	}

	@Override
	public List<FatCbos> pesquisarCadastroBrasileiroOcupacoes(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatCbos filtroFatCbo) {

		return fatCboDAO.pesquisarCadastroBrasileiroOcupacoes(firstResult,
				maxResult, orderProperty, asc, filtroFatCbo);
	}
	
	@Override
	public List<FatExclusaoCritica> listarExclusaoCritica(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FatExclusaoCritica fatExclusaoCritica) {
		return getFatExclusaoCriticaDAO().listarExclusoesCriticas(firstResult, maxResult, orderProperty, asc, fatExclusaoCritica);
	}
	
	@Override
	public Long pesquisarExclusaoCriticaCount(FatExclusaoCritica fatExclusaoCritica) {
		return  getFatExclusaoCriticaDAO().pesquisarExclusaoCriticaCount(fatExclusaoCritica);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void alterarSituacaoExclusaoCritica(final FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException{
		getFatExclusaoCriticaRN().ativarDesativarSituacao(fatExclusaoCritica);		
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void removerExclusaoCritica(final Short seq) throws BaseException {
		getFatExclusaoCriticaRN().removerExclusaoCritica(seq);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void persistirExclusaoCritica(final FatExclusaoCritica fatExclusaoCritica)	throws ApplicationBusinessException, BaseException{
		getFatExclusaoCriticaRN().persistir(fatExclusaoCritica);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void alterarExclusaoCritica(final FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException, BaseException{
		getFatExclusaoCriticaRN().alterar(fatExclusaoCritica);
	}
	
	@Override
	public List<FatMotivoSaidaPaciente> recuperarListaPaginadaMotivoSaidaPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoSaidaPaciente fatMotivoSaidaPaciente) {
		return this.fatMotivoSaidaPacienteDAO.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, fatMotivoSaidaPaciente);
	}

	@Override
	public Long recuperarCountMotivoSaidaPaciente(final FatMotivoSaidaPaciente fatMotivoSaidaPaciente) {
		return this.fatMotivoSaidaPacienteDAO.recuperarCount(fatMotivoSaidaPaciente);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void gravarMotivoSaidaPaciente(final FatMotivoSaidaPaciente fatMotivoSaidaPaciente) throws ApplicationBusinessException {
		this.fatMotivoSaidaPacienteON.gravarMotivoSaidaPaciente(fatMotivoSaidaPaciente);
	}
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void removerMotivoSaidaPaciente(final Short id) throws ApplicationBusinessException {
		this.fatMotivoSaidaPacienteON.removerMotivoSaidaPaciente(id);
	}

	@Override
	public List<FatSituacaoSaidaPaciente> recuperarListaPaginadaSituacaoSaidaPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoSaidaPaciente fatMotivoSaidaPaciente) {
		return this.fatSituacaoSaidaPacienteDAO.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, fatMotivoSaidaPaciente);
	}

	@Override
	public Long recuperarCountSituacaoSaidaPaciente(final FatMotivoSaidaPaciente fatMotivoSaidaPaciente) {
		return this.fatSituacaoSaidaPacienteDAO.recuperarCount(fatMotivoSaidaPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void gravarSituacaoSaidaPaciente(final FatSituacaoSaidaPaciente fatSituacaoSaidaPaciente) throws ApplicationBusinessException {
		this.fatSituacaoSaidaPacienteON.gravarSituacaoSaidaPaciente(fatSituacaoSaidaPaciente);
	}

    @Override
    public FatConvenioSaude obterConvenioSaudeComPagadorEUF(Short codigoConvenio){
        return this.getConvenioSaudeON().obterConvenioSaudeComPagadorEUF(codigoConvenio);
    }
	@Override
	@Secure("#{s:hasPermission('manterCadastrosBasicosFaturamento','executar')}")
	public void removerSituacaoSaidaPaciente(final FatSituacaoSaidaPacienteId fatSituacaoSaidaPacienteId) throws ApplicationBusinessException {
		this.fatSituacaoSaidaPacienteON.removerSituacaoSaidaPaciente(fatSituacaoSaidaPacienteId);
	}
}
