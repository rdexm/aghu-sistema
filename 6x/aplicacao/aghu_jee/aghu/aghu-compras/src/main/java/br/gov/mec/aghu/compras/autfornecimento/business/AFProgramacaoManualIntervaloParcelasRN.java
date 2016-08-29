package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoManualVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoManualParcelasEntregaFiltroVO;
import br.gov.mec.aghu.dominio.DominioFormaProgramacao;
import br.gov.mec.aghu.dominio.DominioUrgencia;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AFProgramacaoManualIntervaloParcelasRN extends BaseBusiness {

	private static final long serialVersionUID = -148027677264781838L;
	
	
	private static final Log LOG = LogFactory.getLog(AFProgramacaoManualIntervaloParcelasRN.class);

	@EJB
	private AFProgramacaoManualDiasEspecificosRN afProgramacaoManualDiasEspecificosRN;
	
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
		private boolean atualizarData;
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
		public boolean isAtualizarData() {
			return atualizarData;
		}
		public void setAtualizarData(boolean atualizarData) {
			this.atualizarData = atualizarData;
		}
		public ScoProgEntregaItemAutorizacaoFornecimento getUltimaParcela() {
			return ultimaParcela;
		}
		public void setUltimaParcela(ScoProgEntregaItemAutorizacaoFornecimento ultimaParcela) {
			this.ultimaParcela = ultimaParcela;
		}
	}
	
	public void gerarParcelasPorIntervalo(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaParcelasSelecionadas) throws BaseException {
		
		DominioFormaProgramacao formaProgramacao = filtro.getFormaProgramacao();
		DominioUrgencia urgencia = filtro.getUrgencia();
		Integer numeroDias = filtro.getNumeroDias();
		Integer qtdeParcelas = filtro.getQtdeParcelas();
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
			// Indica se deve alterar a data da entrega. Necessário pois a primeira vez que entra no método deve usar a data informada na tela.
			saldosLimites.setAtualizarData(false);
			while (saldosLimites.getSaldoFinal() > 0) {
				gerarParcelas(numeroDias, qtdeParcelas, urgencia, saldosLimites, parcela, formaProgramacao);
				// Seta para true pois na próxima vez que chamar o método deve atualizar a data de entrega.
				saldosLimites.setAtualizarData(true);
			}
		}
	}
	
	private void gerarParcelas(Integer numeroDias, Integer qtdeParcelas, DominioUrgencia urgencia, SaldosLimites saldosLimites,
			ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (formaProgramacao.equals(DominioFormaProgramacao.PS) || formaProgramacao.equals(DominioFormaProgramacao.QL)) {
			gerarParcelasPorDiaEspecifico(numeroDias, qtdeParcelas, urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.VL)) {
			gerarParcelasPorDiaEspecificoVL(numeroDias, qtdeParcelas, urgencia, saldosLimites, parcela);
			
		} else if (formaProgramacao.equals(DominioFormaProgramacao.ND) || formaProgramacao.equals(DominioFormaProgramacao.NP)) {
			gerarParcelasPorDiaEspecificoNDNP(numeroDias, qtdeParcelas, urgencia, saldosLimites, parcela, formaProgramacao);
		}
	}
	
	private void gerarParcelasPorDiaEspecifico(Integer numeroDias, Integer qtdeParcelas, DominioUrgencia urgencia, SaldosLimites saldosLimites,
			ConsultaItensAFProgramacaoManualVO parcela) throws BaseException {
		
		if (saldosLimites.getSaldoFinal() > 0 && saldosLimites.getSaldoFinal() < qtdeParcelas) {
			this.getAFProgramacaoManualDiasEspecificosRN()
				.atualizarUltimaParcela(saldosLimites.getUltimaParcela(), saldosLimites.getSaldoFinal(), null, parcela.getValorUnitario(), Boolean.FALSE);
			saldosLimites.setSaldoFinal(0L);

		} else if (saldosLimites.getSaldoFinal() > 0) {
			if (saldosLimites.isAtualizarData()) {
				saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), numeroDias));
			}
			saldosLimites.setSaldoFinal(saldosLimites.getSaldoFinal() - qtdeParcelas);

			saldosLimites.setUltimaParcela(this.getAFProgramacaoManualDiasEspecificosRN().gerarParcela(saldosLimites.getDtPrimeiraEntrega(),
					qtdeParcelas, parcela, urgencia));
		}
	}
	
	private void gerarParcelasPorDiaEspecificoVL(Integer numeroDias, Integer qtdeParcelas, DominioUrgencia urgencia, SaldosLimites saldosLimites,
			ConsultaItensAFProgramacaoManualVO parcela) throws BaseException {
		
		BigDecimal itemTotal = BigDecimal.valueOf(parcela.getValorUnitario()).multiply(BigDecimal.valueOf(qtdeParcelas));
		
		if (BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(BigDecimal.ZERO) > 0
				&& saldosLimites.getSaldoFinal() > 0 && (saldosLimites.getSaldoFinal() < qtdeParcelas
						|| BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(itemTotal) < 0)) {

			this.getAFProgramacaoManualDiasEspecificosRN()
				.atualizarUltimaParcela(saldosLimites.getUltimaParcela(), saldosLimites.getSaldoFinal(), saldosLimites.getLimiteMonetario(),
						parcela.getValorUnitario(), Boolean.FALSE);
			saldosLimites.setLimiteMonetario(0.00);
			saldosLimites.setSaldoFinal(0L);

		} else if (BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(BigDecimal.ZERO) > 0
				&& saldosLimites.getSaldoFinal() > 0) {

			if (saldosLimites.isAtualizarData()) {
				saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), numeroDias));
			}
			saldosLimites.setLimiteMonetario(saldosLimites.getLimiteMonetario() - itemTotal.doubleValue());
			if (BigDecimal.valueOf(saldosLimites.getLimiteMonetario()).compareTo(BigDecimal.ZERO) <= 0) {
				saldosLimites.setSaldoFinal(0L);
			} else {
				saldosLimites.setSaldoFinal(saldosLimites.getSaldoFinal() - qtdeParcelas);
			}
			saldosLimites.setUltimaParcela(this.getAFProgramacaoManualDiasEspecificosRN().gerarParcela(saldosLimites.getDtPrimeiraEntrega(),
					qtdeParcelas, parcela, urgencia));
		}
	}
	
	private void gerarParcelasPorDiaEspecificoNDNP(Integer numeroDias, Integer qtdeParcelas, DominioUrgencia urgencia, SaldosLimites saldosLimites,
			ConsultaItensAFProgramacaoManualVO parcela,
			DominioFormaProgramacao formaProgramacao) throws BaseException {
		
		if (saldosLimites.getSaldoFinal() > 0 && (saldosLimites.getSaldoFinal() < qtdeParcelas
				|| saldosLimites.getNumeroLimite() == 0)) {

			this.getAFProgramacaoManualDiasEspecificosRN()
				.atualizarUltimaParcela(saldosLimites.getUltimaParcela(), saldosLimites.getSaldoFinal(), null, parcela.getValorUnitario(), Boolean.FALSE);
			saldosLimites.setSaldoFinal(0L);

		} else if (saldosLimites.getSaldoFinal() > 0) {
			if (saldosLimites.isAtualizarData()) {
				saldosLimites.setDtPrimeiraEntrega(DateUtil.adicionaDias(saldosLimites.getDtPrimeiraEntrega(), numeroDias));
			}
			saldosLimites.setSaldoFinal(saldosLimites.getSaldoFinal() - qtdeParcelas);
			saldosLimites.setNumeroLimite(saldosLimites.getNumeroLimite()-1);
			
			saldosLimites.setUltimaParcela(this.getAFProgramacaoManualDiasEspecificosRN().gerarParcela(saldosLimites.getDtPrimeiraEntrega(),
					qtdeParcelas, parcela, urgencia));
		}
	}
	
	private AFProgramacaoManualDiasEspecificosRN getAFProgramacaoManualDiasEspecificosRN() {
		return afProgramacaoManualDiasEspecificosRN;
	}
}
