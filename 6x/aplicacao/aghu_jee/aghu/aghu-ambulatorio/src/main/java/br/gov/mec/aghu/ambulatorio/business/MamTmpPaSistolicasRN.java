package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaSistolicasDAO;
import br.gov.mec.aghu.model.MamTmpPaSistolicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamTmpPaSistolicasRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5486851267060241160L;

	@Inject
	private MamTmpPaSistolicasDAO mamTmpPaSistolicasDAO;
	
	private static final Log LOG = LogFactory.getLog(MamTmpPaSistolicasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum MamTmpPesosRNExceptionCode implements BusinessExceptionCode {
		MAM_00323, MAM_00324, MAM_00325, MAM_00326,	MAM_01850, MAM_01851, MAM_01852, MAM_01853,MAM_01854_01857, MAM_01855, MAM_01856,
		MAM_01858, MAM_01859, MAM_01837_01839_01843_01849_02360, MAM_01838_01840_01844_01866_01868, MAM_01860_01861_01865_01867, MAM_01862,
		MAM_02036_02040_02042_02048, MAM_02037_02041_02043, MAM_02051_02052_02056_02058_02078_02079_02083_02085, MAM_02053_02057_02059_02080_02084_02086,
		MAM_02358, MAM_02359_02361,	MAM_02362, MAM_02363, MAM_02205
	}
	
	public MamTmpPaSistolicas inserirMamTmpPaSistolicas(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, Short pResposta, Integer pPacCodigo, Date pCriadoEm){
		MamTmpPaSistolicas mamTmpPaSistolicas = new MamTmpPaSistolicas();
			mamTmpPaSistolicas.setChave(pChave);
			mamTmpPaSistolicas.setQusQutSeq(pQusQutSeq);
			mamTmpPaSistolicas.setQusSeqp(pQusSeqP);
			mamTmpPaSistolicas.setSeqp(pseq);
			mamTmpPaSistolicas.setTipo(pTipo);
			mamTmpPaSistolicas.setSistolica(pResposta);
			mamTmpPaSistolicas.setIndRecuperado("N");
			Date data = (Date) CoreUtil.nvl(pCriadoEm, new Date());
			mamTmpPaSistolicas.setCriadoEm(data);
			mamTmpPaSistolicas.setPacCodigo(pPacCodigo);
		this.mamTmpPaSistolicasDAO.persistir(mamTmpPaSistolicas);
		return mamTmpPaSistolicas;
		
	}
	
	public MamTmpPaSistolicas atualizarPaSistolicas(Long pChave, Short pResposta, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pTipo){
		MamTmpPaSistolicas mamTmpPaSistolicas = buscarPaSistolica(pChave, pQusQutSeq, pQusSeqP, pseq, pTipo);
			mamTmpPaSistolicas.setSistolica(pResposta);
		this.mamTmpPaSistolicasDAO.atualizar(mamTmpPaSistolicas);
		return mamTmpPaSistolicas;
	}
	
	public void deletarMamTpmPaSistolicas(Long pChave, String pResposta, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pTipo){
		MamTmpPaSistolicas mamTmpPaSistolicas = buscarPaSistolica(pChave, pQusQutSeq, pQusSeqP, pseq, pTipo);
		this.mamTmpPaSistolicasDAO.remover(mamTmpPaSistolicas);
	}

	private MamTmpPaSistolicas buscarPaSistolica(Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pTipo) {
		MamTmpPaSistolicas mamTmpPaSistolicas = this.mamTmpPaSistolicasDAO.obterPaDiastolicasPorPorChaveTipoSeq(
					pChave.longValue(), pQusQutSeq, pQusSeqP.shortValue(), pseq.shortValue(), pTipo);
		return mamTmpPaSistolicas;
	}
	
}