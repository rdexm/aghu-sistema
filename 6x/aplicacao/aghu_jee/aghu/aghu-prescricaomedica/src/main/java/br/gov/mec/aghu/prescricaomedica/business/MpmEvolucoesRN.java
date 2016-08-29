package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoEvolucao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.MpmEvolucoesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAnamnesesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEvolucoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEvolucoesJnDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotaAdicionalEvolucoesDAO;

@Stateless
public class MpmEvolucoesRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MpmEvolucoesRN.class);
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private MpmEvolucoesJnDAO mpmEvolucoesJnDAO;

	@Inject
	private MpmNotaAdicionalEvolucoesDAO mpmNotaAdicionalEvolucoesDAO;

	@Inject
	private MpmEvolucoesDAO mpmEvolucoesDAO;

	@Inject
	private MpmAnamnesesDAO mpmAnamnesesDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = -2205745574778165789L;

	public enum MpmEvolucoesRNExceptionCode implements BusinessExceptionCode {
		EVOLUCAO_EM_USO, PRESCRICAO_NAO_INFORMATIZADA, ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO,
		MENSAGEM_ERRO_EVOLUCAO_PRAZO_VENCIDO, MENSAGEM_INCLUIR_DESCRICAO_EVOLUCAO,
		MSG_NO_PARAMETRO_TIPO_ITEM_EVOLUCAO, MENSAGEM_EVOLUCAO_JA_VALIDADA_OUTRO_USUARIO,
		MENSAGEM_ERRO_ALTERAR_SITUACAO_EVOLUCAO, MENSAGEM_ERRO_EXCLUIR_EVOLUCAO_OUTRO_USUARIO,
		MENSAGEM_ERRO_ECLUIR_EVOLUCAO_POSSUI_NA;
	}

	public MpmEvolucoes criarMpmEvolucoes(MpmAnamneses anamnese,
			Date dataReferencia, RapServidores servidor)
			throws ApplicationBusinessException {
		
		anamnese = mpmAnamnesesDAO.obterUnidadeFuncionalAtendimentoAnamnese(anamnese);

		MpmEvolucoes novaEvolucao = new MpmEvolucoes();
		Date dataAtual = new Date();
		novaEvolucao.setDthrCriacao(dataAtual);
		novaEvolucao.setPendente(DominioIndPendenteAmbulatorio.R);
		novaEvolucao.setSituacao(DominioSituacaoEvolucao.U);
		novaEvolucao.setServidor(servidor);
		novaEvolucao.setAnamnese(anamnese);
		novaEvolucao.setDthrReferencia(dataReferencia);
		Date horaValidadePME = anamnese.getAtendimento().getUnidadeFuncional()
				.getHrioValidadePme();
		Date dataHoraFim = DateUtil.comporDiaHora(
				DateUtil.adicionaDias(dataReferencia, 1), horaValidadePME);
		novaEvolucao.setDthrFim(dataHoraFim);
		this.verificarPrazoAlteracaoEvolucao(novaEvolucao);
		mpmEvolucoesDAO.persistir(novaEvolucao);
		return novaEvolucao;
	}

	public MpmEvolucoes criarMpmEvolucaoComDescricao(String descricao,
			MpmAnamneses anamnese, Date dataReferencia, RapServidores servidor)
			throws ApplicationBusinessException {
		MpmEvolucoes evolucao = criarMpmEvolucoes(anamnese, dataReferencia,
				servidor);
		evolucao.setDescricao(descricao);
		mpmEvolucoesDAO.atualizar(evolucao);
		return evolucao;
	}

	public void atualizarMpmEvolucaoEmUso(MpmEvolucoes evolucao,
			RapServidores servidor) {
		Date dataAtual = new Date();
		evolucao.setDthrAlteracao(dataAtual);
		evolucao.setSituacao(DominioSituacaoEvolucao.U);
		evolucao.setServidor(servidor);
		mpmEvolucoesDAO.atualizar(evolucao);
	}

	/**
	 * Obtém data de referência para a próxima evolucao
	 * @throws ApplicationBusinessException
	 */

	public Date obterDataReferenciaEvolucao(AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new IllegalArgumentException("Anamnese não informada.");
		}
		
		// Prescricao informatizada
		Date horaValidadePME;
		try {
			verificaSeUnidadeFuncionalEInformatizada(atendimento.getUnidadeFuncional());
			horaValidadePME = atendimento.getUnidadeFuncional()
					.getHrioValidadePme();
		} catch (Exception e) {
			AghUnidadesFuncionais aghUnidadesFuncional = this.aghUnidadesFuncionaisDAO.obterPorUnfSeq(atendimento.getUnidadeFuncional().getSeq());
			verificaSeUnidadeFuncionalEInformatizada(aghUnidadesFuncional);
			horaValidadePME = aghUnidadesFuncional.getHrioValidadePme();
		}
		Date dataAtual = new Date();
		Date dataReferencia;
		Date dataTrocaEvolucao = DateUtil.comporDiaHora(dataAtual,
				horaValidadePME);
		if (dataAtual.compareTo(dataTrocaEvolucao) < 0) {
			dataReferencia = DateUtil.adicionaDias(dataAtual, -1);
		} else {
			dataReferencia = dataAtual;
		}
		return DateUtils.truncate(dataReferencia, Calendar.DAY_OF_MONTH);
	}

	private void verificaSeUnidadeFuncionalEInformatizada(AghUnidadesFuncionais unidadesFuncional) throws ApplicationBusinessException  {
		if (!unidadesFuncional.possuiCaracteristica(
				ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)) {
			throw new ApplicationBusinessException(
					MpmEvolucoesRNExceptionCode.PRESCRICAO_NAO_INFORMATIZADA);
		}
	}
	
	/**
	 * 
	 * Verifica se o atendimento está em andamento
	 * 
	 * 
	 * 
	 * @param atendimento
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 *             ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO
	 */

	public void verificaAtendimento(AghAtendimentos atendimento)
			throws ApplicationBusinessException {

		if (!DominioPacAtendimento.S.equals(atendimento.getIndPacAtendimento())) {

			throw new ApplicationBusinessException(
					MpmEvolucoesRNExceptionCode.ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO);

		}

	}

	public void verificarEvolucaoEmUso(MpmAnamneses anamnese, Date dataAtual)
			throws ApplicationBusinessException {

		verificaAtendimento(anamnese.getAtendimento());

		// Prescricao informatizada

		if (anamnese
				.getAtendimento()
				.getUnidadeFuncional()
				.possuiCaracteristica(
						ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)) {

			throw new ApplicationBusinessException(
					MpmEvolucoesRNExceptionCode.PRESCRICAO_NAO_INFORMATIZADA);

		}

		validarOutraEvolucaoEmUso(anamnese, null, dataAtual);

	}

	/**
	 * 
	 * Valida se não há uma outra evolucao em uso para o mesmo atendimento, o
	 * 
	 * que inviabiliza criação ou edição de outras evolucao.
	 * 
	 * 
	 * 
	 * @param atendimento
	 * 
	 * @param evolucaoDesconsiderar
	 * 
	 * @param dataAtual
	 * 
	 * @throws ApplicationBusinessException
	 */

	private void validarOutraEvolucaoEmUso(MpmAnamneses anamnese,
			MpmEvolucoes evolucaoDesconsiderar, Date dataAtual)
			throws ApplicationBusinessException {

		MpmEvolucoes evolucaoEmUso = obterEvolucaoEmUso(anamnese,
				evolucaoDesconsiderar, dataAtual);

		if (evolucaoEmUso != null) {

			throw new ApplicationBusinessException(
					MpmEvolucoesRNExceptionCode.EVOLUCAO_EM_USO);

		}

	}

	/**
	 * 
	 * Verifica se existe alguma evolucao com situação "em uso"
	 * 
	 * 
	 * 
	 * @param anamnese
	 * 
	 * @param evolucaoDesconsiderar
	 * 
	 * @param dataAtual
	 */

	private MpmEvolucoes obterEvolucaoEmUso(MpmAnamneses anamnese,
			MpmEvolucoes evolucaoDesconsiderar, Date dataAtual) {

		List<MpmEvolucoes> evolucoes = mpmEvolucoesDAO.obterEvolucoesAnamnese(anamnese, dataAtual,
						new ArrayList<DominioIndPendenteAmbulatorio>());

		for (MpmEvolucoes evolucao : evolucoes) {

			if (evolucao.equals(evolucaoDesconsiderar)) {

				continue;

			}

			if (DominioSituacaoPrescricao.U.equals(evolucao.getSituacao())) {

				return evolucao;

			}

		}

		return null;

	}

	public Boolean validarAdiantamento(AghAtendimentos atendimento,
			Date dataAtual) {

		Short quantidade = atendimento.getUnidadeFuncional()
				.getNroUnidTempoPmeAdiantadas();

		Date horaValidadePME = atendimento.getUnidadeFuncional()
				.getHrioValidadePme();

		Date inicioPeriodo = DateUtil.comporDiaHora(dataAtual, horaValidadePME);

		Calendar inicioPeriodoAdiantamento = Calendar.getInstance();

		inicioPeriodoAdiantamento.setTime(inicioPeriodo);

		inicioPeriodoAdiantamento.add(Calendar.HOUR_OF_DAY, -quantidade);

		return DateUtil.entre(dataAtual, inicioPeriodoAdiantamento.getTime(),
				inicioPeriodo);

	}

	public void validarEvolucaoEmUso(MpmEvolucoes evolucao)
			throws ApplicationBusinessException {
		if (DominioSituacaoEvolucao.U.equals(evolucao.getSituacao())) {
			throw new ApplicationBusinessException(MpmEvolucoesRNExceptionCode.EVOLUCAO_EM_USO,
					new SimpleDateFormat("dd/MM/yyyy HH:mm").format( 
							evolucao.getDthrCriacao()
					),obterLoginUsuarioLogado()
			);
		}

	}

	/**
	 * 
	 * Ações do botão concluir Evolução
	 * 
	 * 
	 * 
	 * @param evolucao
	 * 
	 * @param descricaoEvolucao
	 * 
	 * @param servidor
	 * 
	 * @throws ApplicationBusinessException
	 */

	public void concluirEvolucao(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		validarPreenchimentoDescricaoEvolucao(evolucao, descricaoEvolucao, servidor);

		if (DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente())) {
			gerarJournalMpmEvolucoes(evolucao, servidor, DominioOperacoesJournal.UPD);
			salvarEvolucaoConcluida(evolucao, descricaoEvolucao, servidor);
			return;

		}

		if (DominioIndPendenteAmbulatorio.P.equals(evolucao.getPendente())) {

			gerarJournalMpmEvolucoes(evolucao, servidor,
					DominioOperacoesJournal.UPD);

			salvarEvolucaoConcluida(evolucao, descricaoEvolucao, servidor);

			return;

		}

		if (DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())) {

			if (CoreUtil.modificados(servidor, evolucao.getServidor())) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_EVOLUCAO_JA_VALIDADA_OUTRO_USUARIO);

			}

			gerarJournalMpmEvolucoes(evolucao, servidor,
					DominioOperacoesJournal.UPD);

			salvarEvolucaoConcluida(evolucao, descricaoEvolucao, servidor);

		}

	}

	/**
	 * 
	 * Ações do botão deixar Pendente
	 * 
	 * 
	 * 
	 * @param evolucao
	 * 
	 * @param descricaoEvolucao
	 * 
	 * @param servidor
	 * 
	 * @throws ApplicationBusinessException
	 */

	public void deixarPendenteEvolucao(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		if (descricaoEvolucao == null) {
			throw new ApplicationBusinessException(MpmEvolucoesRNExceptionCode.MENSAGEM_INCLUIR_DESCRICAO_EVOLUCAO);
		}

		if (DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente())
				|| DominioIndPendenteAmbulatorio.P.equals(evolucao.getPendente())) {

			validarPreenchimentoDescricaoEvolucao(evolucao, descricaoEvolucao, servidor);
			gerarJournalMpmEvolucoes(evolucao, servidor, DominioOperacoesJournal.UPD);
			salvarEvolucaoPendente(evolucao, descricaoEvolucao, servidor);
			return;

		}

		if (DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())) {

			if (CoreUtil.modificados(servidor, evolucao.getServidor())) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_EVOLUCAO_JA_VALIDADA_OUTRO_USUARIO);

			}

			String descricaoPadrao = ambulatorioFacade.getDescricaoItemEvolucao();

			if (!CoreUtil.modificados(descricaoEvolucao, descricaoPadrao)) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_INCLUIR_DESCRICAO_EVOLUCAO);

			}

			if (mpmNotaAdicionalEvolucoesDAO.possuiNotaAdicional(
					evolucao.getSeq())) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_ERRO_ALTERAR_SITUACAO_EVOLUCAO);

			}

			gerarJournalMpmEvolucoes(evolucao, servidor,
					DominioOperacoesJournal.UPD);

			salvarEvolucaoPendente(evolucao, descricaoEvolucao, servidor);

		}

	}

	public void validarEclusaoEvolucao(Long seqEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		MpmEvolucoes evolucao = mpmEvolucoesDAO.obterPorChavePrimaria(
				seqEvolucao);

		verificarPrazoAlteracaoEvolucao(evolucao);

		if (DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())) {

			if (CoreUtil.modificados(servidor, evolucao.getServidor())) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_ERRO_EXCLUIR_EVOLUCAO_OUTRO_USUARIO);

			}

			if (mpmNotaAdicionalEvolucoesDAO.possuiNotaAdicional(
					evolucao.getSeq())) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_ERRO_ECLUIR_EVOLUCAO_POSSUI_NA);

			}

		}

	}

	/**
	 * 
	 * Acoes do botão excluir evolucao
	 * 
	 * 
	 * 
	 * @param evolucao
	 * 
	 * @param servidor
	 * 
	 * @throws ApplicationBusinessException
	 */

	public void excluirEvolucao(Long seqEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		MpmEvolucoes evolucao = mpmEvolucoesDAO.obterPorChavePrimaria(
				seqEvolucao);

		verificarPrazoAlteracaoEvolucao(evolucao);

		excluir(servidor, evolucao);

	}
	
	public void desfazerAlteracoesEvolucao(Long seqEvolucao){
		
		MpmEvolucoes evolucao = mpmEvolucoesDAO.obterPorChavePrimaria(seqEvolucao);
		
		if (DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente())
				|| DominioIndPendenteAmbulatorio.P.equals(evolucao
						.getPendente())) {

		mpmEvolucoesDAO.remover(evolucao);
		mpmEvolucoesDAO.flush();
		}
	}

	public void excluir(RapServidores servidor, MpmEvolucoes evolucao)
			throws ApplicationBusinessException {

		if (DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente())
				|| DominioIndPendenteAmbulatorio.P.equals(evolucao
						.getPendente())) {

			gerarJournalMpmEvolucoes(evolucao, servidor,
					DominioOperacoesJournal.DEL);

			mpmEvolucoesDAO.remover(evolucao);

			return;

		}

		if (DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())) {

			if (CoreUtil.modificados(servidor, evolucao.getServidor())) {

				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_ERRO_EXCLUIR_EVOLUCAO_OUTRO_USUARIO);

			}

			if (mpmNotaAdicionalEvolucoesDAO.possuiNotaAdicional(evolucao.getSeq())) {
				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_ERRO_ALTERAR_SITUACAO_EVOLUCAO);
			}

			gerarJournalMpmEvolucoes(evolucao, servidor, DominioOperacoesJournal.DEL);

			mpmEvolucoesDAO.remover(evolucao);

		}

	}

	/**
	 * 
	 * Salva evolucao concluida
	 * 
	 * 
	 * 
	 * @param evolucao
	 * 
	 * @param descricaoEvolucao
	 * 
	 * @param servidor
	 * 
	 * @throws ApplicationBusinessException
	 */

	private void salvarEvolucaoConcluida(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		evolucao.setDescricao(descricaoEvolucao);

		evolucao.setDthrAlteracao(new Date());

		evolucao.setDthrPendente(null);

		evolucao.setPendente(DominioIndPendenteAmbulatorio.V);

		evolucao.setServidor(servidor);

		evolucao.setSituacao(DominioSituacaoEvolucao.L);

		evolucao.setTipoItemEvolucao(obterMamTipoItemEvolucao(servidor));

		mpmEvolucoesDAO.atualizar(evolucao);

	}

	/**
	 * 
	 * Salva evolucao pendente
	 * 
	 * 
	 * 
	 * @param evolucao
	 * 
	 * @param descricaoEvolucao
	 * 
	 * @param servidor
	 * 
	 * @throws ApplicationBusinessException
	 */

	private void salvarEvolucaoPendente(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException {

		evolucao.setDescricao(descricaoEvolucao);

		evolucao.setDthrAlteracao(new Date());

		evolucao.setDthrPendente(new Date());

		evolucao.setPendente(DominioIndPendenteAmbulatorio.P);

		evolucao.setServidor(servidor);

		evolucao.setSituacao(DominioSituacaoEvolucao.L);

		evolucao.setTipoItemEvolucao(obterMamTipoItemEvolucao(servidor));

		mpmEvolucoesDAO.atualizar(evolucao);

	}

	private MamTipoItemEvolucao obterMamTipoItemEvolucao(RapServidores servidor)
			throws ApplicationBusinessException {

		Integer tieSeq = getTieSeqEvolucao(servidor);

		if (tieSeq != null) {

			return ambulatorioFacade
					.obterMamTipoItemEvolucaoPorChavePrimaria(tieSeq);

		}

		return null;

	}

	private Integer getTieSeqEvolucao(RapServidores servidorLogado)
			throws ApplicationBusinessException {

		try {

			List<CseCategoriaProfissional> lista = cascaFacade.pesquisarCategoriaProfissional(servidorLogado);

			for (CseCategoriaProfissional categoria : lista) {

				if (categoria == null) {

					return null;

				}

				AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);

				if (categoria.getSeq().equals(
						parametro.getVlrNumerico().intValue())) {

					return parametroFacade
							.buscarAghParametro(
									AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_MED)
							.getVlrNumerico().intValue();

				}

				parametro = parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_CATEG_PROF_ENF);

				if (categoria.getSeq().equals(
						parametro.getVlrNumerico().intValue())) {

					return parametroFacade
							.buscarAghParametro(
									AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_ENF)
							.getVlrNumerico().intValue();

				}

				parametro = parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_CATEG_PROF_NUT);

				if (categoria.getSeq().equals(
						parametro.getVlrNumerico().intValue())) {

					return parametroFacade
							.buscarAghParametro(
									AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_NUTRI)
							.getVlrNumerico().intValue();

				}

				parametro = parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_CATEG_PROF_OUTROS);

				if (categoria.getSeq().equals(
						parametro.getVlrNumerico().intValue())) {

					return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_OUTROS)
							.getVlrNumerico().intValue();

				}
			}

		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(MpmEvolucoesRNExceptionCode.MSG_NO_PARAMETRO_TIPO_ITEM_EVOLUCAO);

		}

		return null;

	}

	/**
	 * 
	 * Gera Journal de Evolucoes
	 * 
	 * 
	 * 
	 * @param evolucao
	 * 
	 * @param servidor
	 * 
	 * @param operacao
	 */

	private void gerarJournalMpmEvolucoes(MpmEvolucoes evolucao,
			RapServidores servidor, DominioOperacoesJournal operacao) {

		MpmEvolucoesJn jn = new BaseJournalFactory().getBaseJournal(operacao,
				MpmEvolucoesJn.class, servidor.getUsuario());

		if (evolucao.getAnamnese() != null) {

			jn.setAnamnese(evolucao.getAnamnese().getSeq());

		}

		jn.setDescricao(evolucao.getDescricao());

		jn.setDthrAlteracao(evolucao.getDthrAlteracao());

		jn.setDthrCriacao(evolucao.getDthrCriacao());

		jn.setDthrFim(evolucao.getDthrFim());

		jn.setDthrPendente(evolucao.getDthrPendente());

		jn.setDthrReferencia(evolucao.getDthrReferencia());

		jn.setPendente(evolucao.getPendente());

		jn.setSeq(evolucao.getSeq());

		if (evolucao.getServidor() != null) {

			jn.setServidorMatricula(evolucao.getServidor().getId()
					.getMatricula());

			jn.setServidorVinCodigo(evolucao.getServidor().getId()
					.getVinCodigo());

		}

		jn.setSituacao(evolucao.getSituacao());

		if (evolucao.getTipoItemEvolucao() != null) {

			jn.setTipoItemEvolucao(evolucao.getTipoItemEvolucao().getSeq());

		}

		mpmEvolucoesJnDAO.persistir(jn);

	}

	private void validarPreenchimentoDescricaoEvolucao(MpmEvolucoes evolucao,
			String descricaoEvolucao, RapServidores servidor)
			throws ApplicationBusinessException{

		if (DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente())) {

			String descricaoPadrao = ambulatorioFacade.getDescricaoItemEvolucao();

			if (descricaoEvolucao == null || !CoreUtil.modificados(descricaoEvolucao, descricaoPadrao)) {
				throw new ApplicationBusinessException(MpmEvolucoesRNExceptionCode.MENSAGEM_INCLUIR_DESCRICAO_EVOLUCAO);
			}
		}
		if (descricaoEvolucao == null) {
			throw new ApplicationBusinessException(MpmEvolucoesRNExceptionCode.MENSAGEM_INCLUIR_DESCRICAO_EVOLUCAO);
		}

	}

	private void verificarPrazoAlteracaoEvolucao(MpmEvolucoes evolucao)
			throws ApplicationBusinessException {

		if (DateUtil.validaDataMaior(new Date(), evolucao.getDthrFim())) {

			throw new ApplicationBusinessException(
					MpmEvolucoesRNExceptionCode.MENSAGEM_ERRO_EVOLUCAO_PRAZO_VENCIDO);

		}

	}
}