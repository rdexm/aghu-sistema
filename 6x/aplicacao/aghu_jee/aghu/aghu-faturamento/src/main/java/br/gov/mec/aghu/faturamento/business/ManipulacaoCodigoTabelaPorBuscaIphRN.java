package br.gov.mec.aghu.faturamento.business;

import java.text.DecimalFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

/**
 * 
 * <p>
 * ORADB: <code>FATC_BUSCA_PROCED_PR_CTA</code>
 * </p>
 * 
 * @author gandriotti
 * 
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta","PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ManipulacaoCodigoTabelaPorBuscaIphRN {
	
	@Inject
	private FatCompatExclusItemDAO fatCompatExclusItemDAO;
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;

	public static final int MAGIC_INT_COD_TABELA_SUFFIX_NULL_EQ_1 = 1;
	public static final int MAGIC_INT_COD_TABELA_SUFFIX_NOT_NULL_EQ_2 = 2;

	public static class FatcBuscaProcedPrCtaVO {

		public static final int MAGIC_NUMBER_DIGIT_AMOUNT_FORMATTER_EQ_11 = 11;
		public final Long codTabela;
		public final int codTabelaSufixo;
		public final long codTabelaMod;
		public final String codTabelaModStr;

		public FatcBuscaProcedPrCtaVO(Long codTabela, int codTabelaSufixo) {

			super();

			DecimalFormat format = null;

			if (codTabela == null) {
				throw new IllegalArgumentException("codTabela não pode ser nulo.");
			}
			if (codTabelaSufixo < 0) {
				throw new IllegalArgumentException();
			}
			if (codTabelaSufixo > 9) {
				throw new IllegalArgumentException();
			}
			this.codTabela = codTabela;
			this.codTabelaSufixo = codTabelaSufixo;
			this.codTabelaMod = codTabela.longValue() * 10 + codTabelaSufixo;
			format = new DecimalFormat();
			format.setDecimalSeparatorAlwaysShown(false);
			format.setGroupingUsed(false);
			format.setMinimumIntegerDigits(MAGIC_NUMBER_DIGIT_AMOUNT_FORMATTER_EQ_11);
			format.setMaximumIntegerDigits(11);
			this.codTabelaModStr = format.format(this.codTabelaMod);
		}

		@Override
		public String toString() {

			return this.codTabelaModStr;
		}
	}

	protected FatCompatExclusItemDAO getFatCompatExclusItemDAO() {

		return fatCompatExclusItemDAO;
	}

	protected FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {

		return fatAtoMedicoAihDAO;
	}

	protected List<FatAtoMedicoAih> obterListaFatAtoMedicoAihPorCthIph(
			Integer eaiCthSeq,
			FatItensProcedHospitalar iph) {

		List<FatAtoMedicoAih> result = null;
		FatAtoMedicoAihDAO dao = null;

		dao = this.getFatAtoMedicoAihDAO();
		result = dao.obterListaPorCthIph(
				eaiCthSeq, iph.getId().getPhoSeq(), iph.getId().getSeq());

		return result;
	}

	protected List<FatCompatExclusItem> obterListaFatCompatExclusItemPorIphOrdenadaPorQtdDesc(
			Short iphPhoSeq,
			Integer iphSeq) {

		List<FatCompatExclusItem> result = null;
		FatCompatExclusItemDAO dao = null;

		dao = this.getFatCompatExclusItemDAO();
		result = dao.obterListaPorIphOrdenadaPorQtdDesc(
				iphPhoSeq, iphSeq, new DominioIndCompatExclus[] {
						DominioIndCompatExclus.ICP,
						DominioIndCompatExclus.PCI,
		});

		return result;
	}

	protected Long obterCodigoTabelaConformeBuscaIph(
			Integer eaiCthSeq,
			Short iphPhoSeq,
			Integer iphSeq) {

		Long result = null;
		List<FatCompatExclusItem> listaIct = null;
		List<FatAtoMedicoAih> listaAma = null;
		FatItensProcedHospitalar iph = null;

		listaIct = this.obterListaFatCompatExclusItemPorIphOrdenadaPorQtdDesc(
				iphPhoSeq, iphSeq);
		if ((listaIct != null) && !listaIct.isEmpty()) {
			for (FatCompatExclusItem ict : listaIct) {
				listaAma = this.obterListaFatAtoMedicoAihPorCthIph(
						eaiCthSeq, ict.getItemProcedHosp());
				if ((listaAma != null) && !listaAma.isEmpty()) {
					for (FatAtoMedicoAih ama : listaAma) {
						iph = ama.getItemProcedimentoHospitalar();
						if ((iph != null) && (iph.getCodTabela() != null)) {
							result = iph.getCodTabela();
							break;
						}
					}
					if (result != null) {
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * TODO
	 * <p>
	 * ORADB: <code>FATC_BUSCA_PROCED_PR_CTA</code>
	 * </p>
	 * 
	 * @param eaiCthSeq
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codTabela
	 * 
	 * @return
	 */
	public FatcBuscaProcedPrCtaVO obterCodigoTabelaSuffixadaConformeBuscaIph(
			Integer eaiCthSeq,
			Short iphPhoSeq,
			Integer iphSeq,
			Long codTabela) {

		FatcBuscaProcedPrCtaVO result = null;
		Long retCodTabela = null;
		int codTabelaSufixo = 0;

		//check args
		if (eaiCthSeq == null) {
			throw new IllegalArgumentException("eaiCthSeq não pode ser nulo.");
		}
		if (iphPhoSeq == null) {
			throw new IllegalArgumentException("iphPhoSeq não pode ser nulo.");
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException("iphSeq não pode ser nulo.");
		}
		if (codTabela == null) {
			throw new IllegalArgumentException("codTabela não pode ser nulo.");
		}
		//algo
		retCodTabela = this.obterCodigoTabelaConformeBuscaIph(
				eaiCthSeq, iphPhoSeq, iphSeq);
		if (retCodTabela == null) {
			retCodTabela = codTabela;
			codTabelaSufixo = MAGIC_INT_COD_TABELA_SUFFIX_NULL_EQ_1;
		} else {
			codTabelaSufixo = MAGIC_INT_COD_TABELA_SUFFIX_NOT_NULL_EQ_2;
		}
		result = new FatcBuscaProcedPrCtaVO(retCodTabela, codTabelaSufixo);

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATC_BUSCA_PROCED_PR_CTA</code>
	 * </p>
	 * 
	 * @param ama
	 * @return
	 */
	@SuppressWarnings("ucd")
	public FatcBuscaProcedPrCtaVO obterCodigoTabelaSuffixadaConformeBuscaIph(
			FatAtoMedicoAih ama) {

		FatcBuscaProcedPrCtaVO result = null;

		//check args
		if (ama == null) {
			throw new IllegalArgumentException("ama não pode ser nulo.");
		}
		if (ama.getId() == null) {
			throw new IllegalArgumentException();
		}
		if (ama.getItemProcedimentoHospitalar() == null) {
			throw new IllegalArgumentException();
		}
		if (ama.getItemProcedimentoHospitalar().getId() == null) {
			throw new IllegalArgumentException();
		}
		//algo
		result = this.obterCodigoTabelaSuffixadaConformeBuscaIph(
				ama.getId().getEaiCthSeq(),
				ama.getItemProcedimentoHospitalar().getId().getPhoSeq(),
				ama.getItemProcedimentoHospitalar().getId().getSeq(),
				ama.getIphCodSus());

		return result;
	}

}
