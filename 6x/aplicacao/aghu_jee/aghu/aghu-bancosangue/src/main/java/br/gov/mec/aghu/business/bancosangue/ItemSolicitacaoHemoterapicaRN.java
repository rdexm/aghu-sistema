package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsItensSolHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.bancosangue.vo.AtualizarMatriculaVinculoServidorVO;
import br.gov.mec.aghu.bancosangue.vo.ItemSolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

/**
 * Implementação da package ABSK_ISH_RN.
 */

@Stateless
public class ItemSolicitacaoHemoterapicaRN extends BaseBusiness implements	Serializable {
	
	@EJB
	private JustificativaItemSolicitacaoHemoterapicaRN justificativaItemSolicitacaoHemoterapicaRN;
	@EJB
	private BancoDeSangueRN bancoDeSangueRN;
	
	private static final Log LOG = LogFactory.getLog(ItemSolicitacaoHemoterapicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AbsComponenteSanguineoDAO absComponenteSanguineoDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO absSolicitacoesHemoterapicasDAO;
	
	@Inject
	private AbsItensSolHemoterapicasDAO absItensSolHemoterapicasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AbsProcedHemoterapicoDAO absProcedHemoterapicoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2255191528420119260L;

	private enum ItemSolicitacaoHemoterapicaRNExceptionCode implements
			BusinessExceptionCode {
		ABS_00376, ABS_00377, ABS_00378, ABS_00379, ABS_00380, ABS_00381, ABS_00382, ABS_00385, ABS_00386, ABS_00387, ABS_00388, ABS_00389, ABS_00390, ABS_00401, ABS_00477, ABS_00875
	}

	public void excluirItemSolicitacaoHemoterapica(AbsItensSolHemoterapicas itemSolHemoterapica) throws ApplicationBusinessException {
		AbsItensSolHemoterapicas itemSolicHemo = getAbsItensSolHemoterapicasDAO().obterPorChavePrimaria(itemSolHemoterapica.getId());
		
		if(itemSolicHemo.getItemSolicitacaoHemoterapicaJustificativas() != null) {
			for (AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa : itemSolicHemo.getItemSolicitacaoHemoterapicaJustificativas()) {
				getJustificativaItemSolicitacaoHemoterapicaRN().excluirJustificativaItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapicaJustificativa);
			}
		}
		
		getAbsItensSolHemoterapicasDAO().remover(itemSolicHemo);
		
		// Chamada de trigger "after statement" (enforce)
		this.enforceItemSolicitacaoHemoterapica(itemSolicHemo, TipoOperacaoEnum.DELETE);
	}
	
	public AbsItensSolHemoterapicas inserirItemSolicitacaoHemoterapica(
			AbsItensSolHemoterapicas itemSolHemoterapica) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preInserir(itemSolHemoterapica);
		
