package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaExamesRealizadosVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.view.VMcoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SumarioAdmissaoObstetricaExamesRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaExamesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPerinatologiaFacade perinatologiaFacade;

	private static final long serialVersionUID = -1957634088652382130L;

	/**
	 * Acesso ao modulo perinatologia
	 * @return
	 */
	private IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	/**
	 * Q_RXS
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	public void executarQRxs(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		Short seqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP);

		List<McoResultadoExameSignifs> itens = new ArrayList<McoResultadoExameSignifs>();
		itens = getPerinatologiaFacade().listarResultadosExamesSignifsPorCodigoPacienteSeqpGestacao(pacCodigo, seqp);
		for(McoResultadoExameSignifs tmp : itens){
			SumarioAdmissaoObstetricaExamesRealizadosVO exame = new SumarioAdmissaoObstetricaExamesRealizadosVO();
			//RXS_DATA_REALIZACAO de QRXS
			exame.setDtExame(tmp.getDataRealizacao());
			//RXS_RESULTADO de QRXS
			exame.setResultadoExame(tmp.getResultado());
			
			if(vo.getExamesRealizados() == null){
				vo.setExamesRealizados(new ArrayList<SumarioAdmissaoObstetricaExamesRealizadosVO>());
			}
			// rxs_chave
			StringBuilder rxsChave = new StringBuilder();
			if(tmp.getEmaExaSigla() != null){
				rxsChave.append(tmp.getEmaExaSigla());
			}else{
				rxsChave.append('0');
			}
			if(tmp.getEmaManSeq() != null){
				rxsChave.append(tmp.getEmaManSeq()); 
			}else{
				rxsChave.append('0');
			}			
			if(tmp.getExameExterno() != null){
				rxsChave.append(tmp.getExameExterno().getSeq());
			}else{
				rxsChave.append('0');
			}
			exame.setRxsChave(rxsChave.toString());
			vo.getExamesRealizados().add(exame);
		}		
	}
	
	/**
	 * Q_VEX
	 * @param vo
	 */
	public void executarQVex(SumarioAdmissaoObstetricaInternacaoVO vo) {
		if(vo != null) {
			List<SumarioAdmissaoObstetricaExamesRealizadosVO> examesRealizados = vo.getExamesRealizados();
			if(examesRealizados != null && examesRealizados.size() > 0) {
				for(SumarioAdmissaoObstetricaExamesRealizadosVO exameVO : examesRealizados) {
					VMcoExames exame = null;
					if(StringUtils.isNotBlank(exameVO.getRxsChave())) {
						exame = getPerinatologiaFacade().obterVMcoExamesPorChave(exameVO.getRxsChave());
					}
					if(exame != null) {
						exameVO.setNomeExame(exame.getDescricao());
					}
				}
			}
		}
	}
	
}
