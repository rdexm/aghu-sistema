package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioVivoMorto;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IFaturamentoApoioFacade extends Serializable {

	
	public Long pesquisarCountConvenioSaudePlanos(final String filtro);

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
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(final String filtro);

	
	public Long pesquisarConvenioSaudePlanosCount(final String filtro);
	
	
	public List<FatConvenioSaudePlano> pesquisarConvenioPorGrupoConvenio(final String filtro, DominioGrupoConvenio grupoConvenio);
	
	public FatConvenioSaudePlano obterConvenioPorGrupoConvenio(final Byte seqConvenioSaudePlano, final Short codConvenioSaude, DominioGrupoConvenio grupoConvenioSUS);

	
	public Long pesquisarConvenioPorGrupoConvenioCount(final String filtro, DominioGrupoConvenio grupoConvenio);

	/**
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param filtro
	 * @return
	 */
	
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosSolicitacaoInternacao(
			final String filtro);

	/**
	 * Método que busca os convênios planos aplicando restrições específicas da
	 * internação
	 * 
	 * @param filtro
	 * @return
	 */
	
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosInternacao(
			final String filtro);

	/**
	 * Método que obtem um FatConvenioSaudePlano através de sua chave primária.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param seqConvenioSaudePlano
	 * @param codConvenioSaude
	 * @return
	 */
	
	
	public FatConvenioSaudePlano obterPlanoPorId(
			final Byte seqConvenioSaudePlano, final Short codConvenioSaude);

	
	
	public FatConvenioSaudePlano obterPlanoPorIdConvenioInternacao(
			final Byte seqConvenioSaudePlano, final Short codConvenioSaude);

	
	public FatConvenioSaudePlano obterPlanoPorIdSolicitacaoInternacao(
			final Byte seqConvenioSaudePlano, final Short codConvenioSaude);

	/**
	 * Retorna FatConveniosSaude com base na chave primária
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param codigo
	 * @return
	 */
	
	
	public FatConvenioSaude obterConvenioSaude(final Short codigo);

	/**
	 * Metódo de consulta de ConveniosSaude por Codigo ou Descrição
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param parametro
	 * @return
	 */
	
	
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricao(
			final String codDescConvSaude);

	/**
	 * Método responsável pela persistência.
	 * 
	 * @dbtables FatConvenioSaude insert,update
	 * 
	 * @param objeto
	 * @throws ApplicationBusinessException
	 */
	
	
	public void persistir(final FatConvenioSaude fatConveniosSaude)
			throws ApplicationBusinessException;

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
	
	public void remover(final FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException;

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
	public Long pesquisaCount(final Short codigo, final String descricao,
			final DominioSituacao situacao);

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
	
	public List<FatConvenioSaude> pesquisar(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final Short codigo, final String descricao,
			final DominioSituacao situacao);

	/**
	 * Valida periodos de Emissão.
	 * 
	 * @param lista
	 *            de periodos
	 * @param dominio
	 */
	
	public void validaPeriodos(final List<FatPeriodosEmissao> periodos,
			final DominioPeriodicidade dominio) throws ApplicationBusinessException;

	/**
	 * Valida periodos, tradução da PROGRAM UNIT VALIDA_PERIODOS em
	 * FATF_ATU_CONVENIOS.fmb.
	 * 
	 * @param diaInicio
	 * @param diaFim
	 * @param diaSemana
	 * @param dominio
	 */
	
	public void validaPeriodo(final Integer diaInicio, final Integer diaFim,
			final Integer diaSemana, final DominioPeriodicidade dominio)
			throws ApplicationBusinessException;

	/**
	 * Método usado para persistir as associações entre ConvênioPlano.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * @dbtables FatPeriodosEmissao select
	 * 
	 * @param planosPaciente
	 * @throws ApplicationBusinessException
	 */
	
	public FatConvenioSaudePlano persistirConvenioPlano(
			final FatConvenioSaude convenioSaude,
			final FatConvenioSaudePlano convenioSaudePlano,
			final List<FatPeriodosEmissao> convPeriodos,
			final List<FatConvTipoDocumentos> convTipoDocumentos,
			final List<FatConvPlanoAcomodacoes> convPlanoAcomodacoes)
			throws ApplicationBusinessException;

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
	
	
	public FatConvenioSaudePlano obterConvenioSaudePlanoParaInternacao(
			final Short cnvCodigo, final Byte seq);

	/**
	 * Método para buscar FatConvenioSaudePlano pelo ID.
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param cnvCodigo
	 * @param seq
	 * @return
	 */
	
	
	public FatConvenioSaudePlano obterConvenioSaudePlano(final Short cnvCodigo,
			final Byte seq);

	/**
	 * Definir Grupo Convênio a partir do Pagador. Issue #3704.
	 * 
	 * @param aacPagador
	 * @return DominioGrupoConvenio
	 */
	
	public DominioGrupoConvenio obterGrupoConvenio(final AacPagador aacPagador);

	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(
			final String codDescConvSaude);
	
	public Long pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(
			String objPesquisa);
	
	FatTipoTratamentos obterFatTipoTratamentosPorChavePrimaria(Integer seq);

	void atualizarCompetenciaProducao(final Date dataFimVinculoServidor);

	void iniciarNovaCompetencia(final Date dataFimVinculoServidor) throws ApplicationBusinessException;

	void gravarProducao(final Date dataFimVinculoServidor) throws BaseException;

	void gravarFatCompetenciaProd(FatCompetenciaProd competenciaProducao, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

	public Long pesquisarFatMetaCount(final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean indInternacao,
			final Boolean indAmbulatorio);

	public List<FatMeta> pesquisarFatMeta(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean indInternacao,
			final Boolean indAmbulatorio);

	public boolean pesquisarFatMetaComDthrFimVig(final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean ambulatorio,
			final Boolean internacao);

	public List<FatGrupo> listarGruposAtivosPorCodigoOuDescricao(
			final Object objPesquisa);

	public List<FatSubGrupo> listarSubGruposAtivosPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq);

	public List<FatFormaOrganizacao> listarFormasOrganizacaoAtivosPorCodigoOuDescricao(
			final Object objPesquisa, final Short grpSeq, final Byte subGrupo);

	public List<FatCaractFinanciamento> listarFinanciamentosAtivosPorCodigoOuDescricao(
			final Object objPesquisa);

	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalarAtivosPorCodigoOuDescricao(
			final Object objPesquisa, final FatFormaOrganizacao formaOrganizacao,
			final FatGrupo grupo, final FatSubGrupo subGrupo);

	public abstract FatMeta obterMeta(final Integer seq);
	
	public void atualizarMeta(final FatMeta meta, final Date dataFimVinculoServidor) throws BaseException;

	public void inserirMeta(final FatMeta meta, final Date dataFimVinculoServidor) throws BaseException;
	
	public Long pesquisarCadastroBrasileiroOcupacoesCount(FatCbos filtro);
	
	public List<FatCbos> pesquisarCadastroBrasileiroOcupacoes(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FatCbos filtroFatCbo);
	
	/**
	 * Realiza a consulta principal para popular a grid da entidade {@link FatCadCidNascimento}
	 * 
	 * @param firstResult {@link Integer}
	 * @param maxResult {@link Integer}
	 * @param orderProperty {@link String}
	 * @param asc {@link Boolean}
	 * @param vivo {@link DominioVivoMorto}
	 * @param morto {@link DominioVivoMorto}
	 * @param cid {@link String}
	 * @return {@link List} de {@link FatCadCidNascimento}
	 */
	public List<FatCadCidNascimento> pesquisarFatCadCidNascimento(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final DominioVivoMorto vivo,
			final DominioVivoMorto morto, final String cid);

	/**
	 * Recupera contador de registros da entidade {@link FatCadCidNascimento}
	 * 
	 * @param vivo {@link DominioVivoMorto}
	 * @param morto {@link DominioVivoMorto}
	 * @param cid {@link String}
	 * @return {@link Long}
	 */
	public Long pesquisaCountFatCadCidNascimento(final DominioVivoMorto vivo,
			final DominioVivoMorto morto, final String cid);

	/**
	 * Método usado para a persistencia/alteração da entidade {@link FatCadCidNascimento}
	 * 
	 * @param entity {@link FatCadCidNascimento}
	 * @throws ApplicationBusinessException
	 */
	public FatCadCidNascimento gravarCidPorNascimento(final FatCadCidNascimento fatCadCidNascimento)
			throws ApplicationBusinessException;
	
	/**
	 * Realiza a consulta principal para popular a grid da entidade {@link FatMotivoRejeicaoConta}
	 * 
	 * @param firstResult {@link Integer}
	 * @param maxResult {@link Integer}
	 * @param orderProperty {@link String}
	 * @param asc {@link Boolean}
	 * @param fatMotivoRejeicaoConta {@link FatMotivoRejeicaoConta}
	 * @return {@link List} de {@link FatMotivoRejeicaoConta}
	 */
	public List<FatMotivoRejeicaoConta> recuperarListaPaginadaMotivosRejeicaoConta(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoRejeicaoConta fatMotivoRejeicaoConta);

	/**
	 * Recupera contador de registros da entidade {@link FatMotivoRejeicaoConta}
	 * 
	 * @param fatMotivoRejeicaoConta {@link FatMotivoRejeicaoConta}
	 * @return {@link Long}
	 */
	public Long recuperarCountMotivosRejeicaoConta(final FatMotivoRejeicaoConta fatMotivoRejeicaoConta);

	/**
	 * Método usado para a persistencia/alteração da entidade {@link FatMotivoRejeicaoConta}
	 * 
	 * @param entity {@link FatMotivoRejeicaoConta}
	 * @throws ApplicationBusinessException
	 */
	public void gravarMotivoRejeicaoConta(final FatMotivoRejeicaoConta fatMotivoRejeicaoConta) throws ApplicationBusinessException;

	/**
	 * Realiza a consulta principal para popular a grid da entidade {@link FatMotivoSaidaPaciente}
	 * 
	 * @param firstResult {@link Integer}
	 * @param maxResult {@link Integer}
	 * @param orderProperty {@link String}
	 * @param asc {@link Boolean}
	 * @param fatMotivoSaidaPaciente {@link FatMotivoSaidaPaciente}
	 * @return {@link List} de {@link FatMotivoSaidaPaciente}
	 */
	public List<FatMotivoSaidaPaciente> recuperarListaPaginadaMotivoSaidaPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoSaidaPaciente fatMotivoSaidaPaciente);

	/**
	 * Recupera contador de registros da entidade {@link FatMotivoSaidaPaciente}
	 * 
	 * @param fatMotivoSaidaPaciente {@link FatMotivoSaidaPaciente}
	 * @return {@link Long}
	 */
	public Long recuperarCountMotivoSaidaPaciente(final FatMotivoSaidaPaciente fatMotivoSaidaPaciente);

	/**
	 * Método usado para a persistencia/alteração da entidade {@link FatMotivoSaidaPaciente}
	 * 
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void gravarMotivoSaidaPaciente(final FatMotivoSaidaPaciente entity) throws ApplicationBusinessException;
	
	/**
	 * Método usado para a exclusão da entidade {@link FatMotivoSaidaPaciente}
	 * 
	 * @param id {@link Short}
	 * @throws ApplicationBusinessException
	 */
	public void removerMotivoSaidaPaciente(final Short id) throws ApplicationBusinessException;

	/**
	 * Realiza a consulta principal para popular a grid da entidade {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param firstResult {@link Integer}
	 * @param maxResult {@link Integer}
	 * @param orderProperty {@link String}
	 * @param asc {@link Boolean}
	 * @param fatMotivoSaidaPaciente {@link FatMotivoSaidaPaciente}
	 * @return {@link List} de {@link FatSituacaoSaidaPaciente}
	 */
	public List<FatSituacaoSaidaPaciente> recuperarListaPaginadaSituacaoSaidaPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoSaidaPaciente fatMotivoSaidaPaciente);

	/**
	 * Recupera contador de registros da entidade {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param fatMotivoSaidaPaciente {@link FatMotivoSaidaPaciente}
	 * @return {@link Long}
	 */
	public Long recuperarCountSituacaoSaidaPaciente(final FatMotivoSaidaPaciente fatMotivoSaidaPaciente);

	/**
	 * Método usado para a persistencia/alteração da entidade {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param fatSituacaoSaidaPaciente {@link FatSituacaoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void gravarSituacaoSaidaPaciente(final FatSituacaoSaidaPaciente fatSituacaoSaidaPaciente) throws ApplicationBusinessException;
	
	/**
	 * Método usado para a exclusão da entidade {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param fatSituacaoSaidaPacienteId {@link FatSituacaoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void removerSituacaoSaidaPaciente(final FatSituacaoSaidaPacienteId fatSituacaoSaidaPacienteId) throws ApplicationBusinessException;
	
	public List<FatExclusaoCritica> listarExclusaoCritica(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final FatExclusaoCritica fatExclusaoCritica);
	
	public Long pesquisarExclusaoCriticaCount(final FatExclusaoCritica fatExclusaoCritica);
	
	void alterarSituacaoExclusaoCritica(final FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException;
	
	public void removerExclusaoCritica(final Short seq) throws BaseException;
	
	public void persistirExclusaoCritica(final FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException, BaseException;
	
	public void alterarExclusaoCritica(final FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException, BaseException;

    public FatConvenioSaude obterConvenioSaudeComPagadorEUF(Short codigoConvenio);

}
