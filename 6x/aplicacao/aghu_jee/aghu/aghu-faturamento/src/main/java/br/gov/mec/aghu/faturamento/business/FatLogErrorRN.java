package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatLogErrorRN extends BaseBusiness implements Serializable {
	
	private static final long serialVersionUID = 6502445578169064326L;

	private static final Log LOG = LogFactory.getLog(FatLogErrorRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatLogErrorDAO fatLogErrorDAO; 
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	public void persistir(FatLogError fatLogError) throws ApplicationBusinessException {
		
		if (fatLogError.getSeq() == null) {
			inserir(fatLogError);
			fatLogErrorDAO.flush();
		} 
	}

	private void inserir(FatLogError fatLogError) throws ApplicationBusinessException {
		fatLogError.setCriadoPor(servidorLogadoFacade.obterServidorLogado().getUsuario());
		fatLogError.setCriadoEm(new Date());
		
		executarAntesDeInserirLogErro(fatLogError);
		
		fatLogErrorDAO.persistir(fatLogError);
	}


	/**
	 * ORADB FATT_LER_BRI
	 * 
	 * Migracao diferente do código original, conforme instruções dos analistas
	 */
	public void executarAntesDeInserirLogErro(FatLogError fatLogError){
		
		// Para Garantir que não irá ocorrer InvalidStateException
		fatLogError.setCriadoPor(StringUtils.substring(fatLogError.getCriadoPor(),0,29));
		fatLogError.setErro(StringUtils.substring(fatLogError.getErro(),0,299));
		fatLogError.setModulo(StringUtils.substring(fatLogError.getModulo(),0,29));
		fatLogError.setPrograma(StringUtils.substring(fatLogError.getPrograma(),0,69));
		
		if(fatLogError.getPacCodigo() == null && fatLogError.getProntuario() != null){
			List<Integer> listaPacCodigo = pacienteFacade.executarCursorPaciente(fatLogError.getProntuario());
			if(listaPacCodigo != null && !listaPacCodigo.isEmpty()){
				fatLogError.setPacCodigo(listaPacCodigo.get(0));
			}
		}else if(fatLogError.getPacCodigo() != null && fatLogError.getProntuario() == null){
			Integer prontuario = pacienteFacade.obterProntuarioPorPacCodigo(fatLogError.getPacCodigo());
			fatLogError.setProntuario(prontuario);
		}
	}


}
