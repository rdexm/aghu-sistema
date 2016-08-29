package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtMedicUsualDAO;
import br.gov.mec.aghu.model.PdtMedicUsual;
import br.gov.mec.aghu.model.PdtMedicUsualId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PdtMedicUsualON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(PdtMedicUsualON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtMedicUsualDAO pdtMedicUsualDAO;


	@EJB
	private PdtMedicUsualRN pdtMedicUsualRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7462923545548405090L;
	
	protected enum PdtMedicUsualONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MEDICAMENTO_UNID_CIRURG_CADASTRADO
		;
	}

	public String persistirPdtMedicUsual(PdtMedicUsual pdtMedicUsual) throws ApplicationBusinessException {
		String msgRetorno;
		
		pdtMedicUsual.setCriadoEm(new Date());
		pdtMedicUsual.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		//carrega ID
		Short unfseq = pdtMedicUsual.getAghUnidadesFuncionais().getSeq();
		Integer medMatCodigo = pdtMedicUsual.getAfaMedicamento().getMatCodigo();
		pdtMedicUsual.setId(new PdtMedicUsualId(medMatCodigo,unfseq));
				
		PdtMedicUsual pdtMedicUsualExiste = getPdtMedicUsualDAO().obterPorChavePrimaria(pdtMedicUsual.getId());
		
		// Se já existir um relacionamento, não deve persistir outro
		if(pdtMedicUsualExiste != null){
			throw new ApplicationBusinessException(PdtMedicUsualONExceptionCode.MENSAGEM_MEDICAMENTO_UNID_CIRURG_CADASTRADO);
		}else {
			getPdtMedicUsualDAO().persistir(pdtMedicUsual);
			msgRetorno = "MENSAGEM_SUCESSO_GRAVAR_MEDICAMENTO_UNIDADE_CIRURGICA";
		}
	
		return msgRetorno;
	}



	public String excluirPdtMedicUsual(PdtMedicUsual pdtMedicUsualDelecao) {
		PdtMedicUsual pdt = getPdtMedicUsualDAO().obterPorChavePrimaria(pdtMedicUsualDelecao.getId());
		if (pdt != null) {
			getPdtMedicUsualDAO().remover(pdt);
			getPdtMedicUsualRN().posDeletePdtMedicUsual(pdt);
			return "MENSAGEM_SUCESSO_EXCLUSAO_MEDICAMENTO_UNIDADE_CIRURGICA";
		}
		return "";
	}
	
	private PdtMedicUsualRN getPdtMedicUsualRN() {
		return pdtMedicUsualRN;
	}

	protected PdtMedicUsualDAO getPdtMedicUsualDAO() {
		return pdtMedicUsualDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
}