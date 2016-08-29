package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAFParcelasDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoManualVO;
import br.gov.mec.aghu.compras.vo.HorarioSemanaVO;
import br.gov.mec.aghu.compras.vo.ModalAlertaGerarVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoManualParcelasEntregaFiltroVO;
import br.gov.mec.aghu.dominio.DominioDiaSemanaAbreviado;
import br.gov.mec.aghu.dominio.DominioFormaProgramacao;
import br.gov.mec.aghu.dominio.DominioParcela;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AFProgramacaoManualON extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final long serialVersionUID = -148027677264781838L;
	
	@Inject 
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject 
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;
	
	@Inject 
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject 
	private ScoProgEntregaItemAFParcelasDAO scoProgEntregaItemAFParcelasDAO;	
	
	@EJB
	private AFProgramacaoManualDiasEspecificosRN aFProgramacaoManualDiasEspecificosRN;
	
	@EJB
	private AFProgramacaoManualIntervaloParcelasRN aFProgramacaoManualIntervaloParcelasRN;
	
	
	private static final Log LOG = LogFactory.getLog(AFProgramacaoManualON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}	
	
	public enum AFProgramacaoManualONExceptionCode implements BusinessExceptionCode {
		QUANTIDADE_PARCELA_SUPERIOR_SALDO, QUANTIDADE_PARCELA_SUPERIOR_LIMITE, QUANTIDADE_NAO_MULTIPLA_FC,
		DATA_PRIMEIRA_ENTREGA_INFERIOR, AFP_NAO_ENTREGUE, SELECIONE_ITENS, FILTRO_NAO_INFORMADO, HORARIO_QUANTIDADE_NAO_INFORMADO,
		DIA_SEMANA_NAO_SELECIONADO
	}
	
	public void validarPesquisa(ProgramacaoManualParcelasEntregaFiltroVO filtro) throws ApplicationBusinessException {
		if (filtro.getAutorizacaoForn() == null && filtro.getFornecedor() == null
				&& filtro.getSituacaoProgramacao() == null && filtro.getMaterial() == null && filtro.getGrupoMaterial() == null) {
			
			throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.FILTRO_NAO_INFORMADO);
		}
	}

	public List<ConsultaItensAFProgramacaoManualVO> consultarItensAFProgramacaoManual(final Integer numeroItem, final Integer numeroAF,
			final Short numeroComplemento, final Integer numeroFornecedor, final Integer codigoMaterial,
			final Integer codigoGrupoMaterial, final Boolean isIndProgramado) throws ApplicationBusinessException {
		
		List<ConsultaItensAFProgramacaoManualVO> listVO = getScoItemAutorizacaoFornDAO().consultarItensAFProgramacaoManual(numeroItem,
				numeroAF, numeroComplemento, numeroFornecedor, codigoMaterial, codigoGrupoMaterial, isIndProgramado);
		return formatarConsultaItensAFProgramacaoManualVO(listVO);
		
	}
	
	private List<ConsultaItensAFProgramacaoManualVO> formatarConsultaItensAFProgramacaoManualVO(
			List<ConsultaItensAFProgramacaoManualVO> listVO) {

		for (ConsultaItensAFProgramacaoManualVO vo : listVO) {
			vo.setPossuiParcela(getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.verificarParcelasNaoCanceladas(vo.getAfnNumero(), vo.getNumeroItem()));
			
			Long saldo = vo.getQtdeSolicitada() - (vo.getQtde() != null ? vo.getQtde() : 0);
			vo.setSaldo(saldo);
			
			if (vo.getSaldo() < 0L) {
				vo.setSaldo(0L);
			}
			vo.setSelecionado(vo.getSaldo() > 0L);
		}
		return listVO;
	}
	
	public void gerarProgramacao(ProgramacaoManualParcelasEntregaFiltroVO filtro, List<ConsultaItensAFProgramacaoManualVO> listaItensAF) throws BaseException {
		
		List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas = new ArrayList<ConsultaItensAFProgramacaoManualVO>();
		for (ConsultaItensAFProgramacaoManualVO parcela : listaItensAF) {
			if (parcela.getSelecionado().equals(Boolean.TRUE)) {
				listaParcelasSelecionadas.add(parcela);
			}
		}
		// Deleta as parcelas assinadas.
		excluirParcelasAssinadas(listaParcelasSelecionadas);
		
		if (filtro.getUrgencia() != null) {
			for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
				ScoItemAutorizacaoFornId id = new ScoItemAutorizacaoFornId();
				id.setAfnNumero(parcela.getAfnNumero());
				id.setNumero(parcela.getNumeroItem());
				ScoItemAutorizacaoForn itemAf = getScoItemAutorizacaoFornDAO().obterPorChavePrimaria(id);
				
				Date dataEntregaCalculada = null;
				if (itemAf.getAutorizacoesForn() != null && 
					itemAf.getAutorizacoesForn().getPropostaFornecedor() != null &&
					itemAf.getAutorizacoesForn().getPropostaFornecedor().getPrazoEntrega() != null) {
					Short prazoEntrega = itemAf.getAutorizacoesForn().getPropostaFornecedor().getPrazoEntrega();
					dataEntregaCalculada =  DateUtil.adicionaDias(new Date(), prazoEntrega.intValue());
				} else {
					dataEntregaCalculada = new Date();
				}
				aFProgramacaoManualDiasEspecificosRN.gerarParcela(dataEntregaCalculada, filtro.getQuantidade(),
						parcela, filtro.getUrgencia());
			}
			
		} else if (filtro.getTipoParcela().equals(DominioParcela.IP)) {
			aFProgramacaoManualIntervaloParcelasRN.gerarParcelasPorIntervalo(filtro, listaParcelasSelecionadas);
			
		} else if (filtro.getTipoParcela().equals(DominioParcela.DE)) {
			aFProgramacaoManualDiasEspecificosRN.gerarParcelasPorDiasEspecificos(filtro, listaParcelasSelecionadas);
		}
	}
	
	public ModalAlertaGerarVO preGerarProgramacao(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaItensAF, ModalAlertaGerarVO modalAlertaGerarVO) throws ApplicationBusinessException {
		
		modalAlertaGerarVO = new ModalAlertaGerarVO();
//		modalAlertaGerarVO.setTitulo("Confirmar geração");
		
		List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas = new ArrayList<ConsultaItensAFProgramacaoManualVO>();
		if (listaItensAF != null) {
			for (ConsultaItensAFProgramacaoManualVO parcela : listaItensAF) {

				// impede que solicite quantidade de parcela maior que o saldo disponível
				// caso a forma de programação seja "PS" (programar todo o saldo), este campo ficará null.
				if (filtro.getQtdeLimiteInteiro() != null) {
					if (filtro.getQtdeLimiteInteiro() > parcela.getSaldo()) {
						throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_SALDO);
					}
				}

				if (parcela.getSelecionado().equals(Boolean.TRUE)) {
					listaParcelasSelecionadas.add(parcela);
				}
			}
		}
		if (listaParcelasSelecionadas.isEmpty()) {
			throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.SELECIONE_ITENS);
		}
		if (filtro.getUrgencia() != null) {
			// RN01
			validarQuantidadeUrgencia(filtro.getQuantidade(), listaParcelasSelecionadas);
			
		} else if (filtro.getTipoParcela().equals(DominioParcela.IP)) {
			// RN08
			validarDtPrimeiraEntrega(filtro.getDtPrimeiraEntrega());
			validarQuantidadeParcelasPorIntervalo(filtro, listaParcelasSelecionadas);
		} else {
			// RN09
			validaPreenchimentoHorarios(filtro);
			validarDtPrimeiraEntrega(filtro.getDtPrimeiraEntrega());
			validarQuantidadeParcelasPorDiaEspecifico(filtro, listaParcelasSelecionadas);
			validarFatorConversaoPorDiaEspecifico(filtro, listaParcelasSelecionadas);
		}
		// RN07
		return verificarParcelasAssinadasNaoEntregues(listaParcelasSelecionadas, modalAlertaGerarVO);
	}
	
	private ModalAlertaGerarVO verificarParcelasAssinadasNaoEntregues(List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas,
			ModalAlertaGerarVO modalAlertaGerarVO) throws ApplicationBusinessException {
		
		List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelasExcluir = new ArrayList<ScoProgEntregaItemAutorizacaoFornecimento>();
		StringBuilder alertaItens = new StringBuilder();
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelasAssinadas = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
					.verificaParcelasAssinadasNaoEntregues(parcela.getAfnNumero(), parcela.getNumeroItem());
			if (listaParcelasAssinadas != null && !listaParcelasAssinadas.isEmpty()) {
				alertaItens.append(montarMensagemAlertaComParcelas(listaParcelasAssinadas, parcela, alertaItens)).append("<br/>");
				listaParcelasExcluir.addAll(listaParcelasAssinadas);
				listaParcelasAssinadas.clear();
			}
		}
		if (listaParcelasExcluir != null && !listaParcelasExcluir.isEmpty()) {
			StringBuilder alerta = new StringBuilder(167);
			alerta.append("Existem parcelas assinadas e enviadas ao fornecedor que ainda não foram entregues:<br/>Autorização de Fornecimento/Cp - Item - Prev. Entrega - Qtde Pendente<br/>")
			.append(alertaItens.toString())
			.append("Deseja excluir possíveis parcelas ainda não assinadas e gerar novas parcelas de entrega de acordo com os critérios especificados, ignorando as entregas já solicitadas?");
			
			modalAlertaGerarVO.setAlerta(alerta.toString());
			
		} else {
			modalAlertaGerarVO.setAlerta("Deseja excluir possíveis parcelas ainda não assinadas e gerar novas parcelas de entrega" +
			" de acordo com os critérios especificados?");
		}
		return modalAlertaGerarVO;
	}
	
	private String montarMensagemAlertaComParcelas(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelasAssinadas,
			ConsultaItensAFProgramacaoManualVO parcela, StringBuilder alertaItens) {
		
		for (ScoProgEntregaItemAutorizacaoFornecimento itemAssinado : listaParcelasAssinadas) {
			Integer qtde = itemAssinado.getQtde() != null ? itemAssinado.getQtde() : 0;
			Integer qtdeEntregue = itemAssinado.getQtdeEntregue() != null ? itemAssinado.getQtdeEntregue() : 0;
			Integer qtdePendente = qtde - qtdeEntregue;
			
			alertaItens.append(parcela.getLctNumero()).append('/').append(parcela.getNroComplemento()).append(_HIFEN_)
				.append(parcela.getNumeroItem()).append(_HIFEN_).append(parcela.getNomeMaterial()).append(_HIFEN_)
				.append(DateUtil.obterDataFormatada(itemAssinado.getDtPrevEntrega(), "dd/MM/yy HH:mm")).append(_HIFEN_)
				.append(qtdePendente);
		}
		return alertaItens.toString();
	}
	
	private void validarDtPrimeiraEntrega(Date dtPrimeiraEntrega) throws ApplicationBusinessException {
		if (DateUtil.validaDataMenor(dtPrimeiraEntrega, new Date())) {
			throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.DATA_PRIMEIRA_ENTREGA_INFERIOR);
		}
	}
	
	private void validarQuantidadeUrgencia(Integer quantidade,
			List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws ApplicationBusinessException {
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			if (quantidade > parcela.getSaldo()) {
				throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_SALDO);
			}
		}
	}
	
	private void validarQuantidadeParcelasPorIntervalo(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws ApplicationBusinessException {
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			if (filtro.getQtdeParcelas() > parcela.getSaldo()) {
				throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_SALDO);
			}
			if (filtro.getFormaProgramacao().equals(DominioFormaProgramacao.QL) && filtro.getQtdeParcelas() > filtro.getQtdeLimiteInteiro()) {
				throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_LIMITE);
				
			} else if (filtro.getFormaProgramacao().equals(DominioFormaProgramacao.VL)) {
				if ((BigDecimal.valueOf(parcela.getValorUnitario()).multiply(BigDecimal.valueOf(filtro.getQtdeParcelas())))
						.compareTo(BigDecimal.valueOf(filtro.getQtdeLimiteMonetario())) > 0) {
					throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_LIMITE);
				}
			}
			Integer resto = filtro.getQtdeParcelas() % parcela.getFatorConversao();
			if (resto > 0) {
				throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_NAO_MULTIPLA_FC,
						parcela.getLctNumero(), parcela.getNroComplemento(), parcela.getNumeroItem(), parcela.getNomeMaterial());
			}
		}
	}
	
	private void validaPreenchimentoHorarios(ProgramacaoManualParcelasEntregaFiltroVO filtro) throws ApplicationBusinessException {
		if (filtro.getListaDiasSemana().isEmpty()) {
			throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.DIA_SEMANA_NAO_SELECIONADO);
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SAB)) {
			validaHorariosDiaSemana(filtro.getListaHorarioSabado());
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.DOM)) {
			validaHorariosDiaSemana(filtro.getListaHorarioDomingo());
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEG)) {
			validaHorariosDiaSemana(filtro.getListaHorarioSegunda());
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.TER)) {
			validaHorariosDiaSemana(filtro.getListaHorarioTerca());
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUA)) {
			validaHorariosDiaSemana(filtro.getListaHorarioQuarta());
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUI)) {
			validaHorariosDiaSemana(filtro.getListaHorarioQuinta());
		}
		if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEX)) {
			validaHorariosDiaSemana(filtro.getListaHorarioSexta());
		}
	}
	
	private void validaHorariosDiaSemana(List<HorarioSemanaVO> horarios) throws ApplicationBusinessException {
		boolean exibirErro = false;
		for (HorarioSemanaVO item : horarios) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				exibirErro = true;
				
			} else if (item.getHorario() != null && item.getQuantidadeItem() != null) {
				exibirErro = false;
				break;
			}
		}
		if (exibirErro) {
			throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.HORARIO_QUANTIDADE_NAO_INFORMADO);
		}
	}
	
	private void validarQuantidadeParcelasPorDiaEspecifico(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws ApplicationBusinessException {
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SAB)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioSabado(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
				
			} else if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.DOM)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioDomingo(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
				
			} else if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEG)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioSegunda(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
				
			} else if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.TER)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioTerca(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
				
			} else if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUA)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioQuarta(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
				
			} else if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUI)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioQuinta(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
				
			} else if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEX)) {
				
				validarQtdeValorLimitePorDiaEspecifico(filtro.getFormaProgramacao(), filtro.getListaHorarioSexta(),
						parcela.getSaldo(), filtro.getQtdeLimiteInteiro(), parcela.getValorUnitario(), filtro.getQtdeLimiteMonetario());
			}
			
		}
	}
	
	private void validarQtdeValorLimitePorDiaEspecifico(DominioFormaProgramacao formaProgramacao, List<HorarioSemanaVO> listHorarios,
			Long saldo, Integer qtdeLimiteInteiro, Double valorUnitario, Double qtdeLimiteMonetario) throws ApplicationBusinessException {
		
		for (HorarioSemanaVO item : listHorarios) {
			if (item.getHorario() != null && item.getQuantidadeItem() != null) {
				if (item.getQuantidadeItem() > saldo) {
					throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_SALDO);
				}
				
				if (formaProgramacao.equals(DominioFormaProgramacao.QL) && item.getQuantidadeItem() > qtdeLimiteInteiro) {
					throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_LIMITE);

				} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
					if ((BigDecimal.valueOf(valorUnitario).multiply(BigDecimal.valueOf(item.getQuantidadeItem())))
							.compareTo(BigDecimal.valueOf(qtdeLimiteMonetario)) > 0) {
						throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_PARCELA_SUPERIOR_LIMITE);
					}
				}
			}
		}
		
	}
	
	private void validarFatorConversaoPorDiaEspecifico(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws ApplicationBusinessException {
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SAB)) {
				validarFatorConversaoItem(filtro.getListaHorarioSabado(), parcela);
				
			}
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.DOM)) {
				validarFatorConversaoItem(filtro.getListaHorarioDomingo(), parcela);
			}
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEG)) {
				validarFatorConversaoItem(filtro.getListaHorarioSegunda(), parcela);
			}
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.TER)) {
				validarFatorConversaoItem(filtro.getListaHorarioTerca(), parcela);
			}
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUA)) {
				validarFatorConversaoItem(filtro.getListaHorarioQuarta(), parcela);
			}
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUI)) {
				validarFatorConversaoItem(filtro.getListaHorarioQuinta(), parcela);
			}
			if (filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEX)) {
				validarFatorConversaoItem(filtro.getListaHorarioSexta(), parcela);
			}
		}
	}
	
	private void validarFatorConversaoItem(List<HorarioSemanaVO> listHorarios, ConsultaItensAFProgramacaoManualVO parcela) throws ApplicationBusinessException {
		for (HorarioSemanaVO item : listHorarios) {
			if (item.getHorario() != null && item.getQuantidadeItem() != null) {
				Integer resto = item.getQuantidadeItem() % parcela.getFatorConversao();
				if (resto > 0) {
					throw new ApplicationBusinessException(AFProgramacaoManualONExceptionCode.QUANTIDADE_NAO_MULTIPLA_FC,
							parcela.getLctNumero(), parcela.getNroComplemento(), parcela.getNumeroItem(), parcela.getNomeMaterial());
				}
			}
		}
	}
	
	private void excluirParcelasAssinadas(List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws ApplicationBusinessException {
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			final Integer iafAfnNumero = parcela.getAfnNumero();
			final Integer iafNumero = parcela.getNumeroItem();
			
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelasExcluir = getScoProgEntregaItemAFParcelasDAO()
				.listarProgEntregaItemAf(iafAfnNumero, iafNumero);
			
			for (ScoProgEntregaItemAutorizacaoFornecimento itemExcluir : listaParcelasExcluir) {
				final Integer iafAfnNumeroSolic = itemExcluir.getId().getIafAfnNumero();
				final Integer iafNumeroSolic = itemExcluir.getId().getIafNumero();
				final Integer seqSolic = itemExcluir.getId().getSeq();
				final Integer parcelaSolic = itemExcluir.getId().getParcela();
				
				List<ScoSolicitacaoProgramacaoEntrega> listProgEntregaExcluir = getScoSolicitacaoProgramacaoEntregaDAO()
					.pesquisarSolicitacaoProgEntregaPorItemProgEntrega(iafAfnNumeroSolic, iafNumeroSolic, seqSolic, parcelaSolic);
				
				for (ScoSolicitacaoProgramacaoEntrega itemProgEntregaExcluir : listProgEntregaExcluir) {
					getScoSolicitacaoProgramacaoEntregaDAO().remover(itemProgEntregaExcluir);
				}
				getScoSolicitacaoProgramacaoEntregaDAO().flush();
				
				getScoProgEntregaItemAutorizacaoFornecimentoDAO().remover(itemExcluir);
				getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
			}
		}
	}
		
	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	private ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}
	
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
	
	private ScoProgEntregaItemAFParcelasDAO getScoProgEntregaItemAFParcelasDAO() {
		return scoProgEntregaItemAFParcelasDAO;
	}	
}
