package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMotivoCancelaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExamesHistDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;

/**
 * @author fwinck
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class AelResultadosExamesON extends BaseBusiness {

	@EJB
	private AelResultadoExameRN aelResultadoExameRN;

	@EJB
	private AelExamesRN aelExamesRN;

	private static final Log LOG = LogFactory.getLog(AelResultadosExamesON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;

	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@Inject
	private AelSolicitacaoExamesHistDAO aelSolicitacaoExamesHistDAO;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = 1502610711152633951L;

	/**
	 * Procedure TFormDigitacaoExame.Salvamento:
	 * 
	 * @param valoresCampos
	 * @param soeSeq
	 * @param seqp
	 * @param servidorResponsavelLiberacao
	 * @param usuarioLiberaExame
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public void persistirResultados(Map<AelParametroCamposLaudo, Object> valoresCampos, Integer soeSeq, Short seqp,
			RapServidores servidorResponsavelLiberacao, Boolean usuarioLiberaExame, String nomeMicrocomputador,
			AelItemSolicitacaoExames itemOriginal) throws BaseException {

		// Obtém transação de usuário
		// UserTransaction userTransaction = obterUserTransaction(null);

		// busca item solicitação de exame
		AelItemSolicitacaoExames itemSolicitacao = getAelItemSolicitacaoExameDAO().obterPorId(
				new AelItemSolicitacaoExamesId(soeSeq, seqp));

		// busca parametros necessários
		AghParametros parametroCodCancelaItemDept = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_COD_CANCELA_ITEM_DEPT);
		AghParametros parametroMocCancelaDept = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_MOC_CANCELA_DEPT);
		AghParametros parametroSituacaoCancelado = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SITUACAO_CANCELADO);
		AghParametros parametroSituacaoLiberado = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros parametroSituacaoExecutando = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SITUACAO_EXECUTANDO);

		/**
		 * Inicio da iteração entre os itens
		 */

		for (Entry<AelParametroCamposLaudo, Object> entry : valoresCampos.entrySet()) {
			LOG.info("Campo Laudo: " + entry.getKey().getId() + " - " + entry.getValue());

			AelParametroCamposLaudo parametroCampoLaudo = entry.getKey();
			Object valor = entry.getValue();

			if (valor != null) {
				/**
				 * Busca o último para anular ou cria um novo para inserir RN2
				 */
				AelResultadoExame resultado = getAelResultadoExameDAO()
						.buscaMaximoResultadosExamePorPCLeItemSolicitacaoExame(parametroCampoLaudo, soeSeq, seqp);

				if (resultado == null) {

					AelResultadoExameId resultTempId = new AelResultadoExameId();// ID

					resultTempId.setIseSeqp(seqp);
					resultTempId.setIseSoeSeq(soeSeq);

					resultTempId.setPclCalSeq(parametroCampoLaudo.getId().getCalSeq());
					resultTempId.setPclSeqp(parametroCampoLaudo.getId().getSeqp());
					resultTempId.setPclVelEmaExaSigla(parametroCampoLaudo.getId().getVelEmaExaSigla());
					resultTempId.setPclVelEmaManSeq(parametroCampoLaudo.getId().getVelEmaManSeq());
					resultTempId.setPclVelSeqp(parametroCampoLaudo.getId().getVelSeqp());
					resultTempId.setSeqp(0);

					resultado = new AelResultadoExame();
					resultado.setId(resultTempId);

				} else {
					resultado.setAnulacaoLaudo(true);
					resultado.setAlteradoEm(new Date());
					/* Atualiza */
					getAelResultadoExameRN().atualizar(resultado, nomeMicrocomputador);
				}
				/** Fim da anulação **/

				/**
				 * Insere um novo RN3.1
				 * **/
				AelResultadoExameId novoResultadoId = new AelResultadoExameId();// ID

				novoResultadoId.setIseSeqp(resultado.getId().getIseSeqp());
				novoResultadoId.setIseSoeSeq(resultado.getId().getIseSoeSeq());
				novoResultadoId.setPclCalSeq(resultado.getId().getPclCalSeq());
				novoResultadoId.setPclSeqp(resultado.getId().getPclSeqp());
				novoResultadoId.setPclVelEmaExaSigla(resultado.getId().getPclVelEmaExaSigla());
				novoResultadoId.setPclVelEmaManSeq(resultado.getId().getPclVelEmaManSeq());
				novoResultadoId.setPclVelSeqp(resultado.getId().getPclVelSeqp());
				novoResultadoId.setSeqp(resultado.getId().getSeqp() + 1);

				AelResultadoExame novoResultado = new AelResultadoExame();
				novoResultado.setId(novoResultadoId);
				novoResultado.setAnulacaoLaudo(false);
				novoResultado.setParametroCampoLaudo(parametroCampoLaudo);
				// .............espera um pouco para inserir, verifica
				// outras regras

				/**
				 * RN3.2
				 */

				/** RN 3.2.1 **/
				if (parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO)
						&& valor != null && !valor.toString().equals("")) {// 4
					novoResultado.setValor(trataValorResultado(parametroCampoLaudo, valor));
					// insere novo registro
					getAelResultadoExameRN().persistir(novoResultado);
					/** ........................................... */
				}

				/** RN 3.2.2 **/
				if (parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_CODIFICADO)) {// 2

					/** RN3.2.2.1 **/
					if (parametroCampoLaudo.getCampoLaudo().getGrupoResultadoCodificado() != null) {
						if (valor instanceof AelResultadoCodificado) {
							novoResultado.setResultadoCodificado((AelResultadoCodificado) valor);
						}
					} else {
						if (valor instanceof AelExameGrupoCaracteristica) {
							novoResultado.setResultadoCaracteristica(((AelExameGrupoCaracteristica) valor)
									.getResultadoCaracteristica());
						}
					}
					// insere novo registro
					getAelResultadoExameRN().persistir(novoResultado);
					/** ........................................... */

					/** RN3.2.2.2 **/
					if (parametroCampoLaudo.getCampoLaudo().getCancelaItemDept()
							&& parametroCodCancelaItemDept.getVlrTexto().equals(
									novoResultado.getResultadoCodificado().getDescricao())) {
						// se item diferente de liberado, cancela ??????

						// situação cancelado e motivo do cancelamento
						AelSitItemSolicitacoes sitCancelado = getAelSitItemSolicitacoesDAO().obterPeloId(
								parametroSituacaoCancelado.getVlrTexto());
						AelMotivoCancelaExames motivoCancelamento = getAelMotivoCancelaExamesDAO().obterPeloId(
								parametroMocCancelaDept.getVlrNumerico().shortValue());

						if (!itemSolicitacao.getSituacaoItemSolicitacao().getCodigo()
								.equals(parametroSituacaoLiberado.getVlrTexto())) {
							// seta como cancelado e o motivo
							itemSolicitacao.setSituacaoItemSolicitacao(sitCancelado);
							itemSolicitacao.setAelMotivoCancelaExames(motivoCancelamento);
						}

						// busca todos os itens dependentes para serem
						// cancelados
						List<AelItemSolicitacaoExames> itensCancelar = getAelItemSolicitacaoExameDAO()
								.pesquisarItensSolicitacaoExameDependentesCancelaAutomaticoPorSituacaoIgualOuDiferente(
										soeSeq, seqp, false, parametroSituacaoLiberado.getVlrTexto());
						// atualiza os itens
						for (AelItemSolicitacaoExames item : itensCancelar) {
							item.setSituacaoItemSolicitacao(sitCancelado);
							item.setAelMotivoCancelaExames(motivoCancelamento);
							// atualiza os itens
							getSolicitacaoExameFacade().atualizar(item, nomeMicrocomputador, itemOriginal);
						}

						// anula todos os resulados dos itens canceldos
						// acima
						/***************************************************/
						for (AelItemSolicitacaoExames item : itensCancelar) {
							List<AelResultadoExame> resultadosDependentes = item.getResultadoExames();
							for (AelResultadoExame resultadosDependente : resultadosDependentes) {
								resultadosDependente.setAnulacaoLaudo(true);
								getAelResultadoExameRN().atualizar(resultadosDependente, nomeMicrocomputador);
							}
						}
						/***************************************************/
					}// cancela item
				}

				/** RN 3.2.3 **/
				/** RN 3.2.4 **/
				if ((parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_ALFANUMERICO) || parametroCampoLaudo
						.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_LONGO))
						&& valor != null
						&& !valor.toString().equals("")) {// 1
															// ou
					novoResultado.setDescricao((String) valor); // //5
					// insere novo registro
					getAelResultadoExameRN().persistir(novoResultado);
					/** ........................................... */
				}
				/**
				 * RN 3.2.4 implementada acima
				 */
			}
		}
		/**
		 * RN 4
		 */
		if (itemSolicitacao != null && itemSolicitacao.getAelUnfExecutaExames() != null
				&& itemSolicitacao.getAelUnfExecutaExames().getIndLiberaResultAutom()) {
			if (itemSolicitacao.getSituacaoItemSolicitacao().getCodigo()
					.equals(parametroSituacaoLiberado.getVlrTexto())) {
				itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
						parametroSituacaoExecutando.getVlrTexto()));

				// atualiza uma vez o item
				getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);

				/***************************************************/
				itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
						parametroSituacaoLiberado.getVlrTexto()));
				itemSolicitacao.setDthrLiberada(new Date());
				// atualiza pela segunda vez o item
				getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);
			} else {

				itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
						parametroSituacaoLiberado.getVlrTexto()));
				// atualiza uma vez o item
				getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);
			}
		}

		/**
		 * RN 5
		 */

		// se nçao libera automaticamente
		if (itemSolicitacao != null
				&& itemSolicitacao.getAelUnfExecutaExames() != null
				&& !itemSolicitacao.getAelUnfExecutaExames().getIndLiberaResultAutom()
				&& !itemSolicitacao.getSituacaoItemSolicitacao().getCodigo()
						.equals(parametroSituacaoLiberado.getVlrTexto())) {
			// Se liberado, seta a situação para EXECUTANDO e depois para
			// liberado novamente...??????????
			if (itemSolicitacao.getSituacaoItemSolicitacao().getCodigo()
					.equals(parametroSituacaoLiberado.getVlrTexto())) {
				itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
						parametroSituacaoExecutando.getVlrTexto()));

				// atualiza uma vez o item
				getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);
				/***************************************************/
			}
		}
		
		/**
		 * RN 6
		 */
		// atualiza pela segunda vez o item se o usuário libera o exame
		if (usuarioLiberaExame) {
			
			if (servidorResponsavelLiberacao != null) {
				itemSolicitacao.setServidorResponsabilidade(servidorResponsavelLiberacao);
			}
			
			itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
					parametroSituacaoLiberado.getVlrTexto()));
			itemSolicitacao.setDthrLiberada(new Date());
			getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);
		} else {
			itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
					parametroSituacaoExecutando.getVlrTexto()));
			itemSolicitacao.setDthrLiberada(null);
			getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);
		}
		
	}
	
	public List<VRapServCrmAelVO> obterListaResponsavelLiberacao(String paramPesquisa, boolean filtrarMatricula) throws ApplicationBusinessException {
		String[] parametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONSELHOS_V_RAP_SERV_CRM_AEL).getVlrTexto().split(",");
		VRapServCrmAelVO filtro = new VRapServCrmAelVO();
		
		if (filtrarMatricula) {
			filtro.setMatricula(Integer.valueOf(paramPesquisa));
			
		} else if (StringUtils.isNumeric(paramPesquisa)) {
			filtro.setNroRegConselho(paramPesquisa);
			
		} else if (StringUtils.isNotEmpty(paramPesquisa)) {
			filtro.setNome(paramPesquisa);
		}
		
		List<VRapServCrmAelVO> listaRetorno = this.registroColaboradorFacade.pesquisarViewRapServCrmAelVO(filtro, parametros);
		
		return listaRetorno;
	}

	private Long trataValorResultado(AelParametroCamposLaudo parametroCampoLaudo, Object valor) {
		String valorTemp = valor.toString();

		if (valorTemp.indexOf(',') > -1 || valorTemp.indexOf('.') > -1) {

			if (valorTemp.indexOf('.') > -1) {

				if (valorTemp.indexOf(',') == -1) {
					int casasFaltando = parametroCampoLaudo.getQuantidadeCasasDecimais()
							- valorTemp.substring(valorTemp.lastIndexOf('.') + 1, valorTemp.length()).length();
					if (casasFaltando > 0) {
						valorTemp = StringUtils.rightPad(valorTemp, valorTemp.length() + (casasFaltando), "0");
					}
				}

				valorTemp = valorTemp.replace(".", "");

			}

			if (valorTemp.indexOf(',') > -1) {
				int casasFaltando = parametroCampoLaudo.getQuantidadeCasasDecimais()
						- valorTemp.substring(valorTemp.indexOf(',') + 1, valorTemp.length()).length();
				if (casasFaltando > 0) {
					valorTemp = StringUtils.rightPad(valorTemp, valorTemp.length() + (casasFaltando - 1), "0");
				}
				valorTemp = valorTemp.replace(",", "");
			}

		} else if (StringUtils.isNumeric(valorTemp) && parametroCampoLaudo.getQuantidadeCasasDecimais() != null
				&& parametroCampoLaudo.getQuantidadeCasasDecimais() != 0) {

			valorTemp = StringUtils.rightPad(valorTemp,
					valorTemp.length() + parametroCampoLaudo.getQuantidadeCasasDecimais(), "0");

		} else {
			valorTemp = valor.toString();
		}

		if (valorTemp != null && StringUtils.isNumeric(valorTemp)) {
			return Long.parseLong(valorTemp);
		}
		return null;
	}

	/**
	 * Obter userTransaction
	 * 
	 * @param userTransaction
	 * @return
	 */
	// protected UserTransaction obterUserTransaction(UserTransaction
	// userTransaction) {
	// if (userTransaction == null) {
	// userTransaction = (UserTransaction)
	// org.jboss.seam.org.jboss.seam.transaction.transaction;
	// try {
	// userTransaction.setTransactionTimeout(60*60); // um dia
	// } catch (Exception e) {
	// logError("Erro ao setar um timeout", e);
	// }
	// }
	// try {
	// if (userTransaction.isNoTransaction()) {
	// userTransaction.begin();
	// }
	// } catch (Exception e) {
	// logError("Erro ao iniciar transação", e);
	// }
	// return userTransaction;
	// }

	/**
	 * ORADB PROCEDURE TFormDigitacaoExame.LiberarExame
	 * 
	 * @param itemSolicitacao
	 * @throws BaseException
	 */
	public void liberarExame(AelItemSolicitacaoExames itemSolicitacao, String nomeMicrocomputador,
			AelItemSolicitacaoExames itemOriginal) throws BaseException {
		final String situacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO)
				.getVlrTexto();
		// RN1
		if (itemSolicitacao.getSituacaoItemSolicitacao().getCodigo().equals(situacao)) {
			final AelSitItemSolicitacoes sitItemSolicitacao = this.getAelSitItemSolicitacoesDAO().obterPeloId("EX");
			itemSolicitacao.setSituacaoItemSolicitacao(sitItemSolicitacao);
			getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);
		}

		// RN2
		final AelSitItemSolicitacoes sitItemSolicitacao = this.getAelSitItemSolicitacoesDAO().obterPeloId(situacao);
		itemSolicitacao.setSituacaoItemSolicitacao(sitItemSolicitacao);
		getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);

	}

	/**
	 * Procedure TFormDigitacaoExame.Exclusao
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param usuarioAnulaExame
	 * @throws BaseException
	 */
	public void anularResultado(Integer soeSeq, Short seqp, Boolean usuarioAnulaExame, String nomeMicrocomputador,
			AelItemSolicitacaoExames itemOriginal) throws BaseException {

		// busca item solicitação de exame
		AelItemSolicitacaoExames itemSolicitacao = getAelItemSolicitacaoExameDAO().obterPorId(soeSeq, seqp);
		AelItemSolicitacaoExames itemSolicitacaoOriginal = clonarAelItemSolicitacaoExame(itemSolicitacao);

		/**
		 * RN2: Se na RN1 o usuário clicar no botão ‘Sim’ então
		 */
		if (usuarioAnulaExame) {

			AghParametros parametroMocAnulaLaudo = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_MOC_ANULACAO_LAUDO);
			AghParametros parametroSituacaoCancelado = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_SITUACAO_CANCELADO);
			AghParametros parametroSituacaoAColetar = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_SITUACAO_A_COLETAR);
			AghParametros parametroSituacaoAExecutar = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);

			/**
			 * RN2.1
			 */
			// situação cancelado, a coletar e motivo do cancelamento
			AelSitItemSolicitacoes sitCancelado = getAelSitItemSolicitacoesDAO().obterPeloId(
					parametroSituacaoCancelado.getVlrTexto());
			AelSitItemSolicitacoes sitAColetar = getAelSitItemSolicitacoesDAO().obterPeloId(
					parametroSituacaoAColetar.getVlrTexto());
			AelSitItemSolicitacoes sitAExecutar = getAelSitItemSolicitacoesDAO().obterPeloId(
					parametroSituacaoAExecutar.getVlrTexto());
			AelMotivoCancelaExames motivoCancelamento = getAelMotivoCancelaExamesDAO().obterPeloId(
					parametroMocAnulaLaudo.getVlrNumerico().shortValue());

			/**
			 * RN2.2 melhoria: não faço update agora, espero pelo resultado da
			 * segunda regra para não efetuar duas vezes update
			 */
			// seta como cancelado e o motivo
			itemSolicitacao.setSituacaoItemSolicitacao(sitCancelado);
			itemSolicitacao.setAelMotivoCancelaExames(motivoCancelamento);

			/**
			 * RN2.2.1
			 */
			if (motivoCancelamento.getIndRetornaAExecutar().equals(DominioSimNao.S) && itemSolicitacao != null
					&& itemSolicitacao.getAelAmostraItemExames() != null
					&& itemSolicitacao.getAelAmostraItemExames().size() > 0) {

				// seta o motivo como null e a situação como A COLETAR
				itemSolicitacao.setSituacaoItemSolicitacao(sitAColetar);
				itemSolicitacao.setAelMotivoCancelaExames(null);

			}

			// Atualiza o item principal
			getSolicitacaoExameFacade().atualizar(itemSolicitacao, nomeMicrocomputador, itemOriginal);

			/**
			 * RN2.3 Atualiza os dependentes
			 */
			// busca todos os itens dependentes para serem cancelados
			List<AelItemSolicitacaoExames> itensDependentes = getAelItemSolicitacaoExameDAO()
					.pesquisarItensSolicitacaoExameDependentesCancelaAutomaticoPorSituacaoIgualOuDiferente(soeSeq,
							seqp, true, null);

			// atualiza os itens
			for (AelItemSolicitacaoExames item : itensDependentes) {

				// Se tem amostraas então realiza o seguinte update:
				if (item.getAelAmostraItemExames().size() > 0) {
					item.setSituacaoItemSolicitacao(sitAColetar);
					item.setAelMotivoCancelaExames(null);

				} else {
					item.setSituacaoItemSolicitacao(sitAExecutar);
					item.setAelMotivoCancelaExames(null);
				}

				// atualiza os itens
				getSolicitacaoExameFacade().atualizar(item, nomeMicrocomputador, itemOriginal);
			}
		} else {

			AghParametros parametroSituacaoAreaExecutora = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			itemSolicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(
					parametroSituacaoAreaExecutora.getVlrTexto()));
			getSolicitacaoExameFacade().atualizar(itemSolicitacao, itemSolicitacaoOriginal, nomeMicrocomputador);

		}

		List<AelResultadoExame> resultados = getAelResultadoExameDAO().listarResultadosExame(itemSolicitacao);
		for (AelResultadoExame aelResultadoExame : resultados) {
			aelResultadoExame.setAnulacaoLaudo(true);
			getAelResultadoExameRN().atualizar(aelResultadoExame, nomeMicrocomputador);
		}
	}

	private AelItemSolicitacaoExames clonarAelItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacao) {
		AelItemSolicitacaoExames copia = new AelItemSolicitacaoExames();

		copia.setId(itemSolicitacao.getId());
		copia.setDocResultadoExame(itemSolicitacao.getDocResultadoExame());
		copia.setAelExtratoItemSolicitacao(itemSolicitacao.getAelExtratoItemSolicitacao());
		copia.setSolicitacaoExame(itemSolicitacao.getSolicitacaoExame());
		copia.setSituacaoItemSolicitacao(itemSolicitacao.getSituacaoItemSolicitacao());
		copia.setExame(itemSolicitacao.getExame());
		copia.setMaterialAnalise(itemSolicitacao.getMaterialAnalise());
		copia.setUnidadeFuncional(itemSolicitacao.getUnidadeFuncional());
		copia.setAelUnfExecutaExames(itemSolicitacao.getAelUnfExecutaExames());
		copia.setExameMatAnalise(itemSolicitacao.getExameMatAnalise());
		copia.setTipoColeta(itemSolicitacao.getTipoColeta());
		copia.setIndUsoO2(itemSolicitacao.getIndUsoO2());
		copia.setIndGeradoAutomatico(itemSolicitacao.getIndGeradoAutomatico());
		copia.setIntervaloColeta(itemSolicitacao.getIntervaloColeta());
		copia.setRegiaoAnatomica(itemSolicitacao.getRegiaoAnatomica());
		copia.setAelMotivoCancelaExames(itemSolicitacao.getAelMotivoCancelaExames());
		copia.setTipoTransporte(itemSolicitacao.getTipoTransporte());
		copia.setDescRegiaoAnatomica(itemSolicitacao.getDescRegiaoAnatomica());
		copia.setDescMaterialAnalise(itemSolicitacao.getDescMaterialAnalise());
		copia.setNroAmostras(itemSolicitacao.getNroAmostras());
		copia.setIntervaloDias(itemSolicitacao.getIntervaloDias());
		copia.setIntervaloHoras(itemSolicitacao.getIntervaloHoras());
		copia.setDthrProgramada(itemSolicitacao.getDthrProgramada());
		copia.setPrioridadeExecucao(itemSolicitacao.getPrioridadeExecucao());
		copia.setDataImpSumario(itemSolicitacao.getDataImpSumario());
		copia.setServidorResponsabilidade(itemSolicitacao.getServidorResponsabilidade());
		copia.setItemSolicitacaoExame(itemSolicitacao.getItemSolicitacaoExame());
		copia.setIndImprimiuTicket(itemSolicitacao.getIndImprimiuTicket());
		copia.setDthrLiberada(itemSolicitacao.getDthrLiberada());
		copia.setTipoEmissaoSumario(itemSolicitacao.getTipoEmissaoSumario());
		copia.setEtiologiaInfeccao(itemSolicitacao.getEtiologiaInfeccao());
		copia.setServidorEtiologia(itemSolicitacao.getServidorEtiologia());
		copia.setDthrEtiologia(itemSolicitacao.getDthrEtiologia());
		copia.setUieUoeSeq(itemSolicitacao.getUieUoeSeq());
		copia.setUieSeqp(itemSolicitacao.getUieSeqp());
		copia.setIndCargaContador(itemSolicitacao.getIndCargaContador());
		copia.setCirurgia(itemSolicitacao.getCirurgia());
		copia.setIndImpressoLaudo(itemSolicitacao.getIndImpressoLaudo());
		copia.setFormaRespiracao(itemSolicitacao.getFormaRespiracao());
		copia.setLitrosOxigenio(itemSolicitacao.getLitrosOxigenio());
		copia.setPercOxigenio(itemSolicitacao.getPercOxigenio());
		copia.setUnfSeqAvisa(itemSolicitacao.getUnfSeqAvisa());
		copia.setIndPossuiImagem(itemSolicitacao.getIndPossuiImagem());
		copia.setIndTicketPacImp(itemSolicitacao.getIndTicketPacImp());
		copia.setIndInfComplImp(itemSolicitacao.getIndInfComplImp());
		copia.setIndTipoMsgCxPostal(itemSolicitacao.getIndTipoMsgCxPostal());
		copia.setDthrMsgCxPostal(itemSolicitacao.getDthrMsgCxPostal());
		copia.setComplementoMotCanc(itemSolicitacao.getComplementoMotCanc());
		copia.setNumeroApOrigem(itemSolicitacao.getNumeroApOrigem());
		copia.setNumeroAp(itemSolicitacao.getNumeroAp());
		copia.setTipoTransporteUn(itemSolicitacao.getTipoTransporteUn());
		copia.setIndUsoO2Un(itemSolicitacao.getIndUsoO2Un());
		copia.setPacOruAccNumber(itemSolicitacao.getPacOruAccNumber());
		copia.setItemSolicitacaoExames(itemSolicitacao.getItemSolicitacaoExames());
		copia.setAelAmostraItemExames(itemSolicitacao.getAelAmostraItemExames());
		copia.setInformacaoSolicitacaoUnidadeExecutoras(itemSolicitacao.getInformacaoSolicitacaoUnidadeExecutoras());
		copia.setResultadoExames(itemSolicitacao.getResultadoExames());
		copia.setNotaAdicional(itemSolicitacao.getNotaAdicional());
		copia.setAelExameMaterialAnalise(itemSolicitacao.getAelExameMaterialAnalise());
		copia.setItemHorarioAgendado(itemSolicitacao.getItemHorarioAgendado());
		copia.setAelRespostasQuestoes(itemSolicitacao.getAelRespostasQuestoes());
		copia.setAelExameApItemSolic(itemSolicitacao.getAelExameApItemSolic());
		copia.setAelOrdExameMatAnalise(itemSolicitacao.getAelOrdExameMatAnalise());
		copia.setIndexOrigem(itemSolicitacao.getIndexOrigem());
		copia.setVersion(itemSolicitacao.getVersion());
		// copia.setAelExameInternetGrupoArea(itemSolicitacao.getAelExameInternetGrupoArea());

		return copia;
	}

	public List<ExameAmostraColetadaVO> listarDadosDataExamePorAmostrasColetadasHist(Integer codPaciente,
			String paramSitCodLiberado, String paramSitCodAreaExec) {

		List<ExameAmostraColetadaVO> result = new ArrayList<ExameAmostraColetadaVO>();

		result = getAelSolicitacaoExamesHistDAO().listarExamesSolicManAtdHist(codPaciente);

		// Union
		result.addAll(getAelSolicitacaoExamesHistDAO().listarExamesSolicManAtvHist(codPaciente));

		// apenas para forçar a entrada pelo menos a 1a vez no if do loop
		// abaixo foi atribuído um valor que nunca existirá que é o -1
		Integer soeSeqAnterior = -1;
		Short seqpAnterior = -1;

		Date dataExame = null;

		// Busca data de exames e grupos de material
		for (ExameAmostraColetadaVO vo : result) {
			if (!vo.getSoeSeq().equals(soeSeqAnterior) || !vo.getSeqp().equals(seqpAnterior)) {
				soeSeqAnterior = vo.getSoeSeq();
				seqpAnterior = vo.getSeqp();
				// percorre todos os registros buscando a data de exame
				dataExame = getAelExamesRN().obterDataExameHist(soeSeqAnterior, seqpAnterior, paramSitCodLiberado,
						paramSitCodAreaExec);
			}
			vo.setDtExame(dataExame);
		}

		return result;
	}

	/**
	 * Obtém dados de exames em ordem cronológica das tabelas de histórico
	 */
	public List<ExameOrdemCronologicaVO> obterDadosOrdemCronologicaArvorePolHist(Integer codPaciente,
			String paramSitCodLiberado, String paramSitCodAreaExec) {
		List<ExameOrdemCronologicaVO> result = new ArrayList<ExameOrdemCronologicaVO>();

		result = getAelSolicitacaoExamesHistDAO().listarCriteriaExameOrdemCronologicaSolManAtdHist(codPaciente);

		// Union
		result.addAll(getAelSolicitacaoExamesHistDAO().listarCriteriaExameOrdemCronologicaSolManAtvHist(codPaciente));

		// apenas para forçar a entrada pelo menos a 1a vez no if do loop
		// abaixo foi atribuído um valor que nunca existirá que é o -1
		Integer soeSeqAnterior = -1;
		Short seqpAnterior = -1;

		Date dataExame = null;

		for (ExameOrdemCronologicaVO vo : result) {
			if (!vo.getSoeSeq().equals(soeSeqAnterior) || !vo.getSeqp().equals(seqpAnterior)) {
				soeSeqAnterior = vo.getSoeSeq();
				seqpAnterior = vo.getSeqp();

				// percorre todos os registros buscando a data de exame
				dataExame = getAelExamesRN().obterDataExameHist(soeSeqAnterior, seqpAnterior, paramSitCodLiberado,
						paramSitCodAreaExec);
			}

			vo.setDtExame(dataExame);
		}

		return result;
	}

	protected AelExamesRN getAelExamesRN() {
		return aelExamesRN;
	}

	protected AelSolicitacaoExamesHistDAO getAelSolicitacaoExamesHistDAO() {
		return aelSolicitacaoExamesHistDAO;
	}

	protected AelResultadoExameRN getAelResultadoExameRN() {
		return aelResultadoExameRN;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO() {
		return aelMotivoCancelaExamesDAO;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

}
