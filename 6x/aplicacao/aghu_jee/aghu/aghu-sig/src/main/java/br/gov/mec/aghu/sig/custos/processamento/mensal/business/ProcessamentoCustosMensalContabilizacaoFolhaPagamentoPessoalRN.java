package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoDetalheFolha;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDetalheFolhaPessoa;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.FolhaPagamentoRHVo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por contabilizar a folha de pagamento do pessoal no mês,
 * por grupo de ocupação e centro de custo
 * 
 * #16834 - #19745
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContabilizacaoFolhaPagamentoPessoalRN.class)
public class ProcessamentoCustosMensalContabilizacaoFolhaPagamentoPessoalRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719787L;

	@Override
	public String getTitulo() {
		return "Contabilizar a folha de pagamento do pessoal no mes, por grupo de ocupacao e centro de custo.";
	}

	@Override
	public String getNome() {
		return "processamentoMensalContabilizacaoFolhaPagamentoPessoalON - processamentoInsumosEtapa4";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 4;
	}

	@Override
	protected void executarPassosInternos(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto)
			throws ApplicationBusinessException {
		this.processamentoInsumosEtapa4(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos);
	}

	/**
	 * Método principal da Etapa 4 do processamento, é a responsável em disparar
	 * todos os passos dessa etapa.
	 * 
	 * @author rhrosa
	 * @param sigProcessamentoCusto
	 *            Processamento Atual.
	 * @param rapServidores
	 *            Servidor Logado.
	 * @param sigProcessamentoPassos
	 *            Passo Atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	public void processamentoInsumosEtapa4(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto,
						rapServidores, sigProcessamentoPassos,
						DominioEtapaProcessamento.G,
						"MENSAGEM_INICIO_CONTABILIZACAO_FOLHA_PGTO");

		/*
		 * Passo 2 Executa servico que consulta a view v_sig_detalhe_folha para
		 * buscar a folha de pagamento da competencia informada. Obs: Não está
		 * retornando dados, em 25/07, quando realizado teste Unitário da classe
		 */
		List<FolhaPagamentoRHVo> listaFolha = this
				.getProcessamentoCustoUtils()
				.getPatrimonioService()
				.buscaFolhaPagamentoMensal(
						sigProcessamentoCusto.getCompetenciaMesAno());

		// Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto,
						rapServidores, sigProcessamentoPassos,
						DominioEtapaProcessamento.G,
						"MENSAGEM_BUSCA_SUCESSO_DADOS_FOLHA_PGTO");

		// Passo 4
		Long qtdMvto = 0L;
		BigDecimal valorMvto = BigDecimal.ZERO;
		SigMvtoContaMensal mvtoContaMensalAtual = null;
		int codigoCentroCustoAtual = 0;

		for (FolhaPagamentoRHVo folhaPagamentoRHVo : listaFolha) {

			// [FE01] - Passo 2
			if (codigoCentroCustoAtual != this
					.getCodigoCentroCustoEfetivo(folhaPagamentoRHVo)) {
				codigoCentroCustoAtual = this
						.getCodigoCentroCustoEfetivo(folhaPagamentoRHVo);
				if (mvtoContaMensalAtual != null) {
					this.atualizaQtdeValor(mvtoContaMensalAtual, valorMvto,
							qtdMvto);
					qtdMvto = 0L;
					valorMvto = BigDecimal.ZERO;
					mvtoContaMensalAtual = null;
				}
			}

			// [FE01]
			if (folhaPagamentoRHVo.getGocSeq() != null) {

				// Passo 4
				SigMvtoContaMensal mvtoContaMensal = this
						.criaSigMvtoContaMensal(folhaPagamentoRHVo,
								sigProcessamentoCusto, rapServidores);

				mvtoContaMensal.setSigGrupoOcupacoes(this
						.getProcessamentoCustoUtils()
						.getSigGrupoOcupacoesDAO()
						.obterPorChavePrimaria(
								folhaPagamentoRHVo.getGocSeq().intValue()));
				mvtoContaMensal.setQtde(this
						.totalizaQuantidade(folhaPagamentoRHVo));
				mvtoContaMensal
						.setValor(this.totalizaValor(folhaPagamentoRHVo));

				this.getProcessamentoCustoUtils()
						.getSigMvtoContaMensalDAO().persistir(mvtoContaMensal);

				// Passo 5
				this.efetuaCadastroDoDetalheFolhaParte1(mvtoContaMensal,
						folhaPagamentoRHVo, false);

			} else {
				// [FE01] - Passo 1
				if (mvtoContaMensalAtual == null) {
					mvtoContaMensalAtual = this.criaSigMvtoContaMensal(
							folhaPagamentoRHVo, sigProcessamentoCusto,
							rapServidores);
					mvtoContaMensalAtual.setQtde(0L);
					mvtoContaMensalAtual.setValor(BigDecimal.ZERO);
					this.getProcessamentoCustoUtils()
							.getSigMvtoContaMensalDAO()
							.persistir(mvtoContaMensalAtual);
				}

				qtdMvto += this.totalizaQuantidade(folhaPagamentoRHVo);
				valorMvto = valorMvto.add(this
						.totalizaValor(folhaPagamentoRHVo));

				// [FE01] - Passo 3
				this.efetuaCadastroDoDetalheFolhaParte1(mvtoContaMensalAtual,
						folhaPagamentoRHVo, true);
			}
		}

		if (mvtoContaMensalAtual != null) {
			this.atualizaQtdeValor(mvtoContaMensalAtual, valorMvto, qtdMvto);
		}

		this.commitProcessamentoCusto();

		// Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto,
						rapServidores, sigProcessamentoPassos,
						DominioEtapaProcessamento.G,
						"MENSAGEM_GRAVADOS_SUCESSO_DADOS_FOLHA_PGTO");
	}

	/**
	 * Atualiza e persisti a qtde e valor passads no Movimento e atualiza no BD.
	 * 
	 * @author rmalvezzi
	 * @param mvtoContaMensalAtual
	 *            Objeto atual do Movimento Conta Mensal.
	 * @param valorMvto
	 *            Valor total do Movimento.
	 * @param qtdMvto
	 *            Qtde total do Movimento.
	 */
	private void atualizaQtdeValor(SigMvtoContaMensal mvtoContaMensalAtual,
			BigDecimal valorMvto, Long qtdMvto) {
		mvtoContaMensalAtual.setQtde(qtdMvto);
		mvtoContaMensalAtual.setValor(valorMvto);
		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.atualizar(mvtoContaMensalAtual);
	}

	/**
	 * Descobre o codigo do centro de custo efeitovo, se o Atua é diferente de
	 * null retorna o Atua se não o Lotado.
	 * 
	 * @author rmalvezzi
	 * @param folhaPagamentoRHVo
	 *            VO representado o registro do retorno da consulta.
	 * @return Codigo do centro de custo efetivo.
	 */
	private int getCodigoCentroCustoEfetivo(
			FolhaPagamentoRHVo folhaPagamentoRHVo) {
		return ((folhaPagamentoRHVo.getCctCodigoAtua() != null) ? folhaPagamentoRHVo
				.getCctCodigoAtua().intValue() : folhaPagamentoRHVo
				.getCctCodigoLotado().intValue());
	}

	/**
	 * Cria um movimento default com dados do VO.
	 * 
	 * @author rmalvezzi
	 * @param folhaPagamentoRHVo
	 *            VO representado o registro do retorno da consulta.
	 * @param processamentoCusto
	 *            Processamento atual.
	 * @param servidor
	 *            Servidor logado.
	 * @return Um novo movimento preenchido com informações do VO.
	 */
	private SigMvtoContaMensal criaSigMvtoContaMensal(
			FolhaPagamentoRHVo folhaPagamentoRHVo,
			SigProcessamentoCusto processamentoCusto, RapServidores servidor) {
		SigMvtoContaMensal mvtoContaMensal = new SigMvtoContaMensal();
		mvtoContaMensal.setSigProcessamentoCustos(processamentoCusto);
		mvtoContaMensal.setCriadoEm(new Date());
		mvtoContaMensal.setRapServidores(servidor);
		mvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.SIP);
		mvtoContaMensal.setTipoValor(DominioTipoValorConta.DP);

		mvtoContaMensal.setFccCentroCustos(this
				.getProcessamentoCustoUtils()
				.getCentroCustoFacade()
				.obterCentroCustoPorChavePrimaria(
						folhaPagamentoRHVo.getCctCodigoLotado().intValue()));

		if (folhaPagamentoRHVo.getCctCodigoAtua() != null
				&& folhaPagamentoRHVo.getCctCodigoAtua().intValue() != folhaPagamentoRHVo
						.getCctCodigoLotado().intValue()) {
			mvtoContaMensal.setFccCentroCustosDebita(this
					.getProcessamentoCustoUtils()
					.getCentroCustoFacade()
					.obterCentroCustoPorChavePrimaria(
							folhaPagamentoRHVo.getCctCodigoAtua().intValue()));
		}
		return mvtoContaMensal;
	}

	/**
	 * Calcula a quantidade de horas trabalhadas pelos funcionários
	 * 
	 * @author rhrosa
	 * @param folhaPagamentoRHVo
	 *            VO representado o registro do retorno da consulta.
	 * @return Total de horas.
	 */
	private long totalizaQuantidade(FolhaPagamentoRHVo folhaPagamentoRHVo) {
		return (folhaPagamentoRHVo.getTotHrContrato().longValue() + folhaPagamentoRHVo
				.getTotHrExcede().longValue())
				- folhaPagamentoRHVo.getTotHrDesconto().longValue();
	}

	/**
	 * Calcula o total de salário pago somado a todas as provisões mensais
	 * realizadas, que garantem os pagamentos de 13º salário, férias e outros
	 * benefícios
	 * 
	 * @author rhrosa
	 * @param folhaPagamentoRHVo
	 *            VO representado o registro do retorno da consulta.
	 * @return Valor Total.
	 */
	private BigDecimal totalizaValor(FolhaPagamentoRHVo folhaPagamentoRHVo) {
		return folhaPagamentoRHVo.getTotSalarios()
				.add(folhaPagamentoRHVo.getTotEncargos())
				.add(folhaPagamentoRHVo.getTotProv13())
				.add(folhaPagamentoRHVo.getTotProvFerias())
				.add(folhaPagamentoRHVo.getTotProvEncargos());
	}

	/**
	 * Armazena os valores totalizados em SIG_MVTO_CONTA_MENSAIS, para cada
	 * coluna de total, seja de horas ou valor, será inserida uma linha na
	 * tabela SIG_ DETALHE_FOLHA_PESSOAS. Parte 1.
	 * 
	 * @author rhrosa
	 * @param mvtoContaMensal
	 *            Objeto atual do Movimento Conta Mensal.
	 * @param folhaPagamentoRHVo
	 *            VO representado o registro do retorno da consulta.
	 * @param existeOcaCodigo
	 *            Se existe ou não Oca código.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se algum erro acontecer na hora do commit do
	 *             processamento.
	 */
	private void efetuaCadastroDoDetalheFolhaParte1(
			SigMvtoContaMensal mvtoContaMensal,
			FolhaPagamentoRHVo folhaPagamentoRHVo, boolean existeOcaCodigo)
			throws ApplicationBusinessException {

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getNroFuncionarios(),
				DominioTipoDetalheFolha.NRF);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotHrContrato(),
				DominioTipoDetalheFolha.THC);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotHrDesconto(),
				DominioTipoDetalheFolha.THD);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotHrExcede(),
				DominioTipoDetalheFolha.THE);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotSalBase(),
				DominioTipoDetalheFolha.TSB);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotSalarios(),
				DominioTipoDetalheFolha.TSP);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotEncargos(),
				DominioTipoDetalheFolha.TSE);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotProv13(),
				DominioTipoDetalheFolha.P13);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotProvFerias(),
				DominioTipoDetalheFolha.PFE);

		this.persistirFolhaPessoa(mvtoContaMensal, folhaPagamentoRHVo,
				existeOcaCodigo, folhaPagamentoRHVo.getTotProvEncargos(),
				DominioTipoDetalheFolha.TPE);
	}

	/**
	 * Cria e insere um registro na SigDetalheFolhaPessoa com os paramêtros
	 * informados.
	 * 
	 * @author rmalvezzi
	 * @param mvtoContaMensal
	 *            Objeto atual do Movimento Conta Mensal.
	 * @param folhaPagamentoRHVo
	 *            VO que representa o retorno da consulta.
	 * @param existeOcaCodigo
	 *            Se existe ou não Oca código.
	 * @param valor
	 *            Field Valor no Detalhe Folha Pessoa a ser persistido.
	 * @param dominioTipoDetalheFolha
	 *            Field Tipo no Detalhe Folha Pessoa a ser persistido.
	 */
	private void persistirFolhaPessoa(SigMvtoContaMensal mvtoContaMensal,
			FolhaPagamentoRHVo folhaPagamentoRHVo, boolean existeOcaCodigo,
			BigDecimal valor, DominioTipoDetalheFolha dominioTipoDetalheFolha) {

		SigDetalheFolhaPessoa folha = new SigDetalheFolhaPessoa();
		folha.setSigMvtoContaMensal(mvtoContaMensal);
		folha.setCriadoEm(new Date());
		folha.setRapServidores(mvtoContaMensal.getRapServidores());
		folha.setIndTipoDetalheFolha(dominioTipoDetalheFolha);
		folha.setValor(valor);

		if (existeOcaCodigo) {
			folha.setOcaCarCodigo(folhaPagamentoRHVo.getOcaCarCodigo());
			folha.setOcaCodigo(folhaPagamentoRHVo.getOcaCodigo().intValue());
		}

		this.getProcessamentoCustoUtils().getSigDetalheFolhaPessoaDAO()
				.persistir(folha);
	}

}
