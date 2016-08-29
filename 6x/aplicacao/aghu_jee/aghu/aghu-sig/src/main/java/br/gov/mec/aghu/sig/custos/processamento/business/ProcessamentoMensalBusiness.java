package br.gov.mec.aghu.sig.custos.processamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdProcedimentos;
import br.gov.mec.aghu.model.SigCalculoIndiretoEquipamento;
import br.gov.mec.aghu.model.SigCalculoIndiretoInsumo;
import br.gov.mec.aghu.model.SigCalculoIndiretoPessoa;
import br.gov.mec.aghu.model.SigCalculoIndiretoServico;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.vo.CidFaturamentoVO;
import br.gov.mec.aghu.sig.custos.vo.ProcedimentoConvenioVO;
import br.gov.mec.aghu.sig.custos.vo.ProcedimentoSusVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresIndiretosPesosRateioVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresIndiretosVO;

@TransactionManagement(TransactionManagementType.BEAN)
public abstract class ProcessamentoMensalBusiness extends ProcessamentoCustoBusiness {

	private static final long serialVersionUID = 1796794283526044782L;
	private static final Log LOG = LogFactory.getLog(ProcessamentoMensalBusiness.class);
	
		
	/**
	 * O método principal da etapa, o responsavel em realizar o que a etapa em si precisa fazer.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor logado.
	 * @param sigProcessamentoPassos				Representação do Passo atual.
	 * @param tipoObjetoCusto						Tipo do objeto de custo, Assitencial ou Apoio.
	 * @throws AGHUNegocioExceptionSemRollback		Exceção lançada se algum erro acontecer na hora do commit do processamento. 
	 */
	public void executarProcessamento(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException{
		try{
			this.iniciarProcessamentoCusto();
			this.gravarLogDebugProcessamento("Início "+this.getClass().getSimpleName(), sigProcessamentoCusto, rapServidores, DominioEtapaProcessamento.G, sigProcessamentoPassos);
			this.executarPassosInternos(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
			this.gravarLogDebugProcessamento("Sucesso "+this.getClass().getSimpleName(), sigProcessamentoCusto, rapServidores, DominioEtapaProcessamento.G, sigProcessamentoPassos);
			this.finalizarProcessamentoCusto();
		}
		catch(Exception e){
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_ETAPA_PROCESSAMENTO_MENSAL, e);
		}
	}

	/**
	 * Metódo responsável pela execução dos passos internos da etapa 
	 * 
	 * @param sigProcessamentoCusto
	 * @param rapServidores
	 * @param sigProcessamentoPassos
	 * @param tipoObjetoCusto
	 * @throws ApplicationBusinessException
	 */
	protected abstract void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException;
	
	/**
	 * Get do título da etapa do processamento. 
	 * 
	 * @author rmalvezzi
	 * @return				Título da etapa do processamento.
	 */
	public abstract String getTitulo();

	/**
	 * Get do nome da etapa do processamento.
	 * 
	 * @author rmalvezzi
	 * @return				Nome da etapa do processamento.
	 */
	public abstract String getNome();
		
	/**
	 * Get da etapa do processamento.
	 * 
	 * @author rmalvezzi
	 * @param tipoObjetoCusto	Se o processamento está sendo executado no modo Apoio ou Assistencial.
	 * @return					Seq da etapa do processamento.
	 */
	public abstract Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto);
	
	
	
	//Métodos utilizados nas classes ProcessamentoCustosMensalIndiretoClienteRN e ProcessamentoCustosMensalIndiretoObjetoCustoRN, 
	//mas nessa nova arquitetura do custos, uma RN não consegue chamar a outra, logo eles têm que ficar em uma classe pai.
	
