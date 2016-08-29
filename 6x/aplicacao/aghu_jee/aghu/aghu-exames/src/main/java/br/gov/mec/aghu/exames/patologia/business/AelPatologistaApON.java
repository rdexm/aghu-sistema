package br.gov.mec.aghu.exames.patologia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelPatologistaApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.exames.patologia.vo.AelPatologistaLaudoVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelPatologistaApON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelPatologistaApON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private AelPatologistaDAO aelPatologistaDAO;
	
	@Inject
	private AelPatologistaApDAO aelPatologistaApDAO;

	private static final long serialVersionUID = 4879378548867391256L;

	private enum AelPatologistaApONExceptionCode implements BusinessExceptionCode {
		MSG_NAO_EXCLUIR_PATOLOGISTA_LOGADO;
	}
	
	public List<AelPatologistaLaudoVO> listarPatologistaLaudo(Long luxSeq) {
		List<AelPatologistaAps> lista = getAelPatologistaApDAO().listarPatologistaApPorLuxSeq(luxSeq);
		
		List<AelPatologistaLaudoVO> listaVO = new ArrayList<AelPatologistaLaudoVO>();
		
		for (AelPatologistaAps patologistaAp : lista) {
			AelPatologistaLaudoVO vo = new AelPatologistaLaudoVO();
			vo.setPatologistaAp(patologistaAp);
			vo.setPatologista(getAelPatologistaDAO().obterPatologistaAtivoPorServidor(patologistaAp.getServidor()));
			
			listaVO.add(vo);
		}

		return listaVO;
	}
	
	public void removeAdicionaPatologistaLaudo(AelAnatomoPatologico anatomoPatologico, AelPatologista antigoPatologistaRespParam, AelPatologista novoPatologistaResp, RapServidores servidorLogado) throws BaseException {

		AelExameAp exameAP = examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(anatomoPatologico);
		
		AelPatologista antigoPatologistaResp = getAelPatologistaDAO().obterPorChavePrimaria(antigoPatologistaRespParam.getSeq());
		
		if (exameAP != null) {

			Short ordem = removerPatologistaLaudo(exameAP.getSeq(), antigoPatologistaResp, servidorLogado);
			
			if (ordem == null) {
				List<AelPatologistaAps> patologistas = getAelPatologistaApDAO().obterPatologistasPeloExameSeqOrdenadoPelaOrdemMedicoLaudo(exameAP.getSeq());
				if (!patologistas.isEmpty()) {
					ordem = patologistas.get(0).getOrdemMedicoLaudo();
					ordem++;
				}
				else {
					ordem = 1;
				}
			}
			
			AelPatologistaAps patologistaAP = new AelPatologistaAps();
			patologistaAP.setAelExameAps(exameAP);
			patologistaAP.setServidor(novoPatologistaResp.getServidor());
			
			patologistaAP.setOrdemMedicoLaudo(ordem);
			
			examesPatologiaFacade.persistirAelPatologistaAps(patologistaAP);
		}
		
	}
	
	
	private Short removerPatologistaLaudo(Long luxSeq, AelPatologista antigoPatologistaResp, RapServidores servidorLogado) throws BaseException {
		List<AelPatologistaAps> lista = getAelPatologistaApDAO().listarPatologistaApPorLuxSeq(luxSeq);
		Short retorno = null;
				
		for (AelPatologistaAps patologistaAP : lista) {
			if (antigoPatologistaResp.getServidor().equals(patologistaAP.getServidor())) {
				examesPatologiaFacade.excluir(patologistaAP);
				retorno = patologistaAP.getOrdemMedicoLaudo();
			}
		}
		return retorno;
	}

	
	public void validarPatologistaExcluir(RapServidores servidorPatologista, RapServidores patologistaApExcluir) throws ApplicationBusinessException {
		
		if (servidorPatologista.equals(patologistaApExcluir)) {
			throw new ApplicationBusinessException(AelPatologistaApONExceptionCode.MSG_NAO_EXCLUIR_PATOLOGISTA_LOGADO);
		}
		
	}
	
	public String replaceSustenidoLaudoUnico(String str, String oldValue, String newValue) {
		String newStr = "";
		Integer pos = 0;
		if (oldValue.length() == 2) { //se for do #0 até #9 precisa cuidar pra nao dar replace no #10, #11, #12...
			for (int x = 0; x < str.length(); x++) {
				if (str.substring(x, x + 1).equals("#")) {
					if (str.substring(x + 1, x + 2).equals(oldValue.substring(1)) && 
						(x + 2 == str.length() || !CoreUtil.isNumeroInteger(str.substring(x + 2, x + 3))) ) {
						if (x + 2 == str.length()) {
							newStr = newStr.concat(str.substring(pos, x + 2).replaceFirst(oldValue, newValue));
						}
						else {
							newStr = newStr.concat(str.substring(pos, x + 3).replaceFirst(oldValue, newValue));
						}
					}
					else {
						if (x + 2 == str.length()) {
							newStr = newStr.concat(str.substring(pos, x + 2));
						}
						else {
							newStr = newStr.concat(str.substring(pos, x + 3));
						}
					}
					pos = x + 3;
				}
			}
			if (pos < str.length()) {
				newStr = newStr.concat(str.substring(pos));
			}
		}		
		else { //senao apenas dá o replace, vai funcionar até o #99
			newStr = str.replace(oldValue, newValue);
		}
		
		return newStr;
		
	}
	
	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}

	protected AelPatologistaApDAO getAelPatologistaApDAO() {
		return aelPatologistaApDAO;
	}
	
}
