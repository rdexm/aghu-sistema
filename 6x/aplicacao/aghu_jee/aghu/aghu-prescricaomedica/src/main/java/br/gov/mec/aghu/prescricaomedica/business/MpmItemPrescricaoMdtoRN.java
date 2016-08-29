package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MpmItemPrescricaoMdtoRN  extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(MpmItemPrescricaoMdtoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private MpmPrescricaoMdtoRN mpmPrescricaoMdtoRN;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	private static final long serialVersionUID = -2776477397713835620L;
	
	public void atualizarMpmItemPrescricaoMdto(List<JustificativaMedicamentoUsoGeralVO> medicamentos, Integer jumSeq) throws ApplicationBusinessException {
		for (JustificativaMedicamentoUsoGeralVO vo : medicamentos) {
			MpmItemPrescricaoMdtoId id = new MpmItemPrescricaoMdtoId(vo.getImePmdAtdSeq(), vo.getImePmdSeq(), vo.getMedMatCodigo(), vo.getImeSeqp());
			MpmItemPrescricaoMdto medicamento = this.getMpmItemPrescricaoMdtoDAO().obterPorChavePrimaria(id);
			
			medicamento.setJustificativaUsoMedicamento(this.getMpmJustificativaUsoMdtoDAO().obterJustificativaUsoMdtos(jumSeq));
			medicamento.setOrigemJustificativa(Boolean.TRUE);
			medicamento.setUsoMdtoAntimicrobia(vo.getUsoMdtoAntimicrobia());
			// Atualiza MPM_PRESCRICAO_MDTOS
			this.getMpmPrescricaoMdtoRN().atualizarMpmPrescricaoMdto(vo.getImePmdAtdSeq(), vo.getImePmdSeq(), vo.getDuracaoTratSolicitado());
			
			this.getMpmItemPrescricaoMdtoDAO().merge(medicamento);
		}
	}
	
	public MpmPrescricaoMdtoRN getMpmPrescricaoMdtoRN() {
		return mpmPrescricaoMdtoRN;
	}

	protected MpmItemPrescricaoMdtoDAO getMpmItemPrescricaoMdtoDAO(){
		return mpmItemPrescricaoMdtoDAO;
	}
	
	public MpmJustificativaUsoMdtoDAO getMpmJustificativaUsoMdtoDAO() {
		return mpmJustificativaUsoMdtoDAO;
	}
		
	public MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}
}
