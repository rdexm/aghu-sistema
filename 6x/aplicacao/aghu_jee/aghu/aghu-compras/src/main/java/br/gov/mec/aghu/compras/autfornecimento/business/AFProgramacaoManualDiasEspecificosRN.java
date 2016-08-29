package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoManualVO;
import br.gov.mec.aghu.compras.vo.HorarioSemanaVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoManualParcelasEntregaFiltroVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioDiaSemanaAbreviado;
import br.gov.mec.aghu.dominio.DominioFormaProgramacao;
import br.gov.mec.aghu.dominio.DominioUrgencia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AFProgramacaoManualDiasEspecificosRN extends BaseBusiness {

	private static final long serialVersionUID = -148027677264781838L;	
	private static final Log LOG = LogFactory.getLog(AFProgramacaoManualDiasEspecificosRN.class);

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	@Override
	@Deprecated
	
	protected Log getLogger() {
		return LOG;
	}
	/**
	 * Classe utilizada para armazenar temporariamente
	 * os saldos e limites informados na tela.
	 * 
	 * @author israel.haas
	 *
	 */
	private class SaldosLimites {
		
		private Date dtPrimeiraEntrega;
		private Long saldoFinal;
		private Double limiteMonetario;
		private Integer numeroLimite;
		private ScoProgEntregaItemAutorizacaoFornecimento ultimaParcela;
		
		public SaldosLimites() {
		}
		public Date getDtPrimeiraEntrega() {
			return dtPrimeiraEntrega;
		}
		public void setDtPrimeiraEntrega(Date dtPrimeiraEntrega) {
			this.dtPrimeiraEntrega = dtPrimeiraEntrega;
		}
		public Long getSaldoFinal() {
			return saldoFinal;
		}
		public void setSaldoFinal(Long saldoFinal) {
			this.saldoFinal = saldoFinal;
		}
		public Double getLimiteMonetario() {
			return limiteMonetario;
		}
		public void setLimiteMonetario(Double limiteMonetario) {
			this.limiteMonetario = limiteMonetario;
		}
		public Integer getNumeroLimite() {
			return numeroLimite;
		}
		public void setNumeroLimite(Integer numeroLimite) {
			this.numeroLimite = numeroLimite;
		}
		public ScoProgEntregaItemAutorizacaoFornecimento getUltimaParcela() {
			return ultimaParcela;
		}
		public void setUltimaParcela(ScoProgEntregaItemAutorizacaoFornecimento ultimaParcela) {
			this.ultimaParcela = ultimaParcela;
		}
	}
	
	public void gerarParcelasPorDiasEspecificos(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws BaseException {
		
		DominioFormaProgramacao formaProgramacao = filtro.getFormaProgramacao();
		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(filtro.getDtPrimeiraEntrega());
		List<DominioDiaSemanaAbreviado> listaDiasSemana = filtro.getListaDiasSemana();
		DominioUrgencia urgencia = filtro.getUrgencia();
		SaldosLimites saldosLimites = new SaldosLimites();
		saldosLimites.setUltimaParcela(new ScoProgEntregaItemAutorizacaoFornecimento());
		
		for (ConsultaItensAFProgramacaoManualVO parcela : listaParcelasSelecionadas) {
			saldosLimites.setDtPrimeiraEntrega(filtro.getDtPrimeiraEntrega());
			
			if (formaProgramacao.equals(DominioFormaProgramacao.PS)) {
				saldosLimites.setSaldoFinal(parcela.getSaldo());
			} else if (formaProgramacao.equals(DominioFormaProgramacao.QL)) {
				saldosLimites.setSaldoFinal(filtro.getQtdeLimiteInteiro().longValue());
			} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
				saldosLimites.setLimiteMonetario(filtro.getQtdeLimiteMonetario());
				saldosLimites.setSaldoFinal(parcela.getSaldo());
			} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
				saldosLimites.setNumeroLimite(filtro.getQtdeLimiteInteiro());
				saldosLimites.setSaldoFinal(parcela.getSaldo());
			}
			if (diaSemana.equals(DominioDiaSemana.SABADO)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeSabado(listaDiasSemana, filtro, urgencia, saldosLimites, parcela,  formaProgramacao);
				}
			} else if (diaSemana.equals(DominioDiaSemana.DOMINGO)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeDomingo(listaDiasSemana, filtro, urgencia, saldosLimites, parcela, formaProgramacao);
				}
			} else if (diaSemana.equals(DominioDiaSemana.SEGUNDA)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeSegunda(listaDiasSemana, filtro, urgencia, saldosLimites, parcela, formaProgramacao);
				}
			} else if (diaSemana.equals(DominioDiaSemana.TERCA)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeTerca(listaDiasSemana, filtro, urgencia, saldosLimites, parcela, formaProgramacao);
				}
			} else if (diaSemana.equals(DominioDiaSemana.QUARTA)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeQuarta(listaDiasSemana, filtro, urgencia, saldosLimites, parcela, formaProgramacao);
				}
			} else if (diaSemana.equals(DominioDiaSemana.QUINTA)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeQuinta(listaDiasSemana, filtro, urgencia, saldosLimites, parcela, formaProgramacao);
				}
			} else if (diaSemana.equals(DominioDiaSemana.SEXTA)) {
				while (saldosLimites.getSaldoFinal() > 0) {
					gerarAPartirDeSexta(listaDiasSemana, filtro, urgencia, saldosLimites, parcela,  formaProgramacao);
				}
			}
		}
	}
	
	private void gerarAPartirDeSabado(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
		
	}
	
	private void gerarAPartirDeDomingo(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarAPartirDeSegunda(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarAPartirDeTerca(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarAPartirDeQuarta(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarAPartirDeQuinta(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarAPartirDeSexta(List<DominioDiaSemanaAbreviado> listaDiasSemana, ProgramacaoManualParcelasEntregaFiltroVO filtro,
			DominioUrgencia urgencia, SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemana(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela);
			
			gerarParcelasPorDiaSemanaVL(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEX, DominioDiaSemana.SEXTA,
					filtro.getListaHorarioSexta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SAB, DominioDiaSemana.SABADO,
					filtro.getListaHorarioSabado(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.DOM, DominioDiaSemana.DOMINGO,
					filtro.getListaHorarioDomingo(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.SEG, DominioDiaSemana.SEGUNDA,
					filtro.getListaHorarioSegunda(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.TER, DominioDiaSemana.TERCA,
					filtro.getListaHorarioTerca(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUA, DominioDiaSemana.QUARTA,
					filtro.getListaHorarioQuarta(), urgencia, saldosLimites, parcela, formaProgramacao);
			
			gerarParcelasPorDiaSemanaNDNP(listaDiasSemana, DominioDiaSemanaAbreviado.QUI, DominioDiaSemana.QUINTA,
					filtro.getListaHorarioQuinta(), urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarParcelasPorDiaSemana(List<DominioDiaSemanaAbreviado> listaDiasSemana, DominioDiaSemanaAbreviado diaSemana,
			DominioDiaSemana diaEntrega, List<HorarioSemanaVO> listaHorarios, DominioUrgencia urgencia, SaldosLimites saldosLimites,
			ConsultaItensAFProgramacaoManualVO parcela) throws BaseException {
		
		if (listaDiasSemana.contains(diaSemana)) {
			// Adiciona dias até chegar no dia da semana correto.
			while (!CoreUtil.retornaDiaSemana(saldosLimites.getDtPrimeiraEntrega()).equals(diaEntrega)) {
				saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), 1));
				saldosLimites.setDtPrimeiraEntrega(DateUtil.truncaData(saldosLimites.getDtPrimeiraEntrega()));
			}
			// Ordena a lista para gerar as parcelas corretamente
			ordenarListaHorarios(listaHorarios);
			for (HorarioSemanaVO item : listaHorarios) {
				if (item.getHorario() != null && item.getQuantidadeItem() != null) {
					if (saldosLimites.getSaldoFinal() > 0 && saldosLimites.getSaldoFinal() < item.getQuantidadeItem()) {
						atualizarUltimaParcela(saldosLimites.getUltimaParcela(), saldosLimites.getSaldoFinal(), null, parcela.getValorUnitario(), Boolean.FALSE);
						saldosLimites.setSaldoFinal(0L);
						
					} else if (saldosLimites.getSaldoFinal() > 0 && DateUtil.validaHoraMaior(item.getHorario(), saldosLimites.getDtPrimeiraEntrega())) {
						saldosLimites.setDtPrimeiraEntrega(DateUtil.comporDiaHora(saldosLimites.getDtPrimeiraEntrega(), item.getHorario()));
						
						saldosLimites.setSaldoFinal(saldosLimites.getSaldoFinal() - item.getQuantidadeItem());
						
						saldosLimites.setUltimaParcela(gerarParcela(saldosLimites.getDtPrimeiraEntrega(), item.getQuantidadeItem(),
								parcela, urgencia));
					}
				}
			}
			saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), 1));
			saldosLimites.setDtPrimeiraEntrega(DateUtil.truncaData(saldosLimites.getDtPrimeiraEntrega()));
		}
	}
	
	private void gerarParcelasPorDiaSemanaVL(List<DominioDiaSemanaAbreviado> listaDiasSemana, DominioDiaSemanaAbreviado diaSemana,
			DominioDiaSemana diaEntrega, List<HorarioSemanaVO> listaHorarios, DominioUrgencia urgencia, SaldosLimites saldosLimites,
			ConsultaItensAFProgramacaoManualVO parcela) throws BaseException {
		
		if (listaDiasSemana.contains(diaSemana)) {
			// Adiciona dias até chegar no dia da semana correto.
			while (!CoreUtil.retornaDiaSemana(saldosLimites.getDtPrimeiraEntrega()).equals(diaEntrega)) {
				saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), 1));
				saldosLimites.setDtPrimeiraEntrega(DateUtil.truncaData(saldosLimites.getDtPrimeiraEntrega()));
			}
			// Ordena a lista para gerar as parcelas corretamente
			ordenarListaHorarios(listaHorarios);
			for (HorarioSemanaVO item : listaHorarios) {
				if (item.getHorario() != null && item.getQuantidadeItem() != null) {
					
					BigDecimal itemTotal = BigDecimal.valueOf(parcela.getValorUnitario()).multiply(BigDecimal.valueOf(item.getQuantidadeItem()));
					if (BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(BigDecimal.ZERO) > 0
							&& saldosLimites.getSaldoFinal() > 0 && (saldosLimites.getSaldoFinal() < item.getQuantidadeItem()
							|| BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(itemTotal) < 0)) {
						
						atualizarUltimaParcela(saldosLimites.getUltimaParcela(), saldosLimites.getSaldoFinal(), saldosLimites.getLimiteMonetario(),
								parcela.getValorUnitario(), Boolean.TRUE);
						saldosLimites.setLimiteMonetario(0.00);
						saldosLimites.setSaldoFinal(0L);
						
					} else if (BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(BigDecimal.ZERO) > 0
							&& saldosLimites.getSaldoFinal() > 0
							&& DateUtil.validaHoraMaior(item.getHorario(), saldosLimites.getDtPrimeiraEntrega())) {
						
						saldosLimites.setDtPrimeiraEntrega(DateUtil.comporDiaHora(saldosLimites.getDtPrimeiraEntrega(), item.getHorario()));
						
						saldosLimites.setLimiteMonetario(saldosLimites.getLimiteMonetario() - itemTotal.doubleValue());
						if (BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(BigDecimal.ZERO) <= 0) {
							saldosLimites.setSaldoFinal(0L);
						} else {
							saldosLimites.setSaldoFinal(saldosLimites.getSaldoFinal() - item.getQuantidadeItem());
						}
						saldosLimites.setUltimaParcela(gerarParcela(saldosLimites.getDtPrimeiraEntrega(), item.getQuantidadeItem(),
								parcela, urgencia));
					}
				}
			}
			saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), 1));
			saldosLimites.setDtPrimeiraEntrega(DateUtil.truncaData(saldosLimites.getDtPrimeiraEntrega()));
		}
	}
	
	private void gerarParcelasPorDiaSemanaNDNP(List<DominioDiaSemanaAbreviado> listaDiasSemana, DominioDiaSemanaAbreviado diaSemana,
			DominioDiaSemana diaEntrega, List<HorarioSemanaVO> listaHorarios, DominioUrgencia urgencia,
			SaldosLimites saldosLimites, ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (listaDiasSemana.contains(diaSemana)) {
			// Adiciona dias até chegar no dia da semana correto.
			while (!CoreUtil.retornaDiaSemana(saldosLimites.getDtPrimeiraEntrega()).equals(diaEntrega)) {
				saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), 1));
				saldosLimites.setDtPrimeiraEntrega(DateUtil.truncaData(saldosLimites.getDtPrimeiraEntrega()));
			}
			// Ordena a lista para gerar as parcelas corretamente
			ordenarListaHorarios(listaHorarios);
			for (HorarioSemanaVO item : listaHorarios) {
				if (item.getHorario() != null && item.getQuantidadeItem() != null) {
					if (saldosLimites.getSaldoFinal() > 0 && (saldosLimites.getSaldoFinal() < item.getQuantidadeItem()
							|| saldosLimites.getNumeroLimite() == 0)) {
						
						atualizarUltimaParcela(saldosLimites.getUltimaParcela(), saldosLimites.getSaldoFinal(), null, parcela.getValorUnitario(), Boolean.FALSE);
						saldosLimites.setSaldoFinal(0L);
						
					} else if (saldosLimites.getSaldoFinal() > 0 && DateUtil.validaHoraMaior(item.getHorario(), saldosLimites.getDtPrimeiraEntrega())) {
						saldosLimites.setDtPrimeiraEntrega(DateUtil.comporDiaHora(saldosLimites.getDtPrimeiraEntrega(), item.getHorario()));
						
						saldosLimites.setSaldoFinal(saldosLimites.getSaldoFinal() - item.getQuantidadeItem());
						if (formaProgramacao.equals(DominioFormaProgramacao.NP)) {
							saldosLimites.setNumeroLimite(saldosLimites.getNumeroLimite()-1);
						}
						saldosLimites.setUltimaParcela(gerarParcela(saldosLimites.getDtPrimeiraEntrega(), item.getQuantidadeItem(),
								parcela, urgencia));
					}
				}
			}
			saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), 1));
			saldosLimites.setDtPrimeiraEntrega(DateUtil.truncaData(saldosLimites.getDtPrimeiraEntrega()));
			if (formaProgramacao.equals(DominioFormaProgramacao.ND)) {
				saldosLimites.setNumeroLimite(saldosLimites.getNumeroLimite()-1);
			}
		}
	}
	
	private void ordenarListaHorarios(List<HorarioSemanaVO> listaHorarios) {
		CoreUtil.ordenarLista(listaHorarios, "horario", Boolean.TRUE);
	}
	
	public ScoProgEntregaItemAutorizacaoFornecimento gerarParcela(Date dtPrevEntrega, Integer qtde,
			ConsultaItensAFProgramacaoManualVO parcela, DominioUrgencia urgencia) throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		Integer numParcela = this.getAutFornecimentoFacade().obterMaxNumeroParcela(parcela.getAfnNumero(), parcela.getNumeroItem());
		if (numParcela == null) {
			numParcela = 1;
		} else {
			numParcela++;
		}
		ScoProgEntregaItemAutorizacaoFornecimento progEntrega = new ScoProgEntregaItemAutorizacaoFornecimento();
		ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		id.setIafAfnNumero(parcela.getAfnNumero());
		id.setIafNumero(parcela.getNumeroItem());
		id.setSeq(1);
		id.setParcela(numParcela);
		
		progEntrega.setId(id);
		progEntrega.setDtGeracao(formataData(new Date()));
		progEntrega.setDtPrevEntrega(formataData(dtPrevEntrega));
		progEntrega.setDtEntrega(null);
		progEntrega.setQtde(qtde);
		progEntrega.setQtdeEntregue(0);
		progEntrega.setRapServidor(servidorLogado);
		progEntrega.setRapServidorAlteracao(null);
		progEntrega.setIndPlanejamento(Boolean.FALSE);
		progEntrega.setIndAssinatura(Boolean.FALSE);
		progEntrega.setIndEmpenhada(DominioAfEmpenhada.N);
		progEntrega.setIndEnvioFornecedor(Boolean.FALSE);
		progEntrega.setIndRecalculoAutomatico(Boolean.FALSE);
		progEntrega.setIndRecalculoManual(Boolean.FALSE);
		progEntrega.setValorTotal(parcela.getValorUnitario() * qtde);
		progEntrega.setIndImpressa(Boolean.FALSE);
		progEntrega.setDtAtualizacao(null);
		progEntrega.setDtAssinatura(null);
		progEntrega.setRapServidorAssinatura(null);
		progEntrega.setDtAlteracao(null);
		progEntrega.setDtLibPlanejamento(null);
		progEntrega.setRapServidorLibPlanej(null);
		progEntrega.setIndCancelada(Boolean.FALSE);
		progEntrega.setDtCancelamento(null);
		progEntrega.setRapServidorCancelamento(null);
		progEntrega.setScoJustificativa(null);
		progEntrega.setDtNecessidadeHcpa(null);
		progEntrega.setValorEfetivado(0.00);
		progEntrega.setIndEfetivada(Boolean.FALSE);
		progEntrega.setIndEntregaImediata(urgencia != null ? verificaUrgencia(urgencia, DominioUrgencia.I) : Boolean.FALSE);
		progEntrega.setObservacao(null);
		progEntrega.setQtdeEntregueProv(0);
		progEntrega.setQtdeEntregueAMais(0);
		progEntrega.setIndTramiteInterno(Boolean.FALSE);
		progEntrega.setIndConversaoUnidade(Boolean.FALSE);
		progEntrega.setIndPublicado(false);
		progEntrega.setSlcNumero(null);
		progEntrega.setEslSeqFatura(null);
		progEntrega.setDtPrevEntregaAposAtraso(null);
		progEntrega.setIndEntregaUrgente(urgencia != null ? verificaUrgencia(urgencia, DominioUrgencia.U) : Boolean.FALSE);
		
		ScoSolicitacaoProgramacaoEntrega scoSolicitacaoProgramacaoEntrega = new ScoSolicitacaoProgramacaoEntrega();
		scoSolicitacaoProgramacaoEntrega.setProgEntregaItemAf(progEntrega);
		ScoSolicitacaoDeCompra solicitacao = getComprasFacade().obterScoSolicitacaoDeCompraPorChavePrimaria(parcela.getSlcNumero());
		scoSolicitacaoProgramacaoEntrega.setSolicitacaoCompra(solicitacao);
		scoSolicitacaoProgramacaoEntrega.setSolicitacaoServico(null);
		scoSolicitacaoProgramacaoEntrega.setQtde(progEntrega.getQtde());
		scoSolicitacaoProgramacaoEntrega.setValor(null);
		scoSolicitacaoProgramacaoEntrega.setIndPrioridade(Short.valueOf("1"));
		scoSolicitacaoProgramacaoEntrega.setQtdeEntregue(null);
		scoSolicitacaoProgramacaoEntrega.setValorEfetivado(null);
		scoSolicitacaoProgramacaoEntrega.setItemAfOrigem(null);
		
		this.getAutFornecimentoFacade().persistirProgEntregaItemAf(progEntrega);
		this.getAutFornecimentoFacade().persistir(scoSolicitacaoProgramacaoEntrega);
		
		return progEntrega;
	}
	
	private Date formataData(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private Boolean verificaUrgencia(DominioUrgencia urgencia, DominioUrgencia tipoUrgenciaRequisitada){
		if(urgencia.equals(tipoUrgenciaRequisitada)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public void atualizarUltimaParcela(ScoProgEntregaItemAutorizacaoFornecimento ultimaParcela,
			Long saldoFinal, Double limiteMonetarioSaldo, Double valorUnitario, Boolean assina) throws ApplicationBusinessException {
		
		if (limiteMonetarioSaldo != null) {
			Long qtdeFinal = BigDecimal.valueOf(limiteMonetarioSaldo).divide(BigDecimal.valueOf(valorUnitario), 0, RoundingMode.DOWN).longValue();
			
			if (qtdeFinal > saldoFinal) {
				Integer qtdeNova = ultimaParcela.getQtde() + saldoFinal.intValue();
				ultimaParcela.setQtde(qtdeNova);
				ultimaParcela.setValorTotal(valorUnitario * qtdeNova);
				
			} else if (qtdeFinal > 0) {
				Integer qtdeNova = ultimaParcela.getQtde() + qtdeFinal.intValue();
				ultimaParcela.setQtde(qtdeNova);
				ultimaParcela.setValorTotal(valorUnitario * qtdeNova);
			}
			
		} else {
			Integer qtdeNova = ultimaParcela.getQtde() + saldoFinal.intValue();
			ultimaParcela.setQtde(qtdeNova);
			ultimaParcela.setValorTotal(valorUnitario * qtdeNova);
		}

		// verifica se é para validar status da assinatura da parcela
		if (assina){
			this.getAutFornecimentoFacade().persistirProgEntregaItemAf(ultimaParcela); // valida se IND_ASSINATURA foi alterado
		} else {
			this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(ultimaParcela);
		}

	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() { return this.scoProgEntregaItemAutorizacaoFornecimentoDAO; }
}
