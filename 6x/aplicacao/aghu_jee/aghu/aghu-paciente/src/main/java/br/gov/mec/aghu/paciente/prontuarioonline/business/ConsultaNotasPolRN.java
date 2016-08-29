package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultaNotasPolRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaNotasPolRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	private static final long serialVersionUID = -3039712264781005144L;

	/**
	 * ORADB: AGHC_CERTIF_ASS_ATD
	 * 
	 * Esta função verifica se existem documentos assinados de um tipo de documento para um determinado atendimento ou cirurgia.
  	 * retorna TRUE se encontra e FALSE se não houver.
	 * 
	 * @param nota
	 * @author rodrigo.furtado
	 */
	public Boolean verificarCertificadoAssinado(Integer seqNota, DominioTipoDocumento tipo) {
		List<AghVersaoDocumento> versaoDocumentos = getAmbulatorioFacade().verificaImprime(seqNota, tipo);
		if(versaoDocumentos != null && versaoDocumentos.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ORADB: AGHC_GET_VERSAO_SEQ
	 * 
	 * Esta função retorna o SEQ da versão de um documento de certificação de um
  	 * determinado tipo de documento  Caso tenha mais que uma versão, retorna o primeiro.
  	 * Retorna NULL se não encontra nenhum documento assinado;
	 * 
	 * @param nota
	 * @author rodrigo.furtado
	 */
	public Integer buscarVersaoSeqDoc(Integer seqNota, DominioTipoDocumento tipo) {
		List<AghVersaoDocumento> versaoDocumentos = getAmbulatorioFacade().buscarVersaoSeqDoc(seqNota, tipo);
		if(versaoDocumentos != null &&versaoDocumentos.size() > 0){
			return versaoDocumentos.get(0).getSeq();
		}else{
			return null;
		}
	}	
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	/**
	 * ORADB:AIPP_CHAMA_DOC_CERTIF
	 * 
	 * @param seqNota
	 * @return
	 */
	public Boolean chamarDocCertificado(Integer seqNota, DominioTipoDocumento tipo) {
		if(verificarCertificadoAssinado(seqNota, tipo) && buscarVersaoSeqDoc(seqNota, DominioTipoDocumento.NPO) != null){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * ORADB:CF_cabecalho2Formula
	 * 
	 * @param nota
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public String buscarDadosCabecalho(MamNotaAdicionalEvolucoes nota) throws ApplicationBusinessException {
		String nome;
		//RN9
		nome = getPrescricaoMedicaFacade().buscaConsProf(nota.getServidor())[1].toString();
		
		if(getAmbulatorioFacade().obterDescricaoCidCapitalizada(nome).length() < 35){
			nome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(nome, CapitalizeEnum.TODAS);
		}else{
			nome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(nome, CapitalizeEnum.TODAS).substring(0, 35);
		}
		
		return DateUtil.dataToString(nota.getDthrValida(), "dd/MM/yyyy") + " - " + nome;
	}
	
	/**
	 * ORADB:CF_rodape2Formula
	 * 
	 * @param nota
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public String buscarDadosRodape(MamNotaAdicionalEvolucoes nota) throws ApplicationBusinessException {
		String nome;
		//RN9
		nome = getPrescricaoMedicaFacade().buscaConsProf(nota.getServidor())[1].toString();
		
		if(getAmbulatorioFacade().obterDescricaoCidCapitalizada(nome).length() < 35){
			nome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(nome, CapitalizeEnum.TODAS);
		}else{
			nome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(nome, CapitalizeEnum.TODAS).substring(0, 35);
		}
		
		return "Assinado por " + nome 
			+ " em: " + DateUtil.dataToString(nota.getDthrValida(), "dd/MM/yyyy hh:mm");
	}
	
	/**
	 * ORADB:CF_dados_paciente2Formula
	 * 
	 * @param nota
	 * @return
	 */
	public String[] buscarDadosPaciente(MamNotaAdicionalEvolucoes nota) {
		
		String[] dados = new String[2];
		
		
		AipPacientes paciente = getPacienteFacade().buscaPaciente(nota.getPaciente().getCodigo());
		
		dados[0] = getAmbulatorioFacade().obterDescricaoCidCapitalizada(paciente.getNome());
		dados[1] = paciente.getProntuario().toString();
		
		return dados;
	}
}
