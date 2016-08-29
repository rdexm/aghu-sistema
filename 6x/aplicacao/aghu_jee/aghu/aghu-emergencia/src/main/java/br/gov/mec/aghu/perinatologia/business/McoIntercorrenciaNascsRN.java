package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoIntercorrenciaNascJn;
import br.gov.mec.aghu.model.McoIntercorrenciaNascs;
import br.gov.mec.aghu.model.McoIntercorrenciaNascsId;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaNascJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaNascsDAO;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaNascsVO;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class McoIntercorrenciaNascsRN extends BaseBusiness {

	private static final long serialVersionUID = -7824944539622686246L;

	private enum McoIntercorrenciaNascsRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, //
		MENSAGEM_SUCESSO_CADASTRO_INTERCORRENCIA, //
		MENSAGEM_SUCESSO_ALTERACAO_INTERCORRENCIA, //
		MENSAGEM_SUCESSO_EXCLUSAO_INTERCORRENCIA, //
		MENSAGEM_ERRO_DATA_HORA_INTERCORRENCIA, //
		MENSAGEM_ERRO_CARACT_SIT_EMERGENCIA, //
		ERRO_EXCLUSAO_INTERCORRENCIA;
	}

	@Inject
	private IConfiguracaoService configuracaoService;

	@Inject
	private McoIntercorrenciaNascsDAO mcoIntercorrenciaNascsDAO;

	@Inject
	private McoIntercorrenciaNascJnDAO mcoIntercorrenciaNascJnDAO;

	@EJB
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Pesquisa McoIntercorrenciaNascs por nascimento
	 * 
	 * C1 de #37859
	 * 
	 * @param nasGsoPacCodigo
	 * @param nasGsoSeqp
	 * @param nasSeqp
	 *            )
	 * @return
	 */
	public List<IntercorrenciaNascsVO> pesquisarIntercorrenciasPorNascimento(final Integer nasGsoPacCodigo, final Short nasGsoSeqp,
			final Integer nasSeqp) throws ApplicationBusinessException {

		List<IntercorrenciaNascsVO> result = new ArrayList<IntercorrenciaNascsVO>();
		List<McoIntercorrenciaNascs> list = mcoIntercorrenciaNascsDAO.pesquisarPorNascimento(nasGsoPacCodigo, nasGsoSeqp, nasSeqp);
		List<CidVO> cids = null;

		if (list != null && !list.isEmpty()) {

			List<Integer> idsCid = this.obterIdsCids(list);
			if (idsCid != null && !idsCid.isEmpty()) {
				try {
					cids = configuracaoService.pesquisarCidAtivosPorSeq(idsCid);
				} catch (ServiceException e) {
					throw new ApplicationBusinessException(McoIntercorrenciaNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
				} catch (RuntimeException e) {
					throw new ApplicationBusinessException(McoIntercorrenciaNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
				}

				if (cids != null && !cids.isEmpty()) {
					for (McoIntercorrenciaNascs mcoIntercorrenciaNascs : list) {
						String codigoCid = this.obterCodigoCid(cids, mcoIntercorrenciaNascs.getMcoIntercorrencia().getAghCid().getSeq());
						if (StringUtils.isNotBlank(codigoCid)) {
							Hibernate.initialize(mcoIntercorrenciaNascs.getMcoProcedimentoObstetricos());
							result.add(new IntercorrenciaNascsVO(codigoCid, mcoIntercorrenciaNascs));
						}
					}
				}
			}
		}
		return result;
	}

	private List<Integer> obterIdsCids(List<McoIntercorrenciaNascs> list) {
		List<Integer> idsCid = new ArrayList<>();
		for (McoIntercorrenciaNascs mcoIntercorrenciaNascs : list) {
			if (mcoIntercorrenciaNascs.getMcoIntercorrencia().getAghCid().getSeq() != null) {
				idsCid.add(mcoIntercorrenciaNascs.getMcoIntercorrencia().getAghCid().getSeq());
			}
		}
		return idsCid;
	}

	private String obterCodigoCid(List<CidVO> cids, Integer cidSeq) {
		if (cidSeq != null) {
			for (int i = 0; i < cids.size(); i++) {
				CidVO cidVO = cids.get(i);
				if (cidSeq.equals(cidVO.getSeq())) {
					return cidVO.getCodigo();
				}
			}
		}
		return null;
	}

	/**
	 * RN01 de #37859
	 * 
	 * @param nasGsoPacCodigo
	 * @param nasGsoSeqp
	 * @param nasSeqp
	 * @param consulta
	 * @param dataHoraIntercorrencia
	 * @param intercorrenciaVO
	 * @param procedimentoObstetrico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public IntercorrenciaNascsVO inserirMcoIntercorrenciaNascs(final Integer nasGsoPacCodigo, final Short nasGsoSeqp,
			final Integer nasSeqp, final Integer consulta, final Date dataHoraIntercorrencia, final IntercorrenciaVO intercorrenciaVO,
			final McoProcedimentoObstetricos procedimentoObstetrico) throws ApplicationBusinessException {

		// Monta o objeto
		McoIntercorrenciaNascsId id = new McoIntercorrenciaNascsId();
		id.setNasGsoPacCodigo(nasGsoPacCodigo);
		id.setNasGsoSeqp(nasGsoSeqp);
		id.setNasSeqp(nasSeqp);

		McoIntercorrenciaNascs mcoIntercorrenciaNascs = new McoIntercorrenciaNascs();
		mcoIntercorrenciaNascs.setId(id);
		mcoIntercorrenciaNascs.setDthrIntercorrencia(dataHoraIntercorrencia);
		mcoIntercorrenciaNascs.setMcoIntercorrencia(intercorrenciaVO.getMcoIntercorrencia());
		mcoIntercorrenciaNascs.setMcoProcedimentoObstetricos(procedimentoObstetrico);

		// Realiza a inserção
		this.inserir(mcoIntercorrenciaNascs, consulta);

		// Retorna o VO
		return new IntercorrenciaNascsVO(intercorrenciaVO.getCodigoCid(), mcoIntercorrenciaNascs);
	}

	/**
	 * RN01 de #37859
	 * 
	 * @param mcoIntercorrenciaNascs
	 * @param consulta
	 * @throws ApplicationBusinessException
	 */
	private void inserir(final McoIntercorrenciaNascs mcoIntercorrenciaNascs, Integer consulta) throws ApplicationBusinessException {
		// 1. Executar RN06
		this.validarDataHoraIntercorrencia(consulta, mcoIntercorrenciaNascs.getDthrIntercorrencia());

		// 2. Executar consulta C4 para obter o seqp para o registro que irá ser incluído
		// 3. Setar o SEQP obtido em C4 em MCO_INTERCORRENCIA_NASCS.SEQP
		// ESTES ITENS SÃO IMPLEMENTADOS NO PRÓPRIO DAO (obterValorSequencialId)

		// 4. Executar RN02
		this.preInserir(mcoIntercorrenciaNascs);

		// 5. Inserir registro em MCO_INTERCORRENCIA_NASCS
		this.mcoIntercorrenciaNascsDAO.persistir(mcoIntercorrenciaNascs);
	}

	/**
	 * RN01 de #37859
	 * 
	 * @param intercorrenciaNascsVO
	 * @param dataHoraIntercorrencia
	 * @param intercorrenciaVO
	 * @param procedimentoObstetrico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public IntercorrenciaNascsVO alterarMcoIntercorrenciaNascs(final Integer consulta, final IntercorrenciaNascsVO intercorrenciaNascsVO,
			final Date dataHoraIntercorrencia, final IntercorrenciaVO intercorrenciaVO,
			final McoProcedimentoObstetricos procedimentoObstetrico) throws ApplicationBusinessException {

		// Monta os objetos
		McoIntercorrenciaNascs original = mcoIntercorrenciaNascsDAO
				.obterOriginal(intercorrenciaNascsVO.getMcoIntercorrenciaNascs().getId());
		McoIntercorrenciaNascs mcoIntercorrenciaNascs = intercorrenciaNascsVO.getMcoIntercorrenciaNascs();
		mcoIntercorrenciaNascs.setDthrIntercorrencia(dataHoraIntercorrencia);
		mcoIntercorrenciaNascs.setMcoIntercorrencia(intercorrenciaVO.getMcoIntercorrencia());
		mcoIntercorrenciaNascs.setMcoProcedimentoObstetricos(procedimentoObstetrico);

		// Realiza a alteração
		this.alterar(mcoIntercorrenciaNascs, original, consulta);

		// Retorna o VO
		intercorrenciaNascsVO.setMcoIntercorrenciaNascs(mcoIntercorrenciaNascs);
		return intercorrenciaNascsVO;
	}

	/**
	 * RN01 de #37859
	 * 
	 * @param mcoIntercorrenciaNascs
	 * @param consulta
	 * @throws ApplicationBusinessException
	 */
	private void alterar(final McoIntercorrenciaNascs mcoIntercorrenciaNascs, McoIntercorrenciaNascs original, Integer consulta)
			throws ApplicationBusinessException {
		// 1. Executar RN06
		this.validarDataHoraIntercorrencia(consulta, mcoIntercorrenciaNascs.getDthrIntercorrencia());

		// 2. Atualizar o registro em MCO_INTERCORRENCIA_NASCS
		this.mcoIntercorrenciaNascsDAO.atualizar(mcoIntercorrenciaNascs);

		// 3. Executar RN03
		this.posAtualizar(mcoIntercorrenciaNascs, original);
	}

	/**
	 * RN04 de #37859
	 * 
	 * @param intercorrenciaNascsVO
	 * @throws ApplicationBusinessException
	 */
	public void removerMcoIntercorrenciaNascs(final IntercorrenciaNascsVO intercorrenciaNascsVO) throws ApplicationBusinessException {
		// 1. Excluir o registro de MCO_INTERCORRENCIA_NASCS
		mcoIntercorrenciaNascsDAO.removerPorId(intercorrenciaNascsVO.getMcoIntercorrenciaNascs().getId());

		// 2. Executar RN05
		this.posRemover(intercorrenciaNascsVO.getMcoIntercorrenciaNascs());
	}

	/**
	 * RN02 de #37859
	 * 
	 * @ORADB MCO_INTERCORRENCIA_NASCS.MCOT_INN_BRI
	 * 
	 * @param mcoIntercorrenciaNascs
	 */
	private void preInserir(final McoIntercorrenciaNascs mcoIntercorrenciaNascs) {
		// 1. Setar no campo CRIADO_EM a data atual
		mcoIntercorrenciaNascs.setCriadoEm(new Date());
		// 2. Setar no campo SER_MATRICULA a matricula do usuário logado no sistema
		mcoIntercorrenciaNascs.setSerMatricula(usuario.getMatricula());
		// 3. Setar no campo SER_VIN_CODIGO o código do vínculo do usuário logado no sistema
		mcoIntercorrenciaNascs.setSerVinCodigo(usuario.getVinculo());
	}

	/**
	 * RN03 de #37859
	 * 
	 * @ORADB MCO_INTERCORRENCIA_NASCS.MCOT_INN_ARU
	 * 
	 * @param mcoIntercorrenciaNascs
	 */
	private void posAtualizar(final McoIntercorrenciaNascs mcoIntercorrenciaNascs, final McoIntercorrenciaNascs original) {
		// 1. Se algum dos campos em MCO_INTERCORRENCIA_NASCS tiver sido modificado inserir o objeto original na JOURNAL
		// MCO_INTERCORRENCIA_NASCS_JN setando ‘UPD’ no campo JN_OPERATION
		this.inserirJournal(mcoIntercorrenciaNascs, original, DominioOperacoesJournal.UPD);
	}

	/**
	 * RN05 de #37859
	 * 
	 * @ORADB MCO_INTERCORRENCIA_NASCS.MCOT_INN_ARU
	 * 
	 * @param mcoIntercorrenciaNascs
	 */
	private void posRemover(final McoIntercorrenciaNascs mcoIntercorrenciaNascs) {
		// 1. Inserir o objeto original na JOURNAL MCO_INTERCORRENCIA_NASCS_JN setando ‘DEL’ no campo JN_OPERATION
		this.inserirJournal(null, mcoIntercorrenciaNascs, DominioOperacoesJournal.DEL);
	}

	/**
	 * Regras para verificar data/hora intercorrência
	 * 
	 * RN06 de #37859
	 * 
	 * @param consulta
	 * @throws ApplicationBusinessException
	 */
	private void validarDataHoraIntercorrencia(final Integer consulta, final Date dataHoraIntercorrencia)
			throws ApplicationBusinessException {
		// 1. Executa consulta C7 para a consulta em questão (ver aba consulta CO para obter número de AAC_CONSULTAS). Consulta utilizada
		// para obter o SEQ de MAM_TRIAGEM de quando a paciente entrou na emergência.
		Long trgSeq = this.emergenciaFacade.obterTrgSeq(consulta);

		// 2. Executa consulta C5 passando como parâmetro a característica ‘Enc Triagem’
		List<Short> caracteristicas = this.emergenciaFacade.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_TRIAGEM);

		// 3. Se a consulta C5 não retornar resultados disparar a mensagem “MENSAGEM_ERRO_CARACT_SIT_EMERGENCIA” passando como parâmetro ‘Em
		// Triagem’ e cancelar o processamento
		if (caracteristicas == null || caracteristicas.isEmpty()) {
			throw new ApplicationBusinessException(McoIntercorrenciaNascsRNExceptionCode.MENSAGEM_ERRO_CARACT_SIT_EMERGENCIA, "Em Triagem");
		}

		// 4. Após, executa consulta C6 utilizando como parâmetros o código do acolhimento (TRG_SEQ do primeiro registro obtido em C7) e o
		// código da situação (SEG_SEQ) obtido na consulta C5.
		List<Date> datasExtTrg = this.emergenciaFacade.obterDtHrSituacaoExtratoTriagem(trgSeq, caracteristicas.get(0));

		// 5. Se o valor de MCO_INTERCORRENCIA_NASCS.DTHR_INTERCORRENCIA for maior que a data atual ou menor que o valor
		// (DTHR_SITUACAO) do primeiro registro obtido em C6, disparar mensagem “MENSAGEM_ERRO_DATA_HORA_INTERCORRENCIA” e cancelar o
		// processamento
		if (datasExtTrg != null && !datasExtTrg.isEmpty() && (dataHoraIntercorrencia.getTime() > new Date().getTime()
				|| dataHoraIntercorrencia.getTime() < datasExtTrg.get(0).getTime())) {
			throw new ApplicationBusinessException(McoIntercorrenciaNascsRNExceptionCode.MENSAGEM_ERRO_DATA_HORA_INTERCORRENCIA);
		}
	}

	/**
	 * Insere registro na Journal
	 * 
	 * @ORADB MCO_INTERCORRENCIA_NASCS.MCOT_INN_ARD
	 * @param atual
	 * @param original
	 * @param operacao
	 */
	private void inserirJournal(final McoIntercorrenciaNascs atual, final McoIntercorrenciaNascs original,
			final DominioOperacoesJournal operacao) {
		if (atual == null || CoreUtil.modificados(atual.getDthrIntercorrencia(), original.getDthrIntercorrencia())
				|| CoreUtil.modificados(atual.getMcoIntercorrencia(), original.getMcoIntercorrencia())
				|| CoreUtil.modificados(atual.getMcoProcedimentoObstetricos(), original.getMcoProcedimentoObstetricos())) {

			McoIntercorrenciaNascJn journal = BaseJournalFactory
					.getBaseJournal(operacao, McoIntercorrenciaNascJn.class, usuario.getLogin());

			journal.setCriadoEm(original.getCriadoEm());
			journal.setDthrIntercorrencia(original.getDthrIntercorrencia());
			if (original.getMcoIntercorrencia() != null) {
				journal.setIcrSeq(original.getMcoIntercorrencia().getSeq());
			}
			journal.setNasGsoPacCodigo(original.getId().getNasGsoPacCodigo());
			journal.setNasGsoSeqp(original.getId().getNasGsoSeqp());
			journal.setNasSeqp(original.getId().getNasSeqp());
			if (original.getMcoProcedimentoObstetricos() != null) {
				journal.setObsSeq(original.getMcoProcedimentoObstetricos().getSeq());
			}
			journal.setSeqp(original.getId().getSeqp());
			journal.setSerMatricula(original.getSerMatricula());
			journal.setSerVinCodigo(original.getSerVinCodigo());

			mcoIntercorrenciaNascJnDAO.persistir(journal);
		}
	}

	public void excluirMcoIntercorrenciaNascs(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) throws ApplicationBusinessException {
		if (this.mcoIntercorrenciaNascsDAO.verificaExisteIntercorrencia(gsoPacCodigo, gsoSeqp, pSeqp)) {
			List<McoIntercorrenciaNascs> listIntercorrenciaNascs = this.mcoIntercorrenciaNascsDAO.pesquisarPorNascimento(gsoPacCodigo,
					gsoSeqp, pSeqp);

			for (McoIntercorrenciaNascs intercorrenciaNascs : listIntercorrenciaNascs) {
				McoIntercorrenciaNascs intercorrenciaNascsOriginal = this.mcoIntercorrenciaNascsDAO.obterOriginal(intercorrenciaNascs);

				this.inserirJournal(null, intercorrenciaNascsOriginal, DominioOperacoesJournal.DEL);

				try {
					this.mcoIntercorrenciaNascsDAO.remover(intercorrenciaNascs);

				} catch (RuntimeException e) {
					throw new ApplicationBusinessException(McoIntercorrenciaNascsRNExceptionCode.ERRO_EXCLUSAO_INTERCORRENCIA);
				}
			}
		}
	}
}
