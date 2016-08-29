package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProfissionaisUnidadeCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioProfissionaisUnidadeCirurgicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioProfissionaisUnidadeCirurgicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private IProntuarioOnlineFacade iProntuarioOnlineFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2485578376336320529L;

	public List<RelatorioProfissionaisUnidadeCirurgicaVO> listarProfissionaisPorUnidadeCirurgica(Short seqUnidadeCirurgica, 
			Boolean ativosInativos, Short espSeq, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica ordenacao) {
		List<RelatorioProfissionaisUnidadeCirurgicaVO> listaRetorno = getMbcProfAtuaUnidCirgsDAO()
			.listarProfissionaisPorUnidadeCirurgica(seqUnidadeCirurgica, ativosInativos, espSeq, ordenacao);
		for (RelatorioProfissionaisUnidadeCirurgicaVO vo : listaRetorno) {
			vo.setConselho(getProntuarioOnlineFacade().obterNomeNumeroConselho(vo.getSerMatricula(), vo.getSerCodigo()));
		}
		
		return listaRetorno;
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return iProntuarioOnlineFacade;
	}
}
