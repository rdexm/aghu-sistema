package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioParadaInternacaoRN extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory.getLog(RelatorioParadaInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 5214199237233104529L;

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	/**
	 * @ORADB - MAMC_INT_NOME_PDF
	 * Funcao chamada no mamr_controle_pc_int que retorna o nome para o arquivo pdf 
	 * gerado para o plano de contingência para evolucao de internação do paciente
	 * @param atdSeq - P_ATD_SEQ
	 * @param tipo - P_TIPO
	 * @param rgtSeq - P_RGT_SEQ
	 * @param unfSeq - P_UNF_SEQ
	 * @param serMatricula - P_SER_MATRICULA
	 * @param serVinCodigo - P_SER_VIN_CODIGO
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public String obterNomeArquivoPdfInternacaoPaciente(Integer atdSeq, String tipo, Long rgtSeq, Short unfSeq, Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException {

		String vNomeProf = null;
		Long vRgtSeq = null;
		StringBuilder vRetorno = new StringBuilder(30);
		String vSysdate = null;
		Integer vProntuario = null;
		String vNome = null;
		String vLtoLtoId = null;
		String vTipo = null;

		// CURSOR c_pac
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		vProntuario = atendimento.getPaciente().getProntuario();
		vNome = atendimento.getPaciente().getNome();
		if (atendimento.getLeito() != null) {
			vLtoLtoId = atendimento.getLeito().getLeitoID();
		}
		// CURSOR c_date
		vSysdate = DateUtil.dataToString(new Date(), "dd/MM/yyyy HH:mm:ss");
		
		if (serMatricula != null && serVinCodigo != null) {
			// se veio da evolução ou alta mostra nome do profissional
			RapServidores rapServidores = new RapServidores();
			rapServidores.getId().setMatricula(serMatricula);
			rapServidores.getId().setVinCodigo(serVinCodigo);
			getPrescricaoMedicaFacade().buscaConsProf(rapServidores);
		} else {
			vNomeProf = "diversos";
		}
		
		// Plano de Contingencia da internacao, chamado pelo mamr_int_parada (Sumário paradacardiorespiratoria)
		if (rgtSeq == null) {
			vTipo = "Sumario";
			vSysdate = "000000";
			vNomeProf = "XXXXX";
			vRgtSeq = 0l;
		} else {
			vTipo = tipo;
			vRgtSeq = rgtSeq;
		}
		
		vRetorno.append(vNome)
		.append(_HIFEN_)
		.append(vProntuario)
		.append(_HIFEN_)
		.append(vTipo)
		.append(_HIFEN_)
		.append(vSysdate)
		.append(_HIFEN_)
		.append(vNomeProf)
		.append(_HIFEN_)
		.append(vRgtSeq)
		.append(_HIFEN_)
		.append(unfSeq)
		.append(_HIFEN_)
		.append(atdSeq)
		.append(_HIFEN_)
		.append(vLtoLtoId)
		.append(".pdf");
		
		return vRetorno.toString();
	}
}
