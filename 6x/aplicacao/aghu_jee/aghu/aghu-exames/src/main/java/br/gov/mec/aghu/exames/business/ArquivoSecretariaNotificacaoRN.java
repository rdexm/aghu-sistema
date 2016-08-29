package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ArquivoSecretariaNotificacaoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ArquivoSecretariaNotificacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICadastroPacienteFacade cadastroPacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4586682349669145876L;
	
	private ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}		
	
	/**
	 * @ORADB - AELC_BUSCA_RESULTADO
	 * 
	 * Esta função retorna para um determinado paciente e campo laudo o resultado do exame.
	 * 
	 * @param ree
	 * @return
	 */
	public String obterResultado(AelResultadoExame ree) {
		if (ree != null) {
			if (ree.getValor() != null) {
				return ree.getValor().toString();
			}
			if (ree.getResultadoCodificado() != null) {
				return ree.getResultadoCodificado().getDescricao();
			}
			if (ree.getResultadoCaracteristica() != null) {
				return ree.getResultadoCaracteristica() .getDescricao();
			}
			if (ree.getDescricao() != null) {
				return ree.getDescricao();
			}
		}
		return null;
	}
	
	/**
	 * @ORADB - AIPC_GET_END_PAC
	 * 
	 * Esta função retorna um endereço de um determinado paciente.
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public String obterEnderecoPaciente(Integer pacCodigo) {
		VAipEnderecoPaciente end = getCadastroPacienteFacade().obterEndecoPaciente(pacCodigo);
		StringBuilder sb = new StringBuilder();
		if (end != null) {
			sb.append(end.getLogradouro()).append(' ').append(end.getNroLogradouro());
			if (!StringUtils.isEmpty(end.getComplLogradouro())) {
				sb.append('/').append(end.getComplLogradouro());
			} 
			sb.append(' ').append(end.getCidade()).append(' ').append(StringUtils.upperCase(VAipEnderecoPaciente.Fields.CEP.toString())).append(": ").append(end.getCep());
		}
		return sb.toString();
	}
	
	/**
	 * @ORADB - AELC_BUSCA_RES_NOTIF
	 * 
	 * Esta função retorna para um determinado paciente e campo laudo do resultado do exame
	 * 
	 * @param rcdGtcseq
	 * @param rcdSeqp
	 * @param resultNumExp
	 * @param resultAlfanum
	 * @return
	 */
	public String aelcBuscaResNotif(AelResultadoCodificado rec, Long resultNumExp, String resultAlfanum) {
		String resultado = null;
		
		if (resultNumExp != null) {
			return resultado = resultNumExp.toString();
		}
		
		if (resultAlfanum != null) {
			return resultado = resultAlfanum; 
		}
		
		if (rec != null) {
			//AelResultadoCodificadoId arcId = new AelResultadoCodificadoId(rcdGtcSeq, rcdSeqp);
			//AelResultadoCodificado aec = getAelResultadoCodificadoDAO().obterPorChavePrimaria(arcId);
			//if (rec != null) {
				return resultado = rec.getDescricao();
			//}
		}
		return resultado;
	}
	
}
	