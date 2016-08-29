package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.AghWFTemplateEtapaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.EspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class AcompanharProcessoAutorizacaoOPMsON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AcompanharProcessoAutorizacaoOPMsON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private AghWFTemplateEtapaDAO aghWFTemplateEtapaDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	private static final long serialVersionUID = 4125186330661158905L;

	public List<RequerenteVO> consultarRequerentes(Object requerente) {
		
		List<Object[]> listaRequerentes = getMbcRequisicaoOpmesDAO().consultaRequerentes(requerente);
		List<RequerenteVO> listaRequerenteVO = new ArrayList<RequerenteVO>();
		
		for (Object[] item : listaRequerentes) {
			
			RequerenteVO vo = new RequerenteVO();
			vo.setCodigoPessoa(Integer.parseInt(item[0].toString()));
			vo.setMatricula(Integer.parseInt(item[1].toString()));
			vo.setVinculo(Short.parseShort(item[2].toString()));
			vo.setNome(item[3].toString());
			
			listaRequerenteVO.add(vo);
		}
		
		return listaRequerenteVO;
	}
	
	public List<AghWFTemplateEtapa> obterTemplateEtapasPorModulo() {
		String modulo = null;
		try {
			modulo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_OPME_WF_TEMPLATE).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			logError(e);
		}
		List<AghWFTemplateEtapa> templateEtapas = getAghWFTemplateEtapaDAO().obterTemplateEtapasPorModulo(modulo);
		
		AghWFTemplateEtapa todas = new AghWFTemplateEtapa();
		todas.setSequenciaBase(Short.valueOf("1"));
		todas.setCodigo("TODAS");
		todas.setDescricao("(Todas)");
		AghWFTemplateEtapa andamento = new AghWFTemplateEtapa();
		andamento.setSequenciaBase(Short.valueOf("2"));
		andamento.setCodigo("ANDAMENTO");
		andamento.setDescricao("(Em Andamento)");
		templateEtapas.add(0, todas);
		templateEtapas.add(1, andamento);
		
		return templateEtapas;
	}
	
	public List<ExecutorEtapaAtualVO> consultarExecutoresEtapaAtual(Object executor) {
		return getMbcRequisicaoOpmesDAO().consultarExecutoresEtapaAtual(executor);
	}
	
	public List<UnidadeFuncionalVO> pesquisarUnidadeFuncional(Object unidade) {
		
		List<Object[]> listaUnidades =  getMbcRequisicaoOpmesDAO().pesquisarUnidadeFuncional(unidade);
		List<UnidadeFuncionalVO> listaUnidadeVO = new ArrayList<UnidadeFuncionalVO>();
		
		for (Object[] item : listaUnidades) {
			UnidadeFuncionalVO vo = new UnidadeFuncionalVO();
			
			vo.setSeq(Short.parseShort(item[0].toString()));
			vo.setDescricao(item[1].toString());
			vo.setSigla(item[2].toString());
			
			listaUnidadeVO.add(vo);
		}
		
		return listaUnidadeVO;
	}
	
	public List<EspecialidadeVO> pesquisarEspecialidade(Object especialidade) {
		
		List<Object[]> listaEspecialidades = getMbcRequisicaoOpmesDAO().pesquisarEspecialidade(especialidade);
		List<EspecialidadeVO> listaEspecialidadeVO = new ArrayList<EspecialidadeVO>();
		
		for (Object[] item : listaEspecialidades) {
			EspecialidadeVO vo = new EspecialidadeVO();
			
			vo.setSeq(Short.parseShort(item[0].toString()));
			vo.setNomeEspecialidade(item[1].toString());
			vo.setSigla(item[2].toString());
			
			listaEspecialidadeVO.add(vo);
		}
		
		return listaEspecialidadeVO;
	}
	
	public List<EquipeVO> pesquisarEquipe(Object equipe) {
		
		List<Object[]> listaEquipes = getMbcRequisicaoOpmesDAO().pesquisarEquipe(equipe);
		List<EquipeVO> listaEquipeVO = new ArrayList<EquipeVO>();
		
		for (Object[] item : listaEquipes) {
			EquipeVO vo = new EquipeVO();
			
			vo.setMatricula(Integer.parseInt(item[0].toString()));
			vo.setVinculo(Short.parseShort(item[1].toString()));
			vo.setNomePessoa(item[2].toString());
//			vo.setEquipe(item[3].toString() != null ? item[3].toString() : item[2].toString());
			if (item[3]!= null) {
				vo.setEquipe(item[3].toString());
			} else if (item[2] != null) {
				vo.setEquipe(item[2].toString());
			}
			vo.setFuncao(item[4].toString());
			
			listaEquipeVO.add(vo);
		}
		
		return listaEquipeVO;
	}
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO() {
		return mbcRequisicaoOpmesDAO;	
	}
	
	protected AghWFTemplateEtapaDAO getAghWFTemplateEtapaDAO() {
		return aghWFTemplateEtapaDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

}