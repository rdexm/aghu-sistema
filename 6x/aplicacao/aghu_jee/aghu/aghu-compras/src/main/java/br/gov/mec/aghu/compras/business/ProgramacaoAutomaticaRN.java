package br.gov.mec.aghu.compras.business;

import br.gov.mec.aghu.compras.dao.*;
import br.gov.mec.aghu.dominio.*;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.*;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import org.apache.commons.logging.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Regras de negócio de #5554 – Programar automático parcelas de entrega da autorização de fornecimento
 * 
 * @author luismoura
 * 
 */
@Stateless
public class ProgramacaoAutomaticaRN extends BaseBusiness {
	private static final long serialVersionUID = -4198256273208709126L;

	private enum ProgramacaoAutomaticaRNExceptionCode implements BusinessExceptionCode{
		PROGRAMACAO_REALIZADA_SUCESSO, 	ITEM_SEM_PONTO_PEDIDO,	ITEM_COM_ESTOQUE_MAXIMO_OU_POUCO_MOVIMENTADO,CONFIRMACAO_PROGRAMACAO_ITEM,	CONFIRMACAO_PROGRAMACAO_AF,	PROGRAMACAO_AUTOM_NAO_REALIZADA,
		;
	}
	
	private static final Log LOG = LogFactory.getLog(ProgramacaoAutomaticaRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;
	
	@Inject
	private ScoSolicitacaoDeCompraDAO scoSolicitacaoDeCompraDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;

	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@EJB
	private ProgramacaoAutomaticaON programacaoAutomaticaON;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO scoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	/**
	 * Caso a estória seja chamada de #24904 - Planejamento - Programação Entrega Itens AF - botão Programação Automática (Imagem 1) para cada AF
	 * selecionada buscar seus itens (ver C12) e gerar parcelas de entrega com o saldo ainda não assinado.
	 * 
	 * RN01 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param listNumeroAFs
	 * @throws ApplicationBusinessException
	 */
	public void gerarParcelas(List<Integer> listNumeroAFs) throws ApplicationBusinessException {
		List<ScoItemAutorizacaoFornId> itens = new ArrayList<ScoItemAutorizacaoFornId>();

		if (listNumeroAFs != null) {
			for (Integer afnNumero : listNumeroAFs) {
				List<ScoItemAutorizacaoFornId> itensAF = this.getScoItemAutorizacaoFornDAO().buscarItensAutorizacao(afnNumero);
				if (itensAF != null && !itensAF.isEmpty()) {
					itens.addAll(itensAF);
				} else {
					throw new ApplicationBusinessException(ProgramacaoAutomaticaRNExceptionCode.PROGRAMACAO_AUTOM_NAO_REALIZADA);
				}
			}

			for (ScoItemAutorizacaoFornId scoItemAutorizacaoFornId : itens) {
				this.gerarParcelas(scoItemAutorizacaoFornId.getAfnNumero(), scoItemAutorizacaoFornId.getNumero());
			}
		}

	}

	/**
	 * Caso a estória seja chamada de #5555 – Consultar Itens da Autorização de Fornecimento - Programação de Entrega, (Imagem 2), esta deve passar os
	 * campos sco_itens_autorizacao_forn.afn_numero, sco_itens_autorizacao_forn.numero para a execução das regras de negócio e consequente geração de
	 * parcelas.
	 * 
	 * Parcelas já criadas mas ainda não assinadas serão excluídas antes da geração de novas parcelas.
	 * 
	 * A quantidade de cada parcela será calculada de acordo com a previsão de consumo diário do material, o intervalo entre as parcelas, pelo seu
	 * espaço para armazenamento e sua classificação ABC. Quando o item possuir uma data de validade, esta será levada em consideração no cálculo da
	 * data de entrega e sua quantidade.
	 * 
	 * RN01 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param numero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected String gerarParcelas(Integer afnNumero, Integer numero) throws ApplicationBusinessException {

		ProgramacaoAutomaticaON programacaoAutomaticaON = this.getProgramacaoAutomaticaON();

		// Obtem dados para validação de item
		DadosItemAptoProgramacaoEntregaVO dadosItemApto = programacaoAutomaticaON.obterDadosItemApto(afnNumero, numero);

		// Para cada item que terá sua entrega programada, executar as regras de negócio: RN10 e RN11.

		// RN10
		boolean itemAptoProgramacaoEntrega = this.validarQuantidadePontoPedido(dadosItemApto.getQtdePontoPedido());
		if (!itemAptoProgramacaoEntrega) {
			throw new ApplicationBusinessException(ProgramacaoAutomaticaRNExceptionCode.ITEM_SEM_PONTO_PEDIDO);
		}

		// Saldo na instituição hoje, será calculado apenas de o item for válido
		Integer saldoInstituicaoHoje = this.obterSaldoInstituicao(dadosItemApto, new Date());

		// RN11
		if (dadosItemApto.getQtdeEstqMax() != null && dadosItemApto.getQtdeEstqMax() > 0) {
			itemAptoProgramacaoEntrega = this.validarSaldoInstituicao(dadosItemApto.getQtdeEstqMax(), saldoInstituicaoHoje);
			if (!itemAptoProgramacaoEntrega) {
				ScoMaterial material = this.scoMaterialDAO.obterPorChavePrimaria(dadosItemApto.getCodigoMaterial());
				throw new ApplicationBusinessException(ProgramacaoAutomaticaRNExceptionCode.ITEM_COM_ESTOQUE_MAXIMO_OU_POUCO_MOVIMENTADO, material.getNome());
			}
		}

		// Sendo o item apto a programação de entregas, temos:

		DominioClassifABC classificacaoABC = programacaoAutomaticaON.obterClassificacaoABC(dadosItemApto);

		SceAlmoxarifado sceAlmoxarifadoReferencia = programacaoAutomaticaON.obterAlmoxarifadoReferencia();

		// Quantidade Reposição = Ver RN02
		BigDecimal quantidadeReposicao = null;
		// Intervalo entre parcelas = ver RN02
		BigDecimal intervaloParcelas = null;

		{
			BigDecimal[] quantidadeReposicaoIntervaloParcelas = this.obterQuantidadeReposicaoIntervaloParcelas(dadosItemApto, classificacaoABC,
					sceAlmoxarifadoReferencia);

			quantidadeReposicao = quantidadeReposicaoIntervaloParcelas[0];
			intervaloParcelas = quantidadeReposicaoIntervaloParcelas[1];

		}

		// Data da Primeira Parcela: Ver RN03
		Date dataPrimeiraParcela = null;
		Boolean entregaImediata = null;
		{
			Object[] dataEntrImedPrimeiraParcela = this.obterDataPrimeiraParcela(dadosItemApto, saldoInstituicaoHoje, quantidadeReposicao);

			dataPrimeiraParcela = (Date) dataEntrImedPrimeiraParcela[0];
			entregaImediata = (Boolean) dataEntrImedPrimeiraParcela[1];
		}

		Integer lctNumero = null;
		Short nroComplemento = null;
		Double valorUnitario = null;
		{
			Object[] dadosAutorizacaoForn = programacaoAutomaticaON.obterDadosAutorizacaoFornecedor(dadosItemApto);

			lctNumero = (Integer) dadosAutorizacaoForn[0];
			nroComplemento = (Short) dadosAutorizacaoForn[1];
			valorUnitario = (Double) dadosAutorizacaoForn[2];
		}

		// Quantidade da Primeira Parcela: Ver RN06
		Integer quantidadePrimeiraParcela = this.obterQuantidadePrimeiraParcela(dadosItemApto, dataPrimeiraParcela, quantidadeReposicao, classificacaoABC,
				sceAlmoxarifadoReferencia, nroComplemento, lctNumero);

		// Quantidade da Primeira Parcela: Ver RN09
		Integer quantidadeCadaParcela = this.obterQuantidadeCadaParcela(dadosItemApto, intervaloParcelas, classificacaoABC, sceAlmoxarifadoReferencia);

		// Número de parcelas = ver RN07
		Long numeroParcelas = null;
		Long restoUltimaParcela = null;
		{
			QuantidadesVO parcelasResto = this.obterNumeroParcelasQuantidadeUltima(quantidadePrimeiraParcela, quantidadeCadaParcela, dadosItemApto.getNumero(),
					nroComplemento, lctNumero);
			numeroParcelas = parcelasResto.getQuantidade1();
			restoUltimaParcela = parcelasResto.getQuantidade2();
		}

		// Definidos os parâmetros para cada parcela, caso existam parcelas ainda não assinadas para o item deve-se excluir o vínculo da parcela
		// com a solicitação de compra <D1> e então excluir cada parcela ainda não assinada <D2>, RN13.
		this.excluirParcelasNaoAssinadas(dadosItemApto.getAfnNumero(), dadosItemApto.getNumero());

		ScoSolicitacaoDeCompra solicitacaoCompra = getScoSolicitacaoDeCompraDAO().obterPorChavePrimaria(dadosItemApto.getSlcNumero());

		// Se tem uma sobra para a ultima parcela, insere depois
		int parcelaComSobra = 0;
		if (restoUltimaParcela != null && restoUltimaParcela.intValue() > 0) {
			parcelaComSobra = 1;
		}

		if (numeroParcelas - parcelaComSobra > 0) {
			// Grava a primeira parcela
			this.gravarParcela(dadosItemApto.getAfnNumero(), dadosItemApto.getNumero(), valorUnitario, solicitacaoCompra, dataPrimeiraParcela,
					quantidadePrimeiraParcela, entregaImediata);

			// se gravou a primeira parcela, muda o entrega imediata pra false;
			entregaImediata = false;

			for (int i = 1; i < (numeroParcelas - parcelaComSobra); i++) {

				// Data da 'enésima' parcela = Data da primeira Parcela + (Intervalo entre parcelas * (n - 1))
				Date dataEnezimaParcela = programacaoAutomaticaON.obterDataEnezimaParcela(dataPrimeiraParcela, intervaloParcelas, (i + 1));

				// Grava outras parcelas
				this.gravarParcela(dadosItemApto.getAfnNumero(), dadosItemApto.getNumero(), valorUnitario, solicitacaoCompra, dataEnezimaParcela,
						quantidadeCadaParcela, entregaImediata);
			}
		}

		// Se tem uma sobra para a ultima parcela, insere agora
		if (parcelaComSobra == 1) {

			// Data da 'enésima' parcela = Data da primeira Parcela + (Intervalo entre parcelas * (n - 1))
			Date dataUltimaParcela = programacaoAutomaticaON.obterDataEnezimaParcela(dataPrimeiraParcela, intervaloParcelas, numeroParcelas.intValue());

			// Grava outras parcelas
			this.gravarParcela(dadosItemApto.getAfnNumero(), dadosItemApto.getNumero(), valorUnitario, solicitacaoCompra, dataUltimaParcela,
					restoUltimaParcela.intValue(), entregaImediata);
		}

		return null;
	}

	/**
	 * Quantidade do Item já na instituição
	 * 
	 * RN08 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param dadosItemApto
	 * @param dataReferencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Integer obterSaldoInstituicao(DadosItemAptoProgramacaoEntregaVO dadosItemApto, Date dataReferencia) throws ApplicationBusinessException {

		// Recebimentos realizados mas ainda não confirmados do material <ver C5>
		// +
		// Quantidade em Estoque Diponível (sce_estq_almoxs.qtde_disponivel) <ver C10>
		// +
		// Quantidade em Estoque Bloqueada (sce_estq_almoxs.qtde_bloqueada) <ver C10>

		ProgramacaoAutomaticaON programacaoAutomaticaON = this.getProgramacaoAutomaticaON();

		Integer saldoAdiantamentoAF = programacaoAutomaticaON.obterSaldoAdiantamentoAF(dadosItemApto.getAfnNumero(), dadosItemApto.getNumero());

		Integer quantidadeRecParcelas = programacaoAutomaticaON.obterQuantidadeItemRecebProvisorio(dadosItemApto.getCodigoMaterial());

		Integer saldoTotal = programacaoAutomaticaON.obterSaldoTotal(dadosItemApto.getCodigoMaterial(), dadosItemApto.isIndControleValidade(), dataReferencia);

		Integer saldoMaterialForaEstoque = quantidadeRecParcelas + saldoAdiantamentoAF;

		return saldoMaterialForaEstoque + saldoTotal;
	}

	/**
	 * Quantidade de Reposição e Intervalo entre parcelas
	 * 
	 * RN02 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param dadosItemApto
	 * @param classificacaoABC
	 * @param sceAlmoxarifadoReferencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected BigDecimal[] obterQuantidadeReposicaoIntervaloParcelas(DadosItemAptoProgramacaoEntregaVO dadosItemApto, DominioClassifABC classificacaoABC,
			SceAlmoxarifado sceAlmoxarifadoReferencia) throws ApplicationBusinessException {

		BigDecimal quantidadeReposicao = null;
		BigDecimal intervaloParcelas = null;

		if (dadosItemApto.isIndUtilizaEspaco() && dadosItemApto.getQtdeEspacoArmazena() != null && dadosItemApto.getQtdeEspacoArmazena().intValue() > 0) {
			// Se item da AF possuir restrição de espaço físico (C1: sco_materiais.ind_espaco_armazena = ‘S’ E sce_estq_almoxs.qtd_espaco_armazena >
			// 0), temos:
			// Quantidade de Reposição: (sce_estq_almoxs.qtd_espaco_armazena * P_PERCENTUAL_REPOSICAO_ITENS_ESPACO_FISICO)/100

			ProgramacaoAutomaticaON programacaoAutomaticaON = this.getProgramacaoAutomaticaON();

			BigDecimal percRepoItensEspFisico = programacaoAutomaticaON.obterPercentualReposicaoItensEspacoFisico();

			quantidadeReposicao = new BigDecimal(dadosItemApto.getQtdeEspacoArmazena()).multiply(percRepoItensEspFisico).divide(new BigDecimal(100), 2,
					RoundingMode.HALF_EVEN);
			// Intervalo entre parcelas: (sce_estq_almoxs.qtd_espaco_armazena - Quantidade de Reposição) / Consumo Diário
			intervaloParcelas = new BigDecimal(dadosItemApto.getQtdeEspacoArmazena()).subtract(quantidadeReposicao).divide(dadosItemApto.getConsumoDiario(), 2,
					RoundingMode.HALF_EVEN);

			return new BigDecimal[] { quantidadeReposicao, intervaloParcelas };

		} else {
			// Caso o item não tenha restrições quanto ao espaço de armazenamento, teremos:
			return this.obterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(dadosItemApto.getConsumoDiario(), classificacaoABC, sceAlmoxarifadoReferencia);
		}
	}

	/**
	 * Quantidade de Reposição e Intervalo entre parcelas (DA CLASSIF ABC)
	 * 
	 * RN02 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param consumoDiario
	 * @param classificacaoABC
	 * @param sceAlmoxarifadoReferencia
	 * @return
	 */
	protected BigDecimal[] obterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(BigDecimal consumoDiario, DominioClassifABC classificacaoABC,
			SceAlmoxarifado sceAlmoxarifadoReferencia) {
		BigDecimal quantidadeReposicao = null;
		BigDecimal intervaloParcelas = null;

		// Número de dias Classif. ABC = C2 encontra a classificação ABC do material, relacionar com C3 para:
		if (DominioClassifABC.A.equals(classificacaoABC)) {
			// Quantidade de Reposição = (C3) sce_almoxarifados.dias_saldo_class_A * Consumo Diário Previsto
			quantidadeReposicao = new BigDecimal(sceAlmoxarifadoReferencia.getDiasSaldoClassA()).multiply(consumoDiario);
			// Intervalo entre parcelas = (C3) sce_almoxarifados.dias_parcela_class_A
			intervaloParcelas = new BigDecimal(sceAlmoxarifadoReferencia.getDiasParcelaClassA());
		} else if (DominioClassifABC.B.equals(classificacaoABC)) {
			// Quantidade de Reposição = (C3) sce_almoxarifados.dias_saldo_class_B * Consumo Diário Previsto
			quantidadeReposicao = new BigDecimal(sceAlmoxarifadoReferencia.getDiasSaldoClassB()).multiply(consumoDiario);
			// Intervalo entre parcelas = (C3) sce_almoxarifados.dias_parcela_class_B
			intervaloParcelas = new BigDecimal(sceAlmoxarifadoReferencia.getDiasParcelaClassB());
		} else if (DominioClassifABC.C.equals(classificacaoABC)) {
			// Quantidade de Reposição = (C3) sce_almoxarifados.dias_saldo_class_C * Consumo Diário Previsto
			quantidadeReposicao = new BigDecimal(sceAlmoxarifadoReferencia.getDiasSaldoClassC()).multiply(consumoDiario);
			// Intervalo entre parcelas = (C3) sce_almoxarifados.dias_parcela_class_C
			intervaloParcelas = new BigDecimal(sceAlmoxarifadoReferencia.getDiasParcelaClassC());
		}
		return new BigDecimal[] { quantidadeReposicao, intervaloParcelas };
	}

	/**
	 * A data da primeira: A data da primeira parcela será a data atual + prazo de entrega ou a data para reposição provavelmente seja atingida.
	 * Quando possível, será menor ou igual à data de validade e deverá respeitar o dia favorável para entrega do item.
	 * 
	 * RN03 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param dadosItemApto
	 * @param saldoInstituicao
	 * @param quantidadeReposicao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Object[] obterDataPrimeiraParcela(DadosItemAptoProgramacaoEntregaVO dadosItemApto, Integer saldoInstituicao, BigDecimal quantidadeReposicao)
			throws ApplicationBusinessException {
		// - Prazo de Entrega, ver C8
		Short prazoEntrega = this.getScoAutorizacaoFornDAO().obterPrazoEntrega(dadosItemApto.getAfnNumero());

		// - Data prevista em que o ponto de Reposição do será atingido, ver RN04 (Data de Reposição)
		Date dataReposicao = this.obterDataPrevisaoPontoReposicao(dadosItemApto, saldoInstituicao, quantidadeReposicao);

		// Data atual + Prazo de Entrega
		Date dataAtualMaisPrazoEntrega = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), (prazoEntrega != null ? prazoEntrega.intValue() : 0));

		Date dataPrimeiraParcela = null;
		Boolean entregaImediata = false;

		if (dataReposicao.getTime() < dataAtualMaisPrazoEntrega.getTime()) {
			// Se a Data de Reposição for menor que a data atual + Prazo de Entrega, a data da primeira parcela será:
			dataPrimeiraParcela = dataAtualMaisPrazoEntrega;
			// gerar parcela com indicador IND_ENTREGA_IMEDIATA = ‘S’
			entregaImediata = true;
		} else {
			// Senão (Data de Reposição seja maior que a data atual + Prazo de Entrega)
			// Verificar se item possui restrições quanto ao espaço de armazenamento (C1: mat.ind_espaco_armazena)

			ProgramacaoAutomaticaON programacaoAutomaticaON = this.getProgramacaoAutomaticaON();

			if (dadosItemApto.isIndUtilizaEspaco()) {
				// Data Primeira Parcela = Data de Reposição
				dataPrimeiraParcela = dataReposicao;
			} else {
				// Verificar se o item tem restrições quanto a Data de Validade (ver RN05)
				Date dataValidade = this.obterValidade(dadosItemApto.isIndControleValidade(), dadosItemApto.getEstoqueAlmoxSeq());

				if (dataValidade == null || dataValidade.getTime() > dataReposicao.getTime()) {
					// Se (item não possuir prazo de validade ou Data de Validade do item for maior que a Data de Reposição)
					// Data Primeira Parcela = Data de Reposição
					dataPrimeiraParcela = dataReposicao;
				} else {
					// Senão (Item possui Data de Validade e esta for menor que a Data de Reposição)
					Integer qtDias = programacaoAutomaticaON.obterNumeroDiasEntregueAntesVencimento();
					// Data Primeira Parcela = Data de Validade do item – P_NDIAS_ENTREGA_ANTES_VENCIMENTO
					// desde que esta data seja superior ao dia atual + prazo de entrega
					dataPrimeiraParcela = DateUtil.adicionaDias(dataValidade, -qtDias);
				}
			}

			// Sendo a Data Primeira Parcela maior que a data atual + Prazo de Entrega
			if (dataPrimeiraParcela.getTime() > dataAtualMaisPrazoEntrega.getTime()) {

				// busca-se o dia favorável de entrega do fornecedor e para o grupo do material pelas consultas descritas em C7.
				Integer diaFavoravel = programacaoAutomaticaON.obterDiaFavoravel(dadosItemApto.getNumeroFornecedor(), dadosItemApto.getCodigoMaterial());
				if (diaFavoravel != null) {
					// Se diaFavoravelEntrega for diferente de nulo,

					// Dia da semana da data da primeira parcela
					Calendar calendarPrimeiraParcela = Calendar.getInstance();
					calendarPrimeiraParcela.setTime(dataPrimeiraParcela);
					int diaSemanaEntrega = calendarPrimeiraParcela.get(Calendar.DAY_OF_WEEK);

					if (diaSemanaEntrega != diaFavoravel.intValue()) {

						Date novaDataPrimeiraParcela = programacaoAutomaticaON.obterDataDiaFavoravel(dataPrimeiraParcela, diaFavoravel, diaSemanaEntrega);

						// Caso esta data seja inferior ao dia atual + Prazo de Entrega, não se deve levar em consideração o dia favorável para
						// entrega, mantendo assim o data já calculada.
						if (novaDataPrimeiraParcela.getTime() > dataAtualMaisPrazoEntrega.getTime()) {
							dataPrimeiraParcela = novaDataPrimeiraParcela;
						}
					}
				}

			}

		}

		return new Object[] { dataPrimeiraParcela, entregaImediata };
	}

	/**
	 * Caso o Item tenha indicador de validade (C1: sce_estq_almoxs.ind_controle_validade == ‘S’), buscar a data de validade do item (C9)
	 * 
	 * RN05 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param indControleValidade
	 * @param estoqueAlmoxSeq
	 * @return
	 */
	protected Date obterValidade(boolean indControleValidade, Integer estoqueAlmoxSeq) {
		if (indControleValidade) {
			return this.getEstoqueFacade().obterDataValidadeSceValidade(estoqueAlmoxSeq);
		}
		return null;
	}

	/**
	 * Caso um item da Autorização de Fornecimento não possua quantidade de Ponto Pedido (C1), não devem ser geradas ou excluídas parcelas para o
	 * item. Se chamada da tela #5555 – Itens de AF exibir mensagem ITEM_SEM_PONTO_PEDIDO
	 * 
	 * RN10 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param qtdePontoPedido
	 * @return
	 */
	protected boolean validarQuantidadePontoPedido(Integer qtdePontoPedido) {
		return qtdePontoPedido != null && qtdePontoPedido.intValue() > 0;
	}

	/**
	 * Caso o saldo na instituição seja maior ou igual sce_estq_almoxs.qtde_estoq_max, não devem ser geradas ou excluídas parcelas para itens desta
	 * AF. Se chamada da tela #5555 – Itens de AF exibir mensagem ITEM_COM_ESTOQUE_MAXIMO_OU_POUCO_MOVIMENTADO
	 * 
	 * RN11 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param qtdeEstqMax
	 * @param saldoInstituicao
	 * @return
	 */
	protected boolean validarSaldoInstituicao(Integer qtdeEstqMax, Integer saldoInstituicao) {
		return qtdeEstqMax != null && qtdeEstqMax.intValue() > saldoInstituicao;
	}

	/**
	 * Para cada parcela gerada, I1, deve-se gerar o vínculo do item da AF com sua Solicitação de Compra, I2.
	 * 
	 * RN12 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param numero
	 * @param valorUnitario
	 * @param solicitacaoCompra
	 * @param dataPrevisaoEntrega
	 * @param quantidade
	 * @param indEntregaImediata
	 */
	protected void gravarParcela(Integer afnNumero, Integer numero, Double valorUnitario, ScoSolicitacaoDeCompra solicitacaoCompra,
			Date dataPrevisaoEntrega, Integer quantidade, Boolean indEntregaImediata) {

		// gravar parcela, I1
		ScoProgEntregaItemAutorizacaoFornecimento item = this.gravarScoProgEntregaItemAutorizacaoFornecimento(afnNumero, numero, valorUnitario,
				dataPrevisaoEntrega, quantidade, indEntregaImediata);

		// deve-se gerar o vínculo do item da AF com sua Solicitação de Compra, I2
		this.gravarScoSolicitacaoProgramacaoEntrega(item, solicitacaoCompra);
	}

	/**
	 * Inclusão da parcela: tabela sco_progr_entrega_itens_af
	 * 
	 * I1 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param numero
	 * @param valorUnitario
	 * @param dataPrevisaoEntrega
	 * @param qtde
	 * @param indEntregaImediata
	 * @return
	 */
	private ScoProgEntregaItemAutorizacaoFornecimento gravarScoProgEntregaItemAutorizacaoFornecimento(Integer afnNumero, Integer numero, Double valorUnitario,
			Date dataPrevisaoEntrega, Integer qtde, Boolean indEntregaImediata) {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		ScoProgEntregaItemAutorizacaoFornecimento item = new ScoProgEntregaItemAutorizacaoFornecimento();

		ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();

		// IAF_AFN_NUMERO - pk (sco_itens_autorizacao_forn.afn_numero)
		id.setIafAfnNumero(afnNumero);
		// IAF_NUMERO - pk (sco_itens_autorizacao_forn.numero)
		id.setIafNumero(numero);
		// SEQ - 1
		id.setSeq(1);
		// PARCELA - max(parcela + 1), considerando demais campos da pk ( ==== FEITO NO obterValorSequencialId do DAO ==== )
		id.setParcela(null);

		item.setId(id);

		// AFE_AFN_NUMERO - null
		// AFE_NUMERO - null
		item.setScoAfPedido(null);

		// DT_GERACAO - data atual
		item.setDtGeracao(new Date());

		// DT_PREV_ENTREGA - RN03 e RN01
		item.setDtPrevEntrega(dataPrevisaoEntrega);

		// DT_ENTREGA - null
		item.setDtEntrega(null);

		// QTDE - RN06 e RN09
		item.setQtde(qtde);

		// QTDE_ENTREGUE - 0
		item.setQtdeEntregue(0);

		// SER_MATRICULA - matricula do usuário de conexão
		// SER_VIN_CODIGO - vínculo do usuário de conexão
		item.setRapServidor(servidorLogado);

		// SER_MATRICULA_ALTERACAO - null
		// SER_VIN_CODIGO_ALTERACAO - null
		item.setRapServidorAlteracao(null);

		// IND_PLANEJAMENTO - N
		item.setIndPlanejamento(Boolean.FALSE);

		// IND_ASSINATURA - N
		item.setIndAssinatura(Boolean.FALSE);

		// IND_EMPENHADA - N
		item.setIndEmpenhada(DominioAfEmpenhada.N);

		// IND_ENVIO_FORNECEDOR - N
		item.setIndEnvioFornecedor(Boolean.FALSE);

		// IND_RECALCULO_AUTOMATICO - N
		item.setIndRecalculoAutomatico(Boolean.FALSE);

		// IND_RECALCULO_MANUAL - N
		item.setIndRecalculoManual(Boolean.FALSE);

		// VALOR_TOTAL - sco_itens_autorizacao_forn.VALOR_UNITARIO * QTDE
		item.setValorTotal(valorUnitario * qtde);

		// IND_IMPRESSA - N
		item.setIndImpressa(Boolean.FALSE);

		// DT_ATUALIZACAO - null
		item.setDtAtualizacao(null);

		// DT_ASSINATURA - Null
		item.setDtAssinatura(null);

		// SER_MATRICULA_ASSINATURA - null
		// SER_VIN_CODIGO_ASSINATURA - null
		item.setRapServidorAssinatura(null);

		// DT_ALTERACAO - null
		item.setDtAlteracao(null);

		// DT_LIB_PLANEJAMENTO - null
		item.setDtLibPlanejamento(null);

		// SER_MATRICULA_LIB_PLANEJ - null
		// SER_VIN_CODIGO_LIB_PLANEJ - null
		item.setRapServidorLibPlanej(null);

		// IND_CANCELADA - N
		item.setIndCancelada(Boolean.FALSE);

		// DT_CANCELAMENTO - null
		item.setDtCancelamento(null);

		// SER_MATRICULA_CANCELAMENTO - null
		// SER_VIN_CODIGO_CANCELAMENTO - null
		item.setRapServidorCancelamento(null);

		// JST_CODIGO - null
		item.setScoJustificativa(null);

		// DT_NECESSIDADE_HCPA - null
		item.setDtNecessidadeHcpa(null);

		// VALOR_EFETIVADO - 0
		item.setValorEfetivado(0d);

		// IND_EFETIVADA - N
		item.setIndEfetivada(Boolean.FALSE);

		// IND_ENTREGA_IMEDIATA - N, -- ‘N’ por default. Ver RN03 para situações com ‘S’
		item.setIndEntregaImediata(indEntregaImediata);

		// OBSERVACAO - null
		item.setObservacao(null);

		// QTDE_ENTREGUE_PROV - 0
		item.setQtdeEntregueProv(0);

		// QTDE_ENTREGUE_A_MAIS - 0
		item.setQtdeEntregueAMais(0);

		// IND_TRAMITE_INTERNO - N
		item.setIndTramiteInterno(Boolean.FALSE);

		// IND_CONVERSAO_UNIDADE - N
		item.setIndConversaoUnidade(Boolean.FALSE);

		// IND_protectedADO - N
		item.setIndPublicado(Boolean.FALSE);

		// SLC_NUMERO - null
		item.setSlcNumero(null);

		// ESL_SEQ_FATURA - null
		item.setEslSeqFatura(null);

		// DT_PREV_ENTREGA_APOS_ATRASO - null
		item.setDtPrevEntregaAposAtraso(null);

		// IND_ENTREGA_URGENTE - null
		item.setIndEntregaUrgente(null);

		this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().persistir(item);

		// Necessário fazer o flush para buscar o número correto da parcela na inserção da próxima parcela
		this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().flush();

		this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().desatachar(item);

		return item;
	}

	/**
	 * Vínculo da parcela com a solicitação de compra. Tabela: sco_solic_progr_entrega
	 * 
	 * I2 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param entregaItem
	 * @param solicitacaoCompra
	 */
	private void gravarScoSolicitacaoProgramacaoEntrega(ScoProgEntregaItemAutorizacaoFornecimento entregaItem, ScoSolicitacaoDeCompra solicitacaoCompra) {
		ScoSolicitacaoProgramacaoEntrega solicitacao = new ScoSolicitacaoProgramacaoEntrega();

		// SEQ - sco_progr_entrega_itens_af.IAF_AFN_NUMERO
		// PEA_IAF_AFN_NUMERO - sco_progr_entrega_itens_af.IAF_NUMERO
		// PEA_IAF_NUMERO - sco_progr_entrega_itens_af.SEQ
		// PEA_SEQ - sco_progr_entrega_itens_af.PARCELA
		solicitacao.setProgEntregaItemAf(entregaItem);

		// PEA_PARCELA - sco_solicitacoes_de_compras.NUMERO
		solicitacao.setSolicitacaoCompra(solicitacaoCompra);

		// SLC_NUMERO - null
		solicitacao.setSolicitacaoServico(null);

		// QTDE - sco_progr_entrega_itens_af.qtde
		solicitacao.setQtde(entregaItem.getQtde());

		// VALOR - null
		solicitacao.setValor(null);

		// IND_PRIORIDADE - 1
		solicitacao.setIndPrioridade((short) 1);

		// QTDE_ENTREGUE - Null
		solicitacao.setQtdeEntregue(null);

		// VALOR_EFETIVADO - Null
		solicitacao.setValorEfetivado(null);

		// IAF_AFN_NUMERO_ORIG - null
		// IAF_NUMERO_ORIG - null
		solicitacao.setItemAfOrigem(null);

		this.getScoSolicitacaoProgramacaoEntregaDAO().persistir(solicitacao);

		// Necessário fazer o flush para buscar o número correto da parcela na inserção da próxima parcela
		this.getScoSolicitacaoProgramacaoEntregaDAO().flush();

		this.getScoSolicitacaoProgramacaoEntregaDAO().desatachar(solicitacao);
	}

	/**
	 * Data prevista para reposição será atingida:
	 * 
	 * RN04 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param dadosItemApto
	 * @param saldoInstituicao
	 * @param quantidadeReposicao
	 * @return
	 */
	protected Date obterDataPrevisaoPontoReposicao(DadosItemAptoProgramacaoEntregaVO dadosItemApto, Integer saldoInstituicao, BigDecimal quantidadeReposicao) {
		Integer diasReposicao = null;
		if (saldoInstituicao.intValue() < quantidadeReposicao.intValue()) {
			// Se Saldo na Instituição (RN08, com data de referencia da C10 sendo dia atual) for menor que Quantidade de Reposição <RN02> o Número de
			// Dias para Reposição = 1
			diasReposicao = 1;
		} else {
			// Senão Número de Dias para Reposição = (Saldo na Instituição – Quantidade de Reposição) / Consumo Diário
			diasReposicao = new BigDecimal(saldoInstituicao).subtract(quantidadeReposicao).divide(dadosItemApto.getConsumoDiario(), 2, RoundingMode.HALF_EVEN)
					.intValue();
		}

		// Data de Reposição: Data Atual + Número de Dias para Reposição.
		Date dataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), diasReposicao);

