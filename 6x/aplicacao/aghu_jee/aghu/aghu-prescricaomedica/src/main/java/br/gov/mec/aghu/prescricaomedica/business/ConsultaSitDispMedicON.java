package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ConsultaSitDispMedicON extends BaseBusiness {

	private static final long serialVersionUID = 7940248345198534166L;

	private static final Log LOG = LogFactory.getLog(ConsultaSitDispMedicON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

	public List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacao(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final String leitoId, final Integer prontuario, final String nome, final Date dtHrInicio, final Date dtHrFim, final String seqPrescricao, final Integer seqPrescricaoInt,
			Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException {
		List<MpmPrescricaoMedicaVO> listRetorno = new ArrayList<MpmPrescricaoMedicaVO>();
		listRetorno = criarListaPrescricaoMedicaSituacaoDispensacao(leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao, seqPrescricaoInt, indPacAtendimento, indPmNaoEletronica);

		CoreUtil.ordenarLista(listRetorno, MpmPrescricaoMedicaVO.Fields.DTHR_INICIO.toString(), false);

		// Paginação
		Integer lastResult = (firstResult + maxResult) > listRetorno.size() ? listRetorno.size() : (firstResult + maxResult);
		listRetorno = listRetorno.subList(firstResult, lastResult);

		return listRetorno;
	}

	private List<MpmPrescricaoMedicaVO> criarListaPrescricaoMedicaSituacaoDispensacao(String leitoId, Integer prontuario, String nome, Date dtHrInicio, Date dtHrFim, String seqPrescricao,
			Integer seqPrescricaoInt, Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException {
		List<MpmPrescricaoMedicaVO> listRetorno = new ArrayList<MpmPrescricaoMedicaVO>();

		
		if (indPmNaoEletronica == null || Boolean.FALSE.equals(indPmNaoEletronica)) {
			listRetorno.addAll(getMpmPrescricaoMedicaDAO().listarPrescricaoMedicaSituacaoDispensacaoUnion1(leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricaoInt, indPacAtendimento));
		}

		if (indPmNaoEletronica == null || Boolean.TRUE.equals(indPmNaoEletronica)) {
			listRetorno.addAll(farmaciaFacade.listarPrescricaoMedicaSituacaoDispensacaoUnion2(leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao, indPacAtendimento));
		}

		return listRetorno;
	}

	public Long listarPrescricaoMedicaSituacaoDispensacaoCount(final String leitoId, final Integer prontuario, final String nome, final Date dtHrInicio, final Date dtHrFim,
			final String seqPrescricao, final Integer seqPrescricaoInt, Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException {

		List<MpmPrescricaoMedicaVO> listRetorno = new ArrayList<MpmPrescricaoMedicaVO>();
		listRetorno = criarListaPrescricaoMedicaSituacaoDispensacao(leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao, seqPrescricaoInt, indPacAtendimento, indPmNaoEletronica);

		return Long.valueOf(listRetorno.size());
	}
	
	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

}
