package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.configuracao.vo.EspecialidadeFiltro;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncionalFiltro;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * Regras de negócio relacionadas ao Usuário.
 * 
 * @author geraldo
 * 
 */
@Stateless
public class UnidadeFuncionalON extends BaseBusiness {

	private static final String SERVICE = "SERVICE: ";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3423984755101821178L;

	private static final Log LOG = LogFactory.getLog(UnidadeFuncionalON.class);
	
	@EJB
	private IInternacaoService internacaoService;
	
	@EJB
	private IConfiguracaoService configuracaoService;
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<UnidadeFuncional> pesquisarUnidadeFuncional(Object objPesquisa) {
		
		String auxParam = (String) objPesquisa;
		UnidadeFuncionalFiltro unidadeFuncionalFiltro = new UnidadeFuncionalFiltro();
		
		
		if (CoreUtil.isNumeroShort(auxParam)){
			unidadeFuncionalFiltro.setSeq(Short.valueOf(auxParam));
		}
		else {
			unidadeFuncionalFiltro.setDescricao(auxParam);
		}
		
		List<UnidadeFuncional>  listaRetorno = new ArrayList<UnidadeFuncional>();
		
		try {
		
			listaRetorno = this.internacaoService.pesquisarUnidadeFuncional(unidadeFuncionalFiltro,100);
			
		} catch (Exception e) {
			
			LOG.error(SERVICE + e.getMessage(), e);
			
		}
		
		return listaRetorno;
	}
	
	public Long pesquisarUnidadeFuncionalCount(Object objPesquisa) {
		String auxParam = (String) objPesquisa;
		UnidadeFuncionalFiltro unidadeFuncionalFiltro = new UnidadeFuncionalFiltro();
		Long count = null;
		
		
		if (CoreUtil.isNumeroShort(auxParam)){
			unidadeFuncionalFiltro.setSeq(Short.valueOf(auxParam));
		}
		else {
			unidadeFuncionalFiltro.setDescricao(auxParam);
		}
		
		try {
			
			count = this.internacaoService.pesquisarUnidadeFuncionalCount(unidadeFuncionalFiltro);
			
		} catch (Exception e) {
			
			LOG.error(SERVICE + e.getMessage(), e);
			
		}
		
		return count;
		
	}
	
    public List<Especialidade> pesquisarEspecialidade(Object objPesquisa) {
		
		String auxParam = (String) objPesquisa;
		EspecialidadeFiltro especialidadeFiltro = new EspecialidadeFiltro();
		
		
		if (CoreUtil.isNumeroShort(auxParam)){
			especialidadeFiltro.setSeq(Short.valueOf(auxParam));
		}
		else {
			especialidadeFiltro.setNomeEspecialidade(auxParam);
		}
		
		List<Especialidade>  listaRetorno = new ArrayList<Especialidade>();
		
		try {
		
			listaRetorno = this.configuracaoService.pesquisarEspecialidade(especialidadeFiltro);
			
		} catch (Exception e) {
			
			LOG.error(SERVICE + e.getMessage(), e);
			
		}
		
		return listaRetorno;
	}
    
    
   public Especialidade obterEspecialidade(Short seq) {
		
		EspecialidadeFiltro especialidadeFiltro = new EspecialidadeFiltro();
		
		especialidadeFiltro.setSeq(seq);
		
		List<Especialidade>  listaRetorno = new ArrayList<Especialidade>();
		
		try {
		
			listaRetorno = this.configuracaoService.pesquisarEspecialidade(especialidadeFiltro);
			
		} catch (Exception e) {
			
			LOG.error(SERVICE + e.getMessage(), e);
			
		}
		
		return (listaRetorno != null && listaRetorno.size() > 0) ? listaRetorno.get(0) : null;
	}
	
}