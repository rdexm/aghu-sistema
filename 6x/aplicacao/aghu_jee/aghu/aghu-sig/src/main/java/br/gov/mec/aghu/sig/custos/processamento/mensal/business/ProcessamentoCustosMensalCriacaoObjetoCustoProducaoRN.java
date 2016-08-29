package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoCalculo;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.CalculoSubProdutoVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoExamesVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ETAPA 7: Criar objetos de custo com produção do mês Estória de Usuário #16834
 * - Processamento mensal - entre as páginas 34 e 37
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCriacaoObjetoCustoProducaoRN.class)
public class ProcessamentoCustosMensalCriacaoObjetoCustoProducaoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719785L;

	@Override
	public String getTitulo() {
		return "Criacao dos objetos de custo com produção do mês.";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustomensalCriacaoObjetoCustoProducaoON - processamentoInsumosEtapa7";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 7;
	}

	@Override
	protected void executarPassosInternos(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto)
			throws ApplicationBusinessException {
		this.processamentoInsumosEtapa7(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos);
	}

	/**
	 * Executa todos os passos do processamento da etapa 7
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto
	 *            Processamento atual.
	 * @param rapServidores
	 *            Servidor logado.
	 * @param sigProcessamentoPassos
	 *            Passo atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void processamentoInsumosEtapa7(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_INICIO_CRIACAO_OBJETO_CUSTO_PRODUCAO");
		
		// Passo 2
		List<ObjetoCustoProducaoExamesVO> listProdPHI = this
				.getProcessamentoCustoUtils()
				.getSigObjetoCustoVersoesDAO()
				.buscaObjetosCustoProducaoPHI(sigProcessamentoCusto);// Consulta
																		// passo
																		// 2
																		// (8.4)

		// Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_OBJETO_CUSTO_PRODUCAO_SUCESSO");

		// Passo 4 (internamente são realizados os passos 5 e 6)
		this.inserirCalculoObjetoCusto(listProdPHI, sigProcessamentoCusto,
				rapServidores, DominioTipoCalculo.CM);

		// Passo 7
		List<ObjetoCustoProducaoExamesVO> listProdPaciente = this
				.getProcessamentoCustoUtils()
				.getSigObjetoCustoVersoesDAO()
				.buscaObjetosCustoProducaoPaciente(sigProcessamentoCusto);// Consulta
																			// passo
																			// 9
																			// (8.5)

		// Passo 8
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_OBJETO_CUSTO_PRODUCAO_PACIENTE_SUCESSO");

		// Passo 9 - repetir passos de 04 ate 06 para os resultados da consulta
		// do passo 7
		this.inserirCalculoObjetoCusto(listProdPaciente, sigProcessamentoCusto,
				rapServidores, DominioTipoCalculo.CP);

		// Passo 10 e 11
		this.atualizarComposicaoSubProdutos(sigProcessamentoCusto);

		// Passo 12
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_DADOS_OBJETO_CUSTO_GRAVADOS_SUCESSO");
	}

	/**
	 * Execução dos passos 4, 5 e 6, inserir calculo do objeto de custo
	 * 
	 * @author rogeriovieira
	 * @param listProdExamesVO
	 *            lista retornada no passo 2
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @param rapServidores
	 *            servidor utilizado no processamento
	 * @param tipoCalculo
	 *            tipo do cálculo
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void inserirCalculoObjetoCusto(
			List<ObjetoCustoProducaoExamesVO> listProdExamesVO,
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, DominioTipoCalculo tipoCalculo)
			throws ApplicationBusinessException {

		// Passo 4
		for (ObjetoCustoProducaoExamesVO objProd : listProdExamesVO) {

			SigCalculoObjetoCusto calculoObjetoCusto = new SigCalculoObjetoCusto();

			SigObjetoCustoVersoes objCustoVersao = this
					.getProcessamentoCustoUtils()
					.getSigObjetoCustoVersoesDAO()
					.obterPorChavePrimaria(objProd.getSeq());

			calculoObjetoCusto.setSigProcessamentoCustos(sigProcessamentoCusto);
			calculoObjetoCusto.setSigObjetoCustoVersoes(objCustoVersao);
			calculoObjetoCusto.setFccCentroCustos(objProd.getFccCentroCustos());
			calculoObjetoCusto.setQtdeProduzida(objProd.getQtde());
			calculoObjetoCusto.setIndComposicao(true);
			calculoObjetoCusto.setRapServidores(rapServidores);
			calculoObjetoCusto.setCriadoEm(new Date());
			calculoObjetoCusto.setTipoCalculo(tipoCalculo);
			calculoObjetoCusto.setPagador(objProd.getPagador());

			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().persistir(calculoObjetoCusto);

			// Passo 5
			this.inserirProducaoObjetoCusto(calculoObjetoCusto,
					sigProcessamentoCusto, rapServidores, tipoCalculo, objProd,
					objCustoVersao);

			// Passo 6
			this.inserirCalculoComponente(sigProcessamentoCusto,
					calculoObjetoCusto, objCustoVersao, rapServidores);
		}
		this.commitProcessamentoCusto();
	}

	/**
	 * Execução do passo 5, associar com produção do objeto de custo
	 * 
	 * @author rogeriovieira
	 * @param calculoObjetoCusto
	 *            cálculo do objeto de custo criado no passo 4
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @param rapServidores
	 *            servidor utilizado no processamento
	 * @param tipoCalculo
	 *            tipo do calculo
	 * @param objProd
	 *            vo com a produção do exame
	 * @param objCustoVersao
	 *            versão do objeto de custo
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento
	 */
	private void inserirProducaoObjetoCusto(
			SigCalculoObjetoCusto calculoObjetoCusto,
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, DominioTipoCalculo tipoCalculo,
			ObjetoCustoProducaoExamesVO objProd,
			SigObjetoCustoVersoes objCustoVersao)
			throws ApplicationBusinessException {

		List<SigDetalheProducao> listDetalheProducao = new ArrayList<SigDetalheProducao>();

		switch (tipoCalculo) {
		case CM:
			listDetalheProducao = this
					.getProcessamentoCustoUtils()
					.getSigDetalheProducaoDAO()
					.pesquisarDetalheProducao(sigProcessamentoCusto,
							objProd.getFccCentroCustos(), objCustoVersao);// Consulta passo 5 (8.6)
			break;
		case CP:
			if(objProd.getDhpSeq() != null){
				listDetalheProducao.add(this.getProcessamentoCustoUtils()
						.getSigDetalheProducaoDAO()
						.obterPorChavePrimaria(objProd.getDhpSeq()));
			}
			break;
		}

		for (SigDetalheProducao detalheProducao : listDetalheProducao) {
			SigProducaoObjetoCusto producaoObjetoCusto = new SigProducaoObjetoCusto();

			producaoObjetoCusto.setRapServidores(rapServidores);
			producaoObjetoCusto.setCriadoEm(new Date());
			producaoObjetoCusto.setSigCalculoObjetoCustos(calculoObjetoCusto);

			producaoObjetoCusto.setSigDetalheProducoes(detalheProducao);

			this.getProcessamentoCustoUtils()
					.getSigProducaoObjetoCustoDAO()
					.persistir(producaoObjetoCusto);
		}
		this.commitProcessamentoCusto();
	}

	/**
	 * Execução do Passo 6, inserir calculo componente
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @param calculoObjetoCusto
	 *            cálculo do objeto de custo criado no passo 4
	 * @param objCustoVersao
	 *            versão do objeto de custo
	 * @param rapServidores
	 *            servidor utilizado no processamento
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento
	 */
	private void inserirCalculoComponente(
			SigProcessamentoCusto sigProcessamentoCusto,
			SigCalculoObjetoCusto calculoObjetoCusto,
			SigObjetoCustoVersoes objCustoVersao, RapServidores rapServidores)
			throws ApplicationBusinessException {

		List<SigObjetoCustoComposicoes> listComposicoes = this
				.getProcessamentoCustoUtils()
				.getSigObjetoCustoComposicoesDAO()
				.pesquisarObjetoCustoComposicaoAtivo(objCustoVersao);// Consulta
																		// passo
																		// 6
																		// (8.7)

		if (listComposicoes != null && !listComposicoes.isEmpty()) {

			for (SigObjetoCustoComposicoes composicao : listComposicoes) {

				SigCalculoComponente calculoComponente = new SigCalculoComponente();

				calculoComponente
						.setSigProcessamentoCustos(sigProcessamentoCusto);
				calculoComponente
						.setSigCalculoObjetoCustosByCbjSeq(calculoObjetoCusto);
				calculoComponente.setSigObjetoCustoComposicoes(composicao);
				calculoComponente.setSigDirecionadores(composicao
						.getSigDirecionadores());
				calculoComponente.setRapServidores(rapServidores);
				calculoComponente.setCriadoEm(new Date());

				this.getProcessamentoCustoUtils()
						.getSigCalculoComponenteDAO()
						.persistir(calculoComponente);
			}
		} else {
			// FE03
			calculoObjetoCusto.setIndComposicao(false);
			this.getProcessamentoCustoUtils()
					.getSigCalculoObjetoCustoDAO()
					.atualizar(calculoObjetoCusto);
		}
		this.commitProcessamentoCusto();
	}

	/**
	 * Execução dos passos 10 e 11, inserir composição de sub-produtos
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void atualizarComposicaoSubProdutos(
			SigProcessamentoCusto sigProcessamentoCusto)
			throws ApplicationBusinessException {

		// Passo 10
		List<CalculoSubProdutoVO> listaCalculoSubProduto = this
				.getProcessamentoCustoUtils()
				.getSigCalculoObjetoCustoDAO()
				.pesquisarCalculoObjetoCustoSubProduto(
						sigProcessamentoCusto.getSeq());

		// Passo 11
		for (CalculoSubProdutoVO vo : listaCalculoSubProduto) {
			if (vo.getCalculoComponente() != null) {
				vo.getCalculoComponente()
						.setSigCalculoObjetoCustosByCbjSeqSubProduto(
								vo.getCalculoObjetoCustoSubProduto());
				this.getProcessamentoCustoUtils()
						.getSigCalculoComponenteDAO()
						.atualizar(vo.getCalculoComponente());
			}
		}
		this.commitProcessamentoCusto();
	}
}