		 getAbsItensSolHemoterapicasDAO().persistir(itemSolHemoterapica);
		 return itemSolHemoterapica;
	}

	public AbsItensSolHemoterapicas atualizarItemSolicitacaoHemoterapica(
			AbsItensSolHemoterapicas itemSolHemoterapica) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preAtualizar(itemSolHemoterapica);
		//if(getAbsItensSolHemoterapicasDAO().contains(itemSolHemoterapica)) {
		itemSolHemoterapica = getAbsItensSolHemoterapicasDAO().merge(itemSolHemoterapica);
		//}
		return itemSolHemoterapica;
	}
	
	
	public void preInserir(AbsItensSolHemoterapicas itemSolHemoterapica)
	throws ApplicationBusinessException {

		/* chama as críticas de before INSERT  da ABS_ITENS_SOL_HEMOTERAPICAS */
		AtualizarMatriculaVinculoServidorVO atualizarMatriculaVinculoServidorVO = this.atualizarMatriculaVinculoServidor(null, null, itemSolHemoterapica.getDthrExecProcedimento(), itemSolHemoterapica.getServidor());

		if(atualizarMatriculaVinculoServidorVO != null) {
			RapServidoresId idServidor = new RapServidoresId(atualizarMatriculaVinculoServidorVO
					.getMatricula(), atualizarMatriculaVinculoServidorVO.getVinculo());
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
			itemSolHemoterapica.setServidor(servidor);
		}

		this.verificaAtributo(itemSolHemoterapica.getFrequencia(), itemSolHemoterapica.getQtdeAplicacoes(), itemSolHemoterapica.getQtdeUnidades(), itemSolHemoterapica.getQtdeMl());

		if(itemSolHemoterapica.getComponenteSanguineo() != null) {
			this.verificaComponenteSanguineo(itemSolHemoterapica.getComponenteSanguineo().getCodigo());
		}

		if(itemSolHemoterapica.getComponenteSanguineo() != null) {
			this.verificaIndicadoresComponenteSanguineo(itemSolHemoterapica.getComponenteSanguineo().getCodigo(), itemSolHemoterapica.getComponenteSanguineo().getIndIrradiado(), 
					itemSolHemoterapica.getComponenteSanguineo().getIndFiltrado(), itemSolHemoterapica.getComponenteSanguineo().getIndLavado(),
					itemSolHemoterapica.getComponenteSanguineo().getIndAferese());
		}
		
		if(itemSolHemoterapica.getProcedHemoterapico() != null) {
			this.verificaProcedimentoHemoterapico(itemSolHemoterapica.getProcedHemoterapico().getCodigo());
		}
		
		if(itemSolHemoterapica.getTipoFreqAprazamento() != null) {
			this.verificaTipoFrequencia(itemSolHemoterapica.getFrequencia(), itemSolHemoterapica.getTipoFreqAprazamento().getSeq());
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void preAtualizar(AbsItensSolHemoterapicas itemSolHemoterapica)
	throws ApplicationBusinessException {
		ItemSolicitacaoHemoterapicaVO itemSolicitacaoHemoterapicaVO = getAbsItensSolHemoterapicasDAO().obterItemSolicitacaoHemoterapicaVO(itemSolHemoterapica.getId().getSheAtdSeq(), itemSolHemoterapica.getId().getSheSeq(), itemSolHemoterapica.getId().getSequencia());
		
		/* chama as críticas de before UPDATE da ABS_ITENS_SOL_HEMOTERAPICAS */
		AtualizarMatriculaVinculoServidorVO atualizarMatriculaVinculoServidorVO = this.atualizarMatriculaVinculoServidor((itemSolHemoterapica.getProcedHemoterapico() != null)?itemSolHemoterapica.getProcedHemoterapico().getCodigo():null, itemSolicitacaoHemoterapicaVO.getDthrExecProcedimento(), itemSolHemoterapica.getDthrExecProcedimento(), (itemSolHemoterapica.getServidor() != null) ? itemSolHemoterapica.getServidor() : null);

		if(atualizarMatriculaVinculoServidorVO != null && atualizarMatriculaVinculoServidorVO.getMatricula() != null && atualizarMatriculaVinculoServidorVO.getVinculo() != null) {
			RapServidoresId idServidor = new RapServidoresId(atualizarMatriculaVinculoServidorVO.getMatricula(), atualizarMatriculaVinculoServidorVO.getVinculo());
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
			itemSolHemoterapica.setServidor(servidor);
		}

		this.verificaAtributo(itemSolHemoterapica.getFrequencia(), itemSolHemoterapica.getQtdeAplicacoes(), itemSolHemoterapica.getQtdeUnidades(), itemSolHemoterapica.getQtdeMl());
		
		this.verificaComponenteSanguineo((itemSolHemoterapica.getComponenteSanguineo() != null)?itemSolHemoterapica.getComponenteSanguineo().getCodigo():null);
		
		this.verificaIndicadoresComponenteSanguineo((itemSolHemoterapica.getComponenteSanguineo() != null)?itemSolHemoterapica.getComponenteSanguineo().getCodigo():null, itemSolHemoterapica.getIndIrradiado(), itemSolHemoterapica.getIndFiltrado(), itemSolHemoterapica.getIndLavado(), itemSolHemoterapica.getIndAferese());
		
		if(CoreUtil.modificados(itemSolHemoterapica.getIndIrradiado(), itemSolicitacaoHemoterapicaVO.getIndIrradiado())
				|| CoreUtil.modificados(itemSolHemoterapica.getIndFiltrado(), itemSolicitacaoHemoterapicaVO.getIndFiltrado())
				|| CoreUtil.modificados(itemSolHemoterapica.getIndLavado(), itemSolicitacaoHemoterapicaVO.getIndLavado())
				|| CoreUtil.modificados(itemSolHemoterapica.getIndAferese(), itemSolicitacaoHemoterapicaVO.getIndAferese())
				|| CoreUtil.modificados(itemSolHemoterapica.getFrequencia(), itemSolicitacaoHemoterapicaVO.getFrequencia())
				|| CoreUtil.modificados(itemSolHemoterapica.getQtdeAplicacoes(), itemSolicitacaoHemoterapicaVO.getQtdeAplicacoes())
				|| CoreUtil.modificados(itemSolHemoterapica.getQtdeUnidades(), itemSolicitacaoHemoterapicaVO.getQtdeUnidades())
				|| CoreUtil.modificados(itemSolHemoterapica.getQtdeMl(), itemSolicitacaoHemoterapicaVO.getQtdeMl())
				|| CoreUtil.modificados(itemSolHemoterapica.getDthrExecProcedimento(), itemSolicitacaoHemoterapicaVO.getDthrExecProcedimento())
				|| CoreUtil.modificados(itemSolHemoterapica.getDthrDigtExecucao(), itemSolicitacaoHemoterapicaVO.getDthrDigtExecucao())) {
			this.verificaPendente(itemSolicitacaoHemoterapicaVO.getSheAtdSeq(), itemSolicitacaoHemoterapicaVO.getSheSeq());
		}
		
		this.verificaProcedimentoHemoterapico((itemSolHemoterapica.getProcedHemoterapico()!=null)?itemSolHemoterapica.getProcedHemoterapico().getCodigo():null);
		
		this.verificaTipoFrequencia(itemSolHemoterapica.getFrequencia(), (itemSolHemoterapica.getTipoFreqAprazamento() != null)?itemSolHemoterapica.getTipoFreqAprazamento().getSeq():null);
	}
	
	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_ATU_SERVIDOR
	 * 
	 * Atualiza matricula e vinculo do servidor que informoua data e hora de
	 * execução do procedimento.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AtualizarMatriculaVinculoServidorVO atualizarMatriculaVinculoServidor(
			String codigoProcedimentoHemoterapico,
			Date oldDthrExecucaoProcedimento, Date newDthrExecucaoProcedimento,
			final RapServidores servidor)
			throws ApplicationBusinessException {
		AtualizarMatriculaVinculoServidorVO atualizarMatriculaVinculoServidorVO = null;
		if (codigoProcedimentoHemoterapico != null
				&& CoreUtil.modificados(oldDthrExecucaoProcedimento,
						newDthrExecucaoProcedimento)) {
			AtualizaCartaoPontoVO atualizaCartaoPontoVO = getBancoDeSangueRN()
					.atualizaCartaoPontoServidor();
			if (atualizaCartaoPontoVO != null) {
				atualizarMatriculaVinculoServidorVO = new AtualizarMatriculaVinculoServidorVO(
						atualizaCartaoPontoVO.getMatricula(),
						atualizaCartaoPontoVO.getVinCodigo());
			}
		}
		return atualizarMatriculaVinculoServidorVO;
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_ATRIBUTO
	 * 
	 * Testa se as quantidades não são nulas, então devem ser maiores que zero.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaAtributo(Short frequencia,
			Short quantidadeAplicacoes, Byte quantidadeUnidades,
			Short quantidadeMl) throws ApplicationBusinessException {
		if (frequencia != null && frequencia <= 0) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00379);
		}

		if (quantidadeAplicacoes != null && quantidadeAplicacoes <= 0) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00380);
		}

		if (quantidadeUnidades != null && quantidadeUnidades <= 0) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00381);
		}

		if (quantidadeMl != null && quantidadeMl <= 0) {
			throw new ApplicationBusinessException(
					ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00382);
		}
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_COMP_SAN
	 * 
	 * Se for informado componente sangüíneo este deve estar ativo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaComponenteSanguineo(String codigoComponenteSanguineo)
			throws ApplicationBusinessException {
		if (codigoComponenteSanguineo != null) {
			AbsComponenteSanguineo componenteSanguineo = getAbsComponenteSanguineoDAO()
					.obterPorChavePrimaria(codigoComponenteSanguineo);

			if (componenteSanguineo != null
					&& !DominioSituacao.A.equals(componenteSanguineo
							.getIndSituacao())) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00386);
			}
		}
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_IND_COMP
	 * 
	 * Consiste indicadores de irradiado, filtrado e lavado.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaIndicadoresComponenteSanguineo(
			String codigoComponenteSanguineo, Boolean indIrradiado,
			Boolean indFiltrado, Boolean indLavado, Boolean indAferese)
			throws ApplicationBusinessException {
		if (codigoComponenteSanguineo != null) {
			AbsComponenteSanguineo componenteSanguineo = getAbsComponenteSanguineoDAO()
					.obterPorChavePrimaria(codigoComponenteSanguineo);

			if (componenteSanguineo != null) {
				if (indIrradiado != null && indIrradiado
						&& !componenteSanguineo.getIndIrradiado()) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00387);
				} else if (indFiltrado != null && indFiltrado
						&& !componenteSanguineo.getIndFiltrado()) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00388);
				} else if (indLavado != null && indLavado
						&& !componenteSanguineo.getIndLavado()) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00389);
				} else if (indAferese != null && indAferese
						&& !componenteSanguineo.getIndAferese()) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00875);
				}
			}
		}
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_PENDENTE
	 * 
	 * Verifica se o ind_pendente permite alterações e exclusões no item da
	 * solicitação.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaPendente(Integer seqAtendimentoSolicitacaoHemoterapica,
			Integer seqSolicitacaoHemoterapica)
			throws ApplicationBusinessException {
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = getAbsSolicitacoesHemoterapicasDAO()
				.obterPorChavePrimaria(
						new AbsSolicitacoesHemoterapicasId(
								seqAtendimentoSolicitacaoHemoterapica,
								seqSolicitacaoHemoterapica));

		if (solicitacaoHemoterapica != null) {
			DominioIndPendenteItemPrescricao indPendente = solicitacaoHemoterapica
					.getIndPendente();
			if (!DominioIndPendenteItemPrescricao.B.equals(indPendente)
					&& !DominioIndPendenteItemPrescricao.P.equals(indPendente)) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00390);
			}
		}
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_PROC_HEM
	 * 
	 * Se for informado procedimento hemoterapico este deve estar ativo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaProcedimentoHemoterapico(
			String codigoProcedimentoHemoterapico)
			throws ApplicationBusinessException {
		if (codigoProcedimentoHemoterapico != null) {
			AbsProcedHemoterapico procedimentoHemoterapico = getAbsProcedHemoterapicoDAO()
					.obterPorChavePrimaria(codigoProcedimentoHemoterapico);

			if (procedimentoHemoterapico != null
					&& !DominioSituacao.A.equals(procedimentoHemoterapico
							.getIndSituacao())) {
				throw new ApplicationBusinessException(
						ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00385);
			}
		}
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_TIPO_FRE
	 * 
	 * Valida o tipo de frequencia do item solicitado.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaTipoFrequencia(Short frequencia,
			Short seqTipoFrequenciaAprazamento)
			throws ApplicationBusinessException {
		if (seqTipoFrequenciaAprazamento != null) {
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = getPrescricaoMedicaFacade()
					.obterTipoFrequenciaAprazamentoId(seqTipoFrequenciaAprazamento);
			if (tipoFrequenciaAprazamento != null) {
				if (!DominioSituacao.A.equals(tipoFrequenciaAprazamento
						.getIndSituacao())) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00376);
				}

				if (tipoFrequenciaAprazamento.getIndUsoHemoterapia() == null
						|| !tipoFrequenciaAprazamento.getIndUsoHemoterapia()) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00377);
				}

				if (frequencia == null
						&& tipoFrequenciaAprazamento.getIndDigitaFrequencia() != null
						&& tipoFrequenciaAprazamento.getIndDigitaFrequencia()) {
					throw new ApplicationBusinessException(
							ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00378);
				}
			}
		}
	}

	/**
	 * ORADB Procedure ABSK_ISH_RN.RN_ISHP_VER_ULT_ITEM
	 * 
	 * A Exclusão do último item da solicitação pendente aciona a exclusão da
	 * própria solicitação.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaUltimoItem(Integer seqAtendimentoSolicitacaoHemoterapica, Integer seqSolicitacaoHemoterapica) throws ApplicationBusinessException {
		// Ate esta data (14/MAR/2014) esta Variavel de Sessao é setada para false no sessionCreate e nao mais alterada.
		//Boolean autoExcluirItemHemoterapia = (Boolean) obter ContextoSessao("ABSK_ISH_RN_AUTO_EXCLUIR_ITEM_HEMOTERAPICA");
		Boolean autoExcluirItemHemoterapia = Boolean.FALSE;
		if (autoExcluirItemHemoterapia == null || !autoExcluirItemHemoterapia) {
			Long count = getAbsItensSolHemoterapicasDAO().pesquisarItensSolicitacaoHemoterapicaCount(seqAtendimentoSolicitacaoHemoterapica,	seqSolicitacaoHemoterapica, null);
			if (count == null || count == 0) {
				// Não excluir o último item da solicitação hemoterapica
				throw new ApplicationBusinessException(ItemSolicitacaoHemoterapicaRNExceptionCode.ABS_00477);
			}
		}
	}

	/**
	 * ORADB ABST_ISH_BRI
	 * 
	 * Implementação da trigger de before INSERT da tabela
	 * ABS_ITENS_SOL_HEMOTERAPICAS
	 * 
	 * @param itemSolicitacaoHemoterapica
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.NPathComplexity", "ucd"})
	public void preInsereItemSolicitacaoHemoterapica(
			AbsItensSolHemoterapicas itemSolicitacaoHemoterapica)
			throws ApplicationBusinessException {
		RapServidores servidor = itemSolicitacaoHemoterapica.getServidor();
		AtualizarMatriculaVinculoServidorVO atualizarMatriculaVinculoServidorVO = this
				.atualizarMatriculaVinculoServidor(null, null,
						itemSolicitacaoHemoterapica.getDthrExecProcedimento(),
						servidor != null ? servidor : null);
		if (atualizarMatriculaVinculoServidorVO != null) {
			servidor = getRegistroColaboradorFacade().obterRapServidor(
					new RapServidoresId(atualizarMatriculaVinculoServidorVO
							.getMatricula(),
							atualizarMatriculaVinculoServidorVO.getVinculo()));
			itemSolicitacaoHemoterapica.setServidor(servidor);
		}

		this.verificaAtributo(itemSolicitacaoHemoterapica.getFrequencia(),
				itemSolicitacaoHemoterapica.getQtdeAplicacoes(),
				itemSolicitacaoHemoterapica.getQtdeUnidades(),
				itemSolicitacaoHemoterapica.getQtdeMl());

		this.verificaComponenteSanguineo(itemSolicitacaoHemoterapica
				.getComponenteSanguineo() != null ? itemSolicitacaoHemoterapica
				.getComponenteSanguineo().getCodigo() : null);

		this.verificaIndicadoresComponenteSanguineo(itemSolicitacaoHemoterapica
				.getComponenteSanguineo() != null ? itemSolicitacaoHemoterapica
				.getComponenteSanguineo().getCodigo() : null,
				itemSolicitacaoHemoterapica.getIndIrradiado(),
				itemSolicitacaoHemoterapica.getIndFiltrado(),
				itemSolicitacaoHemoterapica.getIndLavado(),
				itemSolicitacaoHemoterapica.getIndAferese());

		this.verificaProcedimentoHemoterapico(itemSolicitacaoHemoterapica
				.getProcedHemoterapico() != null ? itemSolicitacaoHemoterapica
				.getProcedHemoterapico().getCodigo() : null);

		this
				.verificaTipoFrequencia(
						itemSolicitacaoHemoterapica.getFrequencia(),
						itemSolicitacaoHemoterapica.getTipoFreqAprazamento() != null ? itemSolicitacaoHemoterapica
								.getTipoFreqAprazamento().getSeq()
								: null);
	}

	/**
	 * ORADB ABST_ISH_BRU
	 * 
	 * Implementação da trigger de before UPDATE da tabela
	 * ABS_ITENS_SOL_HEMOTERAPICAS
	 * 
	 * @param newItemSolicitacaoHemoterapica
	 * @param oldItemSolicitacaoHemoterapica
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "ucd"})
	public void preUpdateItemSolicitacaoHemoterapica(
			AbsItensSolHemoterapicas newItemSolicitacaoHemoterapica,
			AbsItensSolHemoterapicas oldItemSolicitacaoHemoterapica)
			throws ApplicationBusinessException {
		RapServidores servidor = newItemSolicitacaoHemoterapica
				.getServidor() != null ? newItemSolicitacaoHemoterapica
				.getServidor() : null;
		this
				.atualizarMatriculaVinculoServidor(
						oldItemSolicitacaoHemoterapica != null
								&& oldItemSolicitacaoHemoterapica
										.getProcedHemoterapico() != null ? oldItemSolicitacaoHemoterapica
								.getProcedHemoterapico().getCodigo()
								: null,
						oldItemSolicitacaoHemoterapica != null ? oldItemSolicitacaoHemoterapica
								.getDthrExecProcedimento()
								: null, newItemSolicitacaoHemoterapica
								.getDthrExecProcedimento(),
								servidor != null ? servidor : null);

		this.verificaAtributo(newItemSolicitacaoHemoterapica.getFrequencia(),
				newItemSolicitacaoHemoterapica.getQtdeAplicacoes(),
				newItemSolicitacaoHemoterapica.getQtdeUnidades(),
				newItemSolicitacaoHemoterapica.getQtdeMl());

		this
				.verificaComponenteSanguineo(newItemSolicitacaoHemoterapica
						.getComponenteSanguineo() != null ? newItemSolicitacaoHemoterapica
						.getComponenteSanguineo().getCodigo()
						: null);

		this
				.verificaIndicadoresComponenteSanguineo(
						newItemSolicitacaoHemoterapica.getComponenteSanguineo() != null ? newItemSolicitacaoHemoterapica
								.getComponenteSanguineo().getCodigo()
								: null, newItemSolicitacaoHemoterapica
								.getIndIrradiado(),
						newItemSolicitacaoHemoterapica.getIndFiltrado(),
						newItemSolicitacaoHemoterapica.getIndLavado(),
						newItemSolicitacaoHemoterapica.getIndAferese());

		if ((newItemSolicitacaoHemoterapica != null && oldItemSolicitacaoHemoterapica != null)
				&& ((newItemSolicitacaoHemoterapica == null && oldItemSolicitacaoHemoterapica != null)
						|| (newItemSolicitacaoHemoterapica != null && oldItemSolicitacaoHemoterapica == null)
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getIndIrradiado(),
								newItemSolicitacaoHemoterapica
										.getIndIrradiado())
						|| CoreUtil
								.modificados(oldItemSolicitacaoHemoterapica
										.getIndFiltrado(),
										newItemSolicitacaoHemoterapica
												.getIndFiltrado())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getIndLavado(), newItemSolicitacaoHemoterapica
								.getIndLavado())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getIndAferese(),
								newItemSolicitacaoHemoterapica.getIndAferese())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getFrequencia(),
								newItemSolicitacaoHemoterapica.getFrequencia())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getQtdeAplicacoes(),
								newItemSolicitacaoHemoterapica
										.getQtdeAplicacoes())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getQtdeUnidades(),
								newItemSolicitacaoHemoterapica
										.getQtdeUnidades())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getQtdeMl(), newItemSolicitacaoHemoterapica
								.getQtdeMl())
						|| CoreUtil.modificados(oldItemSolicitacaoHemoterapica
								.getDthrExecProcedimento(),
								newItemSolicitacaoHemoterapica
										.getDthrExecProcedimento()) || CoreUtil
						.modificados(oldItemSolicitacaoHemoterapica
								.getDthrDigtExecucao(),
								newItemSolicitacaoHemoterapica
										.getDthrDigtExecucao()))) {
			AbsSolicitacoesHemoterapicas oldSolicitaoHemoterapica = oldItemSolicitacaoHemoterapica
					.getSolicitacaoHemoterapica();

			this
					.verificaPendente(
							oldSolicitaoHemoterapica != null
									&& oldSolicitaoHemoterapica.getId() != null ? oldSolicitaoHemoterapica
									.getId().getAtdSeq()
									: null,
							oldSolicitaoHemoterapica != null
									&& oldSolicitaoHemoterapica.getId() != null ? oldSolicitaoHemoterapica
									.getId().getSeq()
									: null);
		}

		this
				.verificaProcedimentoHemoterapico(newItemSolicitacaoHemoterapica
						.getProcedHemoterapico() != null ? newItemSolicitacaoHemoterapica
						.getProcedHemoterapico().getCodigo()
						: null);

		this
				.verificaTipoFrequencia(
						newItemSolicitacaoHemoterapica.getFrequencia(),
						newItemSolicitacaoHemoterapica.getTipoFreqAprazamento() != null ? newItemSolicitacaoHemoterapica
								.getTipoFreqAprazamento().getSeq()
								: null);
	}

	/**
	 * ORADB Procedure ABSP_ENFORCE_ISH_RULES
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void enforceItemSolicitacaoHemoterapica(
			AbsItensSolHemoterapicas itemSolicitacaoHemoterapica,
			TipoOperacaoEnum tipoOperacao)
			throws ApplicationBusinessException {
		if (TipoOperacaoEnum.DELETE.equals(tipoOperacao)) {
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = itemSolicitacaoHemoterapica.getSolicitacaoHemoterapica();
			this.verificaUltimoItem(
					(solicitacaoHemoterapica != null && solicitacaoHemoterapica.getId() != null) 
						? solicitacaoHemoterapica.getId().getAtdSeq() : null
					, (solicitacaoHemoterapica != null && solicitacaoHemoterapica.getId() != null)
					? solicitacaoHemoterapica.getId().getSeq() : null
			);
		}
	}

	

	protected AbsComponenteSanguineoDAO getAbsComponenteSanguineoDAO() {
		return absComponenteSanguineoDAO;
	}

	protected AbsProcedHemoterapicoDAO getAbsProcedHemoterapicoDAO() {
		return absProcedHemoterapicoDAO;
	}

	protected BancoDeSangueRN getBancoDeSangueRN() {
		return bancoDeSangueRN;
	}

	protected AbsSolicitacoesHemoterapicasDAO getAbsSolicitacoesHemoterapicasDAO() {
		return absSolicitacoesHemoterapicasDAO;
	}

	protected AbsItensSolHemoterapicasDAO getAbsItensSolHemoterapicasDAO() {
		return absItensSolHemoterapicasDAO;
	}

	protected JustificativaItemSolicitacaoHemoterapicaRN getJustificativaItemSolicitacaoHemoterapicaRN() {
		return justificativaItemSolicitacaoHemoterapicaRN;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}	

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
