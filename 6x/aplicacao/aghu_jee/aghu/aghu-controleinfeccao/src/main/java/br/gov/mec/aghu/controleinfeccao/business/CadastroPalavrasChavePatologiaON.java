package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciPalavraChavePatologiaDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class CadastroPalavrasChavePatologiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CadastroPalavrasChavePatologiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MciPalavraChavePatologiaDAO mciPalavraChavePatologiaDAO;
	
	@EJB
	private MciPalavraChavePatologiaON mciPalavraChavePatologiaON;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private static final long serialVersionUID = -8472096228465751522L;
	
	public enum CadastroPalavrasChavePatologiaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PERIODO_EXCLUSAO_PALAVRA_CHAVE;
	}

	public List<MciPalavraChavePatologia> listarPalavraChavePatologia(Integer codigoPatologia) {
		return this.mciPalavraChavePatologiaDAO.listarPalavraChavePatologia(codigoPatologia);
	}
	
	public void inserirPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia, Integer codigoPatologia) {
		
		this.mciPalavraChavePatologiaON.inserirMciPalavraChavePatologia(palavraChavePatologia, codigoPatologia);
	}
	
	public void atualizarPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) throws ApplicationBusinessException {
		
		MciPalavraChavePatologia palavraChavePatologiaOriginal = this.mciPalavraChavePatologiaDAO.obterOriginal(palavraChavePatologia);
		
		this.mciPalavraChavePatologiaON.alterarMciPalavraChavePatologia(palavraChavePatologia, palavraChavePatologiaOriginal);
	}
	
	public void excluirPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		
		Integer numeroDias = DateUtil.obterQtdDiasEntreDuasDatas(palavraChavePatologia.getCriadoEm(), new Date());
		
		if (CoreUtil.maior(numeroDias, parametro.getVlrNumerico())) {
			throw new ApplicationBusinessException(CadastroPalavrasChavePatologiaONExceptionCode.MENSAGEM_PERIODO_EXCLUSAO_PALAVRA_CHAVE);
		}
		this.mciPalavraChavePatologiaON.excluirMciPalavraChavePatologia(palavraChavePatologia);
	}
}
