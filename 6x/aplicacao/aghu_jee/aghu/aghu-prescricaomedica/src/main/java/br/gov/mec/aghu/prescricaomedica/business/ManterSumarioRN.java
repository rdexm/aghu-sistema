package br.gov.mec.aghu.prescricaomedica.business;

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
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioValorDataItemSumario;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.MpmDataItemSumario;
import br.gov.mec.aghu.model.MpmDataItemSumarioId;
import br.gov.mec.aghu.model.MpmItemPrescricaoSumario;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmDataItemSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * ORADB MPMK_GERA_SUMARIO
 * 
 * @author dlaks
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
@Stateless
public class ManterSumarioRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

@EJB
private ManterSintaxeSumarioRN manterSintaxeSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterSumarioRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;

@EJB
private IBancoDeSangueFacade bancoDeSangueFacade;

@Inject
private MpmDataItemSumarioDAO mpmDataItemSumarioDAO;

@Inject
private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO;

@Inject
private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

@Inject
private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;

@Inject
private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;

@Inject
private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

@Inject
private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4899739978832634584L;

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMARIO
	 * 
	 * Executa a rotina gera os dados para o Sumário de Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosSumarioPrescricao(
			Integer seqAtendimento, DominioTipoEmissaoSumario tipoEmissao)
			throws ApplicationBusinessException {
		MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO = this.getMpmPrescricaoMedicaDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		IPacienteFacade pacienteFacade = getPacienteFacade();
		ManterAltaSumarioRN manterAltaSumarioRN = getManterAltaSumarioRN();
		
		Date dataInicio = null;
		Date dataFim = null;
		Date dataMenorReferencia = null;
		
		Integer apaSeq = pacienteFacade.gerarAtendimentoPaciente(seqAtendimento);

		dataInicio = manterAltaSumarioRN.obterDataInternacao2(seqAtendimento);
		dataFim = getAghuFacade().obterDataFimAtendimento(seqAtendimento);
		if(dataFim == null){
			dataFim = new Date();
		}
		
		if(DominioTipoEmissaoSumario.I.equals(tipoEmissao)){
			MpmPrescricaoMedica pm = mpmPrescricaoMedicaDAO.obterPrescricaoMedicaComMaiorDataReferenciaParaGerarSumario(seqAtendimento, dataInicio, dataFim);
			if(pm == null || pm.getDtReferencia() == null){
				List<MpmPrescricaoMedica> listaPrescricaoMedica = mpmPrescricaoMedicaDAO.listarPrescricoesMedicasDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(seqAtendimento, dataInicio);
				
				if(listaPrescricaoMedica != null && !listaPrescricaoMedica.isEmpty() && listaPrescricaoMedica.get(0).getDtReferencia() != null){
					dataMenorReferencia = listaPrescricaoMedica.get(0).getDtReferencia();
				}else{
					dataMenorReferencia = dataInicio;
				}
			} else {
				if(pm.getDtReferencia() != null){
					dataMenorReferencia = DateUtil.adicionaDias(pm.getDtReferencia(), 1);
				} else{
					dataMenorReferencia = dataInicio;
				}
			}
			
			if(DateUtils.truncate(dataMenorReferencia, Calendar.DAY_OF_MONTH).before(DateUtils.truncate(dataFim, Calendar.DAY_OF_MONTH))) {
				List<MpmPrescricaoMedica> listaPrescricaoMed = mpmPrescricaoMedicaDAO.listarPrescricoesMedicasComDataImpSumario(seqAtendimento, dataMenorReferencia, dataFim);
				if(listaPrescricaoMed != null){
					for (MpmPrescricaoMedica prescricaoMedica : listaPrescricaoMed) {
						this.geraItens(prescricaoMedica.getId().getAtdSeq(), apaSeq, prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia());
					}
				}
			}
		}else if(DominioTipoEmissaoSumario.P.equals(tipoEmissao) || DominioTipoEmissaoSumario.C.equals(tipoEmissao)){
			boolean temDados = false;
			
			List<MpmPrescricaoMedica> listaPrescricaoMed = mpmPrescricaoMedicaDAO.listarPrescricoesMedicasParaGerarSumarioDePrescricao(seqAtendimento, dataInicio, dataFim);
			if(listaPrescricaoMed != null){
				for (MpmPrescricaoMedica prescricaoMedica : listaPrescricaoMed) {
					temDados = true;
					if(DateValidator.validaDataMenor(DateUtils.truncate(prescricaoMedica.getDtReferencia(), Calendar.DAY_OF_MONTH), DateUtils.truncate(dataInicio, Calendar.DAY_OF_MONTH))){
						dataInicio = prescricaoMedica.getDtReferencia();
					}
					if(prescricaoMedica.getDataImpSumario() == null){
						this.geraItens(prescricaoMedica.getId().getAtdSeq(), apaSeq, prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia());
					}
				}
					
			}
			if(!temDados){
				Date dataInicioPesq = mpmDataItemSumarioDAO.obterMenorDataDoDataItemSumario(seqAtendimento, apaSeq);
				if(dataInicioPesq != null){
					temDados = true;
					if(DateValidator.validaDataMenor(DateUtils.truncate(dataInicioPesq, Calendar.DAY_OF_MONTH), DateUtils.truncate(dataInicio, Calendar.DAY_OF_MONTH))){
						dataInicio = dataInicioPesq;
					}
				}
			}
		}
		
		mpmPrescricaoMedicaDAO.flush();
	}
	
	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.P_GERA_ITENS
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void geraItens(Integer seqAtendimento,Integer seqAtendimentoPaciente, Date dataHoraInicio,
						Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		
		//mpmp_gera_sumr_diet
		this.geraDadosPrescricaoDietas(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);
	
		//mpmp_gera_sumr_cuid
		this.geraDadosPrescricaoCuidados(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);
	
		//mpmp_gera_sumr_mdto
		this.geraDadosPrescricaoMedicamentos(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);
	
		//mpmp_gera_sumr_sol
		this.geraDadosPrescricaoSolucoes(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);

		//mpmp_gera_sumr_proc
		this.geraDadosPrescricaoProcedimentos(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);
		
		//mpmp_gera_sumr_hemo
		this.geraDadosSolicitacoesHemoterapicas(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);
		
		//mpmp_gera_sumr_cons
		this.geraDadosSolicitacaoConsultoria(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);

		//mpmp_gera_sumr_npt
		this.geraDadosPrescricaoNutricaoParental(seqAtendimento, seqAtendimentoPaciente, dataHoraInicio, dataHoraFim, dataReferencia);

	}
	
	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_DIET
	 * 
	 * Rotina para gerar os dados de prescrição de dieta para o Sumário de
	 * Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void geraDadosPrescricaoDietas(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia)
			throws ApplicationBusinessException {
		List<MpmPrescricaoDieta> listaPrescricaoDieta = getMpmPrescricaoDietaDAO()
				.obterPrescricoesDietaParaSumarioDieta(seqAtendimento,
						dataHoraInicio, dataHoraFim);
		
		processaListaPrescricaoDietas(listaPrescricaoDieta, seqAtendimento,
				apaSeq, dataHoraInicio,
				dataHoraFim, dataReferencia);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected void processaListaPrescricaoDietas(
			List<MpmPrescricaoDieta> listaPrescricaoDieta, Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia)
			throws ApplicationBusinessException {
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getMpmPrescricaoDietaDAO();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (MpmPrescricaoDieta prescricaoDietaAux : listaPrescricaoDieta) {
			MpmPrescricaoDieta prescricaoDieta = prescricaoDietaAux;
			Integer ituSeq = manterSintaxeSumarioRN
					.montaSintaxeSumarioItemDietasMedicas(seqAtendimento,
							apaSeq, prescricaoDieta.getId().getSeq());
			if (ituSeq > 0) {
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if (DateValidator.validaDataMenor(prescricaoDieta.getDthrInicio(),
						dataHoraFim)) {
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if (prescricaoDieta.getDthrFim() != null
						&& DateValidator.validaDataMenor(prescricaoDieta
								.getDthrFim(), dataHoraFim)
						&& prescricaoDieta.getServidorValidaMovimentacao() != null
						&& DominioIndPendenteItemPrescricao.N
								.equals(prescricaoDieta.getIndPendente())) {
					valorS = DominioValorDataItemSumario.S.toString();
				}
				achouP = false;
				achouS = false;
				seqp = 0;

				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO
						.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
					seqp = dataItemSumario.getId().getSeqp();
					if (valorP != null
							&& valorP.equals(dataItemSumario.getValor())
							&& DateUtil.diffInDays(DateUtil
									.truncaData(dataItemSumario.getData()),
									DateUtil.truncaData(data)) == 0) {
						achouP = true;
					}
					if (valorS != null
							&& valorS.equals(dataItemSumario.getValor())
							&& DateUtil.diffInDays(DateUtil
									.truncaData(dataItemSumario.getData()),
									DateUtil.truncaData(data)) == 0) {
						achouS = true;
					}

				}
				if (!achouP && valorP != null) {
					seqp = seqp + 1;

					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);

					dis.setData(data);
					dis.setValor(valorP);

					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO
							.obterPorChavePrimaria(ituSeq);

					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);

					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if (!achouS && valorS != null) {
					seqp = seqp + 1;

					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);

					dis.setData(data);
					dis.setValor(valorS);

					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO
							.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);

					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}

			List<MpmPrescricaoDieta> listaAux = mpmPrescricaoDietaDAO
					.obterPrescricoesDietaPai(prescricaoDieta.getId().getSeq(),
							seqAtendimento, dataHoraInicio, dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoDietas(listaAux, seqAtendimento, apaSeq,
						dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_CUID
	 * 
	 * Rotina para gerar os dados de prescrição de cuidados para o Sumário de
	 * Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void geraDadosPrescricaoCuidados(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<MpmPrescricaoCuidado> listaPrescricaoCuidado =  getMpmPrescricaoCuidadoDAO().obterPrescricoesCuidadoParaSumarioCuidado(seqAtendimento, dataHoraInicio, dataHoraFim);
		processaListaPrescricaoCuidados(listaPrescricaoCuidado, seqAtendimento,
				apaSeq, dataHoraInicio, dataHoraFim,
				dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoCuidados(
			List<MpmPrescricaoCuidado> listaPrescricaoCuidado,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO = getMpmPrescricaoCuidadoDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (MpmPrescricaoCuidado prescricaoCuidado : listaPrescricaoCuidado) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemCuidadosMedicos(seqAtendimento, apaSeq, prescricaoCuidado.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(prescricaoCuidado.getDthrInicio(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(prescricaoCuidado.getDthrFim() != null && DateValidator.validaDataMenor(prescricaoCuidado.getDthrFim(), dataHoraFim) && prescricaoCuidado.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(prescricaoCuidado.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}

			List<MpmPrescricaoCuidado> listaAux = mpmPrescricaoCuidadoDAO
					.obterPrescricoesCuidadoPai(prescricaoCuidado.getId()
							.getSeq(), seqAtendimento, dataHoraInicio,
							dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoCuidados(listaAux, seqAtendimento,
						apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_MDTO
	 * 
	 * Rotina para gerar os dados de prescrição de medicamentos para o Sumário
	 * de Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosPrescricaoMedicamentos(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<MpmPrescricaoMdto> listaPrescricaoMdto = getMpmPrescricaoMdtoDAO().obterPrescricoesMdtoParaSumarioMdto(seqAtendimento, dataHoraInicio, dataHoraFim);
		
		processaListaPrescricaoMdtos(listaPrescricaoMdto, seqAtendimento, apaSeq, dataHoraInicio, dataHoraFim,	dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoMdtos(
			List<MpmPrescricaoMdto> listaPrescricaoMdto,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getMpmPrescricaoMdtoDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoMdto) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemMedicamento(seqAtendimento, apaSeq, prescricaoMdto.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(prescricaoMdto.getDthrInicio(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(prescricaoMdto.getDthrFim() != null && DateValidator.validaDataMenor(prescricaoMdto.getDthrFim(), dataHoraFim) && prescricaoMdto.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(prescricaoMdto.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}
			
			List<MpmPrescricaoMdto> listaAux = mpmPrescricaoMdtoDAO
					.obterPrescricoesMdtoPai(prescricaoMdto.getId().getSeq(),
							seqAtendimento, dataHoraInicio, dataHoraFim, false);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoMdtos(listaAux, seqAtendimento, apaSeq,
						dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_SOL
	 * 
	 * Rotina para gerar os dados de prescrição de soluções para o Sumário de
	 * Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosPrescricaoSolucoes(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<MpmPrescricaoMdto> listaPrescricaoMdto = getMpmPrescricaoMdtoDAO().obterPrescricoesSolucaoParaSumarioMdto(seqAtendimento, dataHoraInicio, dataHoraFim); 
		
		processaListaPrescricaoSolucoes(listaPrescricaoMdto, seqAtendimento, apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoSolucoes(
			List<MpmPrescricaoMdto> listaPrescricaoMdto,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getMpmPrescricaoMdtoDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoMdto) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemSolucoes(seqAtendimento, apaSeq, prescricaoMdto.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(prescricaoMdto.getDthrInicio(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(prescricaoMdto.getDthrFim() != null && DateValidator.validaDataMenor(prescricaoMdto.getDthrFim(), dataHoraFim) && prescricaoMdto.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(prescricaoMdto.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}
			
			List<MpmPrescricaoMdto> listaAux = mpmPrescricaoMdtoDAO
					.obterPrescricoesMdtoPai(prescricaoMdto.getId().getSeq(),
							seqAtendimento, dataHoraInicio, dataHoraFim, true);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoSolucoes(listaAux, seqAtendimento,
						apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_PROC
	 * 
	 * Rotina para gerar os dados de prescrição de procedimentos para o Sumário
	 * de Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosPrescricaoProcedimentos(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<MpmPrescricaoProcedimento> listaPrescricaoProcedimento = getMpmPrescricaoProcedimentoDAO().obterPrescricoesProcedimentoParaSumarioMdto(seqAtendimento, dataHoraInicio, dataHoraFim); 
		processaListaPrescricaoProcedimentos(listaPrescricaoProcedimento, seqAtendimento, apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoProcedimentos(
			List<MpmPrescricaoProcedimento> listaPrescricaoProcedimento,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO = getMpmPrescricaoProcedimentoDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		for (MpmPrescricaoProcedimento prescricaoProcedimento : listaPrescricaoProcedimento) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemProcedimento(seqAtendimento, apaSeq, prescricaoProcedimento.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(prescricaoProcedimento.getDthrInicio(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(prescricaoProcedimento.getDthrFim() != null && DateValidator.validaDataMenor(prescricaoProcedimento.getDthrFim(), dataHoraFim) && prescricaoProcedimento.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(prescricaoProcedimento.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}
			
			List<MpmPrescricaoProcedimento> listaAux = mpmPrescricaoProcedimentoDAO
					.obterPrescricoesProcedimentoPai(prescricaoProcedimento
							.getId().getSeq(), seqAtendimento, dataHoraInicio,
							dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoProcedimentos(listaAux, seqAtendimento,
						apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_HEMO
	 * 
	 * Rotina para gerar os dados de solicitação hemoterapicas para o Sumário de
	 * Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosSolicitacoesHemoterapicas(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapicas = getBancoDeSangueFacade().obterSolicitacoesHemoterapicasParaSumarioPrescricao(seqAtendimento, dataHoraInicio, dataHoraFim); 
		processaListaPrescricaoHemoterapica(listaSolicitacoesHemoterapicas, seqAtendimento, apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoHemoterapica(
			List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapicas,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (AbsSolicitacoesHemoterapicas solicitacaoHemoterapica : listaSolicitacoesHemoterapicas) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemHemoterapia(seqAtendimento, apaSeq, solicitacaoHemoterapica.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(solicitacaoHemoterapica.getDthrSolicitacao(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(solicitacaoHemoterapica.getDthrFim() != null && DateValidator.validaDataMenor(solicitacaoHemoterapica.getDthrFim(), dataHoraFim) && solicitacaoHemoterapica.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(solicitacaoHemoterapica.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}

			List<AbsSolicitacoesHemoterapicas> listaAux = getBancoDeSangueFacade()
					.obterPrescricoesHemoterapicaPai(solicitacaoHemoterapica
							.getId().getSeq(), seqAtendimento, dataHoraInicio,
							dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoHemoterapica(listaAux, seqAtendimento,
						apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_CONS
	 * 
	 * Rotina para gerar os dados de solicitação consultoria para o Sumário de
	 * Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosSolicitacaoConsultoria(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria =  getMpmSolicitacaoConsultoriaDAO().obterSolicitacoesConsultoriaParaSumarioPrescricao(seqAtendimento, dataHoraInicio, dataHoraFim);
		processaListaPrescricaoConsultoria(listaSolicitacaoConsultoria, seqAtendimento,
				apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoConsultoria(
			List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria,
			Integer seqAtendimento, Integer apaSeq, Date dataHoraInicio,
			Date dataHoraFim, Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO = getMpmSolicitacaoConsultoriaDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		 
		for (MpmSolicitacaoConsultoria solicitacaoConsultoria : listaSolicitacaoConsultoria) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemConsultoria(seqAtendimento, apaSeq, solicitacaoConsultoria.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(solicitacaoConsultoria.getDthrSolicitada(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(solicitacaoConsultoria.getDthrFim() != null && DateValidator.validaDataMenor(solicitacaoConsultoria.getDthrFim(), dataHoraFim) && solicitacaoConsultoria.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(solicitacaoConsultoria.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}

			List<MpmSolicitacaoConsultoria> listaAux = mpmSolicitacaoConsultoriaDAO
					.obterSolicitacoesConsultoriaPai(solicitacaoConsultoria
							.getId().getSeq(), seqAtendimento, dataHoraInicio,
							dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoConsultoria(listaAux, seqAtendimento,
						apaSeq, dataHoraInicio, dataHoraFim, dataReferencia);
			}
		}	
	}

	/**
	 * ORADB Procedure MPMK_GERA_SUMARIO.MPMP_GERA_SUMR_NPT
	 * 
	 * Rotina para gerar os dados de prescrição de nutrição parental para o
	 * Sumário de Prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void geraDadosPrescricaoNutricaoParental(Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		List<MpmPrescricaoNpt> listaPrescricaoNpt = getMpmPrescricaoNptDAO()
				.obterPrescricoesNptParaSumarioPrescricao(seqAtendimento,
						dataHoraInicio, dataHoraFim);
		processaListaPrescricaoNutricaoParental(listaPrescricaoNpt,
				seqAtendimento, apaSeq, dataHoraInicio, dataHoraFim,
				dataReferencia);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaListaPrescricaoNutricaoParental(
			List<MpmPrescricaoNpt> listaPrescricaoNpt, Integer seqAtendimento,
			Integer apaSeq, Date dataHoraInicio, Date dataHoraFim,
			Date dataReferencia) throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmPrescricaoNptDAO mpmPrescricaoNptDAO = getMpmPrescricaoNptDAO();
		MpmDataItemSumarioDAO mpmDataItemSumarioDAO = getMpmDataItemSumarioDAO();
		ManterSintaxeSumarioRN manterSintaxeSumarioRN = getManterSintaxeSumarioRN();
		
		Date data = null;
		String valorP = null;
		String valorS = null;
		Boolean achouP = false;
		Boolean achouS = false;
		Integer seqp = null;
		
		for (MpmPrescricaoNpt prescricaoNpt : listaPrescricaoNpt) {
			Integer ituSeq = manterSintaxeSumarioRN.montaSintaxeSumarioItemNutricaoParental(seqAtendimento, apaSeq, prescricaoNpt.getId().getSeq());
			if(ituSeq > 0){
				data = dataReferencia;
				valorP = null;
				valorS = null;
				if(DateValidator.validaDataMenor(prescricaoNpt.getDthrInicio(), dataHoraFim)){
					valorP = DominioValorDataItemSumario.P.toString();
				}
				if(prescricaoNpt.getDthrFim() != null && DateValidator.validaDataMenor(prescricaoNpt.getDthrFim(), dataHoraFim) && prescricaoNpt.getServidorValidaMovimentacao() != null && DominioIndPendenteItemPrescricao.N.equals(prescricaoNpt.getIndPendente())){
					valorS = DominioValorDataItemSumario.S.toString();
				}
				
				achouP = false;
				achouS = false;
				seqp = 0;
				List<MpmDataItemSumario> listaDataItemSumario = mpmDataItemSumarioDAO.listarDataItemSumario(seqAtendimento, apaSeq, ituSeq);
				for (MpmDataItemSumario dataItemSumario : listaDataItemSumario) {
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
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorP);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
				if(!achouS && valorS != null){
					seqp = seqp + 1;
					
					MpmDataItemSumario dis = new MpmDataItemSumario();
					MpmDataItemSumarioId disId = new MpmDataItemSumarioId();
					disId.setApaAtdSeq(seqAtendimento);
					disId.setApaSeq(apaSeq);
					disId.setItuSeq(ituSeq);
					disId.setSeqp(seqp);
					dis.setId(disId);
					
					dis.setData(data);
					dis.setValor(valorS);
					
					MpmItemPrescricaoSumario itemPrescricaoSumarios = mpmItemPrescricaoSumarioDAO.obterPorChavePrimaria(ituSeq);
					dis.setItemPrescricaoSumarios(itemPrescricaoSumarios);
					
					mpmDataItemSumarioDAO.persistir(dis);
					mpmDataItemSumarioDAO.flush();
				}
			}
			
			List<MpmPrescricaoNpt> listaAux = mpmPrescricaoNptDAO
					.obterPrescricoesNptPai(prescricaoNpt.getId().getSeq(),
							seqAtendimento, dataHoraInicio, dataHoraFim);

			if (listaAux != null && !listaAux.isEmpty()) {
				processaListaPrescricaoNutricaoParental(listaAux,
						seqAtendimento, apaSeq, dataHoraInicio, dataHoraFim,
						dataReferencia);
			}
		}
	}

	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}

	protected MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

	protected MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}

	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	protected MpmPrescricaoNptDAO getMpmPrescricaoNptDAO() {
		return mpmPrescricaoNptDAO;
	}

	protected MpmDataItemSumarioDAO getMpmDataItemSumarioDAO() {
		return mpmDataItemSumarioDAO;
	}

	protected ManterSintaxeSumarioRN getManterSintaxeSumarioRN() {
		return manterSintaxeSumarioRN;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ManterAltaSumarioRN getManterAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}	
	
	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected MpmItemPrescricaoSumarioDAO getMpmItemPrescricaoSumarioDAO() {
		return mpmItemPrescricaoSumarioDAO;
	}

}