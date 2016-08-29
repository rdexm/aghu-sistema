package br.gov.mec.aghu.farmacia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FarmaciaEmergiaON extends BaseBusiness {

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	private static final Log LOG = LogFactory.getLog(FarmaciaEmergiaON.class);
	
	private static final long serialVersionUID = 7397876968103447732L;

	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	public List<MedicamentoVO> pesquisarMedicamentoAtivoPorCodigoOuDescricao(String parametro) {
		List<MedicamentoVO> result = new ArrayList<MedicamentoVO>();
		List<AfaMedicamento> lista = afaMedicamentoDAO.pesquisarMedicamentoAtivoPorCodigoOuDescricao(parametro, 100);
		if (lista != null && !lista.isEmpty()) {
			for (AfaMedicamento afaMedicamento : lista) {
				result.add(new MedicamentoVO(afaMedicamento.getMatCodigo(), afaMedicamento.getDescricao()));
			}
		}
		return result;
	}

	public List<MedicamentoVO> pesquisarMedicamentoPorCodigos(List<Integer> matCodigos) {
		List<MedicamentoVO> result = new ArrayList<MedicamentoVO>();
		List<AfaMedicamento> lista = afaMedicamentoDAO.pesquisarMedicamentoPorCodigos(matCodigos);
		if (lista != null && !lista.isEmpty()) {
			for (AfaMedicamento afaMedicamento : lista) {
				result.add(new MedicamentoVO(afaMedicamento.getMatCodigo(), afaMedicamento.getDescricao()));
			}
		}
		return result;
	}

	public MedicamentoVO buscarMedicamentoPorCodigo(Integer matCodigo) {
		AfaMedicamento medicamento =  afaMedicamentoDAO.obterMedicamentoPorMatCodigo(matCodigo);
		if(medicamento == null) {
			return null;
		}
		return new MedicamentoVO(medicamento.getMatCodigo(), medicamento.getDescricao());
	}
}
