package br.gov.mec.aghu.compras.autfornecimento.business;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoParamProgEntgAfDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.AlteracaoEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ParcelasEntregaMatDiretoVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.model.ScoParamProgEntgAfId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ParcelasEntregaMatDiretoRN extends BaseBusiness {

	private static final long serialVersionUID = -7303680621952306703L;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject
	private ScoParamProgEntgAfDAO scoParamProgEntgAfDAO;

	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;

	@Inject
	private SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO;
	
	private static final Log LOG = LogFactory.getLog(ParcelasEntregaMatDiretoRN.class);
	
	
	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO(){
		return scoSolicitacoesDeComprasDAO;
	}
	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
		
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO(){
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
	private ScoParamProgEntgAfDAO getScoParamProgEntgAfDAO(){
		return scoParamProgEntgAfDAO;
	}
	
	/**
	 * RN1 
	 * @param entregaProgramada
	 * @param afNumero
	 * @param usuarioLogado
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AlteracaoEntregaProgramadaVO gerarParcelas(DominioSimNao entregaProgramada, Integer afNumero) throws ApplicationBusinessException {
		Boolean existeItemSaldoNProgramado, existeParcNCanceladas;
		if (entregaProgramada == null) {
			return new AlteracaoEntregaProgramadaVO(true, "");
		}
		//1. Se o campo Entrega Programada (AF.IND_ENTREGA_PROGRAMADA) estiver sendo modificado para N, fazer:
		if(!entregaProgramada.isSim()) {
			//1.1. Executar a C7, passando como parâmetro a PK da AF em edição, se esta retornar registros, 
			//Executar a C1, passando como parâmetro a PK da AF em edição, se esta também retornar registros, fazer:
			existeParcNCanceladas = getScoProgEntregaItemAutorizacaoFornecimentoDAO().verificarParcelasNaoCanceladas(afNumero, null);
			if(existeParcNCanceladas) {
				existeItemSaldoNProgramado =  getScoItemAutorizacaoFornDAO().verificarItemConsumoDiretoSaldoAFNaoProg(afNumero);
				//1.1.1. Emitir a mensagem de confirmação: M1, se o usuário confirmar executar a RN02, senão emitir a M2 e não modificar o campo.
				if(existeItemSaldoNProgramado) {
					return new AlteracaoEntregaProgramadaVO(false, true, "M1_GPEMD");
				} else {
				//1.1.2. Se C1 não retornar registros emitir a M3 e não permitir a alteração do campo.
					return new AlteracaoEntregaProgramadaVO(false, "M3_GPEMD");
				}
				//1.2. Se C7 não retornar registros, permitir a modificação do campo.
			} else {
				return new AlteracaoEntregaProgramadaVO(true, "");
			}
			//2. Se o campo Entrega Programada (AF.IND_ENTREGA_PROGRAMADA) estiver sendo modificado para S, fazer:
		} else {
			//2.1. Executar a C1, passando como parâmetro a PK da AF em edição, se esta retornar registros, executar a RN02.
			existeItemSaldoNProgramado =  getScoItemAutorizacaoFornDAO().verificarItemConsumoDiretoSaldoAFNaoProg(afNumero);
			if(existeItemSaldoNProgramado) {
				gerarProgEntregaMatDireto(afNumero, true, false);
			}
		}
		return new AlteracaoEntregaProgramadaVO(true, "");
	}
	
	/**
	 * RN 2
	 * @param afNumero
	 * @param usuarioLogado
	 * @param gerarProgramacao
	 * @param usarMensagem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AlteracaoEntregaProgramadaVO gerarProgEntregaMatDireto(Integer afNumero, Boolean gerarProgramacao, Boolean usarMensagem) throws ApplicationBusinessException {
		//Gera_progr_entg_mat_direto
		//	1. Executar a C1, passando como parâmetro a PK da AF em edição.
		//2. Se C1 retornar registros, executar C2 e para cada registro retornado por C2, fazer:
		if (gerarProgramacao) {
			List<ParcelasEntregaMatDiretoVO> itensAFMateriaisDireto = getScoItemAutorizacaoFornDAO().buscarItensAFMaterialDireto(afNumero);
			if(itensAFMateriaisDireto != null && !itensAFMateriaisDireto.isEmpty()) {
				for (ParcelasEntregaMatDiretoVO item : itensAFMateriaisDireto) {
					Integer iafAfnNumero = item.getIafAfnNumero();
					Integer iafNumero = item.getIafNumero();
					//2.1. Executar D1
					deletaRelacaoParcelaSolicProgramacaoEntrega(item);
					deletaRelacaoParcelaItemRecebXProgEntrega(item);
					deletarParcelasCanceladasOuNaoAssinadas(item);
					//2.2. Se é material consignado (Se C2. iaf.ind_contrato = ‘S’ e C2. iaf.ind_consignado = ‘S’ e C2. 
					//mat.gmt_codigo in (Valores do Parâmetro P_AGHU_GRUPO_MATERIAIS_CONSIGNADOS)), fazer:
					if (isMaterialConsignado(item)) {
						//2.2.1. Executar a C6, para obter a quantidade da parcela zero (QTD_PARCELA_ZERO).
						setQtdParcelaZero(item, iafAfnNumero, iafNumero);
						//2.2.2. NUM_PARCELA recebe 0 (Zero).
						setNrParcelaItem(item, 0);
					} else {
						//2.3. Se não é consignado, fazer:
						//2.3.1. Executar C3 para obter o número da próxima parcela (NUM_PARCELA).
						setNrParcelaItem(item, getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterProxParcelaPorItemAF(iafAfnNumero, iafNumero));
					}				
					//2.4. Executar C4 para obter o número da próxima seq (NUM_SEQ)
					setSeqItem(item, iafAfnNumero, iafNumero);
					//2.5. Executar C5 para obter o saldo (QTD_SALDO)
					setQtdSaldo(item, iafAfnNumero, iafNumero);
					//2.6.Se a quantidade do item em questão (C2.qtde_saldo_item) for maior que QTD_SALDO:
					//2.6.1. QTD_PARCELA = C2.qtde_saldo_item - QTD_SALDO
					//2.7. Senão:
					//2.7.1.  QTD_PARCELA = 0
					setQtdParcelaItem(item);
					//2.8. Se QTD_PARCELA for maior que ZERO e QTD_PARCELA_ZERO for igual a ZERO.
					//2.8.1. Executar o I1
					//2.8.2. Executar o D2
					//2.8.3. Executar o I2
					//2.9. Se QTD_PARCELA for maior que ZERO e QTD_PARCELA_ZERO também for maior que ZERO, fazer:
					//2.9.1. Executar o U1
					verificarQtdParcela(item);
				}
			}
			return new AlteracaoEntregaProgramadaVO(true, "");
		} else if (usarMensagem){
			return new AlteracaoEntregaProgramadaVO(false, "M2_GPEMD");
		}
		return new AlteracaoEntregaProgramadaVO(true, "");
	}
	
	private void deletaRelacaoParcelaSolicProgramacaoEntrega(
			ParcelasEntregaMatDiretoVO item)
			throws ApplicationBusinessException {
		Integer registrosDel = getScoSolicitacaoProgramacaoEntregaDAO()
				.deletaRelacaoParcelaSolicProgramacaoEntrega(
						item.getIafAfnNumero(), item.getIafNumero(),
						item.getNumeroParcela(), item.getSeq());
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		getLogger().info(
				"Quantidade de parcelas deletadas SCO_SOLIC_PROGR_ENTREGA: "
						+ registrosDel);
	}

	private void deletaRelacaoParcelaItemRecebXProgEntrega(
			ParcelasEntregaMatDiretoVO item)
			throws ApplicationBusinessException {
		Integer registrosDel = getSceItemRecbXProgrEntregaDAO().deletaRelacaoParcelaItemRecebXProgEntrega(
						item.getIafAfnNumero(), item.getIafNumero(),
						item.getNumeroParcela(), item.getSeq());
		getSceItemRecbXProgrEntregaDAO().flush();
		getLogger().info(
				"Quantidade de parcelas deletadas SCE_ITEM_RECB_X_PROGR_ENTREGAS: "
						+ registrosDel);
	}
	
	private void verificarQtdParcela(ParcelasEntregaMatDiretoVO item) throws ApplicationBusinessException {
		
		if((item.getQtdParcela() != null &&  item.getQtdParcela() > 0) && (item.getQtdParcelaZero() != null && item.getQtdParcelaZero() == 0)) {
			inserirParcela(criarParcela(item));
			deletarRelacaoParcelaSolicitacaoCompra(item);
			inserirParamProgEntrega(item);
			getScoParamProgEntgAfDAO().flush();	
		} else if((item.getQtdParcela() != null && item.getQtdParcela() > 0) && (item.getQtdParcelaZero()!= null && item.getQtdParcelaZero() > 0)) {
			atualizarSaldoParcela(item, item.getQtdParcela());
		}
	}
	
	private void inserirParamProgEntrega(ParcelasEntregaMatDiretoVO item) {
		getScoParamProgEntgAfDAO().persistir(criarScoParamProgEntgAf(item));
	}
	
	private void deletarRelacaoParcelaSolicitacaoCompra(ParcelasEntregaMatDiretoVO item) throws ApplicationBusinessException {
		Integer registrosDel =  getScoParamProgEntgAfDAO().deletarRelacaoParcelaSolicitacaoCompra(item.getSclNumero());
		getScoParamProgEntgAfDAO().flush();
		getLogger().info("Registros deletados da tabela SCO_PARAM_PROG_ENTG_AF: " + registrosDel); 	
	}

	private void setQtdParcelaItem(ParcelasEntregaMatDiretoVO item) {		
		if(item.getQtdSaldoItem() > item.getQtdSaldo()) {
			Integer qtdParcela = item.getQtdSaldoItem() - item.getQtdSaldo();  
			item.setQtdParcela(qtdParcela);
		} else {
			item.setQtdParcela(0);
		}
	}
	private void setNrParcelaItem(ParcelasEntregaMatDiretoVO item, Integer numeroParcela) {
		item.setNumeroParcela(numeroParcela);
	}
	private void setQtdSaldo(ParcelasEntregaMatDiretoVO item, Integer iafAfnNumero, Integer iafNumero) {
		Integer qtdSaldo;
		qtdSaldo = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterSaldoPorItemAF(iafAfnNumero, iafNumero);
		item.setQtdSaldo(qtdSaldo);
	}
	private void setSeqItem(ParcelasEntregaMatDiretoVO item, Integer iafAfnNumero, Integer iafNumero) {
		Integer seq;
		seq = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterProxSeqPorItemAF(iafAfnNumero, iafNumero);
		item.setSeq(seq);
	}
	private void setQtdParcelaZero(ParcelasEntregaMatDiretoVO item, Integer iafAfnNumero, Integer iafNumero) {
		Integer qtdParcelaZero;
		qtdParcelaZero = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQtdPrimeiraParcelaPorItemAF(iafAfnNumero, iafNumero);
		item.setQtdParcelaZero(qtdParcelaZero);
	}
	
	public boolean isMaterialConsignado(ParcelasEntregaMatDiretoVO item) throws ApplicationBusinessException {
		return (item.getIafIndContrato() && item.getIafIndConsignado() && isGrupoMaterialConsignado(item.getCodGrupoMaterial()));
	}
	
	private boolean isGrupoMaterialConsignado(Integer codigoGM) throws ApplicationBusinessException {
		List<String> materiaisConsignado = obterGrupoMateriaisConsignados();
		if(materiaisConsignado == null || materiaisConsignado.isEmpty()) {
			return false;
		}
		return materiaisConsignado.contains(codigoGM);
	}
	
	private void deletarParcelasCanceladasOuNaoAssinadas(ParcelasEntregaMatDiretoVO item) throws ApplicationBusinessException {
		Integer registrosDel = getScoProgEntregaItemAutorizacaoFornecimentoDAO().deletarParcelasCanceladasNaoAssinadasItemAF(item.getIafAfnNumero(),item.getIafNumero());
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		getLogger().info("Quantidade de parcelas deletadas SCO_PROGR_ENTREGA_ITENS_AF: " + registrosDel);
	}
	
	/**
	 * Inserir parcela.   
	 * @param parcela parcela a ser inserida
	 */
	private void inserirParcela(ScoProgEntregaItemAutorizacaoFornecimento parcela) {		
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(parcela);
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		getLogger().info("Parcela inserida na SCO_PROGR_ENTREGA_ITENS_AF");
	}

	private ScoProgEntregaItemAutorizacaoFornecimento criarParcela(ParcelasEntregaMatDiretoVO item) {
		ScoProgEntregaItemAutorizacaoFornecimento parcela = new ScoProgEntregaItemAutorizacaoFornecimento();
		criarIdParcela(item, parcela);
		Date diaUtil = obterProxDiaUtilMaisTres();
		
		parcela.setScoItensAutorizacaoForn(getScoItemAutorizacaoForn(item));
		parcela.setDtGeracao(new Date());
		parcela.setDtPrevEntrega(diaUtil);
		parcela.setQtde(item.getQtdParcela());
		parcela.setRapServidor(getServidorLogadoFacade().obterServidorLogado());
		parcela.setIndPlanejamento(false);
		parcela.setIndAssinatura(false);
		parcela.setIndEmpenhada(DominioAfEmpenhada.N);
		parcela.setIndEnvioFornecedor(false);
		parcela.setIndRecalculoAutomatico(false);
		parcela.setIndRecalculoManual(false);
		parcela.setValorTotal(calculaValorTotal(item.getQtdParcela(), item.getIafValorUnitario()));
		parcela.setIndImpressa(false);
		parcela.setIndCancelada(false);
		parcela.setDtNecessidadeHcpa(diaUtil);
		parcela.setIndEfetivada(false);
		parcela.setIndEntregaImediata(false);
		parcela.setIndTramiteInterno(false);
		return parcela;
	}

	private void criarIdParcela(ParcelasEntregaMatDiretoVO item,
			ScoProgEntregaItemAutorizacaoFornecimento parcela) {
		parcela.setId(new ScoProgEntregaItemAutorizacaoFornecimentoId());
		parcela.getId().setIafAfnNumero(item.getIafAfnNumero());
		parcela.getId().setIafNumero(item.getIafNumero());
		parcela.getId().setParcela(item.getNumeroParcela());
		parcela.getId().setSeq(item.getSeq());
	}
	
	private Double calculaValorTotal(Integer qtdParcela, Double valorUnitario) {
		if (qtdParcela != null && valorUnitario != null) {
			Double calculo  = qtdParcela * valorUnitario;
			return formatarValorTotal(calculo);
		}
		return null;
	}
	
	private Double formatarValorTotal(Double valorTotal){
		 DecimalFormat formatador = new DecimalFormat();
		 formatador.setMinimumFractionDigits(2);
		 formatador.setMaximumFractionDigits(2);
		 String valorStr = formatador.format(valorTotal);
		 valorStr = valorStr.replaceAll(",", ".");
         return  Double.parseDouble(valorStr);
	}
	
	//I2	
	private ScoParamProgEntgAf criarScoParamProgEntgAf(ParcelasEntregaMatDiretoVO item) {
		ScoParamProgEntgAf scoParamProgEntgAf = new ScoParamProgEntgAf();
		Short um = Short.valueOf("1");
		scoParamProgEntgAf.setId(new ScoParamProgEntgAfId(item.getSclNumero(), um));
		scoParamProgEntgAf.setMatCodigo(item.getMatCodigo());
		scoParamProgEntgAf.setQtdeTotalItem(item.getIafQtdSolicitada());
		scoParamProgEntgAf.setUmdCodigo(item.getIafUmdCod());
		scoParamProgEntgAf.setNroParcelas(um);
		scoParamProgEntgAf.setDtInicioEntrega(new Date());
		scoParamProgEntgAf.setScoItemAutorizacaoForn(getScoItemAutorizacaoForn(item));
		scoParamProgEntgAf.setScoSolicitacaoDeCompra(getSolicitacaoCompra(item));
		scoParamProgEntgAf.setObservacao("Geração Automática Mat Direto");
		return  scoParamProgEntgAf;
	}
	
	private ScoItemAutorizacaoForn getScoItemAutorizacaoForn(ParcelasEntregaMatDiretoVO item) {		
		return getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(item.getIafAfnNumero(), item.getIafAfnNumero());
	}
	
	private ScoSolicitacaoDeCompra getSolicitacaoCompra(ParcelasEntregaMatDiretoVO item) {
		return getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item.getSclNumero());
	}

	private void atualizarSaldoParcela(ParcelasEntregaMatDiretoVO item, Integer qtdParcela) {
		ScoProgEntregaItemAutorizacaoFornecimento itemProg = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.obterProgramacaoEntregaItemAFPorItemAfNumeroParcela(item.getIafAfnNumero(), item.getIafNumero(), 0);
		if (itemProg != null) {
			calcularValorTotalProg(itemProg, qtdParcela, item);
			calcularQtdItemProgEntrega(itemProg, qtdParcela);
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(itemProg);
			
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();	
			getLogger().info("Saldo do item iafAfnNumero: " +  item.getIafAfnNumero() + " iafNumero " + item.getIafNumero() + " parcela: 0. Atualizado!");
		}
	}
	
	private void calcularValorTotalProg(ScoProgEntregaItemAutorizacaoFornecimento itemProg, Integer qtdParcela, ParcelasEntregaMatDiretoVO item) {
		Double valorTotal = itemProg.getValorTotal();
		valorTotal = (Double) CoreUtil.nvl(valorTotal, 0);
		Double valorUnitario = item.getIafValorUnitario();
		if(valorTotal != null && qtdParcela != null && valorUnitario != null) {
			Double resultado = valorTotal + (qtdParcela * valorUnitario);
			itemProg.setValorTotal(resultado);
		}
	}

	private void calcularQtdItemProgEntrega(ScoProgEntregaItemAutorizacaoFornecimento itemProg, Integer qtdParcela) {
		Integer qtd = itemProg.getQtde();
		qtd = (Integer) CoreUtil.nvl(qtd, 0);
		if (qtd !=null && qtdParcela !=null) {
			itemProg.setQtde(qtd + qtdParcela);
		}
	}
	
	private Date obterProxDiaUtilMaisTres() {
		DateTime diaUtil = obterProximoDiaUtilMaisUm(new DateTime());
		return adicionarDiasDataTime(diaUtil, 3);
	}

	private DateTime obterProximoDiaUtilMaisUm(DateTime date) {
		date = date.plusDays(1);
		if(DateUtil.isFinalSemana(date.toDate()) || isFeriado(date.toDate())) {
			return obterProximoDiaUtilMaisUm(date);
		}
		return date;
	}
	
	private Date adicionarDiasDataTime(DateTime data, int qtdDias){
		return data.plusDays(qtdDias).toDate();
	}
	
	private boolean isFeriado(Date date){
		AghFeriados feriado = getAghuFacade().obterFeriado(date);
		return feriado != null && feriado.getData() != null;
	}
	
	private List<String> obterGrupoMateriaisConsignados() throws ApplicationBusinessException{ 
		String grupoMatConsignados = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_GRUPO_MATERIAIS_CONSIGNADOS);		
		return Arrays.asList(grupoMatConsignados.split(","));
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	private ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO(){
		return scoSolicitacaoProgramacaoEntregaDAO;
	}

	private SceItemRecbXProgrEntregaDAO getSceItemRecbXProgrEntregaDAO(){
		return sceItemRecbXProgrEntregaDAO;
	}
}
