package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.AghuEnumUtils;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por prover os métodos de negócio genéricos para Bloqueio de sala. 
 * 
 * @autor cristiane.barbado
 * 
 */
@Stateless
public class BloqueioSalaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(BloqueioSalaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade iBlocoCirurgicoCedenciaSalaFacade;

	private static final long serialVersionUID = -9001235819703398140L;

	protected enum BloqueioSalaRNExceptionCode implements BusinessExceptionCode {
		MBC_01219, MBC_01220, MBC_01224, MBC_01225, MBC_01295, MBC_01221 ;
	}


	/**
	 * ORADB TRIGGER "AGH".MBCT_BSC_BRI
	 * @param bloqSalaCirurgica, servidorLogado
	 * @throws ApplicationBusinessException 
	 * RN1
	 */
	public void executarAntesInserir(MbcBloqSalaCirurgica bloqSalaCirurgica) throws ApplicationBusinessException{
		
		bloqSalaCirurgica.setCriadoEm(new Date());
		bloqSalaCirurgica.setRapServidoresByMbcBscSerFk1(servidorLogadoFacade.obterServidorLogado());
		
		rnBscpVerDatas(bloqSalaCirurgica.getDtInicio(), bloqSalaCirurgica.getDtFim()); //RN2
		
		rnBscpVerDiaSem(bloqSalaCirurgica.getDtInicio(), bloqSalaCirurgica.getDtFim(), bloqSalaCirurgica.getDiaSemana()); //RN3

		rnBscpVerEquipe(bloqSalaCirurgica); //RN4
	}
	
	/**
	 * ORADB mbck_bsc_rn.rn_bscp_ver_datas
	 * @param DtInicio, DtFim
	 * @throws ApplicationBusinessException 
	 * RN2
	 */
	public void rnBscpVerDatas(Date dtInicio, Date dtFim) throws ApplicationBusinessException{
		
		String mmyyInicial =DateUtil.obterDataFormatada(dtInicio, "MMyyyy");
		
		String mmyyFim = DateUtil.obterDataFormatada(dtFim, "MMyyyy");		
		
		if(!mmyyInicial.equals(mmyyFim)) {
			throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01219);
		}
		AghParametros paramDiasBloqSalaRetroativo = null;
		paramDiasBloqSalaRetroativo = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_BLOQ_SALA_RETROATIVO);
		Integer diasBloqSalaRetroativo = paramDiasBloqSalaRetroativo.getVlrNumerico().intValue();		
		
		Date dataAtual = DateUtil.truncaData(new Date());
		Date dataRetroativa = DateUtil.adicionaDias(dataAtual, - (diasBloqSalaRetroativo));
		if(dtInicio.before(dataRetroativa)){
			throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01220, DateUtil.obterDataFormatada(dataRetroativa, "dd/MM/yyyy"));					
		}
	}
	
	/**
	 * ORADB mbck_bsc_rn.rn_bscp_ver_dia_sem
	 * @param DtInicio, DtFim, diaSemana
	 * @throws ApplicationBusinessException 
	 * RN3
	 */
	public void rnBscpVerDiaSem(Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana) throws ApplicationBusinessException {
		
		if(diaSemana != null){
			DominioDiaSemana diaSemDtInicial = AghuEnumUtils.retornaDiaSemanaAghu(dtInicio);
			DominioDiaSemana diaSemDtFim = AghuEnumUtils.retornaDiaSemanaAghu(dtFim);
			
			DominioDiaSemanaSigla diaSemanaDtIni = DominioDiaSemanaSigla.valueOf(diaSemDtInicial.name());
			DominioDiaSemanaSigla diaSemanaDtFim = DominioDiaSemanaSigla.valueOf(diaSemDtFim.name());			
			
			if(!diaSemanaDtIni.equals(diaSemana)){
				throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01224, diaSemana.toString());
			}
			if(!diaSemanaDtFim.equals(diaSemana)){
				throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01225, diaSemana.toString());
			}
		}		
	}
	
	/**
	 * ORADB mbck_bsc_rn.rn_bscp_ver_equipe
	 * @param bloqSalaCirurgica
	 * @throws ApplicationBusinessException 
	 * RN4
	 */
	public void rnBscpVerEquipe(MbcBloqSalaCirurgica bloqSalaCirurgica) throws ApplicationBusinessException {
				
		Date vDtInicio = bloqSalaCirurgica.getDtInicio();
		String vEncontraEquipe = "N";
		String vConsiste = "N"; 
		
		if (bloqSalaCirurgica.getMbcProfAtuaUnidCirgs() != null){
				
			do {
				
				DominioDiaSemana diaSemVDtInicio = AghuEnumUtils.retornaDiaSemanaAghu(vDtInicio);
					
				if (bloqSalaCirurgica.getDiaSemana() != null){
					if(diaSemVDtInicio.equals(bloqSalaCirurgica.getDiaSemana())){
						vConsiste = "S";
					}
				}else{
					vConsiste = "S";
				}
						
				if (vConsiste.equals("S")){
					List<MbcCaractSalaEsp> listaCaractEsp = getMbcCaractSalaEspDAO().pesquisarCaractSalaEsp(bloqSalaCirurgica);
					if (listaCaractEsp != null) {
						vEncontraEquipe = "S";
					} 
				}
	
				DateUtil.adicionaDias(vDtInicio, 1);
				
			} while (CoreUtil.isMenorDatas(bloqSalaCirurgica.getDtInicio(), bloqSalaCirurgica.getDtFim()));
				
			if (vEncontraEquipe.equals("N")){
				throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01295);	
				
			}
		}
	}
	
	/**
	 * ORADB TRIGGER "AGH".MBCT_BSC_BRU
	 * @param bloqueioSalaCirurgica
	 * @throws ApplicationBusinessException 
	 * RN5
	 */
	public void setarMbcBloqSalaCirurgicaServidorLogadoECriadoEm(MbcBloqSalaCirurgica bloqueioSalaCirurgica) throws ApplicationBusinessException {
		MbcBloqSalaCirurgica original = getBlocoCirurgicoCedenciaSalaFacade().obterOriginalMbcBloqSalaCirurgica(bloqueioSalaCirurgica);		

		bloqueioSalaCirurgica.setAlteradoEm(new Date());
		
		bloqueioSalaCirurgica.setRapServidoresByMbcBscSerFk2(servidorLogadoFacade.obterServidorLogado());	
		
		if(CoreUtil.modificados(bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getUnfSeq(), original.getMbcSalaCirurgica().getId().getUnfSeq()) ||
				CoreUtil.modificados(bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(), original.getMbcSalaCirurgica().getId().getSeqp()) ||
				CoreUtil.modificados(bloqueioSalaCirurgica.getDtInicio(), original.getDtInicio()) ||
				CoreUtil.modificados(bloqueioSalaCirurgica.getDtFim(), original.getDtFim()) ||
				CoreUtil.modificados(bloqueioSalaCirurgica.getDiaSemana(), original.getDiaSemana()) ||
				CoreUtil.modificados(bloqueioSalaCirurgica.getTurno(), original.getTurno()) ||
				CoreUtil.modificados(bloqueioSalaCirurgica.getMotivo(), original.getMotivo())) {

			throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01221);
		}		
		
		if (bloqueioSalaCirurgica.getMbcProfAtuaUnidCirgs() != null && original.getMbcProfAtuaUnidCirgs() != null){

			if(CoreUtil.modificados(bloqueioSalaCirurgica.getMbcProfAtuaUnidCirgs().getId().getSerMatricula(), original.getMbcProfAtuaUnidCirgs().getId().getSerMatricula()) ||
					CoreUtil.modificados(bloqueioSalaCirurgica.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo(), original.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo()) ||
					CoreUtil.modificados(bloqueioSalaCirurgica.getMbcProfAtuaUnidCirgs().getUnidadeFuncional().getSeq(), original.getMbcProfAtuaUnidCirgs().getUnidadeFuncional().getSeq()) ||
					CoreUtil.modificados(bloqueioSalaCirurgica.getMbcProfAtuaUnidCirgs().getIndFuncaoProf(), original.getMbcProfAtuaUnidCirgs().getIndFuncaoProf())) {

				throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01221);
			}			
		}else{
			if(CoreUtil.modificados(bloqueioSalaCirurgica.getMbcProfAtuaUnidCirgs(), original.getMbcProfAtuaUnidCirgs())){
				throw new ApplicationBusinessException(BloqueioSalaRNExceptionCode.MBC_01221);
			}
		}
	}
	
	private MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IBlocoCirurgicoCedenciaSalaFacade getBlocoCirurgicoCedenciaSalaFacade() {
		return iBlocoCirurgicoCedenciaSalaFacade;
	}	
		
}