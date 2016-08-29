package br.gov.mec.aghu.sig.custos.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.dao.SigAtividadesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;

@Stateless
public class ManterObjetosCustoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterObjetosCustoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;

@EJB
private ICentroCustoFacade centroCustoFacade;

@Inject
private SigAtividadesDAO sigAtividadesDAO;

@Inject
private SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO;

	private static final long serialVersionUID = -8041190985139315215L;

	public enum ManterObjetosCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PHI_JA_ASSOCIADO_OBJETO_CUSTO,
		MENSAGEM_DATA_INICIO_INVALIDA_PROGRAMADA,
		MENSAGEM_DATA_INICIO_OBJETO_CUSTO_INVALIDA,
		MENSAGEM_DATA_INICIO_OBRIGATORIA_OBJETO_CUSTO,
		MENSAGEM_VERSAO_OBJETO_CUSTO_INVALIDA,
		MENSAGEM_EXCLUSAO_NAO_PERMITIDA_OBJETO_CUSTO_ASSOCIADO,
		MENSAGEM_EXCLUSAO_NAO_PERMITIDA_OBJETO_CUSTO_ATIVO,
		MENSAGEM_ATIVIDADE_OU_OC_JA_VINCULADO,
		MENSAGEM_NRO_EXECUCAO_OBRIGATORIO,
		MENSAGEM_COMPOSICAO_CONTEM_CALCULOS,
		MESSAGEM_OBJETO_CUSTO_NAO_CLIENTES,
		MENSAGEM_PHI_ASSOCIADO_OUTRO_OC,
		MESSAGEM_OBJETO_CUSTO_NAO_DIRECIONADOR_CLIENTE;
	}

	public List<FccCentroCustos> pesquisarCentroCustoSemObrasPeloTipoCentroProducao(Object paramPesquisa, Integer seqCentroProducao, DominioSituacao situacao,
			DominioTipoCentroProducaoCustos... tipos) throws BaseException {

		List<DominioTipoCentroProducaoCustos> listaTiposPossiveis = Arrays.asList(tipos);
		List<FccCentroCustos> listaAntiga = this.getCentroCustoFacade().pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, seqCentroProducao,
				situacao);
		List<FccCentroCustos> listaNova = new ArrayList<FccCentroCustos>();

		for (FccCentroCustos fccCentroCustos : listaAntiga) {
			if (fccCentroCustos.getCentroProducao() != null && listaTiposPossiveis.contains(fccCentroCustos.getCentroProducao().getIndTipo())) {
				listaNova.add(fccCentroCustos);
			}
		}

		return listaNova;
	}

	public void validarInclusaoPhiObjetoCusto(SigObjetoCustoPhis sigObjetoCustoPhis, List<SigObjetoCustoPhis> listaPhis) throws ApplicationBusinessException {
		if (listaPhis != null && !listaPhis.isEmpty()) {
			for (SigObjetoCustoPhis itemLista : listaPhis) {
				if (sigObjetoCustoPhis.getFatProcedHospInternos().getSeq().equals(itemLista.getFatProcedHospInternos().getSeq())) {
					throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_PHI_JA_ASSOCIADO_OBJETO_CUSTO);
				}
			}
		}

		//Efetua Validação para objetos de custos sem centro de custo
		SigObjetoCustoCcts sigObjetoCustoCctsPrincipal = sigObjetoCustoPhis.getSigObjetoCustoVersoes().getSigObjetoCustoCctsPrincipal();
		if (sigObjetoCustoCctsPrincipal == null || sigObjetoCustoCctsPrincipal.getSeq() == null) {
			List<SigObjetoCustoVersoes> listVersoes = this.getSigObjetoCustoVersoesDAO().buscarListaObjetoCustoPeloPHI(
					sigObjetoCustoPhis.getFatProcedHospInternos());
			if (listVersoes != null && !listVersoes.isEmpty()) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_PHI_ASSOCIADO_OUTRO_OC, listVersoes.get(0).getNroVersao(), listVersoes.get(0).getSigObjetoCustos().getNome());
			}
		} else {
			List<SigObjetoCustoVersoes> listVersoes = this.getSigObjetoCustoVersoesDAO().buscarListaObjetoCustoPeloPHIeCC(
					sigObjetoCustoPhis.getFatProcedHospInternos(), sigObjetoCustoCctsPrincipal.getFccCentroCustos());
			if (listVersoes != null && !listVersoes.isEmpty()) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_PHI_ASSOCIADO_OUTRO_OC, listVersoes.get(0).getNroVersao(), listVersoes.get(0).getSigObjetoCustos().getNome());
			}
		}
	}

	public boolean verificaVersoesAtivas(SigObjetoCustoVersoes objetoCustoVersao) {
		boolean versaoAtiva = false;
		if (objetoCustoVersao != null) {
			List<SigObjetoCustoVersoes> listObjetoCustoVersoes = getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoes(
					objetoCustoVersao.getSigObjetoCustos(), DominioSituacaoVersoesCustos.A);

			for (SigObjetoCustoVersoes versao : listObjetoCustoVersoes) {
				if (versao.getSeq() != null && !versao.getSeq().equals(objetoCustoVersao.getSeq())) {
					versaoAtiva = true;
					break;
				}
			}
		}
		return versaoAtiva;
	}

	public void validarInclusaoComposicaoObjetoCustoAssistencial(SigObjetoCustoComposicoes objetoCustoComposicao, List<SigObjetoCustoComposicoes> listComposicao)
			throws ApplicationBusinessException {
		if (objetoCustoComposicao.getSigDirecionadores() != null && objetoCustoComposicao.getSigDirecionadores().getIndNroExecucoes()
				&& objetoCustoComposicao.getNroExecucoes() == null) {
			throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_NRO_EXECUCAO_OBRIGATORIO);
		}

		for (SigObjetoCustoComposicoes listaObjetos : listComposicao) {
			if (objetoCustoComposicao.getSigAtividades() != null && listaObjetos.getSigAtividades() != null
					&& objetoCustoComposicao.getSigAtividades().getSeq().intValue() == listaObjetos.getSigAtividades().getSeq().intValue()
					&& objetoCustoComposicao.getSigDirecionadores().getSeq().intValue() == listaObjetos.getSigDirecionadores().getSeq().intValue()) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_ATIVIDADE_OU_OC_JA_VINCULADO, objetoCustoComposicao
						.getSigAtividades().getNome());

			}
			if (objetoCustoComposicao.getSigObjetoCustoVersoesCompoe() != null
					&& listaObjetos.getSigObjetoCustoVersoesCompoe() != null
					&& objetoCustoComposicao.getSigObjetoCustoVersoesCompoe().getSeq().intValue() == listaObjetos.getSigObjetoCustoVersoesCompoe().getSeq()
							.intValue()
					&& objetoCustoComposicao.getSigDirecionadores().getSeq().intValue() == listaObjetos.getSigDirecionadores().getSeq().intValue()) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_ATIVIDADE_OU_OC_JA_VINCULADO, objetoCustoComposicao
						.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome());
			}
		}
	}

	public void validarInclusaoComposicaoObjetoCustoApoio(SigObjetoCustoComposicoes objetoCustoComposicao, List<SigObjetoCustoComposicoes> listComposicao)
			throws ApplicationBusinessException {
		for (SigObjetoCustoComposicoes listaObjetos : listComposicao) {
			if (objetoCustoComposicao.getSigAtividades() != null && listaObjetos.getSigAtividades() != null
					&& objetoCustoComposicao.getSigAtividades().getSeq().intValue() == listaObjetos.getSigAtividades().getSeq().intValue()) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_ATIVIDADE_OU_OC_JA_VINCULADO, objetoCustoComposicao
						.getSigAtividades().getNome());

			}
		}
	}

	public void validarExclusaoComposicaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		if (objetoCustoVersao != null
				&& (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A) || objetoCustoVersao.getIndSituacao().equals(
						DominioSituacaoVersoesCustos.I))) {
			Calendar dataAtual = Calendar.getInstance();
			Calendar dataInicioVigencia = Calendar.getInstance();
			int prazoMessesMinimo = Integer.valueOf(1);
			dataInicioVigencia.setTime(objetoCustoVersao.getDataInicio());
			int difMes = dataAtual.get(Calendar.MONTH) - dataInicioVigencia.get(Calendar.MONTH);
			int difAno = ((dataAtual.get(Calendar.YEAR) - dataInicioVigencia.get(Calendar.YEAR)) * 12);
			int prazoMesses = difAno + difMes;
			if (prazoMesses >= prazoMessesMinimo) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_COMPOSICAO_CONTEM_CALCULOS);
			}
		}
	}

	public boolean validarCentroCustoComposicaoAssistencial(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, FccCentroCustos centroCustoObjetoCusto) {
		if (centroCustoObjetoCusto != null) {
			for (SigObjetoCustoComposicoes composicao : listaObjetoCustoComposicoes) {
				
				if (composicao.getSigAtividades() != null){
					
					SigAtividades atividade = this.getSigAtividadesDAO().merge(composicao.getSigAtividades());
					
					SigAtividadeCentroCustos atividadeCentroCusto = atividade.getSigAtividadeCentroCustos(); 
					
					if( atividadeCentroCusto != null && atividadeCentroCusto.getFccCentroCustos().getCodigo() != null && (!atividade.getSigAtividadeCentroCustos().getFccCentroCustos().equals(centroCustoObjetoCusto))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public Object[] validarAtividadePertenceAoCentroCustoComposicaoApoio(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes,
			List<FccCentroCustos> listaCC) {
		Object[] retorno = new Object[2];
		retorno[0] = true;
		List<String> listaAtividadesForaCentroCusto = new ArrayList<String>();
		for (SigObjetoCustoComposicoes composicao : listaObjetoCustoComposicoes) {

			SigAtividades atividade = this.getSigAtividadesDAO().merge(composicao.getSigAtividades());
			
			Set<SigAtividadeCentroCustos> setCustoAtividade = atividade.getListSigAtividadeCentroCustos();
			FccCentroCustos centroCustoAtividade = null;
			for (SigAtividadeCentroCustos sigAtividadeCentroCustos : setCustoAtividade) {
				centroCustoAtividade = sigAtividadeCentroCustos.getFccCentroCustos();
			}

			if (!listaCC.contains(centroCustoAtividade) && !atividade.getListSigAtividadeCentroCustos().isEmpty()) {
				retorno[0] = false;
				listaAtividadesForaCentroCusto.add(atividade.getNome());
			}
		}
		retorno[1] = formatMensagemAtividades(listaAtividadesForaCentroCusto);

		return retorno;
	}

	private String formatMensagemAtividades(List<String> atividades) {
		String formatada = "";
		if (atividades != null && !atividades.isEmpty()) {
			for (String string : atividades) {
				if (formatada.equals("")) {
					formatada = formatada.concat(string);
				} else {
					formatada = formatada.concat(", " + string);
				}
			}
		}
		return formatada;
	}

	public void validaDataInicioAutomatica(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		// Quando a data de vigência não for informada e a situação estiver ativa
		if (objetoCustoVersao.getDataInicio() == null && objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			// Deve gerar automáticamente a data
			this.gerarDataInicioAutomatica(objetoCustoVersao);
		}

		// A data de vigência só deve ser obrigatória nas situações: "Programada" e "Ativa".
		if (objetoCustoVersao.getDataInicio() == null
				&& (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.P) || objetoCustoVersao.getIndSituacao().equals(
						DominioSituacaoVersoesCustos.A))) {
			throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_DATA_INICIO_OBRIGATORIA_OBJETO_CUSTO);
		}

	}

	public void gerarDataInicioAutomatica(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {

		Calendar dtIniVig = Calendar.getInstance();
		dtIniVig.setTime(new Date());
		dtIniVig.set(Calendar.DAY_OF_MONTH, 1);
		dtIniVig.set(Calendar.HOUR_OF_DAY, 0);
		dtIniVig.set(Calendar.MINUTE, 0);
		dtIniVig.set(Calendar.SECOND, 0);
		dtIniVig.set(Calendar.MILLISECOND, 0);

		objetoCustoVersao.setDataInicio(dtIniVig.getTime());
	}

	public void validarObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, DominioSituacaoVersoesCustos situacaoAnterior) throws ApplicationBusinessException {
		if (objetoCustoVersao == null) {
			throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_VERSAO_OBJETO_CUSTO_INVALIDA);
		}

		validaDataInicioAutomatica(objetoCustoVersao);
		validaDataInicioProgramada(objetoCustoVersao);
		validaDataInicioAtivada(objetoCustoVersao, situacaoAnterior);
		validaDataInicioVersaoInativaEProgramada(objetoCustoVersao);
	}

	public void inativarVersaoAnterior(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {

		if (objetoCustoVersao != null && objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {

			List<SigObjetoCustoVersoes> listObjetoCustoVersoes = getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoes(
					objetoCustoVersao.getSigObjetoCustos(), DominioSituacaoVersoesCustos.A);

			for (SigObjetoCustoVersoes versaoAnterior : listObjetoCustoVersoes) {

				// Data foi informada
				if (objetoCustoVersao.getDataInicio() != null) {

					// Passa pelas validações da RN05
					this.validaDataInicioAtivada(objetoCustoVersao, null);
					this.validaDataInicioVersaoInativaEProgramada(objetoCustoVersao);

					Calendar dtFimVig = Calendar.getInstance();

					dtFimVig.setTime(objetoCustoVersao.getDataInicio());
					dtFimVig.add(Calendar.MONTH, -1);

					if (versaoAnterior.getDataInicio().after(dtFimVig.getTime())) {
						versaoAnterior.setDataFim(versaoAnterior.getDataInicio());
					} else {
						versaoAnterior.setDataFim(dtFimVig.getTime());
					}

					// Data não foi informada
				} else {

					Calendar dtFimVig = Calendar.getInstance();
					dtFimVig.setTime(new Date());
					dtFimVig.set(Calendar.DAY_OF_MONTH, 1);
					dtFimVig.set(Calendar.HOUR_OF_DAY, 0);
					dtFimVig.set(Calendar.MINUTE, 0);
					dtFimVig.set(Calendar.SECOND, 0);
					dtFimVig.set(Calendar.MILLISECOND, 0);

					dtFimVig.add(Calendar.MONTH, -1);

					if (versaoAnterior.getDataInicio().after(dtFimVig.getTime())) {
						versaoAnterior.setDataFim(versaoAnterior.getDataInicio());
					} else {
						versaoAnterior.setDataFim(dtFimVig.getTime());
					}
				}

				versaoAnterior.setIndSituacao(DominioSituacaoVersoesCustos.I);

				getSigObjetoCustoVersoesDAO().atualizar(versaoAnterior);
			}
		}
	}

	private void validaDataInicioVersaoInativaEProgramada(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A) && objetoCustoVersao.getSigObjetoCustos() != null
				&& objetoCustoVersao.getSigObjetoCustos().getSeq() != null) {

			Calendar dtIniVig = Calendar.getInstance();
			dtIniVig.setTime(objetoCustoVersao.getDataInicio());
			dtIniVig.set(Calendar.DAY_OF_MONTH, 1);
			dtIniVig.set(Calendar.HOUR_OF_DAY, 0);
			dtIniVig.set(Calendar.MINUTE, 0);
			dtIniVig.set(Calendar.SECOND, 0);
			dtIniVig.set(Calendar.MILLISECOND, 0);

			// Versão INATIVADA
			List<SigObjetoCustoVersoes> listObjetoCustoVersoesInativadas = getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoes(
					objetoCustoVersao.getSigObjetoCustos(), DominioSituacaoVersoesCustos.I);

			boolean erro = false;

			for (SigObjetoCustoVersoes versaoInativa : listObjetoCustoVersoesInativadas) {
				if (versaoInativa.getDataFim() != null && !dtIniVig.getTime().equals(versaoInativa.getDataFim())
						&& !dtIniVig.getTime().after(versaoInativa.getDataFim())) {
					erro = true;
					break;
				}
			}

			if (erro) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_DATA_INICIO_OBJETO_CUSTO_INVALIDA);
			}

			// Versão PROGRAMADA
			List<SigObjetoCustoVersoes> listObjetoCustoVersoesProgramadas = getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoes(
					objetoCustoVersao.getSigObjetoCustos(), DominioSituacaoVersoesCustos.P);

			for (SigObjetoCustoVersoes versaoProgramada : listObjetoCustoVersoesProgramadas) {
				if (!dtIniVig.getTime().equals(versaoProgramada.getDataInicio()) && !dtIniVig.getTime().before(versaoProgramada.getDataInicio())) {
					erro = true;
					break;
				}
			}

			if (erro) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_DATA_INICIO_OBJETO_CUSTO_INVALIDA);
			}
		}
	}

	private void validaDataInicioAtivada(SigObjetoCustoVersoes objetoCustoVersao, DominioSituacaoVersoesCustos situacaoAnterior) throws ApplicationBusinessException {
		if (objetoCustoVersao.getSeq() != null) {
			if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)
					&& (situacaoAnterior == null || !situacaoAnterior.equals(DominioSituacaoVersoesCustos.A))) {

				List<SigObjetoCustoVersoes> listaVersoes = this.getSigObjetoCustoVersoesDAO().buscarVersoesObjetoCustoDataInicioConflito(objetoCustoVersao);

				if (listaVersoes != null && !listaVersoes.isEmpty()) {
					throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_DATA_INICIO_OBJETO_CUSTO_INVALIDA);
				}
			}
		}

	}

	private void validaDataInicioProgramada(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {

		if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.P)) {

			Calendar dtIniVig = Calendar.getInstance();
			dtIniVig.setTime(objetoCustoVersao.getDataInicio());
			dtIniVig.set(Calendar.DAY_OF_MONTH, 1);
			dtIniVig.set(Calendar.HOUR_OF_DAY, 0);
			dtIniVig.set(Calendar.MINUTE, 0);
			dtIniVig.set(Calendar.SECOND, 0);
			dtIniVig.set(Calendar.MILLISECOND, 0);

			Calendar dataAtual = Calendar.getInstance();
			dataAtual.setTime(new Date());
			dataAtual.set(Calendar.DAY_OF_MONTH, 1);
			dataAtual.set(Calendar.HOUR_OF_DAY, 0);
			dataAtual.set(Calendar.MINUTE, 0);
			dataAtual.set(Calendar.SECOND, 0);
			dataAtual.set(Calendar.MILLISECOND, 0);

			if (!dtIniVig.getTime().after(dataAtual.getTime())) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_DATA_INICIO_INVALIDA_PROGRAMADA);
			}
		}
	}

	public List<DominioSituacaoVersoesCustos> selecionaSituacaoPossivelDoObjetoCusto(DominioSituacaoVersoesCustos situacaoAnterior,
			SigObjetoCustoVersoes objetoCustoVersao) {

		List<DominioSituacaoVersoesCustos> listaSituacaoObjCusto = new ArrayList<DominioSituacaoVersoesCustos>();
		if (situacaoAnterior != null) {

			if (situacaoAnterior.equals(DominioSituacaoVersoesCustos.A)) {
				listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.A);
				listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.I);
			} else {
				if (situacaoAnterior.equals(DominioSituacaoVersoesCustos.I)) {
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.I);
					// setVisualizar(true);
				} else {
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.E);
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.P);
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.A);
				}
			}
		} else if (objetoCustoVersao != null && objetoCustoVersao.getIndSituacao() != null) {

			if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
				listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.A);
				listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.I);
			} else {
				if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.I)) {
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.I);
					// setVisualizar(true);
				} else {
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.E);
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.P);
					listaSituacaoObjCusto.add(DominioSituacaoVersoesCustos.A);
				}
			}
		}
		return listaSituacaoObjCusto;
	}
	
	public void persistPhi(SigObjetoCustoPhis sigObjetoCustoPhis) {
		if(sigObjetoCustoPhis.getSeq() == null){
			this.getSigObjetoCustoPhisDAO().persistir(sigObjetoCustoPhis);
		}
		else{
			this.getSigObjetoCustoPhisDAO().merge(sigObjetoCustoPhis);
		}
	}
	
	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoes(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao, String nome,
			DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao){
		List<SigObjetoCustoVersoes> lista =  getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoesPrincipal(firstResult, maxResult, orderProperty, asc, sigCentroProducao, fccCentroCustos,
				situacao, nome, tipoObjetoCusto, possuiComposicao);
		//Carrega os centro de custos ligados ao objeto de custo
		for(SigObjetoCustoVersoes item: lista){
			
			if(item.getListObjetoCustoCcts() != null){
				
				for(SigObjetoCustoCcts sigObjetoCustoCcts : item.getListObjetoCustoCcts()){
					Hibernate.initialize(sigObjetoCustoCcts.getFccCentroCustos());
					if(sigObjetoCustoCcts.getFccCentroCustos().getCentroProducao()!= null){
						Hibernate.initialize(sigObjetoCustoCcts.getFccCentroCustos().getCentroProducao());
					}
				}
			}
		}
		return lista;
	}

	// FACADES
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	// DAOs

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}
	
	protected SigAtividadesDAO getSigAtividadesDAO() {
		return sigAtividadesDAO;
	}
	
	protected SigObjetoCustoPhisDAO getSigObjetoCustoPhisDAO() {
		return sigObjetoCustoPhisDAO;
	}
}