	//FIXME Analisar se tem como incluir em uma nova classe pai (ProcessamentoMensalBusiness), já que o mesmo método é executado nas duas. 
	//Não faço isso hoje pois tem uma regra de PMD que não permite o extends de outra classe quando a classe começar com o nome ProcessamentoCusto
	/**
	 * Equivale a etapa 4 do processamento indireto de clientes e é a etapa 1 do processamento indireto de objetos de custo
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto					Processamento atual.
	 * @param rapServidores							Servidor logado.
	 * @param sigProcessamentoPassos				Passo atual.
	 * @param cliente								Indicador que informa se será utilizado o processamento de cliente ou de objeto de custo.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	public void processamentoRateioIndiretos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, boolean cliente, Integer iteracao) throws ApplicationBusinessException {

		String chaveMensagemPasso1 = null;
		String chaveMensagemPasso3 = null;
		String chaveMensagemPasso15 = null;
		if (cliente) {
			chaveMensagemPasso1 = "MENSAGEM_PROCESSAMENTO_INDIRETO_4_INICIO";
			chaveMensagemPasso3 = "MENSAGEM_PROCESSAMENTO_INDIRETO_4_CONSULTA";
			chaveMensagemPasso15 = "MENSAGEM_FIM_RATEIO_CUSTOS_INDIRETO_ASSISTENCIAL";
		} else {
			chaveMensagemPasso1 = "MENSAGEM_INICIO_RATEIO_CUSTOS_INDIRETO_OC_ASSISTENCIAL";
			chaveMensagemPasso3 = "MENSAGEM_DEBITO_SUCESSO_RATEIO_CUSTOS_INDIRETO_OC_ASSISTENCIAL";
			chaveMensagemPasso15 = "MENSAGEM_FIM_RATEIO_CUSTOS_INDIRETO_OC_ASSISTENCIAL";
		}

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.N, chaveMensagemPasso1);

		//Passo 2
		List<ValoresIndiretosVO> listaValoresIndiretos = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarValoresIndiretos(sigProcessamentoCusto.getSeq(), cliente, iteracao);

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.N, chaveMensagemPasso3);

		FccCentroCustos centroCusto = null;
		BigDecimal somaPesoObjetoCusto = null;
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaValoresIndiretos)) {
			//Passo 13
			for (ValoresIndiretosVO valoresIndiretosVO : listaValoresIndiretos) {

				//Passo 4
				List<ValoresIndiretosPesosRateioVO> listaObjetoCustoRateio;
				if (cliente) {
					listaObjetoCustoRateio = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
							.buscarObjetosCustoPesosRateioCliente(sigProcessamentoCusto.getSeq(), valoresIndiretosVO.getCctCodigo());
				} else {
					listaObjetoCustoRateio = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
							.buscarObjetosCustoPesosRateioOC(sigProcessamentoCusto.getSeq(), valoresIndiretosVO.getCctCodigo());
				}

				//Verifica se o centro de custo deve ter suas informações buscadas no banco de dados
				if (centroCusto == null || !centroCusto.getCodigo().equals(valoresIndiretosVO.getCctCodigo())) {

					//Busca o centro de custo atual
					centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade()
							.obterCentroCustoPorChavePrimaria(valoresIndiretosVO.getCctCodigo());

					//Passo 5
					if (cliente) {
						somaPesoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
								.buscarSomaPesosObjetosCustoCliente(sigProcessamentoCusto.getSeq(), valoresIndiretosVO.getCctCodigo());
					} else {
						somaPesoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
								.buscarSomaPesosObjetosCustoOC(sigProcessamentoCusto.getSeq(), valoresIndiretosVO.getCctCodigo());
					}
				}

				SigCalculoObjetoCusto calculoObjetoCustoIndireto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(valoresIndiretosVO.getCbjSeq());
				String nomeObjetoCusto = calculoObjetoCustoIndireto.getSigObjetoCustoVersoes().getSigObjetoCustos().getNome();

				boolean fezAlgumRateioValoresIndiretos = false;
				// Passo 9
				if (listaObjetoCustoRateio != null) {
					for (ValoresIndiretosPesosRateioVO valoresIndiretosPesosRateioVO : listaObjetoCustoRateio) {
						//Passos 6, 7, 8
						this.efetuaCadastroCalculoIndireto(iteracao, sigProcessamentoCusto, rapServidores, valoresIndiretosVO, calculoObjetoCustoIndireto, somaPesoObjetoCusto, valoresIndiretosPesosRateioVO);
						this.commitProcessamentoCusto();
						fezAlgumRateioValoresIndiretos = true;
					}

					if (fezAlgumRateioValoresIndiretos) {

						//Passos 10, 11 
						this.efetuaCadastroMovimentoIndireto(iteracao, cliente, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, valoresIndiretosVO,calculoObjetoCustoIndireto, centroCusto);
						this.commitProcessamentoCusto();
						
						// Passo 12
						this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_INDIRETO_CENTRO_CUSTO_RATEADO_SUCESSO",nomeObjetoCusto);
					}
				}
			}
		}
		//Passo 14: Quando mudar o centro de custo, volta ao passo 04 para buscar os objetos de custo de apoio do novo/próximo centro de custo e então proceder aos rateios dos seus valores.
		//Como o passo 4 é um Scrollable, tem que ser repetido toda hora pq não tem como reiniciar o ponteiro

		//Passo 15
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.N, chaveMensagemPasso15);
	}

	/**
	 * Calcula as parcelas de rateio (Passo 6), registra os valores indiretos(Passo 7)  
	 * e atualizar os valores do calculo de objeto de custo (Passo 8)
	 *  
	 * @author rogeriovieira
	 * @param processamento					Processamento atual.
	 * @param servidor						Servidor logado.
	 * @param valoresIndiretosVO			VO que representa uma tupla da consulta do passo 2.
	 * @param calculoObjetoCustoIndireto	Cálculo do objeto de custo que está gerando o débito.
	 * @param somaPesoObjetoCusto			Soma dos pesos do objeto de custo retornada na etapa 5.
	 * @param pesosRateioVO 				VO que representa uma tupla da consulta do passo 4.	 
	 */
	private void efetuaCadastroCalculoIndireto(Integer iteracao, SigProcessamentoCusto processamento, RapServidores servidor, ValoresIndiretosVO valoresIndiretosVO,
			SigCalculoObjetoCusto calculoObjetoCustoIndireto, BigDecimal somaPesoObjetoCusto, ValoresIndiretosPesosRateioVO pesosRateioVO) {

		//Passo 6 
		Double somaPeso = somaPesoObjetoCusto.doubleValue();

		Double qtdeParcelaRateioInd = pesosRateioVO.getPeso() / somaPeso;
		Double valorIndInsumos = valoresIndiretosVO.getValorIndInsumos() / somaPeso * pesosRateioVO.getPeso();
		Double valorIndServicos = valoresIndiretosVO.getValorIndServicos() / somaPeso * pesosRateioVO.getPeso();
		Double valorIndPessoas = valoresIndiretosVO.getValorIndPessoas() / somaPeso * pesosRateioVO.getPeso();
		Double valorIndEquipamentos = valoresIndiretosVO.getValorIndEquipamentos() / somaPeso * pesosRateioVO.getPeso();

		BigDecimal peso = ProcessamentoCustoUtils.criarBigDecimal(pesosRateioVO.getPeso());

		//Passo 7: Objeto de custo que os valores estao sendo adicionados
		SigCalculoObjetoCusto cbj = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(pesosRateioVO.getCbjSeq());

		SigCalculoIndiretoInsumo insumo = new SigCalculoIndiretoInsumo();
		insumo.setCriadoEm(new Date());
		insumo.setRapServidores(servidor);
		insumo.setSigCalculoObjetoCusto(cbj);
		insumo.setSigCalculoObjetoCustoDebita(calculoObjetoCustoIndireto);
		insumo.setQtde(ProcessamentoCustoUtils.criarBigDecimal(qtdeParcelaRateioInd));
		insumo.setVlrInsumos(ProcessamentoCustoUtils.criarBigDecimal(valorIndInsumos));
		insumo.setPeso(peso);
		insumo.setIteracao(iteracao);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoInsumoDAO().persistir(insumo);

		SigCalculoIndiretoEquipamento equipamento = new SigCalculoIndiretoEquipamento();
		equipamento.setCriadoEm(new Date());
		equipamento.setRapServidores(servidor);
		equipamento.setSigCalculoObjetoCusto(cbj);
		equipamento.setSigCalculoObjetoCustoDebita(calculoObjetoCustoIndireto);
		equipamento.setQtde(ProcessamentoCustoUtils.criarBigDecimal(qtdeParcelaRateioInd));
		equipamento.setVlrEquipamentos(ProcessamentoCustoUtils.criarBigDecimal(valorIndEquipamentos));
		equipamento.setPeso(peso);
		equipamento.setIteracao(iteracao);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoEquipamentoDAO().persistir(equipamento);

		SigCalculoIndiretoPessoa pessoa = new SigCalculoIndiretoPessoa();
		pessoa.setCriadoEm(new Date());
		pessoa.setRapServidores(servidor);
		pessoa.setSigCalculoObjetoCusto(cbj);
		pessoa.setSigCalculoObjetoCustoDebita(calculoObjetoCustoIndireto);
		pessoa.setQtde(ProcessamentoCustoUtils.criarBigDecimal(qtdeParcelaRateioInd));
		pessoa.setVlrPessoas(ProcessamentoCustoUtils.criarBigDecimal(valorIndPessoas));
		pessoa.setPeso(peso);
		pessoa.setIteracao(iteracao);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoPessoaDAO().persistir(pessoa);

		SigCalculoIndiretoServico servico = new SigCalculoIndiretoServico();
		servico.setCriadoEm(new Date());
		servico.setRapServidores(servidor);
		servico.setSigCalculoObjetoCusto(cbj);
		servico.setSigCalculoObjetoCustoDebita(calculoObjetoCustoIndireto);
		servico.setQtde(ProcessamentoCustoUtils.criarBigDecimal(qtdeParcelaRateioInd));
		servico.setVlrServicos(ProcessamentoCustoUtils.criarBigDecimal(valorIndServicos));
		servico.setPeso(peso);
		servico.setIteracao(iteracao);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoServicoDAO().persistir(servico);

		//Passo 8
		cbj.setVlrIndEquipamentos(ProcessamentoCustoUtils.criarBigDecimal(cbj.getVlrIndEquipamentos().doubleValue() + valorIndEquipamentos));
		cbj.setVlrIndPessoas(ProcessamentoCustoUtils.criarBigDecimal(cbj.getVlrIndPessoas().doubleValue() + valorIndPessoas));
		cbj.setVlrIndInsumos(ProcessamentoCustoUtils.criarBigDecimal(cbj.getVlrIndInsumos().doubleValue() + valorIndInsumos));
		cbj.setVlrIndServicos(ProcessamentoCustoUtils.criarBigDecimal(cbj.getVlrIndServicos().doubleValue() + valorIndServicos));

		this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(cbj);
	}

