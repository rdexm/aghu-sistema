package br.gov.mec.aghu.faturamento.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatTratamentoApacCaractDAO;
import br.gov.mec.aghu.faturamento.vo.DadosCaracteristicaTratamentoApacVO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTratamentoApacCaract;
import br.gov.mec.aghu.model.FatTratamentoApacCaractId;

/**
 * <p>
 * Linhas: 41 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 1 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 1 <br/>
 * Metodos externos: 1 <br/>
 * Horas: 1.9 <br/>
 * Pontos: 0.2 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_TAR_RN</code>
 * </p>
 * 
 * @author gandriotti
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class CaracteristicaTratamentoApacRN
		extends AbstractFatDebugLogEnableRN {


@EJB
private CaracteristicaItemProcedimentoHospitalarRN caracteristicaItemProcedimentoHospitalarRN;

private static final Log LOG = LogFactory.getLog(CaracteristicaTratamentoApacRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatTratamentoApacCaractDAO fatTratamentoApacCaractDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6703262112699085560L;

	protected FatTratamentoApacCaractDAO getFatTratamentoApacCaractDAO() {

		return fatTratamentoApacCaractDAO;
	}

	/**
	 * @return
	 */
	protected CaracteristicaItemProcedimentoHospitalarRN getFatkCihRn() {

		return caracteristicaItemProcedimentoHospitalarRN;
	}

	/**
	 * TODO APAC - Ambulatorio
	 * 
	 * @param iphId
	 * @param cihId
	 * @return
	 */
	protected FatTratamentoApacCaract obterFatTratApacCaract(final FatItensProcedHospitalarId iphId,
			final FatCaractItemProcHospId cihId) {

		FatTratamentoApacCaract result = null;
		FatTratamentoApacCaractDAO dao = null;
		FatTratamentoApacCaractId id = null;

		id = new FatTratamentoApacCaractId(
				iphId.getPhoSeq(),
				iphId.getSeq(),
				cihId.getIphPhoSeq(),
				cihId.getIphSeq(),
				cihId.getTctSeq());
		dao = this.getFatTratamentoApacCaractDAO();
		result = dao.obterPorChavePrimaria(id);

		return result;
	}

	/**
	 * 
	 * @ORADB FATK_TAR_RN.RN_TARC_VER_CARAC
	 * 
	 * @param phoSeqTrat
	 * @param iphSeqTrat
	 * @param phoSeqItem
	 * @param iphSeqItem
	 * @param tciSeq
	 * @return
	 */
	public DadosCaracteristicaTratamentoApacVO verificarCaracteristicaTratamento (final Short phoSeqTrat, final Integer iphSeqTrat, final Short phoSeqItem, 
			final Integer iphSeqItem, final Integer tciSeq) {
		return this.obterDadosCaracteristicaTratamentoApac(new FatItensProcedHospitalarId(phoSeqTrat, iphSeqTrat),
				new FatCaractItemProcHospId(phoSeqItem, iphSeqItem, tciSeq));
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATK_TAR_RN.RN_TARC_VER_CARAC</code>
	 * </p>
	 * 
	 * @param iphId
	 * @param cihId
	 * @return
	 * @see FatTratamentoApacCaract
	 * @see FatCaractItemProcHosp
	 * @see CaracteristicaItemProcedimentoHospitalarRN#obterCaracteristicaProcHospPorId(FatCaractItemProcHospId)
	 * @see #obterFatTratApacCaract(FatItensProcedHospitalarId,
	 *      FatCaractItemProcHospId)
	 * @see #getFatkCihRn()
	 */
	public DadosCaracteristicaTratamentoApacVO obterDadosCaracteristicaTratamentoApac(final FatItensProcedHospitalarId iphId,
			final FatCaractItemProcHospId cihId) {

		DadosCaracteristicaTratamentoApacVO result = null;
		CaracteristicaItemProcedimentoHospitalarRN fatkCihRn = null;
		FatTratamentoApacCaract tac = null;
		FatCaractItemProcHosp cih = null;

		//arg check
		if (iphId == null) {
			throw new IllegalArgumentException("iphId não pode ser nulo.");
		}
		if (cihId == null) {
			throw new IllegalArgumentException("cihId não pode ser nulo.");
		}
		//algo
		tac = this.obterFatTratApacCaract(iphId, cihId);
		if (tac == null) {
			fatkCihRn = this.getFatkCihRn();
			cih = fatkCihRn.obterCaracteristicaProcHospPorId(cihId);
			if (cih != null) {
				result = new DadosCaracteristicaTratamentoApacVO(cih);
			}
		} else {
			result = new DadosCaracteristicaTratamentoApacVO(tac);
		}

		return result;
	}
}
