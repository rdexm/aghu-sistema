package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtCidPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtComplementoPorCidDAO;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtCidPorProcId;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtComplementoPorCidId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PdtCidPorProcON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtCidPorProcON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtComplementoPorCidDAO pdtComplementoPorCidDAO;

	@Inject
	private PdtCidPorProcDAO pdtCidPorProcDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private PdtCidPorProcRN pdtCidPorProcRN;

	private static final long serialVersionUID = 5722305668331041985L;
	
	protected enum CidsProcedimentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CID_POR_PROCEDIMENTO_JA_GRAVADO
		;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}	
	
	protected PdtCidPorProcRN getCidsProcedimentoRN() {
		return pdtCidPorProcRN;
	}
	
	public String persistirCidProcedimento(PdtCidPorProc cidProcedimento, Integer dptSeq, Integer cidSeq) throws ApplicationBusinessException {
		if (cidProcedimento.getId() == null) {
			if(getPdtCidPorProcDAO().obterPorChavePrimaria(new PdtCidPorProcId(dptSeq, cidSeq)) != null){
				throw new ApplicationBusinessException(CidsProcedimentoONExceptionCode.MENSAGEM_CID_POR_PROCEDIMENTO_JA_GRAVADO);
			}
			cidProcedimento.setId(new PdtCidPorProcId(dptSeq, cidSeq));			
			cidProcedimento.setCriadoEm(new Date());
			cidProcedimento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			getPdtCidPorProcDAO().persistir(cidProcedimento);
			return "MENSAGEM_CIDS_PROCEDIMENTO_INSERIDO_COM_SUCESSO";
		} else {
			cidProcedimento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			getPdtCidPorProcDAO().atualizar(cidProcedimento);
			getCidsProcedimentoRN().posUpdatePdtCidPorProc(cidProcedimento);
			return "MENSAGEM_CIDS_PROCEDIMENTO_ALTERADO_COM_SUCESSO";
		}
	}

	public String persistirComplemento(PdtComplementoPorCid complemento, Integer dptSeq, Integer cidSeq) throws ApplicationBusinessException {
		if (complemento.getCriadoEm() == null) {
			complemento.setCriadoEm(new Date());
			Short maxSep = getPdtComplementoPorCidDAO().obterMaxSeqP(dptSeq, cidSeq);
			maxSep++;
			complemento.setId(new PdtComplementoPorCidId(dptSeq, cidSeq, maxSep));
			complemento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			getPdtComplementoPorCidDAO().persistir(complemento);
			return "MENSAGEM_CIDS_PROCEDIMENTO_COMPLEMENTO_INSERIDO_COM_SUCESSO";
		} else {
			complemento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			getPdtComplementoPorCidDAO().atualizar(complemento);
			getCidsProcedimentoRN().posUpdatePdtComplementoPorCid(complemento);
			return "MENSAGEM_CIDS_PROCEDIMENTO_COMPLEMENTO_ALTERADO_COM_SUCESSO";
		}
		
	}
	
	protected PdtComplementoPorCidDAO getPdtComplementoPorCidDAO(){
		return pdtComplementoPorCidDAO;
	}

	protected PdtCidPorProcDAO getPdtCidPorProcDAO() {
		return pdtCidPorProcDAO;
	}
}
