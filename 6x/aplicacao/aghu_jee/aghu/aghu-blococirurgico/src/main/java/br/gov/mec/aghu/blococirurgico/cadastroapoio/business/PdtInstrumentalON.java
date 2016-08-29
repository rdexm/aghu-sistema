package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtInstrPorEquipDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrumentalDAO;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PdtInstrumentalON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtInstrumentalON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtInstrPorEquipDAO pdtInstrPorEquipDAO;

	@Inject
	private PdtInstrumentalDAO pdtInstrumentalDAO;


	@EJB
	private PdtInstrumentalRN pdtInstrumentalRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long serialVersionUID = 5722305668331041985L;
	
	protected enum InstrumentosONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INSTRUMENTOS_EQUIPAMENTOS_JA_ATIBUIDO
		;
	}
	
	public String persistirPdtInstrumental(PdtInstrumental instrumental) throws ApplicationBusinessException{
		if(instrumental.getSeq() == null){
			instrumental.setCriadoEm(new Date());
			instrumental.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			getInstrumentosRN().atualizarServidor();
			getPdtInstrumentalDAO().persistir(instrumental);
			return "MENSAGEM_INSTRUMENTOS_INSERCAO_COM_SUCESSO";
		}else{
			getInstrumentosRN().atualizarServidor();
			getPdtInstrumentalDAO().atualizar(instrumental);
			getInstrumentosRN().posUpdatePdtInstrumental(instrumental);
			return "MENSAGEM_INSTRUMENTOS_ALTERACAO_COM_SUCESSO";
		}
	}
	
	public String persistirPdtInstrPorEquip(PdtInstrPorEquip instrPorEquip) throws ApplicationBusinessException{
		instrPorEquip.setCriadoEm(new Date());
		instrPorEquip.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		getInstrumentosRN().atualizarServidor();
		if(getPdtInstrPorEquipDAO().obterPorChavePrimaria(instrPorEquip.getId()) != null){
			throw new ApplicationBusinessException(InstrumentosONExceptionCode.MENSAGEM_INSTRUMENTOS_EQUIPAMENTOS_JA_ATIBUIDO);
		}
		getPdtInstrPorEquipDAO().persistir(instrPorEquip);
		return "MENSAGEM_INSTRUMENTOS_EQUIPAMENTOS_INSERCAO_COM_SUCESSO";
	}
	
	public String removerPdtInstrumental(PdtInstrumental instrumental){
		getPdtInstrumentalDAO().remover(instrumental);
		getInstrumentosRN().posDeletePdtInstrumental(instrumental);
		return "MENSAGEM_INSTRUMENTOS_REMOCAO_COM_SUCESSO";
	}

	protected PdtInstrumentalRN getInstrumentosRN() {
		return pdtInstrumentalRN;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected PdtInstrumentalDAO getPdtInstrumentalDAO() {
		return pdtInstrumentalDAO;
	}
	
	protected PdtInstrPorEquipDAO getPdtInstrPorEquipDAO() {
		return pdtInstrPorEquipDAO;
	}
}
