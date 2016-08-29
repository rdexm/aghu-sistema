package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndTempo;
import br.gov.mec.aghu.dominio.DominioOperacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoControleVidaUtil;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumosId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigControleVidaUtil;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.business.SigNoEnumOperationException;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.InsumoAtividadeEtapa3ObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.InsumoAtividadeExcedenteVO;
import br.gov.mec.aghu.sig.custos.vo.InsumoAtividadeMaterialExcedenteVO;
import br.gov.mec.aghu.sig.custos.vo.InsumoAtividadeObjetoCustoVO;

/**
 * Classe de procesamento do custo de insumos das atividades, tem por objetivo descrever a forma como os insumos requisitados 
 * e efetivados para os centros de custos tem seu custo alocado nas atividades e, por conseqüência, nos objetos de custo 
 * (produtos/serviços) da área.
 * 
 * #14918 e #24688
 * 
 * @author rhrosa
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCustoInsumoRN.class)
public class ProcessamentoCustosMensalCustoInsumoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719786L;
	private static final Integer FATOR_CONVERSAO_DIAS = 86400000;

	@Override
	public String getTitulo() {
		return "Processar o custo dos insumos das atividades.";
	}

	@Override
	public String getNome() {
		return "processamentoCustoInsumoAtividadeRN - processamentoCalculoCustoInsumosAtividadesQuantidadeEspecifica";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return ProcessamentoCustoUtils.definirPassoUtilizado(tipoObjetoCusto, 8, 13);
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processarCalculoCustoInsumosAtividadesQuantidadeEspecifica(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
	}

	/**
	 * Metodo principal, executa a chamada dos metodos responsáveis pela execucao das etapas da estoria
	 * 
	 * @param sigProcessamentoCusto
	 * @param rapServidores
	 * @param sigProcessamentoPassos
	 * @param tipoObjetoCusto
	 * @throws ApplicationBusinessException
	 * @author rhrosa
	 */
	private void processarCalculoCustoInsumosAtividadesQuantidadeEspecifica(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		if (this.executarEtapa1(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto)) {
			this.executarEtapa2(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
			this.executarEtapa3(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
		}
	}

	/**
	 * Calcula a quantidade prevista de consumo de insumo em atividades com quantidade específica
	 * 
	 * @param sigProcessamentoCusto                 Processamento atual
	 * @param rapServidores                         Servidor logado
	 * @param sigProcessamentoPassos                Passo atual
	 * @param tipoObjetoCusto                       Tipo do objeto de custo
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento
	 * @author rhrosa
	 */
	private boolean executarEtapa1(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 01
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_INICIO_PROCESSAMENTO_CUSTOS_INSUMOS");

		ScrollableResults retorno = this.executaConsultaBuscaInsumosAlocadosNaAtividade(tipoObjetoCusto, sigProcessamentoCusto);
		String mensagem = this.buscaMensagemStatus(tipoObjetoCusto);
		boolean passou = false;

		// Passo 04
		while (retorno.next()) {

			InsumoAtividadeObjetoCustoVO vo = new InsumoAtividadeObjetoCustoVO((Object[]) retorno.get(), tipoObjetoCusto);

			SigCalculoAtividadeInsumo entidade = new SigCalculoAtividadeInsumo();
			entidade.setRapServidores(rapServidores);
			entidade.setCriadoEm(new Date());
			entidade.setVlrInsumo(BigDecimal.ZERO);
			entidade.setSigCalculoComponentes(this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().obterPorChavePrimaria(vo.getCmtSeq()));
			entidade.setSigAtividades(this.getProcessamentoCustoUtils().getCustosSigFacade().obterAtividade(vo.getTvdSeq()));

			if (vo.getAisSeq() != null) {
				entidade.setSigAtividadeInsumos(this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
						.obterPorChavePrimaria(vo.getAisSeq().longValue()));
			}
			if (vo.getDirSeqTempo() != null) {
				entidade.setSigDirecionadores(this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(vo.getDirSeqTempo()));
			}

			entidade.setScoMaterial(this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo()));

			// opcao 1 - [FE03]
			if (vo.getQtdeUso() == null && vo.getVidaUtilTempo() == null && vo.getVidaUtilQtde() == null) {
				entidade.setQtdePrevista((double) 0);
				continue;
			}

			//opcao 2
			if (vo.getVidaUtilTempo() != null) {
				entidade.setQtdePrevista(new Double(0));
			}

			if (vo.getVidaUtilQtde() != null) {
				entidade.setQtdePrevista(vo.getSumDhpQtde().doubleValue());
			}

			// opcao 3 [RN01]
			this.executarRN01CalculoQtdePrevistaInsumoAtividade(vo, entidade, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);

			// RN's do documento de analise -> [RN03] Assistencial - [RN02] Apoio
			this.executarRN03CalculoValorInsumoAtividade(sigProcessamentoCusto, vo, entidade);

			this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().persistir(entidade);

			// Passo 05
			if (vo.getVidaUtilTempo() != null) {
				this.executarRN02AtualizarControleVidaUtil(sigProcessamentoCusto, vo, rapServidores);
			}

			passou = true;
		}

		retorno.close();

		this.commitProcessamentoCusto();

		// [FE01]
		if (!passou) {

			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_FLUXO_EXC1_CALCULO_INSUMOS");
			return false;

		} else {

			// passo 3
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.I, mensagem);

			// Passo 06
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_CALCULO_INSUMOS_CALCULO_PREVISTO");

			return true;
		}
	}

	private void executarRN01CalculoQtdePrevistaInsumoAtividade(InsumoAtividadeObjetoCustoVO vo, SigCalculoAtividadeInsumo entidade,
			SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		if (vo.getQtdeUso() != null) {

			// [RN04] [FE04]
			this.converterQtdePorUnidadeUso(vo, entidade, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

			switch (tipoObjetoCusto) {
			case AP:
				this.executarRN01CalculoQtdePrevistaInsumoAtividadeApoio(vo, entidade);
				break;
			case AS:
				this.executarRN01CalculoQtdePrevistaInsumoAtividadeAssistencial(vo, entidade);
				break;
			default:
				throw new SigNoEnumOperationException();
			}
		}
	}

	/**
	 * Verifica se as quantidades previstas calculadas de insumos, alocadas em atividades, excedem o requisitado no centro de custo
	 * 
	 * @param sigProcessamentoCusto              Processamento atual
	 * @param rapServidores                      Servidor logado
	 * @param sigProcessamentoPassos             Passo atual
	 * @param tipoObjetoCusto                    Tipo do objeto de custo
	 * @throws ApplicationBusinessException   Exceção lançada se alguma coisa aconteceu na hora do commit do processamento
	 * @author rhrosa
	 */
	private void executarEtapa2(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 01
		switch (tipoObjetoCusto) {
		case AP:
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_VERIFICA_VALORES_PREVISTOS_INSUMOS_APOIO");
			break;
		case AS:
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_VERIFICA_VALORES_PREVISTOS_INSUMOS_ASSISTENCIAL");
			break;
		default:
			throw new SigNoEnumOperationException();
		}

		// Passo 02
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO()
				.buscaMateriaisComConsumoExcedenteAssistencialApoio(sigProcessamentoCusto.getSeq(), tipoObjetoCusto);

		// Passo 03
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_ALOCADOS_PREVISTOS_INSUMOS");

		boolean passou = false;

		// Passo 04
		while (retorno.next()) {
			InsumoAtividadeMaterialExcedenteVO voMaterial = new InsumoAtividadeMaterialExcedenteVO((Object[]) retorno.get());

			ScrollableResults atividades = this
					.getProcessamentoCustoUtils()
					.getSigCalculoAtividadeInsumoDAO()
					.buscaAtividadesComConsumoExcedenteAssistencialApoio(voMaterial.getMatCodigo(), sigProcessamentoCusto.getSeq(), voMaterial.getCctCodigo(),
							tipoObjetoCusto);

			while (atividades.next()) {
				// Passo 05
				InsumoAtividadeExcedenteVO voAtividade = new InsumoAtividadeExcedenteVO((Object[]) atividades.get());
				SigCalculoAtividadeInsumo sigCalculoAtividadeInsumo = this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO()
						.obterPorChavePrimaria(voAtividade.getCnvCodigo());

				double novaQtRealizada = calcularNovaQtdeRealizada(voMaterial, voAtividade);
				double novoValorInsumo = calcularNovoValorInsumo(voAtividade, sigCalculoAtividadeInsumo, novaQtRealizada);

				sigCalculoAtividadeInsumo.setQtdeRealizada(ProcessamentoCustoUtils.criarBigDecimal(novaQtRealizada).doubleValue());
				sigCalculoAtividadeInsumo.setVlrInsumo(ProcessamentoCustoUtils.criarBigDecimal(novoValorInsumo));

				this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().atualizar(sigCalculoAtividadeInsumo);
			}
			atividades.close();

			// Passo 06
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.I, "MENSAGEM_MATERIAL_ALOCADOS_PREVISTOS_INSUMOS");

			passou = true;
		}
		retorno.close();
		this.commitProcessamentoCusto();

		// [FE02]
		if (!passou) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_NENHUM_MATERIAL_CALCULO_INSUMOS");
		}

		// Passo 08
		switch (tipoObjetoCusto) {
		case AP:
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_AJUSTE_VALORES_PREVISTOS_INSUMOS_APOIO");
			break;
		case AS:
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_AJUSTE_VALORES_PREVISTOS_INSUMOS_ASSISTENCIAL");
			break;
		default:
			throw new SigNoEnumOperationException();
		}
	}

	/**
	 * Realiza os débitos dos movimentos de insumo e atualiza os dados nas tabelas de cálculo dos componentes e de cálculo de objeto de custos
	 * 
	 * @param sigProcessamentoCusto             Processamento atual
	 * @param rapServidores                     Servidor logado
	 * @param sigProcessamentoPassos            Passo atual
	 * @param tipoObjetoCusto                   Tipo do objeto de custo
	 * @throws ApplicationBusinessException  Exceção lançada se alguma coisa aconteceu na hora do commit do processamento
	 * @author rhrosa
	 */
	private void executarEtapa3(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 01
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
				.buscaAtividadeValoresRealizadosCalculosAssistencialApoio(sigProcessamentoCusto.getSeq(), tipoObjetoCusto);

		while (retorno.next()) {
			
			InsumoAtividadeEtapa3ObjetoCustoVO vo = new InsumoAtividadeEtapa3ObjetoCustoVO((Object[]) retorno.get());

			// Passo 02
			this.debitarValorConsumidoPelaAtividadeDosInsumos(sigProcessamentoCusto, rapServidores, this.getProcessamentoCustoUtils().getComprasFacade()
					.obterScoMaterialPorChavePrimaria(vo.getMatCodigo()), this.getProcessamentoCustoUtils().getCentroCustoFacade()
					.obterCentroCustoPorChavePrimaria(vo.getCctCodigo()), vo.getQtdeRealizada(), vo.getVlrInsumo());

			// Passo 03
			SigCalculoComponente cmt = this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().obterPorChavePrimaria(vo.getCmtSeq());
			
			cmt.setVlrInsumos(cmt.getVlrInsumos().add(vo.getVlrInsumo())) ;
			this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(cmt);
			
			// Passo 04
			SigCalculoObjetoCusto cbj = cmt.getSigCalculoObjetoCustosByCbjSeq();
			cbj.setVlrAtvInsumos(cbj.getVlrAtvInsumos().add(vo.getVlrInsumo()));
			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(cbj);

			// Passo 05
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_CALCULO_INSUMOS_ATIVIDADE_ETAPA3");
		}
		retorno.close();

		this.commitProcessamentoCusto();

		// Passo 06
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_CALCULO_INSUMOS_ATIVIDADE_ETAPA3_FINAL");
	}

	private ScrollableResults executaConsultaBuscaInsumosAlocadosNaAtividade(DominioTipoObjetoCusto tipoObjetoCusto, SigProcessamentoCusto sigProcessamentoCusto) {
		// Passo 2
		ScrollableResults retorno = null;
		switch (tipoObjetoCusto) {
		case AP:
			retorno = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO().buscaInsumosAlocadosNaAtividadeApoio(sigProcessamentoCusto);
			break;
		case AS:
			retorno = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
					.buscarInsumosAlocadosNaAtividadeAssistencial(sigProcessamentoCusto);
			break;
		default:
			throw new SigNoEnumOperationException();
		}
		return retorno;
	}

	private String buscaMensagemStatus(DominioTipoObjetoCusto tipoObjetoCusto) {
		String mensagem = null;
		switch (tipoObjetoCusto) {
		case AP:
			mensagem = "MENSAGEM_BUSCA_SUCESSO_PROCESSAMENTO_CUSTOS_INSUMOS_APOIO";
			break;
		case AS:
			mensagem = "MENSAGEM_BUSCA_SUCESSO_PROCESSAMENTO_CUSTOS_INSUMOS_ASSISTENCIAL";
			break;
		default:
			throw new SigNoEnumOperationException();
		}
		return mensagem;
	}

	/**
	 * Calculo da quantidade prevista de insumo de uma atividade - Apoio - Estoria #24688
	 * 
	 * @param vo        VO com informacoes do insumo
	 * @param entidade  Atividade para a qual sera realizado o calculo de quantidade prevista
	 * @author rhrosa
	 */
	private void executarRN01CalculoQtdePrevistaInsumoAtividadeApoio(InsumoAtividadeObjetoCustoVO vo, SigCalculoAtividadeInsumo entidade) {
		if (vo.getQtdeUso() == null && vo.getVidaUtilTempo() == null && vo.getVidaUtilQtde() == null) {
			entidade.setQtdePrevista(new Double(0));
		}
		if (vo.getVidaUtilTempo() != null) {
			entidade.setQtdePrevista(new Double(0));
		} else if (vo.getVidaUtilQtde() != null) {
			entidade.setQtdePrevista(vo.getVidaUtilQtde().doubleValue());
		}
		if (vo.getQtdeUso() != null) {
			entidade.setQtdePrevista(vo.getQtdeUso().doubleValue());
		}
	}

	/**
	 * Cálculo da quantidade prevista de insumo de uma atividade - Assistencial - Estoria #14918
	 * 
	 * @param vo			VO com informacoes do insumo
	 * @param entidade      Atividade para a qual será realizado o calculo de quantidade prevista
	 * @author rhrosa
	 */
	private void executarRN01CalculoQtdePrevistaInsumoAtividadeAssistencial(InsumoAtividadeObjetoCustoVO vo, SigCalculoAtividadeInsumo entidade) {
		Boolean necessitaRegraNegocio = Boolean.TRUE;
		SigDirecionadores direcionador = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(vo.getDirSeqAtividade());
		this.executarRN01CalculoQtdePrevistaInsumoAtividadeAssistencialParte1(vo, entidade, direcionador, necessitaRegraNegocio);
		this.executarRN01CalculoQtdePrevistaInsumoAtividadeAssistencialParte2(vo, entidade, direcionador, necessitaRegraNegocio);
	}

	private void executarRN01CalculoQtdePrevistaInsumoAtividadeAssistencialParte1(InsumoAtividadeObjetoCustoVO vo, SigCalculoAtividadeInsumo entidade,
			SigDirecionadores direcionador, Boolean necessitaRegraNegocio) {
		
		if (vo.getQtdeUso() == null && vo.getVidaUtilTempo() == null && vo.getVidaUtilQtde() == null) {
			entidade.setQtdePrevista(new Double(0));
			necessitaRegraNegocio = false;
		}
		
		if (vo.getVidaUtilTempo() != null) {
			entidade.setQtdePrevista(new Double(0));
			necessitaRegraNegocio = false;
			
		} else 
		if (vo.getVidaUtilQtde() != null) {
			entidade.setQtdePrevista(vo.getSumDhpQtde().doubleValue());
			necessitaRegraNegocio = false;
		}
	}

	private void executarRN01CalculoQtdePrevistaInsumoAtividadeAssistencialParte2(InsumoAtividadeObjetoCustoVO vo, SigCalculoAtividadeInsumo entidade,
			SigDirecionadores direcionador, Boolean necessitaRegraNegocio) {

		if (necessitaRegraNegocio && direcionador != null && direcionador.getOperacao() != null && vo.getQtdeUso() != null){
			if(direcionador.getOperacao().equals(DominioOperacao.M) ) {
				boolean realizarCalculo = true;
	
				if (direcionador.getIndTipoCalculo() == null && vo.getNroExecucoes() != null) {
					entidade.setQtdePrevista(ProcessamentoCustoUtils.criarBigDecimal(vo.getQtdeUso().doubleValue() * vo.getNroExecucoes().doubleValue()).doubleValue());
					realizarCalculo = false;
				}
	
				if (direcionador.getIndTipoCalculo() != null) {
					switch (direcionador.getIndTipoCalculo()) {
					case PR:
						if(vo.getSumDhpQtde().equals(BigDecimal.ZERO)){
							entidade.setQtdePrevista((double)0);
						}
						else{
							entidade.setQtdePrevista(ProcessamentoCustoUtils.dividir(vo.getQtdeUso(), vo.getSumDhpQtde()).doubleValue());
						}
						break;
					case DP:
						entidade.setQtdePrevista(vo.getQtdeUso().doubleValue() * vo.getSumDhpNroDiasProducao().doubleValue());
						break;
					case PP:
						entidade.setQtdePrevista(vo.getQtdeUso().doubleValue() * (getDateDifference(vo.getProcDataFim(), vo.getProcDataInicio())) + 1);
						break;
					default:
						throw new SigNoEnumOperationException();
					}
				}
	
				if (realizarCalculo && vo.getNroExecucoes() != null) {
					entidade.setQtdePrevista(entidade.getQtdePrevista() * vo.getNroExecucoes());
				}
			}
		}
		else if (direcionador.getOperacao().equals(DominioOperacao.D) ){
			entidade.setQtdePrevista(vo.getQtdeUso().doubleValue());
		}
	}

	/**
	 * Atualizar controle da vida útil - RN 02
	 * 
	 * @param sigProcessamentoCusto               Processamento atual
	 * @param vo                                  Vo com informacoes do insumo
	 * @param rapServidores                       Servidor logado
	 * @throws ApplicationBusinessException
	 * @author rhrosa
	 */
	private void executarRN02AtualizarControleVidaUtil(SigProcessamentoCusto sigProcessamentoCusto, InsumoAtividadeObjetoCustoVO vo, RapServidores rapServidores)
			throws ApplicationBusinessException {

		SigControleVidaUtil controleVidaUtil = new SigControleVidaUtil();
		controleVidaUtil.setSigProcessamentoCustos(sigProcessamentoCusto);
		controleVidaUtil.setCriadoEm(new Date());
		controleVidaUtil.setRapServidores(rapServidores);
		controleVidaUtil.setTipo(DominioTipoControleVidaUtil.T);
		controleVidaUtil.setIndSituacao(DominioSituacao.A);

		FccCentroCustos centroCustos = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());
		controleVidaUtil.setFccCentroCustos(centroCustos);

		ScoMaterial material = this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo());
		controleVidaUtil.setScoMaterial(material);
		controleVidaUtil.setSigAtividadeInsumos(this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
				.obterPorChavePrimaria(vo.getAisSeq().longValue()));

		controleVidaUtil.setDataInicio(sigProcessamentoCusto.getDataInicio());
		controleVidaUtil.setDataFim(calcularDataFimVidaUtil(sigProcessamentoCusto.getDataInicio(), vo.getVidaUtilTempo(), vo.getDirSeqTempo()));

		BigDecimal vlrSaldo = buscarSaldoVidaUtil(material, centroCustos, sigProcessamentoCusto);
		controleVidaUtil.setVlrSaldo(vlrSaldo);
		controleVidaUtil.setValorMes(calcularValorMesVidaUtil(controleVidaUtil));
		controleVidaUtil.setScoUnidadeMedida(this.getProcessamentoCustoUtils().getComprasFacade()
				.obterScoUnidadeMedidaPorChavePrimaria(vo.getUnMaterial()));

		this.getProcessamentoCustoUtils().getSigControleVidaUtilDAO().persistir(controleVidaUtil);
	}

	/**
	 * Cálculo do valor do insumo da atividade - RN03
	 * 
	 * @param sigProcessamentoCusto     Processamento atual
	 * @param vo                        VO com as informacoes do insumo para realizar o calculo
	 * @param entidade					Atividade para qual sera calculado o valor o custo
	 * @author rhrosa
	 */
	private void executarRN03CalculoValorInsumoAtividade(SigProcessamentoCusto sigProcessamentoCusto, InsumoAtividadeObjetoCustoVO vo,
			SigCalculoAtividadeInsumo entidade) {
		BigDecimal custoMedio = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
				.buscaDetalheInsumosConsumidos(sigProcessamentoCusto.getSeq(), vo.getCctCodigo(), vo.getMatCodigo());

		if (custoMedio != null && custoMedio.doubleValue() != new Double(0).doubleValue()) {
			entidade.setQtdeRealizada(entidade.getQtdePrevista());
			BigDecimal vlrInsumo =  ProcessamentoCustoUtils.criarBigDecimal(entidade.getQtdePrevista() * custoMedio.doubleValue());
			entidade.setVlrInsumo(vlrInsumo);
		} else {
			entidade.setQtdeRealizada(new Double(0));
			entidade.setVlrInsumo(BigDecimal.ZERO);
		}
	}

	/**
	 * Utilizado para calcular a diferenca entre as datas de inicio e fim do processamento
	 * 
	 * @param dataFinal
	 * @param dataInicial
	 * @return diferenca entre as datas
	 * @author rhrosa
	 */
	private long getDateDifference(Date dataFinal, Date dataInicial) {
		return ((dataFinal.getTime() - dataInicial.getTime()) / FATOR_CONVERSAO_DIAS);
	}

	/**
	 * Calcula a data fim da vida util, utilizado quando a vida util for por tempo
	 * 
	 * @param dtInicioProcessamento
	 * @param vidaUtilTempo
	 * @param dirSeqTempo
	 * @return a data fim da vida util
	 * @author rhrosa
	 */
	private Date calcularDataFimVidaUtil(Date dtInicioProcessamento, Integer vidaUtilTempo, Integer dirSeqTempo) {

		SigDirecionadores direcionador = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(dirSeqTempo);

		Calendar dtFim = Calendar.getInstance();
		dtFim.setTime(dtInicioProcessamento);

		if (direcionador != null) {

			// Adiciona Mês(es)
			if (direcionador.getIndTempo().equals(DominioIndTempo.MS)) {
				dtFim.add(Calendar.MONTH, vidaUtilTempo);
				return dtFim.getTime();
			} else if (direcionador.getIndTempo().equals(DominioIndTempo.BI)) {
				dtFim.add(Calendar.MONTH, vidaUtilTempo * 2);
				return dtFim.getTime();
			} else if (direcionador.getIndTempo().equals(DominioIndTempo.TM)) {
				dtFim.add(Calendar.MONTH, vidaUtilTempo * 3);
				return dtFim.getTime();
			} else if (direcionador.getIndTempo().equals(DominioIndTempo.QM)) {
				dtFim.add(Calendar.MONTH, vidaUtilTempo * 4);
				return dtFim.getTime();
			} else if (direcionador.getIndTempo().equals(DominioIndTempo.SM)) {
				dtFim.add(Calendar.MONTH, vidaUtilTempo * 6);
				return dtFim.getTime();
			}

			// Adiciona ano(s)
			if (direcionador.getIndTempo().equals(DominioIndTempo.AN)) {
				dtFim.add(Calendar.YEAR, vidaUtilTempo);
				return dtFim.getTime();
			}

			// Adiciona dia(s)
			if (direcionador.getIndTempo().equals(DominioIndTempo.DI)) {
				dtFim.add(Calendar.DAY_OF_MONTH, vidaUtilTempo);
				return dtFim.getTime();
			} else if (direcionador.getIndTempo().equals(DominioIndTempo.SE)) {
				dtFim.add(Calendar.DAY_OF_MONTH, vidaUtilTempo * 7);
				return dtFim.getTime();
			} else if (direcionador.getIndTempo().equals(DominioIndTempo.QZ)) {
				dtFim.add(Calendar.DAY_OF_MONTH, vidaUtilTempo * 15);
				return dtFim.getTime();
			}

			// Adiciona hora(s)
			if (direcionador.getIndTempo().equals(DominioIndTempo.HR)) {
				dtFim.add(Calendar.HOUR_OF_DAY, vidaUtilTempo);
				return dtFim.getTime();
			}

			// Adiciona minuto(s)
			if (direcionador.getIndTempo().equals(DominioIndTempo.MI)) {
				dtFim.add(Calendar.MINUTE, vidaUtilTempo);
			}
		}

		return dtFim.getTime();
	}

	/**
	 * Buscar o valor do movimento de saldo inicial do material. Utilizado para setar o valor do saldo no objeto vidaUtil que sera persistido
	 * na RN 02
	 * 
	 * @param material
	 * @param centroCustos
	 * @param sigProcessamentoCusto
	 * @return saldo
	 * @author rhrosa
	 */
	private BigDecimal buscarSaldoVidaUtil(ScoMaterial material, FccCentroCustos centroCustos, SigProcessamentoCusto sigProcessamentoCusto) {
		return this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().buscarSaldoVidaUtil(material, centroCustos, sigProcessamentoCusto);
	}

	/**
	 * Utilizado para calcular e setar o valor mês do controle de vida util que será persistido quando a vida util for calculada por tempo
	 * 
	 * @param controleVidaUtil
	 * @return valorMes
	 * @author rhrosa
	 */
	private BigDecimal calcularValorMesVidaUtil(SigControleVidaUtil controleVidaUtil) {

		BigDecimal valorMes = BigDecimal.ZERO;

		if (controleVidaUtil.getVlrSaldo() != null && (controleVidaUtil.getVlrSaldo().compareTo(BigDecimal.ZERO) == 1)) {

			Integer difMeses = DateUtil.difMeses(controleVidaUtil.getDataInicio(), controleVidaUtil.getDataFim());

			if (difMeses > 1) {
				ProcessamentoCustoUtils.dividir(controleVidaUtil.getVlrSaldo(), new BigDecimal(difMeses));
			} else {
				valorMes = controleVidaUtil.getVlrSaldo();
			}
		}
		return valorMes;
	}

	/**
	 * Converte quantidade por unidade de uso.Quando a unidade de uso do material, especificada na atividade, 
	 * for diferente da unidade de uso do material dispensada, é preciso converter.
	 * @param vo
	 * @param entidade
	 * @param processamentoCusto
	 * @param rapServidores
	 * @param sigProcessamentoPassos
	 * @throws ApplicationBusinessException
	 * @author rhrosa
	 */
	private void converterQtdePorUnidadeUso(InsumoAtividadeObjetoCustoVO vo, SigCalculoAtividadeInsumo entidade, SigProcessamentoCusto processamentoCusto,
			RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		if (vo.getUnUso() != null && vo.getUnMaterial() != null && !vo.getUnUso().toString().equals(vo.getUnMaterial().toString()) && vo.getQtdeUso() != null) {

			SceConversaoUnidadeConsumosId conversaoUnidadeConsumosId = new SceConversaoUnidadeConsumosId(vo.getMatCodigo(), vo.getUnUso());
			SceConversaoUnidadeConsumos conversaoUnidadeConsumos = this.getProcessamentoCustoUtils().getEstoqueFacade()
					.obterConversaoUnidadeConsumos(conversaoUnidadeConsumosId);

			if (conversaoUnidadeConsumos != null) {
				entidade.setQtdeRealizada(ProcessamentoCustoUtils.dividir(vo.getQtdeUso(), conversaoUnidadeConsumos.getFatorConversao()).doubleValue());
			} else {
				// [FE04] Unidade de Uso sem Fator de Conversão
				ScoUnidadeMedida unidadeMedida = this.getProcessamentoCustoUtils().getComprasFacade()
						.obterScoUnidadeMedidaPorChavePrimaria(vo.getUnUso());

				this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoCusto, rapServidores,
						sigProcessamentoPassos, DominioEtapaProcessamento.I, "MENSAGEM_UNIDADE_USO_SEM_FATOR_CONVERSAO",
						entidade.getScoMaterial().getCodigoENome(), unidadeMedida.getDescricao(), entidade.getSigAtividades().getNome());

				throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_UNIDADE_USO_SEM_FATOR_CONVERSAO, entidade
						.getScoMaterial().getCodigoENome(), unidadeMedida.getDescricao(), entidade.getSigAtividades().getNome());
			}
		}
	}

	/**
	 * @param sigProcessamentoCustos
	 * @param rapServidores
	 * @param scoMaterial
	 * @param fccCentroCustos
	 *            (recebe o centro de custo dono do objeto de custo ao qual a atividade que gerou o débito do insumo, está associada.
	 * @param qtde
	 *            (recebe o valor de QTDE_REALIZADA calculada na RN03)
	 * @param custoMedio
	 *            (recebe o valor de CUSTO_MEDIO utilizado na RN03 para o cálculo do valor do insumo da atividade)
	 * @param valor
	 *            (recebe o valor de VLR_INSUMO, calculado na RN03)
	 * @throws ApplicationBusinessException
	 */
	private void debitarValorConsumidoPelaAtividadeDosInsumos(SigProcessamentoCusto sigProcessamentoCustos, RapServidores rapServidores,
			ScoMaterial scoMaterial, FccCentroCustos fccCentroCustos, Double qtde, BigDecimal valor) throws ApplicationBusinessException {

		SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();

		sigMvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCustos);
		sigMvtoContaMensal.setCriadoEm(new Date());
		sigMvtoContaMensal.setRapServidores(rapServidores);
		sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAA);
		sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DI);
		sigMvtoContaMensal.setFccCentroCustos(fccCentroCustos);
		sigMvtoContaMensal.setScoMaterial(scoMaterial);
		sigMvtoContaMensal.setScoUnidadeMedida(scoMaterial.getUnidadeMedida());
		sigMvtoContaMensal.setQtde(qtde.longValue());
		sigMvtoContaMensal.setCustoMedio(this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
				.buscaDetalheInsumosConsumidos(sigProcessamentoCustos.getSeq(), fccCentroCustos.getCodigo(), scoMaterial.getCodigo()));
		sigMvtoContaMensal.setValor(valor.multiply(new BigDecimal(-1)));

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);
	}

	/**
	 * @param voAtividade
	 * @param sigCalculoAtividadeInsumo
	 * @param novaQtRealizada
	 * @return novo valor calculado do insumo
	 * @author rhrosa
	 */
	private double calcularNovoValorInsumo(InsumoAtividadeExcedenteVO voAtividade, SigCalculoAtividadeInsumo sigCalculoAtividadeInsumo, double novaQtRealizada) {
		return (sigCalculoAtividadeInsumo.getVlrInsumo().doubleValue() / (voAtividade.getQtPrevista() + 0.0)) * novaQtRealizada;
	}

	/**
	 * @param voMaterial
	 * @param voAtividade
	 * @return nova quantidade realizada calculada
	 * @author rhrosa
	 */
	private int calcularNovaQtdeRealizada(InsumoAtividadeMaterialExcedenteVO voMaterial, InsumoAtividadeExcedenteVO voAtividade) {
		return (voAtividade.getQtPrevista() / voMaterial.getQtPrevista()) * voMaterial.getQtDisponivel();
	}
}
