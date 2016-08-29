package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioInsumo;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ConsumoInsumoIndiretoVO;
import br.gov.mec.aghu.sig.custos.vo.InsumosAtividadeRateioPesoQuantidadeVO;
import br.gov.mec.aghu.sig.custos.vo.InsumosAtividadeRateioPesoVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoRateioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável pela rateio dos valores de insumos do mês por centro de custo ainda não divididos.
 * #21003
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalRateioDiretoInsumoRN.class)
public class ProcessamentoCustosMensalRateioDiretoInsumoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 4490173536662254274L;

	@Override
	public String getTitulo() {
		return "Processar rateio custos diretos - insumos.";
	}

	@Override
	public String getNome() {
		return "processamentoMensalRateioDiretoInsumoON - processamentoRateioDiretoCustoInsumos";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 20;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processarRateioDiretoCustoInsumos(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Fluxo principal do Rateio de insumos em objetos de custo.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void processarRateioDiretoCustoInsumos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_INICIO_PROCESSAMENTO_DO_CONSUMO_INSUMOS");

		//Passo 2
		this.executarEtapa1CalculoDistribuicaoInsumosConsumidosAssociadosAtividade(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		//Passo 2A
		this.executarEtapa3CalculoDistribuicaoInsumosConsumidosAssociadosAtividadeComQuantidade(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
				
		
		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_PROCESSAMENTO_INSUMO_ETAPA1");

		//Passo 4
		this.executarEtapa2CalculoDistribuicaoInsumosConsumidosNaoAssociadosAtividade(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		//Passo 5
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_PROCESSAMENTO_INSUMO_ETAPA2");

		//Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_FIM_RATEIO_INSUMOS");

	}

	/**
	 * Etapa 1 - Calculo de distribuição de insumos consumidos associados a atividade.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa1CalculoDistribuicaoInsumosConsumidosAssociadosAtividade(SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_INICIO_PROCESSAMENTO_CUSTOS_INSUMOS");

		//Passo 2
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO()
				.buscarInsumosAlocadosAtividadePesoPorRateio(sigProcessamentoCusto.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_BUSCA_CONSUMO_INSUMOS_ESPECIFICOS");

		BigDecimal acumuloPeso = BigDecimal.ZERO;
		Integer centroCustoCorrente = null;
		Integer codigoMaterialCorrente = null;
		List<SigCalculoAtividadeInsumo> atividadesInsumos = new ArrayList<SigCalculoAtividadeInsumo>();
		SigCalculoAtividadeInsumo sigCalculoAtividadeInsumo = null;

		if (retorno != null) {
			//Passo 11
			while (retorno.next()) {
				InsumosAtividadeRateioPesoVO insumoAtividadeVO = new InsumosAtividadeRateioPesoVO((Object[]) retorno.get());

				//Passo 6
				if (!insumoAtividadeVO.getSeqScoMaterial().equals(codigoMaterialCorrente)
						|| !insumoAtividadeVO.getSeqFccCentroCusto().equals(centroCustoCorrente)) {

					if (codigoMaterialCorrente != null && centroCustoCorrente != null) {
						this.executarRN01CalculoQtdeValorConsumoAtividade(sigProcessamentoCusto, centroCustoCorrente, codigoMaterialCorrente, acumuloPeso,
								rapServidores, sigProcessamentoPassos, atividadesInsumos);
					}
					acumuloPeso = BigDecimal.ZERO;
					codigoMaterialCorrente = insumoAtividadeVO.getSeqScoMaterial();
					centroCustoCorrente = insumoAtividadeVO.getSeqFccCentroCusto();
					atividadesInsumos = new ArrayList<SigCalculoAtividadeInsumo>();
				}

				//Passo 5				
				acumuloPeso = acumuloPeso.add(insumoAtividadeVO.getPeso());

				//Passo 4
				sigCalculoAtividadeInsumo  = this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().obterCalculoAtividadeInsumo(insumoAtividadeVO.getSeqSigCalculoComponente(), insumoAtividadeVO.getSeqSigAtividadeInsumo().longValue());
				if (sigCalculoAtividadeInsumo == null) {
					sigCalculoAtividadeInsumo = new SigCalculoAtividadeInsumo();
					sigCalculoAtividadeInsumo.setCriadoEm(new Date());
					sigCalculoAtividadeInsumo.setRapServidores(rapServidores);
					sigCalculoAtividadeInsumo.setSigCalculoComponentes(this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO()
							.obterPorChavePrimaria(insumoAtividadeVO.getSeqSigCalculoComponente()));
					sigCalculoAtividadeInsumo.setSigAtividades(this.getProcessamentoCustoUtils().getSigAtividadesDAO()
							.obterPorChavePrimaria(insumoAtividadeVO.getSeqSigAtividade()));
					sigCalculoAtividadeInsumo.setSigAtividadeInsumos(this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
							.obterPorChavePrimaria(insumoAtividadeVO.getSeqSigAtividadeInsumo().longValue()));

					if (insumoAtividadeVO.getSeqSigDirecionador() != null) {
						sigCalculoAtividadeInsumo.setSigDirecionadores(this.getProcessamentoCustoUtils().getSigDirecionadoresDAO()
								.obterPorChavePrimaria(insumoAtividadeVO.getSeqSigDirecionador()));
					}

					sigCalculoAtividadeInsumo.setScoMaterial(this.getProcessamentoCustoUtils().getComprasFacade()
							.obterScoMaterialPorChavePrimaria(insumoAtividadeVO.getSeqScoMaterial()));
					sigCalculoAtividadeInsumo.setQtdePrevista(0D);
					sigCalculoAtividadeInsumo.setQtdeRealizada(0D);
					sigCalculoAtividadeInsumo.setVlrInsumo(BigDecimal.ZERO);
					sigCalculoAtividadeInsumo.setPeso(insumoAtividadeVO.getPeso());
					
					this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().persistir(sigCalculoAtividadeInsumo);

					atividadesInsumos.add(sigCalculoAtividadeInsumo);
				} else {
					sigCalculoAtividadeInsumo.setPeso(insumoAtividadeVO.getPeso());
					this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().atualizar(sigCalculoAtividadeInsumo);
					atividadesInsumos.add(sigCalculoAtividadeInsumo);
				}
			}

			if (ProcessamentoCustoUtils.verificarListaNaoVazia(atividadesInsumos)) {
				this.executarRN01CalculoQtdeValorConsumoAtividade(sigProcessamentoCusto, centroCustoCorrente, codigoMaterialCorrente, acumuloPeso,
						rapServidores, sigProcessamentoPassos, atividadesInsumos);
			}

			retorno.close();
			this.commitProcessamentoCusto();
		}

		//Passo 12
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_RATEIO_INSUMOS_ESPECIFICOS");
	}

	/**
	 * Etapa 2 - Cálculo de distribuição de insumos consumidos não associados a atividades.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa2CalculoDistribuicaoInsumosConsumidosNaoAssociadosAtividade(SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_INICIO_PROCESSAMENTO_RATEIO_CUSTOS_INSUMOS");

		BigDecimal cctCentroCustoAplic = null;
		BigDecimal cctCentroCustoSolic = null;

		if (this.getProcessamentoCustoUtils().getParametroFacade().verificarExisteAghParametroValor(AghuParametrosEnum.CENTRO_CUSTO_APLIC)) {
			cctCentroCustoAplic = this.getProcessamentoCustoUtils().getParametroFacade()
					.obterValorNumericoAghParametros(AghuParametrosEnum.CENTRO_CUSTO_APLIC.toString());
		}

		if (this.getProcessamentoCustoUtils().getParametroFacade().verificarExisteAghParametroValor(AghuParametrosEnum.CENTRO_CUSTO_SOLIC)) {
			cctCentroCustoSolic = this.getProcessamentoCustoUtils().getParametroFacade()
					.obterValorNumericoAghParametros(AghuParametrosEnum.CENTRO_CUSTO_SOLIC.toString());
		}

		if (cctCentroCustoAplic == null || cctCentroCustoSolic == null) {
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_PARAMETROS);
		}

		//Passo 2
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarConsumoInsumoRateio(sigProcessamentoCusto.getSeq(), cctCentroCustoAplic.intValue(), cctCentroCustoSolic.intValue());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_BUSCA_INSUMOS_RATEIO_INDIRETO");

		Integer centroCusto = 0;
		boolean ocorreuExcecao = false;

		if (retorno != null) {
			//Passo 10
			while (retorno.next()) {

				ConsumoInsumoIndiretoVO consumoInsumoIndiretoVO = new ConsumoInsumoIndiretoVO((Object[]) retorno.get());
				if (!consumoInsumoIndiretoVO.getCctCodigo().equals(centroCusto)) {
					centroCusto = consumoInsumoIndiretoVO.getCctCodigo();
					ocorreuExcecao = false;
				}

				if (!ocorreuExcecao) {
					//Passo 4
					List<ObjetoCustoProducaoRateioVO> retornoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
							.buscarObjetosCustoComProducaoParaRateio(sigProcessamentoCusto.getSeq(), consumoInsumoIndiretoVO.getCctCodigo());

					if (ProcessamentoCustoUtils.verificarListaNaoVazia(retornoObjetoCusto )) {
						
						//Passo 7
						for(ObjetoCustoProducaoRateioVO objetoCustoProducaoRateioVO : retornoObjetoCusto ){

							SigCalculoObjetoCusto calculoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(objetoCustoProducaoRateioVO.getCbjSeq());

							//Passo 5
							SigCalculoRateioInsumo insumo = new SigCalculoRateioInsumo();
							insumo.setCriadoEm(new Date());
							insumo.setRapServidores(rapServidores);
							insumo.setSigCalculoObjetoCustos(calculoObjetoCusto);
							insumo.setGrupoMaterial(this.getProcessamentoCustoUtils().getComprasFacade()
									.obterGrupoMaterialPorId(consumoInsumoIndiretoVO.getGmtCodigo()));

							Double qtde = consumoInsumoIndiretoVO.getQtdeConsumoInsumo().doubleValue()
									/ objetoCustoProducaoRateioVO.getSomaPesos().doubleValue() * objetoCustoProducaoRateioVO.getPesoOc().doubleValue();
							Double valorInsumo = consumoInsumoIndiretoVO.getValorConsumoInsumo().doubleValue()
									/ objetoCustoProducaoRateioVO.getSomaPesos().doubleValue() * objetoCustoProducaoRateioVO.getPesoOc().doubleValue();

							insumo.setQtde(ProcessamentoCustoUtils.criarBigDecimal(qtde).doubleValue());
							insumo.setVlrInsumo(ProcessamentoCustoUtils.criarBigDecimal(valorInsumo));
							insumo.setPeso(objetoCustoProducaoRateioVO.getPesoOc());

							this.getProcessamentoCustoUtils().getSigCalculoRateioInsumoDAO().persistir(insumo);

							//Passo 6
							calculoObjetoCusto.setVlrRateioInsumos(calculoObjetoCusto.getVlrRateioInsumos().add(insumo.getVlrInsumo()));

							this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(calculoObjetoCusto);
						

							//Passo 9 e 10
							this.debitarValorRateadoDosObjetosCusto(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, consumoInsumoIndiretoVO);
						}
					} else {
						//[FE03]
						ocorreuExcecao = true;
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
								sigProcessamentoPassos, DominioEtapaProcessamento.I, "MENSAGEM_CC_NAO_POSSUI_OBJETO_CUSTO_OU_NAO_HOUVE_PRODUCAO_MES",
								consumoInsumoIndiretoVO.getCctCodigo().toString(), sigProcessamentoCusto.getCompetenciaMesAno());
					}
				}
			}
			retorno.close();
			this.commitProcessamentoCusto();
		}

		//Passo 11
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_CALCULO_RATEIO_CONSUMO_INSUMO_OC");
	}
	
	/**
	 * Etapa 3 - Cálculo de distribuição de insumos consumidos associados a atividades com informacao de quantidade consumida.
	 * 
	 * @author danilosantos
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa3CalculoDistribuicaoInsumosConsumidosAssociadosAtividadeComQuantidade(SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_INICIO_PROCESSAMENTO_RATEIO_CUSTOS_INSUMOS");

		//Passo 2
		List<InsumosAtividadeRateioPesoQuantidadeVO> lista = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarConsumoInsumoRateioComQuantidade(sigProcessamentoCusto.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_BUSCA_CONSUMO_INSUMOS_ESPECIFICOS_QUANTIDADE");

		BigDecimal acumuloPeso = BigDecimal.ZERO;
		Integer centroCustoCorrente = null;
		Integer codigoMaterialCorrente = null;
		List<SigCalculoAtividadeInsumo> atividadesInsumos = new ArrayList<SigCalculoAtividadeInsumo>();
	
		//Passo 4
		for(InsumosAtividadeRateioPesoQuantidadeVO insumosAtividadeRateioPesoQuantidadeVO: lista){
			//Passo 5				
			acumuloPeso = acumuloPeso.add(insumosAtividadeRateioPesoQuantidadeVO.getPeso());
		
			//Passo 6
			if (!insumosAtividadeRateioPesoQuantidadeVO.getMatCodigo().equals(codigoMaterialCorrente)
					|| !insumosAtividadeRateioPesoQuantidadeVO.getCctCodigo().equals(centroCustoCorrente)) {
				if (codigoMaterialCorrente != null && centroCustoCorrente != null) {
					this.executarCalculoRateioInsumosComQuantidade(sigProcessamentoCusto, centroCustoCorrente, codigoMaterialCorrente, acumuloPeso, insumosAtividadeRateioPesoQuantidadeVO.getQtdeRealizada(),
							rapServidores, sigProcessamentoPassos, atividadesInsumos);
				}
				acumuloPeso = BigDecimal.ZERO;
				codigoMaterialCorrente = insumosAtividadeRateioPesoQuantidadeVO.getMatCodigo();
				centroCustoCorrente = insumosAtividadeRateioPesoQuantidadeVO.getCctCodigo();
				atividadesInsumos = new ArrayList<SigCalculoAtividadeInsumo>();
			}
		}
		//Passo 13
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_RATEIO_INSUMOS_ESPECIFICOS");
		this.commitProcessamentoCusto();

	}

	/**
	 * Metodo que efetua os passos 08 e 09 da etapa 2
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto				Processamento Atual.
	 * @param rapServidores						Servidor Logado.
	 * @param sigProcessamentoPassos			Passo Atual.	
	 * @param consumoInsumoIndiretoVO			VO do resultado da consulta do Passo 2.
	 */
	private void debitarValorRateadoDosObjetosCusto(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, ConsumoInsumoIndiretoVO consumoInsumoIndiretoVO) {

		//Passo 8
		SigMvtoContaMensal contaMensal = new SigMvtoContaMensal();
		contaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
		contaMensal.setCriadoEm(new Date());
		contaMensal.setRapServidores(rapServidores);
		contaMensal.setTipoMvto(DominioTipoMovimentoConta.VRG);
		contaMensal.setTipoValor(DominioTipoValorConta.DI);
		contaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade()
				.obterCentroCustoPorChavePrimaria(consumoInsumoIndiretoVO.getCctCodigo()));
		contaMensal.setGrupoMaterial(this.getProcessamentoCustoUtils().getComprasFacade().obterGrupoMaterialPorId(consumoInsumoIndiretoVO.getGmtCodigo()));
		contaMensal.setQtde(consumoInsumoIndiretoVO.getQtdeConsumoInsumo());
		contaMensal.setValor(consumoInsumoIndiretoVO.getValorConsumoInsumo().multiply(new BigDecimal(-1)));

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaMensal);

		//Passo 9
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_SUCESSO_RATEIO_CONSUMO_INSUMO", contaMensal.getGrupoMaterial().getCodigo().toString(),
				contaMensal.getGrupoMaterial().getDescricao());
	}

	/**
	 * Cálculo quantidade e valor do consumo do insumo na atividade.
	 * 
	 * Para calcular o custo da atividade, é preciso buscar as informações do consumo mensal daquele insumo, 
	 * para depois distribuir esse valor proporcional ao peso do objeto de custo de cada atividade ao qual o insumo foi associado.
	 *  
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto						Processamento Atual.
	 * @param cctCodigo									Centro de custo correspondente.
	 * @param codigoMaterial							Material correspondente.
	 * @param acumuloPeso								Peso acumulado.
	 * @param rapServidores								Servidor logado.
	 * @param sigProcessamentoPassos					Passo Atual.
	 * @param atividadesInsumos							Lista dos calculos de atividades de insumos que foram acumulados.
	 */
	private void executarRN01CalculoQtdeValorConsumoAtividade(SigProcessamentoCusto sigProcessamentoCusto, Integer cctCodigo, Integer codigoMaterial,
			BigDecimal acumuloPeso, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			List<SigCalculoAtividadeInsumo> atividadesInsumos) {

		FccCentroCustos centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(cctCodigo);

		ScoMaterial material = this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(codigoMaterial);

		Object[] somatorios = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarConsumoInsumoAlocadoAtividades(sigProcessamentoCusto.getSeq(), centroCusto.getCodigo(), material.getCodigo());

		Long qtdConsumoInsumo = (Long) somatorios[0];
		BigDecimal vlrConsumoInsumo = (BigDecimal) somatorios[1];

		//[FE01]
		if (qtdConsumoInsumo == null || vlrConsumoInsumo == null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.I, "MENSAGEM_INSUMO_SEM_CONSUMO_MES", material.getCodigoENome(),
					material.getCodigoENome());
			return;
		}

		//[FE02]
		if (qtdConsumoInsumo <= 0 || vlrConsumoInsumo.intValue() <= 0) {
			return;
		}

		this.executarPassosSeteOitoNoveDez(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, atividadesInsumos, centroCusto, material,
				qtdConsumoInsumo, vlrConsumoInsumo, acumuloPeso);
	}
	
	
	/**
	 * Cálculo de distribuição de insumos com quantidade consumida
	 */
	private void executarCalculoRateioInsumosComQuantidade(SigProcessamentoCusto sigProcessamentoCusto, Integer cctCodigo, Integer codigoMaterial,
			BigDecimal acumuloPeso, Double qtdRealizadaAcumulada, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			List<SigCalculoAtividadeInsumo> atividadesInsumos) {

		FccCentroCustos centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(cctCodigo);

		ScoMaterial material = this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(codigoMaterial);

		Object[] somatorios = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarConsumoInsumoAlocadoAtividades(sigProcessamentoCusto.getSeq(), centroCusto.getCodigo(), material.getCodigo());

		Long qtdConsumoInsumo = (Long) somatorios[0];
		BigDecimal vlrConsumoInsumo = (BigDecimal) somatorios[1];
		Double vlrInsumo = 0.0;
		
		if(qtdRealizadaAcumulada<qtdConsumoInsumo){
			//Passo 7
			for (SigCalculoAtividadeInsumo atividadeInsumo : atividadesInsumos) {
				Double qtdRealizada = qtdConsumoInsumo.doubleValue() / acumuloPeso.doubleValue() * atividadeInsumo.getPeso().doubleValue();
				qtdRealizadaAcumulada = qtdRealizadaAcumulada + qtdRealizada;
				vlrInsumo = vlrInsumo + vlrConsumoInsumo.doubleValue() / acumuloPeso.doubleValue() * atividadeInsumo.getPeso().doubleValue();
				
				atividadeInsumo.setQtdeRealizada(ProcessamentoCustoUtils.criarBigDecimal(qtdRealizada).doubleValue());
				atividadeInsumo.setVlrInsumo(ProcessamentoCustoUtils.criarBigDecimal(vlrInsumo));

				this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().atualizar(atividadeInsumo);

				//Passo 08
				SigCalculoComponente componente = atividadeInsumo.getSigCalculoComponentes();
				componente.setVlrInsumos(componente.getVlrInsumos().add(atividadeInsumo.getVlrInsumo()));
				this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(componente);

				//Passo 09
				SigCalculoObjetoCusto objetoCusto = componente.getSigCalculoObjetoCustosByCbjSeq();
				objetoCusto.setVlrAtvInsumos(objetoCusto.getVlrAtvInsumos().add(atividadeInsumo.getVlrInsumo()));
				this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(objetoCusto);

			}
			
			//Passo 10
			Object[] custoMedioMaterialComUnidadeMedida = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().buscarCustoMedioMaterialComUnidadeMedida(sigProcessamentoCusto.getSeq(), centroCusto.getCodigo(), material.getCodigo());
			BigDecimal custoMedio = null;
			ScoUnidadeMedida unidadeMedida = null;
			if(custoMedioMaterialComUnidadeMedida != null){
				if(custoMedioMaterialComUnidadeMedida[0] != null){
					custoMedio = new BigDecimal(custoMedioMaterialComUnidadeMedida[0].toString());
				}
				
				if(custoMedioMaterialComUnidadeMedida[1] != null){
					unidadeMedida =  this.getProcessamentoCustoUtils().getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(custoMedioMaterialComUnidadeMedida[1].toString());
				}
			}
			
			SigMvtoContaMensal valoresDebitadosConsumoInsumo = new SigMvtoContaMensal();
			valoresDebitadosConsumoInsumo.setSigProcessamentoCustos(sigProcessamentoCusto);
			valoresDebitadosConsumoInsumo.setCriadoEm(new Date());
			valoresDebitadosConsumoInsumo.setRapServidores(rapServidores);
			valoresDebitadosConsumoInsumo.setTipoMvto(DominioTipoMovimentoConta.VAR);
			valoresDebitadosConsumoInsumo.setTipoValor(DominioTipoValorConta.DI);
			valoresDebitadosConsumoInsumo.setFccCentroCustos(centroCusto);
			valoresDebitadosConsumoInsumo.setScoMaterial(material);
			valoresDebitadosConsumoInsumo.setQtde(qtdConsumoInsumo);
			valoresDebitadosConsumoInsumo.setCustoMedio(custoMedio);
			valoresDebitadosConsumoInsumo.setScoUnidadeMedida(unidadeMedida);
			valoresDebitadosConsumoInsumo.setValor(vlrConsumoInsumo.negate());

			this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(valoresDebitadosConsumoInsumo);
			
			//Passo 11
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.I, "MENSAGEM_INSUMO_RATEADO_COM_SUCESSO", material.getCodigoENome());
		} 
	}

	/**
	 * Método responsavel em executas os passos 7, 8, 9 e 10 da etapa 1.
	 *   
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto				Processamento Atual.
	 * @param rapServidores						Servidor logado.
	 * @param sigProcessamentoPassos			Passo Atual.
	 * @param atividadesInsumos					Lista dos calculos de atividades de insumos que foram acumulados.
	 * @param centroCusto						Centro de custo correspondente.
	 * @param material							Material correspondente.
	 * @param qtdConsumoInsumo					Quantidade total de consumo.
	 * @param vlrConsumoInsumo					Valor total de consumo.
	 * @param acumuloPeso						Peso acumulado.
	 */
	private void executarPassosSeteOitoNoveDez(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, List<SigCalculoAtividadeInsumo> atividadesInsumos, FccCentroCustos centroCusto,
			ScoMaterial material, Long qtdConsumoInsumo, BigDecimal vlrConsumoInsumo, BigDecimal acumuloPeso) {

		for (SigCalculoAtividadeInsumo atividadeInsumo : atividadesInsumos) {
						
			//Ainda executando os calculos da RN01
			Double qtdRealizada = qtdConsumoInsumo.doubleValue() / acumuloPeso.doubleValue() * atividadeInsumo.getPeso().doubleValue();
			Double vlrInsumo = vlrConsumoInsumo.doubleValue() / acumuloPeso.doubleValue() * atividadeInsumo.getPeso().doubleValue();
			
			atividadeInsumo.setQtdeRealizada(ProcessamentoCustoUtils.criarBigDecimal(qtdRealizada).doubleValue());
			atividadeInsumo.setVlrInsumo(ProcessamentoCustoUtils.criarBigDecimal(vlrInsumo));

			this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().atualizar(atividadeInsumo);

			//Passo 07
			SigCalculoComponente componente = atividadeInsumo.getSigCalculoComponentes();
			componente.setVlrInsumos(componente.getVlrInsumos().add(atividadeInsumo.getVlrInsumo()));
			this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(componente);

			//Passo 08
			SigCalculoObjetoCusto objetoCusto = componente.getSigCalculoObjetoCustosByCbjSeq();
			objetoCusto.setVlrAtvInsumos(objetoCusto.getVlrAtvInsumos().add(atividadeInsumo.getVlrInsumo()));
			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(objetoCusto);

		}

		//Passo 09
		Object[] custoMedioMaterialComUnidadeMedida = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().buscarCustoMedioMaterialComUnidadeMedida(sigProcessamentoCusto.getSeq(), centroCusto.getCodigo(), material.getCodigo());
		BigDecimal custoMedio = null;
		ScoUnidadeMedida unidadeMedida = null;
		if(custoMedioMaterialComUnidadeMedida != null){
			if(custoMedioMaterialComUnidadeMedida[0] != null){
				custoMedio = new BigDecimal(custoMedioMaterialComUnidadeMedida[0].toString());
			}
			
			if(custoMedioMaterialComUnidadeMedida[1] != null){
				unidadeMedida =  this.getProcessamentoCustoUtils().getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(custoMedioMaterialComUnidadeMedida[1].toString());
			}
		}
		
		SigMvtoContaMensal valoresDebitadosConsumoInsumo = new SigMvtoContaMensal();
		valoresDebitadosConsumoInsumo.setSigProcessamentoCustos(sigProcessamentoCusto);
		valoresDebitadosConsumoInsumo.setCriadoEm(new Date());
		valoresDebitadosConsumoInsumo.setRapServidores(rapServidores);
		valoresDebitadosConsumoInsumo.setTipoMvto(DominioTipoMovimentoConta.VAR);
		valoresDebitadosConsumoInsumo.setTipoValor(DominioTipoValorConta.DI);
		valoresDebitadosConsumoInsumo.setFccCentroCustos(centroCusto);
		valoresDebitadosConsumoInsumo.setScoMaterial(material);
		valoresDebitadosConsumoInsumo.setQtde(qtdConsumoInsumo);
		valoresDebitadosConsumoInsumo.setCustoMedio(custoMedio);
		valoresDebitadosConsumoInsumo.setScoUnidadeMedida(unidadeMedida);
		valoresDebitadosConsumoInsumo.setValor(vlrConsumoInsumo.negate());

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(valoresDebitadosConsumoInsumo);

		//Passo 10
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.I, "MENSAGEM_INSUMO_RATEADO_COM_SUCESSO", material.getCodigoENome());

	}
}
