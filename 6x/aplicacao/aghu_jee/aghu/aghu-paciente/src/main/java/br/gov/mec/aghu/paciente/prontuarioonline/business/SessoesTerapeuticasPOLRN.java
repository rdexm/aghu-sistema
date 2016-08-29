package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("ucd")
@Stateless
public class SessoesTerapeuticasPOLRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SessoesTerapeuticasPOLRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IFarmaciaFacade farmaciaFacade;

	public enum SessoesTerapeuticasPOLRNExceptionCode implements BusinessExceptionCode {
		MPT_00528
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8071848129063862077L;

	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	/**
	 * ORADB MPMC_ORD_SUM_PRCR
	 * 
	 * @param ituSeq
	 * @return {@link Integer}
	 *  
	 */
	public Short obterOrdemSumario(Integer ituSeq){
		List<Short> list = getFarmaciaFacade().listarOrdemSumarioPrecricao(ituSeq);
		
		if(!list.isEmpty() && list.size() > 0){
			return list.get(0);
		}else{
			return 999;
		}
	}
	
	/**
	 * ORADB MPTC_LOCAL_PAC_ATD
	 * 
	 * @param atdSeq
	 * @return {@link Integer}
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public String obterUnidadeAtendimentoPaciente(Integer atdSeq) throws ApplicationBusinessException{
		AghAtendimentos atendimentoUnidade = getAghuFacade().obterAtendimentoComUnidadeFuncional(atdSeq);
		
		if(atendimentoUnidade == null){
			throw new ApplicationBusinessException(SessoesTerapeuticasPOLRNExceptionCode.MPT_00528);
		}
		String unfDescricao = atendimentoUnidade.getUnidadeFuncional().getAndarAlaDescricao();
		
		AghAtendimentos atendimentoPaciente = getAghuFacade().obterAtendimentoPorPacienteEOrigem(atendimentoUnidade.getPaciente().getCodigo());
		
		if(atendimentoPaciente != null){
			return getPrescricaoMedicaFacade().buscarResumoLocalPaciente(atendimentoPaciente);
		}else{
			if(unfDescricao != null){
				if (DominioSimNao.N == getAghuFacade()
						.verificarCaracteristicaDaUnidadeFuncional(	atendimentoUnidade.getUnidadeFuncional()
								.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO)
						&& DominioSimNao.N == getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(
								atendimentoUnidade.getUnidadeFuncional().getSeq(),
								ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)) {
					return "U:".concat(unfDescricao);
				}else{
					return "AMB";
				}
			}else{
				return "Unidade não identificada";
			}
		}
	}
}
