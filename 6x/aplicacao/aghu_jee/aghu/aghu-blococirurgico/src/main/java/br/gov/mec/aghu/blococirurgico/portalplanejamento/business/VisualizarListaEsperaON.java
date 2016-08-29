package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class VisualizarListaEsperaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VisualizarListaEsperaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;


	@EJB
	private IPesquisaInternacaoFacade iPesquisaInternacaoFacade;

	private static final long serialVersionUID = 5422856624813137110L;

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return this.iPesquisaInternacaoFacade;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
		return mbcEspecialidadeProcCirgsDAO;
	}
	
	public List<ListaEsperaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PortalPesquisaCirurgiasParametrosVO parametros) throws ApplicationBusinessException {
		List<MbcAgendas> listaAgendas = new ArrayList<MbcAgendas>();
		List<ListaEsperaVO> listaEspera = new ArrayList<ListaEsperaVO>();
		if (orderProperty == null || ListaEsperaVO.getFieldByDesc(orderProperty) == null) {
			listaAgendas = getMbcAgendasDAO().listarAgendasListaEspera(firstResult,maxResult,orderProperty,asc,parametros);
		} else {
			listaAgendas = getMbcAgendasDAO().listarAgendasListaEspera(parametros);
		}
		for (MbcAgendas agenda : listaAgendas) {
			ListaEsperaVO vo = new ListaEsperaVO();
			vo.setRegime(agenda.getRegime());
			vo.setDthrInclusao(agenda.getDthrInclusao());
			vo.setSeq(agenda.getSeq());
			// ... o BeanUtils.copyProperties(agenda,vo) não funcionou então fiz a cópia direta mesmo
			vo.setPesNomeUsual(getPesquisaInternacaoFacade().buscarNomeUsual(agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo(),agenda.getProfAtuaUnidCirgs().getId().getSerMatricula()));
			vo.setVpeDescricao(getMbcEspecialidadeProcCirgsDAO().getVpeDescricao(agenda.getEspProcCirgs()));

			vo.setEspecialidadeNome(agenda.getEspecialidade().getNomeEspecialidade());
			vo.setEspecialidadeSigla(agenda.getEspecialidade().getSigla());
			vo.setPacienteNome(agenda.getPaciente().getNome());
			vo.setPacienteProntuario(agenda.getPaciente().getProntuario());
			
			listaEspera.add(vo);
		}
		if (orderProperty != null && ListaEsperaVO.getFieldByDesc(orderProperty) != null) {
			CoreUtil.ordenarLista(listaEspera, orderProperty, asc);
			Integer lastResult = (firstResult+maxResult)>listaEspera.size()?listaEspera.size():(firstResult+maxResult);
			listaEspera = listaEspera.subList(firstResult, lastResult);		
		}
		return listaEspera;
	}
	
	public Long recuperarCount(PortalPesquisaCirurgiasParametrosVO parametros) {
		return getMbcAgendasDAO().listarAgendasListaEsperaCount(parametros);
	}

}
