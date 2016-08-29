package br.gov.mec.aghu.perinatologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.MedicamentoVO;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.ComponenteSanguineo;
import br.gov.mec.aghu.perinatologia.dao.McoProcReanimacaoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoReanimacaoRnsDAO;
import br.gov.mec.aghu.perinatologia.vo.MedicamentoRecemNascidoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * #41973
 * @author Paulo Silveira
 *
 */
@Stateless
public class MedicamentoRecemNascidoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7634300971138427057L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private McoProcReanimacaoDAO mcoProcReanimacaoDAO;
	
	@Inject
	private McoReanimacaoRnsDAO mcoReanimacaoRnsDAO;
	
	@Inject
	private IPacienteService pacienteService;
	
	private enum MedicamentoRecemNascidoRNExceptionCode implements BusinessExceptionCode {
		MCO_00123;
	}
	
	/**
	 * C1 Consulta utilizada para buscar os dados dos Medicamentos na aba Recém Nascido.
	 * @param descricao
	 * @return
	 */
	public List<MedicamentoRecemNascidoVO> buscarMedicamentosPorDescricao(String descricao) {
		List<MedicamentoRecemNascidoVO> lista =  mcoProcReanimacaoDAO.buscarMedicamentosPorDescricao(descricao);
		if(isNotListaVazia(lista)) {
			for (MedicamentoRecemNascidoVO item : lista) {
				item.setDescricaoMed(obterDescricaoMedPorCodigo(item.getMedMatCodigo()));
				item.setDescricaoCsa(obterDescricaoCsaPorCodigo(item.getCodigoCsa()));
			}
		}
		return lista;
	}
	
	/**
	 * Consulta utilizada para buscar a descrição da Via de Administração. Serviço #43247.
	 * C4
	 * @param param
	 * @return
	 */
	public List<ViaAdministracaoVO> pesquisarViaAdminstracaoSiglaouDescricao(String param) {
		return farmaciaFacade.pesquisarViaAdminstracaoSiglaouDescricao(param);
	}
	
	/**
	 * Consulta utilizada para verificar se o medicamento está gravado no banco de dados.
	 * @param rnaGsoPacCodigo
	 * @param rnaGsoSeqp
	 * @param rnaSeqp
	 * @param pniSeq
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private void verificarMedicamentoCadastradoBD(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp, Integer pniSeq) throws ApplicationBusinessException {
		if(mcoReanimacaoRnsDAO.isReanimacaoCadastradoBD(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp, pniSeq)){
			throw new ApplicationBusinessException(MedicamentoRecemNascidoRNExceptionCode.MCO_00123);
		}
	}
	
	
	/**
	 * Consulta utilizada para buscar os dados dos Medicamentos na aba Recém Nascido. 
	 * @param rnaGsoPacCodigo
	 * @param rnaGsoSeqp
	 * @param rnaSeqp
	 * @return
	 */
	public List<MedicamentoRecemNascidoVO> buscarMedicamentosPorRecemNascido(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp){
		return mcoReanimacaoRnsDAO.buscarMedicamentosPorRecemNascido(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp);
	}
	
	/**
	 * Consulta utilizada para buscar a descrição do Medicamento. Serviço #43244.
	 * C3
	 * @param codigo
	 * @return
	 */
	private String obterDescricaoMedPorCodigo(Integer codigo) {
		if(codigo == null) {
			return "";
		}
		MedicamentoVO medicamento = farmaciaFacade.buscarMedicamentoPorCodigo(codigo);
		if(medicamento == null) {
			return "";
		}
		return medicamento.getDescricao();
	}
	
	/**
	 * Consulta utilizada para buscar a descrição do Componente Sanguíneos. Serviço #43243.
	 * C2
	 * @param codigo
	 * @return
	 */
	private String obterDescricaoCsaPorCodigo(String codigo) {
		if(StringUtils.isBlank(codigo)) {
			return "";
		}
		ComponenteSanguineo csa = pacienteService.obterComponentePorId(codigo);
		if(csa == null) {
			return "";
		}
		return csa.getDescricao();
	}
	
	@SuppressWarnings("rawtypes")
	private Boolean isNotListaVazia(List lista) {
		return lista != null && !lista.isEmpty();
	}
	
	public Long buscarMedicamentosCountPorDescricao(String descricao) {
		return mcoProcReanimacaoDAO.buscarMedicamentosCountPorDescricao(descricao);
	}
	
	public Long pesquisarViaAdminstracaoSiglaouDescricaoCount(String param) {
		return farmaciaFacade.listarViasAdministracaoCount(param);
	}	

	/**
	 * RN01
	 * @param medicamento
	 * @param lista
	 * @throws ApplicationBusinessException
	 */
	public void validarMedicamento(MedicamentoRecemNascidoVO medicamento, List<MedicamentoRecemNascidoVO> lista) throws ApplicationBusinessException {
		if(isNotListaVazia(lista) && medicamento != null) {
			verificarMedicamentoNaLista(medicamento, lista);
			verificarMedicamentoCadastradoBD(medicamento.getRnaGsoPacCodigo(), medicamento.getRnaGsoSeqp(), medicamento.getRnaSeqp(), medicamento.getSeqPni());
		}		
	}

	private void verificarMedicamentoNaLista(
			MedicamentoRecemNascidoVO medicamento,
			List<MedicamentoRecemNascidoVO> lista) throws ApplicationBusinessException {
		for (MedicamentoRecemNascidoVO item : lista) {
			if(medicamento.getSeqPni() != null && item.getSeqPni() !=null) {
				if(medicamento.getSeqPni() == item.getSeqPni()) {
					throw new ApplicationBusinessException(MedicamentoRecemNascidoRNExceptionCode.MCO_00123);
				}
			}
		}
	}
	
	
	public boolean isMedicamentoRecemNascidoCadastrado(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp, Integer seqPni){
		return mcoReanimacaoRnsDAO.isReanimacaoCadastradoBD(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp, seqPni);
	}
	
	@Deprecated
	@Override
	protected Log getLogger() {
		return null;
	}
}
