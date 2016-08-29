package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.scheduler.AutomaticJobEnum;
import br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLogGeracaoScMatEstocavelDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.BaseCalculoSaldoAfVO;
import br.gov.mec.aghu.compras.vo.GeraSolicCompraEstoqueVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Movido de br.gov.mec.aghu.compras.business.ScoSolicitacaoDeCompraRN. .
 */
@Stateless
public class GerarSolicitacaoCompraAlmoxarifadoRN extends BaseBusiness {

	@EJB
	private SolicitacaoCompraRN solicitacaoCompraRN;
	@EJB
	private ScoLogGeracaoScMatEstocavelRN scoLogGeracaoScMatEstocavelRN;
	
	private static final Log LOG = LogFactory.getLog(GerarSolicitacaoCompraAlmoxarifadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;
	
	@Inject
	private ScoLogGeracaoScMatEstocavelDAO scoLogGeracaoScMatEstocavelDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private static final long serialVersionUID = -6943869303983504689L;

	private static final String DESTINO = "PLAN";
	private static final String DESCRICAO_SOLICITACAO_COMPRA = "**";
	private static final String JUSTIFICATIVA_DE_USO_2 = "REPOSIÇÃO AUTOMÁTICA NO SUB-ALMOXARIFADO";
	private static final String JUSTIFICATIVA_DE_USO_1 = "REPOSIÇÃO AUTOMÁTICA NO ALMOX CENTRAL";
	private static final String ORCAMENTO_PREVIO = "N";
	private static final String DIAS_DURACAO = "999";

	/**
	 * Dispara manualmente o processo de geracao de SC automatica para material estocavel para o almoxarifado central
	 * @param dtAnalise
	 * @return
	 * @throws BaseException
	 */
	public List<String> gerarSolicitacaoCompraAlmox(Date dtAnalise)
			throws BaseException {
		
		AghParametros parametroAlmoxarifadoFarmaciaCentral = this
				.getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_ALMOX_FARM_CENTRAL);

		SceAlmoxarifado almoxarifado = getEstoqueFacade()
				.obterAlmoxarifadoPorSeq(
						parametroAlmoxarifadoFarmaciaCentral.getVlrNumerico()
								.shortValue());

		return gerarSolicitacaoCompraAlmox(dtAnalise, almoxarifado);
	}

	/**
	 * Dispara manualmente o processo de geracao de SC automatica para material estocavel por almoxarifado
	 * @param dtAnalise
	 * @param almox
	 * @return
	 * @throws BaseException
	 */
	public List<String> gerarScRepAutomatica(Date dtAnalise,
			SceAlmoxarifado almox)
			throws BaseException {
		return gerarSolicitacaoCompraAlmox(dtAnalise, almox);
	}
	
	/**
	 * Atualiza o parametro cadastrado como horario para execucao automatica da geracao de SC para material estocavel
	 * @param horaAgendamento
	 * @throws BaseException
	 */
	public void atualizarHorarioAgendamentoGeracaoAutomaticaSolCompras(Date horaAgendamento) throws BaseException {
		AghParametros parametroHorarioAgendamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORA_AGENDA_SC);
		
		if (horaAgendamento != null) {
			parametroHorarioAgendamento.setVlrData(horaAgendamento);
			parametroHorarioAgendamento.setVlrNumerico(null);
		}
		else {
			parametroHorarioAgendamento.setVlrData(null);
			parametroHorarioAgendamento.setVlrNumerico(null);
		}
		this.getParametroFacade().setAghpParametro(parametroHorarioAgendamento);
		