	/**
	 * Debita os valores rateados dentre os objetos de custo, inserindo movimentos na tabela SIG_MVTO_CONTA_MENSAIS (Passo 10 e 11).
	 *  
	 * @author rogeriovieira
	 * @param processamento					Processamento atual.
	 * @param servidor							Servidor logado.
	 * @param sigProcessamentoPassos				Passo atual.
	 * @param vo					VO que representa uma tupla da consulta do passo 2
	 * @param cbj			Cálculo do objeto de custo que está gerando o débito.
	 * @param centroCustocliente						Centro de custo
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void efetuaCadastroMovimentoIndireto(Integer iteracao, Boolean cliente, SigProcessamentoCusto processamento, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos, ValoresIndiretosVO vo, SigCalculoObjetoCusto cbj,
			FccCentroCustos centroCustocliente) throws ApplicationBusinessException {

		// Passo 10 e 11
		this.criarMvtoContaMensal(servidor, processamento, cbj, centroCustocliente, null, (vo.getValorIndInsumos() * -1), DominioTipoMovimentoConta.VRG, DominioTipoValorConta.II, iteracao);

		this.criarMvtoContaMensal(servidor, processamento, cbj, centroCustocliente, null, (vo.getValorIndPessoas() * -1), DominioTipoMovimentoConta.VRG, DominioTipoValorConta.IP, iteracao);

		this.criarMvtoContaMensal(servidor, processamento, cbj, centroCustocliente, null, (vo.getValorIndEquipamentos() * -1), DominioTipoMovimentoConta.VRG, DominioTipoValorConta.IE, iteracao);

		this.criarMvtoContaMensal(servidor, processamento, cbj, centroCustocliente, null, (vo.getValorIndServicos() * -1), DominioTipoMovimentoConta.VRG, DominioTipoValorConta.IS, iteracao);
	}

	public void criarMvtoContaMensal(RapServidores servidor, SigProcessamentoCusto processamento, SigCalculoObjetoCusto calculoObjetoCusto,
			FccCentroCustos centroCusto, FccCentroCustos centroCustoDebita, Double valor, DominioTipoMovimentoConta tipoMvto, DominioTipoValorConta tipoValor, Integer iteracao) {
		SigMvtoContaMensal movimentoObjetoCusto = new SigMvtoContaMensal();
		movimentoObjetoCusto.setCriadoEm(new Date());
		movimentoObjetoCusto.setRapServidores(servidor);
		movimentoObjetoCusto.setSigProcessamentoCustos(processamento);
		movimentoObjetoCusto.setCalculoObjetoCusto(calculoObjetoCusto);
		movimentoObjetoCusto.setFccCentroCustosDebita(centroCustoDebita);
		movimentoObjetoCusto.setFccCentroCustos(centroCusto);
		movimentoObjetoCusto.setTipoMvto(tipoMvto);
		movimentoObjetoCusto.setTipoValor(tipoValor);
		movimentoObjetoCusto.setQtde(0L);
		movimentoObjetoCusto.setValor(ProcessamentoCustoUtils.criarBigDecimal(valor));
		movimentoObjetoCusto.setIteracao(iteracao);

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(movimentoObjetoCusto);
	}
	
	/**
	 * Cria e insere os movimentos de acordo com os parâmetros informados
	 * 
	 * @author rogeriovieira
	 * @param servidor							Servidor logado.
	 * @param processamento						Processamento atual.
	 * @param calculoObjetoCusto				Cálculo do objeto de custo que está gerando o débito.
	 * @param centroCusto						Centro de custo.
	 * @param centroCustoDebita					Centro de custo que está debitando.
	 * @param valor								Valor que será movimentado
	 * @param tipoValor							Tipo do valor que será movimentado.
	 */
	public void criarMvtoContaMensal(RapServidores servidor, SigProcessamentoCusto processamento, SigCalculoObjetoCusto calculoObjetoCusto,
			FccCentroCustos centroCusto, FccCentroCustos centroCustoDebita, Double valor, DominioTipoMovimentoConta tipoMvto, DominioTipoValorConta tipoValor) {
		this.criarMvtoContaMensal(servidor, processamento, calculoObjetoCusto, centroCusto, centroCustoDebita, valor, tipoMvto, tipoValor, 0);
		
	}
	
