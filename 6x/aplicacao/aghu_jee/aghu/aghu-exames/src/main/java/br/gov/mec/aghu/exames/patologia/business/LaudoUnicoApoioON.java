package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.vo.AelPatologistaLaudoVO;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class LaudoUnicoApoioON extends BaseBusiness {
	private static final long serialVersionUID = -8718884073902328558L;
	
	private static final Log LOG = LogFactory.getLog(LaudoUnicoApoioON.class);
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public void setPatologistasLaudo(StringBuffer vLaudo, String QUEBRA_LINHA, Long luxSeq) {
		// PATOLOGISTAS
		List<AelPatologistaLaudoVO> listaPatologistaLaudoVO	= getExamesPatologiaFacade().listarPatologistaLaudo(luxSeq);
		int count = 0;
		if (!this.isHCPA()) {
			vLaudo.append(QUEBRA_LINHA);
			vLaudo.append("<table cellspacing=\"15px\" style=\"width: 750px\"><tr valign='bottom' height='40'>");
		}
		for( AelPatologistaLaudoVO patologista : listaPatologistaLaudoVO) {

			RapConselhosProfissionais conselho = getCadastrosBasicosFacade().obterConselhoProfissionalComNroRegConselho(patologista.getPatologista().getServidor().getId().getMatricula(), 
					patologista.getPatologista().getServidor().getId().getVinCodigo(), DominioSituacao.A);
			String numeroConselho = getCadastrosBasicosFacade().obterRapQualificaoComNroRegConselho(patologista.getPatologista().getServidor().getId().getMatricula(), 
					patologista.getPatologista().getServidor().getId().getVinCodigo(), DominioSituacao.A);
			
			StringBuffer nomePatologista = new StringBuffer();
			nomePatologista.append(patologista.getPatologista().getServidor().getPessoaFisica().getNome()).append(" - ");
			if (!DominioFuncaoPatologista.P.equals(patologista.getPatologista().getFuncao()) && !DominioFuncaoPatologista.T.equals(patologista.getPatologista().getFuncao())) {
				nomePatologista.append("Médico ");
			}
			nomePatologista.append(patologista.getPatologista().getFuncao().getDescricao().replace("Patologista ", ""));
			if (conselho != null) {
				nomePatologista.append(" - ").append(conselho.getSigla()).append(": ").append(numeroConselho);
			}
			
			if (this.isHCPA()) {
				vLaudo.append(nomePatologista)
					  .append(QUEBRA_LINHA).append(QUEBRA_LINHA).append(QUEBRA_LINHA);
			}
			else {
				if (count == 3) {
					count = 0;
					vLaudo.append("</tr><tr valign='bottom' height='80'>");
				}
				else {
					vLaudo.append("<td>").append(nomePatologista).append("</td>");
					count++;
				}
			}
		}
		if (!this.isHCPA()) {
			vLaudo.append("</tr></table>");
		}
		
	}	
	
	protected ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}

	protected IExamesPatologiaFacade getExamesPatologiaFacade(){
		return examesPatologiaFacade;
	}
}
