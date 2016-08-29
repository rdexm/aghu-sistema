package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@SuppressWarnings("PMD.AtributoEmSeamContextManager")
@Stateless
public class ImprimeAltaAmbulatorialPolRN extends BaseBusiness implements Serializable {


@EJB
private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;

private static final Log LOG = LogFactory.getLog(ImprimeAltaAmbulatorialPolRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IProntuarioOnlineFacade prontuarioOnlineFacade;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2824966939088338168L;
	
	private String enderecoCompleto;
	
	private String textoPostoSaude;
	
	/**
	 * @ORADB MAMC_GET_CONSELHO
	 * @param Integer matricula, Short vinCodigo
	 * @return String assinatura
	 * @throws ApplicationBusinessException 
	 */
	public String buscaAssinaturaMedicoCrm(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {

		if (matricula != null && vinCodigo != null)  {

			/*BuscaConselhoProfissionalServidorVO consProfServidorVO = getProntuarioOnlineFacade()
					.buscaConselhoProfissionalServidorVO(matricula, vinCodigo);
			String assinatura =  consProfServidorVO.getNome() + "\n" + consProfServidorVO.getSiglaConselho() + " " + consProfServidorVO.getNumeroRegistroConselho();
			return assinatura;*/
			return getRelExameFisicoRecemNascidoPOLON().formataNomeProf(matricula, vinCodigo);
		}
		
		return null;

	}	
	
	protected RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON(){
		return relExameFisicoRecemNascidoPOLON;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public String getEnderecoCompleto() throws ApplicationBusinessException  {
		
		AghParametros linha1 = this.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA1);
		AghParametros linha2 = this.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA2);
		
		this.enderecoCompleto = linha1.getVlrTexto() + "\n" + linha2.getVlrTexto();
		
		//this.enderecoCompleto = param.getVlrTexto();

		return this.enderecoCompleto;
	}
	
	public String getTextoPostoSaude() throws ApplicationBusinessException  {
		
		AghParametros param = this.obterAghParametro(AghuParametrosEnum.P_ALTA_AMBUL_MSG_POSTO_POL);
		this.textoPostoSaude = param.getVlrTexto();

		return this.textoPostoSaude;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AghParametros obterAghParametro(AghuParametrosEnum param) throws ApplicationBusinessException  {
		return getParametroFacade().obterAghParametro(param);		
	}
	
	public void setEnderecoCompleto(String enderecoCompleto) {
		this.enderecoCompleto = enderecoCompleto;
	}

	public void setTextoPostoSaude(String textoPostoSaude) {
		this.textoPostoSaude = textoPostoSaude;
	}
}