	/**
	 * Insere cids e procedimentos ligados as internações que tiveram alta
	 * @param calculoPaciente
	 * @param rapServidores
	 * @param pConvenioSus
	 * @param pTipoGrupoContaSus
	 */
	protected void inserirCidsProcedimentos(SigCalculoAtdPaciente calculoPaciente, RapServidores rapServidores, Short pConvenioSus, Short pTipoGrupoContaSus) {

		//Preenche o pagador a partir da internação
		if(calculoPaciente.getInternacao() != null){
			calculoPaciente.setPagador(this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().obterPagador(calculoPaciente.getInternacao().getSeq()));
		}
		
		//Somente pode buscar os cids e procedimentos para internação com alta
		if(calculoPaciente.getSituacaoCalculoPaciente().equals(DominioSituacaoCalculoPaciente.IA)){
			
			//Verifica se existe algum faturamento pendente para o atendimento/internação
			calculoPaciente.setIndFatPendente(this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().verificarPossuiFaturamentoPendente(calculoPaciente.getSeq(), pConvenioSus));
			
			//Insere os cids ep rocedimentos somente quando a DthrFim do atendimento estiver entre a data de inicio e fim do processamento
			if(DateUtil.validaDataMaiorIgual(calculoPaciente.getAtendimento().getDthrFim(), calculoPaciente.getProcessamentoCusto().getDataInicio()) && DateUtil.validaDataMenorIgual(calculoPaciente.getAtendimento().getDthrFim(), calculoPaciente.getProcessamentoCusto().getDataFim())){
			
				//Passo 9
				MpmAltaDiagPrincipal cidPrincipal = this.getProcessamentoCustoUtils().getPrescricaoMedicaFacade().buscaAltaPrincipalPorAtendimento(calculoPaciente.getAtendimento());
				if(cidPrincipal != null){//Quando não existe principal, também não existe secundário
					
					//Passo 10
					this.inserirCalculoCid(cidPrincipal.getCid(), true, calculoPaciente, rapServidores);
					
					//Union do passo 9
					List<MpmAltaDiagSecundario> cidsSecundarios = this.getProcessamentoCustoUtils().getPrescricaoMedicaFacade().buscaAltasSecundariosPorAtendimento(calculoPaciente.getAtendimento());
					for (MpmAltaDiagSecundario secundario : cidsSecundarios) {
						//Passo 10
						if(!secundario.getCidSeq().getSeq().equals(cidPrincipal.getCid().getSeq())){
							this.inserirCalculoCid(secundario.getCidSeq(), false, calculoPaciente, rapServidores);
						}
					}
				}
				else{
					List<CidFaturamentoVO> cidsFaturamento = this.getProcessamentoCustoUtils().getSigCalculoAtdCIDSDAO().buscarCidsFaturamento(calculoPaciente.getInternacao().getSeq());
					for (CidFaturamentoVO vo : cidsFaturamento) {
						this.inserirCalculoCid(vo.getCid(), (vo.getPrioridadeCid().equals(DominioPrioridadeCid.P) ? true : false), calculoPaciente, rapServidores);
					}
				}
				
				//Passo 11
				List<ProcedimentoSusVO> procedimentosPrincipaisSus = this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().buscarProcedimentosSusPrincipaisInternacao(calculoPaciente.getInternacao().getSeq(), pConvenioSus, pTipoGrupoContaSus);
				for (ProcedimentoSusVO vo : procedimentosPrincipaisSus) {
					//Passo 12
					this.inserirCalculoProcedimentoSus(vo, true, calculoPaciente, rapServidores);
				}
				
				//Union do passo 11
				Set<ProcedimentoSusVO> procedimentosSecundariosSus = this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().buscarProcedimentosSusSecundariosInternacao(calculoPaciente.getInternacao().getSeq(), pConvenioSus, pTipoGrupoContaSus);
				boolean inseriuPrincipal = procedimentosPrincipaisSus.size() > 0;
				for (ProcedimentoSusVO vo : procedimentosSecundariosSus) {
					
					//RN03 - Se não achou nenhum principal, então primeiro secundário será o principal
					if(!inseriuPrincipal){
						this.inserirCalculoProcedimentoSus(vo, true, calculoPaciente, rapServidores);
						inseriuPrincipal = true;
						continue;
					}
					
					//Somente se já não foi cadastrado como principal, insere como secundário
					if(!this.verificarExisteComoPrincipal(vo, procedimentosPrincipaisSus)){
						//Passo 12
						this.inserirCalculoProcedimentoSus(vo, false, calculoPaciente, rapServidores);
					}
				}
				
				//Passo 13
				List<ProcedimentoConvenioVO> procedimentosConvenios =  this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().buscarProcedimentosConveniosInternacao(calculoPaciente.getInternacao().getSeq(), pConvenioSus);
				boolean inseriuProcedimentoConvenioPrincipal = false;
				for (ProcedimentoConvenioVO vo : procedimentosConvenios) {
					
					//Passo 14 e RN02
					if(vo.getCtaCspCnvCodigo().equals(vo.getIntCspCnvCodigo()) && vo.getCtaCspSeq().equals(vo.getIntCspSeq()) && !inseriuProcedimentoConvenioPrincipal){
						this.inserirCalculoProcedimentoConvenio(vo, true, calculoPaciente, rapServidores);
						inseriuProcedimentoConvenioPrincipal = true;
					}	
					else{
						this.inserirCalculoProcedimentoConvenio(vo, false, calculoPaciente, rapServidores);
					}
				}
			}
		}
		
		this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().merge(calculoPaciente);
	}
	
