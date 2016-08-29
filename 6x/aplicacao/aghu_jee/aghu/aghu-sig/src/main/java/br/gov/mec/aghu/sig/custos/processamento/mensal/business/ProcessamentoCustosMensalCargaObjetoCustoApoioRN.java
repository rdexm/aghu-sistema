package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSigTipoAlerta;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculo;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoAlertas;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ColetaViaSistemaVO;

/**
 * 
 * Estória do usuario #24673 Classe responsavel pela criação objetos de custos
 * de apoio serão criados no processamento mensal do módulo de custos.
 * 
 * 
 * Estoria usuario #23089 A coleta dos direcionadores será chamada mensalmente,
 * no momento da criação dos objetos de custo de apoio na base de cálculo.
 * 
 * 
 * @author jgugel
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCargaObjetoCustoApoioRN.class)
public class ProcessamentoCustosMensalCargaObjetoCustoApoioRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719785L;

	@Override
	public String getTitulo() {
		return "Criacao dos objetos de custo Apoio no mes de processamento.";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustoMensalCargaObjetoCustoApoioON - processamentoInsumosEtapa12";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 12;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto)
			throws ApplicationBusinessException {

		// Passo 01
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_INICIO_CARGA_OBJETO_CUSTO_APOIO");


		// Passo 2 - #23089 Realizar coleta de direcionadores para produtos de
		// apoio
		this.realizarColetaDirecionadoresProdutosApoio(sigProcessamentoCusto,rapServidores, sigProcessamentoPassos);

		// Passo 3
		ScrollableResults objetosCusto = this.getProcessamentoCustoUtils().getSigObjetoCustoCctsDAO().buscarObjetosCustoApoioCarga();

		// Passo 4
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_CARGA_OBJETO_CUSTO_APOIO_SUCESSO");

		//Passo 5
		List<SigObjetoCustoClientes> buscarClientesRateioObjetoCustoSemValor = this.getProcessamentoCustoUtils().getSigObjetoCustoCctsDAO().buscarClientesRateioObjetoCustoSemValor();
		
		//[FE01]
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(buscarClientesRateioObjetoCustoSemValor)) {		
			objetosCusto.close();
			for (SigObjetoCustoClientes sigObjetoCustoClientes : buscarClientesRateioObjetoCustoSemValor) {
				this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,
						sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_CLIENTE_SEM_VALOR",
						sigObjetoCustoClientes.getCentroCusto().getNomeReduzido(),
						sigObjetoCustoClientes.getObjetoCustoVersoes().getSigObjetoCustos().getNome());
			}
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_CLIENTE_SEM_VALOR);
		}
		
		// Passo 6 (internamente são realizados os passos 6 e 7 e 8)
		this.inserirCalculoObjetoCusto(objetosCusto, sigProcessamentoCusto,
				rapServidores);

		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_CARGA_OBJETO_CUSTO_APOIO_SUCESSO_CALCULO");
	}

	/**
	 * Insere os calculos do objeto de custo vinculando com o mesmo.
	 * 
	 * @param objetosCusto
	 *            - objeto de custo que vai receber a carga
	 * @param sigProcessamentoCusto
	 *            - processamento mensal competente.
	 * @param rapServidores
	 *            servidor responsável.
	 * @param sigProcessamentoPassos
	 *            passo do processamento mensal.
	 * @throws ApplicationBusinessException
	 *             - exeção lançada caso erro ocorra.
	 * @author jgugel
	 */
	private void inserirCalculoObjetoCusto(ScrollableResults objetosCusto, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores) throws ApplicationBusinessException {

		while (objetosCusto.next()) {

			Object[] retorno = objetosCusto.get();

			SigCalculoObjetoCusto calculoObjetoCusto = new SigCalculoObjetoCusto();

			SigObjetoCustoVersoes objCustoVersao = ((SigObjetoCustoCcts) retorno[0]).getSigObjetoCustoVersoes();
			//Passo 7
			if (objCustoVersao.getIndSituacao() == DominioSituacaoVersoesCustos.P) {
				objCustoVersao.setIndSituacao(DominioSituacaoVersoesCustos.A);
			}

			calculoObjetoCusto.setSigProcessamentoCustos(sigProcessamentoCusto);
			calculoObjetoCusto.setSigObjetoCustoVersoes(objCustoVersao);
			calculoObjetoCusto.setFccCentroCustos(((SigObjetoCustoCcts) retorno[0]).getFccCentroCustos());
			calculoObjetoCusto.setQtdeProduzida(BigDecimal.ZERO);
			calculoObjetoCusto.setIndComposicao(true);
			calculoObjetoCusto.setRapServidores(rapServidores);
			calculoObjetoCusto.setCriadoEm(new Date());
			calculoObjetoCusto.setTipoCalculo(DominioTipoCalculo.CM);

			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().persistir(calculoObjetoCusto);

			//Passo 8
			this.gravarCalculoComponentes(sigProcessamentoCusto,calculoObjetoCusto, objCustoVersao, rapServidores);
		}

		objetosCusto.close();
		this.commitProcessamentoCusto();
	}

	/**
	 * Grava os componentes do calculo do objeto de custo
	 * 
	 * @param sigProcessamentoCusto - processamento mensal competente.
	 * @param calculoObjetoCusto - calculo para vincular aos componentes
	 * @param objCustoVersao - objeto de custo para vicular ao calculo
	 * @param rapServidores servidor responsável.
	 * @throws ApplicationBusinessException - exeção lançada caso erro ocorra.
	 * @author jgugel
	 */
	private void gravarCalculoComponentes(SigProcessamentoCusto sigProcessamentoCusto,SigCalculoObjetoCusto calculoObjetoCusto, SigObjetoCustoVersoes objCustoVersao, RapServidores rapServidores) throws ApplicationBusinessException {

		List<SigObjetoCustoComposicoes> listComposicoes = this.getProcessamentoCustoUtils().getSigObjetoCustoComposicoesDAO()
				.pesquisarObjetoCustoComposicaoAtivo(objCustoVersao);

		if (listComposicoes != null && !listComposicoes.isEmpty()&& listComposicoes.size() > 0) {

			for (SigObjetoCustoComposicoes composicao : listComposicoes) {

				SigCalculoComponente calculoComponente = new SigCalculoComponente();
				calculoComponente.setSigProcessamentoCustos(sigProcessamentoCusto);
				calculoComponente.setSigCalculoObjetoCustosByCbjSeq(calculoObjetoCusto);
				calculoComponente.setSigObjetoCustoComposicoes(composicao);
				calculoComponente.setSigDirecionadores(composicao.getSigDirecionadores());
				calculoComponente.setRapServidores(rapServidores);
				calculoComponente.setCriadoEm(new Date());
				this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().persistir(calculoComponente);
			}
		} else {
			// FE03
			calculoObjetoCusto.setIndComposicao(false);
			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(calculoObjetoCusto);
		}
	}


	/**
	 * Efetua chamada das coletas
	 * 
	 * @param sigProcessamentoCusto
	 *            - processamento mensal competente.
	 * @param rapServidores
	 *            servidor responsável.
	 * @param sigProcessamentoPassos
	 *            passo do processamento mensal.
	 * @throws ApplicationBusinessException
	 *             - exeção lançada caso erro ocorra.
	 * @author jgugel
	 */
	private void realizarColetaDirecionadoresProdutosApoio(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {		

		//Buscar todos os direcionadores com o indicador de coleta via sistema = 'S'
		List<SigDirecionadores> direcionadoresColetaViaSistema = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().buscarDirecionadoresColetaViaSistema();
		
		for (SigDirecionadores direcionador : direcionadoresColetaViaSistema) {
			this.coletar(direcionador, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		}
	}


	/**
	 * Método responsável por realizar a coleta de valores de um determinado
	 * direcionador
	 * 
	 * @author rogeriovieira
	 * @param parametro
	 *            indica qual o o direcionador está sendo utilizado
	 * @param direcionador
	 *            direcionador que já foi obtido anteriormente para o parâmetro
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @param rapServidores
	 *            servidor responsável
	 * @param sigProcessamentoPassos
	 *            passo do processamento mensal
	 * @throws ApplicationBusinessException
	 *             exeção lançada caso erro ocorra
	 */
	private void coletar( SigDirecionadores direcionador, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos)throws ApplicationBusinessException {

		// Passo 0 que corresponde a inserir os clientes dos registros que
		// utilizam o indicador de todos os centros de custo
		this.associarCentrosCustoClientes(direcionador, rapServidores);

		//Busca os valores agrupados na view do direcionador
		List<ColetaViaSistemaVO> lista = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().buscarValoresViewDirecionador(direcionador, sigProcessamentoCusto);
		
		//Transforma a lista para um map dos centro de custo
		Map<Integer, BigDecimal> valores = new HashMap<Integer, BigDecimal>();
		for (ColetaViaSistemaVO vo : lista) {
			if(!valores.containsKey(vo.getCctCodigo())){
				valores.put(vo.getCctCodigo(), vo.getQtde());
			}
		}
		 
		// Grava no log
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto,rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_COLETA_DIRECIONADOR", direcionador.getNome());

		// Busca os clientes de acordo com o direcionador
		List<SigObjetoCustoClientes> clientes = this.getProcessamentoCustoUtils().getSigObjetoCustoClientesDAO().buscarClientesAtivosPorDirecionador(direcionador);

		// Mapemento que armazenará os valores já buscados de centros de custo
		
		List<Integer> centrosCustoComAlertaJaCadastrado = new ArrayList<Integer>();
		BigDecimal valor = null;

		for (SigObjetoCustoClientes cliente : clientes) {

			// FE02
			valor = this.calcularValor(direcionador, cliente, valores, centrosCustoComAlertaJaCadastrado, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

			// Se for um cliente de centro de produção, não precisa fazer essa
			// verificação, já que os valores foram verificados na FE02
			if (cliente.getCentroCusto() != null) {
				// FEO3
				if (this.verificarValorColetado(valor, direcionador, cliente.getCentroCusto(), cliente.getObjetoCustoVersoes(), centrosCustoComAlertaJaCadastrado, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos)) {
					continue;
				}
			}

			// Define o valor do cliente
			cliente.setValor(valor);
			this.getProcessamentoCustoUtils().getSigObjetoCustoClientesDAO().atualizar(cliente);
		}

		this.commitProcessamentoCusto();
	}
	
	/**
	 * Método que corresponde a FE01 executada em todas as coletas, mas será
	 * atualizada no documento para
	 * 
	 * @author rogeriovieira
	 * @param direcionadores
	 *            direcionadores que os clientes devem estar ligados
	 * @param rapServidores
	 *            servidor responsável
	 * @throws ApplicationBusinessException
	 *             exeção lançada caso erro ocorra
	 */
	private void associarCentrosCustoClientes(
			SigDirecionadores direcionador, RapServidores rapServidores)
			throws ApplicationBusinessException {

		List<SigObjetoCustoClientes> listaObjetoCustoClientes = this.getProcessamentoCustoUtils().getSigObjetoCustoClientesDAO()
				.buscarClientesAtivosComIndicadorTodosCentrosCustoPorDirecionadores(direcionador);

		for (SigObjetoCustoClientes objetoCustoCliente : listaObjetoCustoClientes) {
			// Re-utiliza o método criado para a estória de manter peso dos
			// clientes
			this.getProcessamentoCustoUtils().getCustosSigCadastrosBasicosFacade()
					.associarCentrosCustoClientes(objetoCustoCliente, rapServidores);
		}
		this.commitProcessamentoCusto();
	}

	/**
	 * Metódo que corresponde a execução da FE02 executada quando o cliente for
	 * um centro de produção
	 * 
	 * @author rogeriovieira
	 * @param parametro
	 *            indica qual o o direcionador está sendo utilizado
	 * @param cliente
	 *            cliente com o direcionador informado
	 * @param valores
	 *            estrutura que armazenará os valores que já foram buscados do
	 *            banco
	 * @return valor calculado na coleta
	 * @throws ApplicationBusinessException
	 *             exeção lançada caso erro ocorra
	 */
	private BigDecimal calcularValor(SigDirecionadores direcionador, SigObjetoCustoClientes cliente, Map<Integer, BigDecimal> valores, List<Integer> centrosCustoComAlertaJaCadastrado, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		// Se não for um centro de produção, então vai retornar diretamente o
		// valor
		if (cliente.getCentroProducao() == null) {
			return valores.get(cliente.getCentroCusto().getCodigo());
		}
		// Caso contrário deverá executar a FE02
		else {
			// Passo 1
			List<FccCentroCustos> listaCentrosCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade()
					.pesquisarCentroCustosPorCentroProdExcluindoGcc(null, cliente.getCentroProducao().getSeq(), DominioSituacao.A);

			BigDecimal somatorio = BigDecimal.ZERO;
			BigDecimal valor = null;
			// Passo 2
			for (FccCentroCustos centroCusto : listaCentrosCusto) {

				valor = valores.get(centroCusto.getCodigo());

				// FEO3
				if (this.verificarValorColetado(valor, direcionador, centroCusto, cliente.getObjetoCustoVersoes(), centrosCustoComAlertaJaCadastrado, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos)) {
					continue;
				}

				somatorio = somatorio.add(valor);
			}
			// Passo 3 e 4
			return somatorio;
		}
	}

	/**
	 * Método que corresponde a FE03 executada quando o centro de custo não
	 * estiver com o peso informado
	 * 
	 * @author rogeriovieira
	 * @param valor
	 *            valor calculado para o cliente
	 * @param parametro
	 *            indica qual o direcionador que está sendo coletado
	 * @param objetoCustoVersao
	 *            versão do objeto de custo que o cliente está ligado
	 * @param centroCusto
	 *            representa o cliente de um objeto de custo
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @param rapServidores
	 *            servidor do processamento
	 * @param sigProcessamentoPassos
	 *            passo atual
	 * @return verdadeiro se o valor for nulo, ou zero, e falso se for um valor
	 *         válido
	 * @throws ApplicationBusinessException
	 *             dispara se ocorrer um erro no commit
	 */
	public boolean verificarValorColetado(BigDecimal valor,SigDirecionadores direcionador, FccCentroCustos centroCusto, SigObjetoCustoVersoes objetoCustoVersao,
			List<Integer> centrosCustoComAlertaJaCadastrado, SigProcessamentoCusto sigProcessamentoCusto,RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		// Passo 1
		if (valor == null || (valor != null && valor.equals(BigDecimal.ZERO))) {

			if (!centrosCustoComAlertaJaCadastrado.contains(centroCusto
					.getCodigo())) {

				// Passo 2
				this.gerarAlertaCP(sigProcessamentoCusto, centroCusto, rapServidores);

				// Passo 3
				this.buscarMensagemEGravarLogProcessamentoSemCommit(
								sigProcessamentoCusto,
								rapServidores,
								sigProcessamentoPassos,
								DominioEtapaProcessamento.G,
								"MENSAGEM_SEM_COLETA_DIRECIONADOR",
								centroCusto.getDescricao(),
								objetoCustoVersao.getSigObjetoCustos().getNome(), direcionador.getNome());

				// Passo 4
				centrosCustoComAlertaJaCadastrado.add(centroCusto.getCodigo());
			}
			return true;
		}
		return false;
	}
	
	/**
	 * CP: cliente sem peso, ou seja, quando um centro de custo que é cliente de
	 * um objeto de custo de apoio, não possuir peso/valor para receber rateio.
	 * Neste caso o cliente não será considerado no rateio do objeto de custo.
	 * Esta situação é específica para clientes com direcionadores coletados via
	 * sistema (m2, número de micros e outros). Este alerta será utilizado na
	 * estória #23089 - Coleta de direcionadores para produtos de apoio. Este
	 * alerta deverá ser apresentado para o usuário que realizou o cadastro do
	 * cliente sem peso no objeto de custo apoio.
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto
	 *            processamento atual
	 * @param centroCusto
	 *            centro de custo com cliente sem peso
	 * @param rapServidores
	 *            servidor logado
	 */
	public void gerarAlertaCP(SigProcessamentoCusto sigProcessamentoCusto, FccCentroCustos centroCusto, RapServidores rapServidores) {

		SigProcessamentoAlertas alerta = new SigProcessamentoAlertas();

		alerta.setTipoAlerta(DominioSigTipoAlerta.CP);
		alerta.setSigProcessamentoCustos(sigProcessamentoCusto);
		alerta.setFccCentroCustos(centroCusto);
		alerta.setQtdeOcorrencias(1);
		alerta.setCriadoEm(new Date());
		alerta.setRapServidores(rapServidores);

		this.getProcessamentoCustoUtils().getSigProcessamentoAlertasDAO().persistir(alerta);
	}
}
