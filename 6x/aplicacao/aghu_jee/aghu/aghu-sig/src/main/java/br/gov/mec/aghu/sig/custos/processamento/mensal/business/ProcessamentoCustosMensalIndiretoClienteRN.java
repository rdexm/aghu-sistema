package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoCliente;
import br.gov.mec.aghu.model.SigCalculoDirecionador;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.CalculoDirecionadoresVO;
import br.gov.mec.aghu.sig.custos.vo.ClienteObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.CustoIndiretoRatearObjetoCustoApoioVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresIndiretosCustoDiretoVO;

/**
 * Classe responsável pelo processamento dos valores relativos a custos de
 * objetos de custo de apoio são repassados aos seus clientes, para depois serem
 * repassados aos objetos de custo assistenciais. #23092
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalIndiretoClienteRN.class)
public class ProcessamentoCustosMensalIndiretoClienteRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 926721298285886272L;
	private static final Log LOG = LogFactory.getLog(ProcessamentoCustosMensalIndiretoClienteRN.class);

	@Override
	public String getTitulo() {
		return "Processamento dos custos de objetos de custo de apoio entre seus clientes.";
	}

	@Override
	public String getNome() {
		return "processamentoMensalIndiretoClienteON - processamentoRateioIndiretosCliente";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 21;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.executarEtapa1(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		this.executarEtapa2(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		this.executarEtapa3(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		this.repassarIndiretosRecursivo(0, null, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}
	
	private void repassarIndiretosRecursivo(Integer iteracao, Map<String, CustoIndiretoRatearObjetoCustoApoioVO> mapeamentoAnterior, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException{
		
		this.executarEtapa4(iteracao, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		this.executarEtapa5(iteracao, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		this.executarEtapa6(iteracao, mapeamentoAnterior, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Executar a etapa 1, que calcula a distribuição do custo total do objeto de custo entre os direcionadores selecionado para o objeto de custo.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa1(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		//Passo 1		
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_INICIO_RATEIO_OBJETO_CUSTO_DIRECIONADORES");

		//Passo 2
		List<CalculoDirecionadoresVO> lista = this.getProcessamentoCustoUtils().getSigObjetoCustoDirRateiosDAO().buscarDirecionadoresRateioObjetoCusto(sigProcessamentoCusto.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_BUSCA_SUCESSO_RATEIO_OBJETO_CUSTO_DIRECIONADORES");

		Integer cbjSeqAtual = null;
		SigCalculoDirecionador ultimoDirecionador = null;

		BigDecimal vlrAcumInsumos = BigDecimal.ZERO;
		BigDecimal vlrAcumPessoal = BigDecimal.ZERO;
		BigDecimal vlrAcumEquipamentos = BigDecimal.ZERO;
		BigDecimal vlrAcumServicos = BigDecimal.ZERO;

		BigDecimal vlrOrigDirInsumos = BigDecimal.ZERO;
		BigDecimal vlrOrigDirPessoal = BigDecimal.ZERO;
		BigDecimal vlrOrigDirEquipamentos = BigDecimal.ZERO;
		BigDecimal vlrOrigDirServicos = BigDecimal.ZERO;

		
		for (int i = 0; i < lista.size() ; i++) {
			
			CalculoDirecionadoresVO vo = lista.get(i);
			
			if (cbjSeqAtual == null) {
				cbjSeqAtual = vo.getCbjSeq();
			}
	
			if (cbjSeqAtual != null && cbjSeqAtual.intValue() != vo.getCbjSeq().intValue()) {
	
				// Passo 06			
				this.ajustarCalculoObjetoCustoDirecionadorDireto(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, ultimoDirecionador,
						vlrAcumInsumos, vlrAcumPessoal, vlrAcumEquipamentos, vlrAcumServicos, vlrOrigDirInsumos, vlrOrigDirPessoal, vlrOrigDirEquipamentos,
						vlrOrigDirServicos);
	
				vlrAcumInsumos = BigDecimal.ZERO;
				vlrAcumPessoal = BigDecimal.ZERO;
				vlrAcumEquipamentos = BigDecimal.ZERO;
				vlrAcumServicos = BigDecimal.ZERO;
				cbjSeqAtual = vo.getCbjSeq();
			}
	
			//Passo 4
			SigCalculoDirecionador calculoDirecionador = new SigCalculoDirecionador();
	
			calculoDirecionador.setCriadoEm(new Date());
			calculoDirecionador.setServidor(rapServidores);
			calculoDirecionador.setDirecionador(this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(vo.getDirSeq()));
			calculoDirecionador.setCalculoObjetoCusto(this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(vo.getCbjSeq()));
			calculoDirecionador.setPercentual(vo.getPercentual());
	
			calculoDirecionador.setVlrDirInsumos(this.calcularPorcentual(vo.getVlrDirInsumos(), vo.getPercentual()));
			calculoDirecionador.setVlrDirPessoas(this.calcularPorcentual(vo.getVlrDirPessoal(), vo.getPercentual()));
			calculoDirecionador.setVlrDirEquipamentos(this.calcularPorcentual(vo.getVlrDirEquipamentos(), vo.getPercentual()));
			calculoDirecionador.setVlrDirServicos(this.calcularPorcentual(vo.getVlrDirServicos(), vo.getPercentual()));
	
			//Passo 05
			vlrAcumInsumos = vlrAcumInsumos.add(calculoDirecionador.getVlrDirInsumos());
			vlrAcumPessoal = vlrAcumPessoal.add(calculoDirecionador.getVlrDirPessoas());
			vlrAcumEquipamentos = vlrAcumEquipamentos.add(calculoDirecionador.getVlrDirEquipamentos());
			vlrAcumServicos = vlrAcumServicos.add(calculoDirecionador.getVlrDirServicos());
	
			this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().persistir(calculoDirecionador);
			
			vlrOrigDirInsumos = vo.getVlrDirInsumos();
			vlrOrigDirPessoal = vo.getVlrDirPessoal();
			vlrOrigDirEquipamentos = vo.getVlrDirEquipamentos();
			vlrOrigDirServicos = vo.getVlrDirServicos();
			ultimoDirecionador = calculoDirecionador;
			
			//[FE01]
			if(i < lista.size() - 1 ){
				this.ajustarCalculoObjetoCustoDirecionadorDireto(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, ultimoDirecionador,
						vlrAcumInsumos, vlrAcumPessoal, vlrAcumEquipamentos, vlrAcumServicos, vlrOrigDirInsumos, vlrOrigDirPessoal, vlrOrigDirEquipamentos,
						vlrOrigDirServicos);
			}
			this.commitProcessamentoCusto();
		}
		

		//Passo 7
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.N, "MENSAGEM_OBJETO_CUSTO_CALCULADO_RATEIO_DIRECIONADORES");
	}

	/**
	 * Calcula a fatia de uma valor baseado no valor e na taxa informados:   (VALOR / 100) * TAXA
	 * 
	 * @author rmalvezzi
	 * @param valor				Valor principal.
	 * @param taxa				Porcetagem da fatia.
	 * @return					Resultado do calculo.
	 */
	private BigDecimal calcularPorcentual(BigDecimal valor, BigDecimal taxa) {
		return ProcessamentoCustoUtils.dividir(valor, new BigDecimal(100)).multiply(taxa);
	}

	/**
	 * Verifica que sobrou alguma diferença entre os valores (insumos, pessoas, equipamentos, serviços) DIRETOS
	 * do objeto de custo calculado e a soma dos valores rateados entre os seus direcionadores.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @param calculoDirecionador					Ultimo Calculo direcionador.
	 * @param vlrAcumInsumos						Valor Acumulado para Insumos.
	 * @param vlrAcumPessoal						Valor Acumulado para Pessoal.
	 * @param vlrAcumEquipamentos					Valor Acumulado para Equipamentos.
	 * @param vlrAcumServicos						Valor Acumulado para Servicos.
	 * @param vlrOrigDirInsumos						Valor Total para Insumos.
	 * @param vlrOrigDirPessoal						Valor Total para Pessoal.
	 * @param vlrOrigDirEquipamentos				Valor Total para Equipamentos.
	 * @param vlrOrigDirServicos					Valor Total para Servicos.
	 */
	private void ajustarCalculoObjetoCustoDirecionadorDireto(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, SigCalculoDirecionador calculoDirecionador, BigDecimal vlrAcumInsumos, BigDecimal vlrAcumPessoal,
			BigDecimal vlrAcumEquipamentos, BigDecimal vlrAcumServicos, BigDecimal vlrOrigDirInsumos, BigDecimal vlrOrigDirPessoal,
			BigDecimal vlrOrigDirEquipamentos, BigDecimal vlrOrigDirServicos) {
		//[FE01]
		boolean ajuste = false;

		if (vlrOrigDirInsumos != null && !vlrAcumInsumos.equals(BigDecimal.ZERO) && vlrOrigDirInsumos.compareTo(vlrAcumInsumos) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrDirInsumos(calculoDirecionador.getVlrDirInsumos().add(vlrOrigDirInsumos.subtract(vlrAcumInsumos)));
		}

		if (vlrOrigDirPessoal != null && !vlrAcumPessoal.equals(BigDecimal.ZERO) && vlrOrigDirPessoal.compareTo(vlrAcumPessoal) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrDirPessoas(calculoDirecionador.getVlrDirPessoas().add(vlrOrigDirPessoal.subtract(vlrAcumPessoal)));
		}

		if (vlrOrigDirEquipamentos != null && !vlrAcumEquipamentos.equals(BigDecimal.ZERO) && vlrOrigDirEquipamentos.compareTo(vlrAcumEquipamentos) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrDirEquipamentos(calculoDirecionador.getVlrDirEquipamentos().add(vlrOrigDirEquipamentos.subtract(vlrAcumEquipamentos)));
		}

		if (vlrOrigDirServicos != null && !vlrAcumServicos.equals(BigDecimal.ZERO) && vlrOrigDirServicos.compareTo(vlrAcumServicos) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrDirServicos(calculoDirecionador.getVlrDirServicos().add(vlrOrigDirServicos.subtract(vlrAcumServicos)));
		}

		if (ajuste == true) {
			this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().atualizar(calculoDirecionador);
		}
	}

	/**
	 * Executar a etapa 2, que define os clientes para os quais o custo do objeto de custo calculado será rateado.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa2(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_DEFINICAO_CLIENTES_RATEIO_OBJETO");

		//Passo 2
		List<ClienteObjetoCustoVO> lista = this.getProcessamentoCustoUtils().getSigObjetoCustoClientesDAO().buscarClientesRateioObjetoCusto(sigProcessamentoCusto.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MSG_BUSCA_DEFINICAO_CLIENTES_RATEIO_OBJETO_SUCESSO");

		//Passo 4
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {
			for (ClienteObjetoCustoVO vo : lista) {
				// RN01 - Inclusão de clientes dos objetos de custo
				if (vo.getCctCodigo() != null && vo.getCctCodigo().intValue() != 0) {
					this.incluirCalculoCliente(rapServidores, vo);
				}
				else if (vo.getCtoSeq() != null && vo.getCtoSeq().intValue() != 0) {
					this.incluirCalculoClienteCentroProducao(rapServidores, vo);
				}
				else if (vo.getIndTodosCct() != null && vo.getIndTodosCct()) {
					this.incluirCalculoClienteTodosCct(rapServidores, vo);
				}
				this.commitProcessamentoCusto();
			}
		}

		//Passo 5
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.N, "MENSAGEM_DEFINICAO_CLIENTES_RATEIO_OBJETO_FINALIZADO");
	}

	/**
	 * Centro de custo - Cliente do objeto de custo  com a coluna CCT_CODIGO preenchida, então incluir uma linha na tabela SIG_CALCULO_CLIENTES.
	 * 
	 * @author rmalvezzi
	 * @param rapServidores			Servidor logado.
	 * @param vo					Linha representada pelo VO da consulta.
	 */
	private void incluirCalculoCliente(RapServidores rapServidores, ClienteObjetoCustoVO vo) {
		this.criarSigCalculoCliente(rapServidores, vo, this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo()));
	}

	/**
	 * Centro de produção - Cliente do objeto de custo com a coluna CTO_SEQ preenchida, então é preciso obter todos os centros de custo ativos 
	 * (não de obras) associados aquele centro de produção e, para cada centro de custo, incluir uma linha na tabela SIG_CALCULO_CLIENTES.
	 * 
	 * @author rmalvezzi
	 * @param rapServidores			Servidor logado.
	 * @param vo					Linha representada pelo VO da consulta.
	 */
	private void incluirCalculoClienteCentroProducao(RapServidores rapServidores, ClienteObjetoCustoVO vo) {
		List<FccCentroCustos> listCentroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().pesquisarCentroCustosPorCentroProdExcluindoGcc(null, vo.getCtoSeq(), DominioSituacao.A);

		for (FccCentroCustos centroCusto : listCentroCusto) {
			this.criarSigCalculoCliente(rapServidores, vo, centroCusto);
		}
	}

	/**
	 * Todos os centros de custos - Cliente do objeto de custo com a coluna IND_TODOS_CCT preenchida, então é preciso obter todos os 
	 * centros de custo ativos (não de obras) e, para cada centro de custo, incluir uma linha na tabela SIG_CALCULO_CLIENTES.
	 * 
	 * @author rmalvezzi
	 * @param rapServidores			Servidor logado.
	 * @param vo					Linha representada pelo VO da consulta.
	 */
	private void incluirCalculoClienteTodosCct(RapServidores rapServidores, ClienteObjetoCustoVO vo) {
		List<FccCentroCustos> listCentroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().pesquisarCentroCustosPorCentroProdExcluindoGcc(null, null, DominioSituacao.A);

		for (FccCentroCustos centroCusto : listCentroCusto) {
			this.criarSigCalculoCliente(rapServidores, vo, centroCusto);
		}
	}

	/**
	 * Cria um Calculo do cliente com os parametros passados.
	 * 
	 * @author rmalvezzi
	 * @param rapServidores			Servidor logado.
	 * @param vo					Linha representada pelo VO da consulta.
	 * @param centroCusto			Centro de custo ao qual o Calculo do cliente estará relacionado.
	 */
	private void criarSigCalculoCliente(RapServidores rapServidores, ClienteObjetoCustoVO vo, FccCentroCustos centroCusto) {
		SigCalculoCliente calculoCliente = new SigCalculoCliente();
		calculoCliente.setCriadoEm(new Date());
		calculoCliente.setServidor(rapServidores);
		calculoCliente.setDirecionador(this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(vo.getDirSeq()));
		calculoCliente.setCalculoObjetoCusto(this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(vo.getCbjSeq()));
		calculoCliente.setCentroCusto(centroCusto);
		calculoCliente.setValor(new BigDecimal(vo.getValor()));
		this.getProcessamentoCustoUtils().getSigCalculoClienteDAO().persistir(calculoCliente);
	}

	/**
	 * Executar a etapa 3, que rateia os custos diretos dos objetos de custo de apoio aos seus clientes.
	 * 
	 * @author rmalvezzi
	 * @param processamento					Processamento Atual.
	 * @param servidor							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa3(SigProcessamentoCusto processamento, RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(processamento, servidor, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_PROCESSAMENTO_INDIRETO_3_INICIO");

		//Passo 02
		List<ValoresIndiretosCustoDiretoVO> lista = this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().buscarCustosDiretosDosObjetosCustoApoioRatear(processamento.getSeq());

		// Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, servidor, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_PROCESSAMENTO_INDIRETO_3_CONSULTA");

		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {
			
			for (ValoresIndiretosCustoDiretoVO vo : lista) {

				SigDirecionadores sigDirecionadores = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(vo.getDirSeq());
				SigCalculoObjetoCusto cbj = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(vo.getCbjSeq());
				FccCentroCustos fornecedor = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());

				//Passo 4
				List<SigCalculoCliente> clientes = this.getProcessamentoCustoUtils().getSigCalculoClienteDAO().buscarClientesObjetoCustoApoioRatear(cbj, sigDirecionadores);

				//Passo 5
				BigDecimal somaPesos = this.getProcessamentoCustoUtils().getSigCalculoClienteDAO().buscarSomaPesosClienesObjetoCusto(cbj, sigDirecionadores);

				//Passo 6 e 7
				if (clientes != null) {
					for (SigCalculoCliente calculoCliente : clientes) {
						
						FccCentroCustos cliente = calculoCliente.getCentroCusto();
						
						Double parcelaRateioInsumo = vo.getValorInsumos() / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();
						Double parcelaRateioPessoa = vo.getValorPessoas() / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();
						Double parcelaRateioEquipamento = vo.getValorEquipamentos() / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();
						Double parcelaRateioServico = vo.getValorServicos() / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();

						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor, parcelaRateioInsumo, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.II);
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor, parcelaRateioPessoa, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.IP);
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor, parcelaRateioEquipamento, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.IE);
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor, parcelaRateioServico, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.IS);
						this.commitProcessamentoCusto();
					}
				}
			}
		}

		//Passo 8
		this.buscarMensagemEGravarLogProcessamento(processamento, servidor, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_PROCESSAMENTO_INDIRETO_3_FIM");
	}

	/**
	 * Executar a etapa 4, que rateia os custos indiretos recebidos nos objetos de custo de apoio.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa4(Integer iteracao, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		this.processamentoRateioIndiretos(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, true, iteracao);
	}

	/**
	 * Executar a etapa 5, que atualiza a distribuição do custo total do objeto de custo (agora considerando os valores indiretos recebidos) entre os direcionadores selecionados para o objeto de custo.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa5(Integer iteracao, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.N, "MENSAGEM_INICIO_ATUALIZACAO_RATEIO_OBJETO_CUSTO_DIRECIONADORES");

		//Passo 2
		List<SigCalculoDirecionador> listaValoresIndiretos = this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().buscarValoresIndiretosObjetoCustoApoio(sigProcessamentoCusto);

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_SUCESSO_BUSCA_DADOS_DIRECIONADORES_RATEIO_OBJETO_CUSTO");

		BigDecimal vlrAcumInsumos = BigDecimal.ZERO;
		BigDecimal vlrAcumPessoal = BigDecimal.ZERO;
		BigDecimal vlrAcumEquipamentos = BigDecimal.ZERO;
		BigDecimal vlrAcumServicos = BigDecimal.ZERO;

		SigCalculoObjetoCusto cbjAtual = null;
		BigDecimal vlrInsumosAtual = BigDecimal.ZERO;
		BigDecimal vlrPessoalAtual = BigDecimal.ZERO;
		BigDecimal vlrEquipamentosAtual = BigDecimal.ZERO;
		BigDecimal vlrServicosAtual = BigDecimal.ZERO;
		BigDecimal cemBigDecimal = new BigDecimal(100.0);
		
		SigCalculoDirecionador ultimoDirecionador = null;

		if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaValoresIndiretos)){
			
			for (SigCalculoDirecionador calculoDirecionador : listaValoresIndiretos) {
				
				if (cbjAtual == null || cbjAtual.getSeq().intValue() != calculoDirecionador.getCalculoObjetoCusto().getSeq().intValue()) {
	
					if(cbjAtual != null){
						//Passo 6			
						this.ajustarCalculoObjetoCustoDirecionadorIndireto(ultimoDirecionador,
								vlrAcumInsumos, vlrAcumPessoal, vlrAcumEquipamentos, vlrAcumServicos, ultimoDirecionador.getCalculoObjetoCusto().getVlrIndInsumos(),
								ultimoDirecionador.getCalculoObjetoCusto().getVlrIndPessoas(), ultimoDirecionador.getCalculoObjetoCusto().getVlrIndEquipamentos(),
								ultimoDirecionador.getCalculoObjetoCusto().getVlrIndServicos());
					}
	
					vlrAcumInsumos = BigDecimal.ZERO;
					vlrAcumPessoal = BigDecimal.ZERO;
					vlrAcumEquipamentos = BigDecimal.ZERO;
					vlrAcumServicos = BigDecimal.ZERO;
					
					cbjAtual = calculoDirecionador.getCalculoObjetoCusto();
					vlrInsumosAtual = cbjAtual.getVlrIndInsumos().divide(cemBigDecimal);
					vlrPessoalAtual = cbjAtual.getVlrIndPessoas().divide(cemBigDecimal);
					vlrEquipamentosAtual = cbjAtual.getVlrIndEquipamentos().divide(cemBigDecimal);
					vlrServicosAtual = cbjAtual.getVlrIndServicos().divide(cemBigDecimal);
				}
	
				//Passo 4
				calculoDirecionador.setVlrIndInsumos(vlrInsumosAtual.multiply(calculoDirecionador.getPercentual()));
				calculoDirecionador.setVlrIndPessoas(vlrPessoalAtual.multiply(calculoDirecionador.getPercentual()));
				calculoDirecionador.setVlrIndEquipamentos(vlrEquipamentosAtual.multiply(calculoDirecionador.getPercentual()));
				calculoDirecionador.setVlrIndServicos(vlrServicosAtual.multiply(calculoDirecionador.getPercentual()));
				this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().atualizar(calculoDirecionador);
	
				ultimoDirecionador = calculoDirecionador;
	
				//Passo 5
				vlrAcumInsumos = vlrAcumInsumos.add(calculoDirecionador.getVlrIndInsumos());
				vlrAcumPessoal = vlrAcumPessoal.add(calculoDirecionador.getVlrIndPessoas());
				vlrAcumEquipamentos = vlrAcumEquipamentos.add(calculoDirecionador.getVlrIndEquipamentos());
				vlrAcumServicos = vlrAcumServicos.add(calculoDirecionador.getVlrIndServicos());
				
				this.commitProcessamentoCusto();
			}
			//[FE01]
			this.ajustarCalculoObjetoCustoDirecionadorIndireto(ultimoDirecionador,
					vlrAcumInsumos, vlrAcumPessoal, vlrAcumEquipamentos, vlrAcumServicos, ultimoDirecionador.getCalculoObjetoCusto().getVlrIndInsumos(),
					ultimoDirecionador.getCalculoObjetoCusto().getVlrIndPessoas(), ultimoDirecionador.getCalculoObjetoCusto().getVlrIndEquipamentos(),
					ultimoDirecionador.getCalculoObjetoCusto().getVlrIndServicos());
			
			this.commitProcessamentoCusto();
		}

		//Passo 7
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_SUCESSO_CALCULO_CUSTO_OBJETO_CUSTO_DIRECIONADORES");
	}

	/**
	 * Verifica que sobrou alguma diferença entre os valores (insumos, pessoas, equipamentos, serviços) INDIRETOS
	 * do objeto de custo calculado e a soma dos valores rateados entre os seus direcionadores.	
	 * 
	 * @author rmalvezzi
	 * @param processamento					Processamento Atual.
	 * @param servidor							Servidor Logado.
	 * @param passo				Passo Atual.
	 * @param calculoDirecionador					Ultimo Calculo direcionador.
	 * @param vlrAcumInsumos						Valor Acumulado para Insumos.
	 * @param vlrAcumPessoal						Valor Acumulado para Pessoal.
	 * @param vlrAcumEquipamentos					Valor Acumulado para Equipamentos.
	 * @param vlrAcumServicos						Valor Acumulado para Servicos.
	 * @param vlrOrigIndInsumos						Valor Total para Insumos.
	 * @param vlrOrigIndPessoal						Valor Total para Pessoal.				
	 * @param vlrOrigIndEquipamentos				Valor Total para Equipamentos.
	 * @param vlrOrigIndServicos					Valor Total para Servicos.
	 */
	private void ajustarCalculoObjetoCustoDirecionadorIndireto(SigCalculoDirecionador calculoDirecionador, BigDecimal vlrAcumInsumos, BigDecimal vlrAcumPessoal,
			BigDecimal vlrAcumEquipamentos, BigDecimal vlrAcumServicos, BigDecimal vlrOrigIndInsumos, BigDecimal vlrOrigIndPessoal,
			BigDecimal vlrOrigIndEquipamentos, BigDecimal vlrOrigIndServicos) {
		//[FE01]
		boolean ajuste = false;

		if (vlrOrigIndInsumos != null && !vlrAcumInsumos.equals(BigDecimal.ZERO) && vlrOrigIndInsumos.compareTo(vlrAcumInsumos) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrIndInsumos(calculoDirecionador.getVlrIndInsumos().add(vlrOrigIndInsumos.subtract(vlrAcumInsumos)));
		}

		if (vlrOrigIndPessoal != null && !vlrAcumPessoal.equals(BigDecimal.ZERO) && vlrOrigIndPessoal.compareTo(vlrAcumPessoal) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrIndPessoas(calculoDirecionador.getVlrIndPessoas().add(vlrOrigIndPessoal.subtract(vlrAcumPessoal)));
		}

		if (vlrOrigIndEquipamentos != null && !vlrAcumEquipamentos.equals(BigDecimal.ZERO) && vlrOrigIndEquipamentos.compareTo(vlrAcumEquipamentos) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrIndEquipamentos(calculoDirecionador.getVlrIndEquipamentos().add(vlrOrigIndEquipamentos.subtract(vlrAcumEquipamentos)));
		}

		if (vlrOrigIndServicos != null && !vlrAcumServicos.equals(BigDecimal.ZERO) && vlrOrigIndServicos.compareTo(vlrAcumServicos) != 0) {
			ajuste = true;
			calculoDirecionador.setVlrIndServicos(calculoDirecionador.getVlrIndServicos().add(vlrOrigIndServicos.subtract(vlrAcumServicos)));
		}

		if (ajuste == true) {
			this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().atualizar(calculoDirecionador);
		}
	}

	/**
	 * Executar a etapa 6, que rateia os custos indiretos dos objetos de custo de apoio nos seus clientes intermediários ou finalísticos.
	 * 
	 * @author rmalvezzi
	 * @param processamento					Processamento Atual.
	 * @param servidor							Servidor Logado.
	 * @param passo				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void executarEtapa6(Integer iteracao, Map<String, CustoIndiretoRatearObjetoCustoApoioVO> mapeamentoAnterior, SigProcessamentoCusto processamento, RapServidores servidor, SigProcessamentoPassos passo) throws ApplicationBusinessException {
		
		LOG.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOG.debug("++ ITERAÇÃO "+iteracao+"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOG.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		//Só precisa processar se for a primeira iteração (não tem mapeamento anterior), ou se o mapeamento anterior possuir algum valor 
		if(mapeamentoAnterior == null || !mapeamentoAnterior.isEmpty() ){
			
			//Passo 1
			this.buscarMensagemEGravarLogProcessamento(processamento, servidor, passo, DominioEtapaProcessamento.N, "MENSAGEM_INICIO_RATEIO_CUSTOS_INDIRETOS_CLIENTES_INTERMEDIARIOS_FINALISTICOS");
	
			//Passo 2
			List<CustoIndiretoRatearObjetoCustoApoioVO> listaCustoIndireto = this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().buscarCustosIndiretosRatearObjetosCustoApoio(processamento.getSeq());
			
			//Passo 3
			this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, servidor, passo,DominioEtapaProcessamento.N, "MENSAGEM_SUCESSO_BUSCA_DADOS_OBJETO_CUSTO_APOIO_RATEAR");
	
			Map<String, CustoIndiretoRatearObjetoCustoApoioVO> mapeamentoAtual = new HashMap<String, CustoIndiretoRatearObjetoCustoApoioVO>();
			
			Integer proximaIteracao = iteracao + 1;
			
			Boolean executarProximaIteracao = false;
			
			for (CustoIndiretoRatearObjetoCustoApoioVO vo : listaCustoIndireto) {
				
				Double vlrInsumos = vo.getValorIndInsumos();
				Double vlrPessoas = vo.getValorIndPessoas();
				Double vlrEquipamentos = vo.getValorIndEquipamentos();
				Double vlrServicos = vo.getValorIndServicos();
				
				if(mapeamentoAnterior != null){
					
					CustoIndiretoRatearObjetoCustoApoioVO voAnt = null;
					
					//Se possui a tupla no mapeamento anterior
					if(mapeamentoAnterior.containsKey(vo.getChave())){
						voAnt = mapeamentoAnterior.get(vo.getChave()); 
						vlrInsumos = this.calcularValor(vlrInsumos, voAnt.getValorIndInsumos());
						vlrPessoas = this.calcularValor(vlrPessoas, voAnt.getValorIndPessoas());
						vlrEquipamentos = this.calcularValor(vlrEquipamentos, voAnt.getValorIndEquipamentos());
						vlrServicos = this.calcularValor(vlrServicos, voAnt.getValorIndServicos());
					}
				
					//Se não possui na anterior, ou todos os valores estão zerados então não precisa mais distribuir
					if(voAnt == null || (vlrInsumos <= 0.0001 && vlrPessoas <= 0.0001 && vlrEquipamentos <= 0.0001 && vlrServicos <= 0.0001)){
						continue;//Não tem mais valores para repassar
					}
				}
				
				SigCalculoObjetoCusto cbj = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(vo.getCbjSeq());
	
				FccCentroCustos fornecedor = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());
	
				SigDirecionadores direcionador = this.getProcessamentoCustoUtils().getSigDirecionadoresDAO().obterPorChavePrimaria(vo.getDirSeq());
	
				// Passo 04
				List<SigCalculoCliente> clientes = this.getProcessamentoCustoUtils().getSigCalculoClienteDAO().buscarClientesIntermediariosFinalisticosObjetoCustoApoio(cbj, direcionador);
	
				// Passo 05
				BigDecimal somaPesos = this.getProcessamentoCustoUtils().getSigCalculoClienteDAO().buscarSomaPesosClientesIntermediariosFinalisticosObjetoCustoApoio(cbj, direcionador);
	
				if(ProcessamentoCustoUtils.verificarListaNaoVazia(clientes)){
										
					/*if( fornecedor.getCodigo().equals(45608) || fornecedor.getCodigo().equals(45519) ){
						LOG.info("----------------------------------------------------------------------------------------------------");
						LOG.info("FORNECEDOR: "+fornecedor.getCodigoDescricao() + " ("+clientes.size()+" clientes) "+vlrInsumos + " | " + vlrPessoas + " | " + vlrEquipamentos + " | " +vlrServicos);
						LOG.info("----------------------------------------------------------------------------------------------------");
					}*/
					
					// Passo 06
					for (SigCalculoCliente calculoCliente : clientes) {
						
						FccCentroCustos cliente = calculoCliente.getCentroCusto();
						
						Double parcelaRateioInsumos = vlrInsumos / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();
						Double parcelaRateioPessoal = vlrPessoas / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();
						Double parcelaRateioEquipamentos = vlrEquipamentos / somaPesos.doubleValue() * calculoCliente.getValor().doubleValue();
						Double parcelaRateioServicos = vlrServicos  / somaPesos.doubleValue()* calculoCliente.getValor().doubleValue();
						
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor, parcelaRateioInsumos, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.II, proximaIteracao);
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor, parcelaRateioPessoal, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.IP, proximaIteracao);
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor,parcelaRateioEquipamentos, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.IE, proximaIteracao);
						this.criarMvtoContaMensal(servidor, processamento, cbj, cliente, fornecedor,parcelaRateioServicos, DominioTipoMovimentoConta.SIP, DominioTipoValorConta.IS, proximaIteracao);
						
						/*if( (fornecedor.getCodigo().equals(45608) && cliente.getCodigo().equals(45519)) || (fornecedor.getCodigo().equals(45519) && cliente.getCodigo().equals(45608))){
							LOG.info(cliente.getCodigoDescricao()+ " = " + parcelaRateioInsumos + " | " + parcelaRateioPessoal + " | " + parcelaRateioEquipamentos + " | " + parcelaRateioServicos  );
						}*/
					}
					this.commitProcessamentoCusto();
					
					mapeamentoAtual.put(vo.getChave(), vo); //Armazena o valor atual
					executarProximaIteracao = true;// e informa que deve executar a próxima iteração
				}
			}	

			//Passo 8
			this.buscarMensagemEGravarLogProcessamento(processamento, servidor, passo, DominioEtapaProcessamento.N, "MENSAGEM_SUCESSO_CRIACAO_CLIENTES_RATEIO_FINALIZADO");
			
			if(executarProximaIteracao && proximaIteracao < 100 /*Limite de iterações*/ ){
				this.repassarIndiretosRecursivo(proximaIteracao, mapeamentoAtual, processamento, servidor, passo);
			}
		}
	}
	
	private Double calcularValor(Double vlrAtual, Double vlrAntigo){
		return vlrAtual > vlrAntigo ?  vlrAtual - vlrAntigo : 0;
	}
}
