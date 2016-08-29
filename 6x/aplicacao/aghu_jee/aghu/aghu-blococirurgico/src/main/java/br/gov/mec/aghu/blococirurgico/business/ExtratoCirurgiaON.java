package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ExtratoCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExtratoCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;


	@EJB
	private ExtratoCirurgiaRN extratoCirurgiaRN;

	private static final long serialVersionUID = 5722305668331041985L;
	
	protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}
	
	protected ExtratoCirurgiaRN getExtratoCirurgiaRN() {
		return extratoCirurgiaRN;
	}
	
	public String persistirMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia) throws ApplicationBusinessException {
		MbcExtratoCirurgia original = getMbcExtratoCirurgiaDAO().obterOriginal(extratoCirurgia);
		if (!extratoCirurgia.getCriadoEm().equals(original.getCriadoEm())) {
			getMbcExtratoCirurgiaDAO().atualizar(extratoCirurgia);
			getExtratoCirurgiaRN().posUpdateMbcExtratoCirurgia(extratoCirurgia);
		}
		return "MENSAGEM_EXTRATO_CIRURGIA_ALTERACAO_COM_SUCESSO";
	}
	
	public String excluirMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia, String usuarioLogado) throws ApplicationBusinessException {
		MbcExtratoCirurgia original = getMbcExtratoCirurgiaDAO().obterMbcExtratoCirurgiaPorId(extratoCirurgia.getId());
		getMbcExtratoCirurgiaDAO().remover(original);
		return "MENSAGEM_EXTRATO_CIRURGIA_EXCLUSAO_COM_SUCESSO";
	}
	
}
