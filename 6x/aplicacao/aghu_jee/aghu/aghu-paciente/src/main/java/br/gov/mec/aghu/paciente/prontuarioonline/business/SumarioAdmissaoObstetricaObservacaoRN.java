package br.gov.mec.aghu.paciente.prontuarioonline.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SumarioAdmissaoObstetricaObservacaoRN extends BaseBusiness {


@EJB
private RelatorioAtendEmergObstetricaRN relatorioAtendEmergObstetricaRN;

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaObservacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = 4131305012567998372L;

	/**
	 * RESPONSAVEL
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	 public void executarResponsavel(SumarioAdmissaoObstetricaInternacaoVO vo) throws ApplicationBusinessException {
         Integer matricula = null;
         Short vinCodigo = null;
         if(vo.getParametrosHQL().get(ParametrosReportEnum.P_MATRICULA) != null) {
                 matricula = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_MATRICULA);                        
         }
         if(vo.getParametrosHQL().get(ParametrosReportEnum.P_VIN_CODIGO) != null) {                        
                 vinCodigo = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_VIN_CODIGO);
         }
         if(matricula != null && vinCodigo != null) {
                 //vo.setNomeRespNota(obterCPResponsavel(matricula, vinCodigo));
                 vo.setNomeProfissional(obterCPResponsavel(matricula, vinCodigo));
         }
	 }

	 protected RelatorioAtendEmergObstetricaRN getRelatorioAtendEmergObstetricaRN() {
			return relatorioAtendEmergObstetricaRN;
	 }
	 
	 /**
	 * 
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private String obterCPResponsavel(Integer matricula, Short vinculo) throws ApplicationBusinessException {
	
		BuscaConselhoProfissionalServidorVO conselhoVO;
		try {
			conselhoVO = getRelatorioAtendEmergObstetricaRN().buscaConselhoProfissionalServidorVO(matricula, vinculo);
			StringBuilder sb = new StringBuilder();		
			if (conselhoVO != null) {
				if (StringUtils.isNotEmpty(conselhoVO.getNome())) {
					sb.append(conselhoVO.getNome()).append("   ");
				}
				if (StringUtils.isNotEmpty(conselhoVO.getSiglaConselho())) {
					sb.append(conselhoVO.getSiglaConselho()).append("   ");
				}
				if (StringUtils.isNotEmpty(conselhoVO.getNumeroRegistroConselho())) {
					sb.append(conselhoVO.getNumeroRegistroConselho());
				}
			} 
			
			if (StringUtils.isBlank(sb.toString())){
				sb.append(obterNomeProfissional(matricula, vinculo));
			}
			
			return sb.toString();
		} catch (ApplicationBusinessException e) {
			logError(e);
		}
	
		return "";
		
	}
	
	/**
	 * @ORADB RAPC_BUSCA_NOME
	 * 
	 * Obtem o nome do profissional envolvido no nascimento
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * 
	 * @author guilherme.finotti
	 * @since 09/08/2012
	 */
	private String obterNomeProfissional(Integer matricula, Short vinCodigo) {
		RapServidores servidor = getRegistroColaboradorFacade().buscarServidor(vinCodigo, matricula);
		if(servidor != null && servidor.getPessoaFisica() != null) {
			return servidor.getPessoaFisica().getNome();
		}
		return "";
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
