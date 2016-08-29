package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOcorrenciaIntercorrenciaGestacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.vo.IntercorrenciaAtualVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIntercorPasatus;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoes;
import br.gov.mec.aghu.paciente.dao.AipRegSanguineosDAO;
import br.gov.mec.aghu.paciente.vo.DescricaoIntercorrenciaVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SumarioAdmissaoObstetricaGestAtualRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaGestAtualRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@Inject
private AipRegSanguineosDAO aipRegSanguineosDAO;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

	private static final long serialVersionUID = 6843613068003221295L;

	/**
	 * Acesso ao modulo perinatologia
	 * @return
	 */
	private IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	/**
	 * Q_GESTACAO
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	public void executarQGestacao(SumarioAdmissaoObstetricaInternacaoVO vo) {

		Integer pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		Short seqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP);
		Date pDthrMovimento = (Date) vo.getParametrosHQL().get(ParametrosReportEnum.P_DTHR_MOVIMENTO);

		// obtem gestacao
		McoGestacoesId gestacaoId = new McoGestacoesId();
		gestacaoId.setPacCodigo(pacCodigo);
		gestacaoId.setSeqp(seqp);
		McoGestacoes gestacao = getPerinatologiaFacade().obterMcoGestacoes(gestacaoId);
		
		if(gestacao != null) {

			AipRegSanguineos regSanguineo = this.aipRegSanguineosDAO.obterRegSanguineosPorCodigoPaciente(
					gestacao.getId().getPacCodigo(), gestacao.getId().getSeqp().byteValue());
			vo.setGesta(gestacao.getGesta());//9
			vo.setPara(gestacao.getPara());//10
			vo.setCesarea(gestacao.getCesarea());//11
			vo.setAborto(gestacao.getAborto());	//12
			vo.setEctopica(gestacao.getEctopica());//13
			vo.setGemelar(gestacao.getGemelar());//14
			vo.setDum(gestacao.getDtUltMenstruacao());//15
			vo.setDtProvavelParto(gestacao.getDtProvavelParto());//16
			vo.setDtPrimeiraEco(gestacao.getDtPrimEco());//17
			
			if(gestacao.getIgPrimEco() != null) {				
				vo.setIdadeGestPrimeiraEco(gestacao.getIgPrimEco().toString() + " semanas /"); //18
			}			
			if(gestacao.getIgPrimEcoDias() != null) {
				vo.setIdadeGestPrimeiraEcoDias(gestacao.getIgPrimEcoDias().toString() + " dias"); // 19
			}
			
			vo.setDtInformadaIG(gestacao.getDtInformadaIg() );  //20
			preencherIdadeGestacionalInformada(vo, pDthrMovimento, gestacao); //21

			vo.setNroConsultasPreNatal(gestacao.getNumConsPrn()); //23
			vo.setDtPrimeiraConsulta(gestacao.getDtPrimConsPrn());//24
			
			if(regSanguineo != null) {
				vo.setTipoSangueMae(regSanguineo.getGrupoSanguineo() + regSanguineo.getFatorRh()); // 25
				if (regSanguineo.getCoombs()!=null && regSanguineo.getFatorRh() != null && regSanguineo.getFatorRh().equals("-")){
					vo.setCoombs(regSanguineo.getCoombs().getDescricao()); //26
				}				
			}
			if(DominioSimNao.S.equals(gestacao.getVatCompleta())) { //27
				vo.setVatCompleta(getResourceBundleValue("MSG_VACINA_ANTI_TETANICA_COMPLETA"));
			}			
			if(StringUtils.isNotBlank(gestacao.getUsoMedicamentos())) {
				StringBuilder sb = new StringBuilder();
				sb.append(gestacao.getUsoMedicamentos());
				vo.setUsoMedicamentos(sb.toString()); //32
			}
						
			vo.setObservacaoExame(gestacao.getObsExames()); //74
			vo.setGravidez(gestacao.getGravidez().getDescricao()); //80	
			
			// seta os parametros que serao utilizados em outras consultas  
			vo.getParametrosHQL().put(ParametrosReportEnum.QGESTACAO_GSO_PAC_CODIGO, gestacao.getId().getPacCodigo());
			vo.getParametrosHQL().put(ParametrosReportEnum.QGESTACAO_GSO_SEQP, gestacao.getId().getSeqp());
			vo.getParametrosHQL().put(ParametrosReportEnum.QGESTACAO_GESTA, gestacao.getGesta());
			vo.getParametrosHQL().put(ParametrosReportEnum.QGESTACAO_CRIADO_EM, gestacao.getCriadoEm());
		}
	}

	private void preencherIdadeGestacionalInformada(
			SumarioAdmissaoObstetricaInternacaoVO vo, Date pDthrMovimento,
			McoGestacoes gestacao) {
		if ((gestacao.getDtInformadaIg() != null && pDthrMovimento != null) && gestacao.getDtInformadaIg().after(pDthrMovimento)){
			vo.setIdadeGestacionalInformada(null);
		}else{
			StringBuilder sb = new StringBuilder();
							
			if (gestacao.getIgAtualSemanas() == null){
				sb.append('0');
			}else{
				sb.append(gestacao.getIgAtualSemanas());
			}
			
			sb.append(" semanas / ");
			
			if(gestacao.getIgAtualDias() == null){
				sb.append('0');
			}else{
				sb.append(gestacao.getIgAtualDias());
			}
			
			sb.append(" dias");
			
			vo.setIdadeGestacionalInformada(sb.toString());
		}
	}
	
	/**
	 * CF_JUSTIFICATIVA_FORMULA
	 * @param vo
	 */
	public String executarCFJustificativaFormula(SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		Integer pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_GSO_PAC_CODIGO);
		Byte gesta = (Byte) vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_GESTA);
		Date criadoEm = (Date) vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_CRIADO_EM);
	
		 Date data = getPerinatologiaFacade().obterMaxCriadoEmMcoGestacoes(pacCodigo, gesta, criadoEm);
		
		if(data != null){
			StringBuilder jus = new StringBuilder();
			jus.append("Observação: Existe a informação gesta ")
			.append(gesta).append(" anterior para esta paciente em ")
			.append(DateUtil.obterDataFormatada(data, "dd/MM/yyyy"));
			vo.setJustificativa(jus.toString());
			return jus.toString();
		}
		return "";
	}
	
	
	
	/**
	 * Q_OPA1
	 * @param vo
	 *//*
	 * Comentado para ser realizado dentro do metodo que migra q_passadas
	 *
	public void executarQOpa1(SumarioAdmissaoObstetricaInternacaoVO vo) {
		BigDecimal ingOpaSeq = null;
		if(vo.getParametrosHQL().get(ParametrosReportEnum.QPASSADAS_ING_OPA_SEQ) != null) {		
			ingOpaSeq = (BigDecimal) vo.getParametrosHQL().get(ParametrosReportEnum.QPASSADAS_ING_OPA_SEQ);
		}
		if(ingOpaSeq != null) {
			McoIntercorPasatus obj = getPerinatologiaFacade().obterMcoIntercorPasatusPorChavePrimaria(ingOpaSeq.intValue());
			vo.setDescricaoInterPassada(obj.getDescricao());
		}
	}*/
	
	/**
	 * Q_ING
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	public void executarQIng(SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		Short gsoSeqp = null;
		Integer pacCodigo = null;
		
		if(vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP) != null) {
			gsoSeqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP);
		}
		if(vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO) != null) {
			pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		}
		
		if (gsoSeqp != null && pacCodigo != null) {
			List<McoIntercorrenciaGestacoes> lista = getPerinatologiaFacade()
					.listarIntercorrenciasGestacoesPorCodGestCodPaciente(
							gsoSeqp, pacCodigo,
							DominioOcorrenciaIntercorrenciaGestacao.A);

			IntercorrenciaAtualVO intercorrenciaAtualVO = null;
			for (McoIntercorrenciaGestacoes intercorrenciaGestacao : lista) {

				McoIntercorrenciaGestacoes interGestacao = intercorrenciaGestacao;
				intercorrenciaAtualVO = new IntercorrenciaAtualVO();
				if(interGestacao
						.getComplemento()!=null){
					intercorrenciaAtualVO.setComplementoInterAtual(interGestacao
							.getComplemento());
				} else {
					intercorrenciaAtualVO.setComplementoInterAtual("");
				}
				
				if (interGestacao.getId().getOpaSeq() != null) {
					BigDecimal ingOpaSeq = interGestacao.getId().getOpaSeq();
					McoIntercorPasatus obj = getPerinatologiaFacade()
							.obterMcoIntercorPasatusPorChavePrimaria(
									ingOpaSeq.intValue());
					intercorrenciaAtualVO.setDescricaoInterAtual(obj
							.getDescricao());
				}

				vo.getIntercorrenciasAtuais().add(intercorrenciaAtualVO);

			}

		}
	}

	/**
	 * Q_PASSADAS
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	public void  executarQPassadas(SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		Short gsoSeqp = null;
		Integer pacCodigo = null;
		
		if(vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP) != null) {
			gsoSeqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP);
		}
		if(vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO) != null) {
			pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		}
		
		if(gsoSeqp != null && pacCodigo != null) {
			List<McoIntercorrenciaGestacoes> lista = getPerinatologiaFacade().listarIntercorrenciasGestacoesPorCodGestCodPaciente(gsoSeqp,pacCodigo,DominioOcorrenciaIntercorrenciaGestacao.P);
			
			List<DescricaoIntercorrenciaVO> listaIntercorrenciaPassada = new ArrayList<DescricaoIntercorrenciaVO>();
			
			for (McoIntercorrenciaGestacoes itemIntercorrenciaPassada : lista){
				
				DescricaoIntercorrenciaVO voIntercorrencia = new DescricaoIntercorrenciaVO();
			
				if(itemIntercorrenciaPassada.getComplemento() != null) {
					voIntercorrencia.setComplemento(itemIntercorrenciaPassada.getComplemento());
				}
				
				if (itemIntercorrenciaPassada.getId()!=null && itemIntercorrenciaPassada.getId().getOpaSeq()!=null){
					//Regra adaptada de Q_OPA1
					McoIntercorPasatus obj = getPerinatologiaFacade().obterMcoIntercorPasatusPorChavePrimaria(itemIntercorrenciaPassada.getId().getOpaSeq().intValue());
					voIntercorrencia.setDescricao(obj.getDescricao());
				}
				listaIntercorrenciaPassada.add(voIntercorrencia);
			}
			vo.setIntercorrenciasPassadas(listaIntercorrenciaPassada);
		}
	}

}
