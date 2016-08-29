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

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioPessoa;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.GrupoOcupacaoAlocadoAtividadeVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoRateioVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresPessoalVO;

/**
 * Classe responsável pela rateio dos valores de pessoas do mês por centro de
 * custo ainda não divididos. #21005
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalRateioDiretoPessoalRN.class)
public class ProcessamentoCustosMensalRateioDiretoPessoalRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 2757894142800253168L;

	@Override
	public String getTitulo() {
		return "Rateio custo direto de pessoal.";
	}

	@Override
	public String getNome() {
		return "processamentoRateioCustoDiretoPessoalRN - processamentoRateioPessoal";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 18;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoRateioPessoal(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Fluxo principal do Rateio de pessoas em objetos de custo.
	 * 
	 * @author rmalvezzi
	 * @param processamentoAtual					Processamento Atual.
	 * @param servidor								Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void processamentoRateioPessoal(SigProcessamentoCusto processamentoAtual, RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_INICIO_PROCESSAMENTO_RATEIO_PESSOAL");

		//Passo 2
		this.executarEtapa1CalculoDistribuicaoPessoalAssociadoAtividade(processamentoAtual, servidor, sigProcessamentoPassos);

		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_RATEIO_CUSTOS_PESSOAL_ASSOCIADOS_ATIVIDADE");

		//Passo 4
		this.executarEtapa2CalculoDistribuicaoPessoalNaoAssociadoAtividade(processamentoAtual, servidor, sigProcessamentoPassos);

		//Passo 5
		this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_RATEIO_CUSTOS_PESSOAL_NAO_ASSOCIADOS_ATIVIDADE");

		//Passo 6
		this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_CALCULO_PESSOAL_ATIVIDADE");

	}

	/**
	 * Etapa 1 - Cálculo de distribuição de pessoal associados a atividades 
	 * 
	 * @author rmalvezzi
	 * @param processamento							Processamento Atual.
	 * @param servidor								Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa1CalculoDistribuicaoPessoalAssociadoAtividade(SigProcessamentoCusto processamento, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(processamento, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_INICIO_RATEIO_RELACIONADO_PESSOAL");

		//Passo 2
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO()
				.buscarGruposOcupacaoAlocadosAtividades(processamento.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_BUSCA_PESSOAL_ALOCADOS_ATIVIDADES");

		Integer gocSeq = null;
		BigDecimal acumuloPesoOC = BigDecimal.ZERO;
		List<SigCalculoAtividadePessoa> listaIdAtividadePessoas = new ArrayList<SigCalculoAtividadePessoa>();
		Integer centroCusto = null;

		if (retorno != null) {
			//Passo 11
			while (retorno.next()) {
				GrupoOcupacaoAlocadoAtividadeVO vo = new GrupoOcupacaoAlocadoAtividadeVO((Object[]) retorno.get());

				if (!vo.getSeqGrupoOcupacao().equals(gocSeq) || !vo.getSeqCentroCusto().equals(centroCusto)) {
					if (gocSeq != null && centroCusto != null) {
						this.executarRN01CalculoQuantidadeValorDoCustoDePessoasNaAtividade(processamento, servidor, sigProcessamentoPassos,
								listaIdAtividadePessoas, acumuloPesoOC, gocSeq, centroCusto);
					}
					gocSeq = vo.getSeqGrupoOcupacao();
					acumuloPesoOC = BigDecimal.ZERO;
					listaIdAtividadePessoas = new ArrayList<SigCalculoAtividadePessoa>();
					centroCusto = vo.getSeqCentroCusto();
				}

				//Passo 5
				acumuloPesoOC = acumuloPesoOC.add(vo.getSomaPesoOC());

				//Passo 4
				if (vo.getSeqCalculoAtividadePessoa() == null) {
					SigCalculoAtividadePessoa calculoAtividadePessoa = new SigCalculoAtividadePessoa();
					calculoAtividadePessoa.setCriadoEm(new Date());
					calculoAtividadePessoa.setRapServidores(servidor);
					calculoAtividadePessoa.setSigCalculoComponentes(this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO()
							.obterPorChavePrimaria(vo.getSeqCalculoComponente()));
					calculoAtividadePessoa.setSigAtividades(this.getProcessamentoCustoUtils().getSigAtividadesDAO()
							.obterPorChavePrimaria(vo.getSeqAtividade()));
					calculoAtividadePessoa.setSigAtividadePessoas(this.getProcessamentoCustoUtils().getSigAtividadePessoasDAO()
							.obterPorChavePrimaria(vo.getSeqAtividadePessoa()));

					if (vo.getSeqDirecionador() != null) {
						calculoAtividadePessoa.setSigDirecionadores(this.getProcessamentoCustoUtils().getSigDirecionadoresDAO()
								.obterPorChavePrimaria(vo.getSeqDirecionador()));
					}

					calculoAtividadePessoa.setSigGrupoOcupacoes(this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO()
							.obterPorChavePrimaria(vo.getSeqGrupoOcupacao()));
					calculoAtividadePessoa.setQtdePrevista(0D);
					calculoAtividadePessoa.setQtdeRealizada(0D);
					calculoAtividadePessoa.setValorGrupoOcupacao(0D);
					calculoAtividadePessoa.setPeso(vo.getSomaPesoOC());

					this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO().persistir(calculoAtividadePessoa);

					listaIdAtividadePessoas.add(calculoAtividadePessoa);
				} else {
					SigCalculoAtividadePessoa calculoAtividadePessoa = this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO()
							.obterPorChavePrimaria(vo.getSeqCalculoAtividadePessoa());
					calculoAtividadePessoa.setPeso(vo.getSomaPesoOC());
					this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO().atualizar(calculoAtividadePessoa);
					listaIdAtividadePessoas.add(calculoAtividadePessoa);
				}
			}

			if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaIdAtividadePessoas)) {
				this.executarRN01CalculoQuantidadeValorDoCustoDePessoasNaAtividade(processamento, servidor, sigProcessamentoPassos, listaIdAtividadePessoas,
						acumuloPesoOC, gocSeq, centroCusto);
			}

			retorno.close();
			this.commitProcessamentoCusto();
		}

		//Passo 12
		this.buscarMensagemEGravarLogProcessamento(processamento, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_ETAPA1_RATEIO_DIRETO_PESSOAL");
	}

	/**
	 * Cálculo quantidade e valor do custo de pessoal na atividade
	 * 
	 * Quando trocar de grupo de ocupação, realiza o cálculo do rateio de pessoal alocado a cada uma das atividades 
	 * cujo peso do objeto de custo ao qual pertence foi acumulado nos passos 04 e 05, conforme descrito na RN01. [FE01] [FE02]
	 * 
	 * @author rmalvezzi
	 * @param processamento						Processamento Atual.
	 * @param servidor							Servidor Logado.
	 * @param sigProcessamentoPassos			Passo Atual.
	 * @param listaIdAtividadePessoas			Lista dos calculos de atividades de pessoas que foram acumulados.
	 * @param acumuloPesoOC						Peso acumulado.
	 * @param gocSeq							Grupo de Ocupação correspondente.
	 * @param codigoCentroCusto					Centro de custo correspondente.
	 */
	private void executarRN01CalculoQuantidadeValorDoCustoDePessoasNaAtividade(SigProcessamentoCusto processamento, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos, List<SigCalculoAtividadePessoa> listaIdAtividadePessoas, BigDecimal acumuloPesoOC, Integer gocSeq,
			Integer codigoCentroCusto) {

		FccCentroCustos centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(codigoCentroCusto);

		SigGrupoOcupacoes grupoOcupacao = this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(gocSeq);

		Object[] somatorios = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarFolhaPessoal(processamento.getSeq(), centroCusto.getCodigo(), grupoOcupacao.getSeq());

		Long qtdeHorasFolha = (Long) somatorios[0];
		BigDecimal valorFolhaPessoa = (BigDecimal) somatorios[1];

		//[FE01]
		if (qtdeHorasFolha == null || valorFolhaPessoa == null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, servidor, sigProcessamentoPassos,
					DominioEtapaProcessamento.P, "MENSAGEM_FLUXO_EXECAO_1_RATEIO_RELACIONADO_PESSOAL",
					this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(gocSeq).getDescricao(),
					processamento.getCompetenciaMesAno());
			return;
		}

		//[FE02]
		if (qtdeHorasFolha <= 0 || valorFolhaPessoa.intValue() <= 0) {
			return;
		}

		this.executarPassosSeteOitoNoveDezEtapa1(processamento, servidor, sigProcessamentoPassos, listaIdAtividadePessoas, acumuloPesoOC, qtdeHorasFolha,
				valorFolhaPessoa, centroCusto, grupoOcupacao);
	}

	/**
	 * Método responsavel em executas os passos 7, 8, 9 e 10 da etapa 1.
	 * 
	 * @author rmalvezzi
	 * @param processamento					Processamento Atual.
	 * @param servidor						Servidor logado.
	 * @param sigProcessamentoPassos		Passo Atual.
	 * @param listaIdAtividadePessoas		Lista dos calculos de atividades de pessoas que foram acumulados.
	 * @param acumuloPesoOC					Peso acumulado.
	 * @param qtdeHorasFolha				Quantidade total de consumo.
	 * @param valorFolhaPessoa				Valor total de consumo.
	 * @param centroCusto					Centro de custo correspondente.
	 * @param grupoOcupacao					Grupo de Ocupação correspondente.
	 */
	private void executarPassosSeteOitoNoveDezEtapa1(SigProcessamentoCusto processamento, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos, List<SigCalculoAtividadePessoa> listaIdAtividadePessoas, BigDecimal acumuloPesoOC,
			Long qtdeHorasFolha, BigDecimal valorFolhaPessoa, FccCentroCustos centroCusto, SigGrupoOcupacoes grupoOcupacao) {

		for (SigCalculoAtividadePessoa calculoPessoa : listaIdAtividadePessoas) {
			Double qtdRealizada = qtdeHorasFolha.doubleValue() / acumuloPesoOC.doubleValue() * calculoPessoa.getPeso().doubleValue();
			Double vlrGrupoOcupacao = valorFolhaPessoa.doubleValue() / acumuloPesoOC.doubleValue() * calculoPessoa.getPeso().doubleValue();
			calculoPessoa.setQtdeRealizada(qtdRealizada);
			calculoPessoa.setValorGrupoOcupacao(vlrGrupoOcupacao);
			this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO().atualizar(calculoPessoa);

			// Passo 7
			SigCalculoComponente componente = calculoPessoa.getSigCalculoComponentes();
			componente.setVlrPessoal(componente.getVlrPessoal().add(new BigDecimal(vlrGrupoOcupacao.toString())));
			this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(componente);

			// Passo 8
			SigCalculoObjetoCusto objetoCusto = componente.getSigCalculoObjetoCustosByCbjSeq();
			objetoCusto.setVlrAtvPessoal(objetoCusto.getVlrAtvPessoal().add(new BigDecimal(vlrGrupoOcupacao.toString())));
			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(objetoCusto);

		}

		//Passo 9
		SigMvtoContaMensal contaPessoal = new SigMvtoContaMensal();
		contaPessoal.setSigProcessamentoCustos(processamento);
		contaPessoal.setCriadoEm(new Date());
		contaPessoal.setRapServidores(servidor);
		contaPessoal.setTipoMvto(DominioTipoMovimentoConta.VAR);
		contaPessoal.setTipoValor(DominioTipoValorConta.DP);
		contaPessoal.setFccCentroCustos(centroCusto);
		contaPessoal.setSigGrupoOcupacoes(grupoOcupacao);
		contaPessoal.setQtde(qtdeHorasFolha);
		contaPessoal.setValor(valorFolhaPessoa.negate());
		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaPessoal);

		//Passo 10
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_CALCULO_PESSOA_ALOCADO_AO_COMPONENTE", contaPessoal.getSigGrupoOcupacoes().getDescricao());
	}

	/**
	 * Etapa 2 – Cálculo de distribuição de pessoal não associado a atividades
	 * 
	 * @author rmalvezzi
	 * @param processamentoAtual					Processamento Atual.
	 * @param servidor								Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa2CalculoDistribuicaoPessoalNaoAssociadoAtividade(SigProcessamentoCusto processamentoAtual, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_INICIO_RATEIO_NAO_RELACIONADO_PESSOAL");

		//Passo 2
		ScrollableResults retornoValorPessoal = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarValoresPessoal(processamentoAtual.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_BUSCA_VALORES_PESSOAL");

		Integer seqCentroCustoAtual = 0;
		boolean ocorreuExcecao = false;

		if (retornoValorPessoal != null) {
			//Passo 10
			while (retornoValorPessoal.next()) {

				ValoresPessoalVO valorPessoalVo = new ValoresPessoalVO((Object[]) retornoValorPessoal.get());

				if (!seqCentroCustoAtual.equals(valorPessoalVo.getSeqCentroCusto())) {
					seqCentroCustoAtual = valorPessoalVo.getSeqCentroCusto();
					ocorreuExcecao = false;
				}

				if (!ocorreuExcecao) {
					//Passo 4
					List<ObjetoCustoProducaoRateioVO> retornoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
							.buscarObjetosCustoComProducaoParaRateio(processamentoAtual.getSeq(), valorPessoalVo.getSeqCentroCusto());

					if (ProcessamentoCustoUtils.verificarListaNaoVazia(retornoObjetoCusto)) {

						SigGrupoOcupacoes grupoOcupacao = null;
						if (valorPessoalVo.getSeqGrupoOcupacao() != null) {
							grupoOcupacao = this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(valorPessoalVo.getSeqGrupoOcupacao());
						}

						for(ObjetoCustoProducaoRateioVO objetoCustoVO : retornoObjetoCusto){

							SigCalculoObjetoCusto calculoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
									.obterPorChavePrimaria(objetoCustoVO.getCbjSeq());

							Double qtde = valorPessoalVo.getQuantidadeHoras().doubleValue() / objetoCustoVO.getSomaPesos().doubleValue()
									* objetoCustoVO.getPesoOc().doubleValue();

							Double vlrPessoal = valorPessoalVo.getQuantidadeFolhasPessoal().doubleValue() / objetoCustoVO.getSomaPesos().doubleValue()
									* objetoCustoVO.getPesoOc().doubleValue();

							//Passo 5
							SigCalculoRateioPessoa calculoRateioPessoa = new SigCalculoRateioPessoa();
							calculoRateioPessoa.setCriadoEm(new Date());
							calculoRateioPessoa.setRapServidores(servidor);
							calculoRateioPessoa.setSigGrupoOcupacoes(grupoOcupacao);
							calculoRateioPessoa.setSigCalculoObjetoCustos(calculoObjetoCusto);
							calculoRateioPessoa.setQtde(ProcessamentoCustoUtils.criarBigDecimal(qtde.doubleValue()).doubleValue());
							calculoRateioPessoa.setVlrPessoal(ProcessamentoCustoUtils.criarBigDecimal(vlrPessoal));
							calculoRateioPessoa.setPeso(objetoCustoVO.getPesoOc());
							this.getProcessamentoCustoUtils().getSigCalculoRateioPessoaDAO().persistir(calculoRateioPessoa);

							//Passo 6
							calculoObjetoCusto.setVlrRateioPessoas(calculoRateioPessoa.getVlrPessoal());
							this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(calculoObjetoCusto);
							
							//Passo 8 e 9
							this.debitarValorRateadoDosObjetosCusto(processamentoAtual, servidor, sigProcessamentoPassos, valorPessoalVo, grupoOcupacao);
						}						
					} else {
						//[FE01]
						ocorreuExcecao = true;
						this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoAtual, servidor,
								sigProcessamentoPassos, DominioEtapaProcessamento.P, "MENSAGEM_ERRO_CENTRO_CUSTO_NAO_POSSUI_OBJETO_CUSTO",
								seqCentroCustoAtual, processamentoAtual.getCompetenciaMesAno());
					}
				}
			}
			retornoValorPessoal.close();
			this.commitProcessamentoCusto();
		}

		//Passo 12
		this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_RATEIO_CUSTO_PESSOAL");

	}

	/**
	 * Método que efetua os passos 08 e 09 da etapa 2
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto				Processamento Atual.
	 * @param rapServidores						Servidor Logado.
	 * @param sigProcessamentoPassos			Passo Atual.	
	 * @param valorPessoalVo					VO do resultado da consulta do Passo 2.
	 * @param grupoOcupacao						Grupo de Ocupação atual.
	 */
	private void debitarValorRateadoDosObjetosCusto(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, ValoresPessoalVO valorPessoalVo, SigGrupoOcupacoes grupoOcupacao) {

		//Passo 8
		SigMvtoContaMensal mvtoContaMensal = new SigMvtoContaMensal();
		mvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
		mvtoContaMensal.setCriadoEm(new Date());
		mvtoContaMensal.setRapServidores(rapServidores);
		mvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAR);
		mvtoContaMensal.setTipoValor(DominioTipoValorConta.DP);
		mvtoContaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade()
				.obterCentroCustoPorChavePrimaria(valorPessoalVo.getSeqCentroCusto()));
		mvtoContaMensal.setSigGrupoOcupacoes(grupoOcupacao);
		mvtoContaMensal.setQtde(valorPessoalVo.getQuantidadeHoras().longValue());
		mvtoContaMensal.setValor(valorPessoalVo.getQuantidadeFolhasPessoal().negate());
		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(mvtoContaMensal);

		//Passo 9
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_RATEIO_GRUPO_OCUPACAO_CUSTO_PESSOAL",
				(grupoOcupacao != null) ? grupoOcupacao.getDescricao() : "");

	}
}
