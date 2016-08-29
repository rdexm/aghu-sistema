package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioValorDataItemSumario;
import br.gov.mec.aghu.model.EpeDataItemSumario;
import br.gov.mec.aghu.model.EpeDataItemSumarioId;
import br.gov.mec.aghu.model.EpeItemPrescricaoSumario;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDataItemSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * ORADB EPEK_GERA_SUMARIO
 * 
 */
@SuppressWarnings("PMD.AtributoEmSeamContextManager")
@Stateless
public class ManterSumarioEnfermagemRN extends BaseBusiness {


@EJB
private ManterSintaxeSumarioEnfermagemRN manterSintaxeSumarioEnfermagemRN;

private static final Log LOG = LogFactory.getLog(ManterSumarioEnfermagemRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;

@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private EpeItemPrescricaoSumarioDAO epeItemPrescricaoSumarioDAO;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private EpeDataItemSumarioDAO epeDataItemSumarioDAO;

@Inject
private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4899739978832634584L;

	
	/**
	 * ORADB Procedure EPEK_GERA_SUMARIO.EPEP_GERA_SUMARIO
	 * 
	 * Executa a rotina gera os dados para o Sumário de Prescrição Enfermagem.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosSumarioPrescricaoEnfermagem(
			Integer seqAtendimento, DominioTipoEmissaoSumario tipoEmissao)
			throws ApplicationBusinessException {
		EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO = this.getEpePrescricaoEnfermagemDAO();
		EpeDataItemSumarioDAO epeDataItemSumarioDAO = getEpeDataItemSumarioDAO();
		IPacienteFacade pacienteFacade = getPacienteFacade();
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		
		Date dataInicio = null;
		Date dataFim = null;
		Date dataMenorReferencia = null;
		
		Integer apaSeq = pacienteFacade.gerarAtendimentoPaciente(seqAtendimento);

		dataInicio = prescricaoMedicaFacade.obterDataInternacao2(seqAtendimento);
		dataFim = getAghuFacade().obterDataFimAtendimento(seqAtendimento);
		if(dataFim == null){
			dataFim = new Date();
		}
		
		if(DominioTipoEmissaoSumario.I.equals(tipoEmissao)){
			EpePrescricaoEnfermagem pe = epePrescricaoEnfermagemDAO.obterPrescricaoEnfermagemComMaiorDataReferenciaParaGerarSumario(seqAtendimento, dataInicio, dataFim);
			if(pe == null || pe.getDtReferencia() == null){
				List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem = epePrescricaoEnfermagemDAO.listarPrescricoesEnfermagemDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(seqAtendimento, dataInicio);
				
				if(listaPrescricaoEnfermagem != null && !listaPrescricaoEnfermagem.isEmpty() && listaPrescricaoEnfermagem.get(0).getDtReferencia() != null){
					dataMenorReferencia = listaPrescricaoEnfermagem.get(0).getDtReferencia();
				}else{
					dataMenorReferencia = dataInicio;
				}
			} else {
				if(pe.getDtReferencia() != null){
					dataMenorReferencia = DateUtil.adicionaDias(pe.getDtReferencia(), 1);
				} else{
					dataMenorReferencia = dataInicio;
				}
			}
			
			if(DateUtils.truncate(dataMenorReferencia, Calendar.DAY_OF_MONTH).before(DateUtils.truncate(dataFim, Calendar.DAY_OF_MONTH))) {
				List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem = epePrescricaoEnfermagemDAO.listarPrescricoesEnfermagemComDataImpSumario(seqAtendimento, dataMenorReferencia, dataFim);
				if(listaPrescricaoEnfermagem != null){
					for (EpePrescricaoEnfermagem prescricaoEnfermagem : listaPrescricaoEnfermagem) {
						this.geraItens(prescricaoEnfermagem.getId().getAtdSeq(), apaSeq, prescricaoEnfermagem.getDthrInicio(), prescricaoEnfermagem.getDthrFim(), prescricaoEnfermagem.getDtReferencia());
					}
				}
			}
		}else if(DominioTipoEmissaoSumario.P.equals(tipoEmissao) || DominioTipoEmissaoSumario.C.equals(tipoEmissao)){
			boolean temDados = false;
			
			List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem = epePrescricaoEnfermagemDAO.listarPrescricoesParaGerarSumarioDePrescricaoEnfermagem(seqAtendimento, dataInicio, dataFim);
			if(listaPrescricaoEnfermagem != null){
				for (EpePrescricaoEnfermagem prescricaoEnfermagem : listaPrescricaoEnfermagem) {
					temDados = true;
					if(DateValidator.validaDataMenor(DateUtils.truncate(prescricaoEnfermagem.getDtReferencia(), Calendar.DAY_OF_MONTH), DateUtils.truncate(dataInicio, Calendar.DAY_OF_MONTH))){
						dataInicio = prescricaoEnfermagem.getDtReferencia();
					}
					if(prescricaoEnfermagem.getDataImpSumario() == null){
						this.geraItens(prescricaoEnfermagem.getId().getAtdSeq(), apaSeq, prescricaoEnfermagem.getDthrInicio(), prescricaoEnfermagem.getDthrFim(), prescricaoEnfermagem.getDtReferencia());
					}
				}
					
			}
			if(!temDados){
				Date dataInicioPesq = epeDataItemSumarioDAO.obterMenorDataDoDataItemSumario(seqAtendimento, apaSeq);
				if(dataInicioPesq != null){
					temDados = true;
					if(DateValidator.validaDataMenor(DateUtils.truncate(dataInicioPesq, Calendar.DAY_OF_MONTH), DateUtils.truncate(dataInicio, Calendar.DAY_OF_MONTH))){
						dataInicio = dataInicioPesq;
					}
				}
			}
		}
		
		epePrescricaoEnfermagemDAO.flush();
	}
	
	/**
	 * ORADB Procedure EPEK_GERA_SUMARIO.P_GERA_ITENS
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void geraItens(Integer seqAtendimento,Integer seqAtendimentoPaciente, Date dataHoraInicio,
						Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
	
		//epep_gera_sumr_cuid
		this.geraDadosPrescricaoEnfermagemCuidados(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);

	}
	
	

	/**
	 * ORADB Procedure EPEP_GERA_SUMARIO.EPEP_GERA_SUMR_CUID
	 * 
	 * Rotina para gerar os dados de prescrição de cuidados para o Sumário de
	 * Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void geraDadosPrescricaoEnfermagemCuidados(Integer seqPrescricaoEnfermagem,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<EpePrescricoesCuidados> listaPrescricaoCuidado =  getEpePrescricoesCuidadosDAO().obterPrescricoesCuidadoParaSumarioCuidado(seqPrescricaoEnfermagem, dataHoraInicio, dataHoraFim);
		processaListaPrescricaoEnfermagemCuidados(listaPrescricaoCuidado, seqPrescricaoEnfermagem,
				apaSeq, dataHoraInicio, dataHoraFim,
				dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoEnfermagemCuidados(
			List<EpePrescricoesCuidados> listaPrescricaoCuidado,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		EpeItemPrescricaoSumarioDAO epeItemPrescricaoSumarioDAO = getEpeItemPrescricaoSumarioDAO();
		EpePrescricoesCuidadosDAO epePrescricaoCuidadoDAO = getEpePrescricoesCuidadosDAO();
		EpeDataItemSumarioDAO epeDataItemSumarioDAO = getEpeDataItemSumarioDAO();
		ManterSintaxeSumarioEnfermagemRN manterSintaxeSumarioRN = getManterSintaxeSumarioEnfermagemRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (EpePrescricoesCuidados prescricaoCuidado : listaPrescricaoCuidado) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemCuidados(seqAtendimento, apaSeq, prescricaoCuidado.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(prescricaoCuidado.getDthrInicio(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString(); 
				}
				if(prescricaoCuidado.getDthrFim() != null && DateValidator.validaDataMenor(prescricaoCuidado.getDthrFim(), dataHoraFim) && prescricaoCuidado.getServidorMvtoValida() != null && DominioIndPendentePrescricoesCuidados.N.equals(prescricaoCuidado.getPendente())){
					valorS = DominioValorDataItemSumario.S.toString(); 
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<EpeDataItemSumario> listaDataItemSumario = epeDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (EpeDataItemSumario dataItemSumario : listaDataItemSumario) {
					seqp = dataItemSumario.getId().getSeqp();
					if(valorP != null && valorP.equals(dataItemSumario.getValor()) && DateUtil.diffInDays(DateUtil.truncaData(dataItemSumario.getData()), DateUtil.truncaData(data)) == 0){
						achouP = true;
					}
					if(valorS != null && valorS.equals(dataItemSumario.getValor())&& DateUtil.diffInDays(DateUtil.truncaData(dataItemSumario.getData()), DateUtil.truncaData(data)) == 0){
						achouS = true;
					}
				}
				if(!achouP && valorP != null){
					seqp = seqp + 1;
					
					EpeDataItemSumario dis = new EpeDataItemSumario();
					EpeDataItemSumarioId disId = new EpeDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setIsuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					EpeItemPrescricaoSumario itemPrescricaoSumarios = epeItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumario(itemPrescricaoSumarios);
					
					epeDataItemSumarioDAO.persistir(dis);
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					EpeDataItemSumario dis = new EpeDataItemSumario();
					EpeDataItemSumarioId disId = new EpeDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setIsuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					EpeItemPrescricaoSumario itemPrescricaoSumarios = epeItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumario(itemPrescricaoSumarios);
					
					epeDataItemSumarioDAO.persistir(dis);
				}
			}

			List<EpePrescricoesCuidados> listaAux = epePrescricaoCuidadoDAO
					.obterPrescricoesCuidadoPai(prescricaoCuidado.getId()
							.getSeq(), seqAtendimento, dataHoraInicio,
							dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoEnfermagemCuidados(listaAux, seqAtendimento,
						apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		if(epePrescricaoEnfermagemDAO==null){
			epePrescricaoEnfermagemDAO = epePrescricaoEnfermagemDAO;
		}
		return epePrescricaoEnfermagemDAO;
	}

	protected EpeDataItemSumarioDAO getEpeDataItemSumarioDAO() {
		if(epeDataItemSumarioDAO==null){
			epeDataItemSumarioDAO = epeDataItemSumarioDAO;
		}
		return epeDataItemSumarioDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		if(pacienteFacade==null){
			pacienteFacade =  pacienteFacade;
		}
		return pacienteFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		if(prescricaoMedicaFacade==null){
			prescricaoMedicaFacade = this.prescricaoMedicaFacade;
		}
		return prescricaoMedicaFacade;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO(){
		if(epePrescricoesCuidadosDAO==null){
			epePrescricoesCuidadosDAO = epePrescricoesCuidadosDAO;
		}
		return epePrescricoesCuidadosDAO;
	}
	
	protected EpeItemPrescricaoSumarioDAO getEpeItemPrescricaoSumarioDAO() {
		if(epeItemPrescricaoSumarioDAO==null){
			epeItemPrescricaoSumarioDAO = epeItemPrescricaoSumarioDAO;
		}
		return epeItemPrescricaoSumarioDAO;
	}
	
	protected ManterSintaxeSumarioEnfermagemRN getManterSintaxeSumarioEnfermagemRN() {
		if(manterSintaxeSumarioEnfermagemRN == null){
			manterSintaxeSumarioEnfermagemRN = manterSintaxeSumarioEnfermagemRN;
		}
		return manterSintaxeSumarioEnfermagemRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
}