	/**
	 * Utilizado pelo inserirCidsProcedimentos
	 * @param secundario
	 * @param principais
	 * @return
	 */
	private boolean verificarExisteComoPrincipal(ProcedimentoSusVO secundario, List<ProcedimentoSusVO> principais){
		for (ProcedimentoSusVO principal : principais) {
			if(principal.getIphPhoSeq().equals(secundario.getIphPhoSeq()) 
				&& principal.getIphSeq().equals(secundario.getIphSeq()) 
				&& principal.getCthSeq().equals(secundario.getCthSeq()) ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Utilizado pelo inserirCidsProcedimentos
	 * @param cid
	 * @param principal
	 * @param calculoPaciente
	 * @param rapServidores
	 */
	private void inserirCalculoCid(AghCid cid, Boolean principal, SigCalculoAtdPaciente calculoPaciente, RapServidores rapServidores) {
		SigCalculoAtdCIDS calculoCid = new SigCalculoAtdCIDS();
		calculoCid.setCid(cid);
		calculoCid.setRapServidores(rapServidores);
		calculoCid.setCriadoEm(new Date());
		calculoCid.setPrincipal(principal);
		calculoCid.setCalculoAtdPaciente(calculoPaciente);
		this.getProcessamentoCustoUtils().getSigCalculoAtdCIDSDAO().persistir(calculoCid);
	}
	
	/**
	 * Utilizado pelo inserirCidsProcedimentos
	 * @param vo
	 * @param principal
	 * @param calculoPaciente
	 * @param rapServidores
	 */
	private void inserirCalculoProcedimentoSus(ProcedimentoSusVO vo, Boolean principal, SigCalculoAtdPaciente calculoPaciente, RapServidores rapServidores) {
		SigCalculoAtdProcedimentos procedimento = new SigCalculoAtdProcedimentos();
		procedimento.setCalculoAtdPaciente(calculoPaciente);
		procedimento.setPhiSeq(vo.getPhiSeq());
		procedimento.setCthSeq(vo.getCthSeq());
		procedimento.setIphPhoSeq(vo.getIphPhoSeq());
		procedimento.setIphSeq(vo.getIphSeq());
		procedimento.setPrincipal(principal);
		procedimento.setRapServidores(rapServidores);
		procedimento.setCriadoEm(new Date());
		this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().persistir(procedimento);
	}
	
	/**
	 * Utilizado pelo inserirCidsProcedimentos
	 * @param vo
	 * @param principal
	 * @param calculoPaciente
	 * @param rapServidores
	 */
	private void inserirCalculoProcedimentoConvenio(ProcedimentoConvenioVO vo, Boolean principal, SigCalculoAtdPaciente calculoPaciente, RapServidores rapServidores) {
		SigCalculoAtdProcedimentos procedimento = new SigCalculoAtdProcedimentos();
		procedimento.setCalculoAtdPaciente(calculoPaciente);
		procedimento.setCtaNro(vo.getCtaNro());
		procedimento.setIphPhoSeq(vo.getIphPhoSeq());
		procedimento.setIphSeq(vo.getIphSeq());
		procedimento.setPrincipal(principal);
		procedimento.setRapServidores(rapServidores);
		procedimento.setCriadoEm(new Date());
		this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().persistir(procedimento);
	}
}