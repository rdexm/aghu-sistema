package br.gov.mec.aghu.exames.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PesquisarMateriaisColetaEnfermagemON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisarMateriaisColetaEnfermagemON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AelAmostrasDAO aelAmostrasDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 315005505075598051L;
	
	public List<MateriaisColetarEnfermagemAmostraVO> pesquisarMateriaisColetaEnfermagem(
			AghUnidadesFuncionais unidadeFuncional,
			Integer prontuarioPaciente, Integer soeSeq,
			DominioSituacaoAmostra situacao)
			throws ApplicationBusinessException {

		List<MateriaisColetarEnfermagemAmostraVO> materiaisAmostras = this
				.getAelAmostrasDAO().pesquisarMateriaisColetaEnfermagemAmostra(
						unidadeFuncional, prontuarioPaciente, soeSeq, situacao);

		for (MateriaisColetarEnfermagemAmostraVO item : materiaisAmostras) {

			String local = this.getPrescricaoMedicaFacade().obterLocalPaciente(
					item.getAtdSeq());

			item.setLocal(local);
		}
		
		Collections.sort(materiaisAmostras,
				new Comparator<MateriaisColetarEnfermagemAmostraVO>() {
					@Override
					public int compare(MateriaisColetarEnfermagemAmostraVO o1,
							MateriaisColetarEnfermagemAmostraVO o2) {
						if (o1.getLocal() != null) {
							return o1.getLocal().compareTo(o2.getLocal());
						}
						return 1;
					}
				});

		return materiaisAmostras;
	}
	
	public List<MateriaisColetarEnfermagemAmostraItemExamesVO> pesquisarMateriaisColetarEnfermagemPorAmostra(
			Integer amoSoeSeq, Short amoSeqp) {
		
		return this.getAelAmostrasDAO().pesquisarMateriaisColetarEnfermagemPorAmostra(amoSoeSeq, amoSeqp);
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
}
