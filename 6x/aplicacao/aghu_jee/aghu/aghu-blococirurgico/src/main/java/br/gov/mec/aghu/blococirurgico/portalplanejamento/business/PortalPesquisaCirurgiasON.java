package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasC2VO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PortalPesquisaCirurgiasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PortalPesquisaCirurgiasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 6727355541065849900L;
	
	public enum PortalPesquisaCirurgiasONExceptionCode implements	BusinessExceptionCode {
		INTERVALO_MAXIMO_DIAS_PORTAL_PESQUISA_CIRURGIAS, MBC_01051
	}

	public List<AghCaractUnidFuncionais> listarAghCaractUnidFuncionais(String objPesquisa) {
		
		return getAghuFacade().listarCaractUnidFuncionaisEUnidadeFuncional(objPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS);
	}
	
	public Long listarAghCaractUnidFuncionaisCount(String objPesquisa) {
		return getAghuFacade().listarCaractUnidFuncionaisEUnidadeFuncionalCount(objPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS);
	}
	
	public List<PortalPesquisaCirurgiasC2VO> listarMbcProfAtuaUnidCirgsPorUnfSeq(Short unfSeq, String strPesquisa) {
		List<PortalPesquisaCirurgiasC2VO> listVO = getMbcProfAtuaUnidCirgsDAO().listarMbcProfAtuaUnidCirgsPorUnfSeq(unfSeq, strPesquisa);
		for (PortalPesquisaCirurgiasC2VO vo : listVO) {
			if(StringUtils.isEmpty(vo.getNomeUsual())){
				vo.setNomeUsual(vo.getNome());
			}
		}
        
        CoreUtil.ordenarLista(listVO, PortalPesquisaCirurgiasC2VO.Fields.NOME_USUAL.toString(), Boolean.TRUE);
        
        if(listVO.size() >100){
        	listVO = listVO.subList(0, 100);
        }
        
		return listVO;
	}
	
	public Long listarMbcProfAtuaUnidCirgsPorUnfSeqCount(Short unfSeq, String strPesquisa) {
		return getMbcProfAtuaUnidCirgsDAO().listarMbcProfAtuaUnidCirgsPorUnfSeqCount(unfSeq, strPesquisa);
	}
	
	public List<LinhaReportVO> listarCaracteristicaSalaCirgPorUnidade(String pesquisa, Short unfSeq) {
		List<LinhaReportVO> listLinhaReportVO = getMbcCaracteristicaSalaCirgDAO().listarCaracteristicaSalaCirgPorUnidade(pesquisa,unfSeq);
		return listLinhaReportVO;
	}
	
	public Long listarCaracteristicaSalaCirgPorUnidadeCount(String pesquisa, Short unfSeq) {
		return getMbcCaracteristicaSalaCirgDAO().listarCaracteristicaSalaCirgPorUnidadeCount(pesquisa,unfSeq);
	}
	
	public  List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicoPorTipo(String strPesquisa) {
		List<DominioTipoProcedimentoCirurgico> tipos = new ArrayList<DominioTipoProcedimentoCirurgico>();
		tipos.add(DominioTipoProcedimentoCirurgico.CIRURGIA);
		tipos.add(DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO);
		return getMbcProcedimentoCirurgicoDAO().listarMbcProcedimentoCirurgicoPorTipo(strPesquisa, tipos);
	}
	
	public  Long listarMbcProcedimentoCirurgicoPorTipoCount(String strPesquisa) {
		List<DominioTipoProcedimentoCirurgico> tipos = new ArrayList<DominioTipoProcedimentoCirurgico>();
		tipos.add(DominioTipoProcedimentoCirurgico.CIRURGIA);
		tipos.add(DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO);
		return getMbcProcedimentoCirurgicoDAO().listarMbcProcedimentoCirurgicoPorTipoCount(strPesquisa, tipos);
	}
	
	protected IAghuFacade getAghuFacade(){
		return this.iAghuFacade;
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}

	public void validarPesquisaPortalCirurgias(PortalPesquisaCirurgiasParametrosVO parametrosVO) throws ApplicationBusinessException  {
		
		if (parametrosVO.getPacProntuario() != null){
			if (parametrosVO.getDataInicio() != null || parametrosVO.getDataFim() != null){
				throw new ApplicationBusinessException(PortalPesquisaCirurgiasONExceptionCode.MBC_01051); //Informe o prontuário ou a data de início e fim para pesquisar.								
			}			
		}else{
			if(parametrosVO.getDataInicio() == null || parametrosVO.getDataFim() == null){
				throw new ApplicationBusinessException(PortalPesquisaCirurgiasONExceptionCode.MBC_01051); //Informe o prontuário ou a data de início e fim para pesquisar.				
			}
		}
		
		AghParametros intervaloParam = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MAX_INTERVALO_DIAS_PORTAL_PESQUISA_CIRURGIAS);
		Integer intervaloMaximo = intervaloParam.getVlrNumerico().intValue();
		
		Integer intervaloDatas = DateUtil.obterQtdDiasEntreDuasDatas(parametrosVO.getDataInicio(), parametrosVO.getDataFim());
		if(intervaloDatas > intervaloMaximo){
			String dtInicioFormatada = DateUtil.dataToString(parametrosVO.getDataInicio(), "dd/MM/yyyy");
			String dtFimFormatada = DateUtil.dataToString(parametrosVO.getDataFim(), "dd/MM/yyyy");
			throw new ApplicationBusinessException(
					PortalPesquisaCirurgiasONExceptionCode.INTERVALO_MAXIMO_DIAS_PORTAL_PESQUISA_CIRURGIAS,
					intervaloMaximo, dtInicioFormatada, dtFimFormatada, intervaloDatas);
		}		
		
	}
	
	protected IParametroFacade getParametroFacade(){
		return this.iParametroFacade;
	}

		
}