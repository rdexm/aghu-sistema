package br.gov.mec.aghu.sig.custos.processamento.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSigTipoAlerta;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigPassos;
import br.gov.mec.aghu.model.SigProcessamentoAlertas;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoExamesVO;
import br.gov.mec.aghu.sig.custos.vo.ProcessamentoCustoFinalizadoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Estória do Usuário #27890
 * Classe responsavel por orquestrar o Processamento Diário/Mensal do Módulo de custo.
 * Essa rotina será rodada todo o dia as 00:30
 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class OrquestracaoProcessamentoDiarioMensal extends ProcessamentoCustoBusiness {

	private static final long serialVersionUID = 3741920517718553668L;
	
	private static final Log LOG = LogFactory.getLog(OrquestracaoProcessamentoDiarioMensal.class);

	/**
	 * Método responsável em disparar a execução de um agendamento automatizado.
	 * 
	 * @author rmalvezzi
	 * @return										True se toda a execução ocorreu sem problemas e false se algum problema aconteceu.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 * @throws ApplicationBusinessException					Exceção lançada se algum erro acontecer na criar as pendencias no processamento.
	 */
	public List<ProcessamentoCustoFinalizadoVO> executarProcessamentoCustoAutomatizado() throws ApplicationBusinessException {
		
		SigProcessamentoCusto processmentoAgendadoManual = null;
		RapServidores servidor = null;
		List<ProcessamentoCustoFinalizadoVO> processamentosFinalizados = new ArrayList<ProcessamentoCustoFinalizadoVO>();
		
		try{
			LOG.debug("PMSigCustos: Inicio SCHEDULER debug");
			
			this.iniciarProcessamentoCusto();
			
			servidor = this.obterServidorDefault();

			//Passo 1
			LOG.debug("PMSigCustos: Início Diário Agendado Manual");
			List<SigProcessamentoCusto> competenciasAgendadoManualDiario = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO()
					.pesquisarCompetencia(DominioSituacaoProcessamentoCusto.SD);
	
			if (ProcessamentoCustoUtils.verificarListaNaoVazia(competenciasAgendadoManualDiario)) {
				//[FE01]
				this.processarEtapa1(competenciasAgendadoManualDiario.get(0), servidor, false, processamentosFinalizados);
			}
			LOG.debug("PMSigCustos: Fim Diário Agendado Manual");
	
			//Passo 2
			LOG.debug("PMSigCustos: Início Diário Automatico");
			List<SigProcessamentoCusto> competenciasMesAtualDiario = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO()
					.obterSigProcessamentoCustoCompetencia(ProcessamentoCustoUtils.obterCompetenciaMesAtual());
	
			SigProcessamentoCusto processamentoMesAtual;
	
			if (ProcessamentoCustoUtils.verificarListaNaoVazia(competenciasMesAtualDiario)) {
				processamentoMesAtual = competenciasMesAtualDiario.get(0);
			} else {
				//[FE02]
				processamentoMesAtual = this.gravarInicioProcessamento();
			}
	
			//Passo 3
			this.processarEtapa1(processamentoMesAtual, servidor, false, processamentosFinalizados);
			LOG.debug("PMSigCustos: Fim Diário Automatico");
	
			//Passo 4
			LOG.debug("PMSigCustos: Início Mensal Agendado Manual");
			List<SigProcessamentoCusto> competenciasAgendadoManualMensal = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO()
					.pesquisarCompetencia(DominioSituacaoProcessamentoCusto.S);
	
			//[FE03]
			if (ProcessamentoCustoUtils.verificarListaNaoVazia(competenciasAgendadoManualMensal)) {
				processmentoAgendadoManual = competenciasAgendadoManualMensal.get(0);
				this.limpezaProcessamento(processmentoAgendadoManual.getSeq());
				this.processarEtapa2(processmentoAgendadoManual, servidor, processamentosFinalizados);
			}
			LOG.debug("PMSigCustos: Fim Mensal Agendado Manual");
	
			//Passo 5		
			LOG.debug("PMSigCustos: Início Mensal Automatico");
			if (this.getProcessamentoCustoUtils().getParametroFacade().verificarExisteAghParametroValor(AghuParametrosEnum.P_AGHU_SIG_DIA_PROCESSAMENTO)) {
				//[FE04]
				Integer diaProcessamento = this.getProcessamentoCustoUtils().getParametroFacade()
						.obterValorNumericoAghParametros(AghuParametrosEnum.P_AGHU_SIG_DIA_PROCESSAMENTO.toString()).intValue();
				if (diaProcessamento.equals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))) {
					List<SigProcessamentoCusto> competenciasMesAtualMensal = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO()
							.obterSigProcessamentoCustoCompetencia(ProcessamentoCustoUtils.obterCompetenciaProcessamento());
					if (ProcessamentoCustoUtils.verificarListaNaoVazia(competenciasMesAtualMensal)) {
						this.processarEtapa2(competenciasMesAtualMensal.get(0), servidor, processamentosFinalizados);
					}
				}
			}
			LOG.debug("PMSigCustos: Fim Mensal Automatico");
			this.finalizarProcessamentoCusto();
			return processamentosFinalizados;
		}
		catch(Exception e){
			LOG.error(e.getMessage(), e);
			if(processmentoAgendadoManual != null){
				this.buscarMensagemEGravarLogProcessamento(processmentoAgendadoManual, servidor, null, DominioEtapaProcessamento.G, "MENSAGEM_PROCESSAMENTO_FINALIZADO_ERRO", processmentoAgendadoManual.getSeq(), processmentoAgendadoManual.getCompetenciaMesAno());
				this.atualizarSituacaoProcessamento(processmentoAgendadoManual, DominioSituacaoProcessamentoCusto.E);
				this.finalizarProcessamentoCusto();
			}
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_ORQUESTRACAO, e);
		}
	}

	private boolean processarEtapa1(SigProcessamentoCusto processamentoCusto, RapServidores servidor, boolean processamentoMensal,  List<ProcessamentoCustoFinalizadoVO> processamentosFinalizados)
			throws ApplicationBusinessException {
		
		DominioSituacaoProcessamentoCusto dominioErroProcessamento = null;
		if (processamentoMensal) {
			dominioErroProcessamento = DominioSituacaoProcessamentoCusto.E;
		}
		else{
			dominioErroProcessamento = DominioSituacaoProcessamentoCusto.ED;
		}
		
		try{	
			//Etapa 1
			LOG.debug("PMSigCustos: Inicio Processamento Diário");
	
			//Passo 2
			
			List<SigProcessamentoCusto> competenciasADs = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO().pesquisarCompetencia(
					DominioSituacaoProcessamentoCusto.AD);
	
			if (ProcessamentoCustoUtils.verificarListaNaoVazia(competenciasADs)) {
				//[FE07]processamentoMensal
				this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, null, DominioEtapaProcessamento.C,
						"LOG_PROCESSAMENTO_DIARIO_NAO_EXECUTAR", processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), competenciasADs.get(0).getCompetenciaMesAno());
				this.atualizarSituacaoProcessamento(processamentoCusto, dominioErroProcessamento);
				processamentosFinalizados.add(new ProcessamentoCustoFinalizadoVO(processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), true) );
				return false;
			}
			
			//Passo 3
			this.atualizarSituacaoProcessamento(processamentoCusto, DominioSituacaoProcessamentoCusto.AD);
	
			//Passo 4
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, null, DominioEtapaProcessamento.C,
					"LOG_INICIO_PROCESSAMENTO_DIARIO", processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), new Date().toString());
		
			//Passo 5,6,7
		 	if (!this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemPacientesRN(), processamentoCusto, servidor, null)) {
				//[FE05] //[FE06]
		 		this.atualizarSituacaoProcessamento(processamentoCusto, dominioErroProcessamento);
		 		processamentosFinalizados.add(new ProcessamentoCustoFinalizadoVO(processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), true) );
				return false;
			}
	
			//Passo 8
			if(!this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPRN(), processamentoCusto, servidor, null)){
				this.atualizarSituacaoProcessamento(processamentoCusto, dominioErroProcessamento);
		 		processamentosFinalizados.add(new ProcessamentoCustoFinalizadoVO(processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), true));
				return false;
			}
	
			//Passo 9		
			//[RN01]
			Calendar hoje = Calendar.getInstance();
			if (hoje.get(Calendar.DAY_OF_MONTH) == hoje.getActualMaximum(Calendar.DAY_OF_MONTH) || processamentoCusto.getDataFim().before(hoje.getTime())) {
				this.atualizarSituacaoProcessamento(processamentoCusto, DominioSituacaoProcessamentoCusto.FD );
			} else {
				this.atualizarSituacaoProcessamento(processamentoCusto, DominioSituacaoProcessamentoCusto.PD);
			}
			return true;
		}
		catch(Exception e){
			LOG.error(e.getMessage(), e);
			if(processamentoCusto != null){
				this.reiniciarProcessamentoQuandoNecessario();
				this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, null, DominioEtapaProcessamento.G, "MENSAGEM_PROCESSAMENTO_FINALIZADO_ERRO", processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno());
				this.atualizarSituacaoProcessamento(processamentoCusto, dominioErroProcessamento);
				processamentosFinalizados.add(new ProcessamentoCustoFinalizadoVO(processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), true) );
			}
			return false;
		}
	}

	private void processarEtapa2(SigProcessamentoCusto processamentoCusto, RapServidores servidor, List<ProcessamentoCustoFinalizadoVO> processamentosFinalizados) throws ApplicationBusinessException {
		//Etapa 2
		LOG.debug("PMSigCustos: Inicio Processamento Mensal");

		//Passo 2
		//[FE06]
		if (this.processarEtapa1(processamentoCusto, servidor, true, processamentosFinalizados)) {
			//Passo 3 
			this.atualizarSituacaoProcessamento(processamentoCusto, DominioSituacaoProcessamentoCusto.A);

			//Passo 4
			this.processarCustoMensal(processamentoCusto, processamentosFinalizados);
		}
	}
	
	/**
	 * Inicia os valores iniciais para o processamento.
	 * 
	 * @author rmalvezzi
	 * @return										O objeto processamento acabado de ser criado.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public SigProcessamentoCusto gravarInicioProcessamento() throws ApplicationBusinessException {
		SigProcessamentoCusto sigProcessamentoCusto = new SigProcessamentoCusto();
		sigProcessamentoCusto.setCompetencia(ProcessamentoCustoUtils.obterCompetenciaMesAtual());
		sigProcessamentoCusto.setDataInicio(DateUtil.obterDataInicioCompetencia(sigProcessamentoCusto.getCompetencia()));
		sigProcessamentoCusto.setDataFim(DateUtil.obterDataFimCompetencia(sigProcessamentoCusto.getCompetencia()));
		sigProcessamentoCusto.setIndSituacao(DominioSituacaoProcessamentoCusto.PD);
		sigProcessamentoCusto.setCriadoEm(new Date());
		sigProcessamentoCusto.setRapServidores(this.obterServidorDefault());
		LOG.debug("PMSigCustos: Criando Novo Processamento para COMPETENCIA " + sigProcessamentoCusto.getCompetenciaMesAno());
		this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO().persistir(sigProcessamentoCusto);
		this.commitProcessamentoCusto();
		LOG.debug("PMSigCustos: Processamento para COMPETENCIA " + sigProcessamentoCusto.getCompetenciaMesAno() + " incluido com SUCESSO");
		return sigProcessamentoCusto;
	}
	
	/**
	 * Método que dispara a remoção/limpeza de todas as tabelas de processamento para assim ser possível começar um processamento do zero, 
	 * sem nenhuma sujeira do processamento de mesma compentencia anteiror.
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamentoCusto 	Processamento a qual a limpreza precisa ser feita.
	 * @throws ApplicationBusinessException 
	 */
	public void limpezaProcessamento(Integer seqProcessamentoCusto) throws ApplicationBusinessException {
		this.getProcessamentoCustoUtils().getSigObjetoCustoAnaliseDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtdCIDSDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoEquipamentoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoInsumoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoPessoaDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoIndiretoServicoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtividadeInsumoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoRateioInsumoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtividadeEquipamentoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoClienteDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoRateioEquipamentoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtividadePessoaDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoRateioPessoaDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoAtividadeServicoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoRateioServicoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigProducaoObjetoCustoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigDetalheProducaoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigDetalheFolhaPessoaDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoDirecionadorDAO().removerPorProcessamento(seqProcessamentoCusto);		
		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigControleVidaUtilDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigProcessamentoAlertasDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigProcessamentoCustoLogDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.getProcessamentoCustoUtils().getSigProcessamentoPassosDAO().removerPorProcessamento(seqProcessamentoCusto);
		this.commitProcessamentoCusto();
	}
	
	/**
	 * Estória do Usuário #16834
	 * Método responsável por receber as execuções de processamentos agendados manuamente ou automatizados, gerenciado
	 * assim toda a execução do processamento mensal, como logs, tratamentos de fluxos alternativos e tratamentos de exceções. 
	 *  
	 * @author rmalvezzi
	 * @param processamentoCusto					Processamento a ser executado.
	 * @return										True se toda a execução ocorreu sem problemas e false se algum problema aconteceu.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public boolean processarCustoMensal(SigProcessamentoCusto processamentoCusto, List<ProcessamentoCustoFinalizadoVO> processamentosFinalizados) throws ApplicationBusinessException  {
		long tempoInicio = System.currentTimeMillis();
		LOG.debug("Rotina Processamento Mensal iniciada em: " + Calendar.getInstance().getTime());
		boolean ocorreuErroProcessamento = false;
		RapServidores servidor = null;

		try {
			servidor = this.obterServidorDefault();

			//Passo 2
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto,
					servidor, null, DominioEtapaProcessamento.G, "LOG_PROCESSAMENTO_MENSAL_INICIO", processamentoCusto.getSeq(),
					processamentoCusto.getCompetenciaMesAno(), new Date().toString());

			LOG.debug("PMSigCustos: Executar Etapas do Processamento Mensal");

			//Passos 3, 4, 5, 6
			ocorreuErroProcessamento = !this.executaEtapasProcessamentoMensal(processamentoCusto, servidor);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			ocorreuErroProcessamento = true;
		}

		//Passo 7
		if (ocorreuErroProcessamento) {
			//[FE05] 
			LOG.debug("----------------ERRO-----------------------");
			LOG.debug("PMSigCustos: ERRO em Etapa do Processamento");
			//Reinicia a transação do processamento caso o erro tenha finalizado a transação aberta anteriormente anterior
			this.reiniciarProcessamentoQuandoNecessario();
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, null, DominioEtapaProcessamento.G,
					"MENSAGEM_PROCESSAMENTO_FINALIZADO_ERRO", processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno());
			this.atualizarSituacaoProcessamento(processamentoCusto, DominioSituacaoProcessamentoCusto.E);

		} else {
			LOG.debug("------------------------SUCESSO-------------------------------");
			LOG.debug("PMSigCustos: OK Etapas do processamento finalizadas com SUCESSO");
			//Passo 8
			this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, null, DominioEtapaProcessamento.G,
					"MENSAGEM_PROCESSAMENTO_FINALIZADO", processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno());
			//Passo 9
			this.atualizarSituacaoProcessamento(processamentoCusto, DominioSituacaoProcessamentoCusto.P);
			//Passo 10
			this.gerarAlertasProcessamento(processamentoCusto, servidor);

			LOG.debug("PMSigCustos: Criando uma pendência para indicar que o processamento foi finalizado");	
		}
		
		//Passo 11
		processamentosFinalizados.add(new ProcessamentoCustoFinalizadoVO(processamentoCusto.getSeq(), processamentoCusto.getCompetenciaMesAno(), ocorreuErroProcessamento));
		
		ProcessamentoCustoUtils.calcularLogarTempoExecucao(tempoInicio);

		return !ocorreuErroProcessamento;
	}

	/**
	 * Método que chama a execução de todas as etapas do processamento.
	 * 
	 * @param processamentoAtual					Processamento Atual.
	 * @param servidor								Servidor logado.
	 * @return 										False se ocorreu algum erro que deve interromper o processamento, 
	 * 												True se não ocorreram erros que interromperiam o processamento.
	 * @throws ApplicationBusinessException			Exceção lançada se alguma coisa acontecer fora do esperado.
	 */
	private boolean executaEtapasProcessamentoMensal(SigProcessamentoCusto processamentoAtual, RapServidores servidor) throws ApplicationBusinessException {
		return
		//Etapas de contabilização
		this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalContabilizacaoConsumoInsumoCCON(), processamentoAtual, servidor, null) // 1 - "Contabilização do consumo de insumos do mês por centro de custo"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalContabilizacaoDebitoParcelaMaterialON(), processamentoAtual,servidor, null) // 2 - "Contabilização de débitos de parcelas de materiais com controle de vida útil"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalContabilizacaoDepreciacaoEquipamentoON(), processamentoAtual,servidor, null) // 3 - "Contabilização das depreciações dos equipamentos no mês por centro de custo"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalContabilizacaoFolhaPagamentoPessoalON(), processamentoAtual,servidor, null) // 4 - "Contabilização da folha de pagamento do pessoal no mês por grupo de ocupação e centro de custo"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalContabilizacaoContratoServicoCCON(), processamentoAtual, servidor,null)// 5 - "Contabilização dos custos de contratos de serviços do mês por centro de custo"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalContabilizacaoProducaoExameON(), processamentoAtual, servidor, null)// 6 - "Contabilização da produção de exames do mês por centro de custo"

				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN(), processamentoAtual, servidor, null) //28: Executa a contagem de bolsas, seringas e dispensações de quimioterapias por tipo de atendimento do paciente (internação e/ou ambulatorial)		
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN(), processamentoAtual, servidor, null) //29: Executa a contagem de cuidados de quimioterapias por tipo de atendimento do paciente (internação e/ou ambulatorial) 
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalContagemDialisePacienteRN(), processamentoAtual, servidor, null)//30: Executa a contagem de cuidados de diálise por tipo de atendimento do paciente (internação e/ou ambulatorial)
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN(), processamentoAtual, servidor, null)//31: Executa a contagem de medicamentos de diálise por tipo de atendimento do paciente (internação e/ou ambulatorial) 
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPacienteRN(), processamentoAtual, servidor, null)//32: Executa a contagem de procedimentos e materiais de diálise por tipo de atendimento do paciente (internação e/ou ambulatorial)
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalSumarizarProdAssistencialON(), processamentoAtual, servidor, null) //23 - "Sumarização da produção assistencial do paciente"	
						
				// Cálculos dos objetos de custo assistencial
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCriacaoObjetoCustoProducaoON(), processamentoAtual, servidor, null)// 7 - "Criação de objetos de custo com produção do mês"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoInsumoON(), processamentoAtual, servidor, DominioTipoObjetoCusto.AS)// 8 - "Processamento do custo dos insumos nas atividades"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoEquipamentoON(), processamentoAtual, servidor, DominioTipoObjetoCusto.AS)// 9 - "Processamento do custo dos equipamentos nas atividades"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoServicoON(), processamentoAtual, servidor,DominioTipoObjetoCusto.AS)// 10 - "Processamento do custo dos contratos de serviços nas atividades"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoPessoalON(), processamentoAtual, servidor,DominioTipoObjetoCusto.AS)// 11 - "Processamento do custo de pessoal nas atividades"

				// Cálculos dos objetos de custo de apoio (provavelmente os rateios não serão executados)
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCargaObjetoCustoApoioON(), processamentoAtual, servidor, null) // 12 - "Criação de objetos de custo de apoio do mês"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoInsumoON(), processamentoAtual, servidor, DominioTipoObjetoCusto.AP) // 13 -> 8 - "Processamento do custo dos insumos nas atividades de objetos de custo de apoio"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoPessoalON(), processamentoAtual, servidor, DominioTipoObjetoCusto.AP)// 14 -> 11 - "Processamento do custo de pessoal nas atividades de objetos de custo de apoio" 
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoEquipamentoON(), processamentoAtual, servidor, DominioTipoObjetoCusto.AP)// 15 -> 9 - "Processamento do custo dos equipamentos nas atividades de objetos de custo de apoio"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalCustoServicoON(), processamentoAtual, servidor,DominioTipoObjetoCusto.AP)// 16 -> 10 - "Processamento do custo dos contratos de serviços nas atividades de objetos de custo de apoio"

				// Rateios que devem ser executados somente uma vez e no final
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalRateioDiretoEquipamentoON(), processamentoAtual, servidor, null)// 17 - "Processamento do rateio dos custos diretos de equipamentos"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalRateioDiretoPessoalON(), processamentoAtual, servidor, null)// 18 - "Processamento do rateio dos custos diretos de pessoal"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalRateioDiretoServicoON(), processamentoAtual, servidor, null)// 19 - "Processamento do rateio dos custos diretos de serviços"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalRateioDiretoInsumoON(), processamentoAtual, servidor, null)// 20 - "Processamento do rateio dos custos diretos de insumos"

				// Indiretos
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalIndiretoClienteON(), processamentoAtual, servidor, null)// 21 - "Processamento dos custos indiretos nos clientes"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalIndiretoObjetoCustoON(), processamentoAtual, servidor, null)// 22 - "Processamento dos custos indiretos nos objetos de custos assistenciais"
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalSumarizarCustoMedioON(), processamentoAtual, servidor, null)// 23 - "Sumarizar o valor dos objetos de custo com produção no mês de processamento"				

				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN(), processamentoAtual, servidor, null) // 33 - "Cálculo de custo variável de insumos de NPT e quimioterapias"
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalCalculoOrtesesEProtesesRN(), processamentoAtual, servidor, null) // 34 - "Cálculo de custo de órteses e próteses"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalCalculoMedicamentosDialiseRN(), processamentoAtual, servidor, null) // 37 - "Cálculo de custo de medicamentos de diálise"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalCalculoMedicamentosRN(), processamentoAtual, servidor, null) // 38 - "Cálculo de custo de medicamentos"
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN(), processamentoAtual, servidor, null) // 39 - "Cálculo de custo de procedimentos e materiais de diálise"
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoMensalAlocarOCPacienteON(), processamentoAtual, servidor, null) // 24 - "Alocação do custo de objetos de custo (com custo médio) nos pacientes"
				
				& this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalAssociarPacienteReceitaPHIRN(), processamentoAtual, servidor, null) // 35 - "Associar custos do paciente x receita convênio/particular para internação. Custo para PHIs."                
                & this.executaProcessamento(this.getProcessamentoCustoUtils().getProcessamentoCustosMensalReceitaSusCustosPacienteRN(), processamentoAtual, servidor, null) // 40 - "Associar valores de receita SUS com os custos do paciente"
		;
	}
	
	/** 
	 * Método genérico responsável pela execução do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param <T> 								Qual o tipo/classe do processamento.
	 * @param obj 								Objeto a ser executada a etapa de processamento.
	 * @param processamentoAtual				O processamento atual.
	 * @param servidor							O objeto que representa o usuário logado.
	 * @param tipoObjetoCusto					Null se não possuir um tipo associado.
	 * @return 									True se o processamento foi executado com sucesso.
	 * @throws ApplicationBusinessException	Exceção lançada se for para abortar o processamento.
	 */
	public <T extends ProcessamentoMensalBusiness> boolean executaProcessamento(T obj, SigProcessamentoCusto processamentoAtual, RapServidores servidor, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		//Verifica se o processamento ainda está ativo, caso contrário aborta o mesmo
		this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO().refresh(processamentoAtual);
		if(processamentoAtual.getIndSituacao().equals(DominioSituacaoProcessamentoCusto.E)){
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ABORTA_PROCESSAMENTO);
		}
		
		long tempoInicio = System.currentTimeMillis();
		LOG.debug("PMSigCustos: " + obj.getTitulo());	
		//Passo 3
		Integer seqPasso = obj.getPassoProcessamento(tipoObjetoCusto);
		SigProcessamentoPassos sigProcessamentoPassos = this.criaProcessamentoPassosEmAndamento(seqPasso, processamentoAtual, servidor);
		if(sigProcessamentoPassos != null ){
			if(sigProcessamentoPassos.getSituacao().equals(DominioSituacaoEtapaProcessamento.A)){
				try {
					LOG.debug("PMSigCustos: INICIO - " + obj.getNome() + " - Etapa " + sigProcessamentoPassos.getSigPassos().getSeq());
		
					//Passo 4
					obj.executarProcessamento(processamentoAtual, servidor, sigProcessamentoPassos, tipoObjetoCusto);
					LOG.debug("PMSigCustos: RETORNO OK - " + obj.getNome() + " - Etapa " + sigProcessamentoPassos.getSigPassos().getSeq());
		
					//Passo 5
					this.reiniciarProcessamentoQuandoNecessario();
					return this.atualizarPassoProcessado(sigProcessamentoPassos);
				} catch (Exception e) {
					//[FE02]
					LOG.debug("PMSigCustos: ERRO - " + obj.getNome() + " - Etapa " + sigProcessamentoPassos.getSigPassos().getSeq());
					return this.atualizarPassoErro(sigProcessamentoPassos, e);
				} finally {
					ProcessamentoCustoUtils.calcularLogarTempoExecucao(tempoInicio);
				}
			}
			else{
				return true;
			}
		}
		else{
			this.buscarMensagemEGravarLogProcessamento(processamentoAtual, servidor, null, DominioEtapaProcessamento.G, "MENSAGEM_LOG_PASSO_NAO_ENCONTRADO", seqPasso);
			return false;
		}
	}
	
	/**
	 * Cria um registro do processamento passo com situação em Andamento e já relaiza o commit do processamento.
	 * 
	 * @author rmalvezzi
	 * @param seqPasso								Seq do passo.
	 * @param processamentoAtual					Processamento atual.
	 * @param servidor								Servidor logado.
	 * @return										O processamento passo que acabou de ser criado.	
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	private SigProcessamentoPassos criaProcessamentoPassosEmAndamento(Integer seqPasso, SigProcessamentoCusto processamentoAtual, RapServidores servidor)
			throws ApplicationBusinessException {
		SigPassos sigPassos = this.getProcessamentoCustoUtils().getSigPassosDAO().obterPorChavePrimaria(seqPasso);
		if(sigPassos != null){
			SigProcessamentoPassos sigProcessamentoPassos = new SigProcessamentoPassos();
			sigProcessamentoPassos.setRapServidores(servidor);
			sigProcessamentoPassos.setCriadoEm(new Date());
			sigProcessamentoPassos.setSituacao(DominioSituacaoEtapaProcessamento.A);
			sigProcessamentoPassos.setSigProcessamentoCusto(processamentoAtual);
			sigProcessamentoPassos.setSigPassos(sigPassos);
			LOG.debug("PMSigCustos: Incluindo Passo de Processamento - Passo: " + sigPassos.getDescricao());
			this.getProcessamentoCustoUtils().getSigProcessamentoPassosDAO().persistir(sigProcessamentoPassos);
			this.commitProcessamentoCusto();
			LOG.debug("PMSigCustos: Passo Incluido com SUCESSO");
			return sigProcessamentoPassos;
		}
		return null;
	}
	
	/**
	 * Atualiza no BD a situação do processamento passado por parametro e faz o commit do processamento.
	 *  
	 * @author rmalvezzi
	 * @param processamento							Processamento Atual.
	 * @param indSituacao							Situação a qual o processamento deve ser atualizado.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public void atualizarSituacaoProcessamento(SigProcessamentoCusto processamento, DominioSituacaoProcessamentoCusto indSituacao)
			throws ApplicationBusinessException {
		LOG.debug("PMSigCustos: Atualizando Situacao Processamento para " + indSituacao.getDescricao());
		processamento.setIndSituacao(indSituacao);
		this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO().atualizar(processamento);
		this.commitProcessamentoCusto();
		LOG.debug("PMSigCustos: Situacao Processamento atualizada com SUCESSO");
	}

	
	/**
	 * Atualiza o campo Situação para 'P' (processado) da entidade {@link SigProcessamentoPassos} passada por paremtro. 
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoPassos				O objeto a ser atualizado no BD.
	 * @return										Sempre true;
	 * @throws ApplicationBusinessException 		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public boolean atualizarPassoProcessado(SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		this.atualizarProcessamentoPassos(sigProcessamentoPassos, DominioSituacaoEtapaProcessamento.P);
		return true;
	}
	
	/**
	 * Atualiza o campo Situação para 'E' (erro) da entidade {@link SigProcessamentoPassos} passada por parametro, verifica se é necessário 
	 * abortar o processamento. Esse método também loga de o erro de uma maneira mais detalhada para facilitar a investigação de possíveis erros. 
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoPassos			O objeto a ser atualizado no BD.
	 * @param e									Qual exceção (erro) aconteceu.
	 * @return									Sempre false.
	 * @throws ApplicationBusinessException	Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public boolean atualizarPassoErro(SigProcessamentoPassos sigProcessamentoPassos, Exception e) throws ApplicationBusinessException {
		LOG.error(e.getMessage(), e);

		this.gravarErrosNoLog(0, e.getCause(), sigProcessamentoPassos);

		//[FE02] - Passo 2
		this.atualizarProcessamentoPassos(sigProcessamentoPassos, DominioSituacaoEtapaProcessamento.E);
		//[FE02] - Passo 4
		ProcessamentoCustoUtils.abortarProcessamento(sigProcessamentoPassos.getSigPassos().getIndParaProcessamento());
		return false;
	}
	
	/**
	 * Atualiza o campo Situação do processamento para o passado por parametro.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoPassosAtualizar		O objeto a ser atualizado no BD.
	 * @param situacaoEtapaProcessamento			Situação a qual o processamento deve ser atualizado.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public void atualizarProcessamentoPassos(SigProcessamentoPassos sigProcessamentoPassosAtualizar,
			DominioSituacaoEtapaProcessamento situacaoEtapaProcessamento) throws ApplicationBusinessException {
		LOG.debug("PMSigCustos: Atualizando Passo de Processamento para Concluido - Passo: "
				+ sigProcessamentoPassosAtualizar.getSigPassos().getDescricao());
		sigProcessamentoPassosAtualizar.setSituacao(situacaoEtapaProcessamento);
		this.getProcessamentoCustoUtils().getSigProcessamentoPassosDAO().atualizar(sigProcessamentoPassosAtualizar);
		this.commitProcessamentoCusto();
	}
	
	/**
	 * Gerar os alertas necessários após a conclusão do processamento.
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto					Processamento Atual
	 * @param servidor 								Servidor logado.
	 * @throws ApplicationBusinessException 		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public void gerarAlertasProcessamento(SigProcessamentoCusto processamentoCusto, RapServidores servidor) throws ApplicationBusinessException {
		LOG.debug("PMSigCustos: Inicio da  Geracao de Alertas de Processamento");
		this.gerarAlertaProcessamento(processamentoCusto, servidor);
		this.commitProcessamentoCusto();
		LOG.debug("PMSigCustos: Fim da Geracao de Alertas de Processamento");
	}
	
	/**
	 * Obtem o servidor default a partir de um parametro do BD, se não existir o
	 * parametro no BD ou o servidor não for encontrado um exceção é lançada.
	 * 
	 * @author rmalvezzi
	 * @return O servidor default.
	 * @throws ApplicationBusinessException
	 *             Éxceção laçada se não achar um servidor.
	 */
	public RapServidores obterServidorDefault() throws ApplicationBusinessException {
		LOG.debug("PMSigCustos: Pesquisa Servidor AGHU");
		RapServidores servidor = null;

		if (this. getProcessamentoCustoUtils().getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_USUARIO_AGENDADOR)) {
			AghParametros aghParametro = this.getProcessamentoCustoUtils().getParametroFacade().getAghParametro(AghuParametrosEnum.P_USUARIO_AGENDADOR);
			String userName = aghParametro.getVlrTexto();
			if (userName != null) {
				servidor = this.getProcessamentoCustoUtils().getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(userName);
			}
		}

		if (servidor == null || servidor.getId() == null) {
			LOG.error("PMSigCustos: Servidor agendador não encontrado - Abortando Execucao");
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_SERVIDOR_NAO_ENCONTRADO);
		}
		return servidor;
	}
	
	/**
	 * Geração de alertas 
	 * @param pmuSeq
	 * @param servidor
	 * 
	 * @author agerling
	 */
	public void gerarAlertaProcessamento(SigProcessamentoCusto processamentoCusto, RapServidores servidor) {
		this.gerarAlertaNC(processamentoCusto, servidor);
		this.gerarAlertaCR(processamentoCusto, servidor);
		this.gerarAlertaQA(processamentoCusto, servidor);
	}

	/**
	 * Alerta NC: Geração de alertas para centro de custo sem objetos de custos ativos, ou seja, custos não foram calculados e repassados até chegar ao paciente, ficaram contidos na área.
	 * @author agerling
	 * @param processamentoCusto
	 * @param servidor
	 */
	private void gerarAlertaNC(SigProcessamentoCusto processamentoCusto, RapServidores servidor) {
		List<ObjetoCustoProducaoExamesVO> listVO = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.consultarCentroCustoSemObjCustoAlocado(processamentoCusto);

		if (listVO != null && !listVO.isEmpty()) {
			for (ObjetoCustoProducaoExamesVO vo : listVO) {
				SigProcessamentoAlertas processamentoAlerta = new SigProcessamentoAlertas();
				processamentoAlerta.setCriadoEm(new Date());
				processamentoAlerta.setRapServidores(servidor);
				processamentoAlerta.setSigProcessamentoCustos(processamentoCusto);
				processamentoAlerta.setFccCentroCustos(vo.getFccCentroCustos());
				processamentoAlerta.setQtdeOcorrencias(1);
				processamentoAlerta.setTipoAlerta(DominioSigTipoAlerta.NC);

				this.getProcessamentoCustoUtils().getSigProcessamentoAlertasDAO().persistir(processamentoAlerta);
			}
		}
	}

	/**
	 * Alerta CR: Geração de alertas onde o cálculo do custo de um objeto de custo foi feito apenas por rateio, pois o mesmo não tinha nenhuma atividade na sua composição.
	 * @param processamentoCusto
	 * @param servidor
	 * @author agerling
	 */
	private void gerarAlertaCR(SigProcessamentoCusto processamentoCusto, RapServidores servidor) {

		List<ObjetoCustoProducaoExamesVO> listVO = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().pesquisarCentroCustoCalcRateio(processamentoCusto.getSeq());

		if (listVO != null && !listVO.isEmpty()) {
			for (ObjetoCustoProducaoExamesVO vo : listVO) {
				SigProcessamentoAlertas processamentoAlerta = new SigProcessamentoAlertas();
				processamentoAlerta.setCriadoEm(new Date());
				processamentoAlerta.setRapServidores(servidor);
				processamentoAlerta.setSigProcessamentoCustos(processamentoCusto);
				processamentoAlerta.setFccCentroCustos(vo.getFccCentroCustos());
				processamentoAlerta.setTipoAlerta(DominioSigTipoAlerta.CR);
				processamentoAlerta.setQtdeOcorrencias(vo.getQtdeInteira().intValue());

				this.getProcessamentoCustoUtils().getSigProcessamentoAlertasDAO().persistir(processamentoAlerta);
			}
		}
	}

	/**
	 * Alerta QA: Geração de alertas sobre a quantidade, de insumos ou tempo das pessoas, de uma atividade de um objeto de custo foi ajustada durante os
	 * cálculos, pois o valor informado excedeu os valores disponíveis na área de insumos ou tempo trabalhado das pessoas.
	 * @param processamentoCusto
	 * @param servidor
	 * @author agerling
	 */
	private void gerarAlertaQA(SigProcessamentoCusto processamentoCusto, RapServidores servidor) {
		List<ObjetoCustoProducaoExamesVO> listVO = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
				.pesquisarCentroCustoAtvAjusteQtde(processamentoCusto.getSeq());
		if (listVO != null && !listVO.isEmpty()) {
			for (ObjetoCustoProducaoExamesVO vo : listVO) {
				SigProcessamentoAlertas processamentoAlerta = new SigProcessamentoAlertas();
				processamentoAlerta.setCriadoEm(new Date());
				processamentoAlerta.setRapServidores(servidor);
				processamentoAlerta.setSigProcessamentoCustos(processamentoCusto);
				processamentoAlerta.setFccCentroCustos(vo.getFccCentroCustos());
				processamentoAlerta.setTipoAlerta(DominioSigTipoAlerta.QA);
				processamentoAlerta.setQtdeOcorrencias(vo.getQtdeInteira().intValue());
				this.getProcessamentoCustoUtils().getSigProcessamentoAlertasDAO().persistir(processamentoAlerta);
			}
		}
	}
	
}