package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * lista de cirurgias canceladas
 * 
 * @author Cristiane.Barbado
 * 
 */
@Stateless
public class ListarCirurgiasCanceladasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ListarCirurgiasCanceladasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private ListaCirurgiasCanceladasRN listaCirurgiasCanceladasRN;

	@EJB
	private IInternacaoFacade iInternacaoFacade;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5422856624813137110L;

	/**
	 * Método que retorna lista PlanejamentoCirurgiasVO para a geração do
	 * relatório.
	 * 
	 */
	public List<CirurgiasCanceladasVO> listarCirurgiasCanceladas(
			String orderProperty, boolean asc,
			PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) throws ApplicationBusinessException {
		try{
			portalPesquisaCirurgiasParametrosVO.setDesmarcar(parametroFacade.buscarValorShort(AghuParametrosEnum.P_MOT_DESMARCAR));
			portalPesquisaCirurgiasParametrosVO.setDesmarcarAdm(parametroFacade.buscarValorShort(AghuParametrosEnum.P_MOT_DESMARCAR_ADM));
		}catch(ApplicationBusinessException e){
			logError("Ocorreu um erro ao buscar o parâmetro P_AGHU_CODIGOS_TIPO_ALTA_MEDICA na tabela AGH_PARAMETROS.");
		}
		
		List<CirurgiasCanceladasVO> listaCirurgiasCanceladasCompleta = new ArrayList<CirurgiasCanceladasVO>();

		List<CirurgiasCanceladasVO> listaCirurgiasCanceladas = 
			getMbcCirurgiasDAO().pesquisarCirurgiasCanceladas(
					portalPesquisaCirurgiasParametrosVO);

		Integer pciSeqPortal = portalPesquisaCirurgiasParametrosVO.getPciSeqPortal();
		
		for(CirurgiasCanceladasVO vo : listaCirurgiasCanceladas){

			// FUNCTION MBCC_PROC_PRIN_SEQ - EPR_PCI_SEQ - condicao do where 
			
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = getListaCirurgiasCanceladasRN().
				pesquisarMbcProcEspPorCirurgiasByCirurgia(vo.getCirSeq());
			
			Integer eprPciSeq = mbcProcedimentoCirurgicos != null ? mbcProcedimentoCirurgicos.getSeq() : null;
			
			
			if  (pciSeqPortal == null || pciSeqPortal.equals(eprPciSeq)) {
				
				//C4
				AghCaractUnidFuncionais caract = getAghuFacade()
				.obterAghCaractUnidFuncionaisPorChavePrimaria(
						new AghCaractUnidFuncionaisId(
								vo.getUnfSeq(),
								ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));

				AghUnidadesFuncionais aghUnidadeFuncional = caract
					.getUnidadeFuncional();
				vo.setUnfSigla(aghUnidadeFuncional.getSigla());
		
		
				// C5
				vo.setDescConv(this.getInternacaoFacade().obterDescricaoConvenioPlano
						(vo.getCspSeq(), vo.getCspCnvCodigo()));
		
				// Regime
				if (vo.getOrigemIntLocal() != null){
					vo.setRegime(vo.getOrigemIntLocal());
				}else {
					
					vo.setRegime(vo.getOrigemPacienteCirurgia().getDescricao());
				}
				
				// FUNCTION MBCC_PROC_PRIN_DESCR - PCI_DESCRICAO - DESC_PROCED
	
				vo.setPciDescricao(mbcProcedimentoCirurgicos != null ? mbcProcedimentoCirurgicos.getDescricao() : null);
				
				// FUNCTION MBCC_BUSCA_RESP_USUL - EQUIPE
				
				vo.setEquipe(this.getListaCirurgiasCanceladasRN().
						pesquisarEquipeporCirurgia(vo.getCirSeq()));
				
				listaCirurgiasCanceladasCompleta.add(vo);
			}
		}
		
		
		CoreUtil.ordenarLista(listaCirurgiasCanceladasCompleta,orderProperty,asc);
		
		return listaCirurgiasCanceladasCompleta;
	}


	protected IInternacaoFacade getInternacaoFacade() {
		return this.iInternacaoFacade;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.iBlocoCirurgicoFacade;
	}
	
	

	protected ListaCirurgiasCanceladasRN getListaCirurgiasCanceladasRN() {
		return listaCirurgiasCanceladasRN;
	}

}
