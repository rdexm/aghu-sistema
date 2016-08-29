package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.dao.FatConvPlanoAcomodacoesDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvTipoDocumentosDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatPeriodosEmissaoDAO;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoes;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoesId;
import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvTipoDocumentosId;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatPeriodosEmissao;
import br.gov.mec.aghu.model.FatPeriodosEmissaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ConvenioSaudeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConvenioSaudeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	@Inject
	private FatConvPlanoAcomodacoesDAO fatConvPlanoAcomodacoesDAO;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private FatPeriodosEmissaoDAO fatPeriodosEmissaoDAO;

	@Inject
	private FatConvTipoDocumentosDAO fatConvTipoDocumentosDAO;

	@Inject
	private FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO;

	@Inject
	private FatConvenioSaudeDAO fatConvenioSaudeDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1217297526100414024L;
	
	private enum ConvenioSaudeONExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_CONVENIO_SAUDE, ERRO_REMOVER_CONVENIO_SAUDE_COM_IND_HOSP_TEMP, ERRO_REMOVER_CONVENIO_SAUDE_COM_PLANOS, CODIGO_PAGADOR_NULO, ERRO_PERSISTIR_CONVENIO_SAUDE, ERRO_ATUALIZAR_CONVENIO_SAUDE, ERRO_REMOVER_CONVENIO_SAUDE_COM_DEPENDENCIA, FAT_00794,

		INFORME_PERIODIDADE_EMISSAO, INFORME_SOMENTE_DIA_SEMANA, DIAS_DA_SEMANA_VALIDOS, INFORME_SOMENTE_DIAS, DIA_INICIO_VAZIO, DIA_DO_MES_VALIDOS, PERIODO_EMISSAO_INVALIDO,

		DESCRICAO_CONVENIO_SAUDE_JA_EXISTENTE;

	}

	public Long pesquisarCountConvenioSaudePlanos(String filtro) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioSaudePlanosCount(filtro);
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
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioSaudePlanos(filtro);
	}

	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioSaudePlanosCount(filtro);
	}
	
	public FatConvenioSaudePlano obterConvenioPorGrupoConvenio(final Byte seqConvenioSaudePlano, final Short codConvenioSaude, DominioGrupoConvenio grupoConvenioSUS) {
		return getFatConvenioSaudePlanoDAO().obterConvenioPorGrupoConvenio(seqConvenioSaudePlano, codConvenioSaude, grupoConvenioSUS);
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioPorGrupoConvenio(String filtro, DominioGrupoConvenio grupoConvenio) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioPorGrupoConvenio(filtro, grupoConvenio);
	}

	public Long pesquisarConvenioPorGrupoConvenioCount(String filtro, DominioGrupoConvenio grupoConvenio) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioPorGrupoConvenioCount(filtro, grupoConvenio);
	}
	
	/**
	 * 
	 * @dbtables FatConvenioSaudePlano select
	 * 
	 * @param filtro
	 * @return
	 */
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosSolicitacaoInternacao(String filtro) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioSaudePlanosSolicitacaoInternacao(filtro);		
	}

	/**
	 * Método que busca os convênios planos aplicando restrições específicas da
	 * internação
	 * 
	 * @param filtro
	 * @return
	 */
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosInternacao(String filtro) {
		return getFatConvenioSaudePlanoDAO().pesquisarConvenioSaudePlanosInternacao(filtro);
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
	public FatConvenioSaudePlano obterPlanoPorId(Byte seqConvenioSaudePlano, Short codConvenioSaude) {
		return getFatConvenioSaudePlanoDAO().obterPorCspCnvCodigoECnvCodigo(codConvenioSaude, seqConvenioSaudePlano);
	}

	public FatConvenioSaudePlano obterPlanoPorIdConvenioInternacao(Byte seqConvenioSaudePlano, Short codConvenioSaude) {
		return getFatConvenioSaudePlanoDAO()
			.obterPlanoPorIdConvenioInternacao(seqConvenioSaudePlano, codConvenioSaude);
	}

	public FatConvenioSaudePlano obterPlanoPorIdSolicitacaoInternacao(Byte seqConvenioSaudePlano, Short codConvenioSaude) {
		return getFatConvenioSaudePlanoDAO().obterPlanoPorIdSolicitacaoInternacao(seqConvenioSaudePlano, codConvenioSaude);
	}

	/**
	 * Retorna FatConveniosSaude com base na chave primária
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param codigo
	 * @return
	 */
	public FatConvenioSaude obterConvenioSaude(Short codigo) {
		return getFatConvenioSaudeDAO().obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Metódo de consulta de ConveniosSaude por Codigo ou Descrição
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(String codDescConvSaude) {
		return getFatConvenioSaudeDAO().pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(codDescConvSaude);
	}
	
	public Long pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(
			String codDescConvSaude) {
		return getFatConvenioSaudeDAO().pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(codDescConvSaude);
	}

	/**
	 * Metódo de consulta de ConveniosSaude por Codigo ou Descrição
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricao(String codDescConvSaude) {
		return getFatConvenioSaudeDAO().pesquisarConveniosSaudePorCodigoOuDescricao(codDescConvSaude, FatConvenioSaude.Fields.DESCRICAO);
	}

	/**
	 * Método responsável pela persistência.
	 * 
	 * @dbtables FatConvenioSaude insert,update
	 * 
	 * @param objeto
	 * @throws ApplicationBusinessException
	 */
	public void persistir(FatConvenioSaude fatConveniosSaude) throws ApplicationBusinessException {
		this.validarDados(fatConveniosSaude);
		
		if (fatConveniosSaude.getCodigo() == null) {
			// inclusao
			this.incluir(fatConveniosSaude);
		} else {
			// edicao
			this.atualizar(fatConveniosSaude);
		}
	}

	/**
	 * Método responsável por incluir um novo tipo de característica de leito.
	 * 
	 * @dbtables AacPagador select
	 * @dbtables FatConvenioSaude insert
	 * 
	 * @param tipo
	 *            de característica de leito
	 * @throws ApplicationBusinessException
	 */
	private void incluir(FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException {
		try {
			getFatConvenioSaudeDAO().persistir(fatConvenioSaude);
			getFatConvenioSaudeDAO().flush();
		} catch (Exception e) {
			logError("Erro ao inserir convenio saude.", e);

			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e
						.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),
						"FAT_CNV_UK1")) {
					throw new ApplicationBusinessException(
							ConvenioSaudeONExceptionCode.DESCRICAO_CONVENIO_SAUDE_JA_EXISTENTE);
				}
			}

			throw new ApplicationBusinessException(
					ConvenioSaudeONExceptionCode.ERRO_PERSISTIR_CONVENIO_SAUDE);
		}
	}

	/**
	 * Método responsável pela atualização de um tipo de característica de
	 * leito.
	 * 
	 * @dbtables AacPagador select
	 * @dbtables FatConvenioSaude update
	 * 
	 * @param
	 * @throws ApplicationBusinessException
	 */
	private void atualizar(FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException {
		try {
			getFatConvenioSaudeDAO().atualizar(fatConvenioSaude);
			getFatConvenioSaudeDAO().flush();
		} catch (Exception e) {
			logError("Erro ao atualizar convenio saude.", e);

			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e
						.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),
						"FAT_CNV_UK1")) {
					throw new ApplicationBusinessException(
							ConvenioSaudeONExceptionCode.DESCRICAO_CONVENIO_SAUDE_JA_EXISTENTE);
				}

				// TODO: Tratar FK que levanta quando dessassocia Plano a
				// Convenio.
			}
			throw new ApplicationBusinessException(
					ConvenioSaudeONExceptionCode.ERRO_ATUALIZAR_CONVENIO_SAUDE);
		}
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
	public void remover(FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException {
		try {
			getFatConvenioSaudeDAO().remover(fatConvenioSaude);
			getFatConvenioSaudeDAO().flush();
		} catch (Exception e) {
			logError("Erro ao remover a cidade.", e);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e
						.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),
						"AIN_INH_CNV_FK1")) {
					throw new ApplicationBusinessException(
							ConvenioSaudeONExceptionCode.ERRO_REMOVER_CONVENIO_SAUDE_COM_IND_HOSP_TEMP);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),
						"FAT_CSP_CNV_FK1")) {
					throw new ApplicationBusinessException(
							ConvenioSaudeONExceptionCode.ERRO_REMOVER_CONVENIO_SAUDE_COM_PLANOS);
				}

				// TODO: As FKs abaixo estão associadas a FAT_CONVENIO_SAUDE:
				// AGH_MMC_CNV_FK1,AIN_INH_CNV_FK1,FAT_COH_CNV_FK1,FAT_CFE_CNV_FK1,
				// FAT_CPG_CNV_FK1,FAT_CAV_CNV_FK1,FAT_CSP_CNV_FK1,
				// FAT_CEA_CNV_FK1,FAT_CTC_CNV_FK1,FAT_FMC_CNV_FK1,FAT_GRA_CNV_FK1,
				// FAT_PPT_CNV_FK1, FAT_PTB_CNV_FK1,FAT_PCR_CNV_FK1,
				// FAT_TAC_CNV_FK1

				throw new ApplicationBusinessException(
						ConvenioSaudeONExceptionCode.ERRO_REMOVER_CONVENIO_SAUDE_COM_DEPENDENCIA);
			}
			throw new ApplicationBusinessException(
					ConvenioSaudeONExceptionCode.ERRO_REMOVER_CONVENIO_SAUDE);
		}
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
	public Long pesquisaCount(Short codigo, String descricao, DominioSituacao situacao) {
		return getFatConvenioSaudeDAO().pesquisaCount(codigo, descricao, situacao);
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
	public List<FatConvenioSaude> pesquisar(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short codigo, String descricao, DominioSituacao situacao) {
		
		return getFatConvenioSaudeDAO().pesquisar(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	/**
	 * Método responsável por validar antes de salvar/editar.
	 * 
	 * @dbtables AacPagador select
	 * 
	 * @param Convenio
	 *            Saúde
	 * @throws ApplicationBusinessException
	 */
	private void validarDados(FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException {
		if (fatConvenioSaude.getPagador() == null
				|| fatConvenioSaude.getPagador().getSeq() == null) {
			throw new ApplicationBusinessException(
					ConvenioSaudeONExceptionCode.CODIGO_PAGADOR_NULO);
		}
	}

	/**
	 * Obtem sequence da tabela
	 * 
	 * @dbtables FatPeriodosEmissao select
	 * 
	 * @param fatConvenioSaude
	 */
	private Short obterValorSequencialPeriodoEmissaoId(FatConvenioSaudePlano fatConvenioSaudePlano) {
		return getFatConvenioSaudePlanoDAO().obterValorSequencialPeriodoEmissaoId(fatConvenioSaudePlano);
	}

	/**
	 * Valida periodos de Emissão.
	 * 
	 * @param lista
	 *            de periodos
	 * @param dominio
	 */
	public void validaPeriodos(List<FatPeriodosEmissao> periodos, DominioPeriodicidade dominio) throws ApplicationBusinessException {
		if (dominio != null) {
			for (FatPeriodosEmissao periodo : periodos) {
				// Mensal
				if (periodo.getDiaSemana() == null) {
					if (!dominio.getDescricao().equalsIgnoreCase("Mensal")) {
						throw new ApplicationBusinessException(
								ConvenioSaudeONExceptionCode.PERIODO_EMISSAO_INVALIDO);
					}
					// Semanal
				} else {
					if (!dominio.getDescricao().equalsIgnoreCase("Semanal")) {
						throw new ApplicationBusinessException(
								ConvenioSaudeONExceptionCode.PERIODO_EMISSAO_INVALIDO);
					}
				}
			}
		}
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
	public void validaPeriodo(Integer diaInicio, Integer diaFim,
			Integer diaSemana, DominioPeriodicidade dominio)
			throws ApplicationBusinessException {

		if (dominio == null) {
			throw new ApplicationBusinessException(
					ConvenioSaudeONExceptionCode.INFORME_PERIODIDADE_EMISSAO);
		}

		if (dominio.equals(DominioPeriodicidade.S)) {
			if (diaInicio != null || diaFim != null) {
				throw new ApplicationBusinessException(
						ConvenioSaudeONExceptionCode.INFORME_SOMENTE_DIA_SEMANA);
			} else {
				if (diaSemana == null || (diaSemana < 2 || diaSemana > 6)) {
					throw new ApplicationBusinessException(
							ConvenioSaudeONExceptionCode.DIAS_DA_SEMANA_VALIDOS);
				}
			}
		}

		if (dominio.equals(DominioPeriodicidade.M)) {
			if (diaSemana != null) {
				throw new ApplicationBusinessException(
						ConvenioSaudeONExceptionCode.INFORME_SOMENTE_DIAS);
			} else {
				if (diaFim != null && diaInicio == null) {
					throw new ApplicationBusinessException(
							ConvenioSaudeONExceptionCode.DIA_INICIO_VAZIO);
				}
			}

			if (diaFim != null && (diaFim < 0 || diaFim > 31)) {
				throw new ApplicationBusinessException(
						ConvenioSaudeONExceptionCode.DIA_DO_MES_VALIDOS);
			}

			if (diaInicio == null || (diaInicio < 0 || diaInicio > 31)) {
				throw new ApplicationBusinessException(
						ConvenioSaudeONExceptionCode.DIA_DO_MES_VALIDOS);
			}
		}

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
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public FatConvenioSaudePlano persistirConvenioPlano(
			FatConvenioSaude convenioSaude,
			FatConvenioSaudePlano convenioSaudePlano,
			List<FatPeriodosEmissao> convPeriodos,
			List<FatConvTipoDocumentos> convTipoDocumentos,
			List<FatConvPlanoAcomodacoes> convPlanoAcomodacoes)
			throws ApplicationBusinessException {

		try {
			if (convenioSaudePlano.getId().getSeq() == null) {
				getFatConvenioSaudePlanoDAO().persistir(convenioSaudePlano);
				getFatConvenioSaudePlanoDAO().flush();
			} else {

				// Valida <code>FatConvenioSaudePlano</code> antes de atualizar.
				validarConvenioSaudePlanoAntesAtualizar(convenioSaudePlano);
				getFatConvenioSaudePlanoDAO().merge(convenioSaudePlano);
			}

			// Commita as alterações.
			// this.entityManager.flush();

			// Salvar os filhos e verificação de triggers.

			// Início da rotina de persist/merge/remove de
			// <code>FatPeriodosEmissao</code>.

			List<FatPeriodosEmissao> periodosEmissaoGravados = getFatPeriodosEmissaoDAO().pesquisarPeriodosEmissaoConvenioSaudePlano(convenioSaudePlano);

			HashMap<FatPeriodosEmissaoId, FatPeriodosEmissao> periodosEmissaoMap = new HashMap<FatPeriodosEmissaoId, FatPeriodosEmissao>(
					periodosEmissaoGravados.size());

			for (FatPeriodosEmissao periodoEmissao : periodosEmissaoGravados) {
				periodosEmissaoMap.put(periodoEmissao.getId(), periodoEmissao);
			}

			Short seq = null;

			for (FatPeriodosEmissao periodoEmissao : convPeriodos) {

				boolean isNew = false;

				if (periodoEmissao.getId().getSeq() == null) {

					if (seq == null) {
						seq = obterValorSequencialPeriodoEmissaoId(convenioSaudePlano);
					} else {
						seq++;
					}

					periodoEmissao.getId().setSeq(seq);

					isNew = true;
				}

				// Atribui parâmetros do pai, caso NULO.
				if (periodoEmissao.getId().getCspCnvCodigo() == null) {
					periodoEmissao.getId().setCspCnvCodigo(
							convenioSaudePlano.getId().getCnvCodigo());
				}

				if (periodoEmissao.getId().getCspSeq() == null) {
					periodoEmissao.getId().setCspSeq(
							convenioSaudePlano.getId().getSeq());
				}

				// Remove do Map, assim quem sobrar no map, são aqueles que
				// foram excluídos.
				periodosEmissaoMap.remove(periodoEmissao.getId());

				if (isNew) {
					getFatPeriodosEmissaoDAO().persistir(periodoEmissao);
					getFatPeriodosEmissaoDAO().flush();
				} else {
					getFatPeriodosEmissaoDAO().merge(periodoEmissao);
				}

			}

			// Remove apenas os <code>FatPeriodosEmissao</code> que não estão
			// mais associados ao pai.
			for (FatPeriodosEmissao periodoEmissao : periodosEmissaoMap.values()) {
				getFatPeriodosEmissaoDAO().refresh(periodoEmissao);
				getFatPeriodosEmissaoDAO().remover(periodoEmissao);
				getFatPeriodosEmissaoDAO().flush();
			}
			// FIM da rotina de persist/merge/remove de
			// <code>FatPeriodosEmissao</code>.

			// Início da rotina de persist/merge/remove de
			// <code>FatConvPlanoAcomodacoes</code>.
			List<FatConvPlanoAcomodacoes> planosAcomodacoesGravados = getFatConvPlanoAcomodacoesDAO().pesquisarConvPlanoAcomodocaoConvenioSaudePlano(convenioSaudePlano); 

			HashMap<FatConvPlanoAcomodacoesId, FatConvPlanoAcomodacoes> planosAcomodacoesMap = new HashMap<FatConvPlanoAcomodacoesId, FatConvPlanoAcomodacoes>(
					planosAcomodacoesGravados.size());

			for (FatConvPlanoAcomodacoes planosAcomodacao : planosAcomodacoesGravados) {
				planosAcomodacoesMap.put(planosAcomodacao.getId(), planosAcomodacao);
			}

			for (FatConvPlanoAcomodacoes convPlanoAcomodacao : convPlanoAcomodacoes) {

				boolean isNew = false;

				// Atribui parâmetros do pai, caso NULO.
				if (convPlanoAcomodacao.getId().getCspSeq() == null) {
					convPlanoAcomodacao.getId().setCspSeq(convenioSaudePlano.getId().getSeq());
					isNew = true;
				}

				if (convPlanoAcomodacao.getId().getCspCnvCodigo() == null) {
					convPlanoAcomodacao.getId().setCspCnvCodigo(convenioSaudePlano.getId().getCnvCodigo());
				}

				// Remove do Map, assim quem sobrar no map, são aqueles que
				// foram excluídos.
				planosAcomodacoesMap.remove(convPlanoAcomodacao.getId());

				// Metodo alterado, para atender condição abaixo, 
				// necessário exclusão do BD e depois adição.
				// "Essa verificação existe para o caso de exclusão e logo
				// em seguida inclusão do mesmo registro."
				if (isNew){
					
					FatConvPlanoAcomodacoes acomodacoes = getFatConvPlanoAcomodacoesDAO().obterPorChavePrimaria(convPlanoAcomodacao.getId());
					if(acomodacoes != null){
						getFatConvPlanoAcomodacoesDAO().remover(acomodacoes);
					}
					
					// Trigger BRI.
					getFaturamentoFacade().validarConvenioPlanoAcomodacaoAntesInserir(convPlanoAcomodacao);
					getFatConvPlanoAcomodacoesDAO().persistir(convPlanoAcomodacao);
					getFatConvPlanoAcomodacoesDAO().flush();
				} else {
					getFatConvPlanoAcomodacoesDAO().merge(convPlanoAcomodacao);
				}
			}

			// Remove apenas os <code>FatConvPlanoAcomodacoes</code> que não
			// estão
			// mais associados ao pai.
			for (FatConvPlanoAcomodacoes convPlanoAcomodacao : planosAcomodacoesMap.values()) {
				getFatConvPlanoAcomodacoesDAO().refresh(convPlanoAcomodacao);
				getFatConvPlanoAcomodacoesDAO().remover(convPlanoAcomodacao);
			}
			// FIM da rotina de persist/merge/remove de
			// <code>FatConvPlanoAcomodacoes</code>.

			// Início da rotina de persist/merge/remove de
			// <code>FatConvTipoDocumentos</code>.
			List<FatConvTipoDocumentos> tiposDocsGravados = getFatConvTipoDocumentosDAO().pesquisarConvTipoDocumentoConvenioSaudePlano(convenioSaudePlano);

			HashMap<FatConvTipoDocumentosId, FatConvTipoDocumentos> tiposDocsMap = new HashMap<FatConvTipoDocumentosId, FatConvTipoDocumentos>(
					tiposDocsGravados.size());

			for (FatConvTipoDocumentos tipoDoc : tiposDocsGravados) {
				tiposDocsMap.put(tipoDoc.getId(), tipoDoc);
			}

			for (FatConvTipoDocumentos tipoDoc : convTipoDocumentos) {

				boolean isNew = false;

				// Atribui parâmetros do pai, caso NULO.
				if (tipoDoc.getId().getCspSeq() == null) {
					tipoDoc.getId().setCspSeq(convenioSaudePlano.getId().getSeq());
					isNew = true;
				}

				if (tipoDoc.getId().getCspCnvCodigo() == null) {
					tipoDoc.getId().setCspCnvCodigo(convenioSaudePlano.getId().getCnvCodigo());
				}

				// Remove do Map, assim quem sobrar no map, são aqueles que
				// foram excluídos.
				tiposDocsMap.remove(tipoDoc.getId());

				// Metodo alterado, para atender condição abaixo, 
				// necessário exclusão do BD e depois adição.
				// "Essa verificação existe para o caso de exclusão e logo
				// em seguida inclusão do mesmo registro."
				if (isNew) {
					
					FatConvTipoDocumentos documentos = getFatConvTipoDocumentosDAO().obterPorChavePrimaria(tipoDoc.getId());
					if(documentos != null){
						getFatConvTipoDocumentosDAO().remover(documentos);
					}
					
					// Trigger BRI.
					getFaturamentoFacade().validarConvenioTipoDocumentoAntesInserir(tipoDoc);
					getFatConvTipoDocumentosDAO().persistir(tipoDoc);
					getFatConvTipoDocumentosDAO().flush();
				} else {
					getFatConvTipoDocumentosDAO().merge(tipoDoc);
				}
			}

			// Remove apenas os <code>FatConvTipoDocumentos</code> que não estão
			// mais associados ao pai.
			for (FatConvTipoDocumentos tipoDoc : tiposDocsMap.values()) {
				getFatConvTipoDocumentosDAO().refresh(tipoDoc);
				getFatConvTipoDocumentosDAO().remover(tipoDoc);
			}
			// FIM da rotina de persist/merge/remove de
			// <code>FatConvTipoDocumentos</code>.

			convenioSaudePlano.setConvPeriodos(convPeriodos);
			convenioSaudePlano.setConvPlanoAcomodacoes(convPlanoAcomodacoes);
			convenioSaudePlano.setConvTipoDocumentos(convTipoDocumentos);

			// Refresh na entidade.
			if (convenioSaudePlano.getId() != null) {
				getFatConvenioSaudePlanoDAO().merge(convenioSaudePlano);
			}

			// Commita as alterações.
			getFatConvenioSaudePlanoDAO().flush();

			return convenioSaudePlano;
		} catch (ApplicationBusinessException e) {
			// Exceções da ON.
			logError(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logError("Erro ao atualizar convenio saude plano.", e);
			//logError(e);

			if (e.getCause() != null) {
				if (ConstraintViolationException.class.equals(e.getCause()
						.getClass())) {
					ConstraintViolationException ecv = (ConstraintViolationException) e
							.getCause();

					logError(ecv.getMessage(),ecv);
					// Eventualmente subir essas exceções.
				}
			}

			throw new ApplicationBusinessException(
					ConvenioSaudeONExceptionCode.ERRO_ATUALIZAR_CONVENIO_SAUDE);
		}
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
	public FatConvenioSaudePlano obterConvenioSaudePlanoParaInternacao(Short cnvCodigo, Byte seq) {
		return getFatConvenioSaudePlanoDAO().obterConvenioSaudePlanoParaInternacao(cnvCodigo, seq);
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
	public FatConvenioSaudePlano obterConvenioSaudePlano(Short cnvCodigo, Byte seq) {
		return getFatConvenioSaudePlanoDAO().obterConvenioSaudePlano(cnvCodigo, seq);
	}

	/**
	 * Definir Grupo Convênio a partir do Pagador. Issue #3704.
	 * 
	 * @param aacPagador
	 * @return DominioGrupoConvenio
	 */
	public DominioGrupoConvenio obterGrupoConvenio(AacPagador aacPagador) {

		DominioGrupoConvenio res = null;

		if (aacPagador != null && aacPagador.getSeq() != null) {
			int seq = aacPagador.getSeq();
			switch (seq) {
			case 1:
				res = DominioGrupoConvenio.S;
				break;
			// Caso 2, 4 ou 5.
			case 2:
			case 4:
			case 5:
				res = DominioGrupoConvenio.C;
				break;
			case 3:
				res = DominioGrupoConvenio.P;
				break;
			default:
				// TODO: Não está previsto AAC_PAGADORES diferente de 1, 2,
				// 3, 4 ou 5. Caso um novo registro seja adicionado à tabela
				// AAC_PAGADORES rever essa rotina.
				break;
			}
		}

		return res;
	}

    public FatConvenioSaude obterConvenioSaudeComPagadorEUF(Short codigoConvenio) {
        return getFatConvenioSaudeDAO().obterConvenioSaudeComPagadorEUF(codigoConvenio);
    }
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	FatConvenioSaudePlanoDAO getFatConvenioSaudePlanoDAO() {
		return fatConvenioSaudePlanoDAO;
	}
	
	FatConvenioSaudeDAO getFatConvenioSaudeDAO() {
		return fatConvenioSaudeDAO;
	}
	
	FatPeriodosEmissaoDAO getFatPeriodosEmissaoDAO() {
		return fatPeriodosEmissaoDAO;
	}
	
	FatConvPlanoAcomodacoesDAO getFatConvPlanoAcomodacoesDAO() {
		return fatConvPlanoAcomodacoesDAO;
	}
	
	FatConvTipoDocumentosDAO getFatConvTipoDocumentosDAO() {
		return fatConvTipoDocumentosDAO;
	}

	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>FatConvenioSaudePlano</code>.
	 * 
	 * ORADB Trigger FATT_CSP_BRU
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarConvenioSaudePlanoAntesAtualizar(final FatConvenioSaudePlano convenioSaudePlano) throws ApplicationBusinessException {

		// TODO: Mudar para verificação em tempo de execução, pois a situação
		// pode ser alterada antes de atualizar a entidade.
		if (CoreUtil.igual(convenioSaudePlano.getIndSituacao(), DominioSituacao.A)) {
			// fatk_csp_rn.rn_cspp_ver_cps_ativ
			getFatConvenioSaudeDAO().validarConvenioSaudeAtivo(convenioSaudePlano.getId().getCnvCodigo());
		}
	}

}
