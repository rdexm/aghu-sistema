package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.farmacia.vo.HistoricoCadastroMedicamentoVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoJn;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class HistoricoCadastroMedicamentoON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(HistoricoCadastroMedicamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaTipoApresentacaoMedicamentoDAO afaTipoApresentacaoMedicamentoDAO;

@EJB
private IComprasFacade comprasFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AfaMedicamentoJnDAO afaMedicamentoJnDAO;

@Inject
private AfaTipoUsoMdtoDAO afaTipoUsoMdtoDAO;
	
	public enum HistoricoCadastroMedicamentoONExceptionCode implements BusinessExceptionCode {
		ERRO_PROCESSAR_HISTORICO_MEDICAMENTO_VO;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 779525633787607949L;

	public Long pesquisarHistoricoCadastroMedicamentoCount(AfaMedicamento medicamento) {

		return getAfaMedicamentoJnDAO().pesquisarHistoricoCadastroMedicamentoCount(medicamento);
	}

	public List<AfaMedicamentoJn> pesquisarHistoricoCadastroMedicamento(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {


		List<AfaMedicamentoJn> listaHistoricoCadastroMedicamento = 
			getAfaMedicamentoJnDAO().pesquisarHistoricoCadastroMedicamento(firstResult, maxResult, orderProperty, asc, medicamento);

		return listaHistoricoCadastroMedicamento;
	}
	
	/**
	 * Obtém as informações do histórico do cadastro medicamento.
	 * 
	 * @param 
	 * @return 
	 * @throws ApplicationBusinessException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public HistoricoCadastroMedicamentoVO obterHistoricoCadastroMedicamento(
			Integer seqJn) throws ApplicationBusinessException{
		HistoricoCadastroMedicamentoVO historicoCadastroMedicamentoVO = new HistoricoCadastroMedicamentoVO();
		AfaMedicamentoJn medicamentoJn = null;

		medicamentoJn = getAfaMedicamentoJnDAO().obterAfaMedicamentoJn(seqJn);
		
		processaHistoricoCadastroMedicamentoVO(historicoCadastroMedicamentoVO, medicamentoJn);
		
		return historicoCadastroMedicamentoVO;
	}
	
	private void processaHistoricoCadastroMedicamentoVO(
			HistoricoCadastroMedicamentoVO histMedVo,
			AfaMedicamentoJn medicamentoJn) throws ApplicationBusinessException {
		try {
			PropertyUtils.copyProperties(histMedVo, medicamentoJn);
			histMedVo.setNomeMaterial(getNomeMaterial(histMedVo.getMatCodigo()));
			histMedVo.setDescricaoTprSigla(getDescricaoTprSigla(histMedVo.getTprSigla()));
			histMedVo.setDescricaoSiglaTipoUsoMtdo(getDescricaoSiglaTipoUsoMtdo(histMedVo.getSiglaTipoUsoMdto()));
			histMedVo.setDescricacaoUnidadeMedidaMedica(getDescricacaoUnidadeMedidaMedica(histMedVo.getSeqUnidadeMedidaMedica()));
			histMedVo.setDescricaoTipoFrequenciaAprazamento(getdescricaoTipoFrequenciaAprazamento(histMedVo.getSeqTipoFrequenciaAprazamento()));
			histMedVo.setResponsavelCadastro(getResponsavelCadastro(histMedVo.getMatricula(), histMedVo.getVinCodigo()));
		} catch (IllegalAccessException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					HistoricoCadastroMedicamentoONExceptionCode.ERRO_PROCESSAR_HISTORICO_MEDICAMENTO_VO);
		} catch (InvocationTargetException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					HistoricoCadastroMedicamentoONExceptionCode.ERRO_PROCESSAR_HISTORICO_MEDICAMENTO_VO);
		} catch (NoSuchMethodException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					HistoricoCadastroMedicamentoONExceptionCode.ERRO_PROCESSAR_HISTORICO_MEDICAMENTO_VO);
		}
	}

	private String getNomeMaterial(Integer matCodigo) {
		ScoMaterial material = getComprasFacade().obterScoMaterial(matCodigo);
		return material.getNome();
	}
	
	private String getDescricaoTprSigla(String tprSigla) {
		
		if (tprSigla==null) {
			return null;
		}
		
		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = getAfaTipoApresentacaoMedicamentoDAO().obterPorChavePrimaria(tprSigla);
		return tipoApresentacaoMedicamento.getDescricao();
	}

	private String getDescricaoSiglaTipoUsoMtdo(String siglaTipoUsoMdto) {
		
		if (siglaTipoUsoMdto==null) {
			return null;
		}
		
		AfaTipoUsoMdto tipoUsoMdto = getAfaTipoUsoMdtoDAO().obterPorChavePrimaria(siglaTipoUsoMdto);
		return tipoUsoMdto.getDescricao();
	}

	private String getDescricacaoUnidadeMedidaMedica(Integer seqUnidadeMedidaMedica) {
		
		if (seqUnidadeMedidaMedica==null) {
			return null;
		}
		
		MpmUnidadeMedidaMedica mpmUMM = getPrescricaoMedicaFacade().obterUnidadesMedidaMedicaPeloId(seqUnidadeMedidaMedica);
		return mpmUMM.getDescricao();
	}
	
	private String getdescricaoTipoFrequenciaAprazamento(Short seqTipoFrequenciaAprazamento) {
		
		if (seqTipoFrequenciaAprazamento==null) {
			return null;
		}
		MpmTipoFrequenciaAprazamento mpmTFA = getPrescricaoMedicaFacade().obterTipoFrequenciaAprazamentoId(seqTipoFrequenciaAprazamento);
		return mpmTFA.getDescricao();
	}
	
	private String getResponsavelCadastro(Integer matricula, Short vinCodigo) {
		
		RapServidoresId idRapServidor = new RapServidoresId();
		idRapServidor.setMatricula(matricula);
		idRapServidor.setVinCodigo(vinCodigo);

		RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idRapServidor);

		if(servidor != null){
			RapPessoasFisicas pessoa = servidor.getPessoaFisica();
			if(pessoa != null){
				return pessoa.getNome();						
			}	
		}
		return null;
	}

	private IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected AfaTipoApresentacaoMedicamentoDAO getAfaTipoApresentacaoMedicamentoDAO(){
		return afaTipoApresentacaoMedicamentoDAO;
	}

	protected AfaTipoUsoMdtoDAO getAfaTipoUsoMdtoDAO(){
		return afaTipoUsoMdtoDAO;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AfaMedicamentoJnDAO getAfaMedicamentoJnDAO() {
		return afaMedicamentoJnDAO;
	}
	
}
