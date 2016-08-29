package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoIngressoCtiDAO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmMotivoIngressoCtiRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4299864551975184699L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private MpmMotivoIngressoCti mpmMotivoIngressoCti;
	
	@Inject
	private MpmMotivoIngressoCtiDAO mpmMotivoIngressoCtiDAO;
	
	@Inject
	private AghAtendimentoDAO aghuAtendimentosDAO;
	
	private static final Log LOG = LogFactory.getLog(MpmMotivoIngressoCtiRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MpmMotivoIngressoCtiRNExceptionCode implements BusinessExceptionCode {
		
	}
	
	public String salvarDiagPacCti(PrescricaoMedicaVO prescricaoMedicaVO, String complementoCid, AghCid aghCid, Integer atdSeq)
			throws ApplicationBusinessException{
		String msg = null;
		MpmMotivoIngressoCti mpmMotivoIngressoCti = new MpmMotivoIngressoCti();
		
		try {
			if(aghCid == null){
				return "MS01_INFORMAR_CID";
			}else{
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
				
				mpmMotivoIngressoCti.setServidor(servidorLogado);
				
				mpmMotivoIngressoCti.setCid(aghCid);
				mpmMotivoIngressoCti.setComplementoCid(complementoCid);
				
				AghAtendimentos agAt = this.aghuAtendimentosDAO.obterPorChavePrimaria(atdSeq);
				if(agAt != null){
					mpmMotivoIngressoCti.setAtendimento(agAt);
				}
				mpmMotivoIngressoCti.setDthrIngressoUnid(prescricaoMedicaVO.getDthrInicio());
				mpmMotivoIngressoCti.setDthrInformacao(new Date());
				mpmMotivoIngressoCti.setCriadoEm(new Date());
				mpmMotivoIngressoCti.setUnidadeFuncional(agAt.getUnidadeFuncional());
				
				mpmMotivoIngressoCtiDAO.persistir(mpmMotivoIngressoCti);
				msg = "MENSAGEM_DIAGNOSTICO_GRAVADO_COM_SUCESSO";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return msg;
	}

	public MpmMotivoIngressoCti getMpmMotivoIngressoCti() {
		return mpmMotivoIngressoCti;
	}

	public void setMpmMotivoIngressoCti(MpmMotivoIngressoCti mpmMotivoIngressoCti) {
		this.mpmMotivoIngressoCti = mpmMotivoIngressoCti;
	}
}