		if (horaAgendamento != null) {
			// verificar se já existia agendamento prévio.
			if (obterAgendamentoPrevio() == null) {
				// se não existia, agenda...
				this.agendarProcessamento(horaAgendamento);
			} else {
				// se existia, tem que reagendar...
				this.reagendarProcessamento(horaAgendamento);
			}
		} else {
			this.removerAgendamento();
		}

	}

	private Date agendarProcessamento(Date horaAgendamento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		IAutomaticJobEnum automaticJobEnum = AutomaticJobEnum.SCHEDULERINIT_GERACAO_AUTOMATICA_SOL_COMPRAS_MAT_ESTOCAVEL;

		// Seconds Minutes Hours Day-of-Month Month Day-of-Week
		automaticJobEnum.setCron(obterCron(horaAgendamento));
		
		this.logInfo(" # Agendando rotina de geração automática de solicitações de compras de material estocável...");
		this.logInfo(" crontab = "+automaticJobEnum.getCron());
		this.getSchedulerFacade().agendarRotinaAutomatica(automaticJobEnum, automaticJobEnum.getCron(), null, null, servidorLogado);
		
		return horaAgendamento;
	}

	private void reagendarProcessamento(Date horaAgendamento) {
		AghJobDetail jobDetail = obterAgendamentoPrevio();
		
		if (jobDetail != null) {
			this.logInfo(" # Reagendando rotina de geração automática de solicitações de compras de material estocável...");
	
			try {
				this.getSchedulerFacade().reAgendar(jobDetail, obterCron(horaAgendamento), null);
				this.logInfo(" # Reagendado com sucesso...");
			} catch (BaseException e) {
				this.logError("Erro ao reagendar processo solicitacaoComprasFacade.gerarSolicitacaoComprasMaterialEstocavel!");
			}
		} else {
			this.logInfo(" # Não existe job de geração automática de solicitações de compras de material estocável...");
		}
	}
	
	private String obterCron(Date horaAgendamento) {
		String cron = "";
		
		if (horaAgendamento != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(horaAgendamento);
			
			Integer hora = calendar.get(Calendar.HOUR_OF_DAY);
			Integer minuto = calendar.get(Calendar.MINUTE);
			//Seconds Minutes Hours Day-of-Month Month Day-of-Week
			cron = "0 "+minuto.intValue()+" "+hora.intValue()+" * * ? *";
		}
		
		return cron;
	}
	
	private void removerAgendamento() {
		AghJobDetail jobDetail = obterAgendamentoPrevio();
		
		if (jobDetail != null) {
			this.logInfo(" # Removendo rotina de geração automática de solicitações de compras de material estocável...");
			try {
				this.getSchedulerFacade().removerAghJobDetail(jobDetail, Boolean.FALSE);
				this.logInfo(" # job excluido com sucesso com sucesso...");
			} catch (BaseException e) {
				this.logError("Erro ao reagendar processo solicitacaoComprasFacade.gerarSolicitacaoComprasMaterialEstocavel!");
			}
		} else {
			this.logInfo(" # Não existe job de geração automática de solicitações de compras de material estocável...");
		}
	}
	
	/**
	 * Atualiza o parametro cadastrado como horario para execucao automatica da geracao de SC para material estocavel
	 * @param msg
	 * @throws ApplicationBusinessException
	 */
	public void atualizarUltimaExecucaoGeracaoAutomaticaSolCompras(String msg) throws ApplicationBusinessException {
		AghParametros parametroHorarioAgendamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORA_AGENDA_SC);
		parametroHorarioAgendamento.setVlrTexto(msg);
		this.getParametroFacade().setAghpParametro(parametroHorarioAgendamento);
	}

	/**
	 * Obtem o parametro cadastrado como horario para execucao automatica da geracao de SC para material estocavel
	 * @return Date
	 * @throws ApplicationBusinessException
	 * @throws ParseException
	 */
	public Date carregarHorarioAgendamentoGeracaoAutomaticaSolCompras() throws ApplicationBusinessException, ParseException {
		Date horarioAgendamento = null;
		
		AghParametros parametroHorarioAgendamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORA_AGENDA_SC);
		
		if (parametroHorarioAgendamento != null && parametroHorarioAgendamento.getVlrData() != null) {
		    horarioAgendamento = parametroHorarioAgendamento.getVlrData();
		}
		
	    return horarioAgendamento;
	}
	
	/**
	 * Método que dispara o processo de geracao de SC para material estocavel 
	 * @return QuartzTriggerHandle
	 
	@Asynchronous
	public QuartzTriggerHandle gerarSolicitacaoComprasMaterialEstocavel() {
		
		List<String> resultadoGeracao = null;
		
		this.logInfo("Rotina GerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoComprasMaterialEstocavel iniciada em: "
				+ Calendar.getInstance().getTime());
		
		try {
			resultadoGeracao = gerarSolicitacaoCompraAlmox(new Date());
		} catch (BaseException e) {
			this.logError("Rotina GerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoComprasMaterialEstocavel: Erro ao gerar solicitações de compras:" + e.getMessage());
		}

		if (resultadoGeracao == null) {
			this.logInfo("Rotina GerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoComprasMaterialEstocavel não gerou solicitações! ");
		} else {
			this.logInfo("Rotina GerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoComprasMaterialEstocavel: ");
			for (String item : resultadoGeracao) {
				this.logInfo(item);
			}
		}
		
		this.logInfo("Rotina GerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoComprasMaterialEstocavel finalizada em: "
				+ Calendar.getInstance().getTime());

		return null;
	}
	*/
	
	public AghJobDetail obterAgendamentoPrevio() {
		IAutomaticJobEnum automaticJobEnum = AutomaticJobEnum.SCHEDULERINIT_GERACAO_AUTOMATICA_SOL_COMPRAS_MAT_ESTOCAVEL;
		return this.getSchedulerFacade().obterAghJobDetailPorNome(automaticJobEnum.getTriggerName());	
	}

	/**
	 * Grava no banco um resumo do processamento por material
	 * @param qtdProcessar
	 * @param dtAnalise
	 * @param almoxarifado
	 * @return
	 */
	private ScoLogGeracaoScMatEstocavel obterLogGeracao(Integer seqProcesso, Integer qtdProcessar, Date dtAnalise, SceAlmoxarifado almoxarifado) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoLogGeracaoScMatEstocavel logGeracao = new ScoLogGeracaoScMatEstocavel();
		logGeracao.setSeqProcesso(seqProcesso);
		logGeracao.setServidor(servidorLogado);
		logGeracao.setDtGeracao(new Date());
		logGeracao.setQtdeAProcessar(qtdProcessar);
		logGeracao.setDtAnalise(dtAnalise);
		logGeracao.setAlmoxarifado(almoxarifado);
		logGeracao.setTexto("");
		return logGeracao;
	}
	
	/**
	 * ORADB SCOP_GERA_SLTC_ALMOX GERA SOLICITAÕES DE COMPRAS PARA MATERIAIS
	 * ESTOCÁVEIS NO ALMOXARIFADO
	 * 
	 * @return
	 * 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<String> gerarSolicitacaoCompraAlmox(Date dtAnalise,
			SceAlmoxarifado almoxarifado) throws BaseException {
		ICentroCustoFacade centroCustoFacade = this.getCentroCustoFacade();
		IEstoqueFacade estoqueFacade = this.getEstoqueFacade();
		Integer cctCodigoSolicMatEstoc = null;
		Integer cctCodigoAplicMatEstoc = null;
		Integer cctCodigoSolic = null;
		Integer qtdeCompra = null;
		Integer qtdeRefSc = null;
		Integer qtdeEmAf = null;
		Integer qtdeEmSc = null;
		Short ppsCodigo = null;
		Short ppsCodigoLocProx = null;
		Integer qtdeScContrato = 0;
		Double valorUnitPrevisto = 0.0;
		RapServidores servidorAutorizada;
		Date dtAutorizacao;
		Integer qtdePropLidas = 0;
		Integer qtdeProp = 0;
		Double valorUnitarioAcumulado = 0.0;
		String descricao;
		String texto = "";
		Integer qtdeScGerada = 0;
		Integer seqProcesso = getScoLogGeracaoScMatEstocavelDAO().obterMaxProcesso();
		
		if (dtAnalise == null) {
			throw new ApplicationBusinessException(
					GerarSolicitacaoCompraAlmoxarifadoRNExceptionCode.DT_ANALISE_VAZIA);
		}

		excluirRegistrosLogAnteriorParametro();
		eliminaReforcosNaoLiberadosParaAf(dtAnalise);
		
		List<GeraSolicCompraEstoqueVO> geraScs = obterMateriaisQueNecessitamGeracaoSc(almoxarifado);

		// Busca Ccusto do Planejamento de compras
		AghParametros centroCustoSolic = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_SOLIC);
		cctCodigoSolicMatEstoc = centroCustoSolic.getVlrNumerico().intValue();
		
		// Busca Ccusto do Almox Central
		AghParametros centroCustoAplic = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_APLIC);
		cctCodigoAplicMatEstoc = centroCustoAplic.getVlrNumerico().intValue();
		int qtdeMateriaisProcessados = 0;

		if (geraScs == null || geraScs.isEmpty()) {
			ScoLogGeracaoScMatEstocavel logGeracao = obterLogGeracao(seqProcesso, 0, dtAnalise, almoxarifado);
			getScoLogGeracaoScMatEstocavelRN().persistir(logGeracao);
		}
		for (GeraSolicCompraEstoqueVO geraSc : geraScs) {
			ScoLogGeracaoScMatEstocavel logGeracao = obterLogGeracao(seqProcesso, geraScs.size(), dtAnalise, geraSc.getAlmoxarifado());
			
			if (DESTINO.equals(geraSc.getDestino())) {
				cctCodigoSolic = cctCodigoSolicMatEstoc;
			} else {
				cctCodigoSolic = geraSc.getCctCodigoAplic().getCodigo();
			}

			// Repõe no máximo o Ponto de Pedido
			qtdeCompra = geraSc.getQtdePontoPedido();
			logGeracao.setMaterial(geraSc.getMatCodigo());
			logGeracao.setQtdeBloqueada((Integer) CoreUtil.nvl(geraSc.getQtdeBloqueada(), 0));
			logGeracao.setQtdeDisponivel((Integer) CoreUtil.nvl(geraSc.getQtdeDisponivel(), 0));

			if (geraSc.getSlcNumero() != null) {
				geraSc.setDestino(DESTINO);
				cctCodigoSolic = cctCodigoSolicMatEstoc;
				
				// Busca qtde reforço da SC, adiada e ainda não liberada para a
				// AF (pelo Planejamento)
				qtdeRefSc = DateUtil.validaDataMaior(
						DateUtil.truncaData(geraSc.getSlcNumero().getDtAnalise()),
						DateUtil.truncaData(new Date())) ? geraSc.getSlcNumero()
						.getQtdeReforco().intValue() : 0;
						
				qtdeEmSc = getScoSolicitacoesDeComprasDAO()
						.buscarQtdeSaldoScNaoAf(geraSc.getMatCodigo());
				
				qtdeEmAf = getSaldo(getScoFaseSolicitacaoDAO()
						.buscarSaldoEmAfSemContrato(
								geraSc.getSlcNumero().getNumero(),
								geraSc.getMatCodigo().getCodigo()));
				
				// SC de fundo fixo é gerada para compra de material de pequeno
				// valor para o qual não é gerado um processo de compra e que
				// entra via ESL tipo FF (Fundo Fixo). A SC é vinculada ao ESL
				// gerado e este por sua vez dará origem a entrada pelo
				// Recebimento da NF (Entrada não via AF)
				Integer qtdeEmAfProg = getSaldo(getScoFaseSolicitacaoDAO()
						.buscarSaldoEmAfSemParcelas(geraSc.getSlcNumero()));
				
				qtdeCompra = geraSc.getQtdePontoPedido() - qtdeEmSc - qtdeEmAf - qtdeEmAfProg;

				logGeracao.setSolicitacaoCompra(geraSc.getSlcNumero());
				logGeracao.setQtdePontoPedido(geraSc.getQtdePontoPedido());
				logGeracao.setQtdeEmSc(qtdeEmSc);
				logGeracao.setQtdeEmFundoFixo(qtdeEmAfProg);
				logGeracao.setQtdeEmAf(qtdeEmAf);
				logGeracao.setQtdeAComprar(qtdeCompra);
				logGeracao.setQtdeReforcoSc(qtdeRefSc);
				if (qtdeCompra < 0) {
					logGeracao.setTexto("vou zerar qtdeCompra");
					qtdeCompra = 0;
				}
			} else {
				Integer qtdPendenteSc = getScoSolicitacoesDeComprasDAO()
						.buscarQtdeSaldoScNaoAf(geraSc.getMatCodigo());
				
				// Vai ver saldo da AF em Contrato para ver se deverá repor material
				// Busca qtde saldo em  AF (AE/PA)
				qtdeEmAf = getSaldo(getScoFaseSolicitacaoDAO().buscarItensScEmAf(geraSc.getMatCodigo()));
				
				// Calcula a qtde a ser solicitada
				qtdeCompra = geraSc.getQtdePontoPedido() - qtdPendenteSc - qtdeEmAf;

				logGeracao.setQtdeAComprar(qtdeCompra);
				logGeracao.setQtdePontoPedido(geraSc.getQtdePontoPedido());
				logGeracao.setQtdeEmSc(qtdPendenteSc);
				logGeracao.setQtdeEmAf(qtdeEmAf);
			}

			// Emite Compra ou Reforço.
			if (qtdeCompra > 0) {
				logGeracao.getTexto().concat("qtdeCompra > 0 VOU GERAR.  ");
				
				// Busca o ponto de parada Geração automática.
				AghParametros pontoParadaGeraAutomaticaParam = this
						.getParametroFacade().buscarAghParametro(
								AghuParametrosEnum.PPS_GER_AUTO);
				
				ppsCodigo = pontoParadaGeraAutomaticaParam.getVlrNumerico()
						.shortValue();

				// Busca o ponto de parada do Planejamento de compras.
				AghParametros pontoParadaLocProxParam = this
						.getParametroFacade().buscarAghParametro(
								AghuParametrosEnum.PPS_PLANEJAMENTO);
				
				ppsCodigoLocProx = pontoParadaLocProxParam.getVlrNumerico()
						.shortValue();

				if (geraSc.getSlcNumero() != null) {
					logGeracao.getTexto().concat("TEM NUMERO DE SC VOU DAR UPDATE NO PONTO DE PARADA E NO REFORCO...");

					ScoSolicitacaoDeCompra solicitacao = geraSc.getSlcNumero();
					ScoSolicitacaoDeCompra solicitacaoClone = getSolicitacaoCompraRN().clonarSolicitacaoDeCompra(solicitacao);
					
					if (solicitacao.getDtAnalise() == null) {
						ScoPontoParadaSolicitacao pontoParada = getScoPontoParadaSolicitacaoDAO()
								.obterPorChavePrimaria(ppsCodigo);
						
						solicitacao.setPontoParada(pontoParada);
						
						ScoPontoParadaSolicitacao pontoParadaLocProx = getScoPontoParadaSolicitacaoDAO()
								.obterPorChavePrimaria(ppsCodigoLocProx);
						
						solicitacao.setPontoParadaProxima(pontoParadaLocProx);						
						solicitacao.setDtAnalise(new Date());
						
						getSolicitacaoCompraRN().inserirAtualizacaoSolicitacaoComprasJournal(solicitacao.getNumero(), pontoParadaLocProx);

					}

					// verifica se a SC esta em contrato e se a quantidade eh multipla do fator de conversao do fornecedor
					// se nao for, arredonda para o proximo multiplo
					Integer qtdMultipla = this.calcularReforcoMultiplo(solicitacao, qtdeCompra);
					
					// Atualiza a Qtde (não considera mais a qtde anterior).
					solicitacao.setQtdeReforco(Long.valueOf(qtdMultipla));
					
					getSolicitacaoCompraRN().atualizarSolicitacaoCompra(solicitacao, solicitacaoClone);
					
					qtdeScContrato++;
				} else {
					logGeracao.getTexto().concat("NAO TEM NUMERO DE SC VOU INSERIR A SC PARA MATERIAL = "+geraSc.getMatCodigo());

					// Busca matrícula/vínculo responsável Planejamento
					FccCentroCustos centroCustos = centroCustoFacade
							.obterFccCentroCustos(cctCodigoSolic);
					
					servidorAutorizada = centroCustos.getRapServidor();
					dtAutorizacao = new Date();

					// Busca último custo entrada do material (via NR).
					AghParametros parametroTipoMovimento = this
							.getParametroFacade().buscarAghParametro(
									AghuParametrosEnum.P_TMV_DOC_NR);
					
					valorUnitPrevisto = estoqueFacade
							.buscarUltimoCustoEntradaPorMaterialTipoMov(geraSc
									.getMatCodigo(), parametroTipoMovimento
									.getVlrNumerico().shortValue());

					qtdePropLidas = 0;
					qtdeProp = 0;
					valorUnitarioAcumulado = 0.0;

					List<Double> valoresUnitario = getScoItemPropostaFornecedorDAO()
							.buscarValorUnitarioPropostas2Anos(
									geraSc.getMatCodigo());
					
					for (Double valorUnitario : valoresUnitario) {
						qtdePropLidas++;
						
						if (qtdePropLidas < 11) {
							qtdeProp++;
							valorUnitarioAcumulado += valorUnitario;
						}
					}

					if (qtdeProp > 0) {
						valorUnitPrevisto = valorUnitarioAcumulado / qtdeProp;
					} else {
						valorUnitPrevisto = 1.0;
					}

					if (geraSc.getMatCodigo().getIndGenerico()
							.equals(DominioSimNao.S)) {
						descricao = DESCRICAO_SOLICITACAO_COMPRA;
					} else {
						descricao = "";
					}

					if (geraSc.getCctCodigoAplic().getCodigo()
							.equals(cctCodigoAplicMatEstoc)) {
						texto = JUSTIFICATIVA_DE_USO_1;
					} else {
						texto = JUSTIFICATIVA_DE_USO_2;
					}

					// Se o CCusto não tiver Usuário responsável, usa usuário
					// autorizante OU conectado.
					if (servidorAutorizada == null) {
						RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
						servidorAutorizada = servidorLogado;
					}

					ScoSolicitacaoDeCompra solicitacaoDeCompra = new ScoSolicitacaoDeCompra();

					ScoPontoParadaSolicitacao pontoParada = getScoPontoParadaSolicitacaoDAO()
							.obterPorChavePrimaria(ppsCodigo);
					solicitacaoDeCompra.setPontoParada(pontoParada);

					ScoPontoParadaSolicitacao pontoParadaLocProx = getScoPontoParadaSolicitacaoDAO()
							.obterPorChavePrimaria(ppsCodigoLocProx);
					solicitacaoDeCompra
							.setPontoParadaProxima(pontoParadaLocProx);

					solicitacaoDeCompra.setServidor(servidorAutorizada);
					solicitacaoDeCompra.setMaterial(geraSc.getMatCodigo());

					solicitacaoDeCompra.setCentroCusto(centroCustos);
					solicitacaoDeCompra.setCentroCustoAplicada(geraSc
							.getCctCodigoAplic());
					solicitacaoDeCompra
							.setServidorAutorizacao(servidorAutorizada);
					solicitacaoDeCompra.setDtSolicitacao(new Date());
					solicitacaoDeCompra.setDtDigitacao(new Date());
					solicitacaoDeCompra.setQtdeSolicitada(Long
							.valueOf(qtdeCompra));
					solicitacaoDeCompra.setQtdeAprovada(Long
							.valueOf(qtdeCompra));
					solicitacaoDeCompra.setExclusao(false);
					solicitacaoDeCompra.setUrgente(false);
					solicitacaoDeCompra.setDevolucao(false);
					solicitacaoDeCompra.setOrcamentoPrevio(ORCAMENTO_PREVIO);
					solicitacaoDeCompra.setDiasDuracao(Short
							.valueOf(DIAS_DURACAO));
					solicitacaoDeCompra.setQtdeReforco(Long.valueOf(0));
					solicitacaoDeCompra.setJustificativaUso(texto);
					solicitacaoDeCompra.setDtAutorizacao(dtAutorizacao);
					solicitacaoDeCompra.setValorUnitPrevisto(new BigDecimal(
							valorUnitPrevisto));
					solicitacaoDeCompra.setDescricao(descricao);
					solicitacaoDeCompra.setGeracaoAutomatica(true);
					solicitacaoDeCompra.setQtdeEntregue(Long.valueOf(0));
					solicitacaoDeCompra.setEfetivada(false);
					solicitacaoDeCompra.setFundoFixo(false);
					solicitacaoDeCompra.setUnidadeMedida(geraSc.getUmdCodigo());
					solicitacaoDeCompra.setRecebimento(false);
					solicitacaoDeCompra.setAlmoxarifado(null);
					solicitacaoDeCompra.setPrioridade(Boolean.FALSE);

					aplicarRegrasOrcamentarias(solicitacaoDeCompra);
					
					getSolicitacaoCompraRN().inserirSolicitacaoCompra(solicitacaoDeCompra, false);
					qtdeScGerada++;
				}
			}
			
			getScoLogGeracaoScMatEstocavelRN().persistir(logGeracao);
			qtdeMateriaisProcessados ++;
		}

		List<String> mensagemConfirmacao = new ArrayList<String>();
		
		String msg = getResourceBundleValue(GerarSolicitacaoCompraAlmoxarifadoRNExceptionCode.MSG_001
						.toString());
		mensagemConfirmacao.add(msg);
		msg = getResourceBundleValue(GerarSolicitacaoCompraAlmoxarifadoRNExceptionCode.MSG_002
						.toString());
		mensagemConfirmacao.add(MessageFormat.format(msg, qtdeMateriaisProcessados));
		msg = getResourceBundleValue(GerarSolicitacaoCompraAlmoxarifadoRNExceptionCode.MSG_003
						.toString());
		mensagemConfirmacao.add(MessageFormat.format(msg, qtdeScGerada));
		msg = getResourceBundleValue(GerarSolicitacaoCompraAlmoxarifadoRNExceptionCode.MSG_004
						.toString());
		mensagemConfirmacao.add(MessageFormat.format(msg, qtdeScContrato));

		return mensagemConfirmacao;
	}

	private Integer calcularReforcoMultiplo(ScoSolicitacaoDeCompra solicitacaoCompra, Integer qtdReforco) {
		ScoItemAutorizacaoForn itemAf = getAutFornecimentoFacade().obterItemAfPorSolicitacaoCompra(solicitacaoCompra.getNumero(), false, false);
		
		if (itemAf != null && itemAf.getIndContrato()) {
			Integer resto = qtdReforco % (Integer) CoreUtil.nvl(itemAf.getFatorConversaoForn(), 1);

			if (resto != 0) {
				qtdReforco = ((qtdReforco / (Integer) CoreUtil.nvl(itemAf.getFatorConversaoForn(), 1)) + 1) * itemAf.getFatorConversaoForn();
			}
		}		

		return qtdReforco;
	}
	
	/**
	 * Obtem o saldo a partir de itens de AF.
	 * 
	 * @param base Base de Cálculo
	 * @return Saldo
	 */
	private Integer getSaldo(List<BaseCalculoSaldoAfVO> base) {
		Integer saldo = 0;
		
		for (BaseCalculoSaldoAfVO item : base) {
			Integer x = item.getQtdeSolicitada();
			
			if (item.getQtdeRecebida() != null) {
				x -= item.getQtdeRecebida();
			}
			
			x *= item.getFatorConversao();
			saldo += x;
		}

		return saldo;
	}

	/**
	 * Obtem materiais que necessitam a geração de Solicitação de compras.
	 * 
	 * @param almoxarifado Almoxarifado.
	 * @return Materiais que necessitam a geração de Solicitação de compras.
	 * @throws ApplicationBusinessException
	 */
	private List<GeraSolicCompraEstoqueVO> obterMateriaisQueNecessitamGeracaoSc(
			SceAlmoxarifado almoxarifado) throws ApplicationBusinessException {

		SceAlmoxarifado almoxCentral = getEstoqueFacade()
				.obterAlmoxarifadoPorSeq(
						getParametroFacade()
								.buscarAghParametro(
										AghuParametrosEnum.P_ALMOX_CENTRAL)
								.getVlrNumerico().shortValue());

		Integer fornecedorPadraoId = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)
				.getVlrNumerico().intValue();

		List<GeraSolicCompraEstoqueVO> union2 = getEstoqueFacade()
				.obterMateriaisGeracaoSc(almoxarifado, almoxCentral, fornecedorPadraoId);
		
		return union2;
	}

	/**
	 * Elimina todos os reforços gerados e não liberados para AF e não adiadas
	 * para análise posterior, para geração de novo reforço com o saldo atual.
	 * 
	 * @param dtAnalise
	 * @throws BaseException
	 */
	private void eliminaReforcosNaoLiberadosParaAf(Date dtAnalise)
			throws BaseException {
		List<ScoSolicitacaoDeCompra> solicitacaoDeCompras = getScoSolicitacoesDeComprasDAO()
				.pesquisarSolicitacaoDeCompraPorDtAnalise(dtAnalise);

		for (ScoSolicitacaoDeCompra solicitacao : solicitacaoDeCompras) {
			ScoSolicitacaoDeCompra scOriginal = getScoSolicitacoesDeComprasDAO().obterOriginal(solicitacao);
			solicitacao.setDtAnalise(null);
			solicitacao.setQtdeReforco(Long.valueOf(0));			
			getSolicitacaoCompraRN().atualizarSolicitacaoCompra(solicitacao, scOriginal);
			getAutFornecimentoFacade().excluirParcelasPendentes(solicitacao);
		}
	}

	/**
	 * Aplica regras orçamentárias à SC.
	 * 
	 * @param sc
	 *            SC.
	 */
	public void aplicarRegrasOrcamentarias(ScoSolicitacaoDeCompra sc) {
		// Centro de Custo Aplicação
		boolean setaCct = true;

		if (sc.getCentroCustoAplicada() != null) {
			setaCct = !getCadastrosBasicosOrcamentoFacade()
					.isCentroCustoValidScParam(sc.getMaterial(),
							sc.getCentroCusto(), sc.getValorTotal(),
							sc.getCentroCustoAplicada());
		}

		if (setaCct) {
			FccCentroCustos cct = getCadastrosBasicosOrcamentoFacade()
					.getCentroCustoScParam(sc.getMaterial(),
							sc.getCentroCusto(), sc.getValorTotal());

			sc.setCentroCustoAplicada(cct);
		}

		// Grupo de Natureza
		FsoGrupoNaturezaDespesa gnd = getCadastrosBasicosOrcamentoFacade()
				.getGrupoNaturezaScParam(sc.getMaterial(), sc.getCentroCusto(),
						sc.getValorTotal());

		if (gnd != null) {
			// Natureza
			FsoNaturezaDespesa ntd = getCadastrosBasicosOrcamentoFacade()
					.getNaturezaScParam(sc.getMaterial(), sc.getCentroCusto(),			
							gnd, sc.getValorTotal());
			sc.setGrupoNaturezaDespesa(gnd);
			sc.setNaturezaDespesa(ntd);
		}

		// Verba de Gestão
		FsoVerbaGestao vbg = getCadastrosBasicosOrcamentoFacade()
				.getVerbaGestaoScParam(sc.getMaterial(), sc.getCentroCusto(),
						sc.getValorTotal());

		sc.setVerbaGestao(vbg);
	}

	private void excluirRegistrosLogAnteriorParametro() throws ApplicationBusinessException {
		AghParametros parametroDias = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_EXC_LOG_SC);
		
		if (parametroDias != null) {
			List<ScoLogGeracaoScMatEstocavel> listaRegistrosLog = this.getScoLogGeracaoScMatEstocavelDAO().pesquisarRegistrosLogPorNumDias(parametroDias.getVlrNumerico().intValue());
			
			if (listaRegistrosLog != null) {
				for (ScoLogGeracaoScMatEstocavel reg : listaRegistrosLog) {
					this.getScoLogGeracaoScMatEstocavelDAO().remover(reg);
				}
			}
		}
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SolicitacaoCompraRN getSolicitacaoCompraRN() {
		return solicitacaoCompraRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}

	protected ScoLogGeracaoScMatEstocavelDAO getScoLogGeracaoScMatEstocavelDAO() {
		return scoLogGeracaoScMatEstocavelDAO;
	}
	
	protected ScoLogGeracaoScMatEstocavelRN getScoLogGeracaoScMatEstocavelRN() {
		return scoLogGeracaoScMatEstocavelRN;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}

	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	protected ISchedulerFacade getSchedulerFacade() {
		return this.schedulerFacade;
	}

	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return this.cadastrosBasicosOrcamentoFacade;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public enum GerarSolicitacaoCompraAlmoxarifadoRNExceptionCode implements
			BusinessExceptionCode {
		DT_ANALISE_VAZIA, MSG_001, MSG_002, MSG_003, MSG_004;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
}