		// Verifica-se quantidade de parcelas já assinadas e não entregues cuja previsão de entrega é menor que a data encontrada: C11
		Object[] qtds = this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(
				dadosItemApto.getAfnNumero(), dadosItemApto.getNumero(), dataReposicao);
		if (qtds != null) {
			Long quantidade = qtds[0] != null ? (Long) qtds[0] : 0;
			Long quantidadeEntregue = qtds[1] != null ? (Long) qtds[1] : 0;
			Long quantidadeNaoEntregue = quantidade - quantidadeEntregue;
			if (quantidadeNaoEntregue > 0) {
				// Caso seja encontrado valores nesta consulta, a quantidade deve ser somada ao saldo na instituição e a conta refeita para
				// determinada nova data para previsão de entrega. Neste caso executa-se a C11 novamente e possivelmente determina-se nova data para
				// previsão de entrega.
				saldoInstituicao = saldoInstituicao + quantidadeNaoEntregue.intValue();

				Date novaDataReposicao = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), new BigDecimal(saldoInstituicao).subtract(quantidadeReposicao).divide(dadosItemApto.getConsumoDiario(), 2, RoundingMode.HALF_EVEN).intValue());

				//verificar nova data de reposicao.
				//Caso esta Data de Reposição tenha sido modificada, deve-se refazer a consulta C11 usando esta nova data como parâmetro.
				dataReposicao = verificarDatasReposicao(dataReposicao, novaDataReposicao, dadosItemApto);
			}
		}

		return dataReposicao;
	}
	
	protected Date verificarDatasReposicao(Date dataReposicao, Date novaDataReposicao, DadosItemAptoProgramacaoEntregaVO dadosItemApto){
		//Caso esta Data de Reposição tenha sido modificada, deve-se refazer a consulta C11 usando esta nova data como parâmetro.
		if(!DateUtil.isDatasIguais(dataReposicao, novaDataReposicao)){
			Object[] qtds = this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().buscarParcelasAssinadasSaldoReceberAntesPrimeiraParcelas(
					dadosItemApto.getAfnNumero(), dadosItemApto.getNumero(), novaDataReposicao);
			if (qtds != null) {
				Long quantidade = qtds[0] != null ? (Long) qtds[0] : 0;
				Long quantidadeEntregue = qtds[1] != null ? (Long) qtds[1] : 0;
				Long quantidadeNaoEntregue = quantidade - quantidadeEntregue;
				if (quantidadeNaoEntregue > 0) {
					return novaDataReposicao;
				}
			}	
		}
		return dataReposicao;
	}

	/**
	 * Na exclusão de parcelas não assinadas, D2, deve-se primeiramente excluir o vínculo de uma parcela com sua solicitação de compra, D1
	 * 
	 * RN13 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param numero
	 */
	protected void excluirParcelasNaoAssinadas(Integer afnNumero, Integer numero) {
		List<ScoProgEntregaItemAutorizacaoFornecimento> itens = this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().pesquisarParcelasNaoAssinadas(
				afnNumero, numero);

		if (itens != null && !itens.isEmpty()) {

			for (ScoProgEntregaItemAutorizacaoFornecimento item : itens) {

				if (item.getSolicitacoesProgEntrega() != null && !item.getSolicitacoesProgEntrega().isEmpty()) {
					for (ScoSolicitacaoProgramacaoEntrega scoSolicitacaoProgramacaoEntrega : item.getSolicitacoesProgEntrega()) {
						this.removerScoSolicitacaoProgramacaoEntregaPorItem(scoSolicitacaoProgramacaoEntrega);
					}
				}

				this.removerScoProgEntregaItemAutorizacaoFornecimento(item);
			}
		}
	}

	/**
	 * Exclusão dos registros que relacionam solicitações de compra à parcelas de entrega. Tabela: sco_solic_progr_entrega
	 * 
	 * D1 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param scoSolicitacaoProgramacaoEntrega
	 */
	private void removerScoSolicitacaoProgramacaoEntregaPorItem(ScoSolicitacaoProgramacaoEntrega scoSolicitacaoProgramacaoEntrega) {
		this.getScoSolicitacaoProgramacaoEntregaDAO().remover(scoSolicitacaoProgramacaoEntrega);
		// Necessário fazer o flush para buscar o número correto da parcela na inserção das parcelas
		this.getScoSolicitacaoProgramacaoEntregaDAO().flush();

		this.getScoSolicitacaoProgramacaoEntregaDAO().desatachar(scoSolicitacaoProgramacaoEntrega);
	}

	/**
	 * Exclusão das parcelas ainda não assinadas, Tabela: sco_progr_entrega_itens_af
	 * 
	 * D2 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param item
	 */
	private void removerScoProgEntregaItemAutorizacaoFornecimento(ScoProgEntregaItemAutorizacaoFornecimento item) {
		this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().remover(item);
		// Necessário fazer o flush para buscar o número correto da parcela na inserção das parcelas
		this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().flush();

		this.getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO().desatachar(item);
	}

	/**
	 * Quantidade da 1º Parcela:
	 * 
	 * RN06 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param dadosItemApto
	 * @param dataPrimeiraParcela
	 * @param quantidadeReposicao
	 * @param classificacaoABC
	 * @param sceAlmoxarifadoReferencia
	 * @param nroComplemento
	 * @param lctNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Integer obterQuantidadePrimeiraParcela(DadosItemAptoProgramacaoEntregaVO dadosItemApto, Date dataPrimeiraParcela, BigDecimal quantidadeReposicao,
			DominioClassifABC classificacaoABC, SceAlmoxarifado sceAlmoxarifadoReferencia, Short nroComplemento, Integer lctNumero) throws ApplicationBusinessException {

		Integer result = null;

		// Quantidade do Item já na instituição <RN08, com data de referencia da C10 sendo a data da primeira parcela>
		Integer quantidadeTotalInstituicao = this.obterSaldoInstituicao(dadosItemApto, dataPrimeiraParcela);

		// (Data da primeira parcela <RN03> – Data Atual)
		int dias = DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(new Date()), dataPrimeiraParcela);

		// Saldo na data da primeira parcela = Quantidade do Item já na instituição <RN08, com data de referencia da C10 sendo a data da primeira
		// parcela> – (Consumo Diário Previsto (C1) * (Data da primeira parcela <RN03> – Data Atual))
		Integer saldoDataPrimeiraParcela = quantidadeTotalInstituicao - dadosItemApto.getConsumoDiario().multiply(new BigDecimal(dias)).intValue();

		// Caso o saldo seja menor que zero, assume-se saldo igual a zero para a data da primeira parcela.
		if (saldoDataPrimeiraParcela.intValue() < 0) {
			saldoDataPrimeiraParcela = 0;
		}

		// Caso o item tenha restrições no espaço de armazenamento, sua primeira parcela será o que falta para completar seu espaço máximo:
		// Quantidade da 1º Parcela = eal.qtd_espaco_armazena - Saldo na data da primeira parcela
		// Senão:
		// Quantidade da 1º Parcela = Quantidade de Reposição - Saldo na data da primeira parcela + (Consumo Diário <C1> *
		// sce_almoxarifados.dias_parcela_class_X <C3>).

		if (dadosItemApto.isIndUtilizaEspaco() && dadosItemApto.getQtdeEspacoArmazena() != null && dadosItemApto.getQtdeEspacoArmazena().intValue() > 0) {
			result = dadosItemApto.getQtdeEspacoArmazena() - saldoDataPrimeiraParcela;
		} else {
			result = quantidadeReposicao.intValue() - saldoDataPrimeiraParcela;

			BigDecimal intervaloDias = BigDecimal.ZERO;
			BigDecimal[] consumo = this.obterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(dadosItemApto.getConsumoDiario(), classificacaoABC,
					sceAlmoxarifadoReferencia);
			if (consumo != null && consumo[1] != null) {
				intervaloDias = consumo[1];
			}
			result = result + (intervaloDias.multiply(dadosItemApto.getConsumoDiario()).intValue());
		}

		ProgramacaoAutomaticaON programacaoAutomaticaON = this.getProgramacaoAutomaticaON();
		Integer fatorConversao = programacaoAutomaticaON.obterFatorConversao(dadosItemApto.getNumero(), nroComplemento, lctNumero);

		// A quantidade de cada parcela deve ser um múltiplo do fator de conversão do fornecedor. (C4:
		// sco_itens_autorizacao_forn.fator_conversao_forn)
		// Se a quantidade não for um múltiplo do fator de conversão deve-se retirar unidades desta parcela até que a quantidade desta seja um
		// múltiplo.
		Integer resto = result % fatorConversao;
		if (resto > 0) {
			result = result - resto;
		}

		return result;
	}

	/**
	 * Quantidade de cada parcela
	 * 
	 * RN09 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param dadosItemApto
	 * @param intervaloParcelas
	 * @param classificacaoABC
	 * @param sceAlmoxarifadoReferencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Integer obterQuantidadeCadaParcela(DadosItemAptoProgramacaoEntregaVO dadosItemApto, BigDecimal intervaloParcelas, DominioClassifABC classificacaoABC,
			SceAlmoxarifado sceAlmoxarifadoReferencia) throws ApplicationBusinessException {

		Integer result = null;

		// Caso o item possua restrições quando ao espaço de armazenamento:
		if (dadosItemApto.isIndUtilizaEspaco()) {
			// Consumo Diário <C1> * Intervalo entre parcelas <RN02>
			result = dadosItemApto.getConsumoDiario().multiply(intervaloParcelas).intValue();
		} else {
			// Senão: Consumo Diário <C1> * sce_almoxarifados.dias_parcela_class_X <C3>
			BigDecimal intervaloDias = BigDecimal.ZERO;
			BigDecimal[] consumo = this.obterQuantidadeReposicaoIntervaloParcelasClassificacaoABC(dadosItemApto.getConsumoDiario(), classificacaoABC,
					sceAlmoxarifadoReferencia);
			if (consumo != null && consumo[1] != null) {
				intervaloDias = consumo[1];
			}
			result = dadosItemApto.getConsumoDiario().multiply(intervaloDias).intValue();
		}

		return result;
	}

	/**
	 * Numero de parcelas e o resto da ultima, se houver
	 * 
	 * RN07 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param quantidadePrimeiraParcela
	 * @param quantidadeCadaParcela
	 * @param numero
	 * @param nroComplemento
	 * @param lctNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected QuantidadesVO obterNumeroParcelasQuantidadeUltima(Integer quantidadePrimeiraParcela, Integer quantidadeCadaParcela, Integer numero,
			Short nroComplemento, Integer lctNumero) throws ApplicationBusinessException {

		int parcelas = 0;
		int resto = 0;

		Integer saldoAfNaoPlanejado = this.getProgramacaoAutomaticaON().obterSaldoNaoAssinado(numero, nroComplemento, lctNumero);

		if (saldoAfNaoPlanejado >= quantidadePrimeiraParcela) {

			// remove a primeira parcela
			int saldo = saldoAfNaoPlanejado - quantidadePrimeiraParcela;
			parcelas++;

			// calcula o resto da divisão do saldo ainda não planejado pela quantidade de cada parcela
			if (quantidadeCadaParcela > 0 && saldo > 0) {
				resto = saldo % quantidadeCadaParcela;

				// se houve resto, vai ter uma parcela a mais no fim
				if (resto > 0) {
					// remove a ultima parcela
					saldo = saldo - resto;
					resto = resto + quantidadeCadaParcela; // adiciona o resto na ultima parcela
				}

				parcelas = parcelas + (saldo / quantidadeCadaParcela);
			}
		} else {
			// caso o saldoAfNaoPlanejado seja inferor à primeira parcela, cria apenas a parcela extra, com o valor do saldoAfNaoPlanejado
			parcelas++;
			resto = saldoAfNaoPlanejado;
		}

		return new QuantidadesVO((long) parcelas, (long) resto);

	}

	// ------------------ GETs e SETs

	protected IEstoqueFacade getEstoqueFacade() {	return estoqueFacade;	}

	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {return scoSolicitacaoProgramacaoEntregaDAO;	}

	protected ScoSolicitacaoDeCompraDAO getScoSolicitacaoDeCompraDAO() {return scoSolicitacaoDeCompraDAO;	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {return scoItemAutorizacaoFornDAO;	}

	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {return scoAutorizacaoFornDAO;	}

	protected ProgramacaoAutomaticaON getProgramacaoAutomaticaON() {return programacaoAutomaticaON;	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO() {	return scoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {	return this.servidorLogadoFacade;	}
}