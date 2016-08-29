package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.vo.SolicitacoesAgendaColetaAmbulatorioVO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;

/**
 * Responsavel pelas regras de negocio do relatório de agenda de coleta do ambulatório.
 * 
 * @author fwinck
 *
 */
@Stateless
public class ImprimirAgendaColetarAmbulatorioON extends BaseBusiness {


@EJB
private EtiquetasON etiquetasON;

private static final Log LOG = LogFactory.getLog(ImprimirAgendaColetarAmbulatorioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -310033162942555805L;

	public List<SolicitacoesAgendaColetaAmbulatorioVO> pesquisarAgendaColetaAmbulatorio(final RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws BaseException{

		List<SolicitacoesAgendaColetaAmbulatorioVO> listVo = getAelSolicitacaoExameDAO().pesquisarAgendaColetaAmbulatorio(filtro);

		if(filtro.getIndImpressaoEtiquetas().equals(DominioSimNao.S)){
			chamarEtiquetaBarras(listVo, filtro);
		}

		if(listVo!=null){
			listVo = agruparMontarLista(listVo);
		}
		
		return listVo;
	}

	private void chamarEtiquetaBarras(List<SolicitacoesAgendaColetaAmbulatorioVO> listVo, RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws BaseException {
		if(listVo!=null && listVo.size()>0){
			
			Integer solicitacaoAnterior =  0;

			AelSolicitacaoExameDAO dao = getAelSolicitacaoExameDAO();

			for (SolicitacoesAgendaColetaAmbulatorioVO solicitacaoColetarVO : listVo) {
				if(!solicitacaoColetarVO.getSoeSeq().equals(solicitacaoAnterior)){

					solicitacaoAnterior = solicitacaoColetarVO.getSoeSeq();

					AelSolicitacaoExames solic = dao.obterPeloId(solicitacaoAnterior);
					if(solic!=null){
						
							if(filtro.getImpressora()!=null){
								getEtiquetasON().gerarEtiquetas(solic, getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(filtro.getUnidadeColeta().getSeq()), filtro.getImpressora().getFilaImpressora(), null,false);
							}else{
								getEtiquetasON().gerarEtiquetas(solic, getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(filtro.getUnidadeColeta().getSeq()), null, null,false);
							}
						}
					}
				}
			}
		}


	private List<SolicitacoesAgendaColetaAmbulatorioVO> agruparMontarLista(List<SolicitacoesAgendaColetaAmbulatorioVO> listVo) {
		SolicitacoesAgendaColetaAmbulatorioVO voPai = null;
		List<SolicitacoesAgendaColetaAmbulatorioVO> listVoAux = new ArrayList<SolicitacoesAgendaColetaAmbulatorioVO>();

		for(SolicitacoesAgendaColetaAmbulatorioVO vo : listVo){
			if(voPai == null || (!vo.getSoeSeq().equals(voPai.getSoeSeq()) || !vo.getAmoSeqp().equals(voPai.getAmoSeqp()))){
				vo.setSubListExames(new ArrayList<SolicitacoesAgendaColetaAmbulatorioVO>());
				voPai = vo;
				voPai.getSubListExames().add(vo);
				listVoAux.add(voPai);
			}else{
				voPai.getSubListExames().add(vo);
			}
		}
		return listVoAux;
	}

	//------------------------------
	//GETTERS / SETTERS
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO(){
		return aelSolicitacaoExameDAO;
	}
	
	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}