package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
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
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividadePessoaRestricoes;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.business.SigNoEnumOperationException;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.AtividadeAlocacaoExcedenteVO;
import br.gov.mec.aghu.sig.custos.vo.AtividadeValorRealizadoCalculadoVO;
import br.gov.mec.aghu.sig.custos.vo.GrupoOcupacaoAlocacaoExcedenteVO;
import br.gov.mec.aghu.sig.custos.vo.GrupoOcupacaoAtividadeObjetoCustoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Estória de Usuário #14917(Assistencial) e #24806 (Apoio): Processar custo de pessoal nas atividades.
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCustoPessoalRN.class)
public class ProcessamentoCustosMensalCustoPessoalRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -2824063111100573981L;

	@Override
	public String getTitulo() {
		return "Processar custo de pessoal nas atividades.";
	}

	@Override
	public String getNome() {
		return "processamentoCustoPessoalAtividadeRN - processamentoPessoal";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return ProcessamentoCustoUtils.definirPassoUtilizado(tipoObjetoCusto, 11, 14);
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoPessoal(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
	}

	/**
	 * Executa todos os passos do processamento deste etapa
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto					Processamento atual.
	 * @param rapServidores							Servidor logado.
	 * @param sigProcessamentoPassos				Passo atual.
	 * @param tipoObjetoCusto						Tipo do objeto de custo.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void processamentoPessoal(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		if (this.executarEtapa1(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto)) {

			this.executarEtapa2(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);

			this.executarEtapa3(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
		}
	}

	/**
	 * Cálculo do tempo previsto de alocação de pessoal em atividades com tempo específico.
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto Processamento atual.
	 * @param rapServidores Servidor logado.
	 * @param sigProcessamentoPassos Passo atual.
	 * @param tipoObjetoCusto Tipo do objeto de custo.
	 * @throws ApplicationBusinessException Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 * 
	 * @return true se consegui executar a etapa, ou false não obteve dados e não conseguiu executar a etapa
	 */
	private boolean executarEtapa1(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		ScrollableResults retorno = null;
		String chaveMensagemFE01 = null;
		String chaveMensagemPasso5 = null;

		switch (tipoObjetoCusto) {
		case AS:

			//passo 01
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.P, "MENSAGEM_INICIO_PROCESSAMENTO_PESSOAL");

			//passo 02
			retorno = this.getProcessamentoCustoUtils().getSigAtividadePessoasDAO()
					.buscarGruposOcupacaoAlocadosAtividade(sigProcessamentoCusto.getSeq());

			chaveMensagemFE01 = "MENSAGEM_NENHUM_OBJETO_CUSTO_COM_GRUPO_OCUPACAO";
			chaveMensagemPasso5 = "MENSAGEM_SUCESSO_CALCULO_PREVISTA_REALIZADA_VALOR_PESSOAL";
			break;
		case AP:

			//Passo 1
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.P, "MENSAGEM_INICIO_PROCESSAMENTO_PESSOAL_APOIO");

			//Passo 2
			retorno = this.getProcessamentoCustoUtils().getSigAtividadePessoasDAO()
					.buscarGruposOcupacaoAlocadosAtividadeApoio(sigProcessamentoCusto.getSeq());

			chaveMensagemFE01 = "MENSAGEM_NENHUM_OBJETO_CUSTO_COM_GRUPO_OCUPACAO_APOIO";
			chaveMensagemPasso5 = "MENSAGEM_SUCESSO_CALCULO_PREVISTA_REALIZADA_VALOR_PESSOAL_APOIO";
			break;
		default:
			throw new SigNoEnumOperationException();
		}

		//passo 03
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_BUSCA_DADOS_PESSOAL_ALOCADOS");

		boolean retornoNulo = true;

		while (retorno.next()) {

			retornoNulo = false;

			GrupoOcupacaoAtividadeObjetoCustoVO vo = new GrupoOcupacaoAtividadeObjetoCustoVO((Object[]) retorno.get(), tipoObjetoCusto);

			//passo 04
			SigCalculoAtividadePessoa calculoAtividadePessoa = new SigCalculoAtividadePessoa();
			calculoAtividadePessoa.setCriadoEm(new Date());
			calculoAtividadePessoa.setRapServidores(rapServidores);
			calculoAtividadePessoa.setSigCalculoComponentes(this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO()
					.obterPorChavePrimaria(vo.getCmtSeq()));
			calculoAtividadePessoa.setSigAtividades(this.getProcessamentoCustoUtils().getSigAtividadesDAO().obterPorChavePrimaria(vo.getTvdSeq()));
			calculoAtividadePessoa.setSigAtividadePessoas(this.getProcessamentoCustoUtils().getSigAtividadePessoasDAO()
					.obterPorChavePrimaria(vo.getAisSeq()));
			calculoAtividadePessoa
					.setSigGrupoOcupacoes(this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(vo.getGocSeq()));
			calculoAtividadePessoa.setValorGrupoOcupacao(0D);
			calculoAtividadePessoa.setQtdePrevista(this.calcularQtdePrevista(vo, tipoObjetoCusto));//qtdePrevista

			if (calculoAtividadePessoa.getQtdePrevista() == null || calculoAtividadePessoa.getQtdePrevista().equals(Double.valueOf(0))) {
				//FE03 - Grupo de alocação sem quantidade especificada para realizar cálculos, então não deve persistir e passa para o próximo registro
				continue;
			} else {
				//RN02(#14917[Assistencial] e RN01(#24806[Apoio])) 
				this.calcularValorPessoalAtividade(calculoAtividadePessoa, sigProcessamentoCusto, vo);
			}

			this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO().persistir(calculoAtividadePessoa);

		}
		retorno.close();
		this.commitProcessamentoCusto();

		//FE01
		if (retornoNulo) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.P, chaveMensagemFE01);
			//Informa que o caso de uso principal (etapa 0) deve ser encerrado
			return false;
		}

		//passo 05
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.P, chaveMensagemPasso5);

		//Pode prosseguir para a próxima etapa
		return true;
	}

	/**
	 * Cálculo da quantidade prevista de horas de pessoal de uma atividade
	 * 
	 * RN01(#14917[Assistencial] chamada no passo 4
	 * @author rogeriovieira
	 * @param vo grupo de ocupação de atividade alocada em objeto de custo.
	 * @param tipoObjetoCusto tipo do objeto de custo
	 * @return valor que representa a quantidade prevista
	 * @throws ApplicationBusinessException 
	 */
	private Double calcularQtdePrevista(GrupoOcupacaoAtividadeObjetoCustoVO vo, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		Double qtdePrevista = null;

		//O grupo de ocupação foi apenas associado à atividade, sem especificar mais nenhuma informação de tempo
		if (vo.getQtdeTempo() == null || (vo.getQtdeTempo() != null && vo.getQtdeTempo().equals(BigDecimal.ZERO))) {
			return Double.valueOf(0);
		}

		//Tipo do objeto de custo
		switch (tipoObjetoCusto) {

		//Assistencial
		case AS:
			//RN01(#14917)
			SigDirecionadores sigDirecionadores = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO()
					.obterPorChavePrimaria(vo.getDirSeqAtividade());

			//Tipo de operação do direcionador
			switch (sigDirecionadores.getOperacao()) {

			//Se a operação do direcionador for M (multiplica), então:
			case M:
				if (sigDirecionadores.getIndTipoCalculo() != null) {

					//Tipo de cálculo do direcionador
					switch (sigDirecionadores.getIndTipoCalculo()) {

					//Se o tipo de cálculo do direcionador for PR (Produção)
					case PR:
						//QTDE_PREVISTA = QTDE_PROFISSIONAIS * QTDE_TEMPO * QTDE
						qtdePrevista = vo.getQtdeProfissionais().doubleValue() * vo.getQtdeTempo().doubleValue() * vo.getQtde().doubleValue();
						break;

					//Se o tipo de cálculo do direcionador for DP (Dias Produtivos)
					case DP:
						//QTDE_PREVISTA = QTDE_PROFISSIONAIS * QTDE_TEMPO * NRO_DIAS_PRODUCAO
						qtdePrevista = vo.getQtdeProfissionais().doubleValue() * vo.getQtdeTempo().doubleValue() * vo.getNroDiasProducao().doubleValue();
						break;

					//Se o tipo de cálculo do direcionador for PP (Período Processamento)
					case PP:
						//this.getProcessamentoCustoUtils().calcularEmDiasDiferencaEntreDatas( vo.getProcDataInicio(), vo.getProcDataFim())
						long dtInicio = vo.getProcDataInicio().getTime();
						long dtFim = vo.getProcDataFim().getTime();
						long difDt = dtFim - dtInicio;
						long diasDif = (difDt / 86400000);

						//QTDE_PREVISTA = (QTDE_PROFISSIONAIS * QTDE_TEMPO) * (PROC_DATA_FIM – PROC_DATA_INICIO)+1
						qtdePrevista = ((vo.getQtdeProfissionais().doubleValue() * vo.getQtdeTempo().doubleValue()) * diasDif) + 1;
						break;
					default:
						throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_ENUM_NAO_UTILIZADO);
					}

					//Se o tipo de cálculo não for nulo, ou seja, realizou algum dos cálculos acima e houver número de execuções informado na atividade,
					if (vo.getNroExecucoes() != null && vo.getNroExecucoes().intValue() != 0) {

						//QTDE_PREVISTA = QTDE_ PREVISTA * NRO_EXECUCOES
						qtdePrevista = qtdePrevista * vo.getNroExecucoes().doubleValue();
					}
				}
				//Se o tipo de cálculo do direcionador for nulo e houver número de execuções informado na atividade
				else if (vo.getNroExecucoes() != null && !vo.getNroExecucoes().equals(Integer.valueOf(0))) {

					//QTDE_PREVISTA = QTDE_PROFISSIONAIS * QTDE_TEMPO * NRO_EXECUCOES 
					qtdePrevista = vo.getQtdeProfissionais().doubleValue() * vo.getQtdeTempo().doubleValue() * vo.getNroExecucoes().doubleValue();
				}
				break;
			//Se a operação do direcionador for D (divide – rateia), então:
			case D:
				//QTDE_PREVISTA = QTDE_PROFISSIONAIS * QTDE_TEMPO
				qtdePrevista = vo.getQtdeProfissionais().doubleValue() * vo.getQtdeTempo().doubleValue();
				break;
			default:
				throw new SigNoEnumOperationException();
			}
			break;
		case AP:
			qtdePrevista = vo.getQtdeTempo().doubleValue() * vo.getQtdeProfissionais().doubleValue();
			break;
		default:
			throw new SigNoEnumOperationException();
		}

		return qtdePrevista;
	}

	/**
	 * Cálculo do valor de pessoal da atividade 
	 * RN02(#14917[Assistencial] e RN01(#24806[Apoio])) chamadas no passo 4
	 * 
	 * @author rogeriovieira
	 * @param sigCalculoAtividadePessoa calculo da atividade de pessoal.
	 * @param sigProcessamentoCusto processamento atual.
	 * @param vo grupo de ocupação de atividade alocada em objeto de custo.
	 */
	private void calcularValorPessoalAtividade(SigCalculoAtividadePessoa sigCalculoAtividadePessoa, SigProcessamentoCusto sigProcessamentoCusto,
			GrupoOcupacaoAtividadeObjetoCustoVO vo) {

		BigDecimal vlrMedioHora = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarDetalheFolhaPessoal(sigProcessamentoCusto.getSeq(), vo.getCctCodigo(), vo.getGocSeq());

		if (vlrMedioHora == null) {

			sigCalculoAtividadePessoa.setQtdeRealizada(Double.valueOf(0));
			sigCalculoAtividadePessoa.setValorGrupoOcupacao(Double.valueOf(0));
		} else {
			sigCalculoAtividadePessoa.setQtdeRealizada(sigCalculoAtividadePessoa.getQtdePrevista());
			sigCalculoAtividadePessoa.setValorGrupoOcupacao(sigCalculoAtividadePessoa.getQtdePrevista() * vlrMedioHora.doubleValue());
		}
	}

	/**
	 * Verifica se as quantidades previstas calculadas de pessoas, alocadas em atividades, excedem o disponível no centro de custo.
	 * 
	 * @author rogeriovieira
	 * @param processamentoCusto Processamento atual.
	 * @param servidor Servidor logado.
	 * @param passo Passo atual.
	 * @param tipoObjetoCusto Tipo do objeto de custo.
	 * @throws ApplicationBusinessException Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa2(SigProcessamentoCusto processamentoCusto, RapServidores servidor, SigProcessamentoPassos passo,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		String chaveMensagemPasso3 = null;
		String chaveMensagemPasso6 = null;
		String chaveMensagemFE02 = null;
		String chaveMensagemPasso8 = null;

		//passo 01	
		switch (tipoObjetoCusto) {
		case AS:
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, passo, DominioEtapaProcessamento.P,
					"MENSAGEM_VERIFICA_EXCEDENTE_VALORES_PESSOAL");

			chaveMensagemPasso3 = "MENSAGEM_SUCESSO_BUSCA_DADOS_PESSOAL_EXCEDENTE";
			chaveMensagemPasso6 = "MENSAGEM_SUCESSO_AJUSTE_DADOS_GRUPO_OCUPACAO";
			chaveMensagemFE02 = "MENSAGEM_NENHUM_GRUPO_OCUPACAO_ALOCACAO_EXCEDENTE";
			chaveMensagemPasso8 = "MENSAGEM_SUCESSO_AJUSTE_VALORES_GRUPOS_OCUPACAO";
			break;
		case AP:
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, passo, DominioEtapaProcessamento.P,
					"MENSAGEM_VERIFICA_EXCEDENTE_VALORES_PESSOAL_APOIO");

			chaveMensagemPasso3 = "MENSAGEM_SUCESSO_BUSCA_DADOS_PESSOAL_EXCEDENTE_APOIO";
			chaveMensagemPasso6 = "MENSAGEM_SUCESSO_AJUSTE_DADOS_GRUPO_OCUPACAO_APOIO";
			chaveMensagemFE02 = "MENSAGEM_NENHUM_GRUPO_OCUPACAO_ALOCACAO_EXCEDENTE_APOIO";
			chaveMensagemPasso8 = "MENSAGEM_SUCESSO_AJUSTE_VALORES_GRUPOS_OCUPACAO_APOIO";
			break;
		default:
			throw new SigNoEnumOperationException();
		}

		//passo 02
		//busca todos os grupos de ocupacao de um centro de custo cujos valores previstos de alocação de pessoas nas atividades excederam a quantidade
		//disponível na competência do processamento
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO()
				.buscarGruposOcupacaoAlocacaoExcedente(processamentoCusto.getSeq(), tipoObjetoCusto);

		//passo 03
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoCusto, servidor, passo,
				DominioEtapaProcessamento.P, chaveMensagemPasso3);

		boolean retornoNulo = true;
		while (retorno.next()) {
			retornoNulo = false;

			GrupoOcupacaoAlocacaoExcedenteVO vo = new GrupoOcupacaoAlocacaoExcedenteVO((Object[]) retorno.get());

			SigGrupoOcupacoes grupoOcupacao = this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(vo.getGocSeq());

			//passo 04
			ScrollableResults retornoAtivAlocExcedente = this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO()
					.buscarAtividadesAlocacaoExcedente(processamentoCusto.getSeq(), vo.getGocSeq(), vo.getCctCodigo(), tipoObjetoCusto);

			while (retornoAtivAlocExcedente.next()) {
				//passo 05
				AtividadeAlocacaoExcedenteVO atividadeVO = new AtividadeAlocacaoExcedenteVO((Object[]) retornoAtivAlocExcedente.get());

				Double qtdeRealizada = (atividadeVO.getQtdePrevistaAtividade() / vo.getQtdePrevista()) * vo.getQtdeDisponivel();
				Double vlrGrupoOcupacao = (atividadeVO.getVlrGrupoOcupacao() / atividadeVO.getQtdePrevistaAtividade()) * qtdeRealizada;

				SigCalculoAtividadePessoa calculoAtividadePessoaAtualizar = this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO()
						.obterPorChavePrimaria(atividadeVO.getCapSeq());
				calculoAtividadePessoaAtualizar.setQtdeRealizada(qtdeRealizada);
				calculoAtividadePessoaAtualizar.setValorGrupoOcupacao(vlrGrupoOcupacao);
				this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO().atualizar(calculoAtividadePessoaAtualizar);
			}
			retornoAtivAlocExcedente.close();

			//passo 06
			this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoCusto, servidor, passo,
					DominioEtapaProcessamento.P, chaveMensagemPasso6, grupoOcupacao.getDescricao());

		}
		retorno.close();
		this.commitProcessamentoCusto();

		//FE02
		if (retornoNulo) {
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, passo, DominioEtapaProcessamento.P,
					chaveMensagemFE02);
		} else {
			//passo 08
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, passo, DominioEtapaProcessamento.P,
					chaveMensagemPasso8);
		}
	}

	/**
	 * Realiza os débitos dos movimentos de pessoal e atualiza os dados nas tabelas de cálculo dos componentes e de cálculo de objeto de custos.
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto Processamento atual.
	 * @param rapServidores Servidor logado.
	 * @param sigProcessamentoPassos Passo atual.
	 * @param tipoObjetoCusto Tipo do objeto de custo.
	 * @throws ApplicationBusinessException Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa3(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		//passo 01
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO()
				.buscarAtividadesComValoresRealizadosCalculados(sigProcessamentoCusto.getSeq(), tipoObjetoCusto);

		Integer pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().intValue();
		
		BigDecimal cem = BigDecimal.valueOf(100.0);

		BigDecimal valorGrupoOcupacaoAcumulado = BigDecimal.ZERO;
		
		while (retorno.next()) {

			AtividadeValorRealizadoCalculadoVO vo = new AtividadeValorRealizadoCalculadoVO((Object[]) retorno.get());

			BigDecimal valorGrupoOcupacao = new BigDecimal(vo.getVlrGrupoOcupacao());

			//passo 02
			SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();
			sigMvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
			sigMvtoContaMensal.setCriadoEm(new Date());
			sigMvtoContaMensal.setRapServidores(rapServidores);
			sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAA);
			sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DP);
			sigMvtoContaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade()
					.obterCentroCustoPorChavePrimaria(vo.getCctCodigo()));
			sigMvtoContaMensal.setSigGrupoOcupacoes(this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(vo.getGocSeq()));
			sigMvtoContaMensal.setQtde(vo.getQtdeRealizada().longValue());
			sigMvtoContaMensal.setValor(valorGrupoOcupacao.negate());
			this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);

			if(tipoObjetoCusto == DominioTipoObjetoCusto.AS){
				
				BigDecimal valorAtvPessoal = BigDecimal.ZERO;
				//RN03 [#14917]
				List<SigAtividadePessoaRestricoes> restricoes = this.getProcessamentoCustoUtils().getSigAtividadePessoaRestricoesDAO().buscarRestricoesAtividade(vo.getTvdSeq(), vo.getGocSeq());
				
				if(restricoes.isEmpty()){
					//1.Se não existir registro, prosseguir o processamento normalmente;
					valorAtvPessoal = valorGrupoOcupacao;
				}
				else {
					//2.Se existir um registro, comparar o PGD_SEQ retornado da consulta 8.7 com o da consulta 8.6:
					for (SigAtividadePessoaRestricoes restricao : restricoes) {
						if(restricao.getPagador().getSeq().equals(vo.getPgdSeq())){
							//b.Se for o mesmo, aplicar o valor percentual: CBJ.VLR_ATV_PESSOAL=CAP.VLR_GRP_OCUPACAO * AVPR.PERCENTUAL / 100 e armazenar o valor residual, se existir, em memória;
							valorAtvPessoal = valorGrupoOcupacao.multiply(restricao.getPercentual().divide(cem));	
						}
					}
					
					//a.Se não for o mesmo, zerar CBJ.VLR_ATV_PESSOAL e armazenar o valor do CAP.VLR_GRP_OCUPACAO em memória;
					valorGrupoOcupacaoAcumulado = valorGrupoOcupacaoAcumulado.add(valorGrupoOcupacao.subtract(valorAtvPessoal));
				}
				
				//c.O resultado armazenado em memória deverá ser somado ao registro cujo pagador seja SUS < parâmetro P_CONVENIO_SUS >.
				if(valorGrupoOcupacaoAcumulado.compareTo(BigDecimal.ZERO) == 1 && vo.getPgdSeq().equals(pConvenioSus)){
					valorAtvPessoal = valorAtvPessoal.add(valorGrupoOcupacaoAcumulado);
					valorGrupoOcupacaoAcumulado = BigDecimal.ZERO;
				} 
				
				valorGrupoOcupacao = valorAtvPessoal;
			}
			
			//passo 03
			SigCalculoComponente calculoComponente = this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().obterPorChavePrimaria(vo.getCmtSeq());
			calculoComponente.setVlrPessoal(calculoComponente.getVlrPessoal().add(valorGrupoOcupacao));
			this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(calculoComponente);

			//passo 04
			SigCalculoObjetoCusto calculoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
					.obterPorChavePrimaria(vo.getCbjSeq());
			calculoObjetoCusto.setVlrAtvPessoal(calculoObjetoCusto.getVlrAtvPessoal().add(valorGrupoOcupacao));
			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(calculoObjetoCusto);

			//passo 05
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_SUMARIZACAO_DADOS_PESSOAL", vo.getNomeAtividade());
		}
		retorno.close();
		this.commitProcessamentoCusto();

		//passo 07
		switch (tipoObjetoCusto) {
		case AS:
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_CALCULO_DADOS_PESSOAL_ATIVIDADE");
			break;
		case AP:
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.P, "MENSAGEM_SUCESSO_CALCULO_DADOS_PESSOAL_ATIVIDADE_APOIO");
			break;
		default:
			throw new SigNoEnumOperationException();
		}
	}
}
