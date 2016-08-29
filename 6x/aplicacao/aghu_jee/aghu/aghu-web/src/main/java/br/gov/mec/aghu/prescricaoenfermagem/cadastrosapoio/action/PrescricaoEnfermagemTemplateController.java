package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.core.action.ActionController;

public class PrescricaoEnfermagemTemplateController extends ActionController {

	private static final long serialVersionUID = 2431220734019178161L;

	private PrescricaoEnfermagemVO prescricaoEnfermagemVO = new PrescricaoEnfermagemVO();

	public PrescricaoEnfermagemVO getPrescricaoEnfermagemVO() {
		return prescricaoEnfermagemVO;
	}

	public void setPrescricaoEnfermagemVO(PrescricaoEnfermagemVO prescricaoEnfermagemVO) {
		this.prescricaoEnfermagemVO = prescricaoEnfermagemVO;
	}

}
