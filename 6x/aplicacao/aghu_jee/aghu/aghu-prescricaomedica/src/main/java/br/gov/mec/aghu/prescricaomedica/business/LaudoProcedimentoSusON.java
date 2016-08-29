package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioLaudosProcSusVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class LaudoProcedimentoSusON extends BaseBusiness {
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@EJB
	private LaudoProcedimentoSusRN laudoProcedimentoSusRN;
	
	private static final Log LOG = LogFactory.getLog(LaudoProcedimentoSusON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5461212995543860448L;
	
	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			Integer seqAtendimento, Integer apaSeq, Short seqp) throws ApplicationBusinessException {
		return getLaudoProcedimentoSusRN().pesquisaLaudoProcedimentoSus(
				seqAtendimento, apaSeq, seqp, null);
	}
	
	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			Integer seqAtendimento, Integer apaSeq, Short seqp, RapServidores servidorLogado) throws ApplicationBusinessException {
		return getLaudoProcedimentoSusRN().pesquisaLaudoProcedimentoSus(
				seqAtendimento, apaSeq, seqp, servidorLogado);
	}

	protected LaudoProcedimentoSusRN getLaudoProcedimentoSusRN() {
		return laudoProcedimentoSusRN;
	}

	public RapServidores obterServidorCriacaoLaudoProcedimentoSus(
			Integer seqAtendimento) {
		RapServidores servidorValida = null;
		MpmAltaSumario mpmAltaSumario = getMpmAltaSumarioDAO()
				.obterServidorAltaSumariosConcluidoAltaEObitoPorAtdSeq(
						seqAtendimento);
		if (mpmAltaSumario != null) {
				servidorValida = mpmAltaSumario.getServidorValida();
		}
		return servidorValida;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}
}
