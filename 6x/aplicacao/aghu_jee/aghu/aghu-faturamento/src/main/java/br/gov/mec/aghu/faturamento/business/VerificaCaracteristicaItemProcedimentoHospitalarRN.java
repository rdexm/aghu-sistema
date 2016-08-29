package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * <p>
 * Linhas: 50 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 1 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 1 <br/>
 * Metodos externos: 0 <br/>
 * Horas: 1.4 <br/>
 * Pontos: 0.2 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATC_VER_CARACT_IPH</code>
 * </p>
 * 
 * @author gandriotti
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class VerificaCaracteristicaItemProcedimentoHospitalarRN
		extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(VerificaCaracteristicaItemProcedimentoHospitalarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatTipoCaractItemSeqCache fatTipoCaractItemSeqCache;

@Inject
private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7225324203871925086L;
	public static final String STRING_S = "S";

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {

		return fatCaractItemProcHospDAO;
	}

	protected Integer obterTctSeq(final DominioFatTipoCaractItem caracteristica) {
		List<Integer> tctSeqs = this.getFatTipoCaractItemSeqCache().listarSeqsPorCaracteristica(caracteristica);

		return tctSeqs != null && !tctSeqs.isEmpty() ? tctSeqs.get(0) : null;
	}

	protected FatCaractItemProcHosp obterCaractItemProcHosp(final Short iphPhoSeq,
			final Integer iphSeq,
			final Integer tctSeq) {

		return getFatCaractItemProcHospDAO().obterPorChavePrimaria(new FatCaractItemProcHospId(iphPhoSeq, iphSeq, tctSeq));
	}

	/**
	 * <p>
	 * ORADB: <code>FATC_VER_CARACT_IPH</code>
	 * </p>
	 * 
	 * @return
	 */
	public boolean verificarCaracteristicaItemProcHosp(final Short iphPhoSeq,
			final Integer iphSeq,
			final DominioFatTipoCaractItem caracteristica) {

		if ((iphPhoSeq != null) && (iphSeq != null) && (caracteristica != null)) {
			final Integer tctSeq = this.obterTctSeq(caracteristica);
			final FatCaractItemProcHosp ciph = this.obterCaractItemProcHosp(iphPhoSeq, iphSeq, tctSeq);
			
			if (ciph != null) {
				// v_resultado := nvl(v_vlr_char,'S');
				return VerificaCaracteristicaItemProcedimentoHospitalarRN.STRING_S.equals( (String) CoreUtil.nvl(ciph.getValorChar(), "S") );
			} else {
				return false;
			}
		}
		return false;
	}
	
	protected FatTipoCaractItemSeqCache getFatTipoCaractItemSeqCache() {
		return fatTipoCaractItemSeqCache;
	}
	
}
